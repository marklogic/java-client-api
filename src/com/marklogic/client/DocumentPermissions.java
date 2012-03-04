package com.marklogic.client;

import java.util.Map;
import java.util.Set;

public interface DocumentPermissions extends Map<String,Set<PermittedCapability>> {
    public void add(String role, PermittedCapability... capabilities);
}