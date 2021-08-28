package com.twentyone.offerguard.models;

import java.util.List;

public class MoBrandResponse {

	private String id;
	private List<MobBrandUrl> urls;
	private String bundleIdMatch;
	private String resultFlags;
	private String label;
	private String screenshotUrl;
	private String status;
	private String nRedir;
}

class MobBrandUrl {
	private String url;
	private String loadTime;
	private String code;
	private String contentType;
	private List<Object> resultStates;
}
