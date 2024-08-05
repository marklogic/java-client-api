/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.Arrays;

import com.marklogic.client.expression.SemValue;
import com.marklogic.client.impl.BaseTypeImpl.BaseArgImpl;
import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.SemIriSeqVal;
import com.marklogic.client.type.SemIriVal;
import com.marklogic.client.type.SemStoreExpr;
import com.marklogic.client.type.SemStoreSeqExpr;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsAnySimpleTypeVal;
import com.marklogic.client.type.XsAnyURIVal;
import com.marklogic.client.type.XsStringSeqVal;

public class SemValueImpl implements SemValue {
  @Override
  public SemIriVal iri(String stringIri) {
    return new SemIriValImpl(stringIri);
  }
  @Override
  public SemIriSeqVal iriSeq(String... stringIris) {
    return new SemIriSeqValImpl(stringIris);
  }
  @Override
  public SemIriSeqVal iriSeq(SemIriVal... iris) {
    return new SemIriSeqValImpl(iris);
  }

  @Override
  public SemStoreExpr store(String... options) {
    return store(new XsValueImpl.StringSeqValImpl(options), (CtsQueryExpr) null);
  }
  @Override
  public SemStoreExpr store(XsStringSeqVal options, CtsQueryExpr query) {
    return new SemStoreExprImpl("store", new Object[]{options, query});
  }
  @Override
  public SemStoreExpr rulesetStore(String... locations) {
    return rulesetStore(new XsValueImpl.StringSeqValImpl(locations), (SemStoreSeqExpr) null, (String[]) null);
  }
  @Override
  public SemStoreExpr rulesetStore(XsStringSeqVal locations, SemStoreExpr... stores) {
    return rulesetStore(locations, stores(stores), (String[]) null);
  }
  @Override
  public SemStoreExpr rulesetStore(XsStringSeqVal locations, SemStoreSeqExpr stores, String... options) {
    return new SemStoreExprImpl("ruleset-store",
      new Object[]{locations, stores, new XsValueImpl.StringSeqValImpl(options)}
    );
  }
  @Override
  public SemStoreSeqExpr stores(SemStoreExpr... stores) {
    return new SemStoreSeqExprImpl(BaseTypeImpl.convertList(stores));
  }

  static class SemIriSeqValImpl
    extends XsValueImpl.AnyAtomicTypeSeqValImpl<SemIriValImpl>
    implements SemIriSeqVal {
    SemIriSeqValImpl(String[] values) {
      this((SemIriValImpl[]) Arrays.stream(values)
        .map(val -> new SemIriValImpl(val))
        .toArray(size -> new SemIriValImpl[size]));
    }
    SemIriSeqValImpl(SemIriVal[] values) {
      this(Arrays.copyOf(values, values.length, SemIriValImpl[].class));
    }
    SemIriSeqValImpl(SemIriValImpl[] values) {
      super(values);
    }
    @Override
    public SemIriVal[] getIriItems() {
      return getItems();
    }
    @Override
    public XsAnyURIVal[] getAnyURIItems() {
      return getItems();
    }
    @Override
    public XsAnyAtomicTypeVal[] getAnyAtomicTypeItems() {
      return getItems();
    }
  }
  static class SemIriValImpl
    extends XsValueImpl.AnyAtomicTypeValImpl
    implements SemIriVal, BaseTypeImpl.ParamBinder {
    private String value = null;
    public SemIriValImpl(String value) {
      super("sem", "iri");
      if (value == null) {
        throw new IllegalArgumentException("cannot take null value");
      }
      this.value = value;
    }
    @Override
    public XsAnyAtomicTypeVal[] getAnyAtomicTypeItems() {
      return new XsAnyAtomicTypeVal[]{this};
    }
    @Override
    public XsAnyURIVal[] getAnyURIItems() {
      return new XsAnyURIVal[]{this};
    }
    @Override
    public String getString() {
      return value;
    }
    @Override
    public SemIriVal[] getIriItems() {
      return getItems();
    }
    @Override
    public SemIriVal[] getItems() {
      return new SemIriVal[]{this};
    }
    @Override
    public String toString() {
      return getString();
    }
    @Override
    public String getParamQualifier() {
      return null;
    }
    @Override
    public String getParamValue() {
      return toString();
    }
    @Override
    public StringBuilder exportAst(StringBuilder strb) {
      return strb.append("{\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"").append(getString()).append("\"]}");
    }
  }

  static class SemStoreSeqExprImpl extends BaseTypeImpl.BaseListImpl<BaseArgImpl> implements SemStoreSeqExpr {
    SemStoreSeqExprImpl(BaseArgImpl[] stores) {
      super(stores);
    }
  }
  static class SemStoreExprImpl extends BaseTypeImpl.BaseCallImpl<BaseArgImpl> implements SemStoreExpr {
    public SemStoreExprImpl(String name, Object[] args) {
      super("sem", name, BaseTypeImpl.convertList(args));
    }
  }
}
