package com.marklogic.client.io;

import com.marklogic.client.Format;

public class HandleHelper<R,W> {
	private BaseHandle<R,W> handle;

	static public boolean isHandle(Object object) {
		return object == null || object instanceof BaseHandle;
	}
	static public HandleHelper<?,?> newHelper(Object object) {
		if (object == null)
			return (HandleHelper<?,?>) null;
		return new HandleHelper((BaseHandle) object);
	}
	static public void release(HandleHelper<?,?> helper) {
		if (helper != null)
			helper.handle = null;
	}

	private HandleHelper(BaseHandle<R,W> handle) {
		this.handle = handle;
	}

	public BaseHandle<R,W> get() {
		return handle;
	}
	public Class<R> receiveAs() {
		return handle.receiveAs();
	}
	public void receiveContent(R content) {
		handle.receiveContent(content);
	}
	public W sendContent() {
		return handle.sendContent();
	}
	public Format getFormat() {
		return handle.getFormat();
	}
	public void setFormat(Format format) {
		handle.setFormat(format);
	}
}
