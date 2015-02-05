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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.config.QueryOptions;
import com.marklogic.client.admin.config.QueryOptions.Facets;
import com.marklogic.client.admin.config.QueryOptions.FragmentScope;
import com.marklogic.client.admin.config.QueryOptions.PathIndex;
import com.marklogic.client.admin.config.QueryOptions.QueryCollection;
import com.marklogic.client.admin.config.QueryOptions.QueryCustom;
import com.marklogic.client.admin.config.QueryOptions.QueryElementQuery;
import com.marklogic.client.admin.config.QueryOptions.QueryExtractMetadata;
import com.marklogic.client.admin.config.QueryOptions.QueryGeospatialElement;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner.Comparator;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryStarter;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.Tokenize;
import com.marklogic.client.admin.config.QueryOptions.QueryProperties;
import com.marklogic.client.admin.config.QueryOptions.QueryRange;
import com.marklogic.client.admin.config.QueryOptions.QueryRange.ComputedBucket.AnchorValue;
import com.marklogic.client.admin.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.admin.config.QueryOptions.QuerySortOrder.Direction;
import com.marklogic.client.admin.config.QueryOptions.QueryTerm.TermApply;
import com.marklogic.client.admin.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.admin.config.QueryOptions.QueryTuples;
import com.marklogic.client.admin.config.QueryOptions.QueryValue;
import com.marklogic.client.admin.config.QueryOptions.QueryValues;
import com.marklogic.client.admin.config.QueryOptions.QueryWord;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.io.QueryOptionsHandle;

/* 
 * This test targets the QueryOptionsBuilder.
 * It focuses on validity of generated options and on the ability
 * to use Java to manipulate them.
 * testRootOptions is the entry point for the test, which parallels the 
 * schema search.rnc.
 */
@SuppressWarnings("deprecation")
public class QueryOptionsBuilderTest {

