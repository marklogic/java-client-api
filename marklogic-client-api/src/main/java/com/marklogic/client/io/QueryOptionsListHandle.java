/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.QueryOptionsListReadHandle;
import com.marklogic.client.query.QueryOptionsListBuilder;
import com.marklogic.client.query.QueryOptionsListResults;

/**
 * A QueryOptionsListHandle is used to access the list of named query options that exist on the server.
 */
public class QueryOptionsListHandle
  extends BaseHandle<InputStream, OperationNotSupported>
  implements QueryOptionsListReadHandle, QueryOptionsListResults
{
  static final private Logger logger = LoggerFactory.getLogger(QueryOptionsListHandle.class);

  private QueryOptionsListBuilder.OptionsList optionsHolder;
  private Marshaller marshaller;
  private Unmarshaller unmarshaller;

  /**
   * The constructor.
   */
  public QueryOptionsListHandle() {
    super();
    super.setFormat(Format.XML);
    try {
      JAXBContext jc = JaxbContextLoader.CACHED_CONTEXT;
      marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
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
      throw new IllegalArgumentException("QueryOptionsListHandle supports the XML format only");
  }

  /**
   * Fluent setter for the format associated with this handle.
   *
   * This handle only supports XML.
   *
   * @param format The format, which must be Format.XML or an exception will be raised.
   * @return The QueryOptionsListHandle instance on which this method was called.
   */
  public QueryOptionsListHandle withFormat(Format format) {
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
      optionsHolder = (QueryOptionsListBuilder.OptionsList) unmarshaller.unmarshal(
        new InputStreamReader(content, StandardCharsets.UTF_8)
      );
    } catch (JAXBException e) {
      logger.error("Failed to unmarshall query options list",e);
      throw new MarkLogicIOException(e);
    }
  }

  /**
   * Returns a HashMap of the named query options from the server.
   *
   * The keys are the names of the query options, the values are the corresponding URIs on the server.
   *
   * @return The map of names to URIs.
   */
  @Override
  public HashMap<String, String> getValuesMap() {
    if (optionsHolder == null ) return null;
    else return optionsHolder.getOptionsMap();
  }

  /**
   * Initialization-on-demand holder for {@link QueryOptionsListHandle}'s JAXB context.
   */
  private static class JaxbContextLoader {

    private static final JAXBContext CACHED_CONTEXT;

    static {
      try {
        CACHED_CONTEXT = JAXBContext.newInstance(QueryOptionsListBuilder.OptionsList.class);
      } catch (JAXBException e) {
        throw new MarkLogicBindingException(e);
      }
    }
  }
}
