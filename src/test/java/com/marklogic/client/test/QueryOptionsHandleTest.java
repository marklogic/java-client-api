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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.configpojos.AnchorValue;
import com.marklogic.client.configpojos.XQueryFunctionLocator;
import com.marklogic.client.configpojos.Bucket;
import com.marklogic.client.configpojos.Collection;
import com.marklogic.client.configpojos.ComputedBucket;
import com.marklogic.client.configpojos.Constraint;
import com.marklogic.client.configpojos.Custom;
import com.marklogic.client.configpojos.DefaultSuggestionSource;
import com.marklogic.client.configpojos.ElementQuery;
import com.marklogic.client.configpojos.GeoAttrPair;
import com.marklogic.client.configpojos.GeoElement;
import com.marklogic.client.configpojos.GeoElementPair;
import com.marklogic.client.configpojos.Grammar;
import com.marklogic.client.configpojos.Heatmap;
import com.marklogic.client.configpojos.Joiner;
import com.marklogic.client.configpojos.Operator;
import com.marklogic.client.configpojos.Options;
import com.marklogic.client.configpojos.Properties;
import com.marklogic.client.configpojos.QNamePOJO;
import com.marklogic.client.configpojos.QueryAnnotation;
import com.marklogic.client.configpojos.Range;
import com.marklogic.client.configpojos.SortOrder;
import com.marklogic.client.configpojos.Starter;
import com.marklogic.client.configpojos.State;
import com.marklogic.client.configpojos.SuggestionSource;
import com.marklogic.client.configpojos.Term;
import com.marklogic.client.configpojos.TermApply;
import com.marklogic.client.configpojos.TransformResults;
import com.marklogic.client.configpojos.Value;
import com.marklogic.client.configpojos.Word;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.QueryOptionsHandle;

public class QueryOptionsHandleTest {

