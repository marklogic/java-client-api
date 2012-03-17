package com.marklogic.client.impl;

import com.marklogic.client.Format;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.DBResolver;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

class XMLDocumentImpl
    extends AbstractDocumentImpl<XMLReadHandle, XMLWriteHandle>
    implements XMLDocumentManager
{
	XMLDocumentImpl(RESTServices services) {
		super(services, Format.XML);
	}

	private DocumentRepair repair;
	public DocumentRepair getDocumentRepair() {
		return repair;
	}
	public void setDocumentRepair(DocumentRepair policy) {
		repair = policy;
	}

	public DBResolver newDBResolver() {
		return new DBResolverImpl(getServices());
	}
}
