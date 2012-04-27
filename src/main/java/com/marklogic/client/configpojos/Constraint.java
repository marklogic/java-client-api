package com.marklogic.client.configpojos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


/**
 * Models a constraint node in Search API configuration.
 */
//TODO review method of dispatch/definition.
@XmlAccessorType(XmlAccessType.FIELD)
public class Constraint   {
	@XmlElement(namespace=Options.SEARCH_NS, name="annotation", required=false)
	private List<QueryAnnotation> annotations;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="collection")
	private Collection collection;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="value")
	private Value value;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="range")
	private Range range;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="word")
	private Word word;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="element-query")
	private ElementQuery elementQuery;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="properties")
	private Properties properties;
	
	@XmlElement(namespace=Options.SEARCH_NS,name="custom")
	private Custom custom;
	
	@XmlElement(namespace=Options.SEARCH_NS,name="geo-elem")
	private GeoElement geoElem;

	@XmlElement(namespace=Options.SEARCH_NS,name="geo-attr-pair")
	private GeoAttrPair geoAttrPair;
	
	@XmlElement(namespace=Options.SEARCH_NS,name="geo-elem-pair")
	private GeoElementPair geoElemPair;
	
	@XmlAttribute
	private String name;

	public Constraint() {
		annotations = new ArrayList<QueryAnnotation>();
	}
	
	public Constraint(String name) {
		this();
		setName(name);
	}

	public List<QueryAnnotation> getAnnotations() {
		return annotations;
	}
	
	@SuppressWarnings("rawtypes")
	public <T extends ConstraintDefinition> void setImplementation(T constraintDefinition) {
		if (constraintDefinition.getClass() == Collection.class) {
			collection = (Collection) constraintDefinition;
		} else if (constraintDefinition.getClass() == Value.class) {
			value = (Value) constraintDefinition;
		} else if (constraintDefinition.getClass() == Word.class) {
			word = (Word) constraintDefinition;
		} else if (constraintDefinition.getClass() == Range.class) {
			range = (Range) constraintDefinition;
		} else if (constraintDefinition.getClass() == ElementQuery.class) {
			elementQuery = (ElementQuery) constraintDefinition;
		} else if (constraintDefinition.getClass() == Properties.class) {
			properties = (Properties) constraintDefinition;
		} else if (constraintDefinition.getClass() == Custom.class) {
			custom = (Custom) constraintDefinition;
		} else if (constraintDefinition.getClass() == GeoElement.class) {
			geoElem = (GeoElement) constraintDefinition;
		} else if (constraintDefinition.getClass() == GeoAttrPair.class) {
			geoAttrPair = (GeoAttrPair) constraintDefinition;
		} else if (constraintDefinition.getClass() == GeoElementPair.class) {
			geoElemPair = (GeoElementPair) constraintDefinition;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	//TODO review generics
	public <T extends ConstraintDefinition> T getConstraintDefinition() {
		if (collection != null) {
			return (T) collection.inside(this);
		} else

		if (value != null) {
			return (T) value.inside(this);
		} else

		if (range != null) {
			return (T) range.inside(this);
		} else

		if (word != null) {
			return (T) word.inside(this);
		} else

		if (elementQuery != null) {
			return (T) elementQuery.inside(this);
		} else

		if (properties != null) {
			return (T) properties.inside(this);
		} else

		if (custom != null) {
			return (T) custom.inside(this);
		} else

		if (geoElem != null) {
			return (T) geoElem.inside(this);
		} else

		if (geoAttrPair != null) {
			return (T) geoAttrPair.inside(this);
		} else

		if (geoElemPair != null) {
			return (T) geoElemPair.inside(this);
		}
		return null;

	}

	public String getName() {
		return name;
	};

	public void setName(String name) {
		this.name = name;
	}

	
	
}
