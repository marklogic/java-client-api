/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * ClientCookie is a wrapper around the Cookie implementation so that the
 * underlying implementation can be changed.
 *
 */
public class ClientCookie {
  Cookie cookie;

  ClientCookie(String name, String value, long expiresAt, String domain, String path,
      boolean secure) {
    Cookie.Builder cookieBldr = new Cookie.Builder()
           .domain(domain)
           .path(path)
           .name(name)
           .value(value)
           .expiresAt(expiresAt);
    if ( secure == true ) cookieBldr = cookieBldr.secure();
    this.cookie = cookieBldr.build();
  }

  public ClientCookie(ClientCookie cookie) {
    this(cookie.getName(), cookie.getValue(), cookie.expiresAt(), cookie.getDomain(), cookie.getPath(),
        cookie.isSecure());
  }

  public boolean isSecure() {
    return cookie.secure();
  }

  public String getPath() {
    return cookie.path();
  }

  public String getDomain() {
    return cookie.domain();
  }

  public long expiresAt() {
    return cookie.expiresAt();
  }

  public String getName() {
    return cookie.name();
  }

  public int getMaxAge() {
    return (int) TimeUnit.MILLISECONDS.toSeconds(cookie.expiresAt() - System.currentTimeMillis());
  }
  public String getValue() {
    return cookie.value();
  }

  public static ClientCookie parse(HttpUrl url, String setCookie) {
    Cookie cookie = Cookie.parse(url, setCookie);
    if(cookie == null) throw new IllegalStateException(setCookie + "is not a well-formed cookie");
    return new ClientCookie(cookie.name(), cookie.value(), cookie.expiresAt(), cookie.domain(), cookie.path(),
        cookie.secure());
  }

  public String toString() {
    return cookie.toString();
  }
}
