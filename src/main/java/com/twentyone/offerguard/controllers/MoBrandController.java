package com.twentyone.offerguard.controllers;

import com.twentyone.offerguard.affiliateVendors.MoBrandVendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobrand")
@Slf4j
public class MoBrandController {

	@PostMapping("/")
	public void postAllMoBrandAsyncResponse(@RequestBody List<Object> response) {
		log.info("post mobrand rest api requested");
		log.info("response length {} with response like {}", response.size(), response);
		response.forEach((res)->{
			log.info(res.toString());
		});
	}

	@GetMapping("/job")
	public String triggerMoBrandJob() {
		log.info("trigger mo brand job rest api requested");
		return MoBrandVendor.triggerMobBrandJob();
	}
}
