/*
 * Copyright 2018-2019 MarkLogic Corporation
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
package com.marklogic.client.datamovement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.StringHandle;

import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.util.RequestParameters;

/**
 * This QueryBatchListener takes in one or more uris for templates as defined by
 * Marklogic TDE (Template Driven Extraction) and applies them to each batch of
 * documents. It extracts the rows from the documents as specified in the
 * template and it applies the listeners registered with onTypedRowReady method
 * to each row.
 *
 * <br>
 * For example:
 *
 * <pre>{@code
 * StructuredQueryDefinition query = new StructuredQueryBuilder().directory(1, "/employees/");
 * QueryBatcher qb = moveMgr.newQueryBatcher(query)
 *     .onUrisReady(new ExtractRowsViaTemplateListener().withTemplate(templateUri).onTypedRowReady(row -&gt; {
 *       System.out.println("row:" + row);
 *     }));
 * moveMgr.startJob(qb);
 * qb.awaitCompletion();
 * moveMgr.stopJob(qb);
 * }</pre>
 *
 * If any of the consumers registered with this listener implements the
 * AutoCloseable interface and has a resource that needs to be closed, we have
 * to make sure that this listener also override the close() method and make
 * sure we call the close method of all the consumers registered with it. This
 * close method of the listener will be called internally by the QueryBatcher
 * when stopJob is called on the batcher. It is the responsibility of the
 * listeners to call the close method if any of its consumers have the close
 * method implemented to close any resources used.
 *
 * <br>
 * <br>
 * Here we call the close method because the WriteRowToTableauConsumer which
 * would be registered with this listener needs to close the Tableau Extract
 * instance after the extract to Tableau is complete.
 *
 * <br>
 * <br>
 * Important Note: You have to pass in templates which should map rows to a
 * single view. If we have two templates, we should make sure that the
 * WriteRowToTableauConsumer has all the columns to accommodate the columns
 * emitted by the rows. Also, the templates should emit only rows and not
 * triples.
 */
public class ExtractRowsViaTemplateListener implements QueryBatchListener, AutoCloseable {
  private static Logger logger =
    LoggerFactory.getLogger(ExtractRowsViaTemplateListener.class);
  private List<String> templateUris = new ArrayList<>();
  private String templateDb;
  private List<Consumer<TypedRow>> rowListeners = new ArrayList<>();
  private List<BatchFailureListener<QueryBatch>> failureListeners = new ArrayList<>();
  private PlanBuilder pb;

  public ExtractRowsViaTemplateListener() {
    logger.debug("new ExtractRowsViaTemplateListener - this should print once/job; " +
      "if you see this once/batch, fix your job configuration");
  }

  /**
   * Register one or more template uris which needs to be applied to each batch
   * to extract the rows
   *
   * @param templateUri the uri of the template to be applied to each batch.
   * @return the instance for chaining
   */
  public ExtractRowsViaTemplateListener withTemplate(String templateUri) {
    this.templateUris.add(templateUri);
    return this;
  }

  private ExtractRowsViaTemplateListener withTemplateDatabase(String templateDatabase) {
    this.templateDb = templateDatabase;
    return this;
  }

