package com.marklogic.client.config.search.impl;

import java.util.List;

import com.marklogic.client.config.search.Heatmap;

public class HeatmapImpl extends
		AbstractQueryOption<com.marklogic.client.config.search.jaxb.Heatmap>
		implements Heatmap {

	public HeatmapImpl(com.marklogic.client.config.search.jaxb.Heatmap hm) {
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
	public Object asJaxbObject() {
		return jaxbObject;
	}

	@Override
	public List<Object> getJAXBChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}
