/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.rows;

import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

/**
 * Shared test fixture for {@link FromSearchWithFragmentTest} and
 * {@link FromSearchDocsWithFragmentTest}. Holds the XQuery setup/teardown scripts and
 * expected URI constants so that changes to the test documents only need to be made in
 * one place.
 */
abstract class AbstractFromSearchFragmentTest extends AbstractOpticUpdateTest {

	static final String SETUP_XQUERY =
		"xquery version '1.0-ml';" +
		"let $jsondoc1 := object-node {'AllDataTypes': array-node {object-node {'word':'dog'}, object-node {'rank':1}, object-node {'score':4}}}" +
		"let $jsondoc2 := object-node {'AllDataTypes': array-node {object-node {'word':'cat'}, object-node {'rank':2}, object-node {'score':5}}}" +
		"let $jsondoc3 := object-node {'AllDataTypes': array-node {object-node {'word':'duck'}, object-node {'rank':3}, object-node {'score':6}}}" +
		"return (" +
		"xdmp:document-insert('range-prop-1.json', $jsondoc1, xdmp:default-permissions(), ('elemCol','jsondoc-range','from-search-fragment-test'))," +
		"xdmp:document-insert('range-prop-2.json', $jsondoc2, xdmp:default-permissions(), ('elemCol','jsondoc-range','from-search-fragment-test'))," +
		"xdmp:document-insert('range-prop-3.json', $jsondoc3, xdmp:default-permissions(), ('elemCol','jsondoc-range','from-search-fragment-test'))," +
		"xdmp:document-set-properties('range-prop-1.json', (<my-prop>opticfragmentpropvalue prop1value</my-prop>))," +
		"xdmp:document-set-properties('range-prop-2.json', (<my-prop>opticfragmentpropvalue prop2value</my-prop>))," +
		"xdmp:document-set-properties('range-prop-3.json', (<my-prop>opticfragmentpropvalue prop3value</my-prop>))," +
		"xdmp:lock-acquire('range-prop-1.json', 'exclusive', '0', 'dog rose',  xs:unsignedLong(120))," +
		"xdmp:lock-acquire('range-prop-2.json', 'exclusive', '0', 'cat tulip', xs:unsignedLong(120))," +
		"xdmp:lock-acquire('range-prop-3.json', 'exclusive', '0', 'duck lily', xs:unsignedLong(120))" +
		")";

	static final String TEARDOWN_XQUERY =
		"xquery version '1.0-ml';" +
		"for $uri in ('range-prop-1.json', 'range-prop-2.json', 'range-prop-3.json') return xdmp:document-delete($uri)";

	static final List<String> EXPECTED_URIS = List.of(
		"range-prop-1.json", "range-prop-2.json", "range-prop-3.json");

	@BeforeEach
	void setupTest() {
		rowManager.withUpdate(false);
		Common.newEvalClient().newServerEval().xquery(SETUP_XQUERY).evalAs(String.class);
	}

	@AfterEach
	void teardownTest() {
		Common.newEvalClient().newServerEval().xquery(TEARDOWN_XQUERY).evalAs(String.class);
	}
}
