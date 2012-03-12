package com.marklogic.client.iml;

import com.marklogic.client.TextDocument;
import com.marklogic.client.docio.TextReadHandle;
import com.marklogic.client.docio.TextWriteHandle;

class TextDocumentImpl
	extends AbstractDocumentImpl<TextReadHandle, TextWriteHandle>
	implements TextDocument
{
	TextDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
		setMimetype("text/plain");
	}
}
