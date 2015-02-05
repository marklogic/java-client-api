/*
 * Copyright 2012-2015 MarkLogic Corporation
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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

public class Common {
	final public static String USERNAME = "rest-writer";
	final public static String PASSWORD = "x";
	final public static String ADMIN_USERNAME = "rest-admin";
	final public static String ADMIN_PASSWORD = "x";
	final public static String EVAL_USERNAME = "rest-evaluator";
	final public static String EVAL_PASSWORD = "x";
	final public static String HOST     = "localhost";
	final public static int    PORT     = 8012;

	public static DatabaseClient client;
	public static void connect() {
		if (client != null) {
			client.release();
			client = null;
		}
		client = newClient();
	}

	public static void connectAdmin() {
		client = newAdminClient();
	}
	public static void connectEval() {
		client = newEvalClient();
	}
	public static DatabaseClient newClient() {
		return DatabaseClientFactory.newClient(
				Common.HOST, Common.PORT, Common.USERNAME, Common.PASSWORD, Authentication.DIGEST
				);
	}
	public static DatabaseClient newEvalClient() {
		return DatabaseClientFactory.newClient(
				Common.HOST, Common.PORT, Common.EVAL_USERNAME, Common.EVAL_PASSWORD, Authentication.DIGEST
				);
	}
	public static DatabaseClient newAdminClient() {
		return DatabaseClientFactory.newClient(
				Common.HOST, Common.PORT, Common.ADMIN_USERNAME, Common.ADMIN_PASSWORD, Authentication.DIGEST
				);
	}
	public static void release() {
		client = null;
	}

	public static byte[] streamToBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		byte[] b = new byte[1000];
		int len = 0;
		while (((len=is.read(b)) != -1)) {
			baos.write(b, 0, len);
		}
		return baos.toByteArray();
	}
	public static String readerToString(Reader r) throws IOException {
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
	public static String testFileToString(String filename) throws IOException {
		return testFileToString(filename, null);
	}
	public static String testFileToString(String filename, String encoding) throws IOException {
		return readerToString(testFileToReader(filename, encoding));
	}
	public static Reader testFileToReader(String filename) {
		return testFileToReader(filename, null);
	}
	public static Reader testFileToReader(String filename, String encoding) {
		try {
			return (encoding != null) ?
					new InputStreamReader(testFileToStream(filename), encoding) :
					new InputStreamReader(testFileToStream(filename));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	public static InputStream testFileToStream(String filename) {
		return ResourceExtensionsTest.class.getClassLoader().getResourceAsStream(filename);
	}
	public static String testDocumentToString(Document document) {
		try {
			return ((DOMImplementationLS) DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.getDOMImplementation()
					).createLSSerializer().writeToString(document);
		} catch (DOMException e) {
			throw new RuntimeException(e);
		} catch (LSException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	public static Document testStringToDocument(String document) {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					new InputSource(new StringReader(document)));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
