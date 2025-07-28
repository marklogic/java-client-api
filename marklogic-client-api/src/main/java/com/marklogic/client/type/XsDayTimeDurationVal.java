/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

import javax.xml.datatype.Duration;

/**
 * An instance of a server day-time duration value.
 */
public interface XsDayTimeDurationVal extends XsDurationVal, XsDayTimeDurationSeqVal {
    public Duration getDuration();
}
