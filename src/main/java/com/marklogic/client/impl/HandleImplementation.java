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

import com.marklogic.client.document.ContentDescriptor;

// exposes BaseHandle protected methods in implementation package
public abstract class HandleImplementation<R,W>
    implements ContentDescriptor
{
	private boolean resendable = false;

	protected HandleImplementation() {
		super();
	}

	/**
	 * As part of the contract between a read handle and the API, 
	 * declares the class of the content received from the database.
	 * You should rarely if ever need to call this method directly when using the handle.
	 * @return
	 */
	protected Class<R> receiveAs() {
		throw new UnsupportedOperationException(this.getClass().getName()+" cannot receive content");
	}
	/**
	 * As part of the contract between a read handle and the API, 
	 * receives content from the database.  You should rarely
	 * if ever need to call this method directly when using the handle.
	 * @return
	 */
	protected void receiveContent(R content) {
		throw new UnsupportedOperationException(this.getClass().getName()+" cannot receive content");
	}

	/**
	 * As part of the contract between a write handle and the API, 
	 * sends content to the database.  You should rarely
	 * if ever need to call this method directly when using the handle.
	 * @return
	 */
	protected W sendContent() {
		throw new UnsupportedOperationException(this.getClass().getName()+" cannot send content");
	}

	/**
	 * As part of the contract between a write handle and the API, 
	 * specifies whether the content can be sent again if the request
	 * must be retried.  The method returns false unless overridden.
	 * You should rarely if ever need to call this method directly
	 * when using the handle.
	 * @return
	 */
	protected boolean isResendable() {
		return resendable;
	}
	/**
	 * Specifies whether the content can be sent again if the request
	 * must be retried.
	 * @param resendable	true if the content can be sent again
	 */
	protected void setResendable(boolean resendable) {
		this.resendable = resendable;
	}
}
