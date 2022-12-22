package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.SessionState;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.impl.BaseProxy;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientApiFunctionalTest extends AbstractFunctionalTest {

	private final static String serverName = "java-functest";

	private static DatabaseClient dbclient = null;
	private static String host;
	private static int port;
	private static int restTestport;


	// Create an identifier for modules document - Client API call will be to endpoint
	private final static String endPointURI_1 = "/ext/TestE2EIntegerParamReturnDouble/TestE2EIntegerParamReturnDouble";
	private final static String endPointURI_2 = "/ext/TestE2EIntegerParamReturnDouble/TestE2EIntegerParamReturnDoubleErrorCond";
	private final static String endPointURI_3 = "/ext/TestE2EIntegerParamReturnDouble/TestRequiredParam";

	private final static String endPointURI_5 = "/ext/TestE2EMultiStringsInStringsOut/TestE2EJsonStringsInStringsOut";
	private final static String endPointURI_6 = "/ext/TestE2EModuleNotFound/TestE2EModuleNotFound";
	private final static String endPointURI_7 = "/ext/TestE2ESession/TestE2ESession";

	// For testing Open API start
	// Atomic params in and params out
	private final static String endPointURI_8 = endPointURI_1;
	private final static String endPointURI_9 = "/ext/TestOpenApi/TestOpenApiParamsInDocOut";
	private final static String endPointURI_10 = "/ext/TestOpenApi/TestOpenApiParamInReturnsNone";
	// Atomic params in and param out with "$javaClass"
	private final static String endPointURI_11 = endPointURI_5;
	// For testing Open API end

	private final static String endPointURI_12 = "/ext/TestE2EModuleXQY/TestE2EModuleXQY";

	/* Note : In case there is a need to re-run the tests, please
	delete these App Servers manually. The db and forest for these are deleted in aftercalss().
	These server are left behind and in case of re-runs DB and forest creations will not happen,
	hence delete app servers and re-run tests.
	- TestClientQAServer
	- TestRESTServerOnAPI
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		createUserRolesWithPrevilages("apiRole", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke", "xdmp:eval", "xdmp:eval-in", "xdmp:invoke", "xdmp:invoke-in");
		createRESTUser("apiUser", "ap1U53r", "apiRole", "rest-admin", "rest-writer", "rest-reader",
				"rest-extension-user", "manage-user");
		createRESTUser("secondApiUser", "ap1U53r", "apiRole", "rest-admin", "rest-writer", "rest-reader",
				"rest-extension-user", "manage-user");
		createUserRolesWithPrevilages("ForbiddenRole", "any-uri");
		createRESTUser("ForbiddenUser", "ap1U53r", "apiRole", "rest-admin", "rest-writer", "rest-reader",
				"manage-user");
		dbclient = client;
		DatabaseClient modulesClient = adminModulesClient;
		host = getServer();
		port = getRestServerPort();
		restTestport = getRestServerPort();

		TextDocumentManager docMgr = modulesClient.newTextDocumentManager();
		File file = new File(
				"src/test/java/com/marklogic/client/functionaltest/data/api/TestE2EIntegerParamReturnDouble.sjs");

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		metadataHandle.getPermissions().add("apiRole", Capability.UPDATE, Capability.READ, Capability.EXECUTE);
		metadataHandle.getPermissions().add("rest-reader", Capability.READ, Capability.EXECUTE);

		// write the document content
		docMgr.write(endPointURI_1+".sjs", metadataHandle, handle);

		file = new File(
				"src/test/java/com/marklogic/client/functionaltest/data/api/TestE2EIntegerParamReturnDouble.api");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_1+".api", metadataHandle, handle);

		file = new File(
				"src/test/java/com/marklogic/client/functionaltest/data/api/TestE2EIntegerParamReturnDoubleErrorCond.sjs");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_2+".sjs", metadataHandle, handle);

		file = new File(
				"src/test/java/com/marklogic/client/functionaltest/data/api/TestE2EIntegerParamReturnDoubleErrorCond.api");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_2+".api", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestRequiredParam.sjs");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_3+".sjs", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestRequiredParam.api");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_3+".api", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestE2EJsonStringsInStringsOut.sjs");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_5+".sjs", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestE2EJsonStringsInStringsOut.api");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_5+".api", metadataHandle, handle);

		// Do not add the SJS module to DB
		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestE2EModuleNotFound.api");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_6+".api", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestE2ESession.sjs");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_7+".sjs", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestE2ESession.api");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_7+".api", metadataHandle, handle);

		// For Open API tests begin. Note - These SJS modules need not or may not produce valid outputs.
		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestOpenApiParamsInDocOut.sjs");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_9+".sjs", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestOpenApiParamsInDocOut.api");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_9+".api", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestOpenApiParamsInReturnsNone.sjs");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_10+".sjs", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestOpenApiParamsInReturnsNone.api");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_10+".api", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestE2EModuleXQY.xqy");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_12+".xqy", metadataHandle, handle);

		file = new File("src/test/java/com/marklogic/client/functionaltest/data/api/TestE2EModuleXQY.api");
		handle = new FileHandle(file);
		docMgr.write(endPointURI_12+".api", metadataHandle, handle);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		System.out.println("In tear down");
	deleteUserRole("apiRole");
	deleteRESTUser("apiUser");
	deleteRESTUser("secondApiUser");
	deleteUserRole("ForbiddenRole");
	deleteRESTUser("ForbiddenUser");

	// release client
	dbclient.release();
	}

	@Test
	public void TestE2EItemPrice() {

		System.out.println("Running TestE2EItemPrice");
		// Invoke the function
		Double responseBack1 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPrice(0);
		System.out.println("Expected 1.0. Response from the Client API call is " + responseBack1);
		assertEquals(1.0, responseBack1, 0.00);

		Double responseBack2 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPrice(-1);
		System.out.println("Expected 0.0.  Response from the Client API call is " + responseBack2);
		assertEquals(0.0, responseBack2, 0.00);

		Double responseBack3 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPrice(1);
		System.out.println("Expected 2.0. Response from the Client API call is " + responseBack3);
		assertEquals(2.0, responseBack3, 0.00);

		Double responseBack4 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPrice(0);
		System.out.println("Expected 1.0.  Response from the Client API call is " + responseBack4);
		assertEquals(1.0, responseBack4, 0.00);

		short srt = 12;
		Double responseBack5 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPrice((int) srt);
		System.out.println("Expected 13.0.  Response from the Client API call is " + responseBack5);
		assertEquals(13.0, responseBack5, 0.00);
		// Integer.MAX_VALUE
		double responseBack6 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPrice(Integer.MAX_VALUE);
		System.out.println("Expected Integer.MAX_VALUE.  Response from the Client API call is " + responseBack6);
		assertTrue("Expected value not returned", String.valueOf(responseBack6).contains("2.147483648E9"));

		// Integer.MIN_VALUE
		double responseBack7 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPrice(Integer.MIN_VALUE);
		System.out.println("Expected Integer.MIN_VALUE.  Response from the Client API call is " + responseBack7);
		assertTrue("Expected value not returned", String.valueOf(responseBack7).contains("-2.147483647E9"));

		// Expecting incorrect data type from module
		double responseBack8 = 0.0;
		try {
			TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPrice(10);
		} catch (Exception ex) {
			System.out.println("Expecting 0. Response from the Client API call is " + responseBack8);
			System.out.println("Exception response from the Client API call is " + ex);
			assertTrue("Exception message incorrect", ex.toString().contains("java.lang.IllegalArgumentException: Could not convert to double: String10"));
			assertEquals(0.0, responseBack8, 0.00);
		}

		// Null input. Expect 55555.00 as the return from API module so that we know
		// passing null works
		Double responseBack9 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPrice(null);
		System.out.println("Expected 55555.00.  Response from the Client API call is " + responseBack9);
		assertEquals(55555.00, responseBack9, 0.00);

		// Verify calls with two parameters - both nulls
		float responseBack11 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPriceErrorCond(null, null);
		System.out.println("Expected 30000.0.  Response from the Client API call is " + responseBack11);
		assertEquals(35000.0, responseBack11, 0.00);

		// Verify calls with two parameters - second null parameter
		float responseBack12 = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPriceErrorCond(10, null);
		System.out.println("Expected 20000.0.  Response from the Client API call is " + responseBack12);
		assertEquals(20000.0, responseBack12, 0.00);
	}

	@Test
	public void TestE2EXqyFunction() {
		System.out.println("Running TestE2EXqyFunction");
		// Invoke the function
		String responseBack1 = TestE2EModuleXQY.on(dbclient).xqyfunction("MAGLITE");
		System.out.println("Response from the Client API call is " + responseBack1);
		assertTrue("Response when valid parameter passed incorrect", responseBack1.contains("QA Module Returns MAGLITE"));
		// Pass null for parameter
		String responseBack2 = TestE2EModuleXQY.on(dbclient).xqyfunction(null);
		System.out.println("Response from the Client API call is " + responseBack2);
		assertTrue("Response when null parameter passed incorrect", responseBack2.contains("QA Module Returns Passed in null parameter."));
	}

		// This test requires TestE2ERequiredParam.api Fn Decl file
	@Test
	public void TestE2ERequiredParam() {

		System.out.println("Running TestE2ERequiredParam");
		// Invoke the function with no Params - API decl states params as required
		String msg;
		try {
			TestE2EIntegerParaReturnDouble.on(dbclient).TestE2ERequiredParam(null, null);
		} catch (Exception ex) {
			msg = ex.toString();
			System.out.println("Exception response from the Client API call is " + ex);
			assertTrue("Expected exception type not returned", msg.contains("RequiredParamException"));
			assertTrue("Expected exception returned", msg.contains("null value for required parameter: items"));
		}
	}

	@Test
	public void TestE2EUnAuthorizedUser() {
		System.out.println("Running TestE2EUnAuthorizedUser");
		DatabaseClient dbForbiddenclient = newClient(host, port, newSecurityContext("ForbiddenUser", "ap1U53r"), getConnType());
		String msg;
		try {
			TestE2EIntegerParaReturnDouble.on(dbForbiddenclient).TestE2EItemPriceErrorCond(10, 50);
		} catch (FailedRequestException ex) {
			msg = ex.getMessage();
			assertTrue("Unexpected exception: " + msg,
				msg.contains("failed to POST at") &&
					msg.contains("/ext/TestE2EIntegerParamReturnDouble/TestE2EIntegerParamReturnDoubleErrorCond.sjs"));
		}
	}

	// No module installed for test.
	@Test
	public void TestE2EModuleNotFound() {
		System.out.println("Running TestE2EModuleNotFound");
		String msg;
		try {
			TestE2EModuleNotFound.on(dbclient).ModuleNotFound("a", "b");
		} catch (Exception ex) {
			msg = ex.toString();
			System.out.println("Exception response from the Client API call is " + ex);
			assertTrue("Expected exception type not returned",
					msg.contains("com.marklogic.client.FailedRequestException"));
			assertTrue("Unexpected message: " + msg,
				msg.contains("failed to POST at ") &&
				msg.contains("/ext/TestE2EModuleNotFound/TestE2EModuleNotFound.sjs"));
		}
	}

	// This test sets the App Server concurrent users to be 1 instead of 0
	// (default). Expect to see ForbiddenUser when same user logins multiple times.
	@Test
	public void TestE2ENumberOfConcurrentUsers() throws InterruptedException {
		StringBuilder msgEx = new StringBuilder();
		Thread w1, w2, w3;

		System.out.println("Running TestE2ENumberOfConcurrentUsers");
		modifyConcurrentUsersOnHttpServer(serverName, 1);

		class MultipleApiUsers implements Runnable {

			final String msg;
			Float f;

			public void run() {
				try {
					f = TestE2EIntegerParaReturnDouble.on(dbclient).TestE2EItemPriceErrorCond(1000, 1000);
				} catch (FailedRequestException ex) {
					System.out.println("Exception is from Thread " + msg);
					ex.printStackTrace();
					msgEx.append(msg);
					msgEx.append("******");
					msgEx.append(ex.getMessage());
				}
			}

			MultipleApiUsers(String in) {
				msg = in;
			}
		}

		// Setup 3 API calls
		try {
			w1 = new Thread(new MultipleApiUsers("m1"));
			w2 = new Thread(new MultipleApiUsers("m2"));
			w3 = new Thread(new MultipleApiUsers("m3"));

			w1.start();
			w2.start();
			w3.start();
			w1.join();
			w2.join();
			w3.join();
		} finally {
			modifyConcurrentUsersOnHttpServer(serverName, 0);
			System.out.println("Exception from API responses of call are " + msgEx);
			assertTrue("Unexpected error message: " + msgEx,
				msgEx.toString().contains("Local message: failed to POST at") &&
				msgEx.toString().contains("/ext/TestE2EIntegerParamReturnDouble/TestE2EIntegerParamReturnDoubleErrorCond.sjs"));
		}
	}

	// Test users with invalid roles
	@Test
	public void TestE2EuserWithInvalidRole() {
		// Used this test to verify ResourceNotFoundException when sjs module is installed with incorrect doc URI

		System.out.println("Running TestE2EuserWithInvalidRole");
		SecurityContext secContext = newSecurityContext("secondApiUser", "ap1U53r");
		DatabaseClient dbSecondClient = newClient(host, port, secContext, getConnType());
		String msg;
		try {
			TestE2EIntegerParaReturnDouble.on(dbSecondClient).TestE2EItemPriceErrorCond(10, 50);
		} catch (Exception ex) {
			msg = ex.getMessage();
			assertTrue("Unexpected exception: " + msg,
				msg.contains("failed to POST at") &&
				msg.contains("ext/TestE2EIntegerParamReturnDouble/TestE2EIntegerParamReturnDoubleErrorCond.sjs"));
		}
	}

	// This test requires TestE2EJsonDocsInStringsOut.api Fn Decl file
	@Test
	public void TestE2EJsonDocsInStringsOut() {

		System.out.println("Running TestE2EJsonDocsInStringsOut");
		String[] filenames = { "constraint1", "constraint2"/*, "constraint3.json", "constraint4.json", "constraint5.json" */};
		// Invoke the function with no Params - API decl states params as required
		// Holder for module function's output
		ArrayNode outputStrSeq;

		String s1 = "Vannevar Bush wrote an article for The Atlantic Monthly";
		String s2 = "Lisa wrote an article for The Strait Times";

		Stream<String> inputFiles = Stream.of(s1,s2);

		Stream<java.lang.String> uris = Stream.of(filenames);
		Stream<java.lang.String> searchItem =  Stream.of("Bush");

		try {
			outputStrSeq = TestE2EMultipleStringsInMultipleStringsout.on(dbclient).stringsInAndStringOutAsArray(inputFiles, uris, searchItem);
			System.out.println("outputStrSeq " + outputStrSeq.toString());
			assertTrue("Correct URI not returned", outputStrSeq.get(0).asText().contains("constraint1.json"));
		} catch (Exception ex) {
			System.out.println("Exception response from the Client API call is " + ex);
		}
	}

	// Tests API calls with session data-type.
	@Test
	public void TestE2ESessions() {
		System.out.println("Running TestE2ESessions");
		SessionState apiSession1 = TestE2ESession.on(dbclient).newSessionState();

		TestE2ESession.on(dbclient).SessionChecks(apiSession1, "/session1.json", "{\"value\":\"Checking first sessions\"}");

		// Try multiple calls sequentially  - different session
		SessionState apiSession2 = TestE2ESession.on(dbclient).newSessionState();
		TestE2ESession.on(dbclient).SessionChecks(apiSession2, "/session2.json", "{\"value\":\"Checking sessions 2\"}");
		SessionState apiSession3 = TestE2ESession.on(dbclient).newSessionState();
		TestE2ESession.on(dbclient).SessionChecks(apiSession3, "/session3.json", "{\"value\":\"Checking sessions 3\"}");

		DatabaseClient dbclientRest = newClient(host, restTestport, newSecurityContext("apiUser", "ap1U53r"), getConnType());
		waitForPropertyPropagate();
		JSONDocumentManager docMgr = dbclientRest.newJSONDocumentManager();
		JacksonHandle jh = new JacksonHandle();
		// Validate the content
		docMgr.read("/session1.json", jh);
		String nodeStr = jh.get().get("value").asText();
		System.out.println("JacksonHandle 1: " + jh);
		assertTrue("Server module returned false. session1.json not inserted? See Logs", nodeStr.contains("Checking first sessions") );

		// Try multiple calls sequentially - same session
		SessionState apiSession4 = TestE2ESession.on(dbclient).newSessionState();
		TestE2ESession.on(dbclient).SessionChecks(apiSession4, "/session4.json",
				"{\"value\":\"Checking sessions 4\"}");
		TestE2ESession.on(dbclient).SessionChecks(apiSession4, "/session5.json",
				"{\"value\":\"Checking sessions 5\"}");

		// Validate the content
		jh = new JacksonHandle();
		docMgr.read("/session5.json", jh);
		nodeStr = jh.get().get("value").asText();
		System.out.println("JacksonHandle 5 " + jh);
		assertTrue("Server module returned false. session5.json not inserted? See Logs",
				nodeStr.contains("Checking sessions 5"));

		// Use null session
		try {
			TestE2ESession.on(dbclient).SessionChecks(null, "/session6.json",
					"{\"value\":\"Checking sessions 6\"}");

		} catch (Exception ex) {
			String msg = ex.toString();
			System.out.println("Exception - session6.json - Client API call is " + msg);
			assertTrue("Exception incorrect - when sessions is null",msg.contains("RequiredParamException"));
			assertTrue("Exception incorrect - when sessions is null",msg.contains("null value for required session parameter: api_session"));
		}

		// Use session with cleared cookies
		SessionState apiSession7 = TestE2ESession.on(dbclient).newSessionState();

		try {
			TestE2ESession.on(dbclient).SessionChecks(apiSession7, "/session7.json",
					"{\"value\":\"Checking sessions 7\"}");

		} catch (Exception ex) {
			String msg = ex.toString();
			System.out.println("Exception - session6.json - Client API call is " + msg);
		}
		jh = new JacksonHandle();
		docMgr.read("/session7.json", jh);
		nodeStr = jh.get().get("value").asText();
		System.out.println("JacksonHandle 7 " + jh);
		assertTrue("Server module returned false. session7.json not inserted? See Logs",
				nodeStr.contains("Checking sessions 7"));
	}

	private String buildUrl(String path) {
		String url = "http://" + host + ":" + restTestport;
		if (StringUtils.hasText(basePath)) {
			url += "/" + basePath;
		}
		return url + path;
	}

	//Test Open API docs for Param In and Param Out
	@Test
	public void TestOpenApiParamInParamOut() throws Exception {
		System.out.println("Running TestOpenApiParamInParamOut");

		OkHttpClient okHttpClient = (OkHttpClient) dbclient.getClientImplementation();

		String url = buildUrl(endPointURI_8 + ".sjs");

		String credential = Credentials.basic(getAdminUser(), getAdminPassword());

		Request.Builder requestBuilder = new Request.Builder()
				.addHeader("Accept", "application/vnd.oai.openapi+json")
				.addHeader("Authorization", credential)
				.url(url);

		Request request = requestBuilder.header("Authorization", credential)
				.build();
		String respStr = makeCall(okHttpClient, request);
		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsNode = mapper.readTree(respStr);
		System.out.println(respStr);

		assertTrue("OpenAPI response in TestOpenApiParamInParamOut incorrect", jsNode.path("openapi").asText().contains("3.0.0"));
		assertTrue("OpenAPI response in TestOpenApiParamInParamOut incorrect", jsNode.path("info").path("title").asText().contains("TestE2EIntegerParamReturnDouble.sjs services"));
	}

	private String makeCall(OkHttpClient client, Request request) throws IOException {
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful())
				throw new RuntimeException("Unexpected code " + response);
			return response.body().string();
		}
	}

	//Test Open API docs for Param In and doc Out
	@Test
	public void TestOpenApiParamInDocOut() throws Exception {
		System.out.println("Running TestOpenApiParamInDocOut");

		OkHttpClient okHttpClient = (OkHttpClient) dbclient.getClientImplementation();

		String url = buildUrl(endPointURI_9 + ".sjs");

		String credential = Credentials.basic(getAdminUser(), getAdminPassword());

		Request.Builder requestBuilder = new Request.Builder()
				.addHeader("Accept", "application/vnd.oai.openapi+json")
				.addHeader("Authorization", credential)
				.url(url);

		Request request = requestBuilder.header("Authorization", credential)
				.build();
		String respStr = makeCall(okHttpClient, request);
		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsNode = mapper.readTree(respStr);
		System.out.println(respStr);

		assertTrue("OpenAPI response in TestOpenApiParamInDocOut incorrect", jsNode.path("openapi").asText().contains("3.0.0"));
		assertTrue("OpenAPI response in TestOpenApiParamInDocOut incorrect", jsNode.path("info").path("title").asText().contains("TestOpenApiParamsInDocOut.sjs services"));
	}

	//Test Open API docs for Param In and returns none from module function
	@Test
	public void TestOpenApiParamInReturnsNone() throws Exception {
		System.out.println("Running TestOpenApiParamInReturnsNone");

		OkHttpClient okHttpClient = (OkHttpClient) dbclient.getClientImplementation();

		String url = buildUrl(endPointURI_10 + ".sjs");

		String credential = Credentials.basic(getAdminUser(), getAdminPassword());

		Request.Builder requestBuilder = new Request.Builder()
				.addHeader("Accept", "application/vnd.oai.openapi+json")
				.addHeader("Authorization", credential)
				.url(url);

		Request request = requestBuilder.header("Authorization", credential)
				.build();
		String respStr = makeCall(okHttpClient, request);
		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsNode = mapper.readTree(respStr);
		System.out.println(respStr);

		assertTrue("OpenAPI response in TestOpenApiParamInReturnsNone incorrect", jsNode.path("openapi").asText().contains("3.0.0"));
		assertTrue("OpenAPI response in TestOpenApiParamInReturnsNone incorrect", jsNode.path("info").path("title").asText().contains("TestOpenApiParamInReturnsNone.sjs services"));
	}

	//Test Open API docs for Param In and returns "$javaClass" from module function
	@Test
	public void TestOpenApiParamInReturnsJavaClass() throws Exception {
		System.out.println("Running TestOpenApiParamInReturnsJavaClass");

		OkHttpClient okHttpClient = (OkHttpClient) dbclient.getClientImplementation();

		String url = buildUrl(endPointURI_11 + ".sjs");

		String credential = Credentials.basic(getAdminUser(), getAdminPassword());

		Request.Builder requestBuilder = new Request.Builder()
				.addHeader("Accept", "application/vnd.oai.openapi+json")
				.addHeader("Authorization", credential)
				.url(url);

		Request request = requestBuilder.header("Authorization", credential)
				.build();
		String respStr = makeCall(okHttpClient, request);
		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsNode = mapper.readTree(respStr);
		System.out.println(respStr);

		assertTrue("OpenAPI response in TestOpenApiParamInReturnsJavaClass incorrect", jsNode.path("openapi").asText().contains("3.0.0"));
		assertTrue("OpenAPI response in TestOpenApiParamInReturnsJavaClass incorrect", jsNode.path("info").path("title").asText().contains("TestE2EJsonStringsInStringsOut.sjs services"));
	}

	//Test Open API docs for directory
	@Test
	public void TestOpenApiDirectory() throws Exception {
		System.out.println("Running TestOpenApiDirectory");

		OkHttpClient okHttpClient = (OkHttpClient) dbclient.getClientImplementation();

		String url = buildUrl("/ext/TestOpenApi/");

		String credential = Credentials.basic(getAdminUser(), getAdminPassword());

		Request.Builder requestBuilder = new Request.Builder()
				.addHeader("Accept", "application/vnd.oai.openapi+json")
				.addHeader("Authorization", credential)
				.url(url);

		Request request = requestBuilder.header("Authorization", credential)
				.build();
		String respStr = makeCall(okHttpClient, request);
		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsNode = mapper.readTree(respStr);
		System.out.println(respStr);

		assertTrue("OpenAPI response in TestOpenApiDirectory incorrect", jsNode.path("openapi").asText().contains("3.0.0"));
		assertTrue("OpenAPI response in TestOpenApiDirectory incorrect", jsNode.path("info").path("title").asText().contains("/ext/TestOpenApi services"));
	}

	public static void modifyConcurrentUsersOnHttpServer(String restServerName, int numberOfUsers) {
		String body = "{\"server-name\":\"" + restServerName + "\", " +
			"\"group-name\": \"Default\", " +
			"\"concurrent-request-limit\":\"" + numberOfUsers + "\"}";
		new ServerManager(newManageClient()).save(body);
	}
}

