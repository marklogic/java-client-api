package com.marklogic.client.impl;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * DigestChallengeFilter is a work around for a Jersey bug: http://java.net/jira/browse/JERSEY-1445
 */
class DigestChallengeFilter extends ClientFilter {
	DigestChallengeFilter() {
		super();
	}

	@Override
	public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        ClientResponse response = getNext().handle(request);

        if (response.getClientResponseStatus() == Status.UNAUTHORIZED) {
        	// NOTE: ApacheHttpClient4Handler.handle() also calls response.bufferEntity()
        	response.close();
        }

		return response;
	}
}
