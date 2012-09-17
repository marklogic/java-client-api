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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Encapsulate data passed in an error response from a REST server instance
 * 
 * This class wraps the following kind of XML structure, which is the standard
 * error payload for error conditions from the server:
 * 
 * &lt;rapi:error&gt; &lt;rapi:status-code&gt;404&lt;/rapi:status-code&gt;
 * &lt;rapi:status&gt;NOT FOUND&lt;/rapi:status&gt;
 * &lt;rapi:message-code&gt;RESTAPI-NODOCUMENT&lt;/rapi:message-code&gt;
 * &lt;rapi:message&gt;RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document
 * does not exist: category: options message: Options configuration
 * &#39;photosd&#39; not found&lt;/rapi:message&gt; &lt;/rapi:error&gt;
 * 
 */
public class FailedRequest {

	private String messageCode;

	private String messageString;

	private int statusCode;

	private String statusString;

	/*
	 * send an InputStream to this handler in order to create an error block.
	 */
	FailedRequest(int httpStatus, InputStream content) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(content);
			String statusCode = doc
					.getElementsByTagNameNS(JerseyServices.ERROR_NS,
							"status-code").item(0).getTextContent();
			this.statusCode = Integer.parseInt(statusCode);
			statusString = doc
					.getElementsByTagNameNS(JerseyServices.ERROR_NS, "status")
					.item(0).getTextContent();
			messageCode = doc
					.getElementsByTagNameNS(JerseyServices.ERROR_NS,
							"message-code").item(0).getTextContent();
			messageString = doc
					.getElementsByTagNameNS(JerseyServices.ERROR_NS, "message")
					.item(0).getTextContent();
		} catch (ParserConfigurationException e) {
			statusCode = httpStatus;
			messageString = "Request failed. Unable to parse server error.";
		} catch (SAXException e) {
			statusCode = httpStatus;
			messageString = "Request failed. Unable to parse server error details";
		} catch (IOException e) {
			statusCode = httpStatus;
			messageString = "Request failed. Error body not received from server";
		}

	}

	public String getMessage() {
		return messageString;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public String getStatus() {
		return statusString;
	}

	public int getStatusCode() {
		return statusCode;
	}

}
