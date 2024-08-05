/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.marklogic.client.*;
import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.impl.RESTServices.RESTServiceResult;
import com.marklogic.client.impl.RESTServices.RESTServiceResultIterator;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.*;
import com.marklogic.client.row.*;
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
  private Integer optimize;
  private String traceLabel;
  private boolean update;

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
  public String getTraceLabel() {
    return traceLabel;
  }
  @Override
  public void setTraceLabel(String label) {
    this.traceLabel = label;
  }
  @Override
  public Integer getOptimize() {
    return this.optimize;
  }
  @Override
  public void setOptimize(Integer value) {
    this.optimize = value;
  }

	@Override
	public RowManager withUpdate(boolean update) {
		this.update = update;
		return this;
	}

	@Override
  public RawPlanDefinition newRawPlanDefinition(JSONWriteHandle handle) {
    return new RawPlanDefinitionImpl(handle);
  }

  @Override
  public RawQueryDSLPlan newRawQueryDSLPlan(TextWriteHandle handle) {
    return new RawQueryDSLPlanImpl(handle);
  }
  @Override
  public RawSQLPlanImpl newRawSQLPlan(TextWriteHandle handle) {
    return new RawSQLPlanImpl(handle);
  }
  @Override
  public RawSPARQLSelectPlanImpl newRawSPARQLSelectPlan(TextWriteHandle handle) {
    return new RawSPARQLSelectPlanImpl(handle);
  }

  private RowsParamsBuilder newRowsParamsBuilder(PlanBuilderBaseImpl.RequestPlan plan) {
    return new RowsParamsBuilder(plan)
        .withOptimize(this.optimize)
        .withTraceLabel(this.traceLabel);
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
    if (resultsHandle == null) {
      throw new IllegalArgumentException("Must specify a handle to read the row result document");
    }
    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);
    AbstractWriteHandle astHandle = requestPlan.getHandle();
    RequestParameters params = newRowsParamsBuilder(requestPlan)
        .withColumnTypes(getDatatypeStyle())
        .withOutput(getRowStructureStyle())
        .getRequestParameters();
    return services.postResource(requestLogger, determinePath(), transaction, params, astHandle, resultsHandle);
  }

  private String determinePath() {
	  return this.update ? "rows/update" : "rows";
  }

  @Override
  public RowSet<RowRecord> resultRows(Plan plan) {
    return resultRows(plan, (Transaction) null);
  }

  @Override
  public RowSet<RowRecord> resultRows(Plan plan, Transaction transaction) {
    RowSetPart datatypeStyle = getDatatypeStyle();
    RowStructure rowStructureStyle = getRowStructureStyle();

    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);
    RequestParameters params = newRowsParamsBuilder(requestPlan)
        .withRowFormat("json")
        .withNodeColumns("reference")
        .withColumnTypes(datatypeStyle)
        .withOutput(rowStructureStyle)
        .getRequestParameters();

    RESTServiceResultIterator iter = submitPlan(requestPlan, params, transaction);

    RowSetRecord rowset = new RowSetRecord(
      "json", datatypeStyle, rowStructureStyle, iter, handleRegistry
    );
    rowset.init();

    return rowset;
  }

  @Override
  public void execute(Plan plan) {
    execute(plan, null);
  }

  @Override
  public void execute(Plan plan, Transaction transaction) {
    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);
    RequestParameters params = newRowsParamsBuilder(requestPlan).getRequestParameters();
    RESTServiceResultIterator iter = submitPlan(requestPlan, params, transaction);
    if (iter != null) {
      iter.close();
    }
  }

  @Override
  public <T extends StructureReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle) {
    return resultRows(plan, rowHandle, null);
  }

  @Override
  public <T extends StructureReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle, Transaction transaction) {
    RowSetPart datatypeStyle = getDatatypeStyle();
    RowStructure rowStructureStyle = getRowStructureStyle();
    String rowFormat = getRowFormat(rowHandle);

    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);

	RowsParamsBuilder rowsParamsBuilder = newRowsParamsBuilder(requestPlan)
		.withRowFormat(rowFormat)
		.withNodeColumns("inline")
		.withColumnTypes(datatypeStyle)
		.withOutput(rowStructureStyle);

	if (rowHandle instanceof BaseHandle) {
		rowsParamsBuilder.withTimestamp(((BaseHandle)rowHandle).getPointInTimeQueryTimestamp());
	}

    RequestParameters params = rowsParamsBuilder.getRequestParameters();
    RESTServiceResultIterator iter = submitPlan(requestPlan, params, transaction);
    RowSetHandle<T> rowset = new RowSetHandle<>(rowFormat, datatypeStyle, rowStructureStyle, iter, rowHandle);
    rowset.init();
    return rowset;
  }

  @Override
  public <T> RowSet<T> resultRowsAs(Plan plan, Class<T> as) {
    return resultRowsAs(plan, as, (Transaction) null);
  }
  @Override
  public <T> RowSet<T> resultRowsAs(Plan plan, Class<T> as, Transaction transaction) {
    ContentHandle<T> rowHandle = handleFor(as);
    String rowFormat = getRowFormat(rowHandle);

    RowSetPart datatypeStyle = getDatatypeStyle();
    RowStructure rowStructureStyle = getRowStructureStyle();

    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);
    RequestParameters params = newRowsParamsBuilder(requestPlan)
        .withRowFormat(rowFormat)
        .withNodeColumns("inline")
        .withColumnTypes(datatypeStyle)
        .withOutput(rowStructureStyle)
        .getRequestParameters();

    RESTServiceResultIterator iter = submitPlan(requestPlan, params, transaction);
    RowSetObject<T> rowset = new RowSetObject<>(rowFormat, datatypeStyle, rowStructureStyle, iter, rowHandle);
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

    return services.postResource(requestLogger, determinePath(), null, params, astHandle, resultsHandle);
  }

  @Override
  public <T> T explainAs(Plan plan, Class<T> as) {
    ContentHandle<T> handle = handleFor(as);
    if (explain(plan, (StructureReadHandle) handle) == null) {
      return null;
    }

    return handle.get();
  }

  @Override
  public <T extends XMLReadHandle> T generateView(PlanBuilder.PreparePlan plan, String schema, String view, T resultsHandle) {
    if (resultsHandle == null) {
      throw new IllegalArgumentException("Must specify a handle to generate a view for the plan");
    } else if (schema == null || schema.length() == 0) {
      throw new IllegalArgumentException("Must specify a schema name to generate a view for the plan");
    } else if (view == null || view.length() == 0) {
      throw new IllegalArgumentException("Must specify a view name to generate a view for the plan");
    }

    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);
    AbstractWriteHandle astHandle = requestPlan.getHandle();

    RequestParameters params = new RequestParameters();
    params.add("output",     "generateView");
    params.add("schemaName", "schema");
    params.add("viewName",   "view");

    return services.postResource(requestLogger, "rows", null, params, astHandle, resultsHandle);
  }
  @Override
  public <T> T generateViewAs(PlanBuilder.PreparePlan plan, String schema, String view, Class<T> as) {
    ContentHandle<T> handle = handleFor(as);
    if (generateView(plan, schema, view, (XMLReadHandle) handle) == null) {
      return null;
    }

    return handle.get();
  }

  @Override
  public <T extends JSONReadHandle> T columnInfo(PlanBuilder.Plan plan, T resultsHandle) {
    if (resultsHandle == null) {
      throw new IllegalArgumentException("Must specify a handle to generate a view for the plan");
    }

    PlanBuilderBaseImpl.RequestPlan requestPlan = checkPlan(plan);
    AbstractWriteHandle astHandle = requestPlan.getHandle();

    RequestParameters params = new RequestParameters();
    params.add("output",     "columnInfo");

    return services.postResource(requestLogger, "rows", null, params, astHandle, resultsHandle);
  }
  @Override
  public <T> T columnInfoAs(PlanBuilder.Plan plan, Class<T> as) {
    ContentHandle<T> handle = handleFor(as);
    if (!(handle instanceof JSONReadHandle))
      throw new IllegalArgumentException("The handle is not an instance of JSONReadHandle.");
    if (columnInfo(plan, (JSONReadHandle) handle) == null) {
      return null;
    }

    return handle.get();
  }

	@Override
	public <T extends JSONReadHandle> T graphql(JSONWriteHandle query, T resultsHandle) {
		if (resultsHandle == null) {
			throw new IllegalArgumentException("Must specify a handle for the results of the GraphQL query");
		}
		RequestParameters params = new RequestParameters();
		// Must force the MIME type before passing this to OkHttpServices - which does the exact same check below,
		// requiring that the query handle be an instance of HandleImplementation.
		HandleAccessor.checkHandle(query, "write").setMimetype("application/graphql");
		return services.postResource(requestLogger, "rows/graphql", null, params, query, resultsHandle);
	}

	@Override
	public <T> T graphqlAs(JSONWriteHandle query, Class<T> as) {
	  ContentHandle<T> handle = handleFor(as);
	  if (!(handle instanceof JSONReadHandle)) {
		  throw new IllegalArgumentException("The handle is not an instance of JSONReadHandle.");
	  }
	  if (graphql(query, (JSONReadHandle) handle) == null) {
		  return null;
	  }
	  return handle.get();
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

  private RESTServiceResultIterator submitPlan(PlanBuilderBaseImpl.RequestPlan requestPlan, RequestParameters params, Transaction transaction) {
    AbstractWriteHandle astHandle = requestPlan.getHandle();
    List<ContentParam> contentParams = requestPlan.getContentParams();
	final String path = determinePath();
	try {
		if (contentParams != null && !contentParams.isEmpty()) {
			contentParams.add(new ContentParam(new PlanBuilderBaseImpl.PlanParamBase("query"), astHandle, null));
			return services.postMultipartForm(requestLogger, path, transaction, params, contentParams);
		}
		return services.postIteratedResource(requestLogger, path, transaction, params, astHandle);
	} catch (FailedRequestException ex) {
		String message = ex.getMessage();
		if (message != null && message.contains("RESTAPI-UPDATEFROMQUERY")) {
			String betterMessage = "The Optic plan is attempting an update but was sent to the wrong REST API endpoint. " +
				"You must invoke `withUpdate(true)` on the instance of com.marklogic.client.row.RowManager that you " +
				"are using to submit the plan";
			throw new FailedRequestException(betterMessage, ex.getFailedRequest());
		}
		throw ex;
	}
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
        Map<String, Object>               row       = new HashMap<>();

        InputStream rowStream = currentRow.getContent(new InputStreamHandle()).get();

        ObjectMapper rowMapper = new ObjectMapper();
        JsonNode rowNode = rowMapper.readTree(rowStream);

        switch(rowStructureStyle) {
          case ARRAY:
            int i=0;

            switch(datatypeStyle) {
              case HEADER:
                datatypes = headerDatatypes;

                for (JsonNode columnNode: rowNode) {
                  String columnName = columnNames[i];
                  i++;

                  Object value = getColumnValue(columnName, columnNode);
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

                for (JsonNode columnBinding: rowNode) {
                  String columnName = columnNames[i];
                  Object value = getTypedRowValue(datatypes, kinds, columnName, columnBinding);
                  row.put(columnName, value);
                  i++;
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
            Iterator<Map.Entry<String,JsonNode>> fields = rowNode.fields();

            switch(datatypeStyle) {
              case HEADER:
                datatypes = headerDatatypes;

                while (fields.hasNext()) {
                  Map.Entry<String,JsonNode> field = fields.next();
                  String   columnName = field.getKey();
                  JsonNode columnNode = field.getValue();
                  Object value = getColumnValue(columnName, columnNode);
                  row.put(columnName, value);
                }

                for (Map.Entry<String, RowRecord.ColumnKind> entry: headerKinds.entrySet()) {
                  String columnName = entry.getKey();

                  Object value = row.get(columnName);
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
                datatypes = new HashMap<>();
                kinds     = new HashMap<>();

                while (fields.hasNext()) {
                  Map.Entry<String,JsonNode> field = fields.next();
                  String   columnName    = field.getKey();
                  JsonNode columnBinding = field.getValue();
                  Object value = getTypedRowValue(datatypes, kinds, columnName, columnBinding);
                  row.put(columnName, value);
                }
                break;
              default:
                throw new MarkLogicInternalException("Row record set with unknown datatype style: "+datatypeStyle);
            }
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

    private Object getColumnValue(String columnName, JsonNode columnNode) {
      JsonNodeType nodeType = columnNode.getNodeType();
      switch(nodeType) {
        case NULL:
          return null;
        case ARRAY:
        case BOOLEAN:
        case NUMBER:
        case OBJECT:
        case STRING:
          return columnNode;
        case BINARY:
        case MISSING:
        case POJO:
          throw new MarkLogicIOException(columnName+" column with invalid node type: "+nodeType);
        default:
          throw new MarkLogicIOException(columnName+" column with unknown node type: "+nodeType);
      }
    }
    private Object getTypedRowValue(
      Map<String, String>               datatypes,
      Map<String, RowRecord.ColumnKind> kinds,
      String                            columnName,
      JsonNode                          binding
    ) {
      RowRecord.ColumnKind columnKind = null;
      Object value = null;
      String datatype = binding.get("type").asText();
      if (datatype != null) {
        datatypes.put(columnName, datatype);
      }
      columnKind = getColumnKind(datatype, null);
      kinds.put(columnName, columnKind);
      value = (columnKind == RowRecord.ColumnKind.NULL || datatype == null || "cid".equals(datatype)) ?
          null : getColumnValue(columnName, binding.get("value"));

// TODO: for RowRecord.ColumnKind.CONTENT, increment the count of expected nodes and list the column names expecting values?
      return value;
    }
    private RowRecord.ColumnKind getColumnKind(String datatype, RowRecord.ColumnKind defaultKind) {
      if (datatype == null) {
        throw new MarkLogicInternalException("Column value with null datatype");
      }
      switch(datatype) {
        case "array":
          return RowRecord.ColumnKind.CONTAINER_VALUE;
        case "cid":
          return RowRecord.ColumnKind.CONTENT;
        case "null":
          return RowRecord.ColumnKind.NULL;
        case "object":
          return RowRecord.ColumnKind.CONTAINER_VALUE;
        default:
          if (datatype.contains(":")) {
            return RowRecord.ColumnKind.ATOMIC_VALUE;
          } else if (datatype != null && defaultKind != null) {
            return defaultKind;
          }
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
      return asString(columnName, get(columnName));
    }

    private boolean asBoolean(String columnName, Object value) {
      return asPrimitiveValueNode(columnName, value).asBoolean();
    }
    private byte asByte(String columnName, Object value) {
      return (byte) asPrimitiveValueNode(columnName, value).asInt();
    }
    private double asDouble(String columnName, Object value) {
      return asPrimitiveValueNode(columnName, value).asDouble();
    }
    private float asFloat(String columnName, Object value) {
      return (float) asPrimitiveValueNode(columnName, value).asDouble();
    }
    private int asInt(String columnName, Object value) {
      return asPrimitiveValueNode(columnName, value).asInt();
    }
    private long asLong(String columnName, Object value) {
      return asPrimitiveValueNode(columnName, value).asLong();
    }
    private short asShort(String columnName, Object value) {
      return (short) asPrimitiveValueNode(columnName, value).asInt();
    }
    private String asString(String columnName, Object value)  {
      if (value == null) {
        return null;
      } else if (value instanceof JsonNode) {
        JsonNode node = (JsonNode) value;
        if (node.isNull()) {
          return null;
        } else if (node.isValueNode()) {
          return node.asText();
        } else if (node.isContainerNode()) {
          return node.toPrettyString();
        }
      } else if (value instanceof RESTServiceResult) {
        RESTServiceResult result = (RESTServiceResult) value;
        Format format = result.getFormat();
        if (format != null && format != Format.BINARY) {
          return result.getContent(new StringHandle()).get();
        }
      }
      throw new IllegalStateException("column "+columnName+" cannot convert to string");
    }

    private JsonNode asPrimitiveValueNode(String columnName, Object value) {
      JsonNode node = asAtomicValueNode(columnName, value);
      if (node == null) {
        throw new IllegalStateException("column "+columnName+" has null instead of primitive value");
      }
      return node;
    }
    private JsonNode asAtomicValueNode(String columnName, Object value) {
      JsonNode node = asJsonNode(columnName, value);
      if (node != null && !node.isValueNode()) {
        throw new IllegalStateException("column "+columnName+" does not have an atomic value");
      }
      return node;
    }
    private JsonNode asJsonNode(String columnName, Object value) {
      if (value == null) {
        return null;
      }
      JsonNode node;
      if (value instanceof JsonNode) {
        node = (JsonNode) value;
      } else if (value instanceof RESTServiceResult && getContentFormat(columnName) == Format.JSON) {
        node = ((RESTServiceResult) value).getContent(new JacksonHandle()).get();
      } else {
        throw new IllegalStateException("column "+columnName+" does not have a value");
      }
      return node.isNull() ? null : node;
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

      try {
        JsonNode node = asAtomicValueNode(columnName, get(columnName));
        if (node == null) {
          return null;
        }

        String valueStr = node.asText();
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
    public JsonNode getContainer(PlanExprCol col) {
      return getContainer(getNameForColumn(col));
    }
    @Override
    public JsonNode getContainer(String columnName) {
      return asJsonNode(columnName, get(columnName));
    }
    @Override
    public <T extends JSONReadHandle> T getContainer(PlanExprCol col, T containerHandle) {
      return getContainer(getNameForColumn(col), containerHandle);
    }
    @Override
    public <T extends JSONReadHandle> T getContainer(String columnName, T containerHandle) {
      Object value = get(columnName);
      if (value == null) {
        return null;
      } else if (value instanceof JsonNode) {
        return NodeConverter.jsonNodeToHandle((JsonNode) value, containerHandle);
      } else if (value instanceof RESTServiceResult) {
        return ((RESTServiceResult) value).getContent(containerHandle);
      }
      throw new IllegalStateException("column "+columnName+" does not have a container value");
    }
    @Override
    public <T> T getContainerAs(PlanExprCol col, Class<T> as) {
      return getContainerAs(getNameForColumn(col), as);
    }
    @Override
    public <T> T getContainerAs(String columnName, Class<T> as) {
      ContentHandle<T> handle = getHandle(as);
      if (!JSONReadHandle.class.isInstance(handle)) {
        throw new IllegalArgumentException("handle cannot read JSON: "+handle.getClass().getSimpleName());
      }
      getContainer(columnName, (JSONReadHandle) handle);
      return handle.get();
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
      ContentHandle<T> handle = getHandle(as);
      getContent(columnName, handle);
      return handle.get();
    }
    private <T> ContentHandle<T> getHandle(Class<T> as) {
      if (as == null) {
        throw new IllegalArgumentException("Must specify a class for content with a registered handle");
      }

      ContentHandle<T> handle = set.getHandleRegistry().makeHandle(as);
      if (handle == null) {
        throw new IllegalArgumentException("No handle registered for class: "+as.getName());
      }

      return handle;
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
              String atomicVal = getString(colName);
              buf.append("value: ");
              if (atomicVal == null) {
                buf.append("null");
              } else {
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
                    buf.append(atomicVal);
                    break;
                  default:
                    buf.append("\"");
                    buf.append(atomicVal.replace("\"", "\\\""));
                    buf.append("\"");
                    break;
                }
              }
              break;
            case CONTAINER_VALUE:
              String containerVal = getString(colName);
              buf.append("value: ");
              buf.append((containerVal == null) ? "null" : containerVal);
              break;
            case CONTENT:
              buf.append("format: \"");
              Format contentFormat = getContentFormat(colName);
              buf.append((contentFormat == null) ? "unknown" : contentFormat.name().toLowerCase());
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

  static abstract class RawPlanImpl<W extends AbstractWriteHandle> implements RawPlan, PlanBuilderBaseImpl.RequestPlan {
    // TODO There's some significant duplication here with PlanSubImpl that ideally can be factored out
    private Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params = null;
    private List<ContentParam> contentParams;
    private W handle;
    private RawPlanImpl(W handle) {
      setHandle(handle);
    }
    private RawPlanImpl(
            W handle,
            Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
            List<ContentParam> contentParams) {
      this(handle);
      this.params = params;
      this.contentParams = contentParams;
    }

    abstract RawPlanImpl<W> parameterize(W handle,
                                         Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
                                         List<ContentParam> contentParams);
    abstract void configHandle(BaseHandle handle);

    @Override
    public W getHandle() {
      return handle;
    }
    public void setHandle(W handle) {
      if (handle == null) {
        throw new IllegalArgumentException("Must specify handle for reading raw plan");
      }
      if (!(handle instanceof BaseHandle)) {
        throw new IllegalArgumentException(
                "Cannot provide raw plan with invalid handle having class "+handle.getClass().getName()
        );
      }
      configHandle((BaseHandle) handle);
      this.handle = handle;
    }

    @Override
    public Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> getParams() {
      return params;
    }

    @Override
    public Plan bindParam(String paramName, boolean literal) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(paramName), literal);
    }
    @Override
    public Plan bindParam(PlanParamExpr param, boolean literal) {
      return bindParam(param, new XsValueImpl.BooleanValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, byte literal) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(paramName), literal);
    }
    @Override
    public Plan bindParam(PlanParamExpr param, byte literal) {
      return bindParam(param, new XsValueImpl.ByteValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, double literal) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(paramName), literal);
    }
    @Override
    public Plan bindParam(PlanParamExpr param, double literal) {
      return bindParam(param, new XsValueImpl.DoubleValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, float literal) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(paramName), literal);
    }
    @Override
    public Plan bindParam(PlanParamExpr param, float literal) {
      return bindParam(param, new XsValueImpl.FloatValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, int literal) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(paramName), literal);
    }
    @Override
    public Plan bindParam(PlanParamExpr param, int literal) {
      return bindParam(param, new XsValueImpl.IntValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, long literal) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(paramName), literal);
    }
    @Override
    public Plan bindParam(PlanParamExpr param, long literal) {
      return bindParam(param, new XsValueImpl.LongValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, short literal) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(paramName), literal);
    }
    @Override
    public Plan bindParam(PlanParamExpr param, short literal) {
      return bindParam(param, new XsValueImpl.ShortValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, String literal) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(paramName), literal);
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

      return parameterize(getHandle(), nextParams, this.contentParams);
    }

    @Override
    public Plan bindParam(String param, DocumentWriteSet writeSet) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(param), writeSet);
    }

    @Override
    public Plan bindParam(PlanParamExpr param, DocumentWriteSet writeSet) {
      if (!(param instanceof PlanBuilderBaseImpl.PlanParamBase)) {
        throw new IllegalArgumentException("param must be an instance of PlanParamBase");
      }
      List<ContentParam> nextContentParams = new ArrayList<>();
      if (this.contentParams != null) {
        nextContentParams.addAll(this.contentParams);
      }
      nextContentParams.add(ContentParam.fromDocumentWriteSet((PlanBuilderBaseImpl.PlanParamBase) param, writeSet));
      return parameterize(getHandle(), this.params, nextContentParams);
    }

    @Override
    public Plan bindParam(String param, AbstractWriteHandle content) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(param), content, null);
    }

    @Override
    public Plan bindParam(String param, AbstractWriteHandle content, Map<String, Map<String, AbstractWriteHandle>> columnAttachments) {
      return bindParam(new PlanBuilderBaseImpl.PlanParamBase(param), content, columnAttachments);
    }

    @Override
    public Plan bindParam(PlanParamExpr param, AbstractWriteHandle content, Map<String, Map<String, AbstractWriteHandle>> columnAttachments) {
      if (!(param instanceof PlanBuilderBaseImpl.PlanParamBase)) {
        throw new IllegalArgumentException("param must be an instance of PlanParamBase");
      }
      PlanBuilderBaseImpl.PlanParamBase baseParam = (PlanBuilderBaseImpl.PlanParamBase) param;
      List<ContentParam> nextContentParams = new ArrayList<>();
      if (this.contentParams != null) {
        nextContentParams.addAll(this.contentParams);
      }
      nextContentParams.add(new ContentParam(baseParam, content, columnAttachments));
      return parameterize(getHandle(), this.params, nextContentParams);
    }

    @Override
    public List<ContentParam> getContentParams() {
      return contentParams;
    }
  }
  static class RawSQLPlanImpl extends RawPlanImpl<TextWriteHandle> implements RawSQLPlan {
    RawSQLPlanImpl(TextWriteHandle handle) {
      super(handle);
    }
    RawSQLPlanImpl(
            TextWriteHandle handle, Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
            List<ContentParam> contentParams
    ) {
      super(handle, params, contentParams);
    }

    @Override
    RawSQLPlanImpl parameterize(
            TextWriteHandle handle, Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
            List<ContentParam> contentParams
    ) {
      return new RawSQLPlanImpl(handle, params, contentParams);
    }
    @Override
    void configHandle(BaseHandle handle) {
      handle.setFormat(Format.TEXT);
      handle.setMimetype("application/sql");
    }

    @Override
    public RawSQLPlan withHandle(TextWriteHandle handle) {
      setHandle(handle);
      return this;
    }
  }
  static class RawSPARQLSelectPlanImpl extends RawPlanImpl<TextWriteHandle> implements RawSPARQLSelectPlan {
    RawSPARQLSelectPlanImpl(TextWriteHandle handle) {
      super(handle);
    }
    RawSPARQLSelectPlanImpl(
            TextWriteHandle handle, Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
            List<ContentParam> contentParams
    ) {
      super(handle, params, contentParams);
    }

    @Override
    RawSPARQLSelectPlanImpl parameterize(
            TextWriteHandle handle, Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
            List<ContentParam> contentParams
    ) {
      return new RawSPARQLSelectPlanImpl(handle, params, contentParams);
    }
    @Override
    void configHandle(BaseHandle handle) {
      handle.setFormat(Format.TEXT);
      handle.setMimetype("application/sparql-query");
    }

    @Override
    public RawSPARQLSelectPlan withHandle(TextWriteHandle handle) {
      setHandle(handle);
      return this;
    }
  }
  static class RawQueryDSLPlanImpl extends RawPlanImpl<TextWriteHandle> implements RawQueryDSLPlan {
    RawQueryDSLPlanImpl(TextWriteHandle handle) {
      super(handle);
    }
    RawQueryDSLPlanImpl(
            TextWriteHandle handle, Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
            List<ContentParam> contentParams
    ) {
      super(handle, params, contentParams);
    }

    @Override
    RawQueryDSLPlanImpl parameterize(
            TextWriteHandle handle, Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
            List<ContentParam> contentParams
    ) {
      return new RawQueryDSLPlanImpl(handle, params, contentParams);
    }
    @Override
    void configHandle(BaseHandle handle) {
      handle.setFormat(Format.TEXT);
      handle.setMimetype("application/vnd.marklogic.querydsl+javascript");
    }

    @Override
    public RawQueryDSLPlan withHandle(TextWriteHandle handle) {
      setHandle(handle);
      return this;
    }
  }
  static class RawPlanDefinitionImpl extends RawPlanImpl<JSONWriteHandle> implements RawPlanDefinition {
    RawPlanDefinitionImpl(JSONWriteHandle handle) {
      super(handle);
    }
    private RawPlanDefinitionImpl(
      JSONWriteHandle handle,
      Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
      List<ContentParam> contentParams) {
      super(handle, params, contentParams);
    }

    @Override
    RawPlanDefinitionImpl parameterize(
            JSONWriteHandle handle, Map<PlanBuilderBaseImpl.PlanParamBase,BaseTypeImpl.ParamBinder> params,
            List<ContentParam> contentParams
    ) {
      return new RawPlanDefinitionImpl(handle, params, contentParams);
    }
    @Override
    void configHandle(BaseHandle handle) {
      handle.setFormat(Format.JSON);
      handle.setMimetype("application/json");
    }

    @Override
    public RawPlanDefinition withHandle(JSONWriteHandle handle) {
      setHandle(handle);
      return this;
    }
  }
}
