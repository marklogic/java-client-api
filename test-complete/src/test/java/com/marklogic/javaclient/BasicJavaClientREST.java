package com.marklogic.javaclient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;





import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


//import sun.java2d.loops.XorPixelWriter.ByteData;



import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputSourceHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.OutputStreamHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.XMLEventReaderHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;



//import com.sun.xml.internal.ws.util.xml.StAXSource;
//Importing for http client calls
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.*;


public abstract class BasicJavaClientREST extends ConnectedRESTQA
{
    protected static String checkDoc =
    	"xquery version '1.0-ml';\n" +
    	"fn:doc('/bar/test/myBar.txt')";
	
    public BasicJavaClientREST() {
		
	}
	
	/**
	 * Write document using InputStreamHandle
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws FileNotFoundException 
	 */
	public void writeDocumentUsingInputStreamHandle(DatabaseClient client, String filename, String uri, String type) throws FileNotFoundException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	

		// create handle
		InputStreamHandle contentHandle = new InputStreamHandle();

		// get the file
		InputStream inputStream = new FileInputStream("src/test/java/com/marklogic/javaclient/data/" + filename);
		
		// set uri
		String docId = uri + filename;

		contentHandle.set(inputStream);
			
		// write doc
		docMgr.write(docId, contentHandle);
		
