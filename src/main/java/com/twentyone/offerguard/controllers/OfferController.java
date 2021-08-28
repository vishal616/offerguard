package com.twentyone.offerguard.controllers;

import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.repositories.OfferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@Slf4j
public class OfferController {

	@Autowired
	private OfferRepository offerRepository;

	@GetMapping("/")
	public List<Offer> getAllOffers() {
		log.info("get all offers rest api requested");
		return offerRepository.findAll();
	}
}
