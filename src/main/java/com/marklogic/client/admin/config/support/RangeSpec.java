package com.marklogic.client.admin.config.support;

import javax.xml.namespace.QName;


public interface RangeSpec extends Typed, IndexSpec {
	
	public void setType(QName type);

	public void setCollation(String collation);

	public QName getType();

	public String getCollation();
	
	public void build(RangeIndexed rangeIndexed);
	

}