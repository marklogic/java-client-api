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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.config.QueryOptions;
import com.marklogic.client.admin.config.QueryOptions.Heatmap;
import com.marklogic.client.admin.config.QueryOptions.QueryAnnotation;
import com.marklogic.client.admin.config.QueryOptions.QueryConstraint;
import com.marklogic.client.admin.config.QueryOptions.QueryGeospatialAttributePair;
import com.marklogic.client.admin.config.QueryOptions.QueryGeospatialElement;
import com.marklogic.client.admin.config.QueryOptions.QueryGeospatialElementPair;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryStarter;
import com.marklogic.client.admin.config.QueryOptions.QueryOperator;
import com.marklogic.client.admin.config.QueryOptions.QueryRange;
import com.marklogic.client.admin.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.admin.config.QueryOptions.QuerySortOrder.Direction;
import com.marklogic.client.admin.config.QueryOptions.QuerySortOrder.Score;
import com.marklogic.client.admin.config.QueryOptions.QueryState;
import com.marklogic.client.admin.config.QueryOptions.QuerySuggestionSource;
import com.marklogic.client.admin.config.QueryOptions.QueryTerm;
import com.marklogic.client.admin.config.QueryOptions.QueryTerm.TermApply;
import com.marklogic.client.admin.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.admin.config.QueryOptions.QueryWord;
import com.marklogic.client.admin.config.QueryOptions.XQueryExtension;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;

@SuppressWarnings("deprecation")
public class QueryOptionsHandleTest {

	private static QueryOptionsManager mgr;
	private static QueryOptionsHandle testOptions;
	private static QueryOptionsHandle geoOptions;
	private static ServerConfigurationManager serverConfig;
	private static Boolean initialConfig;
    private static XpathEngine xpathEngine;

