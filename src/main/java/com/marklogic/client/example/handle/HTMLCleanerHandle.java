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
package com.marklogic.client.example.handle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CompactXmlSerializer;
import org.htmlcleaner.DefaultTagProvider;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.ITagInfoProvider;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XmlSerializer;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * An HTMLCleaner Handle writes an HTML document to the database
 * as an XHTML document.
 */
public class HTMLCleanerHandle
	extends BaseHandle<OperationNotSupported, OutputStreamSender>
	implements OutputStreamSender, XMLWriteHandle
{
	private CleanerProperties configuration;
	private ITagInfoProvider  rulesProvider;
	private HtmlCleaner       parser;
	private XmlSerializer     serializer;
	private TagNode           content;

	public HTMLCleanerHandle() {
		super();
		super.setFormat(Format.XML);
   		setResendable(true);
	}
	public HTMLCleanerHandle(TagNode content) {
		this();
    	set(content);
	}

	public CleanerProperties getConfiguration() {
		if (configuration == null)
			configuration = makeConfiguration();
		return configuration;
	}
	public void setConfiguration(CleanerProperties configuration) {
		this.configuration = configuration;
	}
	protected CleanerProperties makeConfiguration() {
		return new CleanerProperties();
	}

	public ITagInfoProvider getRulesProvider() {
		if (rulesProvider == null)
			rulesProvider = makeRulesProvider();
		return rulesProvider;
	}
	public void setRulesProvider(ITagInfoProvider rulesProvider) {
		this.rulesProvider = rulesProvider;
	}
	protected ITagInfoProvider makeRulesProvider() {
		return new DefaultTagProvider();
	}

	public HtmlCleaner getParser() {
		if (parser == null)
			parser = makeParser();
		return parser;
	}
	public void setParser(HtmlCleaner parser) {
		this.parser = parser;
	}
	protected HtmlCleaner makeParser() {
		return new HtmlCleaner(getRulesProvider(), getConfiguration());
	}

	public XmlSerializer getSerializer() {
		if (serializer == null)
			serializer = makeSerializer();
		return serializer;
	}
	public void setSerializer(XmlSerializer serializer) {
		this.serializer = serializer;
	}
	protected XmlSerializer makeSerializer() {
		CleanerProperties configuration = getConfiguration();
		return new CompactXmlSerializer(
				(configuration != null) ?
						configuration : getParser().getProperties()
				);
	}

	public TagNode get() {
		return content;
	}
    public void set(TagNode content) {
    	this.content = content;
    }
    public HTMLCleanerHandle with(TagNode content) {
    	set(content);
    	return this;
    }

    public void set(File content, String charset) throws IOException {
    	set(getParser().clean(content, charset));
    }
    public HTMLCleanerHandle with(File content, String charset)
    throws IOException {
    	set(content, charset);
    	return this;
    }

    public void set(InputStream content, String charset) throws IOException {
    	set(getParser().clean(content, charset));
    }
    public HTMLCleanerHandle with(InputStream content, String charset)
    throws IOException {
    	set(content, charset);
    	return this;
    }

    public void set(Reader content) throws IOException {
    	set(getParser().clean(content));
    }
    public HTMLCleanerHandle with(Reader content) throws IOException {
    	set(content);
    	return this;
    }

    public void set(String content) throws IOException {
    	set(getParser().clean(content));
    }
    public HTMLCleanerHandle with(String content) throws IOException {
    	set(content);
    	return this;
    }

	public void setFormat(Format format) {
		if (format != Format.XML)
			throw new IllegalArgumentException(
					"HTMLCleanerHandle supports the XML format only");
	}

	@Override
	protected OutputStreamSender sendContent() {
		if (content == null) {
			throw new IllegalStateException("No content to write");
		}

		return this;
	}
	@Override
	public void write(OutputStream out) throws IOException {
		getSerializer().writeToStream(content, out, "UTF-8");
	}

}
