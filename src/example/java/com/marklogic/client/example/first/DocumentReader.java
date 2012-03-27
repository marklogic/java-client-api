package com.marklogic.client.example.first;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;

/**
 * DocumentReader illustrates how to read the content of a database document.
 */
public class DocumentReader {

	public static void main(String[] args) throws IOException {
		Properties props = loadProperties();

		// connection parameters
		String         host     = props.getProperty("example.host");
		int            port     = Integer.parseInt(props.getProperty("example.port"));
		String         user     = props.getProperty("example.writer_user");
		String         password = props.getProperty("example.writer_password");
		Authentication authType = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		run(host, port, user, password, authType);
	}

	public static void run(String host, int port, String user, String password, Authentication authType)
	throws IOException {
		String filename = "flipper.xml";

		// connect the client
		DatabaseClient client =
			DatabaseClientFactory.connect(host, port, user, password, authType);

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document
		DocumentIdentifier docId = client.newDocId("/example/"+filename);

		setUpExample(docMgr, docId, filename);

		// create a handle to receive the document content
		DOMHandle handle = new DOMHandle();

		// read the document content
		docMgr.read(docId, handle);

		// access the document content
		Document document = handle.get();

		String rootName = document.getDocumentElement().getTagName();
		System.out.println("Read /example/"+filename+" content with <"+rootName+"/> root");

		tearDownExample(docMgr, docId);

		// release the client
		client.release();
	}

	// set up by writing document content for the example to read
	public static void setUpExample(XMLDocumentManager docMgr, DocumentIdentifier docId, String filename) {
		InputStream docStream = DocumentReader.class.getClassLoader().getResourceAsStream(
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
			DocumentReader.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
