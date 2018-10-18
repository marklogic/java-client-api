/*
 * Copyright 2016-2018 MarkLogic Corporation
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

import com.marklogic.client.type.ServerExpression;
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
  *
  * <a name="ml-server-type-bnode"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:bnode" target="mlserverdoc">sem:bnode</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_blank.html">sem:blank</a> server data type
  */
  public SemBlankExpr bnode();
/**
  * This function returns an identifier for a blank node, allowing the construction of a triple that refers to a blank node. This XQuery function backs up the SPARQL BNODE() function. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:bnode" target="mlserverdoc">sem:bnode</a> server function.
  * @param value  If provided, the same blank node identifier is returned for the same argument value passed to the function.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_blank.html">sem:blank</a> server data type
  */
  public SemBlankExpr bnode(ServerExpression value);
/**
  * Returns the value of the first argument that evaluates without error. This XQuery function backs up the SPARQL COALESCE() functional form. 
  *
  * <a name="ml-server-type-coalesce"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:coalesce" target="mlserverdoc">sem:coalesce</a> server function.
  * @param parameter1  A value.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ItemSeqExpr coalesce(ServerExpression... parameter1);
/**
  * Returns the name of the simple type of the atomic value argument as a SPARQL style IRI. If the value is derived from sem:unknown or sem:invalid, the datatype IRI part of those values is returned. This XQuery function backs up the SPARQL datatype() function. 
  *
  * <a name="ml-server-type-datatype"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:datatype" target="mlserverdoc">sem:datatype</a> server function.
  * @param value  The value to return the type of.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a> server data type
  */
  public SemIriExpr datatype(ServerExpression value);
/**
  * The IF function form evaluates the first argument, interprets it as a effective boolean value, then returns the value of expression2 if the EBV is true, otherwise it returns the value of expression3. Only one of expression2 and expression3 is evaluated. If evaluating the first argument raises an error, then an error is raised for the evaluation of the IF expression. This XQuery function backs up the SPARQL IF() functional form. 
  *
  * <a name="ml-server-type-if"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:if" target="mlserverdoc">sem:if</a> server function.
  * @param condition  The condition.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @param then  The then expression.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param elseExpr  The else expression.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ItemSeqExpr ifExpr(ServerExpression condition, ServerExpression then, ServerExpression elseExpr);
/**
  * Returns a sem:invalid value with the given literal value and datatype IRI. The sem:invalid type extends xs:untypedAtomic, and represents an RDF value whose literal string is invalid according to the schema for it's datatype. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:invalid" target="mlserverdoc">sem:invalid</a> server function.
  * @param string  The lexical value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param datatype  The datatype IRI.  (of <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_invalid.html">sem:invalid</a> server data type
  */
  public SemInvalidExpr invalid(ServerExpression string, String datatype);
/**
  * Returns a sem:invalid value with the given literal value and datatype IRI. The sem:invalid type extends xs:untypedAtomic, and represents an RDF value whose literal string is invalid according to the schema for it's datatype. 
  *
  * <a name="ml-server-type-invalid"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:invalid" target="mlserverdoc">sem:invalid</a> server function.
  * @param string  The lexical value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param datatype  The datatype IRI.  (of <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_invalid.html">sem:invalid</a> server data type
  */
  public SemInvalidExpr invalid(ServerExpression string, ServerExpression datatype);
/**
  * Returns the datatype IRI of a sem:invalid value. 
  *
  * <a name="ml-server-type-invalid-datatype"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:invalid-datatype" target="mlserverdoc">sem:invalid-datatype</a> server function.
  * @param val  The sem:invalid value.  (of <a href="{@docRoot}/doc-files/types/sem_invalid.html">sem:invalid</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a> server data type
  */
  public SemIriExpr invalidDatatype(ServerExpression val);
/**
  * This is a constructor function that takes a string and constructs an item of type sem:iri from it.
  *
  * <a name="ml-server-type-iri"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:iri" target="mlserverdoc">sem:iri</a> server function.
  * @param stringIri  The string with which to construct the sem:iri.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a> server data type
  */
  public SemIriExpr iri(ServerExpression stringIri);
