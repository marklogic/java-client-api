package com.marklogic.client.config.search;

import static org.junit.Assert.assertEquals;
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
import javax.xml.namespace.QName;

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
	

	@Test
	public void setFragmentScope() {
		SearchOptions options = new SearchOptions();
		options.setFragmentScope("Properties");
		logger.debug("here is fragment-scope: " + options.getFragmentScope());
		assertEquals(options.getFragmentScope(), "Properties");
		options.setFragmentScope("Documents");	
		assertEquals(options.getFragmentScope(), "Documents");
	}
	
	
	@Test
	public void buildRangeConstraintTest() {
		SearchOptions options = new SearchOptions();
		RangeConstraint range = new RangeConstraint("decade");
		options.add(range);
		
		assertEquals(range.getName(), "decade");
		
		range.addElementAttributeIndex(new QName("http://marklogic.com/wikipedia", "nominee"), new QName("year"));
		
		IndexReference indexReference = range.getIndex();
		
		assertEquals(indexReference.getElement(), new QName("http://marklogic.com/wikipedia", "nominee"));
		assertEquals(indexReference.getAttribute(), new QName("year"));
		
		range.addBucket("2000s", "2000s");
		range.addBucket("1990s", "1990s", "1990", "2000");
		range.addBucket("1980s", "1980s", "1980", "1990");
		range.addBucket("1970s", "1970s", "1970", "1980");
		range.addBucket("1960s", "1960s", "1960", "1970");
		range.addBucket("1950s", "1950s", "1950", "1960");
		range.addBucket("1940s", "1940s", "1940", "1950");
		range.addBucket("1930s", "1930s", "1930", "1940");
		range.addBucket("1920s", "1920s", "1920", "1930");
		
		range.addFacetOption("limit=10");
		
		String optionsString = options.toString();
		
		logger.debug(optionsString);
		assertTrue("Serialized Range Constraint should contain this string", optionsString.contains("<bucket name=\"2000s\">2000s</bucket>"));
		
	}
	
	@Test
	public void buildIndexesTest() {
		
	}
	
	
}