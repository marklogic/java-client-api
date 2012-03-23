package com.marklogic.client.impl;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.marklogic.client.io.DBResolver;

class DBResolverImpl implements DBResolver {
	static final private Logger logger = LoggerFactory.getLogger(DBResolverImpl.class);

	private RESTServices services;
	private String baseURI;

	DBResolverImpl(RESTServices services) {
		this.services = services;
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	public LSInput resolveResource(String type, String namespaceURI,
			String publicId, String systemId, String baseURI) {
		// TODO Auto-generated method stub
		return null;
	}
	public Source resolve(String href, String base) throws TransformerException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object resolveEntity(String publicID, String systemID,
			String baseURI, String namespace) throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBaseURI() {
		return baseURI;
	}
	public void setBaseUri(String uri) {
		this.baseURI = uri;
	}

}
