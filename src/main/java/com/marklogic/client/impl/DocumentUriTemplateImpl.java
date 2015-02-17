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

import java.util.regex.Pattern;

import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.io.Format;

public class DocumentUriTemplateImpl implements DocumentUriTemplate {
	// basic initial directory validation -- full URI validation happens on the server
	static private final Pattern DIRECTORY_CHECK = Pattern.compile("^[^?#]*$");
	// extension validation in support of extension mapping to mime types on the server
	static private final Pattern EXTENSION_CHECK = Pattern.compile("^\\.?\\w+$");

	private String directory;
	private String extension;
	private Format format;
	private String mimetype;
	private long length;

	public DocumentUriTemplateImpl(String extension) {
		super();
		setExtension(extension);
	}

	@Override
	public String getDirectory() {
		return directory;
	}
	@Override
	public void setDirectory(String directory) {
		if (!DIRECTORY_CHECK.matcher(directory).matches()) {
			throw new IllegalArgumentException(
				"Directory is not valid: "+directory
				);
		}
		this.directory = directory;
	}
	@Override
	public DocumentUriTemplate withDirectory(String directory) {
		setDirectory(directory);
		return this;
	}

	@Override
	public String getExtension() {
		return extension;
	}
	@Override
	public void setExtension(String extension) {
		if (!EXTENSION_CHECK.matcher(extension).matches()) {
			throw new IllegalArgumentException(
				"Extension may contain only word characters after initial period: "+extension
				);
		}
		this.extension = extension;
	}

	@Override
	public Format getFormat() {
		return format;
	}
	@Override
	public void setFormat(Format format) {
		this.format = format;
	}
	@Override
	public DocumentUriTemplate withFormat(Format format) {
		setFormat(format);
		return this;
	}

	@Override
	public String getMimetype() {
		return mimetype;
	}
	@Override
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	@Override
	public long getByteLength() {
		return length;
	}
	@Override
	public void setByteLength(long length) {
		this.length = length;
	}
}
