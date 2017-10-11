package com.marklogic.client.datamovement.functionaltests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.FilteredForestConfiguration;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.HostAvailabilityListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.NoResponseListener;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

public class WBFailover extends BasicJavaClientREST {
	private static String dbName = "WBFailover";
	private static DataMovementManager dmManager;
	private static final String OS = System.getProperty("os.name").toLowerCase();

	private static DatabaseClient dbClient;
	private static DatabaseClient evalClient;
	private static String host = null;
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";
	private static StringHandle stringHandle;
	private static String[] hostNames;
	private static String stringTriple;
	private static JobTicket writeTicket;
	private static String dataDir;
	final String query1 = "fn:count(fn:doc())";
	private static List<String> hostLists;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		loadGradleProperties();
		host = getRestAppServerHostName();
		hostNames = getHosts();
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
		// Assuming the tests are run on 3 node cluster
		Assert.assertEquals(hostLists.size(), 7);
		int index = new Random().nextInt(hostLists.size());
		dbClient = DatabaseClientFactory.newClient(hostLists.get(index), port, user, password, Authentication.DIGEST);
		evalClient = DatabaseClientFactory.newClient(host, port, user, password, Authentication.DIGEST);
		dmManager = dbClient.newDataMovementManager();

		Map<String, String> props = new HashMap<>();
		String version = String.valueOf(evalClient.newServerEval().xquery("xquery version \"1.0-ml\"; xdmp:version()")
				.eval().next().getString().charAt(0));
/*		if (OS.indexOf("win") >= 0) {
			dataDir = "//netapp1-10g.colo.marklogic.com/lab1/space/dmsdk-failover/win/" + version + "/temp-";
		} else */
		if (OS.indexOf("nux") >= 0) {
			dataDir = "/project/qa-netapp/space/dmsdk-failover/linux/" + version + "/temp-";
		} else if (OS.indexOf("mac") >= 0) {
			dataDir = "/project/qa-netapp/space/dmsdk-failover/mac/" + version + "/temp-";
		} else {
			Assert.fail("Unsupported platform");
		}
		createDB(dbName);
		Thread.currentThread().sleep(500L);
		for (int i = 0; i < hostNames.length; i++) {
			if (i != 0) {
				createForest(dbName + "-" + (i + 1), hostNames[i], dataDir + (i + 1), hostNames[0]);
			} else {
				createForest(dbName + "-" + (i + 1), hostNames[i], dataDir + (i + 1), null);

			}
			props.put("database", dbName);
			props.put("state", "attach");
			postRequest(null, props, "/manage/v2/forests/" + dbName + "-" + (i + 1));

			Thread.currentThread().sleep(3000L);
		}
		props = new HashMap<>();
		props.put("journaling", "strict");
		changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");

		Thread.currentThread().sleep(2000L);

		associateRESTServerWithDB(server, dbName);

