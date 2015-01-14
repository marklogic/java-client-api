/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.marklogic.client.MarkLogicIOException;

/**
 * A CountedDistinctValue is a value that includes a frequency.
 */
public class ValuesMetricImpl {
    private static DatatypeFactory dtFactory = null;
    private static Calendar now = Calendar.getInstance();

    private static DatatypeFactory getDatatypeFactory() {
        if (dtFactory == null) {
            try {
                dtFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException dce) {
                throw new MarkLogicIOException("Cannot instantiate datatypeFactory", dce);
            }
        }
        return dtFactory;
    }

    public ValuesMetricImpl() {
    }

    @XmlValue
    String value;

    /**
     * Returns the metric as a long number of milliseconds.
     *
     * @return The metric.
     */
    public long getValue() {
        return parseTime(value);
    }

    private long parseTime(String time) {
        Duration d = getDatatypeFactory().newDurationDayTime(time);
        return d.getTimeInMillis(now);
    }
}
