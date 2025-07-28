/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

// IMPORTANT: Do not edit. This file is generated.

/**
 * An option controlling whether to check for duplicate triples (which is more expensive) or
 * to supply a base IRI for triples IRIs.
 */
public interface PlanSparqlOptions {
    XsStringVal getBase();
    PlanSparqlOptions withBase(String base);
    PlanSparqlOptions withBase(XsStringVal base);
    XsBooleanVal getDeduplicated();
    PlanSparqlOptions withDeduplicated(boolean deduplicate);
    PlanSparqlOptions withDeduplicated(XsBooleanVal deduplicate);
}
