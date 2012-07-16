package com.marklogic.client.config.support;

import javax.xml.namespace.QName;

public interface RangeIndexed extends Indexed {

	void setType(QName type);

	void setCollation(String collation);

}
