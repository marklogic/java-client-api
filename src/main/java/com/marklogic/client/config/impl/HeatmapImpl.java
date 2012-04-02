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
package com.marklogic.client.config.impl;

import java.util.List;

import com.marklogic.client.config.Heatmap;

public class HeatmapImpl extends
		AbstractQueryOption<com.marklogic.client.config.search.jaxb.Heatmap>
		implements Heatmap {

	HeatmapImpl(com.marklogic.client.config.search.jaxb.Heatmap hm) {
		jaxbObject = hm;
	}

	/**
	 * Gets the northern boundary of the box.
	 * 
	 */
	public double getN() {
		return jaxbObject.getN();
	}

	/**
	 * Sets the northern boundary of the box.
	 * 
	 */
	public void setN(double value) {
		jaxbObject.setN(value);
	}

	/**
	 * Gets the Southern boundary of the box.
	 * 
	 */
	public double getS() {
		return jaxbObject.getS();
	}

	/**
	 * Sets the Southern boundary of the box.
	 * 
	 */
	public void setS(double value) {
		jaxbObject.setS(value);
	}

	/**
	 * Gets the Eastern boundary of the box.
	 * 
	 */
	public double getE() {
		return jaxbObject.getE();
	}

	/**
	 * Sets the Eastern boundary of the box.
	 * 
	 */
	public void setE(double value) {
		jaxbObject.setE(value);
	}

	/**
	 * Gets the Western boundary of the box.
	 * 
	 */
	public double getW() {
		return jaxbObject.getW();
	}

	/**
	 * Sets the Western boundary of the box.
	 * 
	 */
	public void setW(double value) {
		jaxbObject.setW(value);
	}

	/**
	 * Gets the number of latitudinal divisions.
	 * 
	 */
	public long getLatdivs() {
		return jaxbObject.getLatdivs();
	}

	/**
	 * Sets the number of latitudinal divisions.
	 * 
	 */
	public void setLatdivs(long value) {
		jaxbObject.setLatdivs(value);
	}

	/**
	 * Gets the number of longitudinal divisions.
	 * 
	 */
	public long getLondivs() {
		return jaxbObject.getLondivs();
	}

	/**
	 * Sets the number of longitudinal divisions.
	 * 
	 */
	public void setLondivs(long value) {
		jaxbObject.setLondivs(value);
	}

	@Override
	public Object asJAXB() {
		return jaxbObject;
	}

	@Override
	public List<Object> getJAXBChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}
