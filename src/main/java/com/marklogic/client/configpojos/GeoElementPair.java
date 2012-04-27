package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;



/**
 * A geospatial constraint type in which latitude and longitude are
 * modelled as text values within two separate elements.
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="geo-elem-pair")
public class GeoElementPair extends GeospatialConstraintDefinition<GeoElementPair> {


}
