/*
 * Copyright 2013 MarkLogic Corporation
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.SuggestDefinition;

public class SuggestTest {

	private static String optionsName = "suggest";
	
	@SuppressWarnings("unused")
	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(SuggestTest.class);

	@BeforeClass
	public static void beforeClass() {
		Common.connectAdmin();
		writeOptions();
	}

	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	// case one, zero definition
	@Test
	public void testNoSuggestion() {
		QueryManager queryMgr = Common.client.newQueryManager();
		SuggestDefinition def = queryMgr.newSuggestionDefinition(optionsName);
		
		String[] suggestions = queryMgr.suggest(def);
		
		assertEquals(suggestions.length, 10);
		assertEquals("string:", suggestions[0]);
		assertEquals("hate", suggestions[1]);
	}

	@Test
	public void testOneSuggestion() {
		QueryManager queryMgr = Common.client.newQueryManager();
		SuggestDefinition def = queryMgr.newSuggestionDefinition("l", optionsName);
		
		String[] suggestions = queryMgr.suggest(def);
		
		assertEquals(4, suggestions.length);
		assertEquals("lard", suggestions[0]);
		assertEquals("limes", suggestions[1]);
	}
	
	@Test
	public void testSuggestionWithQuery() {
		QueryManager queryMgr = Common.client.newQueryManager();
		SuggestDefinition def = queryMgr.newSuggestionDefinition(optionsName);
		
		def.setStringCriteria("li");
		def.setQueryStrings("string:FINDME");
		
		String[] suggestions = queryMgr.suggest(def);
		
		assertEquals(1, suggestions.length);
		assertEquals("limes", suggestions[0]);
	}
	
	@Test
	public void testSuggestionWithLimit() {
		QueryManager queryMgr = Common.client.newQueryManager();
		SuggestDefinition def = queryMgr.newSuggestionDefinition("li", optionsName);
		
		def.setLimit(1);
		String[] suggestions = queryMgr.suggest(def);
		
		assertEquals(1, suggestions.length);
		assertEquals("limes", suggestions[0]);
		
		def.setLimit(2);
		suggestions = queryMgr.suggest(def);
		
		assertEquals(2, suggestions.length);
		assertEquals("liver", suggestions[1]);
	}
	
	@Test
	public void testSuggestionWithCursor() {
		QueryManager queryMgr = Common.client.newQueryManager();
		SuggestDefinition def = queryMgr.newSuggestionDefinition("la", optionsName);
		
		def.setCursorPosition(0);
		String[] suggestions = queryMgr.suggest(def);
		
		assertEquals(suggestions.length, 10);
		
		// if cursor position is at the 'a', then we get full suggest.
		def.setCursorPosition(1);
		suggestions = queryMgr.suggest(def);
		
		assertEquals(suggestions.length, 1);
	}
	
	
	
	

	private static String writeOptions() {
		String optionsName = "suggest";

		String suggestionOptions = "            <options xmlns='http://marklogic.com/appservices/search'>"
				+ "                <default-suggestion-source>"
				+ "                    <word><element ns='' name='suggest'/></word>"
				+ "                </default-suggestion-source>"
				+ "                <constraint name='string'>"
				+ "                    <range type='xs:string' collation='http://marklogic.com/collation/'>"
				+ "                        <element ns='' name='string'/>"
				+ "                    </range>"
				+ "                </constraint>" + "            </options>";

		QueryOptionsManager queryOptionsMgr = Common.client
				.newServerConfigManager().newQueryOptionsManager();

		queryOptionsMgr.writeOptions(optionsName, new StringHandle(
				suggestionOptions));

		return optionsName;
	}
}
