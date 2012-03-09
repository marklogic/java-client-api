package com.marklogic.client.iml;

import com.marklogic.client.GenericDocument;
import com.marklogic.client.docio.GenericReadHandle;
import com.marklogic.client.docio.GenericWriteHandle;

public class GenericDocumentImpl
    extends AbstractDocumentImpl<GenericReadHandle, GenericWriteHandle>
    implements GenericDocument
{
	GenericDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
	}

}
