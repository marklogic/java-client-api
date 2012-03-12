package com.marklogic.client.iml;

import com.marklogic.client.XMLDocumentBuffer;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

class XMLDocumentImpl
    extends AbstractDocumentImpl<XMLReadHandle, XMLWriteHandle>
    implements XMLDocumentBuffer
{

	XMLDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
		setMimetype("application/xml");
	}

	private DocumentRepair repair;
	public DocumentRepair getDocumentRepair() {
		return repair;
	}
	public void setDocumentRepair(DocumentRepair policy) {
		repair = policy;
	}


}
