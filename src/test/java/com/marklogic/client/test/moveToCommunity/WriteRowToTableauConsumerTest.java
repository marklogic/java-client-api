/*
 * Copyright 2018 MarkLogic Corporation
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
package com.marklogic.client.test.moveToCommunity;

import com.marklogic.client.example.cookbook.datamovement.TypedRow;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.moveToCommunity.WriteRowToTableauConsumer;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import org.junit.Test;

import com.tableausoftware.common.Type;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WriteRowToTableauConsumerTest {
  @Test
  public void testValidTypes() throws IOException {
    String expectedFile = "src/test/java/com/marklogic/client/test/moveToCommunity/expected.tde";
    byte[] expectedBytes = Files.readAllBytes(new File(expectedFile).toPath());
    String outFile = "test.tde";
    new File(outFile).delete();
    WriteRowToTableauConsumer tableauWriter = new WriteRowToTableauConsumer("test.tde")
      .withColumn("booleanCol", Type.BOOLEAN)
      .withColumn("integerCol", Type.INTEGER)
      .withColumn("doubleCol", Type.DOUBLE)
      .withColumn("charStringCol", Type.CHAR_STRING)
      .withColumn("unicodeStringCol", Type.UNICODE_STRING);
    PlanBuilder pb = Common.connect().newRowManager().newPlanBuilder();
    TypedRow values = new TypedRow("tempURI", "1");
    values.put("booleanCol", pb.xs.booleanVal(true));
    values.put("integerCol", pb.xs.integer(1));;
    values.put("doubleCol", pb.xs.doubleVal(1.0));;
    values.put("stringCol", pb.xs.string("str"));;
    tableauWriter.accept(values);
    values.put("integerCol", pb.xs.integer(1l));;
    values.put("doubleCol", pb.xs.floatVal(1.0f));;
    tableauWriter.accept(values);
    values.put("integerCol", pb.xs.integer(BigInteger.valueOf(1l)));
    tableauWriter.accept(values);
    values.put("integerCol", pb.xs.integer("1"));
    tableauWriter.accept(values);
    values.put("integerCol", pb.xs.shortVal((short)1));
    tableauWriter.accept(values);
    values.put("integerCol", pb.xs.longVal(1));
    tableauWriter.accept(values);
    tableauWriter.close();
    byte[] outBytes = Files.readAllBytes(new File(outFile).toPath());
    assertEquals("Generated " + outFile + " file size didn't match " + expectedFile,
      expectedBytes.length, outBytes.length);
    assertTrue("Generated " + outFile + " didn't match " + expectedFile,
      Arrays.equals(expectedBytes, outBytes));
  }

  @Test
  public void testInvalidTypes() throws IOException {
    // before long we should probably add support for these types, and just pull them from
    // strings in the tde:node-data-extract output
    File outFile = new File("test.tde");
    outFile.delete();
    WriteRowToTableauConsumer tableauWriter = new WriteRowToTableauConsumer("test.tde");
    try {
      tableauWriter.withColumn("dateCol", Type.DATE);
      fail("should have thrown IllegalStateException because Date type is not yet supported");
    } catch (IllegalStateException e) {
      System.out.println("Tableau Data Extract threw expected exception since the type is not yet supported - " + e);
    }
    try {
      tableauWriter.withColumn("dateTimeCol", Type.DATETIME);
      fail("should have thrown IllegalStateException because DateTime type is not yet supported");
    } catch (IllegalStateException e) {
      System.out.println("Tableau Data Extract threw expected exception since the type is not yet supported - " + e);
    }
    try {
      tableauWriter.withColumn("durationCol", Type.DURATION);
      fail("should have thrown IllegalStateException because Duration type is not yet supported");
    } catch (IllegalStateException e) {
      System.out.println("Tableau Data Extract threw expected exception since the type is not yet supported - " + e);
    }
    try {
      tableauWriter.withColumn("spatialCol", Type.SPATIAL);
      fail("should have thrown IllegalStateException because Spatial type is not yet supported");
    } catch (IllegalStateException e) {
      System.out.println("Tableau Data Extract threw expected exception since the type is not yet supported - " + e);
    }
    tableauWriter.close();
  }

  @Test
  public void testMismatchedTypes() throws IOException {
    File outFile = new File("test.tde");
    outFile.delete();
    WriteRowToTableauConsumer tableauWriter = new WriteRowToTableauConsumer("test.tde")
        .withColumn("integerCol", Type.INTEGER);
    PlanBuilder pb = Common.connect().newRowManager().newPlanBuilder();
    TypedRow values = new TypedRow("tempURI", "2");
    values.put("integerCol", pb.xs.booleanVal(true));
    try {
      tableauWriter.accept(values);
      fail("should have thrown IllegalStateException because the types are not compatible");
    } catch (IllegalStateException e) {
      System.out.println("Tableau Data Extract threw expected exception since the types are not compatible - " + e);
    }
    tableauWriter.close();
  }
}
