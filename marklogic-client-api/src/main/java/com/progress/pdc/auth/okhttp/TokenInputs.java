/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.progress.pdc.auth.okhttp;

/**
 * Defines the inputs for obtaining a token from PDC based on an API key.
 *
 * @since 8.1.0
 */
public interface TokenInputs {

	default String getTokenEndpoint() {
		return "/token";
	}

	default String getGrantType() {
		return "api_key";
	}

	String getApiKey();

	default Integer getTokenDuration() {
		return null;
	}
}
