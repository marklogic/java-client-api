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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

public class Common {
	final public static String USERNAME = "rest-writer";
	final public static String PASSWORD = "x";
	final public static String ADMIN_USERNAME = "rest-admin";
	final public static String ADMIN_PASSWORD = "x";
	final public static String HOST     = "localhost";
	final public static int    PORT     = 8010;

	static DatabaseClient client;
	static void connect() {
		if (client != null) {
			client.release();
			client = null;
		}
		client = DatabaseClientFactory.connect(
			Common.HOST, Common.PORT, Common.USERNAME, Common.PASSWORD, Authentication.DIGEST
			);
	}

	static void connectAdmin() {
		client = DatabaseClientFactory.connect(
				Common.HOST, Common.PORT, Common.ADMIN_USERNAME, Common.ADMIN_PASSWORD, Authentication.DIGEST
				);
	}
	static void release() {
		client = null;
	}

	static byte[] streamToBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		byte[] b = new byte[1000];
		int len = 0;
		while (((len=is.read(b)) != -1)) {
			baos.write(b, 0, len);
		}
		return baos.toByteArray();
	}
	static String readerToString(Reader r) throws IOException {
		StringWriter w = new StringWriter(); 
		char[] cbuf = new char[1000];
		int len = 0;
		while (((len=r.read(cbuf)) != -1)) {
			w.write(cbuf, 0, len);
		}
		r.close();
		String result = w.toString();
		w.close();
		return result;
	}
	// the testFile*() methods get a file in the src/test/resources directory
	static String testFileToString(String filename) throws IOException {
		return readerToString(testFileToReader(filename));
	}
	static Reader testFileToReader(String filename) {
		return new InputStreamReader(testFileToStream(filename));
	}
	static InputStream testFileToStream(String filename) {
		return ResourceExtensionsTest.class.getClassLoader().getResourceAsStream(filename);
	}
}
