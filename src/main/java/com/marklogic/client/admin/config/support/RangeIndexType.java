package com.marklogic.client.admin.config.support;

import javax.xml.namespace.QName;


public class RangeIndexType implements Typed {
	public QName type;
	private String collation;
	public RangeIndexType(QName qname) {
		this.type = qname;
	}
	public RangeIndexType(String collation) {
		this.type = new QName("xs:string");
		this.collation=collation;
	}
	public String getCollation() {
		return collation;
	}
	public QName getType() {
		return type;
	}
}