public XsQNameExpr iriToQName(ServerExpression arg1);
/**
  * Returns true if the argument is an RDF blank node - that is, derived from type sem:blank. This XQuery function backs up the SPARQL isBlank() function. 
  *
  * <a name="ml-server-type-isBlank"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:isBlank" target="mlserverdoc">sem:isBlank</a> server function.
  * @param value  The value to test.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public XsBooleanExpr isBlank(ServerExpression value);
/**
  * Returns true if the argument is an RDF IRI - that is, derived from type sem:iri, but not derived from type sem:blank. This XQuery function backs up the SPARQL isIRI() and isURI() functions. 
  *
  * <a name="ml-server-type-isIRI"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:isIRI" target="mlserverdoc">sem:isIRI</a> server function.
  * @param value  The value to test.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public XsBooleanExpr isIRI(ServerExpression value);
/**
  * Returns true if the argument is an RDF literal - that is, derived from type xs:anyAtomicType, but not derived from type sem:iri. This XQuery function backs up the SPARQL isLiteral() function. 
  *
  * <a name="ml-server-type-isLiteral"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:isLiteral" target="mlserverdoc">sem:isLiteral</a> server function.
  * @param value  The value to test.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public XsBooleanExpr isLiteral(ServerExpression value);
/**
  * Returns true if the argument is a valid numeric RDF literal. This XQuery function backs up the SPARQL isNumeric() function. 
  *
  * <a name="ml-server-type-isNumeric"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:isNumeric" target="mlserverdoc">sem:isNumeric</a> server function.
  * @param value  The value to test.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public XsBooleanExpr isNumeric(ServerExpression value);
/**
  * Returns the language of the value passed in, or the empty string if the value has no language. Only values derived from rdf:langString have a language. This XQuery function backs up the SPARQL lang() function. 
  *
  * <a name="ml-server-type-lang"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:lang" target="mlserverdoc">sem:lang</a> server function.
  * @param value  The value to return the language of.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public XsStringExpr lang(ServerExpression value);
/**
  * Returns true if lang-tag matches lang-range according to the basic filtering scheme defined in RFC4647. This XQuery function backs up the SPARQL langMatches() function. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:langMatches" target="mlserverdoc">sem:langMatches</a> server function.
  * @param langTag  The language tag.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param langRange  The language range.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public XsBooleanExpr langMatches(ServerExpression langTag, String langRange);
/**
  * Returns true if lang-tag matches lang-range according to the basic filtering scheme defined in RFC4647. This XQuery function backs up the SPARQL langMatches() function. 
  *
  * <a name="ml-server-type-langMatches"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:langMatches" target="mlserverdoc">sem:langMatches</a> server function.
  * @param langTag  The language tag.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param langRange  The language range.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public XsBooleanExpr langMatches(ServerExpression langTag, ServerExpression langRange);
public SemIriExpr QNameToIri(ServerExpression arg1);
/**
  * Returns a random double between 0 and 1. This XQuery function backs up the SPARQL RAND() function. 
  *
  * <a name="ml-server-type-random"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:random" target="mlserverdoc">sem:random</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public XsDoubleExpr random();
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a> server function.
  * @param locations  The locations of the rulesets.
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr rulesetStore(String locations);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a> server function.
  * @param locations  The locations of the rulesets.
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr rulesetStore(XsStringSeqVal locations);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a> server function.
  * @param locations  The locations of the rulesets.
  * @param store  The base store(s) over which to apply the ruleset to get inferred triples. The default for sem:store is an empty sequence, which means accessing the current database's triple index using the default rulesets configured for that database.
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr rulesetStore(String locations, SemStoreExpr... store);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a> server function.
  * @param locations  The locations of the rulesets.
  * @param store  The base store(s) over which to apply the ruleset to get inferred triples. The default for sem:store is an empty sequence, which means accessing the current database's triple index using the default rulesets configured for that database.
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr rulesetStore(XsStringSeqVal locations, SemStoreSeqExpr store);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a> server function.
  * @param locations  The locations of the rulesets.
  * @param store  The base store(s) over which to apply the ruleset to get inferred triples. The default for sem:store is an empty sequence, which means accessing the current database's triple index using the default rulesets configured for that database.
  * @param options  Options as a sequence of string values. Available options are:  "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. 
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr rulesetStore(String locations, SemStoreSeqExpr store, String options);
/**
  * The sem:ruleset-store function returns a set of triples derived by applying the ruleset to the triples in the sem:store constructor provided in store ("the triples that can be inferred from these rules"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:ruleset-store" target="mlserverdoc">sem:ruleset-store</a> server function.
  * @param locations  The locations of the rulesets.
  * @param store  The base store(s) over which to apply the ruleset to get inferred triples. The default for sem:store is an empty sequence, which means accessing the current database's triple index using the default rulesets configured for that database.
  * @param options  Options as a sequence of string values. Available options are:  "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. 
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr rulesetStore(XsStringSeqVal locations, SemStoreSeqExpr store, XsStringSeqVal options);
/**
  * Returns true if the arguments are the same RDF term as defined by the RDF concepts specification. This XQuery function backs up the SPARQL sameTerm() function. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:sameTerm" target="mlserverdoc">sem:sameTerm</a> server function.
  * @param a  The first value to test.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param b  The second value to test.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public XsBooleanExpr sameTerm(ServerExpression a, String b);
/**
  * Returns true if the arguments are the same RDF term as defined by the RDF concepts specification. This XQuery function backs up the SPARQL sameTerm() function. 
  *
  * <a name="ml-server-type-sameTerm"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:sameTerm" target="mlserverdoc">sem:sameTerm</a> server function.
  * @param a  The first value to test.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param b  The second value to test.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public XsBooleanExpr sameTerm(ServerExpression a, ServerExpression b);
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a> server function.
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr store();
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a> server function.
  * @param options  Options as a sequence of string values. Available options are:  "any" Values from any fragment should be included. "document" Values from document fragments should be included. "properties" Values from properties fragments should be included. "locks" Values from locks fragments should be included. "checked" Word positions should be checked when resolving the query. "unchecked" Word positions should not be checked when resolving the query. "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. "no-default-rulesets" Don't apply the database's default rulesets to the sem:store. "locking=read-write/write" read-write: Read-lock documents containing triples being accessed, write-lock documents being updated; write: Only write-lock documents being updated. Default is locking=read-write. Locking is ignored in query transaction.  
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr store(String options);
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a> server function.
  * @param options  Options as a sequence of string values. Available options are:  "any" Values from any fragment should be included. "document" Values from document fragments should be included. "properties" Values from properties fragments should be included. "locks" Values from locks fragments should be included. "checked" Word positions should be checked when resolving the query. "unchecked" Word positions should not be checked when resolving the query. "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. "no-default-rulesets" Don't apply the database's default rulesets to the sem:store. "locking=read-write/write" read-write: Read-lock documents containing triples being accessed, write-lock documents being updated; write: Only write-lock documents being updated. Default is locking=read-write. Locking is ignored in query transaction.  
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr store(XsStringSeqVal options);
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a> server function.
  * @param options  Options as a sequence of string values. Available options are:  "any" Values from any fragment should be included. "document" Values from document fragments should be included. "properties" Values from properties fragments should be included. "locks" Values from locks fragments should be included. "checked" Word positions should be checked when resolving the query. "unchecked" Word positions should not be checked when resolving the query. "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. "no-default-rulesets" Don't apply the database's default rulesets to the sem:store. "locking=read-write/write" read-write: Read-lock documents containing triples being accessed, write-lock documents being updated; write: Only write-lock documents being updated. Default is locking=read-write. Locking is ignored in query transaction.  
  * @param query  Only include triples in fragments selected by the cts:query. The triples do not need to match the query, but they must occur in fragments selected by the query. The fragments are not filtered to ensure they match the query, but instead selected in the same manner as  "unfiltered" cts:search operations. If a string is entered, the string is treated as a cts:word-query of the specified string.
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr store(String options, CtsQueryExpr query);
/**
  * The sem:store function defines a set of criteria, that when evaluated, selects a set of triples to be passed in to sem:sparql(), sem:sparql-update(), or sem:sparql-values() as part of the options argument. The sem:store constructor queries from the current database's triple index, restricted by the options and the cts:query argument (for instance, "triples in documents matching this query"). 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:store" target="mlserverdoc">sem:store</a> server function.
  * @param options  Options as a sequence of string values. Available options are:  "any" Values from any fragment should be included. "document" Values from document fragments should be included. "properties" Values from properties fragments should be included. "locks" Values from locks fragments should be included. "checked" Word positions should be checked when resolving the query. "unchecked" Word positions should not be checked when resolving the query. "size=number of MB" The maximum size of the memory used to cache inferred triples. This defaults to the default inference size set for the app-server. If the value provided is bigger than the maximum inference size set for the App Server, an error is raised [XDMP-INFSIZE]. "no-default-rulesets" Don't apply the database's default rulesets to the sem:store. "locking=read-write/write" read-write: Read-lock documents containing triples being accessed, write-lock documents being updated; write: Only write-lock documents being updated. Default is locking=read-write. Locking is ignored in query transaction.  
  * @param query  Only include triples in fragments selected by the cts:query. The triples do not need to match the query, but they must occur in fragments selected by the query. The fragments are not filtered to ensure they match the query, but instead selected in the same manner as  "unfiltered" cts:search operations. If a string is entered, the string is treated as a cts:word-query of the specified string.
  * @return  a SemStoreExpr server expression
  */
  public SemStoreExpr store(XsStringSeqVal options, CtsQueryExpr query);
