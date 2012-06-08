package com.marklogic.client.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.FailedRequestException;

/**
 * Encapsulate data passed in an error response from a REST server instance
 *
 */
public class FailedRequest {

	private String messageCode;

	private String messageString;

	private int statusCode;

	private String statusString;
	

	/*
	 * send an InputStream to this handler in order to create an error
	 * block.
	 */
	FailedRequest(InputStream content) {
		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(content);
			String statusCode = doc
					.getElementsByTagNameNS(JerseyServices.ERROR_NS, "status-code")
					.item(0).getTextContent();
			this.statusCode = Integer.parseInt(statusCode);
			statusString = doc.getElementsByTagNameNS(JerseyServices.ERROR_NS, "status")
					.item(0).getTextContent();
			messageCode = doc
					.getElementsByTagNameNS(JerseyServices.ERROR_NS, "message-code")
					.item(0).getTextContent();
			messageString = doc.getElementsByTagNameNS(JerseyServices.ERROR_NS, "message")
					.item(0).getTextContent();
		} catch (ParserConfigurationException e) {
			throw new FailedRequestException(
					"Request failed. Cannot determine error from server.");
		} catch (SAXException e) {
			throw new FailedRequestException(
					"Request failed. Cannot determine error from server.");
		} catch (IOException e) {
			throw new FailedRequestException(
					"Request failed. Cannot determine error from server.");
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