		System.out.println("Write " + docId + " to database");
	}
	
	/**
	 * Write document using InputStreamHandle with metadata
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @param metadataHandle
	 * @throws FileNotFoundException
	 */
	public void writeDocumentUsingInputStreamHandle(DatabaseClient client, String filename, String uri, DocumentMetadataHandle metadataHandle, String type) throws FileNotFoundException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	

		// create handle
		InputStreamHandle contentHandle = new InputStreamHandle();

		// get the file
		InputStream inputStream = new FileInputStream("src/test/java/com/marklogic/javaclient/data/" + filename);
		
		// set uri
		String docId = uri + filename;

		contentHandle.set(inputStream);
			
		// write doc
		docMgr.write(docId, metadataHandle, contentHandle);
		
		System.out.println("Write " + docId + " to database");
	}

	public void writeDocumentUsingInputStreamHandle(DatabaseClient client, String filename, String uri, Transaction transaction, String type) throws FileNotFoundException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	

		// create handle
		InputStreamHandle contentHandle = new InputStreamHandle();

		// get the file
		InputStream inputStream = new FileInputStream("src/test/java/com/marklogic/javaclient/data/" + filename);
		
		// set uri
		String docId = uri + filename;

		contentHandle.set(inputStream);
			
		// write doc
		docMgr.write(docId, contentHandle, transaction);
		
		System.out.println("Write " + docId + " to database");
	}
		
	/**
	 * Reading document using InputStreamHandle
	 * @param client
	 * @param uri
	 * @param type
	 * @return
	 */
	public InputStreamHandle readDocumentUsingInputStreamHandle(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);
		
		// create handle
		InputStreamHandle contentHandle = new InputStreamHandle();
		
		// create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		
		docMgr.read(readDocId, contentHandle);
		
		return contentHandle;
	}
	/**
	 * Reading document using InputSourceHandle
	 * @param client
	 * @param uri
	 * @param type
	 * @return
	 */
	
	public InputSourceHandle readDocumentUsingInputSourceHandle(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);
		
		// create handle
		InputSourceHandle contentHandle = new InputSourceHandle();		
		
		// create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		
		docMgr.read(readDocId, contentHandle);
		
		return contentHandle;
	}
	/**
	 * Reading document using SourceHandle
	 * @param client
	 * @param uri
	 * @param type
	 * @return
	 */
	
	public SourceHandle readDocumentUsingSourceHandle(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);
		
		// create handle
		SourceHandle contentHandle = new SourceHandle();		
		
		// create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		
		docMgr.read(readDocId, contentHandle);
		
		return contentHandle;
	}
	
	
	/**
	 * Update document using InputStreamHandle
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws FileNotFoundException
	 */
	public void updateDocumentUsingInputStreamHandle(DatabaseClient client, String filename, String uri, String type) throws FileNotFoundException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	

		// create handle
		InputStreamHandle contentHandle = new InputStreamHandle();

		// get the file
		InputStream inputStream = new FileInputStream("src/test/java/com/marklogic/javaclient/data/" + filename);
		
		// set uri
		String docId = uri;

		contentHandle.set(inputStream);
			
		// write doc
		docMgr.write(docId, contentHandle);
		
		System.out.println("Update " + docId + " to database");
	}

	/**
	 * Write document using BytesHandle
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws IOException
	 */
	public void writeDocumentUsingBytesHandle(DatabaseClient client, String filename, String uri, String type) throws IOException
	{
		// get the content to bytes
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);		 
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        for (int readNum; (readNum = fis.read(buf)) != -1;) 
        {
            bos.write(buf, 0, readNum);
        }
        
        byte[] bytes = bos.toByteArray();
        
        fis.close();
        bos.close();
        
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
	 
		String docId = uri + filename;
		
	    // create handle
	    BytesHandle contentHandle = new BytesHandle();
	    contentHandle.set(bytes);
	    
	    // write the doc
	    docMgr.write(docId, contentHandle);
	    
	    System.out.println("Write " + docId + " to the database");
	}
		
	/**
	 * Write document using BytesHandle with metadata
	 * @param client
	 * @param filename
	 * @param uri
	 * @param metadataHandle
	 * @param type
	 * @throws IOException
	 */
	public void writeDocumentUsingBytesHandle(DatabaseClient client, String filename, String uri, DocumentMetadataHandle metadataHandle, String type) throws IOException
	{
		// get the content to bytes
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);		 
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        for (int readNum; (readNum = fis.read(buf)) != -1;) 
        {
            bos.write(buf, 0, readNum);
        }
        
        byte[] bytes = bos.toByteArray();
        
        fis.close();
        bos.close();
        
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
	 
		String docId = uri + filename;
		
	    // create handle
	    BytesHandle contentHandle = new BytesHandle();
	    contentHandle.set(bytes);
	    
	    // write the doc
	    docMgr.write(docId, metadataHandle, contentHandle);
	    
	    System.out.println("Write " + docId + " to the database");
	}

        /**
         * Write document using StringHandle with metadata
         * @param client
         * @param filename
         * @param uri
         * @param metadataHandle
         * @param type
         * @throws IOException
         */
        public void writeDocumentUsingStringHandle(DatabaseClient client, String filename, String uri, DocumentMetadataHandle metadataHandle, String type) throws IOException
        {
                // acquire the content
                File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);
        FileInputStream fis = new FileInputStream(file);
            Scanner scanner = new Scanner(fis).useDelimiter("\\Z");
            String readContent = scanner.next();
            fis.close();
            scanner.close();

                // create doc manager
                DocumentManager docMgr = null;
                docMgr = documentManagerSelector(client, docMgr, type);

                String docId = uri + filename;

            // create handle
            StringHandle contentHandle = new StringHandle();
            contentHandle.set(readContent);

            // write the doc
            docMgr.write(docId, metadataHandle, contentHandle);

            System.out.println("Write " + docId + " to the database");
        }
	
        /**
         * Write document using StringHandle
         * @param client
         * @param filename
         * @param uri
         * @param type
         * @throws IOException
         */
        public void writeDocumentUsingStringHandle(DatabaseClient client, String filename, String uri, String type) throws IOException
        {
                // acquire the content
                File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);
        FileInputStream fis = new FileInputStream(file);
            Scanner scanner = new Scanner(fis).useDelimiter("\\Z");
            String readContent = scanner.next();
            fis.close();
            scanner.close();

                // create doc manager
                DocumentManager docMgr = null;
                docMgr = documentManagerSelector(client, docMgr, type);

                String docId = uri + filename;

            // create handle
            StringHandle contentHandle = new StringHandle();
            contentHandle.set(readContent);

            // write the doc
            docMgr.write(docId, contentHandle);

            System.out.println("Write " + docId + " to the database");
        }
	
	/**
	 * Write document using JAXBHandle with metadata
	 * @param client
	 * @param product
	 * @param uri
	 * @param metadataHandle
	 * @param type
	 * @throws JAXBException
	 */
	public void writeDocumentUsingJAXBHandle(DatabaseClient client, Product product, String uri, DocumentMetadataHandle metadataHandle, String type) throws JAXBException
	{
		// set jaxb context 
		JAXBContext context = JAXBContext.newInstance(Product.class);
		
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
		
	    // create an identifier for the document
	    String docId = uri + product.getName() + ".xml";

	    // create a handle on the content
	    JAXBHandle contentHandle = new JAXBHandle(context);
	    contentHandle.set(product);
	    
	    // write the doc
	    docMgr.write(docId, metadataHandle, contentHandle);
	    
	    System.out.println("Write " + docId + " to the database");
	}
	
	/**
	 * Write document using OutputStreamHandle with metadata
	 * @param client
	 * @param filename
	 * @param uri
	 * @param metadataHandle
	 * @param type
	 */
	public void writeDocumentUsingOutputStreamHandle(DatabaseClient client, final String filename, String uri, DocumentMetadataHandle metadataHandle, String type)
	{
		final int MAX_BUF = 1024;
		
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
	 
		String docId = uri + filename;
		
		// create an anonymous class with a callback method
		OutputStreamSender sender = new OutputStreamSender() {
            // the callback receives the output stream
			public void write(OutputStream out) throws IOException {
        		// acquire the content
				InputStream docStream = new FileInputStream("src/test/java/com/marklogic/javaclient/data/" + filename);
				
        		// copy content to the output stream
        		byte[] buf = new byte[MAX_BUF];
        		int byteCount = 0;
        		while ((byteCount=docStream.read(buf)) != -1) {
        			out.write(buf, 0, byteCount);
        		}
            }
        };
        
        // create the handle
        OutputStreamHandle contentHandle = new OutputStreamHandle(sender);
        
     // write the doc
	    docMgr.write(docId, metadataHandle, contentHandle);
	    
        System.out.println("Write " + docId + " to the database");
	}
	public void writeDocumentUsingOutputStreamHandle(DatabaseClient client, final String filename, String uri, String type)
	{
		final int MAX_BUF = 1024;
		
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
	 
		String docId = uri + filename;
		
		// create an anonymous class with a callback method
		OutputStreamSender sender = new OutputStreamSender() {
            // the callback receives the output stream
			public void write(OutputStream out) throws IOException {
        		// acquire the content
				InputStream docStream = new FileInputStream("src/test/java/com/marklogic/javaclient/data/" + filename);
				
        		// copy content to the output stream
        		byte[] buf = new byte[MAX_BUF];
        		int byteCount = 0;
        		while ((byteCount=docStream.read(buf)) != -1) {
        			out.write(buf, 0, byteCount);
        		}
            }
        };
        
        // create the handle
        OutputStreamHandle contentHandle = new OutputStreamHandle(sender);
        
     // write the doc
	    docMgr.write(docId, contentHandle);
	    
        System.out.println("Write " + docId + " to the database");
	}
	public void updateDocumentUsingOutputStreamHandle(DatabaseClient client,final String filename, String uri, String type) throws FileNotFoundException
	{
		final int MAX_BUF = 1024;
		
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
	
		String docId = uri;
		
		// create an anonymous class with a callback method
		OutputStreamSender sender = new OutputStreamSender() {
            // the callback receives the output stream
			public void write(OutputStream out) throws IOException {
        		// acquire the content
				InputStream docStream = new FileInputStream("src/test/java/com/marklogic/javaclient/data/" + filename);
				
        		// copy content to the output stream
        		byte[] buf = new byte[MAX_BUF];
        		int byteCount = 0;
        		while ((byteCount=docStream.read(buf)) != -1) {
        			out.write(buf, 0, byteCount);
        		}
            }
        };

		// create handle
		OutputStreamHandle contentHandle = new OutputStreamHandle(sender);
		// set uri
		// write doc
		docMgr.write(docId, contentHandle);
		
		System.out.println("Update " + docId + " to database");
	}
	/**
	 * Write document to databae using ReaderHandle
	 * @param client: the database client connection
	 * @param filename: the filename 
	 * @param uri: the document uri
	 * @param type: the document type (XML, Text, JSON, or Binary)
	 * @throws IOException 
	 */
	public void writeDocumentReaderHandle(DatabaseClient client, String filename, String uri, String type) throws IOException
	{   
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
				
		// acquire the content
		BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/javaclient/data/" + filename));
			 
	    // create an identifier for the document
		String docId = uri + filename;
	    
	    // create a handle on the content
	    ReaderHandle handle = new ReaderHandle();
	    handle.set(docStream);
	    
	    // write the document content
	    docMgr.write(docId, handle);
	    
	    System.out.println("Write " + docId + " to database");
	    
	    docStream.close();
	}
	
	/**
	 * Update document using ReaderHandle
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws IOException 
	 */
	public void updateDocumentReaderHandle(DatabaseClient client, String filename, String uri, String type) throws IOException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
		
		// acquire the content
		BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/javaclient/data/" + filename));
			 
	    // create an identifier for the document
		String docId = uri;
	    
	    // create a handle on the content
	    ReaderHandle handle = new ReaderHandle();
	    handle.set(docStream);
	    
	    // write the document content
	    docMgr.write(docId, handle);
	    
	    System.out.println("Update " + docId + " to database");	
	    
	    docStream.close();
	}

	/**
	 * Read document using ReaderHandle
	 * @param client
	 * @param uri
	 * @param type
	 * @return
	 */
	public ReaderHandle readDocumentReaderHandle(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
				
		// create handle
		ReaderHandle readerHandle = new ReaderHandle();
		
		// create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		
		docMgr.read(readDocId, readerHandle);
		
		return readerHandle;
	}
	/**
	 * Read document using XMLEventReaderHandle
	 * @param client
	 * @param uri
	 * @param type
	 * @return
	 */
	public XMLEventReaderHandle readDocumentUsingXMLEventReaderHandle(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
				
		// create handle
		XMLEventReaderHandle readerHandle = new XMLEventReaderHandle();
		// create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		
		docMgr.read(readDocId, readerHandle);
		
		return readerHandle;
	}
	
	public XMLStreamReaderHandle readDocumentUsingXMLStreamReaderHandle (DatabaseClient client, String uri, String type){
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
				
		// create handle
		XMLStreamReaderHandle readerHandle = new XMLStreamReaderHandle();
		// create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		
		docMgr.read(readDocId, readerHandle);
		
		return readerHandle;
	}
	/**
	 * Write document using DOMHandle
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public void writeDocumentUsingDOMHandle(DatabaseClient client, String filename, String uri, String type) throws IOException, ParserConfigurationException, SAXException
	{   
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
				
		// acquire the content
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document content = docBuilder.parse(new File("src/test/java/com/marklogic/javaclient/data/" + filename));
			 
	    // create an identifier for the document
		String docId = uri + filename;
	    
	    // create a handle on the content
	    DOMHandle handle = new DOMHandle();
	    handle.set(content);
	    
	    // write the document content
	    docMgr.write(docId, handle);
	    
	    System.out.println("Write " + docId + " to database");
	}
	/**
	 * Returning a content of the document
	 * 
	 */
	
	public Document getDocumentContent(String xmltype) throws IOException, ParserConfigurationException, SAXException
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document content = docBuilder.newDocument();
		Element rootElement = content.createElement("foo");
		rootElement.appendChild(content.createTextNode(xmltype));
		content.appendChild(rootElement);
		
