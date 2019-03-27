/*
 * Copyright 2012-2019 MarkLogic Corporation
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

