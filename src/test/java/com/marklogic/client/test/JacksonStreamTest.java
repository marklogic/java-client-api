/*
 * Copyright 2012-2014 MarkLogic Corporation
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
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
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

	@Test
	public void testReadWrite() throws IOException {
        JacksonParserHandle handle = new JacksonParserHandle();
        handle = docMgr.read(ORDER_URI, handle);
        JsonParser jp = handle.getParser();
        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }
        ArrayList<OrderItem> orderItems = getOrderItems(jp);
        assertEquals("orderItems array length is wrong", orderItems.size(), 4);
        assertEquals("OrderItem 1 productId is wrong", orderItems.get(0).getProductId(), "widget");
        assertEquals("OrderItem 1 quantity is wrong", orderItems.get(0).getQuantity(), 1);
        assertEquals("OrderItem 1 itemCostUSD is wrong", orderItems.get(0).getItemCostUSD(), 31.99, 0.001);
        assertEquals("OrderItem 2 productId is wrong", orderItems.get(1).getProductId(), "cog");
        assertEquals("OrderItem 2 quantity is wrong", orderItems.get(1).getQuantity(), 5);
        assertEquals("OrderItem 2 itemCostUSD is wrong", orderItems.get(1).getItemCostUSD(), 23.99, 0.001);
        assertEquals("OrderItem 3 productId is wrong", orderItems.get(2).getProductId(), "spring");
        assertEquals("OrderItem 3 quantity is wrong", orderItems.get(2).getQuantity(), 1);
        assertEquals("OrderItem 3 itemCostUSD is wrong", orderItems.get(2).getItemCostUSD(), 12.99, 0.001);
        assertEquals("OrderItem 4 productId is wrong", orderItems.get(3).getProductId(), "bearings");
        assertEquals("OrderItem 4 quantity is wrong", orderItems.get(3).getQuantity(), 3);
        assertEquals("OrderItem 4 itemCostUSD is wrong", orderItems.get(3).getItemCostUSD(), 1.99, 0.001);
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

    private static void setup() {
        ReaderHandle handle = new ReaderHandle(Common.testFileToReader(ORDER_FILE));
        docMgr.write(ORDER_URI, handle);
    }

    private static void cleanUp() {
        docMgr.delete(ORDER_URI);
    }
}

