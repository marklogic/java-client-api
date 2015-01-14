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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;

public class HandleAsTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testBuiltinReadWrite()
	throws IOException, XMLStreamException, ParserConfigurationException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, DOMException, SAXException, TransformerFactoryConfigurationError, TransformerException {
		String beforeText = "A simple text document";
		String afterText  = null;

		char[] cbuf = new char[beforeText.length() + 1024];
		int cnum = 0;

		String binDocId = "/test/testAs1.bin";
		BinaryDocumentManager binMgr = Common.client.newBinaryDocumentManager();

		byte[] beforeBytes = beforeText.getBytes("UTF-8");

		binMgr.writeAs(binDocId, beforeBytes);
		afterText = new String(binMgr.readAs(binDocId, byte[].class), "UTF-8");
		binMgr.delete(binDocId);
		assertEquals("byte[] difference in document read/write as", beforeText, afterText);

		binMgr.writeAs(binDocId, new ByteArrayInputStream(beforeBytes));
		cnum = new InputStreamReader(
				binMgr.readAs(binDocId, InputStream.class),
				"UTF-8"
				).read(cbuf);
		binMgr.delete(binDocId);
		assertEquals("InputStream count difference in document read/write as", beforeText.length(), cnum);
		afterText = new String(cbuf, 0, cnum);
		assertEquals("InputStream difference in document read/write as", beforeText, afterText);

		String textDocId = "/test/testAs1.txt";
		TextDocumentManager textMgr = Common.client.newTextDocumentManager();

		textMgr.writeAs(textDocId, beforeText);
		afterText = textMgr.readAs(textDocId, String.class);
		textMgr.delete(textDocId);
		assertEquals("String difference in document read/write as", beforeText, afterText);

		textMgr.writeAs(textDocId, new StringReader(beforeText));
		cnum = textMgr.readAs(textDocId, Reader.class).read(cbuf);
		textMgr.delete(textDocId);
		assertEquals("Reader count difference in document read/write as", beforeText.length(), cnum);
		afterText = new String(cbuf, 0, cnum);
		assertEquals("Reader difference in document read/write as", beforeText, afterText);

		File beforeFile = File.createTempFile("testAs", "txt");
		FileWriter tempWriter = new FileWriter(beforeFile);
		tempWriter.write(beforeText);
		tempWriter.flush();
		tempWriter.close();
		tempWriter = null;

		textMgr.writeAs(textDocId, beforeFile);
		beforeFile.delete();
		File afterFile = textMgr.readAs(textDocId, File.class);
		textMgr.delete(textDocId);
		cnum = new FileReader(afterFile).read(cbuf);
		afterFile.delete();
		assertEquals("File count difference in document read/write as", beforeText.length(), cnum);
		afterText = new String(cbuf, 0, cnum);
		assertEquals("File difference in document read/write as", beforeText, afterText);

		String xmlDocId = "/test/testAs1.xml";
		XMLDocumentManager xmlMgr = Common.client.newXMLDocumentManager();

		DocumentBuilder xmlDocBldr = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		Document beforeDocument = xmlDocBldr.newDocument();
		Element root = beforeDocument.createElement("doc");
		root.setTextContent(beforeText);
		beforeDocument.appendChild(root);

		String beforeDocStr = Common.testDocumentToString(beforeDocument);

		xmlMgr.writeAs(xmlDocId, beforeDocument);
		afterText = xmlMgr.readAs(xmlDocId, Document.class).getDocumentElement().getTextContent();
		xmlMgr.delete(xmlDocId);
		assertEquals("DOM difference in document read/write as", beforeText, afterText);

		xmlMgr.writeAs(xmlDocId, new InputSource(new StringReader(beforeDocStr)));
		afterText = xmlDocBldr.parse(
				xmlMgr.readAs(xmlDocId, InputSource.class)
				).getDocumentElement().getTextContent();
		xmlMgr.delete(xmlDocId);
		assertEquals("InputSource difference in document read/write as", beforeText, afterText);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();

		xmlMgr.writeAs(xmlDocId, new DOMSource(beforeDocument));
		DOMResult afterResult = new DOMResult();
		transformer.transform(
				xmlMgr.readAs(xmlDocId, Source.class),
				afterResult
				);
		xmlMgr.delete(xmlDocId);
		afterText = ((Document) afterResult.getNode()).getDocumentElement().getTextContent();
		assertEquals("Source difference in document read/write as", beforeText, afterText);

		XMLInputFactory inputFactory = XMLInputFactory.newFactory();
		
		xmlMgr.writeAs(xmlDocId,
				inputFactory.createXMLEventReader(new StringReader(beforeDocStr))
				);
		XMLEventReader afterEventReader = xmlMgr.readAs(xmlDocId, XMLEventReader.class);
		xmlMgr.delete(xmlDocId);
		afterEventReader.nextTag();
		afterText = afterEventReader.getElementText();
		afterEventReader.close();
		assertEquals("EventReader difference in document read/write as", beforeText, afterText);

		xmlMgr.writeAs(xmlDocId,
				inputFactory.createXMLStreamReader(new StringReader(beforeDocStr))
				);
		XMLStreamReader afterStreamReader = xmlMgr.readAs(xmlDocId, XMLStreamReader.class);
		xmlMgr.delete(xmlDocId);
		afterStreamReader.nextTag();
		afterText = afterStreamReader.getElementText();
		afterStreamReader.close();
		assertEquals("StreamReader difference in document read/write as", beforeText, afterText);
	}

	@Test
	public void testSearch() throws JAXBException {
		DatabaseClientFactory.Bean clientFactoryBean = makeClientFactory();

		clientFactoryBean.getHandleRegistry().register(
				JAXBHandle.newFactory(Product.class)
				);

		DatabaseClient client = clientFactoryBean.newClient();

		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		QueryManager queryMgr = client.newQueryManager();

		String basedir = "/tmp/jaxb/test/";

		Product product1 = new Product();
		product1.setName("Widgetry");
		product1.setIndustry("IT");
		product1.setDescription("More widgets than you can shake a stick at");

		Product product2 = new Product();
		product2.setName("AppExcess");
		product2.setIndustry("IT");
		product2.setDescription("There's an app for that.");

		Product[] products = {product1, product2};

		// setup
		Set<String> prodNames = new HashSet<String>(products.length);
		for (Product product: products) {
			String prodName = product.getName();
			prodNames.add(prodName);
			String docId = basedir+prodName+".xml";
			docMgr.writeAs(docId, product);			
		}

		// test
		String rawQuery = new StringBuilder()
			.append("<search:search xmlns:search=\"http://marklogic.com/appservices/search\">")
			.append("<search:qtext>IT</search:qtext>")
			.append("<search:options>")
			.append("<search:transform-results apply=\"raw\"/> ")
			.append("</search:options>")
			.append("</search:search>")
			.toString();

		QueryDefinition queryDef =
			queryMgr.newRawCombinedQueryDefinitionAs(Format.XML, rawQuery);
		queryDef.setDirectory(basedir);

		SearchHandle handle = queryMgr.search(queryDef, new SearchHandle());

		MatchDocumentSummary[] summaries = handle.getMatchResults();
		assertEquals("raw query should retrieve all products", products.length, summaries.length);
		for (MatchDocumentSummary summary: summaries) {
			Product product = summary.getFirstSnippetAs(Product.class);
			assertTrue("raw product should exist", product != null);
			assertTrue("raw product name should be preserved", prodNames.contains(product.getName()));
		}

		rawQuery = new StringBuilder()
			.append("<search:search xmlns:search=\"http://marklogic.com/appservices/search\">")
			.append("<search:qtext>IT</search:qtext>")
			.append("<search:options>")
			.append("<search:extract-metadata>")
			.append("<search:qname elem-ns=\"\" elem-name=\"name\"/>")
			.append("<search:qname elem-ns=\"\" elem-name=\"industry\"/>")
			.append("</search:extract-metadata>")
			.append("</search:options>")
			.append("</search:search>")
			.toString();

		queryDef =
			queryMgr.newRawCombinedQueryDefinitionAs(Format.XML, rawQuery);
		queryDef.setDirectory(basedir);

		handle = queryMgr.search(queryDef, new SearchHandle());

		summaries = handle.getMatchResults();
		assertEquals("metadata query should retrieve all products", products.length, summaries.length);
		for (MatchDocumentSummary summary: summaries) {
			Document productDoc = summary.getMetadataAs(Document.class);

			Element name     = (Element) productDoc.getElementsByTagName("name").item(0);
			assertTrue("metadata product name should exist", name != null);
			assertTrue("metadata product name should be preserved", prodNames.contains(name.getTextContent()));

			Element industry = (Element) productDoc.getElementsByTagName("industry").item(0);
			assertTrue("metadata product industry should exist", industry != null);
		}

		// cleanup
		DeleteQueryDefinition deleteDef = queryMgr.newDeleteDefinition();
		deleteDef.setDirectory(basedir);

		queryMgr.delete(deleteDef);

		client.release();
	}

	@Test
	public void testHandleRegistry() {
		int[] iterations = {1,2};
		for (int i: iterations) {
			DatabaseClientFactory.Bean clientFactoryBean = (i == 1) ? null : makeClientFactory();

			HandleFactoryRegistry registry =
				(i == 1) ? DatabaseClientFactory.getHandleRegistry()
				: clientFactoryBean.getHandleRegistry();

			registry.register(new BufferHandleFactory());
			assertTrue("Handle is not registered",registry.isRegistered(StringBuilder.class));
	
			Set<Class<?>> registered = registry.listRegistered();
			assertTrue("Handle is not in registered set",registered.contains(StringBuilder.class));

			ContentHandle<StringBuilder> handle = registry.makeHandle(StringBuilder.class);
			assertNotNull("Made a null handle", handle);
			assertEquals("Made handle with the wrong class",handle.getClass(),BufferHandle.class);

			// instantiate a client with a copy of the registry
			DatabaseClient client = 
				(i == 1) ? Common.newClient()
				: clientFactoryBean.newClient();

			registry.unregister(StringBuilder.class);
			assertTrue("Handle is still registered",!registry.isRegistered(StringBuilder.class));

			String beforeText = "A simple text document";

			TextDocumentManager textMgr = client.newTextDocumentManager();
			String textDocId = "/test/testAs1.txt";

			// use the handled class
			StringBuilder buffer = new StringBuilder();
			buffer.append(beforeText);
			
			textMgr.writeAs(textDocId, buffer);
			buffer = textMgr.readAs(textDocId, StringBuilder.class);
			textMgr.delete(textDocId);
			assertEquals("Registered handle difference in document read/write as", beforeText, buffer.toString());

			boolean threwError = false;
			try {
				textMgr.writeAs(textDocId, new Integer(5));
			} catch(Exception e) {
				threwError = true;
			}
			assertTrue("No error for write of unregistered class",threwError);
				
			threwError = false;
			try {
				textMgr.readAs(textDocId, Integer.class);
			} catch(Exception e) {
				threwError = true;
			}
			assertTrue("No error for read of unregistered class",threwError);

			client.release();
		}
	}

	private DatabaseClientFactory.Bean makeClientFactory() {
		DatabaseClientFactory.Bean clientFactoryBean = new DatabaseClientFactory.Bean();
		clientFactoryBean.setHost(Common.HOST);
		clientFactoryBean.setPort(Common.PORT);
		clientFactoryBean.setUser(Common.USERNAME);
		clientFactoryBean.setPassword(Common.PASSWORD);
		clientFactoryBean.setAuthentication(Authentication.DIGEST);
		return clientFactoryBean;
	}

	static public class BufferHandle
	extends BaseHandle<String, String>
	implements ContentHandle<StringBuilder> {
		private StringBuilder content = new StringBuilder();

		@Override
		public StringBuilder get() {
			return content;
		}
		@Override
		public void set(StringBuilder content) {
			this.content = content;
		}
		@Override
		protected Class<String> receiveAs() {
			return String.class;
		}
		@Override
		protected void receiveContent(String content) {
			this.content.delete(0, this.content.length());
			this.content.append(content);
		}
		@Override
		protected String sendContent() {
			return content.toString();
		}
		@Override
		protected boolean isResendable() {
			return true;
		}
	}
	static public class BufferHandleFactory implements ContentHandleFactory {
		@Override
		public Class<?>[] getHandledClasses() {
			return new Class<?>[]{StringBuilder.class};
		}
		@Override
		public boolean isHandled(Class<?> type) {
			return (type == StringBuilder.class);
		}
		@Override
		public <C> ContentHandle<C> newHandle(Class<C> type) {
			if (!isHandled(type))
				return null;
			@SuppressWarnings("unchecked")
			ContentHandle<C> handle = (ContentHandle<C>) new BufferHandle();
			return handle;
		}
	}

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
}
