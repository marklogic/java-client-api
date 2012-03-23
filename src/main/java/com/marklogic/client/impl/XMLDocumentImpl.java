package com.marklogic.client.impl;

import java.util.HashMap;
import java.util.Map;

import com.marklogic.client.Format;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.DBResolver;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

class XMLDocumentImpl
    extends AbstractDocumentImpl<XMLReadHandle, XMLWriteHandle>
    implements XMLDocumentManager
{
	private DocumentRepair repair;

	XMLDocumentImpl(RESTServices services) {
		super(services, Format.XML);
	}

	public DocumentRepair getDocumentRepair() {
		return repair;
	}
	public void setDocumentRepair(DocumentRepair policy) {
		repair = policy;
	}

	protected Map<String,String> getWriteParams() {
		if (repair == null)
			return null;

		HashMap<String,String> params = new HashMap<String,String>();
		if (repair == DocumentRepair.FULL)
			params.put("repair", "full");
		else if (repair == DocumentRepair.NONE)
			params.put("repair", "none");
		else
			throw new RuntimeException("Internal error - unknown repair policy: "+repair.name());

		return params;
	}

	public DBResolver newDBResolver() {
		return new DBResolverImpl(getServices());
	}
}
