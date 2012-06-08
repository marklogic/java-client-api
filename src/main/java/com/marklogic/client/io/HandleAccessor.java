/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.io;

import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

/**
 * HandleAccessor is used internally.  Applications will not ordinarily need to use it.
 *
 */
public class HandleAccessor {
	static public BaseHandle checkHandle(Object object, String type) {
		if (!isHandle(object))
			throw new IllegalArgumentException(
					type+" handle does not extend BaseHandle: "+object.getClass().getName()
					);
		return ((BaseHandle) object);
	}
	static public boolean isHandle(Object object) {
		return object == null || object instanceof BaseHandle;
	}

	static public <R extends AbstractReadHandle> Class<R> receiveAs(R handle) {
		if (handle == null)
			return null;
		return ((BaseHandle) handle).receiveAs();
	}
	static public <R extends AbstractReadHandle> void receiveContent(R handle, Object content) {
		if (handle == null)
			return;
		((BaseHandle) handle).receiveContent(content);
	}
	static public <W extends AbstractWriteHandle> Object sendContent(W handle) {
		if (handle == null)
			return null;
		return ((BaseHandle) handle).sendContent();
	}
	static public BaseHandle as(Object handle) {
		return ((BaseHandle) handle);
	}
}
