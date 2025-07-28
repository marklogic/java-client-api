/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A CtsQueryDefinition represents all kinds of cts queries that can be performed.
 */
public interface CtsQueryDefinition extends SearchQueryDefinition{

    /**
     * Serialize the cts query it represents in AST(Abstract Sytax Tree) format.
     * @return serialized AST format of the cts query
     */
    String serialize();
}
