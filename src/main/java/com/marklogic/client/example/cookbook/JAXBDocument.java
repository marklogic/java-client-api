/*
 * Copyright 2012 MarkLogic Corporation
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
 * JAXBDocument illustrates how to write and read a JAXB object structure as a database document.
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
		System.out.println("example: "+JAXBDocument.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		JAXBContext context = JAXBContext.newInstance(Product.class);

		Product product = new Product();
		product.setName("FashionForward");
		product.setIndustry("Retail");
		product.setDescription(
				"Creates demand with high prices, hours from midnight to dawn, and frequent moves");

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document
		String docId = "/example/"+product.getName()+".xml";

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
		
		// ... do something with the JAXB object ...

		// read the persisted XML document for the logging message
		String productDoc = docMgr.read(docId, new StringHandle()).get();

		// delete the document
		docMgr.delete(docId);

		System.out.println("Wrote, read, and deleted "+product.getName()+" using JAXB:\n"+productDoc);

		// release the client
		client.release();
	}
}
