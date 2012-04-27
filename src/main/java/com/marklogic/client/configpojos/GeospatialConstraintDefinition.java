package com.marklogic.client.configpojos;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public abstract class GeospatialConstraintDefinition<T extends GeospatialConstraintDefinition<T>> extends ConstraintDefinition<T> {

	@XmlElement(namespace=Options.SEARCH_NS, name="parent")
	private QNamePOJO parent;

	@XmlElement(namespace=Options.SEARCH_NS, name="lat")
	private QNamePOJO latitude;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="lon")
	private QNamePOJO longitude;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="heatmap")
	private Heatmap heatmap;

	@XmlElement(namespace=Options.SEARCH_NS, name="geo-option")
	private List<String> geoOptions;


	public QNamePOJO getParent() {
		return parent;
	}


	public void setParent(QNamePOJO parent) {
		this.parent = parent;
	}


	public QNamePOJO getLatitude() {
		return latitude;
	}


	public void setLatitude(QNamePOJO latitude) {
		this.latitude = latitude;
	}


	public QNamePOJO getLongitude() {
		return longitude;
	}


	public void setLongitude(QNamePOJO longitude) {
		this.longitude = longitude;
	}


	public Heatmap getHeatmap() {
		return heatmap;
	}


	public void setHeatmap(Heatmap heatmap) {
		this.heatmap = heatmap;
	}

	@SuppressWarnings("unchecked")
	public T withGeoOptions(String geoOption) {
		this.geoOptions.add(geoOption);
		return (T) this;
	}
}
