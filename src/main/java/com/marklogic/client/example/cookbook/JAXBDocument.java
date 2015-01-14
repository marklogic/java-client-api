/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.example.cookbook;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.StringHandle;

/**
 * JAXBDocument illustrates how to write and read a POJO structure as a database document.
 */
public class JAXBDocument {
	public static void main(String[] args) throws JAXBException, IOException {
		run(Util.loadProperties());
	}

	/**
	 * Product provides an example of a class with JAXB annotations.
	 */
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

	public static void run(ExampleProperties props) throws JAXBException {
		System.out.println("example: "+JAXBDocument.class.getName()+"\n");

		// use either shortcut or strong typed IO
		runShortcut(props);
		runStrongTyped(props);

		System.out.println("Wrote, read, and deleted "+Product.class.getName()+" using JAXB");
	}
	public static void runShortcut(ExampleProperties props) throws JAXBException {
		// register the POJO classes
		DatabaseClientFactory.getHandleRegistry().register(
			JAXBHandle.newFactory(Product.class)
			);

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an instance of the POJO class
		Product product = new Product();
		product.setName("FashionForward");
		product.setIndustry("Retail");
		product.setDescription(
				"(Shortcut) Creates demand with high prices, hours from midnight to dawn, and frequent moves");

		// create an identifier for the document
		String docId = "/example/"+product.getName()+".xml";

		// write the POJO as the document content
		docMgr.writeAs(docId, product);

		// ... at some other time ...

		// read the POJO from the document content
		product = docMgr.readAs(docId, Product.class);

		// log the persisted XML document
		System.out.println(docMgr.readAs(docId, String.class));

		// delete the document
		docMgr.delete(docId);

		// release the client
		client.release();
	}
	public static void runStrongTyped(ExampleProperties props) throws JAXBException {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		JAXBContext context = JAXBContext.newInstance(Product.class);

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create a handle for a POJO class marshaled by the JAXB context
		// Note:  to use a single handle for any of the POJO classes
		// identified to the JAXB context, use a <?> wildcard instead of
		// a specific class and call the get(class) method instead of
		// the get() method.
		JAXBHandle<Product> handle = new JAXBHandle<Product>(context);

		// create an instance of the POJO class
		Product product = new Product();
		product.setName("FashionForward");
		product.setIndustry("Retail");
		product.setDescription(
				"(Strong Typed) Creates demand with high prices, hours from midnight to dawn, and frequent moves");

		// create an identifier for the document
		String docId = "/example/"+product.getName()+".xml";

		// set the handle to the POJO instance
		handle.set(product);

		// write the POJO as the document content
		docMgr.write(docId, handle);

		// ... at some other time ...

		// read the POJO from the document content
		docMgr.read(docId, handle);

		// access the document content
		product = handle.get();
		
		// ... do something with the POJO ...

		// read the persisted XML document for the logging message
		System.out.println(docMgr.read(docId, new StringHandle()).get());

		// delete the document
		docMgr.delete(docId);

		// release the client
		client.release();
	}
}
