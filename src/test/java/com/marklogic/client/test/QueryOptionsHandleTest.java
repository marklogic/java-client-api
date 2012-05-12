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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.config.QueryOptionsBuilder.FragmentScope;
import com.marklogic.client.config.QueryOptionsBuilder.GeoAttrPair;
import com.marklogic.client.config.QueryOptionsBuilder.GeoElement;
import com.marklogic.client.config.QueryOptionsBuilder.GeoElementPair;
import com.marklogic.client.config.QueryOptionsBuilder.Heatmap;
import com.marklogic.client.config.QueryOptionsBuilder.QueryAnnotation;
import com.marklogic.client.config.QueryOptionsBuilder.QueryCollection;
import com.marklogic.client.config.QueryOptionsBuilder.QueryConstraint;
import com.marklogic.client.config.QueryOptionsBuilder.QueryCustom;
import com.marklogic.client.config.QueryOptionsBuilder.QueryDefaultSuggestionSource;
import com.marklogic.client.config.QueryOptionsBuilder.QueryElementQuery;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.QueryJoiner;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.QueryJoiner.Comparator;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.QueryStarter;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.Tokenize;
import com.marklogic.client.config.QueryOptionsBuilder.QueryOperator;
import com.marklogic.client.config.QueryOptionsBuilder.QueryOptions;
import com.marklogic.client.config.QueryOptionsBuilder.QueryProperties;
import com.marklogic.client.config.QueryOptionsBuilder.QueryRange;
import com.marklogic.client.config.QueryOptionsBuilder.QueryRange.Bucket;
import com.marklogic.client.config.QueryOptionsBuilder.QueryRange.QueryComputedBucket;
import com.marklogic.client.config.QueryOptionsBuilder.QueryRange.QueryComputedBucket.AnchorValue;
import com.marklogic.client.config.QueryOptionsBuilder.QuerySortOrder;
import com.marklogic.client.config.QueryOptionsBuilder.QuerySortOrder.Direction;
import com.marklogic.client.config.QueryOptionsBuilder.QueryState;
import com.marklogic.client.config.QueryOptionsBuilder.QuerySuggestionSource;
import com.marklogic.client.config.QueryOptionsBuilder.QueryTerm;
import com.marklogic.client.config.QueryOptionsBuilder.QueryTerm.TermApply;
import com.marklogic.client.config.QueryOptionsBuilder.QueryTransformResults;
import com.marklogic.client.config.QueryOptionsBuilder.QueryValue;
import com.marklogic.client.config.QueryOptionsBuilder.QueryWord;
import com.marklogic.client.config.QueryOptionsBuilder.XQueryExtension;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.util.QueryOptionsUtilities;

public class QueryOptionsHandleTest {

