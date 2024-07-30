/*
 * Copyright (c) 2023 MarkLogic Corporation
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
package com.marklogic.client;

import com.marklogic.client.extra.httpclient.HttpClientConfigurator;
import com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator;
import com.marklogic.client.impl.*;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import okhttp3.OkHttpClient;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * A Database Client Factory configures a database client for making
 * database requests.
 */
public class DatabaseClientFactory {

  static private List<ClientConfigurator<?>> clientConfigurators = Collections.synchronizedList(new ArrayList<>());

  static private HandleFactoryRegistry handleRegistry =
    HandleFactoryRegistryImpl.newDefault();

  /**
   * An SSLHostnameVerifier checks whether a hostname is acceptable during SSL
   * authentication.  By default, {@link #COMMON} is used, allowing any level
   * of subdomains for SSL certificates with wildcard domains.  If only one
   * level of subdomains is allowed for SSL certificates with wildcard domains,
   * use {@link #STRICT}.
   */
  public interface SSLHostnameVerifier {
    /**
     * The ANY SSLHostnameVerifier allows any hostname, which can be useful
     * during initial development when a valid SSL certificate is not available
     * but is not recommended for production because it would permit an invalid
     * SSL certificate.
     */
    final static public Builtin ANY    = new Builtin("ANY");
    /**
     * The COMMON SSLHostnameVerifier applies common rules for checking
     * hostnames during SSL authentication (similar to
     * org.apache.http.conn.ssl.BrowserCompatHostnameVerifier). It allows any
     * level of subdomains for SSL certificates with wildcard domains.
     */
    final static public Builtin COMMON = new Builtin("COMMON");
    /**
     * The STRICT SSLHostnameVerifier applies strict rules
     * for checking hostnames during SSL authentication (similar to
     * org.apache.http.conn.ssl.StrictHostnameVerifier).  It allows one level
     * of subdomain for SSL certificates with wildcard domains like RFC 2818.
     */
    final static public Builtin STRICT = new Builtin("STRICT");

    /**
     * Checks during SSL authentication that a hostname matches the Common Name
     * or "DNS" Subject alts from the SSL certificate presented by the server.
     * @param hostname	the DNS host name of the server
     * @param cns	common names from the SSL certificate presented by the server
     * @param subjectAlts	alternative subject names from the SSL certificate presented by the server
     * @throws SSLException if the hostname isn't acceptable
     */
    public void verify(String hostname, String[] cns, String[] subjectAlts) throws SSLException;

    /**
     * HostnameVerifierAdapter verifies the hostname,SSLSession and X509Certificate certificate.
     * */
    static class HostnameVerifierAdapter implements HostnameVerifier {
    	private SSLHostnameVerifier verifier;

    	public HostnameVerifierAdapter(SSLHostnameVerifier verifier) {
	          this.verifier = verifier;
	        }
    	/**
    	 * verify method verifies the incoming hostname and SSLSession.
    	 * @param hostname denotes the hostname.
    	 * @param session represents the SSLSession containing the peer certificates.
    	 * @return true if the hostname and peer certificates are valid and false if they are invalid.
    	 * */
    	@Override
    	public boolean verify(String hostname, SSLSession session) {
    		try {
    			Certificate[] certificates = session.getPeerCertificates();
	            verify(
	                hostname,
                    (X509Certificate) ((certificates == null || certificates.length == 0) ? null : certificates[0])
                );
	            return true;
	          } catch(SSLException e) {
	            return false;
	          }
	        }
    	/**
    	 * verify method verifies the hostname and X509Certificate certificate.
    	 * @param hostname denotes the hostname.
    	 * @param cert represents the X509Certificate certificate.
    	 * @throws SSLException if the hostname or certificate is/are invalid.
    	 * */
    	public void verify(String hostname, X509Certificate cert) throws SSLException {
    		ArrayList<String> cnArray = new ArrayList<>();
    		try {
    			LdapName ldapDN = new LdapName(cert.getSubjectX500Principal().getName());
	            for(Rdn rdn: ldapDN.getRdns()) {
	              Object value = rdn.getValue();
	              if ( "CN".equalsIgnoreCase(rdn.getType()) && value instanceof String ) {
	                cnArray.add((String) value);
	              }
	            }
	            int type_dnsName = 2;
	            int type_ipAddress = 7;
	            ArrayList<String> subjectAltArray = new ArrayList<>();
	            Collection<List<?>> alts = cert.getSubjectAlternativeNames();
	            if ( alts != null ) {
	              for ( List<?> alt : alts ) {
	                if ( alt != null && alt.size() == 2 && alt.get(1) instanceof String ) {
	                  Integer type = (Integer) alt.get(0);
	                  if ( type == type_dnsName || type == type_ipAddress ) {
	                    subjectAltArray.add((String) alt.get(1));
	                  }
	                }
	              }
	            }
	            String[] cns = cnArray.toArray(new String[cnArray.size()]);
	            String[] subjectAlts = subjectAltArray.toArray(new String[subjectAltArray.size()]);
	            verifier.verify(hostname, cns, subjectAlts);
	          }
    		catch(CertificateParsingException e) {
	            throw new MarkLogicIOException(e);
	          }
    		catch(InvalidNameException e) {
	            throw new MarkLogicIOException(e);
	          }
	        }
	   }

    /**
     * Builtin supports builtin implementations of SSLHostnameVerifier.
     */
    public class Builtin implements SSLHostnameVerifier {
      private String name;
      private Builtin(String name) {
        super();
        this.name = name;
      }
      @Override
      public void verify(String hostname, String[] cns, String[] subjectAlts) throws SSLException {
        throw new MarkLogicInternalException(
          "SSLHostnameVerifier.Builtin called directly instead of passed as parameter");
      }
      /**
       * Returns the name of the built-in.
       * @return	the built-in name
       */
      public String getName() {
        return name;
      }

    }
  }

