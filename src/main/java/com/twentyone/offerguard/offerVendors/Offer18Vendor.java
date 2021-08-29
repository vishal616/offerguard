package com.twentyone.offerguard.offerVendors;

import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.models.Offer18VendorModel;
import com.twentyone.offerguard.models.Offer18Response;
import com.twentyone.offerguard.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Offer18Vendor {

	@Autowired
	private OfferRepository offerRepository;

	private static String key = "adfdccd32ae7efce92c59abe5b27c510";
	private static String aid = "265882";
	private static String mid = "4146";
	private static String VENDOR_URL = "https://api.offer18.com/api/af/offers?mid={mid}&aid={aid}&key={key}";
	private static String ANDROID_DEVICE_KEY = "c6aee72f-72d8-4a71-ad7a-742acfd35a60";
	private static String IOS_DEVICE_KEY = "97F0C91E-946E-449A-B722-BCE6BF5451A9";
	private static OfferRepository offerService;

	@PostConstruct
	public void init() {
		this.offerService = offerRepository;
	}

//	@Scheduled(cron = "0 */1 * ? * *")
	public void startOffer18Job() {
		log.info("Execution of offer 18 job started");
		Offer18VendorModel offer18VendorModel = new Offer18VendorModel(null,null,null,"1","1",null);
		getOffers(offer18VendorModel);
		log.info("Execution of offer 18 job finished");
	}

	public static List<Offer> getOffers(Offer18VendorModel offer18VendorModel) {
		log.info("get offers call started");
		String apiUrl = buildUrl(offer18VendorModel);
		RestTemplate restTemplate = new RestTemplate();
		Offer18Response offer18Response = null;
		List<Offer> offers = null;
		try {
			log.info("calling api");
			offer18Response = restTemplate.getForObject(apiUrl, Offer18Response.class);
			log.info("api response:: {}", offer18Response.getResponse());

			Map<String, Offer> data = offer18Response.getData();
			offers = new ArrayList<>(data.values());
			offers.forEach((offer -> {
				String operatingSystem = offer.getOsAllowed();
				String osAppender = null;
				if(operatingSystem.contains("android")) {
					osAppender = "&googleaid="+ANDROID_DEVICE_KEY;
				} else {
					osAppender = "&iosidfa="+IOS_DEVICE_KEY;
				}
				offer.setClickUrl(offer.getClickUrl() + osAppender);
			}));
			updateOffers(offers);
		} catch (HttpServerErrorException e) {
			log.error("api call failed:: {}", e);
		}
		catch (SQLException e) {
			log.error("database offer table update failed:: {}", e);
		}
		return offers;
	}

	private static void updateOffers(List<Offer> offerList) throws SQLException {
		try {
			log.info("started deleting entries in offer table");
			offerService.deleteAll();
			log.info("delete successful");

			log.info("started saving new entries in offer table");
			offerService.saveAll(offerList);
			log.info("saving successful");

		} catch (Exception e) {
			log.error("Error in updating database:: {}", e);
			throw new SQLException(e);
		}
	}

	private static String buildUrl(Offer18VendorModel offer18VendorModel) {
		String newUrl = VENDOR_URL;
		log.info("building url with vendor url as :: {}", newUrl);
		newUrl = newUrl.replace("{mid}", mid).replace("{aid}", aid).replace("{key}", key);

		if (offer18VendorModel.getAuthorized() != null ) {
			newUrl = newUrl + "&authorized=" + offer18VendorModel.getAuthorized();
		}
		if (offer18VendorModel.getCountry() != null ) {
			newUrl = newUrl + "&country=" + offer18VendorModel.getCountry();
		}
		if (offer18VendorModel.getModel() != null ) {
			newUrl = newUrl + "&model=" + offer18VendorModel.getModel();
		}
		if (offer18VendorModel.getOfferId() != null ) {
			newUrl = newUrl + "&offer_id=" + offer18VendorModel.getOfferId();
		}
		if (offer18VendorModel.getOfferStatus() != null ) {
			newUrl = newUrl + "&offer_status=" + offer18VendorModel.getOfferStatus();
		}
		if (offer18VendorModel.getPage() != null ) {
			newUrl = newUrl + "&page=" + offer18VendorModel.getPage();
		}
		newUrl = newUrl + "&i=1";

		log.info("url built successfully:: {}", newUrl);

		return newUrl;
	}
}
