package com.marklogic.client;

import com.marklogic.client.impl.FailedRequest;


public abstract class MarkLogicServerException extends RuntimeException {

	
	private FailedRequest failedRequest;
	
	public MarkLogicServerException(String localMessage, FailedRequest failedRequest) {
		super(localMessage);
		this.failedRequest = failedRequest;
	}

	public MarkLogicServerException(String localMessage) {
		super(localMessage);
	}
	
	@Override
	public String getMessage() {
		if (super.getMessage() != null && failedRequest != null) {
			return "Local message: " + super.getMessage() + "Server Message: " + failedRequest.getMessage();
		}
		else if (failedRequest != null) {
			return failedRequest.getMessage();
		}
		else return super.getMessage();
	}
	
	public FailedRequest getFailedRequest() {
		return failedRequest;
	}
}

