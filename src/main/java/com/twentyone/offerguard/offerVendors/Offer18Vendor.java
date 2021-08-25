package com.twentyone.offerguard.offerVendors;

import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.models.Offer18VendorModel;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Offer18Vendor {

	private static String key = "test1";
	private static String aid = "test2";
	private static String mid = "test3";

	private static String VENDOR_URL = "https://api.offer18.com/api/af/offers?mid={mid}&aid={aid}&key={key}";

	public static List<Offer> getOffers(Offer18VendorModel offer18VendorModel) throws IOException {
		List<Offer> offers = new ArrayList<>();
		buildUrl(offer18VendorModel);
		// TODO

//		try (CloseableHttpClient client = HttpClients.createDefault()) {
//
//			HttpGet request = new HttpGet("https://api.offer18.com/api/af/offers?mid=4146&aid=265882&key=adfdccd32ae7efce92c59abe5b27c510&authorized=1");
//
//			CloseableHttpResponse response =  client.execute(request);
//			System.out.println(response.getProtocolVersion());              // HTTP/1.1
//			System.out.println(response.getStatusLine().getStatusCode());   // 200
//			System.out.println(response.getStatusLine().getReasonPhrase()); // OK
//			System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
//
//			HttpEntity entity = response.getEntity();
//			if (entity != null) {
//				String responseString = EntityUtils.toString(entity);
//				System.out.println(responseString);
//				System.out.println(entity.getContent().);
//			}
//		}
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
