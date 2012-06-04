package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import com.marklogic.client.config.QueryOptions.FragmentScope;
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;

public class QueryOptionsTest {

	QueryOptionsBuilder builder;

	@Before
	public void before() {
		builder = new QueryOptionsBuilder();
	}

	@Test
	public void testRootOptions() {

		testAdditionalQuery();
		/*
		testAnnotations();
		testConstraint();
		testValues();
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
		*/
		testAtomicOptions();
	}

	private void testAtomicOptions() {

		QueryOptionsHandle options = new QueryOptionsHandle();

		options.build(
				builder.returnFacets(true),
				builder.returnMetrics(false),
				builder.searchableExpression("<e>/sf1</e>"),
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
				builder.returnValues(false), builder.searchOption("limit=10"),
				builder.transformResults("raw"));

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
		assertEquals("SearchOption from build", "limit=10", options
				.getSearchOptions().get(0));
		options.getSearchOptions().set(0, "limit=1000");
		assertTrue("SearchOption() from setter", options.getSearchOptions()
				.get(0).equals("limit=1000"));
		// ? assertEquals("TransformResults from build", "raw",
		// options.getTransformResults()); options.setTransformResults("none");
		// assertEquals("TransformResults(); from setter", "none",
		// options.getTransformResults());
	}

	private void testAdditionalQuery() {
		QueryOptionsHandle options = new QueryOptionsHandle();
		
		options.build(
				builder.additionalQuery("<cts:query xmlns:cts=\"http://marklogic.com/cts\" />")
				);
			
		Element aq = options.getAdditionalQuery();
		assertEquals("query", aq.getLocalName());
		assertEquals("http://marklogic.com/cts", aq.getNamespaceURI());
		aq.setTextContent("some arbitrary query string");
		
		options.setAdditionalQuery(aq);
		assertTrue(options.getAdditionalQuery().getTextContent().equals("some arbitrary query string"));
		
	};

	private void testAnnotations() {
		fail("Not implemented");
	};

	private void testConstraint() {
		fail("Not implemented");
	};

	private void testValues() {
		fail("Not implemented");
	};

	private void testDefaultSuggestionSource() {
		fail("Not implemented");
	};

	private void testExtractMetadata() {
		fail("Not implemented");
	};

	private void testGrammar() {
		fail("Not implemented");
	};

	private void testOperator() {
		fail("Not implemented");
	};

	private void testSearchOption() {
		fail("Not implemented");
	};

	private void testSearchableExpression() {
		fail("Not implemented");
	};

	private void testSortOrder() {
		fail("Not implemented");
	};

	private void testSuggestionSource() {
		fail("Not implemented");
	};

	private void testTerm() {
		fail("Not implemented");
	};

	private void testTransformResults() {
		fail("Not implemented");
	};

}
