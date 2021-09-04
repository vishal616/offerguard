package com.twentyone.offerguard.controllers;

import com.twentyone.offerguard.models.Stats;
import com.twentyone.offerguard.repositories.StatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@Slf4j
public class StatsController {

	@Autowired
	private StatsRepository statsRepository;

	@GetMapping("/")
	public List<Stats> getAllOffers() {
		log.info("get all stats rest api requested");
		return statsRepository.findAll();
	}
}
