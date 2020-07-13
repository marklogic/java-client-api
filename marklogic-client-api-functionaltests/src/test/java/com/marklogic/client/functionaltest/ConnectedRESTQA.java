/*
 * Copyright (c) 2019 MarkLogic Corporation
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

package com.marklogic.client.functionaltest;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClient.ConnectionType;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;

/**
 * @author gvaidees
 *
 */
public abstract class ConnectedRESTQA {
	private String serverName = "";
	private static String restServerName = null;
	private static String restSslServerName = null;
	private static String ssl_enabled = null;
	private static String https_port = null;
	private static String http_port = null;
	private static String admin_port = null;
	// This needs to be a FQDN when SSL is enabled. Else localhost. Set in
	// test.properties
	private static String host_name = null;
	// This needs to be a FQDN when SSL is enabled. Else localhost
	private static String ssl_host_name = null;
	private static String admin_user = null;
	private static String admin_password = null;
	private static String mlRestWriteUser = null;
	private static String mlRestWritePassword = null;
	private static String mlRestAdminUser = null;
	private static String mlRestAdminPassword = null;
	private static String mlRestReadUser = null;
	private static String mlRestReadPassword = null;
	private static String ml_certificate_password = null;
	private static String ml_certificate_file = null;
	private static String ml_certificate_path = null;
	private static String mlDataConfigDirPath = null;
	private static Boolean isLBHost = false;
	
	private static int PROPERTY_WAIT = 0;

	SSLContext sslContext = null;

	/**
	 * Use Rest call to create a database.
	 * 
	 * @param dbName
	 */
	private static final Logger logger = LogManager.getLogger(ConnectedRESTQA.class);

	public static void createDB(String dbName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/databases?format=json");
			String JSONString = "[{\"database-name\":\"" + dbName + "\"}]";

			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(JSONString));

			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				// EntityUtils to get the response content
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
			EntityUtils.consume(respEntity);
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static String getBootStrapHostFromML() {
		InputStream jstream = null;
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet(
					"http://" + host_name + ":" + admin_port + "/manage/v2/properties?format=json");
			HttpResponse resp = client.execute(getrequest);
			jstream = resp.getEntity().getContent();
			JsonNode jnode = new ObjectMapper().readTree(jstream);
			String propName = "bootstrap-host";
			if (!jnode.isNull()) {
				if (jnode.has(propName)) {
					System.out.println(
							"Bootstrap Host: " + jnode.withArray(propName).get(0).get("bootstrap-host-name").asText());
					return jnode.withArray(propName).get(0).get("bootstrap-host-name").asText();
				} else {
					System.out.println("Missing " + propName
							+ " field from properties end point so sending java conanical host name\n"
							+ jnode.toString());
					return InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
				}
			} else {
				System.out.println("Rest endpoint returns empty stream");
				return InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
			return host_name;
		} finally {
			try {
				jstream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jstream = null;
			client.getConnectionManager().shutdown();
		}
	}

	public static void createForest(String fName, String dbName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/forests?format=json");
			String hName = getBootStrapHostFromML();
			String JSONString = "{\"database\":\"" + dbName + "\",\"forest-name\":\"" + fName + "\",\"host\":\"" + hName
					+ "\"}";
			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(JSONString));

			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				// EntityUtils to get the response content
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * creating forests on different hosts
	 */
	public static void createForestonHost(String fName, String dbName, String hName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/forests?format=json");
			String JSONString = "{\"database\":\"" + dbName + "\",\"forest-name\":\"" + fName + "\",\"host\":\"" + hName
					+ "\"}";

			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(JSONString));

			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				// EntityUtils to get the response content
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void postRequest(Map<String, String> payload, Map<String, String> params, String endpoint) {
		DefaultHttpClient client = null;
		JSONObject JSONpayload = null; 
		try {
			if (payload == null) {
				JSONpayload = new JSONObject();
			}
			else {
				JSONpayload = new JSONObject(payload);
			}
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + endpoint);
			if (params != null) {
				Iterator localIterator;
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				for (localIterator = params.entrySet().iterator(); localIterator.hasNext();) {
					Map.Entry<String, String> localEntry = (Map.Entry<String, String>) localIterator.next();
					postParameters.add(new BasicNameValuePair(localEntry.getKey(), localEntry.getValue()));
				}
				post.setEntity(new UrlEncodedFormEntity(postParameters));
			}
			if (payload != null) {
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(JSONpayload.toString()));
			}

			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();
			if (respEntity != null) {
				// EntityUtils to get the response content
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			//client.close();
		}
	}

