package com.marklogic.client.config.search;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryOptionsTest {

	protected JAXBContext jc;
	protected Unmarshaller unmarshaller;
	protected Marshaller m;
	
	Logger logger = (Logger) LoggerFactory.getLogger(QueryOptionsTest.class);
		
	@Before
	public void createJAXBContext() throws JAXBException {
		jc = JAXBContext.newInstance("com.marklogic.client.config.search.jaxb");
		unmarshaller = jc.createUnmarshaller();
		m = jc.createMarshaller();
	}

	@Test
	public void testEmptyQueryOptions() throws JAXBException {
		SearchOptions options = new SearchOptions();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		options.writeTo(baos);
		
		String optionsResult = baos.toString();

		assertTrue("empty options result",optionsResult.contains("<options xmlns=\"http://marklogic.com/appservices/search\"/>"));
	}

	
	@Test
	public void marshallAndExamine() throws FileNotFoundException, JAXBException {
		SearchOptions options = new SearchOptions(new FileInputStream(
				new File("src/test/resources/search-config.xml")));
		
		List<SearchOption> optionList = options.getAll();
		for (SearchOption o : optionList) {
		  logger.debug(o.toString());
		}
		assertTrue(!options.getReturnFacets());
		
	};
	
	@Test
	public void setReturnFacets() {
		SearchOptions options = new SearchOptions();
		options.setReturnFacets(true);
		logger.debug("here is return facets: " + options.getReturnFacets());
		assertTrue(options.getReturnFacets());
		options.setReturnFacets(false);
		assertTrue(!options.getReturnFacets());
		
	}
}