package com.marklogic.client;

import javax.ws.rs.core.MultivaluedMap;

public class RequestParametersAccessor {
	static public MultivaluedMap<String, String> getMap(RequestParameters params) {
		return params.getMap();
	}
}
