package com.marklogic.client.datamovement.functionaltests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
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
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.ApplyTransformListener;
import com.marklogic.client.datamovement.ApplyTransformListener.ApplyResult;
import com.marklogic.client.datamovement.BatchFailureListener;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.HostAvailabilityListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.NoResponseListener;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.StructuredQueryBuilder;

public class QBFailover extends BasicJavaClientREST {
	private static String dbName = "QBFailover";
	private static DataMovementManager dmManager = null;
	private static DataMovementManager tempMgr = null;
	private static DatabaseClient dbClient;
	private static DatabaseClient evalClient;
	private static String host = null;
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";
	private static final String OS = System.getProperty("os.name").toLowerCase();
	private static StringHandle stringHandle;
	private static DocumentMetadataHandle meta2;
	private static String stringTriple;
	private static final String query1 = "fn:count(fn:doc())";
	private static String[] hostNames;
	private static String dataDir;
	private static JobTicket ticket;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		loadGradleProperties();
		server = getRestAppServerName();
	    port = getRestAppServerPort();
	    
		host = getRestAppServerHostName();
		hostNames = getHosts();
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
		createDB(dbName);
		Thread.currentThread().sleep(500L);
		int index = new Random().nextInt(hostLists.size());
		dbClient = getDatabaseClientOnDatabase(hostLists.get(index), port, dbName, user, password, Authentication.DIGEST);
		evalClient = DatabaseClientFactory.newClient(host, port, dbName, user, password, Authentication.DIGEST);
		System.out.println("Connected to: " + dbClient.getHost());
		dmManager = dbClient.newDataMovementManager();
		tempMgr = evalClient.newDataMovementManager();