  /**
   * A HandleFactoryRegistry associates IO representation classes
   * with handle factories. The API uses the registry to create
   * a content handle to act as an adapter for a supported
   * IO representation class.  The IO class and its instances can
   * then be passed directly to convenience methods.
   * The default registry associates the content handles provided
   * by the API and the supported IO representation classes.
   * Create instances of this interface only if you need to modify
   * the default registry.
   * @see DatabaseClientFactory#getHandleRegistry()
   * @see DatabaseClientFactory.Bean#getHandleRegistry()
   */
  public interface HandleFactoryRegistry {
    /**
     * Associates a factory for content handles with the classes
     * for IO representations known to the factory.
     * @param factory	a factory for creating content handle instances
     */
    public void register(ContentHandleFactory factory);
    /**
     * Associates a factory for content handles with the specified classes
     * for IO representations.
     * @param factory	a factory for creating content handle instances
     * @param ioClasses	the IO classes for which the factory should create handles
     */
    public void register(ContentHandleFactory factory, Class<?>... ioClasses);
    /**
     * Returns whether the registry associates the class with a factory.
     * @param ioClass	the class for an IO representation
     * @return	true if a factory has been registered
     */
    public boolean isRegistered(Class<?> ioClass);
    /**
     * Returns the classes for the IO representations for which a factory
     * has been registered.
     * @return	the IO classes
     */
    public Set<Class<?>> listRegistered();
    /**
     * Creates a ContentHandle if the registry has a factory
     * for the class of the IO representation.
     * @param type	the class for an IO representation
     * @param <C> the registered type for the returned handle
     * @return	a content handle or null if no factory supports the class
     */
    public <C> ContentHandle<C> makeHandle(Class<C> type);
    /**
     * Removes the classes from the registry
     * @param ioClasses	one or more registered classes for an IO representation
     */
    public void unregister(Class<?>... ioClasses);
    /**
     * Create a copy of the current registry
     * @return	a copy of the current registry
     */
    public HandleFactoryRegistry copy();
  }

  /**
   * A ClientConfigurator provides custom configuration for the communication library
   * used to sending client requests and receiving server responses.
   * @see com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator
   * @param <T>	the configurable class for the communication library
   */
  public interface ClientConfigurator<T> {
    /**
     * Called as the last step in configuring the communication library.
     * @param client	the configurable object for the communication library
     */
    public void configure(T client);
  }

  private DatabaseClientFactory() {
  }

  public interface SecurityContext {
    /**
     * Returns the SSLContext for an SSL client.
     * @return	the SSL context
     */
    SSLContext getSSLContext();

	  /**
	   * @return the trust manager for this connection
	   * @since 6.1.0 ; was already implemented by both implementations of this interface, and exposing it here
	   * simplifies configuring SSL regardless of the interface implementation
	   */
	  X509TrustManager getTrustManager();

   	/*
     * Returns the host verifier.
     * @return	the host verifier
     */
    SSLHostnameVerifier getSSLHostnameVerifier();

    /**
     * Specifies the host verifier for a client that verifies hosts for
     * additional security.
     * @param verifier	the host verifier
     */
    void setSSLHostnameVerifier(SSLHostnameVerifier verifier);

    /**
     * The SSLContext should be initialized with KeyManager and TrustManager
     * using a KeyStore. <br>
     * <br>
     *
     * If we init the sslContext with null TrustManager, it would use the
     * &lt;java-home&gt;/lib/security/cacerts file for trusted root certificates, if
     * javax.net.ssl.trustStore system property is not set and
     * &lt;java-home&gt;/lib/security/jssecacerts is not present. See <a href =
     * "http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html">JSSE
     * Reference Guide</a> for more information on SSL and TrustManagers.<br>
     * <br>
     *
     * If self signed certificates, signed by CAs created internally are used,
     * then the internal CA's root certificate should be added to the keystore.
     * See this <a href =
     * "https://docs.oracle.com/cd/E19226-01/821-0027/geygn/index.html">link</a>
     * for adding a root certificate in the keystore.
     *
     * @param context - the SSLContext object required for the SSL connection
     * @param trustManager - X509TrustManager with which we initialize the
     *          SSLContext
     * @return a context containing authentication information
     */
    SecurityContext withSSLContext(SSLContext context, X509TrustManager trustManager);

    /**
     * Specifies the host verifier for a client that verifies hosts for
     * additional security.
     * @param verifier	the host verifier
     * @return a context configured with the host verifier
     */
    SecurityContext withSSLHostnameVerifier(SSLHostnameVerifier verifier);
  }

  private static class AuthContext implements SecurityContext {
    SSLContext sslContext;
    X509TrustManager trustManager;
    SSLHostnameVerifier sslVerifier;

    @Override
    public SSLContext getSSLContext() {
      return sslContext;
    }

    @Override
    public SSLHostnameVerifier getSSLHostnameVerifier() {
      return sslVerifier;
    }

    @Override
    public void setSSLHostnameVerifier(SSLHostnameVerifier verifier) {
      this.sslVerifier = verifier;
    }

	public X509TrustManager getTrustManager() {
		return this.trustManager;
	}

    @Override
    public SecurityContext withSSLHostnameVerifier(SSLHostnameVerifier verifier) {
      this.sslVerifier = verifier;
      return this;
    }

    @Override
    public SecurityContext withSSLContext(SSLContext context, X509TrustManager trustManager) {
      this.sslContext = context;
      this.trustManager = trustManager;
      return this;
    }
  }

	/**
	 * @since 6.1.0
	 */
	public static class MarkLogicCloudAuthContext extends AuthContext {
		private String tokenEndpoint;
		private String grantType;
		private String apiKey;
		private Integer tokenDuration;

		/**
		 * @param apiKey user's API key for accessing MarkLogic Cloud
		 */
		public MarkLogicCloudAuthContext(String apiKey) {
			this(apiKey, null);
		}

		/**
		 * @param apiKey user's API key for accessing MarkLogic Cloud
		 * @param tokenDuration length in minutes until the generated access token expires
		 * @since 6.3.0
		 */
		public MarkLogicCloudAuthContext(String apiKey, Integer tokenDuration) {
			this(apiKey, "/token", "apikey", tokenDuration);
		}

