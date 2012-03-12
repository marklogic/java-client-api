package com.marklogic.client.iml;

import com.marklogic.client.GenericDocumentBuffer;
import com.marklogic.client.docio.GenericReadHandle;
import com.marklogic.client.docio.GenericWriteHandle;

public class GenericDocumentImpl
    extends AbstractDocumentImpl<GenericReadHandle, GenericWriteHandle>
    implements GenericDocumentBuffer
{
	GenericDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
	}

}
