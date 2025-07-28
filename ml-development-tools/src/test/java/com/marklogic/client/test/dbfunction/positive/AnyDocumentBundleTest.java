/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dbfunction.positive;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.impl.BaseProxy;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.BufferableContentHandle;
import com.marklogic.client.test.dbfunction.DBFunctionTestUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;
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

   @Test
   public void sendReceiveOptionalJSONDocTest() {
       testInputStreamImpl(uris[0], Format.JSON, docs[0], testObj::sendReceiveOptionalDoc);
   }
   @Test
   public void sendReceiveOptionalXMLDocTest() {
      testInputStreamImpl(uris[1], Format.XML, docs[1], testObj::sendReceiveOptionalDoc);
   }
   @Test
   public void sendReceiveOptionalNullUriTest() {
      // send null uri with non-null handle to optional endpoint
      testInputStreamImpl(null, Format.JSON, docs[0], testObj::sendReceiveOptionalDoc);
   }
   @Test
   public void sendReceiveOptionalNullDocTest() {
      // send non-null uri with null handle to optional endpoint
      testInputStreamImpl(uris[0], null, null, testObj::sendReceiveOptionalDoc);
   }
   @Test
   public void sendReceiveOptionalNullUriDocTest() {
      // send null uri and null handle to optional endpoint
      testInputStreamImpl(null, null, null, testObj::sendReceiveOptionalDoc);
   }

   @Test
   public void sendReceiveRequiredJSONDocTest() {
      testInputStreamImpl(uris[0], Format.JSON, docs[0], testObj::sendReceiveRequiredDoc);
   }
   @Test
   public void sendReceiveRequiredXMLDocTest() {
      testInputStreamImpl(uris[1], Format.XML, docs[1], testObj::sendReceiveRequiredDoc);
   }
   @Test
   public void sendReceiveRequiredNullDocTest() {
      // negative test: send non-null uri with null handle to required endpoint
      try {
         testInputStreamImpl(uris[0], null, null, testObj::sendReceiveRequiredDoc);
         fail("no exception for required parameter with null value");
      } catch(BaseProxy.RequiredParamException e) {
         assertEquals("null value for required parameter: doc", e.getMessage());
      }
   }
   @Test
   public void sendReceiveOptionalInvalidFormatDocTest() {
      // negative test: send handle with invalid format
      try {
         testInputStreamImpl(uris[0], formats[1], docs[0], testObj::sendReceiveOptionalDoc);
         fail("no exception for invalid format");
      } catch(FailedRequestException e) {
         assertEquals(400, e.getServerStatusCode());
         assertEquals("XDMP-DOCROOTTEXT", e.getServerMessageCode());
      }
   }

   @Test
   public void sendReceiveMappedJSONDocTest() {
      testStringImpl(uris[0], Format.JSON, docs[0], testObj::sendReceiveMappedDoc);
   }
   @Test
   public void sendReceiveMappedXMLDocTest() {
      testStringImpl(uris[1], Format.XML, docs[1], testObj::sendReceiveMappedDoc);
   }
   @Test
   public void sendReceiveMappedNullUriTest() {
      // send null uri with non-null handle to optional endpoint
      testStringImpl(null, Format.JSON, docs[0], testObj::sendReceiveMappedDoc);
   }
   @Test
   public void sendReceiveMappedNullDocTest() {
      // send non-null uri with null handle to optional endpoint
      testStringImpl(uris[0], null, null, testObj::sendReceiveMappedDoc);
   }
   @Test
   public void sendReceiveMappedNullUriDocTest() {
      // send null uri and null handle to optional endpoint
      testStringImpl(null, null, null, testObj::sendReceiveMappedDoc);
   }

   @Test
   public void sendReceiveAnyTwoDocsTest() {
      testDocsImpl(uris, formats, docs, testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveAnyOneJSONDocTest() {
      testDocsImpl(uris[0], formats[0], docs[0], testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveAnyOneXMLDocTest() {
      testDocsImpl(uris[1], formats[1], docs[1], testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveAnyNullUrisTest() {
      testDocsImpl(null, formats, docs, testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveAnyNullDocsTest() {
      testDocsImpl(uris, null, null, testObj::sendReceiveAnyDocs);
   }
   @Test
   public void sendReceiveAnyNullUrisDocsTest() {
      testDocsImpl(null, (Format[]) null, null, testObj::sendReceiveAnyDocs);
   }

   @Test
   public void sendReceiveManyTwoDocsTest() {
      testDocsImpl(uris, formats, docs, testObj::sendReceiveManyDocs);
   }
   @Test
   public void sendReceiveManyOneJSONDocTest() {
      testDocsImpl(uris[0], formats[0], docs[0], testObj::sendReceiveManyDocs);
   }
   @Test
   public void sendReceiveManyOneXMLDocTest() {
      testDocsImpl(uris[1], formats[1], docs[1], testObj::sendReceiveManyDocs);
   }
   @Test
   public void sendReceiveManyNullDocsTest() {
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
   public void sendReceiveAnyInvalidFormatDocsTest() {
      // negative test: send handle with invalid format
      try {
         testDocsImpl(uris[1], formats[0], docs[1], testObj::sendReceiveAnyDocs);
         fail("no exception for invalid format");
      } catch(FailedRequestException e) {
         assertEquals(400, e.getServerStatusCode());
         assertEquals("XDMP-JSONDOC", e.getServerMessageCode());
      }
   }

   private void testStringImpl(String uri, Format format, String doc, BiFunction<String, StringHandle, StringHandle> caller) {
      testDocImpl(uri, format, (doc == null) ? null : new StringHandle(doc), caller);
   }
   private void testInputStreamImpl(String uri, Format format, String doc, BiFunction<String, InputStreamHandle, InputStreamHandle> caller) {
      testDocImpl(uri, format, (doc == null) ? null : new InputStreamHandle(new ByteArrayInputStream(doc.getBytes())), caller);
   }
   private <T extends BufferableContentHandle<?,?>> void testDocImpl(String uri, Format format, T inputHandle, BiFunction<String, T, T> caller) {
      if (inputHandle != null && format != null) {
         BaseHandle<?, ?> inputBase = (BaseHandle<?, ?>) inputHandle;
         inputBase.setFormat(format);
      }
      testDocImpl(uri, inputHandle, caller);
   }
   private <T extends BufferableContentHandle<?,?>> void testDocImpl(String uri, T inputHandle, BiFunction<String, T, T> caller) {
      T outputHandle = caller.apply(uri, inputHandle);

      if (inputHandle != null) {
         assertNotNull(outputHandle);
         BaseHandle<?,?> inputBase = (BaseHandle<?,?>) inputHandle;
         BaseHandle<?,?> outputBase = (BaseHandle<?,?>) outputHandle;
         assertEquals(inputBase.getFormat(), outputBase.getFormat());
         assertEquals(new String(inputHandle.toBuffer()).trim(), new String(outputHandle.toBuffer()).trim());
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
