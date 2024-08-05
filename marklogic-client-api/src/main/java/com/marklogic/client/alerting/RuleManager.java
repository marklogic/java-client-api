/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.alerting;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.marker.RuleListReadHandle;
import com.marklogic.client.io.marker.RuleReadHandle;
import com.marklogic.client.io.marker.RuleWriteHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.QueryDefinition;

/**
 * Manages CRUD of rules for the REST API alerting capability, as well as match
 * operations against installed rules.
 */
public interface RuleManager {
  /**
   * Tests for existence of rule on the REST server.
   *
   * @param ruleName	Name of the rule
   * @return true if rule exists, false otherwise.
   */
  boolean exists(String ruleName);

  /**
   * Reads a rule from the server in an XML representation provided
   * as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param ruleName	name of rule on REST server
   * @param as	the IO class for reading the rule
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the rule
   */
  <T> T readRuleAs(String ruleName, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  /**
   * Reads a rule from the server into the provided handle.
   *
   * @param ruleName	Name of rule on REST server.
   * @param readHandle	Handle that will accept the rule payload. Often will be an instance of RuleDefinition.
   * @param <T> the type of RuleReadHandle to return
   * @return Handle or object that models the rule.
   */
  <T extends RuleReadHandle> T readRule(String ruleName, T readHandle)
    throws ResourceNotFoundException, ForbiddenUserException,
    FailedRequestException;

  /**
   * Writes a rule to the server in an XML representation provided
   * as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param ruleName	name of rule on REST server
   * @param ruleSource	an IO representation of the rule
   */
  void writeRuleAs(String ruleName, Object ruleSource)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  /**
   * Writes a rule to the server from the provided handle.
   *
   * @param writeHandle	Handle that contains the rule payload. Must be a RuleDefinition object to use this method, which has no ruleName.
   */
  void writeRule(RuleDefinition writeHandle)
    throws ResourceNotFoundException, ForbiddenUserException,
    FailedRequestException;

  /**
   * Writes a rule to the server from the provided handle.
   *
   * @param ruleName	Name of rule on REST server.
   * @param writeHandle	Handle that contains the rule payload. Often will be an instance of RuleDefinition.
   */
  void writeRule(String ruleName, RuleWriteHandle writeHandle);

  /**
   * Removes a rule from the server.
   *
   * @param ruleName	Name of rule on REST server.
   */
  void delete(String ruleName) throws ForbiddenUserException,
    FailedRequestException;

  /**
   * Matches server rules based on the results of a search.
   * @param docQuery A query definition to qualify documents to match.
   * @param ruleListHandle A handle to hold the match results.
   * @param <T> the type of RuleListReadHandle to return
   * @return The List of rules matched by the documents returned by this query.
   */
  <T extends RuleListReadHandle> T match(QueryDefinition docQuery, T ruleListHandle);

  /**
   * Matches server rules based on results of a search, with pagination applied to search.
   * Use this method to match, say, a page or search results against rules.
   * @param docQuery A query definition to qualify documents to match.
   * @param start The start position in query results to match.
   * @param pageLength The number of results in the filtering query to match. Use null to return all matches.
   * @param candidateRules An array of rule names to return in matches.  Null matches all rules.
   * @param ruleListHandle A handle to hold the match results.
   * @param <T> the type of RuleListReadHandle to return
   * @return The list of rules matched by the documents returned by the query.
   */
  <T extends RuleListReadHandle> T match(QueryDefinition docQuery,
                                         long start, long pageLength, String[] candidateRules, T ruleListHandle);

  /**
   * Matches server rules based on results of a search, with pagination applied to search.
   * Use this method to match, say, a page or search results against rules.
   * @param docQuery A query definition to qualify documents to match.
   * @param start The start position in query results to match.
   * @param pageLength The number of results in the filtering query to match. Use null to return all matches.
   * @param candidateRules An array of rule names to return in matches.  A zero-length array matches all rules.
   * @param ruleListHandle A handle to hold the match results.
   * @param transform	a server transform to modify the rule list payload.
   * @param <T> the type of RuleListReadHandle to return
   * @return The list of rules matched by the documents returned by the query.
   */
  <T extends RuleListReadHandle> T match(QueryDefinition docQuery,
                                         long start, long pageLength, String[] candidateRules, T ruleListHandle,
                                         ServerTransform transform);

  /**
   * Matches server rules based on an array of document IDS.
   * @param docIds An array of document IDs to match against.
   * @param ruleListHandle A handle to hold the match results.
   * @param <T> the type of RuleListReadHandle to return
   * @return The union of all rules matched by the document ids provided.
   */
  <T extends RuleListReadHandle> T match(String[] docIds, T ruleListHandle);

  /**
   * Matches server rules based on an array of document IDs and an array of rule names.
   * @param docIds An array of document IDs to match against.
   * @param candidateRules An array of rule names to return in matches.  A zero-length array matches all rules.
   * @param ruleListHandle A handle to hold the match results
   * @param <T> the type of RuleListReadHandle to return
   * @return The union of rules in candidateRules matched by the document ids provided.
   */
  <T extends RuleListReadHandle> T match(String[] docIds, String[] candidateRules, T ruleListHandle);

  /**
   * Matches server rules based on an array of document IDs and an array of rule names.
   * @param docIds An array of document IDs to match against.
   * @param candidateRules An array of rule names to return in matches.   A zero-length array matches all rules.
   * @param ruleListHandle A handle to hold the match results.
   * @param transform	a server transform to modify the rule list payload.
   * @param <T> the type of RuleListReadHandle to return
   * @return The union of rules in candidateRules matched by the document ids provided.
   */
  <T extends RuleListReadHandle> T match(String[] docIds, String[] candidateRules, T ruleListHandle, ServerTransform transform);

  /**
   * Matches server rules based on a document supplied
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param content	an IO representation of the document to match against rules
   * @param ruleListHandle a handle to hold the match results
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return the union of rules matched by the document provided
   */
  <T extends RuleListReadHandle> T matchAs(Object content, T ruleListHandle);
  /**
   * Matches server rules based on a document supplied
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param content	an IO representation of the document to match against rules
   * @param candidateRules an array of rule names to match.  A zero-length array matches all rules.
   * @param ruleListHandle a handle to hold the match results
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return the union of rules matched by the document provided
   */
  <T extends RuleListReadHandle> T matchAs(Object content, String[] candidateRules,
                                           T ruleListHandle);
  /**
   * Matches server rules based on a document supplied
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param content	an IO representation of the document to match against rules
   * @param candidateRules an array of rule names to match.  A zero-length array matches all rules.
   * @param ruleListHandle a handle to hold the match results
   * @param transform	a server transform to modify the rule list payload
   * @param <T> the type of RuleListReadHandle to return
   * @return the union of rules matched by the document provided
   */
  <T extends RuleListReadHandle> T matchAs(Object content, String[] candidateRules,
                                           T ruleListHandle, ServerTransform transform);

  /**
   * Matches server rules based on a document supplied in a write handle.
   * @param document A document payload to match against rules.
   * @param ruleListHandle A handle to hold the match results.
   * @param <T> the type of RuleListReadHandle to return
   * @return The union of rules matched by the document provided.
   */
  <T extends RuleListReadHandle> T match(StructureWriteHandle document, T ruleListHandle);
  /**
   * Matches server rules based on a document supplied in a write handle.
   * @param document A document payload to match against rules.
   * @param candidateRules An array of rule names to match.  A zero-length array matches all rules.
   * @param ruleListHandle A handle to hold the match results
   * @param <T> the type of RuleListReadHandle to return
   * @return The union of rules in candidateRules matched by the document.
   */
  <T extends RuleListReadHandle> T match(StructureWriteHandle document,
                                         String[] candidateRules, T ruleListHandle);

  /**
   * Matches server rules based on a document supplied in a write handle.
   * @param document A document payload to match against rules.
   * @param candidateRules An array of rule names to match.  A zero-length array matches all rules.
   * @param ruleListHandle A handle to hold the match results.
   * @param transform	a server transform to modify the rule list payload.
   * @param <T> the type of RuleListReadHandle to return
   * @return The union of rules in candidateRules matched by the document.
   */
  <T extends RuleListReadHandle> T match(StructureWriteHandle document,
                                         String[] candidateRules, T ruleListHandle, ServerTransform transform);
}
