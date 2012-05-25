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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.marklogic.client.config.CountedDistinctValue;
import com.marklogic.client.io.ValuesHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ValuesHandleTest {
	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);
	
    @BeforeClass
    public static void beforeClass() {
        Common.connectAdmin();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testValuesHandle() throws IOException, ParserConfigurationException, SAXException {
        File f = new File("src/test/resources/values.xml");
        FileInputStream is = new FileInputStream(f);

        MyValuesHandle v = new MyValuesHandle();

        v.parseTestData(is);

        assertTrue("Name should be 'size'", "size".equals(v.getName()));
        assertEquals("Type should be 'Long'", Long.class, v.getType());

        CountedDistinctValue dv[] = v.getValues();

        assertEquals("There should be 8 values", 8, dv.length);
        assertEquals("Frequency should be 1", 1, dv[0].getCount());
        assertEquals("Value should be 815", (long) 815, (long) dv[0].get(Long.class));
    }

    public class MyValuesHandle extends ValuesHandle {
        public void parseTestData(InputStream stream) {
            receiveContent(stream);
        }
    }
}