/**
 * Provides a set of operations on the database server
 */
interface TestE2EIntegerParaReturnDouble {
	/**
	 * Creates a TestE2EIntegerParaReturnDouble object for executing operations on the database server.
	 *
	 * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
	 * client object can be used for any number of requests and in multiple threads.
	 *
	 * @param db	provides a client for communicating with the database server
	 * @return	an object for session state
	 */
	static TestE2EIntegerParaReturnDouble on(DatabaseClient db) {
		final class TestE2EIntegerParaReturnDoubleImpl implements TestE2EIntegerParaReturnDouble {
			private BaseProxy baseProxy;

			private TestE2EIntegerParaReturnDoubleImpl(DatabaseClient dbClient) {
				baseProxy = new BaseProxy(dbClient, "/ext/TestE2EIntegerParamReturnDouble/");
			}

			@Override
			public Double TestE2EItemPrice(Integer itemId) {
				return BaseProxy.DoubleType.toDouble(
						baseProxy
								.request("TestE2EIntegerParamReturnDouble.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC)
								.withSession()
								.withParams(
										BaseProxy.atomicParam("itemId", true, BaseProxy.IntegerType.fromInteger(itemId)))
								.withMethod("POST")
								.responseSingle(true, null)
				);
			}


			@Override
			public java.lang.Float TestE2EItemPriceErrorCond(Integer items, java.lang.Integer price) {
				return BaseProxy.FloatType.toFloat(
						baseProxy
								.request("TestE2EIntegerParamReturnDoubleErrorCond.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS)
								.withSession()
								.withParams(
										BaseProxy.atomicParam("items", true, BaseProxy.IntegerType.fromInteger(items)),
										BaseProxy.atomicParam("price", true, BaseProxy.IntegerType.fromInteger(price)))
								.withMethod("POST")
								.responseSingle(true, null)
				);
			}


			@Override
			public java.lang.Float TestE2ERequiredParam(Integer items, java.lang.Integer price) {
				return BaseProxy.FloatType.toFloat(
						baseProxy
								.request("TestRequiredParam.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS)
								.withSession()
								.withParams(
										BaseProxy.atomicParam("items", false, BaseProxy.IntegerType.fromInteger(items)),
										BaseProxy.atomicParam("price", false, BaseProxy.IntegerType.fromInteger(price)))
								.withMethod("POST")
								.responseSingle(true, null)
				);
			}

		}

		return new TestE2EIntegerParaReturnDoubleImpl(db);
	}

	/**
	 * Invokes the TestE2EItemPrice operation on the database server
	 *
	 * @param itemId	provides input
	 * @return	as output
	 */
	Double TestE2EItemPrice(Integer itemId);

	/**
	 * Invokes the TestE2EItemPriceErrorCond operation on the database server
	 *
	 * @param items	provides input
	 * @param price	provides input
	 * @return	as output
	 */
	java.lang.Float TestE2EItemPriceErrorCond(Integer items, java.lang.Integer price);

	/**
	 * Invokes the TestE2ERequiredParam operation on the database server
	 *
	 * @param items	provides input
	 * @param price	provides input
	 * @return	as output
	 */
	java.lang.Float TestE2ERequiredParam(Integer items, java.lang.Integer price);

}

/**
 *  This class verifies the stream of Json documents written into DB and accepts a string query. REturns URIs
 */
interface TestE2EMultipleStringsInMultipleStringsout {
	/**
	 * Creates a TestE2EMultipleStringsInMultipleStringsout object for executing operations on the database server.
	 *
	 * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
	 * client object can be used for any number of requests and in multiple threads.
	 *
	 * @param db	provides a client for communicating with the database server
	 * @return	an object for session state
	 */
	static TestE2EMultipleStringsInMultipleStringsout on(DatabaseClient db) {
		final class TestE2EMultipleStringsInMultipleStringsoutImpl implements TestE2EMultipleStringsInMultipleStringsout {
			private BaseProxy baseProxy;

			private TestE2EMultipleStringsInMultipleStringsoutImpl(DatabaseClient dbClient) {
				baseProxy = new BaseProxy(dbClient, "/ext/TestE2EMultiStringsInStringsOut/");
			}

			@Override
			public com.fasterxml.jackson.databind.node.ArrayNode stringsInAndStringOutAsArray(Stream<String> inputFiles, Stream<String> uris, Stream<String> searchItem) {
				return BaseProxy.ArrayType.toArrayNode(
						baseProxy
								.request("TestE2EJsonStringsInStringsOut.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS)
								.withSession()
								.withParams(
										BaseProxy.atomicParam("inputFiles", false, BaseProxy.StringType.fromString(inputFiles)),
										BaseProxy.atomicParam("uris", false, BaseProxy.StringType.fromString(uris)),
										BaseProxy.atomicParam("searchItem", false, BaseProxy.StringType.fromString(searchItem)))
								.withMethod("POST")
								.responseSingle(false, Format.JSON)
				);
			}

		}

		return new TestE2EMultipleStringsInMultipleStringsoutImpl(db);
	}

	/**
	 * Invokes the stringsInAndStringOutAsArray operation on the database server
	 *
	 * @param inputFiles	A sequence of strings that need to be written to Database
	 * @param uris	A sequence of document uris for docs written to Database
	 * @param searchItem	Search string with 1 word or multiples
	 * @return	Module to return array of doc Ids
	 */
	com.fasterxml.jackson.databind.node.ArrayNode stringsInAndStringOutAsArray(Stream<String> inputFiles, Stream<String> uris, Stream<String> searchItem);

}

/**
 * This class verifies the error message when module is not available
 */
interface TestE2EModuleNotFound {
	/**
	 * Creates a TestE2EModuleNotFound object for executing operations on the database server.
	 *
	 * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
	 * client object can be used for any number of requests and in multiple threads.
	 *
	 * @param db	provides a client for communicating with the database server
	 * @return	an object for session state
	 */
	static TestE2EModuleNotFound on(DatabaseClient db) {
		final class TestE2EModuleNotFoundImpl implements TestE2EModuleNotFound {
			private BaseProxy baseProxy;

			private TestE2EModuleNotFoundImpl(DatabaseClient dbClient) {
				baseProxy = new BaseProxy(dbClient, "/ext/TestE2EModuleNotFound/");
			}

			@Override
			public java.lang.String ModuleNotFound(String count, String starttime) {
				return BaseProxy.TextDocumentType.toString(
						baseProxy
								.request("TestE2EModuleNotFound.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS)
								.withSession()
								.withParams(
										BaseProxy.atomicParam("count", false, BaseProxy.DecimalType.fromString(count)),
										BaseProxy.atomicParam("starttime", false, BaseProxy.TimeType.fromString(starttime)))
								.withMethod("POST")
								.responseSingle(false, Format.TEXT)
				);
			}

		}

		return new TestE2EModuleNotFoundImpl(db);
	}

	/**
	 * Invokes the ModuleNotFound operation on the database server
	 *
	 * @param count	Big Decimal numbers
	 * @param starttime	Java Local time
	 * @return	Module to return text document
	 */
	java.lang.String ModuleNotFound(String count, String starttime);

}

/**
 * This class verifies the sessions
 */
interface TestE2ESession {
	/**
	 * Creates a TestE2ESession object for executing operations on the database server.
	 *
	 * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
	 * client object can be used for any number of requests and in multiple threads.
	 *
	 * @param db	provides a client for communicating with the database server
	 * @return	an object for session state
	 */
	static TestE2ESession on(DatabaseClient db) {
		final class TestE2ESessionImpl implements TestE2ESession {
			private BaseProxy baseProxy;

			private TestE2ESessionImpl(DatabaseClient dbClient) {
				baseProxy = new BaseProxy(dbClient, "/ext/TestE2ESession/");
			}
			@Override
			public SessionState newSessionState() {
				return baseProxy.newSessionState();
			}

			@Override
			public void SessionChecks(SessionState api_session, String uri, String content) {
				baseProxy
						.request("TestE2ESession.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS)
						.withSession("api_session", api_session, false)
						.withParams(
								BaseProxy.atomicParam("uri", false, BaseProxy.StringType.fromString(uri)),
								BaseProxy.atomicParam("content", false, BaseProxy.StringType.fromString(content)))
						.withMethod("POST")
						.responseNone();
			}

		}

		return new TestE2ESessionImpl(db);
	}
	/**
	 * Creates an object to track a session for a set of operations
	 * that require session state on the database server.
	 *
	 * @return	an object for session state
	 */
	SessionState newSessionState();

	/**
	 * Invokes the SessionChecks operation on the database server
	 *
	 * @param api_session	Holds the session object
	 * @param uri	Doc Id of the inserted document
	 * @param content	Doc contents of the inserted document
	 *
	 */
	void SessionChecks(SessionState api_session, String uri, String content);

}

interface TestE2EModuleXQY {
	/**
	 * Creates a TestE2EModuleXQY object for executing operations on the database server.
	 *
	 * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
	 * client object can be used for any number of requests and in multiple threads.
	 *
	 * @param db	provides a client for communicating with the database server
	 * @return	an object for executing database operations
	 */
	static TestE2EModuleXQY on(DatabaseClient db) {
		return on(db, null);
	}
	/**
	 * Creates a TestE2EModuleXQY object for executing operations on the database server.
	 *
	 * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
	 * client object can be used for any number of requests and in multiple threads.
	 *
	 * The service declaration uses a custom implementation of the same service instead
	 * of the default implementation of the service by specifying an endpoint directory
	 * in the modules database with the implementation. A service.json file with the
	 * declaration can be read with FileHandle or a string serialization of the JSON
	 * declaration with StringHandle.
	 *
	 * @param db	provides a client for communicating with the database server
	 * @param serviceDeclaration	substitutes a custom implementation of the service
	 * @return	an object for executing database operations
	 */
	static TestE2EModuleXQY on(DatabaseClient db, JSONWriteHandle serviceDeclaration) {
		final class TestE2EModuleXQYImpl implements TestE2EModuleXQY {
			private DatabaseClient dbClient;
			private BaseProxy baseProxy;

			private BaseProxy.DBFunctionRequest req_xqyfunction;

			private TestE2EModuleXQYImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
				this.dbClient  = dbClient;
				this.baseProxy = new BaseProxy("/ext/TestE2EModuleXQY/", servDecl);

				this.req_xqyfunction = this.baseProxy.request(
						"TestE2EModuleXQY.xqy", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC);
			}

			@Override
			public String xqyfunction(String items) {
				return xqyfunction(
						this.req_xqyfunction.on(this.dbClient), items
				);
			}
			private String xqyfunction(BaseProxy.DBFunctionRequest request, String items) {
				return BaseProxy.StringType.toString(
						request
								.withParams(
										BaseProxy.atomicParam("items", true, BaseProxy.StringType.fromString(items))
								).responseSingle(false, null)
				);
			}
		}

		return new TestE2EModuleXQYImpl(db, serviceDeclaration);
	}

	/**
	 * Invokes the xqyfunction operation on the database server
	 *
	 * @param items	provides input
	 * @return	as output
	 */
	String xqyfunction(String items);

}
