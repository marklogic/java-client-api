/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.*;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.SuggestDefinition;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SuggestTest {

  private static String optionsName = "suggest";

  @SuppressWarnings("unused")
  private static final Logger logger = (Logger) LoggerFactory
    .getLogger(SuggestTest.class);

  @AfterAll
  public static void teardown()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.delete("/sample/suggestion.xml");
    docMgr.delete("/sample2/suggestion.xml");

  }

  @BeforeAll
  public static void setup()
    throws FileNotFoundException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
    XMLUnit.setIgnoreWhitespace(true);
    Common.connectRestAdmin();
    writeOptions(Common.restAdminClient);

    Common.restAdminClient.newServerConfigManager().setServerRequestLogging(true);
    Common.connect();

    // write three files for alert tests.
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.write("/sample/suggestion.xml", new StringHandle("<suggest><string>FINDME</string>Something I love to suggest is sugar with savory succulent limes.</suggest>"));
    docMgr.write("/sample2/suggestion.xml", new StringHandle("<suggest>Something I hate to suggest is liver with lard.</suggest>"));

  }


  // case one, zero definition
  @Test
  public void testNoSuggestion() {
    QueryManager queryMgr = Common.client.newQueryManager();
    SuggestDefinition def = queryMgr.newSuggestDefinition(optionsName);

    String[] suggestions = queryMgr.suggest(def);

    assertEquals(suggestions.length, 10);
    assertEquals("string:", suggestions[0]);
    assertEquals("hate", suggestions[1]);
  }

  @Test
  public void testOneSuggestion() {
    QueryManager queryMgr = Common.client.newQueryManager();
    SuggestDefinition def = queryMgr.newSuggestDefinition("l", optionsName);

    String[] suggestions = queryMgr.suggest(def);

    assertEquals(4, suggestions.length);
    assertEquals("lard", suggestions[0]);
    assertEquals("limes", suggestions[1]);
  }

  @Test
  public void testSuggestionWithQuery() {
    QueryManager queryMgr = Common.client.newQueryManager();
    SuggestDefinition def = queryMgr.newSuggestDefinition(optionsName);

    def.setStringCriteria("li");
    def.setQueryStrings("string:FINDME");

    String[] suggestions = queryMgr.suggest(def);

    assertEquals(1, suggestions.length);
    assertEquals("limes", suggestions[0]);
  }

  @Test
  public void testSuggestionWithLimit() {
    QueryManager queryMgr = Common.client.newQueryManager();
    SuggestDefinition def = queryMgr.newSuggestDefinition("li", optionsName);

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
    SuggestDefinition def = queryMgr.newSuggestDefinition("la", optionsName);

    def.setCursorPosition(0);
    String[] suggestions = queryMgr.suggest(def);

    assertEquals(suggestions.length, 10);

    // if cursor position is at the 'a', then we get full suggest.
    def.setCursorPosition(1);
    suggestions = queryMgr.suggest(def);

    assertEquals(suggestions.length, 1);
  }





  private static String writeOptions(DatabaseClient adminClient)
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException
  {
    String optionsName = "suggest";

    String suggestionOptions =
        "            <options xmlns='http://marklogic.com/appservices/search'>"
      + "                <default-suggestion-source>"
      + "                    <word><element ns='' name='suggest'/></word>"
      + "                </default-suggestion-source>"
      + "                <constraint name='string'>"
      + "                    <range type='xs:string' collation='http://marklogic.com/collation/'>"
      + "                        <element ns='' name='string'/>"
      + "                    </range>"
      + "                </constraint>"
      + "            </options>";

    QueryOptionsManager queryOptionsMgr = adminClient.newServerConfigManager().newQueryOptionsManager();

    queryOptionsMgr.writeOptions(optionsName, new StringHandle(
      suggestionOptions));

    return optionsName;
  }


}