//		content.createTextNode(xmltype);
		return content;
		
				
			
	}
	/**
	 * Update document using DOMHandle
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void updateDocumentUsingDOMHandle(DatabaseClient client, String filename, String uri, String type) throws IOException, ParserConfigurationException, SAXException
	{   
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	
				
		// acquire the content
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document content = docBuilder.parse(new File("src/test/java/com/marklogic/javaclient/data/" + filename));
			 
	    // create an identifier for the document
		String docId = uri;
	    
	    // create a handle on the content
	    DOMHandle handle = new DOMHandle();
	    handle.set(content);
	    
	    // write the document content
	    docMgr.write(docId, handle);
	    
	    System.out.println("Update " + docId + " to database");
	}
	
	/**
	 * Read document using DOMHandle
	 * @param client
	 * @param uri
	 * @param type
	 * @return
	 */
	public DOMHandle readDocumentUsingDOMHandle(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);
		
		// create handle
		DOMHandle contentHandle = new DOMHandle();
		
		// create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		
		docMgr.read(readDocId, contentHandle);
		
		return contentHandle;
	}
	/**
	 * Read document using JAXBHandle
	 * @param client
	 * @param uri
	 * @param type
	 * @return
	 * @throws JAXBException 
	 */
	public JAXBHandle readDocumentUsingJAXBHandle(DatabaseClient client, String uri, String type) throws JAXBException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);
		
		// create handle
		JAXBContext con = JAXBContext.newInstance(Product.class);
		JAXBHandle contentHandle = new JAXBHandle(con);
		
		
		// create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		
		docMgr.read(readDocId, contentHandle);
		
		return contentHandle;
	}
        /**
         * Read document using StringHandle
         * @param client
         * @param uri
         * @param type
         * @return
         */
        public StringHandle readDocumentUsingStringHandle(DatabaseClient client, String uri, String type)
        {
                // create doc manager
                DocumentManager docMgr = null;
                docMgr = documentManagerSelector(client, docMgr, type);

                // create handle
                StringHandle contentHandle = new StringHandle();
//                contentHandle.set(readContent);

                // create doc id
                String readDocId = uri;
                System.out.println("Read " + readDocId + " from database");

                docMgr.read(readDocId, contentHandle);

                return contentHandle;
        }
	
	/**
	 * Write document using FileHandle
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws IOException
	 */
	public void writeDocumentUsingFileHandle(DatabaseClient client, String filename, String uri, String type) throws IOException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);

		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);
				
	    // create an identifier for the document
		String docId = uri + filename;
	    
	    // create a handle on the content
	    FileHandle handle = new FileHandle(file);
	    handle.set(file);
	    
	    // write the document content
	    docMgr.write(docId, handle);
	    
	    System.out.println("Write " + docId + " to database");		
	}

	/**
	 * Read document using FileHandle
	 * @param client
	 * @param uri
	 * @param type
	 * @return
	 */
	public FileHandle readDocumentUsingFileHandle(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);
		
		// create handle
		FileHandle contentHandle = new FileHandle();
		
		// create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		
		docMgr.read(readDocId, contentHandle);
		
		return contentHandle;		
	}
	
	/**
	 * Update document using FileHandle
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws IOException
	 */
	public void updateDocumentUsingFileHandle(DatabaseClient client, String filename, String uri, String type) throws IOException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);

		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);
			    	 
	    // create an identifier for the document
		String docId = uri;
	    
	    // create a handle on the content
	    FileHandle handle = new FileHandle(file);
	    handle.set(file);
	    
	    // write the document content
	    docMgr.write(docId, handle);
	    
	    System.out.println("Update " + docId + " to database");		
	}

        /**
         * Update document using FileHandle
         * @param client
         * @param filename
         * @param uri
         * @param type
         * @throws IOException
         */
        public void updateDocumentUsingStringHandle(DatabaseClient client, String filename, String uri, String type) throws IOException
        {
            // create doc manager
            DocumentManager docMgr = null;
            docMgr = documentManagerSelector(client, docMgr, type);

            File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);
            FileInputStream fis = new FileInputStream(file);
            Scanner scanner = new Scanner(fis).useDelimiter("\\Z");
            String readContent = scanner.next();
            fis.close();
            scanner.close();

            // create an identifier for the document
            String docId = uri;

            // create a handle on the content
            // create handle
            StringHandle contentHandle = new StringHandle();
            contentHandle.set(readContent);

            // write the document content
            docMgr.write(docId, contentHandle);

            System.out.println("Update " + docId + " to database");
        }

        /**
         * Read document using BytesHandle
         * @param client
         * @param uri
         * @param type
         * @throws IOException
         */
	public BytesHandle readDocumentUsingBytesHandle(DatabaseClient client, String uri, String type)throws IOException , NullPointerException
	{
		//create doc manager
		DocumentManager docMgr = null;
		docMgr=documentManagerSelector(client, docMgr, type);

		//create handle
		BytesHandle contentHandle = new BytesHandle();
		
		//create doc id
		String readDocId = uri;
		System.out.println("Read " + readDocId + " from database");
		docMgr.read(readDocId, contentHandle);
		return contentHandle;
		
	}
        /**
         * get Binary Size From Byte
         * @param binaryFileInByte 
         * @throws IOException
         */
	public int getBinarySizeFromByte(byte[] fileRead) throws IOException, IndexOutOfBoundsException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		byte[] b = new byte[1000];
		int len = fileRead.length;
		
		return len;
	}

        /**
         * Read document using BytesHandle
         * @param client
         * @param filename
         * @param uri
         * @param type
         * @throws IOException
         */
	public void updateDocumentUsingByteHandle(DatabaseClient client, String filename, String uri, String type) throws IOException, ParserConfigurationException, SAXException
	{   
	    // create doc manager
	    DocumentManager docMgr = null;
	    docMgr = documentManagerSelector(client, docMgr, type);	
				
	    // acquire the content
	    FileReader content = new FileReader("src/test/java/com/marklogic/javaclient/data/" + filename);
	    //String contentInString = new String(content);
	    BufferedReader br = new BufferedReader(content);
	    String readContent = "";
	    String line = null;
	    while ((line = br.readLine()) != null)
	    		readContent = readContent + line; 
	    br.close();

	    byte[] contentInByte = (byte[])readContent.getBytes();
	    // create an identifier for the document
	    String docId = uri;
	    // create a handle on the content
	    BytesHandle handle = new BytesHandle();
	    handle.set(contentInByte);
	        
	    // write the document content
	    docMgr.write(docId, handle);
	    
	    System.out.println("Update " + docId + " to database");
	}

	
	/**
	 * Delete document
	 * @param client
	 * @param uri
	 * @param type
	 */
	public void deleteDocument(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	

		String deleteDocId = uri;

		docMgr.delete(deleteDocId);
		System.out.println("Delete " + deleteDocId + " from database");
	}
	
	/**
	 * Check if document exist
	 * @param client
	 * @param uri
	 * @return
	 */
	/*public DocumentDescriptor isDocumentExist(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);
		
		String checkDocId = uri;
		
		DocumentDescriptor docExist;
		
		docExist = docMgr.exists(checkDocId);
		return docExist;
	}*/
			
	/**
	 * Read metadata from document
	 * @param client
	 * @param uri
	 * @param type
	 * @return
	 */
	public DocumentMetadataHandle readMetadataFromDocument(DatabaseClient client, String uri, String type)
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);		
		
		// create handle
		DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();
		
		// create doc id
		String readDocId = uri;
		System.out.println("Read metadata from " + readDocId);

		// read metadata
		docMgr.readMetadata(readDocId, readMetadataHandle);
		
		return readMetadataHandle;
	}
	
	/**
	 * Set query option in XML
	 * @param client
	 * @param queryOptionName
	 * @throws FileNotFoundException
	 */
	public void setQueryOption(DatabaseClient client, String queryOptionName) throws FileNotFoundException
	{
		// create a manager for writing query options
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create handle
		ReaderHandle handle = new ReaderHandle();
		
		// write the files
		BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/javaclient/queryoptions/" + queryOptionName));
		handle.set(docStream);
			
		//handle.setFormat(Format.XML);
		
		// write the query options to the database
		optionsMgr.writeOptions(queryOptionName, handle);		    

		System.out.println("Write " + queryOptionName + " to database");
	}
	/**
	 * Copy Files from One location to Other
	 * @param Source File
	 * @param target File
	 * @param Boolean Value
	 * @throws FileNotFoundException
	 */	
	public void copyWithChannels(File aSourceFile, File aTargetFile, boolean aAppend) {
	    //log("Copying files with channels.");
	    //ensureTargetDirectoryExists(aTargetFile.getParentFile());
	    FileChannel inChannel = null;
	    FileChannel outChannel = null;
	    FileInputStream inStream = null;
	    FileOutputStream outStream = null;
	    try{
	      try {
	        inStream = new FileInputStream(aSourceFile);
	        inChannel = inStream.getChannel();
	        outStream = new  FileOutputStream(aTargetFile, aAppend);        
	        outChannel = outStream.getChannel();
	        long bytesTransferred = 0;
	        //defensive loop - there's usually only a single iteration :
	        while(bytesTransferred < inChannel.size()){
	          bytesTransferred += inChannel.transferTo(0, inChannel.size(), outChannel);
	        }
	      }
	      finally {
	        //being defensive about closing all channels and streams 
	        if (inChannel != null) inChannel.close();
	        if (outChannel != null) outChannel.close();
	        if (inStream != null) inStream.close();
	        if (outStream != null) outStream.close();
	      }
	    }
	    catch (FileNotFoundException ex){
	      System.out.println("File not found: " + ex);
	    }
	    catch (IOException ex){
	      System.out.println(ex);
	    }
	  }
	/**
	 * Set query option in JSON
	 * @param client
	 * @param queryOptionName
	 * @throws FileNotFoundException
	 */
	public void setJSONQueryOption(DatabaseClient client, String queryOptionName) throws FileNotFoundException
	{
		// create a manager for writing query options
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create handle to write option in xml
		ReaderHandle handle = new ReaderHandle();
		handle.setFormat(Format.JSON);
		// write the files
		BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/javaclient/queryoptions/" + queryOptionName));
		handle.set(docStream);
		
		// write the query options to the database in xml
		optionsMgr.writeOptions(queryOptionName, handle);		    

		System.out.println("Write " + queryOptionName + " to database");	
		
		// read query option in json
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions(queryOptionName, readHandle);
		
		String jsonQueryOption = readHandle.get();
		
		// create handle to write back option in json
		String queryOptionNameJson = queryOptionName.replaceAll(".xml", ".json");
		StringHandle writeHandle = new StringHandle();
		writeHandle.set(jsonQueryOption);
		writeHandle.setFormat(Format.JSON);
		optionsMgr.writeOptions(queryOptionNameJson, writeHandle);
		System.out.println("Write " + queryOptionNameJson + " to database");
	}
	
	public SearchHandle runSearch(DatabaseClient client, String queryOptionName, String criteria)
	{
		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria(criteria);
		
		// create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// run the search
		return queryMgr.search(querydef, resultsHandle);
	
	}
	
	public String returnSearchResult(SearchHandle resultsHandle)
	{
		String matchedDoc = ""; 
		
		// iterate over the result documents
		MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
		for (MatchDocumentSummary docSummary: docSummaries) {
			String uri = docSummary.getUri();

			// iterate over the match locations within a result document
			MatchLocation[] locations = docSummary.getMatchLocations();
		        matchedDoc = matchedDoc + "|" + "Matched "+locations.length+" locations in "+uri;
		}
		return matchedDoc;
	}

	/**
	 * Get the size of binary file
	 * @param fileRead
	 * @return
	 * @throws IOException
	 */
	public int getBinarySize(InputStream fileRead) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		byte[] b = new byte[1000];
		int len = 0;
		while (((len=fileRead.read(b)) != -1)) {
			baos.write(b, 0, len);
		}
		
		byte[] buf = baos.toByteArray();
		return buf.length;
	}
	
	/**
	 * Get document properties in string
	 * @param properties
	 * @return
	 */
	public String getDocumentPropertiesString(DocumentProperties properties)
	{
	    Set setProperties = properties.entrySet(); 
	    Iterator iProperties = setProperties.iterator(); 
	    String stringProperties = "size:" + properties.size() + "|";
	    while(iProperties.hasNext()) 
	    {
	    	Map.Entry meProperties = (Map.Entry)iProperties.next();
	    	stringProperties = stringProperties + meProperties.getKey() + ":" + meProperties.getValue() + "|";
	    }
	    
	    return stringProperties;
	}
	
	/**
	 * Get document permissions in string
	 * @param permissions
	 * @return
	 */
	public String getDocumentPermissionsString(DocumentPermissions permissions)
	{
	    Set setPermissions = permissions.entrySet(); 
	    Iterator iPermissions = setPermissions.iterator(); 
	    String stringPermissions = "size:" + permissions.size() + "|";
	    while(iPermissions.hasNext()) 
	    {
	    	Map.Entry mePermissions = (Map.Entry)iPermissions.next();
	    	stringPermissions = stringPermissions + mePermissions.getKey() + ":" + mePermissions.getValue() + "|";
	    }
	    
	    return stringPermissions;
	}
	
	/**
	 * Get document collections in string
	 * @param collections
	 * @return
	 */
	public String getDocumentCollectionsString(DocumentCollections collections)
	{
	    Iterator iCollections = collections.iterator();
	    String stringCollections = "size:" + collections.size() + "|";
	    while (iCollections.hasNext()) {
	        // Get element
	        Object element = iCollections.next();
	        stringCollections = stringCollections + element + "|";
	    }
	    
	    return stringCollections;
	}	
	
	/**
	 * Function to select and create document manager based on the type
	 * @param client
	 * @param docMgr
	 * @param type
	 * @return
	 */
	public DocumentManager documentManagerSelector(DatabaseClient client, DocumentManager docMgr, String type)
	{
		// create doc manager
		if(type == "XML")
		{
			docMgr = client.newXMLDocumentManager();
		}
		else if(type == "Text")
		{	
			docMgr = client.newTextDocumentManager();
		}
		else if(type == "JSON")
		{	
			docMgr = client.newJSONDocumentManager();
		}
		else if(type == "Binary")
		{	
			docMgr = client.newBinaryDocumentManager();
		}
		else if (type == "JAXB") {
			docMgr = client.newXMLDocumentManager();
		}
		else 
		{ 
	
			System.out.println("Invalid type");
		}
		
		return docMgr;
	}
	
	/**
	 * Return search report based on element names
	 * @param resultDoc
	 * @param tagNames
	 */
	public String returnSearchReport(Document resultDoc, String[] tagNames)
	{
		String sConcat = "<result>";
		Element root = resultDoc.getDocumentElement();
		NodeList searchResultNodeList = root.getElementsByTagName("search:result");
		for(int i = 0; i < searchResultNodeList.getLength(); i++)
		{	
			Element attributeElement = (Element) searchResultNodeList.item(i);
			String attributeValue = attributeElement.getAttribute("uri");
					
			sConcat = sConcat + "<item uri='" + attributeValue + "'>";
			for(String tagName : tagNames)
			{
				NodeList elementNodeList = root.getElementsByTagName(tagName);
				Node elementNode = elementNodeList.item(i);
				String elementValue = elementNode.getTextContent();
				sConcat = sConcat + "<" + tagName + ">" + elementValue + "</" + tagName + ">";
			}
			sConcat = sConcat + "</item>";
		}
		NodeList searchReportList = root.getElementsByTagName("search:report");
		Node searchReportNode = searchReportList.item(0);
		String searchReportValue = searchReportNode.getTextContent();
		sConcat = sConcat + "<search:report>" + searchReportValue + "</search:report></result>";
		
		return sConcat;
	}
	
	/**
	 * Get the expected XML document
	 * @param filename
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document expectedXMLDocument(String filename) throws ParserConfigurationException, SAXException, IOException
	{
		// get xml document for expected result
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document expectedDoc = docBuilder.parse(new File("src/test/java/com/marklogic/javaclient/data/" + filename));
		return expectedDoc;
	}
	
	/**
	 * Get the expected JSON document
	 * @param filename
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public JsonNode expectedJSONDocument(String filename) throws JsonParseException, IOException
	{
		// get json document for expected result
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory jfactory = new JsonFactory();
		JsonParser jParser = jfactory.createJsonParser(new File("src/test/java/com/marklogic/javaclient/data/" + filename));
		JsonNode expectedDoc = mapper.readTree(jParser);		
		return expectedDoc;
	}
	
	/**
	 * Get the expected JSON query option
	 * @param filename
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public JsonNode expectedJSONQueryOption(String filename) throws JsonParseException, IOException
	{
		// get json document for expected result
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory jfactory = new JsonFactory();
		JsonParser jParser = jfactory.createJsonParser(new File("src/test/java/com/marklogic/javaclient/queryoptions/" + filename));
		JsonNode expectedDoc = mapper.readTree(jParser);		
		return expectedDoc;
	}
	
	/**
	 * Get the expected xml key
	 * @param filename
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document expectedXMLKey(String filename) throws ParserConfigurationException, SAXException, IOException
	{
		// get xml document for expected result
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document expectedDoc = docBuilder.parse(new File("src/test/java/com/marklogic/javaclient/keys/" + filename));
		return expectedDoc;
	}
	
	/**
	 * Get the metadata xml
	 * @param filename
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document getXMLMetadata(String filename) throws ParserConfigurationException, SAXException, IOException
	{
		// get xml document for expected result
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document metadataDoc = docBuilder.parse(new File("src/test/java/com/marklogic/javaclient/metadata/" + filename));
		return metadataDoc;
	}
	
	/**
	 * Convert string to xml document. Used on actual read content for XML comparison
	 * @param readContent
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document convertStringToXMLDocument(String readContent) throws ParserConfigurationException, SAXException, IOException
	{
		// convert actual string to xml doc
	    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
	    InputSource is = new InputSource( new StringReader( readContent ) );
	    Document readDoc = docBuilder.parse( is );
	    
	    return readDoc;
	}
	/**
	 * Convert XMLEventReader To String
	 * @param XMLEventReader
	 * @return String
	 * @throws XMLStreamException, TransformerException, IOException, ParserConfigurationException, SAXException
	 */
	
	public String convertXMLEventReaderToString(XMLEventReader fileRead) throws IOException, TransformerException, XMLStreamException
	{
	    //BufferedReader br = (BufferedReader) fileRead;
String readContent = "";
String line = null;
while (fileRead.hasNext())
		readContent = readContent +fileRead.next();

return readContent;	    

}
	/**
	 * Convert XMLStreamReader To String
	 * @param XMLStreamReader
	 * @return String
	 * @throws XMLStreamException, TransformerException, IOException, ParserConfigurationException, SAXException
	 */
	public String convertXMLStreamReaderToString(XMLStreamReader reader) throws XMLStreamException, TransformerException, IOException, ParserConfigurationException, SAXException {
        String str = null;
		while (reader.hasNext())
		{
		    reader.next();
		    int a = reader.getEventType();
		    if (reader.hasText())
		    	if ( reader.getText() != "null" )
		        str =str + reader.getText().trim();
		}
		return str;
	}
	/**
	 * Convert xml document to string. Useful for debugging purpose
	 * @param readContent
	 * @return
	 * @throws TransformerException
	 */
	public String convertXMLDocumentToString(Document readContent) throws TransformerException
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(readContent), new StreamResult(writer));
		String output = writer.getBuffer().toString();
		return output;
	}
	
