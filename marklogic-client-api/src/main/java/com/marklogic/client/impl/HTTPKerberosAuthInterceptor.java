package com.marklogic.client.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Set;
import java.util.Base64;

import java.security.Principal;
import java.security.PrivilegedAction;
import com.marklogic.client.DatabaseClientFactory.KerberosConfig;
import com.marklogic.client.FailedRequestException;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.kerberos.KerberosTicket;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An HTTP Request interceptor that modifies the request headers to enable
 * Kerberos authentication. It appends the Kerberos authentication token to the
 * 'Authorization' request header for Kerberos authentication
 *
 */
public class HTTPKerberosAuthInterceptor implements Interceptor {
	String host;
	Map<String,String> krbOptions;
	LoginContext loginContext;

  public HTTPKerberosAuthInterceptor(String host, Map<String,String> krbOptions) {
    this.host = host;
    this.krbOptions = krbOptions;
    try {
      buildSubjectCredentials();
    } catch (LoginException e) {
      throw new FailedRequestException(e.getMessage(), e);
    }
  }

  /**
   * Class to create Kerberos Configuration object which specifies the Kerberos
   * Login Module to be used for authentication.
   *
   */
  private class KerberosLoginConfiguration extends Configuration {
    Map<String,String> krbOptions = null;

    public KerberosLoginConfiguration() {}

