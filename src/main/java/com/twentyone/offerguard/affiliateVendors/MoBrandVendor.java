package com.twentyone.offerguard.affiliateVendors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twentyone.offerguard.models.MoBrandResponse;
import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.models.RedirectUrl;
import com.twentyone.offerguard.models.Stats;
import com.twentyone.offerguard.repositories.OfferRepository;
import com.twentyone.offerguard.repositories.RedirectUrlRepository;
import com.twentyone.offerguard.services.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MoBrandVendor {


	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private RedirectUrlRepository redirectUrlRepository;

	@Autowired
	private StatsService statsService;

	private static String token;
	private static String USER_ID;

	@Value("${offer.guard.mobrand.token}")
	public void setToken(String apiToken) {
		token = apiToken;
	}

	@Value("${offer.guard.mobrand.userid}")
	public void setUserId(String id) {
		USER_ID = id;
	}

	private static OfferRepository offerService;
	private static RedirectUrlRepository redirectUrlService;
	private static RestTemplate restTemplate;
	private static HttpHeaders httpHeaders;
	private static StatsService statsServiceForDb;

	@PostConstruct
	public void init() {
		this.offerService = offerRepository;
		this.redirectUrlService = redirectUrlRepository;
		this.statsServiceForDb = statsService;
		restTemplate = buildRestTemplate();
		httpHeaders = buildHeaders();
	}

	@Scheduled(cron = "${offer.guard.mobrand.job.cron}")
	public void startMoBrandJob() {
		getAffiliateStatusForOffers();
	}

	public static String triggerMobBrandJob() {
		log.info("Execution of mo brand affiliate link check job started");
		getAffiliateStatusForOffers();
		log.info("Execution of mo brand affiliate link check job finished");
		statsServiceForDb.updateStats(new Stats("mobrand", LocalDateTime.now().toString()));
		return "Execution of mo brand affiliate link check job finished";
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
			} catch (StringIndexOutOfBoundsException e) {
				log.error("Error occurred during bundle id extraction");
			}
		}));
	}

	public static void callMoBrand(Offer offer) throws RestClientException, SQLException, StringIndexOutOfBoundsException {
		log.info("calling mo brand api for offer:: {}", offer.getName());

		MultiValueMap<String, String> requestBody = null;
		try {
			requestBody = buildPayloadMap(offer);
		} catch (StringIndexOutOfBoundsException e) {
			throw new StringIndexOutOfBoundsException();
		}

		HttpEntity requestEntity = new HttpEntity<>(requestBody.toSingleValueMap(), httpHeaders);
		ResponseEntity<MoBrandResponse> response = null;

		try {
			log.info("waiting for the api response");
			response = restTemplate.exchange("https://api.offertest.net/offertest?async=true", HttpMethod.POST, requestEntity, MoBrandResponse.class);
			log.info("response code:: {}", response.getStatusCode());
		} catch (RestClientException e) {
			throw new RestClientException("api call failed for mo brand");
		}

//		MoBrandResponse moBrandResponse = response.getBody();
//
//		offer.setRedirects(moBrandResponse.getRedirects());
//
//		String bundleIdMatch = moBrandResponse.getBundleIdMatch();
//		if(bundleIdMatch.equalsIgnoreCase("true")) {
//			offer.setAffiliateStatus("SUCCESS");
//		} else {
//			offer.setAffiliateStatus("FAILED");
//		}
//
//		List<RedirectUrl> redirectUrlList = moBrandResponse.getUrls().stream().map((moBrandUrl -> new RedirectUrl(offer.getId(), moBrandUrl.getUrl()))).collect(Collectors.toList());
//
//		log.info("offer status for {} is {}", offer.getName(), offer.getAffiliateStatus());
//
//		try {
//			updateOfferStatus(offer);
//			addRedirectUrls(redirectUrlList);
//		} catch (SQLException e) {
//			throw new SQLException(e);
//		}

	}

	public static void saveMoBrandOfferResult(MoBrandResponse moBrandResponse, String offerId) {
		String bundleIdMatch = moBrandResponse.getBundleIdMatch();
		Offer offer = offerService.getById(offerId);
		if(bundleIdMatch.equalsIgnoreCase("true")) {
			offer.setAffiliateStatus("SUCCESS");
		} else {
			offer.setAffiliateStatus("FAILED");
		}

		List<RedirectUrl> redirectUrlList = moBrandResponse.getUrls().stream().map((moBrandUrl -> new RedirectUrl(offerId, moBrandUrl.getUrl()))).collect(Collectors.toList());

		log.info("offer status for {} is {}", offer.getName(), offer.getAffiliateStatus());

		try {
			updateOfferStatus(offer);
			addRedirectUrls(redirectUrlList);
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

	private static void addRedirectUrls(List<RedirectUrl> redirectUrlList) throws SQLException {
		try {
			log.info("going to add redirects urls in database");
			redirectUrlService.saveAll(redirectUrlList);
			log.info("redirect urls updated successfully");
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

	private static MultiValueMap<String, String> buildPayloadMap(Offer offer) throws StringIndexOutOfBoundsException {
		log.info("building payload for api call");
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		try {
			requestBody.add("userid", USER_ID);
			requestBody.add("country", offer.getCountryAllowed().toLowerCase());
			requestBody.add("url", offer.getClickUrl());
			requestBody.add("platform", offer.getOsAllowed());
			requestBody.add("expectedBundleId", getBundleId(offer.getPreviewUrl(), offer.getOsAllowed()));
			requestBody.add("callback", getCallBackUrl(offer.getId()));
			log.info("payload built successful");
		} catch (StringIndexOutOfBoundsException e) {
			throw new StringIndexOutOfBoundsException();
		}
		return requestBody;
	}

	private static String getCallBackUrl(String offerId) {
		return "https://offer-guard.herokuapp.com/api/mobrand/" + offerId +"/result";
	}

	private static String getBundleId(String previewUrl, String os) throws StringIndexOutOfBoundsException {
		log.info("building bundle id for payload");
		String bundleId = null;
		try {
			if (os.contains("android")) {
				int firstEqualCharPos = previewUrl.indexOf('=');
				int firstQuestionCharPosAfterLastEqual = previewUrl.indexOf('&', firstEqualCharPos);
				if(firstQuestionCharPosAfterLastEqual == -1) {
					bundleId = previewUrl.substring(firstEqualCharPos + 1);
				} else {
					bundleId = previewUrl.substring(firstEqualCharPos + 1, firstQuestionCharPosAfterLastEqual);
				}
			} else {
				int lastForwardSlashCharPos = previewUrl.lastIndexOf('/');
				int lastQuestionCharPos = previewUrl.lastIndexOf('?');
				if(lastQuestionCharPos == -1) {
					bundleId = previewUrl.substring(lastForwardSlashCharPos+1);
				} else {
					bundleId = previewUrl.substring(lastForwardSlashCharPos+1,lastQuestionCharPos);
				}

			}
		} catch (StringIndexOutOfBoundsException e) {
			throw new StringIndexOutOfBoundsException();
		}
		log.info("bundle id for preview url :: {} is {}", previewUrl, bundleId);
		return bundleId;
	}

	private static boolean allowOffer(Offer offer) {
		if(offer.getClickUrl() == null || offer.getClickUrl().length() == 0) {
			return false;
		}
		if(offer.getOsAllowed() == null || offer.getOsAllowed().length() == 0) {
			return false;
		}
		if(offer.getCountryAllowed() == null || offer.getCountryAllowed().length() == 0) {
			return false;
		}
		return true;
	}
}
