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

import javax.xml.bind.annotation.XmlElement;

import com.marklogic.client.impl.ValuesBuilder.Values;
import com.marklogic.client.query.ValuesMetrics;

/**
 * A CountedDistinctValue is a value that includes a frequency.
 */
public class ValuesMetricsImpl implements ValuesMetrics {
    @XmlElement(namespace = Values.VALUES_NS, name = "values-resolution-time")
    private ValuesMetricImpl valuesResolutionTime;

    @XmlElement(namespace = Values.VALUES_NS, name = "aggregate-resolution-time")
    private ValuesMetricImpl aggregateResolutionTime;

    @XmlElement(namespace = Values.VALUES_NS, name = "total-time")
    private ValuesMetricImpl totalTime;

    public long getValuesResolutionTime() {
        if (valuesResolutionTime == null) {
            return -1;
        }
        return valuesResolutionTime.getValue();
    }

    public long getAggregateResolutionTime() {
        if (aggregateResolutionTime == null) {
            return -1;
        }
        return aggregateResolutionTime.getValue();
    }

    public long getTotalTime() {
        if (totalTime == null) {
            return -1;
        }
        return totalTime.getValue();
    }
}
