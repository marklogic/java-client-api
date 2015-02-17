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

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.core.StreamingOutput;

import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.io.OutputStreamSender;

class StreamingOutputImpl implements StreamingOutput {
	private OutputStreamSender handle;
	private RequestLogger      logger;

	StreamingOutputImpl(OutputStreamSender handle, RequestLogger logger) {
		super();
		this.handle = handle;
		this.logger = logger;
	}

	public void write(OutputStream out) throws IOException {
		if (logger != null) {
			OutputStream tee = logger.getPrintStream();
			long         max = logger.getContentMax();
			if (tee != null && max > 0) {
				handle.write(new OutputStreamTee(out, tee, max));

				return;
			}
		}

		handle.write(out);
	}
}
