package com.marklogic.client.impl;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.BinaryDocumentManager;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.GenericDocumentManager;
import com.marklogic.client.JSONDocumentManager;
import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.Transaction;
import com.marklogic.client.XMLDocumentManager;

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

	public GenericDocumentManager newDocumentManager() {
		return new GenericDocumentImpl(services);
	}

	public BinaryDocumentManager newBinaryDocumentManager() {
		return new BinaryDocumentImpl(services);
	}

	public JSONDocumentManager newJSONDocumentManager() {
		return new JSONDocumentImpl(services);
	}

	public TextDocumentManager newTextDocumentManager() {
		return new TextDocumentImpl(services);
	}

	public XMLDocumentManager newXMLDocumentManager() {
		return new XMLDocumentImpl(services);
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