//	public String convertJAXBToString(JAXB readfile) throws JAXBException{
//		String xml = null;
//		readfile.marshal(readfile, xml);
//		System.out.println(xml);
//		return xml;
//		
//	}
	/**
	 * Convert inputstream to string. Used on InputStreamHandle
	 * @param fileRead
	 * @return
	 * @throws IOException 
	 */
	public String convertInputStreamToString(InputStream fileRead) throws IOException
	{
		int ch;
		StringBuffer strContent = new StringBuffer("");
		
		while((ch = fileRead.read()) != -1)
			strContent.append((char)ch);
			
		fileRead.close();
		
		String readContent = strContent.toString();
		return readContent;
	}
	/**
	 * Convert inputsource to string. Used on InputSourceHandle
	 * @param fileRead
	 * @return
	 * @throws IOException 
	 * @throws TransformerException 
	 */
	public String convertInputSourceToString(InputSource fileRead) throws IOException, TransformerException
	{
		SAXSource saxsrc = new SAXSource(fileRead);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(saxsrc, new StreamResult(writer));
		String output = writer.getBuffer().toString();
		return output;
	}
	
	/**
	 * Convert source to string. Used on SourceHandle
	 * @param fileRead
	 * @return
	 * @throws IOException 
	 * @throws TransformerException 
	 */
	public String convertSourceToString(Source reader) throws IOException, TransformerException
	{
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(reader, result);
		String str = stringWriter.getBuffer().toString();
		return str;

	}
	/**
	 * Convert reader to string. Used on ReaderHandle
	 * @param fileRead
	 * @return
	 * @throws IOException
	 */
	public String convertReaderToString(Reader fileRead) throws IOException
	{
	    BufferedReader br = new BufferedReader(fileRead);
	    String readContent = "";
	    String line = null;
	    while ((line = br.readLine()) != null)
	    		readContent = readContent + line;
	    
	    br.close();
	    
	    return readContent;	    
	}
	
	/**
	 * Convert file to string. Used on FileHandle
	 * @param fileRead
	 * @return
	 * @throws FileNotFoundException
	 */
	public String convertFileToString(File fileRead) throws FileNotFoundException
	{
	    Scanner scanner = new Scanner(fileRead).useDelimiter("\\Z");
	    String readContent = scanner.next();
	    scanner.close();
	    
	    return readContent;
	}
	
	/**
	 * Load geo data for geo spatial tests
	 * @throws FileNotFoundException
	 */
	public void loadGeoData() throws FileNotFoundException
	{
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(int i = 1; i <= 24; i++)
		{
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}
		
		//release client
		client.release();
	}
