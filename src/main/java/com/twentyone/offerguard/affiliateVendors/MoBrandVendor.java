package com.twentyone.offerguard.affiliateVendors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MoBrandVendor {

	@Scheduled(cron = "0 */3 * ? * *")
	public static void startMoBrandJob() {
		log.info("mo brand job running");
	}
}
