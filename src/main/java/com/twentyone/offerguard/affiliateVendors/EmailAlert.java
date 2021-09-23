package com.twentyone.offerguard.affiliateVendors;

import com.sendgrid.helpers.mail.objects.Content;
import com.twentyone.offerguard.config.SendGridConfig;
import com.twentyone.offerguard.models.Offer;
import com.twentyone.offerguard.repositories.OfferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EmailAlert {

	@Autowired
	private OfferRepository offerRepository;

	private static OfferRepository offerService;

	@PostConstruct
	public void init() {
		this.offerService = offerRepository;
	}

	@Scheduled(cron = "${offer.guard.emailalert.job.cron}")
	public void startEmailAlertJob() {
		sendEmailAlerts();
	}

	public static void sendEmailAlerts() {
		log.info("Execution of email alerts job started");
		log.info("Getting all offers with status failed or pending");
		List<Offer> offerList = offerService.findByAffiliateStatus("FAILED");
		log.info("offers pulled successfully");

		log.info("total offers: {}", offerList.size());

		List<String> offerIdList =  offerList.stream()
				.map(offer -> offer.getId())
				.collect(Collectors.toList());

		String message = offerIdList.size() > 0 ? offerIdList.toString() : "No offers with failed status found in database, everything looks good :)";

		try {
			log.info("Sending email alerts");
			SendGridConfig.sendEmail("OfferGuard Update For Failed offers", new Content("text/plain", message));
		} catch (IOException e) {
			log.error("Error in sending email alert", e);
		}
	}
}
