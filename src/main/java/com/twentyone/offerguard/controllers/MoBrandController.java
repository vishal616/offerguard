package com.twentyone.offerguard.controllers;

import com.twentyone.offerguard.affiliateVendors.MoBrandVendor;
import com.twentyone.offerguard.models.MoBrandResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/mobrand")
@Slf4j
public class MoBrandController {

	@PostMapping("/{offerId}/result")
	public void receiveMoBrandAsyncResponse(@RequestBody MoBrandResponse moBrandResponse, @PathVariable String offerId) {
		log.info("receiving response from mobrand async rest api for offer Id :: {} with bundle Id match as:: {}", offerId, moBrandResponse.getBundleIdMatch());
		MoBrandVendor.saveMoBrandOfferResult(moBrandResponse, offerId);
		log.info("offer successfully integrated into the database");
	}

	@GetMapping("/job")
	public String triggerMoBrandJob() {
		log.info("trigger mo brand job rest api requested");
		return MoBrandVendor.triggerMobBrandJob();
	}
}
