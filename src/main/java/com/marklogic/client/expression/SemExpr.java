/*
 * Copyright 2016-2017 MarkLogic Corporation
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
package com.marklogic.client.expression;

import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsDateTimeExpr;
import com.marklogic.client.type.XsDateTimeVal;
import com.marklogic.client.type.XsDoubleExpr;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.SemBlankExpr;
import com.marklogic.client.type.SemBlankSeqExpr;
import com.marklogic.client.type.SemInvalidExpr;
import com.marklogic.client.type.SemInvalidSeqExpr;
import com.marklogic.client.type.SemIriExpr;
import com.marklogic.client.type.SemIriSeqExpr;
import com.marklogic.client.type.SemStoreExpr;
import com.marklogic.client.type.SemStoreSeqExpr;
import com.marklogic.client.type.SemUnknownExpr;
import com.marklogic.client.type.SemUnknownSeqExpr;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the sem server library for a row
 * pipeline and constructs client values with sem.* server types.
 */
public interface SemExpr extends SemValue {
    /**
  * This function returns an identifier for a blank node, allowing the construction of a triple that refers to a blank node. This XQuery function backs up the SPARQL BNODE() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:bnode" target="mlserverdoc">sem:bnode</a>
  * @return  a SemBlankExpr expression
  */
  public SemBlankExpr bnode();
/**
  * This function returns an identifier for a blank node, allowing the construction of a triple that refers to a blank node. This XQuery function backs up the SPARQL BNODE() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:bnode" target="mlserverdoc">sem:bnode</a>
  * @param value  If provided, the same blank node identifier is returned for the same argument value passed to the function.
  * @return  a SemBlankExpr expression
  */
  public SemBlankExpr bnode(XsAnyAtomicTypeExpr value);
/**
  * Returns the value of the first argument that evaluates without error. This XQuery function backs up the SPARQL COALESCE() functional form. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:coalesce" target="mlserverdoc">sem:coalesce</a>
  * @param parameter1  A value.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr coalesce(ItemExpr... parameter1);
/**
  * Returns the name of the simple type of the atomic value argument as a SPARQL style IRI. If the value is derived from sem:unknown or sem:invalid, the datatype IRI part of those values is returned. This XQuery function backs up the SPARQL datatype() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:datatype" target="mlserverdoc">sem:datatype</a>
  * @param value  The value to return the type of.
  * @return  a SemIriExpr expression
  */
  public SemIriExpr datatype(XsAnyAtomicTypeExpr value);
/**
  * The IF function form evaluates the first argument, interprets it as a effective boolean value, then returns the value of expression2 if the EBV is true, otherwise it returns the value of expression3. Only one of expression2 and expression3 is evaluated. If evaluating the first argument raises an error, then an error is raised for the evaluation of the IF expression. This XQuery function backs up the SPARQL IF() functional form. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:if" target="mlserverdoc">sem:if</a>
  * @param condition  The condition.
  * @param then  The then expression.
  * @param elseExpr  The else expression.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr ifExpr(XsBooleanExpr condition, ItemSeqExpr then, ItemSeqExpr elseExpr);
/**
  * Returns a sem:invalid value with the given literal value and datatype IRI. The sem:invalid type extends xs:untypedAtomic, and represents an RDF value whose literal string is invalid according to the schema for it's datatype. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:invalid" target="mlserverdoc">sem:invalid</a>
  * @param string  The lexical value.
  * @param datatype  The datatype IRI.
  * @return  a SemInvalidExpr expression
  */
  public SemInvalidExpr invalid(XsStringExpr string, String datatype);
/**
  * Returns a sem:invalid value with the given literal value and datatype IRI. The sem:invalid type extends xs:untypedAtomic, and represents an RDF value whose literal string is invalid according to the schema for it's datatype. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:invalid" target="mlserverdoc">sem:invalid</a>
  * @param string  The lexical value.
  * @param datatype  The datatype IRI.
  * @return  a SemInvalidExpr expression
  */
  public SemInvalidExpr invalid(XsStringExpr string, SemIriExpr datatype);
/**
  * Returns the datatype IRI of a sem:invalid value. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:invalid-datatype" target="mlserverdoc">sem:invalid-datatype</a>
  * @param val  The sem:invalid value.
  * @return  a SemIriExpr expression
  */
  public SemIriExpr invalidDatatype(SemInvalidExpr val);
/**
  * This is a constructor function that takes a string and constructs an item of type sem:iri from it.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:iri" target="mlserverdoc">sem:iri</a>
  * @param stringIri  The string with which to construct the sem:iri.
  * @return  a SemIriExpr expression
  */
  public SemIriExpr iri(ItemSeqExpr stringIri);
public XsQNameExpr iriToQName(XsStringExpr arg1);
/**
  * Returns true if the argument is an RDF blank node - that is, derived from type sem:blank. This XQuery function backs up the SPARQL isBlank() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:isBlank" target="mlserverdoc">sem:isBlank</a>
  * @param value  The value to test.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr isBlank(XsAnyAtomicTypeExpr value);
/**
  * Returns true if the argument is an RDF IRI - that is, derived from type sem:iri, but not derived from type sem:blank. This XQuery function backs up the SPARQL isIRI() and isURI() functions. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:isIRI" target="mlserverdoc">sem:isIRI</a>
  * @param value  The value to test.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr isIRI(XsAnyAtomicTypeExpr value);
/**
  * Returns true if the argument is an RDF literal - that is, derived from type xs:anyAtomicType, but not derived from type sem:iri. This XQuery function backs up the SPARQL isLiteral() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:isLiteral" target="mlserverdoc">sem:isLiteral</a>
  * @param value  The value to test.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr isLiteral(XsAnyAtomicTypeExpr value);
/**
  * Returns true if the argument is a valid numeric RDF literal. This XQuery function backs up the SPARQL isNumeric() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:isNumeric" target="mlserverdoc">sem:isNumeric</a>
  * @param value  The value to test.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr isNumeric(XsAnyAtomicTypeExpr value);
/**
  * Returns the language of the value passed in, or the empty string if the value has no language. Only values derived from rdf:langString have a language. This XQuery function backs up the SPARQL lang() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:lang" target="mlserverdoc">sem:lang</a>
  * @param value  The value to return the language of.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr lang(XsAnyAtomicTypeExpr value);
/**
  * Returns true if lang-tag matches lang-range according to the basic filtering scheme defined in RFC4647. This XQuery function backs up the SPARQL langMatches() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:langMatches" target="mlserverdoc">sem:langMatches</a>
  * @param langTag  The language tag.
  * @param langRange  The language range.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr langMatches(XsStringExpr langTag, String langRange);
/**
  * Returns true if lang-tag matches lang-range according to the basic filtering scheme defined in RFC4647. This XQuery function backs up the SPARQL langMatches() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:langMatches" target="mlserverdoc">sem:langMatches</a>
  * @param langTag  The language tag.
  * @param langRange  The language range.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr langMatches(XsStringExpr langTag, XsStringExpr langRange);
public SemIriExpr QNameToIri(XsQNameExpr arg1);
/**
  * Returns a random double between 0 and 1. This XQuery function backs up the SPARQL RAND() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:random" target="mlserverdoc">sem:random</a>
  * @return  a XsDoubleExpr expression
  */
  public XsDoubleExpr random();
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a>
  * @param locations  The locations of the rulesets.
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr rulesetStore(String locations);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a>
  * @param locations  The locations of the rulesets.
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr rulesetStore(XsStringSeqVal locations);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a>
  * @param locations  The locations of the rulesets.
  * @param store  The base store(s) over which to apply the ruleset to get inferred triples. The default for sem:store is an empty sequence, which means accessing the current database's triple index using the default rulesets configured for that database.
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr rulesetStore(String locations, SemStoreExpr... store);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a>
  * @param locations  The locations of the rulesets.
  * @param store  The base store(s) over which to apply the ruleset to get inferred triples. The default for sem:store is an empty sequence, which means accessing the current database's triple index using the default rulesets configured for that database.
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr rulesetStore(XsStringSeqVal locations, SemStoreSeqExpr store);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a>
  * @param locations  The locations of the rulesets.
  * @param store  The base store(s) over which to apply the ruleset to get inferred triples. The default for sem:store is an empty sequence, which means accessing the current database's triple index using the default rulesets configured for that database.
  * @param options  Options as a sequence of string values. Available options are:  "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. 
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr rulesetStore(String locations, SemStoreSeqExpr store, String options);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a>
  * @param locations  The locations of the rulesets.
  * @param store  The base store(s) over which to apply the ruleset to get inferred triples. The default for sem:store is an empty sequence, which means accessing the current database's triple index using the default rulesets configured for that database.
  * @param options  Options as a sequence of string values. Available options are:  "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. 
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr rulesetStore(XsStringSeqVal locations, SemStoreSeqExpr store, XsStringSeqVal options);
/**
  * Returns true if the arguments are the same RDF term as defined by the RDF concepts specification. This XQuery function backs up the SPARQL sameTerm() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:sameTerm" target="mlserverdoc">sem:sameTerm</a>
  * @param a  The first value to test.
  * @param b  The second value to test.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr sameTerm(XsAnyAtomicTypeExpr a, String b);
/**
  * Returns true if the arguments are the same RDF term as defined by the RDF concepts specification. This XQuery function backs up the SPARQL sameTerm() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:sameTerm" target="mlserverdoc">sem:sameTerm</a>
  * @param a  The first value to test.
  * @param b  The second value to test.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr sameTerm(XsAnyAtomicTypeExpr a, XsAnyAtomicTypeExpr b);
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a>
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr store();
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a>
  * @param options  Options as a sequence of string values. Available options are:  "any" Values from any fragment should be included. "document" Values from document fragments should be included. "properties" Values from properties fragments should be included. "locks" Values from locks fragments should be included. "checked" Word positions should be checked when resolving the query. "unchecked" Word positions should not be checked when resolving the query. "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. "no-default-rulesets" Don't apply the database's default rulesets to the sem:store. "locking=read-write/write" read-write: Read-lock documents containing triples being accessed, write-lock documents being updated; write: Only write-lock documents being updated. Default is locking=read-write. Locking is ignored in query transaction.  
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr store(String options);
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a>
  * @param options  Options as a sequence of string values. Available options are:  "any" Values from any fragment should be included. "document" Values from document fragments should be included. "properties" Values from properties fragments should be included. "locks" Values from locks fragments should be included. "checked" Word positions should be checked when resolving the query. "unchecked" Word positions should not be checked when resolving the query. "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. "no-default-rulesets" Don't apply the database's default rulesets to the sem:store. "locking=read-write/write" read-write: Read-lock documents containing triples being accessed, write-lock documents being updated; write: Only write-lock documents being updated. Default is locking=read-write. Locking is ignored in query transaction.  
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr store(XsStringSeqVal options);
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a>
  * @param options  Options as a sequence of string values. Available options are:  "any" Values from any fragment should be included. "document" Values from document fragments should be included. "properties" Values from properties fragments should be included. "locks" Values from locks fragments should be included. "checked" Word positions should be checked when resolving the query. "unchecked" Word positions should not be checked when resolving the query. "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. "no-default-rulesets" Don't apply the database's default rulesets to the sem:store. "locking=read-write/write" read-write: Read-lock documents containing triples being accessed, write-lock documents being updated; write: Only write-lock documents being updated. Default is locking=read-write. Locking is ignored in query transaction.  
  * @param query  Only include triples in fragments selected by the cts:query. The triples do not need to match the query, but they must occur in fragments selected by the query. The fragments are not filtered to ensure they match the query, but instead selected in the same manner as  "unfiltered" cts:search operations. If a string is entered, the string is treated as a cts:word-query of the specified string.
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr store(String options, CtsQueryExpr query);
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a>
  * @param options  Options as a sequence of string values. Available options are:  "any" Values from any fragment should be included. "document" Values from document fragments should be included. "properties" Values from properties fragments should be included. "locks" Values from locks fragments should be included. "checked" Word positions should be checked when resolving the query. "unchecked" Word positions should not be checked when resolving the query. "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. "no-default-rulesets" Don't apply the database's default rulesets to the sem:store. "locking=read-write/write" read-write: Read-lock documents containing triples being accessed, write-lock documents being updated; write: Only write-lock documents being updated. Default is locking=read-write. Locking is ignored in query transaction.  
  * @param query  Only include triples in fragments selected by the cts:query. The triples do not need to match the query, but they must occur in fragments selected by the query. The fragments are not filtered to ensure they match the query, but instead selected in the same manner as  "unfiltered" cts:search operations. If a string is entered, the string is treated as a cts:word-query of the specified string.
  * @return  a SemStoreExpr expression
  */
  public SemStoreExpr store(XsStringSeqVal options, CtsQueryExpr query);
/**
  * Returns the timezone of an xs:dateTime value as a string. This XQuery function backs up the SPARQL TZ() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:timezone-string" target="mlserverdoc">sem:timezone-string</a>
  * @param value  The dateTime value
  * @return  a XsStringExpr expression
  */
  public XsStringExpr timezoneString(XsDateTimeExpr value);
/**
  * Returns a value to represent the RDF typed literal with lexical value value and datatype IRI datatype. Returns a value of type sem:unknown for datatype IRIs for which there is no schema, and a value of type sem:invalid for lexical values which are invalid according to the schema for the given datatype. This XQuery function backs up the SPARQL STRDT() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:typed-literal" target="mlserverdoc">sem:typed-literal</a>
  * @param value  The lexical value.
  * @param datatype  The datatype IRI.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr typedLiteral(XsStringExpr value, String datatype);
/**
  * Returns a value to represent the RDF typed literal with lexical value value and datatype IRI datatype. Returns a value of type sem:unknown for datatype IRIs for which there is no schema, and a value of type sem:invalid for lexical values which are invalid according to the schema for the given datatype. This XQuery function backs up the SPARQL STRDT() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:typed-literal" target="mlserverdoc">sem:typed-literal</a>
  * @param value  The lexical value.
  * @param datatype  The datatype IRI.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr typedLiteral(XsStringExpr value, SemIriExpr datatype);
/**
  * Returns a sem:unknown value with the given literal value and datatype IRI. The sem:unknown type extends xs:untypedAtomic, and represents an RDF value with a datatype IRI for which no schema is available. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:unknown" target="mlserverdoc">sem:unknown</a>
  * @param string  The lexical value.
  * @param datatype  The datatype IRI.
  * @return  a SemUnknownExpr expression
  */
  public SemUnknownExpr unknown(XsStringExpr string, String datatype);
/**
  * Returns a sem:unknown value with the given literal value and datatype IRI. The sem:unknown type extends xs:untypedAtomic, and represents an RDF value with a datatype IRI for which no schema is available. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:unknown" target="mlserverdoc">sem:unknown</a>
  * @param string  The lexical value.
  * @param datatype  The datatype IRI.
  * @return  a SemUnknownExpr expression
  */
  public SemUnknownExpr unknown(XsStringExpr string, SemIriExpr datatype);
/**
  * Returns the datatype IRI of a sem:unknown value. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:unknown-datatype" target="mlserverdoc">sem:unknown-datatype</a>
  * @param val  The sem:unknown value.
  * @return  a SemIriExpr expression
  */
  public SemIriExpr unknownDatatype(SemUnknownExpr val);
/**
  * Return a UUID URN (RFC4122) as a sem:iri value. This XQuery function backs up the SPARQL UUID() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:uuid" target="mlserverdoc">sem:uuid</a>
  * @return  a SemIriExpr expression
  */
  public SemIriExpr uuid();
/**
  * Return a string that is the scheme specific part of random UUID URN (RFC4122). This XQuery function backs up the SPARQL STRUUID() function. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:uuid-string" target="mlserverdoc">sem:uuid-string</a>
  * @return  a XsStringExpr expression
  */
  public XsStringExpr uuidString();
/**
  * Constructs a sequence of SemBlankExpr items.
  * @param items  the SemBlankExpr items collected by the sequence
  * @return  a SemBlankSeqExpr sequence
  */
  public SemBlankSeqExpr blankSeq(SemBlankExpr... items);
 
/**
  * Constructs a sequence of SemInvalidExpr items.
  * @param items  the SemInvalidExpr items collected by the sequence
  * @return  a SemInvalidSeqExpr sequence
  */
  public SemInvalidSeqExpr invalidSeq(SemInvalidExpr... items);
 
/**
  * Constructs a sequence of SemIriExpr items.
  * @param items  the SemIriExpr items collected by the sequence
  * @return  a SemIriSeqExpr sequence
  */
  public SemIriSeqExpr iriSeq(SemIriExpr... items);
 
/**
  * Constructs a sequence of SemStoreExpr items.
  * @param items  the SemStoreExpr items collected by the sequence
  * @return  a SemStoreSeqExpr sequence
  */
  public SemStoreSeqExpr storeSeq(SemStoreExpr... items);
 
/**
  * Constructs a sequence of SemUnknownExpr items.
  * @param items  the SemUnknownExpr items collected by the sequence
  * @return  a SemUnknownSeqExpr sequence
  */
  public SemUnknownSeqExpr unknownSeq(SemUnknownExpr... items);

}