	private static QueryOptionsBuilder builder;

	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);
	
	private static String[] testOptionsCorpus = new String[] {
			"search-config-empty.xml", "search-config-simple.xml",
			"search-config-annotated.xml", "search-config-geo.xml",
			"search-config.xml", "6-1.0.xml", "8-1.0.xml", "7-1.0.xml" };

	private static int testIndex = 4; // search-config.xml
	private static int geoIndex = 3; // search-config-geo.xml
	private static List<QueryOptionsHandle> optionsPOJOs;

	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@BeforeClass
	public static void setupTestOptions()
	throws FileNotFoundException, FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
		Common.connectAdmin();
		serverConfig = Common.client.newServerConfigManager();

		serverConfig.readConfiguration();
		initialConfig = serverConfig.getQueryOptionValidation();

		serverConfig.setQueryOptionValidation(false);
		serverConfig.writeConfiguration();

		mgr = Common.client.newServerConfigManager().newQueryOptionsManager();
	
		optionsPOJOs = new ArrayList<QueryOptionsHandle>();

		for (String option : testOptionsCorpus) {
			FileHandle f = new FileHandle(new File("src/test/resources/"
					+ option));
			logger.debug(option);
			mgr.writeOptions("tmp", f);
			QueryOptionsHandle handle = mgr.readOptions("tmp",
					new QueryOptionsHandle());
			optionsPOJOs.add(handle);
			logger.debug("Loaded query config at " + option);
		}
		testOptions = optionsPOJOs.get(testIndex);

		geoOptions = optionsPOJOs.get(geoIndex);

		builder = new QueryOptionsBuilder();

        HashMap<String,String> xpathNS = new HashMap<String, String>();
        xpathNS.put("search", "http://marklogic.com/appservices/search");
        SimpleNamespaceContext xpathNsContext = new SimpleNamespaceContext(xpathNS);

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setNormalize(true);
        XMLUnit.setNormalizeWhitespace(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

        xpathEngine = XMLUnit.newXpathEngine();
        xpathEngine.setNamespaceContext(xpathNsContext);

    }
	
	@AfterClass
	public static void resetOptionsValidation()
	throws FailedRequestException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException {
		serverConfig.setQueryOptionValidation(initialConfig);
		serverConfig.writeConfiguration();
	}

    @Test
    public void emptyOptions() throws IOException, SAXException {
        String correct = "<search:options xmlns:search='http://marklogic.com/appservices/search'/>";
        QueryOptionsHandle options = new QueryOptionsHandle();
        String result = options.toString();

        Document cd = XMLUnit.buildControlDocument(correct);
        Document rd = XMLUnit.buildControlDocument(result);

        XMLAssert.assertXMLEqual(cd, rd);
    }

    @Test
    public void addForest() throws IOException, SAXException {
        String correct = "<options xmlns='http://marklogic.com/appservices/search'>"
                + "<forest>1234</forest>"
                + "</options>";
        QueryOptionsHandle options = new QueryOptionsHandle();
        options.addForest(1234);
        String result = options.toString();

        Document cd = XMLUnit.buildControlDocument(correct);
        Document rd = XMLUnit.buildControlDocument(result);

        XMLAssert.assertXMLEqual(cd, rd);
    }

   

	
	
	@Test
	public void parseAndBuildAdditionalQuery()
	throws JAXBException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		String optionsString = "<options xmlns=\"http://marklogic.com/appservices/search\"><additional-query><directory-query xmlns=\"http://marklogic.com/cts\"><uri>/oscars/</uri></directory-query></additional-query></options>";
		mgr.writeOptions("tmp", new StringHandle(optionsString));
		QueryOptionsHandle options = mgr.readOptions("tmp",
				new QueryOptionsHandle());

		logger.debug(options.toString());
		
		Element e = options.getAdditionalQuery();
		assertEquals("QueryString wrong after serializing QueryAdditionalQuery", "directory-query", e.getNodeName());

		e.setTextContent("/oscars2/");
		options.setAdditionalQuery(e);
		optionsString = options.toString();

		logger.debug(optionsString);
		assertTrue("Updated Option not updated from QueryAdditionalQuery",
				optionsString.contains("/oscars2/"));

	}

	@Test
	public void buildOperator() throws FileNotFoundException, IOException, SAXException, XpathException,
			JAXBException {
		QueryOptionsHandle options = testOptions;

		QueryOperator operatorOption = testOptions.getOperators().get(0);
		logger.debug("OperatorOption found from test config {}", operatorOption);

		QueryState s = (QueryState) operatorOption.getStates().get(0);

		assertEquals("State from operator", "relevance", s.getName());

		// cut out building -- test may not work
		QueryOperator operator = builder.operator("sortcolor",
				builder.state("pantone")
				.withSortOrders(builder.sortOrder(
						builder.elementAttributeRangeIndex(
								new QName("http://my/namespace", "green"), 
								new QName("http://my/namespace", "pantone"), 
								builder.stringRangeType(QueryOptions.DEFAULT_COLLATION)),
						Direction.ASCENDING)));

		options.getOperators().add(operator);
		String optionsString = options.toString();

        Document doc = XMLUnit.buildControlDocument(optionsString);
        NodeList nl = xpathEngine.getMatchingNodes("//search:sort-order/search:score[. = '' and not(*)]", doc);

    	
    	
    	logger.debug("Sort order found from test config {}", optionsString);
		assertTrue("Sort order should contain 3 empty score elements", nl.getLength() == 2);

        String name = xpathEngine.evaluate("//search:operator[@name='sortcolor']/search:state[@name='pantone']/search:sort-order/search:element/@name", doc);

		assertTrue("Sort order should contain element index def", "green".equals(name));
	}


	@Test
	public void parseComprehensiveOptions() {
		QueryOptionsHandle options = testOptions;
		List<QueryConstraint> constraintOptions = options.getConstraints();
		for (QueryConstraint c : constraintOptions) {
			logger.debug("Testing constraint named {} with class {}",
					c.getName(), c.getClass().getName());
		}
		QueryRange r = options.getConstraint("award")
				.getSource();
		assertEquals("index from range constraint", "award", r.getAttribute()
				.getLocalPart());

		QueryWord v = options.getConstraint("inname")
				.getSource();
		assertNotNull(v);
		assertEquals("index from valueOption constraint", "name", v
				.getElement().getLocalPart());

		QueryTerm termConfig = options.getTerm();
		List<String> termOptions = termConfig.getTermOptions();
		assertEquals("First term option is 'punctuation-insensitive'",
				termOptions.get(0), "punctuation-insensitive");
		assertEquals("Second term option is 'unwildcarded'",
				termOptions.get(1), "unwildcarded");

		XQueryExtension applyFunction = termConfig.getTermFunction();
		assertNull("Apply function doesn't exist on this term", applyFunction);
		assertEquals("TermConfig empty apply is all-results",
				TermApply.ALL_RESULTS, termConfig.getEmptyApply());

		QueryGrammar grammar = options.getGrammar();

		assertEquals("GrammarOption quotation", "\"", grammar.getQuotation());
		assertEquals("GrammarOption implicit", "and-query", grammar
				.getImplicit().getLocalName());
		List<QueryJoiner> joiners = grammar.getJoiners();
		assertEquals("joiner attribute incorrect", joiners.get(0).getApply(),
				JoinerApply.INFIX);
		List<QueryStarter> starters = grammar.getStarters();
		assertNull("starter attribute should be null", starters.get(1)
				.getDelimiter());
		assertEquals("starter attribute incorrect", starters.get(1)
				.getStrength(), 40);

		List<QueryOperator> operatorOptions = options.getOperators();
		QueryOperator o = operatorOptions.get(0);
		assertEquals("OperatorOption list invalid return", o.getName(), "sort");

		List<QueryState> states = o.getStates();
		QueryState state1 = states.get(0);
		QuerySortOrder so1 = state1.getSortOrders().get(0);
		
		assertEquals(so1.getScore(), Score.YES);
		
		QueryTransformResults transformResultsOption = options
				.getTransformResults();
		assertEquals("Apply attribute for transform-results",
				transformResultsOption.getApply(), "snippet");
		QueryOptions.MarkLogicQName element = transformResultsOption.getPreferredElements().get(0);
		assertEquals("preferred element from transform-results", "p", element.getName());
		// assertEquals("Max Matches from transform-results", 1,
		// transformResults.getMaxMatches());

	}

	@Test
	public void buildGeoOptions() {
		
		QueryOptionsHandle options = geoOptions;
		List<QueryConstraint> constraintOptions = options.getConstraints();

		QueryGeospatialElement gec = (QueryGeospatialElement) constraintOptions.get(0)
				.getSource();
		QueryGeospatialAttributePair gapc = (QueryGeospatialAttributePair) constraintOptions.get(1)
				.getSource();
		QueryGeospatialElementPair gepc = (QueryGeospatialElementPair) constraintOptions.get(2)
				.getSource();

		assertEquals("sf1", gec.getParent().getName());
		assertEquals("intptlat", gapc.getLatitude().getName());
		assertEquals("intptlon", gepc.getLongitude().getName());
		Heatmap h = gapc.getHeatmap();
		assertEquals("Heatmap attribute check", "-118.2",
				Double.toString(h.getE()));
		
		
		
	}

	
	

	

	
	
	@Test
	public void testEmptyListAccessors() throws JAXBException {
		QueryOptionsHandle options = new QueryOptionsHandle();

		List<QueryConstraint> l = options.getConstraints();
		assertEquals("no constraints for empty options result", 0, l.size());

		List<QueryOperator> m = options.getOperators();
		assertEquals("no operators for empty options result", 0, m.size());

		List<Long> n = options.getForests();
		assertEquals("no forests for empty options result", 0, n.size());

		List<String> searchOptions = options.getSearchOptions();
		assertEquals("no searchOptions for empty options result", 0,
				searchOptions.size());

		List<QuerySortOrder> sortOrders = options.getSortOrders();
		assertEquals("no sortOrders for empty options result", 0,
				sortOrders.size());

		List<QuerySuggestionSource> suggestionSourceOptions = options
				.getSuggestionSources();
		assertEquals("no suggestionSources for empty options result", 0,
				suggestionSourceOptions.size());

	}

	@Test
	public void testMarshall() throws SAXException, IOException,
			ParserConfigurationException {
		for (QueryOptionsHandle option : optionsPOJOs) {

			Element rootElement = Utilities.domElement(option.toString());
			assertEquals(
					"QName of root element incorrect",
					new QName("http://marklogic.com/appservices/search",
							"options"),
					new QName(rootElement.getNamespaceURI(), rootElement
							.getLocalName()));

			NodeList nl = rootElement.getElementsByTagNameNS(
					QueryOptions.SEARCH_NS, "constraint");
			if (nl.getLength() > 0) {
				Element constraintElement = (Element) nl.item(0);
				String name = constraintElement.getAttribute("name");
				logger.debug("ConstraintOption name {} found.", name);
			}
			nl = rootElement.getElementsByTagNameNS(QueryOptions.SEARCH_NS,
					"return-facets");
			if (nl.getLength() > 0) {
				Element constraintElement = (Element) nl.item(0);
				String text = constraintElement.getTextContent();
				logger.debug("Text {} found for return-facets.", text);
			}
		}
	}

    @Test
    public void bug17240()
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8012, "rest-admin", "x", DatabaseClientFactory.Authentication.DIGEST);
        // create a manager for writing query options

        QueryOptionsManager optionsMgr =
        	client.newServerConfigManager().newQueryOptionsManager();

        // create the query options
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        builder.append("<options xmlns=\"http://marklogic.com/appservices/search\">\n");
        builder.append(" <constraint name=\"industry\">\n");
        builder.append(" <value>\n");
        builder.append(" <element ns=\"\" name=\"industry\"/>\n");
        builder.append(" </value>\n");
        builder.append(" </constraint>\n");
        builder.append(" <constraint name=\"description\">\n");
        builder.append(" <word>\n");
        builder.append(" <element ns=\"\" name=\"heatmap\"/>\n");
        builder.append(" </word>\n");
        builder.append(" </constraint>\n");
        builder.append(" <constraint name=\"duration\">\n");
        builder.append(" <range type=\"xs:string\" facet=\"true\">\n");
        builder.append(" <element ns=\"\" name=\"duration\"/>\n");
        builder.append(" </range>\n");
        builder.append(" </constraint>\n");
        builder.append("</options>\n");

        // initialize a handle with the query options
        StringHandle writeHandle = new StringHandle(builder.toString());

        // Delete it
        optionsMgr.deleteOptions("searchOptions1");

        // create the query
        optionsMgr.writeOptions("searchOptions1", writeHandle);

        // write the query
        optionsMgr.writeOptions("searchOptions1", writeHandle);
    }

	@Test
	public void testQueryAnnotations() {
		QueryOptionsHandle annotatedOptions = optionsPOJOs.get(2);
		logger.debug("annotatedOptions: " + annotatedOptions.toString());

		// assertFalse("Options shoudn't contain tacit default elements",
		// annotatedOptions.toString().contains("return-query"));

		QueryConstraint constraintOption = annotatedOptions.getConstraints()
				.get(0);
		List<QueryAnnotation> annotations = constraintOption.getAnnotations();
		QueryAnnotation firstQueryAnnotation = annotations.get(0);

		org.w3c.dom.Element annotationElement = firstQueryAnnotation.get(0);
		logger.debug("Annotation element received from test {}",
				annotationElement);
		assertEquals("Annotation element's namespace", "http://namespace/x",
				annotationElement.getNamespaceURI());
		assertEquals("Annotation localname", "note",
				annotationElement.getLocalName());
		assertEquals("Annotation text",
				"This is an annotation in the x namespace",
				annotationElement.getTextContent());

		annotatedOptions.annotate("<a:note xmlns:a=\"http://marklogic.com/note\">note</a:note>");
		
	}
}
