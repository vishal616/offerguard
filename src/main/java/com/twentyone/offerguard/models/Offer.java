package com.twentyone.offerguard.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Offer {

	private String id;
	private String name;
	private String status;
	private String category;
	private String currency;
	private String price;
	private String model;
	private String startDate;
	private String endDate;
	private String previewUrl;
	private String countryAllowed;
	private String countryBlocked;
	private String cityAllowed;
	private String cityBlocked;
	private String osAllowed;
	private String osBlocked;
	private String deviceAllowed;
	private String deviceBlocked;
	private String ispAllowed;
	private String ispBlocked;
	private String clickUrl;
	private String impressionUrl;
	private String redirects;

}
