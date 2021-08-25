package com.twentyone.offerguard.offerVendors;

import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.models.Offer18VendorModel;
import com.twentyone.offerguard.models.OfferResponse;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class Offer18Vendor {

	private static String key = "test1";
	private static String aid = "test2";
	private static String mid = "test3";

	private static String VENDOR_URL = "https://api.offer18.com/api/af/offers?mid={mid}&aid={aid}&key={key}";

	public static List<Offer> getOffers(Offer18VendorModel offer18VendorModel) {
		buildUrl(offer18VendorModel);
		RestTemplate restTemplate = new RestTemplate();
		OfferResponse offerResponse = restTemplate.getForObject("https://api.offer18.com/api/af/offers?mid=4146&aid=265882&key=adfdccd32ae7efce92c59abe5b27c510", OfferResponse.class);
		log.info("value:: {}", offerResponse.getData());
		Map<String, Offer> data = offerResponse.getData();
		List<Offer> offers = new ArrayList<>(data.values());
		log.info("value:: {}", offers);
		offers.forEach((t) -> {
			System.out.println(t.getName());
		});
		return offers;
	}

	private static String buildUrl(Offer18VendorModel offer18VendorModel) {
		String newUrl = VENDOR_URL;
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

		return newUrl;
	}
}
