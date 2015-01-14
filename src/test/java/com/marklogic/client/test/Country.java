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
package com.marklogic.client.test;

import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;

public class Country {
    private String name, continent, currencyCode, currencyName, isoCode;

    public String getIsoCode() {
        return isoCode;
    }

    public Country setIsoCode(String isoCode) {
        this.isoCode = isoCode;
        return this;
    }

    public String getName() {
        return name;
    }

    public Country setName(String name) {
        this.name = name;
        return this;
    }

    @PathIndexProperty(scalarType=ScalarType.STRING)
    public String getContinent() {
        return continent;
    }

    public Country setContinent(String continent) {
        this.continent = continent;
        return this;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Country setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public Country setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
        return this;
    }
}
