package com.marklogic.client;

/**
 * An InternalException suggests a defect in the API.  Please contact MarkLogic support.
 */
public class MarkLogicInternalException extends RuntimeException {
	public MarkLogicInternalException() {
		super();
	}
	public MarkLogicInternalException(String message) {
		super(message);
	}
	public MarkLogicInternalException(Throwable cause) {
		super(cause);
	}
	public MarkLogicInternalException(String message, Throwable cause) {
		super(message, cause);
	}

}
