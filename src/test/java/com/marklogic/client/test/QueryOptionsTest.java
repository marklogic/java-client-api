package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.search.AdditionalQuery;
import com.marklogic.client.config.search.Annotation;
import com.marklogic.client.config.search.CustomConstraint;
import com.marklogic.client.config.search.IndexReference;
import com.marklogic.client.config.search.Operator;
import com.marklogic.client.config.search.RangeConstraint;
import com.marklogic.client.config.search.SearchOption;
import com.marklogic.client.config.search.SearchOptions;
import com.marklogic.client.config.search.SortOrder;
import com.marklogic.client.config.search.State;
import com.marklogic.client.config.search.WordConstraint;
import com.marklogic.client.config.search.impl.CollectionConstraintImpl;
import com.marklogic.client.config.search.impl.DefaultSuggestionSourceImpl;
import com.marklogic.client.config.search.impl.ElementQueryConstraintImpl;
import com.marklogic.client.config.search.impl.PropertiesConstraintImpl;
import com.marklogic.client.config.search.impl.RangeConstraintImpl;
import com.marklogic.client.config.search.impl.SearchOptionsImpl;
import com.marklogic.client.config.search.impl.SortOrderImpl;
import com.marklogic.client.config.search.impl.SuggestionSourceImpl;
import com.marklogic.client.config.search.impl.ValueConstraintImpl;
import com.marklogic.client.config.search.impl.WordConstraintImpl;


public class QueryOptionsTest {

	private JAXBContext jc;
	private Marshaller m;
	
	Logger logger = (Logger) LoggerFactory.getLogger(QueryOptionsTest.class);
	
	
	@BeforeClass
	public static void beforeClass() {
		Common.connectAdmin();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}
	
	
	@Before
	public void createJAXBContext() throws JAXBException {
		jc = JAXBContext.newInstance("com.marklogic.client.config.search.jaxb");
		jc.createUnmarshaller();
		m = jc.createMarshaller();
	}

	@Test
	public void testEmptyQueryOptions() throws JAXBException {
		SearchOptions options = new SearchOptionsImpl();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		options.writeTo(baos);
		
		String optionsResult = baos.toString();

		assertTrue("empty options result",optionsResult.contains("<options xmlns=\"http://marklogic.com/appservices/search\"/>"));
	}

	
	@Test
	public void marshallAndExamine() throws FileNotFoundException, JAXBException {
		SearchOptions options = new SearchOptionsImpl(new FileInputStream(
				new File("src/test/resources/search-config.xml")));
		
		List<SearchOption> optionList = options.getAll();
		
		for (SearchOption o : optionList) {
		  logger.debug(o.toString());
		}
		assertTrue(!options.getReturnFacets());
		
		
	};
	
	@Test
	public void setReturnFacets() {
		SearchOptions options = new SearchOptionsImpl();
		options.setReturnFacets(true);
		logger.debug("here is return facets: " + options.getReturnFacets());
		assertTrue(options.getReturnFacets());
		options.setReturnFacets(false);
		assertTrue(!options.getReturnFacets());		
	}
	

	@Test
	public void setFragmentScope() {
		SearchOptions options = new SearchOptionsImpl();
		options.setFragmentScope("Properties");
		logger.debug("here is fragment-scope: " + options.getFragmentScope());
		assertEquals(options.getFragmentScope(), "Properties");
		options.setFragmentScope("Documents");	
		assertEquals(options.getFragmentScope(), "Documents");
	}
	
	
	@Test
	public void parseRangeConstraintTest() {
		
	}
	@Test
	public void buildRangeConstraintTest() {
		SearchOptions options = new SearchOptionsImpl();
		RangeConstraint range = new RangeConstraintImpl("decade");
		options.add(range);
		
		assertEquals(range.getName(), "decade");
		
		range.addElementAttributeIndex(new QName("http://marklogic.com/wikipedia", "nominee"), new QName("year"));
		
		IndexReference indexReferenceImpl = range.getIndex();		
		assertEquals(indexReferenceImpl.getElement(), new QName("http://marklogic.com/wikipedia", "nominee"));
		assertEquals(indexReferenceImpl.getAttribute(), new QName("year"));
		
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
		assertTrue("Serialized Range AbstractSearchOption should contain this string", optionsString.contains("<bucket name=\"2000s\">2000s</bucket>"));
		
	}
	
