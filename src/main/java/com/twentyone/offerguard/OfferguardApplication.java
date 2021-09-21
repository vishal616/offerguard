package com.twentyone.offerguard;

import com.twentyone.offerguard.config.Heroku;
import com.twentyone.offerguard.config.SendGridConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OfferguardApplication {

	public static void main(String[] args) {

		SpringApplication.run(OfferguardApplication.class, args);
		try {
			SendGridConfig.sendEmail(null, null);
		} catch (Exception e) {

		}

	}

	@Bean
	public Heroku heroku(){
		return new Heroku();
	}

}
