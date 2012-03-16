package com.marklogic.client.docio;

import com.marklogic.client.Format;

public interface AbstractWriteHandle<C> {
	public Format getFormat();
	public void setFormat(Format format);

	public C sendContent();
}
