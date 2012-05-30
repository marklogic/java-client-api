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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.ServerConfigurationManager;
import com.marklogic.client.config.QueryOptions;
import com.marklogic.client.config.QueryOptions.FragmentScope;
import com.marklogic.client.config.QueryOptions.Heatmap;
import com.marklogic.client.config.QueryOptions.QueryAnnotation;
import com.marklogic.client.config.QueryOptions.QueryCollection;
import com.marklogic.client.config.QueryOptions.QueryConstraint;
import com.marklogic.client.config.QueryOptions.QueryCustom;
import com.marklogic.client.config.QueryOptions.QueryDefaultSuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryElementQuery;
import com.marklogic.client.config.QueryOptions.QueryGeospatialAttributePair;
import com.marklogic.client.config.QueryOptions.QueryGeospatialElement;
import com.marklogic.client.config.QueryOptions.QueryGeospatialElementPair;
import com.marklogic.client.config.QueryOptions.QueryGrammar;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner.Comparator;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryStarter;
import com.marklogic.client.config.QueryOptions.QueryGrammar.Tokenize;
import com.marklogic.client.config.QueryOptions.QueryOperator;
import com.marklogic.client.config.QueryOptions.QueryProperties;
import com.marklogic.client.config.QueryOptions.QueryRange;
import com.marklogic.client.config.QueryOptions.QueryRange.Bucket;
import com.marklogic.client.config.QueryOptions.QueryRange.ComputedBucket;
import com.marklogic.client.config.QueryOptions.QueryRange.ComputedBucket.AnchorValue;
import com.marklogic.client.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.config.QueryOptions.QuerySortOrder.Direction;
import com.marklogic.client.config.QueryOptions.QueryState;
import com.marklogic.client.config.QueryOptions.QuerySuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryTerm;
import com.marklogic.client.config.QueryOptions.QueryTerm.TermApply;
import com.marklogic.client.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.config.QueryOptions.QueryValue;
import com.marklogic.client.config.QueryOptions.QueryValues;
import com.marklogic.client.config.QueryOptions.QueryWord;
import com.marklogic.client.config.QueryOptions.XQueryExtension;
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;

public class QueryOptionsHandleTest {

	private static QueryOptionsManager mgr;
	private static QueryOptionsHandle testOptions;
	private static QueryOptionsHandle geoOptions;
	private static ServerConfigurationManager serverConfig;
	private static Boolean initialConfig;
	
