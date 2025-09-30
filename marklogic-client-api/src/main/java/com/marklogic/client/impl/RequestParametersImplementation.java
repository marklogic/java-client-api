/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RequestParametersImplementation {

	// Prior to 8.0.0, this was a threadsafe map. However, that fact was not documented for a user. And in practice,
	// it would not make sense for multiple threads to share a mutable instance of this, or of one of its subclasses.
	// Additionally, the impl was from the 'javax.ws.rs:javax.ws.rs-api:2.1.1' dependency which wasn't used for
	// anything else. So for 8.0.0, this is now simply a map that matches the intended usage of this class and its
	// subclasses, which is to be used by a single thread.
	private final Map<String, List<String>> map = new HashMap<>();

	protected RequestParametersImplementation() {
		super();
	}

	protected Map<String, List<String>> getMap() {
		return map;
	}
}
