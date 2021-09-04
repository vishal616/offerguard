package com.twentyone.offerguard.controllers;

import com.twentyone.offerguard.affiliateVendors.MoBrandVendor;
import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.offerVendors.Offer18Vendor;
import com.twentyone.offerguard.repositories.OfferRepository;
import com.twentyone.offerguard.repositories.RedirectUrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/offers")
@Slf4j
public class OfferController {

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private RedirectUrlRepository redirectUrlRepository;

	private List<String> mmpsToFind = Arrays.asList("kochava", "branch", "singular", "appsflyer", "adjust");

	@GetMapping("/")
	public List<Offer> getAllOffers() {
		log.info("get all offers rest api requested");
		return offerRepository.findAll().stream().map((offer) -> {
			offer.setMobileMarketingPlatforms(getMMPs(offer.getId()));
			return offer;
		}).collect(Collectors.toList());
	}

	@GetMapping("/{offerId}/status")
	public Offer checkOfferStatus(@PathVariable String offerId) {
		log.info("check offer for offer id:: {} rest api requested", offerId);
		Optional<Offer> offer = offerRepository.findById(offerId);
		if (!offer.isPresent()) {
			log.error("Offer Id:: {} is not present in offer 18", offerId);
			return null;
		}
		log.info("Offer Id:: {} is present in offer 18", offerId);
		try {
			MoBrandVendor.callMoBrand(offer.get());
		} catch (SQLException e) {
			log.error("Error occurred during offer check", e);
		}
		return offer.get();
	}

	@GetMapping("/job")
	public String triggerOffer18Job() {
		log.info("trigger offer 18 job rest api requested");
		return Offer18Vendor.triggerOffer18Job();
	}

	private String getMMPs(String offerId) {
		Set<String> foundMMPs = new HashSet<>();
		List<String> urls = redirectUrlRepository.findByOfferId(offerId).stream().map(redirectUrl -> redirectUrl.getUrl()).collect(Collectors.toList());
		urls.forEach((url) -> {
			mmpsToFind.forEach((mmp) -> {
				if(url.contains(mmp)) {
					foundMMPs.add(mmp);
				}
			});
		});
		return String.join(", ", foundMMPs);
	}
}
