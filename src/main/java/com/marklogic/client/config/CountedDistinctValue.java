/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * A CountedDistinctValue is a value that includes a frequency.
 */
public class CountedDistinctValue {
    @XmlAttribute(name = "frequency")
    long frequency;

    @XmlValue
    String value;

    /**
     * Returns the frequency associated with this value.
     * @return The frequency.
     */
    public long getCount() {
        return frequency;
    }

    /**
     * Returns the value cast to the specified type.
     *
     * See DistinctValue.getValue() for a list of the supported types.
     *
     * @param as The class parameter
     * @param <T> The class to cast to
     * @return The value, cast to the specified type or
     */
    public <T> T get(Class<T> as) {
        return DistinctValue.getValue(value, as);
    }
}
