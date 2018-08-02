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
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.Transaction;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.impl.RESTServices.RESTServiceResult;
import com.marklogic.client.impl.RESTServices.RESTServiceResultIterator;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.PlanExprCol;
import com.marklogic.client.type.PlanParamBindingVal;
import com.marklogic.client.type.PlanParamExpr;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.util.RequestParameters;

public class RowManagerImpl
  extends AbstractLoggingManager
  implements RowManager
{
  private RESTServices services;
  private HandleFactoryRegistry handleRegistry;
  private RowSetPart   datatypeStyle     = null;
  private RowStructure rowStructureStyle = null;

  public RowManagerImpl(RESTServices services) {
    super();
    this.services = services;
  }

  HandleFactoryRegistry getHandleRegistry() {
    return handleRegistry;
  }
  void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
    this.handleRegistry = handleRegistry;
  }

  @Override
  public PlanBuilder newPlanBuilder() {
    PlanBuilderImpl planBuilder = new PlanBuilderSubImpl();

    planBuilder.setHandleRegistry(handleRegistry);

    return planBuilder;
  }

  @Override
  public RowSetPart getDatatypeStyle() {
    if (datatypeStyle == null) {
      return RowSetPart.ROWS;
    }
    return datatypeStyle;
  }
  @Override
  public void setDatatypeStyle(RowSetPart style) {
    this.datatypeStyle = style;
  }
  @Override
  public RowStructure getRowStructureStyle() {
    if (rowStructureStyle == null) {
      return RowStructure.OBJECT;
    }
    return rowStructureStyle;
  }
  @Override
  public void setRowStructureStyle(RowStructure style) {
    this.rowStructureStyle = style;
  }

  @Override
  public RawPlanDefinition newRawPlanDefinition(JSONWriteHandle handle) {
    return new RawPlanDefinitionImpl(handle);
  }

  @Override
  public <T> T resultDocAs(Plan plan, Class<T> as) {
    return resultDocAs(plan, as, null);
  }
  @Override
  public <T> T resultDocAs(Plan plan, Class<T> as, Transaction transaction) {
    ContentHandle<T> handle = handleFor(as);
    if (resultDoc(plan, (StructureReadHandle) handle, transaction) == null) {
      return null;
    }

    return handle.get();
  }
  @Override
  public <T extends StructureReadHandle> T resultDoc(Plan plan, T resultsHandle) {
    return resultDoc(plan, resultsHandle, null);
  }
  @Override
  public <T extends StructureReadHandle> T resultDoc(Plan plan, T resultsHandle, Transaction transaction) {
    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);

    AbstractWriteHandle astHandle = requestPlan.getHandle();

    if (resultsHandle == null) {
      throw new IllegalArgumentException("Must specify a handle to read the row result document");
    }

    RequestParameters params = getParamBindings(requestPlan);
    addDatatypeStyleParam(params,     getDatatypeStyle());
    addRowStructureStyleParam(params, getRowStructureStyle());

    return services.postResource(requestLogger, "rows", transaction, params, astHandle, resultsHandle);
  }

  @Override
  public RowSet<RowRecord> resultRows(Plan plan) {
    return resultRows(plan, (Transaction) null);
  }
  @Override
  public RowSet<RowRecord> resultRows(Plan plan, Transaction transaction) {
    RowSetPart   datatypeStyle     = getDatatypeStyle();
    RowStructure rowStructureStyle = getRowStructureStyle();

    RESTServiceResultIterator iter = makeRequest(
      plan, "json", datatypeStyle, rowStructureStyle, "reference", transaction
    );

    RowSetRecord rowset = new RowSetRecord(
      "json", datatypeStyle, rowStructureStyle, iter, handleRegistry
    );
    rowset.init();

    return rowset;
  }
  @Override
  public <T extends StructureReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle) {
    return resultRows(plan, rowHandle, (Transaction) null);
  }
  @Override
  public <T extends StructureReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle, Transaction transaction) {
    RowSetPart   datatypeStyle     = getDatatypeStyle();
    RowStructure rowStructureStyle = getRowStructureStyle();

    String rowFormat = getRowFormat(rowHandle);

    RESTServiceResultIterator iter = makeRequest(
      plan, rowFormat, datatypeStyle, rowStructureStyle, "inline", transaction
    );

    RowSetHandle<T> rowset = new RowSetHandle<>(
      rowFormat, datatypeStyle, rowStructureStyle, iter, rowHandle
    );
    rowset.init();

    return rowset;
  }
  @Override
  public <T> RowSet<T> resultRowsAs(Plan plan, Class<T> as) {
    return resultRowsAs(plan, as, (Transaction) null);
  }
  @Override
  public <T> RowSet<T> resultRowsAs(Plan plan, Class<T> as, Transaction transaction) {
    RowSetPart   datatypeStyle     = getDatatypeStyle();
    RowStructure rowStructureStyle = getRowStructureStyle();

    ContentHandle<T> rowHandle = handleFor(as);

    String rowFormat = getRowFormat(rowHandle);

    RESTServiceResultIterator iter = makeRequest(
      plan, rowFormat, datatypeStyle, rowStructureStyle, "inline", transaction
    );

    RowSetObject<T> rowset = new RowSetObject<>(
      rowFormat, datatypeStyle, rowStructureStyle, iter, rowHandle
    );
    rowset.init();

    return rowset;
  }

  @Override
  public <T extends StructureReadHandle> T explain(Plan plan, T resultsHandle) {
    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);

    AbstractWriteHandle astHandle = requestPlan.getHandle();

    if (resultsHandle == null) {
      throw new IllegalArgumentException("Must specify a handle to read the explanation for the plan");
    }

    RequestParameters params = new RequestParameters();
    params.add("output", "explain");

    return services.postResource(requestLogger, "rows", null, params, astHandle, resultsHandle);
  }
  @Override
  public <T> T explainAs(Plan plan, Class<T> as) {
    ContentHandle<T> handle = handleFor(as);
    if (explain(plan, (StructureReadHandle) handle) == null) {
      return null;
    }

    return handle.get();
  }

  private void addDatatypeStyleParam(RequestParameters params, RowSetPart datatypeStyle) {
    if (datatypeStyle != null) {
      switch (datatypeStyle) {
        case HEADER:
          params.add("column-types", "header");
          break;
        case ROWS:
          params.add("column-types", "rows");
          break;
        default:
          throw new IllegalStateException("unknown data type style: "+datatypeStyle);
      }
    }
  }
  private void addRowStructureStyleParam(RequestParameters params, RowStructure rowStructureStyle) {
    if (rowStructureStyle != null) {
      switch (rowStructureStyle) {
        case ARRAY:
          params.add("output", "array");
          break;
        case OBJECT:
          params.add("output", "object");
          break;
        default:
          throw new IllegalStateException("unknown row structure style: "+rowStructureStyle);
      }
    }
  }

  private <T extends AbstractReadHandle> String getRowFormat(T rowHandle) {
    if (rowHandle == null) {
      throw new IllegalArgumentException("Must specify a handle to iterate over the rows");
    }

    if (!(rowHandle instanceof BaseHandle)) {
      throw new IllegalArgumentException("Cannot iterate rows with invalid handle having class "+rowHandle.getClass().getName());
    }

    BaseHandle<?,?> baseHandle = (BaseHandle<?,?>) rowHandle;

    Format handleFormat = baseHandle.getFormat();
    switch (handleFormat) {
      case JSON:
      case UNKNOWN:
        return "json";
      case XML:
        return "xml";
      default:
        throw new IllegalArgumentException("Must use JSON or XML format to iterate rows instead of "+handleFormat.name());
    }
  }
  private RESTServiceResultIterator makeRequest(
    Plan plan,
    String rowFormat, RowSetPart datatypeStyle, RowStructure rowStructureStyle, String nodeCols,
    Transaction transaction
  ) {
    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);

    AbstractWriteHandle astHandle = requestPlan.getHandle();

    RequestParameters params = getParamBindings(requestPlan);
    params.add("row-format",   rowFormat);
    params.add("node-columns", nodeCols);
    addDatatypeStyleParam(params,     datatypeStyle);
    addRowStructureStyleParam(params, rowStructureStyle);

