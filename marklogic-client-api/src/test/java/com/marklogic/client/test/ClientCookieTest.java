/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.ClientCookie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientCookieTest {

    @Test
    void toStringReturnsNameEqualsValue() {
        ClientCookie cookie = new ClientCookie("HostId", "abc123", Long.MAX_VALUE, "localhost", "/", false);
        assertEquals("HostId=abc123", cookie.toString(),
            "ClientCookie.toString() must return name=value for correct Cookie header formatting");
    }

    @Test
    void toStringWithEmptyValue() {
        ClientCookie cookie = new ClientCookie("SessionId", "", Long.MAX_VALUE, "localhost", "/", false);
        assertEquals("SessionId=", cookie.toString());
    }
}