	private static QueryOptionsManager mgr;
	private static QueryOptionsHandle testOptions;
	private static QueryOptionsHandle geoOptions;
	private static QueryOptionsBuilder cb;

	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);
	private static final String Collation = null;
	private static final String DEFAULT_COLLATION = null;

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
	public static void setupTestOptions() throws FileNotFoundException {
		Common.connectAdmin();
		mgr = Common.client.newQueryOptionsManager();
		optionsPOJOs = new ArrayList<QueryOptionsHandle>();

		for (String option : testOptionsCorpus) {
			FileHandle f = new FileHandle(new File("src/test/resources/"
					+ option));
			mgr.writeOptions("tmp", f);
			QueryOptionsHandle handle = mgr.readOptions("tmp",
					new QueryOptionsHandle());
			optionsPOJOs.add(handle);
			logger.debug("Loaded query config at " + option);
		}
		testOptions = optionsPOJOs.get(testIndex);

		geoOptions = optionsPOJOs.get(geoIndex);

		cb = new QueryOptionsBuilder();
	}

	@Test
	public void buildCollectionConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(cb.constraint(
				null,
				cb.collection(true, "http://myprefix",
						cb.facetOption("limit=10"))));

		String optionsString = QueryOptionsUtilities.toXMLString(mgr, options);
		QueryCollection c = options.getConstraints().get(0)
				.getConstraintConfiguration();
		logger.debug(optionsString);
		assertTrue(
				"Serialized CollectionConstraintImpl should contain facet option",
				optionsString.contains("<search:facet-option>limit=10"));
		assertEquals("CollectionOption prefix is wrong", "http://myprefix",
				c.getPrefix());

	}

	@Test
	public void buildCustomConstraint() {
		QueryConstraint constraintOption = cb.constraint("queryCustom", cb
				.customFacet(cb.extension("parse", "http://my/namespace",
						"/my/parse.xqy"), cb.extension("start",
						"http://my/namespace", "/my/start.xqy"), cb.extension(
						"finish", "http://my/namespace", "/my/finish.xqy")));

		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(constraintOption);
		QueryCustom queryCustom = constraintOption.getConstraintConfiguration();
		assertEquals("queryCustom constraint builder", "parse", queryCustom
				.getParse().getApply());
		assertEquals("queryCustom constraint builder", "/my/finish.xqy",
				queryCustom.getFinishFacet().getAt());
		assertEquals("queryCustom constraint builder", "http://my/namespace",
				queryCustom.getStartFacet().getNs());

		mgr.writeOptions("tmp", options);

		// ---------------

		options = mgr.readOptions("tmp", new QueryOptionsHandle());
		List<QueryConstraint> l = options.getConstraints();
		QueryCustom cc = (QueryCustom) l.get(0).getConstraintConfiguration();

		logger.debug(options.toString());

		assertEquals("Facets are true for test constraint", cc.getDoFacets(),
				true);
		assertEquals("getAt for test constraint", cc.getFinishFacet().getAt(),
				"/my/finish.xqy");
		assertEquals("getNs for test constraint", cc.getStartFacet().getNs(),
				"http://my/namespace");
		assertEquals("getApply for test constraint", cc.getParse().getApply(),
				"parse");
		assertEquals("Name for queryCustom constratin", l.get(0).getName(),
				"queryCustom");

	}

	@Test
	public void buildGrammar() {
		QueryGrammar g = cb
				.grammar(
						cb.quotation("\""),
						cb.domElement("<cts:and-query strength=\"20\" xmlns:cts=\"http://marklogic.com/cts\"/>"),
						cb.starterGrouping("(", 30, ")"), cb.starterPrefix("-",
								40, new QName("cts:not-query)")), cb.joiner(
								"AND", 20, JoinerApply.PREFIX, new QName(
										"cts:and-query"), Tokenize.WORD), cb
								.joiner("OR", 10, JoinerApply.INFIX, new QName(
										"cts:or-query"), Tokenize.WORD), cb
								.joiner("NEAR", 30, JoinerApply.INFIX,
										new QName("cts:near-query"),
										Tokenize.WORD), cb.joiner("NEAR/", 30,
								JoinerApply.NEAR2, new QName("cts:near-query"),
								null, 2), cb.joiner("LT", 50,
								JoinerApply.CONSTRAINT, Comparator.LT,
								Tokenize.WORD));

		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(g);
		mgr.writeOptions("grammar", options);
		options = mgr.readOptions("grammar", options);

		g = options.getGrammar();
		assertEquals("Number of starters", 2, g.getStarters().size());
		assertEquals("Number of joiners", 5, g.getJoiners().size());
		assertEquals("DomElement on grammar", "and-query", g.getImplicit().getLocalName());

	}

	@Test
	public void buildRangeConstraintTest() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		QueryRange range = cb.range(cb.facet(true), cb.type("xs:gYear"),
				cb.element("http://marklogic.com/wikipedia", "nominee"),
				cb.attribute("year"), cb.bucket("2000s", "2000s", null, null),
				cb.bucket("1990s", "1990s", "1990", "2000"),
				cb.bucket("1980s", "1980s", "1980", "1990"),
				cb.bucket("1970s", "1970s", "1970", "1980"),
				cb.bucket("1960s", "1960s", "1960", "1970"),
				cb.bucket("1950s", "1950s", "1950", "1960"),
				cb.bucket("1940s", "1940s", "1940", "1950"),
				cb.bucket("1930s", "1930s", "1930", "1940"),
				cb.bucket("1920s", "1920s", "1920", "1930"),
				cb.facetOption("limit=10"));

		options.build(cb.constraint("decade", range));

		assertEquals(range.getElement(), new QName(
				"http://marklogic.com/wikipedia", "nominee"));
		assertEquals(range.getAttribute(), new QName("year"));

		String optionsString = QueryOptionsUtilities.toXMLString(mgr, options);

		logger.debug(optionsString);
		// namespace prefixes make these comparisons tricky.
		assertTrue(
				"Serialized Range AbstractQueryOption should contain this string",
				optionsString.contains("bucket name=\"2000s\">2000s"));
		assertTrue(
				"Serialized Range AbstractQueryOption should contain this string",
				optionsString.contains("type=\"xs:gYear\""));

		Bucket b = (Bucket) range.getBuckets().get(0);
		assertNull("Bucket valueOption should be as expected. ", b.getLt());

		b = (Bucket) range.getBuckets().get(1);
		assertEquals("Bucket valueOption should be as expected. ", b.getLt(),
				"2000");

		QueryComputedBucket c = new QueryComputedBucket();
		c.setAnchor(AnchorValue.NOW);
		range.deleteBuckets();
		range.addComputedBucket(c);

		List<QueryComputedBucket> computedBuckets = range.getComputedBuckets();
		assertEquals("Size of computed buckets", 1, computedBuckets.size());
		QueryComputedBucket computedBucket = computedBuckets.get(0);

		assertEquals("Computed bucket anchor", AnchorValue.NOW,
				computedBucket.getAnchorValue());
	}

	@Test
	public void buildSuggestionSources() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		QueryDefaultSuggestionSource dss = cb.defaultSuggestionSource(
				cb.wordLexicon());
		
		options.build(
				cb.suggestionSource(
						cb.range(false, new QName("gs:year"),
								cb.element("http://marklogic.com/wikipedia", "nominee"),
								cb.attribute("year")),
						cb.suggestionSource("suggestionOption"),
						cb.wordLexicon("http://marklogic.com/collation", FragmentScope.DOCUMENTS)));
				
		options.build(dss);

		mgr.writeOptions("tmp", options);
		QueryOptionsHandle options2 = mgr.readOptions("tmp", new QueryOptionsHandle());
		String optionsString = QueryOptionsUtilities.toXMLString(mgr, options2);
		logger.debug(optionsString);

		assertEquals(
				"Serialized Suggestion Source",
				"http://marklogic.com/wikipedia",
				options2.getSuggestionSources().get(0).getConstraintConfiguration().getElement().getNamespaceURI());

		assertEquals("Serialized SuggestionSource and wordLexicon", 
				FragmentScope.DOCUMENTS, 
				options2.getSuggestionSources().get(0).getWordLexicon().getFragmentScope());

		assertEquals("Suggestion Option", "suggestionOption", options.getSuggestionSources().get(0).getSuggestionOptions().get(0));
	}

	@Test
	public void buildTerm() {
		QueryOptionsHandle options = new QueryOptionsHandle().build(cb.term(
				TermApply.ALL_RESULTS, cb.word(cb.element("nation"))));

		QueryWord word = (QueryWord) options.getTerm()
				.getConstraintConfiguration();
		assertEquals("TermConfig constraint definition", "nation", word
				.getElement().getLocalPart());
		mgr.writeOptions("tmp", options);
		QueryOptionsHandle options2 = mgr.readOptions("tmp",
				new QueryOptionsHandle());
		assertEquals("TermConfig after storing", "nation", options2.getTerm()
				.getConstraintConfiguration().getElement().getLocalPart());

	}

	@Test
	public void buildTransformResults() {
		QueryTransformResults t = cb.transformResults("snippet");
		assertEquals("Apply attribute for transform-results", t.getApply(),
				"snippet");

		t = cb.transformResultsOption(cb.extension("function", "ns", "aplace"));

		assertEquals("ns", t.getNs());
		assertEquals("function", t.getApply());
		assertEquals("aplace", t.getAt());

	}

	@Test
	public void buildValueConstraintTest() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		QueryConstraint constraint = cb.constraint("sumlev",
				cb.value(cb.element("sumlev")));

		QueryValue vc = constraint.getConstraintConfiguration();
		options.build(constraint);

		assertEquals(vc.getElement(), new QName("sumlev"));

		String optionsString = QueryOptionsUtilities.toXMLString(mgr, options);
		logger.debug(optionsString);

		assertTrue(
				"Serialized ValueOption AbstractQueryOption should contain this string",
				optionsString.contains("name=\"sumlev\""));

	}

	@Test
	public void buildWordConstraintTest() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(cb.constraint("intitle",
				cb.word(cb.field("titlefield"))));

		QueryWord wc = options.getConstraint("intitle")
				.getConstraintConfiguration();
		assertEquals(wc.getFieldName(), "titlefield");

		String optionsString = QueryOptionsUtilities.toXMLString(mgr, options);
		logger.debug(optionsString);

		assertTrue(
				"Serialized ValueOption AbstractQueryOption should contain this string",
				optionsString.contains("<search:field name=\"titlefield\""));

	}

	@Test
	public void parseAndBuildAdditionalQuery() throws JAXBException {
		String optionsString = "<options xmlns=\"http://marklogic.com/appservices/search\"><additional-query><directory-query xmlns=\"http://marklogic.com/cts\"><uri>/oscars/</uri></directory-query></additional-query></options>";
		mgr.writeOptions("tmp", new StringHandle(optionsString));
		QueryOptionsHandle options = mgr.readOptions("tmp",
				new QueryOptionsHandle());

		Element e = options.getAdditionalQuery();
		assertEquals("QueryString wrong after serializing AdditionalQuery", e
				.getFirstChild().getNodeName(), "uri");

		Element uri = (Element) e.getFirstChild();
		uri.setTextContent("/oscars2/");
		options.withAdditionalQuery(e);
		optionsString = QueryOptionsUtilities.toXMLString(mgr, options);

		logger.debug(optionsString);
		assertTrue("Updated Option not updated from AdditionalQuery",
				optionsString.contains("/oscars2/"));

	}

	@Test
	public void parseAndBuildOperator() throws FileNotFoundException,
			JAXBException {
		QueryOptionsHandle options = testOptions;

		QueryOperator operatorOption = testOptions.getOperators().get(0);
		logger.debug("OperatorOption found from test config {}", operatorOption);

		QueryState s = (QueryState) operatorOption.getStates().get(0);

		assertEquals("State from operator", "relevance", s.getName());

		QuerySortOrder so = cb.sortOrder("xs:string",
				"http://marklogic.com/collation", Direction.ASCENDING,
				cb.element("http://my/namespace", "green"),
				cb.attribute("http://my/namespace", "pantone"), cb.score());

		options.build(cb.operator("sortcolor", cb.state("pantone", so)));

		String optionsString = QueryOptionsUtilities.toXMLString(mgr, options);

		logger.debug("Sort order found from test config {}", optionsString);
		assertTrue("Sort order should contain empty score element",
				optionsString.contains("<search:score/>"));
		assertTrue("Sort order should contain element index def",
				optionsString.contains("name=\"green\""));

		// TODO unset score
		//
	}

	@Test
	public void parseAndBuildPropertiesConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(cb.constraint("props", cb.properties()));

		logger.debug(options.toString());

		mgr.writeOptions("props", options);

		QueryOptionsHandle options2 = mgr.readOptions("props",
				new QueryOptionsHandle());
		assertEquals("Unexpected class from JAXB unmarshalling", options2
				.getConstraints().get(0).getConstraintConfiguration()
				.getClass().getName(), QueryProperties.class.getName());

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
				.getConstraintConfiguration();
		assertEquals("index from range constraint", "award", r.getAttribute()
				.getLocalPart());

		QueryWord v = options.getConstraint("inname")
				.getConstraintConfiguration();
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
				"infix");
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

		QueryTransformResults transformResultsOption = options
				.getTransformResults();
		assertEquals("Apply attribute for transform-results",
				transformResultsOption.getApply(), "snippet");
		// MarkLogicQName element =
		// transformResults.getPreferredElements().get(0);
		// //TODO MarkLogicQName or ElementLocator
		// assertEquals("Element name from transform-results", "p", element
		// .getElement().getLocalPart());
		// assertEquals("Max Matches from transform-results", 1,
		// transformResults.getMaxMatches());

	}

	@Test
	public void parseGeoOptions() {
		QueryOptionsHandle options = geoOptions;
		List<QueryConstraint> constraintOptions = options.getConstraints();

		GeoElement gec = (GeoElement) constraintOptions.get(0)
				.getConstraintConfiguration();
		GeoAttrPair gapc = (GeoAttrPair) constraintOptions.get(1)
				.getConstraintConfiguration();
		GeoElementPair gepc = (GeoElementPair) constraintOptions.get(2)
				.getConstraintConfiguration();

		Heatmap h = gapc.getHeatmap();
		assertEquals("Heatmap attribute check", "-118.2",
				Double.toString(h.getE()));
	}

	@Test
	public void serializeAndStoreElementQueryConstraint() throws JAXBException {
		String optionsString = "<options xmlns=\"http://marklogic.com/appservices/search\"><constraint name=\"sample\"><element-query name=\"title\" ns=\"http://my/namespace\" /></constraint></options>";
		mgr.writeOptions("tmp", new StringHandle(optionsString));
		QueryOptionsHandle options = mgr.readOptions("tmp",
				new QueryOptionsHandle());
		List<QueryConstraint> l = options.getConstraints();
		QueryElementQuery eqc = (QueryElementQuery) l.get(0)
				.getConstraintConfiguration();

		logger.debug(options.toString());

		assertEquals("Name attribute doesn't match expected.", "title",
				eqc.getName());
		assertEquals("Namespace attribute doesn't match expected.",
				eqc.getNs(), "http://my/namespace");
	}

	@Test
	public void buildTextOptions() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		
		options.build(
				cb.returnFacets(true),
				cb.returnMetrics(false),
//TODO implement these:				cb.searchableExpression("/sf1"), 
//				cb.additionalQuery("<cts:"),
				cb.annotation("<a:note xmlns:a=\"http://marklogic.com/note\">note</a:note>"),
				cb.concurrencyLevel(2),
				cb.debug(true),
				cb.fragmentScope(FragmentScope.PROPERTIES),
				cb.forest(123L),
				cb.forest(235L),
				cb.pageLength(10L),
				cb.qualityWeight(3.4),
				cb.returnAggregates(true),
				cb.returnConstraints(true),
				cb.returnFrequencies(true),
				cb.returnPlan(true),
				cb.returnQtext(true),
				cb.returnQuery(true),
				cb.returnResults(false),
				cb.returnSimilar(true),
				cb.returnValues(false),
				cb.searchOption("limit=10"),
				cb.transformResults("raw"));
		
		assertEquals("builders for facets options", true, options.getReturnFacets());
		assertEquals("builders for metrics options", false, options.getReturnMetrics());
		
				
		logger.debug("here is fragment-scope: " + options.getFragmentScope());
		assertEquals(options.getFragmentScope(), "properties");
		options.setFragmentScope(FragmentScope.DOCUMENTS);
		assertEquals(options.getFragmentScope(), "documents");
	}

	@Test
	public void setReturnFacets() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.withReturnFacets(true);
		logger.debug("here is return facets: " + options.getReturnFacets());
		assertTrue(options.getReturnFacets());
		options.withReturnFacets(false);
		logger.debug("here is return facets: " + options.getReturnFacets());
		assertTrue(!options.getReturnFacets());
	}

	@Test
	public void testConstraintFluentSetter() {
		QueryConstraint awardConstraint = cb.constraint("award", cb
				.range(false, new QName("xs:string"), DEFAULT_COLLATION,
						cb.bucket("2000s", "2000s", null, null),
						cb.bucket("1990s", "1990s", "1990", "2000"),
						cb.bucket("1980s", "1980s", "1980", "1990"),
						cb.bucket("1970s", "1970s", "1970", "1980"),
						cb.bucket("1960s", "1960s", "1960", "1970"),
						cb.bucket("1950s", "1950s", "1950", "1960"),
						cb.bucket("1940s", "1940s", "1940", "1950"),
						cb.bucket("1930s", "1930s", "1930", "1940"),
						cb.bucket("1920s", "1920s", "1920", "1930"),
						cb.facetOption("limit=10")));

		assertEquals("Getting to bucket from constraint", "1990",
				((QueryRange) awardConstraint.getConstraintConfiguration())
						.getBuckets().get(1).getGe());
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

			Document document = QueryOptionsUtilities.toDocument(mgr, option);
			Element rootElement = document.getDocumentElement();
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

		annotatedOptions.build(cb.annotation("<a:note xmlns:a=\"http://marklogic.com/note\">note</a:note>"));
		
	}
}
