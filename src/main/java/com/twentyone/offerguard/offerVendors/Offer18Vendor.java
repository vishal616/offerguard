package com.twentyone.offerguard.offerVendors;

import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.models.Offer18VendorModel;
import com.twentyone.offerguard.models.Offer18Response;
import com.twentyone.offerguard.models.Stats;
import com.twentyone.offerguard.repositories.OfferRepository;
import com.twentyone.offerguard.repositories.RedirectUrlRepository;
import com.twentyone.offerguard.repositories.StatsRepository;
import com.twentyone.offerguard.services.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Offer18Vendor {

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private RedirectUrlRepository redirectUrlRepository;

	@Autowired
	private StatsService statsService;

	private static String key;
	private static String aid;
	private static String mid;
	private static String ANDROID_DEVICE_KEY;
	private static String IOS_DEVICE_KEY;
	private static String VENDOR_URL = "https://api.offer18.com/api/af/offers?mid={mid}&aid={aid}&key={key}";
	private static OfferRepository offerService;
	private static RedirectUrlRepository redirectUrlService;
	private static StatsService statsServiceForDb;

	@Value("${offer.guard.offer18.key}")
	public void setKey(String apiKey) {
		key = apiKey;
	}

	@Value("${offer.guard.offer18.aid}")
	public void setAid(String apiAid) {
		aid = apiAid;
	}
	@Value("${offer.guard.offer18.mid}")
	public void setMid(String apiMid) {
		mid = apiMid;
	}
	@Value("${offer.guard.offer18.android.device.key}")
	public void setAndroidDeviceKey(String androidDeviceKey) {
		ANDROID_DEVICE_KEY = androidDeviceKey;
	}

	@Value("${offer.guard.offer18.ios.device.key}")
	public void setIosDeviceKey(String iosDeviceKey) {
		IOS_DEVICE_KEY = iosDeviceKey;
	}

	@PostConstruct
	public void init() {
		this.offerService = offerRepository;
		this.redirectUrlService = redirectUrlRepository;
		this.statsServiceForDb = statsService;
	}

	@Scheduled(cron = "${offer.guard.offer18.job.cron}")
	public void startOffer18Job() {
		triggerOffer18Job();
	}

	public static String triggerOffer18Job() {
		log.info("Execution of offer 18 job started");
		Offer18VendorModel offer18VendorModel = new Offer18VendorModel(null,null,null,"1","1",null);
		getOffers(offer18VendorModel);
		log.info("Execution of offer 18 job finished");
		statsServiceForDb.updateStats(new Stats("offer18", LocalDateTime.now().toString()));
		return "Execution of offer 18 job finished";
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

			log.info("started deleting entries in redirect urls table");
			redirectUrlService.deleteAll();
			log.info("delete successful");

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