		/**
		 * Only intended to be used in the scenario that the token endpoint of "/token" and the grant type of "apikey"
		 * are not the intended values.
		 *
		 * @param apiKey user's API key for accessing MarkLogic Cloud
		 * @param tokenEndpoint for overriding the default token endpoint if necessary
		 * @param grantType for overriding the default grant type if necessary
		 */
		public MarkLogicCloudAuthContext(String apiKey, String tokenEndpoint, String grantType) {
			this(apiKey, tokenEndpoint, grantType, null);
		}

		/**
		 * Only intended to be used in the scenario that the token endpoint of "/token" and the grant type of "apikey"
		 * are not the intended values.
		 *
		 * @param apiKey user's API key for accessing MarkLogic Cloud
		 * @param tokenEndpoint for overriding the default token endpoint if necessary
		 * @param grantType for overriding the default grant type if necessary
		 * @param tokenDuration length in minutes until the generated access token expires
		 * @since 6.3.0
		 */
		public MarkLogicCloudAuthContext(String apiKey, String tokenEndpoint, String grantType, Integer tokenDuration) {
			this.apiKey = apiKey;
			this.tokenEndpoint = tokenEndpoint;
			this.grantType = grantType;
			this.tokenDuration = tokenDuration;
		}

		public String getTokenEndpoint() {
			return tokenEndpoint;
		}

		public String getGrantType() {
			return grantType;
		}

		public String getApiKey() {
			return apiKey;
		}

		/**
		 * @return
		 * @since 6.3.0
		 */
		public Integer getTokenDuration() {
			return tokenDuration;
		}

		@Override
		public MarkLogicCloudAuthContext withSSLContext(SSLContext context, X509TrustManager trustManager) {
			this.sslContext = context;
			this.trustManager = trustManager;
			return this;
		}

		@Override
		public MarkLogicCloudAuthContext withSSLHostnameVerifier(SSLHostnameVerifier verifier) {
			this.sslVerifier = verifier;
			return this;
		}
	}

	/**
	 * @since 6.6.0
	 */
	public static class OAuthContext extends AuthContext {
		private String token;

		/**
		 * @param token the OAuth token to include in the Authorization header in each request sent to MarkLogic.
		 */
		public OAuthContext(String token) {
			this.token = token;
		}

		public String getToken() {
			return token;
		}
	}

  public static class BasicAuthContext extends AuthContext {
    String user;
    String password;

    public BasicAuthContext(String user, String password) {
      this.user = user;
      this.password = password;
    }

    public String getUser() {
      return user;
    }

    public String getPassword() {
      return password;
    }

    @Override
    public BasicAuthContext withSSLContext(SSLContext context, X509TrustManager trustManager) {
      this.sslContext = context;
      this.trustManager = trustManager;
      return this;
    }

    @Override
    public BasicAuthContext withSSLHostnameVerifier(SSLHostnameVerifier verifier) {
      this.sslVerifier = verifier;
      return this;
    }
  }

  public static class DigestAuthContext extends AuthContext {
    String user;
    String password;

    public DigestAuthContext(String user, String password) {
      this.user = user;
      this.password = password;
    }

    public String getUser() {
      return user;
    }

    public String getPassword() {
      return password;
    }

    @Override
    public DigestAuthContext withSSLContext(SSLContext context, X509TrustManager trustManager) {
      this.sslContext = context;
      this.trustManager = trustManager;
      return this;
    }

    @Override
    public DigestAuthContext withSSLHostnameVerifier(SSLHostnameVerifier verifier) {
      this.sslVerifier = verifier;
      return this;
    }
  }

  public static class KerberosAuthContext extends AuthContext {

    Map<String, String> krbOptions;
	public Map<String, String> getKrbOptions() {
		return krbOptions;
	}

    public KerberosAuthContext() {

      krbOptions = Collections.unmodifiableMap(new KerberosConfig().toOptions());
    }

    public KerberosAuthContext(String principal) {

      krbOptions = Collections.unmodifiableMap(new KerberosConfig().withPrincipal(principal).toOptions());
    }

    public KerberosAuthContext(KerberosConfig krbConfig) {

      krbOptions = Collections.unmodifiableMap(krbConfig.toOptions());
    }

    @Override
    public KerberosAuthContext withSSLContext(SSLContext context, X509TrustManager trustManager) {
      this.sslContext = context;
      this.trustManager = trustManager;
      return this;
    }

    @Override
    public KerberosAuthContext withSSLHostnameVerifier(SSLHostnameVerifier verifier) {
      this.sslVerifier = verifier;
      return this;
    }
  }
  /**
   * A SAMLAuthContext is used for authorization using SAML.
   * It consists of a authorization token each with an expiration time stamp.
   * SAMLAuthContext asks for a new token with a new expiration time stamp if
   * the previous token expires and the session is still valid.
   */
	public static class SAMLAuthContext implements SecurityContext {
		private String token;
		private SSLContext sslContext;
		private X509TrustManager trustManager;
		private SSLHostnameVerifier sslVerifier;
		private AuthorizerCallback authorizer;
		private ExpiringSAMLAuth authorization;
		private RenewerCallback renewer;

      /**
       * Constructs a context for authorization using a SAML assertions token.
       * @param authorizationToken the token with the SAML assertions
       */
		public SAMLAuthContext(String authorizationToken) {
			this.token = authorizationToken;
		}
      /**
       * Constructs a context for authorization using an authorizer callback.
       * The authorizer must get a SAML assertions token from the IDP (Identity Provider)
       * for the first request and when the current SAML assertions token is expiring.
       * @param authorizer the callback returning the assertions token
       */
		public SAMLAuthContext(AuthorizerCallback authorizer) {
			this.authorizer = authorizer;
		}
      /**
       * Constructs a context for authorization using a SAML assertions token
       * and a renewer callback. The renewer callback must renew the SAML
       * assertions token with the IDP (Identity Provider) when the SAML assertions
       * token is expiring.
       * @param authorization the expiring object with the SAML assertions token and expiry
       * @param renewer the renewer callback
       */
		public SAMLAuthContext(ExpiringSAMLAuth authorization, RenewerCallback renewer) {
		    this.authorization = authorization;
		    this.renewer = renewer;
		}

