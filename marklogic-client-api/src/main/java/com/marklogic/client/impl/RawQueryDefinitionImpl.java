/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.CtsQueryWriteHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawCtsQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.RawQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;

abstract public class RawQueryDefinitionImpl<T extends StructureWriteHandle>
  extends AbstractQueryDefinition
  implements RawQueryDefinition
{
  static public class Combined
    extends RawQueryDefinitionImpl<StructureWriteHandle>
    implements RawCombinedQueryDefinition {
    Combined(StructureWriteHandle handle) {
      super(handle);
    }
    Combined(StructureWriteHandle handle, String optionsName) {
      super(handle, optionsName);
    }

    @Override
    public RawCombinedQueryDefinition withHandle(StructureWriteHandle handle) {
      setHandle(handle);
      return this;
    }
  }

  static abstract class RawCriteriaQueryImpl<T extends StructureWriteHandle>
    extends RawQueryDefinitionImpl<T>
  {
    private String criteria = null;

    public RawCriteriaQueryImpl(T handle) {
      super(handle);
    }
    public RawCriteriaQueryImpl(T handle, String optionsName) {
      super(handle, optionsName);
    }

    public String getCriteria() {
      return criteria;
    }

    public void setCriteria(String criteria) {
      this.criteria = criteria;
    }
  }

  static public class Structured
    extends RawCriteriaQueryImpl<StructureWriteHandle>
    implements RawStructuredQueryDefinition
  {
    public Structured(StructureWriteHandle handle) {
      super(handle);
    }
    public Structured(StructureWriteHandle handle, String optionsName) {
      super(handle, optionsName);
    }

    @Override
    public RawStructuredQueryDefinition withHandle(StructureWriteHandle handle) {
      setHandle(handle);
      return this;
    }

    @Override
    public RawStructuredQueryDefinition withCriteria(String criteria) {
      setCriteria(criteria);
      return this;
    }
  }

  static public class CtsQuery
          extends RawCriteriaQueryImpl<CtsQueryWriteHandle>
          implements RawCtsQueryDefinition
  {
    public CtsQuery(CtsQueryWriteHandle handle) {
      super(handle);
    }
    public CtsQuery(CtsQueryWriteHandle handle, String optionsName) {
      super(handle, optionsName);
    }

    @Override
    public RawCtsQueryDefinition withCriteria(String criteria) {
      setCriteria(criteria);
      return this;
    }

    @Override
    public void setHandle(StructureWriteHandle handle) {
      if (handle != null && !(handle instanceof CtsQueryWriteHandle)) {
        throw new IllegalArgumentException(
                "handle must be an instance of CtsQueryWriteHandle instead of: "+
                handle.getClass().getCanonicalName()
        );
      }
      super.setHandle((CtsQueryWriteHandle) handle);
    }

    @Override
    public RawCtsQueryDefinition withHandle(StructureWriteHandle handle) {
      setHandle(handle);
      return this;
    }
  }

  static public class ByExample
    extends RawQueryDefinitionImpl<StructureWriteHandle>
    implements RawQueryByExampleDefinition
  {
    ByExample(StructureWriteHandle handle) {
      super(handle);
    }
    ByExample(StructureWriteHandle handle, String optionsName) {
      super(handle, optionsName);
    }

    @Override
    public RawQueryByExampleDefinition withHandle(StructureWriteHandle handle) {
      setHandle(handle);
      return this;
    }
  }

  private T handle;

  RawQueryDefinitionImpl(T handle) {
    super();
    setHandle(handle);
  }
  RawQueryDefinitionImpl(T handle, String optionsName) {
    this(handle);
    setOptionsName(optionsName);
  }

  public String serialize() {
    if (handle == null) return "";
    return HandleAccessor.contentAsString(handle);
  }

  @Override
  public T getHandle() {
    return handle;
  }

  // must override with instanceof test if T is not StructureWriteHandle
  @Override
  public void setHandle(StructureWriteHandle handle) {
    this.handle = (T) handle;
  }

  @Override
  public String toString() {
    if (handle == null) {
      return "";
    }
    return handle.toString();
  }

  @Override
  public boolean canSerializeQueryAsJSON() {
    StructureWriteHandle handle = getHandle();
    if (handle == null) return false;
    return HandleAccessor.checkHandle(handle, "raw query").getFormat() == Format.JSON &&
            getOptionsName() == null;
  }
}