/*
 * {
  "collections": [
    "shapes",
    "squares"
  ],
  "permissions": [
    {
      "role-name": "hadoop-user-read",
      "capabilities": [
        "read"
      ]
    }
  ],
  "properties": {
    "myprop": "this is my prop",
    "myotherprop": "this is my other prop"
  },
  "quality": 0
}
 */
  public JsonNode constructJSONPropertiesMetadata(Map prop){
	  ObjectMapper mapper = new ObjectMapper();
	  ObjectNode mainNode = mapper.createObjectNode();
	  ObjectNode cNode = mapper.createObjectNode();
	 
	   Iterator keys = prop.keySet().iterator();
	   while(keys.hasNext())
	   {
		   String keyvalue = keys.next().toString();
		   cNode.put(keyvalue,prop.get(keyvalue).toString());
	   }
	   mainNode.set("properties", cNode);
	  return (mainNode);
  }
  public JsonNode constructJSONCollectionMetadata(String...col){
	  ObjectMapper mapper= new ObjectMapper();
		 ObjectNode mNode = mapper.createObjectNode();
		 ArrayNode aNode = mapper.createArrayNode();
		 
		 for(String c : col){
			 aNode.add(c);
		 }
		 mNode.withArray("collections").addAll(aNode);
		 return mNode;
  }
}

