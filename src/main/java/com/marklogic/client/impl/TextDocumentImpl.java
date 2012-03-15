package com.marklogic.client.impl;

import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.docio.TextReadHandle;
import com.marklogic.client.docio.TextWriteHandle;

class TextDocumentImpl
	extends AbstractDocumentImpl<TextReadHandle, TextWriteHandle>
	implements TextDocumentManager
{
	TextDocumentImpl(RESTServices services) {
		super(services, "text/plain");
	}
}