	@Test
	public void buildValueConstraintTest() {
		SearchOptions options = new SearchOptionsImpl();
		ValueConstraintImpl vc = new ValueConstraintImpl("sumlev");
		options.add(vc);
		
		vc.addElementIndex(new QName("sumlev"));
		assertEquals(vc.getIndex().getElement(), new QName("sumlev"));
		
		String optionsString = options.toString();
		logger.debug(optionsString);
		
		assertTrue("Serialized Value AbstractSearchOption should contain this string", optionsString.contains("<element name=\"sumlev\" ns=\"\""));
		
	}

	@Test
	public void buildWordConstraintTest() {
		SearchOptions options = new SearchOptionsImpl();
		WordConstraint wc = new WordConstraintImpl("intitle");
		options.add(wc);
		
		wc.addFieldIndex("titlefield");
		assertEquals(wc.getIndex().getField(), "titlefield");
		
		
		String optionsString = options.toString();
		logger.debug(optionsString);
		
		assertTrue("Serialized Value AbstractSearchOption should contain this string", optionsString.contains("<field name=\"titlefield\""));
		
	}
	

	@Test
	public void buildSuggestionSources() {
		SearchOptions options = new SearchOptionsImpl();
		DefaultSuggestionSourceImpl dss = new DefaultSuggestionSourceImpl();
		SuggestionSourceImpl ss = new SuggestionSourceImpl();
		
		ss.addElementAttributeIndex(new QName("http://marklogic.com/wikipedia", "nominee"), new QName("year"));
		dss.addFieldIndex("yearfield");
		ss.useWordLexicon();
		options.add(ss);
		options.add(dss);
		
		String optionsString = options.toString();
		logger.debug(optionsString);
		
		assertTrue("Serialized Suggestion Source should contain ", optionsString.contains("<field name=\"yearfield\""));
		
	}
	
	@Test
	public void buildCollectionConstraint() {
		SearchOptions options = new SearchOptionsImpl();
		CollectionConstraintImpl c = new CollectionConstraintImpl("coll");
		c.setDoFacets(true);
		c.addFacetOption("limit=10");
		options.add(c);
		
		String optionsString = options.toString();
		assertTrue("Serialized CollectionConstraintImpl should contain facet option", optionsString.contains("<facet-option>limit=10"));
		
	}
	
	@Test
	public void serializeAndStoreElementQueryConstraint() throws JAXBException {
		String optionsString = "<options xmlns=\"http://marklogic.com/appservices/search\"><constraint name=\"sample\"><element-query name=\"title\" ns=\"http://my/namespace\" /></constraint></options>";
		SearchOptions options = new SearchOptionsImpl(new ByteArrayInputStream(optionsString.getBytes()));
		List<SearchOption> l = options.getAll();
		ElementQueryConstraintImpl eqc = (ElementQueryConstraintImpl) l.get(0);
		
		assertEquals("Name attribute doesn't match expected.", eqc.getName(), "title" );
		assertEquals("Namespace attribute doesn't match expected.", eqc.getNs(), "http://my/namespace" );
	}
	
	@Test
	public void serializeAndStoreCustomConstraint() throws JAXBException
	{
		String optionsString = "<options xmlns=\"http://marklogic.com/appservices/search\"><constraint name=\"custom\"><custom facet=\"true\"><parse apply=\"parse\" ns=\"http://my/namespace\" at=\"/my/parse.xqy\" /><start-facet apply=\"start\" ns=\"http://my/namespace\" at=\"/my/start.xqy\" /><finish-facet apply=\"finish\" ns=\"http://my/namespace\" at=\"/my/finish.xqy\" /></custom></constraint></options>";
		SearchOptions options = new SearchOptionsImpl(new ByteArrayInputStream(optionsString.getBytes()));
		List<SearchOption> l = options.getAll();
		CustomConstraint cc = (CustomConstraint) l.get(0);
		
		logger.debug(options.toString());
		
		assertEquals("Facets are true for test constraint", cc.getDoFacets(), true);
		assertEquals("getAt for test constraint", cc.getFinishFacet().getAt(), "/my/finish.xqy");
		assertEquals("getNs for test constraint", cc.getStartFacet().getNs(), "http://my/namespace");
		assertEquals("getApply for test constraint", cc.getParse().getApply(), "parse");
		assertEquals("Name for custom constratin", cc.getName(), "custom");
		
		
	}
	
