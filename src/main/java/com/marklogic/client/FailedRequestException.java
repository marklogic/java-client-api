package com.marklogic.client;

public class FailedRequestException extends RuntimeException {
	public FailedRequestException() {
		super();
	}
	public FailedRequestException(String message) {
		super(message);
	}
	public FailedRequestException(Throwable cause) {
		super(cause);
	}
	public FailedRequestException(String message, Throwable cause) {
		super(message, cause);
	}

}
