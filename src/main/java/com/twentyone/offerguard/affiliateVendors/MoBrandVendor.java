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
import org.springframework.web.client.RestClientException;
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
	private static RestTemplate restTemplate;
	private static HttpHeaders httpHeaders;

	@PostConstruct
	public void init() {
		this.offerService = offerRepository;
		restTemplate = buildRestTemplate();
		httpHeaders = buildHeaders();
	}

//	@Scheduled(cron = "0 */1 * ? * *")
	public static void startMoBrandJob() {
		log.info("Execution of mo brand affiliate link check job started");
		getAffiliateStatusForOffers();
		log.info("Execution of mo brand affiliate link check job finished");
	}

	public static void getAffiliateStatusForOffers() {
		log.info("getting all offers from database");
		List<Offer> offerList = offerService.findAll();
		log.info("offers pulled successfully");

		log.info("total offers: {}", offerList.size());

		offerList.forEach((offer -> {
			try {
				if(allowOffer(offer)) {
					callMoBrand(offer);
				}
			} catch (RestClientException e) {
				log.error("Error in calling mo brand rest api:: {}", e);
			} catch (SQLException e) {
				log.error("Error in updating the offer:: {}", e);
			}
		}));
	}

	private static void callMoBrand(Offer offer) throws RestClientException, SQLException {
		log.info("calling mo brand api for offer:: {}", offer.getName());

		MultiValueMap<String, String> requestBody = buildPayloadMap(offer);

		HttpEntity requestEntity = new HttpEntity<>(requestBody.toSingleValueMap(), httpHeaders);
		ResponseEntity<MoBrandResponse> response = null;

		try {
			response = restTemplate.exchange("https://api.offertest.net/offertest", HttpMethod.POST, requestEntity, MoBrandResponse.class);
		} catch (RestClientException e) {
			throw new RestClientException("api called failed for mobrand");
		}

		log.info("response code:: {}", response.getStatusCode());

		MoBrandResponse moBrandResponse = response.getBody();

		offer.setRedirects(moBrandResponse.getRedirects());

		String bundleIdMatch = moBrandResponse.getBundleIdMatch();
		if(bundleIdMatch.equalsIgnoreCase("true")) {
			offer.setAffiliateStatus("SUCCESS");
		} else {
			offer.setAffiliateStatus("FAILED");
		}

		log.info("offer status for {} is {}", offer.getName(), offer.getAffiliateStatus());

		try {
			updateOfferStatus(offer);
		} catch (SQLException e) {
			throw new SQLException(e);
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

	private static RestTemplate buildRestTemplate() {
		return new RestTemplateBuilder()
				.messageConverters(
						new MappingJackson2HttpMessageConverter(new ObjectMapper()),
						new FormHttpMessageConverter())
				.build();
	}

	private static HttpHeaders buildHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private static MultiValueMap<String, String> buildPayloadMap(Offer offer) {
		log.info("building payload for api call");
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("userid", USER_ID);
		requestBody.add("country", offer.getCountryAllowed().toLowerCase());
		requestBody.add("url", offer.getClickUrl());
		requestBody.add("platform", offer.getOsAllowed());
		requestBody.add("expectedBundleId", getBundleId(offer.getPreviewUrl(), offer.getOsAllowed()));
		log.info("payload built successful");
		return requestBody;
	}

	private static String getBundleId(String previewUrl, String os) {
		log.info("building bundle id for payload");
		String bundleId = null;
		if (os.contains("android")) {
			int lastEqualCharPos = previewUrl.lastIndexOf('=');
			bundleId = previewUrl.substring(lastEqualCharPos + 1);
		} else {
			int lastForwardSlashCharPos = previewUrl.lastIndexOf('/');
			int lastQuestionCharPos = previewUrl.lastIndexOf('?');
			bundleId = previewUrl.substring(lastForwardSlashCharPos+1,lastQuestionCharPos);
		}
		log.info("bundle id for preview url :: {} is {}", previewUrl, bundleId);
		return bundleId;
	}

	private static boolean allowOffer(Offer offer) {
		if(offer.getClickUrl() == null || offer.getClickUrl() == "") {
			return false;
		}
		if(offer.getOsAllowed() == null || offer.getOsAllowed() == "") {
			return false;
		}
		if(offer.getCountryAllowed() == null || offer.getCountryAllowed() == "") {
			return false;
		}
		return true;
	}
}
