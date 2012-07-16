package com.marklogic.client.impl;

import javax.xml.namespace.QName;

import com.marklogic.client.admin.config.support.IndexSpecImpl;
import com.marklogic.client.admin.config.support.RangeIndexed;
import com.marklogic.client.admin.config.support.RangeSpec;

public class RangeSpecImpl extends IndexSpecImpl implements RangeSpec {

	private QName type;
	private String collation;

	public void setType(QName type) {
		this.type = type;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	@Override
	public QName getType() {
		return type;
	}

	@Override
	public String getCollation() {
		return collation;
	}

	public void build(RangeIndexed indexable) {
		indexable.setType(getType());
	    indexable.setCollation(getCollation());	
	    super.build(indexable);
	}
}
