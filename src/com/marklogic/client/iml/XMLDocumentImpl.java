package com.marklogic.client.iml;

import com.marklogic.client.XMLDocument;
import com.marklogic.client.abstractio.XMLContentHandle;
import com.marklogic.client.abstractio.XMLReadHandle;
import com.marklogic.client.abstractio.XMLWriteHandle;

class XMLDocumentImpl
    extends AbstractDocumentImpl<XMLContentHandle, XMLReadHandle, XMLWriteHandle>
    implements XMLDocument
{

	XMLDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
	}

	private DocumentRepair repair;
	public DocumentRepair getDocumentRepair() {
		return repair;
	}
	public void setDocumentRepair(DocumentRepair policy) {
		repair = policy;
	}


}
