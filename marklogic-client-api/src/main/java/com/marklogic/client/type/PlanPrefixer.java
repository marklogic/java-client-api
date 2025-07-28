/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

//IMPORTANT: Do not edit. This file is generated.

/**
 * An instance of a convenience that can prepend a base URL
 * when creating iteral semantic IRI values for use in triple
 * patterns for a row pipeline.
 */
public interface PlanPrefixer extends PlanPrefixerSeq {
    public SemIriVal iri(String name);
    public SemIriVal iri(XsStringVal name);
}
