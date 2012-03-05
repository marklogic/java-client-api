package com.marklogic.client.iml.io;

import com.marklogic.client.io.BytesHandle;

public class BytesHandleImpl implements BytesHandle {
	public BytesHandleImpl() {
	}

	public Class<byte[]> handles() {
		return byte[].class;
	}

	private byte[] content;
	public byte[] get() {
		return content;
	}
	public void set(byte[] content) {
		this.content = content;
	}

	public BytesHandle on(byte[] content) {
		set(content);
		return this;
	}

}
