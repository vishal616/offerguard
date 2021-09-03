package com.twentyone.offerguard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class Heroku {

	@Scheduled(fixedDelay=120000)
	public void herokuNotIdle(){
		log.info("Heroku not idle execution");
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.getForObject("http://offer-guard.herokuapp.com", Object.class);
		} catch (Exception e) {}
	}
}
