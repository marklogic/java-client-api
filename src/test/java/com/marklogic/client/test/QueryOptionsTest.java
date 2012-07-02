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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.ServerConfigurationManager;
import com.marklogic.client.config.QueryOptions.FragmentScope;
import com.marklogic.client.config.QueryOptions.QueryCollection;
import com.marklogic.client.config.QueryOptions.QueryCustom;
import com.marklogic.client.config.QueryOptions.QueryDefaultSuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryElementQuery;
import com.marklogic.client.config.QueryOptions.QueryExtractMetadata;
import com.marklogic.client.config.QueryOptions.QueryGeospatialElement;
import com.marklogic.client.config.QueryOptions.QueryGrammar;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner.Comparator;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryStarter;
import com.marklogic.client.config.QueryOptions.QueryGrammar.Tokenize;
import com.marklogic.client.config.QueryOptions.QueryProperties;
import com.marklogic.client.config.QueryOptions.QueryRange;
import com.marklogic.client.config.QueryOptions.QueryRange.ComputedBucket.AnchorValue;
import com.marklogic.client.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.config.QueryOptions.QuerySortOrder.Direction;
import com.marklogic.client.config.QueryOptions.QuerySortOrder.Score;
import com.marklogic.client.config.QueryOptions.QueryTerm.TermApply;
import com.marklogic.client.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.config.QueryOptions.QueryTuples;
import com.marklogic.client.config.QueryOptions.QueryValue;
import com.marklogic.client.config.QueryOptions.QueryValues;
import com.marklogic.client.config.QueryOptions.QueryWord;
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;

/* 
 * This test targets the QueryOptionsBuilder.
 * testRootOptions is the entry point for the test, which parallels the 
 * schema search.rnc.
 */
public class QueryOptionsTest {

