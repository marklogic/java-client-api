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

import com.marklogic.client.*;
import com.marklogic.client.DatabaseClient.ConnectionResult;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.BasicAuthContext;
import com.marklogic.client.DatabaseClientFactory.CertificateAuthContext;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.DatabaseClientFactory.KerberosAuthContext;
import com.marklogic.client.DatabaseClientFactory.SAMLAuthContext;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.bitemporal.TemporalDescriptor;
import com.marklogic.client.bitemporal.TemporalDocumentManager.ProtectionLevel;
import com.marklogic.client.document.ContentDescriptor;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;

import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.CtsQueryWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawCtsQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.RawQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.SuggestDefinition;
import com.marklogic.client.query.ValueQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesListDefinition;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.SPARQLBinding;
import com.marklogic.client.semantics.SPARQLBindings;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLRuleset;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.util.RequestParameters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Part;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class OkHttpServices implements RESTServices {
  static final private Logger logger = LoggerFactory.getLogger(OkHttpServices.class);

  static final public String OKHTTP_LOGGINGINTERCEPTOR_LEVEL = "com.marklogic.client.okhttp.httplogginginterceptor.level";
  static final public String OKHTTP_LOGGINGINTERCEPTOR_OUTPUT = "com.marklogic.client.okhttp.httplogginginterceptor.output";

  static final private String DOCUMENT_URI_PREFIX = "/documents?uri=";

  static final private int DELAY_FLOOR       =    125;
  static final private int DELAY_CEILING     =   2000;
  static final private int DELAY_MULTIPLIER  =     20;
  static final private int DEFAULT_MAX_DELAY = 120000;
  static final private int DEFAULT_MIN_RETRY =      8;

  private final static MediaType URLENCODED_MIME_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
  private final static String UTF8_ID = StandardCharsets.UTF_8.toString();

  static final private ConnectionPool connectionPool = new ConnectionPool();

  private DatabaseClient databaseClient;
  private String database = null;
  private HttpUrl baseUri;
  private OkHttpClient client;
  private boolean released = false;
  private Authentication type = null;

  private Random randRetry    = new Random();

  private int maxDelay = DEFAULT_MAX_DELAY;
  private int minRetry = DEFAULT_MIN_RETRY;

  private boolean checkFirstRequest = true;

  private Set<Integer> retryStatus = new HashSet<>();

  static protected class ThreadState {
    boolean isFirstRequest;
    ThreadState(boolean value) {
      isFirstRequest = value;
    }
  }

  private final ThreadLocal<ThreadState> threadState = new ThreadLocal<ThreadState>() {
    @Override
    protected ThreadState initialValue() {
      return new ThreadState(checkFirstRequest);
    }
  };

  public OkHttpServices() {
    retryStatus.add(STATUS_BAD_GATEWAY);
    retryStatus.add(STATUS_SERVICE_UNAVAILABLE);
    retryStatus.add(STATUS_GATEWAY_TIMEOUT);
  }

  @Override
  public Set<Integer> getRetryStatus() {
    return retryStatus;
  }

  @Override
  public int getMaxDelay() {
    return maxDelay;
  }
  @Override
  public void setMaxDelay(int maxDelay) {
    this.maxDelay = maxDelay;
  }

  private FailedRequest extractErrorFields(Response response) {
    if (response == null) return null;
    try {
      if (response.code() == STATUS_UNAUTHORIZED) {
        FailedRequest failure = new FailedRequest();
        failure.setMessageString("Unauthorized");
        failure.setStatusString("Failed Auth");
        return failure;
      }
      String responseBody = getEntity(response.body(), String.class);
      InputStream is = new ByteArrayInputStream(responseBody.getBytes("UTF-8"));
      FailedRequest handler = FailedRequest.getFailedRequest(response.code(), response.header(HEADER_CONTENT_TYPE), is);
      if (handler.getMessage() == null) {
        handler.setMessageString(responseBody);
      }
      return handler;
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("UTF-8 is unsupported", e);
    } finally {
      closeResponse(response);
    }
  }

  @Override
  public void connect(String host, int port, String database, SecurityContext securityContext){
    SSLContext sslContext = null;
    SSLHostnameVerifier sslVerifier = null;
    X509TrustManager trustManager = null;
      
    if (host == null) 
    	throw new IllegalArgumentException("No host provided");
    
    OkHttpClient.Builder clientBldr = new OkHttpClient.Builder()
    	      .followRedirects(false)
    	      .followSslRedirects(false)
    	      // all clients share a single connection pool
    	      .connectionPool(connectionPool)
    	      // cookies are ignored (except when a Transaction is being used)
    	      .cookieJar(CookieJar.NO_COOKIES)
    	      // no timeouts since some of our clients' reads and writes can be massive
    	      .readTimeout(0, TimeUnit.SECONDS)
    	      .writeTimeout(0, TimeUnit.SECONDS);
    
	if (securityContext instanceof BasicAuthContext) {
	    BasicAuthContext basicContext = (BasicAuthContext) securityContext;
	    if (basicContext.getSSLContext() != null) {
            sslContext = basicContext.getSSLContext();
            if (basicContext.getTrustManager() != null)
                trustManager = basicContext.getTrustManager();
        }
		clientBldr = configureAuthentication(basicContext, clientBldr);
	} 
	else if (securityContext instanceof DigestAuthContext) {
	    DigestAuthContext digestContext = (DigestAuthContext) securityContext;
	    if (digestContext.getSSLContext() != null) {
            sslContext = digestContext.getSSLContext();
            if (digestContext.getTrustManager() != null)
                trustManager = digestContext.getTrustManager();
        }
		clientBldr = configureAuthentication((DigestAuthContext) securityContext, clientBldr);
	} 
	else if (securityContext instanceof KerberosAuthContext) {
		KerberosAuthContext kerberosContext = (KerberosAuthContext) securityContext;
		if (kerberosContext.getSSLContext() != null) {
			sslContext = kerberosContext.getSSLContext();
			if (kerberosContext.getTrustManager() != null)
				trustManager = kerberosContext.getTrustManager();
		}
	    clientBldr = configureAuthentication(kerberosContext, host,clientBldr); 
	} else if (securityContext instanceof CertificateAuthContext) {
		CertificateAuthContext certificateContext = (CertificateAuthContext) securityContext;
		type = Authentication.CERTIFICATE;
		sslContext = certificateContext.getSSLContext();
		if (certificateContext.getTrustManager() != null)
			trustManager = certificateContext.getTrustManager();
		checkFirstRequest = false;
	} else if (securityContext instanceof SAMLAuthContext) {
		SAMLAuthContext samlAuthContext = (SAMLAuthContext) securityContext;
		if (samlAuthContext.getSSLContext() != null) {
            sslContext = samlAuthContext.getSSLContext();
            if (samlAuthContext.getTrustManager() != null)
                trustManager = samlAuthContext.getTrustManager();
        }
        clientBldr = configureAuthentication(samlAuthContext, clientBldr);
	} else {
		throw new IllegalArgumentException("securityContext must be of type BasicAuthContext, "
				+ "DigestAuthContext, KerberosAuthContext, CertificateAuthContext or SAMLAuthContext");
	}
	if ((securityContext.getSSLContext() != null) || securityContext instanceof CertificateAuthContext) {
        if (securityContext.getSSLHostnameVerifier() != null) {
            sslVerifier = securityContext.getSSLHostnameVerifier();
        } 
        else {
	        sslVerifier = SSLHostnameVerifier.COMMON;
	    }
	}
	HostnameVerifier hostnameVerifier = null;
	if (sslVerifier == SSLHostnameVerifier.ANY) {
		hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
	} else if (sslVerifier == SSLHostnameVerifier.COMMON || sslVerifier == SSLHostnameVerifier.STRICT) {
		hostnameVerifier = null;
	} else if (sslVerifier != null) {
		hostnameVerifier = new SSLHostnameVerifier.HostnameVerifierAdapter(sslVerifier);
	} 
	
    this.database = database;

    this.baseUri = new HttpUrl.Builder()
      .scheme(sslContext == null ? "http" : "https")
      .host(host)
      .port(port)
      .encodedPath("/v1/ping")
      .build();

    if (sslContext != null) {
      if (trustManager == null) {
        String javaVersion = System.getProperty("java.version");
        int javaMajorVersion = Integer.parseInt(javaVersion.substring(0, javaVersion.indexOf(".")));
        // OKHttp starts requiring the trust manager in Java 9
        if (javaMajorVersion < 9) {
          clientBldr.sslSocketFactory(sslContext.getSocketFactory());
        } else {
          // transitional workaround -- at next backward incompatibility boundary, replace try with thrown exception
          try {
            TrustManagerFactory trustMgrFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustMgrFactory.init((KeyStore) null);
            TrustManager[] trustMgrs = trustMgrFactory.getTrustManagers();
            if (trustMgrs == null || trustMgrs.length == 0)
              throw new IllegalArgumentException("no trust manager and could not get default trust manager");
            if (!(trustMgrs[0] instanceof X509TrustManager))
              throw new IllegalArgumentException("no trust manager and default is not an X509TrustManager");
            trustManager = (X509TrustManager) trustMgrs[0];
            sslContext.init(null, trustMgrs, null);
            clientBldr.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
          } catch (KeyStoreException e) {
            throw new IllegalArgumentException("no trust manager and cannot initialize factory for default", e);
          } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("no trust manager and no algorithm for default manager", e);
          } catch (KeyManagementException e) {
            throw new IllegalArgumentException("no trust manager and cannot initialize context with default", e);
          }
        }
      } else {
        clientBldr.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
      }
    }

    if ( hostnameVerifier != null ) {
      clientBldr = clientBldr.hostnameVerifier(hostnameVerifier);
    }

    Properties props = System.getProperties();

    if (props.containsKey(OKHTTP_LOGGINGINTERCEPTOR_LEVEL)) {
      final boolean useLogger = "LOGGER".equalsIgnoreCase(props.getProperty(OKHTTP_LOGGINGINTERCEPTOR_OUTPUT));
      final boolean useStdErr = "STDERR".equalsIgnoreCase(props.getProperty(OKHTTP_LOGGINGINTERCEPTOR_OUTPUT));
      HttpLoggingInterceptor networkInterceptor = new HttpLoggingInterceptor(
        new HttpLoggingInterceptor.Logger() {
          public void log(String message) {
            if ( useLogger == true ) {
              logger.debug(message);
            } else if ( useStdErr == true ) {
              System.err.println(message);
            } else {
              System.out.println(message);
            }
          }
        }
      );
      if ( "BASIC".equalsIgnoreCase(props.getProperty(OKHTTP_LOGGINGINTERCEPTOR_LEVEL)) ) {
        networkInterceptor = networkInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
      } else if ( "BODY".equalsIgnoreCase(props.getProperty(OKHTTP_LOGGINGINTERCEPTOR_LEVEL)) ) {
        networkInterceptor = networkInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      } else if ( "HEADERS".equalsIgnoreCase(props.getProperty(OKHTTP_LOGGINGINTERCEPTOR_LEVEL)) ) {
        networkInterceptor = networkInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
      } else if ( "NONE".equalsIgnoreCase(props.getProperty(OKHTTP_LOGGINGINTERCEPTOR_LEVEL)) ) {
        networkInterceptor = networkInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
      }
      clientBldr = clientBldr.addNetworkInterceptor(networkInterceptor);
    }

    if (props.containsKey(MAX_DELAY_PROP)) {
      String maxDelayStr = props.getProperty(MAX_DELAY_PROP);
      if (maxDelayStr != null && maxDelayStr.length() > 0) {
        int max = Integer.parseInt(maxDelayStr);
        if (max > 0) {
          maxDelay = max * 1000;
        }
      }
    }
    if (props.containsKey(MIN_RETRY_PROP)) {
      String minRetryStr = props.getProperty(MIN_RETRY_PROP);
      if (minRetryStr != null && minRetryStr.length() > 0) {
        int min = Integer.parseInt(minRetryStr);
        if (min > 0) {
          minRetry = min;
        }
      }
    }

    this.client = clientBldr.build();
    // System.setProperty("javax.net.debug", "all"); // all or ssl
    /*
    // long-term alternative to isFirstRequest alive
    // HttpProtocolParams.setUseExpectContinue(httpParams, false);
    // httpParams.setIntParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE, 1000);
    */
  }
  
  public OkHttpClient.Builder configureAuthentication(BasicAuthContext basicAuthContext, OkHttpClient.Builder clientBuilder) {
      String user = basicAuthContext.getUser();
      String password = basicAuthContext.getPassword();
      type = Authentication.BASIC;
      if (user == null) 
          throw new IllegalArgumentException("No user provided");
      if (password == null) 
          throw new IllegalArgumentException("No password provided");
      Credentials credentials = new Credentials(user, password);
	  OkHttpClient.Builder builder = clientBuilder;
	  Interceptor interceptor = new HTTPBasicAuthInterceptor(credentials);
	  checkFirstRequest = false;
	  
	  if(interceptor != null) 
		  builder.addInterceptor(interceptor);
	  return builder;
  }
  
  public OkHttpClient.Builder configureAuthentication(DigestAuthContext digestAuthContext, OkHttpClient.Builder clientBuilder) {
	  	OkHttpClient.Builder builder = clientBuilder;
        String user = digestAuthContext.getUser();
        String password = digestAuthContext.getPassword();
        type = Authentication.DIGEST;
        if (user == null) 
            throw new IllegalArgumentException("No user provided");
        if (password == null) 
            throw new IllegalArgumentException("No password provided");
        Credentials credentials = new Credentials(user, password);
        final Map<String,CachingAuthenticator> authCache = new ConcurrentHashMap<String,CachingAuthenticator>();
	    CachingAuthenticator authenticator = new DigestAuthenticator(credentials);
	    Interceptor interceptor =  new AuthenticationCacheInterceptor(authCache);
        checkFirstRequest = true;
        
        if(authenticator != null) {
        	builder.authenticator(new CachingAuthenticatorDecorator(authenticator, authCache));
        }
        if(interceptor != null) {
  		  builder.addInterceptor(interceptor);
        }
	    return builder;
  }
  
  public OkHttpClient.Builder configureAuthentication(KerberosAuthContext keberosAuthContext, String host, OkHttpClient.Builder clientBuilder) {
	  type = Authentication.KERBEROS;
	  Map<String, String> kerberosOptions = keberosAuthContext.getKrbOptions();
	  Interceptor interceptor = new HTTPKerberosAuthInterceptor(host, kerberosOptions);
      checkFirstRequest = false;
	  OkHttpClient.Builder builder = clientBuilder;
	  if(interceptor != null) 
		  builder.addInterceptor(interceptor);
	  return builder;
  }
  
  public OkHttpClient.Builder configureAuthentication(SAMLAuthContext samlAuthContext, OkHttpClient.Builder clientBuilder) {
      type = Authentication.SAML;
      Interceptor interceptor = null;
      String authorizationTokenValue = samlAuthContext.getToken();
      
      if(authorizationTokenValue != null && authorizationTokenValue.length() > 0) {
          interceptor = new HTTPSamlAuthInterceptor(authorizationTokenValue);
      } else if(samlAuthContext.getAuthorizer()!=null) {
           interceptor = new HTTPSamlAuthInterceptor(samlAuthContext.getAuthorizer());
      } else if(samlAuthContext.getRenewer()!=null) {
          interceptor = new HTTPSamlAuthInterceptor(samlAuthContext.getAuthorization(),samlAuthContext.getRenewer());
      } else
          throw new IllegalArgumentException("Either a call back or renewer expected.");
	  
      checkFirstRequest = false;
	  OkHttpClient.Builder builder = clientBuilder;
	  
	  if(interceptor != null) 
		  builder.addInterceptor(interceptor);
	  return builder;
  }

  @Override
  public DatabaseClient getDatabaseClient() {
    return databaseClient;
  }
  @Override
  public void setDatabaseClient(DatabaseClient client) {
    this.databaseClient = client;
  }

  private OkHttpClient getConnection() {
    if ( client != null ) {
      return client;
    } else if ( released ) {
      throw new IllegalStateException(
        "You cannot use this connected object anymore--connection has already been released");
    } else {
      throw new MarkLogicInternalException("Cannot proceed--connection is null for unknown reason");
    }
  }

  @Override
  public void release() {
    if ( client == null ) return;
    try {
      released = true;
      client.dispatcher().executorService().shutdownNow();
    } finally {
      try {
        if ( client.cache() != null ) client.cache().close();
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      } finally {
        client = null;
        logger.debug("Releasing connection");
      }
    }
  }

  private boolean isFirstRequest() {
    return threadState.get().isFirstRequest;
  }
  private void setFirstRequest(boolean value) {
    threadState.get().isFirstRequest = value;
  }
  private void checkFirstRequest() {
    if (checkFirstRequest) setFirstRequest(true);
  }

  private int makeFirstRequest(int retry) {
    return makeFirstRequest(baseUri, "ping", retry);
  }

  private int makeFirstRequest(HttpUrl requestUri, String path, int retry) {
    Response response = sendRequestOnce(setupRequest(requestUri, path, null).head());
    int statusCode = response.code();
    if (!retryStatus.contains(statusCode)) {
      closeResponse(response);
      return 0;
    }

    String retryAfterRaw = response.header("Retry-After");
    closeResponse(response);

    int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
    return Math.max(retryAfter, calculateDelay(randRetry, retry));
  }

  private RequestParameters addTemporalProtectionParams(RequestParameters params, String uri, ProtectionLevel level,
                                                        String duration, Calendar expiryTime, String archivePath) {
    if (params == null)
      params = new RequestParameters();
    params.add("uri", uri);
    params.add("level", level.toString());
    if (duration != null)
      params.add("duration", duration);
    if (expiryTime != null) {
      String formattedSystemTime = DatatypeConverter.printDateTime(expiryTime);
      params.add("expireTime", formattedSystemTime);
    }
    if (archivePath != null)
      params.add("archivePath", archivePath);
    return params;
  }

  @Override
  public String advanceLsqt(RequestLogger reqlog, String temporalCollection, long lag) {
    if (logger.isDebugEnabled())
      logger.debug("Advancing LSQT in temporal collection {}", temporalCollection);
    logRequest(reqlog, "wiped %s document", temporalCollection);
    RequestParameters params = new RequestParameters();
    params.add("result", "advance-lsqt");
    if ( lag > 0 ) params.add("lag", String.valueOf(lag));
    Map<String,List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    postResource(reqlog, "temporal/collections/" + temporalCollection,
      null, params, null, null, "advanceLsqt", headers);
    List<String> values = headers.get(HEADER_ML_LSQT);
    if ( values != null && values.size() > 0 ) {
      return values.get(0);
    } else {
      throw new FailedRequestException("Response missing header \"" + HEADER_ML_LSQT + "\"");
    }
  }

  @Override
  public void protectDocument(RequestLogger requestLogger, String temporalDocumentURI, Transaction transaction,
                              RequestParameters extraParams, ProtectionLevel level, String duration,
                              Calendar expiryTime, String archivePath) {
    if (temporalDocumentURI == null)
      throw new IllegalArgumentException(
        "Document protection for document identifier without uri");
    extraParams = addTemporalProtectionParams(extraParams, temporalDocumentURI, level, duration, expiryTime, archivePath);
    if (logger.isDebugEnabled())
      logger.debug("Protecting {} in transaction {}", temporalDocumentURI, getTransactionId(transaction));

    postResource(requestLogger, "documents/protection", transaction, extraParams, null, null, "protect");
  }
  @Override
  public void wipeDocument(RequestLogger reqlog, String temporalDocumentURI, Transaction transaction,
                           RequestParameters extraParams) {
    if (logger.isDebugEnabled())
      logger.debug("Wiping {} in transaction {}", temporalDocumentURI, getTransactionId(transaction));
    extraParams.add("result", "wiped");
    extraParams.add("uri", temporalDocumentURI);
    deleteResource(reqlog, "documents", transaction, extraParams, null);
    logRequest(reqlog, "wiped %s document", temporalDocumentURI);
  }

  @Override
  public TemporalDescriptor deleteDocument(RequestLogger reqlog, DocumentDescriptor desc,
                                           Transaction transaction, Set<Metadata> categories, RequestParameters extraParams)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    String uri = desc.getUri();
    if (uri == null) {
      throw new IllegalArgumentException(
        "Document delete for document identifier without uri");
    }

    logger.debug("Deleting {} in transaction {}", uri, getTransactionId(transaction));

    Request.Builder requestBldr = makeDocumentResource(makeDocumentParams(uri,
      categories, transaction, extraParams));

    requestBldr = addVersionHeader(desc, requestBldr, "If-Match");
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doDeleteFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.delete().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doDeleteFunction, null);
    int status = response.code();

    if (status == STATUS_NOT_FOUND) {
      closeResponse(response);
      throw new ResourceNotFoundException(
        "Could not delete non-existent document");
    }
    if (status == STATUS_PRECONDITION_REQUIRED) {
      FailedRequest failure = extractErrorFields(response);
      if (failure.getMessageCode().equals("RESTAPI-CONTENTNOVERSION")) {
        throw new FailedRequestException(
          "Content version required to delete document", failure);
      }
      throw new FailedRequestException(
        "Precondition required to delete document", failure);
    } else if (status == STATUS_FORBIDDEN) {
      FailedRequest failure = extractErrorFields(response);
      throw new ForbiddenUserException(
        "User is not allowed to delete documents", failure);
    }
    if (status == STATUS_PRECONDITION_FAILED) {
      FailedRequest failure = extractErrorFields(response);
      if (failure.getMessageCode().equals("RESTAPI-CONTENTWRONGVERSION")) {
        throw new FailedRequestException(
          "Content version must match to delete document",
          failure);
      } else if (failure.getMessageCode().equals("RESTAPI-EMPTYBODY")) {
        throw new FailedRequestException(
          "Empty request body sent to server", failure);
      }
      throw new FailedRequestException("Precondition Failed", failure);
    }
    if (status != STATUS_NO_CONTENT) {
      throw new FailedRequestException("delete failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }
    Headers responseHeaders = response.headers();
    TemporalDescriptor temporalDesc = updateTemporalSystemTime(desc, responseHeaders);

    closeResponse(response);
    logRequest(reqlog, "deleted %s document", uri);
    return temporalDesc;
  }

  @Override
  public boolean getDocument(RequestLogger reqlog, DocumentDescriptor desc,
                             Transaction transaction, Set<Metadata> categories,
                             RequestParameters extraParams,
                             DocumentMetadataReadHandle metadataHandle,
                             AbstractReadHandle contentHandle)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {

    HandleImplementation metadataBase = HandleAccessor.checkHandle(
      metadataHandle, "metadata");
    HandleImplementation contentBase = HandleAccessor.checkHandle(
      contentHandle, "content");

    String metadataFormat = null;
    String metadataMimetype = null;
    if (metadataBase != null) {
      metadataFormat = metadataBase.getFormat().toString().toLowerCase();
      metadataMimetype = metadataBase.getMimetype();
    }

    String contentMimetype = null;
    if (contentBase != null) {
      contentMimetype = contentBase.getMimetype();
    }

    if (metadataBase != null && contentBase != null) {
      return getDocumentImpl(reqlog, desc, transaction, categories,
        extraParams, metadataFormat, metadataHandle, contentHandle);
    } else if (metadataBase != null) {
      return getDocumentImpl(reqlog, desc, transaction, categories,
        extraParams, metadataMimetype, metadataHandle);
    } else if (contentBase != null) {
      return getDocumentImpl(reqlog, desc, transaction, null,
        extraParams, contentMimetype, contentHandle);
    }

    return false;
  }

  private int getRetryAfterTime(Response response) {
    String retryAfterRaw = response.header("Retry-After");
    return (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
  }

  private Response sendRequestOnce(Request.Builder requestBldr) {
    return sendRequestOnce(requestBldr.build());
  }

  private Response sendRequestOnce(Request request) {
    try {
      return getConnection().newCall(request).execute();
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }

  private Response sendRequestWithRetry(Request.Builder requestBldr, Function<Request.Builder, Response> doFunction, Consumer<Boolean> resendableConsumer) {
    return sendRequestWithRetry(requestBldr, true, doFunction, resendableConsumer);
  }

  private Response sendRequestWithRetry(
        Request.Builder requestBldr, boolean isRetryable, Function<Request.Builder, Response> doFunction, Consumer<Boolean> resendableConsumer
  ) {
    Response response = null;
    int status = -1;
    long startTime = System.currentTimeMillis();
    int nextDelay = 0;
    int retry = 0;
    /*
     * This loop is for retrying the request if the service is unavailable
     */
    for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
      if (nextDelay > 0) {
        try { Thread.sleep(nextDelay);} catch (InterruptedException e) {}
      }

      /*
       * Execute the function which is passed as an argument
       * in order to get the Response
       */
      response = doFunction.apply(requestBldr);
      if (response == null) {
          throw new MarkLogicInternalException(
                  "null response for: "+requestBldr.build().url().toString()
          );
      }
      status = response.code();
      if (!isRetryable || !retryStatus.contains(status)) {
        if (isFirstRequest()) setFirstRequest(false);
        /*
         * If we don't get a service unavailable status or if the request
         * is not retryable, we break from the retrying loop and return
         * the response
         */
        break;
      }
      /*
       * This code will be executed whenever the service is unavailable.
       * When the service becomes unavailable, we close the Response
       * we got and retry it to try and get a new Response
       */
      closeResponse(response);
      /*
       * There are scenarios where we don't want to retry and we just want to
       * throw ResourceNotResendableException. In that case, we pass that code from
       * the caller through the Consumer and execute it here. In the rest of the
       * scenarios, we pass it as null and it is just a no-operation.
       */
      if(resendableConsumer != null) resendableConsumer.accept(null);
      /*
       * Calculate the delay before which we shouldn't retry
       */
      nextDelay = Math.max(getRetryAfterTime(response), calculateDelay(randRetry, retry));
    }
    /*
     * If the service is still unavailable after all the retries, we throw a
     * FailedRetryException indicating that the service is unavailable.
     */
    if (retryStatus.contains(status)) {
      checkFirstRequest();
      closeResponse(response);
      throw new FailedRetryException(
        "Service unavailable and maximum retry period elapsed: "+
          ((System.currentTimeMillis() - startTime) / 1000)+
          " seconds after "+retry+" retries");
    }
    /*
     * Once we break from the retry loop, we just return the Response
     * back to the caller in order to proceed with the flow
     */
    return response;
  }

  private boolean getDocumentImpl(RequestLogger reqlog,
                                  DocumentDescriptor desc, Transaction transaction,
                                  Set<Metadata> categories, RequestParameters extraParams,
                                  String mimetype, AbstractReadHandle handle)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    String uri = desc.getUri();
    if (uri == null) {
      throw new IllegalArgumentException(
        "Document read for document identifier without uri");
    }

    logger.debug("Getting {} in transaction {}", uri, getTransactionId(transaction));

    addPointInTimeQueryParam(extraParams, handle);

    Request.Builder requestBldr = makeDocumentResource(
      makeDocumentParams(uri, categories, transaction, extraParams));
    if ( mimetype != null ) {
      requestBldr = requestBldr.header(HEADER_ACCEPT, mimetype);
    }
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    if (extraParams != null && extraParams.containsKey("range")) {
      requestBldr = requestBldr.header("range", extraParams.get("range").get(0));
    }

    requestBldr = addVersionHeader(desc, requestBldr, "If-None-Match");

    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.get().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doGetFunction, null);

    int status = response.code();
    if (status == STATUS_NOT_FOUND) {
      throw new ResourceNotFoundException(
        "Could not read non-existent document",
        extractErrorFields(response));
    }
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException(
        "User is not allowed to read documents",
        extractErrorFields(response));
    }
    if (status == STATUS_NOT_MODIFIED) {
      closeResponse(response);
      return false;
    }
    if (status != STATUS_OK && status != STATUS_PARTIAL_CONTENT) {
      throw new FailedRequestException("read failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    logRequest(
      reqlog,
      "read %s document from %s transaction with %s mime type and %s metadata categories",
      uri, (transaction != null) ? transaction.getTransactionId() : "no",
      (mimetype != null) ? mimetype : "no",
      stringJoin(categories, ", ", "no"));

    HandleImplementation handleBase = HandleAccessor.as(handle);

    Headers responseHeaders = response.headers();
    if (isExternalDescriptor(desc)) {
      updateVersion(desc, responseHeaders);
      updateDescriptor(desc, responseHeaders);
      copyDescriptor(desc, handleBase);
    } else {
      updateDescriptor(handleBase, responseHeaders);
    }

    Class as = handleBase.receiveAs();
    ResponseBody body = response.body();
    Object entity = body.contentLength() != 0 ? getEntity(body, as) : null;

    if (entity == null || (!InputStream.class.isAssignableFrom(as) && !Reader.class.isAssignableFrom(as))) {
      closeResponse(response);
    }

    handleBase.receiveContent((reqlog != null) ? reqlog.copyContent(entity) : entity);

    return true;
  }

  @Override
  public DocumentPage getBulkDocuments(RequestLogger reqlog, long serverTimestamp,
                                       Transaction transaction, Set<Metadata> categories,
                                       Format format, RequestParameters extraParams, boolean withContent, String... uris)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
    {
    boolean hasMetadata = categories != null && categories.size() > 0;
    OkHttpResultIterator iterator =
      getBulkDocumentsImpl(reqlog, serverTimestamp, transaction, categories, format, extraParams,
        withContent, uris);
    return new OkHttpDocumentPage(iterator, withContent, hasMetadata);
  }

  @Override
  public DocumentPage getBulkDocuments(RequestLogger reqlog, long serverTimestamp,
                                       QueryDefinition querydef,
                                       long start, long pageLength,
                                       Transaction transaction,
                                       SearchReadHandle searchHandle, QueryView view,
                                       Set<Metadata> categories, Format format, ServerTransform responseTransform,
                                       RequestParameters extraParams)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    boolean hasMetadata = categories != null && categories.size() > 0;
    boolean hasContent = true;
    OkHttpResultIterator iterator =
      getBulkDocumentsImpl(reqlog, serverTimestamp, querydef, start, pageLength, transaction,
        searchHandle, view, categories, format, responseTransform, extraParams);
    return new OkHttpDocumentPage(iterator, hasContent, hasMetadata);
  }

  private class OkHttpDocumentPage extends BasicPage<DocumentRecord> implements DocumentPage, Iterator<DocumentRecord> {
    private OkHttpResultIterator iterator;
    private boolean hasMetadata;
    private boolean hasContent;

    OkHttpDocumentPage(OkHttpResultIterator iterator, boolean hasContent, boolean hasMetadata) {
      super(
        new ArrayList<DocumentRecord>().iterator(),
        iterator != null ? iterator.getStart() : 1,
        iterator != null ? iterator.getPageSize() : 0,
        iterator != null ? iterator.getTotalSize() : 0
      );
      this.iterator = iterator;
      this.hasContent = hasContent;
      this.hasMetadata = hasMetadata;
      if ( iterator == null ) {
        setSize(0);
      } else if ( hasContent && hasMetadata ) {
        setSize(iterator.getSize() / 2);
      } else {
        setSize(iterator.getSize());
      }
    }

    @Override
    public Iterator<DocumentRecord> iterator() {
      return this;
    }

    @Override
    public boolean hasNext() {
      if ( iterator == null ) return false;
      return iterator.hasNext();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public DocumentRecord next() {
      if ( iterator == null ) throw new NoSuchElementException("No documents available");
      OkHttpResult result = iterator.next();
      DocumentRecord record;
      if ( hasContent && hasMetadata ) {
        OkHttpResult metadata = result;
        OkHttpResult content = iterator.next();
        record = new OkHttpDocumentRecord(content, metadata);
      } else if ( hasContent ) {
        OkHttpResult content = result;
        record = new OkHttpDocumentRecord(content);
      } else if ( hasMetadata ) {
        OkHttpResult metadata = result;
        record = new OkHttpDocumentRecord(null, metadata);
      } else {
        throw new IllegalStateException("Should never have neither content nor metadata");
      }
      return record;
    }

    @Override
    public <T extends AbstractReadHandle> T nextContent(T contentHandle) {
      return next().getContent(contentHandle);
    }

    @Override
    public void close() {
      if ( iterator != null ) iterator.close();
    }
  }

  private OkHttpResultIterator getBulkDocumentsImpl(RequestLogger reqlog, long serverTimestamp,
                                                    Transaction transaction, Set<Metadata> categories,
                                                    Format format, RequestParameters extraParams, boolean withContent,
                                                    String... uris)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {

    String path = "documents";
    RequestParameters params = new RequestParameters();
    if ( extraParams != null ) params.putAll(extraParams);
    if (serverTimestamp != -1) params.add("timestamp", Long.toString(serverTimestamp));
    addCategoryParams(categories, params, withContent);
    if (format != null)        params.add("format",     format.toString().toLowerCase());
    for (String uri: uris) {
      if ( uri != null && uri.length() > 0 ) {
        params.add("uri", uri);
      }
    }

    OkHttpResultIterator iterator = getIteratedResourceImpl(DefaultOkHttpResultIterator::new,
      reqlog, path, transaction, params, MIMETYPE_MULTIPART_MIXED);
    if ( iterator != null ) {
      if ( iterator.getStart() == -1 ) iterator.setStart(1);
      if ( iterator.getSize() != -1 ) {
        if ( iterator.getPageSize() == -1 ) iterator.setPageSize(iterator.getSize());
        if ( iterator.getTotalSize() == -1 )  iterator.setTotalSize(iterator.getSize());
      }
    }
    return iterator;
  }

  private OkHttpResultIterator getBulkDocumentsImpl(RequestLogger reqlog, long serverTimestamp,
                                                    QueryDefinition querydef, long start, long pageLength,
                                                    Transaction transaction, SearchReadHandle searchHandle, QueryView view,
                                                    Set<Metadata> categories, Format format, ServerTransform responseTransform,
                                                    RequestParameters extraParams)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    try {
      RequestParameters params = new RequestParameters();
      if ( extraParams != null ) params.putAll(extraParams);
      boolean withContent = true;
      addCategoryParams(categories, params, withContent);
      if ( searchHandle != null && view != null ) params.add("view", view.toString().toLowerCase());
      if ( start > 1 ) params.add("start", Long.toString(start));
      if ( pageLength >= 0 ) params.add("pageLength", Long.toString(pageLength));
      if (serverTimestamp != -1) params.add("timestamp",  Long.toString(serverTimestamp));
      addPointInTimeQueryParam(params, searchHandle);
      if ( format != null ) params.add("format", format.toString().toLowerCase());
      HandleImplementation handleBase = HandleAccessor.as(searchHandle);
      if ( format == null && searchHandle != null ) {
        if ( Format.XML == handleBase.getFormat() ) {
          params.add("format", "xml");
        } else if ( Format.JSON == handleBase.getFormat() ) {
          params.add("format", "json");
        }
      }

      OkHttpSearchRequest request =
        generateSearchRequest(reqlog, querydef, MIMETYPE_MULTIPART_MIXED, transaction, responseTransform, params, null);
      Response response = request.getResponse();
      if ( response == null ) return null;
      MimeMultipart entity = null;
      if ( searchHandle != null ) {
        updateServerTimestamp(handleBase, response.headers());
        ResponseBody body = response.body();
        if ( body.contentLength() != 0 ) {
          entity = getEntity(body, MimeMultipart.class);
          if ( entity != null ) {
            List<BodyPart> partList = getPartList(entity);
            if ( entity.getCount() > 0 ) {
              BodyPart searchResponsePart = entity.getBodyPart(0);
              handleBase.receiveContent(getEntity(searchResponsePart, handleBase.receiveAs()));
              partList = partList.subList(1, partList.size());
            }
            Closeable closeable = response;
            return makeResults(OkHttpServiceResultIterator::new, reqlog, "read", "resource", partList, response,
              closeable);
          }
        }
      }
      return makeResults(OkHttpServiceResultIterator::new, reqlog, "read", "resource", response);
    } catch (MessagingException e) {
      throw new MarkLogicIOException(e);
    }
  }

  private boolean getDocumentImpl(RequestLogger reqlog,
                                  DocumentDescriptor desc, Transaction transaction,
                                  Set<Metadata> categories, RequestParameters extraParams,
                                  String metadataFormat, DocumentMetadataReadHandle metadataHandle,
                                  AbstractReadHandle contentHandle)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    String uri = desc.getUri();
    if (uri == null) {
      throw new IllegalArgumentException(
        "Document read for document identifier without uri");
    }

    assert metadataHandle != null : "metadataHandle is null";
    assert contentHandle != null : "contentHandle is null";

    logger.debug("Getting multipart for {} in transaction {}", uri, getTransactionId(transaction));

    addPointInTimeQueryParam(extraParams, contentHandle);

    RequestParameters docParams = makeDocumentParams(uri, categories, transaction, extraParams, true);
    docParams.add("format", metadataFormat);

    Request.Builder requestBldr = makeDocumentResource(docParams);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);
    requestBldr = addVersionHeader(desc, requestBldr, "If-None-Match");

    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.addHeader(HEADER_ACCEPT, multipartMixedWithBoundary()).get());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doGetFunction, null);
    int status = response.code();
    if (status == STATUS_NOT_FOUND) {
      throw new ResourceNotFoundException(
        "Could not read non-existent document",
        extractErrorFields(response));
    }
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException(
        "User is not allowed to read documents",
        extractErrorFields(response));
    }
    if (status == STATUS_NOT_MODIFIED) {
      closeResponse(response);
      return false;
    }
    if (status != STATUS_OK) {
      throw new FailedRequestException("read failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    logRequest(
      reqlog,
      "read %s document from %s transaction with %s metadata categories and content",
      uri, (transaction != null) ? transaction.getTransactionId() : "no", stringJoin(categories, ", ", "no"));

    try {
      ResponseBody body = response.body();
      MimeMultipart entity = body.contentLength() != 0 ?
        getEntity(body, MimeMultipart.class) : null;
      if (entity == null) return false;

      int partCount = entity.getCount();
      if (partCount == 0) return false;
      List<BodyPart> partList = getPartList(entity);

      if (partCount != 2) {
        throw new FailedRequestException("read expected 2 parts but got " + partCount + " parts",
          extractErrorFields(response));
      }

      HandleImplementation metadataBase = HandleAccessor.as(metadataHandle);
      HandleImplementation contentBase = HandleAccessor.as(contentHandle);

      BodyPart contentPart = partList.get(1);

      Headers responseHeaders = response.headers();
      if (isExternalDescriptor(desc)) {
        updateVersion(desc, responseHeaders);
        updateFormat(desc, responseHeaders);
        updateMimetype(desc, getHeaderMimetype(getHeader(contentPart, HEADER_CONTENT_TYPE)));
        updateLength(desc, getHeaderLength(getHeader(contentPart, HEADER_CONTENT_LENGTH)));
        copyDescriptor(desc, contentBase);
      } else {
        updateDescriptor(contentBase, responseHeaders);
      }

      metadataBase.receiveContent(getEntity(partList.get(0),
        metadataBase.receiveAs()));

      Object contentEntity = getEntity(contentPart, contentBase.receiveAs());
      contentBase.receiveContent((reqlog != null) ? reqlog.copyContent(contentEntity) : contentEntity);

      closeResponse(response);

      return true;
    } catch (MessagingException e) {
      throw new MarkLogicIOException(e);
    }
  }

  @Override
  public DocumentDescriptor head(RequestLogger reqlog, String uri,
                                 Transaction transaction)
    throws ForbiddenUserException, FailedRequestException
  {
    Response response = headImpl(reqlog, uri, transaction, makeDocumentResource(makeDocumentParams(uri,
      null, transaction, null)));

    // 404
    if (response == null) return null;

    Headers responseHeaders = response.headers();

    closeResponse(response);
    logRequest(reqlog, "checked %s document from %s transaction", uri,
      (transaction != null) ? transaction.getTransactionId() : "no");

    DocumentDescriptorImpl desc = new DocumentDescriptorImpl(uri, false);

    updateVersion(desc, responseHeaders);
    updateDescriptor(desc, responseHeaders);

    return desc;
  }

  @Override
  public boolean exists(String uri) throws ForbiddenUserException, FailedRequestException {
    return headImpl(null, uri, null, setupRequest(uri, null)) == null ? false : true;
  }
  
  @Override
  public ConnectionResult checkConnection() {
	  Request.Builder request = new Request.Builder()
		        .url(this.baseUri);
	  Response response = headImplExec(null, this.baseUri.uri().toString(), null, request);
	  ConnectionResultImpl connectionResultImpl = new ConnectionResultImpl();
	  int statusCode = response.code();
	  if(statusCode < 300) {
		  connectionResultImpl.setConnected(true);
	  }
	  else {
		  connectionResultImpl.setConnected(false);
		  connectionResultImpl.setStatusCode(statusCode);
		  connectionResultImpl.setErrorMessage(getReasonPhrase(response));
	  }
	  return connectionResultImpl;
  }
  
  private Response headImpl(RequestLogger reqlog, String uri,
                            Transaction transaction, Request.Builder requestBldr) {
    Response response = headImplExec(reqlog, uri, transaction, requestBldr);
    int status = response.code();
    if (status != STATUS_OK) {
      if (status == STATUS_NOT_FOUND) {
        closeResponse(response);
        return null;
      } else if (status == STATUS_FORBIDDEN) {
        throw new ForbiddenUserException(
          "User is not allowed to check the existence of documents",
          extractErrorFields(response));
      } else {
        throw new FailedRequestException(
          "Document existence check failed: "
            + getReasonPhrase(response),
          extractErrorFields(response));
      }
    }
    return response;
  }

  private Response headImplExec(RequestLogger reqlog, String uri,
          Transaction transaction, Request.Builder requestBldr) {
	  if (uri == null) {
	      throw new IllegalArgumentException(
	        "Existence check for document identifier without uri");
	    }

	    logger.debug("Requesting head for {} in transaction {}", uri, getTransactionId(transaction));

	    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
	    requestBldr = addTelemetryAgentId(requestBldr);

	    Function<Request.Builder, Response> doHeadFunction = new Function<Request.Builder, Response>() {
	      public Response apply(Request.Builder funcBuilder) {
	        return sendRequestOnce(funcBuilder.head().build());
	      }
	    };
	    return sendRequestWithRetry(requestBldr, (transaction == null), doHeadFunction, null);
  }
  
  @Override
  public TemporalDescriptor putDocument(RequestLogger reqlog, DocumentDescriptor desc,
                                        Transaction transaction, Set<Metadata> categories,
                                        RequestParameters extraParams,
                                        DocumentMetadataWriteHandle metadataHandle,
                                        AbstractWriteHandle contentHandle)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    if (desc.getUri() == null) {
      throw new IllegalArgumentException(
        "Document write for document identifier without uri");
    }

    HandleImplementation metadataBase = HandleAccessor.checkHandle(
      metadataHandle, "metadata");
    HandleImplementation contentBase = HandleAccessor.checkHandle(
      contentHandle, "content");

    String metadataMimetype = null;
    if (metadataBase != null) {
      metadataMimetype = metadataBase.getMimetype();
    }

    Format descFormat = desc.getFormat();
    String contentMimetype = (descFormat != null && descFormat != Format.UNKNOWN) ? desc.getMimetype() : null;
    if (contentMimetype == null && contentBase != null) {
      Format contentFormat = contentBase.getFormat();
      if (descFormat != null && descFormat != contentFormat) {
        contentMimetype = descFormat.getDefaultMimetype();
      } else if (contentFormat != null && contentFormat != Format.UNKNOWN) {
        contentMimetype = contentBase.getMimetype();
      }
    }

    if (metadataBase != null && contentBase != null) {
      return putPostDocumentImpl(reqlog, "put", desc, transaction, categories,
        extraParams, metadataMimetype, metadataHandle,
        contentMimetype, contentHandle);
    } else if (metadataBase != null) {
      return putPostDocumentImpl(reqlog, "put", desc, transaction, categories, false,
        extraParams, metadataMimetype, metadataHandle);
    } else if (contentBase != null) {
      return putPostDocumentImpl(reqlog, "put", desc, transaction, null, true,
        extraParams, contentMimetype, contentHandle);
    }
    throw new IllegalArgumentException("Either metadataHandle or contentHandle must not be null");
  }

  @Override
  public DocumentDescriptorImpl postDocument(RequestLogger reqlog, DocumentUriTemplate template,
                                             Transaction transaction, Set<Metadata> categories, RequestParameters extraParams,
                                             DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    DocumentDescriptorImpl desc = new DocumentDescriptorImpl(false);

    HandleImplementation metadataBase = HandleAccessor.checkHandle(
      metadataHandle, "metadata");
    HandleImplementation contentBase = HandleAccessor.checkHandle(
      contentHandle, "content");

    String metadataMimetype = null;
    if (metadataBase != null) {
      metadataMimetype = metadataBase.getMimetype();
    }

    Format templateFormat = template.getFormat();
    String contentMimetype = (templateFormat != null && templateFormat != Format.UNKNOWN) ?
      template.getMimetype() : null;
    if (contentMimetype == null && contentBase != null) {
      Format contentFormat = contentBase.getFormat();
      if (templateFormat != null && templateFormat != contentFormat) {
        contentMimetype = templateFormat.getDefaultMimetype();
        desc.setFormat(templateFormat);
      } else if (contentFormat != null && contentFormat != Format.UNKNOWN) {
        contentMimetype = contentBase.getMimetype();
        desc.setFormat(contentFormat);
      }
    }
    desc.setMimetype(contentMimetype);

    if (extraParams == null) extraParams = new RequestParameters();

    String extension = template.getExtension();
    if (extension != null) extraParams.add("extension", extension);

    String directory = template.getDirectory();
    if (directory != null) extraParams.add("directory", directory);

    if (metadataBase != null && contentBase != null) {
      putPostDocumentImpl(reqlog, "post", desc, transaction, categories, extraParams,
        metadataMimetype, metadataHandle, contentMimetype, contentHandle);
    } else if (contentBase != null) {
      putPostDocumentImpl(reqlog, "post", desc, transaction, null, true, extraParams,
        contentMimetype, contentHandle);
    }

    return desc;
  }

  private TemporalDescriptor putPostDocumentImpl(RequestLogger reqlog, String method, DocumentDescriptor desc,
                                                 Transaction transaction, Set<Metadata> categories, boolean isOnContent, RequestParameters extraParams,
                                                 String mimetype, AbstractWriteHandle handle)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    String uri = desc.getUri();

    HandleImplementation handleBase = HandleAccessor.as(handle);

    logger.debug("Sending {} document in transaction {}",
        (uri != null) ? uri : "new", getTransactionId(transaction));

    logRequest(
      reqlog,
      "writing %s document from %s transaction with %s mime type and %s metadata categories",
      (uri != null) ? uri : "new",
      (transaction != null) ? transaction.getTransactionId() : "no",
      (mimetype != null) ? mimetype : "no",
      stringJoin(categories, ", ", "no"));

    Request.Builder requestBldr = makeDocumentResource(
      makeDocumentParams(
        uri, categories, transaction, extraParams, isOnContent
      ));

    requestBldr = requestBldr.header(HEADER_CONTENT_TYPE,
      (mimetype != null) ? mimetype : MIMETYPE_WILDCARD);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);
    if (uri != null) {
      requestBldr = addVersionHeader(desc, requestBldr, "If-Match");
    }

    if ("patch".equals(method)) {
      requestBldr = requestBldr.header("X-HTTP-Method-Override", "PATCH");
      method  = "post";
    }
    boolean isResendable = handleBase.isResendable();

    Response response = null;
    int status = -1;
    Headers responseHeaders = null;
    long startTime = System.currentTimeMillis();
    int nextDelay = 0;
    int retry = 0;
    for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
      if (nextDelay > 0) {
        try {
          Thread.sleep(nextDelay);
        } catch (InterruptedException e) {
        }
      }

      Object value = handleBase.sendContent();
      if (value == null) {
        throw new IllegalArgumentException(
          "Document write with null value for " + ((uri != null) ? uri : "new document"));
      }

      if (isFirstRequest() && !isResendable && isStreaming(value)) {
        nextDelay = makeFirstRequest(retry);
        if (nextDelay != 0) continue;
      }

      MediaType mediaType = makeType(requestBldr.build().header(HEADER_CONTENT_TYPE));
      if (value instanceof OutputStreamSender) {
        StreamingOutputImpl sentStream =
          new StreamingOutputImpl((OutputStreamSender) value, reqlog, mediaType);
        requestBldr =
          ("put".equals(method)) ?
            requestBldr.put(sentStream) :
            requestBldr.post(sentStream);
      } else {
        Object sentObj = (reqlog != null) ?
          reqlog.copyContent(value) : value;
        requestBldr =
          ("put".equals(method)) ?
            requestBldr.put(new ObjectRequestBody(sentObj, mediaType)) :
            requestBldr.post(new ObjectRequestBody(sentObj, mediaType));
      }
      response = sendRequestOnce(requestBldr);

      status = response.code();

      responseHeaders = response.headers();
      if (transaction != null || !retryStatus.contains(status)) {
        if (isFirstRequest()) setFirstRequest(false);

        break;
      }

      String retryAfterRaw = response.header("Retry-After");
      closeResponse(response);

      if (!isResendable) {
        checkFirstRequest();
        throw new ResourceNotResendableException(
          "Cannot retry request for " +
            ((uri != null) ? uri : "new document"));
      }

      int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
      nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
    }
    if (retryStatus.contains(status)) {
      checkFirstRequest();
      closeResponse(response);
      throw new FailedRetryException(
        "Service unavailable and maximum retry period elapsed: "+
          ((System.currentTimeMillis() - startTime) / 1000)+
          " seconds after "+retry+" retries");
    }
    if (status == STATUS_NOT_FOUND) {
      throw new ResourceNotFoundException(
        "Could not write non-existent document",
        extractErrorFields(response));
    }
    if (status == STATUS_PRECONDITION_REQUIRED) {
      FailedRequest failure = extractErrorFields(response);
      if (failure.getMessageCode().equals("RESTAPI-CONTENTNOVERSION")) {
        throw new FailedRequestException(
          "Content version required to write document", failure);
      }
      throw new FailedRequestException(
        "Precondition required to write document", failure);
    } else if (status == STATUS_FORBIDDEN) {
      FailedRequest failure = extractErrorFields(response);
      throw new ForbiddenUserException(
        "User is not allowed to write documents", failure);
    }
    if (status == STATUS_PRECONDITION_FAILED) {
      FailedRequest failure = extractErrorFields(response);
      if (failure.getMessageCode().equals("RESTAPI-CONTENTWRONGVERSION")) {
        throw new FailedRequestException(
          "Content version must match to write document", failure);
      } else if (failure.getMessageCode().equals("RESTAPI-EMPTYBODY")) {
        throw new FailedRequestException(
          "Empty request body sent to server", failure);
      }
      throw new FailedRequestException("Precondition Failed", failure);
    }
    if (status == -1) {
      throw new FailedRequestException("write failed: Unknown Reason", extractErrorFields(response));
    }
    if (status != STATUS_CREATED && status != STATUS_NO_CONTENT) {
      throw new FailedRequestException("write failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    if (uri == null) {
      String location = response.header("Location");
      if (location != null) {
        int offset = location.indexOf(DOCUMENT_URI_PREFIX);
        if (offset == -1) {
          closeResponse(response);
          throw new MarkLogicInternalException(
            "document create produced invalid location: " + location);
        }
        uri = location.substring(offset + DOCUMENT_URI_PREFIX.length());
        if (uri == null) {
          closeResponse(response);
          throw new MarkLogicInternalException(
            "document create produced location without uri: " + location);
        }
        desc.setUri(uri);
        updateVersion(desc, responseHeaders);
        updateDescriptor(desc, responseHeaders);
      }
    }
    TemporalDescriptor temporalDesc = updateTemporalSystemTime(desc, responseHeaders);
    closeResponse(response);
    return temporalDesc;
  }

  private TemporalDescriptor putPostDocumentImpl(RequestLogger reqlog, String method, DocumentDescriptor desc,
                                                 Transaction transaction, Set<Metadata> categories, RequestParameters extraParams,
                                                 String metadataMimetype, DocumentMetadataWriteHandle metadataHandle, String contentMimetype,
                                                 AbstractWriteHandle contentHandle)
    throws ResourceNotFoundException, ResourceNotResendableException,ForbiddenUserException, FailedRequestException
  {
    String uri = desc.getUri();

    logger.debug("Sending {} multipart document in transaction {}",
      (uri != null) ? uri : "new", getTransactionId(transaction));

    logRequest(
      reqlog,
      "writing %s document from %s transaction with %s metadata categories and content",
      (uri != null) ? uri : "new",
      (transaction != null) ? transaction.getTransactionId() : "no",
      stringJoin(categories, ", ", "no"));

    RequestParameters docParams =
      makeDocumentParams(uri, categories, transaction, extraParams, true);

    Request.Builder requestBldr = makeDocumentResource(docParams)
      .addHeader(HEADER_ACCEPT, MIMETYPE_MULTIPART_MIXED);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);
    if (uri != null) {
      requestBldr = addVersionHeader(desc, requestBldr, "If-Match");
    }

    Response response = null;
    int status = -1;
    Headers responseHeaders = null;
    long startTime = System.currentTimeMillis();
    int nextDelay = 0;
    int retry = 0;
    for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
      if (nextDelay > 0) {
        try {
          Thread.sleep(nextDelay);
        } catch (InterruptedException e) {
        }
      }

      MultipartBody.Builder multiPart = new MultipartBody.Builder();
      boolean hasStreamingPart = addParts(multiPart, reqlog,
        new String[] { metadataMimetype, contentMimetype },
        new AbstractWriteHandle[] { metadataHandle, contentHandle });

      if (isFirstRequest() && hasStreamingPart) {
        nextDelay = makeFirstRequest(retry);
        if (nextDelay != 0) continue;
      }

      requestBldr = ("put".equals(method)) ?  requestBldr.put(multiPart.build()) : requestBldr.post(multiPart.build());
      response = sendRequestOnce(requestBldr);
      status = response.code();

      responseHeaders = response.headers();
      if (transaction != null || !retryStatus.contains(status)) {
        if (isFirstRequest()) setFirstRequest(false);

        break;
      }
      String retryAfterRaw = response.header("Retry-After");
      closeResponse(response);

      if (hasStreamingPart) {
        throw new ResourceNotResendableException(
          "Cannot retry request for " +
            ((uri != null) ? uri : "new document"));
      }

      int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
      nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
    }
    if (retryStatus.contains(status)) {
      checkFirstRequest();
      closeResponse(response);
      throw new FailedRetryException(
        "Service unavailable and maximum retry period elapsed: "+
          ((System.currentTimeMillis() - startTime) / 1000)+
          " seconds after "+retry+" retries");
    }
    if (status == STATUS_NOT_FOUND) {
      closeResponse(response);
      throw new ResourceNotFoundException(
        "Could not write non-existent document");
    }
    if (status == STATUS_PRECONDITION_REQUIRED) {
      FailedRequest failure = extractErrorFields(response);
      if (failure.getMessageCode().equals("RESTAPI-CONTENTNOVERSION")) {
        throw new FailedRequestException(
          "Content version required to write document", failure);
      }
      throw new FailedRequestException(
        "Precondition required to write document", failure);
    } else if (status == STATUS_FORBIDDEN) {
      FailedRequest failure = extractErrorFields(response);
      throw new ForbiddenUserException(
        "User is not allowed to write documents", failure);
    }
    if (status == STATUS_PRECONDITION_FAILED) {
      FailedRequest failure = extractErrorFields(response);
      if (failure.getMessageCode().equals("RESTAPI-CONTENTWRONGVERSION")) {
        throw new FailedRequestException(
          "Content version must match to write document", failure);
      } else if (failure.getMessageCode().equals("RESTAPI-EMPTYBODY")) {
        throw new FailedRequestException(
          "Empty request body sent to server", failure);
      }
      throw new FailedRequestException("Precondition Failed", failure);
    }
    if (status != STATUS_CREATED && status != STATUS_NO_CONTENT) {
      throw new FailedRequestException("write failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    if (uri == null) {
      String location = response.header("Location");
      if (location != null) {
        int offset = location.indexOf(DOCUMENT_URI_PREFIX);
        if (offset == -1) {
          closeResponse(response);
          throw new MarkLogicInternalException(
            "document create produced invalid location: " + location);
        }
        uri = location.substring(offset + DOCUMENT_URI_PREFIX.length());
        if (uri == null) {
          closeResponse(response);
          throw new MarkLogicInternalException(
            "document create produced location without uri: " + location);
        }
        desc.setUri(uri);
        updateVersion(desc, responseHeaders);
        updateDescriptor(desc, responseHeaders);
      }
    }
    TemporalDescriptor temporalDesc = updateTemporalSystemTime(desc, responseHeaders);
    closeResponse(response);
    return temporalDesc;
  }

  @Override
  public void patchDocument(RequestLogger reqlog, DocumentDescriptor desc, Transaction transaction,
                            Set<Metadata> categories, boolean isOnContent, DocumentPatchHandle patchHandle)
    throws ResourceNotFoundException, ResourceNotResendableException,ForbiddenUserException, FailedRequestException
  {
    patchDocument(reqlog, desc, transaction, categories, isOnContent, null, null, patchHandle);
  }

  @Override
  public void patchDocument(RequestLogger reqlog, DocumentDescriptor desc, Transaction transaction,
                            Set<Metadata> categories, boolean isOnContent, RequestParameters extraParams, String sourceDocumentURI,
                            DocumentPatchHandle patchHandle)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    HandleImplementation patchBase = HandleAccessor.checkHandle(patchHandle, "patch");

    if(sourceDocumentURI != null)
      extraParams.add("source-document", sourceDocumentURI);
    putPostDocumentImpl(reqlog, "patch", desc, transaction, categories, isOnContent, extraParams, patchBase.getMimetype(),
      patchHandle);
  }

  @Override
  public Transaction openTransaction(String name, int timeLimit) throws ForbiddenUserException, FailedRequestException {
    logger.debug("Opening transaction");

    RequestParameters transParams = new RequestParameters();
    if ( name != null || timeLimit > 0 ) {
      if ( name != null ) transParams.add("name", name);
      if ( timeLimit > 0 ) transParams.add("timeLimit", String.valueOf(timeLimit));
    }

    Request.Builder requestBldr = setupRequest("transactions", transParams);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doPostFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.post(RequestBody.create(null, "")));
      }
    };
    Response response = sendRequestWithRetry(requestBldr, doPostFunction, null);
    int status = response.code();
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to open transactions", extractErrorFields(response));
    }
    if (status != STATUS_SEE_OTHER) {
      throw new FailedRequestException("transaction open failed: " +
        getReasonPhrase(response), extractErrorFields(response));
    }

    String location = response.headers().get("Location");
    List<ClientCookie> cookies = new ArrayList<>();
    for ( String setCookie : response.headers(HEADER_SET_COOKIE) ) {
      ClientCookie cookie = ClientCookie.parse(requestBldr.build().url(), setCookie);
      cookies.add(cookie);
    }
    closeResponse(response);
    if (location == null) throw new MarkLogicInternalException("transaction open failed to provide location");
    if (!location.contains("/")) {
      throw new MarkLogicInternalException("transaction open produced invalid location: " + location);
    }

    String transactionId = location.substring(location.lastIndexOf("/") + 1);
    return new TransactionImpl(this, transactionId, cookies);
  }

  @Override
  public void commitTransaction(Transaction transaction) throws ForbiddenUserException, FailedRequestException {
    completeTransaction(transaction, "commit");
  }

  @Override
  public void rollbackTransaction(Transaction transaction) throws ForbiddenUserException, FailedRequestException {
    completeTransaction(transaction, "rollback");
  }

  private void completeTransaction(Transaction transaction, String result)
    throws ForbiddenUserException, FailedRequestException
  {
    if (result == null) {
      throw new MarkLogicInternalException(
        "transaction completion without operation");
    }
    if (transaction == null) {
      throw new MarkLogicInternalException(
        "transaction completion without id: " + result);
    }

    logger.debug("Completing transaction {} with {}", transaction.getTransactionId(), result);

    RequestParameters transParams = new RequestParameters();
    transParams.add("result", result);

    Request.Builder requestBldr = setupRequest("transactions/" + transaction.getTransactionId(), transParams);

    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doPostFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.post(RequestBody.create(null, "")).build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, false, doPostFunction, null);
    int status = response.code();

    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException(
        "User is not allowed to complete transaction with "
          + result, extractErrorFields(response));
    }
    if (status != STATUS_NO_CONTENT) {
      throw new FailedRequestException("transaction " + result
        + " failed: " + getReasonPhrase(response),
        extractErrorFields(response));
    }

    closeResponse(response);
  }

  private void addCategoryParams(Set<Metadata> categories, RequestParameters params,
                                 boolean withContent)
  {
    if (withContent)
      params.add("category", "content");
    if (categories != null && categories.size() > 0) {
      if (categories.contains(Metadata.ALL)) {
        params.add("category", "metadata");
      } else {
        for (Metadata category : categories) {
          params.add("category", category.name().toLowerCase());
        }
      }
    }
  }

  private RequestParameters makeDocumentParams(String uri,
                                                            Set<Metadata> categories, Transaction transaction,
                                                            RequestParameters extraParams) {
    return makeDocumentParams(uri, categories, transaction, extraParams,
      false);
  }

  private RequestParameters makeDocumentParams(String uri, Set<Metadata> categories, Transaction transaction,
                                               RequestParameters extraParams, boolean withContent)
  {
    RequestParameters docParams = new RequestParameters();
    if (extraParams != null && extraParams.size() > 0) {
      for ( Map.Entry<String, List<String>> entry : extraParams.entrySet() ) {
        for ( String value : entry.getValue() ) {
          String extraKey = entry.getKey();
          if ( !"range".equalsIgnoreCase(extraKey) ) {
            docParams.add(extraKey, value);
          }
        }
      }
    }
    if ( uri != null ) docParams.add("uri", uri);
    if (categories == null || categories.size() == 0) {
      docParams.add("category", "content");
    } else {
      if (withContent) {
        docParams.add("category", "content");
      }
      if (categories.contains(Metadata.ALL)) {
        docParams.add("category", "metadata");
      } else {
        for (Metadata category : categories) {
          docParams.add("category", category.toString().toLowerCase());
        }
      }
    }
    if (transaction != null) {
      docParams.add("txid", transaction.getTransactionId());
    }
    return docParams;
  }

  private Request.Builder makeDocumentResource(RequestParameters queryParams) {
    return setupRequest("documents", queryParams);
  }

  static private boolean isExternalDescriptor(ContentDescriptor desc) {
    return desc != null && desc instanceof DocumentDescriptorImpl
      && !((DocumentDescriptorImpl) desc).isInternal();
  }

  static private void updateDescriptor(ContentDescriptor desc,
                                Headers headers) {
    if (desc == null || headers == null) return;

    updateFormat(desc, headers);
    updateMimetype(desc, headers);
    updateLength(desc, headers);
    updateServerTimestamp(desc, headers);
  }

  static private TemporalDescriptor updateTemporalSystemTime(DocumentDescriptor desc,
                                                      Headers headers)
  {
    if (headers == null) return null;

    DocumentDescriptorImpl temporalDescriptor;
    if ( desc instanceof DocumentDescriptorImpl ) {
      temporalDescriptor = (DocumentDescriptorImpl) desc;
    } else {
      temporalDescriptor = new DocumentDescriptorImpl(desc.getUri(), false);
    }
    temporalDescriptor.setTemporalSystemTime(headers.get(HEADER_X_MARKLOGIC_SYSTEM_TIME));
    return temporalDescriptor;
  }

  static private void copyDescriptor(DocumentDescriptor desc,
                              HandleImplementation handleBase) {
    if (handleBase == null) return;

    if (desc.getFormat() != null) handleBase.setFormat(desc.getFormat());
    if (desc.getMimetype() != null) handleBase.setMimetype(desc.getMimetype());
    handleBase.setByteLength(desc.getByteLength());
  }

  static private void updateFormat(ContentDescriptor descriptor,
                            Headers headers) {
    updateFormat(descriptor, getHeaderFormat(headers));
  }

  static private void updateFormat(ContentDescriptor descriptor, Format format) {
    if (format != null) {
      descriptor.setFormat(format);
    }
  }

  static private Format getHeaderFormat(Headers headers) {
    String format = headers.get(HEADER_VND_MARKLOGIC_DOCUMENT_FORMAT);
    if (format != null) {
      return Format.valueOf(format.toUpperCase());
    }
    return null;
  }

  static private Format getHeaderFormat(BodyPart part) {
    String contentDisposition = getHeader(part, HEADER_CONTENT_DISPOSITION);
    String formatRegex = ".* format=(text|binary|xml|json).*";
    String format = getHeader(part, HEADER_VND_MARKLOGIC_DOCUMENT_FORMAT);
    String contentType = getHeader(part, HEADER_CONTENT_TYPE);
    if ( format != null && format.length() > 0 ) {
      return Format.valueOf(format.toUpperCase());
    } else if ( contentDisposition != null && contentDisposition.matches(formatRegex) ) {
      format = contentDisposition.replaceFirst("^.*" + formatRegex + ".*$", "$1");
      return Format.valueOf(format.toUpperCase());
    } else if ( contentType != null && contentType.length() > 0 ) {
      return Format.getFromMimetype(contentType);
    }
    return null;
  }

  static private void updateMimetype(ContentDescriptor descriptor,
                              Headers headers) {
    updateMimetype(descriptor, getHeaderMimetype(headers.get(HEADER_CONTENT_TYPE)));
  }

  static private void updateMimetype(ContentDescriptor descriptor, String mimetype) {
    if (mimetype != null) {
      descriptor.setMimetype(mimetype);
    }
  }

  static private String getHeader(Map<String, List<String>> headers, String name) {
    List<String> values = headers.get(name);
    if ( values != null && values.size() > 0 ) {
      return values.get(0);
    }
    return null;
  }

  static private String getHeader(BodyPart part, String name) {
    if ( part == null ) throw new MarkLogicInternalException("part must not be null");
    try {
      String[] values = part.getHeader(name);
      if ( values != null && values.length > 0 ) {
        return values[0];
      }
      return null;
    } catch (MessagingException e) {
      throw new MarkLogicIOException(e);
    }
  }

  static private String getHeaderMimetype(String contentType) {
    if (contentType != null) {
      int offset = contentType.indexOf(";");
      String mimetype = (offset == -1) ? contentType : contentType.substring(0, offset);
      // TODO: if "; charset=foo" set character set
      if (mimetype != null && mimetype.length() > 0) {
        return mimetype;
      }
    }
    return null;
  }

  static private void updateLength(ContentDescriptor descriptor,
                            Headers headers) {
    updateLength(descriptor, getHeaderLength(headers.get(HEADER_CONTENT_LENGTH)));
  }

  static private void updateLength(ContentDescriptor descriptor, long length) {
    descriptor.setByteLength(length);
  }

  static private void updateServerTimestamp(ContentDescriptor descriptor,
                                     Headers headers) {
    updateServerTimestamp(descriptor, getHeaderServerTimestamp(headers));
  }

  static private long getHeaderServerTimestamp(Headers headers) {
    String timestamp = headers.get(HEADER_ML_EFFECTIVE_TIMESTAMP);
    if (timestamp != null && timestamp.length() > 0) {
      return Long.parseLong(timestamp);
    }
    return -1;
  }

  static private void updateServerTimestamp(ContentDescriptor descriptor, long timestamp) {
    if ( descriptor instanceof HandleImplementation ) {
      if ( descriptor != null && timestamp != -1 ) {
        ((HandleImplementation) descriptor).setResponseServerTimestamp(timestamp);
      }
    }
  }

  static private long getHeaderLength(String length) {
    if (length != null) {
      return Long.parseLong(length);
    }
    return ContentDescriptor.UNKNOWN_LENGTH;
  }

  static private String getHeaderUri(BodyPart part) {
    try {
      if ( part != null ) {
        return part.getFileName();
      }
      // if it's not found, just return null
      return null;
    } catch(MessagingException e) {
      throw new MarkLogicIOException(e);
    }
  }

  static private void updateVersion(DocumentDescriptor descriptor, Headers headers) {
      updateVersion(descriptor, extractVersion(headers.get(HEADER_ETAG)));
  }
  static private void updateVersion(DocumentDescriptor descriptor, String header) {
      updateVersion(descriptor, extractVersion(header));
  }
  static private void updateVersion(DocumentDescriptor descriptor, long version) {
    descriptor.setVersion(version);
  }
  static private long extractVersion(String header) {
    if (header != null && header.length() > 0) {
      // trim the double quotes
      return Long.parseLong(header.substring(1, header.length() - 1));
    }
    return DocumentDescriptor.UNKNOWN_VERSION;
  }

  static private Request.Builder addVersionHeader(DocumentDescriptor desc, Request.Builder requestBldr, String name) {
    if ( desc != null &&
         desc instanceof DocumentDescriptorImpl &&
         !((DocumentDescriptorImpl) desc).isInternal())
    {
      long version = desc.getVersion();
      if (version != DocumentDescriptor.UNKNOWN_VERSION) {
        return requestBldr.header(name, "\"" + String.valueOf(version) + "\"");
      }
    }
    return requestBldr;
  }

  @Override
  public <T extends SearchReadHandle> T search(RequestLogger reqlog, T searchHandle,
                                               QueryDefinition queryDef, long start, long len, QueryView view,
                                               Transaction transaction, String forestName)
    throws ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();

    if (start > 1) {
      params.add("start", Long.toString(start));
    }

    if (len > 0) {
      params.add("pageLength", Long.toString(len));
    }

    if (view != null && view != QueryView.DEFAULT) {
      if (view == QueryView.ALL) {
        params.add("view", "all");
      } else if (view == QueryView.RESULTS) {
        params.add("view", "results");
      } else if (view == QueryView.FACETS) {
        params.add("view", "facets");
      } else if (view == QueryView.METADATA) {
        params.add("view", "metadata");
      }
    }

    addPointInTimeQueryParam(params, searchHandle);

    @SuppressWarnings("rawtypes")
    HandleImplementation searchBase = HandleAccessor.checkHandle(searchHandle, "search");

    Format searchFormat = searchBase.getFormat();
    switch(searchFormat) {
      case UNKNOWN:
        searchFormat = Format.XML;
        break;
      case JSON:
      case XML:
        break;
      default:
        throw new UnsupportedOperationException("Only XML and JSON search results are possible.");
    }

    String mimetype = searchFormat.getDefaultMimetype();

    OkHttpSearchRequest request = generateSearchRequest(reqlog, queryDef, mimetype, transaction, null, params, forestName);

    Response response = request.getResponse();
    if ( response == null ) return null;

    Class<?> as = searchBase.receiveAs();


    ResponseBody body = response.body();
    Object entity = body.contentLength() != 0 ? getEntity(body, as) : null;
    if (entity == null || (as != InputStream.class && as != Reader.class)) {
      closeResponse(response);
    }
    searchBase.receiveContent(entity);
    updateDescriptor(searchBase, response.headers());

    logRequest( reqlog,
      "searched starting at %s with length %s in %s transaction with %s mime type",
      start, len, getTransactionId(transaction), mimetype);

    return searchHandle;
  }

  private OkHttpSearchRequest generateSearchRequest(RequestLogger reqlog, QueryDefinition queryDef,
                                                    String mimetype, Transaction transaction, ServerTransform responseTransform,
                                                    RequestParameters params, String forestName)
  {
    if ( params == null ) params = new RequestParameters();
    if ( forestName != null ) params.add("forest-name", forestName);
    return new OkHttpSearchRequest(reqlog, queryDef, mimetype, transaction, responseTransform, params);
  }

  private class OkHttpSearchRequest {
    RequestLogger reqlog;
    QueryDefinition queryDef;
    String mimetype;
    RequestParameters params;
    ServerTransform responseTransform;
    Transaction transaction;

    Request.Builder requestBldr = null;
    String structure = null;
    HandleImplementation baseHandle = null;

    OkHttpSearchRequest(RequestLogger reqlog, QueryDefinition queryDef, String mimetype,
                        Transaction transaction, ServerTransform responseTransform, RequestParameters params) {
      this.reqlog = reqlog;
      this.queryDef = queryDef;
      this.mimetype = mimetype;
      this.transaction = transaction;
      this.responseTransform = responseTransform;
      this.params = params != null ? params : new RequestParameters();
      addParams();
      init();
    }

    void addParams() {
      String directory = queryDef.getDirectory();
      if (directory != null) {
        params.add("directory", directory);
      }

      params.add("collection", queryDef.getCollections());

      String optionsName = queryDef.getOptionsName();
      if (optionsName != null && optionsName.length() > 0) {
        params.add("options", optionsName);
      }

      ServerTransform transform = queryDef.getResponseTransform();
      if (transform != null) {
        if (responseTransform != null) {
          if ( ! transform.getName().equals(responseTransform.getName()) ) {
            throw new IllegalStateException("QueryDefinition transform and DocumentManager transform have different names (" +
              transform.getName() + ", " + responseTransform.getName() + ")");
          }
          logger.warn("QueryDefinition and DocumentManager both specify a ServerTransform--using params from QueryDefinition");
        }
        transform.merge(params);
      } else if (responseTransform != null) {
        responseTransform.merge(params);
      }

      if (transaction != null) {
        params.add("txid", transaction.getTransactionId());
      }
    }

    void init() {
      String text = null;
      if (queryDef instanceof StringQueryDefinition) {
        text = ((StringQueryDefinition) queryDef).getCriteria();
      } else if (queryDef instanceof StructuredQueryDefinition) {
        text = ((StructuredQueryDefinition) queryDef).getCriteria();
      } else if (queryDef instanceof RawStructuredQueryDefinition) {
        text = ((RawStructuredQueryDefinition) queryDef).getCriteria();
      } else if (queryDef instanceof RawCtsQueryDefinition) {
    	text = ((RawCtsQueryDefinition) queryDef).getCriteria();
      }
      if (text != null) {
        params.add("q", text);
      }
      if (queryDef instanceof StructuredQueryDefinition) {
        structure = ((StructuredQueryDefinition) queryDef).serialize();

        if (logger.isDebugEnabled()) {
          String qtextMessage = text == null ? "" : " and string query \"" + text + "\"";
          logger.debug("Searching for structure {}{}", structure, qtextMessage);
        }

        requestBldr = setupRequest("search", params);
        requestBldr = requestBldr.header(HEADER_CONTENT_TYPE, MIMETYPE_APPLICATION_XML);
        requestBldr = requestBldr.header(HEADER_ACCEPT, mimetype);
      } else if (queryDef instanceof RawQueryDefinition || queryDef instanceof RawCtsQueryDefinition) {
        logger.debug("Raw search");

        if (queryDef instanceof RawQueryDefinition) {
          StructureWriteHandle handle = ((RawQueryDefinition) queryDef).getHandle();
          baseHandle = HandleAccessor.checkHandle(handle, "search");
        } else if (queryDef instanceof RawCtsQueryDefinition) {
          CtsQueryWriteHandle handle = ((RawCtsQueryDefinition) queryDef).getHandle();
          baseHandle = HandleAccessor.checkHandle(handle, "search");
        }

        Format payloadFormat = getStructuredQueryFormat(baseHandle);
        String payloadMimetype = getMimetypeWithDefaultXML(payloadFormat, baseHandle);

        String path = (queryDef instanceof RawQueryByExampleDefinition) ?
          "qbe" : "search";

        requestBldr = setupRequest(path, params);
        if ( payloadMimetype != null ) {
          requestBldr = requestBldr.header(HEADER_CONTENT_TYPE, payloadMimetype);
        }
        requestBldr = requestBldr.header(HEADER_ACCEPT, mimetype);
      } else if (queryDef instanceof CombinedQueryDefinition) {
        structure = ((CombinedQueryDefinition) queryDef).serialize();

        logger.debug("Searching for combined query {}", structure);

        requestBldr = setupRequest("search", params);
        requestBldr = requestBldr
          .header(HEADER_CONTENT_TYPE, MIMETYPE_APPLICATION_XML)
          .header(HEADER_ACCEPT, mimetype);
      } else if (queryDef instanceof StringQueryDefinition) {
        logger.debug("Searching for string [{}]", text);

        requestBldr = setupRequest("search", params);
        requestBldr = requestBldr.header(HEADER_CONTENT_TYPE, MIMETYPE_APPLICATION_XML);
        requestBldr = requestBldr.header(HEADER_ACCEPT, mimetype);
      } else if (queryDef instanceof DeleteQueryDefinition) {
        logger.debug("Searching for deletes");

        requestBldr = setupRequest("search", params);
        requestBldr = requestBldr.header(HEADER_ACCEPT, mimetype);
      } else {
        throw new UnsupportedOperationException("Cannot search with "
          + queryDef.getClass().getName());
      }

      requestBldr = addTransactionScopedCookies(requestBldr, transaction);
      requestBldr = addTelemetryAgentId(requestBldr);
    }

    Response getResponse() {
      Response response = null;
      int status = -1;
      long startTime = System.currentTimeMillis();
      int nextDelay = 0;
      int retry = 0;
      for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
        if (nextDelay > 0) {
          try {
            Thread.sleep(nextDelay);
          } catch (InterruptedException e) {
          }
        }

        if (queryDef instanceof StructuredQueryDefinition && ! (queryDef instanceof RawQueryDefinition)) {
          response = doPost(reqlog, requestBldr, structure);
        } else if (queryDef instanceof CombinedQueryDefinition) {
          response = doPost(reqlog, requestBldr, structure);
        } else if (queryDef instanceof DeleteQueryDefinition) {
          response = doGet(requestBldr);
        } else if (queryDef instanceof RawQueryDefinition) {
          response = doPost(reqlog, requestBldr, baseHandle.sendContent());
        } else if (queryDef instanceof RawCtsQueryDefinition) {
          response = doPost(reqlog, requestBldr, baseHandle.sendContent());
        } else if (queryDef instanceof StringQueryDefinition) {
          response = doGet(requestBldr);
        } else {
          throw new UnsupportedOperationException("Cannot search with "
            + queryDef.getClass().getName());
        }

        status = response.code();

        if (transaction != null || !retryStatus.contains(status)) {
          if (isFirstRequest()) setFirstRequest(false);

          break;
        }

        String retryAfterRaw = response.header("Retry-After");
        int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;

        closeResponse(response);

        nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
      }
      if (retryStatus.contains(status)) {
        checkFirstRequest();
        closeResponse(response);
        throw new FailedRetryException(
          "Service unavailable and maximum retry period elapsed: "+
            ((System.currentTimeMillis() - startTime) / 1000)+
            " seconds after "+retry+" retries");
      }
      if (status == STATUS_NOT_FOUND) {
        closeResponse(response);
        return null;
      }
      if (status == STATUS_FORBIDDEN) {
        throw new ForbiddenUserException("User is not allowed to search",
          extractErrorFields(response));
      }
      if (status != STATUS_OK) {
        throw new FailedRequestException("search failed: "
          + getReasonPhrase(response), extractErrorFields(response));
      }
      return response;
    }
  }

  private Format getStructuredQueryFormat(HandleImplementation baseHandle) {
    Format payloadFormat = baseHandle.getFormat();
    if (payloadFormat == Format.UNKNOWN) {
      payloadFormat = null;
    } else if (payloadFormat != Format.XML && payloadFormat != Format.JSON) {
      throw new IllegalArgumentException(
        "Cannot perform raw search for format "+payloadFormat.name());
    }
    return payloadFormat;
  }

  private String getMimetypeWithDefaultXML(Format payloadFormat, HandleImplementation baseHandle) {
    String payloadMimetype = baseHandle.getMimetype();
    if (payloadFormat != null) {
      if (payloadMimetype == null) {
        payloadMimetype = payloadFormat.getDefaultMimetype();
      }
    } else if (payloadMimetype == null) {
      payloadMimetype = MIMETYPE_APPLICATION_XML;
    }
    return payloadMimetype;
  }

  @Override
  public void deleteSearch(RequestLogger reqlog, DeleteQueryDefinition queryDef,
                           Transaction transaction)
    throws ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();

    if (queryDef.getDirectory() != null) {
      params.add("directory", queryDef.getDirectory());
    }

    params.add("collection", queryDef.getCollections());

    if (transaction != null) {
      params.add("txid", transaction.getTransactionId());
    }

    Request.Builder requestBldr = setupRequest("search", params);

    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doDeleteFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.delete().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doDeleteFunction, null);
    int status = response.code();
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to delete",
        extractErrorFields(response));
    }

    if (status != STATUS_NO_CONTENT) {
      throw new FailedRequestException("delete failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    closeResponse(response);

    logRequest(
      reqlog,
      "deleted search results in %s transaction",
      getTransactionId(transaction));
  }

  @Override
  public void delete(RequestLogger logger, Transaction transaction, String... uris)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    for ( String uri : uris ) {
      params.add("uri", uri);
    }
    deleteResource(logger, "documents", transaction, params, null);
  }

  @Override
  public <T> T values(Class<T> as, ValuesDefinition valDef, String mimetype,
                      long start, long pageLength, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException
  {
    RequestParameters docParams = new RequestParameters();

    String optionsName = valDef.getOptionsName();
    if (optionsName != null && optionsName.length() > 0) {
      docParams.add("options", optionsName);
    }

    if (valDef.getAggregate() != null) {
      docParams.add("aggregate", valDef.getAggregate());
    }

    if (valDef.getAggregatePath() != null) {
      docParams.add("aggregatePath",
        valDef.getAggregatePath());
    }

    if (valDef.getView() != null) {
      docParams.add("view", valDef.getView());
    }

    if (valDef.getDirection() != null) {
      if (valDef.getDirection() == ValuesDefinition.Direction.ASCENDING) {
        docParams.add("direction", "ascending");
      } else {
        docParams.add("direction", "descending");
      }
    }

    if (valDef.getFrequency() != null) {
      if (valDef.getFrequency() == ValuesDefinition.Frequency.FRAGMENT) {
        docParams.add("frequency", "fragment");
      } else {
        docParams.add("frequency", "item");
      }
    }

    if (start > 0) {
      docParams.add("start", Long.toString(start));
      if (pageLength > 0) {
        docParams.add("pageLength", Long.toString(pageLength));
      }
    }

    HandleImplementation baseHandle = null;

    if (valDef.getQueryDefinition() != null) {
      ValueQueryDefinition queryDef = valDef.getQueryDefinition();

      if (optionsName == null) {
        optionsName = queryDef.getOptionsName();
        if (optionsName != null) {
          docParams.add("options", optionsName);
        }
      } else if (queryDef.getOptionsName() != null) {
        if (!queryDef.getOptionsName().equals(optionsName)) {
          logger.warn("values definition options take precedence over query definition options");
        }
      }

      if (queryDef.getCollections().length > 0) {
        logger.warn("collections scope ignored for values query");
      }
      if (queryDef.getDirectory() != null) {
        logger.warn("directory scope ignored for values query");
      }

      String text = null;
      if (queryDef instanceof StringQueryDefinition) {
        text = ((StringQueryDefinition) queryDef).getCriteria();
      } else if (queryDef instanceof StructuredQueryDefinition) {
        text = ((StructuredQueryDefinition) queryDef).getCriteria();
      } else if (queryDef instanceof RawStructuredQueryDefinition) {
        text = ((RawStructuredQueryDefinition) queryDef).getCriteria();
      } else if (queryDef instanceof RawCtsQueryDefinition) {
        text = ((RawCtsQueryDefinition) queryDef).getCriteria();
      }
      if (text != null) {
        docParams.add("q", text);
      }
      if (queryDef instanceof StructuredQueryDefinition ) {
        String structure = ((StructuredQueryDefinition) queryDef)
          .serialize();
        if (structure != null) {
          docParams.add("structuredQuery", structure);
        }
      } else if (queryDef instanceof RawQueryDefinition) {
        StructureWriteHandle handle = ((RawQueryDefinition) queryDef).getHandle();
        baseHandle = HandleAccessor.checkHandle(handle, "values");
      } else if (queryDef instanceof RawCtsQueryDefinition) {
        CtsQueryWriteHandle handle = ((RawCtsQueryDefinition) queryDef).getHandle();
        baseHandle = HandleAccessor.checkHandle(handle, "values");
      } else if (queryDef instanceof StringQueryDefinition) {
      } else {
        logger.warn("unsupported query definition: {}", queryDef.getClass().getName());
      }

      ServerTransform transform = queryDef.getResponseTransform();
      if (transform != null) {
        transform.merge(docParams);
      }
    }

    if (transaction != null) {
      docParams.add("txid", transaction.getTransactionId());
    }

    String uri = "values";
    if (valDef.getName() != null) {
      uri += "/" + valDef.getName();
    }

    Request.Builder requestBldr = setupRequest(uri, docParams);
    requestBldr = setupRequest(requestBldr, null, mimetype);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    final HandleImplementation tempBaseHandle = baseHandle;

    Function<Request.Builder, Response> doFunction = (baseHandle == null) ?
      new Function<Request.Builder, Response>() {
        public Response apply(Request.Builder funcBuilder) {
          return doGet(funcBuilder);
        }
      } :
      new Function<Request.Builder, Response>() {
        public Response apply(Request.Builder funcBuilder) {
          return doPost(null, funcBuilder.header(HEADER_CONTENT_TYPE, tempBaseHandle.getMimetype()),
            tempBaseHandle.sendContent());
        }
      };

    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doFunction, null);
    int status = response.code();

    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to search",
        extractErrorFields(response));
    }
    if (status != STATUS_OK) {
      throw new FailedRequestException("search failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    ResponseBody body = response.body();
    T entity = body.contentLength() != 0 ? getEntity(body, as) : null;
    if (entity == null || (as != InputStream.class && as != Reader.class)) {
      closeResponse(response);
    }

    return entity;
  }

  @Override
  public <T> T valuesList(Class<T> as, ValuesListDefinition valDef,
                          String mimetype, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException
  {
    RequestParameters docParams = new RequestParameters();

    String optionsName = valDef.getOptionsName();
    if (optionsName != null && optionsName.length() > 0) {
      docParams.add("options", optionsName);
    }

    if (transaction != null) {
      docParams.add("txid", transaction.getTransactionId());
    }

    String uri = "values";

    Request.Builder requestBldr = setupRequest(uri, docParams);
    requestBldr = setupRequest(requestBldr, null, mimetype);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.get().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doGetFunction, null);
    int status = response.code();

    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to search",
        extractErrorFields(response));
    }
    if (status != STATUS_OK) {
      throw new FailedRequestException("search failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    ResponseBody body = response.body();
    T entity = body.contentLength() != 0 ? getEntity(body, as) : null;
    if (entity == null || (as != InputStream.class && as != Reader.class)) {
      closeResponse(response);
    }

    return entity;
  }

  @Override
  public <T> T optionsList(Class<T> as, String mimetype, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException
  {
    RequestParameters docParams = new RequestParameters();

    if (transaction != null) {
      docParams.add("txid", transaction.getTransactionId());
    }

    String uri = "config/query";

    Request.Builder requestBldr = setupRequest(uri, docParams);
    requestBldr = requestBldr.header(HEADER_ACCEPT, mimetype);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.get().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doGetFunction, null);
    int status = response.code();

    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to search",
        extractErrorFields(response));
    }
    if (status != STATUS_OK) {
      throw new FailedRequestException("search failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    ResponseBody body = response.body();
    T entity = body.contentLength() != 0 ? getEntity(body, as) : null;
    if (entity == null || (as != InputStream.class && as != Reader.class)) {
      closeResponse(response);
    }

    return entity;
  }

  // namespaces, search options etc.
  @Override
  public <T> T getValue(RequestLogger reqlog, String type, String key,
                        boolean isNullable, String mimetype, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
	  return getValue(reqlog, type, key, null, isNullable, mimetype, as);
  }
  @Override
  public <T> T getValue(RequestLogger reqlog, String type, String key, Transaction transaction,
		  boolean isNullable, String mimetype, Class<T> as)
				  throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    logger.debug("Getting {}/{}", type, key);

    Request.Builder requestBldr = setupRequest(type + "/" + key, null, null, mimetype);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.get().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doGetFunction, null);
    int status = response.code();

    if (status != STATUS_OK) {
      if (status == STATUS_NOT_FOUND) {
        closeResponse(response);
        if (!isNullable) {
          throw new ResourceNotFoundException("Could not get " + type + "/" + key);
        }
        return null;
      } else if (status == STATUS_FORBIDDEN) {
        throw new ForbiddenUserException("User is not allowed to read "
          + type, extractErrorFields(response));
      } else {
        throw new FailedRequestException(type + " read failed: "
          + getReasonPhrase(response),
          extractErrorFields(response));
      }
    }

    logRequest(reqlog, "read %s value with %s key and %s mime type", type,
      key, mimetype);

    ResponseBody body = response.body();
    T entity = body.contentLength() != 0 ? getEntity(body, as) : null;
    if (entity == null || (as != InputStream.class && as != Reader.class)) {
      closeResponse(response);
    }

    return (reqlog != null) ? reqlog.copyContent(entity) : entity;
  }

  @Override
  public <T> T getValues(RequestLogger reqlog, String type, String mimetype, Class<T> as)
    throws ForbiddenUserException, FailedRequestException
  {
    return getValues(reqlog, type, null, mimetype, as);
  }
  @Override
  public <T> T getValues(RequestLogger reqlog, String type, RequestParameters extraParams,
                         String mimetype, Class<T> as)
    throws ForbiddenUserException, FailedRequestException
  {
    logger.debug("Getting {}", type);

    Request.Builder requestBldr = setupRequest(type, extraParams).header(HEADER_ACCEPT, mimetype);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.get().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, doGetFunction, null);
    int status = response.code();
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to read "
        + type, extractErrorFields(response));
    }
    if (status != STATUS_OK) {
      throw new FailedRequestException(type + " read failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    logRequest(reqlog, "read %s values with %s mime type", type, mimetype);

    ResponseBody body = response.body();
    T entity = body.contentLength() != 0 ? getEntity(body, as) : null;
    if (entity == null || (as != InputStream.class && as != Reader.class)) {
      closeResponse(response);
    }

    return (reqlog != null) ? reqlog.copyContent(entity) : entity;
  }

  @Override
  public void postValue(RequestLogger reqlog, String type, String key,
                        String mimetype, Object value)
    throws ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    logger.debug("Posting {}/{}", type, key);

    putPostValueImpl(reqlog, "post", type, key, null, mimetype, value, STATUS_CREATED);
  }
  @Override
  public void postValue(RequestLogger reqlog, String type, String key,
                        RequestParameters extraParams)
    throws ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    logger.debug("Posting {}/{}", type, key);

    putPostValueImpl(reqlog, "post", type, key, extraParams, null, null, STATUS_NO_CONTENT);
  }


  @Override
  public void putValue(RequestLogger reqlog, String type, String key,
                       String mimetype, Object value)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    logger.debug("Putting {}/{}", type, key);

    putPostValueImpl(reqlog, "put", type, key, null, mimetype, value, STATUS_NO_CONTENT, STATUS_CREATED);
  }

  @Override
  public void putValue(RequestLogger reqlog, String type, String key,
                       RequestParameters extraParams, String mimetype, Object value)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    logger.debug("Putting {}/{}", type, key);

    putPostValueImpl(reqlog, "put", type, key, extraParams, mimetype, value, STATUS_NO_CONTENT);
  }

  private void putPostValueImpl(RequestLogger reqlog, String method,
                                String type, String key, RequestParameters extraParams,
                                String mimetype, Object value,
                                int... expectedStatuses) {
    if (key != null) {
      logRequest(reqlog, "writing %s value with %s key and %s mime type",
        type, key, mimetype);
    } else {
      logRequest(reqlog, "writing %s values with %s mime type", type, mimetype);
    }

    HandleImplementation handle = (value instanceof HandleImplementation) ?
      (HandleImplementation) value : null;

    MediaType mediaType = makeType(mimetype);

    String connectPath = null;
    Request.Builder requestBldr = null;

    Response response = null;
    int status = -1;
    long startTime = System.currentTimeMillis();
    int nextDelay = 0;
    int retry = 0;
    for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
      if (nextDelay > 0) {
        try {
          Thread.sleep(nextDelay);
        } catch (InterruptedException e) {
        }
      }

      Object nextValue = (handle != null) ? handle.sendContent() : value;

      RequestBody sentValue = null;
      if (nextValue instanceof OutputStreamSender) {
        sentValue = new StreamingOutputImpl(
          (OutputStreamSender) nextValue, reqlog, mediaType);
      } else {
        if (reqlog != null && retry == 0) {
          sentValue = new ObjectRequestBody(reqlog.copyContent(nextValue), mediaType);
        } else {
          sentValue = new ObjectRequestBody(nextValue, mediaType);
        }
      }

      boolean isStreaming = (isFirstRequest() || handle == null) ? isStreaming(sentValue) : false;

      boolean isResendable = (handle == null) ? !isStreaming : handle.isResendable();

      if (isFirstRequest() && !isResendable && isStreaming) {
        nextDelay = makeFirstRequest(retry);
        if (nextDelay != 0) continue;
      }

      if ("put".equals(method)) {
        if (requestBldr == null) {
          connectPath = (key != null) ? type + "/" + key : type;
          Request.Builder resource = setupRequest(connectPath, extraParams);
          requestBldr = (mimetype == null) ?
            resource : resource.header(HEADER_CONTENT_TYPE, mimetype);
          requestBldr = addTelemetryAgentId(requestBldr);
        }

        response = (sentValue == null) ?
                   sendRequestOnce(requestBldr.put(null).build()) :
                   sendRequestOnce(requestBldr.put(sentValue).build());
      } else if ("post".equals(method)) {
        if (requestBldr == null) {
          connectPath = type;
          Request.Builder resource = setupRequest(connectPath, extraParams);
          requestBldr = (mimetype == null) ?
            resource : resource.header(HEADER_CONTENT_TYPE, mimetype);
          requestBldr = addTelemetryAgentId(requestBldr);
        }

        response = (sentValue == null) ?
                   sendRequestOnce(requestBldr.post(RequestBody.create(null, "")).build()) :
                   sendRequestOnce(requestBldr.post(sentValue).build());
      } else {
        throw new MarkLogicInternalException("unknown method type "
          + method);
      }

      status = response.code();

      if (!retryStatus.contains(status)) {
        if (isFirstRequest()) setFirstRequest(false);
        break;
      }

      String retryAfterRaw = response.header("Retry-After");
      closeResponse(response);

      if (!isResendable) {
        checkFirstRequest();
        throw new ResourceNotResendableException(
          "Cannot retry request for " + connectPath);
      }

      int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
      nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
    }
    if (retryStatus.contains(status)) {
      checkFirstRequest();
      closeResponse(response);
      throw new FailedRetryException(
        "Service unavailable and maximum retry period elapsed: "+
          ((System.currentTimeMillis() - startTime) / 1000)+
          " seconds after "+retry+" retries");
    }
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to write "
        + type, extractErrorFields(response));
    }
    if (status == STATUS_NOT_FOUND) {
      throw new ResourceNotFoundException(type + " not found for write",
        extractErrorFields(response));
    }
    boolean statusOk = false;
    for (int expectedStatus : expectedStatuses) {
      statusOk = statusOk || (status == expectedStatus);
      if (statusOk) {
        break;
      }
    }

    if (!statusOk) {
      throw new FailedRequestException(type + " write failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }
    closeResponse(response);
  }

  @Override
  public void deleteValue(RequestLogger reqlog, String type, String key)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    logger.debug("Deleting {}/{}", type, key);

    Request.Builder requestBldr = setupRequest(type + "/" + key, null);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doDeleteFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.delete().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, doDeleteFunction, null);
    int status = response.code();
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to delete "
        + type, extractErrorFields(response));
    }
    if (status == STATUS_NOT_FOUND) {
      throw new ResourceNotFoundException(type + " not found for delete",
        extractErrorFields(response));
    }
    if (status != STATUS_NO_CONTENT) {
      throw new FailedRequestException("delete failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    closeResponse(response);

    logRequest(reqlog, "deleted %s value with %s key", type, key);
  }

  @Override
  public void deleteValues(RequestLogger reqlog, String type)
    throws ForbiddenUserException, FailedRequestException
  {
    logger.debug("Deleting {}", type);

    Request.Builder requestBldr = setupRequest(type, null);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doDeleteFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.delete().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, doDeleteFunction, null);
    int status = response.code();
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to delete "
        + type, extractErrorFields(response));
    }
    if (status != STATUS_NO_CONTENT) {
      throw new FailedRequestException("delete failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }
    closeResponse(response);

    logRequest(reqlog, "deleted %s values", type);
  }

  @Override
  public <R extends UrisReadHandle> R uris(RequestLogger reqlog, Transaction transaction,
        QueryDefinition qdef, long start, String afterUri, long pageLength, String forestName, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    if ( forestName != null )        params.add("forest-name", forestName);
    if (start > 1)                   params.add("start",       Long.toString(start));
    if (afterUri != null )           params.add("after",       afterUri);
    if (pageLength >= 1)             params.add("pageLength",  Long.toString(pageLength));
    if (qdef.getDirectory() != null) params.add("directory",   qdef.getDirectory());
    if (qdef.getCollections() != null ) {
      for ( String collection : qdef.getCollections() ) {
        params.add("collection", collection);
      }
    }
    if (qdef.getOptionsName()!= null && qdef.getOptionsName().length() > 0) {
      params.add("options", qdef.getOptionsName());
    }

    if (qdef instanceof RawQueryByExampleDefinition) {
      throw new UnsupportedOperationException("Cannot search with RawQueryByExampleDefinition");
    }

    String text = null;
    if (qdef instanceof StringQueryDefinition) {
      text = ((StringQueryDefinition) qdef).getCriteria();
    } else if (qdef instanceof StructuredQueryDefinition) {
      text = ((StructuredQueryDefinition) qdef).getCriteria();
    } else if (qdef instanceof RawStructuredQueryDefinition) {
      text = ((RawStructuredQueryDefinition) qdef).getCriteria();
    } else if (qdef instanceof RawCtsQueryDefinition) {
      text = ((RawCtsQueryDefinition) qdef).getCriteria();
    }

    String qtextMessage = "";
    if (text != null) {
      params.add("q", text);
      qtextMessage = " and string query \"" + text + "\"";
    }

    if (qdef instanceof RawCtsQueryDefinition) {
      String structure = qdef instanceof RawQueryDefinitionImpl.CtsQuery ?
              ((RawQueryDefinitionImpl.CtsQuery) qdef).serialize() : "";
      logger.debug("Query uris with raw cts query {}{}", structure, qtextMessage);
      CtsQueryWriteHandle input = ((RawCtsQueryDefinition) qdef).getHandle();
      return postResource(reqlog, "internal/uris", transaction, params, input, output);
    } else if (qdef instanceof StructuredQueryDefinition) {
      String structure = ((StructuredQueryDefinition) qdef).serialize();
      logger.debug("Query uris with structured query {}{}", structure, qtextMessage);
      if (structure != null) {
        params.add("structuredQuery", structure);
      }
    } else if (qdef instanceof RawStructuredQueryDefinition) {
      String structure = ((RawStructuredQueryDefinition) qdef).serialize();
      logger.debug("Query uris with raw structured query {}{}", structure, qtextMessage);
      if (structure != null) {
        params.add("structuredQuery", structure);
      }
    } else if (qdef instanceof CombinedQueryDefinition) {
      String structure = ((CombinedQueryDefinition) qdef).serialize();
      logger.debug("Query uris with combined query {}", structure);
      if (structure != null) {
        params.add("structuredQuery", structure);
      }
    } else if (qdef instanceof StringQueryDefinition) {
      logger.debug("Query uris with string query \"{}\"", text);
    } else if (qdef instanceof RawQueryDefinition) {
      logger.debug("Raw uris query");
      StructureWriteHandle input = ((RawQueryDefinition) qdef).getHandle();
      return postResource(reqlog, "internal/uris", transaction, params, input, output);
    } else {
      throw new UnsupportedOperationException("Cannot query uris with " + qdef.getClass().getName());
    }
    return getResource(reqlog, "internal/uris", transaction, params, output);
  }


  @Override
  public <R extends AbstractReadHandle> R getResource(RequestLogger reqlog,
                                                      String path, Transaction transaction, RequestParameters params, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    if ( params == null ) params = new RequestParameters();
    if ( transaction != null ) params.add("txid", transaction.getTransactionId());
    addPointInTimeQueryParam(params, output);
    HandleImplementation outputBase = HandleAccessor.checkHandle(output,
      "read");

    String mimetype = outputBase.getMimetype();
    Class as = outputBase.receiveAs();

    Request.Builder requestBldr = makeGetWebResource(path, params, mimetype);
    requestBldr = setupRequest(requestBldr, null, mimetype);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return doGet(funcBuilder);
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doGetFunction, null);
    int status = response.code();
    checkStatus(response, status, "read", "resource", path,
      ResponseStatus.OK_OR_NO_CONTENT);

    updateDescriptor(outputBase, response.headers());
    if (as != null) {
      outputBase.receiveContent(makeResult(reqlog, "read", "resource",
        response, as));
    } else {
      closeResponse(response);
    }

    return output;
  }

  @Override
  public RESTServiceResultIterator getIteratedResource(RequestLogger reqlog,
                                                       String path, Transaction transaction, RequestParameters params, String... mimetypes)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    return getIteratedResourceImpl(OkHttpServiceResultIterator::new, reqlog, path, transaction, params, mimetypes);
  }

  private <U extends OkHttpResultIterator> U getIteratedResourceImpl(ResultIteratorConstructor<U> constructor,
        RequestLogger reqlog, String path, Transaction transaction, RequestParameters params, String... mimetypes)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    if ( params == null ) params = new RequestParameters();
    if (transaction != null) params.add("txid", transaction.getTransactionId());

    Request.Builder requestBldr = makeGetWebResource(path, params, null);
    requestBldr = setupRequest(requestBldr, null, null);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    requestBldr = requestBldr.header(HEADER_ACCEPT, multipartMixedWithBoundary());
    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return doGet(funcBuilder);
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doGetFunction, null);
    int status = response.code();
    checkStatus(response, status, "read", "resource", path,
      ResponseStatus.OK_OR_NO_CONTENT);

    return makeResults(constructor, reqlog, "read", "resource", response);
  }

  @Override
  public <R extends AbstractReadHandle> R putResource(RequestLogger reqlog,
                                                      String path, Transaction transaction, RequestParameters params,
                                                      AbstractWriteHandle input, R output)
      throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
    {
    if ( params == null ) params = new RequestParameters();
    if ( transaction != null ) params.add("txid", transaction.getTransactionId());
    HandleImplementation inputBase = HandleAccessor.checkHandle(input,
      "write");
    HandleImplementation outputBase = HandleAccessor.checkHandle(output,
      "read");

    String inputMimetype = inputBase.getMimetype();
    boolean isResendable = inputBase.isResendable();
    String outputMimeType = null;
    Class as = null;
    if (outputBase != null) {
      outputMimeType = outputBase.getMimetype();
      as = outputBase.receiveAs();
    }
    Request.Builder requestBldr = makePutWebResource(path, params);
    requestBldr = setupRequest(requestBldr, inputMimetype, outputMimeType);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Consumer<Boolean> resendableConsumer = (resendable) -> {
      if (!isResendable) {
        checkFirstRequest();
        throw new ResourceNotResendableException(
          "Cannot retry request for " + path);
      }
    };

    Function<Request.Builder, Response> doPutFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return doPut(reqlog, funcBuilder, inputBase.sendContent());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doPutFunction, resendableConsumer);
    int status = response.code();

    checkStatus(response, status, "write", "resource", path,
      ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

    if (as != null) {
      outputBase.receiveContent(makeResult(reqlog, "write", "resource",
        response, as));
    } else {
      closeResponse(response);
    }

    return output;
  }

  @Override
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R putResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    if (input == null || input.length == 0) {
      throw new IllegalArgumentException("input not specified for multipart");
    }
    if ( params == null ) params = new RequestParameters();
    if ( transaction != null ) params.add("txid", transaction.getTransactionId());

    HandleImplementation outputBase = HandleAccessor.checkHandle(output,
      "read");

    String outputMimetype = outputBase.getMimetype();
    Class as = outputBase.receiveAs();

    Response response = null;
    int status = -1;
    long startTime = System.currentTimeMillis();
    int nextDelay = 0;
    int retry = 0;
    for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
      if (nextDelay > 0) {
        try {
          Thread.sleep(nextDelay);
        } catch (InterruptedException e) {
        }
      }

      MultipartBody.Builder multiPart = new MultipartBody.Builder();
      boolean hasStreamingPart = addParts(multiPart, reqlog, input);

      Request.Builder requestBldr = makePutWebResource(path, params);
      requestBldr = setupRequest(requestBldr, multiPart, outputMimetype);
      requestBldr = addTransactionScopedCookies(requestBldr, transaction);
      requestBldr = addTelemetryAgentId(requestBldr);

      response = doPut(requestBldr, multiPart, hasStreamingPart);
      status = response.code();

      if (transaction != null || !retryStatus.contains(status)) {
        if (isFirstRequest()) setFirstRequest(false);

        break;
      }

      String retryAfterRaw = response.header("Retry-After");
      closeResponse(response);

      if (hasStreamingPart) {
        throw new ResourceNotResendableException(
          "Cannot retry request for " + path);
      }

      int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
      nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
    }
    if (retryStatus.contains(status)) {
      checkFirstRequest();
      closeResponse(response);
      throw new FailedRetryException(
        "Service unavailable and maximum retry period elapsed: "+
          ((System.currentTimeMillis() - startTime) / 1000)+
          " seconds after "+retry+" retries");
    }

    checkStatus(response, status, "write", "resource", path,
      ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

    if (as != null) {
      outputBase.receiveContent(makeResult(reqlog, "write", "resource",
        response, as));
    } else {
      closeResponse(response);
    }

    return output;
  }

  @Override
  public <R extends AbstractReadHandle> R postResource(RequestLogger reqlog,
                                                       String path, Transaction transaction, RequestParameters params,
                                                       AbstractWriteHandle input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    return postResource(reqlog, path, transaction, params, input, output, "apply");
  }

  @Override
  public <R extends AbstractReadHandle> R postResource(RequestLogger reqlog,
                                                       String path, Transaction transaction, RequestParameters params,
                                                       AbstractWriteHandle input, R output, String operation)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    return postResource(reqlog, path, transaction, params, input, output, operation, null);
  }

  @Override
  public <R extends AbstractReadHandle> R postResource(RequestLogger reqlog,
                                                       String path, Transaction transaction, RequestParameters params,
                                                       AbstractWriteHandle input, R output, String operation,
                                                       Map<String,List<String>> responseHeaders)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    if ( params == null ) params = new RequestParameters();
    if ( transaction != null ) params.add("txid", transaction.getTransactionId());

    HandleImplementation inputBase = HandleAccessor.checkHandle(input,
      "write");
    HandleImplementation outputBase = HandleAccessor.checkHandle(output,
      "read");

    String inputMimetype = null;
    if(inputBase != null) {
      inputMimetype = inputBase.getMimetype();
      if ( inputMimetype == null &&
           ( Format.JSON == inputBase.getFormat() ||
             Format.XML == inputBase.getFormat() ) )
      {
        inputMimetype = inputBase.getFormat().getDefaultMimetype();
      }
    }
    String outputMimetype = outputBase == null ? null : outputBase.getMimetype();
    boolean isResendable = inputBase == null ? true : inputBase.isResendable();
    Class as = outputBase == null ? null : outputBase.receiveAs();

    Request.Builder requestBldr = makePostWebResource(path, params);
    requestBldr = setupRequest(requestBldr, inputMimetype, outputMimetype);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Consumer<Boolean> resendableConsumer = new Consumer<Boolean>() {
      public void accept(Boolean resendable) {
        if (!isResendable) {
          checkFirstRequest();
          throw new ResourceNotResendableException("Cannot retry request for " + path);
        }
      }
    };
    final Object value = inputBase == null ? null :inputBase.sendContent();
    Function<Request.Builder, Response> doPostFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return doPost(reqlog, funcBuilder, value);
      }
    };

    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doPostFunction, resendableConsumer);
    int status = response.code();
    checkStatus(response, status, operation, "resource", path,
      ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

    if ( responseHeaders != null ) {
      // add all the headers from the OkHttp Headers object to the caller-provided map
      responseHeaders.putAll( response.headers().toMultimap() );
    }

    if (as != null) {
      outputBase.receiveContent(makeResult(reqlog, operation, "resource",
        response, as));
    } else {
      closeResponse(response);
    }

    return output;
  }

  @Override
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    return postResource(reqlog, path, transaction, params, input, null, output);
  }

  @Override
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, Map<String, List<String>>[] requestHeaders, R output)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    if ( params == null ) params = new RequestParameters();
    if ( transaction != null ) params.add("txid", transaction.getTransactionId());

    HandleImplementation outputBase = HandleAccessor.checkHandle(output, "read");

    String outputMimetype = outputBase != null ? outputBase.getMimetype() : null;
    Class as = outputBase != null ? outputBase.receiveAs() : null;

    Response response = null;
    int status = -1;
    long startTime = System.currentTimeMillis();
    int nextDelay = 0;
    int retry = 0;
    for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
      if (nextDelay > 0) {
        try {
          Thread.sleep(nextDelay);
        } catch (InterruptedException e) {
        }
      }

      MultipartBody.Builder multiPart = new MultipartBody.Builder();
      boolean hasStreamingPart = addParts(multiPart, reqlog, null, input, requestHeaders);

      Request.Builder requestBldr = makePostWebResource(path, params);
      requestBldr = setupRequest(requestBldr, multiPart, outputMimetype);
      requestBldr = addTransactionScopedCookies(requestBldr, transaction);
      requestBldr = addTelemetryAgentId(requestBldr);

      response = doPost(requestBldr, multiPart, hasStreamingPart);
      status = response.code();

      if (transaction != null || !retryStatus.contains(status)) {
        if (isFirstRequest()) setFirstRequest(false);

        break;
      }

      String retryAfterRaw = response.header("Retry-After");
      closeResponse(response);

      if (hasStreamingPart) {
        throw new ResourceNotResendableException(
          "Cannot retry request for " + path);
      }

      int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
      nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
    }
    if (retryStatus.contains(status)) {
      checkFirstRequest();
      closeResponse(response);
      throw new FailedRetryException(
        "Service unavailable and maximum retry period elapsed: "+
          ((System.currentTimeMillis() - startTime) / 1000)+
          " seconds after "+retry+" retries");
    }

    checkStatus(response, status, "apply", "resource", path,
      ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

    if (as != null) {
      outputBase.receiveContent(makeResult(reqlog, "apply", "resource",
        response, as));
    } else {
      closeResponse(response);
    }

    return output;
  }

  @Override
  public void postBulkDocuments(
    RequestLogger reqlog, DocumentWriteSet writeSet,
    ServerTransform transform, Transaction transaction, Format defaultFormat)
    throws ForbiddenUserException,  FailedRequestException
  {
    postBulkDocuments(reqlog, writeSet, transform, transaction, defaultFormat, null, null);
  }

  @Override
  public <R extends AbstractReadHandle> R postBulkDocuments(
    RequestLogger reqlog, DocumentWriteSet writeSet,
    ServerTransform transform, Transaction transaction, Format defaultFormat, R output,
    String temporalCollection)
    throws ForbiddenUserException,  FailedRequestException
  {
    List<AbstractWriteHandle> writeHandles = new ArrayList<AbstractWriteHandle>();
    List<RequestParameters> headerList = new ArrayList<RequestParameters>();
    for ( DocumentWriteOperation write : writeSet ) {
      String temporalDocumentURI = write.getTemporalDocumentURI();
      HandleImplementation metadata = HandleAccessor.checkHandle(write.getMetadata(), "write");
      HandleImplementation content = HandleAccessor.checkHandle(write.getContent(), "write");
      String contentDispositionTemporal = "";
      if (temporalDocumentURI != null) {
        // escape any quotes or back-slashes in the uri
        temporalDocumentURI = escapeContentDispositionFilename(temporalDocumentURI);
        contentDispositionTemporal = "; temporal-document="+temporalDocumentURI;
      }
      if ( write.getOperationType() == DocumentWriteOperation.OperationType.DISABLE_METADATA_DEFAULT ) {
        RequestParameters headers = new RequestParameters();
        headers.add(HEADER_CONTENT_TYPE, metadata.getMimetype());
        headers.add(HEADER_CONTENT_DISPOSITION, DISPOSITION_TYPE_INLINE + "; category=metadata" + contentDispositionTemporal);
        headerList.add(headers);
        writeHandles.add(write.getMetadata());
      } else if ( metadata != null ) {
        RequestParameters headers = new RequestParameters();
        headers.add(HEADER_CONTENT_TYPE, metadata.getMimetype());
        if ( write.getOperationType() == DocumentWriteOperation.OperationType.METADATA_DEFAULT ) {
          headers.add(HEADER_CONTENT_DISPOSITION, DISPOSITION_TYPE_INLINE + "; category=metadata" + contentDispositionTemporal);
        } else {
          String disposition = DISPOSITION_TYPE_ATTACHMENT  + "; " +
            DISPOSITION_PARAM_FILENAME + "=" + escapeContentDispositionFilename(write.getUri()) +
            "; category=metadata" + contentDispositionTemporal;
          headers.add(HEADER_CONTENT_DISPOSITION, disposition);
        }
        headerList.add(headers);
        writeHandles.add(write.getMetadata());
      }
      if ( content != null ) {
        RequestParameters headers = new RequestParameters();
        String mimeType = content.getMimetype();
        if ( mimeType == null && defaultFormat != null ) {
          mimeType = defaultFormat.getDefaultMimetype();
        }
        headers.add(HEADER_CONTENT_TYPE, mimeType);
        String disposition = DISPOSITION_TYPE_ATTACHMENT + "; " +
          DISPOSITION_PARAM_FILENAME + "=" + escapeContentDispositionFilename(write.getUri()) + contentDispositionTemporal;
        headers.add(HEADER_CONTENT_DISPOSITION, disposition);
        headerList.add(headers);
        writeHandles.add(write.getContent());
      }
    }
    RequestParameters params = new RequestParameters();
    if ( transform != null ) {
      transform.merge(params);
    }
    if ( temporalCollection != null ) params.add("temporal-collection", temporalCollection);
    return postResource(reqlog, "documents", transaction, params,
      (AbstractWriteHandle[]) writeHandles.toArray(new AbstractWriteHandle[0]),
      (RequestParameters[]) headerList.toArray(new RequestParameters[0]),
      output);
  }

  // TODO: See what other escaping we need to do for filenames
  private String escapeContentDispositionFilename(String str) {
    if ( str == null ) return null;
    // escape any quotes or back-slashes
    return "\"" + str.replace("\"", "\\\"").replace("\\", "\\\\") + "\"";
  }

  public class OkHttpEvalResultIterator implements EvalResultIterator {
    private OkHttpResultIterator iterator;

    OkHttpEvalResultIterator(OkHttpResultIterator iterator) {
      this.iterator = iterator;
    }

    @Override
    public Iterator<EvalResult> iterator() {
      return this;
    }

    @Override
    public boolean hasNext() {
      if ( iterator == null ) return false;
      return iterator.hasNext();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public EvalResult next() {
      if ( iterator == null ) throw new NoSuchElementException("No results available");
      OkHttpResult jerseyResult = iterator.next();
      EvalResult result = new OkHttpEvalResult(jerseyResult);
      return result;
    }

    @Override
    public void close() {
      if ( iterator != null ) iterator.close();
    }
  }
  public class OkHttpEvalResult implements EvalResult {
    private OkHttpResult content;

    public OkHttpEvalResult(OkHttpResult content) {
      this.content = content;
    }

    @Override
    public Format getFormat() {
      return content.getFormat();
    }

    @Override
    public EvalResult.Type getType() {
      String contentType = content.getHeader(HEADER_CONTENT_TYPE);
      String xPrimitive = content.getHeader(HEADER_X_PRIMITIVE);
      if ( contentType != null ) {
        if ( MIMETYPE_APPLICATION_JSON.equals(contentType) ) {
          if ( "null-node()".equals(xPrimitive) ) {
            return EvalResult.Type.NULL;
          } else {
            return EvalResult.Type.JSON;
          }
        } else if ( MIMETYPE_TEXT_JSON.equals(contentType) ) {
          return EvalResult.Type.JSON;
        } else if ( MIMETYPE_APPLICATION_XML.equals(contentType) ) {
          return EvalResult.Type.XML;
        } else if ( MIMETYPE_TEXT_XML.equals(contentType) ) {
          return EvalResult.Type.XML;
        } else if ( "application/x-unknown-content-type".equals(contentType) && "binary()".equals(xPrimitive) ) {
          return EvalResult.Type.BINARY;
        } else if ( "application/octet-stream".equals(contentType) && "node()".equals(xPrimitive) ) {
          return EvalResult.Type.BINARY;
        }
      }
      if ( xPrimitive == null ) {
        return EvalResult.Type.OTHER;
      } else if ( "string".equals(xPrimitive) || "untypedAtomic".equals(xPrimitive) ) {
        return EvalResult.Type.STRING;
      } else if ( "boolean".equals(xPrimitive) ) {
        return EvalResult.Type.BOOLEAN;
      } else if ( "attribute()".equals(xPrimitive) ) {
        return EvalResult.Type.ATTRIBUTE;
      } else if ( "comment()".equals(xPrimitive) ) {
        return EvalResult.Type.COMMENT;
      } else if ( "processing-instruction()".equals(xPrimitive) ) {
        return EvalResult.Type.PROCESSINGINSTRUCTION;
      } else if ( "text()".equals(xPrimitive) ) {
        return EvalResult.Type.TEXTNODE;
      } else if ( "binary()".equals(xPrimitive) ) {
        return EvalResult.Type.BINARY;
      } else if ( "duration".equals(xPrimitive) ) {
        return EvalResult.Type.DURATION;
      } else if ( "date".equals(xPrimitive) ) {
        return EvalResult.Type.DATE;
      } else if ( "anyURI".equals(xPrimitive) ) {
        return EvalResult.Type.ANYURI;
      } else if ( "hexBinary".equals(xPrimitive) ) {
        return EvalResult.Type.HEXBINARY;
      } else if ( "base64Binary".equals(xPrimitive) ) {
        return EvalResult.Type.BASE64BINARY;
      } else if ( "dateTime".equals(xPrimitive) ) {
        return EvalResult.Type.DATETIME;
      } else if ( "decimal".equals(xPrimitive) ) {
        return EvalResult.Type.DECIMAL;
      } else if ( "double".equals(xPrimitive) ) {
        return EvalResult.Type.DOUBLE;
      } else if ( "float".equals(xPrimitive) ) {
        return EvalResult.Type.FLOAT;
      } else if ( "gDay".equals(xPrimitive) ) {
        return EvalResult.Type.GDAY;
      } else if ( "gMonth".equals(xPrimitive) ) {
        return EvalResult.Type.GMONTH;
      } else if ( "gMonthDay".equals(xPrimitive) ) {
        return EvalResult.Type.GMONTHDAY;
      } else if ( "gYear".equals(xPrimitive) ) {
        return EvalResult.Type.GYEAR;
      } else if ( "gYearMonth".equals(xPrimitive) ) {
        return EvalResult.Type.GYEARMONTH;
      } else if ( "integer".equals(xPrimitive) ) {
        return EvalResult.Type.INTEGER;
      } else if ( "QName".equals(xPrimitive) ) {
        return EvalResult.Type.QNAME;
      } else if ( "time".equals(xPrimitive) ) {
        return EvalResult.Type.TIME;
      }
      return EvalResult.Type.OTHER;
    }

    @Override
    public <H extends AbstractReadHandle> H get(H handle) {
      if ( getType() == EvalResult.Type.NULL && handle instanceof StringHandle ) {
        return (H) ((StringHandle) handle).with(null);
      } else if ( getType() == EvalResult.Type.NULL && handle instanceof BytesHandle ) {
        return (H) ((BytesHandle) handle).with(null);
      } else {
        return content.getContent(handle);
      }
    }

    @Override
    public <T> T getAs(Class<T> as) {
      if ( getType() == EvalResult.Type.NULL ) return null;
      if (as == null) throw new IllegalArgumentException("class cannot be null");

      ContentHandle<T> readHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(as);
      if ( readHandle == null ) return null;
      readHandle = get(readHandle);
      if ( readHandle == null ) return null;
      return readHandle.get();
    }

    @Override
    public String getString() {
      if ( getType() == EvalResult.Type.NULL ) {
        return null;
      } else {
        return content.getContentAs(String.class);
      }
    }

    @Override
    public Number getNumber() {
      if      ( getType() == EvalResult.Type.DECIMAL ) return new BigDecimal(getString());
      else if ( getType() == EvalResult.Type.DOUBLE )  return Double.valueOf(getString());
      else if ( getType() == EvalResult.Type.FLOAT )   return Float.valueOf(getString());
        // MarkLogic integers can be much larger than Java integers, so we'll use Long instead
      else if ( getType() == EvalResult.Type.INTEGER ) return Long.valueOf(getString());
      else return new BigDecimal(getString());
    }

    @Override
    public Boolean getBoolean() {
      return Boolean.valueOf(getString());
    }

  }

  @Override
  public EvalResultIterator postEvalInvoke(
    RequestLogger reqlog, String code, String modulePath,
    ServerEvaluationCallImpl.Context context,
    Map<String, Object> variables, EditableNamespaceContext namespaces,
    Transaction transaction)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    String formUrlEncodedPayload;
    String path;
    RequestParameters params = new RequestParameters();
    try {
      StringBuffer sb = new StringBuffer();
      if ( context == ServerEvaluationCallImpl.Context.ADHOC_XQUERY ) {
        path = "eval";
        sb.append("xquery=");
        sb.append(URLEncoder.encode(code, "UTF-8"));
      } else if ( context == ServerEvaluationCallImpl.Context.ADHOC_JAVASCRIPT ) {
        path = "eval";
        sb.append("javascript=");
        sb.append(URLEncoder.encode(code, "UTF-8"));
      } else if ( context == ServerEvaluationCallImpl.Context.INVOKE ) {
        path = "invoke";
        sb.append("module=");
        sb.append(URLEncoder.encode(modulePath, "UTF-8"));
      } else {
        throw new IllegalStateException("Invalid eval context: " + context);
      }
      if ( variables != null && variables.size() > 0 ) {
        int i=0;
        for ( String name : variables.keySet() ) {
          String namespace = "";
          String localname = name;
          if ( namespaces != null ) {
            for ( String prefix : namespaces.keySet() ) {
              if ( name != null && prefix != null && name.startsWith(prefix + ":") ) {
                localname = name.substring(prefix.length() + 1);
                namespace = namespaces.get(prefix);
              }
            }
          }
          // set the variable namespace
          sb.append("&evn" + i + "=");
          sb.append(URLEncoder.encode(namespace, "UTF-8"));
          // set the variable localname
          sb.append("&evl" + i + "=");
          sb.append(URLEncoder.encode(localname, "UTF-8"));

          String value;
          String type = null;
          Object valueObject = variables.get(name);
          if ( valueObject == null ) {
            value = "null";
            type = "null-node()";
          } else if ( valueObject instanceof JacksonHandle ||
                      valueObject instanceof JacksonParserHandle )
          {
            JsonNode jsonNode = null;
            if ( valueObject instanceof JacksonHandle ) {
              jsonNode = ((JacksonHandle) valueObject).get();
            } else if ( valueObject instanceof JacksonParserHandle ) {
              jsonNode = ((JacksonParserHandle) valueObject).get().readValueAs(JsonNode.class);
            }
            value = jsonNode.toString();
            type = getJsonType(jsonNode);
          } else if ( valueObject instanceof AbstractWriteHandle ) {
            value = HandleAccessor.contentAsString((AbstractWriteHandle) valueObject);
            HandleImplementation valueBase = HandleAccessor.as((AbstractWriteHandle) valueObject);
            Format format = valueBase.getFormat();
            //TODO: figure out what type should be
            // I see element() and document-node() are two valid types
            if ( format == Format.XML ) {
              type = "document-node()";
            } else if ( format == Format.JSON ) {
              try ( JacksonParserHandle handle = new JacksonParserHandle() ) {
                JsonNode jsonNode = handle.getMapper().readTree(value);
                type = getJsonType(jsonNode);
              }
            } else if ( format == Format.TEXT ) {
              /* Comment next line until 32608 is resolved
              type = "text()";
              // until then, use the following line */
              type = "xs:untypedAtomic";
            } else if ( format == Format.BINARY ) {
              throw new UnsupportedOperationException("Binary format is not supported for variables");
            } else {
              throw new UnsupportedOperationException("Undefined format is not supported for variables. " +
                "Please set the format on your handle for variable " + name + ".");
            }
          } else if ( valueObject instanceof String ||
                      valueObject instanceof Boolean ||
                      valueObject instanceof Number )
          {
            value = valueObject.toString();
            // when we send type "xs:untypedAtomic" via XDBC, the server attempts to intelligently decide
            // how to cast the type
            type = "xs:untypedAtomic";
          } else {
            throw new IllegalArgumentException("Variable with name=" +
              name + " is of unsupported type" +
              valueObject.getClass() + ". Supported types are String, Boolean, Number, " +
              "or AbstractWriteHandle");
          }

          // set the variable value
          sb.append("&evv" + i + "=");
          sb.append(URLEncoder.encode(value, "UTF-8"));
          // set the variable type
          sb.append("&evt" + i + "=" + type);
          i++;
        }
      }
      formUrlEncodedPayload = sb.toString();
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("UTF-8 is unsupported", e);
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
    StringHandle input = new StringHandle(formUrlEncodedPayload)
      .withMimetype("application/x-www-form-urlencoded");
    return new OkHttpEvalResultIterator( postIteratedResourceImpl(DefaultOkHttpResultIterator::new,
      reqlog, path, transaction, params, input) );
  }

  private String getJsonType(JsonNode jsonNode) {
    if ( jsonNode instanceof ArrayNode ) {
      return "json:array";
    } else if ( jsonNode instanceof ObjectNode ) {
      return "json:object";
    } else {
      throw new IllegalArgumentException("When using JacksonHandle or " +
        "JacksonParserHandle with ServerEvaluationCall the content must be " +
        "a valid array or object");
    }
  }

  @Override
  public RESTServiceResultIterator postIteratedResource(RequestLogger reqlog,
                                                        String path, Transaction transaction, RequestParameters params, AbstractWriteHandle input,
                                                        String... outputMimetypes)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    return postIteratedResourceImpl(OkHttpServiceResultIterator::new,
      reqlog, path, transaction, params, input, outputMimetypes);
  }

  private <U extends OkHttpResultIterator> U postIteratedResourceImpl(
    ResultIteratorConstructor<U> constructor, final RequestLogger reqlog,
    final String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, String... outputMimetypes)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    if ( params == null ) params = new RequestParameters();
    if ( transaction != null ) params.add("txid", transaction.getTransactionId());
    HandleImplementation inputBase = HandleAccessor.checkHandle(input,
      "write");

    String inputMimetype = inputBase.getMimetype();
    boolean isResendable = inputBase.isResendable();

    Request.Builder requestBldr = makePostWebResource(path, params);
    requestBldr = setupRequest(requestBldr, inputMimetype, null);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Consumer<Boolean> resendableConsumer = new Consumer<Boolean>() {
      public void accept(Boolean resendable) {
        if (!isResendable) {
          checkFirstRequest();
          throw new ResourceNotResendableException(
            "Cannot retry request for " + path);
        }
      }
    };
    Function<Request.Builder, Response> doPostFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return doPost(reqlog, funcBuilder.header(HEADER_ACCEPT, multipartMixedWithBoundary()),
          inputBase.sendContent());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doPostFunction, resendableConsumer);
    int status = response.code();

    checkStatus(response, status, "apply", "resource", path,
      ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

    return makeResults(constructor, reqlog, "apply", "resource", response);
  }

  @Override
  public <W extends AbstractWriteHandle> RESTServiceResultIterator postIteratedResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, String... outputMimetypes)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    return postIteratedResourceImpl(OkHttpServiceResultIterator::new,
      reqlog, path, transaction, params, input, outputMimetypes);
  }

  private <W extends AbstractWriteHandle, U extends OkHttpResultIterator> U postIteratedResourceImpl(
    ResultIteratorConstructor<U> constructor, RequestLogger reqlog, String path, Transaction transaction,
    RequestParameters params, W[] input, String... outputMimetypes)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    if ( params == null ) params = new RequestParameters();
    if ( transaction != null ) params.add("txid", transaction.getTransactionId());
    Response response = null;
    int status = -1;
    long startTime = System.currentTimeMillis();
    int nextDelay = 0;
    int retry = 0;
    for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
      if (nextDelay > 0) {
        try {
          Thread.sleep(nextDelay);
        } catch (InterruptedException e) {
        }
      }

      MultipartBody.Builder multiPart = new MultipartBody.Builder();
      boolean hasStreamingPart = addParts(multiPart, reqlog, input);

      Request.Builder requestBldr = makePostWebResource(path, params);
      requestBldr = setupRequest(
        requestBldr,
        multiPart,
        multipartMixedWithBoundary());
      requestBldr = addTransactionScopedCookies(requestBldr, transaction);
      requestBldr = addTelemetryAgentId(requestBldr);

      response = doPost(requestBldr, multiPart, hasStreamingPart);
      status = response.code();

      if (transaction != null || !retryStatus.contains(status)) {
        if (isFirstRequest()) setFirstRequest(false);

        break;
      }

      String retryAfterRaw = response.header("Retry-After");
      closeResponse(response);

      if (hasStreamingPart) {
        throw new ResourceNotResendableException(
          "Cannot retry request for " + path);
      }

      int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
      nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
    }
    if (retryStatus.contains(status)) {
      checkFirstRequest();
      closeResponse(response);
      throw new FailedRetryException(
        "Service unavailable and maximum retry period elapsed: "+
          ((System.currentTimeMillis() - startTime) / 1000)+
          " seconds after "+retry+" retries");
    }

    checkStatus(response, status, "apply", "resource", path,
      ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

    return makeResults(constructor, reqlog, "apply", "resource", response);
  }

  @Override
  public <R extends AbstractReadHandle> R deleteResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    if ( params == null ) params = new RequestParameters();
    if ( transaction != null ) params.add("txid", transaction.getTransactionId());
    HandleImplementation outputBase = HandleAccessor.checkHandle(output,
      "read");

    String outputMimeType = null;
    Class as = null;
    if (outputBase != null) {
      outputMimeType = outputBase.getMimetype();
      as = outputBase.receiveAs();
    }
    Request.Builder requestBldr = makeDeleteWebResource(path, params);
    requestBldr = setupRequest(requestBldr, null, outputMimeType);
    requestBldr = addTransactionScopedCookies(requestBldr, transaction);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doDeleteFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return doDelete(funcBuilder);
      }
    };
    Response response = sendRequestWithRetry(requestBldr, (transaction == null), doDeleteFunction, null);
    int status = response.code();
    checkStatus(response, status, "delete", "resource", path,
      ResponseStatus.OK_OR_NO_CONTENT);

    if (as != null) {
      outputBase.receiveContent(makeResult(reqlog, "delete", "resource",
        response, as));
    } else {
      closeResponse(response);
    }

    return output;
  }

  private Request.Builder makeGetWebResource(String path,
                                             RequestParameters params, Object mimetype) {
    if (path == null) throw new IllegalArgumentException("Read with null path");

    logger.debug(String.format("Getting %s as %s", path, mimetype));

    return setupRequest(path, params);
  }

  private Response doGet(Request.Builder requestBldr) {
    requestBldr = requestBldr.get();
    Response response = sendRequestOnce(requestBldr);

    if (isFirstRequest()) setFirstRequest(false);

    return response;
  }

  private Request.Builder makePutWebResource(String path,
                                             RequestParameters params) {
    if (path == null) throw new IllegalArgumentException("Write with null path");

    logger.debug("Putting {}", path);

    return setupRequest(path, params);
  }

  private Response doPut(RequestLogger reqlog, Request.Builder requestBldr, Object value) {
    if (value == null) throw new IllegalArgumentException("Resource write with null value");

    if (isFirstRequest() && isStreaming(value)) makeFirstRequest(0);

    MediaType mediaType = makeType(requestBldr.build().header(HEADER_CONTENT_TYPE));
    if (value instanceof OutputStreamSender) {
      requestBldr = requestBldr.put(new StreamingOutputImpl((OutputStreamSender) value, reqlog, mediaType));
    } else {
      if (reqlog != null) {
        requestBldr = requestBldr.put(new ObjectRequestBody(reqlog.copyContent(value), mediaType));
      } else {
        requestBldr = requestBldr.put(new ObjectRequestBody(value, mediaType));
      }
    }
    Response response = sendRequestOnce(requestBldr);

    if (isFirstRequest()) setFirstRequest(false);

    return response;
  }

  private Response doPut(Request.Builder requestBldr,
                         MultipartBody.Builder multiPart, boolean hasStreamingPart) {
    if (isFirstRequest() && hasStreamingPart) makeFirstRequest(0);

    requestBldr = requestBldr.put(multiPart.build());
    Response response = sendRequestOnce(requestBldr);

    if (isFirstRequest()) setFirstRequest(false);

    return response;
  }

  private Request.Builder makePostWebResource(String path, RequestParameters params) {
    if (path == null) throw new IllegalArgumentException("Apply with null path");

    logger.debug("Posting {}", path);

    return setupRequest(path, params);
  }

  private Response doPost(RequestLogger reqlog, Request.Builder requestBldr, Object value) {
    if (isFirstRequest() && isStreaming(value)) {
      makeFirstRequest(0);
    }

    MediaType mediaType = makeType(requestBldr.build().header(HEADER_CONTENT_TYPE));
    if(value == null) {
      requestBldr = requestBldr.post(new ObjectRequestBody(null, null));
    } else if (value instanceof OutputStreamSender) {
      requestBldr = requestBldr
        .post(new StreamingOutputImpl((OutputStreamSender) value, reqlog, mediaType));
    } else {
      if (reqlog != null) {
        requestBldr = requestBldr.post(new ObjectRequestBody(reqlog.copyContent(value), mediaType));
      } else {
        requestBldr = requestBldr.post(new ObjectRequestBody(value, mediaType));
      }
    }
    Response response = sendRequestOnce(requestBldr);

    if (isFirstRequest()) setFirstRequest(false);

    return response;
  }

  private Response doPost(Request.Builder requestBldr,
                          MultipartBody.Builder multiPart, boolean hasStreamingPart) {
    if (isFirstRequest() && hasStreamingPart) makeFirstRequest(0);

    Response response = sendRequestOnce(requestBldr.post(multiPart.build()));

    if (isFirstRequest()) setFirstRequest(false);

    return response;
  }

  private Request.Builder makeDeleteWebResource(String path, RequestParameters params) {
    if (path == null) throw new IllegalArgumentException("Delete with null path");

    logger.debug("Deleting {}", path);

    return setupRequest(path, params);
  }

  private Response doDelete(Request.Builder requestBldr) {
    Response response = sendRequestOnce(requestBldr.delete().build());

    if (isFirstRequest()) setFirstRequest(false);

    return response;
  }


  private void addPointInTimeQueryParam(RequestParameters params, Object outputHandle) {
    HandleImplementation handleBase = HandleAccessor.as(outputHandle);
    if ( params != null && handleBase != null &&
      handleBase.getPointInTimeQueryTimestamp() != -1 )
    {
      logger.trace("param timestamp=[" + handleBase.getPointInTimeQueryTimestamp() + "]");
      params.add("timestamp", Long.toString(handleBase.getPointInTimeQueryTimestamp()));
    }
  }

  private Request.Builder addTransactionScopedCookies(Request.Builder requestBldr, Transaction transaction) {
    if ( transaction != null && transaction.getCookies() != null ) {
      if ( requestBldr == null ) {
        throw new MarkLogicInternalException("no requestBldr available to get the URI");
      }
      requestBldr = addCookies(
              requestBldr, transaction.getCookies(), ((TransactionImpl) transaction).getCreatedTimestamp()
              );
    }
    return requestBldr;
  }

  private Request.Builder addCookies(Request.Builder requestBldr, List<ClientCookie> cookies, Calendar creation) {
    HttpUrl uri = requestBldr.build().url();
    for (ClientCookie cookie : cookies) {
      // don't forward the cookie if it requires https and we're not using https
      if ( cookie.isSecure() && ! uri.isHttps() ) {
        continue;
      }
      // don't forward the cookie if it requires a path and we're using a different path
      if ( cookie.getPath() != null ) {
        String path = uri.encodedPath();
        if ( path == null || ! path.startsWith(cookie.getPath()) ) {
          continue;
        }
      }
      // don't forward the cookie if it requires a domain and we're using a different domain
      if ( cookie.getDomain() != null ) {
        if ( uri.host() == null || ! uri.host().equals(cookie.getDomain()) ) {
          continue;
        }
      }
      // don't forward the cookie if it has 0 for max age
      if ( cookie.getMaxAge() == 0 ) {
        continue;
      }
      // TODO: determine if we need handling for MIN_VALUE
      // else if ( cookie.getMaxAge() == Integer.MIN_VALUE ) {
      // don't forward the cookie if it has a max age and we're past the max age
      if ( creation != null && cookie.getMaxAge() > 0 ) {
          int currentAge = (int) TimeUnit.MILLISECONDS.toSeconds(
                System.currentTimeMillis() - creation.getTimeInMillis()
          );
          if ( currentAge > cookie.getMaxAge() ) {
            logger.warn(
                  cookie.getName()+" cookie expired after "+cookie.getMaxAge()+" seconds: "+cookie.getValue()
            );
          continue;
        }
      }
      requestBldr = requestBldr.addHeader(HEADER_COOKIE, cookie.toString());
    }
    return requestBldr;
  }

  private Request.Builder addTelemetryAgentId(Request.Builder requestBldr) {
    if ( requestBldr == null ) throw new MarkLogicInternalException("no requestBldr available to set ML-Agent-ID header");
    return requestBldr.header("ML-Agent-ID", "java");
  }

  private <W extends AbstractWriteHandle> boolean addParts(
    MultipartBody.Builder multiPart, RequestLogger reqlog, W[] input)
  {
    return addParts(multiPart, reqlog, null, input, null);
  }

  private <W extends AbstractWriteHandle> boolean addParts(
    MultipartBody.Builder multiPart, RequestLogger reqlog, String[] mimetypes, W[] input)
  {
    return addParts(multiPart, reqlog, null, input, null);
  }

  private <W extends AbstractWriteHandle> boolean addParts(
    MultipartBody.Builder multiPart, RequestLogger reqlog, String[] mimetypes,
    W[] input, Map<String, List<String>>[] headers)
  {
    if (mimetypes != null && mimetypes.length != input.length) {
      throw new IllegalArgumentException(
        "Mismatch between count of mimetypes and input");
    }
    if (headers != null && headers.length != input.length) {
      throw new IllegalArgumentException(
        "Mismatch between count of headers and input");
    }

    multiPart.setType(MediaType.parse(MIMETYPE_MULTIPART_MIXED));

    boolean hasStreamingPart = false;
    for (int i = 0; i < input.length; i++) {
      AbstractWriteHandle handle = input[i];
      HandleImplementation handleBase = HandleAccessor.checkHandle(
        handle, "write");

      if (!hasStreamingPart) {
        hasStreamingPart = !handleBase.isResendable();
      }

      Object value = handleBase.sendContent();

      String inputMimetype = null;
      if ( mimetypes != null ) inputMimetype = mimetypes[i];
      if ( inputMimetype == null && headers != null ) {
        inputMimetype = getHeaderMimetype(getHeader(headers[i], HEADER_CONTENT_TYPE));
      }
      if ( inputMimetype == null ) inputMimetype = handleBase.getMimetype();

      MediaType mediaType = (inputMimetype != null)
                              ? MediaType.parse(inputMimetype)
                              : MediaType.parse(MIMETYPE_WILDCARD);

      Headers.Builder partHeaders = new Headers.Builder();
      if ( headers != null ) {
        for ( String key : headers[i].keySet() ) {
          // OkHttp wants me to skip the Content-Type header
          if ( HEADER_CONTENT_TYPE.equalsIgnoreCase(key) ) continue;
          for ( String headerValue : headers[i].get(key) ) {
            partHeaders.add(key, headerValue);
          }
        }
      }

      Part bodyPart = null;
      if (value instanceof OutputStreamSender) {
        bodyPart = Part.create(partHeaders.build(), new StreamingOutputImpl(
          (OutputStreamSender) value, reqlog, mediaType));
      } else {
        if (reqlog != null) {
          bodyPart = Part.create(partHeaders.build(), new ObjectRequestBody(reqlog.copyContent(value), mediaType));
        } else {
          bodyPart = Part.create(partHeaders.build(), new ObjectRequestBody(value, mediaType));
        }
      }

      multiPart = multiPart.addPart(bodyPart);
    }

    return hasStreamingPart;
  }

  private String multipartMixedWithBoundary() {
    return MIMETYPE_MULTIPART_MIXED + "; boundary=" + UUID.randomUUID().toString();
  }

  private Request.Builder setupRequest(HttpUrl requestUri, String path, RequestParameters params) {
    if ( requestUri == null ) throw new IllegalArgumentException("request URI cannot be null");
    if ( path == null ) throw new IllegalArgumentException("path cannot be null");
    if ( path.startsWith("/") ) path = path.substring(1);
    HttpUrl.Builder uri = requestUri.resolve(path).newBuilder();
    if ( params != null ) {
      for ( String key : params.keySet() ) {
        for ( String value : params.get(key) ) {
          uri.addQueryParameter(key, value);
        }
      }
    }
    if ( database != null && ! path.startsWith("config/") ) {
      uri.addQueryParameter("database", database);
    }
    Request.Builder request = new Request.Builder()
        .url(uri.build());
    return request;
  }

  private Request.Builder setupRequest(String path, RequestParameters params) {
    return setupRequest(baseUri, path, params);
  }

  private Request.Builder setupRequest(Request.Builder requestBldr,
                                       Object inputMimetype, Object outputMimetype) {

    if (inputMimetype == null) {
    } else if (inputMimetype instanceof String) {
      requestBldr = requestBldr.header(HEADER_CONTENT_TYPE, (String) inputMimetype);
    } else if (inputMimetype instanceof MediaType) {
      requestBldr = requestBldr.header(HEADER_CONTENT_TYPE, inputMimetype.toString());
    } else if (inputMimetype instanceof MultipartBody.Builder) {
      requestBldr = requestBldr.header(HEADER_CONTENT_TYPE, MIMETYPE_MULTIPART_MIXED);
      logger.debug("Sending multipart for {}", requestBldr.build().url().encodedPath());
    } else {
      throw new IllegalArgumentException(
        "Unknown input mimetype specifier "
          + inputMimetype.getClass().getName());
    }

    if (outputMimetype == null) {
    } else if (outputMimetype instanceof String) {
      requestBldr = requestBldr.header(HEADER_ACCEPT, (String) outputMimetype);
    } else if (outputMimetype instanceof MediaType) {
      requestBldr = requestBldr.header(HEADER_ACCEPT, outputMimetype.toString());
    } else {
      throw new IllegalArgumentException(
        "Unknown output mimetype specifier "
          + outputMimetype.getClass().getName());
    }

    return requestBldr;
  }

  private Request.Builder setupRequest(String path, RequestParameters params, Object inputMimetype,
                                       Object outputMimetype)
  {
    return setupRequest(setupRequest(path, params), inputMimetype, outputMimetype);
  }

  private void checkStatus(Response response, int status, String operation, String entityType,
                           String path, ResponseStatus expected)
  {
    if (!expected.isExpected(status)) {
      FailedRequest failure = extractErrorFields(response);
      if (status == STATUS_NOT_FOUND) {
        throw new ResourceNotFoundException("Could not " + operation
          + " " + entityType + " at " + path,
          failure);
      }
      if ("RESTAPI-CONTENTNOVERSION".equals(failure.getMessageCode())) {
        throw new FailedRequestException("Content version required to " +
          operation + " " + entityType + " at " + path, failure);
      } else if (status == STATUS_FORBIDDEN) {
        throw new ForbiddenUserException("User is not allowed to "
          + operation + " " + entityType + " at " + path,
          failure);
      }
      throw new FailedRequestException("failed to " + operation + " "
        + entityType + " at " + path + ": "
        + getReasonPhrase(response), failure);
    }
  }

  private <T> T makeResult(RequestLogger reqlog, String operation,
                           String entityType, Response response, Class<T> as) {
    if (as == null) {
      return null;
    }

    logRequest(reqlog, "%s for %s", operation, entityType);

    ResponseBody body = response.body();
    T entity = body.contentLength() != 0 ? getEntity(body, as) : null;
    if (entity == null || (as != InputStream.class && as != Reader.class)) {
      closeResponse(response);
    }

    return (reqlog != null) ? reqlog.copyContent(entity) : entity;
  }

  private <U extends OkHttpResultIterator> U makeResults(
    ResultIteratorConstructor<U> constructor, RequestLogger reqlog,
    String operation, String entityType, Response response) {
    if ( response == null ) return null;
    ResponseBody body = response.body();
    MimeMultipart entity = body.contentLength() != 0 ?
      getEntity(body, MimeMultipart.class) : null;

    List<BodyPart> partList = getPartList(entity);
    Closeable closeable = response;
    return makeResults(constructor, reqlog, operation, entityType, partList, response, closeable);
  }

  private <U extends OkHttpResultIterator> U makeResults(
    ResultIteratorConstructor<U> constructor, RequestLogger reqlog,
    String operation, String entityType, List<BodyPart> partList, Response response,
    Closeable closeable) {
    logRequest(reqlog, "%s for %s", operation, entityType);

    if ( response == null ) return null;

    try {
      OkHttpResultIterator result = constructor.construct(reqlog, partList, closeable);
      Headers headers = response.headers();
      if (headers.get(HEADER_VND_MARKLOGIC_START) != null) {
        result.setStart(Long.parseLong(headers.get(HEADER_VND_MARKLOGIC_START)));
      }
      if (headers.get(HEADER_VND_MARKLOGIC_PAGELENGTH) != null) {
        result.setPageSize(Long.parseLong(headers.get(HEADER_VND_MARKLOGIC_PAGELENGTH)));
      }
      if (headers.get(HEADER_VND_MARKLOGIC_RESULT_ESTIMATE) != null) {
        result.setTotalSize(Long.parseLong(headers.get(HEADER_VND_MARKLOGIC_RESULT_ESTIMATE)));
      }
      return (U) result;
    } catch (Throwable t) {
      throw new MarkLogicInternalException("Error constructing iterator", t);
    }
  }

  private boolean isStreaming(Object value) {
    return !(value instanceof String || value instanceof byte[] || value instanceof File);
  }

  private void logRequest(RequestLogger reqlog, String message,
                          Object... params) {
    if (reqlog == null) return;

    PrintStream out = reqlog.getPrintStream();
    if (out == null) return;

    if (params == null || params.length == 0) {
      out.println(message);
    } else {
      out.format(message, params);
      out.println();
    }
  }

  private String stringJoin(Collection collection, String separator,
                            String defaultValue) {
    if (collection == null || collection.size() == 0) return defaultValue;

    StringBuilder builder = null;
    for (Object value : collection) {
      if (builder == null) {
        builder = new StringBuilder();
      } else {
        builder.append(separator);
      }

      builder.append(value);
    }

    return (builder != null) ? builder.toString() : null;
  }

  private int calculateDelay(Random rand, int i) {
    int min   =
      (i  > 6) ? DELAY_CEILING :
        (i == 0) ? DELAY_FLOOR   :
          DELAY_FLOOR + (1 << i) * DELAY_MULTIPLIER;
    int range =
      (i >  6) ? DELAY_FLOOR          :
        (i == 0) ? 2 * DELAY_MULTIPLIER :
          (i == 6) ? DELAY_CEILING - min  :
            (1 << i) * DELAY_MULTIPLIER;
    return min + randRetry.nextInt(range);
  }

  static class OkHttpResult {
    private RequestLogger reqlog;
    private BodyPart part;
    private boolean extractedHeaders = false;
    private String uri;
    private RequestParameters headers = new RequestParameters();
    private Format format;
    private String mimetype;
    private long length;

    OkHttpResult(RequestLogger reqlog, BodyPart part) {
      this.reqlog = reqlog;
      this.part = part;
    }

    public <R extends AbstractReadHandle> R getContent(R handle) {
      if (part == null) throw new IllegalStateException("Content already retrieved");

      HandleImplementation handleBase = HandleAccessor.as(handle);

      extractHeaders();
      updateFormat(handleBase, format);
      updateMimetype(handleBase, mimetype);
      updateLength(handleBase, length);

      try {
        Object contentEntity = getEntity(part, handleBase.receiveAs());
        handleBase.receiveContent((reqlog != null) ? reqlog.copyContent(contentEntity) : contentEntity);

        return handle;
      } finally {
        part = null;
        reqlog = null;
      }
    }

    public <T> T getContentAs(Class<T> as) {
      ContentHandle<T> readHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(as);
      readHandle = getContent(readHandle);
      if ( readHandle == null ) return null;
      return readHandle.get();
    }

    public String getUri() {
      extractHeaders();
      return uri;
    }
    public Format getFormat() {
      extractHeaders();
      return format;
    }

    public String getMimetype() {
      extractHeaders();
      return mimetype;
    }

    public long getLength() {
      extractHeaders();
      return length;
    }

    public String getHeader(String name) {
      extractHeaders();
      List<String> values = headers.get(name);
      if ( values != null && values.size() > 0 ) {
        return values.get(0);
      }
      return null;
    }

    public Map<String,List<String>> getHeaders() {
      extractHeaders();
      return headers.getMap();
    }

    private void extractHeaders() {
      if (part == null || extractedHeaders) return;
      try {
        for ( Enumeration<Header> e = part.getAllHeaders(); e.hasMoreElements(); ) {
          Header header = e.nextElement();
          headers.put(header.getName(), header.getValue());
        }
        format = getHeaderFormat(part);
        mimetype = getHeaderMimetype(OkHttpServices.getHeader(part, HEADER_CONTENT_TYPE));
        length = getHeaderLength(OkHttpServices.getHeader(part, HEADER_CONTENT_LENGTH));
        uri = getHeaderUri(part);
        extractedHeaders = true;
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      }
    }
  }

  static class OkHttpServiceResult extends OkHttpResult implements RESTServices.RESTServiceResult {
    OkHttpServiceResult(RequestLogger reqlog, BodyPart part) {
      super(reqlog, part);
    }
  }

  static abstract class OkHttpResultIterator<T extends OkHttpResult> {
    private RequestLogger reqlog;
    private Iterator<BodyPart> partQueue;
    private long start = -1;
    private long size = -1;
    private long pageSize = -1;
    private long totalSize = -1;
    private Closeable closeable;

    OkHttpResultIterator(RequestLogger reqlog, List<BodyPart> partList, Closeable closeable) {
      this.reqlog = reqlog;
      if (partList != null && partList.size() > 0) {
        this.size = partList.size();
        this.partQueue = new ConcurrentLinkedQueue<>(
          partList).iterator();
      } else {
        this.size = 0;
      }
      this.closeable = closeable;
    }

    public long getStart() {
      return start;
    }

    public OkHttpResultIterator<T> setStart(long start) {
      this.start = start;
      return this;
    }

    public long getSize() {
      return size;
    }

    public OkHttpResultIterator<T> setSize(long size) {
      this.size = size;
      return this;
    }

    public long getPageSize() {
      return pageSize;
    }

    public OkHttpResultIterator<T> setPageSize(long pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public long getTotalSize() {
      return totalSize;
    }

    public OkHttpResultIterator<T> setTotalSize(long totalSize) {
      this.totalSize = totalSize;
      return this;
    }

    public boolean hasNext() {
      if (partQueue == null) return false;
      boolean hasNext = partQueue.hasNext();
      return hasNext;
    }

    public T next() {
      if (partQueue == null) return null;

      try {
        return constructNext(reqlog, partQueue.next());
      } catch (Throwable t) {
        throw new IllegalStateException("Error instantiating iterated result", t);
      }
    }

    abstract T constructNext(RequestLogger logger, BodyPart part);

    public void remove() {
      if (partQueue == null) return;
      partQueue.remove();
      if (!partQueue.hasNext()) close();
    }

    public void close() {
      partQueue = null;
      reqlog = null;
      if ( closeable != null ) {
        try {
          closeable.close();
        } catch (IOException e) {
          throw new MarkLogicIOException(e);
        }
      }
    }
  }

  static class OkHttpServiceResultIterator
    extends OkHttpResultIterator<OkHttpServiceResult>
    implements RESTServiceResultIterator
  {
    OkHttpServiceResultIterator(RequestLogger reqlog,
                                       List<BodyPart> partList, Closeable closeable) {
      super(reqlog, partList, closeable);
    }
    OkHttpServiceResult constructNext(RequestLogger logger, BodyPart part) {
      return new OkHttpServiceResult(logger, part);
    }
  }

  static class DefaultOkHttpResultIterator
    extends OkHttpResultIterator<OkHttpResult>
    implements Iterator<OkHttpResult>
  {
    DefaultOkHttpResultIterator(RequestLogger reqlog,
                                       List<BodyPart> partList, Closeable closeable) {
      super(reqlog, partList, closeable);
    }
    OkHttpResult constructNext(RequestLogger logger, BodyPart part) {
      return new OkHttpResult(logger, part);
    }
  }

  static class OkHttpDocumentRecord implements DocumentRecord {
    private OkHttpResult content;
    private OkHttpResult metadata;

    OkHttpDocumentRecord(OkHttpResult content, OkHttpResult metadata) {
      this.content = content;
      this.metadata = metadata;
    }

    OkHttpDocumentRecord(OkHttpResult content) {
      this.content = content;
    }

    @Override
    public String getUri() {
      if ( content == null && metadata != null ) {
        return metadata.getUri();
      } else if ( content != null ) {
        return content.getUri();
      } else {
        throw new IllegalStateException("Missing both content and metadata!");
      }
    }

    @Override
    public DocumentDescriptor getDescriptor() {
      if (content == null) {
        throw new IllegalStateException("getDescriptor() called when no content is available");
      }
      DocumentDescriptorImpl descriptor = new DocumentDescriptorImpl(getUri(), false);
      updateFormat(descriptor, getFormat());
      updateMimetype(descriptor, getMimetype());
      updateLength(descriptor, getLength());
      updateVersion(descriptor, content.getHeader(HEADER_ETAG));
      return descriptor;
    }

    @Override
    public Format getFormat() {
      if (content == null) {
        throw new IllegalStateException("getFormat() called when no content is available");
      }
      return content.getFormat();
    }

    @Override
    public String getMimetype() {
      if (content == null) {
        throw new IllegalStateException("getMimetype() called when no content is available");
      }
      return content.getMimetype();
    }

    @Override
    public long getLength() {
      if (content == null) {
        throw new IllegalStateException("getLenth() called when no content is available");
      }
      return content.getLength();
    }

    @Override
    public <T extends DocumentMetadataReadHandle> T getMetadata(T metadataHandle) {
      if (metadata == null) {
        throw new IllegalStateException("getMetadata called when no metadata is available");
      }
      return metadata.getContent(metadataHandle);
    }

    @Override
    public <T> T getMetadataAs(Class<T> as) {
      if ( as == null ) {
        throw new IllegalStateException("getMetadataAs cannot accept null");
      }
      return metadata.getContentAs(as);
    }

    @Override
    public <T extends AbstractReadHandle> T getContent(T contentHandle) {
      if ( content == null ) {
        throw new IllegalStateException("getContent called when no content is available");
      }
      return content.getContent(contentHandle);
    }

    @Override
    public <T> T getContentAs(Class<T> as) {
      if ( as == null ) {
        throw new IllegalStateException("getContentAs cannot accept null");
      }
      return content.getContentAs(as);
    }
  }

  @Override
  public OkHttpClient getClientImplementation() {
    if (client == null) return null;
    return client;
  }

  public void setClientImplementation(OkHttpClient client) {
    this.client = client;
  }

  @Override
  public <T> T suggest(Class<T> as, SuggestDefinition suggestionDef) {
    RequestParameters params = new RequestParameters();

    String suggestCriteria = suggestionDef.getStringCriteria();
    String[] queries = suggestionDef.getQueryStrings();
    String optionsName = suggestionDef.getOptionsName();
    Integer limit = suggestionDef.getLimit();
    Integer cursorPosition = suggestionDef.getCursorPosition();

    if (suggestCriteria != null) {
      params.add("partial-q", suggestCriteria);
    }
    if (optionsName != null) {
      params.add("options", optionsName);
    }
    if (limit != null) {
      params.add("limit", Long.toString(limit));
    }
    if (cursorPosition != null) {
      params.add("cursor-position", Long.toString(cursorPosition));
    }
    if (queries != null) {
      for (String stringQuery : queries) {
        params.add("q", stringQuery);
      }
    }
    Request.Builder requestBldr = null;
    requestBldr = setupRequest("suggest", params, null, MIMETYPE_APPLICATION_XML);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return sendRequestOnce(funcBuilder.get().build());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, doGetFunction, null);
    int status = response.code();
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException(
        "User is not allowed to get suggestions",
        extractErrorFields(response));
    }
    if (status != STATUS_OK) {
      throw new FailedRequestException("Suggest call failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    ResponseBody body = response.body();
    T entity = body.contentLength() != 0 ? getEntity(body, as) : null;
    if (entity == null || (as != InputStream.class && as != Reader.class)) {
      closeResponse(response);
    }

    return entity;
  }

  @Override
  public InputStream match(StructureWriteHandle document,
                           String[] candidateRules, String mimeType, ServerTransform transform) {
    RequestParameters params = new RequestParameters();

    HandleImplementation baseHandle = HandleAccessor.checkHandle(document, "match");
    if (candidateRules != null) {
      for (String candidateRule : candidateRules) {
        params.add("rule", candidateRule);
      }
    }
    if (transform != null) {
      transform.merge(params);
    }
    Request.Builder requestBldr = null;
    requestBldr = setupRequest("alert/match", params, MIMETYPE_APPLICATION_XML, mimeType);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doPostFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return doPost(null, funcBuilder, baseHandle.sendContent());
      }
    };
    Response response = sendRequestWithRetry(requestBldr, doPostFunction, null);
    int status = response.code();

    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to match",
        extractErrorFields(response));
    }
    if (status != STATUS_OK) {
      throw new FailedRequestException("match failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    ResponseBody body = response.body();
    InputStream entity = body.contentLength() != 0 ?
      getEntity(body, InputStream.class) : null;
    if (entity == null) closeResponse(response);

    return entity;
  }

  @Override
  public InputStream match(QueryDefinition queryDef,
                           long start, long pageLength, String[] candidateRules, ServerTransform transform) {
    if (queryDef == null) {
      throw new IllegalArgumentException("Cannot match null query");
    }

    RequestParameters params = new RequestParameters();

    if (start > 1) {
      params.add("start", Long.toString(start));
    }
    if (pageLength >= 0) {
      params.add("pageLength", Long.toString(pageLength));
    }
    if (transform != null) {
      transform.merge(params);
    }
    if (candidateRules.length > 0) {
      for (String candidateRule : candidateRules) {
        params.add("rule", candidateRule);
      }
    }

    if (queryDef.getOptionsName() != null) {
      params.add("options", queryDef.getOptionsName());
    }

    Request.Builder requestBldr = null;
    String structure = null;
    HandleImplementation baseHandle = null;

    String text = null;
    if (queryDef instanceof StringQueryDefinition) {
      text = ((StringQueryDefinition) queryDef).getCriteria();
    } else if (queryDef instanceof StructuredQueryDefinition) {
      text = ((StructuredQueryDefinition) queryDef).getCriteria();
    } else if (queryDef instanceof RawStructuredQueryDefinition) {
      text = ((RawStructuredQueryDefinition) queryDef).getCriteria();
    }
    if (text != null) {
      params.add("q", text);
    }
    if (queryDef instanceof StructuredQueryDefinition) {
      structure = ((StructuredQueryDefinition) queryDef).serialize();

      logger.debug("Searching with structured query {}", structure);

      requestBldr = setupRequest("alert/match", params, MIMETYPE_APPLICATION_XML, MIMETYPE_APPLICATION_XML);
    } else if (queryDef instanceof RawQueryDefinition) {
      StructureWriteHandle handle = ((RawQueryDefinition) queryDef).getHandle();
      baseHandle = HandleAccessor.checkHandle(handle, "match");

      logger.debug("Searching with raw query");

      requestBldr = setupRequest("alert/match", params, MIMETYPE_APPLICATION_XML, MIMETYPE_APPLICATION_XML);
    } else if (queryDef instanceof StringQueryDefinition) {
      logger.debug("Searching with string query [{}]", text);

      requestBldr = setupRequest("alert/match", params, null, MIMETYPE_APPLICATION_XML);
    } else {
      throw new UnsupportedOperationException("Cannot match with "
        + queryDef.getClass().getName());
    }
    requestBldr = addTelemetryAgentId(requestBldr);

    MediaType mediaType = makeType(requestBldr.build().header(HEADER_CONTENT_TYPE));

    Response response = null;
    int status = -1;
    long startTime = System.currentTimeMillis();
    int nextDelay = 0;
    int retry = 0;
    for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
      if (nextDelay > 0) {
        try {
          Thread.sleep(nextDelay);
        } catch (InterruptedException e) {
        }
      }

      if (queryDef instanceof StructuredQueryDefinition) {
        response = doPost(null, requestBldr, structure);
      } else if (queryDef instanceof RawQueryDefinition) {
        response = doPost(null, requestBldr, baseHandle.sendContent());
      } else if (queryDef instanceof StringQueryDefinition) {
        response = sendRequestOnce(requestBldr.get());
      } else {
        throw new UnsupportedOperationException("Cannot match with "
          + queryDef.getClass().getName());
      }
      status = response.code();

      if (!retryStatus.contains(status)) {
        if (isFirstRequest()) setFirstRequest(false);

        break;
      }

      String retryAfterRaw = response.header("Retry-After");
      int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;

      closeResponse(response);

      nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
    }
    if (retryStatus.contains(status)) {
      checkFirstRequest();
      closeResponse(response);
      throw new FailedRetryException(
        "Service unavailable and maximum retry period elapsed: "+
          ((System.currentTimeMillis() - startTime) / 1000)+
          " seconds after "+retry+" retries");
    }
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to match",
        extractErrorFields(response));
    }
    if (status != STATUS_OK) {
      throw new FailedRequestException("match failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    ResponseBody body = response.body();
    InputStream entity = body.contentLength() != 0 ?
      getEntity(body, InputStream.class) : null;
    if (entity == null) closeResponse(response);

    return entity;
  }

  @Override
  public InputStream match(String[] docIds, String[] candidateRules, ServerTransform transform) {
    RequestParameters params = new RequestParameters();

    if (docIds.length > 0) {
      for (String docId : docIds) {
        params.add("uri", docId);
      }
    }
    if (candidateRules.length > 0) {
      for (String candidateRule : candidateRules) {
        params.add("rule", candidateRule);
      }
    }
    if (transform != null) {
      transform.merge(params);
    }
    Request.Builder requestBldr = setupRequest("alert/match", params, MIMETYPE_APPLICATION_XML, MIMETYPE_APPLICATION_XML);
    requestBldr = addTelemetryAgentId(requestBldr);

    Function<Request.Builder, Response> doGetFunction = new Function<Request.Builder, Response>() {
      public Response apply(Request.Builder funcBuilder) {
        return doGet(funcBuilder);
      }
    };
    Response response = sendRequestWithRetry(requestBldr, doGetFunction, null);
    int status = response.code();
    if (status == STATUS_FORBIDDEN) {
      throw new ForbiddenUserException("User is not allowed to match",
        extractErrorFields(response));
    }
    if (status != STATUS_OK) {
      throw new FailedRequestException("match failed: "
        + getReasonPhrase(response), extractErrorFields(response));
    }

    ResponseBody body = response.body();
    InputStream entity = body.contentLength() != 0 ?
      getEntity(body, InputStream.class) : null;
    if (entity == null) closeResponse(response);

    return entity;
  }

  private void addGraphUriParam(RequestParameters params, String uri) {
    if ( uri == null || uri.equals(GraphManager.DEFAULT_GRAPH) ) {
      params.add("default", "");
    } else {
      params.add("graph", uri);
    }
  }

  private void addPermsParams(RequestParameters params, GraphPermissions permissions) {
    if ( permissions != null ) {
      for ( Map.Entry<String,Set<Capability>> entry : permissions.entrySet() ) {
        if ( entry.getValue() != null ) {
          for ( Capability capability : entry.getValue() ) {
            params.add("perm:" + entry.getKey(), capability.toString().toLowerCase());
          }
        }
      }
    }
  }

  @Override
  public <R extends AbstractReadHandle> R getGraphUris(RequestLogger reqlog, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    return getResource(reqlog, "graphs", null, null, output);
  }

  @Override
  public <R extends AbstractReadHandle> R readGraph(RequestLogger reqlog, String uri, R output,
                                                    Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    addGraphUriParam(params, uri);
    return getResource(reqlog, "graphs", transaction, params, output);
  }

  @Override
  public void writeGraph(RequestLogger reqlog, String uri,
                         AbstractWriteHandle input, GraphPermissions permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    addGraphUriParam(params, uri);
    addPermsParams(params, permissions);
    putResource(reqlog, "graphs", transaction, params, input, null);
  }

  @Override
  public void writeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    putResource(reqlog, "graphs", transaction, params, input, null);
  }

  @Override
  public void mergeGraph(RequestLogger reqlog, String uri,
                         AbstractWriteHandle input, GraphPermissions permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    addGraphUriParam(params, uri);
    addPermsParams(params, permissions);
    postResource(reqlog, "graphs", transaction, params, input, null);
  }

  @Override
  public void mergeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    postResource(reqlog, "graphs", transaction, params, input, null);
  }

  @Override
  public <R extends AbstractReadHandle> R getPermissions(RequestLogger reqlog, String uri,
                                                         R output,Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    addGraphUriParam(params, uri);
    params.add("category", "permissions");
    return getResource(reqlog, "graphs", transaction, params, output);
  }

  @Override
  public void deletePermissions(RequestLogger reqlog, String uri, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    addGraphUriParam(params, uri);
    params.add("category", "permissions");
    deleteResource(reqlog, "graphs", transaction, params, null);
  }

  @Override
  public void writePermissions(RequestLogger reqlog, String uri,
                               AbstractWriteHandle permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    addGraphUriParam(params, uri);
    params.add("category", "permissions");
    putResource(reqlog, "graphs", transaction, params, permissions, null);
  }

  @Override
  public void mergePermissions(RequestLogger reqlog, String uri,
                               AbstractWriteHandle permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    addGraphUriParam(params, uri);
    params.add("category", "permissions");
    postResource(reqlog, "graphs", transaction, params, permissions, null);
  }

  @Override
  public Object deleteGraph(RequestLogger reqlog, String uri, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException
  {
    RequestParameters params = new RequestParameters();
    addGraphUriParam(params, uri);
    return deleteResource(reqlog, "graphs", transaction, params, null);

  }

  @Override
  public void deleteGraphs(RequestLogger reqlog, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException
  {
    deleteResource(reqlog, "graphs", transaction, null, null);
  }

  @Override
  public <R extends AbstractReadHandle> R getThings(RequestLogger reqlog, String[] iris, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    if ( iris == null ) throw new IllegalArgumentException("iris cannot be null");
    RequestParameters params = new RequestParameters();
    for ( String iri : iris ) {
      params.add("iri", iri);
    }
    return getResource(reqlog, "graphs/things", null, params, output);
  }

  @Override
  public <R extends AbstractReadHandle> R executeSparql(RequestLogger reqlog,
                                                        SPARQLQueryDefinition qdef, R output, long start, long pageLength,
                                                        Transaction transaction, boolean isUpdate)
  {
    if ( qdef == null )   throw new IllegalArgumentException("qdef cannot be null");
    if ( output == null ) throw new IllegalArgumentException("output cannot be null");
    RequestParameters params = new RequestParameters();
    if (start > 1)             params.add("start",      Long.toString(start));
    if (pageLength >= 0)       params.add("pageLength", Long.toString(pageLength));
    if (qdef.getOptimizeLevel() >= 0) {
      params.add("optimize", Integer.toString(qdef.getOptimizeLevel()));
    }
    if (qdef.getCollections() != null ) {
      for ( String collection : qdef.getCollections() ) {
        params.add("collection", collection);
      }
    }
    addPermsParams(params, qdef.getUpdatePermissions());
    String sparql = qdef.getSparql();
    SPARQLBindings bindings = qdef.getBindings();
    for ( Map.Entry<String,List<SPARQLBinding>> entry : bindings.entrySet() ) {
      String paramName = "bind:" + entry.getKey();
      String typeOrLang = "";
      for ( SPARQLBinding binding : entry.getValue() ) {
        if ( binding.getDatatype() != null ) {
          typeOrLang = ":" + binding.getDatatype();
        } else if ( binding.getLanguageTag() != null ) {
          typeOrLang = "@" + binding.getLanguageTag().toLanguageTag();
        }
        params.add(paramName + typeOrLang, binding.getValue());
      }
    }
    QueryDefinition constrainingQuery = qdef.getConstrainingQueryDefinition();
    StructureWriteHandle input;
    if ( constrainingQuery != null ) {
      if (qdef.getOptionsName()!= null && qdef.getOptionsName().length() > 0) {
        params.add("options", qdef.getOptionsName());
      }
      if ( constrainingQuery instanceof RawCombinedQueryDefinition ) {
        CombinedQueryDefinition combinedQdef = new CombinedQueryBuilderImpl().combine(
          (RawCombinedQueryDefinition) constrainingQuery, null, null, sparql);
        Format format = combinedQdef.getFormat();
        input = new StringHandle(combinedQdef.serialize()).withFormat(format);
      } else if ( constrainingQuery instanceof RawStructuredQueryDefinition ) {
        CombinedQueryDefinition combinedQdef = new CombinedQueryBuilderImpl().combine(
          (RawStructuredQueryDefinition) constrainingQuery, null, null, sparql);
        Format format = combinedQdef.getFormat();
        input = new StringHandle(combinedQdef.serialize()).withFormat(format);
      } else if ( constrainingQuery instanceof StringQueryDefinition ||
                  constrainingQuery instanceof StructuredQueryDefinition )
      {
        String stringQuery = constrainingQuery instanceof StringQueryDefinition ?
          ((StringQueryDefinition) constrainingQuery).getCriteria() : null;
        StructuredQueryDefinition structuredQuery =
          constrainingQuery instanceof StructuredQueryDefinition ?
            (StructuredQueryDefinition) constrainingQuery : null;
        CombinedQueryDefinition combinedQdef = new CombinedQueryBuilderImpl().combine(
          structuredQuery, null, stringQuery, sparql);
        input = new StringHandle(combinedQdef.serialize()).withMimetype(MIMETYPE_APPLICATION_XML);
      } else {
        throw new IllegalArgumentException(
          "Constraining query must be of type SPARQLConstrainingQueryDefinition");
      }
    } else {
      String mimetype = isUpdate ? "application/sparql-update" : "application/sparql-query";
      input = new StringHandle(sparql).withMimetype(mimetype);
    }
    if (qdef.getBaseUri() != null) {
      params.add("base", qdef.getBaseUri());
    }
    if (qdef.getDefaultGraphUris() != null) {
      for (String defaultGraphUri : qdef.getDefaultGraphUris()) {
        params.add("default-graph-uri", defaultGraphUri);
      }
    }
    if (qdef.getNamedGraphUris() != null) {
      for (String namedGraphUri : qdef.getNamedGraphUris()) {
        params.add("named-graph-uri", namedGraphUri);
      }
    }
    if (qdef.getUsingGraphUris() != null) {
      for (String usingGraphUri : qdef.getUsingGraphUris()) {
        params.add("using-graph-uri", usingGraphUri);
      }
    }
    if (qdef.getUsingNamedGraphUris() != null) {
      for (String usingNamedGraphUri : qdef.getUsingNamedGraphUris()) {
        params.add("using-named-graph-uri", usingNamedGraphUri);
      }
    }

    // rulesets
    if (qdef.getRulesets() != null) {
      for (SPARQLRuleset ruleset : qdef.getRulesets()) {
        params.add("ruleset", ruleset.getName());
      }
    }
    if (qdef.getIncludeDefaultRulesets() != null) {
      params.add("default-rulesets", qdef.getIncludeDefaultRulesets() ? "include" : "exclude");
    }

    return postResource(reqlog, "/graphs/sparql", transaction, params, input, output);
  }

  static private String getTransactionId(Transaction transaction) {
    if ( transaction == null ) return null;
    return transaction.getTransactionId();
  }

  static private String getReasonPhrase(Response response) {
    if (response == null || response.message() == null) return "";
    // strip off the number part of the reason phrase
    return response.message().replaceFirst("^\\d+ ", "");
  }

  static private <T> T getEntity(BodyPart part, Class<T> as) {
    try {
      String contentType = part.getContentType();
      return getEntity(ResponseBody.create(MediaType.parse(contentType), part.getSize(),
        Okio.buffer(Okio.source(part.getInputStream()))), as);
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    } catch (MessagingException e) {
      throw new MarkLogicIOException(e);
    }
  }

  static private MediaType makeType(String mimetype) {
    if ( mimetype == null ) return null;
    MediaType type = MediaType.parse(mimetype);
    if ( type == null ) throw new IllegalArgumentException("Invalid mime-type: " + mimetype);
    return type;
  }

  static private <T> T getEntity(ResponseBody body, Class<T> as) {
    try {
      if ( as == InputStream.class ) {
        return (T) body.byteStream();
      } else if ( as == byte[].class ) {
        return (T) body.bytes();
      } else if ( as == Reader.class ) {
        return (T) body.charStream();
      } else if ( as == String.class ) {
        return (T) body.string();
      } else if ( as == MimeMultipart.class ) {
        MediaType mediaType = body.contentType();
        String contentType = (mediaType != null) ? mediaType.toString() : "application/x-unknown-content-type";
        ByteArrayDataSource dataSource = new ByteArrayDataSource(body.byteStream(), contentType);
        return (T) new MimeMultipart(dataSource);
      } else if ( as == File.class ) {
        // write out the response body to a temp file in the system temp folder
        // then return the path to that file as a File object
        String suffix = ".unknown";
        boolean isBinary = true;
        MediaType mediaType = body.contentType();
        if (mediaType != null) {
          String subtype = mediaType.subtype();
          if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.endsWith("json")) {
              suffix = ".json";
              isBinary = false;
            } else if (subtype.endsWith("xml")) {
              suffix = ".xml";
              isBinary = false;
            } else if (subtype.equals("vnd.marklogic-js-module")) {
              suffix = ".mjs";
              isBinary = false;
            } else if (subtype.equals("vnd.marklogic-javascript")) {
              suffix = ".sjs";
              isBinary = false;
            } else if (subtype.equals("vnd.marklogic-xdmp") || subtype.endsWith("xquery")) {
              suffix = ".xqy";
              isBinary = false;
            } else if (subtype.endsWith("javascript")) {
              suffix = ".js";
              isBinary = false;
            } else if (subtype.endsWith("html")) {
              suffix = ".html";
              isBinary = false;
            } else if (mediaType.type().equalsIgnoreCase("text")) {
              suffix = ".txt";
              isBinary = false;
            } else {
              suffix = "." + subtype;
            }
          }
        }
        Path path = Files.createTempFile("tmp", suffix);
        if ( isBinary == true ) {
            Files.copy(body.byteStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } else {
            try(Writer out = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
                Utilities.write(body.charStream(), out);
            }
        }
        return (T) path.toFile();
      } else {
        throw new IllegalArgumentException(
          "Handle recieveAs returned " + as + " which is not a supported type.  " +
          "Try InputStream, Reader, String, byte[], File.");
      }
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    } catch (MessagingException e) {
      throw new MarkLogicIOException(e);
    }
  }

  static private List<BodyPart> getPartList(MimeMultipart multipart) {
    try {
      if ( multipart == null ) return null;
      List<BodyPart> partList = new ArrayList<BodyPart>();
      for ( int i = 0; i < multipart.getCount(); i++ ) {
        partList.add(multipart.getBodyPart(i));
      }
      return partList;
    } catch (MessagingException e) {
      throw new MarkLogicIOException(e);
    }
  }

  static private class ObjectRequestBody extends RequestBody {
    private Object obj;
    private MediaType contentType;

    ObjectRequestBody(Object obj, MediaType contentType) {
      super();
      this.obj = obj;
      this.contentType = contentType;
    }

    @Override
    public MediaType contentType() {
      return contentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
      if ( obj instanceof InputStream ) {
        sink.writeAll(Okio.source((InputStream) obj));
      } else if ( obj instanceof File ) {
        try ( Source source = Okio.source((File) obj) ) {
          sink.writeAll(source);
        }
      } else if ( obj instanceof byte[] ) {
        sink.write((byte[]) obj);
      } else if ( obj instanceof String) {
        sink.write(((String) obj).getBytes("UTF-8"));
      } else if ( obj == null ) {
      } else {
        throw new IllegalStateException("Cannot write object of type: " + obj.getClass());
      }
    }
  }

  // API First Changes
  static private class EmptyRequestBody extends RequestBody {
    @Override
    public MediaType contentType() {
      return null;
    }
    @Override
    public void writeTo(BufferedSink sink) {
    }
  }

  static class AtomicRequestBody extends RequestBody {
    private MediaType  contentType;
    private String     value;
    AtomicRequestBody(String value, MediaType contentType) {
      super();
      this.value = value;
      this.contentType = contentType;
    }
    @Override
    public MediaType contentType() {
      return contentType;
    }
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
      sink.writeUtf8(value);
    }
  }

  class CallRequestImpl implements CallRequest {
    private SessionStateImpl session;
    private Request.Builder requestBldr;
    private RequestBody requestBody;
    private boolean hasStreamingPart;
    private HttpMethod method;
    private String endpoint;
    private HttpUrl callBaseUri;

    CallRequestImpl(String endpoint, HttpMethod method, SessionState session) {
      if (session != null && !(session instanceof SessionStateImpl)) {
        throw new IllegalArgumentException("Session state must be implemented by internal class: "+session.getClass().getName());
      }
      this.endpoint = endpoint;
      this.method = method;
      this.session = (SessionStateImpl) session;
      this.hasStreamingPart = false;
      this.callBaseUri = new HttpUrl.Builder()
          .scheme(baseUri.scheme())
          .host(baseUri.host())
          .port(baseUri.port())
          .encodedPath("/")
          .build();
    }

    @Override
    public CallResponse withEmptyResponse() {
      prepareRequestBuilder();
      CallResponseImpl responseImpl = new CallResponseImpl();
      executeRequest(responseImpl);
      return responseImpl;
    }

    @Override
    public SingleCallResponse withDocumentResponse(Format format) {
      prepareRequestBuilder();
      SingleCallResponseImpl responseImpl = new SingleCallResponseImpl(format);
      this.requestBldr = forDocumentResponse(requestBldr, format);
      executeRequest(responseImpl);
      return responseImpl;
    }

    @Override
    public MultipleCallResponse withMultipartMixedResponse(Format format) {
      prepareRequestBuilder();
      MultipleCallResponseImpl responseImpl = new MultipleCallResponseImpl(format);
      this.requestBldr = forMultipartMixedResponse(requestBldr);
      executeRequest(responseImpl);
      return responseImpl;
    }

    @Override
    public boolean hasStreamingPart() {
      return this.hasStreamingPart;
    }

    @Override
    public SessionState getSession() {
      return this.session;
    }

    @Override
    public String getEndpoint() {
      return this.endpoint;
    }

    @Override
    public HttpMethod getHttpMethod() {
      return this.method;
    }

    private void prepareRequestBuilder() {
      this.requestBldr = setupRequest(callBaseUri, endpoint, null);
      if (session != null) {
    	this.requestBldr = addCookies(this.requestBldr, session.getCookies(), session.getCreatedTimestamp());
        // Add the Cookie header for SessionId if we have a session object passed
        this.requestBldr.addHeader(HEADER_COOKIE, "SessionID="+session.getSessionId());
      }
      addHttpMethod();
      this.requestBldr.addHeader(HEADER_ERROR_FORMAT, MIMETYPE_APPLICATION_JSON);
    }

    private void addHttpMethod() {
      if(method != null && method == HttpMethod.POST) {
        if(requestBody == null) {
          throw new IllegalStateException("Request Body is null!");
        }
        this.requestBldr.post(requestBody);
      } else {
        throw new IllegalStateException("HTTP method is null or invalid!");
      }
    }

    private void executeRequest(CallResponseImpl responseImpl) {
      SessionState session = getSession();
      //TODO: Add a telemetry agent if needed
      // requestBuilder = addTelemetryAgentId(requestBuilder);

      boolean hasStreamingPart = hasStreamingPart();
      Consumer<Boolean> resendableConsumer = resendable -> {
        if (hasStreamingPart) {
          checkFirstRequest();
          throw new ResourceNotResendableException(
              "Cannot retry request for " + getEndpoint());
        }
      };

      Function<Request.Builder, Response> sendRequestFunction = requestBldr -> {
        if (isFirstRequest() && hasStreamingPart) makeFirstRequest(callBaseUri, "", 0);
        Response response = sendRequestOnce(requestBldr);
        if (isFirstRequest()) setFirstRequest(false);
        return response;
      };

      Response response = sendRequestWithRetry(requestBldr, sendRequestFunction, resendableConsumer);

      if(session != null) {
        List<ClientCookie> cookies = new ArrayList<>();
        for ( String setCookie : response.headers(HEADER_SET_COOKIE) ) {
          ClientCookie cookie = ClientCookie.parse(requestBldr.build().url(), setCookie);
          cookies.add(cookie);
        }
        ((SessionStateImpl)session).setCookies(cookies);
      }
      checkStatus(response);
      responseImpl.setResponse(response);
    }

    private void checkStatus(Response response) {
      int statusCode = response.code();
      if (statusCode >= 300) {
        FailedRequest failure = null;
        String contentType = response.header(HEADER_CONTENT_TYPE);
        MediaType mediaType = MediaType.parse(
                (contentType != null) ? contentType : "application/x-unknown-content-type"
        );
        String subtype = (mediaType != null) ? mediaType.subtype() : null;
        if (subtype != null) {
          subtype = subtype.toLowerCase();
          if (subtype.endsWith("json") || subtype.endsWith("xml")) {
            failure = extractErrorFields(response);
          }
        }
        if (failure == null) {
          closeResponse(response);
          if (statusCode == STATUS_UNAUTHORIZED) {
            failure = new FailedRequest();
            failure.setMessageString("Unauthorized");
            failure.setStatusString("Failed Auth");
          } else if (statusCode == STATUS_NOT_FOUND) {
            throw new ResourceNotFoundException("Could not " + method  + " at " + endpoint);
          } else if (statusCode == STATUS_FORBIDDEN) {
            throw new ForbiddenUserException("User is not allowed to " + method + " at " + endpoint);
          } else {
            failure = new FailedRequest();
            failure.setStatusCode(statusCode);
            failure.setMessageCode("UNKNOWN");
            failure.setMessageString("Server did not respond with an expected Error message.");
            failure.setStatusString("UNKNOWN");
          }
        }
        FailedRequestException ex = failure == null ? new FailedRequestException("failed to " + method + " at " + endpoint + ": "
            + getReasonPhrase(response)) : new FailedRequestException("failed to " + method + " at " + endpoint + ": "
                + getReasonPhrase(response), failure);
        throw ex;
      }
    }

    public CallRequest withEmptyRequest() {
      requestBody = new EmptyRequestBody();
      return this;
    }

    public CallRequest withAtomicBodyRequest(CallField... params) {
      String atomics = Stream.of(params)
          .map(param -> encodeParamValue(param))
          .filter(param -> param != null)
          .collect(Collectors.joining("&"));
      requestBody = RequestBody.create(URLENCODED_MIME_TYPE, (atomics == null) ? "" : atomics);
      return this;
    }

    public CallRequest withNodeBodyRequest(CallField... params) {
      this.requestBody = makeRequestBody(params);
      return this;
    }

    private RequestBody makeRequestBody(String value) {
      if (value == null) {
        return new EmptyRequestBody();
      }
      return new AtomicRequestBody(value, MediaType.parse("text/plain"));
    }

    private RequestBody makeRequestBody(AbstractWriteHandle document) {
      if (document == null) {
        return new EmptyRequestBody();
      }
      HandleImplementation handleBase = HandleAccessor.as(document);
      Format format = handleBase.getFormat();
      String mimetype = (format == Format.BINARY) ? null : handleBase.getMimetype();
      MediaType mediaType = MediaType.parse(
              (mimetype != null) ? mimetype : "application/x-unknown-content-type"
      );
      return (document instanceof OutputStreamSender) ?
          new StreamingOutputImpl((OutputStreamSender) document, null, mediaType) :
          new ObjectRequestBody(HandleAccessor.sendContent(document), mediaType);
    }

    private RequestBody makeRequestBody(CallField[] params) {
      if (params == null || params.length == 0) {
        return new EmptyRequestBody();
      }

      MultipartBody.Builder multiBldr = new MultipartBody.Builder();
      multiBldr.setType(MultipartBody.FORM);

      Condition hasValue = new Condition();
      Condition hasStreamingPartCondition = new Condition();
      for (CallField param: params) {
        if (param == null) {
          continue;
        }

        final String paramName = param.getParamName();
        if (param instanceof SingleAtomicCallField) {
          String paramValue = ((SingleAtomicCallField) param).getParamValue();
          if (paramValue != null) {
            hasValue.set();
            multiBldr.addFormDataPart(paramName, null, makeRequestBody(paramValue));
          }
        } else if (param instanceof MultipleAtomicCallField) {
          Stream<String> paramValues = ((MultipleAtomicCallField) param).getParamValues();
          if (paramValues != null) {
            paramValues
                .filter(paramValue -> paramValue != null)
                .forEachOrdered(paramValue ->
                    multiBldr.addFormDataPart(paramName, null, makeRequestBody(paramValue))
                );
          }
        } else if (param instanceof SingleNodeCallField) {
          SingleNodeCallField singleNodeParam = (SingleNodeCallField) param;
          BufferableHandle paramValue = singleNodeParam.getParamValue();
          if (paramValue != null) {
            HandleImplementation handleBase = HandleAccessor.as(paramValue);
            if(! handleBase.isResendable()) {
                BytesHandle bytesHandle = new BytesHandle(paramValue);
                singleNodeParam.setParamValue(bytesHandle);
                    paramValue = bytesHandle;
                }
            hasValue.set();
            multiBldr.addFormDataPart(paramName, null, makeRequestBody(paramValue));
          }
        } else if (param instanceof UnbufferedMultipleNodeCallField) {
          Stream<? extends BufferableHandle> paramValues = ((UnbufferedMultipleNodeCallField) param).getParamValues();
          if (paramValues != null) {
            paramValues
                .filter(paramValue -> paramValue != null)
                .forEachOrdered(paramValue -> {
                  HandleImplementation handleBase = HandleAccessor.as(paramValue);
                  if(!handleBase.isResendable()) {
                    hasStreamingPartCondition.set();
                  }
                  hasValue.set();
                  multiBldr.addFormDataPart(paramName, null, makeRequestBody(paramValue));
                });
          }
        } else if (param instanceof BufferedMultipleNodeCallField) {
            BufferableHandle[] paramValues = ((BufferedMultipleNodeCallField) param).getParamValuesArray();
            if(paramValues != null) {
                boolean checkedBuffer = false;
                for(int i=0; i < paramValues.length; i++) {
                    BufferableHandle paramValue = paramValues[i];
                    if (paramValue != null) {
                        HandleImplementation handleBase = HandleAccessor.as(paramValue);
                        if (!handleBase.isResendable()) {
                            paramValue = new BytesHandle(paramValue);
                            if (!checkedBuffer) {
                                Class<?> actualClass = paramValues.getClass().getComponentType();
                                if (actualClass != BufferableHandle.class && actualClass != BytesHandle.class) {
                                    paramValues =
                                        Arrays.copyOf(paramValues, paramValues.length, BufferableHandle[].class);
                                }
                                checkedBuffer = true;
                            }
                            paramValues[i] = paramValue;
                        }
                        hasValue.set();
                        multiBldr.addFormDataPart(paramName, null, makeRequestBody(paramValue));
                    }
                }
            }
        } 
        else {
          throw new IllegalStateException(
              "unknown multipart "+paramName+" param of: "+param.getClass().getName()
          );
        }
      }

      if (!hasValue.get()) {
        return new EmptyRequestBody();
      }
      this.hasStreamingPart = hasStreamingPartCondition.get();
      return multiBldr.build();
    }

  }

  @Override
  public CallRequest makeEmptyRequest(String endpoint, HttpMethod method, SessionState session) {
      return new CallRequestImpl(endpoint, method, session).withEmptyRequest();
  }

  @Override
  public CallRequest makeAtomicBodyRequest(String endpoint, HttpMethod method, SessionState session, CallField... params) {
    if (params == null || params.length == 0) {
      return makeEmptyRequest(endpoint, method, session);
    }
    return new CallRequestImpl(endpoint, method, session).withAtomicBodyRequest(params);
  }

  @Override
  public CallRequest makeNodeBodyRequest(String endpoint, HttpMethod method, SessionState session, CallField... params) {
    if (params == null || params.length == 0) {
      return makeEmptyRequest(endpoint, method, session);
    }
    return new CallRequestImpl(endpoint, method, session).withNodeBodyRequest(params);
  }

  static private String encodeParamValue(String paramName, String value) {
    if (value == null) {
      return null;
    }
    try {
      return paramName+"="+URLEncoder.encode(value, UTF8_ID);
    } catch(UnsupportedEncodingException e) {
      throw new IllegalStateException("UTF-8 is unsupported", e);
    }
  }

  static private String encodeParamValue(SingleAtomicCallField param) {
    if (param == null) {
      return null;
    }
    return encodeParamValue(param.getParamName(), param.getParamValue());
  }

  static private String encodeParamValue(MultipleAtomicCallField param) {
    if (param == null) {
      return null;
    }
    String         paramName   = param.getParamName();
    Stream<String> paramValues = param.getParamValues();
    if (paramValues == null) {
      return null;
    }
    String encodedParamValues = paramValues
        .map(paramValue -> encodeParamValue(paramName, paramValue))
        .filter(paramValue -> (paramValue != null))
        .collect(Collectors.joining("&"));
    if (encodedParamValues == null || encodedParamValues.length() == 0) {
      return null;
    }
    return encodedParamValues;
  }

  static private String encodeParamValue(CallField param) {
    if (param == null) {
      return null;
    } else if (param instanceof SingleAtomicCallField) {
      return encodeParamValue((SingleAtomicCallField) param);
    } else if (param instanceof MultipleAtomicCallField) {
      return encodeParamValue((MultipleAtomicCallField) param);
    }
    throw new IllegalStateException(
        "could not encode parameter "+param.getParamName()+" of type: "+param.getClass().getName()
    );
  }

  static class CallResponseImpl implements CallResponse {
    private boolean isNull = true;
    private Response response;

    void setResponse(Response response) {
      this.response = response;
    }

    @Override
    public boolean isNull() {
      return isNull;
    }

    void setNull(boolean isNull) {
      this.isNull = isNull;
    }

    @Override
    public int getStatusCode() {
      return response.code();
    }

    @Override
    public String getStatusMsg() {
      return response.message();
    }

    //TODO: Check if this is needed since we are parsing it in the checkStatus(Respose).
    //TODO: It might throw a closed exception since the response would be closed. Remove it after some testing
    @Override
    public String getErrorBody() {
      try (ResponseBody errorBody = response.body()) {
        if (errorBody.contentLength() > 0) {
          MediaType errorType = errorBody.contentType();
          if (errorType != null) {
            String subtype = errorType.subtype();
            if (subtype != null && subtype.toLowerCase().endsWith("json")) {
              return errorBody.string();
            }
          }
        }
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
      return null;
    }

    @Override
    public void close() {
    }
  }

  static class SingleCallResponseImpl extends CallResponseImpl implements SingleCallResponse, AutoCloseable {
    private Format format;
    private ResponseBody responseBody;
    SingleCallResponseImpl(Format format) {
      this.format = format;
    }
    void setResponse(Response response) {
      super.setResponse(response);
      setResponseBody(response.body());
    }
    void setResponseBody(ResponseBody responseBody) {
      if (!checkNull(responseBody, format)) {
        this.responseBody = responseBody;
        setNull(false);
      }
    }

    @Override
    public byte[] asBytes() {
      try {
        if (responseBody == null) {
          return null;
        }
        byte[] value = responseBody.bytes();
        closeImpl();
        return value;
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public InputStream asInputStream() {
      return (responseBody == null) ? null : responseBody.byteStream();
    }
    @Override
    public InputStreamHandle asInputStreamHandle() {
      return (responseBody == null) ? null : new InputStreamHandle(asInputStream());
    }
    @Override
    public Reader asReader() {
      return (responseBody == null) ? null : responseBody.charStream();
    }
    @Override
    public ReaderHandle asReaderHandle() {
      return (responseBody == null) ? null : new ReaderHandle(asReader());
    }
    @Override
    public String asString() {
      try {
        if (responseBody == null) {
          return null;
        }
        String value = responseBody.string();
        closeImpl();
        return value;
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }

    @Override
    public void close() {
      if (responseBody != null) {
        closeImpl();
      }
    }

    private void closeImpl() {
      responseBody.close();
      responseBody = null;
    }
  }

  static class MultipleCallResponseImpl extends CallResponseImpl implements MultipleCallResponse {
    private Format format;
    private MimeMultipart multipart;
    MultipleCallResponseImpl(Format format){
      this.format = format;
    }
    void setResponse(Response response) {
      try {
        super.setResponse(response);
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
          setNull(true);
          return;
        }
        MediaType contentType = responseBody.contentType();
        if (contentType == null) {
          setNull(true);
          return;
        }
        ByteArrayDataSource dataSource = new ByteArrayDataSource(
            responseBody.byteStream(), contentType.toString()
        );
        setMultipart(new MimeMultipart(dataSource));
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      }
    }
    void setMultipart(MimeMultipart multipart) {
      if (!checkNull(multipart, format)) {
        this.multipart = multipart;
        setNull(false);
      }
    }

    @Override
    public Stream<byte[]> asStreamOfBytes() {
      try {
        if (multipart == null) {
          return Stream.empty();
        }
        int partCount = multipart.getCount();

        Stream.Builder<byte[]> builder = Stream.builder();
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          builder.accept(NodeConverter.InputStreamToBytes(bodyPart.getInputStream()));
        }
        return builder.build();
      } catch (MessagingException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    @Override
    public Stream<InputStreamHandle> asStreamOfInputStreamHandle() {
      try {
        if (multipart == null) {
          return Stream.empty();
        }
        int partCount = multipart.getCount();

        Stream.Builder<InputStreamHandle> builder = Stream.builder();
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          builder.accept(new InputStreamHandle(bodyPart.getInputStream()));
        }
        return builder.build();
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public Stream<InputStream> asStreamOfInputStream() {
      try {
        if (multipart == null) {
          return Stream.empty();
        }
        int partCount = multipart.getCount();

        Stream.Builder<InputStream> builder = Stream.builder();
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          builder.accept(bodyPart.getInputStream());
        }
        return builder.build();
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public Stream<Reader> asStreamOfReader() {
      try {
        if (multipart == null) {
          return Stream.empty();
        }
        int partCount = multipart.getCount();

        Stream.Builder<Reader> builder = Stream.builder();
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          builder.accept(NodeConverter.InputStreamToReader(bodyPart.getInputStream()));
        }
        return builder.build();
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public Stream<ReaderHandle> asStreamOfReaderHandle() {
      try {
        if (multipart == null) {
          return Stream.empty();
        }
        int partCount = multipart.getCount();

        Stream.Builder<ReaderHandle> builder = Stream.builder();
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          builder.accept(new ReaderHandle(NodeConverter.InputStreamToReader(bodyPart.getInputStream())));
        }
        return builder.build();
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public Stream<String> asStreamOfString() {
      try {
        if (multipart == null) {
          return Stream.empty();
        }
        int partCount = multipart.getCount();

        Stream.Builder<String> builder = Stream.builder();
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          builder.accept(NodeConverter.InputStreamToString(bodyPart.getInputStream()));
        }
        return builder.build();
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public byte[][] asArrayOfBytes() {
      try {
        if (multipart == null) {
          return new byte[0][];
        }
        int partCount = multipart.getCount();

        byte[][] result = new byte[partCount][];
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          result[i] = NodeConverter.InputStreamToBytes(bodyPart.getInputStream());
        }
        return result;
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public InputStream[] asArrayOfInputStream() {
      try {
        if (multipart == null) {
          return new InputStream[0];
        }
        int partCount = multipart.getCount();

        InputStream[] result = new InputStream[partCount];
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          result[i] = bodyPart.getInputStream();
        }
        return result;
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public InputStreamHandle[] asArrayOfInputStreamHandle() {
      try {
        if (multipart == null) {
          return new InputStreamHandle[0];
        }
        int partCount = multipart.getCount();

        InputStreamHandle[] result = new InputStreamHandle[partCount];
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          result[i] = new InputStreamHandle(bodyPart.getInputStream());
        }
        return result;
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public ReaderHandle[] asArrayOfReaderHandle() {
      try {
        if (multipart == null) {
          return new ReaderHandle[0];
        }
        int partCount = multipart.getCount();

        ReaderHandle[] result = new ReaderHandle[partCount];
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          result[i] = new ReaderHandle(NodeConverter.InputStreamToReader(bodyPart.getInputStream()));
        }
        return result;
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public Reader[] asArrayOfReader() {
      try {
        if (multipart == null) {
          return new Reader[0];
        }
        int partCount = multipart.getCount();

        Reader[] result = new Reader[partCount];
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          result[i] = NodeConverter.InputStreamToReader(bodyPart.getInputStream());
        }
        return result;
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
    @Override
    public String[] asArrayOfString() {
      try {
        if (multipart == null) {
          return new String[0];
        }
        int partCount = multipart.getCount();

        String[] result = new String[partCount];
        for (int i=0; i < partCount; i++) {
          BodyPart bodyPart = multipart.getBodyPart(i);
          result[i] = NodeConverter.InputStreamToString(bodyPart.getInputStream());
        }
        return result;
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }
  }

  static protected boolean checkNull(ResponseBody body, Format expectedFormat) {
    if (body != null) {
      if (body.contentLength() == 0) {
        body.close();
      } else {
        MediaType actualType = body.contentType();
        if (actualType == null) {
          body.close();
          throw new RuntimeException(
              "Returned document with unknown mime type instead of "+expectedFormat.getDefaultMimetype()
          );
        }
        Format actualFormat = Format.getFromMimetype(actualType.toString());
        if (expectedFormat != actualFormat) {
          body.close();
          throw new RuntimeException(
              "Mime type "+actualType.toString()+" for returned document not recognized for "+expectedFormat.name()
          );
        }
        return false;
      }
    }
    return true;
  }
  static protected boolean checkNull(MimeMultipart multipart, Format expectedFormat) {
    if (multipart != null) {
      try {
        if (multipart.getCount() != 0) {
          BodyPart firstPart   = multipart.getBodyPart(0);
          String   actualType  = (firstPart == null) ? null : firstPart.getContentType();
          if (actualType == null) {
            throw new RuntimeException(
                "Returned document with unknown mime type instead of "+expectedFormat.getDefaultMimetype()
            );
          }
          Format actualFormat = Format.getFromMimetype(actualType);
          if (expectedFormat != actualFormat) {
            throw new RuntimeException(
                "Mime type "+actualType+" for returned document not recognized for "+expectedFormat.name()
            );
          }
          return false;
        }
      } catch (MessagingException e) {
        throw new MarkLogicIOException(e);
      }
    }
    return true;
  }

  static private void closeResponse(Response response) {
      if (response == null || response.body() == null) return;
      response.close();
  }

  Request.Builder forDocumentResponse(Request.Builder requestBldr, Format format) {
    return requestBldr.addHeader(HEADER_ACCEPT, (format == Format.BINARY) ? "application/x-unknown-content-type" : format.getDefaultMimetype());
  }

  Request.Builder forMultipartMixedResponse(Request.Builder requestBldr) {
    return requestBldr.addHeader(HEADER_ACCEPT, multipartMixedWithBoundary());
  }

  static protected class Condition {
    private boolean is = false;
    protected boolean get() {
    return is;
  }
    protected void set() {
      if (!is)
        is = true;
    }
  }
  
  static class ConnectionResultImpl implements ConnectionResult {
	private boolean connected = false;
	private int statusCode;
	private String errorMessage;

	@Override
	public boolean isConnected() {
		return connected;
	}

	private void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public Integer getStatusCode() {
		return statusCode;
	}

	private void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
  }

  @FunctionalInterface
  private interface ResultIteratorConstructor<T> {
    T construct(RequestLogger logger, List list, Closeable closeable);
  }
}
