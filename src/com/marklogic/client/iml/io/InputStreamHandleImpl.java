package com.marklogic.client.iml.io;

import java.io.InputStream;

import com.marklogic.client.io.InputStreamHandle;

public class InputStreamHandleImpl implements InputStreamHandle {

	public Class<InputStream> handles() {
		return InputStream.class;
	}

	private InputStream content;
	public InputStream get() {
		return content;
	}
	public void set(InputStream content) {
		this.content = content;
	}

	public InputStreamHandle on(InputStream content) {
		set(content);
		return this;
	}

}
