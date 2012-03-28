package com.marklogic.client.example.cookbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;

/**
 * JAXBDocument illustrates how to write and read a JAXB object structure as a database document.
 */
public class JAXBDocument {
	public static void main(String[] args) throws IOException, JAXBException {
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

	// The class for creating JAXB objects
	@XmlRootElement
	static public class Product {
		private String name;
		private String industry;
		private String description;
		public Product() {
			super();
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getIndustry() {
			return industry;
		}
		public void setIndustry(String industry) {
			this.industry = industry;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static void run(String host, int port, String user, String password, Authentication authType)
	throws IOException, JAXBException {
		// connect the client
		DatabaseClient client =
			DatabaseClientFactory.connect(host, port, user, password, authType);

		JAXBContext context = JAXBContext.newInstance(Product.class);

		Product product = new Product();
		product.setName("FashionForward");
		product.setIndustry("Retail");
		product.setDescription(
				"Creates demand with high prices, hours from midnight to dawn, and frequent moves");

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document
		DocumentIdentifier docId = client.newDocId("/example/"+product.getName()+".xml");

		// create a handle for the JAXB object
		JAXBHandle writeHandle = new JAXBHandle(context);
		writeHandle.set(product);

		// write the JAXB object as a document
		docMgr.write(docId, writeHandle);

		// create a handle to receive the document content
		JAXBHandle readHandle = new JAXBHandle(context);

		// read the JAXB object from the document content
		docMgr.read(docId, readHandle);

		// access the document content
		product = (Product) readHandle.get();

		// delete the document
		docMgr.delete(docId);

		System.out.println("Wrote, read, and deleted "+product.getName());

		// release the client
		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			DocumentFormats.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}
}
