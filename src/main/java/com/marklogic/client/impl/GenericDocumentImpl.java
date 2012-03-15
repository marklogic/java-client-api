package com.marklogic.client.impl;

import com.marklogic.client.GenericDocumentManager;
import com.marklogic.client.docio.GenericReadHandle;
import com.marklogic.client.docio.GenericWriteHandle;

public class GenericDocumentImpl
    extends AbstractDocumentImpl<GenericReadHandle, GenericWriteHandle>
    implements GenericDocumentManager
{
	GenericDocumentImpl(RESTServices services) {
		super(services);
	}

}
