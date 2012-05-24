package com.marklogic.client;

import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

public interface ResourceServices {
	public String getResourceName();

	public <R extends AbstractReadHandle> R get(RequestParameters params, R output);
	public <R extends AbstractReadHandle> R get(RequestParameters params, Transaction transaction, R output);
	public <R extends AbstractReadHandle> R[] get(RequestParameters params, R[] output);
	public <R extends AbstractReadHandle> R[] get(RequestParameters params, Transaction transaction, R[] output);

	public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, R output);
	public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, R output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, Transaction transaction, R output);

	public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, R output);
	public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output);
	public <R extends AbstractReadHandle> R[] post(RequestParameters params, AbstractWriteHandle input, R[] output);
	public <R extends AbstractReadHandle> R[] post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R[] output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, R output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, Transaction transaction, R output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R[] post(RequestParameters params, W[] input, R[] output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R[] post(RequestParameters params, W[] input, Transaction transaction, R[] output);

	public <R extends AbstractReadHandle> R delete(RequestParameters params, R output);
	public <R extends AbstractReadHandle> R delete(RequestParameters params, Transaction transaction, R output);

	// for debugging client requests
    public void startLogging(RequestLogger logger);
    public RequestLogger getRequestLogger();
    public void stopLogging();
}
