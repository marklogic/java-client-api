package com.marklogic.client;

import java.util.List;
import java.util.Map;

public class ServerTransform extends RequestParameters {
	private String name;

	public ServerTransform(String name) {
		super();
    	this.name = name;
	}

	public String getName() {
		return name;
	}

	public RequestParameters merge(RequestParameters currentParams) {
		RequestParameters params = (currentParams != null) ?
				currentParams : new RequestParameters();
		params.put("transform", getName());

		for (Map.Entry<String, List<String>> entry: entrySet()) {
			params.put("trans:"+entry.getKey(), entry.getValue());
		}

		return params;
	}
}
