/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.functionaltests;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class WBFailover extends BasicJavaClientREST {
	private static String dbName = "WBFailover";
	private static DataMovementManager dmManager;
	private static final String OS = System.getProperty("os.name").toLowerCase();

	private static DatabaseClient dbClient;
	private static DatabaseClient evalClient;
	private static String host = null;
	private static String user = null;
	private static Integer port = null;
	private static String password = null;
	private static String server = null;
	private static StringHandle stringHandle;
	private static String[] hostNames;
	private static String stringTriple;
	private static JobTicket writeTicket;
	final String query1 = "fn:count(fn:doc())";
	private static List<String> hostLists;

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
			hostLists = new ArrayList<String>();
			Pattern pattern = null;
			Matcher matcher = null;
			for (String host : hostNames) {
				hostLists.add(host);
				pattern = Pattern.compile("(.+?)(?=\\.)");
				matcher = pattern.matcher(host);
				if (matcher.find()) {
					hostLists.add(matcher.group(1));
				}
			}
			hostLists.add("localhost");
			// Assuming the tests are run on 3 node cluster.
			assertEquals(hostLists.size(), 7);

			int index = new Random().nextInt(hostLists.size());
			dbClient = getDatabaseClientOnDatabase(hostLists.get(index), port, dbName, user, password,
					getConnType());
			evalClient = getDatabaseClientOnDatabase(host, port, dbName, user, password, getConnType());
			dmManager = dbClient.newDataMovementManager();
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
			props = new HashMap<String, String>();
			props.put("journaling", "strict");
			changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
			associateRESTServerWithDB(server, dbName);
			if (IsSecurityEnabled()) {
				enableSecurityOnRESTServer(server, dbName);
			}
			// StringHandle
			stringTriple = "<top-song xmlns=\"http://marklogic.com/MLU/top-songs\"> <!--Copyright (c) 2010 Mark Logic Corporation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published bythe Free Software Foundation; with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the license is included in the section entitled \"GNU Free Documentation License.\" Content derived from http://en.wikipedia.org/w/index.php?title=Blue_Champagne_(song)&action=edit&redlink=1 Modified in February 2010 by Mark Logic Corporation under the terms of the GNU Free Documentation License.-->  <title href=\"http://en.wikipedia.org/w/index.php?title=Blue_Champagne_(song)&amp;action=edit&amp;redlink=1\" xmlns:ts=\"http://marklogic.com/MLU/top-songs\">Blue Champagne</title>  <artist xmlns:ts=\"http://marklogic.com/MLU/top-songs\"/>  <weeks last=\"1941-09-27\">    <week>1941-09-27</week>  </weeks>  <descr/></top-song>";
			stringHandle = new StringHandle(stringTriple);
			stringHandle.setFormat(Format.XML);
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
		evalClient.newServerEval().xquery(query).eval();
	}

	private static void createForest(String forestName, String hostname, String replica) {
		String query = "xquery version \"1.0-ml\";"
				+ "import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";"
				+ "let $forest-create := admin:forest-create(admin:get-configuration(), \"" + forestName
				+ "\", xdmp:host(\"" + hostname + "\"), ()) ";

		query += " let $save := admin:save-configuration($forest-create)" + " return ()";
		evalClient.newServerEval().xquery(query).eval();

		if (replica != null) {
			query = "xquery version \"1.0-ml\";"
					+ "import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";"
					+ " let $config := admin:get-configuration()\r\n"
					+ "let $config :=  admin:forest-add-replica($config,admin:forest-get-id($config, \"" + forestName
					+ "\"),admin:forest-get-id($config, \"" + replica + "\")) ";
			query += " let $save := admin:save-configuration($config)" + " return ()";
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
		int count = 2;
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
			ForestConfiguration fc = dmManager.readForestConfig();
			Forest[] f = fc.listForests();
			f = (Forest[]) Arrays.stream(f).filter(x -> x.getDatabaseName().equals(dbName)).collect(Collectors.toList())
					.toArray(new Forest[hostNames.length]);
			assertEquals(f.length, hostNames.length);
			assertEquals(f.length, 3L);
			assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
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
				if ("test".equals(testStatus)) {
					if (isRunning(hostNames[i])) {
						cond1 = status1.equals("sync replicating");
						cond2 = status2.equals("open");
					} else {
						cond2 = status2.equals("unmounted");
						cond1 = status1.equals("open");
					}
				}
				System.out.println("Status 1: " + status1);
				System.out.println("Status 2: " + status2);
				if (cond1 && cond2)
					break;
				Thread.sleep(5000L);
			}
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
				assertTrue("open".equals(getForestState(dbName + "-" + (i + 1)).toLowerCase()));
			}
		} else {
			System.out.println("Test skipped -  tearDown");
		}
		Thread.sleep(5000L);
	}

	@Test
	public void testBlackListHost() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		try {
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
			final AtomicInteger successCount = new AtomicInteger(0);
			final AtomicBoolean containsBLHost = new AtomicBoolean(false);
			final AtomicBoolean failState = new AtomicBoolean(false);
			SecurityContext secContext = newSecurityContext("admin", "admin");
			DatabaseClient dbClient = DatabaseClientFactory.newClient(hostLists.get(3), 8000, secContext, getConnType());
			DataMovementManager dmManager = dbClient.newDataMovementManager();
			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			FilteredForestConfiguration forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig())
					.withBlackList(hostLists.get(2));
			ihb2.withBatchSize(10).withForestConfig(forestConfig);

			ihb2.withThreadCount(120);
			dmManager.startJob(ihb2);
			HostAvailabilityListener.getInstance(ihb2).withMinHosts(2);
			NoResponseListener.getInstance(ihb2).withMinHosts(2);
			ihb2.onBatchSuccess(batch -> {
				if (batch.getClient().getHost().equals(hostLists.get(2))
						|| batch.getClient().getHost().equals(hostLists.get(3))) {
					containsBLHost.set(true);
				}
				successCount.getAndAdd(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				failState.set(true);
				if (throwable.getMessage().contains("XDMP-XDQPNOSESSION")) {
					ihb2.retry(batch);
				}
			});
			for (int j = 0; j < 10000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
			}
			ihb2.flushAndWait();

			assertFalse(failState.get());
			assertFalse(containsBLHost.get());
			assertEquals(10000, successCount.get());
			assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 10000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testStopOneNode() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		final AtomicInteger successCount = new AtomicInteger(0);
		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);
		try {
			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(2);
			ihb2.withThreadCount(99);
			HostAvailabilityListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
					.withMinHosts(2);
			NoResponseListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
					.withMinHosts(2);
			ihb2.onBatchSuccess(batch -> {
				successCount.addAndGet(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				System.out.println(throwable.getMessage());
				failState.set(true);
				failCount.addAndGet(batch.getItems().length);
				if (throwable.getMessage().contains("XDMP-XDQPNOSESSION")) {
					ihb2.retry(batch);
				}
			});

			writeTicket = dmManager.startJob(ihb2);
			AtomicBoolean isRunning = new AtomicBoolean(true);
			for (int j = 0; j < 50000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
				if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 200 && isRunning.get()) {
					isRunning.set(false);
					serverStartStop(hostNames[hostNames.length - 1], "stop");
				}
			}
			ihb2.flushAndWait();
			Thread.sleep(2000L);
			assertTrue(isRunning(hostNames[hostNames.length - 3]));
			assertTrue(isRunning(hostNames[hostNames.length - 2]));
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Fail : " + failCount.intValue());
		System.out.println("Success : " + successCount.intValue());
		System.out.println("Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 50000);
	}

	@Test
	public void testStopOneNodeLongDuration() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		final AtomicInteger successCount = new AtomicInteger(0);
		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);
		try {
			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(2);
			ihb2.withThreadCount(99);
			HostAvailabilityListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofMinutes(1))
					.withMinHosts(2);
			NoResponseListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofMinutes(1))
					.withMinHosts(2);
			ihb2.onBatchSuccess(batch -> {
				successCount.addAndGet(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				failState.set(true);
				failCount.addAndGet(batch.getItems().length);
				if (throwable.getMessage().contains("XDMP-XDQPNOSESSION")) {
					ihb2.retry(batch);
				}
			});

			writeTicket = dmManager.startJob(ihb2);
			AtomicBoolean isRunning = new AtomicBoolean(true);
			for (int j = 0; j < 50000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
				if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 200 && isRunning.get()) {
					isRunning.set(false);
					serverStartStop(hostNames[hostNames.length - 1], "stop");
				}
			}
			ihb2.flushAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(2000L);
		assertTrue(isRunning(hostNames[hostNames.length - 3]));
		assertTrue(isRunning(hostNames[hostNames.length - 2]));
		System.out.println("Fail : " + failCount.intValue());
		System.out.println("Success : " + successCount.intValue());
		System.out.println("Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 50000);
	}

	@Test
	public void testStopOneNodeShortDuration() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		final AtomicInteger successCount = new AtomicInteger(0);
		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);
		try {
			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(2);
			ihb2.withThreadCount(99);
			HostAvailabilityListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(1))
					.withMinHosts(2);
			NoResponseListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(1))
					.withMinHosts(2);
			ihb2.onBatchSuccess(batch -> {
				successCount.addAndGet(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				failState.set(true);
				failCount.addAndGet(batch.getItems().length);
				if (throwable.getMessage().contains("XDMP-XDQPNOSESSION")) {
					ihb2.retry(batch);
				}
			});

			writeTicket = dmManager.startJob(ihb2);
			AtomicBoolean isRunning = new AtomicBoolean(true);
			for (int j = 0; j < 50000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
				if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 200 && isRunning.get()) {
					isRunning.set(false);
					serverStartStop(hostNames[hostNames.length - 1], "stop");
				}
			}
			ihb2.flushAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(2000L);
		assertTrue(isRunning(hostNames[hostNames.length - 3]));
		assertTrue(isRunning(hostNames[hostNames.length - 2]));
		System.out.println("Fail : " + failCount.intValue());
		System.out.println("Success : " + successCount.intValue());
		System.out.println("Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 50000);
	}

	@Test
	public void testRestart() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		final AtomicInteger successCount = new AtomicInteger(0);
		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);
		try {
			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(2);
			ihb2.withThreadCount(49);

			HostAvailabilityListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(10))
					.withMinHosts(2);
			NoResponseListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(10))
					.withMinHosts(2);
			ihb2.onBatchSuccess(batch -> {
				successCount.addAndGet(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				failState.set(true);
				failCount.addAndGet(batch.getItems().length);
				if (throwable.getMessage().contains("XDMP-XDQPNOSESSION")) {
					ihb2.retry(batch);
				}
			});

			writeTicket = dmManager.startJob(ihb2);
			AtomicBoolean isRunning = new AtomicBoolean(true);
			for (int j = 0; j < 40000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
				if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 50 && isRunning.get()) {
					isRunning.set(false);
					serverStartStop(hostNames[hostNames.length - 1], "stop");
					Thread.currentThread().sleep(6000L);
					serverStartStop(hostNames[hostNames.length - 1], "start");
				}
			}
			ihb2.flushAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(2000L);
		assertTrue(isRunning(hostNames[hostNames.length - 1]));

		System.out.println("Fail : " + failCount.intValue());
		System.out.println("Success : " + successCount.intValue());
		System.out.println("Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 40000);
	}

	@Test
	public void testRepeatedStopOneNode() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			final AtomicInteger successCount = new AtomicInteger(0);
			final AtomicBoolean failState = new AtomicBoolean(false);
			final AtomicInteger failCount = new AtomicInteger(0);

			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(40);
			ihb2.withThreadCount(3);

			HostAvailabilityListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(5))
					.withMinHosts(2);
			NoResponseListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(5))
					.withMinHosts(2);

			ihb2.onBatchSuccess(batch -> {
				successCount.addAndGet(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				failState.set(true);
				failCount.addAndGet(batch.getItems().length);
				if (throwable.getMessage().contains("XDMP-XDQPNOSESSION")) {
					ihb2.retry(batch);
				}
			});

			writeTicket = dmManager.startJob(ihb2);
			AtomicBoolean isRunning = new AtomicBoolean(true);
			for (int j = 0; j < 40000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
				if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 120 && isRunning.get()) {
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
			ihb2.flushAndWait();
			Thread.sleep(2000L);
			assertTrue(isRunning(hostNames[hostNames.length - 3]));
			assertTrue(isRunning(hostNames[hostNames.length - 2]));
			System.out.println("Fail : " + failCount.intValue());
			System.out.println("Success : " + successCount.intValue());
			System.out.println(
					"Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

			assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 40000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testStopTwoNodes() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			final AtomicInteger successCount = new AtomicInteger(0);
			final AtomicBoolean failState = new AtomicBoolean(false);
			final AtomicInteger failCount = new AtomicInteger(0);

			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(25);
			ihb2.withThreadCount(2);

			HostAvailabilityListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
					.withMinHosts(1);
			NoResponseListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
					.withMinHosts(1);

			ihb2.onBatchSuccess(batch -> {
				successCount.addAndGet(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				failState.set(true);
				failCount.addAndGet(batch.getItems().length);
				if (throwable.getMessage().contains("XDMP-XDQPNOSESSION")) {
					ihb2.retry(batch);
				}
			});

			writeTicket = dmManager.startJob(ihb2);
			AtomicBoolean isNode1Running = new AtomicBoolean(true);
			AtomicBoolean isNode2Running = new AtomicBoolean(true);
			for (int j = 0; j < 40000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
				if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 50 && isNode1Running.get()) {
					isNode1Running.set(false);
					serverStartStop(hostNames[hostNames.length - 1], "stop");
				}

				if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 350 && isNode2Running.get()) {
					isNode2Running.set(false);
					serverStartStop(hostNames[hostNames.length - 2], "stop");
					Thread.currentThread().sleep(5000L);
					serverStartStop(hostNames[hostNames.length - 1], "start");
				}
			}

			ihb2.flushAndWait();
			Thread.sleep(2000L);
			assertTrue(isRunning(hostNames[hostNames.length - 3]));
			assertTrue(isRunning(hostNames[hostNames.length - 1]));
			System.out.println("Fail : " + failCount.intValue());
			System.out.println("Success : " + successCount.intValue());
			System.out.println(
					"Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

			assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 40000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testMinHosts() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		final AtomicInteger successCount = new AtomicInteger(0);
		final AtomicInteger failCount = new AtomicInteger(0);
		WriteBatcher ihb2 = null;
		try {
			ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(13);
			ihb2.withThreadCount(4);
			AtomicBoolean isNodesRunning = new AtomicBoolean(true);
			HostAvailabilityListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(3))
					.withMinHosts(3);
			NoResponseListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(3))
					.withMinHosts(3);
			ihb2.onBatchSuccess(batch -> {
				successCount.addAndGet(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				failCount.addAndGet(batch.getItems().length);
			});
			writeTicket = dmManager.startJob(ihb2);
			for (int j = 0; j < 20000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
				if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 13 && isNodesRunning.get()) {
					isNodesRunning.set(false);
					serverStartStop(hostNames[hostNames.length - 1], "stop");
				}
			}
			ihb2.flushAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.currentThread().sleep(2000L);
		serverStartStop(hostNames[hostNames.length - 1], "start");
		Thread.currentThread().sleep(5000L);
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() > 13);
		assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() < 20000);
		System.out.println("Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		System.out.println("Ending min test");
	}

	@Test
	public void testWhiteBlackListNPE() {
		Assumptions.assumeTrue(hostNames.length > 1);

		assertThrows(IllegalArgumentException.class, () -> {
			String[] hostNames = null;
			new FilteredForestConfiguration(dmManager.readForestConfig()).withBlackList(hostNames);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			String[] hostNames = null;
			new FilteredForestConfiguration(dmManager.readForestConfig()).withWhiteList(hostNames);
		});
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

	private boolean isRunning(String host) {
		try {

			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host, 7997),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpGet get = new HttpGet("http://" + host + ":7997?format=json");
			HttpResponse response = client.execute(get);
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			if (body.contains("Healthy")) {
				return true;
			}

		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
