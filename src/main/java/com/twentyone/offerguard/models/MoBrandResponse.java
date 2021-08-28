package com.twentyone.offerguard.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoBrandResponse {

	private String id;
	private List<MoBrandUrl> urls;
	private String bundleIdMatch;
	private List<Object> resultFlags;
	private String label;
	private String screenshotUrl;
	private String status;

	@JsonProperty("nRedir")
	private String redirects;
}