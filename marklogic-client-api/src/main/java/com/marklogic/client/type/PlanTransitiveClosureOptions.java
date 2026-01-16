/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

// IMPORTANT: Do not edit. This file is generated.

/**
 * Options for controlling transitive closure operations, including minimum and maximum
 * path lengths.
 */
public interface PlanTransitiveClosureOptions {
    XsLongVal getMinLength();
    PlanTransitiveClosureOptions withMinLength(long minLength);
    PlanTransitiveClosureOptions withMinLength(XsLongVal minLength);
    XsLongVal getMaxLength();
    PlanTransitiveClosureOptions withMaxLength(long maxLength);
    PlanTransitiveClosureOptions withMaxLength(XsLongVal maxLength);
}