	private static QueryOptionsManager mgr;
	private QueryOptionsHandle testOptions;
	private QueryOptionsHandle geoOptions;

	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);

	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	private static String[] testOptionsCorpus = new String[] {
			"search-config-empty.xml", "search-config-simple.xml",
			"search-config-annotated.xml", "search-config-geo.xml",
			"search-config.xml", "6-1.0.xml", "8-1.0.xml", "7-1.0.xml"  };
	private static List<QueryOptionsHandle> optionsPOJOs;

	@BeforeClass
	public static void setupTestOptions() throws FileNotFoundException {
		Common.connectAdmin();
		mgr = Common.client.newQueryOptionsManager();
		optionsPOJOs = new ArrayList<QueryOptionsHandle>();

		for (String option : testOptionsCorpus) {

			QueryOptionsHandle handle = mgr.newOptions();
			handle.receiveContent(new FileInputStream(new File(
					"src/test/resources/" + option)));
			optionsPOJOs.add(handle);
			logger.debug("Loaded query config at " + option);
		}
	}

	@Before
	public void createTestOptions() throws JAXBException, FileNotFoundException {
		assertNotNull("Client could not create query options manager", mgr);

		testOptions = mgr.newOptions();
		QueryOptionsHandle impl = (QueryOptionsHandle) testOptions;
		impl.receiveContent(new FileInputStream(new File(
				"src/test/resources/search-config.xml")));

		geoOptions = mgr.newOptions();
		impl = (QueryOptionsHandle) geoOptions;
		impl.receiveContent(new FileInputStream(new File(
				"src/test/resources/search-config-geo.xml")));
	}

	@Test
	public void testEmptyQueryOptions() throws JAXBException {
		QueryOptionsHandle options = mgr.newOptions();

		String optionsResult = options.toString();

		logger.debug("Options result from empty options: {}", optionsResult);
		assertTrue(
				"empty options result",
				optionsResult
						.contains("\"http://marklogic.com/appservices/search\"/>"));
	}

	@Test
	public void marshallAndExamine() throws FileNotFoundException,
			JAXBException {
		QueryOptionsHandle options = mgr.newOptions();
		options.receiveContent(new FileInputStream(new File(
				"src/test/resources/search-config.xml")));

		assertTrue(options.isReturnFacets()); // default is true

	};

	@Test
	public void setReturnFacets() {
		QueryOptionsHandle options = mgr.newOptions();
		options.withReturnFacets(true);
		logger.debug("here is return facets: " + options.isReturnFacets());
		assertTrue(options.isReturnFacets());
		options.withReturnFacets(false);
		logger.debug("here is return facets: " + options.isReturnFacets());
		assertTrue(!options.isReturnFacets());
	}

	@Test
	public void setFragmentScope() {
		QueryOptionsHandle options = mgr.newOptions();
		options.withFragmentScope("Properties");
		logger.debug("here is fragment-scope: " + options.getFragmentScope());
		assertEquals(options.getFragmentScope(), "Properties");
		options.withFragmentScope("Documents");
		assertEquals(options.getFragmentScope(), "Documents");
	}

	@Test
	public void parseComprehensiveOptions() {
		QueryOptionsHandle options = testOptions;
		List<Constraint> constraints = options.getConstraints();
		for (Constraint c : constraints) {
			logger.debug("Testing constraint named {} with class {}",
					c.getName(), c.getClass().getName());
		}
		Range r = options.getConstraintTypesByClassName(Range.class).get(0);
		assertEquals(r.getConstraintName(), "award");
		assertEquals("index from range constraint", "award", r.getAttribute()
				.getLocalPart());

		Word v = options.getConstraintTypesByClassName(Word.class).get(0);
		assertNotNull(v);
		assertEquals("index from value constraint", "name", v.getElement()
				.getLocalPart());

		Term term = options.getTerm();
		List<String> termOptions = term.getTermOptions();
		assertEquals("First term option is 'punctuation-insensitive'",
				termOptions.get(0), "punctuation-insensitive");
		assertEquals("Second term option is 'unwildcarded'",
				termOptions.get(1), "unwildcarded");

		XQueryFunctionLocator applyFunction = term.getTermFunction();
		assertNull("Apply function doesn't exist on this term", applyFunction);
		assertEquals("Term empty apply is all-results", TermApply.ALL_RESULTS,
				term.getEmptyApply());

		Grammar grammar = options.getGrammar();

		assertEquals("Grammar quotation", "\"", grammar.getQuotation());
		assertEquals("Grammar implicit", "and-query", grammar.getImplicit()
				.getLocalName());
		List<Joiner> joiners = grammar.getJoiners();
		assertEquals("joiner attribute incorrect", joiners.get(0).getApply(),
				"infix");
		List<Starter> starters = grammar.getStarters();
		assertNull("starter attribute should be null", starters.get(1)
				.getDelimiter());
		assertEquals("starter attribute incorrect", starters.get(1)
				.getStrength(), 40);

		List<Operator> operators = options.getOperators();
		Operator o = operators.get(0);
		assertEquals("Operator list invalid return", o.getName(), "sort");

		List<State> states = o.getStates();
		State state1 = states.get(0);
		SortOrder so1 = state1.getSortOrders().get(0);

		TransformResults transformResults = options.getTransformResults();
		assertEquals("Apply attribute for transform-results",
				transformResults.getApply(), "snippet");
		// QNamePOJO element = transformResults.getPreferredElements().get(0);
		// //TODO QNamePOJO or ElementLocator
		// assertEquals("Element name from transform-results", "p", element
		// .getElement().getLocalPart());
		// assertEquals("Max Matches from transform-results", 1,
		// transformResults.getMaxMatches());

	}

	@Test
	public void buildTransformResults() {
		TransformResults t = new TransformResults().withApply("snippet");
		assertEquals("Apply attribute for transform-results", t.getApply(),
				"snippet");

		t = new TransformResults().withNs("namespace").withApply("function")
				.withAt("aplace");
		assertEquals("namespace", t.getNs());
		assertEquals("function", t.getApply());
		assertEquals("aplace", t.getAt());

	}

	@Test
	public void buildGrammar() {
		Grammar g = new Grammar();
		QueryOptionsHandle options = mgr.newOptions();
		options.withGrammar(g);

		g.withStarter(new Starter(")").withStrength(30).withApply("grouping")
				.withDelimiter(")"));
		g.withJoiner(new Joiner("AND").withStrength(20).withApply("infix")
				.withElement(new QName("cts:and-query")).withTokenize("word"));

	}

	@Test
	public void parseGeoOptions() {
		QueryOptionsHandle options = geoOptions;
		List<Constraint> constraints = options.getConstraints();

		GeoElement gec = (GeoElement) constraints.get(0)
				.getConstraintDefinition();
		GeoAttrPair gapc = (GeoAttrPair) constraints.get(1)
				.getConstraintDefinition();
		GeoElementPair gepc = (GeoElementPair) constraints.get(2)
				.getConstraintDefinition();

		Heatmap h = gapc.getHeatmap();
		assertEquals("Heatmap attribute check", "-118.2",
				Double.toString(h.getE()));
	}

	@Test
	public void buildRangeConstraintTest() {
		QueryOptionsHandle options = mgr.newOptions();
		Range range = new Range().inside(new Constraint("decade"));
		options.withConstraintDefinition(range);

		assertEquals("Wrong name returned from range object", "decade",
				range.getConstraintName());

		range.withElement("http://marklogic.com/wikipedia", "nominee")
				.withAttribute("year").withType(new QName("xs:gYear"));

		assertEquals(range.getElement(), new QName(
				"http://marklogic.com/wikipedia", "nominee"));
		assertEquals(range.getAttribute(), new QName("year"));

		range.withType(new QName("xs:gYear"));

		range.addBucket("2000s", "2000s", null, null);
		range.addBucket("1990s", "1990s", "1990", "2000");
		range.addBucket("1980s", "1980s", "1980", "1990");
		range.addBucket("1970s", "1970s", "1970", "1980");
		range.addBucket("1960s", "1960s", "1960", "1970");
		range.addBucket("1950s", "1950s", "1950", "1960");
		range.addBucket("1940s", "1940s", "1940", "1950");
		range.addBucket("1930s", "1930s", "1930", "1940");
		range.addBucket("1920s", "1920s", "1920", "1930");

		range.withFacetOption("limit=10");

		String optionsString = options.toString();

		logger.debug(optionsString);
		// namespace prefixes make these comparisons tricky.
		assertTrue(
				"Serialized Range AbstractQueryOption should contain this string",
				optionsString.contains("bucket name=\"2000s\">2000s"));
		assertTrue(
				"Serialized Range AbstractQueryOption should contain this string",
				optionsString.contains("type=\"xs:gYear\""));

		Bucket b = (Bucket) range.getBuckets().get(0);
		assertNull("Bucket value should be as expected. ", b.getLt());

		b = (Bucket) range.getBuckets().get(1);
		assertEquals("Bucket value should be as expected. ", b.getLt(), "2000");

		ComputedBucket c = new ComputedBucket();
		c.setAnchor(AnchorValue.NOW);
		range.addBucket(c);

		List<ComputedBucket> computedBuckets = range.getComputedBuckets();
		assertEquals("Size of computed buckets", 1, computedBuckets.size());
		ComputedBucket computedBucket = computedBuckets.get(0);

		assertEquals("Computed bucket anchor", AnchorValue.NOW,
				computedBucket.getAnchorValue());

	}

	@Test
	public void buildValueConstraintTest() {
		QueryOptionsHandle options = mgr.newOptions();
		Value vc = new Value().inside(new Constraint("sumlev")).withElement(
				"sumlev");
		options.withConstraintDefinition(vc);

		assertEquals(vc.getElement(), new QName("sumlev"));

		String optionsString = options.toString();
		logger.debug(optionsString);

		assertTrue(
				"Serialized Value AbstractQueryOption should contain this string",
				optionsString.contains("name=\"sumlev\""));

	}

	@Test
	public void buildWordConstraintTest() {
		QueryOptionsHandle options = mgr.newOptions();
		Word wc = new Word().inside(new Constraint("intitle")).withField(
				"titleField");
		options.withConstraintDefinition(wc);

		assertEquals(wc.getField(), "titleField");

		String optionsString = options.toString();
		logger.debug(optionsString);

		assertTrue(
				"Serialized Value AbstractQueryOption should contain this string",
				optionsString.contains("<search:field name=\"titleField\""));

	}

	@Test
	public void buildSuggestionSources() {
		QueryOptionsHandle options = mgr.newOptions();
		DefaultSuggestionSource dss = (DefaultSuggestionSource) new DefaultSuggestionSource()
				.withWordLexicon();
		Range range = new Range()
				.inside(new SuggestionSource()
						.withSuggestionOption("suggestionoption"))
				.withElement("http://marklogic.com/wikipedia", "nominee")
				.withAttribute("year");

		dss.withWordLexicon();

		options.withConstraintDefinition(range)
			.withDefaultSuggestionSource(dss);
		
		String optionsString = options.toString();
		logger.debug(optionsString);

		assertTrue(
				"Serialized Suggestion Source should contain ",
				optionsString
						.contains("<search:element ns=\"http://marklogic.com/wiki"));

		assertTrue("Serialized Suggestion Source should contain ",
				optionsString.contains("<search:word-lexicon"));

		assertTrue(range.getSuggestionSource().getSuggestionOptions()
				.contains("suggestionoption"));
	}

	@Test
	public void buildCollectionConstraint() {
		QueryOptionsHandle options = mgr.newOptions();
		Collection c = new Collection().inside(new Constraint()).doFacets(true)
				.withFacetOption("limit=10").withPrefix("http://myprefix");
		options.withConstraintDefinition(c);

		String optionsString = options.toString();
		logger.debug(optionsString);
		assertTrue(
				"Serialized CollectionConstraintImpl should contain facet option",
				optionsString.contains("<search:facet-option>limit=10"));
		assertEquals("Collection prefix is wrong", "http://myprefix",
				c.getPrefix());

	}

	@Test
	public void serializeAndStoreElementQueryConstraint() throws JAXBException {
		String optionsString = "<options xmlns=\"http://marklogic.com/appservices/search\"><constraint name=\"sample\"><element-query name=\"title\" ns=\"http://my/namespace\" /></constraint></options>";
		QueryOptionsHandle options = mgr.newOptions();
		QueryOptionsHandle impl = (QueryOptionsHandle) options;
		impl.receiveContent(new ByteArrayInputStream(optionsString.getBytes()));
		List<Constraint> l = options.getConstraints();
		ElementQuery eqc = (ElementQuery) l.get(0).getConstraintDefinition();

		logger.debug(options.toString());

		assertEquals("Name attribute doesn't match expected.", "title",
				eqc.getName());
		assertEquals("Namespace attribute doesn't match expected.",
				eqc.getNs(), "http://my/namespace");
	}

	@Test
	public void serializeAndStoreCustomConstraint() throws JAXBException {
		String optionsString = "<options xmlns=\"http://marklogic.com/appservices/search\"><constraint name=\"custom\"><custom facet=\"true\"><parse apply=\"parse\" ns=\"http://my/namespace\" at=\"/my/parse.xqy\" /><start-facet apply=\"start\" ns=\"http://my/namespace\" at=\"/my/start.xqy\" /><finish-facet apply=\"finish\" ns=\"http://my/namespace\" at=\"/my/finish.xqy\" /></custom></constraint></options>";
		QueryOptionsHandle options = mgr.newOptions();
		QueryOptionsHandle impl = (QueryOptionsHandle) options;
		impl.receiveContent(new ByteArrayInputStream(optionsString.getBytes()));
		List<Constraint> l = options.getConstraints();
		Custom cc = (Custom) l.get(0).getConstraintDefinition();

		logger.debug(options.toString());

		assertEquals("Facets are true for test constraint", cc.getDoFacets(),
				true);
		assertEquals("getAt for test constraint", cc.getFinishFacet().getAt(),
				"/my/finish.xqy");
		assertEquals("getNs for test constraint", cc.getStartFacet().getNs(),
				"http://my/namespace");
		assertEquals("getApply for test constraint", cc.getParse().getApply(),
				"parse");
		assertEquals("Name for custom constratin", l.get(0).getName(), "custom");

	}

	@Test
	public void parseAndBuildPropertiesConstraint() {
		QueryOptionsHandle options = mgr.newOptions();

		options.withConstraintDefinition(new Properties().inside(new Constraint(
				"props")));

		logger.debug(options.toString());

		mgr.writeOptions("props", options);

		QueryOptionsHandle options2 = mgr
				.readOptions("props", mgr.newOptions());
		assertEquals("Unexpected class from JAXB unmarshalling", options2
				.getConstraints().get(0).getConstraintDefinition().getClass()
				.getName(), Properties.class.getName());

	}

	@Test
	public void parseAndBuildAdditionalQuery() throws JAXBException {
		String optionsString = "<options xmlns=\"http://marklogic.com/appservices/search\"><additional-query><directory-query xmlns=\"http://marklogic.com/cts\"><uri>/oscars/</uri></directory-query></additional-query></options>";
		ByteArrayInputStream bais = new ByteArrayInputStream(
				optionsString.getBytes());
		QueryOptionsHandle options = mgr.newOptions();
		options.receiveContent(bais);

		Element e = options.getAdditionalQuery();
		assertEquals("QueryString wrong after serializing AdditionalQuery", e
				.getFirstChild().getNodeName(), "uri");

		Element uri = (Element) e.getFirstChild();
		uri.setTextContent("/oscars2/");
		options.withAdditionalQuery(e);

		logger.debug(options.toString());
		assertTrue("Updated Option not updated from AdditionalQuery", options
				.toString().contains("/oscars2/"));

	}

	@Test
	public void parseAndBuildOperator() throws FileNotFoundException,
			JAXBException {
		QueryOptionsHandle options = testOptions;

		Operator operator = testOptions.getOperators().get(0);
		logger.debug("Operator found from test config {}", operator);

		State s = (State) operator.getStates().get(0);

		assertEquals("State from operator", "relevance", s.getName());

		SortOrder so = new SortOrder();

		so.withElement("http://my/namespace", "green")
				.withAttribute("http://my/namespace", "pantone").withScore();

		options.withOperator(operator.withState(new State("newstate")
				.withSortOrder(so)));

		String optionsString = options.toString();

		logger.debug("Sort order found from test config {}", optionsString);
		assertTrue("Sort order should contain empty score element",
				optionsString.contains("score><"));
		assertTrue("Sort order should contain element index def",
				optionsString.contains("name=\"green\""));

		// TODO unset score
		//
	}

	@Test
	public void testUnMarshall() {
		for (QueryOptionsHandle handle : optionsPOJOs) {
			logger.debug("testing unmarshall of {} ", handle.toString());
			List<Range> ranges = handle
					.getConstraintTypesByClassName(Range.class);
			assertNotNull("List of any options type should be a List", ranges);
			if (ranges.size() > 0) {
				Range range = ranges.get(0);
				assertNotNull("Range should not be null", range);
			}
			List<Collection> collections = handle
					.getConstraintTypesByClassName(Collection.class);
			assertNotNull("List of any options type should be a List",
					collections);
			if (collections.size() > 0) {
				Collection range = collections.get(0);
				assertNotNull("Range should not be null", range);
			}
			List<Value> values = handle
					.getConstraintTypesByClassName(Value.class);
			assertNotNull("List of any options type should be a List", values);
			if (values.size() > 0) {
				Value range = values.get(0);
				assertNotNull("Range should not be null", range);
			}
			List<Word> words = handle.getConstraintTypesByClassName(Word.class);
			assertNotNull("List of any options type should be a List", words);
			if (words.size() > 0) {
				Word range = words.get(0);
				assertNotNull("Range should not be null", range);
			}
			List<ElementQuery> elementQueries = handle
					.getConstraintTypesByClassName(ElementQuery.class);
			assertNotNull("List of any options type should be a List",
					elementQueries);
			if (elementQueries.size() > 0) {
				ElementQuery range = elementQueries.get(0);
				assertNotNull("Range should not be null", range);
			}
			List<Properties> properties = handle
					.getConstraintTypesByClassName(Properties.class);
			assertNotNull("List of any options type should be a List",
					properties);
			if (properties.size() > 0) {
				Properties range = properties.get(0);
				assertNotNull("Range should not be null", range);
			}
			List<Custom> customs = handle
					.getConstraintTypesByClassName(Custom.class);
			assertNotNull("List of any options type should be a List", customs);
			if (customs.size() > 0) {
				Custom range = customs.get(0);
				assertNotNull("Range should not be null", range);
			}

			// TODO GEOSPATIAL
			for (Constraint constraint : handle.getConstraints()) {
				String name = constraint.getName();
				logger.debug("Found constaint with name {}", name);
			}

			// for (Operator operator : handle.getOperators()) {
			//
			// }
		}
	}

	@Test
	public void testMarshall() throws SAXException, IOException,
			ParserConfigurationException {
		for (QueryOptionsHandle option : optionsPOJOs) {
			logger.debug("Testing this option: {}", option.toString());

			DOMHandle dom = new DOMHandle();
			DocumentBuilderFactory factory = dom.getFactory();
			Document d1 = factory.newDocumentBuilder().parse(
					new ByteArrayInputStream(option.toString().getBytes()));
			Document document = dom.with(d1).get();
			Element rootElement = document.getDocumentElement();
			assertEquals(
					"QName of root element incorrect",
					new QName("http://marklogic.com/appservices/search",
							"options"),
					new QName(rootElement.getNamespaceURI(), rootElement
							.getLocalName()));

			NodeList nl = rootElement.getElementsByTagNameNS(Options.SEARCH_NS,
					"constraint");
			if (nl.getLength() > 0) {
				Element constraintElement = (Element) nl.item(0);
				String name = constraintElement.getAttribute("name");
				logger.debug("Constraint name {} found.", name);
			}
			nl = rootElement.getElementsByTagNameNS(Options.SEARCH_NS,
					"return-facets");
			if (nl.getLength() > 0) {
				Element constraintElement = (Element) nl.item(0);
				String text = constraintElement.getTextContent();
				logger.debug("Text {} found for return-facets.", text);
			}
		}
	}

	@Test
	public void testQueryAnnotations() {
		QueryOptionsHandle annotatedOptions = optionsPOJOs.get(2);
		logger.debug("annotatedOptions: " + annotatedOptions.toString());

		// assertFalse("Options shoudn't contain tacit default elements",
		// annotatedOptions.toString().contains("return-query"));

		Constraint constraint = annotatedOptions.getConstraints().get(0);
		List<QueryAnnotation> annotations = constraint.getAnnotations();
		QueryAnnotation firstQueryAnnotation = annotations.get(0);

		Element annotationElement = firstQueryAnnotation.get(0);
		logger.debug("Annotation element received from test {}",
				annotationElement);
		assertEquals("Annotation element's namespace", "http://namespace/x",
				annotationElement.getNamespaceURI());
		assertEquals("Annotation localname", "note",
				annotationElement.getLocalName());
		assertEquals("Annotation text",
				"This is an annotation in the x namespace",
				annotationElement.getTextContent());

	}
}
