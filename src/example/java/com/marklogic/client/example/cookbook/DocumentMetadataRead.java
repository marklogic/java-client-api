package com.marklogic.client.example.cookbook;

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
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;

/**
 * DocumentMetadataReader illustrates how to read the metadata and content of a database document
 * in a single request.
 */
public class DocumentMetadataRead {

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

		// create a handle to receive the document metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

		// create a handle to receive the document content
		DOMHandle contentHandle = new DOMHandle();

		// read the document metadata and content
		docMgr.read(docId, metadataHandle, contentHandle);

		// access the document metadata
		DocumentCollections collections = metadataHandle.getCollections();

		// access the document content
		Document document = contentHandle.get();

		String collFirst = collections.toArray(new String[collections.size()])[0];
		String rootName = document.getDocumentElement().getTagName();
		System.out.println("Read /example/"+filename +
				" metadata and content in the '"+collFirst+"' collection with the <"+rootName+"/> root element");

		tearDownExample(docMgr, docId);

		// release the client
		client.release();
	}

	// set up by writing document metadata and content for the example to read
	public static void setUpExample(XMLDocumentManager docMgr, DocumentIdentifier docId, String filename) {
		InputStream docStream = DocumentMetadataRead.class.getClassLoader().getResourceAsStream(
				"data"+File.separator+filename);
		if (docStream == null)
			throw new RuntimeException("Could not read document example");

		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		metadataHandle.getCollections().addAll("products", "real-estate");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.setQuality(1);

		InputStreamHandle handle = new InputStreamHandle(docStream);
		handle.set(docStream);

		docMgr.write(docId, metadataHandle, handle);
	}

	// clean up by deleting the document read by the example
	public static void tearDownExample(XMLDocumentManager docMgr, DocumentIdentifier docId) {
		docMgr.delete(docId);
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			DocumentMetadataRead.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
