/*
 * Copyright (c) 2021 MarkLogic Corporation
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
import com.marklogic.client.impl.OkHttpServices;
import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import okhttp3.*;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

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
	private static final int ML_RES_OK = 200;
	private static final int ML_RES_CREATED = 201;
	private static final int ML_RES_SRVRDELETE = 202;
	private static final int ML_RES_CHANGED = 204;
	private static final int ML_RES_BADREQT = 400;
	private static final int ML_RES_NOTFND = 404;
	private static final String ML_MANAGE_DB = "App-Services";

	SSLContext sslContext = null;

	// Using MarkLogic client API's OKHttpClient Impl to connect to App-Services DB and use REST Manage API calls.
	private static OkHttpClient createManageAdminClient(String username, String password) {
		// build client with authentication information.
		RESTServices services = new OkHttpServices();
		services.connect(host_name, Integer.parseInt(admin_port), ML_MANAGE_DB, new DatabaseClientFactory.DigestAuthContext(username, password));
		OkHttpClient okHttpClient  = (OkHttpClient) services.getClientImplementation();
		return okHttpClient;
	}

	// Use Rest call to create a database.
	public static void createDB(String dbName) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String JSONString = "[{\"database-name\":\"" + dbName + "\"}]";
			String  urlStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases");

			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(urlStr)
					.post(RequestBody.create(JSONString, MediaType.parse("application/json")))
					.build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
				else {
					// Get response body
					if (response.code() == ML_RES_CREATED) {
						System.out.println("Created " + dbName + " database");
						System.out.println(response);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getBootStrapHostFromML()  {
		OkHttpClient client;
		StringBuilder resp = new StringBuilder();
		try {
			client = createManageAdminClient("admin", "admin");
			StringBuilder strBuf = new StringBuilder();
			String getrequest = new String(
					"http://" + host_name + ":" + admin_port + "/manage/v2/properties?format=json");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
				else {
					// Get response body
					if (response.code() == 200) {
						resp.append(response.body().string());
						System.out.println("BootStrapHostFromML : " + resp.toString());
					}
				}
			}
		 catch (Exception e) {
			e.printStackTrace();
		}
			JsonNode jnode = new ObjectMapper().readTree(resp.toString());
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
			e.printStackTrace();
			return host_name;
		}
	}

	public static void createForest(String fName, String dbName) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String urlStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/forests?format=json");
			String hName = getBootStrapHostFromML();
			String JSONString = "{\"database\":\"" + dbName + "\",\"forest-name\":\"" + fName + "\",\"host\":\"" + hName
					+ "\"}";
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(urlStr)
					.post(RequestBody.create(JSONString, MediaType.parse("application/json")))
					.build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
				else {
					if (response.code() == ML_RES_CREATED) {
						System.out.println("Created forest " + fName);
						System.out.println(response.body().string());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// creating forests on different hosts
	public static void createForestonHost(String fName, String dbName, String hName) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String urlStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/forests?format=json");
			String JSONString = "{\"database\":\"" + dbName + "\",\"forest-name\":\"" + fName + "\",\"host\":\"" + hName
					+ "\"}";

			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(urlStr)
					.post(RequestBody.create(JSONString, MediaType.parse("application/json")))
					.build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
				else {
					if (response.code() == ML_RES_CREATED) {
						System.out.println("Created forest " + fName + " on host " + hName);
						System.out.println(response.body().string());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void postRequest(Map<String, String> payload, Map<String, String> params, String endpoint) {
		OkHttpClient client;
		JSONObject JSONpayload = null; 
		try {
			if (payload == null) {
				JSONpayload = new JSONObject();
			}
			else {
				JSONpayload = new JSONObject(payload);
			}
			client = createManageAdminClient("admin", "admin");
			String postUrl = new String("http://" + host_name + ":" + admin_port + endpoint);
			StringBuilder resp = new StringBuilder();
			// Initialize Builder (not RequestBody)
			FormBody.Builder builder = new FormBody.Builder();

			if (params != null) {
				for(Map.Entry<String, String> entry: params.entrySet()) {
					builder.add(entry.getKey(), entry.getValue());
				}
			}
			RequestBody formBody = builder.build();

			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(postUrl)
					.post(formBody)
					.build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
				else  if (response.code() == ML_RES_OK) {
					resp.append(response.body().string());
					if (!resp.toString().isEmpty()) {
						System.out.println("Posted params ");
						System.out.println(resp);
					}
				} else {
					System.out.println("No proper reponse from post request");
					System.out.println(response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void assocRESTServer(String restServerName, String dbName, int restPort) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String urlStr = new String("http://" + host_name + ":" + admin_port + "/v1/rest-apis?format=json");
			String JSONString = "{ \"rest-api\": {\"name\":\"" + restServerName + "\",\"database\":\"" + dbName
					+ "\",\"port\":\"" + restPort + "\"}}";

			StringBuilder resp = new StringBuilder();
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(urlStr)
					.post(RequestBody.create(JSONString, MediaType.parse("application/json")))
					.build();
			try (Response response = client.newCall(request).execute()) {
				resp.append(response.body().string());
				if (!resp.toString().isEmpty()) {
					System.out.println("Will try to associate RESTServer with DB");
					//System.out.println(resp);
				}
			}
			JsonNode returnResp = new ObjectMapper().readTree(resp.toString());
			if (returnResp.get("errorResponse").get("statusCode").asInt() == ML_RES_BADREQT) {
				System.out.println("AppServer already exist");
				if (dbName.equals("Documents")) {
					System.out.println("and Context database is Documents DB");
				} else {
					System.out.println("and changing context database to " + dbName);
					associateRESTServerWithDB(restServerName, dbName);
				}
			} else if (returnResp.get("errorResponse").get("statusCode").asInt() == ML_RES_CREATED) {
				// Enable security on new REST Http Server if SSL is turned on.
				if (IsSecurityEnabled()) {
					enableSecurityOnRESTServer(restServerName, dbName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void enableSecurityOnRESTServer(String restServerName, String dbName) throws Exception {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String body = "{\"group-name\": \"Default\",\"internal-security\":\"true\", \"ssl-certificate-template\":\"ssl1-QAXdbcServer\", \"ssl-require-client-certificate\":\"true\""
					+ "}";
			StringBuilder resp = new StringBuilder();
			String put = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
					+ "/properties?server-type=http");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(put)
					.put(RequestBody.create(body, MediaType.parse("application/json")))
					.build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
				else {

					if (response.code() == ML_RES_CHANGED) {
						System.out.println("Enabled Security OnRESTServer " + restServerName) ;
						System.out.println(response.body().toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void associateRESTServerWithDB(String restServerName, String dbName) throws Exception {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String body = "{\"content-database\": \"" + dbName + "\",\"group-name\": \"Default\"}";

			String put = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
					+ "/properties?server-type=http");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(put)
					.put(RequestBody.create(body, MediaType.parse("application/json")))
					.build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
				else {
					if (response.code() == ML_RES_CHANGED) {
						System.out.println("Associated " + restServerName + " with database " + dbName);
						System.out.println(response);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Associate REST server with default user this is created for the sake of
	 * runtime DB selection
	 */
	public static void associateRESTServerWithDefaultUser(String restServerName, String userName, String authType)
			throws Exception {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String body = "{ \"default-user\":\"" + userName + "\",\"authentication\": \"" + authType
					+ "\",\"group-name\": \"Default\"}";
			StringBuilder resp = new StringBuilder();
			String put = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
					+ "/properties?server-type=http");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(put)
					.put(RequestBody.create(body, MediaType.parse("application/json")))
					.build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
				else {
					if (response.code() == ML_RES_CREATED) {
						System.out.println("Associate REST server with default user successful") ;
						System.out.println(response);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Creating RESTServer With default content and module database
	 */
	public static void createRESTServerWithDB(String restServerName, int restPort) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String getrequest = new String(
					"http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName + "?group-id=Default");
			Request requestGet = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			try (Response responseGet = client.newCall(requestGet).execute()) {
				if (responseGet.code() == ML_RES_OK) {
						System.out.println("Rest Server already present " + restServerName);
				}
				else if (responseGet.code() == ML_RES_NOTFND) {
						String post = new String("http://" + host_name + ":" + admin_port + "/v1/rest-apis?format=json");
						String JSONString = "{ \"rest-api\": {\"name\":\"" + restServerName + "\",\"port\":\"" + restPort + "\"}}";

						Request request = new Request.Builder()
							.header("Content-type", "application/json")
							.url(post)
							.post(RequestBody.create(JSONString, MediaType.parse("application/json")))
							.build();
						try (Response response = client.newCall(request).execute()) {
						if (!responseGet.isSuccessful()) throw new IOException("Unexpected code " + responseGet);

						if (response.code() == ML_RES_CREATED) {
							System.out.println("created REST Server " + restServerName + " With DB");
							System.out.println(response.body().string());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		//logger.info("### Starting TESTCASE SETUP." + dbName + "### " + d);

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
	 * Create a role with given privileges
	 */
	public static void createUserRolesWithPrevilages(String roleName, String... privNames) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/roles/" + roleName);
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
				Response response = client.newCall(request).execute();
				if (response.code() == ML_RES_OK) {
					System.out.println("Role already exist");
				} else if (response.code() == ML_RES_NOTFND) {
					System.out.println("Role does not exist, will create now");
					String[] roleNames = { "rest-reader", "rest-writer" };

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

					String postUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/roles?format=json");
					Request requestPost = new Request.Builder()
							.header("Content-type", "application/json")
							.url(postUrl)
							.post(RequestBody.create(mainNode.toString(), MediaType.parse("application/json")))
							.build();
					Response responsePost = client.newCall(requestPost).execute();
					if (responsePost.code() == ML_RES_BADREQT) {
						System.out.println("Creation of role has a problem");
					} else if (responsePost.code() == ML_RES_CREATED && responsePost.body().string()!= null) {
						System.out.println("Created role " + roleName + " with required privileges");
						System.out.println(responsePost);
					} else {
						System.out.println("No Proper Response");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			client = null;
		}
	}

	/*
	 * Create a role with given privileges. With added Node Update Capability
	 * Similar to createUserRolesWithPrevileges method, but have Node Update.
	 */
	public static void createRoleWithNodeUpdate(String roleName, String... privNames) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/roles/" + roleName);
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_OK) {
				System.out.println("Role already exist");
			} else if (response.code() == ML_RES_NOTFND) {
				System.out.println("Role does not exist, will create now");
				String[] roleNames = { "rest-reader", "rest-writer" };

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
				String postUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/roles?format=json");
				Request requestPost = new Request.Builder()
						.header("Content-type", "application/json")
						.url(postUrl)
						.post(RequestBody.create(mainNode.toString(), MediaType.parse("application/json")))
						.build();
				Response responsePost = client.newCall(requestPost).execute();
				if (responsePost.code() == ML_RES_BADREQT) {
					System.out.println("Creation of role has a problem");
				} else if (responsePost.code() == ML_RES_CREATED && responsePost.body().string() != null) {
					System.out.println("Created role " + roleName + " with required privileges");
					System.out.println(responsePost);
				} else {
					System.out.println("No Proper Response");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	// This function creates a REST user with given roles
	public static void createRESTUser(String usrName, String pass, String... roleNames) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/users/" + usrName);
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_OK) {
				System.out.println("User already exist");
			} else {
				System.out.println("User does not exist");

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
				String postUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/users?format=json");
				Request requestPost = new Request.Builder()
						.header("Content-type", "application/json")
						.url(postUrl)
						.post(RequestBody.create(mainNode.toString(), MediaType.parse("application/json")))
						.build();
				Response responsePost = client.newCall(requestPost).execute();
				if (responsePost.code() == ML_RES_BADREQT) {
					System.out.println("Creation of user has a problem");
				} else if (responsePost.code() == ML_RES_CREATED && responsePost.body().string()!= null) {
					System.out.println("Created user " + usrName + " with required roles");
					System.out.println(responsePost);
				} else {
					System.out.println("No Proper Response");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	// "permission": [ { "role-name": "dls-user", "capability": "read" }
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
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/users/" + usrName);
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_OK) {
				System.out.println("User already exist");
			} else {
				System.out.println("User does not exist");

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
				String postUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/users?format=json");
				Request requestPost = new Request.Builder()
						.header("Content-type", "application/json")
						.url(postUrl)
						.post(RequestBody.create(mainNode.toString(), MediaType.parse("application/json")))
						.build();
				Response responsePost = client.newCall(requestPost).execute();
				if (responsePost.code() == ML_RES_BADREQT) {
					System.out.println("Bad User creation request");
				} else if (responsePost.code() == ML_RES_CREATED) {
					System.out.println(responsePost.body().string());
				} else {
					System.out.println("No Proper Response");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	public static void deleteRESTUser(String usrName) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String deleteUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/users/" + usrName);
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(deleteUrl)
					.delete()
					.build();
			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_CHANGED) {
				Thread.sleep(3500);
				System.out.println("User " + usrName + " deleted");
				System.out.println(response.body().string());
			}
			else {
					System.out.println("User " + usrName + " deletion has issues");
					System.out.println("Response from user deletion is: " + response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	public static void deleteUserRole(String roleName) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String deleteUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/roles/" + roleName);

			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(deleteUrl)
					.delete()
					.build();
			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_CHANGED) {
				Thread.sleep(3500);
				System.out.println("Role " + roleName + " deleted");
				System.out.println(response.body().string());
			}
			else {
				System.out.println("Role " + roleName + " deletion has issues");
				System.out.println("Response from role deletion is: " + response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
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
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String deleteUrl = new String("http://" + host_name + ":" + admin_port + "/v1/rest-apis/"
					+ restServerName + "?include=content&include=modules");

			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(deleteUrl)
					.delete()
					.build();
			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_SRVRDELETE) {
				Thread.sleep(9500);
				System.out.println("Server " + restServerName + " deleted");
				//System.out.println(response.body().string());
			}
			else {
				System.out.println("Server " + restServerName + " deletion has issues");
				System.out.println("Response from server deletion is: " + response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	public static void deleteRESTServer(String restServerName) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String deleteUrl = new String(
					"http://" + host_name + ":" + admin_port + "/v1/rest-apis/" + restServerName + "&include=modules");

			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(deleteUrl)
					.delete()
					.build();
			Response response = client.newCall(request).execute();

			if (response.code() == ML_RES_SRVRDELETE) {
				Thread.sleep(3500);
				waitForServerRestart();
			} else {
				Thread.sleep(3500);
				System.out.println("Server response " + response.body().string());
			}
		} catch (Exception e) {
			System.out.println("Inside Deleting Rest server is throwing an error");
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	public static void detachForest(String dbName, String fName) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");

			String postUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/forests/" + fName);
			RequestBody formBody = new FormBody.Builder()
					.add("state", "detach")
					.add("database", dbName)
					.build();
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(postUrl)
					.post(formBody)
					.build();
			Response response = client.newCall(request).execute();

			if (response.code() == ML_RES_OK) {
				System.out.println("Forest " + fName + " has been detached from database " + dbName);
			} else {
				System.out.println("Forest " + fName + " detaching from database " + dbName + " ran into problems");
				System.out.println(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	// Deleting a forest is a HTTP Delete request
	public static void deleteForest(String fName) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String deleteUrl = new String(
					"http://" + host_name + ":" + admin_port + "/manage/v2/forests/" + fName + "?level=full");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(deleteUrl)
					.delete()
					.build();
			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_CHANGED) {
				System.out.println("Forest " + fName + " has been deleted");
			} else {
				System.out.println("Forest " + fName + " deletion ran into problems");
				System.out.println(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	// Deleting Database
	public static void deleteDB(String dbName) {
		OkHttpClient client;
		try {
			client = createManageAdminClient("admin", "admin");
			String deleteUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName);
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(deleteUrl)
					.delete()
					.build();
			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_CHANGED) {
				System.out.println("Database " + dbName + " has been deleted");
			} else {
				System.out.println("Database " + dbName + " deletion ran into problems");
				System.out.println(response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	//Clear the Database
	public static void clearDB(int port) {
		OkHttpClient client = createManageAdminClient("admin", "admin");
		try {
			InputStream jsonstream = null;
			String uri = null;
			String resGet = null;
			JsonNode jnode = null;
			if (/*IsSecurityEnabled()*/false) {
				// In case of SSL use 8002 port to clear DB contents.
				String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/"
						+ getRestAppServerName() + "/properties?group-id=Default&format=json");
				Request request = new Request.Builder()
						.header("Content-type", "application/json")
						.url(getrequest)
						.build();
				Response response = client.newCall(request).execute();

				if (response.code() == ML_RES_OK) {
					resGet = response.body().string();
					System.out.println("Response from Get is " + resGet);
				}
				if (resGet != null && !resGet.isEmpty())
					jnode = new ObjectMapper().readTree(resGet);
				else throw new Exception("Unexpected error " + response);

				String dbName = jnode.get("content-database").asText();
				System.out.println("App Server's content database properties value from ClearDB is :" + dbName);

				ObjectMapper mapper = new ObjectMapper();
				ObjectNode mainNode = mapper.createObjectNode();

				mainNode.put("operation", "clear-database");

				String postUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName);
				Request requestSSLClear = new Request.Builder()
						.header("Content-type", "application/json")
						.url(postUrl)
						.post(RequestBody.create(mainNode.toString(), MediaType.parse("application/json")))
						.build();
				Response responseSSLClear = client.newCall(requestSSLClear).execute();
				if (responseSSLClear.code() == ML_RES_OK) {
					System.out.println(dbName + " database contents cleared");
				} else {
					System.out.println("Database contents did not clear");
				}
			} else {
				uri = "http://" + host_name + ":" + port + "/v1/search/";
				Request requestNormClear = new Request.Builder()
						.header("Content-type", "application/json")
						.url(uri)
						.delete()
						.build();
				Response responseNormClear = client.newCall(requestNormClear).execute();
				if (responseNormClear.code() == ML_RES_CHANGED)
					System.out.println("Content database cleared for App Server on port " + port);
				else {
					System.out.println("Content database not cleared");
					throw new Exception("Unexpected error " + responseNormClear);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	public static void waitForServerRestart() {
		OkHttpClient client = createManageAdminClient("admin", "admin");;
		try {
			int count = 0;
			while (count < 20) {
				count++;
				try {
					String getrequestUrl = new String("http://" + host_name + ":8001/admin/v1/timestamp");
					Request requestGet = new Request.Builder()
							.header("Content-type", "application/json")
							.url(getrequestUrl)
							.build();
					Response responseGet = client.newCall(requestGet).execute();

					if (responseGet.code() == 503) {
						Thread.sleep(5000);
					} else if (responseGet.code() == 200) {
						break;
					} else {
						System.out.println("Waiting for response from server, Trial :"
								+ responseGet.code() + count);
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
			client = null;
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
		//logger.info("### StartingTestCase TEARDOWN " + dbName + " ### " + d);

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

	// This function deletes rest server along with default forest and database
	public static void tearDownJavaRESTServerWithDB(String restServerName) throws Exception {
		try {
			deleteRESTServerWithDB(restServerName);
			waitForServerRestart();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(6000);
	}

	// Setting up AppServices configurations setting up database properties whose value is string
	public static void setDatabaseProperties(String dbName, String prop, String propValue) throws IOException {
		String resGet = null;
		JsonNode jnode = null;
		Response responsePut = null;
		OkHttpClient client = createManageAdminClient("admin", "admin");
		try {
			String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			Response response1 = client.newCall(request).execute();
			if (response1.code() == ML_RES_OK) {
				resGet = response1.body().string();
				System.out.println("Response from Get is " + resGet);
			}
			if (resGet != null && !resGet.isEmpty())
				jnode = new ObjectMapper().readTree(resGet);
			else throw new Exception("Unexpected error " + response1);

			if (!jnode.isNull()) {
				((ObjectNode) jnode).put(prop, propValue);
				String putUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
						+ "/properties?format=json");

				String putProps = jnode.toString();
				Request requestPut = new Request.Builder()
						.header("Content-type", "application/json")
						.url(putUrl)
						.put(RequestBody.create(putProps, MediaType.parse("application/json")))
						.build();
				responsePut = client.newCall(requestPut).execute();

				if (responsePut.code() == ML_RES_CHANGED) {
					System.out.println("Database " + dbName + ". property " + prop +" has been updated with " + propValue);
				}
			} else {
				System.out.println("REST call for database properties update has issues");
				System.out.println(responsePut.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	public static void setDatabaseProperties(String dbName, String prop, boolean propValue) throws IOException {
		String resGet = null;
		JsonNode jnode = null;
		Response responsePut = null;
		OkHttpClient client = createManageAdminClient("admin", "admin");
		try {
			String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			Response response1 = client.newCall(request).execute();
			if (response1.code() == ML_RES_OK) {
				resGet = response1.body().string();
				System.out.println("Response from Get is " + resGet);
			}
			if (resGet != null && !resGet.isEmpty())
				jnode = new ObjectMapper().readTree(resGet);
			else throw new Exception("Unexpected error " + response1);

			if (!jnode.isNull()) {
				((ObjectNode) jnode).put(prop, propValue);
				String putUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
						+ "/properties?format=json");

				String putProps = jnode.toString();
				Request requestPut = new Request.Builder()
						.header("Content-type", "application/json")
						.url(putUrl)
						.put(RequestBody.create(putProps, MediaType.parse("application/json")))
						.build();
				responsePut = client.newCall(requestPut).execute();

				if (responsePut.code() == ML_RES_CHANGED) {
					System.out.println("Database " + dbName + ". property " + prop +" has been updated with " + propValue);
				}
			} else {
				System.out.println("REST call for database properties update has issues");
				System.out.println(responsePut.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	/*
	 * This Method takes the root property name and object node under it if root
	 * propname exist and equals to null then it just add the object node under
	 * root property name else if it has an existing sub property name then it
	 * adds elements to that array
	 */
	public static void setDatabaseProperties(String dbName, String propName, ObjectNode objNode) throws IOException {
		String resGet = null;
		JsonNode jnode = null;
		Response responsePut = null;
		OkHttpClient client = createManageAdminClient("admin", "admin");
		try {
			String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			Response response1 = client.newCall(request).execute();
			if (response1.code() == ML_RES_OK) {
				resGet = response1.body().string();
				System.out.println("Response from Get is " + resGet);
			}
			if (resGet != null && !resGet.isEmpty())
				jnode = new ObjectMapper().readTree(resGet);
			else throw new Exception("Unexpected error " + response1);

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
				String putUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
						+ "/properties?format=json");

				String putProps = jnode.toString();
				Request requestPut = new Request.Builder()
						.header("Content-type", "application/json")
						.url(putUrl)
						.put(RequestBody.create(putProps, MediaType.parse("application/json")))
						.build();
				responsePut = client.newCall(requestPut).execute();
				System.out.println(responsePut);
				if (responsePut.code() == ML_RES_CHANGED) {
					System.out.println("Database " + dbName + ". property " + propName +" has been updated");
				}
			} else {
				System.out.println("REST call for database properties update has issues");
				System.out.println(responsePut.toString());
				System.out.println(jnode.toString());
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client = null;
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

	public static void addPathNamespace(String dbName, String[][] pathNamespace) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();

		int nRowsLen = pathNamespace.length;
		int j = 0;
		for (int i = 0; i < nRowsLen; i++) {
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put("prefix", pathNamespace[i][j++]);
			childNodeObject.put("namespace-uri", pathNamespace[i][j++]);
			/*
			 * if new field elements are to be added, then: 1) Increment value
			 * of j 2) add them below here using
			 * childNodeObject.put("FIELD-NAME", rangePaths[i][j++]);
			 */

			childArray.add(childNodeObject);
			j = 0;
		}
		childNode.putArray("path-namespace").addAll(childArray);
		setDatabaseProperties(dbName, "path-namespace", childNode);
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
		String resGet = null;
		JsonNode jnode = null;
		Response responsePut = null;
		OkHttpClient client = createManageAdminClient("admin", "admin");
		try {
			String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			Response response1 = client.newCall(request).execute();
			if (response1.code() == ML_RES_OK) {
				resGet = response1.body().string();
				System.out.println("Response from Get is " + resGet);
			}
			if (resGet != null && !resGet.isEmpty())
				jnode = new ObjectMapper().readTree(resGet);
			else throw new Exception("Unexpected error " + response1);

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
				String putUrl = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
						+ "/properties?format=json");
				String putProps = jnode.toString();
				Request requestPut = new Request.Builder()
						.header("Content-type", "application/json")
						.url(putUrl)
						.put(RequestBody.create(putProps, MediaType.parse("application/json")))
						.build();
				responsePut = client.newCall(requestPut).execute();
				System.out.println(responsePut);
				if (responsePut.code() == ML_RES_CHANGED) {
					System.out.println("Database " + dbName + ". property " + propName +" has been updated");
				}
			} else {
				System.out.println("REST call for database properties update has issues");
				System.out.println(responsePut.toString());
				System.out.println(jnode.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
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

		OkHttpClient client = createManageAdminClient("admin", "admin");

		String postStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/axes?format=json");

		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(postStr)
				.post(RequestBody.create(rootNode.toString(), MediaType.parse("application/json")))
				.build();
		Response response = client.newCall(request).execute();
		System.out.println(response);
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		else {
				if (response.code() == ML_RES_CREATED) {
					System.out.println("Temporal axis: " + axisName + " created");
				}
				else {
					System.out.println("No Proper Response in Temporal axis creation");
					System.out.println(response);
			}
		}
		client = null;
	}

	public static void deleteElementRangeIndexTemporalAxis(String dbName, String axisName) throws Exception {
		OkHttpClient client = createManageAdminClient("admin", "admin");

		String delStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/axes/" + axisName + "?format=json");

		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(delStr)
				.delete()
				.build();
		Response response = client.newCall(request).execute();

		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			else {
				if (response.code() == ML_RES_CHANGED) {
					System.out.println(axisName + " Axis deleted " + " on database " + dbName) ;
				}
				else {
					System.out.println("No Proper Response in Temporal axis deletion");
					System.out.println(response);
			}
		}
		client = null;
	}

	public static void addElementRangeIndexTemporalCollection(String dbName, String collectionName,
			String systemAxisName, String validAxisName) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("collection-name", collectionName);
		rootNode.put("system-axis", systemAxisName);
		rootNode.put("valid-axis", validAxisName);
		System.out.println(rootNode.toString());

		OkHttpClient client = createManageAdminClient("admin", "admin");
		String postStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/collections?format=json");

		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(postStr)
				.post(RequestBody.create(rootNode.toString(), MediaType.parse("application/json")))
				.build();
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		else if (response.code() == ML_RES_CREATED) {
			System.out.println("Temporal collection: " + collectionName + " created");
		} else {
			System.out.println("No Proper Response from Temporal collection creation");
		}
		client = null;
	}

	// Update temporal collection
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

		OkHttpClient client =createManageAdminClient("admin", "admin");

		String putStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/collections/lsqt/properties?collection=" + collectionName);

		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(putStr)
				.put(RequestBody.create(rootNode.toString(), MediaType.parse("application/json")))
				.build();
		Response response = client.newCall(request).execute();
		if (response.code() == ML_RES_CHANGED) {
			System.out.println("Temporal collection: " + collectionName + " updated");
		} else {
			System.out.println("No Proper Response from Temporal collection update");
		}
		client = null;
	}

	// Delete a temporal collection
	public static void deleteElementRangeIndexTemporalCollection(String dbName, String collectionName)
			throws Exception {
		OkHttpClient client = createManageAdminClient("admin", "admin");

		String del = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
				+ "/temporal/collections?collection=" + collectionName + "&format=json");
		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(del)
				.delete()
				.build();
		Response response = client.newCall(request).execute();
		if (response.code() == ML_RES_CHANGED) {
			Thread.sleep(3500);
			System.out.println("collection " + collectionName + " deleted");
			System.out.println(response.body().string());
		}
		else {
			System.out.println("collection " + collectionName + " deletion has issues");
			System.out.println("Response from collection deletion is: " + response);
		}
		client = null;
	}

	public static void loadBug18993() {
		OkHttpClient client = null;
		try {
			client = createManageAdminClient("admin", "admin");
			String document = "<foo>a space b</foo>";
			String perm = "perm:rest-writer=read&perm:rest-writer=insert&perm:rest-writer=update&perm:rest-writer=execute";
			String putStr = new String(
					"http://" + host_name + ":" + getRestAppServerPort() + "/v1/documents?uri=/a%20b&" + perm);
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(putStr)
					.put(RequestBody.create(document.toLowerCase(), MediaType.parse("application/xml")))
					.build();
			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_BADREQT) {
				System.out.println(response);
			}
			else {
				System.out.println("Loading documents for test 189933 has issues");
				System.out.println(response);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		} finally {
			client= null;
		}
	}

	public static void setAuthentication(String level, String restServerName)
			throws IOException {
		OkHttpClient  client = createManageAdminClient("admin", "admin");
		String body = "{\"authentication\": \"" + level + "\"}";

		String putStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
				+ "/properties?server-type=http&group-id=Default");
		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(putStr)
				.put(RequestBody.create(body, MediaType.parse("application/json")))
				.build();
		Response response = client.newCall(request).execute();

		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		else if (response.code() == ML_RES_CHANGED) {
			System.out.println("App Server Authentication value changed to " + level);
		}
		else {
			System.out.println("App Server Authentication value change ran into issues");
			System.out.println(response);
		}
		client = null;
	}

	public static void setDefaultUser(String usr, String restServerName) throws IOException {

		OkHttpClient client = createManageAdminClient("admin", "admin");

		String body = "{\"default-user\": \"" + usr + "\"}";

		String putStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
				+ "/properties?server-type=http&group-id=Default");
		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(putStr)
				.put(RequestBody.create(body, MediaType.parse("application/json")))
				.build();
		Response response = client.newCall(request).execute();

		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		else if (response.code() == ML_RES_CHANGED) {
			System.out.println("Default User name changed to " + usr);
		}
		else {
			System.out.println("Default User name change ran into issues");
			System.out.println(response);
		}
		client = null;
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
		OkHttpClient client = null;
		try {
			client = createManageAdminClient("admin", "admin");

			String putStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(putStr)
					.put(RequestBody.create(jnode.toString(), MediaType.parse("application/json")))
					.build();
			Response response = client.newCall(request).execute();

			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			else if (response.code() == ML_RES_CHANGED) {
				System.out.println("Path index assignment successful ");
			}
			else {
				System.out.println("Path index assignment ran into issues");
				System.out.println(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	/*
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

	/*
	 * Clear the database contents based on port for SSL or non SSL enabled REST
	 * Server.
	 * @throws Exception
	 */
	public static void clearDB() throws Exception {
		clearDB(getRestServerPort());
	}

	/*
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

	//Configure a SSL or non SSL enabled REST Server based on the build.gradle
	public static void configureRESTServer(String dbName, String[] fNames, boolean bAssociateDB) throws Exception {
		loadGradleProperties();
		if (IsSecurityEnabled())
			setupJavaRESTServer(dbName, fNames[0], restSslServerName, getRestServerPort(), bAssociateDB);
		else
			setupJavaRESTServer(dbName, fNames[0], restServerName, getRestServerPort(), bAssociateDB);
	}

	// Removes the database and forest from a REST server.
	public static void cleanupRESTServer(String dbName, String[] fNames) throws Exception {
		if (IsSecurityEnabled())
			tearDownJavaRESTServer(dbName, fNames, restSslServerName);
		else
			tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

	// Returns true or false based security (ssl) is enabled or disabled.
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

	//Return a Server name. For SSL runs returns value in restSslServerName For
	// non SSL runs returns restServerName
	public static String getRestServerName() {
		return (getSslEnabled().trim().equalsIgnoreCase("true") ? restSslServerName : restServerName);
	}

	/*
	 * Return a Server host name configured in build.gradle. For SSL runs
	 * returns SSL_HOST_NAME For non SSL runs returns HOST_NAME
	 * 
	 * @return
	 */
	public static String getRestServerHostName() {
		return (getSslEnabled().trim().equalsIgnoreCase("true") ? getSslServer() : getServer());
	}

	/*
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

	// Returns the name of the REST Application server name. Currently on single node.
	public static String getRestAppServerName() {
		return (IsSecurityEnabled() ? getSslAppServerName() : getAppServerName());
	}

	// Returns the Host name where REST Application server runs. Currently on single node.
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
		OkHttpClient client = createManageAdminClient("admin", "admin");
		String body = "{\"group-name\": \"Default\", \"authentication\":\"kerberos-ticket\",\"internal-security\": \"false\",\"external-security\": \""
				+ extSecurityrName + "\"}";

		String putStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
				+ "/properties?server-type=http");
		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(putStr)
				.put(RequestBody.create(body, MediaType.parse("application/json")))
				.build();
		Response response = client.newCall(request).execute();
		if (response.code() == ML_RES_CHANGED) {
			System.out.println("External security " + extSecurityrName + " has been associated with " + restServerName + " server");
		}
		else {
			System.out.println("External security association with App server has issues");
			System.out.println(response);
		}
		client = null;
	}

	/*
	 * Associate REST server with Digest Auth Property changes needed for
	 * are:
	 * authentication set to "Digest" internal security set to "true"
	 */
	public static void associateRESTServerWithDigestAuth(String restServerName) throws Exception {
		OkHttpClient client = createManageAdminClient("admin", "admin");
		String extSecurityrName = "";
		String body = "{\"group-name\": \"Default\", \"authentication\":\"Digest\",\"internal-security\": \"true\",\"external-security\": \""
				+ extSecurityrName + "\"}";
		String putStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
				+ "/properties?server-type=http");
		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(putStr)
				.put(RequestBody.create(body, MediaType.parse("application/json")))
				.build();
		Response response = client.newCall(request).execute();
		if (response.code() == ML_RES_CHANGED) {
			System.out.println("Digest Auth has been associated with " + restServerName + " server");
		}
		else {
			System.out.println("Digest Auth association with App server has issues");
			System.out.println(response);
		}
		client = null;
	}

	// Creates an external security name.
	public static void createExternalSecurityForKerberos(String restServerName, String extSecurityName)
			throws Exception {
		OkHttpClient client = createManageAdminClient("admin", "admin");
		String body = "{\"authentication\": \"kerberos\", \"external-security-name\":\"" + extSecurityName
				+ "\", \"description\":\"External Kerberos Security\""
				+ ",\"cache-timeout\":\"300\", \"authorization\":\"internal\"," + "\"ldap-server-uri\":\"\","
				+ "\"ldap-base\":\"\"," + "\"ldap-attribute\":\"\"," + "\"ldap-default-user\":\"\","
				+ "\"ldap-password\":\"\"," + "\"ldap-bind-method\":\"MD5\","
				+ "\"ssl-require-client-certificate\":\"true\"" + "}";

		String postStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/external-security");
		Request request = new Request.Builder()
				.header("Content-type", "application/json")
				.url(postStr)
				.post(RequestBody.create(body, MediaType.parse("application/json")))
				.build();
		Response response = client.newCall(request).execute();
		if (response.code() == ML_RES_CREATED) {
			System.out.println("External security " + extSecurityName + " created and associated with " + restServerName + " server");
		}
		else {
			System.out.println("External security creation and association with App server has issues");
			System.out.println(response);
		}
		client = null;
	}

	// This function creates a REST user with a Kerberos External name and given roles
	public static void createRESTKerberosUser(String usrName, String pass, String externalName, String... roleNames) {
		OkHttpClient client = createManageAdminClient("admin", "admin");;
		try {
			String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/users/" + usrName);
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getrequest)
					.build();
			Response responseGet = client.newCall(request).execute();

			if (responseGet.code() == ML_RES_OK) {
				System.out.println("Kerberos User " + usrName + " already exist");
			} else {
				System.out.println("Kerberos User does exist");

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
				String postStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/users?format=json");
				Request requestUsr = new Request.Builder()
						.header("Content-type", "application/json")
						.url(postStr)
						.post(RequestBody.create(mainNode.toString(), MediaType.parse("application/json")))
						.build();
				Response responseUsr = client.newCall(requestUsr).execute();
				if (responseUsr.code() == ML_RES_BADREQT) {
					System.out.println("Kerberos User already exist - Status Code 400");
				} else if (responseUsr.code() == ML_RES_CREATED) {
					System.out.println("Kerberos User " + usrName + " associated with " + externalName + " external name");
				} else {
					System.out.println("No Proper Response - Kerberos User");
					System.out.println(responseUsr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}

	public static void changeProperty(Map<String, String> properties, String endpoint) {
		OkHttpClient client = null;
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
			client = createManageAdminClient("admin", "admin");

			String putStr = new String("http://" + host_name + ":" + admin_port + endpoint);
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(putStr)
					.put(RequestBody.create(xmlBuff.toString(), MediaType.parse("application/json")))
					.build();

			Response response = client.newCall(request).execute();
			if (response.code() == ML_RES_BADREQT) {
				System.out.println("Property change returned - Status Code 400");
				System.out.println(response);
			} else if (response.code() == ML_RES_CHANGED) {
				System.out.println("Property changes successful");
			} else {
				System.out.println("No Proper Response");
				System.out.println(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			client = null;
		}
	}

	public static JsonNode getState(Map<String, String> properties, String endpoint) {
		try {
			OkHttpClient client = createManageAdminClient("admin", "admin");

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
			String getStr = new String("http://" + host_name + ":" + admin_port + endpoint + "?format=json&" + xmlBuff.toString());
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getStr)
					.build();
			Response response = client.newCall(request).execute();
			if(response.code() == ML_RES_OK) {
				String body = response.body().string();
				JsonNode actualObj = new ObjectMapper().readTree(body);
				return actualObj;
			}
			else {
				System.out.println("No proper response from getState");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String[] getHosts() {
		String body = null;
		try {
			OkHttpClient client = createManageAdminClient("admin", "admin");
			String getStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/hosts?format=json");

			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(getStr)
					.build();
			Response response = client.newCall(request).execute();
			if(response.code() != ML_RES_OK) {
				System.out.println("No proper response from getHosts");
				System.out.println(response);
			}
			else if (response.code() == ML_RES_OK) {
				body = response.body().string();
				JsonNode actualObj = new ObjectMapper().readTree(body);
				JsonNode nameNode = actualObj.path("host-default-list").path("list-items");
				List<String> hosts = nameNode.findValuesAsText("nameref");
				String[] s = new String[hosts.size()];
				hosts.toArray(s);
				return s;
			}
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

		  OkHttpClient client = createManageAdminClient("admin", "admin");

	      String putStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName + "/temporal/collections/lsqt/properties?collection=" + collectionName);

		  Request request = new Request.Builder()
				  .header("Content-type", "application/json")
				  .url(putStr)
				  .put(RequestBody.create(rootNode.toString(), MediaType.parse("application/json")))
				  .build();
		  Response response = client.newCall(request).execute();
	      if (response.code() == ML_RES_BADREQT) {
	      	System.out.println("Disable automation for a LSQT enabled DB on a collection - Failed");
	          System.out.println(response);
	      }
	      else if (response.code() == ML_RES_CHANGED) {
	          System.out.println("Disable automation for a LSQT on " + collectionName + " successful");
	      }
	      else {
	          System.out.println("No Proper Response for Disable automation for a LSQT");
	      }
	      client = null;
	  }
	  
	  public static int getDocumentCount(String dbName) throws IOException {
	      String jsonStr = null;
		  OkHttpClient client = null;
	      int nCount = 0;
	      try {
	          client = createManageAdminClient("admin", "admin");
	          String getrequest = new String("http://" + host_name + ":" + admin_port + "/manage/v2/databases/" + dbName
	                  + "?view=counts&format=json");
			  Request request = new Request.Builder()
					  .header("Content-type", "application/json")
					  .url(getrequest)
					  .build();
			  Response response1 = client.newCall(request).execute();
			  if (!response1.isSuccessful()) throw new IOException("Unexpected code " + response1);
			  else {
				  jsonStr = response1.body().string();
				  JsonNode jnode = new ObjectMapper().readTree(jsonStr);

				  if (!jnode.isNull()) {
					  nCount = jnode.path("database-counts").path("count-properties").path("documents").get("value").asInt();
					  System.out.println(jnode);
				  } else {
					  System.out.println("REST call for database properties returned NULL ");
				  }
			  }
	      } catch (Exception e) {
	          e.printStackTrace();
	      } finally {
	          client = null;
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

		  OkHttpClient client = createManageAdminClient("admin", "admin");
		  try {
			  String extSecurityrName = "";
			  String body = "{\"group-name\": \"Default\",\"distribute-timestamps\": \"" + distributeTimestampType + "\"}";

			  String putStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
					  + "/properties?server-type=http");
			  Request request = new Request.Builder()
					  .header("Content-type", "application/json")
					  .url(putStr)
					  .put(RequestBody.create(body, MediaType.parse("application/json")))
					  .build();
			  Response response = client.newCall(request).execute();
			  if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			  else if (response.code() == ML_RES_CHANGED) {
				  System.out.println("Property " + distributeTimestampType + " successfully set");
			  }
		  }
		  catch (Exception ex) {
		  	ex.printStackTrace();
		  } finally {
			  client = null;
		  }
	  }

	public static void associateRESTServerWithModuleDB(String restServerName, String modulesDbName) throws Exception {
		OkHttpClient client = null;
		try {
			client = createManageAdminClient("admin", "admin");
			String body = "{\"modules-database\": \"" + modulesDbName + "\",\"group-name\": \"Default\"}";

			String putStr = new String("http://" + host_name + ":" + admin_port + "/manage/v2/servers/" + restServerName
					+ "/properties?server-type=http");
			Request request = new Request.Builder()
					.header("Content-type", "application/json")
					.url(putStr)
					.put(RequestBody.create(body, MediaType.parse("application/json")))
					.build();
			Response response = client.newCall(request).execute();
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			else if (response.code() == ML_RES_CHANGED) {
				System.out.println(restServerName + " server successfully associated with " + modulesDbName + "database");
			}
			else {
				System.out.println("No proper response in associating RESTServer With ModuleDB");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client = null;
		}
	}
}
