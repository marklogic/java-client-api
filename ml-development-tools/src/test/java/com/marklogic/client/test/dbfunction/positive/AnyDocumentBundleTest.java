/*
 * Copyright (c) 2019 MarkLogic Corporation
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
package com.marklogic.client.test.dbfunction.positive;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.impl.BaseProxy;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.test.dbfunction.DBFunctionTestUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class AnyDocumentBundleTest {
   final static private String[] uris = {"/test/anyDocument1.json", "/test/anyDocument1.xml"};
   final static private Format[] formats = {Format.JSON, Format.XML};
   // docs must align with the formats array
   final static private String[] docs = {
           "{\"key1\":\"value1\"}",
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<elem1 attr1=\"attval1\">text1</elem1>"
   };
   final static private AnyDocumentBundle testObj = AnyDocumentBundle.on(DBFunctionTestUtil.db);

/* TODO:
    positive test:
        mapped StringHandle
    negative test:
        mapped to a class that's not a handle for single or multiple param or return value
    */

   @Test
   public void sendReceiveOptionalJSONDocTest() throws IOException {
       testDocImpl(uris[0], Format.JSON, docs[0], testObj::sendReceiveOptionalDoc);
   }
   @Test
   public void sendReceiveOptionalXMLDocTest() throws IOException {
      testDocImpl(uris[1], Format.XML, docs[1], testObj::sendReceiveOptionalDoc);
   }
   @Test
   public void sendReceiveRequiredJSONDocTest() throws IOException {
      testDocImpl(uris[0], Format.JSON, docs[0], testObj::sendReceiveRequiredDoc);
   }
   @Test
   public void sendReceiveRequiredXMLDocTest() throws IOException {
      testDocImpl(uris[1], Format.XML, docs[1], testObj::sendReceiveRequiredDoc);
   }
   @Test
   public void sendReceiveOptionalNullUriTest() throws IOException {
      // send null uri with non-null handle to optional endpoint
      testDocImpl(null, Format.JSON, docs[0], testObj::sendReceiveOptionalDoc);
   }
   @Test
   public void sendReceiveOptionalNullDocTest() throws IOException {
      // send non-null uri with null handle to optional endpoint
      testDocImpl(uris[0], null, null, testObj::sendReceiveOptionalDoc);
   }
   @Test
   public void sendReceiveOptionalNullUriDocTest() throws IOException {
      // send null uri and null handle to optional endpoint
      testDocImpl(null, null, null, testObj::sendReceiveOptionalDoc);
   }
   @Test
   public void sendReceiveRequiredNullDocTest() throws IOException {
      // negative test: send non-null uri with null handle to required endpoint
      try {
         testDocImpl(uris[0], null, null, testObj::sendReceiveRequiredDoc);
         fail("no exception for required parameter with null value");
      } catch(BaseProxy.RequiredParamException e) {
         assertEquals("null value for required parameter: doc", e.getMessage());
      }
   }
   @Test
   public void sendReceiveOptionalInvalidFormatDocTest() throws IOException {
      // negative test: send handle with invalid format
      try {
         testDocImpl(uris[0], formats[1], docs[0], testObj::sendReceiveOptionalDoc);
         fail("no exception for invalid format");
      } catch(FailedRequestException e) {
         assertEquals(400, e.getServerStatusCode());
         assertEquals("XDMP-DOCROOTTEXT", e.getServerMessageCode());
      }
   }

   @Test
   public void sendReceiveAnyTwoDocsTest() throws IOException {
      testDocsImpl(uris, formats, docs, testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveAnyOneJSONDocTest() throws IOException {
      testDocsImpl(uris[0], formats[0], docs[0], testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveAnyOneXMLDocTest() throws IOException {
      testDocsImpl(uris[1], formats[1], docs[1], testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveManyTwoDocsTest() throws IOException {
      testDocsImpl(uris, formats, docs, testObj::sendReceiveManyDocs);
   }
   @Test
   public void sendReceiveManyOneJSONDocTest() throws IOException {
      testDocsImpl(uris[0], formats[0], docs[0], testObj::sendReceiveManyDocs);
   }
   @Test
   public void sendReceiveManyOneXMLDocTest() throws IOException {
      testDocsImpl(uris[1], formats[1], docs[1], testObj::sendReceiveManyDocs);
   }
   @Test
   public void sendReceiveAnyNullUrisTest() throws IOException {
      testDocsImpl(null, formats, docs, testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveAnyNullDocsTest() throws IOException {
      testDocsImpl(uris, null, null, testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveAnyNullUrisDocsTest() throws IOException {
      testDocsImpl(null, (Format[]) null, null, testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveManyNullDocsTest() throws IOException {
      try {
         testDocsImpl(uris, null, null, testObj::sendReceiveManyDocs);
         fail("no exception for required parameter with null value");
      } catch(FailedRequestException e) {
         assertEquals(400, e.getServerStatusCode());
         assertEquals("XDMP-ENDPOINTNULLABLE", e.getServerMessageCode());
         assertTrue(e.getServerMessage().contains(" docs ") && e.getServerMessage().contains(" parameter "));
      }
   }
   @Test
   public void sendReceiveAnyInvalidFormatDocsTest() throws IOException {
      // negative test: send handle with invalid format
      try {
         testDocsImpl(uris[1], formats[0], docs[1], testObj::sendReceiveAnyDocs);
         fail("no exception for invalid format");
      } catch(FailedRequestException e) {
         assertEquals(400, e.getServerStatusCode());
         assertEquals("XDMP-JSONDOC", e.getServerMessageCode());
      }
   }

   private void testDocImpl(String uri, Format format, String doc, BiFunction<String, InputStreamHandle, InputStreamHandle> caller) {
      InputStreamHandle inputHandle = null;
      if (doc != null) {
         inputHandle = new InputStreamHandle();
         if (format != null) {
            inputHandle.setFormat(format);
         }
         inputHandle.fromBuffer(doc.getBytes());
      }

      InputStreamHandle outputHandle = caller.apply(uri, inputHandle);

      if (doc != null) {
         assertEquals(format, outputHandle.getFormat());
         assertEquals(doc, new String(outputHandle.toBuffer()).trim());
      } else {
         assertNull(outputHandle);
      }
   }

   private void testDocsImpl(
           String uri, Format format, String doc, BiFunction<Stream<String>,Stream<InputStreamHandle>,Stream<InputStreamHandle>> caller
   ) {
      testDocsImpl(new String[]{uri}, new Format[]{format}, new String[]{doc}, caller);
   }
   private void testDocsImpl(
           String[] uris, Format[] formats, String[] docs, BiFunction<Stream<String>,Stream<InputStreamHandle>,Stream<InputStreamHandle>> caller
   ) {
      Stream<String> uriStream = (uris == null || uris.length == 0) ? Stream.empty() : Stream.of(uris);

      Stream<InputStreamHandle> inputHandleStream;
      if (docs == null || docs.length == 0) {
         inputHandleStream = Stream.empty();
      } else {
         InputStreamHandle[] inputHandles = new InputStreamHandle[docs.length];
         for (int i=0; i < docs.length; i++) {
            InputStreamHandle inputHandle = new InputStreamHandle();
            if (formats != null && i < formats.length) {
               inputHandle.setFormat(formats[i]);
            }
            inputHandle.fromBuffer(docs[i].getBytes());
            inputHandles[i] = inputHandle;
         }
         inputHandleStream = Stream.of(inputHandles);
      }

      Stream<InputStreamHandle> outputStream = caller.apply(uriStream, inputHandleStream);

      InputStreamHandle[] outputHandles = outputStream.toArray(InputStreamHandle[]::new);
      if (docs == null || docs.length == 0) {
         assertEquals(0, outputHandles.length);
      } else {
         assertEquals(docs.length, outputHandles.length);
         for (int i=0; i < docs.length; i++) {
            assertEquals(formats[i], outputHandles[i].getFormat());
            assertEquals(docs[i], new String(outputHandles[i].toBuffer()).trim());
         }
      }
   }
}