/**
  * Returns the timezone of an xs:dateTime value as a string. This XQuery function backs up the SPARQL TZ() function. 
  *
  * <a name="ml-server-type-timezone-string"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:timezone-string" target="mlserverdoc">sem:timezone-string</a> server function.
  * @param value  The dateTime value  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public XsStringExpr timezoneString(ServerExpression value);
/**
  * Returns a value to represent the RDF typed literal with lexical value value and datatype IRI datatype. Returns a value of type sem:unknown for datatype IRIs for which there is no schema, and a value of type sem:invalid for lexical values which are invalid according to the schema for the given datatype. This XQuery function backs up the SPARQL STRDT() function. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:typed-literal" target="mlserverdoc">sem:typed-literal</a> server function.
  * @param value  The lexical value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param datatype  The datatype IRI.  (of <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public XsAnyAtomicTypeExpr typedLiteral(ServerExpression value, String datatype);
/**
  * Returns a value to represent the RDF typed literal with lexical value value and datatype IRI datatype. Returns a value of type sem:unknown for datatype IRIs for which there is no schema, and a value of type sem:invalid for lexical values which are invalid according to the schema for the given datatype. This XQuery function backs up the SPARQL STRDT() function. 
  *
  * <a name="ml-server-type-typed-literal"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:typed-literal" target="mlserverdoc">sem:typed-literal</a> server function.
  * @param value  The lexical value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param datatype  The datatype IRI.  (of <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public XsAnyAtomicTypeExpr typedLiteral(ServerExpression value, ServerExpression datatype);
/**
  * Returns a sem:unknown value with the given literal value and datatype IRI. The sem:unknown type extends xs:untypedAtomic, and represents an RDF value with a datatype IRI for which no schema is available. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:unknown" target="mlserverdoc">sem:unknown</a> server function.
  * @param string  The lexical value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param datatype  The datatype IRI.  (of <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_unknown.html">sem:unknown</a> server data type
  */
  public SemUnknownExpr unknown(ServerExpression string, String datatype);