		/** Gets the SAML authentication token
		 * @return the SAML authentication token.
		 */
		public String getToken() {
			if (token == null && authorization != null)
			    return authorization.getAuthorizationToken();
			return token;
		}

      /**
       * Gets the authorizer callback when specified during construction of the SAMLAuthContext.
       * @return the callback
       */
		public AuthorizerCallback getAuthorizer() {
            return authorizer;
        }
      /**
       * Gets the renewer callback when specified during construction of the SAMLAuthContext.
       * @return the callback
       */
        public RenewerCallback getRenewer() {
            return renewer;
        }
      /**
       * Gets the object with the SAML assertions token and expiration when specified during
       * construction of the SAMLAuthContext or renewed by the renewer callback.
       * @return the object with the assertions token and expiration
       */
        public ExpiringSAMLAuth getAuthorization() {
            return authorization;
        }

		/**
		 * ExpiringSAMLAuth is used by SAMLAuthContext when renewing a SAML assertions token.
		 */
		public interface ExpiringSAMLAuth {
		    /**
             * Gets the SAML assertions token
		     * @return the token.
		     */
	        public String getAuthorizationToken();
	        /**
             * Gets the expiration time stamp specified for the SAML assertions token
	         * @return the expiration time stamp
	         */
	        public Instant getExpiry();
	    }

		/**
         * Constructs an ExpiringSAMLAuth with a SAML assertions token and the expiration time stamp
         * for the token.
         * @param authorizationToken refers to the new SAML token.
         * @param expiry refers to the expiration time stamp of authorizationToken.
         * @return an ExpiringSAMLAuth instance.
         */
		public static ExpiringSAMLAuth newExpiringSAMLAuth(final String authorizationToken, final Instant expiry) {
            return new ExpiringSAMLAuth() {
                @Override
                public Instant getExpiry() {
                    return expiry;
                }
                @Override
                public String getAuthorizationToken() {
                    return authorizationToken;
                }
            };
        }

      /**
       * A callback for getting a SAML assertions token from the IDP (Identity Provider).
       */
      @FunctionalInterface
      public interface AuthorizerCallback extends Function<ExpiringSAMLAuth, ExpiringSAMLAuth> { }

      /**
       * A callback for renewing the SAML assertions token with the IDP (Identity Provider)
       * by extending the expiration time.
       */
      @FunctionalInterface
      public interface RenewerCallback extends Function<ExpiringSAMLAuth, Instant> { }

      /**
       * Configures the SSL context and trust manager for a SAML authorization context
       * @param context - the SSLContext object required for the SSL connection
       * @param trustManager - X509TrustManager with which we initialize the SSLContext
       * @return this SAML authorization context for chained configuration
       */
		@Override
		public SAMLAuthContext withSSLContext(SSLContext context, X509TrustManager trustManager) {
			this.sslContext = context;
			this.trustManager = trustManager;
			return this;
		}
      /**
       * Configures the SSL hostname verifier for a SAML authorization context
       * @param verifier	the host verifier
       * @return this SAML authorization context for chained configuration
       */
		@Override
		public SAMLAuthContext withSSLHostnameVerifier(SSLHostnameVerifier verifier) {
			this.sslVerifier = verifier;
			return this;
		}

      /**
       * Gets the trust manager when using SSL.
       * @return the X509TrustManager used for authentication
       */
      public X509TrustManager getTrustManager() {
          return trustManager;
      }
      /**
       * Gets the SSL context when using SSL.
       * @return the SSLContext used for authentication
       */
		@Override
		public SSLContext getSSLContext() {
			return sslContext;
		}

      /**
       * Gets the hostname verifier when using SSL.
       * @return the hostname verifier used for authentication
       */
		@Override
		public SSLHostnameVerifier getSSLHostnameVerifier() {
			return sslVerifier;
		}

		@Override
		public void setSSLHostnameVerifier(SSLHostnameVerifier verifier) {
			this.sslVerifier = verifier;
		}
	}

  public static class KerberosConfig {

    private boolean refreshKrb5Config;
    private String principal = null;
    private boolean useTicketCache = true;
    private String ticketCache = null;
    private boolean renewTGT = false;
    private boolean doNotPrompt = true;
    private boolean useKeyTab = false;
    private String keyTab = null;
    private boolean storeKey = false;
    private boolean isInitiator = true;
    private boolean useFirstPass = false;
    private boolean tryFirstPass = false;
    private boolean storePass = false;
    private boolean clearPass = false;
    private boolean debug = false;

    public KerberosConfig() {
    }

    public KerberosConfig withRefreshKrb5Config(boolean refreshKrb5Config) {
      this.refreshKrb5Config = refreshKrb5Config;
      return this;
    }

    public String getRefreshKrb5Config() {
      return String.valueOf(this.refreshKrb5Config);
    }

    public KerberosConfig withPrincipal(String principal) {
      this.principal = principal;
      return this;
    }

    public String getPrincipal() {
      return this.principal;
    }

    public KerberosConfig withUseTicketCache(boolean useTicketCache) {
      this.useTicketCache = useTicketCache;
      return this;
    }

    public String getUseTicketCache() {
      return String.valueOf(this.useTicketCache);
    }

    public KerberosConfig withTicketCache(String ticketCache) {
      this.ticketCache = ticketCache;
      return this;
    }

    public String getTicketCache() {
      return this.ticketCache;
    }

    public KerberosConfig withRenewTGT(boolean renewTGT) {
      this.renewTGT = renewTGT;
      return this;
    }

    public String getRenewTGT() {
      return String.valueOf(this.renewTGT);
    }

    public KerberosConfig withDoNotPrompt(boolean doNotPrompt) {
      this.doNotPrompt = doNotPrompt;
      return this;
    }

    public String getDoNotPrompt() {
      return String.valueOf(this.doNotPrompt);
    }

    public KerberosConfig withUseKeyTab(boolean useKeyTab) {
      this.useKeyTab = useKeyTab;
      return this;
    }

    public String getUseKeyTab() {
      return String.valueOf(this.useKeyTab);
    }

