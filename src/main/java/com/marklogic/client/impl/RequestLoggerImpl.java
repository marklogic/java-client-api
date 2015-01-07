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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.util.RequestLogger;

class RequestLoggerImpl implements RequestLogger {
	static final private Logger logger = LoggerFactory.getLogger(RequestLoggerImpl.class);

	private PrintStream out;
	private boolean     enabled    = true;
	private long        contentMax = NO_CONTENT;

	RequestLoggerImpl(OutputStream out) {
		if (out == null)
			this.out = System.out;
		else if (out instanceof PrintStream)
			this.out = (PrintStream) out;
		else
			this.out = new PrintStream(out);
	}

	@Override
	public long getContentMax() {
		return contentMax;
	}
	@Override
	public void setContentMax(long max) {
		this.contentMax = max;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	@Override
	public void setEnabled(boolean enabled) {
		if (out == null && enabled == true)
			throw new IllegalStateException("Cannot enable closed request logger");
		this.enabled = enabled;
	}

	@Override
	public PrintStream getPrintStream() {
		return (enabled) ? out : null;
	}

	@Override
    @SuppressWarnings("unchecked")
	public <T> T copyContent(T content) {
		if (content == null)
			return content;

		long max = getContentMax();
		if (max < 1)
			return content;

		PrintStream out = getPrintStream();
		if (out == null)
			return content;

		if (content instanceof byte[]) {
			byte[] b = (byte[]) content;
			out.write(b, 0, (int) Math.min(b.length, max));
			return content;
		}

		if (content instanceof File) {
			out.println("info: cannot copy content from "+
					((File) content).getAbsolutePath());
			return content;
		}

		if (content instanceof InputStream) {
			return (T) new InputStreamTee((InputStream) content, out, max);
		}

		if (content instanceof Reader) {
			return (T) new ReaderTee((Reader) content, out, max);
		}

		if (content instanceof String) {
			String s = (String) content;
			int len = s.length();
			if (len <= max)
				out.print(s);
			else
				out.print(s.substring(0, (int) Math.min(len, max)));
			return content;
		}

		if (logger.isWarnEnabled())
			logger.warn("Unknown {} class for content", content.getClass().getName());
		return content;
	}

	@Override
	public void flush() {
		if (out == null) return;
		out.flush();
	}
	@Override
	public void close() {
		if (out == null) return;
		out.close();
		out = null;
		enabled = false;
	}

}
