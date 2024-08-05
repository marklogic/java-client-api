/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.marker.OperationNotSupported;

/**
 * A UrisHandle represents a set of uris of search results returned by the server.
 *
 * <p>The precise nature of the results returned depends on the query options used for the
 * search and on the configuration of this handle.</p>
 */
public class UrisHandle
  extends BaseHandle<Reader, OperationNotSupported>
  implements UrisReadHandle, Iterable<String>, Closeable
{
  static final private Logger logger = LoggerFactory.getLogger(UrisHandle.class);

  private Reader reader;
  private BufferedReader bufferedReader;

  public UrisHandle() {
    super();
    super.setMimetype("text/uri-list");
  }

  @Override
  protected Class<Reader> receiveAs() {
    return Reader.class;
  }

  @Override
  protected void receiveContent(Reader content) {
    this.reader = content;
  }

  @Override
  public Iterator<String> iterator() {
    if ( bufferedReader == null ) {
      bufferedReader = new BufferedReader(reader);
    }
    return bufferedReader.lines().iterator();
  }

  public void close() {
    try {
      if ( bufferedReader != null ) bufferedReader.close();
    } catch (IOException e) {
    } finally {
      try {
        if ( reader != null ) reader.close();
      } catch (IOException e) {
      }
    }
  }
}

