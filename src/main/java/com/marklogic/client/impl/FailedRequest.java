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
import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.io.JSONErrorParser;

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

	private static class FailedRequestXMLParser implements FailedRequestParser {

		@Override
		public FailedRequest parseFailedRequest(int httpStatus, InputStream is) {
			FailedRequest failure = new FailedRequest();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				Document doc = builder.parse(is);
				String statusCode = null;
				NodeList statusCodes = doc.getElementsByTagNameNS(JerseyServices.ERROR_NS, "status-code");
				if ( statusCodes != null && statusCodes.getLength() > 0 ) {
					statusCode = statusCodes.item(0).getTextContent();
				}
				if ( statusCode != null ) {
					failure.setStatusCode(Integer.parseInt(statusCode));
				} else {
					failure.setStatusCode(httpStatus);
				}
				NodeList statuses = doc.getElementsByTagNameNS(JerseyServices.ERROR_NS, "status");
				if ( statuses != null && statuses.getLength() > 0 ) {
					failure.setStatusString( statuses.item(0).getTextContent() );
				}
				NodeList messageCodes = doc.getElementsByTagNameNS(JerseyServices.ERROR_NS, "message-code");
				if ( messageCodes != null && messageCodes.getLength() > 0 ) {
					failure.setMessageCode( messageCodes.item(0).getTextContent() );
				}
				// the following is for eval errors
				String formatString = null;
				NodeList formatStrings = doc.getElementsByTagNameNS(JerseyServices.ERROR_NS, "format-string");
				if ( formatStrings != null && formatStrings.getLength() > 0 ) {
					formatString = formatStrings.item(0).getTextContent();
				}
				if ( formatString != null ) {
					failure.setMessageString(formatString);
				} else {
					NodeList messageStrings = doc.getElementsByTagNameNS(JerseyServices.ERROR_NS, "message");
					if ( messageStrings != null && messageStrings.getLength() > 0 ) {
						failure.setMessageString( messageStrings.item(0).getTextContent() );
					}
				}
			} catch (ParserConfigurationException e) {
				failure.setStatusCode(httpStatus);
				failure.setMessageString("Request failed. Unable to parse server error.");
			} catch (SAXException e) {
				failure.setStatusCode(httpStatus);
				failure.setMessageString("Request failed. Unable to parse server error details");
			} catch (IOException e) {
				failure.setStatusCode(httpStatus);
				failure.setMessageString("Request failed. Error body not received from server");
			}
			return failure;
		}
		
	}
	/*
	 * send an InputStream to this handler in order to create an error block.
	 */
	public static FailedRequest getFailedRequest(int httpStatus, MediaType contentType, InputStream content) {
		FailedRequest failure;
		
		// by default XML is supported
		if (contentType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
			
			FailedRequestParser xmlParser = new FailedRequestXMLParser();
			
			failure  =  xmlParser.parseFailedRequest(httpStatus, content);
			
		}
		else if (contentType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
			failure = jsonFailedRequest(httpStatus, content);						
		}
		else {
			failure = new FailedRequest();
			failure.setStatusCode(httpStatus);
			failure.setMessageCode("UNKNOWN");
			failure.setMessageString("Server (not a REST instance?) did not respond with an expected REST Error message.");
			failure.setStatusString("UNKNOWN");
		}
		if (failure.getStatusCode() == 401) {
			failure.setMessageString("Unauthorized");
			failure.setStatusString("Failed Auth");
		}
		return failure;

	}
	
	private static FailedRequest jsonFailedRequest(int httpStatus, InputStream content) {
		return new JSONErrorParser().parseFailedRequest(httpStatus, content);
	}
	
	

	/**
	 * No argument constructor so an external class can generate FailedRequest objects.
	 */
	public FailedRequest() {
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

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public void setMessageString(String messageString) {
		this.messageString = messageString;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}


}
