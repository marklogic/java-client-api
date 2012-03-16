package com.marklogic.client.docio;

import com.marklogic.client.Format;

public interface AbstractReadHandle<C> {
	public Format getFormat();
	public void setFormat(Format format);

	public Class<C> receiveAs();
	public void receiveContent(C content);
}
