/*
 * Copyright 2013-2018 MarkLogic Corporation
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
package com.marklogic.client.impl;

import com.marklogic.client.io.marker.CtsQueryWriteHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawCtsQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.RawQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;

abstract public class RawQueryDefinitionImpl
  extends AbstractQueryDefinition
  implements RawQueryDefinition
{
  static public class Combined
    extends RawQueryDefinitionImpl
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

  static public class Structured
    extends RawQueryDefinitionImpl
    implements RawStructuredQueryDefinition {
    private String criteria = null;

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

    public String serialize() {
      if (getHandle() == null) return "";
      return HandleAccessor.contentAsString(getHandle());
    }

    @Override
    public String getCriteria() {
      return criteria;
    }

    @Override
    public void setCriteria(String criteria) {
      this.criteria = criteria;
    }

    @Override
    public RawStructuredQueryDefinition withCriteria(String criteria) {
      setCriteria(criteria);
      return this;
    }
  }

  static public class CtsQuery extends AbstractQueryDefinition implements RawCtsQueryDefinition {
    private String criteria = null;
    private CtsQueryWriteHandle handle;

    public CtsQuery(CtsQueryWriteHandle handle) {
      super();
      setHandle(handle);
    }
    public CtsQuery(CtsQueryWriteHandle handle, String optionsName) {
      super();
      setHandle(handle);
      setOptionsName(optionsName);
    }

    public String serialize() {
      if (getHandle() == null) return "";
      return HandleAccessor.contentAsString(getHandle());
    }

    @Override
    public String getCriteria() {
      return criteria;
    }

    @Override
    public void setCriteria(String criteria) {
      this.criteria = criteria;
    }

    @Override
    public RawCtsQueryDefinition withCriteria(String criteria) {
      setCriteria(criteria);
      return this;
    }

    @Override
    public CtsQueryWriteHandle getHandle() {
      return handle;
    }

    @Override
    public void setHandle(CtsQueryWriteHandle handle) {
      this.handle = handle;
    }

    @Override
    public RawCtsQueryDefinition withHandle(CtsQueryWriteHandle handle) {
      setHandle(handle);
      return this;
    }

    @Override
    public String toString() {
      if (handle == null) {
        return "";
      }
      return handle.toString();
    }
  }

  static public class ByExample
    extends RawQueryDefinitionImpl
    implements RawQueryByExampleDefinition {
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

  private StructureWriteHandle handle;

  RawQueryDefinitionImpl(StructureWriteHandle handle) {
    super();
    setHandle(handle);
  }
  RawQueryDefinitionImpl(StructureWriteHandle handle, String optionsName) {
    this(handle);
    setOptionsName(optionsName);
  }

  @Override
  public StructureWriteHandle getHandle() {
    return handle;
  }

  @Override
  public void setHandle(StructureWriteHandle handle) {
    this.handle = handle;
  }

  @Override
  public String toString() {
    if (handle == null) {
      return "";
    }
    return handle.toString();
  }

}
