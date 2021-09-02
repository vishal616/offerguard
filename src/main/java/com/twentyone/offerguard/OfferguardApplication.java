package com.twentyone.offerguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OfferguardApplication {

	public static void main(String[] args) {

		SpringApplication.run(OfferguardApplication.class, args);
	}

}