		// StringHandle
		stringTriple = "<top-song xmlns=\"http://marklogic.com/MLU/top-songs\"> <!--Copyright (c) 2010 Mark Logic Corporation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published bythe Free Software Foundation; with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the license is included in the section entitled \"GNU Free Documentation License.\" Content derived from http://en.wikipedia.org/w/index.php?title=Blue_Champagne_(song)&action=edit&redlink=1 Modified in February 2010 by Mark Logic Corporation under the terms of the GNU Free Documentation License.-->  <title href=\"http://en.wikipedia.org/w/index.php?title=Blue_Champagne_(song)&amp;action=edit&amp;redlink=1\" xmlns:ts=\"http://marklogic.com/MLU/top-songs\">Blue Champagne</title>  <artist xmlns:ts=\"http://marklogic.com/MLU/top-songs\"/>  <weeks last=\"1941-09-27\">    <week>1941-09-27</week>  </weeks>  <descr/></top-song>";
		stringHandle = new StringHandle(stringTriple);
		stringHandle.setFormat(Format.XML);
	}

	private static void createForest(String forestName, String hostname, String dataDir, String failoverHost) {
		String query = "xquery version \"1.0-ml\";"
				+ "import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";"
				+ "let $forest-create := admin:forest-create(admin:get-configuration(), \"" + forestName
				+ "\", xdmp:host(\"" + hostname + "\"), \"" + dataDir + "\")";
		if (failoverHost != null) {
			query += "let $forest-create :=  admin:forest-add-failover-host($forest-create,admin:forest-get-id($forest-create, \""
					+ forestName + "\"),xdmp:host(\"" + failoverHost + "\")) ";
		}

		query += "let $save := admin:save-configuration($forest-create)" + "return ()";
		System.out.println("Query is " + query);
		evalClient.newServerEval().xquery(query).eval();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		associateRESTServerWithDB(server, "Documents");
		for (int i = 0; i < hostNames.length; i++) {
			System.out.println(dbName + "-" + (i + 1));
			detachForest(dbName, dbName + "-" + (i + 1));
			deleteForest(dbName + "-" + (i + 1));
		}
		deleteDB(dbName);

	}

	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < hostNames.length; i++) {
			Assert.assertTrue(isRunning(hostNames[i]));
		}
		Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		ForestConfiguration fc = dmManager.readForestConfig();
		Assert.assertEquals(fc.listForests().length, hostNames.length);
	}

	@After
	public void tearDown() throws Exception {
		Map<String, String> props = new HashMap<>();
		props.put("database", dbName);
		System.out.println("Restarting servers");
		for (int i = hostNames.length - 1; i >= 1; i--) {
			props.put("enabled", "false");
			System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
			System.out.println("Disabling " + dbName + "-" + (i + 1));
			changeProperty(props, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "/properties");
			Thread.currentThread().sleep(1000L);
			System.out.println("Restarting server " + hostNames[i]);
			serverStartStop(hostNames[i], "start");
			Thread.currentThread().sleep(1000L);
			System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
			System.out.println("Enabling " + dbName + "-" + (i + 1));
			props.put("enabled", "true");
			changeProperty(props, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "/properties");
			Thread.currentThread().sleep(1000L);
		}

		System.out.println("Clearing DB");
		clearDB(port);
	}

	@Test
	public void testBlackListHost() throws Exception {
		try {
			System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
			final AtomicInteger successCount = new AtomicInteger(0);
			final AtomicBoolean containsBLHost = new AtomicBoolean(false);
			final AtomicBoolean failState = new AtomicBoolean(false);

			DatabaseClient dbClient = DatabaseClientFactory.newClient(hostLists.get(3), 8000, "admin", "admin",
					Authentication.DIGEST);
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
				System.out.println(batch.getClient().getHost());
				if (batch.getClient().getHost().equals(hostLists.get(2))
						|| batch.getClient().getHost().equals(hostLists.get(3))) {
					containsBLHost.set(true);
				}
				successCount.getAndAdd(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				failState.set(true);
			});
			for (int j = 0; j < 10000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
			}
			ihb2.flushAndWait();
			Assert.assertFalse(failState.get());
			Assert.assertFalse(containsBLHost.get());
			Assert.assertEquals(10000, successCount.get());
			Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 10000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testStopOneNode() throws Exception {
		Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		final AtomicInteger successCount = new AtomicInteger(0);
		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);
		try {
			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(2);
			ihb2.withThreadCount(99);
			HostAvailabilityListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(40))
					.withMinHosts(2);
			NoResponseListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(40))
					.withMinHosts(2);
			ihb2.onBatchSuccess(batch -> {
				successCount.addAndGet(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				failState.set(true);
				failCount.addAndGet(batch.getItems().length);
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
		Thread.currentThread().sleep(5000L);
		System.out.println("Fail : " + failCount.intValue());
		System.out.println("Success : " + successCount.intValue());
		System.out.println("Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 50000);
	}

	@Test
	public void testRestart() throws Exception {
		Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
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
			});

			writeTicket = dmManager.startJob(ihb2);
			AtomicBoolean isRunning = new AtomicBoolean(true);
			for (int j = 0; j < 20000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
				if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 50 && isRunning.get()) {
					isRunning.set(false);
					serverStartStop(hostNames[hostNames.length - 1], "stop");
					Thread.currentThread().sleep(10000L);
					serverStartStop(hostNames[hostNames.length - 1], "start");
				}
			}
			ihb2.flushAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.currentThread().sleep(5000L);
		System.out.println("Fail : " + failCount.intValue());
		System.out.println("Success : " + successCount.intValue());
		System.out.println("Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 20000);
	}

	@Test
	public void testRepeatedStopOneNode() throws Exception {
		try {
			final AtomicInteger successCount = new AtomicInteger(0);
			final AtomicBoolean failState = new AtomicBoolean(false);
			final AtomicInteger failCount = new AtomicInteger(0);

			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(40);
			ihb2.withThreadCount(3);

			HostAvailabilityListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
					.withMinHosts(2);
			NoResponseListener.getInstance(ihb2).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
					.withMinHosts(2);

			ihb2.onBatchSuccess(batch -> {
				successCount.addAndGet(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				failState.set(true);
				failCount.addAndGet(batch.getItems().length);
			});

			writeTicket = dmManager.startJob(ihb2);
			AtomicBoolean isRunning = new AtomicBoolean(true);
			for (int j = 0; j < 20000; j++) {
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

			System.out.println("Fail : " + failCount.intValue());
			System.out.println("Success : " + successCount.intValue());
			System.out.println(
					"Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

			Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 20000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testStopTwoNodes() throws Exception {
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
			});

			writeTicket = dmManager.startJob(ihb2);
			AtomicBoolean isNode1Running = new AtomicBoolean(true);
			AtomicBoolean isNode2Running = new AtomicBoolean(true);
			for (int j = 0; j < 20000; j++) {
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

			System.out.println("Fail : " + failCount.intValue());
			System.out.println("Success : " + successCount.intValue());
			System.out.println(
					"Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

			Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 20000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testMinHosts() throws Exception {
		System.out.println("Starting min test");
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
		Thread.currentThread().sleep(20000L);
		Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() > 13);
		Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() < 20000);
		System.out.println("Count : " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		System.out.println("Ending min test");
	}

	@Test
	public void testWhiteBlackListNPE() throws Exception {
		try {
			FilteredForestConfiguration forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig())
					.withBlackList(null);
			Assert.assertTrue(1 > 2);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof java.lang.IllegalArgumentException);
		}

		try {
			FilteredForestConfiguration forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig())
					.withWhiteList(null);
			Assert.assertTrue(1 > 2);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof java.lang.IllegalArgumentException);
		}
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