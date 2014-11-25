package com.marklogic.client.test.example.extension;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.marklogic.client.example.extension.GraphSPARQLExample;

public class GraphSPARQLExampleTest {

	
	@Test
	public void testMain() {
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");

		boolean succeeded = false;
		try {
			GraphSPARQLExample.main(new String[0]);
			succeeded = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("GraphSPARQLExample failed", succeeded);
	}

}
