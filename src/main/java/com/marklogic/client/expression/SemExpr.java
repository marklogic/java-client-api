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

import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsDateTimeExpr;
import com.marklogic.client.type.XsDoubleExpr;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsStringExpr;

import com.marklogic.client.type.SemBlankExpr;
import com.marklogic.client.type.SemBlankSeqExpr;
import com.marklogic.client.type.SemInvalidExpr;
import com.marklogic.client.type.SemInvalidSeqExpr;
import com.marklogic.client.type.SemIriExpr;
import com.marklogic.client.type.SemIriSeqExpr;
import com.marklogic.client.type.SemUnknownExpr;
import com.marklogic.client.type.SemUnknownSeqExpr;

// IMPORTANT: Do not edit. This file is generated. 
public interface SemExpr extends SemValue {
    /**
    * This function returns an identifier for a blank node, allowing the construction of a triple that refers to a blank node. This XQuery function backs up the SPARQL BNODE() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:bnode" target="mlserverdoc">sem:bnode</a>
    * @return  a SemBlankExpr expression
    */
    public SemBlankExpr bnode();
    /**
    * This function returns an identifier for a blank node, allowing the construction of a triple that refers to a blank node. This XQuery function backs up the SPARQL BNODE() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:bnode" target="mlserverdoc">sem:bnode</a>
    * @param value  If provided, the same blank node identifier is returned for the same argument value passed to the function.
    * @return  a SemBlankExpr expression
    */
    public SemBlankExpr bnode(XsAnyAtomicTypeExpr value);
    /**
    * Returns the value of the first argument that evaluates without error. This XQuery function backs up the SPARQL COALESCE() functional form. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:coalesce" target="mlserverdoc">sem:coalesce</a>
    * @param parameter1  A value.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr coalesce(ItemExpr... parameter1);
    /**
    * Returns the name of the simple type of the atomic value argument as a SPARQL style IRI. If the value is derived from <code>sem:unknown</code> or <code>sem:invalid</code>, the datatype IRI part of those values is returned. This XQuery function backs up the SPARQL datatype() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:datatype" target="mlserverdoc">sem:datatype</a>
    * @param value  The value to return the type of.
    * @return  a SemIriExpr expression
    */
    public SemIriExpr datatype(XsAnyAtomicTypeExpr value);
    /**
    * The IF function form evaluates the first argument, interprets it as a effective boolean value, then returns the value of expression2 if the EBV is true, otherwise it returns the value of expression3. Only one of expression2 and expression3 is evaluated. If evaluating the first argument raises an error, then an error is raised for the evaluation of the IF expression. This XQuery function backs up the SPARQL IF() functional form. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:if" target="mlserverdoc">sem:if</a>
    * @param condition  The condition.
    * @param then  The then expression.
    * @param elseExpr  The else expression.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr ifExpr(XsBooleanExpr condition, ItemSeqExpr then, ItemSeqExpr elseExpr);
    /**
    * Returns a <code>sem:invalid</code> value with the given literal value and datatype IRI. The <code>sem:invalid</code> type extends <code>xs:untypedAtomic</code>, and represents an RDF value whose literal string is invalid according to the schema for it's datatype. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:invalid" target="mlserverdoc">sem:invalid</a>
    * @param string  The lexical value.
    * @param datatype  The datatype IRI.
    * @return  a SemInvalidExpr expression
    */
    public SemInvalidExpr invalid(XsStringExpr string, String datatype);
    /**
    * Returns a <code>sem:invalid</code> value with the given literal value and datatype IRI. The <code>sem:invalid</code> type extends <code>xs:untypedAtomic</code>, and represents an RDF value whose literal string is invalid according to the schema for it's datatype. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:invalid" target="mlserverdoc">sem:invalid</a>
    * @param string  The lexical value.
    * @param datatype  The datatype IRI.
    * @return  a SemInvalidExpr expression
    */
    public SemInvalidExpr invalid(XsStringExpr string, SemIriExpr datatype);
    /**
    * Returns the datatype IRI of a <code>sem:invalid</code> value. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:invalid-datatype" target="mlserverdoc">sem:invalid-datatype</a>
    * @param val  The sem:invalid value.
    * @return  a SemIriExpr expression
    */
    public SemIriExpr invalidDatatype(SemInvalidExpr val);
    /**
    * This is a constructor function that takes a string and constructs an item of type <code>sem:iri</code> from it.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:iri" target="mlserverdoc">sem:iri</a>
    * @param stringIri  The string with which to construct the <code>sem:iri</code>.
    * @return  a SemIriExpr expression
    */
    public SemIriExpr iri(XsAnyAtomicTypeExpr stringIri);
    public XsQNameExpr iriToQName(XsStringExpr arg1);
    /**
    * Returns true if the argument is an RDF blank node - that is, derived from type <code>sem:blank</code>. This XQuery function backs up the SPARQL isBlank() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:isBlank" target="mlserverdoc">sem:isBlank</a>
    * @param value  The value to test.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr isBlank(XsAnyAtomicTypeExpr value);
    /**
    * Returns true if the argument is an RDF IRI - that is, derived from type <code>sem:iri</code>, but not derived from type <code>sem:blank</code>. This XQuery function backs up the SPARQL isIRI() and isURI() functions. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:isIRI" target="mlserverdoc">sem:isIRI</a>
    * @param value  The value to test.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr isIRI(XsAnyAtomicTypeExpr value);
    /**
    * Returns true if the argument is an RDF literal - that is, derived from type <code>xs:anyAtomicType</code>, but not derived from type <code>sem:iri</code>. This XQuery function backs up the SPARQL isLiteral() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:isLiteral" target="mlserverdoc">sem:isLiteral</a>
    * @param value  The value to test.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr isLiteral(XsAnyAtomicTypeExpr value);
    /**
    * Returns true if the argument is a valid numeric RDF literal. This XQuery function backs up the SPARQL isNumeric() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:isNumeric" target="mlserverdoc">sem:isNumeric</a>
    * @param value  The value to test.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr isNumeric(XsAnyAtomicTypeExpr value);
    /**
    * Returns the language of the value passed in, or the empty string if the value has no language. Only values derived from <code>rdf:langString</code> have a language. This XQuery function backs up the SPARQL lang() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:lang" target="mlserverdoc">sem:lang</a>
    * @param value  The value to return the language of.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr lang(XsAnyAtomicTypeExpr value);
    /**
    * Returns true if <code>lang-tag</code> matches <code>lang-range</code> according to the basic filtering scheme defined in RFC4647. This XQuery function backs up the SPARQL langMatches() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:langMatches" target="mlserverdoc">sem:langMatches</a>
    * @param langTag  The language tag.
    * @param langRange  The language range.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr langMatches(XsStringExpr langTag, String langRange);
    /**
    * Returns true if <code>lang-tag</code> matches <code>lang-range</code> according to the basic filtering scheme defined in RFC4647. This XQuery function backs up the SPARQL langMatches() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:langMatches" target="mlserverdoc">sem:langMatches</a>
    * @param langTag  The language tag.
    * @param langRange  The language range.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr langMatches(XsStringExpr langTag, XsStringExpr langRange);
    public SemIriExpr QNameToIri(XsQNameExpr arg1);
    /**
    * Returns a random double between 0 and 1. This XQuery function backs up the SPARQL RAND() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:random" target="mlserverdoc">sem:random</a>
    
    */
    public XsDoubleExpr random();
    /**
    * Returns true if the arguments are the same RDF term as defined by the RDF concepts specification. This XQuery function backs up the SPARQL sameTerm() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:sameTerm" target="mlserverdoc">sem:sameTerm</a>
    * @param a  The first value to test.
    * @param b  The second value to test.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr sameTerm(XsAnyAtomicTypeExpr a, String b);
    /**
    * Returns true if the arguments are the same RDF term as defined by the RDF concepts specification. This XQuery function backs up the SPARQL sameTerm() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:sameTerm" target="mlserverdoc">sem:sameTerm</a>
    * @param a  The first value to test.
    * @param b  The second value to test.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr sameTerm(XsAnyAtomicTypeExpr a, XsAnyAtomicTypeExpr b);
    /**
    * Returns the timezone of an <code>xs:dateTime</code> value as a string. This XQuery function backs up the SPARQL TZ() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:timezone-string" target="mlserverdoc">sem:timezone-string</a>
    * @param value  The dateTime value
    * @return  a XsStringExpr expression
    */
    public XsStringExpr timezoneString(XsDateTimeExpr value);
    /**
    * Returns a value to represent the RDF typed literal with lexical value <code>value</code> and datatype IRI <code>datatype</code>. Returns a value of type <code>sem:unknown</code> for datatype IRIs for which there is no schema, and a value of type <code>sem:invalid</code> for lexical values which are invalid according to the schema for the given datatype. This XQuery function backs up the SPARQL STRDT() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:typed-literal" target="mlserverdoc">sem:typed-literal</a>
    * @param value  The lexical value.
    * @param datatype  The datatype IRI.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr typedLiteral(XsStringExpr value, String datatype);
    /**
    * Returns a value to represent the RDF typed literal with lexical value <code>value</code> and datatype IRI <code>datatype</code>. Returns a value of type <code>sem:unknown</code> for datatype IRIs for which there is no schema, and a value of type <code>sem:invalid</code> for lexical values which are invalid according to the schema for the given datatype. This XQuery function backs up the SPARQL STRDT() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:typed-literal" target="mlserverdoc">sem:typed-literal</a>
    * @param value  The lexical value.
    * @param datatype  The datatype IRI.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr typedLiteral(XsStringExpr value, SemIriExpr datatype);
    /**
    * Returns a <code>sem:unknown</code> value with the given literal value and datatype IRI. The <code>sem:unknown</code> type extends <code>xs:untypedAtomic</code>, and represents an RDF value with a datatype IRI for which no schema is available. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:unknown" target="mlserverdoc">sem:unknown</a>
    * @param string  The lexical value.
    * @param datatype  The datatype IRI.
    * @return  a SemUnknownExpr expression
    */
    public SemUnknownExpr unknown(XsStringExpr string, String datatype);
    /**
    * Returns a <code>sem:unknown</code> value with the given literal value and datatype IRI. The <code>sem:unknown</code> type extends <code>xs:untypedAtomic</code>, and represents an RDF value with a datatype IRI for which no schema is available. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:unknown" target="mlserverdoc">sem:unknown</a>
    * @param string  The lexical value.
    * @param datatype  The datatype IRI.
    * @return  a SemUnknownExpr expression
    */
    public SemUnknownExpr unknown(XsStringExpr string, SemIriExpr datatype);
    /**
    * Returns the datatype IRI of a <code>sem:unknown</code> value. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:unknown-datatype" target="mlserverdoc">sem:unknown-datatype</a>
    * @param val  The sem:unknown value.
    * @return  a SemIriExpr expression
    */
    public SemIriExpr unknownDatatype(SemUnknownExpr val);
    /**
    * Return a UUID URN (RFC4122) as a <code>sem:iri</code> value. This XQuery function backs up the SPARQL UUID() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:uuid" target="mlserverdoc">sem:uuid</a>
    
    */
    public SemIriExpr uuid();
    /**
    * Return a string that is the scheme specific part of random UUID URN (RFC4122). This XQuery function backs up the SPARQL STRUUID() function. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sem:uuid-string" target="mlserverdoc">sem:uuid-string</a>
    
    */
    public XsStringExpr uuidString();
    public SemBlankSeqExpr blankSeq(SemBlankExpr... items);
 
    public SemInvalidSeqExpr invalidSeq(SemInvalidExpr... items);
 
    public SemIriSeqExpr iriSeq(SemIriExpr... items);
 
    public SemUnknownSeqExpr unknownSeq(SemUnknownExpr... items);

}
