/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import com.marklogic.client.expression.*;
import com.marklogic.client.query.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.CtsQueryWriteHandle;
import com.marklogic.client.io.marker.QueryOptionsListReadHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TuplesReadHandle;
import com.marklogic.client.io.marker.ValuesListReadHandle;
import com.marklogic.client.io.marker.ValuesReadHandle;
import com.marklogic.client.util.RequestParameters;

public class QueryManagerImpl
  extends AbstractLoggingManager
  implements QueryManager
{
  private RESTServices          services;
  private HandleFactoryRegistry handleRegistry;
  private long pageLen = -1;
  private QueryView view = QueryView.DEFAULT;

  public QueryManagerImpl(RESTServices services) {
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
  public long getPageLength() {
    return pageLen;
  }
  @Override
  public void setPageLength(long length) {
    pageLen = length;
  }

  @Override
  public QueryView getView() {
    return view;
  }
  @Override
  public void setView(QueryView view) {
    this.view = (view == null) ? QueryView.DEFAULT : view;
  }

  @Override
  public StringQueryDefinition newStringDefinition() {
    return new StringQueryDefinitionImpl(null);
  }
  @Override
  public StringQueryDefinition newStringDefinition(String optionsName) {
    return new StringQueryDefinitionImpl(optionsName);
  }

  @Override
  public StructuredQueryBuilder newStructuredQueryBuilder() {
    return new StructuredQueryBuilder();
  }
  @Override
  public StructuredQueryBuilder newStructuredQueryBuilder(String optionsName) {
    return new StructuredQueryBuilder(optionsName);
  }

  @Override
  public DeleteQueryDefinition newDeleteDefinition() {
    return new DeleteQueryDefinitionImpl();
  }

  @Override
  public ValuesDefinition newValuesDefinition(String name) {
    return new ValuesDefinitionImpl(name, null);
  }
  @Override
  public ValuesDefinition newValuesDefinition(String name, String optionsName) {
    return new ValuesDefinitionImpl(name, optionsName);
  }

  @Override
  public ValuesListDefinition newValuesListDefinition() {
    return new ValuesListDefinitionImpl(null);
  }
  @Override
  public ValuesListDefinition newValuesListDefinition(String optionsName) {
    return new ValuesListDefinitionImpl(optionsName);
  }

  @Override
  public <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle) {
    return search(querydef, searchHandle, 1, null, null);
  }
  @Override
  public <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, String forestName) {
    return search(querydef, searchHandle, 1, null, forestName);
  }
  @Override
  public <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, long start) {
    return search(querydef, searchHandle, start, null, null);
  }
  @Override
  public <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, long start, String forestName) {
    return search(querydef, searchHandle, start, null, forestName);
  }
  @Override
  public <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, Transaction transaction) {
    return search(querydef, searchHandle, 1, transaction, null);
  }
  @Override
  public <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, Transaction transaction, String forestName) {
    return search(querydef, searchHandle, 1, transaction, forestName);
  }
  @Override
  @SuppressWarnings("unchecked")
  public <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, long start, Transaction transaction) {
    return search(querydef, searchHandle, start, transaction, null);
  }
  @Override
  @SuppressWarnings("unchecked")
  public <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, long start, Transaction transaction, String forestName) {

    if (searchHandle instanceof SearchHandle) {
      SearchHandle responseHandle = (SearchHandle) searchHandle;
      responseHandle.setHandleRegistry(getHandleRegistry());
      responseHandle.setQueryCriteria(querydef);
    }
    return services.search(requestLogger, searchHandle, querydef, start, pageLen, view, transaction, forestName);
  }

  public <T extends UrisReadHandle> T uris(String method, SearchQueryDefinition querydef, Boolean filtered, T urisHandle,
        long start, String afterUri, String forestName) {
    return services.uris(requestLogger, method, querydef, filtered, start, afterUri, pageLen, forestName, urisHandle);
  }

  @Override
  public void delete(DeleteQueryDefinition querydef) {
    delete(querydef, null);
  }
  @Override
  public void delete(DeleteQueryDefinition querydef, Transaction transaction) {
    services.deleteSearch(requestLogger, querydef, transaction);
  }

  @Override
  public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle) {
    return values(valdef, valueHandle, -1, null);
  }
  @Override
  public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, long start) {
    return values(valdef, valueHandle, start, null);
  }
  @Override
  public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, Transaction transaction) {
    return values(valdef, valueHandle, -1, transaction);
  }
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, long start, Transaction transaction) {
    @SuppressWarnings("rawtypes")
    HandleImplementation valuesBase = HandleAccessor.checkHandle(valueHandle, "values");

    if (valueHandle instanceof ValuesHandle) {
      ((ValuesHandle) valueHandle).setQueryCriteria(valdef);
    }

    Format valuesFormat = valuesBase.getFormat();
    switch(valuesFormat) {
      case UNKNOWN:
        valuesFormat = Format.XML;
        break;
      case JSON:
      case XML:
        break;
      default:
        throw new UnsupportedOperationException("Only XML and JSON values results are possible.");
    }

    String mimetype = valuesFormat.getDefaultMimetype();
    long pageLength = (start == -1) ? -1 : getPageLength();

    String tid = transaction == null ? null : transaction.getTransactionId();
    valuesBase.receiveContent(
      services.values(
        valuesBase.receiveAs(), valdef, mimetype, start, pageLength, transaction
      )
    );
    return valueHandle;
  }

  @Override
  public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle) {
    return tuples(valdef, tupleHandle, -1, null);
  }
  @Override
  public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle, long start) {
    return tuples(valdef, tupleHandle, start, null);
  }
  @Override
  public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle, Transaction transaction) {
    return tuples(valdef, tupleHandle, -1, transaction);
  }
  @Override
  @SuppressWarnings("unchecked")
  public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle, long start, Transaction transaction) {
    @SuppressWarnings("rawtypes")
    HandleImplementation valuesBase = HandleAccessor.checkHandle(tupleHandle, "values");

    if (tupleHandle instanceof TuplesHandle) {
      ((TuplesHandle) tupleHandle).setQueryCriteria(valdef);
    }

    Format valuesFormat = valuesBase.getFormat();
    switch(valuesFormat) {
      case UNKNOWN:
        valuesFormat = Format.XML;
        break;
      case JSON:
      case XML:
        break;
      default:
        throw new UnsupportedOperationException("Only XML and JSON values results are possible.");
    }

    String mimetype = valuesFormat.getDefaultMimetype();
    long pageLength = (start == -1) ? -1 : getPageLength();

    String tid = transaction == null ? null : transaction.getTransactionId();
    valuesBase.receiveContent(
      services.values(
        valuesBase.receiveAs(), valdef, mimetype, start, pageLength, transaction
      )
    );
    return tupleHandle;
  }

  @Override
  public <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valueHandle) {
    return valuesList(valdef, valueHandle, null);
  }
  @SuppressWarnings("unchecked")
  @Override
  public <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valuesHandle, Transaction transaction) {
    @SuppressWarnings("rawtypes")
    HandleImplementation valuesBase = HandleAccessor.checkHandle(valuesHandle, "valueslist");

    Format valuesFormat = valuesBase.getFormat();
    switch(valuesFormat) {
      case UNKNOWN:
        valuesFormat = Format.XML;
        break;
      case JSON:
      case XML:
        break;
      default:
        throw new UnsupportedOperationException("Only XML and JSON values list results are possible.");
    }

    String mimetype = valuesFormat.getDefaultMimetype();

    String tid = transaction == null ? null : transaction.getTransactionId();
    valuesBase.receiveContent(services.valuesList(valuesBase.receiveAs(), valdef, mimetype, transaction));
    return valuesHandle;
  }

  @Override
  public <T extends QueryOptionsListReadHandle> T optionsList(T optionsHandle) {
    return optionsList(optionsHandle, null);
  }
  @SuppressWarnings("unchecked")
  @Override
  public <T extends QueryOptionsListReadHandle> T optionsList(T optionsHandle, Transaction transaction) {
    @SuppressWarnings("rawtypes")
    HandleImplementation optionsBase = HandleAccessor.checkHandle(optionsHandle, "optionslist");

    Format optionsFormat = optionsBase.getFormat();
    switch(optionsFormat) {
      case UNKNOWN:
        optionsFormat = Format.XML;
        break;
      case JSON:
      case XML:
        break;
      default:
        throw new UnsupportedOperationException("Only XML and JSON options list results are possible.");
    }

    String mimetype = optionsFormat.getDefaultMimetype();

    String tid = transaction == null ? null : transaction.getTransactionId();
    optionsBase.receiveContent(services.optionsList(optionsBase.receiveAs(), mimetype, transaction));
    return optionsHandle;
  }

  @Override
  public MatchDocumentSummary findOne(QueryDefinition querydef) {
    SearchHandle results = search(querydef, new SearchHandle());
    MatchDocumentSummary[] summaries = results.getMatchResults();
    if (summaries.length > 0) {
      return summaries[0];
    } else {
      return null;
    }
  }
  @Override
  public MatchDocumentSummary findOne(QueryDefinition querydef, Transaction transaction) {
    SearchHandle results = search(querydef, new SearchHandle(), transaction);
    MatchDocumentSummary[] summaries = results.getMatchResults();
    if (summaries.length > 0) {
      return summaries[0];
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public String[] suggest(SuggestDefinition suggestDef) {
    DOMHandle handle = new DOMHandle();

    @SuppressWarnings("rawtypes")
    HandleImplementation suggestBase = HandleAccessor.checkHandle(handle, "suggest");

    suggestBase.receiveContent(services.suggest(suggestBase.receiveAs(), suggestDef));

    Document doc = handle.get();
    NodeList nodeList = doc.getDocumentElement().getChildNodes();
    List<String> suggestions = new ArrayList<>();
    for (int i=0; i <nodeList.getLength(); i++) {
      suggestions.add(nodeList.item(i).getTextContent());
    }
    return suggestions.toArray(new String[suggestions.size()]);
  }

  @Override
  public <T extends StructureReadHandle> T convert(RawQueryByExampleDefinition query, T convertedHandle) {
    return convertOrValidate(query, convertedHandle, "structured");
  }

  @Override
  public <T extends StructureReadHandle> T validate(RawQueryByExampleDefinition query, T convertedHandle) {
    return convertOrValidate(query, convertedHandle, "validate");
  }

  @Override
  public CtsQueryBuilder newCtsSearchBuilder() {
    return new CtsQueryBuilderImpl();
  }

  @SuppressWarnings("unchecked")
  private <T extends StructureReadHandle> T convertOrValidate(RawQueryByExampleDefinition query, T convertedHandle, String view) {
    if (convertedHandle == null)
      throw new IllegalArgumentException("null handle for query");

    RequestParameters params = new RequestParameters();
    params.add("view", view);
    @SuppressWarnings("rawtypes")
    HandleImplementation convertedBase = HandleAccessor.checkHandle(convertedHandle, "convert");

    StructureWriteHandle queryHandle = query.getHandle();

    Format convertedFormat = convertedBase.getFormat();
    if(convertedFormat == Format.UNKNOWN && queryHandle != null) {
      @SuppressWarnings("rawtypes")
      HandleImplementation queryBase = HandleAccessor.checkHandle(queryHandle, "validate");
      if (queryBase.getFormat() == Format.UNKNOWN) {
        convertedBase.setFormat(Format.XML);
      }
    }
    String optionsName = query.getOptionsName();
    if (optionsName != null && optionsName.length() > 0) {
      params.add("options", optionsName);
    }

    services.postResource(requestLogger, "qbe", null, params, queryHandle, convertedHandle);

    return convertedHandle;
  }

  @Override
  public SuggestDefinition newSuggestDefinition() {
    SuggestDefinition def = new SuggestDefinitionImpl();
    return def;
  }
  @Override
  public SuggestDefinition newSuggestDefinition(String optionsName) {
    SuggestDefinition def = new SuggestDefinitionImpl();
    def.setStringCriteria("");
    def.setOptionsName(optionsName);
    return def;
  }
  @Override
  public SuggestDefinition newSuggestDefinition(String suggestString,
                                                String optionsName) {
    SuggestDefinition def = new SuggestDefinitionImpl();
    def.setStringCriteria(suggestString);
    def.setOptionsName(optionsName);
    return def;
  }

  @Override
  public RawCombinedQueryDefinition newRawCombinedQueryDefinitionAs(Format format, Object rawQuery) {
    return newRawCombinedQueryDefinitionAs(format, rawQuery, null);
  }
  @Override
  public RawCombinedQueryDefinition newRawCombinedQueryDefinitionAs(Format format, Object rawQuery, String optionsName) {
    return newRawCombinedQueryDefinition(structuredWrite(format, rawQuery), optionsName);
  }
  @Override
  public RawCombinedQueryDefinition newRawCombinedQueryDefinition(StructureWriteHandle handle) {
    return new RawQueryDefinitionImpl.Combined(handle);
  }
  @Override
  public RawCombinedQueryDefinition newRawCombinedQueryDefinition(StructureWriteHandle handle, String optionsName) {
    return new RawQueryDefinitionImpl.Combined(handle, optionsName);
  }

  @Override
  public RawStructuredQueryDefinition newRawStructuredQueryDefinitionAs(Format format, Object query) {
    return newRawStructuredQueryDefinitionAs(format, query, null);
  }
  @Override
  public RawStructuredQueryDefinition newRawStructuredQueryDefinitionAs(Format format, Object query, String optionsName) {
    return newRawStructuredQueryDefinition(structuredWrite(format, query), optionsName);
  }
  @Override
  public RawStructuredQueryDefinition newRawStructuredQueryDefinition(StructureWriteHandle handle) {
    return new RawQueryDefinitionImpl.Structured(handle);
  }
  @Override
  public RawStructuredQueryDefinition newRawStructuredQueryDefinition(StructureWriteHandle handle, String optionsName) {
    return new RawQueryDefinitionImpl.Structured(handle, optionsName);
  }

  @Override
  public RawCtsQueryDefinition newRawCtsQueryDefinitionAs(Format format, Object query) {
    return newRawCtsQueryDefinitionAs(format, query, null);
  }
  @Override
  public RawCtsQueryDefinition newRawCtsQueryDefinitionAs(Format format, Object query, String optionsName) {
    return newRawCtsQueryDefinition(ctsQueryWrite(format, query), optionsName);
  }
  @Override
  public RawCtsQueryDefinition newRawCtsQueryDefinition(CtsQueryWriteHandle handle) {
    return new RawQueryDefinitionImpl.CtsQuery(handle);
  }
  @Override
  public RawCtsQueryDefinition newRawCtsQueryDefinition(CtsQueryWriteHandle handle, String optionsName) {
    return new RawQueryDefinitionImpl.CtsQuery(handle, optionsName);
  }

  @Override
  public RawQueryByExampleDefinition newRawQueryByExampleDefinitionAs(Format format, Object query) {
    return newRawQueryByExampleDefinitionAs(format, query, null);
  }
  @Override
  public RawQueryByExampleDefinition newRawQueryByExampleDefinitionAs(Format format, Object query, String optionsName) {
    return newRawQueryByExampleDefinition(structuredWrite(format, query), optionsName);
  }
  @Override
  public RawQueryByExampleDefinition newRawQueryByExampleDefinition(StructureWriteHandle handle) {
    return new RawQueryDefinitionImpl.ByExample(handle);
  }
  @Override
  public RawQueryByExampleDefinition newRawQueryByExampleDefinition(StructureWriteHandle handle, String optionsName) {
    return new RawQueryDefinitionImpl.ByExample(handle, optionsName);
  }

  private StructureWriteHandle structuredWrite(Format format, Object query) {
    Class<?> as = query.getClass();
    ContentHandle<?> queryHandle = getHandleRegistry().makeHandle(as);
    if (!StructureWriteHandle.class.isAssignableFrom(queryHandle.getClass())) {
      throw new IllegalArgumentException(
        "Handle "+queryHandle.getClass().getName()+
          " does not provide structure write handle for "+as.getName()
      );
    }

    Utilities.setHandleContent(queryHandle, query);
    Utilities.setHandleStructuredFormat(queryHandle, format);

    return (StructureWriteHandle) queryHandle;
  }

  private CtsQueryWriteHandle ctsQueryWrite(Format format, Object query) {
    Class<?> as = query.getClass();
    ContentHandle<?> queryHandle = getHandleRegistry().makeHandle(as);
    if (!CtsQueryWriteHandle.class.isAssignableFrom(queryHandle.getClass())) {
      throw new IllegalArgumentException(
        "Handle "+queryHandle.getClass().getName()+
          " does not provide a CtsQueryWriteHandle for "+as.getName()
      );
    }

    Utilities.setHandleContent(queryHandle, query);
    Utilities.setHandleStructuredFormat(queryHandle, format);

    return (CtsQueryWriteHandle) queryHandle;
  }
}
