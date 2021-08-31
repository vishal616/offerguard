package com.twentyone.offerguard.controllers;

import com.twentyone.offerguard.models.RedirectUrl;
import com.twentyone.offerguard.repositories.RedirectUrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/redirects")
@Slf4j
public class RedirectUrlController {

	@Autowired
	private RedirectUrlRepository redirectUrlRepository;

	@GetMapping("/")
	public List<RedirectUrl> getAllRedirectUrls() {
		log.info("get all redirects urls rest api requested");
		return redirectUrlRepository.findAll();
	}
}
