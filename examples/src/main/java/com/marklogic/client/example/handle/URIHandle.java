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
package com.marklogic.client.example.handle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

import okhttp3.*;

/**
 * A URI Handle sends read document content to a URI or
 * receives written database content from a URI.
 */
public class URIHandle
  extends BaseHandle<InputStream, InputStream>
  implements
    BinaryReadHandle, BinaryWriteHandle,
    GenericReadHandle, GenericWriteHandle,
    JSONReadHandle, JSONWriteHandle,
    TextReadHandle, TextWriteHandle,
    XMLReadHandle, XMLWriteHandle
{
  private final OkHttpClient client;
  private final HttpUrl      baseUri;

  private String  currentPath;
  private boolean usePut = true;

  public URIHandle(String host, int port, String basePath, String user, String password) {
    super();

    final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();

    // example supports only digest authentication
    this.client = new OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .authenticator(new CachingAuthenticatorDecorator(
                new DigestAuthenticator(new Credentials(user, password)), authCache))
            .addInterceptor(new AuthenticationCacheInterceptor(authCache))
            .build();

    // example supports only http
    this.baseUri = new HttpUrl.Builder()
            .scheme("http" )
            .host(host)
            .port(port)
            .encodedPath(basePath)
            .build();
  }

  public String get() {
    return currentPath;
  }
  public void set(String path) {
    this.currentPath = path;
  }
  public URIHandle with(String path) {
    set(path);
    return this;
  }

  public URIHandle withFormat(Format format) {
    setFormat(format);
    return this;
  }

  public boolean isUsePut() {
    return usePut;
  }
  public void setUsePut(boolean usePut) {
    this.usePut = usePut;
  }

  public boolean check() {
    return check(get());
  }
  public boolean check(String path) {
    try {
      Request.Builder requestBldr = makeRequestBldr(path).head();

      Response response = client
              .newCall(requestBldr.build())
              .execute();

      int responseCode = response.code();
      response.close();

      return responseCode < 300;
    } catch(IOException e) {
      throw new MarkLogicIOException(e);
    }
  }

  @Override
  protected Class<InputStream> receiveAs() {
    return InputStream.class;
  }
  @Override
  protected void receiveContent(InputStream content) {
    if (content == null) return;
    try {
      HttpUrl uri = makeUri();
      if (uri == null) {
        throw new IllegalStateException("No path for output");
      }

      Format format = getFormat();
      String mimetype = (format == null || format == Format.UNKNOWN) ?
              Format.BINARY.getDefaultMimetype() : format.getDefaultMimetype();

      RequestBody requestBody = RequestBody.create(
              inputStreamToBytes(content), (mimetype == null) ? null : MediaType.parse(mimetype));

      Request.Builder requestBldr = makeRequestBldr(uri);
      requestBldr = isUsePut() ? requestBldr.put(requestBody) : requestBldr.post(requestBody);

      Response response = client
              .newCall(requestBldr.build())
              .execute();

      int responseCode = response.code();
      String responseMsg = response.message();
      response.close();

      if (responseCode >= 300) {
        throw new MarkLogicIOException("Could not write to "+uri.toString()+": "+responseMsg);
      }
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }
  @Override
  protected InputStream sendContent() {
    try {
      HttpUrl uri = makeUri();
      if (uri == null) {
        throw new IllegalStateException("No uri for input");
      }

      Request.Builder requestBldr = makeRequestBldr(uri).get();

      Response response = client
              .newCall(requestBldr.build())
              .execute();

      int responseCode = response.code();
      String responseMsg = response.message();

      if (responseCode >= 300) {
        response.close();
        throw new MarkLogicIOException("Could not read from "+uri.toString()+": "+responseMsg);
      }

      ResponseBody responseBody = response.body();
      if (responseBody == null) {
        throw new MarkLogicIOException("Received null response to write for "+uri.toString());
      } else if (responseBody.contentLength() == 0) {
        response.close();
        throw new MarkLogicIOException("Received empty response to write for "+uri.toString());
      }

      return responseBody.byteStream();
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }
  private Request.Builder makeRequestBldr(String path) {
    return makeRequestBldr(makeUri(path));
  }
  private Request.Builder makeRequestBldr(HttpUrl uri) {
    if (uri == null) return null;
    return new Request.Builder()
            .url(uri);
  }
  private HttpUrl makeUri() {
    return makeUri(get());
  }
  private HttpUrl makeUri(String path) {
    if (path == null) return null;
    return this.baseUri.resolve(path);
  }
  private byte[] inputStreamToBytes(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] byteArray = new byte[8192];
    for (int byteCount=in.read(byteArray); byteCount != -1; byteCount=in.read(byteArray)) {
      out.write(byteArray, 0, byteCount);
    }
    out.flush();
    in.close();
    return out.toByteArray();
  }
}
