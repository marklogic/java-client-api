/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.PlanExprCol;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PlanExpressionTest {
    protected static RowManager rowMgr = null;
    protected static PlanBuilder p = null;

    @BeforeAll
    public static void beforeClass() {
        Common.connect();
        rowMgr = Common.client.newRowManager();
        p      = rowMgr.newPlanBuilder();
    }
    @AfterAll
    public static void afterClass() {
        p      = null;
        rowMgr = null;
   }

   private RowRecord redactTest(String testName, Map<String,Object> testRow, PlanExprCol... cols) {
       PlanBuilder.ModifyPlan plan =
               p.fromLiterals(new Map[]{testRow})
                .bind(p.colSeq(cols));

       RowSet<RowRecord> rowSet = rowMgr.resultRows(plan);
       Iterator<RowRecord> rowItr = rowSet.iterator();
       assertTrue(rowItr.hasNext());

       return rowItr.next();
   }

   @Test
   public void testMaskDeterministic() {
      final String testStr = "What is truth?";
      final int testLen    = testStr.length();

      Map<String,Object> testRow = new HashMap<>();
      testRow.put("cDefault",             testStr);
      testRow.put("cCharMixedCaseMaxLen", testStr);

      Map<String,Object> testOpts = new HashMap<>();
      testOpts.put("character", "mixedCase");
      testOpts.put("maxLength", testLen);

      RowRecord resultRow = redactTest("testMaskDeterministic()", testRow,
              p.rdt.maskDeterministic(p.col("cDefault")),
              p.rdt.maskDeterministic(p.col("cCharMixedCaseMaxLen"), testOpts)
      );

      String cDefaultVal = resultRow.getString("cDefault");
      assertNotNull(cDefaultVal);
      assertTrue(cDefaultVal.matches("^[A-za-z0-9+=/]+$"));

      String cCharMixedCaseMaxLenVal = resultRow.getString("cCharMixedCaseMaxLen");
      assertNotNull(cCharMixedCaseMaxLenVal);
      assertEquals(testLen, cCharMixedCaseMaxLenVal.length());
      assertTrue(cCharMixedCaseMaxLenVal.matches("^[A-za-z]+$"));
   }
   @Test
   public void testMaskRandom() {
      final int base64Len  = 26;
      final String testStr = "What is truth?";
      final int testLen    = base64Len - 1;

      Map<String,Object> testRow = new HashMap<>();
      testRow.put("cDefault",                 testStr);
      testRow.put("cCharLowerCaseNumericLen", testStr);

      Map<String,Object> testOpts = new HashMap<>();
      testOpts.put("character", "lowerCaseNumeric");
      testOpts.put("length",    testLen);

      RowRecord resultRow = redactTest("testMaskRandom()", testRow,
              p.rdt.maskRandom(p.col("cDefault")),
              p.rdt.maskRandom(p.col("cCharLowerCaseNumericLen"), testOpts)
      );

      String cDefaultVal = resultRow.getString("cDefault");
      assertNotNull(cDefaultVal);
      assertEquals(base64Len, cDefaultVal.length());
      assertTrue(cDefaultVal.matches("^[A-za-z0-9+=/]+$"));

      String cCharLowerCaseNumericLenVal = resultRow.getString("cCharLowerCaseNumericLen");
      assertNotNull(cCharLowerCaseNumericLenVal);
      assertEquals(testLen, cCharLowerCaseNumericLenVal.length());
      assertTrue(cCharLowerCaseNumericLenVal.matches("^[a-z0-9]+$"));
   }
   @Test
   public void testRedactDatetime() {
      final String testStr = "12/31/2019";

      Map<String,Object> testRow = new HashMap<>();
      testRow.put("cParsed", testStr);
      testRow.put("cRandom", testStr);

      Map<String,Object> parsedOpts = new HashMap<>();
      parsedOpts.put("level",   "parsed");
      parsedOpts.put("picture", "[M01]/[D01]/[Y0001]");
      parsedOpts.put("format",  "xx/xx/[Y01]");

      Map<String,Object> randomOpts = new HashMap<>();
      randomOpts.put("level", "random");
      randomOpts.put("range", "2000,2020");

      RowRecord resultRow = redactTest("testRedactDatetime()", testRow,
              p.rdt.redactDatetime(p.col("cParsed"), parsedOpts),
              p.rdt.redactDatetime(p.col("cRandom"), randomOpts)
      );

      String cParsedVal = resultRow.getString("cParsed");
      assertNotNull(cParsedVal);
      assertEquals("xx/xx/19", cParsedVal);

      String cRandomVal = resultRow.getString("cRandom");
      assertNotNull(cRandomVal);
      assertTrue(cRandomVal.matches("^20\\d{2}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+$"));
   }
   @Test
   public void testRedactEmail() {
      final String testStr = "thename1@thedomain1.com";

      Map<String,Object> testRow = new HashMap<>();
      testRow.put("cDefault", testStr);
      testRow.put("cName",    testStr);

      Map<String,Object> testOpts = new HashMap<>();
      testOpts.put("level", "name");

      RowRecord resultRow = redactTest("testRedactEmail()", testRow,
              p.rdt.redactEmail(p.col("cDefault")),
              p.rdt.redactEmail(p.col("cName"), testOpts)
      );

      String cDefaultVal = resultRow.getString("cDefault");
      assertNotNull(cDefaultVal);
      assertEquals("NAME@DOMAIN", cDefaultVal);

      String cNameVal = resultRow.getString("cName");
      assertNotNull(cNameVal);
      assertEquals("NAME@thedomain1.com", cNameVal);
   }
   @Test
   public void testRedactIpv4() {
      final String testStr = "123.145.167.189";

      Map<String,Object> testRow = new HashMap<>();
      testRow.put("cDefault", testStr);
      testRow.put("cChar",    testStr);

      Map<String,Object> testOpts = new HashMap<>();
      testOpts.put("character", "x");

      RowRecord resultRow = redactTest("testRedactIpv4()", testRow,
              p.rdt.redactIpv4(p.col("cDefault")),
              p.rdt.redactIpv4(p.col("cChar"), testOpts)
      );

      String cDefaultVal = resultRow.getString("cDefault");
      assertNotNull(cDefaultVal);
      assertEquals("###.###.###.###", cDefaultVal);

      String cCharVal = resultRow.getString("cChar");
      assertNotNull(cCharVal);
      assertEquals("xxx.xxx.xxx.xxx", cCharVal);
   }
   @Test
   public void testRedactNumber() {
      Map<String,Object> testRow = new HashMap<>();
      testRow.put("cDefault", 1);
      testRow.put("cDouble",  1.3);

      Map<String,Object> testOpts = new HashMap<>();
      testOpts.put("type", "double");
      testOpts.put("min",  2);
      testOpts.put("max",  4);

      RowRecord resultRow = redactTest("testRedactNumber()", testRow,
              p.rdt.redactNumber(p.col("cDefault")),
              p.rdt.redactNumber(p.col("cDouble"), testOpts)
      );

      int cDefaultVal = resultRow.getInt("cDefault");

      double cDoubleVal = resultRow.getDouble("cDouble");
      assertTrue(2 <= cDoubleVal && cDoubleVal <= 4);
   }
   @Test
   public void testRedactRegex() {
      Map<String,Object> testRow = new HashMap<>();
      testRow.put("cTarget", "thetargettext");

      Map<String,Object> testOpts = new HashMap<>();
      testOpts.put("pattern",     "tar([a-z])et");
      testOpts.put("replacement", "=$1=");

      RowRecord resultRow = redactTest("testRedactRegex()", testRow,
              p.rdt.redactRegex(p.col("cTarget"), testOpts)
      );

      String cTargetVal = resultRow.getString("cTarget");
      assertNotNull(cTargetVal);
      assertEquals("the=g=text", cTargetVal);
   }

   // Currently failing on 12-nightly due to server bug - https://progresssoftware.atlassian.net/browse/MLE-17611
   @Test
   public void testRedactUsPhone() {
      final String testStr = "123-456-7890";

      Map<String,Object> testRow = new HashMap<>();
      testRow.put("cDefault",    testStr);
      testRow.put("cFullRandom", testStr);

      Map<String,Object> testOpts = new HashMap<>();
      testOpts.put("level", "full-random");

      RowRecord resultRow = redactTest("testRedactUsPhone()", testRow,
              p.rdt.redactUsPhone(p.col("cDefault")),
              p.rdt.redactUsPhone(p.col("cFullRandom"), testOpts)
      );

      String cDefaultVal = resultRow.getString("cDefault");
      assertNotNull(cDefaultVal);
      assertEquals("###-###-####", cDefaultVal);

      String cFullRandomVal = resultRow.getString("cFullRandom");
      assertNotNull(cFullRandomVal);
      assertEquals(testStr.length(), cFullRandomVal.length());
      assertTrue(cFullRandomVal.matches("^\\d{3}-\\d{3}-\\d{4}$"));
   }
   @Test
   public void testRedactUsSsn() {
      final String testStr = "123-45-6789";

      Map<String,Object> testRow = new HashMap<>();
      testRow.put("cDefault",    testStr);
      testRow.put("cFullRandom", testStr);

      Map<String,Object> testOpts = new HashMap<>();
      testOpts.put("level", "full-random");

      RowRecord resultRow = redactTest("testRedactUsSsn()", testRow,
              p.rdt.redactUsSsn(p.col("cDefault")),
              p.rdt.redactUsSsn(p.col("cFullRandom"), testOpts)
      );

      String cDefaultVal = resultRow.getString("cDefault");
      assertNotNull(cDefaultVal);
      assertEquals("###-##-####", cDefaultVal);

      String cFullRandomVal = resultRow.getString("cFullRandom");
      assertNotNull(cFullRandomVal);
      assertEquals(testStr.length(), cFullRandomVal.length());
      assertTrue(cFullRandomVal.matches("^\\d{3}-\\d{2}-\\d{4}$"));
   }
}
