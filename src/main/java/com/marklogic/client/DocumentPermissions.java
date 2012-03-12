package com.marklogic.client;

import java.util.Map;
import java.util.Set;

public interface DocumentPermissions extends Map<String,Set<Capability>> {
    public void add(String role, Capability... capabilities);
}