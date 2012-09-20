/*
 * Copyright 2012 MarkLogic Corporation
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.conn.PoolingClientConnectionManager;  // 4.2
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager; // 4.1
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.marklogic.client.DatabaseClientFactory.Authentication;
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
	private HttpClient  client;
	private HttpContext context;
	private URI         baseUri;
	private URI         currentUri;
	private boolean     usePut = true;

	public URIHandle(HttpClient client) {
		super();
   		setResendable(true);
		setClient(client);
	}
	public URIHandle(HttpClient client, URI baseUri) {
		this(client);
		setBaseUri(baseUri);
	}
	public URIHandle(HttpClient client, String baseUri) {
		this(client);
		setBaseUri(baseUri);
	}
	public URIHandle(String host, int port, String user, String password, Authentication authType) {
		super();

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
				new Scheme("http", port, PlainSocketFactory.getSocketFactory())
				);

		// PoolingClientConnectionManager connMgr = new PoolingClientConnectionManager(  // 4.2
        ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(
				schemeRegistry);
		connMgr.setDefaultMaxPerRoute(100);

        DefaultHttpClient defaultClient = new DefaultHttpClient(connMgr);

        List<String> prefList = new ArrayList<String>();
		if (authType == Authentication.BASIC)
			prefList.add(AuthPolicy.BASIC);
		else if (authType == Authentication.DIGEST)
			prefList.add(AuthPolicy.DIGEST);
		else
			throw new IllegalArgumentException("Unknown authentication type "+authType.name());

		defaultClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, prefList);

		defaultClient.getCredentialsProvider().setCredentials(
		        new AuthScope(host, port), 
		        new UsernamePasswordCredentials(user, password)
		        );

		setClient(defaultClient);
	}

	public URI get() {
		return currentUri;
	}
	public void set(URI uri) {
		this.currentUri = makeUri(uri);
	}
	public void set(String uri) {
		this.currentUri = makeUri(uri);
	}
	public URIHandle with(URI uri) {
		set(uri);
		return this;
	}
	public URIHandle with(String uri) {
		set(uri);
		return this;
	}

	public URI getBaseUri() {
		return baseUri;
	}
	public void setBaseUri(URI baseUri) {
		this.baseUri = baseUri;
	}
	public void setBaseUri(String uri) {
		setBaseUri(makeUri(uri));
	}

	public URIHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public HttpClient getClient() {
		return client;
	}
	public void setClient(HttpClient client) {
		this.client = client;
	}

	public HttpContext getContext() {
		if (context == null)
			context = new BasicHttpContext();
		return context;
	}
	public void setContext(HttpContext context) {
		this.context = context;
	}

	public boolean isUsePut() {
		return usePut;
	}
	public void setUsePut(boolean usePut) {
		this.usePut = usePut;
	}

	public boolean check() {
		return checkImpl(currentUri);
	}
	public boolean check(String uri) {
		return checkImpl(makeUri(uri));
	}
	public boolean check(URI uri) {
		return checkImpl(makeUri(uri));
	}
	private boolean checkImpl(URI uri) {
		try {
			HttpHead method = new HttpHead(uri);

			HttpResponse response = client.execute(method, getContext());

			StatusLine status = response.getStatusLine();

			return status.getStatusCode() == 200;
		} catch(ClientProtocolException e) {
			throw new MarkLogicIOException(e);
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
		if (content == null) {
			return;
		}

		try {
			URI uri = get();
			if (uri == null) {
				throw new IllegalStateException("No uri for output");
			}

			HttpUriRequest method = null;
			HttpEntityEnclosingRequestBase receiver = null;
			if (isUsePut()) {
				HttpPut putter = new HttpPut(uri);
				method         = putter;
				receiver       = putter;
			} else {
				HttpPost poster = new HttpPost(uri);
				method          = poster;
				receiver        = poster;
			}

			InputStreamEntity entity = new InputStreamEntity(content, -1);

			receiver.setEntity(entity);

			HttpResponse response = client.execute(method, getContext());

			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() >= 300) {
				throw new MarkLogicIOException("Could not write to "+uri.toString()+": "+status.getReasonPhrase());
			}

			content.close();
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}
	@Override
	protected InputStream sendContent() {
		try {
			URI uri = get();
			if (uri == null) {
				throw new IllegalStateException("No uri for input");
			}

			HttpGet method = new HttpGet(uri);

			HttpResponse response = client.execute(method, getContext());

			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() >= 300) {
				throw new MarkLogicIOException("Could not read from "+uri.toString()+": "+status.getReasonPhrase());
			}

			HttpEntity entity = response.getEntity();
			if (entity == null) {
				throw new MarkLogicIOException("Received empty response to write for "+uri.toString());
			}

			InputStream stream = entity.getContent();
			if (stream == null) {
				throw new MarkLogicIOException("Could not get stream to write for "+uri.toString());
			}

			return stream;
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}

	private URI makeUri(URI uri) {
		return (baseUri != null) ? baseUri.resolve(uri) : uri;
	}
	private URI makeUri(String uri) {
		try {
			return (baseUri != null) ? baseUri.resolve(uri) : new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
