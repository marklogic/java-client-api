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
package com.marklogic.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator;
import com.marklogic.client.extra.httpclient.HttpClientConfigurator;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.impl.HandleFactoryRegistryImpl;
import com.marklogic.client.impl.OkHttpServices;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;

/**
 * A Database Client Factory configures a database client for making
 * database requests.
 */
public class DatabaseClientFactory {

  static private ClientConfigurator<?> clientConfigurator;
  static private HandleFactoryRegistry handleRegistry =
    HandleFactoryRegistryImpl.newDefault();

  /**
   * Authentication enumerates the methods for verifying a user and
   * password with the database.
   * @deprecated (as of 4.0.1) use BasicAuthContext, DigestAuthContext and KerberosAuthContext classes
   */
  @Deprecated
  public enum Authentication {
    /**
     * Minimal security unless used with SSL.
     */
    BASIC,
    /**
     * Moderate security without SSL.
     */
    DIGEST,
    /**
     * Authentication using Kerberos.
     */
    KERBEROS,
    /**
     * Authentication using Certificates;
     */
    CERTIFICATE;
    /**
     * Returns the enumerated value for the case-insensitive name.
     * @param name	the name of the enumerated value
     * @return	the enumerated value
     */
    static public Authentication valueOfUncased(String name) {
      return Authentication.valueOf(name.toUpperCase());
    }
  }

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
    SSLContext getSSLContext();
    void setSSLContext(SSLContext context);
    SSLHostnameVerifier getSSLHostnameVerifier();
    void setSSLHostnameVerifier(SSLHostnameVerifier verifier);
    @Deprecated
    SecurityContext withSSLContext(SSLContext context);

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
    public void setSSLContext(SSLContext context) {
      this.sslContext = context;
    }

    @Override
    public SSLHostnameVerifier getSSLHostnameVerifier() {
      return sslVerifier;
    }

    @Override
    public void setSSLHostnameVerifier(SSLHostnameVerifier verifier) {
      this.sslVerifier = verifier;
    }

