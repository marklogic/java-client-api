/*
 * Copyright 2018 MarkLogic Corporation
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
package com.marklogic.client.example.cookbook.datamovement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.datamovement.BatchFailureListener;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.impl.DataMovementManagerImpl;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.util.RequestParameters;

public class ExtractViaTemplateListener implements QueryBatchListener {
  private static Logger logger =
    LoggerFactory.getLogger(ExtractViaTemplateListener.class);
  private List<String> templates = new ArrayList<>();
  private String templateDb;
  private List<Consumer<TypedRow>> rowListeners = new ArrayList<>();
  private List<BatchFailureListener<QueryBatch>> failureListeners = new ArrayList<>();
  private PlanBuilder pb;

  public ExtractViaTemplateListener() {
    logger.debug("new ExtractViaTemplateListener - this should print once/job; " +
      "if you see this once/batch, fix your job configuration");
  }

  public ExtractViaTemplateListener withTemplate(String template) {
    this.templates.add(template);
    return this;
  }

  public ExtractViaTemplateListener withTemplateDatabase(String templateDatabase) {
    this.templateDb = templateDatabase;
    return this;
  }

  public ExtractViaTemplateListener onTypedRowReady(Consumer<TypedRow> listener) {
    rowListeners.add(listener);
    return this;
  }

  /**
   * When a batch fails or a callback throws an Exception, run this listener
   * code.  Multiple listeners can be registered with this method.
   *
   * @param listener the code to run when a failure occurs
   *
   * @return this instance for method chaining
   */
  public ExtractViaTemplateListener onFailure(BatchFailureListener<QueryBatch> listener) {
    failureListeners.add(listener);
    return this;
  }

  @Override
  public void initializeListener(QueryBatcher queryBatcher) {
    pb = queryBatcher.getPrimaryClient().newRowManager().newPlanBuilder();
  }

  @Override
  public void processEvent(QueryBatch batch) {
    if ( ! (batch.getClient() instanceof DatabaseClientImpl) ) {
      throw new IllegalStateException("DatabaseClient must be instanceof DatabaseClientImpl");
    }
    try {
      for (TypedRow row : getTypedRows(batch) ) {
        for (Consumer<TypedRow> listener : rowListeners) {
          try {
            listener.accept(row);
          } catch (Throwable t) {
            logger.error("Exception thrown by an onTypedRowReady listener", t);
          }
        }
      }
    } catch (Throwable t) {
      for ( BatchFailureListener<QueryBatch> listener : failureListeners ) {
        try {
          listener.processFailure(batch, t);
        } catch (Throwable t2) {
          logger.error("Exception thrown by an onFailure listener", t2);
        }
      }
      logger.warn("Error: [{}] in batch with urs ({})", t.toString(),
        Arrays.asList(batch.getItems()));
    }
  }

  private Iterable<TypedRow> getTypedRows(QueryBatch batch) throws IOException {
    StringHandle uris = new StringHandle(String.join("\n", batch.getItems()))
      .withMimetype("text/uri-list");
    RESTServices services = ((DatabaseClientImpl) batch.getClient()).getServices();
    RequestParameters params = new RequestParameters();
    for ( String template : templates ) params.add("template", template);
    if ( templateDb != null ) params.add("template-database", templateDb);
    JacksonParserHandle handle = services.postResource(null, "internal/extract-via-template", null, params, uris, new JacksonParserHandle());
    JsonParser jp = handle.get();
    if (jp.nextToken() != JsonToken.START_OBJECT) {
      throw new MarkLogicIOException("Expected data to start with an Object");
    }
    jp.nextToken();
    if ( jp.currentToken() == JsonToken.END_OBJECT) {
      logger.warn("No documents found for this batch--are the uris correct?");
      return new ArrayList<TypedRow>();
    } else {
      return new Iterable<TypedRow>() {
        public Iterator<TypedRow> iterator() {
          return new Iterator() {
            public TypedRow next() {
              return getOneTypedRow(jp);
            }

            public boolean hasNext() {
              return jp.currentToken() != JsonToken.END_OBJECT;
            }
          };
        }
      };
    }
  }

  private TypedRow getOneTypedRow(JsonParser jp) {
    try {
      if ( jp.currentToken() != JsonToken.FIELD_NAME ) {
        throw new MarkLogicIOException("Expected a uri for next template result");
      }
      String uri = jp.getCurrentName();
      if ( jp.nextToken() != JsonToken.START_ARRAY ||
          jp.nextToken() != JsonToken.START_OBJECT ||
          ! "row".equals(jp.nextFieldName()) ||
          jp.nextToken() != JsonToken.START_OBJECT ) {
        throw new MarkLogicIOException("Expected an array of rows");
      }
      while ( ! "data".equals(jp.nextFieldName()) ) {}
      if ( jp.nextToken() != JsonToken.START_OBJECT ||
          ! "rownum".equals(jp.nextFieldName()) ) {
        throw new MarkLogicIOException("Expected a row of values");
      }
      String rowNum = jp.nextTextValue();
      MyTypedRow row = new MyTypedRow(uri, rowNum);
      while ( jp.nextToken() == JsonToken.FIELD_NAME ) {
        JsonToken valueType = jp.nextToken();
        if ( valueType == JsonToken.VALUE_STRING ) {
          row.put(jp.getCurrentName(),
              pb.xs.string(jp.getText()));
        } else if ( valueType == JsonToken.VALUE_NUMBER_INT ) {
          row.put(jp.getCurrentName(),
              pb.xs.integer(jp.getIntValue()));
        } else if ( valueType == JsonToken.VALUE_NUMBER_FLOAT ) {
          row.put(jp.getCurrentName(),
              pb.xs.floatVal(jp.getFloatValue()));
        } else if ( valueType == JsonToken.VALUE_TRUE ||
            valueType == JsonToken.VALUE_FALSE ) {
          row.put(jp.getCurrentName(),
              pb.xs.booleanVal(jp.getBooleanValue()));
        } else if ( valueType == JsonToken.VALUE_NULL ) {
          row.put(jp.getCurrentName(), null);
        } else {
          throw new MarkLogicIOException("Unexpected value type for column \"" +
              jp.getCurrentName() + "\"");
        }
      }
      if ( jp.currentToken() != JsonToken.END_OBJECT ||
           jp.nextToken()    != JsonToken.END_OBJECT ||
           jp.nextToken()    != JsonToken.END_OBJECT ||
           jp.nextToken()    != JsonToken.END_ARRAY )
      {
        throw new MarkLogicIOException("Expected row to end");
      }
      jp.nextToken();
      return row;
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }

  private class MyTypedRow
    extends LinkedHashMap<String,XsAnyAtomicTypeVal>
    implements TypedRow
  {
    String uri;
    String rowNum;

    private MyTypedRow(String uri, String rowNum) {
      this.uri = uri;
      this.rowNum = rowNum;
    }
    public String getUri() {
      return uri;
    }
    public long getRowNum() {
      return new Long(rowNum).longValue();
    }
    public XsAnyAtomicTypeVal put(String name, XsAnyAtomicTypeVal val) {
      return super.put(name, val);
    }
  };
}
