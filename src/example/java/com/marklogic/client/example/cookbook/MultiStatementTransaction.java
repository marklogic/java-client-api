package com.marklogic.client.example.cookbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.Transaction;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.InputStreamHandle;

/**
 * MultiStatementTransaction illustrates how to open a transaction, execute
 * multiple statements under the transaction, and commit the transaction.
 */
public class MultiStatementTransaction {

	public static void main(String[] args) throws IOException {
		Properties props = loadProperties();

		// connection parameters for writer user
		String         host            = props.getProperty("example.host");
		int            port            = Integer.parseInt(props.getProperty("example.port"));
		String         writer_user     = props.getProperty("example.writer_user");
		String         writer_password = props.getProperty("example.writer_password");
		Authentication authType        = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		run(host, port, writer_user, writer_password, authType);
	}

	public static void run(String host, int port, String user, String password, Authentication authType) {
		System.out.println("example: "+MultiStatementTransaction.class.getName());

		String beforeFilename = "flipper.xml";
		String afterFilename  = "flapped.xml";

		// connect the client
		DatabaseClient client = DatabaseClientFactory.connect(host, port, user, password, authType);

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document before the move
		DocumentIdentifier beforeDocId = client.newDocId("/example/"+beforeFilename);

		// create an identifier for the document after the move
		DocumentIdentifier afterDocId = client.newDocId("/example/"+afterFilename);

		setUpExample(docMgr, beforeDocId, beforeFilename);

		// start the transaction
		Transaction transaction = client.openTransaction();

		// create a handle to receive the document content
		InputStreamHandle handle = new InputStreamHandle();

		// read the document with the old id
		docMgr.read(beforeDocId, handle, transaction);

		// write the document with the new id
		docMgr.write(afterDocId, handle, transaction);

		// delete the document with the old id
		docMgr.delete(beforeDocId, transaction);

		// commit the transaction for the move operation
		transaction.commit();

		System.out.println("Moved document from "+beforeFilename+" to "+afterFilename);

		tearDownExample(docMgr, afterDocId);

		// release the client
		client.release();
	}

	// set up by writing document content for the example to read
	public static void setUpExample(XMLDocumentManager docMgr, DocumentIdentifier docId, String filename) {
		InputStream docStream = DocumentRead.class.getClassLoader().getResourceAsStream(
				"data"+File.separator+filename);
		if (docStream == null)
			throw new RuntimeException("Could not read document example");

		InputStreamHandle handle = new InputStreamHandle(docStream);
		handle.set(docStream);

		docMgr.write(docId, handle);
	}

	// clean up by deleting the document read by the example
	public static void tearDownExample(XMLDocumentManager docMgr, DocumentIdentifier docId) {
		docMgr.delete(docId);
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			ClientConnect.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
