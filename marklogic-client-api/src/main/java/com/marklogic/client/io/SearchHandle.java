/*
 * Copyright 2012-2018 MarkLogic Corporation
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
package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.query.ExtractedItem;
import com.marklogic.client.query.ExtractedResult;
import com.marklogic.client.query.FacetHeatmapValue;
import com.marklogic.client.query.FacetResult;
import com.marklogic.client.query.FacetValue;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.MatchSnippet;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.SearchMetrics;
import com.marklogic.client.query.SearchResults;
import java.util.Map;

/**
 * A SearchHandle represents a set of search results returned by the server.
 *
 * <p>The precise nature of the results returned depends on the query options used for the
 * search and on the configuration of this handle.</p>
 *
 * <p>Snippets, in particular, are returned in various ways. In the default case, snippets are
 * returned as Java objects. For custom or raw snippets, DOM documents are returned. The
 * <code>forceDOM</code> flag can be set to cause the handle to always return DOM documents,
 * even in the default case.</p>
 *
 */
public class SearchHandle
  extends BaseHandle<InputStream, OperationNotSupported>
  implements SearchReadHandle, SearchResults
{
  static final private Logger logger = LoggerFactory.getLogger(SearchHandle.class);

  static private String SEARCH_NS = "http://marklogic.com/appservices/search";
  static private String QUERY_NS  = "http://marklogic.com/cts/query";

  private QueryDefinition       querydef;
  private HandleFactoryRegistry registry;

  private MatchDocumentSummary[] summary;
  private SearchMetrics          metrics;
  private List<Warning>     warnings;
  private List<Report>      reports;

  private Map<String, FacetResult> facets;
  private Map<String, EventRange>  constraints;

  private EventRange     planEvents;
  private List<XMLEvent> events;

  private long       totalResults = -1;
  private long       start        = -1;
  private int        pageLength   = 0;
  private String     snippetType;
  private String[]   qtext;
  private EventRange queryEvents;

  public SearchHandle() {
    super();
    super.setFormat(Format.XML);
  }

  /**
   * Sets the format associated with this handle.
   *
   * This handle only supports XML.
   *
   * @param format The format, which must be Format.XML or an exception will be raised.
   */
  @Override
  public void setFormat(Format format) {
    if (format != Format.XML)
      throw new IllegalArgumentException("SearchHandle supports the XML format only");
  }

  /**
   * Fluent setter for the format associated with this handle.
   *
   * This handle only supports XML.
   *
   * @param format The format, which must be Format.XML or an exception will be raised.
   * @return The SearchHandle instance on which this method was called.
   */
  public SearchHandle withFormat(Format format) {
    setFormat(format);
    return this;
  }

  @Override
  protected Class<InputStream> receiveAs() {
    return InputStream.class;
  }

  @Override
  protected void receiveContent(InputStream content) {
    try {
      XMLInputFactory factory = XMLInputFactory.newFactory();
      factory.setProperty("javax.xml.stream.isNamespaceAware", true);
      factory.setProperty("javax.xml.stream.isValidating",     false);
      factory.setProperty("javax.xml.stream.isCoalescing",     true);

      XMLEventReader reader = factory.createXMLEventReader(content, "UTF-8");
      SearchResponseImpl response = new SearchResponseImpl();
      response.parse(reader);
      reader.close();
      try {
        content.close();
      } catch (IOException e) {
        // ignore.
      }

      summary          =
        (response.tempSummary == null || response.tempSummary.size() < 1) ?
        new MatchDocumentSummary[0]:
        response.tempSummary.toArray(new MatchDocumentSummary[response.tempSummary.size()]);
      metrics          = response.tempMetrics;
      facets           = response.tempFacets;
      warnings         = response.tempWarnings;
      reports          = response.tempReports;
      planEvents       = response.tempPlanEvents;
      constraints      = response.tempConstraints;
      events           = response.tempEvents;
      totalResults     = response.tempTotalResults;
      start            = response.tempStart;
      pageLength       = response.tempPageLength;
      snippetType      = response.tempSnippetType;
      qtext            =
        (response.qtextList == null || response.qtextList.size() < 1) ?
        null :
        response.qtextList.toArray(new String[response.qtextList.size()]);
      queryEvents      = response.tempQueryEvents;

    } catch (XMLStreamException e) {
      throw new MarkLogicIOException("Could not construct search results: parser error", e);
    }
  }

  /**
   * Sets the query definition used in the search.
   *
   * <p>Calling this method always deletes any cached search results.</p>
   *
   * @param querydef The new QueryDefinition
   */
  public void setQueryCriteria(QueryDefinition querydef) {
    this.querydef = querydef;
    summary      = null;
    metrics      = null;
    facets       = null;
    warnings     = null;
    reports      = null;
    planEvents   = null;
    constraints  = null;
    events       = null;
    totalResults = -1;
    start        = -1;
    pageLength   = 0;
    snippetType  = null;
    qtext        = null;
    queryEvents  = null;
  }
  /**
   * Returns the query definition used for the search represented by this handle.
   * @return The query definition.
   */
  @Override
  public QueryDefinition getQueryCriteria() {
    return querydef;
  }

  /**
   * Makes the handle registry for this database client available
   * to this SearchHandle during processing of the search response.
   * @param registry	the registry of IO representation classes for this database client
   */
  final public void setHandleRegistry(HandleFactoryRegistry registry) {
    this.registry = registry;
  }
  private HandleFactoryRegistry getHandleRegistry() {
    return this.registry;
  }

  /**
   * Returns the total number of results in this search.
   * @return The number of results.
   */
  @Override
  public long getTotalResults() {
    return totalResults;
  }

  /**
   * Returns the start page for this search.
   * @return The offset to the first result.
   */
  @Override
  public long getStart() {
    return start;
  }

  /**
   * Returns the page length for this search.
   * @return The number of results in a page.
   */
  @Override
  public int getPageLength() {
    return pageLength;
  }

  /**
   * Identifies whether results have default,
   * raw document, customer, or no snippets.
   * @return The type of snippets provided by results.
   */
  @Override
  public String getSnippetTransformType() {
    return snippetType;
  }

  /**
   * Returns the list of string queries, if specified
   * by the query options.
   * @return The string queries.
   */
  @Override
  public String[] getStringQueries() {
    return qtext;
  }

  @Override
  public <T extends XMLReadHandle> T getQuery(T handle) {
    return Utilities.exportToHandle(
      getSlice(events, queryEvents), handle
    );
  }

  /**
   * Returns the metrics associated with this search.
   * @return The metrics.
   */
  @Override
  public SearchMetrics getMetrics() {
    return metrics;
  }

  /**
   * Returns an array of summaries for the matched documents.
   * @return The summary array.
   */
  @Override
  public MatchDocumentSummary[] getMatchResults() {
    return summary;
  }

  /**
   * Returns a list of the facet names returned in this search.
   * @return The array of names.
   */
  @Override
  public String[] getFacetNames() {
    if (facets == null || facets.isEmpty()) {
      return new String[0];
    }
    Set<String> names = facets.keySet();
    return names.toArray(new String[names.size()]);
  }
  /**
   * Returns the named facet results.
   * @param name The name of the facet.
   * @return Returns the results for the named facet or null if no such named facet exists.
   */
  @Override
  public FacetResult getFacetResult(String name) {
    if (facets == null || facets.isEmpty()) {
      return null;
    }
    return facets.get(name);
  }
  /**
   * Returns an array of facet results for this search.
   * @return The facets array.
   */
  @Override
  public FacetResult[] getFacetResults() {
    if (facets == null || facets.isEmpty()) {
      return new FacetResult[0];
    }
    Collection<FacetResult> facetResults = facets.values();
    return facetResults.toArray(new FacetResult[facetResults.size()]);
  }

  @Override
  public String[] getConstraintNames() {
    if (constraints == null || constraints.isEmpty()) {
      return new String[0];
    }
    Set<String> names = constraints.keySet();
    return names.toArray(new String[names.size()]);
  }
  @Override
  public <T extends XMLReadHandle> T getConstraint(String name, T handle) {
    if (constraints == null || constraints.isEmpty()) {
      return null;
    }

    EventRange constraintEvents = constraints.get(name);
    if (constraintEvents == null) {
      return null;
    }

    return Utilities.exportToHandle(
      getSlice(events, constraintEvents), handle
    );
  }
  @Override
  public <T extends XMLReadHandle> Iterator<T> getConstraintIterator(T handle) {
    if (constraints == null || constraints.isEmpty()) {
      List<T> list = Collections.emptyList();
      return list.iterator();
    }

    List<EventRange> constraintList =
      new ArrayList<>(constraints.values());

    return new EventIterator<>(events, constraintList, handle);
  }

  /**
   * Returns the query plan associated with this search.
   *
   * <p>Query plans are highly variable.</p>
   * @return the plan as a DOM Document
   */
  @Override
  public Document getPlan() {
    DOMHandle handle = getPlan(new DOMHandle());
    return (handle == null) ? null : handle.get();
  }
  @Override
  public <T extends XMLReadHandle> T getPlan(T handle) {
    return Utilities.exportToHandle(
      getSlice(events, planEvents), handle
    );
  }

  /**
   * Returns an array of any warnings associated with this search.
   * @return The warnings array.
   */
  @Override
  public Warning[] getWarnings() {
    return (warnings == null) ? new Warning[0] : warnings.toArray(new Warning[0]);
  }

  /**
   * Returns an array of any reports associated with this search.
   * @return The reports array.
   */
  @Override
  public Report[] getReports() {
    return (reports == null) ? new Report[0] : reports.toArray(new Report[0]);
  }

  private List<XMLEvent> getSlice(
    List<XMLEvent> eventList, EventRange eventRange
  ) {
    if (eventList == null || eventRange == null) {
      return null;
    }

    return eventList.subList(eventRange.getFirst(), eventRange.getNext());
  }
  private Document[] getEventDocuments(List<XMLEvent> eventList, List<EventRange> rangeList) {
    if (rangeList == null || rangeList.size() < 1) {
      return new Document[0];
    }

    List<Document> documents = new ArrayList<>();

    DOMHandle handle = new DOMHandle();
    for (int i=0; i < rangeList.size(); i++) {
      EventRange eventRange = rangeList.get(i);
      handle = Utilities.exportToHandle(
        getSlice(eventList, eventRange), handle
      );
      Document document = (handle == null) ? null : handle.get();
      if (document != null) {
        documents.add(document);
      }
    }

    int size = documents.size();
    return (size == 0) ? null : documents.toArray(new Document[size]);
  }

  static private class SearchMetricsImpl implements SearchMetrics {
    long qrTime = -1;
    long frTime = -1;
    long srTime = -1;
    long mrTime = -1;
    long erTime = -1;
    long totalTime = -1;

    public SearchMetricsImpl(long qrTime, long frTime, long srTime, long mrTime, long erTime, long totalTime) {
      this.qrTime = qrTime;
      this.frTime = frTime;
      this.srTime = srTime;
      this.mrTime = mrTime;
      this.erTime = erTime;
      this.totalTime = totalTime;
    }

    @Override
    public long getQueryResolutionTime() {
      return qrTime;
    }

    @Override
    public long getFacetResolutionTime() {
      return frTime;
    }

    @Override
    public long getSnippetResolutionTime() {
      return srTime;
    }

    @Override
    public long getMetadataResolutionTime() {
      return mrTime;
    }

    @Override
    public long getExtractResolutionTime() {
      return erTime;
    }

    @Override
    public long getTotalTime() {
      return totalTime;
    }
  }

  static private class EventRange {
    private int first = -1;
    private int next  = -1; // 1 after the last item in the range
    private EventRange(int first, int next) {
      super();
      this.first = first;
      this.next  = next;
    }
    int getFirst() {
      return first;
    }
    int getNext() {
      return next;
    }
  }

  private class MatchDocumentSummaryImpl implements MatchDocumentSummary {
    private String uri = null;
    private int score = -1;
    private double conf = -1;
    private double fit = -1;
    private String path = null;
    private List<MatchLocation> locations = new ArrayList<>();
    private String mimeType = null;
    private Format format = null;

    private List<EventRange> snippetEvents;
    private EventRange            extractedEvents;
    private EventRange            metadataEvents;
    private EventRange            relevanceEvents;
    private List<String>     similarUris;
    private String                extractSelected;

    public MatchDocumentSummaryImpl(String uri, int score, double confidence, double fitness, String path, String mimeType, Format format, String extractSelected) {
      this.uri = uri;
      this.score = score;
      conf = confidence;
      fit = fitness;
      this.path = path;
      this.mimeType = mimeType;
      this.format = format;
      this.extractSelected = extractSelected;
    }

    @Override
    public String getUri() {
      return uri;
    }

    @Override
    public int getScore() {
      return score;
    }

    @Override
    public double getConfidence() {
      return conf;
    }

    @Override
    public double getFitness() {
      return fit;
    }

    @Override
    public String getPath() {
      return path;
    }

    @Override
    public ExtractedResult getExtracted() {
      ExtractedResultImpl result = new ExtractedResultImpl();
      populateExtractedResult( result, events, extractedEvents );
      return result;
    }

    private void populateExtractedResult(ExtractedResultImpl result, List<XMLEvent> events,
                                         EventRange extractedEvents)
    {
      int start = extractedEvents.first;
      int end   = extractedEvents.next;
      StartElement element = events.get(start).asStartElement();
      QName elementName = element.getName();
      if ( "extracted-none".equals(elementName.getLocalPart()) ) {
        result.isEmpty = true;
      } else {
        @SuppressWarnings("unchecked")
        Iterator<Attribute> attributes = element.getAttributes();
        while ( attributes.hasNext() ) {
          Attribute attr = attributes.next();
          String attrName = attr.getName().getLocalPart();
          if ( "kind".equals(attrName) ) {
            result.kind = attr.getValue();
          }
        }
        int startChildren = start + 1;
        int endChildren = end - 1;
        // now get the children (extracted items) as strings
        EventRange extractedItemEvents = new EventRange(startChildren, endChildren);
        if ( Format.XML == getFormat() ) {
          result.setItems( populateExtractedItems(getSlice(events, extractedItemEvents)) );
          // if extractSelected is "include", this is not a root document node
        } else if ( Format.JSON == getFormat() && "include".equals(extractSelected) ) {
          XMLEvent event = events.get(startChildren);
          if ( XMLStreamConstants.CHARACTERS != event.getEventType() ) {
            throw new MarkLogicIOException("Cannot parse JSON for " +
              getPath() + "--content should be characters");
          }
          String json = event.asCharacters().getData();
          try {
            JsonNode jsonArray = new ObjectMapper().readTree(json);
            List<String> items = new ArrayList<>(jsonArray.size());
            for ( JsonNode item : jsonArray ) {
              items.add( item.toString() );
            }
            result.setItems( items );
          } catch (Throwable e) {
            throw new MarkLogicIOException("Cannot parse JSON '" + json + "' for " +
              getPath(), e);
          }
        } else {
          XMLEvent event = events.get(startChildren);
          if ( XMLStreamConstants.CHARACTERS != event.getEventType() ) {
            throw new MarkLogicIOException("Cannot read " +
              getPath() + "--content should be characters");
          }
          String text = event.asCharacters().getData();
          List<String> items = new ArrayList<>(1);
          items.add( event.asCharacters().getData() );
          result.setItems( items );
        }
      }
    }

    private List<String> populateExtractedItems(List<XMLEvent> events) {
      List<String> items = new ArrayList<>();
      List<XMLEvent> itemEvents = new ArrayList<>();
      List<QName> startNames = new ArrayList<>();
      for ( XMLEvent event : events ) {
        itemEvents.add(event);
        switch (event.getEventType()) {
          case XMLStreamConstants.START_ELEMENT: {
            startNames.add(event.asStartElement().getName());
            break;
          }
          case XMLStreamConstants.END_ELEMENT: {
            QName startName = startNames.remove(startNames.size() - 1);
            if (startNames.size() == 0 ) {
              if ( startName.equals(event.asEndElement().getName())) {
                items.add(Utilities.eventsToString(itemEvents));
                itemEvents = new ArrayList<>();
              } else {
                throw new IllegalStateException("Error parsing xml \"" +
                  Utilities.eventsToString(itemEvents) + "\", element " + startName +
                  " doesn't end as expected");
              }
            }
            break;
          }
        }
      }
      return items;
    }

    @Override
    public <T> T getFirstSnippetAs(Class<T> as) {
      ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
      if (!XMLReadHandle.class.isAssignableFrom(handle.getClass())) {
        throw new IllegalArgumentException("cannot read snippet from XML with "+handle.getClass());
      }

      if (null == getFirstSnippet((XMLReadHandle) handle)) {
        return null;
      }

      return handle.get();
    }
    @Override
    public <T extends XMLReadHandle> T getFirstSnippet(T handle) {
      if (snippetEvents == null || snippetEvents.size() < 1) {
        return null;
      }

      return Utilities.exportToHandle(
        getSlice(events, snippetEvents.get(0)), handle
      );
    }
    @Override
    public String getFirstSnippetText() {
      if (snippetEvents == null || snippetEvents.size() < 1) {
        return null;
      }

      return Utilities.eventTextToString(
        getSlice(events, snippetEvents.get(0))
      );
    }

    @Override
    public Document[] getSnippets() {
      return getEventDocuments(events, snippetEvents);
    }
    @Override
    public <T extends XMLReadHandle> Iterator<T> getSnippetIterator(T handle) {
      if (snippetEvents == null || snippetEvents.size() < 1) {
        List<T> list = Collections.emptyList();
        return list.iterator();
      }

      return new EventIterator<T>(events, snippetEvents, handle);
    }

    @Override
    public MatchLocation[] getMatchLocations() {
      if (locations == null) {
        return new MatchLocation[0];
      }

      return locations.toArray(new MatchLocation[locations.size()]);
    }

    @Override
    public Document getMetadata() {
      DOMHandle handle = getMetadata(new DOMHandle());
      return (handle == null) ? null : handle.get();
    }
    @Override
    public <T> T getMetadataAs(Class<T> as) {
      ContentHandle<T> handle = getHandleRegistry().makeHandle(as);

      if (!XMLReadHandle.class.isAssignableFrom(handle.getClass())) {
        throw new IllegalArgumentException("cannot read metadata from XML with "+handle.getClass());
      }

      getMetadata((XMLReadHandle) handle);

      return handle.get();
    }
    @Override
    public <T extends XMLReadHandle> T getMetadata(T handle) {
      return Utilities.exportToHandle(
        getSlice(events, metadataEvents), handle
      );
    }

    @Override
    public String getMimeType() {
      return mimeType;
    }

    @Override
    public Format getFormat() {
      return format;
    }

    public void addLocation(MatchLocation loc) {
      locations.add(loc);
    }

    @Override
    public String[] getSimilarDocumentUris() {
      if (similarUris == null || similarUris.size() < 1) {
        return new String[0];
      }

      return similarUris.toArray(new String[similarUris.size()]);
    }

    @Override
    public Document getRelevanceInfo() {
      DOMHandle handle = getRelevanceInfo(new DOMHandle());
      return (handle == null) ? null : handle.get();
    }
    @Override
    public <T extends XMLReadHandle> T getRelevanceInfo(T handle) {
      return Utilities.exportToHandle(
        getSlice(events, relevanceEvents), handle
      );
    }
  }

  static private class MatchLocationImpl implements MatchLocation {
    private String path = null;
    private List<MatchSnippet> matchEvents = new ArrayList<>();

    public MatchLocationImpl(String path) {
      this.path = path;
    }

    @Override
    public String getPath() {
      return path;
    }

    @Override
    public String getAllSnippetText() {
      if (matchEvents == null) {
        return null;
      }

      StringBuilder text = new StringBuilder();
      for (MatchSnippet snippet : matchEvents) {
        text.append(snippet.getText());
      }

      return text.toString();
    }

    @Override
    public MatchSnippet[] getSnippets() {
      if (matchEvents == null) {
        return new MatchSnippet[0];
      }

      return matchEvents.toArray(new MatchSnippet[matchEvents.size()]);
    }

    public void addMatchSnippet(MatchSnippet s) {
      matchEvents.add(s);
    }
  }

  static private class MatchSnippetImpl implements MatchSnippet {
    private boolean high = false;
    private String text = null;

    public MatchSnippetImpl(boolean high, String text) {
      this.high = high;
      this.text = text;
    }

    @Override
    public boolean isHighlighted() {
      return high;
    }

    @Override
    public String getText() {
      return text;
    }
  }

  static private class FacetResultImpl implements FacetResult {
    private String name = null;
    private FacetValue[] values = null;

    public FacetResultImpl(String name, FacetValue[] values) {
      this.name = name;
      this.values = values;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public FacetValue[] getFacetValues() {
      return values;
    }
  }

  static private class FacetValueImpl implements FacetValue {
    private String name = null;
    private long count = 0;
    private String label = null;

    public FacetValueImpl(String name, long count) {
      this.name = name;
      this.count = count;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public long getCount() {
      return count;
    }

    @Override
    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }
  }

  static private class FacetHeatmapValueImpl implements FacetHeatmapValue {
    private String name = null;
    private long count = 0;
    private String label = null;
    private double[] box = null;

    public FacetHeatmapValueImpl(String name, long count, double s, double w, double n, double e) {
      box = new double[4];
      box[0] = s;
      box[1] = w;
      box[2] = n;
      box[3] = e;

      this.name = "[" + box[0] + ", " + box[1]+ ", " + box[2] + ", " + box[3] + "]";
      this.count = count;
      this.label = name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public long getCount() {
      return count;
    }

    @Override
    public String getLabel() {
      return label;
    }

    @Override
    public double[] getBox() {
      return box;
    }
  }

  /**
   * Represents a warning.
   *
   * <p>The Search API may return warnings, they are represented by objects in this class.</p>
   */
  static public class Warning {
    private String id = null;
    private String text = null;

    protected Warning() {
    }

    /**
     * Returns the ID of the warning.
     * @return The id.
     */
    public String getId() {
      return id;
    }

    protected void setId(String id) {
      this.id = id;
    }

    /**
     * Returns the text of the warning message.
     * @return The message.
     */
    public String getMessage() {
      return text;
    }

    protected void setMessage(String msg) {
      text = msg;
    }
  }

  /**
   * Represents a report message.
   *
   * <p>The Search API may return report messages, they are represented by objects in this class.</p>
   */
  static public class Report {
    private String id = null;
    private String name = null;
    private String type = null;
    private String text = null;

    protected Report() {
    }

    /**
     * Returns the ID of the message.
     * @return The id.
     */
    public String getId() {
      return id;
    }

    protected void setId(String id) {
      this.id = id;
    }

    /**
     * Returns the name of the message.
     * @return The name.
     */
    public String getName() {
      return name;
    }

    protected void setName(String name) {
      this.name = name;
    }

    /**
     * Returns the type of the message.
     * @return The type.
     */
    public String getType() {
      return type;
    }

    protected void setType(String type) {
      this.type = type;
    }

    /**
     * Returns the text of the message.
     * @return The message.
     */
    public String getMessage() {
      return text;
    }

    protected void setMessage(String msg) {
      text = msg;
    }
  }

  class EventIterator<T extends XMLReadHandle> implements Iterator<T> {
    private List<XMLEvent>   eventList;
    private List<EventRange> rangeList;
    private T                handle;
    private int              nextEvent = 0;
    EventIterator(List<XMLEvent> eventList, List<EventRange> rangeList, T handle) {
      super();
      this.eventList = eventList;
      this.rangeList = rangeList;
      this.handle    = handle;
    }
    @Override
    public boolean hasNext() {
      return rangeList != null && nextEvent < rangeList.size();
    }
    @Override
    public T next() {
      if (!hasNext()) {
        return null;
      }

      EventRange eventRange = rangeList.get(nextEvent++);
      return Utilities.exportToHandle(
        getSlice(eventList, eventRange), handle
      );
    }
    @Override
    public void remove() {
      throw new UnsupportedOperationException("Remove not supported");
    }
  }

  private class SearchResponseImpl {
    private List<MatchDocumentSummary> tempSummary;
    private MatchDocumentSummaryImpl currSummary;

    private List<Warning> tempWarnings;
    private List<Report>  tempReports;

    private SearchMetrics          tempMetrics;
    private EventRange             tempPlanEvents;
    private List<XMLEvent>         tempEvents;

    private long tempTotalResults = -1;
    private long tempStart        = -1;
    private int  tempPageLength   = 0;

    private Map<String, FacetResult> tempFacets;
    private Map<String, EventRange>  tempConstraints;

    private String tempSnippetType;
    private String tempExtractSelected;
    private List<String>      qtextList;

    private EventRange tempQueryEvents;

    private SearchResponseImpl() {
      super();
    }

    private void parse(XMLEventReader reader) throws XMLStreamException {
      tempEvents = new ArrayList<>();

      while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();
        int eventType = event.getEventType();

        switch (eventType) {
          case XMLStreamConstants.START_ELEMENT:
            handleTop(reader, event.asStartElement());
            break;
          case XMLStreamConstants.END_ELEMENT:
          case XMLStreamConstants.START_DOCUMENT:
          case XMLStreamConstants.END_DOCUMENT:
          case XMLStreamConstants.CDATA:
          case XMLStreamConstants.CHARACTERS:
          case XMLStreamConstants.ATTRIBUTE:
          case XMLStreamConstants.COMMENT:
          case XMLStreamConstants.DTD:
          case XMLStreamConstants.ENTITY_DECLARATION:
          case XMLStreamConstants.ENTITY_REFERENCE:
          case XMLStreamConstants.NAMESPACE:
          case XMLStreamConstants.NOTATION_DECLARATION:
          case XMLStreamConstants.PROCESSING_INSTRUCTION:
          case XMLStreamConstants.SPACE:
            break;
          default:
            throw new InternalError("unknown event type: "+eventType);
        }
      }
    }

    private void handleTop(XMLEventReader reader, StartElement element) throws XMLStreamException {
      QName name = element.getName();
      if (!SEARCH_NS.equals(name.getNamespaceURI())) {
        logger.warn("unexpected top element "+name.toString());
        return;
      }

      String localName = name.getLocalPart();

      if ("response".equals(localName))           { handleResponse(reader, element);
      } else if ("result".equals(localName))      { handleResult(reader, element);
      } else if ("facet".equals(localName))       { handleFacet(reader, element);
      } else if ("boxes".equals(localName))       { handleGeoFacet(reader, element);
      } else if ("qtext".equals(localName))       { handleQText(reader, element);
      } else if ("query".equals(localName))       { handleQuery(reader, element);
      } else if ("constraint".equals(localName))  { handleConstraint(reader, element);
      } else if ("warning".equals(localName))     { handleWarning(reader, element);
      } else if ("report".equals(localName))      { handleReport(reader, element);
      } else if ("plan".equals(localName))        { handlePlan(reader, element);
      } else if ("metrics".equals(localName))     { handleMetrics(reader, element);
      } else {
        logger.warn("Unexpected top search element "+name.toString());
      }
    }

    private void handleResponse(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      tempSnippetType  = getAttribute(element, "snippet-format");
      if ( getAttribute(element, "total") != null ) {
        tempTotalResults = Long.parseLong(getAttribute(element, "total"));
      }
      tempPageLength   = Integer.parseInt(getAttribute(element, "page-length"));
      tempStart        = Long.parseLong(getAttribute(element, "start"));
      tempExtractSelected = getAttribute(element, "selected");

      collectTop(reader, element);
    }
    private void collectTop(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      QName startName = element.getName();
      events: while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();

        int eventType = event.getEventType();
        switch (eventType) {
          case XMLStreamConstants.START_ELEMENT:
            handleTop(reader, event.asStartElement());
            break;
          case XMLStreamConstants.END_ELEMENT:
            if (startName.equals(event.asEndElement().getName())) {
              break events;
            }
            break;
        }
      }
    }
    private void handleResult(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      String ruri     = getAttribute(element, "uri");
      String path     = getAttribute(element, "path");
      String mimeType = getAttribute(element, "mimetype");

      String formatString = getAttribute(element, "format");
      Format format = Format.UNKNOWN;
      if (formatString != null && !formatString.equals("")) {
        format = Format.valueOf(formatString.toUpperCase());
      }

      int score = Integer.parseInt(getAttribute(element, "score"));

      double confidence = Double.parseDouble(getAttribute(element, "confidence"));
      double fitness = Double.parseDouble(getAttribute(element, "fitness"));

      currSummary = new MatchDocumentSummaryImpl(
        ruri, score, confidence, fitness, path, mimeType, format, tempExtractSelected);

      if (tempSummary == null) {
        tempSummary = new ArrayList<>();
      }
      tempSummary.add(currSummary);

      collectResult(reader, element);
    }
    private void collectResult(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      QName snippetName       = new QName(SEARCH_NS, "snippet");
      QName extractedName     = new QName(SEARCH_NS, "extracted");
      QName extractedNoneName = new QName(SEARCH_NS, "extracted-none");
      QName metadataName      = new QName(SEARCH_NS, "metadata");
      QName similarName       = new QName(SEARCH_NS, "similar");
      QName relevanceInfoName = new QName(QUERY_NS,  "relevance-info");

      List<XMLEvent> eventBuf = new ArrayList<>();

      QName resultName = element.getName();
      events: while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();

        int eventType = event.getEventType();
        eventType: switch (eventType) {
          case XMLStreamConstants.START_ELEMENT:
            StartElement startElement = event.asStartElement();
            QName startName = startElement.getName();
            if (snippetName.equals(startName)) {
              handleSnippet(reader, startElement);
            } else if (extractedName.equals(startName)) {
              handleExtracted(reader, startElement);
            } else if (extractedNoneName.equals(startName)) {
              handleExtracted(reader, startElement);
            } else if (metadataName.equals(startName)) {
              handleMetadata(reader, startElement);
            } else if (similarName.equals(startName)) {
              handleSimilar(reader, startElement);
            } else if (relevanceInfoName.equals(startName)) {
              handleRelevanceInfo(reader, startElement);
            } else {
              break eventType;
            }

            // found result substructure, so cannot be a raw snippet
            if (eventBuf != null) {
              eventBuf = null;
            }

            break eventType;
          case XMLStreamConstants.END_ELEMENT:
            if (resultName.equals(event.asEndElement().getName())) {
              break events;
            }
            break eventType;
        }

        // buffer candidates for a raw snippet
        if (eventBuf != null) {
          eventBuf.add(event);
        }
      }

      // capture raw snippet
      if (eventBuf != null) {
        int first = tempEvents.size();
        tempEvents.addAll(eventBuf);
        addSnippet(new EventRange(first, tempEvents.size()));
      }
    }
    private void handleExtracted(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      currSummary.extractedEvents = consumeEvents(reader, element);
    }
    private void handleMetadata(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      // TODO:  populate map with element name/content key/value pairs
      // TODO:  special handling for constraint-meta, attribute-meta?
      currSummary.metadataEvents = consumeEvents(reader, element);
    }
    private void handleSimilar(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      if (currSummary.similarUris == null) {
        currSummary.similarUris = new ArrayList<>();
      }
      currSummary.similarUris.add(reader.getElementText());
    }
    private void handleRelevanceInfo(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      currSummary.relevanceEvents = consumeEvents(reader, element);
    }
    private void handlePlan(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      tempPlanEvents = consumeEvents(reader, element);
    }
    private void handleSnippet(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      int first = tempEvents.size();
      tempEvents.add(element);

      collectSnippet(reader, element);

      addSnippet(new EventRange(first, tempEvents.size()));
    }
    private void collectSnippet(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      QName matchName = new QName(SEARCH_NS, "match");

      QName snippetName = element.getName();
      events: while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();
        tempEvents.add(event);

        int eventType = event.getEventType();
        eventType: switch (eventType) {
          case XMLStreamConstants.START_ELEMENT:
            StartElement startElement = event.asStartElement();
            if (matchName.equals(startElement.getName())) {
              handleMatch(reader, startElement);
              break eventType;
            }
            break;
          case XMLStreamConstants.END_ELEMENT:
            if (snippetName.equals(event.asEndElement().getName())) {
              break events;
            }
            break eventType;
        }
      }
    }
    private void addSnippet(EventRange snippetRange) {
      if (currSummary.snippetEvents == null) {
        currSummary.snippetEvents = new ArrayList<>();
      }
      currSummary.snippetEvents.add(snippetRange);
    }
    private void handleMatch(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      MatchLocationImpl location = new MatchLocationImpl(getAttribute(element, "path"));

      QName highlightName = new QName(SEARCH_NS, "highlight");

      StringBuilder buf = new StringBuilder();

      // assumes that highlight elements do not nest
      QName matchName = element.getName();
      events: while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();
        tempEvents.add(event);

        int eventType = event.getEventType();
        switch (eventType) {
          case XMLStreamConstants.START_ELEMENT:
            StartElement startElement = event.asStartElement();
            if (highlightName.equals(startElement.getName())) {
              // add any text preceding a highlight
              if (buf.length() > 0) {
                location.addMatchSnippet(new MatchSnippetImpl(false, buf.toString()));
                buf.setLength(0);
              }
            }
            break;
          case XMLStreamConstants.CDATA:
          case XMLStreamConstants.CHARACTERS:
            buf.append(event.asCharacters().getData());
            break;
          case XMLStreamConstants.END_ELEMENT:
            EndElement endElement = event.asEndElement();

            QName endName = endElement.getName();
            if (matchName.equals(endName)) {
              // add any text following the last highlight
              if (buf.length() > 0) {
                location.addMatchSnippet(new MatchSnippetImpl(false, buf.toString()));
              }
              break events;
            } else if (highlightName.equals(endName)) {
              // add any text contained by a highlight
              location.addMatchSnippet(new MatchSnippetImpl(true, buf.toString()));
              buf.setLength(0);
            }

            break;
        }
      }
      buf = null;

      currSummary.addLocation(location);
    }
    private void handleFacet(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      if (tempFacets == null) {
        tempFacets = new LinkedHashMap<>();
      }

      String facetName = getAttribute(element, "name");

      List<FacetValue> values = new ArrayList<>();

      QName facetValuesName = new QName(SEARCH_NS, "facet-value");

      QName facetElementName = element.getName();
      events: while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();

        int eventType = event.getEventType();
        switch (eventType) {
          case XMLStreamConstants.START_ELEMENT:
            StartElement startElement = event.asStartElement();
            if (facetValuesName.equals(startElement.getName())) {
              values.add(handleFacetValue(reader, startElement));
            } else {
              logger.warn("Unexpected facet element "+startElement.getName().toString());
            }
            break;
          case XMLStreamConstants.END_ELEMENT:
            if (facetElementName.equals(event.asEndElement().getName())) {
              break events;
            }
            break;
        }
      }

      tempFacets.put(
        facetName,
        new FacetResultImpl(facetName,
          values.toArray(new FacetValue[values.size()])));
    }
    private FacetValue handleFacetValue(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      String name = getAttribute(element, "name");
      long count = Long.parseLong(getAttribute(element, "count"));
      FacetValueImpl facetValue = new FacetValueImpl(name, count);
      facetValue.setLabel(reader.getElementText());
      return facetValue;
    }
    private void handleGeoFacet(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      if (tempFacets == null) {
        tempFacets = new LinkedHashMap<>();
      }

      String facetName = getAttribute(element, "name");

      List<FacetValue> values = new ArrayList<>();

      QName boxName = new QName(SEARCH_NS, "box");

      QName boxesName = element.getName();
      events: while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();

        int eventType = event.getEventType();
        switch (eventType) {
          case XMLStreamConstants.START_ELEMENT:
            StartElement startElement = event.asStartElement();
            if (boxName.equals(startElement.getName())) {
              values.add(handleGeoFacetValue(reader, startElement));
            } else {
              logger.warn("Unexpected boxes element "+startElement.getName().toString());
            }
            break;
          case XMLStreamConstants.END_ELEMENT:
            if (boxesName.equals(event.asEndElement().getName())) {
              break events;
            }
            break;
        }
      }

      tempFacets.put(
        facetName,
        new FacetResultImpl(facetName,
          values.toArray(new FacetValue[values.size()])));
    }
    private FacetValue handleGeoFacetValue(XMLEventReader reader, StartElement element) {
      String name = getAttribute(element, "name");
      long count = Long.parseLong(getAttribute(element, "count"));
      double s = Double.parseDouble(getAttribute(element, "s"));
      double w = Double.parseDouble(getAttribute(element, "w"));
      double n = Double.parseDouble(getAttribute(element, "n"));
      double e = Double.parseDouble(getAttribute(element, "e"));
      return new FacetHeatmapValueImpl(name, count, s, w, n, e);
    }
    private void handleQText(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      if (qtextList == null) {
        qtextList = new ArrayList<>();
      }
      qtextList.add(reader.getElementText());
    }
    private void handleQuery(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      tempQueryEvents = consumeEvents(reader, element);
    }
    private void handleMetrics(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      DatatypeFactory dtFactory;
      try {
        dtFactory = DatatypeFactory.newInstance();
      } catch (DatatypeConfigurationException dce) {
        throw new MarkLogicIOException("Cannot instantiate datatypeFactory", dce);
      }

      Calendar now = Calendar.getInstance();

      QName queryName    = new QName(SEARCH_NS, "query-resolution-time");
      QName facetName    = new QName(SEARCH_NS, "facet-resolution-time");
      QName snippetName  = new QName(SEARCH_NS, "snippet-resolution-time");
      QName metadataName = new QName(SEARCH_NS, "metadata-resolution-time");
      QName extractName  = new QName(SEARCH_NS, "extract-resolution-time");
      QName totalName    = new QName(SEARCH_NS, "total-time");

      long qrTime = -1;
      long frTime = -1;
      long srTime = -1;
      long mrTime = -1;
      long erTime  = -1;
      long tTime  = -1;

      QName metricsName = element.getName();
      events: while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();

        int eventType = event.getEventType();
        switch (eventType) {
          case XMLStreamConstants.START_ELEMENT:
            StartElement startElement = event.asStartElement();
            QName startName = startElement.getName();
            String readerValue = reader.getElementText();
					if (readerValue != null && readerValue.length() > 0) {
						if (queryName.equals(startName)) {
							qrTime = parseTime(dtFactory, now, readerValue);
						} else if (facetName.equals(startName)) {
							frTime = parseTime(dtFactory, now, readerValue);
						} else if (snippetName.equals(startName)) {
							srTime = parseTime(dtFactory, now, readerValue);
						} else if (metadataName.equals(startName)) {
							mrTime = parseTime(dtFactory, now, readerValue);
						} else if (extractName.equals(startName)) {
								erTime = parseTime(dtFactory, now, readerValue);
						} else if (totalName.equals(startName)) {
							tTime = parseTime(dtFactory, now, readerValue);
						} else {
							logger.warn("Unexpected metrics element " + startName.toString());
						}
					}
            break;
          case XMLStreamConstants.END_ELEMENT:
            if (metricsName.equals(event.asEndElement().getName())) {
              break events;
            }
            break;
        }
      }

      tempMetrics = new SearchMetricsImpl(qrTime, frTime, srTime, mrTime, erTime, tTime);
    }
    private void handleConstraint(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      if (tempConstraints == null) {
        tempConstraints = new LinkedHashMap<>();
      }

      String constraintName = getAttribute(element, "name");

      tempConstraints.put(constraintName, consumeEvents(reader, element));
    }
    private void handleWarning(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      if (tempWarnings == null) {
        tempWarnings = new ArrayList<>();
      }

      Warning warning = new Warning();
      warning.setId(getAttribute(element, "id"));
      warning.setMessage(reader.getElementText());
      tempWarnings.add(warning);
    }
    private void handleReport(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      if (tempReports == null) {
        tempReports = new ArrayList<>();
      }

      Report report = new Report();
      report.setId(getAttribute(element, "id"));
      report.setName(getAttribute(element, "name"));
      report.setType(getAttribute(element, "type"));
      report.setMessage(reader.getElementText());
      tempReports.add(report);
    }

    private String getAttribute(StartElement element, String name) {
      Attribute att = element.getAttributeByName(new QName(name));
      return (att != null) ? att.getValue() : null;
    }
    private long parseTime(DatatypeFactory dtFactory, Calendar now, String time) {
      return dtFactory.newDurationDayTime(time).getTimeInMillis(now);
    }
    private EventRange consumeEvents(XMLEventReader reader, StartElement element)
      throws XMLStreamException
    {
      int first = tempEvents.size();
      tempEvents.add(element);

      QName startName = element.getName();
      events: while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();
        tempEvents.add(event);

        int eventType = event.getEventType();
        switch (eventType) {
          case XMLStreamConstants.END_ELEMENT:
            if (startName.equals(event.asEndElement().getName())) {
              break events;
            }
        }
      }

      return new EventRange(first, tempEvents.size());
    }
  }

  static private class ExtractedItemImpl implements ExtractedItem {
    String item;

    public ExtractedItemImpl(String item) {
      this.item = item;
    }

    @Override
    public <T extends StructureReadHandle> T get(T handle) {
      HandleAccessor.receiveContent(handle, item);
      return handle;
    }
    @Override
    public <T> T getAs(Class<T> as) {
      ContentHandle<T> readHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(as);
      if ( readHandle == null ) return null;
      HandleAccessor.receiveContent(readHandle, item);
      return readHandle.get();
    }
  }

  static private class ExtractedResultImpl implements ExtractedResult {
    boolean isEmpty = false;
    String kind;
    private List<String> itemStrings;
    private List<ExtractedItem> items;
    private Iterator<ExtractedItem> internalIterator;

    @Override
    public boolean isEmpty() {
      return isEmpty;
    }
    @Override
    public String getKind() {
      return kind;
    }
    @Override
    public int size() {
      if ( items == null ) return 0;
      return items.size();
    }

    @Override
    public Iterator<ExtractedItem> iterator() {
      return items.iterator();
    }

    private void setItems(List<String> itemStrings) {
      if ( itemStrings == null ) return;
      this.itemStrings = itemStrings;
      items = new ArrayList<>(itemStrings.size());
      for ( String itemString : itemStrings ) {
        items.add( new ExtractedItemImpl(itemString) );
      }
      internalIterator = items.iterator();
    }

    @Override
    public boolean hasNext() {
      return internalIterator.hasNext();
    }

    @Override
    public ExtractedItem next() {
      return internalIterator.next();
    }

    @Override
    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("ExtractedResult: ");
      sb.append(isEmpty == true ? "isEmpty:[true] " : "");
      sb.append(kind != null ? "kind:[" + kind + "] " : "");
      for ( int i=1; i <= itemStrings.size(); i++ ) {
        String item = itemStrings.get(i - 1);
        sb.append("item_" + i + ":[" + item + "] ");
      }
      return sb.toString();
    }
  };
}
