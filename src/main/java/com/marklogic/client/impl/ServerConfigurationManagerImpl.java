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
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.ServerConfigurationManager;
import com.marklogic.client.io.OutputStreamSender;

class ServerConfigurationManagerImpl
	implements ServerConfigurationManager, OutputStreamSender
{
	static final private Logger logger = LoggerFactory.getLogger(ServerConfigurationManagerImpl.class);

	private Boolean validatingQueryOptions;
	private String  defaultDocumentReadTransform;
	private String  defaultDocumentWriteTransform;

	private RESTServices services;

	public ServerConfigurationManagerImpl(RESTServices services) {
		super();
		this.services = services;
	}

	@Override
	public void readConfiguration() {
		try {
			logger.info("Reading server configuration");

			InputStream stream = services.getValues(null, "config/properties", "application/xml", InputStream.class);
			if (stream == null)
				return;

			XMLInputFactory factory = XMLInputFactory.newFactory();
			factory.setProperty("javax.xml.stream.isNamespaceAware", new Boolean(true));
			factory.setProperty("javax.xml.stream.isValidating",     new Boolean(false));

			XMLStreamReader reader = factory.createXMLStreamReader(stream);
			while (reader.hasNext()) {
				if (reader.next() != XMLStreamReader.START_ELEMENT)
					continue;

				String localName = reader.getLocalName();
				if ("validate-options".equals(localName)) {
					String value = reader.getElementText();
					validatingQueryOptions = new Boolean("true".equals(value));
				}
				// TODO: other properties
			}

			reader.close();
		} catch (XMLStreamException e) {
			logger.error("Failed to read server configuration stream",e);
			throw new MarkLogicInternalException(e);
		}
	}

	@Override
	public void writeConfiguration() {
		logger.info("Writing server configuration");

		services.putValue(null, "config/properties", null, "application/xml", this);
	}
	@Override
	public void write(OutputStream out) throws IOException {
		try {
			XMLOutputFactory factory = XMLOutputFactory.newFactory();
			factory.setProperty("javax.xml.stream.isRepairingNamespaces", new Boolean(true));

			XMLStreamWriter serializer = factory.createXMLStreamWriter(out, "utf-8");

			serializer.writeStartElement("properties");

			if (validatingQueryOptions != null) {
				serializer.writeStartElement("validate-options");
				serializer.writeCharacters(validatingQueryOptions.toString());
				serializer.writeEndElement();
			}
			// TODO: other properties

//			serializer.writeEndElement();
			serializer.writeEndDocument();
		} catch (XMLStreamException e) {
			logger.error("Failed to write server configuration stream",e);
			throw new MarkLogicInternalException(e);
		}
	}

	@Override
	public Boolean getQueryOptionValidation() {
		return validatingQueryOptions;
	}
	@Override
	public void setQueryOptionValidation(Boolean on) {
		validatingQueryOptions = on;
	}

	@Override
	public String getDefaultDocumentReadTransform(String name) {
		return defaultDocumentReadTransform;
	}
	@Override
	public void setDefaultDocumentReadTransform(String name) {
		defaultDocumentReadTransform = name;
	}

	@Override
	public String getDefaultDocumentWriteTransform(String name) {
		return defaultDocumentWriteTransform;
	}
	@Override
	public void setDefaultDocumentWriteTransform(String name) {
		defaultDocumentWriteTransform = name;
	}
}
