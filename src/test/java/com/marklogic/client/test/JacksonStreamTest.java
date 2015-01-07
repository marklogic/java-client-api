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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.test.Common;

public class JacksonStreamTest {
    private static final String ORDER_FILE = "sampleOrder.json";
    private static final String ORDER_URI = "sampleOrder.json";
    private static JSONDocumentManager docMgr;

    @BeforeClass
    public static void beforeClass() {
        Common.connect();
        docMgr = Common.client.newJSONDocumentManager();
        setup();
    }
    @AfterClass
    public static void afterClass() {
        cleanUp();
        Common.release();
    }

    
    public class OrderItem {
        private String productId;
        private int quantity;
        private float itemCostUSD;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public float getItemCostUSD() {
            return itemCostUSD;
        }

        public void setItemCostUSD(float itemCostUSD) {
            this.itemCostUSD = itemCostUSD;
        }
    }

    /** Validate ability to read a stream of json content from the database using JsonParser.
     * This test also demonstrates a scenario where we might want to do this, where we don't
     * want to deserialize the entire json body because we only want a portion of the data.
     */
    @Test
    public void testReadStream() throws IOException {
        JacksonParserHandle handle = new JacksonParserHandle();
        handle = docMgr.read(ORDER_URI, handle);
        JsonParser jp = handle.get();
        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }
        ArrayList<OrderItem> orderItems = getOrderItems(jp);
        assertEquals("orderItems array length is wrong", 4, orderItems.size());
        assertEquals("OrderItem 1 productId is wrong", "widget",   orderItems.get(0).getProductId());
        assertEquals("OrderItem 2 productId is wrong", "cog",      orderItems.get(1).getProductId());
        assertEquals("OrderItem 3 productId is wrong", "spring",   orderItems.get(2).getProductId());
        assertEquals("OrderItem 4 productId is wrong", "bearings", orderItems.get(3).getProductId());
        assertEquals("OrderItem 1 quantity is wrong", 1, orderItems.get(0).getQuantity());
        assertEquals("OrderItem 2 quantity is wrong", 5, orderItems.get(1).getQuantity());
        assertEquals("OrderItem 3 quantity is wrong", 1, orderItems.get(2).getQuantity());
        assertEquals("OrderItem 4 quantity is wrong", 3, orderItems.get(3).getQuantity());
        assertEquals("OrderItem 1 itemCostUSD is wrong", 31.99, orderItems.get(0).getItemCostUSD(), 0.001);
        assertEquals("OrderItem 2 itemCostUSD is wrong", 23.99, orderItems.get(1).getItemCostUSD(), 0.001);
        assertEquals("OrderItem 3 itemCostUSD is wrong", 12.99, orderItems.get(2).getItemCostUSD(), 0.001);
        assertEquals("OrderItem 4 itemCostUSD is wrong", 1.99,  orderItems.get(3).getItemCostUSD(), 0.001);
    }

    private ArrayList<OrderItem> getOrderItems(JsonParser parser) throws IOException {
        ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();
        while ( parser.nextValue() != null ) {
            if ( parser.getCurrentToken() == JsonToken.START_ARRAY &&
                 "orderItems".equals(parser.getCurrentName()) )
            {
                while ( parser.nextValue() == JsonToken.START_OBJECT ) {
                    OrderItem item = getOrderItem(parser);
                    orderItems.add(item);
                }
                return orderItems;
            }
        }
        return null;
    }

    private OrderItem getOrderItem(JsonParser parser) throws JsonParseException, IOException {
        OrderItem item = new OrderItem();
        if ( parser.getCurrentToken() != JsonToken.START_OBJECT ) {
            throw new IllegalStateException("nextValue should have been START_OBJECT but is:[" + parser.getCurrentToken() + "]");
        }
        while ( parser.nextValue() != null ) {
            if ( "productId".equals(parser.getCurrentName()) ) {
                item.setProductId( parser.getText() );
            } else if ( "quantity".equals(parser.getCurrentName()) ) {
                item.setQuantity( parser.getIntValue() );
            } else if ( "itemCostUSD".equals(parser.getCurrentName()) ) {
                item.setItemCostUSD( parser.getFloatValue() );
            }
            if ( parser.getCurrentToken() == JsonToken.END_OBJECT ) {
                return item;
            }
        }
        return null;
    }

    /** Demonstrates how to use a JsonGenerator to stream output that you then persist to the
     * server using StringHandle (in this case, implicitly via writeAs).
     */
    @Test
    public void testWriteStream() throws IOException {
        JacksonParserHandle handle = new JacksonParserHandle();
        handle = docMgr.read(ORDER_URI, handle);
        JsonParser jp = handle.get();
        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }

        StringWriter jsonWriter = new StringWriter();
        JsonGenerator jsonStream = (new ObjectMapper()).getFactory().createGenerator(jsonWriter); 
        // in this sample case we're copying everything up to and excluding the order
        SerializedString order = new SerializedString("order");
        do {
            jsonStream.copyCurrentEvent(jp);
        } while ( ! jp.nextFieldName(order) );
        jsonStream.flush();
        jsonStream.close();
        docMgr.writeAs("testWriteStream.json", jsonWriter.toString());

        JsonNode originalTree = docMgr.readAs(ORDER_URI, JsonNode.class);
        JsonNode streamedTree = docMgr.readAs("testWriteStream.json", JsonNode.class);
        assertEquals("customerName fields don't match", 
            originalTree.get("customerName"), streamedTree.get("customerName"));
        assertEquals("shipToAddress fields don't match", 
            originalTree.get("shipToAddress"), streamedTree.get("shipToAddress"));
        assertEquals("billingAddressRequired fields don't match", 
            originalTree.get("billingAddressRequired"), streamedTree.get("billingAddressRequired"));
    }

    /** To make JacksonParserHandle a ContentHandle<JsonParser> we had to implement set()
     * which is a viable but admittedly corner case.  However, since it's implemented, let's
     * make sure it works.  JsonParser.getInputSource() returns an InputStream or a Reader, 
     * so we'll test to make sure behavior is the same whether the JsonParser was created 
     * with a String, an InputStream, or a Reader.
     */
    @Test
    public void testWriteParserCornerCase() throws IOException {
        // test parsing a string
        String fileString = Common.testFileToString(ORDER_FILE);
        JsonParser parser = (new ObjectMapper()).getFactory().createParser(fileString); 
        JacksonParserHandle handle = new JacksonParserHandle();
        handle.set(parser);
        docMgr.write("/testWriteParser1.json", handle);

        // test parsing an InputStream
        InputStream fileInputStream = Common.testFileToStream(ORDER_FILE);
        parser = (new ObjectMapper()).getFactory().createParser(fileInputStream); 
        handle = new JacksonParserHandle();
        handle.set(parser);
        docMgr.write("/testWriteParser2.json", handle);

        // test parsing a Reader
        Reader fileReader = Common.testFileToReader(ORDER_FILE);
        parser = (new ObjectMapper()).getFactory().createParser(fileReader); 
        handle = new JacksonParserHandle();
        handle.set(parser);
        docMgr.write("/testWriteParser3.json", handle);

        JsonNode writeTree = (new ObjectMapper()).readTree(fileString);
        JsonNode readTree1 = docMgr.readAs("/testWriteParser1.json", JsonNode.class);
        JsonNode readTree2 = docMgr.readAs("/testWriteParser2.json", JsonNode.class);
        JsonNode readTree3 = docMgr.readAs("/testWriteParser3.json", JsonNode.class);
        assertEquals("readTree1 does not match writeTree", writeTree, readTree1);
        assertEquals("readTree2 does not match writeTree", writeTree, readTree2);
        assertEquals("readTree3 does not match writeTree", writeTree, readTree3);
    }

    private static void setup() {
        ReaderHandle handle = new ReaderHandle(Common.testFileToReader(ORDER_FILE));
        docMgr.write(ORDER_URI, handle);
    }

    private static void cleanUp() {
        docMgr.delete(ORDER_URI);
        docMgr.delete("testWriteStream.json");
        docMgr.delete("testWriteParser1.json");
        docMgr.delete("testWriteParser2.json");
        docMgr.delete("testWriteParser3.json");
    }
}

