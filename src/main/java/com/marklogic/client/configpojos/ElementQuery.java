package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="element-query")
public class ElementQuery extends ConstraintDefinition<ElementQuery> {

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNs() {
		return ns;
	}
	public void setNs(String ns) {
		this.ns = ns;
	}
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String ns;
	

}
