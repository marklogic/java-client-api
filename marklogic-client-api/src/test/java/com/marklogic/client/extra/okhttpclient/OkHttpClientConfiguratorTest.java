package com.marklogic.client.extra.okhttpclient;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OkHttpClientConfiguratorTest {

	/**
	 * Not a test exactly of OkHttp, but rather that multiple threads can add configurators and create clients without
	 * encountering a ConcurrentModificationException, which used to occur due to the list of configurators in
	 * DatabaseClientFactory not being synchronized.
	 * 
	 * @throws Exception
	 */
	@Test
	void multipleThreads() throws Exception {
		final int clientsToCreate = 100;

		ExecutorService service = Executors.newFixedThreadPool(16);
		List<Future> futures = new ArrayList<>();
		for (int i = 0; i < clientsToCreate; i++) {
			futures.add(service.submit(() -> {
				DatabaseClientFactory.addConfigurator((OkHttpClientConfigurator) client -> {
					client.callTimeout(10, TimeUnit.SECONDS);
				});
				Common.newClient().release();
			}));
		}

		int clientsCreated = 0;
		for (Future f : futures) {
			f.get();
			clientsCreated++;
		}
		service.shutdown();

		assertEquals(clientsToCreate, clientsCreated, "The expectation is that many threads can " +
			"add a configurator and create a client at the same time because DatabaseClientFactory " +
			"uses a synchronized list for the configurators. So all of the expected clients should be " +
			"created successfully with no concurrent modification errors occurring.");
	}
}
