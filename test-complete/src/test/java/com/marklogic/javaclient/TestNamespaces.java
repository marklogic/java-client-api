package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import javax.xml.namespace.NamespaceContext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.NamespacesManager;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.util.RequestLogger;
import org.junit.*;
public class TestNamespaces extends BasicJavaClientREST {

	private static String dbName = "TestNamespacesDB";
	private static String [] fNames = {"TestNamespacesDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

@SuppressWarnings("deprecation")
@Test
	public void testNamespaces()
	{	
		System.out.println("Running testNamespaces");
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create namespaces manager
		NamespacesManager nsMgr = client.newServerConfigManager().newNamespacesManager();

		// create logger
		RequestLogger logger = client.newLogger(System.out);
		logger.setContentMax(RequestLogger.ALL_CONTENT);
	
		// start logging
		nsMgr.startLogging(logger);
		
		// add prefix
		nsMgr.addPrefix("foo", "http://example.com");
		
		NamespaceContext nsContext = nsMgr.readAll();

		assertEquals("Prefix is not equal", "foo", nsContext.getPrefix("http://example.com"));
		assertEquals("Namespace URI is not equal", "http://example.com", nsContext.getNamespaceURI("foo"));
		
		// update prefix
		nsMgr.updatePrefix("foo", "http://exampleupdated.com");
		nsContext = nsMgr.readAll();
		assertEquals("Updated Namespace URI is not equal", "http://exampleupdated.com", nsContext.getNamespaceURI("foo"));
				
		// stop logging
		nsMgr.stopLogging();
		
		String expectedLogContentMax = "9223372036854775807"; 
		assertEquals("Content log is not equal", expectedLogContentMax, Long.toString(logger.getContentMax()));
		
		// delete prefix
		nsMgr.deletePrefix("foo");
		assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("foo") == null);
		
		nsMgr.deleteAll();
		assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("foo") == null);
		
		// release client
		client.release();
	}
	

@SuppressWarnings("deprecation")
@Test	public void testDefaultNamespaces()
	{
		System.out.println("Running testDefaultNamespaces");
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create namespaces manager
		NamespacesManager nsMgr = client.newServerConfigManager().newNamespacesManager();
		
		// add namespaces
		nsMgr.addPrefix("ns1", "http://foo.com");
		nsMgr.addPrefix("ns2", "http://bar.com");
		nsMgr.addPrefix("ns3", "http://baz.com");
		
		NamespaceContext context = nsMgr.readAll();
		
		// set default namespace
		nsMgr.updatePrefix("defaultns", "http://baz.com");
		String defaultNsUri = nsMgr.readPrefix("defaultns");
		assertEquals("Default NS is wrong", "http://baz.com", defaultNsUri);
				
		// delete namespace
		nsMgr.deletePrefix("baz");
		context = nsMgr.readAll();
		
		// get default namespace
		assertEquals("Default NS is wrong", "http://baz.com", nsMgr.readPrefix("defaultns"));
		
		nsMgr.deleteAll();
		context = nsMgr.readAll();
		assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("ns1") == null);
		assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("ns2") == null);
		assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("ns3") == null);
		assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("defaultns") == null);
		
		// release client
		client.release();
	}


@SuppressWarnings("deprecation")
@Test	public void testBug22396() throws IOException {

		System.out.println("Runing testBug22396");

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		writeDocumentUsingInputStreamHandle(client, "constraint1.xml", "/testBug22396/", "XML");

		String docId = "/testBug22396/constraint1.xml";
		
		//create document manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		
		//get namespaces 
		Collection<String> nameSpaceCollection = patchBldr.getNamespaces().getAllPrefixes();
		assertEquals("getNamespace failed ",false, nameSpaceCollection.isEmpty());
		for(String prefix : nameSpaceCollection){
			System.out.println("Prefixes : "+prefix);
			System.out.println(patchBldr.getNamespaces().getNamespaceURI(prefix));
		}
		//set namespace		
		EditableNamespaceContext namespaces = new EditableNamespaceContext();
		namespaces.put("new", "http://www.marklogic.com");
		patchBldr.setNamespaces(namespaces);
		System.out.println("\n Namespace Output : "+patchBldr.getNamespaces().getNamespaceURI("xmlns")+"\n Next xml : "+patchBldr.getNamespaces().getNamespaceURI("xml")+"\n Next xs : "+patchBldr.getNamespaces().getNamespaceURI("xs")+"\n Next xsi : "+patchBldr.getNamespaces().getNamespaceURI("xsi")+"\n Next rapi : "+patchBldr.getNamespaces().getNamespaceURI("rapi")+"\n Next new : "+patchBldr.getNamespaces().getNamespaceURI("new"));
		String content = docMgr.read(docId, new StringHandle()).get();
		assertTrue("setNamespace didn't worked", patchBldr.getNamespaces().getNamespaceURI("new").contains("www.marklogic.com"));
		System.out.println(content);
		
		// release client
		client.release();
	}
@AfterClass
public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
