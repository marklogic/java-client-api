/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.TuplesBuilder;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.TuplesReadHandle;
import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.query.TuplesResults;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesMetrics;
import java.util.Map;

/**
 * A TuplesHandle represents a set of tuples returned by a query on the server.
 */
public class TuplesHandle
  extends BaseHandle<InputStream, OperationNotSupported>
  implements TuplesReadHandle, TuplesResults
{
  static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

  private TuplesBuilder.Tuples tuplesHolder;
  private Unmarshaller unmarshaller;

  private ValuesDefinition valdef = null;
  private Map<String, AggregateResult> hashedAggregates = null;

  public TuplesHandle() {
    super();
    super.setFormat(Format.XML);

    try {
      JAXBContext jc = JaxbContextLoader.CACHED_CONTEXT;
      unmarshaller = jc.createUnmarshaller();
    } catch (JAXBException e) {
      throw new MarkLogicBindingException(e);
    } catch (NoClassDefFoundError ncdfe) {
      throw new MarkLogicBindingException(new JAXBException("JAXB context initialization failed"));
    }
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
      throw new IllegalArgumentException("ValuesHandle supports the XML format only");
  }

  /**
   * Fluent setter for the format associated with this handle.
   *
   * This handle only supports XML.
   *
   * @param format The format, which must be Format.XML or an exception will be raised.
   * @return The TuplesHandle instance on which this method was called.
   */
  public TuplesHandle withFormat(Format format) {
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
      tuplesHolder = (TuplesBuilder.Tuples) unmarshaller.unmarshal(
        new InputStreamReader(content, StandardCharsets.UTF_8)
      );
    } catch (JAXBException e) {
      logger.error("Failed to unmarshall tuples",e);
      throw new MarkLogicIOException(e);
    } finally {
      try {
        content.close();
      } catch (IOException e) {
        // ignore.
      }
    }
  }

  /**
   * Returns the tuples query definition used to identify this set of tuples.
   * @return The query criteria.
   */
  @Override
  public ValuesDefinition getQueryCriteria() {
    return valdef;
  }
  /**
   * Specifies the tuples query definition used to identify this set of tuples.
   * @param vdef	The query criteria.
   */
  public void setQueryCriteria(ValuesDefinition vdef) {
    valdef = vdef;
  }

  @Override
  public String getName() {
    return tuplesHolder.getName();
  }

  /**
   * Returns an array of the Tuples returned by this query.
   * @return The tuples array.
   */
  @Override
  public Tuple[] getTuples() {
    return tuplesHolder.getTuples();
  }

  @Override
  public AggregateResult[] getAggregates() {
    return tuplesHolder.getAggregates();
  }

  @Override
  public AggregateResult getAggregate(String name) {
    if (hashedAggregates == null) {
      hashedAggregates = new HashMap<>();
      for (AggregateResult aggregate : tuplesHolder.getAggregates()) {
        hashedAggregates.put(aggregate.getName(), aggregate);
      }
    }

    if (hashedAggregates.containsKey(name)) {
      return hashedAggregates.get(name);
    } else {
      return null;
    }
  }

  @Override
  public ValuesMetrics getMetrics() {
    return tuplesHolder.getMetrics();
  }

  /**
   * Initialization-on-demand holder for {@link TuplesHandle}'s JAXB context.
   */
  private static class JaxbContextLoader {

    private static final JAXBContext CACHED_CONTEXT;

    static {
      try {
        CACHED_CONTEXT = JAXBContext.newInstance(TuplesBuilder.Tuples.class);
      } catch (JAXBException e) {
        throw new MarkLogicBindingException(e);
      }
    }
  }

}
