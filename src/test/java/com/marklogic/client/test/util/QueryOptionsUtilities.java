package com.marklogic.client.test.util;

import org.w3c.dom.Document;

import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;

/*
 * Methods to assist in testing QueryOptions
 */
public class QueryOptionsUtilities {

	
	public static Document toDocument(QueryOptionsManager mgr, QueryOptionsHandle options) {
		mgr.writeOptions("tmp1234", options);
		return mgr.readOptions("tmp1234", new DOMHandle()).get();
	}
	
	public static String toXMLString(QueryOptionsManager mgr, QueryOptionsHandle options) {
		mgr.writeOptions("tmp1234", options);
		return mgr.readOptions("tmp1234", new StringHandle()).get();
	}
}
