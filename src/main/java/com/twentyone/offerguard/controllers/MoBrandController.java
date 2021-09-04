package com.twentyone.offerguard.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
