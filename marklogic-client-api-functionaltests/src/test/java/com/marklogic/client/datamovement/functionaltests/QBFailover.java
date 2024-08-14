/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.functionaltests;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.datamovement.ApplyTransformListener.ApplyResult;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.*;
import com.marklogic.client.query.StructuredQueryBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.jupiter.api.*;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QBFailover extends BasicJavaClientREST {
	private static String dbName = "QBFailover";
	private static DataMovementManager dmManager = null;
	private static DataMovementManager tempMgr = null;
	private static DatabaseClient dbClient;
	private static DatabaseClient evalClient;
	private static String host = null;
	private static String user = null;
	private static Integer port = null;
	private static String password = null;
	private static String server = null;
	private static final String OS = System.getProperty("os.name").toLowerCase();
	private static StringHandle stringHandle;
	private static DocumentMetadataHandle meta2;
	private static String stringTriple;
	private static final String query1 = "fn:count(fn:doc())";
	private static String[] hostNames;
	private static JobTicket ticket;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		loadGradleProperties();
		host = getRestAppServerHostName();
		password = getAdminPassword();
		user = getAdminUser();
		server = getRestAppServerName();
		port = getRestAppServerPort();

		// Create App Server if needed.
		createRESTServerWithDB(server, port);
		hostNames = getHosts();
		// Perform the setup on multiple nodes only.
		if (hostNames.length > 1) {
			// Add all possible hostnames and pick a random one to create a client
			List<String> hostLists = new ArrayList<String>();
			String localhost = InetAddress.getLocalHost().getHostName().toLowerCase();
			Pattern pattern = Pattern.compile(localhost + "(.*)");
			Matcher matcher = pattern.matcher(hostNames[0]);
			String domain = null;
			if (matcher.find()) {
				domain = matcher.group(1);
			}
			for (String host : hostNames) {
				hostLists.add(host);
				pattern = Pattern.compile("(.*)" + domain);
				matcher = pattern.matcher(host);
				if (matcher.find()) {
					hostLists.add(matcher.group(1));
				}
			}
			hostLists.add("localhost");
			int index = new Random().nextInt(hostLists.size());
			dbClient = getDatabaseClientOnDatabase(hostLists.get(index), port, dbName, user, password, getConnType());
			evalClient = getDatabaseClientOnDatabase(host, port, dbName, user, password, getConnType());
			dmManager = dbClient.newDataMovementManager();
			tempMgr = evalClient.newDataMovementManager();
			Map<String, String> props = new HashMap<>();
			createDB(dbName);
			for (int i = 0; i < hostNames.length; i++) {
				if (i == 0) {
					createForest(dbName + "-" + (i + 1), hostNames[i], null);
				} else {
					createForest(dbName + "-" + (i + 1) + "-replica", hostNames[0], null);
					createForest(dbName + "-" + (i + 1), hostNames[i], dbName + "-" + (i + 1) + "-replica");

				}
				props.put("database", dbName);
				props.put("state", "attach");
				postRequest(null, props, "/manage/v2/forests/" + dbName + "-" + (i + 1));
			}
			props = new HashMap<>();
			props.put("journaling", "strict");
			changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
			associateRESTServerWithDB(server, dbName);
			if (IsSecurityEnabled()) {
				enableSecurityOnRESTServer(server, dbName);
			}
			// StringHandle
			stringTriple = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so foo</foo>";
			stringHandle = new StringHandle(stringTriple);
			stringHandle.setFormat(Format.XML);
			meta2 = new DocumentMetadataHandle().withCollections("XmlTransform");
			meta2.setFormat(Format.XML);
			// Xquery transformation
			TransformExtensionsManager transMgr = dbClient.newServerConfigManager().newTransformExtensionsManager();
			ExtensionMetadata metadata = new ExtensionMetadata();
			metadata.setTitle("Adding attribute xquery Transform");
			metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
			metadata.setProvider("MarkLogic");
			metadata.setVersion("0.1");
			// get the transform file
			File transformFile = FileUtils
					.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "add-attr-xquery-transform.xqy"));
			FileHandle transformHandle = new FileHandle(transformFile);
			transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
		} else {
			System.out.println("Test skipped -  setUpBeforeClass");
		}
	}

	private static void removeReplica(String string) {
		String query = "xquery version \"1.0-ml\";"
				+ "import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";"
				+ " let $config := admin:get-configuration()\r\n" + "  let $forest := xdmp:forest(\"" + string
				+ "\")\r\n" + "  let $replica-forest := xdmp:forest(\"" + string + "-replica" + "\")\r\n"
				+ "  let $save := admin:forest-remove-replica($config, $forest, $replica-forest)";
		query += " let $saveconfig := admin:save-configuration($save)" + "return ()";
		System.out.println("Query is " + query);
		evalClient.newServerEval().xquery(query).eval();
	}

	private static void createForest(String forestName, String hostname, String replica) {
		String query = "xquery version \"1.0-ml\";"
				+ "import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";"
				+ "let $forest-create := admin:forest-create(admin:get-configuration(), \"" + forestName
				+ "\", xdmp:host(\"" + hostname + "\"), ()) ";

		query += " let $save := admin:save-configuration($forest-create)" + " return ()";
		System.out.println("Query is " + query);
		evalClient.newServerEval().xquery(query).eval();

		if (replica != null) {
			query = "xquery version \"1.0-ml\";"
					+ "import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";"
					+ " let $config := admin:get-configuration()\r\n"
					+ "let $config :=  admin:forest-add-replica($config,admin:forest-get-id($config, \"" + forestName
					+ "\"),admin:forest-get-id($config, \"" + replica + "\")) ";
			query += " let $save := admin:save-configuration($config)" + " return ()";
			System.out.println("Query is " + query);
			evalClient.newServerEval().xquery(query).eval();
		}
	}

	private String getForestState(String forest) {
		String query = "declare namespace h = \"http://marklogic.com/xdmp/status/forest\";\r\n"
				+ "xdmp:forest-status(xdmp:forest(\"" + forest + "\"))/h:state/text()";
		return evalClient.newServerEval().xquery(query).eval().next().getString();
	}

	// replica should be in "sync replicating" and master should be "open"
	private boolean checkForestState() {
		String stateCheck = "declare namespace h = \"http://marklogic.com/xdmp/status/forest\";\r\n"
				+ "let $ids := xdmp:database-forests(xdmp:database(\"" + dbName + "\"),xs:boolean(\"true\"))\r\n"
				+ "let $output := \r\n" + "for $id in $ids\r\n" + "let $forest :=  xdmp:forest-status($id)\r\n"
				+ "let $forest-name := $forest/h:forest-name/text()\r\n"
				+ "let $forest-state := $forest/h:state/text()\r\n" + "let $states :=\r\n"
				+ "if (fn:contains($forest-name, \"replica\"))\r\n" + "then\r\n"
				+ "  if(fn:matches($forest-state,\"sync replicating\"))\r\n" + "  then\r\n"
				+ "  xs:boolean(\"true\")\r\n" + "  else\r\n" + "  xs:boolean(\"false\")\r\n" + "else\r\n"
				+ "  if(fn:matches($forest-state,\"open\"))\r\n" + "  then\r\n" + "  xs:boolean(\"true\")\r\n"
				+ "  else\r\n" + "  xs:boolean(\"false\")\r\n" + "return $states\r\n"
				+ "return cts:contains($output,\"false\")";
		int count = 3;
		while (evalClient.newServerEval().xquery(stateCheck).eval().next().getString().equals("true")) {
			if (count > 0) {
				count--;
				try {
					Thread.sleep(15000L);
				} catch (InterruptedException e) {

				}
			} else {
				return false;
			}
		}
		return true;
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		// Perform the setup on multiple nodes only.
		if (hostNames.length > 1) {
			associateRESTServerWithDB(server, "Documents");
			for (int i = 0; i < hostNames.length; i++) {
				System.out.println(dbName + "-" + (i + 1));
				detachForest(dbName, dbName + "-" + (i + 1));
				if (i != 0) {
					removeReplica(dbName + "-" + (i + 1));
					deleteForest(dbName + "-" + (i + 1) + "-replica");
				}
				deleteForest(dbName + "-" + (i + 1));
			}
			deleteDB(dbName);
		} else {
			System.out.println("Test skipped -  tearDownAfterClass");
		}
	}

	@BeforeEach
	public void setUp() throws Exception {
		// Perform the setup on multiple nodes only.
		if (hostNames.length > 1) {
			assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
			addDocs();
			waitForForest("after");
			assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 20000);
			ForestConfiguration fc = dmManager.readForestConfig();
			Forest[] f = fc.listForests();
			f = (Forest[]) Arrays.stream(f).filter(x -> x.getDatabaseName().equals(dbName)).collect(Collectors.toList())
					.toArray(new Forest[hostNames.length]);
			assertEquals(f.length, hostNames.length);
			assertEquals(f.length, 3L);
		} else {
			System.out.println("Test skipped -  setUp");
		}
	}

	private void clearForests() {
		String forestIds = "xdmp:database-forests(xdmp:database(\"" + dbName + "\"),xs:boolean(\"true\"))";
		EvalResultIterator itr = evalClient.newServerEval().xquery(forestIds).eval();
		while (itr.hasNext()) {
			String forestId = itr.next().getString();
			evalClient.newServerEval().xquery("xdmp:forest-clear(" + forestId + ")").eval();
		}
	}

	private void waitForForest(String testStatus) throws InterruptedException {
		for (int i = hostNames.length - 1; i >= 1; i--) {
			boolean cond1 = false;
			boolean cond2 = false;
			int count = 12;
			while (count > 0) {
				count--;
				String status1 = getForestState(dbName + "-" + (i + 1) + "-replica").toLowerCase();
				String status2 = getForestState(dbName + "-" + (i + 1)).toLowerCase();
				cond1 = (status1.equals("open") || status1.equals("sync replicating"));
				cond2 = (status2.equals("open") || status2.equals("sync replicating"));
				System.out.println("Status 1: " + status1);
				System.out.println("Status 2: " + status2);
				if ("test".equals(testStatus)) {
					cond2 = cond2 || (status2.equals("unmounted"));
				}
				if (cond1 && cond2)
					break;
				Thread.sleep(5000L);
			}
			System.out.println("Wait Count is " + count);
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
		// Perform the setup on multiple nodes only.
		if (hostNames.length > 1) {
			System.out.println("Restarting servers");
			for (int i = hostNames.length - 1; i >= 1; i--) {
				System.out.println("Restarting server " + hostNames[i]);
				serverStartStop(hostNames[i], "start");
				Thread.sleep(2000L);
				assertTrue(isRunning(hostNames[i]));
			}
			waitForForest("after");
			clearForests();
			waitForForest("after");
			Map<String, String> props = new HashMap<>();
			for (int i = hostNames.length - 1; i >= 1; i--) {
				System.out.println("Replica: " + getForestState(dbName + "-" + (i + 1) + "-replica").toLowerCase());
				System.out.println(getForestState(dbName + "-" + (i + 1)).toLowerCase());
				if (!"open".equals(getForestState(dbName + "-" + (i + 1)).toLowerCase())) {
					props.put("enabled", "true");
					System.out.println("Enabling " + dbName + "-" + (i + 1));
					changeProperty(props, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "/properties");
					Thread.sleep(3000L);
					props.put("enabled", "false");
					System.out.println("Disabling " + dbName + "-" + (i + 1) + "-replica");
					changeProperty(props, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "-replica" + "/properties");

					props.put("enabled", "true");
					System.out.println("Enabling " + dbName + "-" + (i + 1) + "-replica");
					changeProperty(props, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "-replica" + "/properties");
				}
				waitForForest("after");
				System.out
						.println("After Replica: " + getForestState(dbName + "-" + (i + 1) + "-replica").toLowerCase());
				System.out.println(getForestState(dbName + "-" + (i + 1)).toLowerCase());
				assertTrue("open".equals(getForestState(dbName + "-" + (i + 1)).toLowerCase()));
			}
			// checkForestState();
		} else {
			System.out.println("Test skipped -  tearDown");
		}
		Thread.sleep(5000L);
	}

	@Test
	public void testStopOneNode() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isRunning = new AtomicBoolean(true);

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(1).withThreadCount(55);

		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(10))
				.withMinHosts(2);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(10))
				.withMinHosts(2);
		batcher.onUrisReady((batch) -> {
			success.addAndGet(batch.getItems().length);
		}).onQueryFailure(queryException -> {
			queryException.printStackTrace();
		});
		ticket = dmManager.startJob(batcher);
		while (!batcher.isStopped()) {
			if (dmManager.getJobReport(ticket).getSuccessEventsCount() > 10 && isRunning.get()) {
				isRunning.set(false);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		System.out.println("Success " + success.intValue());
		System.out.println("Failure " + failure.intValue());

		assertEquals(20000, success.intValue());
		assertEquals(0, failure.intValue());
	}

	@Test
	public void testStopOneNodeShortDuration() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isRunning = new AtomicBoolean(true);

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(1).withThreadCount(55);

		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(1))
				.withMinHosts(2);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(1))
				.withMinHosts(2);
		batcher.onUrisReady((batch) -> {
			success.addAndGet(batch.getItems().length);
		}).onQueryFailure(queryException -> {
			queryException.printStackTrace();
		});
		ticket = dmManager.startJob(batcher);
		while (!batcher.isStopped()) {
			if (dmManager.getJobReport(ticket).getSuccessEventsCount() > 10 && isRunning.get()) {
				isRunning.set(false);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		System.out.println("Success " + success.intValue());
		System.out.println("Failure " + failure.intValue());

		assertEquals(20000, success.intValue());
		assertEquals(0, failure.intValue());
	}

	@Test
	public void testStopOneNodeLongDuration() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isRunning = new AtomicBoolean(true);

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(1).withThreadCount(55);

		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofMinutes(1))
				.withMinHosts(2);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofMinutes(1))
				.withMinHosts(2);
		batcher.onUrisReady((batch) -> {
			success.addAndGet(batch.getItems().length);
		}).onQueryFailure(queryException -> {
			queryException.printStackTrace();
		});
		ticket = dmManager.startJob(batcher);
		while (!batcher.isStopped()) {
			if (dmManager.getJobReport(ticket).getSuccessEventsCount() > 10 && isRunning.get()) {
				isRunning.set(false);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		System.out.println("Success " + success.intValue());
		System.out.println("Failure " + failure.intValue());

		assertEquals(20000, success.intValue());
		assertEquals(0, failure.intValue());
	}

	@Test
	public void testRestart() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isRunning = new AtomicBoolean(true);

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(1).withThreadCount(44);

		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(10))
				.withMinHosts(2);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(10))
				.withMinHosts(2);
		batcher.onUrisReady((batch) -> {
			success.addAndGet(batch.getItems().length);
		}).onQueryFailure(queryException -> {
			queryException.printStackTrace();
		});
		ticket = dmManager.startJob(batcher);
		while (!batcher.isStopped()) {
			if (dmManager.getJobReport(ticket).getSuccessEventsCount() > 10 && isRunning.get()) {
				isRunning.set(false);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
				Thread.currentThread().sleep(15000L);
				serverStartStop(hostNames[hostNames.length - 1], "start");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		System.out.println("Success " + success.intValue());
		System.out.println("Failure " + failure.intValue());

		assertEquals(20000, success.intValue());
		assertEquals(0, failure.intValue());
	}

	@Test
	public void testRepeatedStopOneNode() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isRunning = new AtomicBoolean(true);

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(15).withThreadCount(2);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(5))
				.withMinHosts(2);
		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(5))
				.withMinHosts(2);

		batcher.onUrisReady((batch) -> {
			success.addAndGet(batch.getItems().length);
		}).onQueryFailure(queryException -> {
			queryException.printStackTrace();
		});

		ticket = dmManager.startJob(batcher);
		while (!batcher.isStopped()) {
			if (dmManager.getJobReport(ticket).getSuccessEventsCount() > 10 && isRunning.get()) {
				isRunning.set(false);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "start");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
				Thread.currentThread().sleep(15000L);
				serverStartStop(hostNames[hostNames.length - 1], "start");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		System.out.println("Success " + success.intValue());
		System.out.println("Failure " + failure.intValue());

		assertEquals(20000, success.intValue());
		assertEquals(0, failure.intValue());
	}

	@Test
	public void testMinNodes() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isRunning = new AtomicBoolean(true);
		try {
			QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
					.withBatchSize(4).withThreadCount(2);

			HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(2))
					.withMinHosts(3);
			NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(2))
					.withMinHosts(3);

			batcher.onUrisReady((batch) -> {
				success.addAndGet(batch.getItems().length);
			}).onQueryFailure(queryException -> {
				queryException.printStackTrace();
			});

			ticket = dmManager.startJob(batcher);

			while (isRunning.get()) {
				if (dmManager.getJobReport(ticket).getSuccessEventsCount() > 1 && isRunning.get()) {
					isRunning.set(false);
					serverStartStop(hostNames[hostNames.length - 1], "stop");
				}
			}

			Thread.currentThread().sleep(2000L);
			batcher.awaitCompletion();
			dmManager.stopJob(ticket);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.currentThread().sleep(2000L);
		serverStartStop(hostNames[hostNames.length - 1], "start");
		Thread.currentThread().sleep(20000L);
		System.out.println("Success " + success.intValue());
		System.out.println("Failure " + failure.intValue());
		assertTrue(success.intValue() < 20000);
	}

	@Test
	public void testStopTwoNodes() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			final AtomicInteger success = new AtomicInteger(0);

			AtomicBoolean isNode3Running = new AtomicBoolean(true);
			AtomicBoolean isNode2Running = new AtomicBoolean(true);
			QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
					.withBatchSize(5).withThreadCount(6);

			HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
					.withMinHosts(1);
			NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
					.withMinHosts(1);

			batcher.onUrisReady((batch) -> {
				success.addAndGet(batch.getItems().length);
			}).onQueryFailure(queryException -> {
				queryException.printStackTrace();
			});
			ticket = dmManager.startJob(batcher);
			while (!batcher.isStopped()) {
				if (isNode3Running.get() && dmManager.getJobReport(ticket).getSuccessEventsCount() > 0) {
					isNode3Running.set(false);
					serverStartStop(hostNames[hostNames.length - 1], "stop");
				}
				if (isNode2Running.get() && dmManager.getJobReport(ticket).getSuccessEventsCount() > 50) {
					isNode2Running.set(false);
					serverStartStop(hostNames[hostNames.length - 2], "stop");
					Thread.currentThread().sleep(5000L);
					serverStartStop(hostNames[hostNames.length - 1], "start");
				}
			}
			batcher.awaitCompletion();
			dmManager.stopJob(ticket);
			Thread.sleep(2000L);
			assertTrue(isRunning(hostNames[hostNames.length - 3]));
			assertTrue(isRunning(hostNames[hostNames.length - 1]));
			System.out.println("Success " + success.intValue());
			assertEquals(20000, success.intValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void xQueryMasstransformReplace() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");

		AtomicInteger skipped = new AtomicInteger(0);
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isNode2Running = new AtomicBoolean(true);

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					success.addAndGet(batch.getItems().length);
				}).onSkipped(batch -> {
					skipped.addAndGet(batch.getItems().length);
				});

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.onUrisReady(listener).withBatchSize(4).withThreadCount(30);
		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(10))
				.withMinHosts(2);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(10))
				.withMinHosts(2);
		NoResponseListener noResponseListener = NoResponseListener.getInstance(batcher);
		BatchFailureListener<QueryBatch> retryListener = noResponseListener.initializeRetryListener(listener);
		listener.onFailure(retryListener).onFailure((batch, throwable) -> {
			failure.addAndGet(batch.getItems().length);
			throwable.printStackTrace();
		});

		JobTicket ticket = dmManager.startJob(batcher);
		while (!batcher.isStopped()) {
			if (isNode2Running.get() && dmManager.getJobReport(ticket).getSuccessEventsCount() > 10) {
				isNode2Running.set(false);
				serverStartStop(hostNames[hostNames.length - 2], "stop");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		Thread.sleep(2000L);
		assertTrue(isRunning(hostNames[hostNames.length - 3]));
		assertTrue(isRunning(hostNames[hostNames.length - 1]));
		waitForForest("test");
		System.out.println("State is :" + getForestState("QBFailover-2"));
		System.out.println("State is :" + getForestState("QBFailover-2-replica"));
		AtomicInteger modified = new AtomicInteger(0);
		AtomicBoolean passed = new AtomicBoolean(true);
		QueryBatcher readBatcher = tempMgr.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(10).withThreadCount(5).onUrisReady((batch) -> {
					DocumentPage page = batch.getClient().newDocumentManager().read(batch.getItems());
					while (page.hasNext()) {
						DocumentRecord rec = page.next();
						DOMHandle dh = new DOMHandle();
						rec.getContent(dh);
						Node n = dh.get().getElementsByTagName("foo").item(0);
						if (n.hasAttributes()) {
							if (n.getAttributes().item(0).getNodeValue().equals("English")) {
								modified.incrementAndGet();
							}
						} else {
							passed.set(false);
						}
					}
				});
		tempMgr.startJob(readBatcher);
		readBatcher.awaitCompletion();
		System.out.println("Modified docs: " + modified.intValue());
		System.out.println("Modified docs: " + success.intValue());
		assertTrue(passed.get());
		assertEquals(20000, modified.intValue());
		assertEquals(20000, success.intValue());
		assertEquals(0, skipped.intValue());

	}

	@Test
	public void xQueryMasstransformReplaceTwoNodes() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");

		AtomicInteger skipped = new AtomicInteger(0);
		AtomicInteger success = new AtomicInteger(0);
		AtomicBoolean isNode3Running = new AtomicBoolean(true);
		AtomicBoolean isNode2Running = new AtomicBoolean(true);

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					success.addAndGet(batch.getItems().length);
				}).onSkipped(batch -> {
					skipped.addAndGet(batch.getItems().length);
				});

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.onUrisReady(listener).withBatchSize(10).withThreadCount(5);
		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(12))
				.withMinHosts(1);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(12))
				.withMinHosts(1);
		NoResponseListener noResponseListener = NoResponseListener.getInstance(batcher);
		BatchFailureListener<QueryBatch> retryListener = noResponseListener.initializeRetryListener(listener);
		listener.onFailure(retryListener);

		JobTicket ticket = dmManager.startJob(batcher);
		while (!batcher.isStopped()) {
			if (isNode2Running.get() && dmManager.getJobReport(ticket).getSuccessEventsCount() > 1) {
				isNode2Running.set(false);
				serverStartStop(hostNames[hostNames.length - 2], "stop");
			}
			if (isNode3Running.get() && dmManager.getJobReport(ticket).getSuccessEventsCount() > 40) {
				isNode3Running.set(false);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
				Thread.currentThread().sleep(5000L);
				serverStartStop(hostNames[hostNames.length - 2], "start");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		Thread.sleep(2000L);
		assertTrue(isRunning(hostNames[hostNames.length - 2]));
		waitForForest("test");
		AtomicInteger modified = new AtomicInteger(0);
		AtomicBoolean passed = new AtomicBoolean(true);
		QueryBatcher readBatcher = tempMgr.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(10).withThreadCount(5).onUrisReady((batch) -> {
					DocumentPage page = batch.getClient().newDocumentManager().read(batch.getItems());
					while (page.hasNext()) {
						DocumentRecord rec = page.next();
						DOMHandle dh = new DOMHandle();
						rec.getContent(dh);
						Node n = dh.get().getElementsByTagName("foo").item(0);
						if (n.hasAttributes()) {
							if (n.getAttributes().item(0).getNodeValue().equals("English")) {
								modified.incrementAndGet();
							}
						} else {
							passed.set(false);
						}
					}
				});
		tempMgr.startJob(readBatcher);
		readBatcher.awaitCompletion();
		System.out.println("Modified docs: " + modified.intValue());
		assertTrue(passed.get());
		assertEquals(20000, modified.intValue());
		assertEquals(20000, success.intValue());
		assertEquals(0, skipped.intValue());
	}

	@Test
	public void xQueryMasstransformReplaceRepeated() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");

		AtomicInteger skipped = new AtomicInteger(0);
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isRunning = new AtomicBoolean(true);

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					success.addAndGet(batch.getItems().length);
				}).onSkipped(batch -> {
					skipped.addAndGet(batch.getItems().length);
				});

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.onUrisReady(listener).withBatchSize(10).withThreadCount(5);
		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(12))
				.withMinHosts(1);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(12))
				.withMinHosts(1);
		NoResponseListener noResponseListener = NoResponseListener.getInstance(batcher);
		BatchFailureListener<QueryBatch> retryListener = noResponseListener.initializeRetryListener(listener);
		listener.onFailure(retryListener);

		JobTicket ticket = dmManager.startJob(batcher);

		ticket = dmManager.startJob(batcher);
		while (!batcher.isStopped()) {
			if (dmManager.getJobReport(ticket).getSuccessEventsCount() > 20 && isRunning.get()) {
				isRunning.set(false);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "start");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
				Thread.currentThread().sleep(18000L);
				serverStartStop(hostNames[hostNames.length - 1], "start");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		Thread.sleep(2000L);
		assertTrue(isRunning(hostNames[hostNames.length - 3]));
		assertTrue(isRunning(hostNames[hostNames.length - 2]));
		waitForForest("test");
		System.out.println("Success " + success.intValue());
		System.out.println("Failure " + failure.intValue());
		AtomicInteger modified = new AtomicInteger(0);
		AtomicBoolean passed = new AtomicBoolean(true);
		QueryBatcher readBatcher = tempMgr.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(10).withThreadCount(5).onUrisReady((batch) -> {
					DocumentPage page = batch.getClient().newDocumentManager().read(batch.getItems());
					while (page.hasNext()) {
						DocumentRecord rec = page.next();
						DOMHandle dh = new DOMHandle();
						rec.getContent(dh);
						Node n = dh.get().getElementsByTagName("foo").item(0);
						if (n.hasAttributes()) {
							if (n.getAttributes().item(0).getNodeValue().equals("English")) {
								modified.incrementAndGet();
							}
						} else {
							passed.set(false);
						}
					}
				});
		tempMgr.startJob(readBatcher);
		readBatcher.awaitCompletion();
		System.out.println("Modified docs: " + modified.intValue());
		assertTrue(passed.get());
		assertEquals(20000, modified.intValue());
		assertEquals(20000, success.intValue());
		assertEquals(0, failure.intValue());
	}

	@Test
	public void massDeleteConsistentSnapShot() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicBoolean isRunning = new AtomicBoolean(true);
		Map<String, String> props = new HashMap<String, String>();
		props.put("merge-timestamp", "-6000000000");
		changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
		Thread.currentThread().sleep(5000L);
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(2).withConsistentSnapshot().withThreadCount(55).onUrisReady(new DeleteListener())
				.onQueryFailure(throwable -> {
					System.out.println("Exceptions thrown from callback onQueryFailure");
					throwable.printStackTrace();

				});
		JobTicket ticket = dmManager.startJob(batcher);
		while (!batcher.isStopped()) {
			if (dmManager.getJobReport(ticket).getSuccessEventsCount() > 10 && isRunning.get()) {
				isRunning.set(false);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "start");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
				Thread.currentThread().sleep(18000L);
				serverStartStop(hostNames[hostNames.length - 1], "start");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		Thread.sleep(2000L);
		assertTrue(isRunning(hostNames[hostNames.length - 3]));
		assertTrue(isRunning(hostNames[hostNames.length - 2]));
		props.put("merge-timestamp", "0");
		changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
		System.out.println("Count: " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertEquals(0, evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	}

	/*
	 * This test is intended to test closing of listeners when job is done.
	 *
	 *
	 */

	@Test
	public void testListenerCloseables() {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicInteger success = new AtomicInteger(0);
		// There two variables are to track close method on Listeners.
		AtomicBoolean testCloseOnBatchListenerUriReady = new AtomicBoolean(false);
		AtomicBoolean testCloseOnFailureListenerQueryFailure = new AtomicBoolean(false);

		// This variable tracks the OnJobCompleteion status
		AtomicBoolean getOnePrimaryDBClient = new AtomicBoolean(false);

		// Track primary database client on all listeners and job completion
		StringBuilder sb_strBatchListenerUriReady = new StringBuilder();
		StringBuilder sb_strJobCompletionListener = new StringBuilder();

		class TestCloseOnBatchListenerUriReady implements QueryBatchListener, AutoCloseable {

			@Override
			public void close() throws Exception {
				System.out.println(
						"Close called from testMinNodesWithCloseable in TestCloseOnBatchListenerUriReady class");
				testCloseOnBatchListenerUriReady.set(true);
			}

			@Override
			public void processEvent(QueryBatch batch) {
				System.out.println(
						"processEvent called from testMinNodesWithCloseable in TestCloseOnBatchListenerUriReady class");
				// Verify the Primary DatabaseClient instance
				if (!getOnePrimaryDBClient.get()) {
					getOnePrimaryDBClient.set(true);
					sb_strBatchListenerUriReady.append(batch.getBatcher().getPrimaryClient().getHost());
					sb_strBatchListenerUriReady.append("|");
					sb_strBatchListenerUriReady.append(batch.getBatcher().getPrimaryClient().getPort());
				}
			}
		}

		class TestCloseOnBatchListenerQueryFailure implements QueryFailureListener, AutoCloseable {

			@Override
			public void close() throws Exception {
				System.out.println(
						"Close called from testMinNodesWithCloseable in TestCloseOnBatchListenerQueryFailure class");
				testCloseOnFailureListenerQueryFailure.set(true);
			}

			@Override
			public void processFailure(QueryBatchException failure) {
				System.out.println(
						"processFailure called from testMinNodesWithCloseable in TestCloseOnBatchListenerQueryFailure class");
			}
		}

		// Listener to be called when QueryBatcher has completed reading all URIs
		class TestQBJobCompleteionListener implements QueryBatcherListener {

			@Override
			public void processEvent(QueryBatcher batcher) {
				System.out.println(
						"processEvent called from testMinNodesWithCloseable in TestQBJobCompleteionListener class");

				// Verify a detail - ticket Id at end of completion
				sb_strJobCompletionListener.append(batcher.getBatchSize());
			}
		}

		try {

			TestCloseOnBatchListenerUriReady closeBatchURIs = new TestCloseOnBatchListenerUriReady();
			TestCloseOnBatchListenerQueryFailure closeQueryFailure = new TestCloseOnBatchListenerQueryFailure();
			TestQBJobCompleteionListener jobCompleteListener = new TestQBJobCompleteionListener();

			QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
					.withBatchSize(4000).withThreadCount(5);

			// Add the new Listeners to the batcher.

			batcher.onUrisReady((batch) -> {
				success.addAndGet(batch.getItems().length);
			}).onQueryFailure(queryException -> {
				queryException.printStackTrace();
			}).onUrisReady(closeBatchURIs).onQueryFailure(closeQueryFailure).onJobCompletion(jobCompleteListener);

			ticket = dmManager.startJob(batcher);

			batcher.awaitCompletion();
			dmManager.stopJob(ticket);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Verify the DatabaseClient instances.
		System.out.println("Primary database instance is  " + sb_strBatchListenerUriReady.toString());

		// Verify the close status
		assertTrue(testCloseOnBatchListenerUriReady.get());
		assertTrue(testCloseOnFailureListenerQueryFailure.get());

		// Verify the batch size on job completion
		assertTrue(sb_strJobCompletionListener.toString().equalsIgnoreCase("4000"));
		// Verify the primary database client
		assertTrue(sb_strBatchListenerUriReady.toString().contains(String.valueOf(port)));
	}

	private void serverStartStop(String server, String command) throws Exception {
		System.out.println("Preparing to " + command + " " + server);
		String commandtoRun = null;

		if (OS.indexOf("win") >= 0) {
			commandtoRun = "net " + command.toLowerCase() + " MarkLogic";
		} else {
			commandtoRun = "sudo mladmin " + command.toLowerCase();
		}

		if (command.toLowerCase() != "start" && command.toLowerCase() != "stop") {
			System.out.println("Invalid Command");
			return;

		}
		String[] temp = { "sh", "-c", "ssh " + server + " " + commandtoRun };
		Process proc = Runtime.getRuntime().exec(temp);
		BufferedReader stdOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		String s = null;
		while ((s = stdOut.readLine()) != null) {
			System.out.println(s);
		}

		// read any errors from the attempted command
		System.out.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}
		System.out.println(command + " " + server + " completed");
	}

	private void addDocs() {
		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(125).withThreadCount(13);
		ihb2.onBatchSuccess(batch -> {
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
		});

		dmManager.startJob(ihb2);

		for (int j = 0; j < 20000; j++) {
			String uri = "/local/string-" + j;
			ihb2.add(uri, meta2, stringHandle);
		}
		ihb2.flushAndWait();
	}

	private boolean isRunning(String host) {
		try {

			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host, 7997),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpGet get = new HttpGet("http://" + host + ":7997?format=json");
			HttpResponse response = client.execute(get);
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			if (body.toLowerCase().contains("healthy")) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return false;
		}
	}
}
