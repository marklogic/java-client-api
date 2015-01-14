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
package com.marklogic.client.test.example.cookbook;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;

import com.marklogic.client.example.cookbook.DocumentRead;

public class DocumentReadTest {
	@Test
	public void testMain() {
		boolean succeeded = false;
		try {
			DocumentRead.main(new String[0]);
			succeeded = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		assertTrue("DocumentRead example failed", succeeded);
	}
}
