package com.marklogic.client.config.support;

import javax.xml.namespace.QName;

import com.marklogic.client.config.GeospatialSpec;

public class GeospatialSpecImpl extends IndexSpecImpl implements GeospatialSpec {

	private GeospatialIndexType geospatialIndexType;
	private QName parent;
	private QName latitude;
	public GeospatialIndexType getGeospatialIndexType() {
		return geospatialIndexType;
	}

	public void setGeospatialType(GeospatialIndexType geospatialIndexType) {
		this.geospatialIndexType = geospatialIndexType;
	}

	public QName getParent() {
		return parent;
	}

	public QName getLatitude() {
		return latitude;
	}

	public QName getLongitude() {
		return longitude;
	}

	private QName longitude;
	
	public void setGeospatialIndexType(GeospatialIndexType geospatialIndexType) {
		this.geospatialIndexType = geospatialIndexType;
	}

	@Override
	public void setLatitude(QName latitudeElement) {
		this.latitude = latitudeElement;
	}

	@Override
	public void setParent(QName parent) {
		this.parent = parent;
	}

	@Override
	public void setLongitude(QName longitudeElement) {
		this.longitude = longitudeElement;
	}
	

}