	private static QueryOptionsBuilder builder;

	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);
	private static final String DEFAULT_COLLATION = "http://marklogic.com/collation/";

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
		serverConfig = Common.client.newServerConfigurationManager();

		serverConfig.readConfiguration();
		initialConfig = serverConfig.getQueryOptionValidation();

		serverConfig.setQueryOptionValidation(false);
		serverConfig.writeConfiguration();

		mgr = Common.client.newQueryOptionsManager();
	
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
	}
	
	@AfterClass
	public static void resetOptionsValidation() {
		serverConfig.setQueryOptionValidation(initialConfig);
		serverConfig.writeConfiguration();
	}

	@Test
	public void buildCollectionConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.constraint(
				null,
				builder.collection(true, "http://myprefix",
						builder.facetOption("limit=10"))));

		String optionsString = options.toXMLString();
		QueryCollection c = options.getConstraints().get(0)
				.getSource();
		logger.debug(optionsString);
		assertTrue(
				"Serialized CollectionConstraintImpl should contain facet option",
				optionsString.contains("<search:facet-option>limit=10"));
		assertEquals("CollectionOption prefix is wrong", "http://myprefix",
				c.getPrefix());

	}

	@Test
	public void buildCustomConstraint() {
		QueryConstraint constraintOption = builder.constraint("queryCustom", builder
				.customFacet(
						builder.parse("parse", "http://my/namespace","/my/parse.xqy"), 
						builder.startFacet("start", "http://my/namespace", "/my/start.xqy"), 
						builder.finishFacet("finish", "http://my/namespace", "/my/finish.xqy"),
						builder.facetOption("limit=10"),
						builder.termOption("punctuation-insensitive"),
						builder.annotation("<a>annotation</a>")));

		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(constraintOption);
		QueryCustom queryCustom = (QueryCustom) constraintOption.getSource();
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
		QueryCustom cc = (QueryCustom) l.get(0).getSource();

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

		options = new QueryOptionsHandle();
		options.build(builder.constraint("customParse", builder.customParse(builder.extension("myfunc", "http:/my/namespace", "/my/at.xqy"))));
	}

	@Test
	public void buildGrammar() {
		QueryGrammar g = builder
				.grammar(
						"\"",
						builder.domElement("<cts:and-query strength=\"20\" xmlns:cts=\"http://marklogic.com/cts\"/>"),
						builder.starterGrouping("(", 30, ")"), builder.starterPrefix("-",
								40, new QName("cts:not-query)")), builder.joiner(
								"AND", 20, JoinerApply.PREFIX, new QName(
										"cts:and-query"), Tokenize.WORD), builder
								.joiner("OR", 10, JoinerApply.INFIX, new QName(
										"cts:or-query"), Tokenize.WORD), builder
								.joiner("NEAR", 30, JoinerApply.INFIX,
										new QName("cts:near-query"),
										Tokenize.WORD), builder.joiner("NEAR/", 30,
								JoinerApply.NEAR2, new QName("cts:near-query"),
								null, 2), builder.joiner("LT", 50,
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

		QueryRange range = builder.range(true, builder.type("xs:gYear"),
				builder.element("http://marklogic.com/wikipedia", "nominee"),
				builder.attribute("year"), builder.bucket("2000s", "2000s", null, null),
				builder.bucket("1990s", "1990s", "1990", "2000"),
				builder.bucket("1980s", "1980s", "1980", "1990"),
				builder.bucket("1970s", "1970s", "1970", "1980"),
				builder.bucket("1960s", "1960s", "1960", "1970"),
				builder.bucket("1950s", "1950s", "1950", "1960"),
				builder.bucket("1940s", "1940s", "1940", "1950"),
				builder.bucket("1930s", "1930s", "1930", "1940"),
				builder.bucket("1920s", "1920s", "1920", "1930"),
				builder.facetOption("limit=10"));

		options.build(builder.constraint("decade", range));

		assertEquals(range.getElement(), new QName(
				"http://marklogic.com/wikipedia", "nominee"));
		assertEquals(range.getAttribute(), new QName("year"));

		String optionsString = options.toXMLString();

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

		QueryOptionsHandle options2 = new QueryOptionsHandle();
		options2.build(builder.constraint("date", builder.range(false, new QName("xs:dateTime"),
				builder.computedBucket("older", "Older than 1 years", null, "-P365D", AnchorValue.NOW),
				builder.computedBucket("year", "1 month to 1 year ago", "-P30D", "-P365D", AnchorValue.NOW),
				builder.computedBucket("month", "7 to 30 days ago", "-P30D", "-P7D", AnchorValue.NOW),
				builder.computedBucket("week", "1 to 7 days ago", "-P7D", "-P1D", AnchorValue.NOW),
				builder.computedBucket("today", "Today", "-P1D", "P0D", AnchorValue.NOW),
				builder.computedBucket("future", "Future", "P0D", null, AnchorValue.NOW),
				builder.facetOption("limit=10"),
				builder.facetOption("descending"),
				builder.element("http://purl.org/dc/elements/1.1/", "date"))));
		
		
		List<ComputedBucket> computedBuckets = ((QueryRange) options2.getConstraint("date").getSource()).getComputedBuckets();
		assertEquals("Size of computed buckets", 6, computedBuckets.size());
		ComputedBucket computedBucket = computedBuckets.get(0);

		assertEquals("Computed bucket anchor", AnchorValue.NOW,
				computedBucket.getAnchorValue());
		assertNull("Computed bucket ge value", computedBucket.getGe());
		assertEquals("Computed bucket lt value", "-P365D", computedBucket.getLt());
		
	}

	@Test
	public void buildSuggestionSources() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		QueryDefaultSuggestionSource dss = builder.defaultSuggestionSource(
				builder.wordLexicon());
		
		options.build(
				builder.suggestionSource(
						builder.range(false, new QName("gs:year"),
								builder.element("http://marklogic.com/wikipedia", "nominee"),
								builder.attribute("year")),
						builder.suggestionSourceOption("suggestionOption"),
						builder.wordLexicon("http://marklogic.com/collation", FragmentScope.DOCUMENTS)));
				
		options.build(dss);

		mgr.writeOptions("tmp", options);
		QueryOptionsHandle options2 = mgr.readOptions("tmp", new QueryOptionsHandle());
		String optionsString = options.toXMLString();
		logger.debug(optionsString);

		assertEquals(
				"Serialized Suggestion Source",
				"http://marklogic.com/wikipedia",
				options2.getSuggestionSources().get(0).getConstraintConfiguration().getElement().getNamespaceURI());

		assertEquals("Serialized SuggestionSource and wordLexicon", 
				FragmentScope.DOCUMENTS, 
				options2.getSuggestionSources().get(0).getWordLexicon().getFragmentScope());

		assertEquals("Suggestion Option", "suggestionOption", options.getSuggestionSources().get(0).getSuggestionOptions().get(0));
		
		// Suggestion Source with ref.
		options = new QueryOptionsHandle();
		options.build(
				builder.constraint("nominee",
						builder.range(false, new QName("gs:year"),
								builder.element("http://marklogic.com/wikipedia", "nominee"),
								builder.attribute("year"))),
			     builder.suggestionSource("nominee"));
				
	}

	@Test
	public void buildTerm() {
		QueryOptionsHandle options = new QueryOptionsHandle().build(builder.term(
				TermApply.ALL_RESULTS, builder.word(builder.element("nation"))));

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
		QueryTransformResults t = builder.transformResults("snippet");
		assertEquals("Apply attribute for transform-results", t.getApply(),
				"snippet");

		t = builder.transformResultsOption(builder.extension("function", "ns", "aplace"));

		assertEquals("ns", t.getNs());
		assertEquals("function", t.getApply());
		assertEquals("aplace", t.getAt());

	}

	@Test
	public void buildValueConstraintTest() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		QueryConstraint constraint = builder.constraint("sumlev",
				builder.value(builder.element("sumlev"),
						builder.fragmentScope(FragmentScope.DOCUMENTS),
						builder.termOption("punctuation-insensitive"),
						builder.weight(4.3)
						));

		QueryValue vc = constraint.getSource();
		options.build(constraint);

		assertEquals(vc.getElement(), new QName("sumlev"));

		String optionsString = options.toXMLString();
		logger.debug(optionsString);

		assertTrue(
				"Serialized ValueOption AbstractQueryOption should contain this string",
				optionsString.contains("name=\"sumlev\""));

	}

	@Test
	public void buildWordConstraintTest() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.constraint("intitle",
				builder.word(builder.field("titlefield"),
						builder.termOption("punctuation-insensitive"),
						builder.weight(2.3)
						)));

		QueryWord wc = options.getConstraint("intitle")
				.getSource();
		assertEquals("titlefield", wc.getFieldName());
		assertEquals(new Double(2.3), wc.getWeight());
		assertEquals("punctuation-insensitive", wc.getTermOptions().get(0));

		String optionsString = options.toXMLString();
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
		options.build(builder.additionalQuery(e));
		optionsString = options.toXMLString();

		logger.debug(optionsString);
		assertTrue("Updated Option not updated from AdditionalQuery",
				optionsString.contains("/oscars2/"));

	}

	@Test
	public void buildOperator() throws FileNotFoundException,
			JAXBException {
		QueryOptionsHandle options = testOptions;

		QueryOperator operatorOption = testOptions.getOperators().get(0);
		logger.debug("OperatorOption found from test config {}", operatorOption);

		QueryState s = (QueryState) operatorOption.getStates().get(0);

		assertEquals("State from operator", "relevance", s.getName());

		QuerySortOrder so = builder.sortOrder("xs:string",
				"http://marklogic.com/collation", Direction.ASCENDING,
				builder.element("http://my/namespace", "green"),
				builder.attribute("http://my/namespace", "pantone"), builder.score());

		options.build(builder.operator("sortcolor", builder.state("pantone", so)));

		String optionsString = options.toXMLString();

		logger.debug("Sort order found from test config {}", optionsString);
		assertTrue("Sort order should contain empty score element",
				optionsString.contains("<search:score></search:score>"));
		assertTrue("Sort order should contain element index def",
				optionsString.contains("name=\"green\""));

		
	}

	@Test
	public void parseAndBuildPropertiesConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder.constraint("props", builder.properties()));

		logger.debug(options.toString());

		mgr.writeOptions("props", options);

		QueryOptionsHandle options2 = mgr.readOptions("props",
				new QueryOptionsHandle());
		assertEquals("Unexpected class from JAXB unmarshalling", options2
				.getConstraints().get(0).getSource()
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
		QueryOptions.Element element = transformResultsOption.getPreferredElements().get(0);
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
		
		// build test
		options = new QueryOptionsHandle();
		options.build(builder.constraint("geoelem", 
				builder.geospatialElement(
						builder.element("ns1", "elementwithcoords"),
						builder.geoOption("type=long-lat-point")
				)));
		options.build(builder.constraint("geoattr",
				builder.geospatialAttributePair(
						builder.element("sf1"),
						builder.attribute("intptlat"),
						builder.attribute("intptlon"),
						builder.facetOption("limit=10"),
						builder.heatmap(23.2, -118.3, 23.3, -118.2, 4, 4))));
		options.build(builder.constraint("geoElemPair",
				builder.geospatialElementPair(
						builder.element("sf1"),
						builder.element("intptlat"),
						builder.element("intptlon"))));
		
		QueryGeospatialElement geoElem =  options.getConstraint("geoelem").getSource();
		assertEquals("GeoConstraint latitude", "ns1", geoElem.getElement().getNamespaceURI());
		
		
		
		mgr.writeOptions("tmp", options);
		
		options = mgr.readOptions("tmp", new QueryOptionsHandle());
		
	}

	@Test
	public void buildQueryElementQuery() throws JAXBException {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.constraint("elementQuery", 
				builder.elementQuery("http://marklogic.com/wikipedia", "title")));
		
		mgr.writeOptions("tmp", options);
		QueryOptionsHandle options2 = mgr.readOptions("tmp",
				new QueryOptionsHandle());
		List<QueryConstraint> l = options2.getConstraints();
		QueryElementQuery eqc = (QueryElementQuery) l.get(0)
				.getSource();

		logger.debug(options2.toString());

		assertEquals("Name attribute doesn't match expected.", "title",
				eqc.getName());
		assertEquals("Namespace attribute doesn't match expected.",
				eqc.getNs(), "http://marklogic.com/wikipedia");
	}

	@Test
	public void buildTextOptions() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		
		options.build(
				builder.returnFacets(true),
				builder.returnMetrics(false),
				builder.additionalQuery("<cts:query xmlns:cts=\"http://marklogic.com/cts\" />"),
				builder.searchableExpression("<e>/sf1</e>"), 
				builder.annotation("<a:note xmlns:a=\"http://marklogic.com/note\">note</a:note>"),
				builder.concurrencyLevel(2),
				builder.debug(true),
				builder.fragmentScope(FragmentScope.PROPERTIES),
				builder.forest(123L),
				builder.forest(235L),
				builder.pageLength(10L),
				builder.qualityWeight(3.4),
				builder.returnAggregates(true),
				builder.returnConstraints(true),
				builder.returnFrequencies(true),
				builder.returnPlan(true),
				builder.returnQtext(true),
				builder.returnQuery(true),
				builder.returnResults(false),
				builder.returnSimilar(true),
				builder.returnValues(false),
				builder.searchOption("limit=10"),
				builder.transformResults("raw"));
		
		assertEquals("builders for facets options", true, options.getReturnFacets());
		assertEquals("builders for metrics options", false, options.getReturnMetrics());
		org.w3c.dom.Element se = options.getSearchableExpression();
		assertEquals("/sf1", se.getTextContent());
		
		logger.debug("here is fragment-scope: " + options.getFragmentScope());
		assertEquals(options.getFragmentScope(), "properties");
		options.setFragmentScope(FragmentScope.DOCUMENTS);
		assertEquals(options.getFragmentScope(), "documents");
	}

	@Test
	public void buildValues() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(
				builder.values("uri", builder.uri(), builder.valuesOption("limit=10")),
				builder.values("coll", builder.collection(false, "prefix"), builder.valuesOption("limit=10")),
				builder.values("persona", builder.range(true, new QName("xs:string"), builder.element("element-test"), builder.attribute("attribute-test"))),
				builder.values("fieldrange", builder.range(false, new QName("xs:string"), builder.field("range")),
						builder.aggregate("median")),
				builder.values("field", builder.field("fieldname")));
				
						
		assertEquals("uri", options.getValues("uri").getName());
		QueryValues collectionValues = options.getValues("coll");
		assertTrue(collectionValues.getValuesOptions().get(0).equals("limit=10"));
		
		assertEquals("element-test", options.getValues("persona").getSource().getElement().getLocalPart());
		
		
	}
	@Test
	public void setReturnFacets() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.returnFacets(true));
		logger.debug("here is return facets: " + options.getReturnFacets());
		assertTrue(options.getReturnFacets());
		options.build(builder.returnFacets(false));
		logger.debug("here is return facets: " + options.getReturnFacets());
		assertTrue(!options.getReturnFacets());
	}

	@Test
	public void testRangeConstraint() {
		QueryConstraint awardConstraint = builder.constraint("award", builder
				.range(false, new QName("xs:string"), DEFAULT_COLLATION,
						builder.bucket("2000s", "2000s", null, null),
						builder.bucket("1990s", "1990s", "1990", "2000"),
						builder.bucket("1980s", "1980s", "1980", "1990"),
						builder.bucket("1970s", "1970s", "1970", "1980"),
						builder.bucket("1960s", "1960s", "1960", "1970"),
						builder.bucket("1950s", "1950s", "1950", "1960"),
						builder.bucket("1940s", "1940s", "1940", "1950"),
						builder.bucket("1930s", "1930s", "1930", "1940"),
						builder.bucket("1920s", "1920s", "1920", "1930"),
						builder.facetOption("limit=10")));

		assertEquals("Getting to bucket from constraint", "1990",
				((QueryRange) awardConstraint.getSource())
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

			Element rootElement = domElement(option);
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

	private Element domElement(QueryOptionsHandle option) {
		return builder.domElement(option.toXMLString());
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

		annotatedOptions.build(builder.annotation("<a:note xmlns:a=\"http://marklogic.com/note\">note</a:note>"));
		
	}
}
