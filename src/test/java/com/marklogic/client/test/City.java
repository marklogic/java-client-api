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

import javax.xml.bind.annotation.XmlRootElement;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;
import com.marklogic.client.pojo.annotation.GeospatialPathIndexProperty;
import com.marklogic.client.pojo.annotation.GeospatialLatitude;
import com.marklogic.client.pojo.annotation.GeospatialLongitude;

@XmlRootElement
public class City {
    private int geoNameId;
    private String name;
    private String asciiName;
    private String[] alternateNames;
    private double latitude;
    private double longitude;
    private String latLong;
    private String countryIsoCode;
    private String countryName;
    private String continent;
    private String currencyCode;
    private String currencyName;
    @PathIndexProperty(scalarType=ScalarType.LONG)
    private long population;
    private int elevation;
    private Country country;

    @Id
    public int getGeoNameId() {
        return geoNameId;
    }

    public City setGeoNameId(int geoNameId) {
        this.geoNameId = geoNameId;
        return this;
    }

    public String getName() {
        return name;
    }

    public City setName(String name) {
        this.name = name;
        return this;
    }

    public String getAsciiName() {
        return asciiName;
    }

    public City setAsciiName(String asciiName) {
        this.asciiName = asciiName;
        return this;
    }

    public String[] getAlternateNames() {
        return alternateNames;
    }

    public City setAlternateNames(String[] alternateNames) {
        this.alternateNames = alternateNames;
        return this;
    }

    @GeospatialLatitude
    public double getLatitude() {
        return latitude;
    }

    public City setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    @GeospatialLongitude
    public City setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    @GeospatialPathIndexProperty
    public String getLatLong() {
        return latLong;
    }

    public City setLatLong(String latLong) {
        this.latLong = latLong;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public City setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }

    public String getCountryName() {
        return countryName;
    }

    public City setCountryName(String countryName) {
        this.countryName = countryName;
        return this;
    }

    public String getContinent() {
        return continent;
    }

    public City setContinent(String continent) {
        this.continent = continent;
        return this;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public City setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public City setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
        return this;
    }

    public long getPopulation() {
        return population;
    }

    public City setPopulation(long population) {
        this.population = population;
        return this;
    }

    public int getElevation() {
        return elevation;
    }

    public City setElevation(int elevation) {
        this.elevation = elevation;
        return this;
    }

    public Country getCountry() {
        return country;
    }

    public City setCountry(Country country) {
        this.country = country;
        return this;
    }
}
