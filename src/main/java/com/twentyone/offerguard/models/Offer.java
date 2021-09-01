package com.twentyone.offerguard.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "offers")
public class Offer {

	@Id
	@JsonProperty("offerid")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("status")
	private String status;

	@JsonProperty("category")
	private String category;

	@JsonProperty("currency")
	private String currency;

	@JsonProperty("price")
	private String price;

	@JsonProperty("model")
	private String model;

	@JsonProperty("date_start")
	private String startDate;

	@JsonProperty("date_end")
	private String endDate;

	@JsonProperty("preview_url")
	private String previewUrl;

	@JsonProperty("country_allow")
	private String countryAllowed;

	@JsonProperty("country_block")
	private String countryBlocked;

	@JsonProperty("city_allow")
	private String cityAllowed;

	@JsonProperty("city_block")
	private String cityBlocked;

	@JsonProperty("os_allow")
	private String osAllowed;

	@JsonProperty("os_block")
	private String osBlocked;

	@JsonProperty("device_allow")
	private String deviceAllowed;

	@JsonProperty("device_block")
	private String deviceBlocked;

	@JsonProperty("isp_allow")
	private String ispAllowed;

	@JsonProperty("isp_block")
	private String ispBlocked;

	@JsonProperty("click_url")
	private String clickUrl;

	@JsonProperty("impression_url")
	private String impressionUrl;

	private String redirects;

	private String affiliateStatus;

	@Transient
	private String mobileMarketingPlatforms;

}
