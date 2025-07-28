/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * A sequence of RDF language string values.
 */
public interface RdfLangStringVal extends XsStringVal, RdfLangStringSeqVal, PlanParamBindingVal {
    public String getString();
    public String getLang();
}
