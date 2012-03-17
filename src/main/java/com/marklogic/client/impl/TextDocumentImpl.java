package com.marklogic.client.impl;

import com.marklogic.client.Format;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;

class TextDocumentImpl
	extends AbstractDocumentImpl<TextReadHandle, TextWriteHandle>
	implements TextDocumentManager
{
	TextDocumentImpl(RESTServices services) {
		super(services, Format.TEXT);
	}
}
