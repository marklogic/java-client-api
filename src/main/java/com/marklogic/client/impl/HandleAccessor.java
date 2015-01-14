/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

/**
 * HandleAccessor is used internally.  Applications will not ordinarily need to use it.
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class HandleAccessor {
	static public HandleImplementation checkHandle(Object object, String type) {
		if (!isHandle(object))
			throw new IllegalArgumentException(
					type+" handle does not extend BaseHandle: "+object.getClass().getName()
					);
		return ((HandleImplementation) object);
	}
	static public boolean isHandle(Object object) {
		return object == null || object instanceof BaseHandle;
	}

	static public <R extends AbstractReadHandle> Class<R> receiveAs(R handle) {
		if (handle == null)
			return null;
		return ((HandleImplementation) handle).receiveAs();
	}
	static public <R extends AbstractReadHandle> void receiveContent(R handle, Object content) {
		if (handle == null)
			return;
		((HandleImplementation) handle).receiveContent(content);
	}
	static public <W extends AbstractWriteHandle> Object sendContent(W handle) {
		if (handle == null)
			return null;
		return ((HandleImplementation) handle).sendContent();
	}
	static public <W extends AbstractWriteHandle> boolean isResendable(W handle) {
		if (handle == null)
			return false;
		return ((HandleImplementation) handle).isResendable();
	}
	static public HandleImplementation as(Object handle) {
		return ((HandleImplementation) handle);
	}
    static public String contentAsString(AbstractWriteHandle handle) {
    	try {
        Object content = sendContent(handle);
        if ( content == null ) return null;
        String stringContent = null;
		if (content instanceof String) {
            stringContent = (String) content;
		} else if (content instanceof OutputStreamSender) {
            ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
            ((OutputStreamSender) content).write(bytesStream);
            stringContent = bytesStream.toString("UTF-8");
		} else if ( content instanceof byte[] ) {
            stringContent = new String((byte[]) content, "UTF-8"); 
		} else if ( content instanceof File ) {
            content = new FileInputStream((File) content);
		}
        if ( content instanceof InputStream ) {
            StringBuffer sb = new StringBuffer();
            Reader reader = new InputStreamReader((InputStream) content, "UTF-8");
            char[] cbuf = new char[8000];
            int charsRead = -1;
            while ( (charsRead = reader.read(cbuf)) != -1 ) {
                sb.append(cbuf, 0, charsRead);
            }
            stringContent = sb.toString();
		}
		if ( content instanceof File ) {
			((FileInputStream) content).close();
		}
        if ( stringContent == null ) {
        	throw new UnsupportedOperationException("contentAsString only supports handles with sendContent() " +
        		"of type String, OutputStreamSender, byte[], File, or InputStream");
        }
        return stringContent;
    	} catch (Exception e) {
    		throw new MarkLogicIOException(e);
    	}
    }
}
