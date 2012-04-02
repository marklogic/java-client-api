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

public interface Heatmap extends JAXBBackedQueryOption {
	/**
     * Gets the northern boundary of the box.
     * 
     */
    public double getN();

    /**
     * Sets the northern boundary of the box.
     * 
     */
    public void setN(double value);

    /**
     * Gets the Southern boundary of the box.
     * 
     */
    public double getS();

    /**
     * Sets the Southern boundary of the box.
     * 
     */
    public void setS(double value);

    /**
     * Gets the Eastern boundary of the box.
     * 
     */
    public double getE();

    /**
     * Sets the Eastern boundary of the box.
     * 
     */
    public void setE(double value);
    /**
     * Gets the Western boundary of the box.
     * 
     */
    public double getW();

    /**
     * Sets the Western boundary of the box.
     * 
     */
    public void setW(double value);

    /**
     * Gets the number of latitudinal divisions.
     * 
     */
    public long getLatdivs();

    /**
     * Sets the number of latitudinal divisions.
     * 
     */
    public void setLatdivs(long value);
    
    /**
     * Gets the number of longitudinal divisions.
     * 
     */
    public long getLondivs();

    /**
     * Sets the number of longitudinal divisions.
     * 
     */
    public void setLondivs(long value);
}
