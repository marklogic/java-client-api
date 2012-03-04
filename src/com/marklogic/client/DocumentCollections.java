package com.marklogic.client;

import java.util.Set;

public interface DocumentCollections extends Set<String> {
    public void setCollections(String... collections);
}