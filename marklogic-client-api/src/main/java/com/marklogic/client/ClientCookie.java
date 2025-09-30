/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client;

import java.util.concurrent.TimeUnit;

/**
 * ClientCookie is a wrapper around the Cookie implementation so that the
 * underlying implementation can be changed.
 *
 */
public class ClientCookie {

	private final String name;
	private final String value;
	private final long expiresAt;
	private final String domain;
	private final String path;
	private final boolean secure;

	public ClientCookie(String name, String value, long expiresAt, String domain, String path, boolean secure) {
		this.name = name;
		this.value = value;
		this.expiresAt = expiresAt;
		this.domain = domain;
		this.path = path;
		this.secure = secure;
	}

	public boolean isSecure() {
		return secure;
	}

	public String getPath() {
		return path;
	}

	public String getDomain() {
		return domain;
	}

	public long expiresAt() {
		return expiresAt;
	}

	public String getName() {
		return name;
	}

	public int getMaxAge() {
		return (int) TimeUnit.MILLISECONDS.toSeconds(expiresAt - System.currentTimeMillis());
	}

	public String getValue() {
		return value;
	}
}
