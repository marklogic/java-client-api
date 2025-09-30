/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.ClientCookie;
import com.marklogic.client.SessionState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class SessionStateImpl implements SessionState {

	private List<ClientCookie> cookies;
	private String sessionId;
	private AtomicBoolean setCreatedTimestamp;
	private Calendar created;

	public SessionStateImpl() {
		sessionId = Long.toUnsignedString(ThreadLocalRandom.current().nextLong(), 16);
		cookies = new ArrayList<>();
		setCreatedTimestamp = new AtomicBoolean(false);
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	List<ClientCookie> getCookies() {
		return cookies;
	}

	void setCookies(List<ClientCookie> cookies) {
		if (cookies != null) {
			if (setCreatedTimestamp.compareAndSet(false, true)) {
				for (ClientCookie cookie : cookies) {
					// Drop the SessionId cookie received from the server. We add it every
					// time we make a request with a SessionState object passed
					if (cookie.getName().equalsIgnoreCase("SessionId")) continue;
					this.cookies.add(cookie);
				}
				created = Calendar.getInstance();
			}
		}
	}

	Calendar getCreatedTimestamp() {
		return created;
	}
}
