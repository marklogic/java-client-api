/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
  DATE               ("date"),
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
  @Override
  public String toString() { return string; };
}
