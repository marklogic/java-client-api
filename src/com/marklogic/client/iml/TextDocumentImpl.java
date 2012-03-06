package com.marklogic.client.iml;

import com.marklogic.client.TextDocument;
import com.marklogic.client.abstractio.TextContentHandle;
import com.marklogic.client.abstractio.TextReadHandle;
import com.marklogic.client.abstractio.TextWriteHandle;

class TextDocumentImpl
	extends AbstractDocumentImpl<TextContentHandle, TextReadHandle, TextWriteHandle>
	implements TextDocument
{
	TextDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
	}
}
