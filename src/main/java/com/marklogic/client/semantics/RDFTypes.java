/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.semantics;

/** <p>The XSD/<a href="http://www.w3.org/TR/rdf11-concepts/#xsd-datatypes">RDF
 * literal types</a> supported for binding variables against SPARQL
 * queries.</p>
 */
public enum RDFTypes {
    STRING             ("string"),
    BOOLEAN            ("boolean"),
    DECIMAL            ("decimal"),
    INTEGER            ("integer"),
    DOUBLE             ("double"),
    FLOAT              ("float"),
    TIME               ("time"),
    DATETIME           ("dateTime"),
    GYEAR              ("gYear"),
    GMONTH             ("gMonth"),
    GDAY               ("gDay"),
    GYEARMONTH         ("gYearMonth"),
    GMONTHDAY          ("gMonthDay"),
    DURATION           ("duration"),
    YEARMONTHDURATION  ("yearMonthDuration"),
    DAYTIMEDURATION    ("dayTimeDuration"),
    BYTE               ("byte"),
    SHORT              ("short"),
    INT                ("int"),
    LONG               ("long"),
    UNSIGNEDBYTE       ("unsignedByte"),
    UNSIGNEDSHORT      ("unsignedShort"),
    UNSIGNEDINT        ("unsignedInt"),
    UNSIGNEDLONG       ("unsignedLong"),
    POSITIVEINTEGER    ("positiveInteger"),
    NONNEGATIVEINTEGER ("nonNegativeInteger"),
    NEGATIVEINTEGER    ("negativeInteger"),
    NONPOSITIVEINTEGER ("nonPositiveInteger"),
    HEXBINARY          ("hexBinary"),
    BASE64BINARY       ("base64Binary"),
    ANYURI             ("anyURI"),
    LANGUAGE           ("language"),
    NORMALIZEDSTRING   ("normalizedString"),
    TOKEN              ("token"),
    NMTOKEN            ("NMTOKEN"),
    NAME               ("Name"),
    NCNAME             ("NCName");

    private String string;
    private RDFTypes(String string) {
        this.string = string;
    };
    public String toString() { return string; };
}
