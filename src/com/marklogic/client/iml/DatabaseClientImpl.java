package com.marklogic.client.iml;

import java.io.OutputStream;

import com.marklogic.client.BinaryDocument;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.GenericDocument;
import com.marklogic.client.JSONDocument;
import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.TextDocument;
import com.marklogic.client.Transaction;
import com.marklogic.client.XMLDocument;

public class DatabaseClientImpl implements DatabaseClient {
	private RESTServices services;

	DatabaseClientImpl(RESTServices services) {
		this.services = services;
	}

	public Transaction openTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericDocument newDocument(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public BinaryDocument newBinaryDocument(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONDocument newJSONDocument(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public TextDocument newTextDocument(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public XMLDocument newXMLDocument(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public RequestLogger newLogger(OutputStream out) {
		// TODO Auto-generated method stub
		return null;
	}

	public QueryManager newQueryManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public QueryOptionsManager newQueryOptionsManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void release() {
		// TODO Auto-generated method stub
	}
}
