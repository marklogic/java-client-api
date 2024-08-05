/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.*;
import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HandleAsTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
  }
  @AfterAll
  public static void afterClass() {
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
    assertEquals(beforeText, afterText);

    binMgr.writeAs(binDocId, new ByteArrayInputStream(beforeBytes));
    try ( Reader reader = new InputStreamReader( binMgr.readAs(binDocId, InputStream.class), "UTF-8") ) {
      cnum = reader.read(cbuf);
      binMgr.delete(binDocId);
      assertEquals(beforeText.length(), cnum);
      afterText = new String(cbuf, 0, cnum);
      assertEquals(beforeText, afterText);
    }

    String textDocId = "/test/testAs1.txt";
    TextDocumentManager textMgr = Common.client.newTextDocumentManager();

    textMgr.writeAs(textDocId, beforeText);
    afterText = textMgr.readAs(textDocId, String.class);
    textMgr.delete(textDocId);
    assertEquals(beforeText, afterText);

    textMgr.writeAs(textDocId, new StringReader(beforeText));
    try ( Reader reader = textMgr.readAs(textDocId, Reader.class) ) {
      cnum = reader.read(cbuf);
      textMgr.delete(textDocId);
      assertEquals(beforeText.length(), cnum);
      afterText = new String(cbuf, 0, cnum);
      assertEquals(beforeText, afterText);
    }

    File beforeFile = File.createTempFile("testAs", "txt");
    try (FileWriter tempWriter = new FileWriter(beforeFile)) {
      tempWriter.write(beforeText);
      tempWriter.flush();
    }

    textMgr.writeAs(textDocId, beforeFile);
    beforeFile.delete();
    File afterFile = textMgr.readAs(textDocId, File.class);
    textMgr.delete(textDocId);
    try ( Reader reader = new FileReader(afterFile) ) {
      cnum = reader.read(cbuf);
      afterFile.delete();
      assertEquals(beforeText.length(), cnum);
      afterText = new String(cbuf, 0, cnum);
      assertEquals(beforeText, afterText);
    }

    String xmlDocId = "/test/testAs1.xml";
    XMLDocumentManager xmlMgr = Common.client.newXMLDocumentManager();

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);
    DocumentBuilder xmlDocBldr = factory.newDocumentBuilder();

    Document beforeDocument = xmlDocBldr.newDocument();
    Element root = beforeDocument.createElement("doc");
    root.setTextContent(beforeText);
    beforeDocument.appendChild(root);

    String beforeDocStr = Common.testDocumentToString(beforeDocument);

    xmlMgr.writeAs(xmlDocId, beforeDocument);
    afterText = xmlMgr.readAs(xmlDocId, Document.class).getDocumentElement().getTextContent();
    xmlMgr.delete(xmlDocId);
    assertEquals(beforeText, afterText);

    xmlMgr.writeAs(xmlDocId, new InputSource(new StringReader(beforeDocStr)));
    afterText = xmlDocBldr.parse(
      xmlMgr.readAs(xmlDocId, InputSource.class)
    ).getDocumentElement().getTextContent();
    xmlMgr.delete(xmlDocId);
    assertEquals(beforeText, afterText);

    Transformer transformer = TransformerFactory.newInstance().newTransformer();

    xmlMgr.writeAs(xmlDocId, new DOMSource(beforeDocument));
    DOMResult afterResult = new DOMResult();
    transformer.transform(
      xmlMgr.readAs(xmlDocId, Source.class),
      afterResult
    );
    xmlMgr.delete(xmlDocId);
    afterText = ((Document) afterResult.getNode()).getDocumentElement().getTextContent();
    assertEquals(beforeText, afterText);

    XMLInputFactory inputFactory = XMLInputFactory.newFactory();

    xmlMgr.writeAs(xmlDocId,
      inputFactory.createXMLEventReader(new StringReader(beforeDocStr))
    );
    XMLEventReader afterEventReader = xmlMgr.readAs(xmlDocId, XMLEventReader.class);
    try {
      xmlMgr.delete(xmlDocId);
      afterEventReader.nextTag();
      afterText = afterEventReader.getElementText();
      afterEventReader.close();
      assertEquals(beforeText, afterText);
    } finally {
      afterEventReader.close();
    }

    xmlMgr.writeAs(xmlDocId,
      inputFactory.createXMLStreamReader(new StringReader(beforeDocStr))
    );
    XMLStreamReader afterStreamReader = xmlMgr.readAs(xmlDocId, XMLStreamReader.class);
    try {
      xmlMgr.delete(xmlDocId);
      afterStreamReader.nextTag();
      afterText = afterStreamReader.getElementText();
      afterStreamReader.close();
      assertEquals(beforeText, afterText);
    } finally {
      afterStreamReader.close();
    }
  }

  @Test
  public void testSearch() throws JAXBException {
    DatabaseClientFactory.Bean clientFactoryBean = makeClientFactoryBean();

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
    Set<String> prodNames = new HashSet<>(products.length);
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
    assertEquals(products.length, summaries.length);
    for (MatchDocumentSummary summary: summaries) {
      Product product = summary.getFirstSnippetAs(Product.class);
      assertTrue(product != null);
      assertTrue(prodNames.contains(product.getName()));
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
    assertEquals(products.length, summaries.length);
    for (MatchDocumentSummary summary: summaries) {
      Document productDoc = summary.getMetadataAs(Document.class);

      Element name     = (Element) productDoc.getElementsByTagName("name").item(0);
      assertTrue(name != null);
      assertTrue(prodNames.contains(name.getTextContent()));

      Element industry = (Element) productDoc.getElementsByTagName("industry").item(0);
      assertTrue(industry != null);
    }

    // cleanup
    DeleteQueryDefinition deleteDef = queryMgr.newDeleteDefinition();
    deleteDef.setDirectory(basedir);

    queryMgr.delete(deleteDef);

    client.release();
  }

	@Test
	void testHandleRegistryWithClientFactoryBean() {
		verifyHandleRegistry(makeClientFactoryBean());
	}

	@Test
	void testHandleRegistryWithoutClientFactoryBean() {
		verifyHandleRegistry(null);
	}

	private void verifyHandleRegistry(DatabaseClientFactory.Bean clientFactoryBean) {
		HandleFactoryRegistry registry = clientFactoryBean == null ?
			DatabaseClientFactory.getHandleRegistry() :
			clientFactoryBean.getHandleRegistry();

		registry.register(new BufferHandleFactory());
		assertTrue(registry.isRegistered(StringBuilder.class));

		Set<Class<?>> registered = registry.listRegistered();
		assertTrue(registered.contains(StringBuilder.class));

		ContentHandle<StringBuilder> handle = registry.makeHandle(StringBuilder.class);
		assertNotNull(handle);
		assertEquals(handle.getClass(),BufferHandle.class);

		// instantiate a client with a copy of the registry
		DatabaseClient client = clientFactoryBean == null ?
			Common.newClientBuilder().build() :
			clientFactoryBean.newClient();

		registry.unregister(StringBuilder.class);
		assertTrue(!registry.isRegistered(StringBuilder.class));

		String beforeText = "A simple text document";

		TextDocumentManager textMgr = client.newTextDocumentManager();
		String textDocId = "/test/testAs1.txt";

		// use the handled class
		StringBuilder buffer = new StringBuilder();
		buffer.append(beforeText);

		textMgr.writeAs(textDocId, buffer);
		buffer = textMgr.readAs(textDocId, StringBuilder.class);
		textMgr.delete(textDocId);
		assertEquals(beforeText, buffer.toString());

		boolean threwError = false;
		try {
			textMgr.writeAs(textDocId, new Integer(5));
		} catch(Exception e) {
			threwError = true;
		}
		assertTrue(threwError);

		threwError = false;
		try {
			textMgr.readAs(textDocId, Integer.class);
		} catch(Exception e) {
			threwError = true;
		}
		assertTrue(threwError);

		client.release();
	}

	private DatabaseClientFactory.Bean makeClientFactoryBean() {
		DatabaseClientFactory.Bean clientFactoryBean = new DatabaseClientFactory.Bean();
		clientFactoryBean.setHost(Common.HOST);
		clientFactoryBean.setPort(Common.PORT);
		clientFactoryBean.setBasePath(Common.BASE_PATH);
		clientFactoryBean.setSecurityContext(Common.newSecurityContext(Common.USER, Common.PASS));
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
