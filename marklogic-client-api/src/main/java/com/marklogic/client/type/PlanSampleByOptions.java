/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

// IMPORTANT: Do not edit. This file is generated.

/**
 * An option controlling the sampling of rows from view, triple, or lexicon indexes
 * for a row pipeline.
 */
public interface PlanSampleByOptions {
    XsIntVal getLimit();
    PlanSampleByOptions withLimit(int limit);
    PlanSampleByOptions withLimit(XsIntVal limit);
}
