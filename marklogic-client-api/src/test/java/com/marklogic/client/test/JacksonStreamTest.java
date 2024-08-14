/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.ReaderHandle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonStreamTest {
  private static final String ORDER_FILE = "sampleOrder.json";
  private static final String ORDER_URI = "sampleOrder.json";
  private static JSONDocumentManager docMgr;

  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    docMgr = Common.client.newJSONDocumentManager();

  }

  @BeforeEach
  public void beforeTest() {
    docMgr.write(ORDER_URI, new ReaderHandle(Common.testFileToReader(ORDER_FILE)));
  }

  @AfterAll
  public static void afterClass() {
    cleanUp();
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
    List<OrderItem> orderItems = getOrderItems(jp);
    assertEquals( 4, orderItems.size());
    assertEquals( "widget",   orderItems.get(0).getProductId());
    assertEquals( "cog",      orderItems.get(1).getProductId());
    assertEquals( "spring",   orderItems.get(2).getProductId());
    assertEquals( "bearings", orderItems.get(3).getProductId());
    assertEquals( 1, orderItems.get(0).getQuantity());
    assertEquals( 5, orderItems.get(1).getQuantity());
    assertEquals( 1, orderItems.get(2).getQuantity());
    assertEquals( 3, orderItems.get(3).getQuantity());
    assertEquals( 31.99, orderItems.get(0).getItemCostUSD(), 0.001);
    assertEquals( 23.99, orderItems.get(1).getItemCostUSD(), 0.001);
    assertEquals( 12.99, orderItems.get(2).getItemCostUSD(), 0.001);
    assertEquals( 1.99,  orderItems.get(3).getItemCostUSD(), 0.001);
  }

  private List<OrderItem> getOrderItems(JsonParser parser) throws IOException {
    List<OrderItem> orderItems = new ArrayList<>();
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
    assertEquals(
      originalTree.get("customerName"), streamedTree.get("customerName"));
    assertEquals(
      originalTree.get("shipToAddress"), streamedTree.get("shipToAddress"));
    assertEquals(
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
    assertEquals( writeTree, readTree1);
    assertEquals( writeTree, readTree2);
    assertEquals( writeTree, readTree3);
  }

  private static void cleanUp() {
    docMgr.delete(ORDER_URI);
    docMgr.delete("testWriteStream.json");
    docMgr.delete("testWriteParser1.json");
    docMgr.delete("testWriteParser2.json");
    docMgr.delete("testWriteParser3.json");
  }
}

