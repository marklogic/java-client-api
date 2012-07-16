package com.marklogic.client.admin.config.support;

import javax.xml.namespace.QName;

import com.marklogic.client.admin.config.support.GeospatialIndexType;

public interface GeospatialSpec {


	public QName getElement();
	
	public GeospatialIndexType getGeospatialIndexType();

	public QName getLatitude();

	public QName getLongitude();
	
	public QName getParent();
	
	public void setElement(QName element);
	
	public void setGeospatialIndexType(GeospatialIndexType geospatialIndexType);

	public void setLatitude(QName latitudeElement);	

	public void setLongitude(QName longitudeElement);

	public void setParent(QName parent);
	
	

}
