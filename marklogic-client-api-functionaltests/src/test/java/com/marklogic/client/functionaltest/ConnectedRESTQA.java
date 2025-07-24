/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClient.ConnectionType;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.impl.SSLUtil;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.QueryManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.restapis.RestApiManager;
import com.marklogic.mgmt.resource.security.ExternalSecurityManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.resource.security.UserManager;
import com.marklogic.mgmt.resource.temporal.TemporalAxesManager;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionLSQTManager;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionManager;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class ConnectedRESTQA {

	private static Properties testProperties = null;

	private static String authType;
	protected static String restServerName = null;
	private static String restSslServerName = null;
	private static String ssl_enabled = null;
	private static String https_port = null;
	protected static String http_port = null;
	protected static String fast_http_port = null;
	protected static String basePath = null;
	// This needs to be a FQDN when SSL is enabled. Else localhost
	private static String host_name = null;
	// This needs to be a FQDN when SSL is enabled. Else localhost
	private static String ssl_host_name = null;
	private static String admin_user = null;
	private static String admin_password = null;
	private static String ml_certificate_password = null;
	private static String ml_certificate_file = null;
	private static String mlDataConfigDirPath = null;
	private static Boolean isLBHost = false;

	private static int PROPERTY_WAIT = 0;
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static void createDB(String dbName) {
		new DatabaseManager(newManageClient())
			.save(objectMapper.createObjectNode()
				.put("database-name", dbName)
				.toString());
	}

	public static void createForest(String fName, String dbName) {
		new ForestManager(newManageClient()).save(objectMapper.createObjectNode()
			.put("database", dbName)
			.put("forest-name", fName)
			.toString());
	}

	public static void createForestonHost(String fName, String dbName, String hName) {
		new ForestManager(newManageClient()).save(objectMapper.createObjectNode()
			.put("database", dbName)
			.put("forest-name", fName)
			.put("host", hName)
			.toString());
	}

	public static void postRequest(Map<String, String> params, String endpoint) {
		List<String> paramList = new ArrayList<>();
		params.entrySet().forEach(entry -> {
			paramList.add(entry.getKey());
			paramList.add(entry.getValue());
		});
		newManageClient().postForm(endpoint, paramList.toArray(new String[0]));
	}

	public static void assocRESTServer(String restServerName, String dbName, int restPort) {
		ManageClient client = newManageClient();
		if (new ServerManager(client).exists(restServerName)) {
			associateRESTServerWithDB(restServerName, dbName);
		} else {
			ObjectNode request = objectMapper.createObjectNode();
			request.putObject("rest-api")
				.put("name", restServerName)
				.put("database", dbName)
				.put("port", restPort);

			new RestApiManager(client).createRestApi(restServerName, request.toString());
			if (IsSecurityEnabled()) {
				enableSecurityOnRESTServer(restServerName);
			}
		}
	}

	public static void enableSecurityOnRESTServer(String restServerName) {
		ObjectNode request = objectMapper.createObjectNode()
			.put("group-name", "Default")
			.put("server-name", restServerName)
			.put("internal-security", true)
			.put("ssl-certificate-template", "ssl1-QAXdbcServer")
			.put("ssl-require-client-certificate", true);
		new ServerManager(newManageClient()).save(request.toString());
	}

	public static void associateRESTServerWithDB(String restServerName, String dbName) {
		new ServerManager(newManageClient()).save(objectMapper.createObjectNode()
			.put("content-database", dbName)
			.put("server-name", restServerName)
			.put("group-name", "Default")
			.toString());
	}

	public static void createRESTServerWithDB(String restServerName, int restPort) {
		ManageClient client = newManageClient();
		if (!new ServerManager(client).exists(restServerName)) {
			ObjectNode request = objectMapper.createObjectNode();
			request.putObject("rest-api")
				.put("name", restServerName)
				.put("port", restPort);
			new RestApiManager(client).createRestApi(restServerName, request.toString());
		}
	}

	public static void setupJavaRESTServer(String dbName, String fName, String restServerName, int restPort) {
		createDB(dbName);
		createForest(fName, dbName);
		assocRESTServer(restServerName, dbName, restPort);
		createRESTUser("rest-admin", "x", "rest-admin");
		createRESTUser("rest-writer", "x", "rest-writer");
		createRESTUser("rest-reader", "x", "rest-reader");
	}

	public static void setupJavaRESTServer(String dbName, String fName, String restServerName, int restPort, boolean attachRestContextDB) {
		createDB(dbName);
		createForest(fName, dbName);
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
		ObjectNode mainNode = objectMapper.createObjectNode();
		String[] roleNames = { "rest-reader", "rest-writer" };

		ArrayNode roleArray = objectMapper.createArrayNode();
		ArrayNode privArray = objectMapper.createArrayNode();
		ArrayNode permArray = objectMapper.createArrayNode();
		mainNode.put("role-name", roleName);
		mainNode.put("description", "role discription");

		for (String rolename : roleNames)
			roleArray.add(rolename);
		mainNode.withArray("role").addAll(roleArray);
		for (String privName : privNames) {
			ObjectNode privNode = objectMapper.createObjectNode();
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

		new RoleManager(newManageClient()).save(mainNode.toString());
	}

	public static void createRoleWithNodeUpdate(String roleName, String... privNames) {
		String[] roleNames = { "rest-reader", "rest-writer" };

		ObjectMapper mapper = objectMapper;
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

		new RoleManager(newManageClient()).save(mainNode.toString());
	}

	public static void createRESTUser(String username, String password, String... roleNames) {
		ObjectNode request = objectMapper.createObjectNode()
			.put("user-name", username)
			.put("password", password);
		ArrayNode roles = request.putArray("role");
		for (String roleName : roleNames) {
			roles.add(roleName);
		}
		new UserManager(newManageClient()).save(request.toString());
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
													 ObjectNode collections, String... roleNames) {
		ObjectNode mainNode = objectMapper.createObjectNode();
		ArrayNode childArray = objectMapper.createArrayNode();
		mainNode.put("user-name", usrName);
		mainNode.put("description", "user discription");
		mainNode.put("password", pass);
		for (String rolename : roleNames)
			childArray.add(rolename);
		mainNode.withArray("role").addAll(childArray);
		mainNode.setAll(perm);
		mainNode.setAll(collections);

		new UserManager(newManageClient()).save(mainNode.toString());
	}

	public static void deleteRESTUser(String username) {
		new UserManager(newManageClient()).deleteByIdField(username);
	}

	public static void deleteUserRole(String roleName) {
		new RoleManager(newManageClient()).deleteByIdField(roleName);
	}

	public static void detachForest(String dbName, String fName) {
		newManageClient().postForm("/manage/v2/forests/" + fName,
			"state", "detach",
			"database", dbName
		);
	}

	public static void deleteForest(String fName) {
		new ForestManager(newManageClient()).deleteByIdField(fName);
	}

	public static void deleteDB(String dbName) {
		new DatabaseManager(newManageClient()).deleteByIdField(dbName);
	}

	public static void clearDB(int port) {
		try (DatabaseClient client = newDatabaseClientBuilder().withPort(port).build()) {
			QueryManager mgr = client.newQueryManager();
			mgr.delete(mgr.newDeleteDefinition());
			// Clearing the database occasionally causes a forest to not be available for a moment or two when the tests
			// are running on Jenkins. This leads to intermittent failures. Waiting is not guaranteed to avoid the
			// error but simply hopes to minimize the chance of an intermittent failure.
			waitFor(2000);
		}
	}

	public static void tearDownJavaRESTServer(String dbName, String restServerName) {
		associateRESTServerWithDB(restServerName, "Documents");
		deleteDB(dbName);
	}

	public static void setMergeTimestamp(String dbName, String value) {
		setDatabaseProperties(dbName, "merge-timestamp", value);
	}

	public static void setDatabaseProperties(String dbName, String prop, String propValue) {
		ObjectNode properties = new ObjectMapper().createObjectNode();
		properties.put("database-name", dbName);
		properties.put(prop, propValue);
		new DatabaseManager(newManageClient()).save(properties.toString());
	}

	public static void setDatabaseProperties(String dbName, String prop, boolean propValue) {
		ObjectNode properties = new ObjectMapper().createObjectNode();
		properties.put("database-name", dbName);
		properties.put(prop, propValue);
		new DatabaseManager(newManageClient()).save(properties.toString());
	}

	/*
	 * This Method takes the root property name and object node under it if root
	 * propname exist and equals to null then it just add the object node under
	 * root property name else if it has an existing sub property name then it
	 * adds elements to that array
	 */
	private static void setDatabaseProperties(String dbName, String propName, ObjectNode objNode) {
		ManageClient client = newManageClient();
		String databaseProperties = new DatabaseManager(client).getPropertiesAsJson(dbName);
		JsonNode jnode = null;
		try {
			jnode = new ObjectMapper().readTree(databaseProperties);
		} catch (JsonProcessingException e) {
			fail("Could not parse database-properties: " + databaseProperties + "; cause: " + e.getMessage());
		}

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

		new DatabaseManager(client).save(jnode.toString());
	}

	public static void enableCollectionLexicon(String dbName) {
		setDatabaseProperties(dbName, "collection-lexicon", true);
	}

	public static void enableWordLexicon(String dbName) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		childArray.add("http://marklogic.com/collation/");
		childNode.putArray("word-lexicon").addAll(childArray);
		setDatabaseProperties(dbName, "word-lexicons", childNode);
	}

	public static void enableTrailingWildcardSearches(String dbName) {
		setDatabaseProperties(dbName, "trailing-wildcard-searches", true);
	}

	public static void setMaintainLastModified(String dbName, boolean opt) {
		setDatabaseProperties(dbName, "maintain-last-modified", opt);
	}

	/*
	 * This function constructs a range element index with default
	 * collation,range-value-positions and invalid values
	 */
	public static void addRangeElementIndex(String dbName, String type, String namespace, String localname) {
		addRangeElementIndex(dbName, type, namespace, localname, false);
	}

	public static void addRangeElementIndex(String dbName, String type, String namespace, String localname, boolean positions) {
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

	public static void addRangeElementIndex(String dbName, String[][] rangeElements) {
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

	public static void addRangePathIndex(String dbName, String[][] rangePaths) {
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

	public static void setupAppServicesConstraint(String dbName) {
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

		new TemporalAxesManager(newManageClient(), dbName).save(rootNode.toString());
	}

	public static void addElementRangeIndexTemporalCollection(String dbName, String collectionName,
			String systemAxisName, String validAxisName) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("collection-name", collectionName);
		rootNode.put("system-axis", systemAxisName);
		rootNode.put("valid-axis", validAxisName);

		new TemporalCollectionManager(newManageClient(), dbName).save(rootNode.toString());
	}

	public static void updateTemporalCollectionForLSQT(String dbName, String collectionName, boolean enable) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("lsqt-enabled", enable);

		// Set system time values
		ObjectNode automation = mapper.createObjectNode();
		automation.put("enabled", true);
		rootNode.set("automation", automation);

		new TemporalCollectionLSQTManager(newManageClient(), dbName, collectionName).save(rootNode.toString());
	}

	public static ObjectNode newServerPayload(String serverName) {
		ObjectNode payload = new ObjectMapper().createObjectNode();
		payload.put("server-name", serverName);
		payload.put("group-name", "Default");
		return payload;
	}

	public static void setAuthentication(String authentication, String restServerName) {
		new ServerManager(newManageClient()).save(
			newServerPayload(restServerName).put("authentication", authentication).toString()
		);
	}

	public static void setAuthenticationAndDefaultUser(String restServerName, String authentication, String defaultUser) {
		new ServerManager(newManageClient()).save(
			newServerPayload(restServerName)
				.put("authentication", authentication)
				.put("default-user", defaultUser)
				.toString()
		);
	}

	public static String getServerAuthentication(String serverName) {
		String json = new ServerManager(newManageClient()).getPropertiesAsJson(serverName);
		try {
			return new ObjectMapper().readTree(json).get("authentication").asText();
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setupServerRequestLogging(DatabaseClient client, boolean flag) {
		ServerConfigurationManager scm = client.newServerConfigManager();
		scm.readConfiguration();
		scm.setServerRequestLogging(flag);
		scm.writeConfiguration();
	}

	public static void setPathRangeIndexInDatabase(String dbName, JsonNode jnode) {
		ObjectNode json = (ObjectNode) jnode;
		json.put("database-name", dbName);
		new DatabaseManager(newManageClient()).save(json.toString());
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
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
				// nothing to do
			}

			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
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
			keyStore.load(keyInput, ml_certificate_password.toCharArray());
		} finally {
			if (keyInput != null)
				keyInput.close();
		}
		keyManagerFactory.init(keyStore, ml_certificate_password.toCharArray());
		KeyManager[] keyMgr = keyManagerFactory.getKeyManagers();

		// create an SSL context
		SSLContext mlsslContext = SSLContext.getInstance(SSLUtil.DEFAULT_PROTOCOL);
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
		if (isLBHost()) {
			ObjectNode props = new ObjectMapper().createObjectNode();
			props.put("server-name", restServerName);
			props.put("group-name", "Default");
			props.put("distribute-timestamps", "cluster");
			new ServerManager(newManageClient()).save(props.toString());
		}
	}

	// Removes the database and forest from a REST server.
	public static void cleanupRESTServer(String dbName) {
		if (IsSecurityEnabled())
			tearDownJavaRESTServer(dbName, restSslServerName);
		else
			tearDownJavaRESTServer(dbName, restServerName);
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

	public static DatabaseClientBuilder newDatabaseClientBuilder() {
		Map<String, Object> props = new HashMap<>();
		testProperties.entrySet().forEach(entry -> props.put((String) entry.getKey(), entry.getValue()));
		DatabaseClientBuilder builder = new DatabaseClientBuilder(props);
		// Have to override the port so that the "slow" functional tests can still hit 8011
		return builder.withPort(getRestServerPort());
	}

	public static DatabaseClient newClientAsUser(String username, String password) {
		return newDatabaseClientBuilder()
			.withUsername(username)
			.withPassword(password)
			.build();
	}

	public static DatabaseClient newAdminModulesClient() {
		return newDatabaseClientBuilder()
			.withUsername(getAdminUser())
			.withPassword(getAdminPassword())
			.withDatabase("java-unittest-modules")
			.build();
	}

	public static DatabaseClient getDatabaseClient(String user, String password, ConnectionType connType) {
		return newDatabaseClientBuilder()
			.withUsername(user)
			.withPassword(password)
			.withConnectionType(connType)
			.build();
	}

	/**
	 * Only use this in "slow" functional tests until they're converted over to fast.
	 */
	@Deprecated
	public static DatabaseClient getDatabaseClientOnDatabase(String hostName, int port, String databaseName,
			String user, String password, ConnectionType connType) {
		return newDatabaseClientBuilder()
			.withHost(hostName)
			.withPort(port)
			.withUsername(user)
			.withPassword(password)
			.withDatabase(databaseName)
			.withConnectionType(connType)
			.build();
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

	private static void overrideTestPropertiesWithSystemProperties(Properties testProperties) {
		if ("true".equals(System.getProperty("TEST_USE_REVERSE_PROXY_SERVER"))) {
			System.out.println("TEST_USE_REVERSE_PROXY_SERVER is true, so overriding properties to use reverse proxy server");
			testProperties.setProperty("httpPort", "8020");
			testProperties.setProperty("marklogic.client.port", "8020");
			testProperties.setProperty("marklogic.client.basePath", "testFunctional");
			testProperties.setProperty("marklogic.client.authType", "basic");
		}
	}

	/**
	 * This must be invoked before any test class extending this tries to connect to MarkLogic.
	 */
	public static void loadGradleProperties() {
		Properties properties = new Properties();

		try (InputStream input = ConnectedRESTQA.class.getResourceAsStream("/test.properties")) {
			properties.load(input);
		} catch (IOException ex) {
			throw new RuntimeException("Unable to load properties from test.properties", ex);
		}

		overrideTestPropertiesWithSystemProperties(properties);

		authType = properties.getProperty("marklogic.client.authType");
		restServerName = properties.getProperty("mlAppServerName");
		restSslServerName = properties.getProperty("mlAppServerSSLName");

		https_port = properties.getProperty("httpsPort");
		http_port = properties.getProperty("httpPort");
		fast_http_port = properties.getProperty("marklogic.client.port");
		basePath = properties.getProperty("marklogic.client.basePath");

		// Machine names where ML Server runs
		host_name = properties.getProperty("marklogic.client.host");
		ssl_host_name = properties.getProperty("restSSLHost");

		// Users
		admin_user = properties.getProperty("mlAdminUser");
		admin_password = properties.getProperty("mlAdminPassword");

		// Security and Certificate properties.
		ssl_enabled = properties.getProperty("restSSLset");
		ml_certificate_password = properties.getProperty("ml_certificate_password");
		ml_certificate_file = properties.getProperty("ml_certificate_file");
		mlDataConfigDirPath = properties.getProperty("mlDataConfigDirPath");
		isLBHost = "gateway".equalsIgnoreCase(properties.getProperty("marklogic.client.connectionType"));
		PROPERTY_WAIT = Integer.parseInt(isLBHost ? "15000" : "0");

		testProperties = properties;

		System.out.println("For 'slow' tests, will connect to: " + host_name + ":" + http_port + "; basePath: " +  basePath +
			"; auth: " + authType);
		System.out.println("For 'fast' tests, will connect to: " + host_name + ":" + fast_http_port + "; basePath: " +  basePath +
			"; auth: " + authType);
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

	private static int getHttpsPort() {
		return (Integer.parseInt(https_port));
	}

	private static int getHttpPort() {
		return (Integer.parseInt(http_port));
	}

	/*
	 * This needs to be a FQDN when SSL is enabled. Else localhost.
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
	 * This needs to be a FQDN when SSL is enabled. Else localhost.
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

	public static void associateRESTServerWithKerberosExtSecurity(String restServerName, String extSecurityrName) {
		ObjectNode request = objectMapper.createObjectNode()
			.put("group-name", "Default")
			.put("server-name", restServerName)
			.put("authentication", "kerberos-ticket")
			.put("internal-security", false)
			.put("external-security", extSecurityrName);
		new ServerManager(newManageClient()).save(request.toString());
	}

	/*
	 * Associate REST server with Digest Auth Property changes needed for
	 * are:
	 * authentication set to "Digest" internal security set to "true"
	 */
	public static void associateRESTServerWithDigestAuth(String restServerName) {
		ObjectNode request = objectMapper.createObjectNode()
			.put("group-name", "Default")
			.put("server-name", restServerName)
			.put("authentication", "digest")
			.put("internal-security", true)
			.put("external-security", "");
		new ServerManager(newManageClient()).save(request.toString());
	}

	public static void createExternalSecurityForKerberos(String extSecurityName) {
		ObjectNode request = objectMapper.createObjectNode()
			.put("authentication", "kerberos")
			.put("extenal-security-name", extSecurityName)
			.put("description", "External Kerberos Security")
			.put("cache-timeout", 300)
			.put("authorization", "internal")
			.put("ldap-server-uri", "")
			.put("ldap-base", "")
			.put("ldap-attribute", "")
			.put("ldap-default-user", "")
			.put("ldap-password", "")
			.put("ldap-bind-method", "MD5")
			.put("ssl-require-client-certificate", true);
		new ExternalSecurityManager(newManageClient()).save(request.toString());
	}

	public static void createRESTKerberosUser(String username, String password, String externalName, String... roleNames) {
		ObjectNode request = objectMapper.createObjectNode()
			.put("user-name", username)
			.put("password", password);
		ArrayNode roles = request.putArray("role");
		for (String roleName : roleNames) {
			roles.add(roleName);
		}
		ArrayNode externalNames = request.putArray("external-names");
		externalNames.addObject().put("external-name", externalName);
		new UserManager(newManageClient()).save(request.toString());
	}

	public static void changeProperty(Map<String, String> properties, String endpoint) {
		StringBuffer json = new StringBuffer();
		json.append("{");
		Iterator it = properties.entrySet().iterator();
		int size = properties.size();
		int j = 0;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			json.append("\"").append(pair.getKey()).append("\":");
			if (j == (size - 1))
				json.append("\"").append(pair.getValue()).append("\"");
			else
				json.append("\"").append(pair.getValue()).append("\",");
			j++;
		}
		json.append('}');

		newManageClient().putJson(endpoint, json.toString());
	}

	public static JsonNode getState(Map<String, String> properties, String endpoint) throws IOException {
		StringBuilder querystring = new StringBuilder();
		Iterator it = properties.entrySet().iterator();
		int size = properties.size();
		int j = 0;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			querystring.append(pair.getKey());
			if (j == (size - 1)) {
				querystring.append('=').append(pair.getValue());
			} else {
				querystring.append('=').append(pair.getValue()).append('&');
			}
			j++;
		}

		String json = newManageClient().getJson(endpoint + "?format=json&" + querystring.toString());
		return objectMapper.readTree(json);
	}

	public static String[] getHosts() throws IOException{
		String json = newManageClient().getJson("/manage/v2/hosts?format=json");
		JsonNode actualObj = new ObjectMapper().readTree(json);
		JsonNode nameNode = actualObj.path("host-default-list").path("list-items");
		List<String> hosts = nameNode.findValuesAsText("nameref");
		String[] s = new String[hosts.size()];
		hosts.toArray(s);
		return s;
	}

	  public static void disableAutomationOnTemporalCollection(String dbName, String collectionName, boolean enable) {
	      ObjectMapper mapper = new ObjectMapper();
	      ObjectNode rootNode = mapper.createObjectNode();
	      rootNode.put("lsqt-enabled", enable);

	      ObjectNode automation = mapper.createObjectNode();
	      automation.put("enabled", false);

	      rootNode.set("automation", automation);

		  new TemporalCollectionLSQTManager(newManageClient(), dbName, collectionName).save(rootNode.toString());
	  }

	public static int getDocumentCount(String dbName) throws IOException {
		String jsonStr = newManageClient().getJson("/manage/v2/databases/" + dbName + "?view=counts&format=json");
		JsonNode jnode = new ObjectMapper().readTree(jsonStr);
		int nCount = jnode.path("database-counts").path("count-properties").path("documents").get("value").asInt();
		System.out.println(jnode);
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

	  public static void associateRESTServerWithModuleDB(String restServerName, String modulesDbName) {
		  ObjectNode props = new ObjectMapper().createObjectNode();
		  props.put("server-name", restServerName);
		  props.put("group-name", "Default");
		  props.put("modules-database", modulesDbName);
		  new ServerManager(newManageClient()).save(props.toString());
	}

	public static DatabaseClientFactory.SecurityContext newSecurityContext(String username, String password) {
		if ("basic".equalsIgnoreCase(authType)) {
			return new DatabaseClientFactory.BasicAuthContext(username, password);
		}
		return new DatabaseClientFactory.DigestAuthContext(username, password);
	}

	protected static ManageClient newManageClient() {
		return new ManageClient(new ManageConfig(getServer(), 8002, getAdminUser(), getAdminPassword()));
	}
}
