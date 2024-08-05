/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.Arrays;

import com.marklogic.client.expression.RdfValue;
import com.marklogic.client.type.RdfLangStringSeqVal;
import com.marklogic.client.type.RdfLangStringVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsStringVal;

public class RdfValueImpl implements RdfValue {
  @Override
  public RdfLangStringVal langString(String string, String lang) {
    return new RdfLangStringValImpl(string, lang);
  }
  @Override
  public RdfLangStringSeqVal langStringSeq(RdfLangStringVal... langStrings) {
    return new RdfLangStringSeqValImpl(langStrings);
  }
  static class RdfLangStringSeqValImpl
    extends XsValueImpl.AnyAtomicTypeSeqValImpl<RdfLangStringValImpl>
    implements RdfLangStringSeqVal {
    RdfLangStringSeqValImpl(RdfLangStringVal[] values) {
      this(Arrays.copyOf(values, values.length, RdfLangStringValImpl[].class));
    }
    RdfLangStringSeqValImpl(RdfLangStringValImpl[] values) {
      super(values);
    }
    @Override
    public RdfLangStringVal[] getLangStringItems() {
      return getItems();
    }
    @Override
    public XsStringVal[] getStringItems() {
      return getItems();
    }
    @Override
    public XsAnyAtomicTypeVal[] getAnyAtomicTypeItems() {
      return getItems();
    }
  }
  static class RdfLangStringValImpl
    extends XsValueImpl.AnyAtomicTypeValImpl
    implements RdfLangStringVal, BaseTypeImpl.ParamBinder {
    private String string = null;
    private String lang   = null;
    public RdfLangStringValImpl(String string, String lang) {
      super("rdf", "langString");
      if (string == null) {
        throw new IllegalArgumentException("cannot take null string");
      }
      if (lang == null) {
        throw new IllegalArgumentException("cannot take null lang");
      }
      this.string = string;
      this.lang   = lang;
    }
    @Override
    public XsAnyAtomicTypeVal[] getAnyAtomicTypeItems() {
      return new XsAnyAtomicTypeVal[]{this};
    }
    @Override
    public String getString() {
      return string;
    }
    @Override
    public String getLang() {
      return lang;
    }
    @Override
    public RdfLangStringVal[] getLangStringItems() {
      return getItems();
    }
    @Override
    public RdfLangStringVal[] getItems() {
      return new RdfLangStringVal[]{this};
    }
    @Override
    public XsStringVal[] getStringItems() {
      return getItems();
    }
    @Override
    public String toString() {
      return getString();
    }
    @Override
    public String getParamQualifier() {
      return "@"+getLang();
    }
    @Override
    public String getParamValue() {
      return toString();
    }
    @Override
    public StringBuilder exportAst(StringBuilder strb) {
      return strb.append("{\"ns\":\"rdf\", \"fn\":\"langString\", \"args\":[\"")
        .append(getString()).append("\", \"").append(getLang())
        .append("\"]}");
    }
  }
}
