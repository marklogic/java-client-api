/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dbfunction.negative;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.test.dbfunction.DBFunctionTestUtil;
import org.junit.Test;

import java.io.Reader;

import static org.junit.Assert.*;

public class BadExecutionBundleTest {
    BadExecutionBundle testObj = BadExecutionBundle.on(DBFunctionTestUtil.db);

    @Test
    public void testErrorConditions() {
        // columns = statusCode | sentCode | msgMappedToSentCodeInAPIDeclaration | sentMsg
        String[][] tests = new String[][]{
            new String[]{"406", "Unacceptable1", "Unacceptable in every way"},
            new String[]{"406", "Unacceptable2", "Unacceptable, that's how you'll stay", "Unacceptable, that's how you'll stay"},
            new String[]{"406", "Unacceptable2", "Unacceptable, that's how you'll stay", null},
            new String[]{"406", "Unacceptable2", "Unacceptable, that's how you'll stay", "Unacceptable no matter how near or far"},
            new String[]{"410", "Absent1",       "You may search this wide world over"},
            new String[]{"500", "Unknown1",      null,                                   "Are the stars out tonight?"}
        };

        final String inlineMsg = ": fn.error(null, errCode, errMsg); --  ";

        for (int i=0; i < tests.length; i++) {
            String[] test = tests[i];

            int statusCode   = Integer.parseInt(test[0]);
            String sentCode  = test[1];
            String mappedMsg = (test[2] == null) ? "Internal Server Error" : test[2];
            String sentMsg   = (test.length < 4) ? null                    : test[3];
            try {
                testObj.errorMapping(sentCode, sentMsg);
                fail("Endpoint failed to throw error for "+sentCode+" code and "+mappedMsg+" message");
            } catch(FailedRequestException e) {
                // System.out.println(e.getMessage());
                assertEquals("Server status code mismatch for test "+i,  statusCode, e.getServerStatusCode());
                assertEquals("Server status mismatch for test "+i,       mappedMsg,  e.getServerStatus());
                assertEquals("Server message code mismatch for test "+i, sentCode,   e.getServerMessageCode());
                if (sentMsg != null) {
                    String serverMsg = e.getServerMessage();
                    int msgOffset = serverMsg.indexOf(inlineMsg) + inlineMsg.length();
                    assertEquals("Server message mismatch for test "+i,
                            sentMsg, serverMsg.substring(msgOffset, msgOffset + sentMsg.length()));
                }
            } catch(Exception e) {
                fail("Endpoint Exception was not FailedRequestException: "+e.getClass().getName()+"\n"+e.getMessage());
            }
        }
    }
}