    public KerberosConfig withKeyTab(String keyTab) {
      this.keyTab = keyTab;
      return this;
    }

    public String getKeyTab() {
      return this.keyTab;
    }

    public KerberosConfig withStoreKey(boolean storeKey) {
      this.storeKey = storeKey;
      return this;
    }

    public String getStoreKey() {
      return String.valueOf(this.storeKey);
    }

    public KerberosConfig withUseFirstPass(boolean useFirstPass) {
      this.useFirstPass = useFirstPass;
      return this;
    }

    public String getUseFirstPass() {
      return String.valueOf(this.useFirstPass);
    }

    public KerberosConfig withTryFirstPass(boolean tryFirstPass) {
      this.tryFirstPass = tryFirstPass;
      return this;
    }

    public String getTryFirstPass() {
      return String.valueOf(this.tryFirstPass);
    }

    public KerberosConfig withStorePass(boolean storePass) {
      this.storePass = storePass;
      return this;
    }

    public String getStorePass() {
      return String.valueOf(this.storePass);
    }

    public KerberosConfig withClearPass(boolean clearPass) {
      this.clearPass = clearPass;
      return this;
    }

    public String getClearPass() {
      return String.valueOf(this.clearPass);
    }

    public KerberosConfig withInitiator(boolean initiator) {
      this.isInitiator = initiator;
      return this;
    }

    public String getInitiator() {
      return String.valueOf(this.isInitiator);
    }

    public KerberosConfig withDebug(boolean debug) {
      this.debug = debug;
      return this;
    }

    public String getDebug() {
      return String.valueOf(this.debug);
    }

    public Map<String, String> toOptions() {
      Map<String, String> options = new HashMap<>();
      options.put("refreshKrb5Config", getRefreshKrb5Config());
      if (getPrincipal() != null)
        options.put("principal", getPrincipal());
      options.put("useTicketCache", getUseTicketCache());
      if (getUseTicketCache().equals("true") && getTicketCache() != null)
        options.put("ticketCache", getTicketCache());
      options.put("renewTGT", getRenewTGT());
      options.put("doNotPrompt", getDoNotPrompt());
      options.put("useKeyTab", getUseKeyTab());
      if (getUseKeyTab().equals("true") && getKeyTab() != null)
        options.put("keyTab", getKeyTab());
      options.put("storeKey", getStoreKey());
      options.put("useFirstPass", getUseFirstPass());
      options.put("tryFirstPass", getTryFirstPass());
      options.put("storePass", getStorePass());
      options.put("clearPass", getClearPass());
      options.put("isInitiator", getInitiator());
      options.put("debug", getDebug());
      return options;
    }

  }

  public static class CertificateAuthContext extends AuthContext {
    String certFile;
    String certPassword;

	/**
     * Creates a CertificateAuthContext by initializing the SSLContext of the
     * HTTPS channel with the SSLContext object passed and using the TrustManger
     * passed. The KeyManager of the SSLContext should be initialized with the
     * appropriate client certificate and client's private key
     *
     * @param context the SSLContext with which we initialize the
     *          CertificateAuthContext
     * @param trustManager the X509TrustManager object which is responsible for
     *                     deciding if a credential should be trusted or not.
     */
    public CertificateAuthContext(SSLContext context, X509TrustManager trustManager) {
      this.sslContext = context;
      this.trustManager = trustManager;
    }

    /**
     * Creates a CertificateAuthContext by initializing the SSLContext of the
     * HTTPS channel with the SSLContext object passed and assigns the
     * SSLHostnameVerifier passed to be used for checking host names. The
     * KeyManager of the SSLContext should be initialized with the appropriate
     * client certificate and client's private key
     *
     * @param context the SSLContext with which we initialize the
     *          CertificateAuthContext
     * @param verifier a callback for checking host names
     * @param trustManager the X509TrustManager object which is responsible for
     *                     deciding if a credential should be trusted or not.
     */
    public CertificateAuthContext(SSLContext context, SSLHostnameVerifier verifier, X509TrustManager trustManager) {
      this.sslContext = context;
      this.sslVerifier = verifier;
      this.trustManager = trustManager;
    }

    /**
     * Creates a CertificateAuthContext with a PKCS12 file. The SSLContext is
     * created from the information in the PKCS12 file. This constructor should
     * be called when the export password of the PKCS12 file is empty.
     *
     * @param certFile the p12 file which contains the client's private key and
     *          the client's certificate chain
     * @param trustManager the X509TrustManager object which is responsible for
     *          deciding if a credential should be trusted or not.
     * @throws CertificateException if any of the certificates in the certFile
     *           cannot be loaded
     * @throws UnrecoverableKeyException if the certFile has an export password
     * @throws KeyManagementException if initializing the SSLContext with the
     *           KeyManager fails
     * @throws IOException if there is an I/O or format problem with the
     *           keystore data, if a password is required but not given, or if
     *           the given password was incorrect or if the certFile path is
     *           invalid or if the file is not found If the error is due to a
     *           wrong password, the cause of the IOException should be an
     *           UnrecoverableKeyException.
     */
    public CertificateAuthContext(String certFile, X509TrustManager trustManager)
        throws CertificateException, IOException,
        UnrecoverableKeyException, KeyManagementException {
        this.certFile = certFile;
        this.trustManager = trustManager;
        this.certPassword = "";
        this.sslContext = createSSLContext();
      }

    /**
     * Creates a CertificateAuthContext with a PKCS12 file. The SSLContext
     * is created from the information in the PKCS12 file. This constructor
     * should be called when the export password of the PKCS12 file is non-empty.
     * @param certFile the p12 file which contains the client's private key
     *          and the client's certificate chain
     * @param trustManager the X509TrustManager object which is responsible for
     *                     deciding if a credential should be trusted or not.
     * @param certPassword the export password of the p12 file
     * @throws CertificateException if any of the certificates in the certFile cannot be loaded
     * @throws UnrecoverableKeyException if the certFile has an export password
     * @throws KeyManagementException if initializing the SSLContext with the KeyManager fails
     * @throws IOException if there is an I/O or format problem with the keystore data,
     *                     if a password is required but not given, or if the given password was
     *                     incorrect or if the certFile path is invalid or if the file is not found
     *                     If the error is due to a wrong password, the cause of the IOException
     *                     should be an UnrecoverableKeyException.
     */
    public CertificateAuthContext(String certFile, String certPassword, X509TrustManager trustManager)
        throws CertificateException, IOException,
        UnrecoverableKeyException, KeyManagementException {
        this.certFile = certFile;
        this.certPassword = certPassword;
        this.trustManager = trustManager;
        this.sslContext = createSSLContext();
      }

