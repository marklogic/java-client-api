/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client;

import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.io.InputStreamHandle;

public class ReadTest {

	public static void main(String[] args) {

		try (DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, new DatabaseClientFactory.DigestAuthContext("admin", "admin"))) {
			long start = System.currentTimeMillis();
			System.out.println("Reading!");
			try (DocumentPage page = client.newDocumentManager().read("/big.jar")) {
				InputStreamHandle handle = page.next().getContent(new InputStreamHandle());
				System.out.println("Read it! " + (System.currentTimeMillis() - start) + "ms");
			}
		}
	}
}