	QueryOptionsBuilder builder;

	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsTest.class);

	@Before
	public void before() {
		builder = new QueryOptionsBuilder();
	}

	@Test
	public void testRootOptions() {

		testAdditionalQuery();

		testAnnotations();
		testConstraints();
		testValuesAndTuples();
		testDefaultSuggestionSource();
		testExtractMetadata();
		testGrammar();
		testOperator();
		testSearchOption();
		testSearchableExpression();
		testSortOrder();
		testSuggestionSource();
		testTerm();
		testTransformResults();

		testAtomicOptions();
	}

	private void testAtomicOptions() {

		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(
				builder.returnFacets(true),
				builder.returnMetrics(false),
				builder.searchableExpression("/a:sf1",
						builder.namespace("a", "http://marklogic.com/a")),
				// builder.annotation("<a:note xmlns:a=\"http://marklogic.com/note\">note</a:note>"),
				builder.concurrencyLevel(2), builder.debug(false),
				builder.fragmentScope(FragmentScope.PROPERTIES),
				builder.forest(123L), builder.forest(235L),
				builder.pageLength(10L), builder.qualityWeight(3.4),
				builder.returnAggregates(true),
				builder.returnConstraints(true),
				builder.returnFrequencies(true), builder.returnPlan(true),
				builder.returnQtext(true), builder.returnQuery(true),
				builder.returnResults(false), builder.returnSimilar(true),
				builder.returnValues(false), builder.searchOption("checked"),
				builder.transformResults("raw"));

		System.out.println(options.toXMLString());
		assertTrue("returnFacets from build", options.getReturnFacets());
		options.setReturnFacets(false);
		assertFalse("returnFacets from setter", options.getReturnFacets());
		assertFalse("ReturnMetrics from build", options.getReturnMetrics());
		options.setReturnMetrics(true);
		assertTrue("returnMetrics from setter", options.getReturnMetrics());
		assertEquals("ConcurrencyLevel from build", 2,
				options.getConcurrencyLevel());
		options.setConcurrencyLevel(16);
		assertEquals("concurrencyLevel from setter", 16,
				options.getConcurrencyLevel());
		assertFalse("Debug from build", options.getDebug());
		options.setDebug(true);
		assertTrue("Debug(); from setter", options.getDebug());
		assertEquals("FragmentScope from build", FragmentScope.PROPERTIES,
				options.getFragmentScope());
		options.setFragmentScope(FragmentScope.DOCUMENTS);
		assertEquals("FragmentScope(); from setter", FragmentScope.DOCUMENTS,
				options.getFragmentScope());
		assertEquals("Forest from build", new Long(123), options.getForests()
				.get(0));
		options.addForest(662L);
		assertEquals("forest from setter", new Long(662), options.getForests()
				.get(2));
		options.getForests().remove(123L);
		options.getForests().remove(662L);
		options.getForests().remove(235L);
		
		assertEquals("PageLength from build", 10L, options.getPageLength());
		options.setPageLength(15L);
		assertEquals("PageLength(); from setter", 15, options.getPageLength());
		assertEquals("QualityWeight from build", 3.4,
				options.getQualityWeight(), 0.0);
		options.setQualityWeight(5.6);
		assertEquals("QualityWeight(); from setter", 5.6,
				options.getQualityWeight(), 0.0);
		assertEquals("ReturnAggregates from build", true,
				options.getReturnAggregates());
		options.setReturnAggregates(false);
		assertFalse("ReturnAggregates(); from setter",
				options.getReturnAggregates());
		assertEquals("ReturnConstraints from build", true,
				options.getReturnConstraints());
		options.setReturnConstraints(false);
		assertFalse("ReturnConstraints(); from setter",
				options.getReturnConstraints());
		assertEquals("ReturnFrequencies from build", true,
				options.getReturnFrequencies());
		options.setReturnFrequencies(false);
		assertFalse("ReturnFrequencies(); from setter",
				options.getReturnFrequencies());
		assertEquals("ReturnPlan from build", true, options.getReturnPlan());
		options.setReturnPlan(false);
		assertFalse("ReturnPlan(); from setter", options.getReturnPlan());
		assertEquals("ReturnQtext from build", true, options.getReturnQtext());
		options.setReturnQtext(false);
		assertFalse("ReturnQtext(); from setter", options.getReturnQtext());
		assertEquals("ReturnQuery from build", true, options.getReturnQuery());
		options.setReturnQuery(false);
		assertFalse("ReturnQuery(); from setter", options.getReturnQuery());
		assertEquals("ReturnResults from build", false,
				options.getReturnResults());
		options.setReturnResults(true);
		assertTrue("ReturnResults(); from setter", options.getReturnResults());
		assertEquals("ReturnSimilar from build", true,
				options.getReturnSimilar());
		options.setReturnSimilar(false);
		assertFalse("ReturnSimilar(); from setter", options.getReturnSimilar());
		assertEquals("ReturnValues from build", false,
				options.getReturnValues());
		options.setReturnValues(true);
		assertTrue("ReturnValues(); from setter", options.getReturnValues());
		assertEquals("SearchOption from build", "checked", options
				.getSearchOptions().get(0));
		options.getSearchOptions().set(0, "unchecked");
		assertTrue("SearchOption() from setter", options.getSearchOptions()
				.get(0).equals("unchecked"));
		
		
		options = exercise(options);
		assertFalse("returnFacets from setter", options.getReturnFacets());
		assertTrue("returnMetrics from setter", options.getReturnMetrics());
		assertEquals("concurrencyLevel from setter", 16,
				options.getConcurrencyLevel());
		assertTrue("Debug(); from setter", options.getDebug());
		assertEquals("FragmentScope(); from setter", FragmentScope.DOCUMENTS,
				options.getFragmentScope());
		assertEquals("PageLength(); from server", 15, options.getPageLength());
		assertEquals("QualityWeight(); from server", 5.6,
				options.getQualityWeight(), 0.0);
		assertFalse("ReturnAggregates(); from server",
				options.getReturnAggregates());
		assertFalse("ReturnConstraints(); from server",
				options.getReturnConstraints());
		assertFalse("ReturnFrequencies(); from server",
				options.getReturnFrequencies());
		assertFalse("ReturnPlan(); from server", options.getReturnPlan());
		assertFalse("ReturnQtext(); from server", options.getReturnQtext());
		assertFalse("ReturnQuery(); from server", options.getReturnQuery());
		assertTrue("ReturnResults(); from server", options.getReturnResults());
		assertFalse("ReturnSimilar(); from server", options.getReturnSimilar());
		assertTrue("ReturnValues(); from server", options.getReturnValues());
		assertTrue("SearchOption() from server", options.getSearchOptions()
				.get(0).equals("unchecked"));
	}

	private void testAdditionalQuery() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder
				.additionalQuery("<cts:query xmlns:cts=\"http://marklogic.com/cts\" />"));

		Element aq = options.getAdditionalQuery();
		assertEquals("query", aq.getLocalName());
		assertEquals("http://marklogic.com/cts", aq.getNamespaceURI());
		aq.setTextContent("some arbitrary query string");

		options.setAdditionalQuery(aq);
		assertTrue(options.getAdditionalQuery().getTextContent()
				.equals("some arbitrary query string"));
		logger.debug(options.toXMLString());
		options = exercise(options);

		aq = options.getAdditionalQuery();
		assertEquals("query", aq.getLocalName());
		assertEquals("http://marklogic.com/cts", aq.getNamespaceURI());
		aq.setTextContent("some arbitrary query string");
	};

	private void testAnnotations() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder
				.annotation("<a:note xmlns:a=\"http://marklogic.com/note\">note</a:note>"));

		Element annotation = options.getAnnotations().get(0).get(0);

		assertEquals("note", annotation.getTextContent());
		assertEquals("note", annotation.getLocalName());
		assertEquals("http://marklogic.com/note",
				annotation.lookupNamespaceURI("a"));

		annotation.setTextContent("changed the note");
		assertEquals("changed the note", options.getAnnotations().get(0).get(0)
				.getTextContent());

		options = exercise(options);
		annotation = options.getAnnotations().get(0).get(0);

		assertEquals("changed the note", options.getAnnotations().get(0).get(0)
				.getTextContent());
	};

	/*
	 * This method tests all types of constraints, including the list of
	 * constraint definition types.
	 */
	private void testConstraints() {
		testCollectionConstraint();
		testValueConstraint();
		testRangeConstraint();
		testWordConstraint();
		testElementQueryConstraint();
		testPropertiesConstraint();
		testCustomConstraint();
		testGeospatialConstraint();
	};

	private void testCollectionConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.constraint(
				"collectionConstraint",
				builder.collection(false, "prefix",
						builder.facetOption("limit=10"),
						builder.facetOption("descending"))));

		QueryCollection coll = options.getConstraint("collectionConstraint")
				.getSource();
		assertEquals("prefix", coll.getPrefix());
		assertEquals("limit=10", coll.getFacetOptions().get(0));

		coll.getFacetOptions().add("unchecked");
		assertEquals("unchecked", coll.getFacetOptions().get(2));

		options = exercise(options);
		coll = options.getConstraint("collectionConstraint").getSource();
		assertEquals("prefix", coll.getPrefix());
		assertEquals("limit=10", coll.getFacetOptions().get(0));

	};

	/*
	 * This function writes options to the REST server instance in order to
	 * check validation and round-tripping.
	 */
	private QueryOptionsHandle exercise(QueryOptionsHandle options) {
		Common.connectAdmin();
		ServerConfigurationManager serverConfig = Common.client
				.newServerConfigManager();

		serverConfig.readConfiguration();

		serverConfig.setQueryOptionValidation(true);
		serverConfig.writeConfiguration();

		QueryOptionsManager mgr = Common.client.newServerConfigManager()
				.newQueryOptionsManager();
		mgr.writeOptions("tmp", options);
		return mgr.readOptions("tmp", new QueryOptionsHandle());

	}

	private void testValueConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.constraint("value",
				builder.value(builder.element("", "value"))));

		QueryValue v = options.getConstraint("value").getSource();
		assertEquals("value", v.getElement().getLocalPart());

		v.setFragmentScope(FragmentScope.DOCUMENTS);
	};

	private void testRangeConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder.constraint("decade", builder.range(true,
				builder.type("xs:gYear"),
				builder.element("http://marklogic.com/wikipedia", "nominee"),
				builder.attribute("year"),
				builder.bucket("2000s", "2000s", null, null),
				builder.bucket("1990s", "1990s", "1990", "2000"),
				builder.bucket("1980s", "1980s", "1980", "1990"),
				builder.bucket("1970s", "1970s", "1970", "1980"),
				builder.bucket("1960s", "1960s", "1960", "1970"),
				builder.bucket("1950s", "1950s", "1950", "1960"),
				builder.bucket("1940s", "1940s", "1940", "1950"),
				builder.bucket("1930s", "1930s", "1930", "1940"),
				builder.bucket("1920s", "1920s", "1920", "1930"),
				builder.facetOption("limit=10"))));

		options = exercise(options);

		assertEquals("2000s", ((QueryRange) options.getConstraint("decade")
				.getSource()).getBuckets().get(0).getContent());

		options = new QueryOptionsHandle();
		options.build(builder.constraint("date", builder.range(false,
				new QName("xs:dateTime"), builder.computedBucket("older",
						"Older than 1 years", null, "-P365D", AnchorValue.NOW),
				builder.computedBucket("year", "1 month to 1 year ago",
						"-P365D", "-P30D", AnchorValue.NOW), builder
						.computedBucket("month", "7 to 30 days ago", "-P30D",
								"-P7D", AnchorValue.NOW), builder
						.computedBucket("week", "1 to 7 days ago", "-P7D",
								"-P1D", AnchorValue.NOW), builder
						.computedBucket("today", "Today", "-P1D", "P0D",
								AnchorValue.NOW), builder.computedBucket(
						"future", "Future", "P0D", null, AnchorValue.NOW),
				builder.facetOption("limit=10"), builder
						.facetOption("descending"), builder.element(
						"http://purl.org/dc/elements/1.1/", "date"))));

		logger.debug(options.toXMLString());
		options = exercise(options);

		assertEquals(AnchorValue.NOW,
				((QueryRange) options.getConstraint("date").getSource())
						.getComputedBuckets().get(0).getAnchorValue());

	};

	private void testWordConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder.constraint("word",
				builder.word(builder.field("summary"))));

		options = exercise(options);

		QueryWord qw = options.getConstraint("word").getSource();

		assertEquals("summary", qw.getFieldName());

	};

	private void testElementQueryConstraint() {

		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder.constraint("eq", builder.elementQuery(
				"http://purl.org/dc/elements/1.1/", "date")));

		options = exercise(options);

		QueryElementQuery qw = options.getConstraint("eq").getSource();

		assertEquals("date", qw.getName());
	};

	private void testPropertiesConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder.constraint("props", builder.properties()));

		options = exercise(options);

		QueryProperties props = options.getConstraint("props").getSource();

		assertTrue(props != null);
	};

	private void testCustomConstraint() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder.constraint("queryCustom", builder.customFacet(
				builder.parse("parse", "http://my/namespace", "/my/parse.xqy"),
				builder.startFacet("start", "http://my/namespace",
						"/my/start.xqy"), builder.finishFacet("finish",
						"http://my/namespace", "/my/finish.xqy"), builder
						.facetOption("limit=10"), builder
						.termOption("punctuation-insensitive"), builder
						.annotation("<a>annotation</a>"))));

		QueryCustom queryCustom = (QueryCustom) options.getConstraint(
				"queryCustom").getSource();
		assertEquals("queryCustom constraint builder", "parse", queryCustom
				.getParse().getApply());
		assertEquals("queryCustom constraint builder", "/my/finish.xqy",
				queryCustom.getFinishFacet().getAt());
		assertEquals("queryCustom constraint builder", "http://my/namespace",
				queryCustom.getStartFacet().getNs());

		options = exercise(options);

		assertEquals("queryCustom constraint builder", "parse", queryCustom
				.getParse().getApply());
		assertEquals("queryCustom constraint builder", "/my/finish.xqy",
				queryCustom.getFinishFacet().getAt());
		assertEquals("queryCustom constraint builder", "http://my/namespace",
				queryCustom.getStartFacet().getNs());

	};

	private void testGeospatialConstraint() {
		
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.constraint(
				"geoelem",
				builder.geospatialElement(
						builder.element("ns1", "elementwithcoords"),
						builder.geoOption("type=long-lat-point"))));
		options.build(builder.constraint(
				"geoattr",
				builder.geospatialAttributePair(builder.element("sf1"),
						builder.attribute("intptlat"),
						builder.attribute("intptlon"),
						builder.facetOption("limit=10"),
						builder.heatmap(23.2, -118.3, 23.3, -118.2, 4, 4))));
		options.build(builder.constraint(
				"geoElemPair",
				builder.geospatialElementPair(builder.element("sf1"),
						builder.element("intptlat"),
						builder.element("intptlon"))));

		QueryGeospatialElement geoElem = options.getConstraint("geoelem")
				.getSource();
		assertEquals("GeoConstraint latitude", "ns1", geoElem.getElement()
				.getNamespaceURI());

		options = exercise(options);
		geoElem = options.getConstraint("geoelem")
				.getSource();
		assertEquals("GeoConstraint latitude", "ns1", geoElem.getElement()
				.getNamespaceURI());
		
	};

	/*
	 * scatter test of various values nodes. see constraint methods for
	 * comprehensive coverage of range, value, etc. This method is comprehensive
	 * for values-specific methods
	 */
	private void testValuesAndTuples() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.values("uri", builder.uri(),
							builder.valuesOption("limit=10")),
						builder.values("coll",
							builder.collection(false, "prefix"),
							builder.valuesOption("limit=10")), 
						builder.tuples("persona",
							builder.range(true, new QName("xs:string"),
								builder.element("element-test"),
								builder.attribute("attribute-test")
								),
							builder.range(false, new QName("xs:string"), builder.element("element-test"))), 
						builder.tuples("cttuple", 
							builder.uri(),
							builder.collection(false, "c")),
						builder.tuples("fields",
							builder.field("int1"),
							builder.field("int2")),
						builder.values(
								"fieldrange",
								builder.range(false, new QName("xs:int"),
								builder.field("int1")), builder.aggregate("median")),  // field must exist to pass with an aggregate -- search-impl.xqy 3080 bug?
						
						builder.values("field", builder.field("fieldname")));

		logger.debug(options.toXMLString());
		
		assertEquals(4, options.getValues().size());
		assertEquals("uri", options.getValues("uri").getName());
		QueryValues collectionValues = options.getValues("coll");
		assertTrue(collectionValues.getValuesOptions().get(0)
				.equals("limit=10"));
		
		assertEquals(3, options.getTuples().size());
		QueryTuples tuples = options.getTuples("persona");
		assertEquals("attribute-test", tuples.getRange().get(0)
				.getAttribute().getLocalPart());
		assertEquals("element-test", tuples.getRange().get(1)
				.getElement().getLocalPart());
		assertNotNull(options.getTuples("cttuple").getCollection());
		assertNotNull(options.getTuples("cttuple").getUri());
		assertEquals("int1", options.getTuples("fields").getField().get(0).getName());
		assertEquals("int2", options.getTuples("fields").getField().get(1).getName());
		
		options = exercise(options);
		assertEquals(3, options.getTuples().size());
		
		tuples = options.getTuples("persona");
		assertEquals("attribute-test", tuples.getRange().get(0)
				.getAttribute().getLocalPart());
		assertEquals("element-test", tuples.getRange().get(1)
				.getElement().getLocalPart());
		assertNotNull(options.getTuples("cttuple").getCollection());
		assertNotNull(options.getTuples("cttuple").getUri());
		assertEquals("int1", options.getTuples("fields").getField().get(0).getName());
		assertEquals("int2", options.getTuples("fields").getField().get(1).getName());

		collectionValues.setName("coll3");
		options.addValues(collectionValues);
		assertEquals(5, options.getValues().size());

	};

	
	/*
	 * this test targets default Suggestion Source and wordLexicon
	 * testSuggestionSource targets comprehensive definition of
	 * default-suggestion-source and suggestion-source children.
	 */
	private void testDefaultSuggestionSource() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder.defaultSuggestionSource(builder.wordLexicon(
				"http://marklogic.com/collation/", FragmentScope.DOCUMENTS)));

		QueryDefaultSuggestionSource dss = options.getDefaultSuggestionSource();
		assertEquals("http://marklogic.com/collation/", dss.getWordLexicon()
				.getCollation());
		assertEquals(FragmentScope.DOCUMENTS, dss.getWordLexicon()
				.getFragmentScope());

		dss.getWordLexicon().setFragmentScope(FragmentScope.PROPERTIES);
		assertEquals(FragmentScope.PROPERTIES, options
				.getDefaultSuggestionSource().getWordLexicon()
				.getFragmentScope());

	};

	/**
	 * Set configuration for extracting metadata from search results.
	 */
	private void testExtractMetadata() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.extractMetadata(
				builder.constraintValue("constraint-ref"),
				builder.qname("elem-ns", "elem-name", null, null),
				builder.jsonkey("json-key")));

		QueryExtractMetadata metadata = options.getExtractMetadata();
		assertEquals("constraint-ref", metadata.getConstraintValues().get(0)
				.getRef());
		assertEquals("elem-ns", metadata.getQNames().get(0).getElemNs());
		assertEquals("json-key", metadata.getJsonKeys().get(0).getName());

	};

	private void testGrammar() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.grammar(
				builder.implicit("<cts:and-query strength=\"20\" xmlns:cts=\"http://marklogic.com/cts\"/>"),
				builder.quotation("\""),
				builder.starterGrouping("(", 30, ")"), builder.starterPrefix(
						"-", 40, new QName("cts:not-query")), builder.joiner(
						"AND", 20, JoinerApply.PREFIX, new QName(
								"cts:and-query"), Tokenize.WORD), builder
						.joiner("OR", 10, JoinerApply.INFIX, new QName(
								"cts:or-query"), Tokenize.WORD), builder
						.joiner("NEAR", 30, JoinerApply.INFIX, new QName(
								"cts:near-query"), Tokenize.WORD), builder
						.joiner("NEAR/", 30, JoinerApply.NEAR2, new QName(
								"cts:near-query"), null, 2), builder.joiner(
						"LT", 50, JoinerApply.CONSTRAINT, Comparator.LT,
						Tokenize.WORD)));

		QueryGrammar g = options.getGrammar();
		assertEquals("Number of starters", 2, g.getStarters().size());
		assertEquals("Number of joiners", 5, g.getJoiners().size());
		assertEquals("DomElement on grammar", "and-query", g.getImplicit()
				.getLocalName());

		QueryStarter s1 = g.getStarters().get(0);
		assertEquals("(", s1.getStarterText());
		assertEquals(30, s1.getStrength());
		assertEquals(")", s1.getDelimiter());
		s1.setText(")");
		s1.setStrength(40);
		s1.setDelimiter("(");
		assertEquals(")", s1.getStarterText());
		assertEquals(40, s1.getStrength());
		assertEquals("(", s1.getDelimiter());

		QueryStarter s2 = g.getStarters().get(1);
		assertEquals("-", s2.getStarterText());
		assertEquals(new QName("cts:not-query"), s2.getElement());
		s2.setElement(new QName("cts:and-query"));
		assertEquals(new QName("cts:and-query"), s2.getElement());

		QueryJoiner j1 = g.getJoiners().get(0);
		assertEquals("AND", j1.getJoinerText());
		assertEquals(20, j1.getStrength());
		assertEquals(JoinerApply.PREFIX, j1.getApply());
		assertEquals(new QName("cts:and-query"), j1.getElement());
		assertEquals(Tokenize.WORD, j1.getTokenize());

	};

	private void testOperator() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.operator("sortcolor", builder.state("pantone",
				builder.sortOrder("xs:string",
						"http://marklogic.com/collation", Direction.ASCENDING,
						builder.element("http://my/namespace", "green"),
						builder.attribute("http://my/namespace", "pantone"),
						builder.score()))));

		QuerySortOrder so = options.getOperator("sortcolor")
				.getState("pantone").getSortOrders().get(0);
		assertEquals(new QName("xs:string"), so.getType());
		assertEquals("http://marklogic.com/collation", so.getCollation());
		assertEquals(Direction.ASCENDING, so.getDirection());
		assertEquals(Score.YES, so.getScore());

		so.unsetScore();
		assertNull(so.getScore());

	};

	private void testSearchOption() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.searchOption("limit=10"),
				builder.searchOption("option1"));

		assertTrue(options.getSearchOptions().get(0).equals("limit=10"));
		options.getSearchOptions().add("newoption");
		assertTrue(options.getSearchOptions().get(2).equals("newoption"));

	};

	private void testSearchableExpression() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.searchableExpression("/p:sf1",
				builder.namespace("p", "http://my.namespace")));

		String se = options.getSearchableExpression();
		assertEquals("/p:sf1", se);

		NamespaceContext nscontext = options
				.getSearchableExpressionNamespaceContext();

		assertEquals("http://my.namespace", nscontext.getNamespaceURI("p"));

		se = "/p:sf2";
		options.setSearchableExpression(se);
		assertEquals("/p:sf2", options.getSearchableExpression());

	};

	private void testSortOrder() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		options.build(builder.sortOrder("xs:string",
				"http://marklogic.com/collation", Direction.ASCENDING,
				builder.element("http://my/namespace", "green"),
				builder.attribute("http://my/namespace", "pantone"),
				builder.score()));

		QuerySortOrder so = options.getSortOrders().get(0);
		assertEquals(new QName("xs:string"), so.getType());
		assertEquals("http://marklogic.com/collation", so.getCollation());
		assertEquals(Direction.ASCENDING, so.getDirection());
		assertEquals(Score.YES, so.getScore());

		so.unsetScore();
		assertNull(so.getScore());

	};

	private void testSuggestionSource() {
		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(builder.suggestionSource(builder.range(false, new QName(
				"gs:year"), builder.element("http://marklogic.com/wikipedia",
				"nominee"), builder.attribute("year")), builder
				.suggestionSourceOption("suggestionOption"), builder
				.suggestionSourceOption("suggestionOption2")), builder
				.suggestionSource("ref"));

		assertEquals(new QName("gs:year"),
				((QueryRange) options.getSuggestionSources().get(0)
						.getConstraintConfiguration()).getType());
	};

	private void testTerm() {

		QueryOptionsHandle options = new QueryOptionsHandle().build(builder
				.term(TermApply.ALL_RESULTS,
						builder.word(builder.element("nation"))));

		QueryWord word = (QueryWord) options.getTerm().getSource();
		assertEquals("nation", word.getElement().getLocalPart());

		options = exercise(options);

		assertEquals("TermConfig after storing", "nation", options.getTerm()
				.getSource().getElement().getLocalPart());

	};

	private void testTransformResults() {

		QueryOptionsHandle handle = new QueryOptionsHandle();
		handle.build(builder.returnMetrics(false), builder.returnQtext(false),
				builder.debug(true), builder.transformResults("raw"),
				builder.constraint("id", builder.value(builder.element("id"))));

		assertEquals("raw", handle.getTransformResults().getApply());
		assertEquals("id", handle.getConstraint("id").getName());

		QueryTransformResults tr = handle.getTransformResults();
		tr.setAt("x");
		tr.setNs("http://namespace");
		assertEquals("x", handle.getTransformResults().getAt());
		assertEquals("http://namespace", handle.getTransformResults().getNs());

	}

}
