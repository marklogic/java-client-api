package com.marklogic.client.impl;

import com.marklogic.client.TextDocumentBuffer;
import com.marklogic.client.docio.TextReadHandle;
import com.marklogic.client.docio.TextWriteHandle;

class TextDocumentImpl
	extends AbstractDocumentImpl<TextReadHandle, TextWriteHandle>
	implements TextDocumentBuffer
{
	TextDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
		setMimetype("text/plain");
	}
}
