package com.marklogic.client.docio;

public interface AbstractReadHandle<C> {
	public Class<C> receiveAs();
	public void receiveContent(C content);
}