		Map<String, String> props = new HashMap<>();
		String version = String.valueOf(evalClient.newServerEval().xquery("xquery version \"1.0-ml\"; xdmp:version()")
				.eval().next().getString().charAt(0));
		if (OS.indexOf("win") >= 0) {
			Properties prop = new Properties();
			InputStream input = null;
			String location = null;
			String seperator = File.separator;
			try {
				input = new FileInputStream(System.getProperty("user.dir") + seperator + ".." + seperator + ".."
						+ seperator + "qa" + seperator + "failover-location.properties");
				prop.load(input);
				location = prop.getProperty("location");
				System.out.println(prop.getProperty("location"));
			} catch (IOException ex) {
				ex.printStackTrace();
				Assert.fail("Forest location file not found");
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
						Assert.fail("Forest location file not found");
					}
				}
			}
			dataDir = location + "/space/dmsdk-failover/win/" + version + "/temp-";
		} else if (OS.indexOf("nux") >= 0) {
			dataDir = "/project/qa-netapp/space/dmsdk-failover/linux/" + version + "/temp-";
		} else if (OS.indexOf("mac") >= 0) {
			dataDir = "/project/qa-netapp/space/dmsdk-failover/mac/" + version + "/temp-";
		} else {
			Assert.fail("Unsupported platform");
		}
		
		for (int i = 0; i < hostNames.length; i++) {
			if (i != 0) {
				createForest(dbName + "-" + (i + 1), hostNames[i], dataDir + (i + 1), hostNames[0]);
			} else {
				createForest(dbName + "-" + (i + 1), hostNames[i], dataDir + (i + 1), null);

			}
			props.put("database", dbName);
			props.put("state", "attach");
			postRequest(null, props, "/manage/v2/forests/" + dbName + "-" + (i + 1));

			Thread.currentThread().sleep(500L);
		}
		props = new HashMap<>();
		props.put("journaling", "strict");
		changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
		Thread.currentThread().sleep(500L);
		associateRESTServerWithDB(server, dbName);

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
			if (!isRunning(hostNames[i])) {
				serverStartStop(hostNames[i], "start");
			}
			Assert.assertTrue(isRunning(hostNames[i]));
		}
		if (!(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0)) {
			clearDB(port);
		}
		Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		ForestConfiguration fc = dmManager.readForestConfig();
		Forest[] f = fc.listForests();
		f = (Forest[]) Arrays.stream(f).filter(x -> x.getDatabaseName().equals(dbName)).collect(Collectors.toList())
				.toArray(new Forest[hostNames.length]);
		Assert.assertEquals(f.length, hostNames.length);
		Assert.assertEquals(f.length, 3L);
		addDocs();
		Assert.assertTrue(evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 20000);
	}

	@After
	public void tearDown() throws Exception {
		Map<String, String> props = new HashMap<>();
		props.put("database", dbName);
		System.out.println("Restarting servers");
		for (int i = hostNames.length - 1; i >= 1; i--) {
			props.put("enabled", "false");
			System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm:ss").format(new Date()));
			System.out.println("Disabling " + dbName + "-" + (i + 1));
			changeProperty(props, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "/properties");
			Thread.currentThread().sleep(1000L);
			System.out.println("Restarting server: " + hostNames[i]);
			try {
				serverStartStop(hostNames[i], "start");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.currentThread().sleep(1000L);
			System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm:ss").format(new Date()));
			System.out.println("Enabling " + dbName + "-" + (i + 1));
			props.put("enabled", "true");
			changeProperty(props, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "/properties");
			Thread.currentThread().sleep(1000L);
		}
		Thread.currentThread().sleep(3000L);
		System.out.println("Clearin DB");
		clearDB(port);
	}

	@Test(timeout = 450000)
	public void testStopOneNode() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isRunning = new AtomicBoolean(true);

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(1).withThreadCount(55);

		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
				.withMinHosts(2);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
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

		assertEquals("document count", 20000, success.intValue());
		assertEquals("document count", 0, failure.intValue());
	}

	@Test(timeout = 450000)
	public void testRestart() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);
		
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
				Thread.currentThread().sleep(24000L);
				serverStartStop(hostNames[hostNames.length - 1], "start");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		System.out.println("Success " + success.intValue());
		System.out.println("Failure " + failure.intValue());

		assertEquals("document count", 20000, success.intValue());
		assertEquals("document count", 0, failure.intValue());
	}

	@Test(timeout = 450000)
	public void testRepeatedStopOneNode() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);
		AtomicBoolean isRunning = new AtomicBoolean(true);

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(15).withThreadCount(2);
		NoResponseListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
				.withMinHosts(2);
		HostAvailabilityListener.getInstance(batcher).withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
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
				Thread.currentThread().sleep(18000L);
				serverStartStop(hostNames[hostNames.length - 1], "start");
				Thread.currentThread().sleep(6000L);
				serverStartStop(hostNames[hostNames.length - 1], "stop");
			}
		}
		batcher.awaitCompletion();
		dmManager.stopJob(ticket);
		System.out.println("Success " + success.intValue());
		System.out.println("Failure " + failure.intValue());

		assertEquals("document count", 20000, success.intValue());
		assertEquals("document count", 0, failure.intValue());
	}

	@Test(timeout = 450000)
	public void testMinNodes() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);
		
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
		Assert.assertTrue(success.intValue() < 20000);
	}

	@Test(timeout = 450000)
	public void testStopTwoNodes() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);
		
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
			System.out.println("Success " + success.intValue());
			assertEquals("document count", 20000, success.intValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(timeout = 450000)
	public void xQueryMasstransformReplace() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);
		
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
		Assert.assertTrue(passed.get());
		assertEquals("document count", 20000, modified.intValue());
		assertEquals("document count", 20000, success.intValue());
		assertEquals("document count", 0, skipped.intValue());

	}

	@Test(timeout = 450000)
	public void xQueryMasstransformReplaceTwoNodes() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);
		
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
		Assert.assertTrue(passed.get());
		assertEquals("document count", 20000, modified.intValue());
		assertEquals("document count", 20000, success.intValue());
		assertEquals("document count", 0, skipped.intValue());
	}

	@Test(timeout = 450000)
	public void xQueryMasstransformReplaceRepeated() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);
		
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
		Assert.assertTrue(passed.get());
		assertEquals("document count", 20000, modified.intValue());
		assertEquals("document count", 20000, success.intValue());
		assertEquals("document count", 0, failure.intValue());
	}

	@Test(timeout = 450000)
	public void massDeleteConsistentSnapShot() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);
		
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

		props.put("merge-timestamp", "0");
		changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
		System.out.println("Count: " + evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertEquals(0, evalClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
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
			if (body.contains("Healthy")) {
				return true;
			}

		} catch (Exception e) {
			return false;
		}
		return false;
	}
}