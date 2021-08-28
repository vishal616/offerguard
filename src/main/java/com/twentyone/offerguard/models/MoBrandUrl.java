package com.twentyone.offerguard.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoBrandUrl {

	private String url;
	private String loadTime;
	private String code;
	private String contentType;
	private List<Object> resultStates;

}
