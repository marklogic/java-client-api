package com.marklogic.client.abstractio;

public interface AbstractContentHandle<C> {
	public Class<C> handles();
	public C get();
	public void set(C content);
}