	private QueryOptionsBuilder builder;
	private static XpathEngine xpathEngine;

	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsBuilderTest.class);

	@Before
	public void before() {
		builder = new QueryOptionsBuilder();
		HashMap<String, String> xpathNS = new HashMap<String, String>();
		xpathNS.put("search", "http://marklogic.com/appservices/search");
		SimpleNamespaceContext xpathNsContext = new SimpleNamespaceContext(
				xpathNS);

		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalize(true);
		XMLUnit.setNormalizeWhitespace(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

		xpathEngine = XMLUnit.newXpathEngine();
		xpathEngine.setNamespaceContext(xpathNsContext);

	}

	@Test
	public void testRootOptions()
	throws XpathException, SAXException, IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		testConstraints();
		testValuesAndTuples();
		testOperator();
		testTerm();
		testExtractMetadata();
		testGrammar();
		testSearchableExpression();
		testSortOrder();
		testTransformResults();
		testAdditionalQuery();
		testConfiguration();
		testAnnotations();
	}


	private void testConfiguration()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {

		QueryOptionsHandle options = new QueryOptionsHandle();
		options.withConfiguration(
				builder.configure()
				 .returnFacets(true)
				 .returnMetrics(false)
				 .concurrencyLevel(2)
				 .debug(false)
				 .fragmentScope(FragmentScope.PROPERTIES)
				 .forests(123L, 235L)
				.pageLength(10L)
				.qualityWeight(3.4)
				.returnAggregates(true)
				.returnConstraints(true)
				.returnFrequencies(true)
				.returnPlan(true)
				.returnQtext(true)
				.returnQuery(true)
				.returnResults(false)
				.returnSimilar(true)
				.returnValues(false)
				.searchOptions("checked"));

		logger.debug(options.toString());
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

	private void testAdditionalQuery()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options = new QueryOptionsHandle().withAdditionalQuery(builder
				.additionalQuery("<cts:word-query xmlns:cts=\"http://marklogic.com/cts\"/>"));

		String queryValue = "the one-and-only foo";

		Element aq = options.getAdditionalQuery();
		assertEquals("word-query", aq.getLocalName());
		assertEquals("http://marklogic.com/cts", aq.getNamespaceURI());
		aq.setTextContent(queryValue);

		options.setAdditionalQuery(aq);
		assertTrue(options.getAdditionalQuery().getTextContent()
				.equals(queryValue));
		options = exercise(options);

		aq = options.getAdditionalQuery();
		assertEquals("word-query", aq.getLocalName());
		assertEquals("http://marklogic.com/cts", aq.getNamespaceURI());
		assertTrue(aq.getTextContent().equals(queryValue));
	};

	private void testAnnotations()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options = new QueryOptionsHandle().annotate("<a:note xmlns:a=\"http://marklogic.com/note\">note</a:note>");

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
	private void testConstraints()
	throws XpathException, SAXException, IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		testCollectionConstraint();
		testValueConstraint();
		testRangeConstraint();
		testWordConstraint();
		testElementQueryConstraint();
		testPropertiesConstraint();
		testCustomConstraint();
		testGeospatialConstraint();
	};

	private void testCollectionConstraint()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options = new QueryOptionsHandle();
        options.withConstraints(builder.constraint(
						"collectionConstraint",
						builder.collection("prefix", Facets.UNFACETED, "limit=10", "descending")));

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
	private QueryOptionsHandle exercise(QueryOptionsHandle options)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
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

	private void testValueConstraint()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options = new QueryOptionsHandle();
        options.withConstraints(
						builder.constraint("value",
								builder.value(
										builder.elementTermIndex(
												new QName("value")))));

		QueryValue v = options.getConstraint("value").getSource();
		assertEquals("value", v.getElement().getLocalPart());

		v.setFragmentScope(FragmentScope.DOCUMENTS);
		
		options = exercise(options);
		v = options.getConstraint("value").getSource();
		assertEquals("value", v.getElement().getLocalPart());
		assertEquals(FragmentScope.DOCUMENTS, v.getFragmentScope());
		
		
		
	};

	private void testRangeConstraint()
	throws XpathException, SAXException, IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options = new QueryOptionsHandle();
			options.withConstraints(
					builder.constraint(
							"decade", 
							builder.range(builder.elementAttributeRangeIndex(
									new QName("http://marklogic.com/wikipedia", "nominee"),
									new QName("year"),
									builder.rangeType("xs:gYear")),
									Facets.FACETED,
									FragmentScope.DOCUMENTS,
									builder.buckets(
											builder.bucket("2000s", "2000s", null, null),
											builder.bucket("1990s", "1990s", "1990", "2000"),
											builder.bucket("1980s", "1980s", "1980", "1990"),
											builder.bucket("1970s", "1970s", "1970", "1980"),
											builder.bucket("1960s", "1960s", "1960", "1970"),
											builder.bucket("1950s", "1950s", "1950", "1960"),
											builder.bucket("1940s", "1940s", "1940", "1950"),
											builder.bucket("1930s", "1930s", "1930", "1940"),
											builder.bucket("1920s", "1920s", "1920", "1930")),
									"limit=10")));
		
		
		options = exercise(options);

		QueryRange range1 = (QueryRange) options.getConstraint("decade")
				.getSource();
		assertEquals("2000s",  range1.getBuckets().get(0).getContent());
		
		assertEquals(true, range1.getDoFacets());
		assertEquals("xs:gYear", range1.getType());
		
		String optionsString = options.toString();
        Document doc = XMLUnit.buildControlDocument(optionsString);
        NodeList nl = xpathEngine.getMatchingNodes("//search:range/search:weight", doc);

        // Ranges do not have weights
        assertTrue("Range must not contain a weight", nl.getLength() == 0);
		options = new QueryOptionsHandle();
				options.withConstraints(builder.constraint(
							"date", 
							builder.range(
									builder.elementRangeIndex(
											new QName("http://purl.org/dc/elements/1.1/", "date"), 
											
											builder.rangeType("xs:dateTime")),
											Facets.FACETED,
											FragmentScope.DOCUMENTS,
											builder.buckets(
													builder.computedBucket("older","Older than 1 years", null, "-P365D", AnchorValue.NOW),
													builder.computedBucket("year", "1 month to 1 year ago","-P365D", "-P30D", AnchorValue.NOW), 
													builder.computedBucket("month", "7 to 30 days ago", "-P30D","-P7D", AnchorValue.NOW), 
													builder.computedBucket("week", "1 to 7 days ago", "-P7D","-P1D", AnchorValue.NOW), 
													builder.computedBucket("today", "Today", "-P1D", "P0D",AnchorValue.NOW), 
													builder.computedBucket("future", "Future", "P0D", null, AnchorValue.NOW)),
											"limit=10","descending")));
		
		options = exercise(options);

		assertEquals(AnchorValue.NOW,
				((QueryRange) options.getConstraint("date").getSource())
						.getComputedBuckets().get(0).getAnchorValue());
		
		// one more with a path index
		options = new QueryOptionsHandle();
        		options.withConstraints(
        				builder.constraint("t",
        						builder.range(
        								builder.pathIndex("/doc/para/title", 
        										null, 
        										builder.stringRangeType(QueryOptions.DEFAULT_COLLATION)))))
        				.withSearchableExpression(
        						builder.searchableExpression("/path/to/test"));
		
		QueryRange range =  options.getConstraint("t").getSource();
		assertNull(range.getDoFacets());
		PathIndex pathIndex = range.getPathIndex();
		assertEquals("/doc/para/title", pathIndex.getPath());
		
		
        String xml = options.toString();

        doc = XMLUnit.buildControlDocument(xml);

        String value = xpathEngine.evaluate("/search:options/search:constraint/search:range/search:path-index", doc);

        assertEquals("Path index is correct", "/doc/para/title", value);

        value = xpathEngine.evaluate("/search:options/search:searchable-expression", doc);

        assertEquals("Searchable expression is correct", "/path/to/test", value);
			

	};

	private void testWordConstraint()
	throws SAXException, IOException, XpathException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options = new QueryOptionsHandle();
        options.withConstraints(builder.constraint("word", 
								builder.word(
										builder.fieldTermIndex("summary"))));

		options = exercise(options);

		QueryWord qw = options.getConstraint("word").getSource();

		assertEquals("summary", qw.getFieldName());

		String optionsString = options.toString();
		
        Document doc = XMLUnit.buildControlDocument(optionsString);

        String name = xpathEngine.evaluate("/search:options/search:constraint[@name='word']/search:word/search:field/@name", doc);

        assertTrue("Serialized WordConstraint should contain this string", "summary".equals(name));
	};

	private void testElementQueryConstraint()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {

		QueryOptionsHandle options = new QueryOptionsHandle();
				options.withConstraints(builder.constraint("eq", 
									builder.elementQuery(new QName("http://purl.org/dc/elements/1.1/", "date"))));

		options = exercise(options);

		QueryElementQuery qw = options.getConstraint("eq").getSource();

		assertEquals("date", qw.getName());
	};

	private void testPropertiesConstraint()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options = new QueryOptionsHandle().withConstraints(builder.constraint("props", builder.properties()));

		options = exercise(options);

		QueryProperties props = options.getConstraint("props").getSource();

		assertTrue(props != null);
	};

	private void testCustomConstraint()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options =new QueryOptionsHandle();
				options.withConstraints(builder.constraint("queryCustom", builder.customFacet(
										builder.extension("parse", "http://my/namespace", "/my/parse.xqy"),
										builder.extension("start", "http://my/namespace", "/my/start.xqy"), 
										builder.extension("finish","http://my/namespace", "/my/finish.xqy"), 
										"limit=10"))
										.annotate("<a>annotation</a>"));

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

	private void testGeospatialConstraint()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		
		QueryOptionsHandle options = new QueryOptionsHandle();
			options.withConstraints(
				builder.constraint(
						"geoelem",
						builder.geospatial(
								builder.elementGeospatialIndex(
										new QName("ns1", "elementwithcoords")),
								"type=long-lat-point")),
				builder.constraint(
						"geoattr",
						builder.geospatial(
								builder.attributePairGeospatialIndex(
										new QName("sf1"),
										new QName("intptlat"),
										new QName("intptlon")),
										builder.heatmap(23.2, -118.3, 23.3, -118.2, 4, 4, "limit=10"))),
				builder.constraint(
						"geoElemPair",
						builder.geospatial(
								builder.elementPairGeospatialIndex(new QName("sf1"),
										new QName("intptlat"),
										new QName("intptlon")))),
				builder.constraint(
						"geoElemChild",
						builder.geospatial(
								builder.elementChildGeospatialIndex(
										new QName("sf1"), 
										new QName("latlong")), 
										"type=long-lat-point")));

		QueryGeospatialElement geoElem = options.getConstraint("geoelem")
				.getSource();
		assertEquals("GeoConstraint latitude", "ns1", geoElem.getElement()
				.getNamespaceURI());

		options = exercise(options);
		logger.debug(options.toString());
		
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
	private void testValuesAndTuples()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options =
				new QueryOptionsHandle().withValues(
						builder.values("uri", 
								builder.uri(),
							"limit=10"),
						builder.values("coll",
							builder.collection("prefix"),
							builder.aggregate("min"),
							"limit=10"),
						builder.values(
									"fieldrange",
									builder.range(
											builder.fieldRangeIndex("int1", builder.rangeType("xs:int"))),
									builder.aggregate("stddev")),  // field must exist to pass with an aggregate -- search-impl.xqy 3080 bug? TODO log the bug
						builder.values("field", 
								builder.field("fieldname")))
						.withTuples(
							builder.tuples("persona",
									builder.tupleSources(
											builder.range(builder.elementAttributeRangeIndex(
													new QName("element-test"), new QName("attribute-test"),
													builder.stringRangeType(QueryOptions.DEFAULT_COLLATION))),
											builder.range(
													builder.elementRangeIndex(
															new QName("element-test"), 
															builder.stringRangeType(QueryOptions.DEFAULT_COLLATION))))),
								builder.tuples("cttuple", 
										builder.tupleSources(
												builder.uri(),
												builder.collection("c"))),
								builder.tuples("fields",
										builder.tupleSources(
												builder.field("int1"),
												builder.field("int2"))));
						

		
		assertEquals(4, options.getValues().size());
		assertEquals("uri", options.getValues("uri").getName());
		QueryValues collectionValues = options.getValues("coll");
		assertEquals("min", collectionValues.getAggregate().getApply());
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
		
		logger.debug(options.toString());
		
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

	
	

	/**
	 * Set configuration for extracting metadata from search results.
	 */
	private void testExtractMetadata() {
		QueryOptionsHandle options =  
				new QueryOptionsHandle()
				.withExtractMetadata(builder.extractMetadata(
						builder.constraintValue("constraint-ref"),
						builder.elementValue(
								new QName("elem-ns", "elem-name")),
						builder.jsonValue("json-key")));

		QueryExtractMetadata metadata = options.getExtractMetadata();
		assertEquals("constraint-ref", metadata.getConstraintValues().get(0)
				.getRef());
		assertEquals("elem-ns", metadata.getQNames().get(0).getElemNs());
		assertEquals("json-key", metadata.getJsonKeys().get(0).getName());

	};

	private void testGrammar()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options = 
				new QueryOptionsHandle()
				.withGrammar(builder.grammar(
                        builder.starters(
                            builder.starterGrouping("(", 30, ")"), 
                            builder.starterPrefix( "-", 40, "cts:not-query")), 
                        builder.joiners(
                            builder.joiner( "AND", 20, JoinerApply.PREFIX,  "cts:and-query", Tokenize.WORD), 
                            builder.joiner("OR", 10, JoinerApply.INFIX, "cts:or-query", Tokenize.WORD), 
                            builder.joiner("NEAR", 30, JoinerApply.INFIX, "cts:near-query", Tokenize.WORD), 
                            builder.joiner("NEAR/", 30, JoinerApply.NEAR2, "cts:near-query", null, 2), 
                            builder.joiner( "LT", 50, JoinerApply.CONSTRAINT, Comparator.LT, Tokenize.WORD)),
						"\"",
						Utilities.domElement("<cts:and-query strength=\"20\" xmlns:cts=\"http://marklogic.com/cts\"/>")));
						
		
		QueryGrammar g = options.getGrammar();
		assertEquals("Number of starters", 2, g.getStarters().size());
		assertEquals("Number of joiners", 5, g.getJoiners().size());
		assertEquals("DomElement on grammar", "and-query", g.getImplicit()
				.getLocalName());

		logger.debug(options.toString());
		options = exercise(options);

		g = options.getGrammar();
		QueryStarter s1 = g.getStarters().get(0);
		assertEquals("(", s1.getStarterText());
		assertEquals(30, s1.getStrength());
		assertEquals(")", s1.getDelimiter());
		s1.setStarterText(")");
		s1.setStrength(40);
		s1.setDelimiter("(");
		assertEquals(")", s1.getStarterText());
		assertEquals(40, s1.getStrength());
		assertEquals("(", s1.getDelimiter());

		QueryStarter s2 = g.getStarters().get(1);
		assertEquals("-", s2.getStarterText());
		assertEquals("cts:not-query", s2.getElement());
		s2.setElement("cts:and-query");
		assertEquals("cts:and-query", s2.getElement());

		QueryJoiner j1 = g.getJoiners().get(0);
		assertEquals("AND", j1.getJoinerText());
		assertEquals(20, j1.getStrength());
		assertEquals(JoinerApply.PREFIX, j1.getApply());
		assertEquals("cts:and-query", j1.getElement());
		assertEquals(Tokenize.WORD, j1.getTokenize());
		
		
	};

	private void testOperator() {
		QueryOptionsHandle options = new QueryOptionsHandle()
				.withOperators(builder.operator("sortcolor", 
					builder.state("pantone").withSortOrders(
						builder.sortOrder(
							builder.elementAttributeRangeIndex(
									new QName("http://my/namespace", "green"),
									new QName("http://my/namespace", "pantone"),
									builder.stringRangeType(QueryOptions.DEFAULT_COLLATION)),
									Direction.ASCENDING)),
					builder.state("outthere").withSearchableExpression(
							builder.searchableExpression("/p:sf1",
									builder.ns("p", "http://my.namespace")))));
														
														

		QuerySortOrder so = options.getOperator("sortcolor")
				.getState("pantone").getSortOrders().get(0);
		assertEquals("xs:string", so.getType());
		assertEquals(QueryOptions.DEFAULT_COLLATION, so.getCollation());
		assertEquals(Direction.ASCENDING, so.getDirection());

		so.unsetScore();
		assertNull(so.getScore());
		
		options = new QueryOptionsHandle()
				.withOperators(builder.operator("washington", 
						builder.state("dc").withAdditionalQuery(
								Utilities.domElement(
										"<cts:directory-query xmlns:cts=\"http://marklogic.com/cts\">/washington</cts:directory-query>"))));
		

	};

	
	private void testSearchableExpression() {
		QueryOptionsHandle options = new QueryOptionsHandle().withSearchableExpression(
				builder.searchableExpression("/p:sf1",
				builder.ns("p", "http://my.namespace")));

		String se = options.getSearchableExpression();
		assertEquals("/p:sf1", se);

		NamespaceContext nscontext = options
				.getSearchableExpressionNamespaceContext();

		assertEquals("http://my.namespace", nscontext.getNamespaceURI("p"));

		se = "/p:sf2";
		options.setSearchableExpression(se);
		assertEquals("/p:sf2", options.getSearchableExpression());

	};

	private void testSortOrder()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		QueryOptionsHandle options = 
				new QueryOptionsHandle()
					.withSortOrders(
							builder.sortOrder(
									builder.elementAttributeRangeIndex(
											new QName("http://my/namespace", "green"), 
											new QName("http://my/namespace", "pantone"),
											builder.stringRangeType(QueryOptions.DEFAULT_COLLATION)),
											Direction.ASCENDING),
							builder.sortByScore(Direction.ASCENDING));

		QuerySortOrder so = options.getSortOrders().get(0);
		assertEquals("xs:string", so.getType());
		assertEquals(QueryOptions.DEFAULT_COLLATION, so.getCollation());
		assertEquals(Direction.ASCENDING, so.getDirection());

		so.unsetScore();
		assertNull(so.getScore());
		
		options = exercise(options);
		
		so = options.getSortOrders().get(0);
		assertEquals("xs:string", so.getType());
		

	};

		
	private void testTerm()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {

		QueryOptionsHandle options = 
				new QueryOptionsHandle()
				.withTerm(
						builder.term(TermApply.ALL_RESULTS,
								builder.word(
										builder.elementTermIndex(new QName("nation")))));

		QueryWord word = (QueryWord) options.getTerm().getSource();
		assertEquals("nation", word.getElement().getLocalPart());

		options = exercise(options);

		assertEquals("TermConfig after storing", "nation", options.getTerm()
				.getSource().getElement().getLocalPart());
		
		options = new QueryOptionsHandle();
					options.withConstraints(builder.constraint("nameofconstraint", builder.value(builder.elementTermIndex(new QName("elem")))))
					.withTerm(
							builder.term(TermApply.ALL_RESULTS,
									"nameofconstraint", "punctuation-insensitive"));
		 
		String constraintRef = options.getTerm().getRef();
		assertEquals("nameofconstraint", constraintRef);

		options = exercise(options);

		assertEquals("TermConfig after storing", "nameofconstraint", options.getTerm().getRef());

		
		options = new QueryOptionsHandle()
			.withTerm(builder.term("punctuation-insensitive"));

		options = exercise(options);
		
		assertNull(options.getTerm().getSource());
		assertNull(options.getTerm().getWeight());
		
		assertEquals("TermConfig after storing", "punctuation-insensitive", options.getTerm().getTermOptions().get(0));
		

	};

	private void testTransformResults()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {

		QueryOptionsHandle options = new QueryOptionsHandle()
				.withConfiguration(
						builder.configure().returnMetrics(false)
								.returnQtext(false).debug(true))
				.withTransformResults(builder.rawResults())
				.withConstraints(
						builder.constraint("id", builder.value(builder
								.elementTermIndex(new QName("id")))));
 // build transform results from extension
		
		assertEquals("raw", options.getTransformResults().getApply());
		assertEquals("id", options.getConstraint("id").getName());
		assertNull("no preferred elements", options.getTransformResults().getPreferredElements());
		
		QueryTransformResults tr = options.getTransformResults();
		tr.setAt("x");
		tr.setNs("http://namespace");
		assertEquals("x", options.getTransformResults().getAt());
		assertEquals("http://namespace", options.getTransformResults().getNs());

		logger.debug(options.toString());
		
		options = exercise(options);

		tr = options.getTransformResults();
		assertEquals("x", options.getTransformResults().getAt());
		assertEquals("http://namespace", options.getTransformResults().getNs());
		
		// snippet
		options = new QueryOptionsHandle()
				.withTransformResults(builder.snippetTransform(10, 20, 1000, new QName("ns", "elem")));
		
		assertEquals(10, options.getTransformResults().getPerMatchTokens().intValue());
		assertEquals(20, options.getTransformResults().getMaxMatches().intValue());
		assertEquals(1000, options.getTransformResults().getMaxSnippetChars().intValue());
		assertEquals("elem", options.getTransformResults().getPreferredElements().get(0).getName());
		
		options = exercise(options);
		
		assertEquals(10, options.getTransformResults().getPerMatchTokens().intValue());
		assertEquals(20, options.getTransformResults().getMaxMatches().intValue());
		assertEquals(1000, options.getTransformResults().getMaxSnippetChars().intValue());
		assertEquals("elem", options.getTransformResults().getPreferredElements().get(0).getName());
		
		// raw
		options = new QueryOptionsHandle().withTransformResults(builder.rawResults());
		assertEquals("raw", options.getTransformResults().getApply());
		
		options = exercise(options);
		
		assertEquals("raw", options.getTransformResults().getApply());
		
		// empty
		options = new QueryOptionsHandle().withTransformResults(builder.emptySnippets());
		assertEquals("empty-snippet", options.getTransformResults().getApply());
		
		options = exercise(options);
		
		assertEquals("empty-snippet", options.getTransformResults().getApply());
	
		// metadata snippet
		options = new QueryOptionsHandle()
				.withTransformResults(builder.metadataSnippetTransform(new QName("ns", "elem")));
		
		assertEquals("elem", options.getTransformResults().getPreferredElements().get(0).getName());
		
		options = exercise(options);
		
		assertEquals("elem", options.getTransformResults().getPreferredElements().get(0).getName());
		
	
	}

}