  /**
   * Register one or more listeners which needs to be applied to each row got by
   * applying the templates to the batch of documents.
   *
   * @param listener the listener which needs to be applied to each row
   * @return the instance for chaining
   */
  public ExtractRowsViaTemplateListener onTypedRowReady(Consumer<TypedRow> listener) {
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
  public ExtractRowsViaTemplateListener onFailure(BatchFailureListener<QueryBatch> listener) {
    failureListeners.add(listener);
    return this;
  }

  @Override
  public void initializeListener(QueryBatcher queryBatcher) {
    if (queryBatcher.getPrimaryClient() == null) {
      throw new IllegalStateException("null DatabaseClient");
    }
    pb = queryBatcher.getPrimaryClient().newRowManager().newPlanBuilder();
  }

  /**
   * This is the method QueryBatcher calls for ExtractRowsViaTemplateListener to do
   * its thing. You should not need to call it.
   *
   * @param batch the batch of uris and some metadata about the current status
   *          of the job
   */
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
      logger.warn("Error: [{}] in batch with uris ({})", t.toString(),
        Arrays.asList(batch.getItems()));
    }
  }

  /*
   * Makes a call to an internal REST end point "internal/extract-via-template"
   * and passes in the list of URIs from the batch and the template URIs that
   * needs to be applied to each batch. The REST end point applies the templates
   * to the documents and returns a JSON string containing all the rows. We
   * create an iterator out of it.
   */
  private Iterable<TypedRow> getTypedRows(QueryBatch batch) throws IOException {
    if (batch.getClient() == null) {
      throw new IllegalStateException("null DatabaseClient");
    }
    StringHandle uris = new StringHandle(String.join("\n", batch.getItems()))
      .withMimetype("text/uri-list");
    RESTServices services = ((DatabaseClientImpl) batch.getClient()).getServices();
    RequestParameters params = new RequestParameters();
    for ( String templateUri : templateUris ) params.add("template", templateUri);
    if ( templateDb != null ) params.add("template-database", templateDb);
    JacksonParserHandle handle = services.postResource(null, "internal/extract-via-template", null, params, uris, new JacksonParserHandle());
    JsonParser jp = handle.get();
    if (jp.nextToken() != JsonToken.START_OBJECT) {
      throw new MarkLogicIOException("Expected data to start with an Object");
    }
    jp.nextToken();
    if ( jp.currentToken() == JsonToken.END_OBJECT) {
      logger.warn("No documents found for this batch");
      return new ArrayList<TypedRow>();
    } else {
      return new Iterable<TypedRow>() {
        public Iterator<TypedRow> iterator() {
          return new Iterator<TypedRow>() {
            private String uri = null;
            private TypedRow nextRow = null;
            private boolean rowUsed = true;

            public TypedRow next() {
              // We get the next value in the iterator when we call hasNext().
              // This had to be done since we need to check whether the next
              // entry is a valid row or not as there is a possibility of empty
              // rows.
              if ( nextRow == null && !hasNext() ) {
                throw new NoSuchElementException("No more elements found in this iterator");
              }
              rowUsed = true;
              return nextRow;
            }

            public boolean hasNext() {
              if ( !rowUsed ) {
                return true;
              } else {
                if ( jp.currentToken() != JsonToken.END_OBJECT ) {
                  nextRow = getOneTypedRow(jp);
                  return nextRow == null ? false : true;
                } else {
                  return false;
                }
              }
            }

            /*
             * This is a helper method for the iterator which returns one row
             * (the next row) when next() is called for the iterator. This is
             * designed in such a way that this method returns the next valid
             * row parsed from the JSON handle. This returns null only when
             * there is no valid rows in the remaining contents of the handle.
             */
            private TypedRow getOneTypedRow(JsonParser jp) {
              try {
                // Process the initial URI part
                while (true) {
                  if ( uri == null ) {
                    if ( jp.currentToken() != JsonToken.FIELD_NAME ) {
                      throw new MarkLogicIOException("Expected a uri for next template result");
                    }
                    uri = jp.getCurrentName();
                    if ( jp.nextToken() != JsonToken.START_ARRAY ) {
                      throw new MarkLogicIOException("Expected an array of rows");
                    }
                    jp.nextToken();
                  }
                  // If the current token is an END_ARRAY, then it is an empty
                  // row. Log a warning and continue with the next row or return
                  // null if there is no valid row
                  if ( jp.currentToken() == JsonToken.END_ARRAY ) {
                    logger.warn("No row found for Uri - " + uri);
                    uri = null;
                    if ( jp.nextToken() != JsonToken.END_OBJECT ) {
                      continue;
                    } else {
                      return null;
                    }
                  }
                  // Process subsequent rows for the same URI if there are
                  // multiple templates involved.
                  if ( jp.currentToken() != JsonToken.START_OBJECT ) {
                    throw new MarkLogicIOException("Expected a JSON object containing a row");
                  }
                  if ( "triple".equals(jp.nextFieldName()) ) {
                    throw new MarkLogicIOException("Expected a row but we got a triple. We don't support triples");
                  }
                  if ( !"row".equals(jp.getCurrentName()) || jp.nextToken() != JsonToken.START_OBJECT ) {
                    throw new MarkLogicIOException("Expected row to start");
                  }
                  while (!"data".equals(jp.nextFieldName())) {
                  }
                  if ( jp.nextToken() != JsonToken.START_OBJECT || !"rownum".equals(jp.nextFieldName()) ) {
                    throw new MarkLogicIOException("Expected a row of values");
                  }
                  String rowNum = jp.nextTextValue();
                  TypedRow row = new TypedRow(uri, rowNum);
                  while (jp.nextToken() == JsonToken.FIELD_NAME) {
                    JsonToken valueType = jp.nextToken();
                    if ( valueType == JsonToken.VALUE_STRING ) {
                      row.put(jp.getCurrentName(), pb.xs.string(jp.getText()));
                    } else if ( valueType == JsonToken.VALUE_NUMBER_INT ) {
                      row.put(jp.getCurrentName(), pb.xs.integer(jp.getIntValue()));
                    } else if ( valueType == JsonToken.VALUE_NUMBER_FLOAT ) {
                      row.put(jp.getCurrentName(), pb.xs.floatVal(jp.getFloatValue()));
                    } else if ( valueType == JsonToken.VALUE_TRUE || valueType == JsonToken.VALUE_FALSE ) {
                      row.put(jp.getCurrentName(), pb.xs.booleanVal(jp.getBooleanValue()));
                    } else if ( valueType == JsonToken.VALUE_NULL ) {
                      row.put(jp.getCurrentName(), null);
                    } else {
                      throw new MarkLogicIOException(
                          "Unexpected value type for column \"" + jp.getCurrentName() + "\"");
                    }
                  }
                  if ( jp.currentToken() != JsonToken.END_OBJECT || jp.nextToken() != JsonToken.END_OBJECT
                      || jp.nextToken() != JsonToken.END_OBJECT ) {
                    throw new MarkLogicIOException("Expected row to end");
                  }
                  if ( jp.nextToken() == JsonToken.END_ARRAY ) {
                    uri = null;
                    jp.nextToken();
                  }
                  return row;
                }
              }
              catch (IOException e) {
                throw new MarkLogicIOException(e);
              }
            }
          };
        }
      };
    }
  }

  @Override
  public void close() throws Exception {
    for ( Consumer<TypedRow> listener : rowListeners ) {
      if ( listener instanceof AutoCloseable ) {
        try {
          ((AutoCloseable) listener).close();
        } catch (Exception e) {
          logger.error("onTypedRowReady listener cannot be closed", e);
        }
      }
    }
    for ( BatchFailureListener<QueryBatch> listener : failureListeners ) {
      if ( listener instanceof AutoCloseable ) {
        try {
          ((AutoCloseable) listener).close();
        } catch (Exception e) {
          logger.error("onFailure listener cannot be closed", e);
        }
      }
    }
  }
}
