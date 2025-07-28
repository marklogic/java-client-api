/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * A sequence of RDF language string values.
 */
public interface RdfLangStringVal extends XsStringVal, RdfLangStringSeqVal, PlanParamBindingVal {
    public String getString();
    public String getLang();
}
