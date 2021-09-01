package com.twentyone.offerguard.controllers;

import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.repositories.OfferRepository;
import com.twentyone.offerguard.repositories.RedirectUrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
