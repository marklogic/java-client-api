package com.marklogic.client;

public class ForbiddenUserException extends RuntimeException {
	public ForbiddenUserException() {
		super();
	}
	public ForbiddenUserException(String message) {
		super(message);
	}
	public ForbiddenUserException(Throwable cause) {
		super(cause);
	}
	public ForbiddenUserException(String message, Throwable cause) {
		super(message, cause);
	}

}
