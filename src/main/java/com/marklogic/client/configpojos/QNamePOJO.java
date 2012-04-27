package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;


@XmlAccessorType(XmlAccessType.FIELD)
public class QNamePOJO {

	public QNamePOJO() {
		
	}
	public String getNs() {
		return ns;
	}

	public String getName() {
		return name;
	}

	public QNamePOJO(String ns, String name) {
		this.ns = ns;
		this.name = name;
	}

	@XmlAttribute
	private String ns;
	
	@XmlAttribute
	private String name;

	public QName asQName() {
		return new QName(getNs(), getName());
	}
	
}