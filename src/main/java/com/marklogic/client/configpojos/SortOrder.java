package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
public class SortOrder  {

	@XmlElement(namespace=Options.SEARCH_NS, name="score")
	private String score;
	
	@XmlAttribute
	private String direction;
	
	@XmlAttribute
	private QName type;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="element")
	private QNamePOJO elementReference;

	@XmlElement(namespace=Options.SEARCH_NS, name="attribute")
	private QNamePOJO attributeReference;

	@XmlElement(namespace=Options.SEARCH_NS, name="field")
	private Field fieldReference;
	
	public SortOrder withScore() {
		score = "";
		return this;
	}

	
	/**
	 * Add a reference to an element to this ConstraintDefinition
	 * @param ns Namespace URI of the element's QName;
	 * @param name Local name of the element.
	 * @return this
	 */
	public SortOrder withElement(String ns, String name) {
		this.elementReference = new QNamePOJO(ns, name);
		return this;
	}
	public SortOrder withElement(String name) {
		return this.withElement("", name);
	}
	
	public SortOrder withAttribute(String ns, String name) {
		this.attributeReference = new QNamePOJO(ns, name);
		return this;
	}


	public SortOrder withAttribute(String name) {
		return this.withAttribute("", name);
	}
	
	public QName getAttribute() {
		return attributeReference.asQName();
	}

	public QName getElement() {
		return elementReference.asQName();
	}

	public SortOrder withField(String name) {
		this.fieldReference = new Field(name);
		return this;

	}

	public String getField() {
		return this.fieldReference.getName();
	}
	

}