	@Test
	public void parseAndBuildPropertiesConstraint() {
		SearchOptions options = new SearchOptionsImpl();
		
		PropertiesConstraintImpl pc = new PropertiesConstraintImpl("props");
		options.add(pc);
		
		logger.debug(options.toString());
		
		QueryOptionsManager mgr = Common.client.newQueryOptionsManager();
		mgr.writeOptions("props", options);
		
		SearchOptions options2 = mgr.readOptions("props");
		assertEquals("Unexpected class from JAXB unmarshalling", options2.getAll().get(0).getClass().getName(), PropertiesConstraintImpl.class.getName());
		
	}
	
	@Test
	public void parseAndBuildAdditionalQuery() throws JAXBException {
		String optionsString = "<options xmlns=\"http://marklogic.com/appservices/search\"><additional-query><directory-query xmlns=\"http://marklogic.com/cts\"><uri>/oscars/</uri></directory-query></additional-query></options>";
		ByteArrayInputStream bais = new ByteArrayInputStream(optionsString.getBytes());
		SearchOptions options = new SearchOptionsImpl(bais);
		
		AdditionalQuery aq = (AdditionalQuery) options.getAll().get(0);
		
		Element e = aq.getQuery();
		assertEquals("QueryString wrong after serializing AdditionalQuery", e.getFirstChild().getNodeName(), "uri" );
		
		Element uri = (Element) e.getFirstChild();
		uri.setTextContent("/oscars2/");
		aq.setQuery(e);

		logger.debug(options.toString());
		assertTrue("Updated Option not updated from AdditionalQuery", options.toString().contains("/oscars2/"));
		
	}
	
	@Test
	public void parseAndBuildAnnotation() throws JAXBException, FileNotFoundException {
		SearchOptions options = new SearchOptionsImpl(new FileInputStream(
				new File("src/test/resources/search-config.xml")));
		
		// find the annotation.
		WordConstraint word = (WordConstraint) options.getByClassName(com.marklogic.client.config.search.impl.WordConstraintImpl.class).get(0);
		
		logger.debug("Word: {}", word);
		/* FIXME annotations
		List<Element> annotations = word.getAnnotations();
		
		logger.debug("Annotations from search-config.xml {}", annotations);
		
		Element annotation1 = annotations.get(0);
		logger.debug("Annotation from word constarint: {}",annotation1.getTextContent());
		
		assertEquals("Annotation has wrong text content", annotation1.getTextContent(), "bingo");

		Element annotation2 = annotations.get(2);
		logger.debug("Annotation from word constarint: {}",annotation2.getTextContent());

		assertEquals("Annotation has wrong text content", annotation1.getTextContent(), "bango");
		*/
		
	}
	
	@Test
	public void parseAndBuildOperator() throws FileNotFoundException, JAXBException {
		SearchOptions options = new SearchOptionsImpl(new FileInputStream(
				new File("src/test/resources/search-config.xml")));

		Operator operator = (Operator) options.getByClassName(com.marklogic.client.config.search.impl.OperatorImpl.class).get(0);
		
		logger.debug("Operator found from test config {}", operator);
		
		/* FIXME annotation 
		List<Element> l = operator.getAnnotations();
		logger.debug("Operator annotations {}" , l);
		
		State s = (State) operator.getStates().get(0);
		*/
		
		SortOrder so = new SortOrderImpl();
		so.addElementAttributeIndex(new QName("http://my/namespace", "green"), new QName("http://my/namespace", "pantone"));
		so.setScore();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		m.marshal(so.asJaxbObject(), baos);
		String sortString = baos.toString();
		
		logger.debug("Sort order found from test config {}", sortString);
		assertTrue("Sort order should contain empty score element", sortString.contains("<score/>"));
		assertTrue("Sort order should contain element index def", sortString.contains("<element name=\"green\""));
	
		// TODO unset score
		//
	}
}