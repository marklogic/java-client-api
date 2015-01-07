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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.junit.Test;

import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
public class HandleAccessorTest {
	@Test
    public void testContentAsString() throws URISyntaxException, IOException {
        // I'm purposely using a string with a non-ascii character to test 
        // charset issues
        String hola = "¡Hola!";
        System.out.println("Default Java Charset: " + Charset.defaultCharset());
        assertEquals("String content mismatch", hola, 
            HandleAccessor.contentAsString(new StringHandle(hola)));
        assertEquals("byte[] content mismatch", hola, 
            HandleAccessor.contentAsString(new BytesHandle(hola.getBytes("UTF-8"))));
        URL filePath = this.getClass().getClassLoader().getResource("hola.txt");
        assertEquals("Reader content mismatch", hola, 
            HandleAccessor.contentAsString(new ReaderHandle(new StringReader(hola))));
        assertEquals("File content mismatch", hola, 
            HandleAccessor.contentAsString(new FileHandle(new File(filePath.toURI()))));
        assertEquals("InputStream content mismatch", hola, 
            HandleAccessor.contentAsString(new InputStreamHandle(filePath.openStream())));
    }
}
