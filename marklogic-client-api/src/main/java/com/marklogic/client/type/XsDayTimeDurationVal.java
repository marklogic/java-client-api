/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

import javax.xml.datatype.Duration;

/**
 * An instance of a server day-time duration value.
 */
public interface XsDayTimeDurationVal extends XsDurationVal, XsDayTimeDurationSeqVal {
    public Duration getDuration();
}