    private SSLContext createSSLContext()
      throws CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
      if(certPassword == null) {
        throw new IllegalArgumentException("Certificate export password must not be null");
      }
      KeyStore keyStore = null;
      KeyManagerFactory keyManagerFactory = null;
      KeyManager[] keyMgr = null;
      try {
        keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
      } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException(
          "CertificateAuthContext requires KeyManagerFactory.getInstance(\"SunX509\")");
      }
      try {
        keyStore = KeyStore.getInstance("PKCS12");
      } catch (KeyStoreException e) {
        throw new IllegalStateException("CertificateAuthContext requires KeyStore.getInstance(\"PKCS12\")");
      }
      try {
        FileInputStream certFileStream = new FileInputStream(certFile);
        try {
          keyStore.load(certFileStream, certPassword.toCharArray());
        } finally {
          if (certFileStream != null)
            certFileStream.close();
        }
        keyManagerFactory.init(keyStore, certPassword.toCharArray());
        keyMgr = keyManagerFactory.getKeyManagers();
        sslContext = SSLContext.getInstance("TLSv1.2");
      } catch (NoSuchAlgorithmException | KeyStoreException e) {
        throw new IllegalStateException("The certificate algorithm used or the Key store "
          + "Service provider Implementaion (SPI) is invalid. CertificateAuthContext "
          + "requires SunX509 algorithm and PKCS12 Key store SPI", e);
      }
      TrustManager[] trustManagers = trustManager == null ? null : new TrustManager[] {trustManager};
      sslContext.init(keyMgr, trustManagers, null);
      return sslContext;
    }

    @Override
    public CertificateAuthContext withSSLHostnameVerifier(SSLHostnameVerifier verifier) {
      this.sslVerifier = verifier;
      return this;
    }

    public String getCertificate() {
      return certFile;
    }

    public String getCertificatePassword() {
      return certPassword;
    }
  }

	/**
	 * Creates a client to access the database by means of a REST server with connection and authentication information
	 * retrieved from the given {@code propertySource}. The {@code propertySource} function will be invoked once for
	 * each of the below properties, giving the function a chance to return a value associated with the property if
	 * desired. The set of values returned for the below property names will then be used to construct and return a new
	 * {@code DatabaseClient} instance.
	 *
	 * <ol>
	 *     <li>marklogic.client.host = required; must be a String</li>
	 *     <li>marklogic.client.port = required; must be an int, Integer, or String that can be parse as an int</li>
	 *     <li>marklogic.client.basePath = must be a String</li>
	 *     <li>marklogic.client.database = must be a String</li>
	 *     <li>marklogic.client.connectionType = must be a String or instance of {@code ConnectionType}</li>
	 *     <li>marklogic.client.disableGzippedResponses = can be a String or Boolean; if "true" or true, the client
	 *     will not send an "Accept-Encoding" request header with a value of "gzip" on each request; supported
	 *     since 6.3.0.</li>
	 *     <li>marklogic.client.securityContext = an instance of {@code SecurityContext}; if set, then all other
	 *     authentication properties pertaining to the construction of a {@code SecurityContext} will be ignored,
	 *     including the properties pertaining to SSL; this is effectively an escape hatch for providing a
	 *     {@code SecurityContext} in case an appropriate one cannot be created via the other supported properties</li>
	 *     <li>marklogic.client.authType = determines the type of authentication to use; required if
	 *     marklogic.client.securityContext is not set; must be a String and one of "basic", "digest", "cloud",
	 *     "kerberos", "certificate", or "saml"</li>
	 *     <li>marklogic.client.username = must be a String; required for basic and digest authentication</li>
	 *     <li>marklogic.client.password = must be a String; required for basic and digest authentication</li>
	 *     <li>marklogic.client.certificate.file = must be a String; optional for certificate authentication</li>
	 *     <li>marklogic.client.certificate.password = must be a String; optional for certificate authentication</li>
	 *     <li>marklogic.client.cloud.apiKey = must be a String; required for cloud authentication</li>
	 *     <li>marklogic.client.cloud.tokenDuration = must be a number; optional for configuring the duration in
	 *     minutes for which an access token lasts; supported since 6.3.0.</li>
	 *     <li>marklogic.client.kerberos.principal = must be a String; required for Kerberos authentication</li>
	 *     <li>marklogic.client.saml.token = must be a String; required for SAML authentication</li>
	 *     <li>marklogic.client.oauth.token = must be a String; required for OAuth authentication; supported since 6.6.0.</li>
	 *     <li>marklogic.client.sslContext = must be an instance of {@code javax.net.ssl.SSLContext}</li>
	 *     <li>marklogic.client.sslProtocol = must be a String; if "default', then uses the JVM default SSL
	 *     context; else, the value is passed to the {@code getInstance} method in {@code javax.net.ssl.SSLContext}</li>
	 *     <li>marklogic.client.sslHostnameVerifier = must either be an instance of {@code SSLHostnameVerifier} or
	 *     a String with a value of either "any", "common", or "strict"</li>
	 *     <li>marklogic.client.trustManager = must be an instance of {@code javax.net.ssl.X509TrustManager};
	 *     if not specified and an SSL context is configured, an attempt will be made to use the JVM's default trust manager</li>
	 *     <li>marklogic.client.ssl.keystore.path = must be a String; enables 2-way SSL if set; since 6.4.0.</li>
	 *     <li>marklogic.client.ssl.keystore.password = must be a String; optional password for a key store; since 6.4.0.</li>
	 *     <li>marklogic.client.ssl.keystore.type = must be a String; optional type for a key store, defaults to "JKS"; since 6.4.0.</li>
	 *     <li>marklogic.client.ssl.keystore.algorithm = must be a String; optional algorithm for a key store, defaults to "SunX509"; since 6.4.0.</li>
	 *     <li>marklogic.client.ssl.truststore.path = must be a String; specifies a file path for a trust store for SSL and/or certificate authentication; since 6.5.0.</li>
	 *     <li>marklogic.client.ssl.truststore.password = must be a String; optional password for a trust store; since 6.5.0.</li>
	 *     <li>marklogic.client.ssl.truststore.type = must be a String; optional type for a trust store, defaults to "JKS"; since 6.5.0.</li>
	 *     <li>marklogic.client.ssl.truststore.algorithm = must be a String; optional algorithm for a trust store, defaults to "SunX509"; since 6.5.0.</li>
	 * </ol>
	 *
	 * @param propertySource
	 * @return
	 * @since 6.1.0
	 */
	public static DatabaseClient newClient(Function<String, Object> propertySource) {
		return new DatabaseClientPropertySource(propertySource).newClient();
	}

  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host the host with the REST server
   * @param port the port for the REST server
   * @param securityContext the security context created depending upon the
   *            authentication type and communication channel type (SSL)
   * @return a new client for making database requests
   */
  static public DatabaseClient newClient(String host, int port, SecurityContext securityContext) {
    return newClient(host, port, null, securityContext, null);
  }

  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host the host with the REST server
   * @param port the port for the REST server
   * @param securityContext the security context created depending upon the
   *            authentication type and communication channel type (SSL)
   * @param connectionType whether the client connects directly to the MarkLogic host
   *            or using a gateway such as a load balancer
   * @return a new client for making database requests
   */
  static public DatabaseClient newClient(String host, int port, SecurityContext securityContext,
                                         DatabaseClient.ConnectionType connectionType)
  {
    return newClient(host, port, null, securityContext, connectionType);
  }

  /**
   * Creates a client to access the database by means of a REST server.
   *
   * A data service interface can only call an endpoint for the configured content database
   * of the appserver. You cannot specify the database when constructing a client for working
   * with a data service.
   *
   * @param host the host with the REST server
   * @param port the port for the REST server
   * @param database the database to access (default: configured database for
   *            the REST server)
   * @param securityContext the security context created depending upon the
   *            authentication type and communication channel type (SSL)
   * @return a new client for making database requests
   */
  static public DatabaseClient newClient(String host, int port, String database, SecurityContext securityContext) {
    return newClient(host, port, database, securityContext, null);
  }

  /**
   * Creates a client to access the database by means of a REST server.
   *
   * A data service interface can only call an endpoint for the configured content database
   * of the appserver. You cannot specify the database when constructing a client for working
   * with a data service.
   *
   * @param host the host with the REST server
   * @param port the port for the REST server
   * @param database the database to access (default: configured database for
   *            the REST server)
   * @param securityContext the security context created depending upon the
   *            authentication type and communication channel type (SSL)
   * @param connectionType whether the client connects directly to the MarkLogic host
   *            or using a gateway such as a load balancer
   * @return a new client for making database requests
   */
  static public DatabaseClient newClient(String host, int port, String database,
                                         SecurityContext securityContext,
                                         DatabaseClient.ConnectionType connectionType) {
      return newClient(host, port, null, database, securityContext, connectionType);
  }

	/**
	 * Creates a client to access the database by means of a REST server.
	 *
	 * A data service interface can only call an endpoint for the configured content database
	 * of the appserver. You cannot specify the database when constructing a client for working
	 * with a data service.
	 *
	 * @param host the host with the REST server
	 * @param port the port for the REST server
	 * @param basePath optional base path, typically used when connecting to MarkLogic via a reverse proxy; since 6.1.0
	 * @param database the database to access (default: configured database for
	 *            the REST server)
	 * @param securityContext the security context created depending upon the
	 *            authentication type and communication channel type (SSL)
	 * @param connectionType whether the client connects directly to the MarkLogic host
	 *            or using a gateway such as a load balancer
	 * @return a new client for making database requests
	 * @since 6.1.0
	 */
  static public DatabaseClient newClient(String host, int port, String basePath, String database,
                                         SecurityContext securityContext,
                                         DatabaseClient.ConnectionType connectionType) {
      RESTServices services = new OkHttpServices();
	  // As of 6.1.0, the following optimization is made as it's guaranteed that if the user is connecting to a
	  // MarkLogic Cloud instance, then port 443 will be used. Every path for constructing a DatabaseClient goes through
	  // this method, ensuring that this optimization will always be applied, and thus freeing the user from having to
	  // worry about what port to configure when using MarkLogic Cloud.
	  if (securityContext instanceof MarkLogicCloudAuthContext) {
		  port = 443;
	  }
      services.connect(host, port, basePath, database, securityContext);

      if (clientConfigurators != null) {
		  clientConfigurators.forEach(configurator -> {
			  if (configurator instanceof OkHttpClientConfigurator) {
				  OkHttpClient okHttpClient = (OkHttpClient) services.getClientImplementation();
				  OkHttpClient.Builder clientBuilder = okHttpClient.newBuilder();
				  ((OkHttpClientConfigurator) configurator).configure(clientBuilder);
				  ((OkHttpServices) services).setClientImplementation(clientBuilder.build());
			  } else if (configurator instanceof HttpClientConfigurator) {
				  // do nothing as we no longer use HttpClient so there's nothing this can configure
			  } else {
				  throw new IllegalArgumentException("A ClientConfigurator must implement OkHttpClientConfigurator");
			  }
		  });
      }

      DatabaseClientImpl client = new DatabaseClientImpl(
          services, host, port, basePath, database, securityContext, connectionType
      );
      client.setHandleRegistry(getHandleRegistry().copy());
      return client;
  }

  /**
   * Returns the default registry with factories for creating handles
   * as adapters for IO representations. To create custom registries,
   * use
   * @return	the default registry
   */
  static public HandleFactoryRegistry getHandleRegistry() {
    return handleRegistry;
  }
  /**
   * Removes the current registered associations so the
   * handle registry is empty.
   */
  static public void clearHandleRegistry() {
    handleRegistry = new HandleFactoryRegistryImpl();
  }
  /**
   * Initializes a handle registry with the default associations
   * between the content handles provided by the API and the supported
   * IO representation classes.  Use this method only after clearing
   * or overwriting associations in the handle registry.
   */
  static public void registerDefaultHandles() {
    HandleFactoryRegistryImpl.registerDefaults(getHandleRegistry());
  }

  /**
   * Adds a listener that provides custom configuration when a communication library
   * is created.
   *
   * As of 6.3.0, this method can now be called multiple times. When a {@code DatabaseClient} is constructed,
   * configurators will be invoked in the order they were passed in.
   *
   * @see com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator
   * @param configurator	the listener for configuring the communication library
   */
  static public void addConfigurator(ClientConfigurator<?> configurator) {
    if (!HttpClientConfigurator.class.isInstance(configurator) && !OkHttpClientConfigurator.class.isInstance(configurator)) {
      throw new IllegalArgumentException(
        "Configurator must implement OkHttpClientConfigurator"
      );
    }

    clientConfigurators.add(configurator);
  }

	/**
	 * Removes any instances of {@code ClientConfigurator} that were passed in via {@code addConfigurator}.
	 *
	 * @since 6.3.0
	 */
	static public void removeConfigurators() {
	  clientConfigurators.clear();
  }

  /**
   * A Database Client Factory Bean provides an object for specifying configuration
   * before creating a client to make database requests.
   */
  static public class Bean implements Serializable {
    private static final long serialVersionUID = 1L;

    private           String                host;
    private           int                   port;
    private String basePath;
    private           String                database;
    private           DatabaseClient.ConnectionType connectionType;
    transient private SecurityContext       securityContext;
    transient private HandleFactoryRegistry handleRegistry =
      HandleFactoryRegistryImpl.newDefault();

    /**
     * Zero-argument constructor for bean applications. Other
     * applications can use the static newClient() factory methods
     * of DatabaseClientFactory.
     */
    public Bean() {
      super();
    }


    /**
     * Returns the host for clients created with a
     * DatabaseClientFactory.Bean object.
     * @return	the client host
     */
    public String getHost() {
      return host;
    }
    /**
     * Specifies the host for clients created from a
     * DatabaseClientFactory.Bean object.
     * @param host	the client host
     */
    public void setHost(String host) {
      this.host = host;
    }
    /**
     * Returns the port for clients created with a
     * DatabaseClientFactory.Bean object.
     * @return	the client port
     */
    public int getPort() {
      return port;
    }
    /**
     * Specifies the port for clients created with a
     * DatabaseClientFactory.Bean object.
     * @param port	the client port
     */
    public void setPort(int port) {
      this.port = port;
    }

	  /**
	   *
	   * @return optional base path to use for constructing a client
	   * @since 6.1.0
	   */
      public String getBasePath() {
          return basePath;
      }

	  /**
	   * Set a base path to use for constructing a client
	   *
	   * @param basePath
	   * @since 6.1.0
	   */
      public void setBasePath(String basePath) {
          this.basePath = basePath;
      }

      /**
     * Returns the database for clients created with a
     * DatabaseClientFactory.Bean object.
     * @return	the database
     */
    public String getDatabase() {
      return database;
    }
    /**
     * Specifies the database for clients created with a
     * DatabaseClientFactory.Bean object.
     *
     * A data service interface can only call an endpoint for the configured content database
     * of the appserver. You cannot specify the database when constructing a client for working
     * with a data service.
     *
     * @param database	a database to pass along to new DocumentManager and QueryManager instances
     */
    public void setDatabase(String database) {
      this.database = database;
    }
    /**
     * Returns the security context for clients created with a
     * DatabaseClientFactory.Bean object - BasicAuthContext, DigestAuthContext
     * or KerberosAuthContext
     * @return	the security context
     */
    public SecurityContext getSecurityContext() {
      return securityContext;
    }
    /**
     * Specifies the security context for clients created with a
     * DatabaseClientFactory.Bean object
     * @param securityContext	the security context - BasicAuthContext,
     * DigestAuthContext or KerberosAuthContext
     */
    public void setSecurityContext(SecurityContext securityContext) {
      this.securityContext = securityContext;
    }
    /**
     * Identifies whether the client connects directly with a MarkLogic host
     * or by means of a gateway such as a load balancer.
     * @return	the connection type
     */
    public DatabaseClient.ConnectionType getConnectionType() {
      return connectionType;
    }
    /**
     * Specify whether the client connects directly with a MarkLogic host
     * or by means of a gateway such as a load balancer.
     * @param connectionType	the connection type
     */
    public void setConnectionType(DatabaseClient.ConnectionType connectionType) {
      this.connectionType = connectionType;
    }

    /**
     * Returns the registry for associating
     * IO representation classes with handle factories.
     * @return	the registry instance
     */
    public HandleFactoryRegistry getHandleRegistry() {
      return handleRegistry;
    }
    /**
     * Removes the current registered associations so the
     * handle registry is empty.
     */
    public void clearHandleRegistry() {
      this.handleRegistry = new HandleFactoryRegistryImpl();
    }
    /**
     * Initializes a handle registry with the default associations
     * between the content handles provided by the API and the supported
     * IO representation classes.  Use this method only after clearing
     * or overwriting associations in the handle registry.
     */
    public void registerDefaultHandles() {
      HandleFactoryRegistryImpl.registerDefaults(getHandleRegistry());
    }

    /**
     * Creates a client for bean applications based on the properties.
     * Other applications can use the static newClient() factory methods
     * of DatabaseClientFactory.
     * The client accesses the database by means of a REST server.
     * @return	a new client for making database requests
     */
    public DatabaseClient newClient() {
        DatabaseClientImpl client = (DatabaseClientImpl) DatabaseClientFactory.newClient(
            host, port, basePath, database, securityContext, connectionType);
        client.setHandleRegistry(getHandleRegistry().copy());
        return client;
    }
  }
}