    KerberosLoginConfiguration(Map<String,String> krbOptions) {

      this.krbOptions = krbOptions;
    }
    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {

      return new AppConfigurationEntry[] { new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
          AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, krbOptions) };
    }
  }

  /**
   * This method checks the validity of the TGT in the cache and build the
   * Subject inside the LoginContext using Krb5LoginModule and the TGT cached by
   * the Kerberos client. It assumes that a valid TGT is already present in the
   * kerberos client's cache.
   *
   * @throws LoginException
   */
  private void buildSubjectCredentials() throws LoginException {
    Subject subject = new Subject();
    /**
     * We are not getting the TGT from KDC here. The actual TGT is got from the
     * KDC using kinit or equivalent but we use the cached TGT in order to build
     * the LoginContext and populate the TGT inside the Subject using
     * Krb5LoginModule
     */
    LoginContext lc = new LoginContext("Krb5LoginContext", subject, null,
        (krbOptions != null) ? new KerberosLoginConfiguration(krbOptions) : new KerberosLoginConfiguration());
    lc.login();
    loginContext = lc;
  }

  /**
   * This method is responsible for getting the client principal name from the
   * subject's principal set
   *
   * @return String the Kerberos principal name populated in the subject
   * @throws IllegalStateException if there is more than 0 or more than 1
   *           principal is present
   */
  private String getClientPrincipalName() {
    final Set<Principal> principalSet = loginContext.getSubject().getPrincipals();
    if (principalSet.size() != 1)
      throw new IllegalStateException(
          "Only one principal is expected. Found 0 or more than one principals :" + principalSet);
    return principalSet.iterator().next().getName();
  }

  /**
   * This method builds the Authorization header for Kerberos. It
   * generates a request token based on the service ticket, client principal name and
   * time-stamp
   *
   * @param serverPrincipalName
   *            the name registered with the KDC of the service for which we
   *            need to authenticate
   * @return the HTTP Authorization header token
   */
  private String buildAuthorizationHeader(String serverPrincipalName) throws LoginException
  {
    /*
     * Get the principal from the Subject's private credentials and populate the
     * client and server principal name for the GSS API
     */
    final String clientPrincipal = getClientPrincipalName();
    final CreateAuthorizationHeaderAction action = new CreateAuthorizationHeaderAction(clientPrincipal,
      serverPrincipalName);

    /*
     * Check if the TGT in the Subject's private credentials are valid. If
     * valid, then we use the TGT in the Subject's private credentials. If not,
     * we build the Subject's private credentials again from valid TGT in the
     * Kerberos client cache.
     */
    Set<Object> privateCreds = loginContext.getSubject().getPrivateCredentials();
    for (Object privateCred : privateCreds) {
      if (privateCred instanceof KerberosTicket) {
        String serverPrincipalTicketName = ((KerberosTicket) privateCred).getServer().getName();
        if ((serverPrincipalTicketName.startsWith("krbtgt"))
          && ((KerberosTicket) privateCred).getEndTime().compareTo(new Date()) == -1) {
          buildSubjectCredentials();
          break;
        }
      }
    }

    /*
     * Subject.doAs takes in the Subject context and the action to be run as
     * arguments. This method executes the action as the Subject given in the
     * argument. We do this in order to provide the Subject's context so that we
     * reuse the service ticket which will be populated in the Subject rather
     * than getting the service ticket from the KDC for each request. The GSS
     * API populates the service ticket in the Subject and reuses it
     * 
     */
    Subject.doAs(loginContext.getSubject(), action);
    return action.getNegotiateToken();
  }

  /**
   * Creates a privileged action which will be executed as the Subject using
   * Subject.doAs() method. We do this in order to create a context of the user
   * who has the service ticket and reuse this context for subsequent requests
   */
  private static class CreateAuthorizationHeaderAction implements PrivilegedAction {
    String clientPrincipalName;
    String serverPrincipalName;

    private StringBuffer outputToken = new StringBuffer();

    private CreateAuthorizationHeaderAction(final String clientPrincipalName, final String serverPrincipalName) {
      this.clientPrincipalName = clientPrincipalName;
      this.serverPrincipalName = serverPrincipalName;
    }

    private String getNegotiateToken() {
      return outputToken.toString();
    }

    /*
     * Here GSS API takes care of getting the service ticket from the Subject
     * cache or by using the TGT information populated in the subject which is
     * done by buildSubjectCredentials method. The service ticket received is
     * populated in the subject's private credentials along with the TGT
     * information since we will be executing this method as the Subject. For
     * subsequent requests, the cached service ticket will be re-used. For this
     * to work the System property javax.security.auth.useSubjectCredsOnly must
     * be set to true.
     */
    @Override
    public Object run() {
      try {
        Oid krb5Mechanism = new Oid("1.2.840.113554.1.2.2");
        Oid krb5PrincipalNameType = new Oid("1.2.840.113554.1.2.2.1");
        final GSSManager manager = GSSManager.getInstance();
        final GSSName clientName = manager.createName(clientPrincipalName, krb5PrincipalNameType);
        final GSSCredential clientCred = manager.createCredential(clientName, 8 * 3600, krb5Mechanism,
            GSSCredential.INITIATE_ONLY);
        final GSSName serverName = manager.createName(serverPrincipalName, krb5PrincipalNameType);

        final GSSContext context = manager.createContext(serverName, krb5Mechanism, clientCred,
            GSSContext.DEFAULT_LIFETIME);
        byte[] inToken = new byte[0];
        byte[] outToken = context.initSecContext(inToken, 0, inToken.length);
        context.requestMutualAuth(true);
        outputToken.append(new String(Base64.getEncoder().encode(outToken)));
        context.dispose();
      } catch (GSSException exception) {
        throw new FailedRequestException(exception.getMessage(), exception);
      }
      return null;
    }
  }

  /*
   * The server principal name which we pass as an argument to
   * buildAuthorizationHeader method would always start with 'HTTP/' because we
   * create the principal name for the Marklogic server starting with 'HTTP/'
   * followed by the host name as mentioned in the <a href=
   * "http://docs.marklogic.com/guide/security/external-auth#id_13835"> External
   * Security Guide</a>.
   */
  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    String authValue;
    try {
      authValue = "Negotiate " + buildAuthorizationHeader("HTTP/" + host);
    } catch (Exception e) {
      throw new FailedRequestException(e.getMessage(), e);
    }
    Request authenticatedRequest = request.newBuilder().header("Authorization", authValue).build();
    return chain.proceed(authenticatedRequest);
  }
}