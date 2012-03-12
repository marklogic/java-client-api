package com.marklogic.client.iml;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.BinaryDocumentBuffer;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.GenericDocumentBuffer;
import com.marklogic.client.JSONDocumentBuffer;
import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.TextDocumentBuffer;
import com.marklogic.client.Transaction;
import com.marklogic.client.XMLDocumentBuffer;

public class DatabaseClientImpl implements DatabaseClient {
	static final private Logger logger = LoggerFactory.getLogger(DatabaseClientImpl.class);

	private RESTServices services;

	public DatabaseClientImpl(RESTServices services) {
		this.services = services;
	}

	public Transaction openTransaction() {
		// TODO: open the transaction and pass the transaction ID to the constructor
		String transactionId = null;

		logger.info("Opening transaction {}",transactionId);

		return new TransactionImpl(transactionId);
	}

	public GenericDocumentBuffer newDocumentBuffer(String uri) {
		return new GenericDocumentImpl(services, uri);
	}

	public BinaryDocumentBuffer newBinaryDocumentBuffer(String uri) {
		return new BinaryDocumentImpl(services, uri);
	}

	public JSONDocumentBuffer newJSONDocumentBuffer(String uri) {
		return new JSONDocumentImpl(services, uri);
	}

	public TextDocumentBuffer newTextDocumentBuffer(String uri) {
		return new TextDocumentImpl(services, uri);
	}

	public XMLDocumentBuffer newXMLDocumentBuffer(String uri) {
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
		logger.info("Releasing connection");

		if (services != null)
			services.release();
	}

	@Override
	protected void finalize() throws Throwable {
		release();
		super.finalize();
	}
}
