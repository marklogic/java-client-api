package com.marklogic.client.admin.config.support;

import javax.xml.namespace.QName;

public interface Typed {
	public QName getType();
	public String getCollation();
}