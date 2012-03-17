package com.marklogic.client.impl;

import com.marklogic.client.Format;
import com.marklogic.client.GenericDocumentManager;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;

public class GenericDocumentImpl
    extends AbstractDocumentImpl<GenericReadHandle, GenericWriteHandle>
    implements GenericDocumentManager
{
	GenericDocumentImpl(RESTServices services) {
		super(services, Format.UNKNOWN);
	}

}
