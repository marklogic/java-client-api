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

	public DatabaseClientImpl(RESTServices services) {
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
		return new BinaryDocumentImpl(services, uri);
	}

	public JSONDocument newJSONDocument(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public TextDocument newTextDocument(String uri) {
		return new TextDocumentImpl(services, uri);
	}

	public XMLDocument newXMLDocument(String uri) {
		return new XMLDocumentImpl(services, uri);
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
		if (services != null)
			services.release();
	}

	@Override
	protected void finalize() throws Throwable {
		release();
		super.finalize();
	}
}