/**
  * Returns a sem:unknown value with the given literal value and datatype IRI. The sem:unknown type extends xs:untypedAtomic, and represents an RDF value with a datatype IRI for which no schema is available. 
  *
  * <a name="ml-server-type-unknown"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:unknown" target="mlserverdoc">sem:unknown</a> server function.
  * @param string  The lexical value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param datatype  The datatype IRI.  (of <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_unknown.html">sem:unknown</a> server data type
  */
  public SemUnknownExpr unknown(ServerExpression string, ServerExpression datatype);
/**
  * Returns the datatype IRI of a sem:unknown value. 
  *
  * <a name="ml-server-type-unknown-datatype"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:unknown-datatype" target="mlserverdoc">sem:unknown-datatype</a> server function.
  * @param val  The sem:unknown value.  (of <a href="{@docRoot}/doc-files/types/sem_unknown.html">sem:unknown</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a> server data type
  */
  public SemIriExpr unknownDatatype(ServerExpression val);
/**
  * Return a UUID URN (RFC4122) as a sem:iri value. This XQuery function backs up the SPARQL UUID() function. 
  *
  * <a name="ml-server-type-uuid"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:uuid" target="mlserverdoc">sem:uuid</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/sem_iri.html">sem:iri</a> server data type
  */
  public SemIriExpr uuid();
/**
  * Return a string that is the scheme specific part of random UUID URN (RFC4122). This XQuery function backs up the SPARQL STRUUID() function. 
  *
  * <a name="ml-server-type-uuid-string"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/sem:uuid-string" target="mlserverdoc">sem:uuid-string</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public XsStringExpr uuidString();
/**
  * Constructs a sequence of SemBlankExpr items.
  * @param items  the SemBlankExpr items collected by the sequence
  * @return  a SemBlankSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public SemBlankSeqExpr blankSeq(SemBlankExpr... items);
 
/**
  * Constructs a sequence of SemInvalidExpr items.
  * @param items  the SemInvalidExpr items collected by the sequence
  * @return  a SemInvalidSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public SemInvalidSeqExpr invalidSeq(SemInvalidExpr... items);
 
/**
  * Constructs a sequence of SemIriExpr items.
  * @param items  the SemIriExpr items collected by the sequence
  * @return  a SemIriSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public SemIriSeqExpr iriSeq(SemIriExpr... items);
 
/**
  * Constructs a sequence of SemStoreExpr items.
  * @param items  the SemStoreExpr items collected by the sequence
  * @return  a SemStoreSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public SemStoreSeqExpr storeSeq(SemStoreExpr... items);
 
/**
  * Constructs a sequence of SemUnknownExpr items.
  * @param items  the SemUnknownExpr items collected by the sequence
  * @return  a SemUnknownSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public SemUnknownSeqExpr unknownSeq(SemUnknownExpr... items);

}
