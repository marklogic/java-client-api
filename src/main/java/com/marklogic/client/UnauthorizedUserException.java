package com.marklogic.client;

public class UnauthorizedUserException extends RuntimeException {
	public UnauthorizedUserException() {
		super();
	}
	public UnauthorizedUserException(String message) {
		super(message);
	}
	public UnauthorizedUserException(Throwable cause) {
		super(cause);
	}
	public UnauthorizedUserException(String message, Throwable cause) {
		super(message, cause);
	}

}