// QUESTION: outputMimetypes a noop?
    return services.postIteratedResource(requestLogger, "rows", transaction, params, astHandle);
  }
  private PlanBuilderBaseImpl.RequestPlan checkPlan(Plan plan) {
    if (plan == null) {
      throw new IllegalArgumentException("Must specify a plan to produce row results");
    } else if (!(plan instanceof PlanBuilderBaseImpl.RequestPlan)) {
      throw new IllegalArgumentException(
        "Cannot produce rows with invalid plan having class "+plan.getClass().getName()
      );
    }
    return (PlanBuilderBaseImpl.RequestPlan) plan;
  }
  private RequestParameters getParamBindings(PlanBuilderBaseImpl.RequestPlan requestPlan) {
    RequestParameters params = new RequestParameters();
    Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> planParams = requestPlan.getParams();
    if (planParams != null) {
      for (Map.Entry<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> entry: planParams.entrySet()) {
        BaseTypeImpl.ParamBinder binder = entry.getValue();

        StringBuilder nameBuf = new StringBuilder("bind:");
        nameBuf.append(entry.getKey().getName());
        String paramQual = binder.getParamQualifier();
        if (paramQual != null) {
          nameBuf.append(paramQual);
        }

        params.add(nameBuf.toString(), binder.getParamValue());
      }
    }
    return params;
  }

  <T> ContentHandle<T> handleFor(Class<T> as) {
    if (as == null) {
      throw new IllegalArgumentException("Must specify a class for content with a registered handle");
    }

    ContentHandle<T> handle = handleRegistry.makeHandle(as);
    if (!(handle instanceof StructureReadHandle)) {
      if (handle == null) {
        throw new IllegalArgumentException("Class \"" + as.getName() + "\" has no registered handle");
      } else {
        throw new IllegalArgumentException("Class \"" + as.getName() + "\" uses handle " +
          handle.getClass().getName() + " which is not a StructureReadHandle");
      }
    }

    return handle;
  }

  abstract static class RowSetBase<T> implements RowSet<T>, Iterator<T> {
    String                    rowFormat         = null;
    RESTServiceResultIterator results           = null;
    String[]                  columnNames       = null;
    String[]                  columnTypes       = null;
    RESTServiceResult         nextRow           = null;
    RowSetPart                datatypeStyle     = null;
    RowStructure              rowStructureStyle = null;

    RowSetBase(
      String rowFormat, RowSetPart datatypeStyle, RowStructure rowStructureStyle,
      RESTServiceResultIterator results
    ) {
      this.rowFormat         = rowFormat;
      this.datatypeStyle     = datatypeStyle;
      this.rowStructureStyle = rowStructureStyle;
      this.results           = results;
    }

    void init() {
      parseColumns(datatypeStyle, rowStructureStyle);
      if (results.hasNext()) {
        nextRow = results.next();
      }
    }

    @SuppressWarnings("unchecked")
    private void parseColumns(RowSetPart datatypeStyle, RowStructure rowStructureStyle) {
      if (!results.hasNext()) {
        return;
      }
      RESTServiceResult headerRow = results.next();
      switch(rowFormat) {
        case "json":
          try {
            List<Map<String, String>> cols = null;
            switch (rowStructureStyle) {
              case OBJECT:
                Map<String, Object> headerObj = (Map<String, Object>) new ObjectMapper().readValue(
                  headerRow.getContent(new InputStreamHandle()).get(), Map.class
                );
                if (headerObj != null) {
                  cols = (List<Map<String, String>>) headerObj.get("columns");
                }
                break;
              case ARRAY:
                cols = (List<Map<String, String>>) new ObjectMapper().readValue(
                  headerRow.getContent(new InputStreamHandle()).get(), List.class
                );
                break;
              default:
                throw new InternalError("unknown row structure style: "+rowStructureStyle);
            }
            int colSize = (cols == null) ? 0 : cols.size();
            columnNames = (colSize > 0) ?
              new String[colSize] : new String[0];
            columnTypes = (colSize > 0 && datatypeStyle == RowSetPart.HEADER) ?
              new String[colSize] : new String[0];
            if (colSize > 0) {
              int i=0;
              for (Map<String, String> col: cols) {
                columnNames[i] = col.get("name");
                if (datatypeStyle == RowSetPart.HEADER) {
                  columnTypes[i] = col.get("type");
                }
                i++;
              }
            }
          } catch (JsonParseException e) {
            throw new MarkLogicIOException("could not read JSON header part", e);
          } catch (JsonMappingException e) {
            throw new MarkLogicIOException("could not read JSON header map", e);
          } catch (IOException e) {
            throw new MarkLogicIOException("could not read JSON header", e);
          }
          break;
        case "xml":
          try {
            List<String> cols  = new ArrayList<>();
            List<String> types = (datatypeStyle == RowSetPart.HEADER) ?
              new ArrayList<>() : null;
            XMLStreamReader headerReader =
              headerRow.getContent(new XMLStreamReaderHandle()).get();
            while (headerReader.hasNext()) {
              switch(headerReader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                  if ("column".equals(headerReader.getLocalName())) {
                    cols.add(headerReader.getAttributeValue(null, "name"));
                    if (datatypeStyle == RowSetPart.HEADER) {
                      types.add(headerReader.getAttributeValue(null, "type"));
                    }
                    headerReader.nextTag();
                  }
                  break;
              }
            }
            int colSize = cols.size();
            columnNames = (colSize > 0) ?
              cols.toArray(new String[colSize]) : new String[0];
            columnTypes = (colSize > 0 && datatypeStyle == RowSetPart.HEADER) ?
              types.toArray(new String[colSize]) : new String[0];
          } catch (XMLStreamException e) {
            throw new MarkLogicIOException("could not read XML header", e);
          }
          break;
        default:
          throw new IllegalArgumentException("Row format should be JSON or XML instead of "+rowFormat);
      }
    }

    @Override
    public String[] getColumnNames() {
      return columnNames;
    }

    @Override
    public String[] getColumnTypes() {
      return columnTypes;
    }

    @Override
    public Iterator<T> iterator() {
      return this;
    }
    @Override
    public Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
    }

    @Override
    public boolean hasNext() {
      return nextRow != null;
    }

    @Override
    public void close() {
      closeImpl();
    }
    @Override
    protected void finalize() throws Throwable {
      closeImpl();
      super.finalize();
    }
    private void closeImpl() {
      if (results != null) {
        results.close();
        results = null;
        nextRow = null;
      }
    }
  }
  static class RowSetRecord extends RowSetBase<RowRecord> {
    private HandleFactoryRegistry             handleRegistry  = null;
    private Map<String, RowRecord.ColumnKind> headerKinds     = null;
    private Map<String, String>               headerDatatypes = null;
    private Map<String, String>               aliases         = null;
    RowSetRecord(
      String rowFormat, RowSetPart datatypeStyle, RowStructure rowStructureStyle,
      RESTServiceResultIterator results, HandleFactoryRegistry handleRegistry
    ) {
      super(rowFormat, datatypeStyle, rowStructureStyle, results);
      this.handleRegistry = handleRegistry;
    }

    void init() {
      super.init();
      if (datatypeStyle == RowSetPart.HEADER) {
        headerKinds     = new HashMap<>();
        headerDatatypes = new HashMap<>();
        for (int i=0; i < columnNames.length; i++) {
          String columnName = columnNames[i];
          String columnType = columnTypes[i];
          headerDatatypes.put(columnName, columnType);
          RowRecord.ColumnKind columnKind = getColumnKind(
            columnType,RowRecord.ColumnKind.CONTENT
          );
          headerKinds.put(columnName, columnKind);
        }
      }
    }

    HandleFactoryRegistry getHandleRegistry() {
      return handleRegistry;
    }

    void initAliases(Map<String, ?> cols) {
      if (aliases != null) {
        return;
      }

      Map<String,String> candidates = new HashMap<String,String>();

      Set<String> noncandidates = cols.keySet();
      for (String col: noncandidates.toArray(new String[noncandidates.size()])) {
        String[] parts = col.split("\\.", 3);
        if (parts.length == 1) {
          continue;
        }
        for (int i=1; i < parts.length; i++) {
          int next = i + 1;
          String candidate = (next == parts.length) ?
            parts[i] : parts[i]+"."+parts[next];
          if (noncandidates.contains(candidate)) {
            continue;
          } else if (candidates.containsKey(candidate)) {
            candidates.remove(candidate);
            noncandidates.add(candidate);
            continue;
          }
          candidates.put(candidate, col);
        }
      }

      aliases = candidates;
    }
    <T> boolean hasAlias(Map<String, T> cols, Object colName) {
      if (colName == null) {
        return false;
      }

      initAliases(cols);

      String columnName = (colName instanceof String) ? (String) colName : colName.toString();

      String col = aliases.get(columnName);
      if (col == null) {
        return false;
      }

      cols.put(columnName, cols.get(col));

      return true;
    }

    @Override
    public RowRecord next() {
      RESTServiceResult currentRow = nextRow;
      if (currentRow == null) {
        throw new NoSuchElementException("no next row");
      }

      boolean hasMoreRows = results.hasNext();

      try {
        Map<String, String>               datatypes = null;
        Map<String, RowRecord.ColumnKind> kinds     = null;
        Map<String, Object>               row       = null;

        InputStream  rowStream = currentRow.getContent(new InputStreamHandle()).get();

        // TODO: replace Jackson mapper with binding-sensitive mapper?
        ObjectMapper rowMapper = new ObjectMapper();

        switch(rowStructureStyle) {
          case ARRAY:
            row = new HashMap<String, Object>();

            int i=0;

            switch(datatypeStyle) {
              case HEADER:
                datatypes = headerDatatypes;

                @SuppressWarnings("unchecked")
                List<Object> valueLister = rowMapper.readValue(rowStream, List.class);
                int valueListSize = valueLister.size();

                for (; i < valueListSize; i++) {
                  String columnName = columnNames[i];
                  Object value = valueLister.get(i);

                  row.put(columnName, value);
                  if (value != null) {
                    continue;
                  }

                  RowRecord.ColumnKind columnKind = headerKinds.get(columnName);
                  if (columnKind == RowRecord.ColumnKind.NULL) {
                    continue;
                  }

                  if (kinds == null) {
                    kinds = new HashMap<>();
                    kinds.putAll(headerKinds);
                  }

                  kinds.put(columnName, RowRecord.ColumnKind.NULL);
                }

                if (kinds == null) {
                  kinds = headerKinds;
                }
                break;
              case ROWS:
                datatypes = new HashMap<>();
                kinds     = new HashMap<>();

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> rowLister = rowMapper.readValue(rowStream, List.class);
                int rowListSize = rowLister.size();

                for (; i < rowListSize; i++) {
                  String columnName = columnNames[i];
                  row.put(columnName, getTypedRowValue(datatypes, kinds, columnName, rowLister.get(i)));
                }
                break;
              default:
                throw new MarkLogicInternalException("Row record set with unknown datatype style: "+datatypeStyle);
            }

            for (; i < columnNames.length; i++) {
              String columnName = columnNames[i];
              kinds.put(columnName, RowRecord.ColumnKind.NULL);
              row.put(columnName, null);
            }

            break;
          case OBJECT:
            @SuppressWarnings("unchecked")
            Map<String, Object> mapRow = rowMapper.readValue(rowStream, Map.class);

            switch(datatypeStyle) {
              case HEADER:
                datatypes = headerDatatypes;

                for (Map.Entry<String, RowRecord.ColumnKind> entry: headerKinds.entrySet()) {
                  String columnName = entry.getKey();

                  Object value = mapRow.get(columnName);
                  if (value != null) {
                    continue;
                  }

                  RowRecord.ColumnKind columnKind = entry.getValue();
                  if (columnKind == RowRecord.ColumnKind.NULL) {
                    continue;
                  }

                  if (kinds == null) {
                    kinds = new HashMap<>();
                    kinds.putAll(headerKinds);
                  }

                  kinds.put(columnName, RowRecord.ColumnKind.NULL);
                }

                if (kinds == null) {
                  kinds = headerKinds;
                }
                break;
              case ROWS:
                Map<String, String>               rowDatatypes = new HashMap<>();
                Map<String, RowRecord.ColumnKind> rowKinds     = new HashMap<>();

                mapRow.replaceAll((key, rawBinding) -> {
                  @SuppressWarnings("unchecked")
                  Map<String,Object> binding = (Map<String,Object>) rawBinding;
                  return getTypedRowValue(rowDatatypes, rowKinds, key, binding);
                });

                datatypes = rowDatatypes;
                kinds     = rowKinds;
                break;
              default:
                throw new MarkLogicInternalException("Row record set with unknown datatype style: "+datatypeStyle);
            }

            row = mapRow;
            break;
          default:
            throw new MarkLogicInternalException(
              "Row record set with unknown row structure style: "+rowStructureStyle
            );
        }

        while (hasMoreRows) {
          currentRow = results.next();

          Map<String,List<String>> headers = currentRow.getHeaders();
          List<String> headerList = headers.get("Content-Disposition");
          if (headerList == null || headerList.isEmpty()) {
            break;
          }
          String headerValue = headerList.get(0);
          if (headerValue == null || !headerValue.startsWith("inline; kind=row-attachment")) {
            break;
          }

          headerList = headers.get("Content-ID");
          if (headerList == null || headerList.isEmpty()) {
            break;
          }
          headerValue = headerList.get(0);
          if (headerValue == null || !(headerValue.startsWith("<") && headerValue.endsWith(">"))) {
            break;
          }
          int pos = headerValue.indexOf("[",1);
          if (pos == -1) {
            break;
          }
          String colName = headerValue.substring(1, pos);

// TODO: check column name
          row.put(colName, currentRow);

          hasMoreRows = results.hasNext();
        }

        RowRecordImpl rowRecord = new RowRecordImpl(this);

        rowRecord.init(kinds, datatypes, row);

        if (hasMoreRows) {
          nextRow = currentRow;
        } else {
          close();
        }

        return rowRecord;
      } catch (JsonParseException e) {
        throw new MarkLogicIOException("could not part row record", e);
      } catch (JsonMappingException e) {
        throw new MarkLogicIOException("could not map row record", e);
      } catch (IOException e) {
        throw new MarkLogicIOException("could not read row record", e);
      }
    }

    private Object getTypedRowValue(
      Map<String, String>               datatypes,
      Map<String, RowRecord.ColumnKind> kinds,
      String                            columnName,
      Map<String,Object>                binding
    ) {
      RowRecord.ColumnKind columnKind = null;
      Object value = null;
      String datatype = (String) binding.get("type");
      if (datatype != null) {
        datatypes.put(columnName, datatype);
      }
      columnKind = getColumnKind(datatype, null);
      kinds.put(columnName, columnKind);
      value = (columnKind == RowRecord.ColumnKind.ATOMIC_VALUE) ?
        binding.get("value") : null;

// TODO: for RowRecord.ColumnKind.CONTENT, increment the count of expected nodes and list the column names expecting values?
      return value;
    }
    private RowRecord.ColumnKind getColumnKind(String datatype, RowRecord.ColumnKind defaultKind) {
      if ("cid".equals(datatype)) {
        return RowRecord.ColumnKind.CONTENT;
      } else if ("null".equals(datatype)) {
        return RowRecord.ColumnKind.NULL;
      } else if (datatype.contains(":")) {
        return RowRecord.ColumnKind.ATOMIC_VALUE;
      } else if (datatype != null && defaultKind != null) {
        return defaultKind;
      }
      throw new MarkLogicInternalException("Column value with unsupported datatype: "+datatype);
    }
  }
  abstract static class RowSetHandleBase<T, R extends AbstractReadHandle> extends RowSetBase<T> {
    private R rowHandle = null;
    RowSetHandleBase(
      String rowFormat, RowSetPart datatypeStyle, RowStructure rowStructureStyle,
      RESTServiceResultIterator results, R rowHandle
    ) {
      super(rowFormat, datatypeStyle, rowStructureStyle, results);
      this.rowHandle = rowHandle;
    }

    abstract T makeNextResult(R currentHandle);

    // QUESTION: threading guarantees - multiple handles? precedent?
    @Override
    public T next() {
      RESTServiceResult currentRow = nextRow;
      if (currentRow == null) {
        throw new NoSuchElementException("no next row");
      }

      R currentHandle = rowHandle;

      boolean hasMoreRows = results.hasNext();
      if (hasMoreRows) {
        nextRow = results.next();
      } else {
        close();
      }

      return makeNextResult(currentRow.getContent(currentHandle));
    }
  }
  static class RowSetHandle<T extends StructureReadHandle> extends RowSetHandleBase<T, T> {
    RowSetHandle(
      String rowFormat, RowSetPart datatypeStyle, RowStructure rowStructureStyle,
      RESTServiceResultIterator results, T rowHandle
    ) {
      super(rowFormat, datatypeStyle, rowStructureStyle, results, rowHandle);
    }
    @Override
    T makeNextResult(T currentHandle) {
      return currentHandle;
    }
  }
  static class RowSetObject<T> extends RowSetHandleBase<T, ContentHandle<T>> {
    RowSetObject(
      String rowFormat, RowSetPart datatypeStyle, RowStructure rowStructureStyle,
      RESTServiceResultIterator results, ContentHandle<T> rowHandle) {
      super(rowFormat, datatypeStyle, rowStructureStyle, results, rowHandle);
    }
    @Override
    T makeNextResult(ContentHandle<T> currentHandle) {
      return currentHandle.get();
    }
  }

  static class RowRecordImpl implements RowRecord {
    private static final Map<Class<? extends XsAnyAtomicTypeVal>, Function<String,? extends XsAnyAtomicTypeVal>>
      factories = new HashMap<>();

    private static final Map<Class<? extends XsAnyAtomicTypeVal>,Constructor<?>> constructors = new HashMap<>();

    private Map<String, ColumnKind> kinds     = null;
    private Map<String, String>     datatypes = null;

    private Map<String, Object> row        = null;
    private Map<String, Object> aliasedRow = null;

    private RowSetRecord set = null;

    RowRecordImpl(RowSetRecord set) {
      this.set = set;
    }

    // QUESTION:  threading guarantees - multiple handles? precedent?
    void init(Map<String, ColumnKind> kinds, Map<String, String> datatypes, Map<String, Object> row) {
      this.kinds     = kinds;
      this.datatypes = datatypes;
      this.row       = row;
    }

    @Override
    public ColumnKind getKind(PlanExprCol col) {
      return getKind(getNameForColumn(col));
    }
    @Override
    public ColumnKind getKind(String columnName) {
      if (columnName == null) {
        throw new IllegalArgumentException("cannot get column kind with null name");
      }
      ColumnKind kind = kinds.get(columnName);
      if (kind != null) {
        return kind;
      } else if (kinds.containsKey(columnName)) {
        return ColumnKind.NULL;
      } else if (set.hasAlias(kinds, columnName)) {
        return kinds.get(columnName);
      }
      throw new IllegalArgumentException("no kind for column: "+columnName);
    }

    @Override
    public String getDatatype(PlanExprCol col) {
      return getDatatype(getNameForColumn(col));
    }
    @Override
    public String getDatatype(String columnName) {
      if (columnName == null) {
        throw new IllegalArgumentException("cannot get column datatype with null name");
      }
      String datatype = datatypes.get(columnName);
      if (datatype != null) {
        return datatype;
      } else if (datatypes.containsKey(columnName)) {
        return null;
      } else if (set.hasAlias(datatypes, columnName)) {
        return datatypes.get(columnName);
      }
      throw new IllegalArgumentException("no datatype for column: "+columnName);
    }

    // supported operations for unmodifiable map
    @Override
    public boolean containsKey(Object key) {
      if (aliasedRow == null) {
        boolean isContained = row.containsKey(key);
        if (isContained) {
          return isContained;
        }

        aliasedRow = new HashMap<String, Object>(row);
      }

      return set.hasAlias(aliasedRow, key);
    }
    @Override
    public boolean containsValue(Object value) {
      return row.containsValue(value);
    }
    @Override
    public Set<Entry<String, Object>> entrySet() {
      return row.entrySet();
    }
    @Override
    public Object get(Object key) {
      if (key == null) {
        throw new IllegalArgumentException("cannot get column value with null name");
      }

      Map<String, Object> valueRow = (aliasedRow == null) ? row : aliasedRow;

      Object value = valueRow.get(key);
      if (value != null) {
        return value;
      } else if (valueRow.containsKey(key)) {
        return null;
      } else if (aliasedRow == null) {
        aliasedRow = new HashMap<String, Object>(row);
      }

      if (set.hasAlias(aliasedRow, key)) {
        return aliasedRow.get(key);
      }

// TODO: get ColumnKind.CONTENT of binary as byte[] - getKind()?
// TODO: get ColumnKind.CONTENT if not binary as String - getKind()?
      return null;
    }
    @Override
    public boolean isEmpty() {
      return row.isEmpty();
    }
    @Override
    public Set<String> keySet() {
      return row.keySet();
    }
    @Override
    public Collection<Object> values() {
      return row.values();
    }
    @Override
    public int size() {
      return row.size();
    }

    // unsupported operations for unmodifiable map
    @Override
    public Object put(String key, Object value) {
      throw new UnsupportedOperationException("cannot modify row record");
    }
    @Override
    public Object remove(Object key) {
      throw new UnsupportedOperationException("cannot modify row record");
    }
    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
      throw new UnsupportedOperationException("cannot modify row record");
    }
    @Override
    public void clear() {
      throw new UnsupportedOperationException("cannot modify row record");
    }

    // literal casting convenience getters
    @Override
    public boolean getBoolean(PlanExprCol col) {
      return getBoolean(getNameForColumn(col));
    }
    @Override
    public boolean getBoolean(String columnName) {
      return asBoolean(columnName, get(columnName));
    }
    @Override
    public byte getByte(PlanExprCol col) {
      return getByte(getNameForColumn(col));
    }
    @Override
    public byte getByte(String columnName) {
      return asByte(columnName, get(columnName));
    }
    @Override
    public double getDouble(PlanExprCol col) {
      return getDouble(getNameForColumn(col));
    }
    @Override
    public double getDouble(String columnName) {
      return asDouble(columnName, get(columnName));
    }
    @Override
    public float getFloat(PlanExprCol col) {
      return getFloat(getNameForColumn(col));
    }
    @Override
    public float getFloat(String columnName) {
      return asFloat(columnName, get(columnName));
    }
    @Override
    public int getInt(PlanExprCol col) {
      return getInt(getNameForColumn(col));
    }
    @Override
    public int getInt(String columnName) {
      return asInt(columnName, get(columnName));
    }
    @Override
    public long getLong(PlanExprCol col) {
      return getLong(getNameForColumn(col));
    }
    @Override
    public long getLong(String columnName) {
      return asLong(columnName, get(columnName));
    }
    @Override
    public short getShort(PlanExprCol col) {
      return getShort(getNameForColumn(col));
    }
    @Override
    public short getShort(String columnName) {
      return asShort(columnName, get(columnName));
    }
    @Override
    public String getString(PlanExprCol col) {
      return getString(getNameForColumn(col));
    }
    @Override
    public String getString(String columnName) {
      try {
        return asString(get(columnName));
      } catch(NodeNotAStringException e) {
        throw new IllegalArgumentException("value for column \""+columnName+"\" not a string");
      }
    }

    private boolean asBoolean(String columnName, Object value) {
      if (value instanceof Boolean) {
        return ((Boolean) value).booleanValue();
      }
      throw new IllegalStateException("column "+columnName+" does not have a boolean value");
    }
    private byte asByte(String columnName, Object value) {
      if (value instanceof Number) {
        return ((Number) value).byteValue();
      }
      throw new IllegalStateException("column "+columnName+" does not have a byte value");
    }
    private double asDouble(String columnName, Object value) {
      if (value instanceof Number) {
        return ((Number) value).doubleValue();
      }
      throw new IllegalStateException("column "+columnName+" does not have a double value");
    }
    private float asFloat(String columnName, Object value) {
      if (value instanceof Number) {
        return ((Number) value).floatValue();
      }
      throw new IllegalStateException("column "+columnName+" does not have a float value");
    }
    private int asInt(String columnName, Object value) {
      if (value instanceof Number) {
        return ((Number) value).intValue();
      }
      throw new IllegalStateException("column "+columnName+" does not have an integer value");
    }
    private long asLong(String columnName, Object value) {
      if (value instanceof Number) {
        return ((Number) value).longValue();
      }
      throw new IllegalStateException("column "+columnName+" does not have a long value");
    }
    private short asShort(String columnName, Object value) {
      if (value instanceof Number) {
        return ((Number) value).shortValue();
      }
      throw new IllegalStateException("column "+columnName+" does not have a short value");
    }
    /**
     * @throws NodeNotAStringException when the value is a node but not a single text node
     *   (with content-type "text/plain")
     */
    private String asString(Object value) throws NodeNotAStringException {
      if (value == null || value instanceof String) {
        return (String) value;
      }
      if (value instanceof RESTServiceResult) {
        RESTServiceResult result = (RESTServiceResult) value;
        if ( result.getMimetype() != null && result.getMimetype().startsWith("text/plain") ) {
          return result.getContent(new StringHandle()).get();
        } else {
          throw new NodeNotAStringException();
        }
      }
      return value.toString();
    }

    private RESTServiceResult getServiceResult(String columnName) {
      Object val = get(columnName);
      if (val instanceof RESTServiceResult) {
        return (RESTServiceResult) val;
      }
      return null;
    }

    @Override
    public <T extends XsAnyAtomicTypeVal> T getValueAs(PlanExprCol col, Class<T> as) {
      return getValueAs(getNameForColumn(col), as);
    }

    @Override
    public <T extends XsAnyAtomicTypeVal> T getValueAs(String columnName, Class<T> as) {
      if (as == null) {
        throw new IllegalArgumentException("cannot construct "+columnName+" value with null class");
      }

      Object value = get(columnName);
      if (value == null) {
        return null;
      }

			/* NOTE: use if refactor away from Jackson ObjectMapper to value construction
			if (as.isInstance(value)) {
				return as.cast(value);
			}
			*/

      try {
        String valueStr = asString(value);

        Function<String,? extends XsAnyAtomicTypeVal> factory = getFactory(as);
        if (factory != null) {
          return as.cast(factory.apply(valueStr));
        }

        // fallback
        @SuppressWarnings("unchecked")
        Constructor<T> constructor = (Constructor<T>) constructors.get(as);
        if (constructor == null) {
          constructor = as.getConstructor(String.class);
          constructors.put(as, constructor);
        }

        return constructor.newInstance(valueStr);
      } catch(NodeNotAStringException e) {
        throw new IllegalArgumentException("column \""+columnName+"\" is a node, not an atomic");
      } catch(NoSuchMethodException e) {
        throw new IllegalArgumentException("cannot construct "+columnName+" value as class: "+as.getName());
      } catch (InstantiationException e) {
        throw new MarkLogicBindingException("could not construct value as class: "+as.getName(), e);
      } catch (IllegalAccessException e) {
        throw new MarkLogicBindingException("could not construct value as class: "+as.getName(), e);
      } catch (IllegalArgumentException e) {
        throw new MarkLogicBindingException("could not construct value as class: "+as.getName(), e);
      } catch (InvocationTargetException e) {
        throw new MarkLogicBindingException("could not construct value as class: "+as.getName(), e);
      }
    }
    <T extends XsAnyAtomicTypeVal> Function<String,? extends XsAnyAtomicTypeVal> getFactory(Class<T> as) {
      Function<String,? extends XsAnyAtomicTypeVal> factory = factories.get(as);
      if (factory != null) {
        return factory;
      }

      // NOTE: more general first to avoid false fallback
      if (as.isAssignableFrom(XsValueImpl.DecimalValImpl.class)) {
        factory = XsValueImpl.DecimalValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.IntegerValImpl.class)) {
        factory = XsValueImpl.IntegerValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.LongValImpl.class)) {
        factory = XsValueImpl.LongValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.IntValImpl.class)) {
        factory = XsValueImpl.IntValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.ShortValImpl.class)) {
        factory = XsValueImpl.ShortValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.ByteValImpl.class)) {
        factory = XsValueImpl.ByteValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.UnsignedLongValImpl.class)) {
        factory = XsValueImpl.UnsignedLongValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.UnsignedIntValImpl.class)) {
        factory = XsValueImpl.UnsignedIntValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.UnsignedShortValImpl.class)) {
        factory = XsValueImpl.UnsignedShortValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.UnsignedByteValImpl.class)) {
        factory = XsValueImpl.UnsignedByteValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.DoubleValImpl.class)) {
        factory = XsValueImpl.DoubleValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.FloatValImpl.class)) {
        factory = XsValueImpl.FloatValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.DateTimeValImpl.class)) {
        factory = XsValueImpl.DateTimeValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.DateValImpl.class)) {
        factory = XsValueImpl.DateValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.TimeValImpl.class)) {
        factory = XsValueImpl.TimeValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.AnyURIValImpl.class)) {
        factory = XsValueImpl.AnyURIValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.BooleanValImpl.class)) {
        factory = XsValueImpl.BooleanValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.DayTimeDurationValImpl.class)) {
        factory = XsValueImpl.DayTimeDurationValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.GDayValImpl.class)) {
        factory = XsValueImpl.GDayValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.GMonthValImpl.class)) {
        factory = XsValueImpl.GMonthValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.GMonthDayValImpl.class)) {
        factory = XsValueImpl.GMonthDayValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.GYearValImpl.class)) {
        factory = XsValueImpl.GYearValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.GYearMonthValImpl.class)) {
        factory = XsValueImpl.GYearMonthValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.StringValImpl.class)) {
        factory = XsValueImpl.StringValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.YearMonthDurationValImpl.class)) {
        factory = XsValueImpl.YearMonthDurationValImpl::new;
      } else if (as.isAssignableFrom(XsValueImpl.QNameValImpl.class)) {
        factory = XsValueImpl.QNameValImpl::valueOf;
      }

      if (factory != null) {
        factories.put(as,factory);
      }

      return factory;
    }

    @Override
    public Format getContentFormat(PlanExprCol col) {
      return getContentFormat(getNameForColumn(col));
    }
    @Override
    public Format getContentFormat(String columnName) {
      String mimetype = getContentMimetype(columnName);
      if (mimetype == null) {
        return null;
      }
      switch(mimetype) {
        case "application/json":
          return Format.JSON;
        case "text/plain":
          return Format.TEXT;
        case "application/xml":
        case "application/xml-external-parsed-entity":
          return Format.XML;
        default:
          return Format.BINARY;
      }
    }
    @Override
    public String getContentMimetype(PlanExprCol col) {
      return getContentMimetype(getNameForColumn(col));
    }
    @Override
    public String getContentMimetype(String columnName) {
      if (columnName == null) {
        throw new IllegalArgumentException("cannot get column mime type with null name");
      }
      RESTServiceResult nodeResult = getServiceResult(columnName);
      if (nodeResult == null) {
        return null;
      }
      return nodeResult.getMimetype();
    }
    @Override
    public <T extends AbstractReadHandle> T getContent(PlanExprCol col, T contentHandle) {
      return getContent(getNameForColumn(col), contentHandle);
    }
    @Override
    public <T extends AbstractReadHandle> T getContent(String columnName, T contentHandle) {
      if (columnName == null) {
        throw new IllegalArgumentException("cannot get column node with null name");
      }
      RESTServiceResult nodeResult = getServiceResult(columnName);
      if (nodeResult == null) {
        return null;
      }
      return nodeResult.getContent(contentHandle);
    }
    @Override
    public <T> T getContentAs(PlanExprCol col, Class<T> as) {
      return getContentAs(getNameForColumn(col), as);
    }
    @Override
    public <T> T getContentAs(String columnName, Class<T> as) {
      if (as == null) {
        throw new IllegalArgumentException("Must specify a class for content with a registered handle");
      }

      ContentHandle<T> handle = set.getHandleRegistry().makeHandle(as);
      if (handle == null) {
        throw new IllegalArgumentException("No handle registered for class: "+as.getName());
      }

      handle = getContent(columnName, handle);

      T content = (handle == null) ? null : handle.get();

      return content;
    }

    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder();

      buf.append("{");

      boolean isFirst = true;
      if (row != null) {
        for (String colName: row.keySet()) {
          if (isFirst) {
            buf.append("\n    ");
            isFirst = false;
          } else {
            buf.append(",\n    ");
          }

          RowRecord.ColumnKind colKind = kinds.get(colName);
          String               colType = datatypes.get(colName);

          buf.append(colName);
          buf.append(":{kind: \"");
          buf.append(colKind.name());
          if (!"cid".equals(colType)) {
            buf.append("\", type: \"");
            buf.append(colType);
          }
          buf.append("\", ");

          switch(colKind) {
            case ATOMIC_VALUE:
              buf.append("value: ");

              String colVal = getString(colName);

              switch(colType) {
                case "xs:boolean":
                case "xs:byte":
                case "xs:decimal":
                case "xs:double":
                case "xs:float":
                case "xs:int":
                case "xs:integer":
                case "xs:long":
                case "xs:short":
                case "xs:unsignedByte":
                case "xs:unsignedInt":
                case "xs:unsignedLong":
                case "xs:unsignedShort":
                  buf.append(colVal);
                  break;
                default:
                  if (colVal == null) {
                    buf.append("null");
                  } else {
                    buf.append("\"");
                    buf.append(colVal.replace("\"", "\\\""));
                    buf.append("\"");
                  }
                  break;
              }
              break;
            case CONTENT:
              buf.append("format: \"");
              buf.append(getContentFormat(colName).name().toLowerCase());
              buf.append("\", mimetype: \"");
              buf.append(getContentMimetype(colName));
              buf.append("\"");
              break;
            case NULL:
              buf.append("value: null");
              break;
            default:
              throw new InternalError("unknown value kind: "+colKind);
          }

          buf.append("}");
        }
      }

      if (!isFirst) {
        buf.append("\n    ");
      }
      buf.append("}\n");

      return buf.toString();
    }

    private String getNameForColumn(PlanExprCol col) {
      if (col == null) {
        throw new IllegalArgumentException("null column");
      }
      if (!(col instanceof PlanBuilderSubImpl.ColumnNamer)) {
        throw new IllegalArgumentException("invalid column class: "+col.getClass().getName());
      }
      return ((PlanBuilderSubImpl.ColumnNamer) col).getColName();
    }
  }
  static class RawPlanDefinitionImpl implements RawPlanDefinition, PlanBuilderBaseImpl.RequestPlan {
    private Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params = null;
    private JSONWriteHandle handle = null;
    RawPlanDefinitionImpl(JSONWriteHandle handle) {
      setHandle(handle);
    }
    private RawPlanDefinitionImpl(
      JSONWriteHandle handle,
      Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params) {
      this(handle);
      this.params = params;
    }

    @Override
    public Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> getParams() {
      return params;
    }

    @Override
    public Plan bindParam(PlanParamExpr param, boolean literal) {
      return bindParam(param, new XsValueImpl.BooleanValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, byte literal) {
      return bindParam(param, new XsValueImpl.ByteValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, double literal) {
      return bindParam(param, new XsValueImpl.DoubleValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, float literal) {
      return bindParam(param, new XsValueImpl.FloatValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, int literal) {
      return bindParam(param, new XsValueImpl.IntValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, long literal) {
      return bindParam(param, new XsValueImpl.LongValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, short literal) {
      return bindParam(param, new XsValueImpl.ShortValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, String literal) {
      return bindParam(param, new XsValueImpl.StringValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, PlanParamBindingVal literal) {
      if (!(param instanceof PlanBuilderBaseImpl.PlanParamBase)) {
        throw new IllegalArgumentException("cannot set parameter that doesn't extend base");
      }
      if (!(literal instanceof XsValueImpl.AnyAtomicTypeValImpl)) {
        throw new IllegalArgumentException("cannot set value with unknown implementation");
      }

      Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> nextParams = new HashMap<>();
      if (this.params != null) {
        nextParams.putAll(this.params);
      }

      nextParams.put((PlanBuilderBaseImpl.PlanParamBase) param, (XsValueImpl.AnyAtomicTypeValImpl) literal);

      return new RawPlanDefinitionImpl(getHandle(), nextParams);
    }

    @Override
    public JSONWriteHandle getHandle() {
      return handle;
    }
    @Override
    public void setHandle(JSONWriteHandle handle) {
      if (handle == null) {
        throw new IllegalArgumentException("Must specify handle for reading raw plan");
      }
      if (!(handle instanceof BaseHandle)) {
        throw new IllegalArgumentException(
              "Cannot provide raw plan definition with invalid handle having class "+handle.getClass().getName()
        );
      }
      ((BaseHandle) handle).setFormat(Format.JSON);
      this.handle = handle;
    }
    @Override
    public RawPlanDefinition withHandle(JSONWriteHandle handle) {
      setHandle(handle);
      return this;
    }
  }

  private static class NodeNotAStringException extends Exception {
  }
}
