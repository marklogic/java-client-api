package com.marklogic.client.impl;

import com.marklogic.client.MethodType;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.ResourceServices;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.HandleAccessor;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

class ResourceServicesImpl
    extends AbstractLoggingManager
    implements ResourceServices
{
	private String       resourceName;
	private RESTServices services;

	ResourceServicesImpl(RESTServices services, String resourceName) {
		super();
		this.services     = services;
		this.resourceName = resourceName;
	}

	@Override
	public String getResourceName() {
		return resourceName;
	}
	private String getResourcePath() {
		return "resources/"+getResourceName();
	}

	@Override
	public <R extends AbstractReadHandle> R get(RequestParameters params, R output) {
		return get(params, null, output);
	}
	@Override
	public <R extends AbstractReadHandle> R get(RequestParameters params, Transaction transaction, R output) {
		return makeResult(
				execMethod(MethodType.GET, prepareParams(params, transaction), output),
				output
				);
	}
	@Override
	public <R extends AbstractReadHandle> R[] get(RequestParameters params, R[] output) {
		return get(params, null, output);
	}
	@Override
	public <R extends AbstractReadHandle> R[] get(RequestParameters params, Transaction transaction, R[] output) {
		return makeResult(
				execMethod(MethodType.GET, prepareParams(params, transaction), output),
				output
				);
	}

	@Override
	public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, R output) {
		return put(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output) {
		return makeResult(
				execMethod(MethodType.PUT, prepareParams(params, transaction), input, output),
				output
				);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, R output) {
		return put(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, Transaction transaction, R output) {
		return makeResult(
				execMethod(MethodType.PUT, prepareParams(params, transaction), input, output),
				output
				);
	}

	@Override
	public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, R output) {
		return post(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output) {
		return makeResult(
				execMethod(MethodType.POST, prepareParams(params, transaction), input, output),
				output
				);
	}
	@Override
	public <R extends AbstractReadHandle> R[] post(RequestParameters params, AbstractWriteHandle input, R[] output) {
		return post(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle> R[] post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R[] output) {
		return makeResult(
				execMethod(MethodType.POST, prepareParams(params, transaction), input, output),
				output
				);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, R output) {
		return post(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, Transaction transaction, R output) {
		return makeResult(
				execMethod(MethodType.POST, prepareParams(params, transaction), input, output),
				output
				);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R[] post(RequestParameters params, W[] input, R[] output) {
		return post(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R[] post(RequestParameters params, W[] input, Transaction transaction, R[] output) {
		return makeResult(
				execMethod(MethodType.POST, prepareParams(params, transaction), input, output),
				output
				);
	}

	@Override
	public <R extends AbstractReadHandle> R delete(RequestParameters params, R output) {
		return delete(params, null, output);
	}
	@Override
	public <R extends AbstractReadHandle> R delete(RequestParameters params, Transaction transaction, R output) {
		return makeResult(
				execMethod(MethodType.DELETE, prepareParams(params, transaction), output),
				output
				);
	}

	@Override
    public RequestLogger getRequestLogger() {
    	return requestLogger;
    }

	private RequestParameters prepareParams(RequestParameters params, Transaction transaction) {
		if (params == null && transaction == null)
			return null;
		if (transaction == null)
			return params.copy("rs");

		RequestParameters requestParams =
			(params != null) ? params.copy("rs") : new RequestParameters();
		requestParams.add("txid", transaction.getTransactionId());

		return requestParams;
	}

	private <R extends AbstractReadHandle> Object execMethod(MethodType method, RequestParameters params, R output) {
		BaseHandle outputBase = HandleAccessor.checkHandle(output, "read");

		// TODO: handle must have mimetype
		String outputMimetype = (outputBase != null) ? outputBase.getMimetype() : null;
		Class  as             = HandleAccessor.receiveAs(output);

		switch (method) {
		case GET:
			return services.getResource(requestLogger, getResourcePath(), params, outputMimetype, as);
		case DELETE:
			return services.deleteResource(requestLogger, getResourcePath(), params, outputMimetype, as);
		default:
			throw new IllegalArgumentException("unknown method type: "+method.name());
		}
	}
	private <R extends AbstractReadHandle> Object[] execMethod(MethodType method, RequestParameters params, R[] output) {
		String[] outputMimetype = null;
		Class[]  as             = null;
		if (output != null) {
			int outputSize = output.length;
			outputMimetype = new String[outputSize];
			as             = new Class[outputSize];
			for (int i=0; i < outputSize; i++) {
				AbstractReadHandle handle = output[i];
				BaseHandle handleBase = HandleAccessor.checkHandle(handle, "read");
				// TODO: handle must have mimetype
				outputMimetype[i] = (handleBase != null) ? handleBase.getMimetype() : null;
				as[i]             = HandleAccessor.receiveAs(handle);
			}
		}

		switch (method) {
		case GET:
			return services.getResource(requestLogger, getResourcePath(), params, outputMimetype, as);
		default:
			throw new IllegalArgumentException("unknown method type: "+method.name());
		}
	}
	private <R extends AbstractReadHandle> Object execMethod(MethodType method, RequestParameters params, AbstractWriteHandle input, R output) {
		BaseHandle inputBase  = HandleAccessor.checkHandle(input,  "write");
		BaseHandle outputBase = HandleAccessor.checkHandle(output, "read");

		Object value          = HandleAccessor.sendContent(input);
		// TODO: handle must have mimetype
		String inputMimetype  = (inputBase != null)  ? inputBase.getMimetype()  : null;
		String outputMimetype = (outputBase != null) ? outputBase.getMimetype() : null;
		Class  as             = HandleAccessor.receiveAs(output);

		switch (method) {
		case PUT:
			return services.putResource(requestLogger, getResourcePath(), params, inputMimetype, value, outputMimetype, as);
		case POST:
			return services.postResource(requestLogger, getResourcePath(), params, inputMimetype, value, outputMimetype, as);
		default:
			throw new IllegalArgumentException("unknown method type: "+method.name());
		}
	}
	private <R extends AbstractReadHandle, W extends AbstractWriteHandle> Object execMethod(MethodType method, RequestParameters params, W[] input, R output) {
		BaseHandle outputBase = HandleAccessor.checkHandle(output, "read");

		Object[] value         = null;
		String[] inputMimetype = null;
		if (input != null) {
			int inputSize = input.length;
			value         = new Object[inputSize];
			inputMimetype = new String[inputSize];
			for (int i=0; i < inputSize; i++) {
				AbstractWriteHandle handle = input[i];
				BaseHandle handleBase = HandleAccessor.checkHandle(handle, "write");
				value[i]              = HandleAccessor.sendContent(handle);
				// TODO: handle must have mimetype
				inputMimetype[i] = (handleBase != null) ? handleBase.getMimetype() : null;
			}
		}

		// TODO: handle must have mimetype
		String outputMimetype = (outputBase != null) ? outputBase.getMimetype() : null;
		Class  as             = HandleAccessor.receiveAs(output);

		switch (method) {
		case PUT:
			return services.putResource(requestLogger, getResourcePath(), params, inputMimetype, value, outputMimetype, as);
		case POST:
			return services.postResource(requestLogger, getResourcePath(), params, inputMimetype, value, outputMimetype, as);
		default:
			throw new IllegalArgumentException("unknown method type: "+method.name());
		}
	}
	private <R extends AbstractReadHandle> Object[] execMethod(MethodType method, RequestParameters params, AbstractWriteHandle input, R[] output) {
		BaseHandle inputBase = HandleAccessor.checkHandle(input,  "write");

		Object value         = HandleAccessor.sendContent(input);
		// TODO: handle must have mimetype
		String inputMimetype = (inputBase != null) ? inputBase.getMimetype() : null;

		String[] outputMimetype = null;
		Class[]  as             = null;
		if (output!= null) {
			int outputSize = output.length;
			outputMimetype = new String[outputSize];
			as             = new Class[outputSize];
			for (int i=0; i < outputSize; i++) {
				AbstractReadHandle handle = output[i];
				BaseHandle handleBase = HandleAccessor.checkHandle(handle, "read");
				// TODO: handle must have mimetype
				outputMimetype[i] = (handleBase != null) ? handleBase.getMimetype() : null;
				as[i]             = HandleAccessor.receiveAs(handle);
			}
		}

		switch (method) {
		case POST:
			return services.postResource(requestLogger, getResourcePath(), params, inputMimetype, value, outputMimetype, as);
		default:
			throw new IllegalArgumentException("unknown method type: "+method.name());
		}
	}
	private <R extends AbstractReadHandle, W extends AbstractWriteHandle> Object[] execMethod(MethodType method, RequestParameters params, W[] input, R[] output) {
		Object[] value         = null;
		String[] inputMimetype = null;
		if (input != null) {
			int inputSize = input.length;
			value         = new Object[inputSize];
			inputMimetype = new String[inputSize];
			for (int i=0; i < inputSize; i++) {
				AbstractWriteHandle handle = input[i];
				BaseHandle handleBase = HandleAccessor.checkHandle(handle, "write");
				value[i]         = HandleAccessor.sendContent(handle);
				// TODO: handle must have mimetype
				inputMimetype[i] = (handleBase != null) ? handleBase.getMimetype() : null;
			}
		}

		String[] outputMimetype = null;
		Class[]  as             = null;
		if (output != null) {
			int outputSize = output.length;
			outputMimetype = new String[outputSize];
			as             = new Class[outputSize];
			for (int i=0; i < outputSize; i++) {
				AbstractReadHandle handle = output[i];
				BaseHandle handleBase = HandleAccessor.checkHandle(handle, "read");
				// TODO: handle must have mimetype
				outputMimetype[i] = (handleBase != null) ? handleBase.getMimetype() : null;
				as[i]             = HandleAccessor.receiveAs(handle);
			}
		}

		switch (method) {
		case POST:
			return services.postResource(requestLogger, getResourcePath(), params, inputMimetype, value, outputMimetype, as);
		default:
			throw new IllegalArgumentException("unknown method type: "+method.name());
		}
	}

	private <R extends AbstractReadHandle> R makeResult(Object response, R output) {
		if (response != null) {
			HandleAccessor.receiveContent(output, response);
		}

		return output;
	}
	private <R extends AbstractReadHandle> R[] makeResult(Object[] response, R[] output) {
		if (response != null) {
			for (int i=0; i < response.length; i++) {
				HandleAccessor.receiveContent(output[i], response[i]);
			}
		}

		return output;
	}
}