	public static void assocRESTServer(String restServerName, String dbName, int restPort) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/v1/rest-apis?format=json");
			String JSONString = "{ \"rest-api\": {\"name\":\"" + restServerName + "\",\"database\":\"" + dbName
					+ "\",\"port\":\"" + restPort + "\"}}";

			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(JSONString));

			HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() == 400) {
				System.out.println("AppServer already exist");
				if (dbName.equals("Documents")) {
					System.out.println("and Context database is Documents DB");
				} else {
					System.out.println("and changing context database to " + dbName);
					associateRESTServerWithDB(restServerName, dbName);
				}
			} else if (response.getStatusLine().getStatusCode() == 201) {
				// Enable security on new REST Http Server if SSL is turned on.
				if (IsSecurityEnabled()) {
					enableSecurityOnRESTServer(restServerName, dbName);
				}
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void enableSecurityOnRESTServer(String restServerName, String dbName) throws Exception {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			String body = "{\"group-name\": \"Default\",\"internal-security\":\"true\", \"ssl-certificate-template\":\"ssl1-QAXdbcServer\", \"ssl-require-client-certificate\":\"true\""
					+ "}";

			HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
					+ "/properties?server-type=http");
			put.addHeader("Content-type", "application/json");
			put.setEntity(new StringEntity(body));

			HttpResponse response2 = client.execute(put);
			HttpEntity respEntity = response2.getEntity();
			if (respEntity != null) {
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void associateRESTServerWithDB(String restServerName, String dbName) throws Exception {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			String body = "{\"content-database\": \"" + dbName + "\",\"group-name\": \"Default\"}";

			HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
					+ "/properties?server-type=http");
			put.addHeader("Content-type", "application/json");
			put.setEntity(new StringEntity(body));

			HttpResponse response2 = client.execute(put);
			HttpEntity respEntity = response2.getEntity();
			if (respEntity != null) {
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * Associate REST server with default user this is created for the sake of
	 * runtime DB selection
	 */
	public static void associateRESTServerWithDefaultUser(String restServerName, String userName, String authType)
			throws Exception {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			String body = "{ \"default-user\":\"" + userName + "\",\"authentication\": \"" + authType
					+ "\",\"group-name\": \"Default\"}";

			HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
					+ "/properties?server-type=http");
			put.addHeader("Content-type", "application/json");
			put.setEntity(new StringEntity(body));

			HttpResponse response2 = client.execute(put);
			HttpEntity respEntity = response2.getEntity();
			if (respEntity != null) {
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * Creating RESTServer With default content and module database
	 */
	public static void createRESTServerWithDB(String restServerName, int restPort) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/v1/rest-apis?format=json");
			//
			String JSONString = "{ \"rest-api\": {\"name\":\"" + restServerName + "\",\"port\":\"" + restPort + "\"}}";

			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(JSONString));

			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				// EntityUtils to get the response content
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * This function creates database,forests and REST server independently and
	 * attaches the database to the rest server
	 */
	public static void setupJavaRESTServer(String dbName, String fName, String restServerName, int restPort)
			throws Exception {
		Calendar cal = Calendar.getInstance();
		Date d = cal.getTime();
		long beforeSetup = cal.getTimeInMillis();
		long before = cal.getTimeInMillis();
		logger.info("### Starting TESTCASE SETUP." + dbName + "### " + d);

		createDB(dbName);
		logTestMessages("CREATE-DB", before);

		before = Calendar.getInstance().getTimeInMillis();
		createForest(fName, dbName);
		logTestMessages("CREATE-FOREST", before);

		before = Calendar.getInstance().getTimeInMillis();
		assocRESTServer(restServerName, dbName, restPort);
		logTestMessages("REST-SERVER-ASSOCIATION", before);

		before = Calendar.getInstance().getTimeInMillis();
		createRESTUser("rest-admin", "x", "rest-admin");
		createRESTUser("rest-writer", "x", "rest-writer");
		createRESTUser("rest-reader", "x", "rest-reader");
		logTestMessages("REST-USER-CREATION-CHK", before);
		cal = Calendar.getInstance();
		long after = cal.getTimeInMillis();
		long diff = after - beforeSetup;

		// String msg = "### Ending TESTCASE SETUP ###: "+diff/1000+" seconds";
		// logger.info(msg);
	}

	public static void setupJavaRESTServer(String dbName, String fName, String restServerName, int restPort,
			boolean attachRestContextDB) throws Exception {
		createDB(dbName);
		createForest(fName, dbName);
		Thread.sleep(1500);
		if (attachRestContextDB) {
			assocRESTServer(restServerName, dbName, restPort);
		} else {
			assocRESTServer(restServerName, "Documents", restPort);
		}
		createRESTUser("rest-admin", "x", "rest-admin");
		createRESTUser("rest-writer", "x", "rest-writer");
		createRESTUser("rest-reader", "x", "rest-reader");
	}

	/*
	 * Create a role with given privilages
	 */
	public static void createUserRolesWithPrevilages(String roleName, String... privNames) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/roles/" + roleName);
			HttpResponse resp = client.execute(getrequest);

			if (resp.getStatusLine().getStatusCode() == 200) {
				System.out.println("Role already exist");
			} else {
				System.out.println("Role dont exist, will create now");
				String[] roleNames = { "rest-reader", "rest-writer" };
				client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
						new UsernamePasswordCredentials("admin", "admin"));

				ObjectMapper mapper = new ObjectMapper();
				ObjectNode mainNode = mapper.createObjectNode();

				ArrayNode roleArray = mapper.createArrayNode();
				ArrayNode privArray = mapper.createArrayNode();
				ArrayNode permArray = mapper.createArrayNode();
				mainNode.put("role-name", roleName);
				mainNode.put("description", "role discription");

				for (String rolename : roleNames)
					roleArray.add(rolename);
				mainNode.withArray("role").addAll(roleArray);
				for (String privName : privNames) {
					ObjectNode privNode = mapper.createObjectNode();
					privNode.put("privilege-name", privName);
					privNode.put("action", "http://marklogic.com/xdmp/privileges/" + privName.replace(":", "-"));
					privNode.put("kind", "execute");
					privArray.add(privNode);
				}
				mainNode.withArray("privilege").addAll(privArray);
				permArray.add(getPermissionNode(roleNames[0], Capability.READ).get("permission").get(0));
				permArray.add(getPermissionNode(roleNames[1], Capability.READ).get("permission").get(0));
				permArray.add(getPermissionNode(roleNames[1], Capability.EXECUTE).get("permission").get(0));
				permArray.add(getPermissionNode(roleNames[1], Capability.UPDATE).get("permission").get(0));
				mainNode.withArray("permission").addAll(permArray);
				System.out.println(mainNode.toString());
				HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/roles?format=json");
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(mainNode.toString()));

				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();
				if (response.getStatusLine().getStatusCode() == 400) {
					System.out.println("creation of role got a problem");
				} else if (respEntity != null) {
					// EntityUtils to get the response content
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				} else {
					System.out.println("No Proper Response");
				}
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * Create a role with given privilages. With added Node Update Capability
	 * Similar to createUserRolesWithPrevilages method, but have Node Update.
	 */
	public static void createRoleWithNodeUpdate(String roleName, String... privNames) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/roles/" + roleName);
			HttpResponse resp = client.execute(getrequest);

			if (resp.getStatusLine().getStatusCode() == 200) {
				System.out.println("Role already exist");
			} else {
				System.out.println("Role dont exist, will create now");
				String[] roleNames = { "rest-reader", "rest-writer" };
				client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
						new UsernamePasswordCredentials("admin", "admin"));

				ObjectMapper mapper = new ObjectMapper();
				ObjectNode mainNode = mapper.createObjectNode();

				ArrayNode roleArray = mapper.createArrayNode();
				ArrayNode privArray = mapper.createArrayNode();
				ArrayNode permArray = mapper.createArrayNode();
				mainNode.put("role-name", roleName);
				mainNode.put("description", "role discription");

				for (String rolename : roleNames)
					roleArray.add(rolename);
				mainNode.withArray("role").addAll(roleArray);
				for (String privName : privNames) {
					ObjectNode privNode = mapper.createObjectNode();
					privNode.put("privilege-name", privName);
					privNode.put("action", "http://marklogic.com/xdmp/privileges/" + privName.replace(":", "-"));
					privNode.put("kind", "execute");
					privArray.add(privNode);
				}
				mainNode.withArray("privilege").addAll(privArray);
				permArray.add(getPermissionNode(roleNames[0], Capability.READ).get("permission").get(0));
				permArray.add(getPermissionNode(roleNames[1], Capability.READ).get("permission").get(0));
				permArray.add(getPermissionNode(roleNames[1], Capability.EXECUTE).get("permission").get(0));
				permArray.add(getPermissionNode(roleNames[1], Capability.UPDATE).get("permission").get(0));
				permArray.add(getPermissionNode(roleNames[1], Capability.NODE_UPDATE).get("permission").get(0));

				mainNode.withArray("permission").addAll(permArray);
				System.out.println(mainNode.toString());
				HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/roles?format=json");
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(mainNode.toString()));

				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();
				if (response.getStatusLine().getStatusCode() == 400) {
					System.out.println("creation of role got a problem");
				} else if (respEntity != null) {
					// EntityUtils to get the response content
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				} else {
					System.out.println("No Proper Response");
				}
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * This function creates a REST user with given roles
	 */
	public static void createRESTUser(String usrName, String pass, String... roleNames) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/users/" + usrName);
			HttpResponse resp = client.execute(getrequest);

			if (resp.getStatusLine().getStatusCode() == 200) {
				System.out.println("User already exist");
			} else {
				System.out.println("User dont exist");
				client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
						new UsernamePasswordCredentials("admin", "admin"));

				ObjectMapper mapper = new ObjectMapper();
				ObjectNode mainNode = mapper.createObjectNode();

				ArrayNode childArray = mapper.createArrayNode();
				mainNode.put("user-name", usrName);
				mainNode.put("description", "user discription");
				mainNode.put("password", pass);
				for (String rolename : roleNames)
					childArray.add(rolename);
				mainNode.withArray("role").addAll(childArray);

				System.out.println(mainNode.toString());
				HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/users?format=json");
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(mainNode.toString()));

				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();
				if (response.getStatusLine().getStatusCode() == 400) {
					System.out.println("User already exist");
				} else if (respEntity != null) {
					// EntityUtils to get the response content
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				} else {
					System.out.println("No Proper Response");
				}
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * "permission": [ { "role-name": "dls-user", "capability": "read" }
	 */

	public static ObjectNode getPermissionNode(String roleName, DocumentMetadataHandle.Capability... cap) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mNode = mapper.createObjectNode();
		ArrayNode aNode = mapper.createArrayNode();

		for (DocumentMetadataHandle.Capability c : cap) {
			ObjectNode roleNode = mapper.createObjectNode();
			roleNode.put("role-name", roleName);
			roleNode.put("capability", c.toString().toLowerCase());
			aNode.add(roleNode);
		}
		mNode.withArray("permission").addAll(aNode);
		return mNode;
	}

	public static ObjectNode getCollectionNode(String... collections) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mNode = mapper.createObjectNode();
		ArrayNode aNode = mapper.createArrayNode();

		for (String c : collections) {
			aNode.add(c);
		}
		mNode.withArray("collection").addAll(aNode);
		return mNode;
	}

	public static void createRESTUserWithPermissions(String usrName, String pass, ObjectNode perm,
			ObjectNode colections, String... roleNames) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/users/" + usrName);
			HttpResponse resp = client.execute(getrequest);

			if (resp.getStatusLine().getStatusCode() == 200) {
				System.out.println("User already exist");
			} else {
				System.out.println("User dont exist");
				client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
						new UsernamePasswordCredentials("admin", "admin"));

				ObjectMapper mapper = new ObjectMapper();
				ObjectNode mainNode = mapper.createObjectNode();

				ArrayNode childArray = mapper.createArrayNode();
				mainNode.put("user-name", usrName);
				mainNode.put("description", "user discription");
				mainNode.put("password", pass);
				for (String rolename : roleNames)
					childArray.add(rolename);
				mainNode.withArray("role").addAll(childArray);
				mainNode.setAll(perm);
				mainNode.setAll(colections);

				System.out.println(mainNode.toString());
				HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/users?format=json");
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(mainNode.toString()));

				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();
				if (response.getStatusLine().getStatusCode() == 400) {
					System.out.println("Bad User creation request");
				} else if (respEntity != null) {
					// EntityUtils to get the response content
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				} else {
					System.out.println("No Proper Response");
				}
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void deleteRESTUser(String usrName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete(
					"http://" + host_name + ":" + admin_port + "/manage/v2/users/" + usrName);

			HttpResponse response = client.execute(delete);
			if (response.getStatusLine().getStatusCode() == 202) {
				Thread.sleep(3500);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void deleteUserRole(String roleName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete(
					"http://" + host_name + ":" + admin_port + "/manage/v2/roles/" + roleName);

			HttpResponse response = client.execute(delete);
			if (response.getStatusLine().getStatusCode() == 202) {
				Thread.sleep(3500);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void setupJavaRESTServerWithDB(String restServerName, int restPort) throws Exception {
		loadGradleProperties();
		createRESTServerWithDB(restServerName, restPort);
		createRESTUser("rest-admin", "x", "rest-admin");
		createRESTUser("rest-writer", "x", "rest-writer");
		createRESTUser("rest-reader", "x", "rest-reader");
	}

	/*
	 * This function deletes the REST appserver along with attached content
	 * database and module database
	 */

	public static void deleteRESTServerWithDB(String restServerName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete("http://" + host_name + ":" + admin_port + "/v1/rest-apis/"
					+ restServerName + "?include=content&include=modules");

			HttpResponse response = client.execute(delete);
			if (response.getStatusLine().getStatusCode() == 202) {
				Thread.sleep(9500);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void deleteRESTServer(String restServerName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete(
					"http://" + host_name + ":" + admin_port + "/v1/rest-apis/" + restServerName + "&include=modules");
			HttpResponse response = client.execute(delete);

			if (response.getStatusLine().getStatusCode() == 202) {
				Thread.sleep(3500);
				waitForServerRestart();
			} else
				System.out.println("Server response " + response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			// writing error to Log
			System.out.println("Inside Deleting Rest server is throwing an error");
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void detachForest(String dbName, String fName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/forests/" + fName);
			List<NameValuePair> urlParameters = new ArrayList<>();
			urlParameters.add(new BasicNameValuePair("state", "detach"));
			urlParameters.add(new BasicNameValuePair("database", dbName));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				// EntityUtils to get the response content
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * Deleting a forest is a HTTP Delete request
	 */
	public static void deleteForest(String fName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete(
					"http://" + host_name + ":" + admin_port + "/manage/v2/forests/" + fName + "?level=full");
			client.execute(delete);
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * Deleting Database
	 */
	public static void deleteDB(String dbName) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete(
					"http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName);
			client.execute(delete);

		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * Clear the Database
	 */
	public static void clearDB(int port) {
		DefaultHttpClient client = null;
		try {
			InputStream jsonstream = null;
			String uri = null;
			if (IsSecurityEnabled()) {
				// In case of SSL use 8002 port to clear DB contents.
				client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
						new UsernamePasswordCredentials("admin", "admin"));
				HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/servers/"
						+ getRestAppServerName() + "/properties?group-id=Default&format=json");
				HttpResponse response1 = client.execute(getrequest);
				jsonstream = response1.getEntity().getContent();
				JsonNode jnode = new ObjectMapper().readTree(jsonstream);
				String dbName = jnode.get("content-database").asText();
				System.out.println("App Server's content database properties value from ClearDB is :" + dbName);

				ObjectMapper mapper = new ObjectMapper();
				ObjectNode mainNode = mapper.createObjectNode();

				mainNode.put("operation", "clear-database");

				HttpPost post = new HttpPost(
						"http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName);
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(mainNode.toString()));

				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();
				if (response.getStatusLine().getStatusCode() == 400) {
					System.out.println("Database contents cleared");
				} else if (respEntity != null) {
					// EntityUtils to get the response content
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				} else {
					System.out.println("No Proper Response from clearDB in SSL.");
				}
			} else {
				client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(new AuthScope(host_name, port),
						new UsernamePasswordCredentials("admin", "admin"));
				uri = "http://" + host_name + ":" + port + "/v1/search/";
				HttpDelete delete = new HttpDelete(uri);
				client.execute(delete);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void waitForServerRestart() {
		DefaultHttpClient client = null;
		try {
			int count = 0;
			while (count < 20) {
				client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(new AuthScope(host_name, 8001),
						new UsernamePasswordCredentials("admin", "admin"));

				count++;
				try {
					HttpGet getrequest = new HttpGet("http://" + host_name + ":8001/admin/v1/timestamp");
					HttpResponse response = client.execute(getrequest);
					if (response.getStatusLine().getStatusCode() == 503) {
						Thread.sleep(5000);
					} else if (response.getStatusLine().getStatusCode() == 200) {
						break;
					} else {
						System.out.println("Waiting for response from server, Trial :"
								+ response.getStatusLine().getStatusCode() + count);
						Thread.sleep(6000);
					}
				} catch (Exception e) {
					Thread.sleep(6000);
				}
			}
		} catch (Exception e) {
			System.out.println("Inside wait for server restart is throwing an error");
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void logTestMessages(String txt, long before) {
		/*
		 * Calendar cal = Calendar.getInstance(); long after
		 * =cal.getTimeInMillis(); long diff = after - before; String msg =
		 * "### "+txt+" ### "+diff/1000+" seconds"; logger.info(msg);
		 */
	}

	/*
	 * This function move rest server first to documents and deletes forests and
	 * databases in separate calls
	 */
	public static void tearDownJavaRESTServer(String dbName, String[] fNames, String restServerName) throws Exception {
		Calendar cal = Calendar.getInstance();
		Date d = cal.getTime();
		long beforeTeardown = cal.getTimeInMillis();
		logger.info("### StartingTestCase TEARDOWN " + dbName + " ### " + d);

		long before = cal.getTimeInMillis();
		try {
			associateRESTServerWithDB(restServerName, "Documents");
		} catch (Exception e) {
			System.out.println("From Deleting Rest server called funnction is throwing an error");
			e.printStackTrace();
		}
		logTestMessages("REST-SERVER-ASSOCIATION", before);

		before = Calendar.getInstance().getTimeInMillis();
		try {
			for (int i = 0; i < fNames.length; i++) {
				detachForest(dbName, fNames[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logTestMessages("DETACH-FOREST-FROM-DB", before);

		before = Calendar.getInstance().getTimeInMillis();
		try {
			for (int i = 0; i < fNames.length; i++) {
				deleteForest(fNames[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logTestMessages("DELETE-FOREST", before);

		before = Calendar.getInstance().getTimeInMillis();
		deleteDB(dbName);
		logTestMessages("DELETE-DB", before);

		logTestMessages(" Ending TESTCASE TEARDOWN ", beforeTeardown);
	}

	/*
	 * This function deletes rest server along with default forest and database
	 */
	public static void tearDownJavaRESTServerWithDB(String restServerName) throws Exception {
		try {
			deleteRESTServerWithDB(restServerName);
			waitForServerRestart();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(6000);
	}

	/*
	 * 
	 * setting up AppServices configurations setting up database properties
	 * whose value is string
	 */
	public static void setDatabaseProperties(String dbName, String prop, String propValue) throws IOException {
		InputStream jsonstream = null;
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream = response1.getEntity().getContent();
			JsonNode jnode = new ObjectMapper().readTree(jsonstream);

			if (!jnode.isNull()) {
				((ObjectNode) jnode).put(prop, propValue);
				HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
						+ "/properties?format=json");
				put.addHeader("Content-type", "application/json");
				put.setEntity(new StringEntity(jnode.toString()));

				HttpResponse response2 = client.execute(put);
				HttpEntity respEntity = response2.getEntity();

				if (respEntity != null) {
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				}
			} else {
				System.out.println("REST call for database properties returned NULL ");
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			if (jsonstream == null) {
			} else {
				jsonstream.close();
			}
			client.getConnectionManager().shutdown();
		}
	}

	public static void setDatabaseProperties(String dbName, String prop, boolean propValue) throws IOException {
		InputStream jsonstream = null;
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream = response1.getEntity().getContent();
			JsonNode jnode = new ObjectMapper().readTree(jsonstream);

			if (!jnode.isNull()) {
				((ObjectNode) jnode).put(prop, propValue);

				HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
						+ "/properties?format=json");
				put.addHeader("Content-type", "application/json");
				put.setEntity(new StringEntity(jnode.toString()));

				HttpResponse response2 = client.execute(put);
				HttpEntity respEntity = response2.getEntity();

				if (respEntity != null) {
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				}
			} else {
				System.out.println("REST call for database properties returned NULL ");
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			if (jsonstream == null) {
			} else {
				jsonstream.close();
			}
			client.getConnectionManager().shutdown();
		}
	}

	/*
	 * This Method takes the root property name and object node under it if root
	 * propname exist and equals to null then it just add the object node under
	 * root property name else if it has an existing sub property name then it
	 * adds elements to that array
	 */
	public static void setDatabaseProperties(String dbName, String propName, ObjectNode objNode) throws IOException {
		InputStream jsonstream = null;
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream = response1.getEntity().getContent();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(jsonstream);
			if (!jnode.isNull()) {
				if (!jnode.has(propName)) {
					((ObjectNode) jnode).putArray(propName).addAll(objNode.withArray(propName));
				} else {
					if (!jnode.path(propName).isArray()) {
						System.out.println("property is not array");
						((ObjectNode) jnode).putAll(objNode);
					} else {
						JsonNode member = jnode.withArray(propName);
						if (objNode.path(propName).isArray()) {
							((ArrayNode) member).addAll(objNode.withArray(propName));
						}
					}
				}

				HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
						+ "/properties?format=json");
				put.addHeader("Content-type", "application/json");
				put.setEntity(new StringEntity(jnode.toString()));

				HttpResponse response2 = client.execute(put);
				HttpEntity respEntity = response2.getEntity();
				if (respEntity != null) {
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				}
			} else {
				System.out.println("REST call for database properties returned NULL \n" + jnode.toString() + "\n"
						+ response1.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			if (jsonstream == null) {
			} else {
				jsonstream.close();
			}
			client.getConnectionManager().shutdown();
		}
	}

	public static void enableCollectionLexicon(String dbName) throws Exception {
		setDatabaseProperties(dbName, "collection-lexicon", true);
	}

	// Enable triple-Index
	public static void enableTripleIndex(String dbName) throws Exception {
		setDatabaseProperties(dbName, "triple-index", true);
	}

	// Set triple-positions to false
	public static void enableTriplePositions(String dbName) throws Exception {
		setDatabaseProperties(dbName, "triple-positions", false);
	}

	public static void enableWordLexicon(String dbName) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		childArray.add("http://marklogic.com/collation/");
		childNode.putArray("word-lexicon").addAll(childArray);
		setDatabaseProperties(dbName, "word-lexicons", childNode);
	}

	public static void enableTrailingWildcardSearches(String dbName) throws Exception {
		setDatabaseProperties(dbName, "trailing-wildcard-searches", true);
	}

	public static void setMaintainLastModified(String dbName, boolean opt) throws Exception {
		setDatabaseProperties(dbName, "maintain-last-modified", opt);
	}

	public static void setAutomaticDirectoryCreation(String dbName, String opt) throws Exception {
		setDatabaseProperties(dbName, "directory-creation", opt);
	}

	/*
	 * This function constructs a range element index with default
	 * collation,range-value-positions and invalid values
	 */
	public static void addRangeElementIndex(String dbName, String type, String namespace, String localname)
			throws Exception {
		addRangeElementIndex(dbName, type, namespace, localname, false);
	}

	public static void addRangeElementIndex(String dbName, String type, String namespace, String localname,
			boolean positions) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mainNode = mapper.createObjectNode();

		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("scalar-type", type);
		childNodeObject.put("namespace-uri", namespace);
		childNodeObject.put("localname", localname);
		childNodeObject.put("collation", "");
		childNodeObject.put("range-value-positions", positions);
		childNodeObject.put("invalid-values", "reject");
		childArray.add(childNodeObject);
		mainNode.putArray("range-element-index").addAll(childArray);

		setDatabaseProperties(dbName, "range-element-index", mainNode);
	}

	public static void addRangeElementIndex(String dbName, String[][] rangeElements) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mainNode = mapper.createObjectNode();

		ArrayNode childArray = mapper.createArrayNode();
		int nRowsLen = rangeElements.length;
		int j = 0;
		for (int i = 0; i < nRowsLen; i++) {
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put("scalar-type", rangeElements[i][j++]);
			childNodeObject.put("namespace-uri", rangeElements[i][j++]);
			childNodeObject.put("localname", rangeElements[i][j++]);
			childNodeObject.put("collation", rangeElements[i][j++]);
			if (rangeElements[i][j].equalsIgnoreCase("false"))
				childNodeObject.put("range-value-positions", false);
			else
				childNodeObject.put("range-value-positions", true);
			j++;
			childNodeObject.put("invalid-values", rangeElements[i][j++]);
			/*
			 * if new field elements are to be added, then: 1) Increment value
			 * of j 2) add them below here using
			 * childNodeObject.put("FIELD-NAME", rangeElements[i][j++]);
			 */
			childArray.add(childNodeObject);
			j = 0;
		}
		mainNode.putArray("range-element-index").addAll(childArray);
		setDatabaseProperties(dbName, "range-element-index", mainNode);
	}

	/*
	 * This is a overloaded function constructs a range element index with
	 * default range-value-positions and invalid values
	 */
	public static void addRangeElementIndex(String dbName, String type, String namespace, String localname,
			String collation) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mainNode = mapper.createObjectNode();

		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("scalar-type", type);
		childNodeObject.put("namespace-uri", namespace);
		childNodeObject.put("localname", localname);
		childNodeObject.put("collation", collation);
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", "reject");
		childArray.add(childNodeObject);
		mainNode.putArray("range-element-index").addAll(childArray);

		setDatabaseProperties(dbName, "range-element-index", mainNode);
	}

	/*
	 * "scalar-type": "int", "collation": "", "parent-namespace-uri": "",
	 * "parent-localname": "test", "namespace-uri": "", "localname": "testAttr",
	 * "range-value-positions": false, "invalid-values": "reject"
	 */

	public static void addRangeElementAttributeIndex(String dbName, String type, String parentnamespace,
			String parentlocalname, String namespace, String localname, String collation) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("scalar-type", type);
		childNodeObject.put("collation", collation);
		childNodeObject.put("parent-namespace-uri", parentnamespace);
		childNodeObject.put("parent-localname", parentlocalname);
		childNodeObject.put("namespace-uri", namespace);
		childNodeObject.put("localname", localname);

		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", "reject");
		childArray.add(childNodeObject);
		childNode.putArray("range-element-attribute-index").addAll(childArray);

		setDatabaseProperties(dbName, "range-element-attribute-index", childNode);
	}

	/*
	 * Overloaded function with default collation
	 */
	public static void addRangeElementAttributeIndex(String dbName, String type, String parentnamespace,
			String parentlocalname, String namespace, String localname) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("scalar-type", type);
		childNodeObject.put("collation", "");
		childNodeObject.put("parent-namespace-uri", parentnamespace);
		childNodeObject.put("parent-localname", parentlocalname);
		childNodeObject.put("namespace-uri", namespace);
		childNodeObject.put("localname", localname);

		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", "reject");
		childArray.add(childNodeObject);
		childNode.putArray("range-element-attribute-index").addAll(childArray);

		setDatabaseProperties(dbName, "range-element-attribute-index", childNode);
	}

	/*
	 * "range-path-indexes": { "range-path-index": [ { "scalar-type": "string",
	 * "collation": "http:\/\/marklogic.com\/collation\/", "path-expression":
	 * "\/Employee\/fn", "range-value-positions": false, "invalid-values":
	 * "reject" } ] }
	 */
	public static void addRangePathIndex(String dbName, String type, String pathexpr, String collation,
			String invalidValues) throws Exception {
		addRangePathIndex(dbName, type, pathexpr, collation, invalidValues, false);
	}

	public static void addRangePathIndex(String dbName, String type, String pathexpr, String collation,
			String invalidValues, boolean positions) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("scalar-type", type);
		childNodeObject.put("collation", collation);
		childNodeObject.put("path-expression", pathexpr);
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", invalidValues);
		childArray.add(childNodeObject);
		childNode.putArray("range-path-index").addAll(childArray);

		setDatabaseProperties(dbName, "range-path-index", childNode);
	}

	public static void addRangePathIndex(String dbName, String[][] rangePaths) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();

		int nRowsLen = rangePaths.length;
		int j = 0;
		for (int i = 0; i < nRowsLen; i++) {
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put("scalar-type", rangePaths[i][j++]);
			childNodeObject.put("path-expression", rangePaths[i][j++]);
			childNodeObject.put("collation", rangePaths[i][j++]);
			childNodeObject.put("invalid-values", rangePaths[i][j++]);

			if (rangePaths[i][j].equalsIgnoreCase("false"))
				childNodeObject.put("range-value-positions", false);
			else
				childNodeObject.put("range-value-positions", true);
			/*
			 * if new field elements are to be added, then: 1) Increment value
			 * of j 2) add them below here using
			 * childNodeObject.put("FIELD-NAME", rangePaths[i][j++]);
			 */

			childArray.add(childNodeObject);
			j = 0;
		}
		childNode.putArray("range-path-index").addAll(childArray);
		setDatabaseProperties(dbName, "range-path-index", childNode);
	}

	public static void addGeospatialElementIndexes(String dbName, String localname, String namespace,
			String coordinateSystem, String pointFormat, boolean rangeValuePositions, String invalidValues)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("namespace-uri", namespace);
		childNodeObject.put("localname", localname);
		childNodeObject.put("coordinate-system", coordinateSystem);
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", invalidValues);
		childNodeObject.put("point-format", pointFormat);
		childArray.add(childNodeObject);
		childNode.putArray("geospatial-element-index").addAll(childArray);

		setDatabaseProperties(dbName, "geospatial-element-index", childNode);
	}

	public static void addGeoSpatialElementChildIndexes(String dbName, String parentNamespaceUri,
			String parentLocalName, String namespace, String localname, String coordinateSystem, String pointFormat,
			boolean rangeValuePositions, String invalidValues) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("parent-namespace-uri", parentNamespaceUri);
		childNodeObject.put("parent-localname", parentLocalName);
		childNodeObject.put("namespace-uri", namespace);
		childNodeObject.put("localname", localname);
		childNodeObject.put("coordinate-system", coordinateSystem);
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", invalidValues);
		childNodeObject.put("point-format", pointFormat);
		childArray.add(childNodeObject);
		childNode.putArray("geospatial-element-child-index").addAll(childArray);

		setDatabaseProperties(dbName, "geospatial-element-child-index", childNode);
	}

	public static void addGeospatialElementPairIndexes(String dbName, String parentNamespaceUri, String parentLocalName,
			String latNamespace, String latLocalname, String longNamespace, String longLocalname,
			String coordinateSystem, boolean rangeValuePositions, String invalidValues) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("parent-namespace-uri", parentNamespaceUri);
		childNodeObject.put("parent-localname", parentLocalName);
		childNodeObject.put("latitude-namespace-uri", latNamespace);
		childNodeObject.put("latitude-localname", latLocalname);
		childNodeObject.put("longitude-namespace-uri", latNamespace);
		childNodeObject.put("longitude-localname", longLocalname);
		childNodeObject.put("coordinate-system", coordinateSystem);
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", invalidValues);
		childArray.add(childNodeObject);
		childNode.putArray("geospatial-element-pair-index").addAll(childArray);

		setDatabaseProperties(dbName, "geospatial-element-pair-index", childNode);
	}

	public static void addGeospatialElementAttributePairIndexes(String dbName, String parentNamespaceUri,
			String parentLocalName, String latNamespace, String latLocalname, String longNamespace,
			String longLocalname, String coordinateSystem, boolean rangeValuePositions, String invalidValues)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("parent-namespace-uri", parentNamespaceUri);
		childNodeObject.put("parent-localname", parentLocalName);
		childNodeObject.put("latitude-namespace-uri", latNamespace);
		childNodeObject.put("latitude-localname", latLocalname);
		childNodeObject.put("longitude-namespace-uri", latNamespace);
		childNodeObject.put("longitude-localname", longLocalname);
		childNodeObject.put("coordinate-system", coordinateSystem);
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", invalidValues);
		childArray.add(childNodeObject);
		childNode.putArray("geospatial-element-attribute-pair-index").addAll(childArray);

		setDatabaseProperties(dbName, "geospatial-element-attribute-pair-index", childNode);
	}

	public static void addGeospatialPathIndexes(String dbName, String pathExpression, String coordinateSystem,
			String pointFormat, boolean rangeValuePositions, String invalidValues) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("path-expression", pathExpression);
		childNodeObject.put("coordinate-system", coordinateSystem);
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", invalidValues);
		childNodeObject.put("point-format", pointFormat);
		childArray.add(childNodeObject);
		childNode.putArray("geospatial-path-index").addAll(childArray);

		setDatabaseProperties(dbName, "geospatial-path-index", childNode);
	}

	/*
	 * To create a geo spatial region path index for the following geo spatial
	 * queries.
	 * 
	 * 1) Circle - Example - <circle>@120.5 -26.797920,136.406250</circle> 2)
	 * Box - Example <box>[-40.234, 100.4634, -20.345, 140.45230]</box> 3)
	 * Polygon - Example- <polygon>POLYGON((153.65 -8.35,170.57 -26.0,162.52
	 * -52.52,136.0 -56.35,111.0 -51.0,100.89 -26.0,108.18 1.82,136.0
	 * 10.26,153.65 -8.35))</polygon>
	 * 
	 * End-point used for GeoSpatial Region Path Indexes: REST endpoint:
	 * manage/v2/databases/{id|name}/properties Payload structure:
	 * "geospatial-region-path-index": [ { "path-expression": "//jurisdiction",
	 * "coordinate-system": "wgs84", "geohash-precision": 6, "invalid-values":
	 * "ignore" } ]
	 */

	public static void addGeospatialRegionPathIndexes(String dbName, String pathExpression, String coordinateSystem,
			String geoHashPrecision, String invalidValues) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("path-expression", pathExpression);
		childNodeObject.put("coordinate-system", coordinateSystem);
		childNodeObject.put("invalid-values", invalidValues);
		childNodeObject.put("geohash-precision", geoHashPrecision);
		childArray.add(childNodeObject);
		childNode.putArray("geospatial-region-path-index").addAll(childArray);

		setDatabaseProperties(dbName, "geospatial-region-path-index", childNode);
	}

	/*
	 * Add field will include root and it appends field to an existing fields
	 * "fields":{ "field":[ { "field-name": "", "include-root": true,
	 * "included-elements": null, "excluded-elements": null } , { "field-name":
	 * "para", "include-root": false, "included-elements": null,
	 * "excluded-elements": null, "tokenizer-overrides": null } ] }
	 */
	public static void addField(String dbName, String fieldName) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode arrNode = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("field-name", fieldName);
		childNodeObject.put("include-root", true);
		childNodeObject.putNull("included-elements");
		childNodeObject.putNull("excluded-elements");
		childNodeObject.putNull("tokenizer-overrides");
		arrNode.add(childNodeObject);
		childNode.putArray("field").addAll(arrNode);

		setDatabaseProperties(dbName, "field", childNode);
	}

	public static void addFieldExcludeRoot(String dbName, String fieldName) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode arrNode = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("field-name", fieldName);
		childNodeObject.put("include-root", false);
		childNodeObject.putNull("included-elements");
		childNodeObject.putNull("excluded-elements");
		childNodeObject.putNull("tokenizer-overrides");
		arrNode.add(childNodeObject);
		childNode.putArray("field").addAll(arrNode);

		setDatabaseProperties(dbName, "field", childNode);
	}

	public static void addBuiltInGeoIndex(String dbName) throws Exception {
		addGeospatialElementIndexes(dbName, "g-elem-point", "", "wgs84", "point", false, "reject");
		addGeoSpatialElementChildIndexes(dbName, "", "g-elem-child-parent", "", "g-elem-child-point", "wgs84", "point",
				false, "reject");
		addGeospatialElementPairIndexes(dbName, "", "g-elem-pair", "", "lat", "", "long", "wgs84", false, "reject");
		addGeospatialElementAttributePairIndexes(dbName, "", "g-attr-pair", "", "lat", "", "long", "wgs84", false,
				"reject");
		addGeospatialPathIndexes(dbName, "/doc/g-elem-point", "wgs84", "point", false, "ignore");
	}

	/*
	 * This method is trying to add include element or exclude elements to the
	 * existing fields
	 */
	public static void setDatabaseFieldProperties(String dbName, String field_name, String propName, ObjectNode objNode)
			throws IOException {
		InputStream jsonstream = null;
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream = response1.getEntity().getContent();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(jsonstream);
			if (!jnode.isNull() && jnode.has("field")) {
				JsonNode fieldNode = jnode.withArray("field");
				Iterator<JsonNode> fnode = fieldNode.elements();
				while (fnode.hasNext()) {
					JsonNode fnchild = fnode.next();
					if ((fnchild.path("field-name").asText()).equals(field_name)) {
						if (!fnchild.has(propName)) {
							((ObjectNode) fnchild).putArray(propName).addAll(objNode.withArray(propName));
						} else {
							JsonNode member = fnchild.withArray(propName);
							((ArrayNode) member).addAll(objNode.withArray(propName));
						}
					}
				}

				HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
						+ "/properties?format=json");
				put.addHeader("Content-type", "application/json");
				put.setEntity(new StringEntity(jnode.toString()));

				HttpResponse response2 = client.execute(put);
				HttpEntity respEntity = response2.getEntity();
				if (respEntity != null) {
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				}
			} else {
				System.out.println("REST call for database properties returned NULL \n" + jnode.toString() + "\n"
						+ response1.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			if (jsonstream == null) {
			} else {
				jsonstream.close();
			}
			client.getConnectionManager().shutdown();
		}
	}

	public static void includeElementField(String dbName, String field_name, String namespace, String elementName)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode arrNode = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("namespace-uri", namespace);
		childNodeObject.put("localname", elementName);
		childNodeObject.put("weight", 1.0);

		childNodeObject.put("attribute-namespace-uri", "");
		childNodeObject.put("attribute-localname", "");
		childNodeObject.put("attribute-value", "");

		arrNode.add(childNodeObject);
		childNode.putArray("included-element").addAll(arrNode);
		System.out.println(childNode.toString());
		setDatabaseFieldProperties(dbName, field_name, "included-element", childNode);
	}

	public static void includeElementFieldWithWeight(String dbName, String field_name, String namespace,
			String elementName, double weight, String attrNS_URI, String attr_localname, String attr_value)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode arrNode = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put("namespace-uri", namespace);
		childNodeObject.put("localname", elementName);
		childNodeObject.put("weight", weight);
		// These 3 are new fields that have been added as of 8.0.2 from
		// 03/20/2015
		// in the Management API.
		childNodeObject.put("attribute-namespace-uri", attrNS_URI);
		childNodeObject.put("attribute-localname", attr_localname);
		childNodeObject.put("attribute-value", attr_value);
		arrNode.add(childNodeObject);
		childNode.putArray("included-element").addAll(arrNode);
		setDatabaseFieldProperties(dbName, field_name, "included-element", childNode);
	}

	public static void setupAppServicesGeoConstraint(String dbName) throws Exception {
		enableCollectionLexicon(dbName);
		addRangeElementIndex(dbName, "dateTime", "", "bday", "http://marklogic.com/collation/");
		addRangeElementIndex(dbName, "int", "", "height1", "http://marklogic.com/collation/");
		addRangeElementIndex(dbName, "int", "", "height2", "http://marklogic.com/collation/");
		addRangePathIndex(dbName, "string", "/doc/name", "http://marklogic.com/collation/", "ignore");
		addField(dbName, "description");
		includeElementField(dbName, "description", "", "description");
		addBuiltInGeoIndex(dbName);
	}

	public static void setupAppServicesConstraint(String dbName) throws Exception {
		// Add new range elements into this array
		String[][] rangeElements = {
				// { scalar-type, namespace-uri, localname, collation,
				// range-value-positions, invalid-values }
				// If there is a need to add additional fields, then add them to
				// the end
				// of each array
				// and pass empty strings ("") into an array where the
				// additional field
				// does not have a value.
				// For example : as in namespace, collections below.
				{ "date", "http://purl.org/dc/elements/1.1/", "date", "", "false", "reject" },
				{ "int", "", "popularity", "", "false", "reject" },
				{ "int", "http://test.tups.com", "rate", "", "false", "reject" },
				{ "decimal", "http://test.aggr.com", "score", "", "false", "reject" },
				{ "string", "", "title", "http://marklogic.com/collation/", "false", "reject" }
				// Add new RangeElementIndex as an array below.
		};

		// Add new path elements into this array
		String[][] rangePaths = {
				// { scalar-type, path-expression, collation,
				// range-value-positions,
				// invalid-values }
				// If there is a need to add additional fields, then add them to
				// the end
				// of each array
				// and pass empty strings ("") into an array where the
				// additional field
				// does not have a value.
				// For example : as in namespace, collections below.
				{ "string", "/Employee/fn", "http://marklogic.com/collation/", "ignore", "false" },
				{ "int", "/root/popularity", "", "ignore", "false" }, { "decimal", "//@amt", "", "ignore", "false" }
				// Add new RangePathIndex as an array below.
		};

		enableCollectionLexicon(dbName);
		enableWordLexicon(dbName);

		// Insert the range indices
		addRangeElementIndex(dbName, rangeElements);

		enableTrailingWildcardSearches(dbName);

		// Insert the path range indices
		addRangePathIndex(dbName, rangePaths);
	}

	/*
	 * Create a temporal axis based on 2 element range indexes, for start and
	 * end values (for system or valid axis)
	 * 
	 * @dbName Database Name
	 * 
	 * @axisName Axis Name (name of axis that needs to be created)
	 * 
	 * @namespaceStart Namespace for 'start' element range index
	 * 
	 * @localnameStart Local name for 'start' element range index
	 * 
	 * @namespaceEnd Namespace for 'end' element range index
	 * 
	 * @localnameEnd Local name for 'end' element range index
	 */
	public static void addElementRangeIndexTemporalAxis(String dbName, String axisName, String namespaceStart,
			String localnameStart, String namespaceEnd, String localnameEnd) throws Exception {
		/**
		 * { "axis-name": "eri-json-system", "axis-start": {
		 * "element-reference": { "namespace-uri": "", "localname":
		 * "eri-system-start", "scalar-type": "dateTime" } }, "axis-end": {
		 * "element-reference": { "namespace-uri": "", "localname":
		 * "eri-system-end", "scalar-type": "dateTime" } } }
		 */
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("axis-name", axisName);

		// Set axis start
		ObjectNode axisStart = mapper.createObjectNode();
		ObjectNode elementReferenceStart = mapper.createObjectNode();
		elementReferenceStart.put("namespace-uri", namespaceStart);
		elementReferenceStart.put("localname", localnameStart);
		elementReferenceStart.put("scalar-type", "dateTime");

		axisStart.set("element-reference", elementReferenceStart);
		rootNode.set("axis-start", axisStart);

		// Set axis end
		ObjectNode axisEnd = mapper.createObjectNode();
		ObjectNode elementReferenceEnd = mapper.createObjectNode();
		elementReferenceEnd.put("namespace-uri", namespaceStart);
		elementReferenceEnd.put("localname", localnameEnd);
		elementReferenceEnd.put("scalar-type", "dateTime");

		axisEnd.set("element-reference", elementReferenceEnd);
		rootNode.set("axis-end", axisEnd);

		System.out.println(rootNode.toString());

		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));

		HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/axes?format=json");

		post.addHeader("Content-type", "application/json");
		post.addHeader("accept", "application/json");
		post.setEntity(new StringEntity(rootNode.toString()));

		HttpResponse response = client.execute(post);
		HttpEntity respEntity = response.getEntity();
		if (response.getStatusLine().getStatusCode() == 400) {
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			System.out.println(responseString);
		} else if (respEntity != null) {
			// EntityUtils to get the response content
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);

			System.out.println("Temporal axis: " + axisName + " created");
			System.out.println("==============================================================");
		} else {
			System.out.println("No Proper Response");
		}
		client.getConnectionManager().shutdown();
	}

	/*
	 * Delete a temporal axis
	 * 
	 * @dbName Database Name
	 * 
	 * @axisName Axis Name
	 */
	public static void deleteElementRangeIndexTemporalAxis(String dbName, String axisName) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));

		HttpDelete del = new HttpDelete("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/axes/" + axisName + "?format=json");

		del.addHeader("Content-type", "application/json");
		del.addHeader("accept", "application/json");

		HttpResponse response = client.execute(del);
		HttpEntity respEntity = response.getEntity();
		if (response.getStatusLine().getStatusCode() == 400) {
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			System.out.println(responseString);
		} else if (respEntity != null) {
			// EntityUtils to get the response content
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);
		} else {
			System.out.println("Axis: " + axisName + " deleted");
			System.out.println("==============================================================");
		}
		client.getConnectionManager().shutdown();
	}

	/*
	 * Create a temporal collection
	 * 
	 * @dbName Database Name
	 * 
	 * @collectionName Collection Name (name of temporal collection that needs
	 * to be created)
	 * 
	 * @systemAxisName Name of System axis
	 * 
	 * @validAxisName Name of Valid axis
	 */
	public static void addElementRangeIndexTemporalCollection(String dbName, String collectionName,
			String systemAxisName, String validAxisName) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("collection-name", collectionName);
		rootNode.put("system-axis", systemAxisName);
		rootNode.put("valid-axis", validAxisName);

		System.out.println(rootNode.toString());

		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));

		HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/collections?format=json");

		post.addHeader("Content-type", "application/json");
		post.addHeader("accept", "application/json");
		post.setEntity(new StringEntity(rootNode.toString()));

		HttpResponse response = client.execute(post);
		HttpEntity respEntity = response.getEntity();
		if (response.getStatusLine().getStatusCode() == 400) {
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			System.out.println(responseString);
		} else if (respEntity != null) {
			// EntityUtils to get the response content
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);

			System.out.println("Temporal collection: " + collectionName + " created");
			System.out.println("==============================================================");
		} else {
			System.out.println("No Proper Response");
		}
		client.getConnectionManager().shutdown();
	}

	/*
	 * Create a temporal collection
	 * 
	 * @dbName Database Name
	 * 
	 * @collectionName Collection Name (name of temporal collection that needs
	 * to be created)
	 * 
	 * @systemAxisName Name of System axis
	 * 
	 * @validAxisName Name of Valid axis
	 */
	public static void updateTemporalCollectionForLSQT(String dbName, String collectionName, boolean enable)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("lsqt-enabled", enable);

		// Set system time values
		ObjectNode automation = mapper.createObjectNode();
		automation.put("enabled", true);

		rootNode.set("automation", automation);

		System.out.println(rootNode.toString());

		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));

		HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/collections/lsqt/properties?collection=" + collectionName);

		put.addHeader("Content-type", "application/json");
		put.addHeader("accept", "application/json");
		put.setEntity(new StringEntity(rootNode.toString()));

		HttpResponse response = client.execute(put);
		HttpEntity respEntity = response.getEntity();
		if (response.getStatusLine().getStatusCode() == 400) {
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			System.out.println(responseString);
		} else if (respEntity != null) {
			// EntityUtils to get the response content
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);

			System.out.println("Temporal collection: " + collectionName + " created");
			System.out.println("==============================================================");
		} else {
			System.out.println("No Proper Response");
		}
		client.getConnectionManager().shutdown();
	}

	/*
	 * Delete a temporal collection
	 * 
	 * @dbName Database Name
	 * 
	 * @collectionName Collection Name
	 */
	public static void deleteElementRangeIndexTemporalCollection(String dbName, String collectionName)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));

		HttpDelete del = new HttpDelete("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/collections?collection=" + collectionName + "&format=json");

		del.addHeader("Content-type", "application/json");
		del.addHeader("accept", "application/json");

		HttpResponse response = client.execute(del);
		HttpEntity respEntity = response.getEntity();
		if (response.getStatusLine().getStatusCode() == 400) {
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			System.out.println(responseString);
		} else if (respEntity != null) {
			// EntityUtils to get the response content
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);
		} else {
			System.out.println("Collection: " + collectionName + " deleted");
			System.out.println("==============================================================");
		}
		client.getConnectionManager().shutdown();
	}

	public static void loadBug18993() {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, 8011),
					new UsernamePasswordCredentials("admin", "admin"));
			String document = "<foo>a space b</foo>";
			String perm = "perm:rest-writer=read&perm:rest-writer=insert&perm:rest-writer=update&perm:rest-writer=execute";
			HttpPut put = new HttpPut(
					"http://" + host_name + ":" + getRestAppServerPort() + "/v1/documents?uri=/a%20b&" + perm);
			put.addHeader("Content-type", "application/xml");
			put.setEntity(new StringEntity(document));
			HttpResponse response = client.execute(put);
			HttpEntity respEntity = response.getEntity();
			if (respEntity != null) {
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static void setAuthentication(String level, String restServerName)
			throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));
		String body = "{\"authentication\": \"" + level + "\"}";

		HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
				+ "/properties?server-type=http&group-id=Default");
		put.addHeader("Content-type", "application/json");
		put.setEntity(new StringEntity(body));

		HttpResponse response2 = client.execute(put);
		HttpEntity respEntity = response2.getEntity();
		if (respEntity != null) {
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);
		}
		client.getConnectionManager().shutdown();
	}

	public static void setDefaultUser(String usr, String restServerName) throws ClientProtocolException, IOException {

		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));
		String body = "{\"default-user\": \"" + usr + "\"}";

		HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
				+ "/properties?server-type=http&group-id=Default");
		put.addHeader("Content-type", "application/json");
		put.setEntity(new StringEntity(body));

		HttpResponse response2 = client.execute(put);
		HttpEntity respEntity = response2.getEntity();
		if (respEntity != null) {
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);
		}
		client.getConnectionManager().shutdown();
	}

	public static void setupServerRequestLogging(DatabaseClient client, boolean flag) throws Exception {
		ServerConfigurationManager scm = client.newServerConfigManager();
		scm.readConfiguration();
		scm.setServerRequestLogging(flag);
		scm.writeConfiguration();
	}

	/*
	 * This method inserts a path range index, in a JsonNode object, into the
	 * database.
	 */
	public static void setPathRangeIndexInDatabase(String dbName, JsonNode jnode) throws IOException {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			put.addHeader("Content-type", "application/json");
			put.setEntity(new StringEntity(jnode.toString()));

			HttpResponse response = client.execute(put);
			HttpEntity respEntity = response.getEntity();
			if (respEntity != null) {
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	/**
	 * Returns a SSLContext, so that the tests can run on a SSL enabled REST
	 * server.
	 * 
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws UnrecoverableKeyException
	 */
	public static SSLContext getSslContext() throws IOException, NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, CertificateException, UnrecoverableKeyException {
		// create a trust manager
		// (note: a real application should verify certificates)

		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				// nothing to do
			}

			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				// nothing to do
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};

		// get the client certificate. In case we need to modify path.
		String mlCertFile = new String(ml_certificate_file);

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		Properties property = new Properties();
		InputStream keyInput = property.getClass().getResourceAsStream(mlCertFile);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			keyStore.load(keyInput, ml_certificate_password.toCharArray());
		} finally {
			if (keyInput != null)
				keyInput.close();
		}
		keyManagerFactory.init(keyStore, ml_certificate_password.toCharArray());
		KeyManager[] keyMgr = keyManagerFactory.getKeyManagers();

		// create an SSL context
		SSLContext mlsslContext = SSLContext.getInstance("TLSv1.2");
		mlsslContext.init(keyMgr, new TrustManager[] { tm }, null);

		return mlsslContext;
	}

	/**
	 * Clear the database contents based on port for SSL or non SSL enabled REST
	 * Server.
	 * 
	 * @param dbName
	 * @param fNames
	 * @throws Exception
	 */
	public static void clearDB() throws Exception {
		clearDB(getRestServerPort());
	}

	/**
	 * Configure a SSL or non SSL enabled REST Server based on the build.gradle
	 * ssl setting.
	 * 
	 * @param dbName
	 * @param fNames
	 * @throws Exception
	 */
	public static void configureRESTServer(String dbName, String[] fNames) throws Exception {
		loadGradleProperties();
		if (IsSecurityEnabled())
			setupJavaRESTServer(dbName, fNames[0], restSslServerName, getRestServerPort());
		else
			setupJavaRESTServer(dbName, fNames[0], restServerName, getRestServerPort());
		if (isLBHost())
			setRESTServerWithDistributeTimestamps(restServerName, "cluster");
	}

	/**
	 * Configure a SSL or non SSL enabled REST Server based on the build.gradle
	 * ssl setting. Associate database or not
	 * 
	 * @param dbName
	 * @param fNames
	 * @throws Exception
	 */
	public static void configureRESTServer(String dbName, String[] fNames, boolean bAssociateDB) throws Exception {
		loadGradleProperties();
		if (IsSecurityEnabled())
			setupJavaRESTServer(dbName, fNames[0], restSslServerName, getRestServerPort(), bAssociateDB);
		else
			setupJavaRESTServer(dbName, fNames[0], restServerName, getRestServerPort(), bAssociateDB);
	}

	/**
	 * Removes the database and forest from a REST server.
	 * 
	 * @param dbName
	 * @param fNames
	 * @throws Exception
	 */
	public static void cleanupRESTServer(String dbName, String[] fNames) throws Exception {
		if (IsSecurityEnabled())
			tearDownJavaRESTServer(dbName, fNames, restSslServerName);
		else
			tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

	/**
	 * Returns true or false based security (ssl) is enabled or disabled.
	 * 
	 * @return
	 */
	public static boolean IsSecurityEnabled() {
		boolean bSecurityEnabled = false;
		if (getSslEnabled().trim().equalsIgnoreCase("true"))
			bSecurityEnabled = true;
		else if (getSslEnabled().trim().equalsIgnoreCase("false") || getSslEnabled() == null
				|| getSslEnabled().trim().isEmpty())
			bSecurityEnabled = false;
		return bSecurityEnabled;
	}

	public static DatabaseClient getDatabaseClient()
			throws KeyManagementException, NoSuchAlgorithmException, IOException {
		DatabaseClient client = DatabaseClientFactory.newClient(getServer(), getRestServerPort());
		return client;
	}
	
	public static DatabaseClient getDatabaseClient(String user, String password, ConnectionType connType)
			throws KeyManagementException, NoSuchAlgorithmException, IOException {
		DatabaseClient client = null;
		
		SSLContext sslcontext = null;
		SecurityContext secContext = new DatabaseClientFactory.DigestAuthContext(user,password);
		if (IsSecurityEnabled()) {
			try {
				sslcontext = getSslContext();
			} catch (UnrecoverableKeyException | KeyStoreException | CertificateException e) {
				e.printStackTrace();
			}
			secContext = secContext.withSSLContext(sslcontext).withSSLHostnameVerifier(SSLHostnameVerifier.ANY);
		}
			client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(),
					secContext, connType);				
		return client;
	}

	public static DatabaseClient getDatabaseClient(String user, String password, Authentication authType)
			throws KeyManagementException, NoSuchAlgorithmException, IOException {
		DatabaseClient client = null;
		try {
			SSLContext sslcontext = null;
			if (IsSecurityEnabled()) {
				sslcontext = getSslContext();
				client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), user, password,
						authType, sslcontext, SSLHostnameVerifier.ANY);
			} else
				client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), user, password,
						authType);
		} catch (CertificateException certEx) {
			// TODO Auto-generated catch block
			certEx.printStackTrace();
		} catch (KeyStoreException ksEx) {
			// TODO Auto-generated catch block
			ksEx.printStackTrace();
		} catch (UnrecoverableKeyException unReovkeyEx) {
			// TODO Auto-generated catch block
			unReovkeyEx.printStackTrace();
		}
		return client;
	}

	/*
	 * To provide DatabaseClient instance in the following cases. Access a
	 * specific database on non uber port i.e., 8012 Access a specific database
	 * through uber server on port (specifically port 8000)
	 */
	public static DatabaseClient getDatabaseClientOnDatabase(String hostName, int port, String databaseName,
			String user, String password, ConnectionType connType)
			throws KeyManagementException, NoSuchAlgorithmException, IOException {
		DatabaseClient client = null;
		try {
			SSLContext sslcontext = null;
			// Enable secure access on non 8000 port. Uber servers on port 8000
			// aren't
			// security enabled as of now.
			
			if (IsSecurityEnabled() && port != 8000) {
				
				sslcontext = getSslContext();
				if (hostName.equalsIgnoreCase(host_name))
					hostName = getSslServer();
				
				SecurityContext secContext = new DatabaseClientFactory.DigestAuthContext(user,password);
				secContext.withSSLContext(sslcontext).withSSLHostnameVerifier(SSLHostnameVerifier.ANY);
				
				client = DatabaseClientFactory.newClient(hostName, port, databaseName, secContext, connType);
			} else {
				SecurityContext secContext = new DatabaseClientFactory.DigestAuthContext(user,password);
				if (hostName.equalsIgnoreCase(host_name))
					hostName = getServer();
				client = DatabaseClientFactory.newClient(hostName, port, databaseName, secContext, connType);
			}
		} catch (CertificateException certEx) {
			// TODO Auto-generated catch block
			certEx.printStackTrace();
		} catch (KeyStoreException ksEx) {
			// TODO Auto-generated catch block
			ksEx.printStackTrace();
		} catch (UnrecoverableKeyException unReovkeyEx) {
			// TODO Auto-generated catch block
			unReovkeyEx.printStackTrace();
		}
		return client;
	}

	/**
	 * Return a Server name. For SSL runs returns value in restSslServerName For
	 * non SSL runs returns restServerName
	 * 
	 * @return
	 */

	public static String getRestServerName() {
		return (getSslEnabled().trim().equalsIgnoreCase("true") ? restSslServerName : restServerName);
	}

	/**
	 * Return a Server host name configured in build.gradle. For SSL runs
	 * returns SSL_HOST_NAME For non SSL runs returns HOST_NAME
	 * 
	 * @return
	 */

	public static String getRestServerHostName() {
		return (getSslEnabled().trim().equalsIgnoreCase("true") ? getSslServer() : getServer());
	}

	/**
	 * Return a Server host port configured in build.gradle. For SSL runs
	 * returns HTTPS_PORT For non SSL runs returns HTTP_PORT
	 * 
	 * @return
	 */

	public static int getRestServerPort() {
		return (getSslEnabled().trim().equalsIgnoreCase("true") ? getHttpsPort() : getHttpPort());
	}

	public static void loadGradleProperties() {
		Properties property = new Properties();
		InputStream input = null;

		try {
			input = ConnectedRESTQA.class.getResourceAsStream("/test.properties");

			// load a properties file
			property.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		// Set the variable values.

		// Rest App server names and ports.
		restServerName = property.getProperty("mlAppServerName");
		restSslServerName = property.getProperty("mlAppServerSSLName");

		https_port = property.getProperty("httpsPort");
		http_port = property.getProperty("httpPort");
		admin_port = property.getProperty("adminPort");

		// Machine names where ML Server runs
		host_name = property.getProperty("restHost");
		ssl_host_name = property.getProperty("restSSLHost");

		// Users
		admin_user = property.getProperty("mlAdminUser");
		admin_password = property.getProperty("mlAdminPassword");

		mlRestWriteUser = property.getProperty("mlRestWriteUser");
		mlRestWritePassword = property.getProperty("mlRestWritePassword");

		mlRestAdminUser = property.getProperty("mlRestAdminUser");
		mlRestAdminPassword = property.getProperty("mlRestAdminPassword");

		mlRestReadUser = property.getProperty("mlRestReadUser");
		mlRestReadPassword = property.getProperty("mlRestReadPassword");

		// Security and Certificate properties.
		ssl_enabled = property.getProperty("restSSLset");
		ml_certificate_password = property.getProperty("ml_certificate_password");
		ml_certificate_file = property.getProperty("ml_certificate_file");
		ml_certificate_path = property.getProperty("ml_certificate_path");
		mlDataConfigDirPath = property.getProperty("mlDataConfigDirPath");
		isLBHost = Boolean.parseBoolean(property.getProperty("lbHost"));
		PROPERTY_WAIT = Integer.parseInt(isLBHost ? "15000" : "0");		
	}
	
	public static boolean isLBHost() {
		return isLBHost;
	}

	public static DatabaseClient.ConnectionType getConnType(){
		return (isLBHost==true)?ConnectionType.GATEWAY:ConnectionType.DIRECT;
	}
	public static String getAdminUser() {
		return admin_user;
	}

	public static String getAdminPassword() {
		return admin_password;
	}

	public static String getRestWriterUser() {
		return mlRestWriteUser;
	}

	public static String getRestWriterPassword() {
		return mlRestWritePassword;
	}

	public static String getRestAdminUser() {
		return mlRestAdminUser;
	}

	public static String getRestAdminPassword() {
		return mlRestAdminPassword;
	}

	public static String getRestReaderUser() {
		return mlRestReadUser;
	}

	public static String getRestReaderPassword() {
		return mlRestReadPassword;
	}

	public static String getSslEnabled() {
		return ssl_enabled;
	}

	public static String getDataConfigDirPath() {
		return mlDataConfigDirPath;
	}

	public static int getRestAppServerPort() {
		return (IsSecurityEnabled() ? getHttpsPort() : getHttpPort());
	}

	// Returns the name of the REST Application server name. Currently on single
	// node.
	public static String getRestAppServerName() {
		return (IsSecurityEnabled() ? getSslAppServerName() : getAppServerName());
	}

	// Returns the Host name where REST Application server runs. Currently on
	// single node.
	public static String getRestAppServerHostName() {
		return (IsSecurityEnabled() ? getSslServer() : getServer());
	}

	public static int getHttpsPort() {
		return (Integer.parseInt(https_port));
	}

	public static int getHttpPort() {
		return (Integer.parseInt(http_port));
	}

	public static int getAdminPort() {
		return (Integer.parseInt(admin_port));
	}

	/*
	 * This needs to be a FQDN when SSL is enabled. Else localhost. Set in
	 * test.properties or using a sed in build script
	 */
	public static String getServer() {
		if (IsSecurityEnabled()) {
			// Some servers do not seem to be configured with FQDN.
			if (!host_name.endsWith(".marklogic.com"))
				return host_name + ".marklogic.com";
		}
		return host_name;
	}

	/*
	 * This needs to be a FQDN when SSL is enabled. Else localhost. Set in
	 * test.properties or using a sed in build script
	 */
	public static String getSslServer() {
		if (IsSecurityEnabled()) {
			// Some servers do not seem to be configured with FQDN.
			if (!ssl_host_name.endsWith(".marklogic.com"))
				return ssl_host_name + ".marklogic.com";
		}
		return ssl_host_name;
	}

	public static String getAppServerName() {
		return restServerName;
	}

	public static String getSslAppServerName() {
		return restSslServerName;
	}

	/*
	 * Associate REST server with External Security (Kerberos) Property changes
	 * needed for Kerberos are:
	 * 
	 * authentication set to "kerberos-ticket" internal security set to "false"
	 * external security set to "$extSecurityrName"
	 */
	public static void associateRESTServerWithKerberosExtSecurity(String restServerName, String extSecurityrName)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));
		String body = "{\"group-name\": \"Default\", \"authentication\":\"kerberos-ticket\",\"internal-security\": \"false\",\"external-security\": \""
				+ extSecurityrName + "\"}";

		HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
				+ "/properties?server-type=http");
		put.addHeader("Content-type", "application/json");
		put.setEntity(new StringEntity(body));

		HttpResponse response2 = client.execute(put);
		HttpEntity respEntity = response2.getEntity();
		if (respEntity != null) {
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);
		}
		client.getConnectionManager().shutdown();
	}

	/*
	 * Associate REST server with Digest Auth Property changes needed for
	 * Kerberos are:
	 * 
	 * authentication set to "Digest" internal security set to "true"
	 */
	public static void associateRESTServerWithDigestAuth(String restServerName) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));
		String extSecurityrName = "";
		String body = "{\"group-name\": \"Default\", \"authentication\":\"Digest\",\"internal-security\": \"true\",\"external-security\": \""
				+ extSecurityrName + "\"}";
		;

		HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
				+ "/properties?server-type=http");
		put.addHeader("Content-type", "application/json");
		put.setEntity(new StringEntity(body));

		HttpResponse response2 = client.execute(put);
		HttpEntity respEntity = response2.getEntity();
		if (respEntity != null) {
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);
		}
		client.getConnectionManager().shutdown();
	}

	/*
	 * Creates an external security name.
	 */

	public static void createExternalSecurityForKerberos(String restServerName, String extSecurityName)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				new UsernamePasswordCredentials("admin", "admin"));
		String body = "{\"authentication\": \"kerberos\", \"external-security-name\":\"" + extSecurityName
				+ "\", \"description\":\"External Kerberos Security\""
				+ ",\"cache-timeout\":\"300\", \"authorization\":\"internal\"," + "\"ldap-server-uri\":\"\","
				+ "\"ldap-base\":\"\"," + "\"ldap-attribute\":\"\"," + "\"ldap-default-user\":\"\","
				+ "\"ldap-password\":\"\"," + "\"ldap-bind-method\":\"MD5\","
				+ "\"ssl-require-client-certificate\":\"true\"" + "}";

		HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/external-security");
		post.addHeader("Content-type", "application/json");
		post.setEntity(new StringEntity(body));

		HttpResponse response2 = client.execute(post);
		HttpEntity respEntity = response2.getEntity();
		if (respEntity != null) {
			String content = EntityUtils.toString(respEntity);
			System.out.println(content);
		}
		client.getConnectionManager().shutdown();
	}

	/*
	 * This function creates a REST user with a Kerberos External name and given
	 * roles
	 */

	public static void createRESTKerberosUser(String usrName, String pass, String externalName, String... roleNames) {
		DefaultHttpClient clientReq = null;
		DefaultHttpClient clientPost = null;
		try {
			clientReq = new DefaultHttpClient();
			clientReq.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/users/" + usrName);
			HttpResponse resp = clientReq.execute(getrequest);

			if (resp.getStatusLine().getStatusCode() == 200) {
				System.out.println("Kerberos User already exist");
			} else {
				System.out.println("Kerberos User dont exist");
				clientPost = new DefaultHttpClient();
				clientPost.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
						new UsernamePasswordCredentials("admin", "admin"));

				ObjectMapper mapper = new ObjectMapper();
				ObjectNode mainNode = mapper.createObjectNode();
				// ObjectNode childNode = mapper.createObjectNode();
				ArrayNode childArray = mapper.createArrayNode();
				mainNode.put("user-name", usrName);
				mainNode.put("description", "user discription");
				mainNode.put("password", pass);
				for (String rolename : roleNames)
					childArray.add(rolename);
				mainNode.withArray("role").addAll(childArray);

				// Enable External Name(s)
				ArrayNode childArrayExtNames = mapper.createArrayNode();
				ObjectNode extNameNode = mapper.createObjectNode();
				extNameNode.put("external-name", externalName);

				childArrayExtNames.add(extNameNode);
				mainNode.withArray("external-names").addAll(childArrayExtNames);

				System.out.println(mainNode.toString());
				HttpPost post = new HttpPost("http://" + host_name + ":" + admin_port + "/manage/v2/users?format=json");
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(mainNode.toString()));

				HttpResponse response = clientPost.execute(post);
				HttpEntity respEntity = response.getEntity();
				if (response.getStatusLine().getStatusCode() == 400) {
					System.out.println("Kerberos User already exist - Status Code 400");
				} else if (respEntity != null) {
					// EntityUtils to get the response content
					String content = EntityUtils.toString(respEntity);
					System.out.println(content);
				} else {
					System.out.println("No Proper Response - Kerberos User");
				}
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			clientReq.getConnectionManager().shutdown();
			clientPost.getConnectionManager().shutdown();
		}
	}

	public static void changeProperty(Map<String, String> properties, String endpoint) {
		try {
			StringBuffer xmlBuff = new StringBuffer();
			xmlBuff.append("{");
			Iterator it = properties.entrySet().iterator();
			int size = properties.size();
			int j = 0;
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				xmlBuff.append("\"").append(pair.getKey()).append("\":");
				if (j == (size - 1))
					xmlBuff.append("\"").append(pair.getValue()).append("\"");
				else
					xmlBuff.append("\"").append(pair.getValue()).append("\",");
				j++;
			}
			xmlBuff.append('}');
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpPut post = new HttpPut("http://" + host_name + ":" + admin_port + endpoint);
			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(xmlBuff.toString()));

			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static JsonNode getState(Map<String, String> properties, String endpoint) {
		try {

			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));

			StringBuilder xmlBuff = new StringBuilder();
			Iterator it = properties.entrySet().iterator();
			int size = properties.size();
			int j = 0;
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				xmlBuff.append(pair.getKey());
				if (j == (size - 1)) {
					xmlBuff.append('=').append(pair.getValue());
				} else {
					xmlBuff.append('=').append(pair.getValue()).append('&');
				}

				j++;
			}
			HttpGet get = new HttpGet(
					"http://" + host_name + ":" + admin_port + endpoint + "?format=json&" + xmlBuff.toString());
			HttpResponse response = client.execute(get);
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			JsonNode actualObj = new ObjectMapper().readTree(body);
			return actualObj;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String[] getHosts() {
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet get = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/hosts?format=json");

			HttpResponse response = client.execute(get);
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			JsonNode actualObj = new ObjectMapper().readTree(body);
			JsonNode nameNode = actualObj.path("host-default-list").path("list-items");
			List<String> hosts = nameNode.findValuesAsText("nameref");
			String[] s = new String[hosts.size()];
			hosts.toArray(s);
			return s;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Disable automation for a LSQT enabled DB on a collection. Tests need to manually advance LSQT.
	  public static void disableAutomationOnTemporalCollection(String dbName, String collectionName, boolean enable)
	          throws Exception {
	      ObjectMapper mapper = new ObjectMapper();
	      ObjectNode rootNode = mapper.createObjectNode();
	      rootNode.put("lsqt-enabled", enable);

	      // Set automation value to false
	      ObjectNode automation = mapper.createObjectNode();
	      automation.put("enabled", false);

	      rootNode.set("automation", automation);

	      System.out.println(rootNode.toString());

	      DefaultHttpClient client = new DefaultHttpClient();
	      client.getCredentialsProvider().setCredentials(
	              new AuthScope(host_name, getAdminPort()),
	              new UsernamePasswordCredentials("admin", "admin"));

	      HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName + "/temporal/collections/lsqt/properties?collection=" + collectionName);

	      put.addHeader("Content-type", "application/json");
	      put.addHeader("accept", "application/json");
	      put.setEntity(new StringEntity(rootNode.toString()));

	      HttpResponse response = client.execute(put);
	      HttpEntity respEntity = response.getEntity();
	      if (response.getStatusLine().getStatusCode() == 400) {
	          HttpEntity entity = response.getEntity();
	          String responseString = EntityUtils.toString(entity, "UTF-8");
	          System.out.println(responseString);
	      }
	      else if (respEntity != null) {
	          // EntityUtils to get the response content
	          String content = EntityUtils.toString(respEntity);
	          System.out.println(content);

	          System.out.println("Temporal collection: " + collectionName + " created");
	          System.out.println("==============================================================");
	      }
	      else {
	          System.out.println("No Proper Response");
	      }
	      client.getConnectionManager().shutdown();
	  }
	  
	  public static int getDocumentCount(String dbName) throws IOException {
	      InputStream jsonstream = null;
	      DefaultHttpClient client = null;
	      int nCount = 0;
	      try {
	          client = new DefaultHttpClient();
	          client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
	                  new UsernamePasswordCredentials("admin", "admin"));
	          HttpGet getrequest = new HttpGet("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
	                  + "?view=counts&format=json");
	          HttpResponse response1 = client.execute(getrequest);
	          jsonstream = response1.getEntity().getContent();
	          JsonNode jnode = new ObjectMapper().readTree(jsonstream);


	          if (!jnode.isNull()) {
	              nCount = jnode.path("database-counts").path("count-properties").path("documents").get("value").asInt();
	              System.out.println(jnode);
	          } else {
	              System.out.println("REST call for database properties returned NULL ");
	          }
	      } catch (Exception e) {
	          // writing error to Log
	          e.printStackTrace();
	      } finally {
	          if (jsonstream == null) {
	          } else {
	              jsonstream.close();
	          }
	          client.getConnectionManager().shutdown();
	      }
	      return nCount;
	  }
	  // Wait for all nodes to be informed when property is updated in AWS env
	  public static void waitForPropertyPropagate() {
		  waitFor(PROPERTY_WAIT);
	  }

	  public static void waitFor(int milliseconds) {
		  if (milliseconds > 0) {
			  try {
				  Thread.sleep(milliseconds);
			  } catch (InterruptedException e) {
				  e.printStackTrace(System.out);
			  }
		  }
	  }
	  /*
	   * Associate REST server with timestamps in "distribute timestamps" to specify distribution of commit timestamps
	   * For example set to "strict" for Application Load Balancing (AWS) 
	   * 
	   */
	  private static void setRESTServerWithDistributeTimestamps(String restServerName, String distributeTimestampType) throws Exception {
		  DefaultHttpClient client = new DefaultHttpClient();

		  client.getCredentialsProvider().setCredentials(new AuthScope(host_name, getAdminPort()),
				  new UsernamePasswordCredentials("admin", "admin"));
		  String extSecurityrName = "";
		  String body = "{\"group-name\": \"Default\",\"distribute-timestamps\": \"" + distributeTimestampType + "\"}";
		  ;

		  HttpPut put = new HttpPut("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
				  + "/properties?server-type=http");
		  put.addHeader("Content-type", "application/json");
		  put.setEntity(new StringEntity(body));

		  HttpResponse response2 = client.execute(put);
		  HttpEntity respEntity = response2.getEntity();
		  if (respEntity != null) {
			  String content = EntityUtils.toString(respEntity);
			  System.out.println(content);
		  }
		  client.getConnectionManager().shutdown();
	  }
}