    @Override
    @Deprecated
    public SecurityContext withSSLContext(SSLContext context) {
      this.sslContext = context;
      return this;
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
    @Deprecated
    public BasicAuthContext withSSLContext(SSLContext context) {
      this.sslContext = context;
      return this;
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
    @Deprecated
    public DigestAuthContext withSSLContext(SSLContext context) {
      this.sslContext = context;
      return this;
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
    @Deprecated
    public KerberosAuthContext withSSLContext(SSLContext context) {
      this.sslContext = context;
      return this;
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
     * Creates a CertificateAuthContext by initializing the SSLContext
     * of the HTTPS channel with the SSLContext object passed. The KeyManager of
     * the SSLContext should be initialized with the appropriate client
     * certificate and client's private key
     * @param context the SSLContext with which we initialize the
     *				  CertificateAuthContext
     */
    @Deprecated
    public CertificateAuthContext(SSLContext context) {
      this.sslContext = context;
    }

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
     * Creates a CertificateAuthContext by initializing the SSLContext
     * of the HTTPS channel with the SSLContext object passed and assigns the
     * SSLHostnameVerifier passed to be used for checking host names. The KeyManager of
     * the SSLContext should be initialized with the appropriate client
     * certificate and client's private key
     * @param context the SSLContext with which we initialize the
     *				  CertificateAuthContext
     * @param verifier a callback for checking host names
     */
    @Deprecated
    public CertificateAuthContext(SSLContext context, SSLHostnameVerifier verifier) {
      this.sslContext = context;
      this.sslVerifier = verifier;
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
    @Deprecated
    public CertificateAuthContext(String certFile)
      throws CertificateException, IOException,
      UnrecoverableKeyException, KeyManagementException {
      this.certFile = certFile;
      this.certPassword = "";
      this.sslContext = createSSLContext();
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
     * 					and the client's certificate chain
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
    @Deprecated
    public CertificateAuthContext(String certFile, String certPassword)
      throws CertificateException, IOException,
      UnrecoverableKeyException, KeyManagementException {
      this.certFile = certFile;
      this.certPassword = certPassword;
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
   * Creates a client to access the database by means of a REST server
   * without any authentication. Such clients can be convenient for
   * experimentation but should not be used in production.
   *
   * @param host	the host with the REST server
   * @param port	the port for the REST server
   * @return	a new client for making database requests
   */
  static public DatabaseClient newClient(String host, int port) {
    return newClient(host, port, null, null, null, null, null, null);
  }

  /**
   * Creates a client to access the database by means of a REST server
   * without any authentication. Such clients can be convenient for
   * experimentation but should not be used in production.
   *
   * @param host	the host with the REST server
   * @param port	the port for the REST server
   * @param database	the database to access (default: configured database for the REST server)
   * @return	a new client for making database requests
   */
  static public DatabaseClient newClient(String host, int port, String database) {
    return newClient(host, port, database, null, null, null, null, null);
  }

  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host the host with the REST server
   * @param port the port for the REST server
   * @param securityContext the security context created depending upon the
   *            authentication type - BasicAuthContext, DigestAuthContext or KerberosAuthContext
   *            and communication channel type (SSL)
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
   *            authentication type - BasicAuthContext, DigestAuthContext or KerberosAuthContext
   *            and communication channel type (SSL)
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
   * @param host the host with the REST server
   * @param port the port for the REST server
   * @param database the database to access (default: configured database for
   *            the REST server)
   * @param securityContext the security context created depending upon the
   *            authentication type - BasicAuthContext, DigestAuthContext or KerberosAuthContext
   *            and communication channel type (SSL)
   * @return a new client for making database requests
   */
  static public DatabaseClient newClient(String host, int port, String database, SecurityContext securityContext) {
    return newClient(host, port, database, securityContext, null);
  }

  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host the host with the REST server
   * @param port the port for the REST server
   * @param database the database to access (default: configured database for
   *            the REST server)
   * @param securityContext the security context created depending upon the
   *            authentication type - BasicAuthContext, DigestAuthContext or KerberosAuthContext
   *            and communication channel type (SSL)
   * @param connectionType whether the client connects directly to the MarkLogic host
   *            or using a gateway such as a load balancer
   * @return a new client for making database requests
   */
  static public DatabaseClient newClient(String host, int port, String database,
                                         SecurityContext securityContext,
                                         DatabaseClient.ConnectionType connectionType)
  {
    String user = null;
    Map<String,String> kerberosOptions = null;
    String password = null;
    Authentication type = null;
    SSLContext sslContext = null;
    SSLHostnameVerifier sslVerifier = null;
    X509TrustManager trustManager = null;
    if (securityContext instanceof BasicAuthContext) {
      BasicAuthContext basicContext = (BasicAuthContext) securityContext;
      user = basicContext.user;
      password = basicContext.password;
      type = Authentication.BASIC;
      if (basicContext.sslContext != null) {
        sslContext = basicContext.sslContext;
        if(basicContext.trustManager != null) trustManager = basicContext.trustManager;
        if (basicContext.sslVerifier != null) {
          sslVerifier = basicContext.sslVerifier;
        } else {
          sslVerifier = SSLHostnameVerifier.COMMON;
        }
      }
    } else if (securityContext instanceof DigestAuthContext) {
      DigestAuthContext digestContext = (DigestAuthContext) securityContext;
      user = digestContext.user;
      password = digestContext.password;
      type = Authentication.DIGEST;
      if (digestContext.sslContext != null) {
        sslContext = digestContext.sslContext;
        if(digestContext.trustManager != null) trustManager = digestContext.trustManager;
        if (digestContext.sslVerifier != null) {
          sslVerifier = digestContext.sslVerifier;
        } else {
          sslVerifier = SSLHostnameVerifier.COMMON;
        }
      }
    } else if (securityContext instanceof KerberosAuthContext) {
      KerberosAuthContext kerberosContext = (KerberosAuthContext) securityContext;
      kerberosOptions = kerberosContext.krbOptions;
      type = Authentication.KERBEROS;
      if (kerberosContext.sslContext != null) {
        sslContext = kerberosContext.sslContext;
        if(kerberosContext.trustManager != null) trustManager = kerberosContext.trustManager;
        if (kerberosContext.sslVerifier != null) {
          sslVerifier = kerberosContext.sslVerifier;
        } else {
          sslVerifier = SSLHostnameVerifier.COMMON;
        }
      }
    } else if (securityContext instanceof CertificateAuthContext) {
      CertificateAuthContext certificateContext = (CertificateAuthContext) securityContext;
      type = Authentication.CERTIFICATE;
      sslContext = certificateContext.getSSLContext();
      if(certificateContext.trustManager != null) trustManager = certificateContext.trustManager;
      if (certificateContext.sslVerifier != null) {
        sslVerifier = certificateContext.sslVerifier;
      } else {
        sslVerifier = SSLHostnameVerifier.COMMON;
      }
    } else {
      throw new IllegalArgumentException("securityContext must be of type BasicAuthContext, " +
        "DigestAuthContext, KerberosAuthContext, or CertificateAuthContext");
    }

    OkHttpServices services = new OkHttpServices();
    services.connect(host, port, database, user, password, kerberosOptions, type, sslContext, trustManager, sslVerifier);

    if (clientConfigurator != null) {
      if ( clientConfigurator instanceof OkHttpClientConfigurator ) {
        OkHttpClient client = services.getClientImplementation();
        OkHttpClient.Builder clientBuilder = client.newBuilder();
        ((OkHttpClientConfigurator) clientConfigurator).configure(
          clientBuilder
        );
        ((OkHttpServices) services).setClientImplementation(clientBuilder.build());
      } else if ( clientConfigurator instanceof HttpClientConfigurator ) {
        // do nothing as we not longer use HttpClient so there's nothing this can configure
      } else {
        throw new IllegalArgumentException("A ClientConfigurator must implement OkHttpClientConfigurator");
      }
    }

    DatabaseClientImpl client = new DatabaseClientImpl(
          services, host, port, database, securityContext, connectionType
    );
    client.setHandleRegistry(getHandleRegistry().copy());
    return client;
  }


  static private SecurityContext makeSecurityContext(String user, String password, Authentication type, SSLContext context, SSLHostnameVerifier verifier) {
    if ( Authentication.BASIC == type ) {
      return new BasicAuthContext(user, password)
        .withSSLContext(context)
        .withSSLHostnameVerifier(verifier);
    } else if ( Authentication.DIGEST == type ) {
      return new DigestAuthContext(user, password)
        .withSSLContext(context)
        .withSSLHostnameVerifier(verifier);
    } else {
      throw new IllegalStateException("makeSecurityContext should only be called with BASIC or DIGEST Authentication");
    }
  }

  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host	the host with the REST server
   * @param port	the port for the REST server
   * @param user	the user with read, write, or administrative privileges
   * @param password	the password for the user
   * @param type	the type of authentication applied to the request
   * @return	a new client for making database requests
   * @deprecated	(as of 4.0.1) use {@link #newClient(String host, int port, SecurityContext securityContext)}
   */
  @Deprecated
  static public DatabaseClient newClient(String host, int port, String user, String password, Authentication type) {
    return newClient(host, port, null, makeSecurityContext(user, password, type, null, null), null);
  }
  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host	the host with the REST server
   * @param port	the port for the REST server
   * @param database	the database to access (default: configured database for the REST server)
   * @param user	the user with read, write, or administrative privileges
   * @param password	the password for the user
   * @param type	the type of authentication applied to the request
   * @return	a new client for making database requests
   * @deprecated	(as of 4.0.1) use {@link #newClient(String host, int port, String database, SecurityContext securityContext)}
   */
  @Deprecated
  static public DatabaseClient newClient(String host, int port, String database, String user, String password, Authentication type) {
    return newClient(host, port, database, makeSecurityContext(user, password, type, null, null), null);
  }
  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host	the host with the REST server
   * @param port	the port for the REST server
   * @param user	the user with read, write, or administrative privileges
   * @param password	the password for the user
   * @param type	the type of authentication applied to the request
   * @param context	the SSL context for authenticating with the server
   * @return	a new client for making database requests
   * @deprecated	(as of 4.0.1) use {@link #newClient(String host, int port, SecurityContext securityContext)}
   */
  @Deprecated
  static public DatabaseClient newClient(String host, int port, String user, String password, Authentication type, SSLContext context) {
    return newClient(host, port, null, makeSecurityContext(user, password, type, context, SSLHostnameVerifier.COMMON), null);
  }
  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host	the host with the REST server
   * @param port	the port for the REST server
   * @param database	the database to access (default: configured database for the REST server)
   * @param user	the user with read, write, or administrative privileges
   * @param password	the password for the user
   * @param type	the type of authentication applied to the request
   * @param context	the SSL context for authenticating with the server
   * @return	a new client for making database requests
   * @deprecated	(as of 4.0.1) use {@link #newClient(String host, int port, String database, SecurityContext securityContext)}
   */
  @Deprecated
  static public DatabaseClient newClient(String host, int port, String database, String user, String password, Authentication type, SSLContext context) {
    return newClient(host, port, database, makeSecurityContext(user, password, type, context, SSLHostnameVerifier.COMMON), null);
  }
  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host	the host with the REST server
   * @param port	the port for the REST server
   * @param user	the user with read, write, or administrative privileges
   * @param password	the password for the user
   * @param type	the type of authentication applied to the request
   * @param context	the SSL context for authenticating with the server
   * @param verifier	a callback for checking hostnames
   * @return	a new client for making database requests
   * @deprecated	(as of 4.0.1) use {@link #newClient(String host, int port, SecurityContext securityContext)}
   */
  @Deprecated
  static public DatabaseClient newClient(String host, int port, String user, String password, Authentication type, SSLContext context, SSLHostnameVerifier verifier) {
    return newClient(host, port, null, makeSecurityContext(user, password, type, context, verifier), null);
  }
  /**
   * Creates a client to access the database by means of a REST server.
   *
   * @param host	the host with the REST server
   * @param port	the port for the REST server
   * @param database	the database to access (default: configured database for the REST server)
   * @param user	the user with read, write, or administrative privileges
   * @param password	the password for the user
   * @param type	the type of authentication applied to the request
   * @param context	the SSL context for authenticating with the server
   * @param verifier	a callback for checking hostnames
   * @return	a new client for making database requests
   * @deprecated	(as of 4.0.1) use {@link #newClient(String host, int port, String database, SecurityContext securityContext)}
   */
  @Deprecated
  static public DatabaseClient newClient(String host, int port, String database, String user, String password, Authentication type, SSLContext context, SSLHostnameVerifier verifier) {
    return newClient(host, port, database, makeSecurityContext(user, password, type, context, verifier), null);
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
   * @see com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator
   * @param configurator	the listener for configuring the communication library
   */
  static public void addConfigurator(ClientConfigurator<?> configurator) {
    if (!HttpClientConfigurator.class.isInstance(configurator) && !OkHttpClientConfigurator.class.isInstance(configurator)) {
      throw new IllegalArgumentException(
        "Configurator must implement OkHttpClientConfigurator"
      );
    }

    clientConfigurator = configurator;
  }

  /**
   * A Database Client Factory Bean provides an object for specifying configuration
   * before creating a client to make database requests.
   *
   * <p>For instance, a Spring configuration file might resemble the following
   * example:</p>
   * <pre>
   * &lt;bean name="databaseClientFactory"
   * 	   class="com.marklogic.client.DatabaseClientFactory.Bean"&gt;
   *   &lt;property name="host"                value="localhost"/&gt;
   *   &lt;property name="port"                value="8012"/&gt;
   *   &lt;property name="user"                value="rest-writer-user"/&gt;
   *   &lt;property name="password"            value="rest-writer-password"/&gt;
   *   &lt;property name="authenticationValue" value="digest"/&gt;
   * &lt;/bean&gt;
   *
   * &lt;bean name="databaseClient"
   * 	   class="com.marklogic.client.DatabaseClient"
   * 	   factory-bean="databaseClientFactory"
   * 	   factory-method="newClient"/&gt;
   * </pre>
   */
  static public class Bean implements Serializable {
    private static final long serialVersionUID = 1L;

    private           String                host;
    private           int                   port;
    private           String                database;
    private           String                user;
    private           String                password;
    private           Authentication        authentication;
    private           String                externalName;
    private           SecurityContext       securityContext;
    private           DatabaseClient.ConnectionType connectionType;
    private           HandleFactoryRegistry handleRegistry =
      HandleFactoryRegistryImpl.newDefault();

    transient private SSLContext            context;
    transient private SSLHostnameVerifier   verifier;


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
     * Returns the user authentication for clients created with a
     * DatabaseClientFactory.Bean object.
     * @return	the user
     * @deprecated	(as of 4.0.1) use SecurityContext.getUser() with BasicAuthContext or DigestAuthContext
     */
    @Deprecated
    public String getUser() {
      return user;
    }
    /**
     * Specifies the user authentication for clients created with a
     * DatabaseClientFactory.Bean object.
     * @param user	the user
     * @deprecated	(as of 4.0.1) use constructors for BasicAuthContext or DigestAuthContext
     */
    @Deprecated
    public void setUser(String user) {
      this.user = user;
    }
    /**
     * Returns the password authentication for clients created with a
     * DatabaseClientFactory.Bean object.
     * @return	the password
     * @deprecated	(as of 4.0.1) use SecurityContext.getUser() with BasicAuthContext or DigestAuthContext
     */
    @Deprecated
    public String getPassword() {
      return password;
    }
    /**
     * Specifies the password authentication for clients created with a
     * DatabaseClientFactory.Bean object.
     * @param password	the password
     * @deprecated	(as of 4.0.1) use constructors for BasicAuthContext or DigestAuthContext
     */
    @Deprecated
    public void setPassword(String password) {
      this.password = password;
    }
    /**
     * Returns the external name for Kerberos clients created with a
     * DatabaseClientFactory.Bean object.
     * @return	the external name
     */
    public String getExternalName() {
      return externalName;
    }
    /**
     * Specifies the external name for Kerberos clients created with a
     * DatabaseClientFactory.Bean object.
     * @param externalName	the external name
     */
    public void setExternalName(String externalName) {
      this.externalName = externalName;
    }
    /**
     * Returns the authentication type for clients created with a
     * DatabaseClientFactory.Bean object.
     * @return	the authentication type
     * @deprecated	(as of 4.0.1) use instanceof on any SecurityContext to get its type
     */
    @Deprecated
    public Authentication getAuthentication() {
      return authentication;
    }
    /**
     * Specifies the authentication type for clients created with a
     * DatabaseClientFactory.Bean object.
     * @param authentication	the authentication type
     * @deprecated	(as of 4.0.1) use constructor for any SecurityContext
     */
    @Deprecated
    public void setAuthentication(Authentication authentication) {
      this.authentication = authentication;
    }
    /**
     * Specifies the authentication type for clients created with a
     * DatabaseClientFactory.Bean object based on a string value.
     * @param authentication	the authentication type
     * @deprecated	(as of 4.0.1) use constructor for any SecurityContext
     */
    @Deprecated
    public void setAuthenticationValue(String authentication) {
      this.authentication = Authentication.valueOfUncased(authentication);
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
     * @param database	a database to pass along to new DocumentManager and QueryManager instances
     */
    public void setDatabase(String database) {
      this.database = database;
    }
    /**
     * Returns the SSLContext for SSL clients created with a
     * DatabaseClientFactory.Bean object.
     * @return	the SSL context
     * @deprecated	(as of 4.0.1) use SecurityContext.getSSLContext()
     */
    @Deprecated
    public SSLContext getContext() {
      return context;
    }
    /**
     * Specifies the SSLContext for clients created with a
     * DatabaseClientFactory.Bean object that authenticate with SSL.
     * @param context	the SSL context
     * @deprecated	(as of 4.0.1) use SecurityContext.setSSLContext(SSLContext)
     *   or SecurityContext.withSSLContext(SSLContext)
     */
    @Deprecated
    public void setContext(SSLContext context) {
      this.context = context;
    }
    /**
     * Returns the host verifier for clients created with a
     * DatabaseClientFactory.Bean object.
     * @return	the host verifier
     * @deprecated	(as of 4.0.1) use SecurityContext.getSSLHostnameVerifier()
     */
    @Deprecated
    public SSLHostnameVerifier getVerifier() {
      return verifier;
    }
    /**
     * Specifies the host verifier for clients created with a
     * DatabaseClientFactory.Bean object that verify hosts for
     * additional security.
     * @param verifier	the host verifier
     * @deprecated	(as of 4.0.1) use SecurityContext.setSSLHostnameVerifier(SSLHostnameVerifier)
     *   or SecurityContext.withSSLHostnameVerifier(SSLHostnameVerifier)
     */
    @Deprecated
    public void setVerifier(SSLHostnameVerifier verifier) {
      this.verifier = verifier;
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
            host, port, database,
            makeSecurityContext(user, password, authentication, context, verifier),
            connectionType);
      client.setHandleRegistry(getHandleRegistry().copy());
      return client;
    }
  }
}