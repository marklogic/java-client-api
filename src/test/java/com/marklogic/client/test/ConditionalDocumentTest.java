/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DocumentDescriptor;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.Format;
import com.marklogic.client.ServerConfigurationManager;
import com.marklogic.client.ServerConfigurationManager.Policy;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.impl.FailedRequest;
import com.marklogic.client.io.StringHandle;

public class ConditionalDocumentTest {
	static DatabaseClient             adminClient;
	static ServerConfigurationManager serverConfig;

	@BeforeClass
	public static void beforeClass() {
		Common.connectAdmin();
		serverConfig = Common.client.newServerConfigManager();
		serverConfig.readConfiguration();
		serverConfig.setContentVersionRequests(Policy.REQUIRED);
		serverConfig.writeConfiguration();
		adminClient = Common.client;
		Common.release();

		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
		serverConfig.setContentVersionRequests(Policy.NONE);
		serverConfig.writeConfiguration();
		adminClient.release();
	}

	@Test
	public void testConditional() throws SAXException, IOException {
		String docId = "/test/conditional1.xml";
		long badVersion = 11111;

		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

		DocumentDescriptor desc = docMgr.exists(docId);
		if (desc != null) {
			docMgr.delete(desc);
		}

		desc = docMgr.newDescriptor(docId);
		desc.setFormat(Format.XML);
		desc.setVersion(badVersion);

		StringHandle contentHandle = new StringHandle().with(GenericDocumentTest.content);

		Exception ex = null;
		int statusCode = 0;
		try {
			docMgr.write(desc, contentHandle);
		} catch (FailedRequestException e) {
			FailedRequest failreq = e.getFailedRequest();
			if (failreq != null)
				statusCode = failreq.getStatusCode();
			ex = e;
		}
		assertTrue("Write with bad version succeeded", ex != null);
		assertTrue("Write with bad version had wrong error", statusCode == 412);

		desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
		docMgr.write(desc, contentHandle);

		String result = docMgr.read(desc, new StringHandle()).get();
		long goodVersion = desc.getVersion();
		assertTrue("Failed to read version", goodVersion != DocumentDescriptor.UNKNOWN_VERSION);
		assertXMLEqual("Failed to read document content",result,GenericDocumentTest.content);

		desc.setVersion(badVersion);
		assertTrue("Read with bad version did not get content",
				docMgr.read(desc, new StringHandle()).get() != null);

		desc.setVersion(goodVersion);
		assertTrue("Read with good version did not skip content",
				docMgr.read(desc, new StringHandle()) == null);

		ex = null;
		statusCode = 0;
		try {
			desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
			docMgr.write(desc, contentHandle);
		} catch (FailedRequestException e) {
			FailedRequest failreq = e.getFailedRequest();
			if (failreq != null)
				statusCode = failreq.getStatusCode();
System.out.println(e.getFailedRequest().getStatusCode()+" "+e.getMessage());
			ex = e;
		}
		assertTrue("Overwrite without version succeeded", ex != null);
		assertTrue("Write with bad version had wrong error", statusCode == 403);

		ex = null;
		statusCode = 0;
		try {
			desc.setVersion(badVersion);
			docMgr.write(desc, contentHandle);
		} catch (FailedRequestException e) {
			FailedRequest failreq = e.getFailedRequest();
			if (failreq != null)
				statusCode = failreq.getStatusCode();
System.out.println(e.getFailedRequest().getStatusCode()+" "+e.getMessage());
			ex = e;
		}
		assertTrue("Overwrite with bad version succeeded", ex != null);
		assertTrue("Write with bad version had wrong error", statusCode == 412);

		desc.setVersion(goodVersion);
		docMgr.write(desc, contentHandle);

		desc = docMgr.exists(docId);
		assertTrue("Exists did not get version", desc.getVersion() != DocumentDescriptor.UNKNOWN_VERSION);
		assertTrue("Overwrite did not change version", goodVersion != desc.getVersion());
		goodVersion = desc.getVersion();

		ex = null;
		statusCode = 0;
		try {
			desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
			docMgr.delete(desc);
		} catch (FailedRequestException e) {
			FailedRequest failreq = e.getFailedRequest();
			if (failreq != null)
				statusCode = failreq.getStatusCode();
System.out.println(e.getFailedRequest().getStatusCode()+" "+e.getMessage());
			ex = e;
		}
		assertTrue("Delete without version succeeded", ex != null);
		assertTrue("Write with bad version had wrong error", statusCode == 403);

		ex = null;
		// TODO: statusCode
		try {
			desc.setVersion(badVersion);
			docMgr.delete(desc);
		} catch (FailedRequestException e) {
			ex = e;
		}
		assertTrue("Delete with bad version succeeded", ex != null);

		desc.setVersion(goodVersion);
		docMgr.delete(desc);
	}
}
