package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.search.SearchOptions;
import com.marklogic.client.config.search.impl.SearchOptionsImpl;

public class QueryOptionsManagerTest {
	
	
	@BeforeClass
	public static void beforeClass() {
		Common.connectAdmin();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}
	


	@Test
	public void testOptionsManager() throws JAXBException {
		QueryOptionsManager mgr = Common.client.newQueryOptionsManager();
		assertNotNull("Client could not create query options manager", mgr);

		SearchOptions options = new SearchOptionsImpl();
        mgr.writeOptions("testempty", options);
        
        SearchOptions returned = mgr.readOptions("testempty");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		returned.writeTo(baos);
		
		String optionsResult = baos.toString();

		assertTrue("Empty options result not empty",optionsResult.contains("<options xmlns=\"http://marklogic.com/appservices/search\"/>"));
		
		mgr.deleteOptions("testempty");
		
		
	};
	
	/*
	 * commenting out pending figuring out blocking problem
	@Test(expected=MarkLogicIOException.class)
	public void testNotFoundOptions() {
		QueryOptionsManager mgr = Common.client.newQueryOptionsManager();

		mgr.deleteOptions("testempty");
		
		//mgr.readOptions("testempty");
		
	}
	*/

}
