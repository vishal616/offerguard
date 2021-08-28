package com.twentyone.offerguard.affiliateVendors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twentyone.offerguard.models.MoBrandResponse;
import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.repositories.OfferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MoBrandVendor {


	@Autowired
	private OfferRepository offerRepository;

	private static String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNbzRJZTRXcVFfLWliUGdmcW5GYUxBIiwiaWF0IjoxNjI5NzI5MDkwLCJqdGkiOiIzOUswaVNsRDZ2Q1V1MnFZRVFrRnp3In0.L8oxEDtbzebbuM6l3dMI-BkCQKGNWVGMsWh_jYiJaE0";
	private static String USER_ID = "Mo4Ie4WqQ_-ibPgfqnFaLA";

	private static OfferRepository offerService;

	@PostConstruct
	public void init() {
		this.offerService = offerRepository;
	}

//	@Scheduled(cron = "0 */3 * ? * *")
	public static void startMoBrandJob() {
		log.info("mo brand job running");
	}

	public static void getAffiliateStatusForOffers() {
		log.info("getting all offers from database");
		List<Offer> offerList = offerService.findAll();
		log.info("offers pulled successfully");

		log.info("total offers: {}", offerList.size());

		offerList.forEach((offer -> {
			callMoBrand(offer);
		}));

	}

	private static void callMoBrand(Offer offer) {
		log.info("calling mo brand api for offer:: {}", offer.getName());
		RestTemplate restTemplate = new RestTemplateBuilder()
				.messageConverters(
						new MappingJackson2HttpMessageConverter(new ObjectMapper()),
						new FormHttpMessageConverter())
				.build();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("userid", USER_ID);
		requestBody.add("country", offer.getCountryAllowed().toLowerCase());
		requestBody.add("url", offer.getClickUrl());
		requestBody.add("platform", offer.getOsAllowed());

		HttpEntity requestEntity = new HttpEntity<>(requestBody.toSingleValueMap(), headers);
		ResponseEntity<MoBrandResponse> response = null;

		try {
			response = restTemplate.exchange("https://api.offertest.net/offertest", HttpMethod.POST, requestEntity, MoBrandResponse.class);
		} catch (HttpClientErrorException e) {
			log.error("api called failed for mobrand:: {}", e);
		}

		log.info("response code:: {}", response.getStatusCode());

		MoBrandResponse moBrandResponse = response.getBody();
		log.info("offer status for {} is {}", offer.getName(), moBrandResponse.getStatus());

		offer.setRedirects(moBrandResponse.getRedirects());
		offer.setAffiliateStatus(moBrandResponse.getStatus());

		try {
			updateOfferStatus(offer);
		} catch (SQLException e) {
			log.error("Error in updating the offer:: {}", e);
		}

	}

	private static void updateOfferStatus(Offer offer) throws SQLException {
		try {
			log.info("going to update offer in database");
			offerService.save(offer);
			log.info("offer updated successfully");
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
}
