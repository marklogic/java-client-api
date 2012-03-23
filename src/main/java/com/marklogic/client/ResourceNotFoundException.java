package com.marklogic.client;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException() {
		super();
	}
	public ResourceNotFoundException(String message) {
		super(message);
	}
	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}
	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
