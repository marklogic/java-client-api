package com.marklogic.client.fastfunctest.datamovement;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExportBatchesTest extends AbstractFunctionalTest {

	List<String> testDocUris;

	@BeforeEach
	void beforeEach() {
		deleteDocuments(client);
		testDocUris = writeJsonDocs(20, "ExportBatchesTest");
	}

	@Test
	void simplePageConsumer() {
		AtomicInteger pageCount = new AtomicInteger();
		AtomicInteger docCount = new AtomicInteger();

		ExportListener listener = new ExportListener().onDocumentPageReady(documentPage -> {
			pageCount.incrementAndGet();
			while (documentPage.hasContent()) {
				documentPage.next();
				docCount.incrementAndGet();
			}
		});

		runJob(listener, 5);
		assertEquals(4, pageCount.get(), "Should get 4 pages of 5 docs each");
		assertEquals(20, docCount.get());
	}

	@Test
	void consumerThrowsException() {
		AtomicInteger failureCount = new AtomicInteger();

		ExportListener listener = new ExportListener()
			.onDocumentPageReady(documentPage -> {
				throw new RuntimeException("Intentional error");
			})
			.onFailure(((batch, throwable) -> failureCount.incrementAndGet()));

		runJob(listener, 5);
		assertEquals(4, failureCount.get(), "The failure listener should have been invoked once for each batch, " +
			"and batch should have failed.");
	}

	@Test
	void documentListenerAlreadySet() {
		ExportListener listener = new ExportListener().onDocumentReady(doc -> doc.getUri());
		IllegalStateException ex = assertThrows(IllegalStateException.class,
			() -> listener.onDocumentPageReady(page -> page.next()));
		assertEquals("Cannot call onDocumentPageReady if a listener has already been added via onDocumentReady",
			ex.getMessage(), "Both listeners cannot be set because the DocumentPage can only be iterated through once.");
	}

	@Test
	void documentPageListenerAlreadySet() {
		ExportListener listener = new ExportListener().onDocumentPageReady(page -> page.next());
		IllegalStateException ex = assertThrows(IllegalStateException.class,
			() -> listener.onDocumentReady(doc -> doc.getUri()));
		assertEquals("Cannot call onDocumentReady if a listener has already been set via onDocumentPageReady",
			ex.getMessage(), "Both listeners cannot be set because the DocumentPage can only be iterated through once.");
	}

	private void runJob(ExportListener listener, int batchSize) {
		DataMovementManager dmm = client.newDataMovementManager();
		QueryBatcher qb = dmm.newQueryBatcher(testDocUris.iterator())
			.withBatchSize(batchSize)
			.onUrisReady(listener);
		dmm.startJob(qb);
		qb.awaitCompletion();
		dmm.stopJob(qb);
	}
}
