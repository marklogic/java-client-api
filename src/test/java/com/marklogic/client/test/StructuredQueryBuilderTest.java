package com.marklogic.client.test;

import com.marklogic.client.config.search.StructuredQueryDefinition;
import com.marklogic.client.config.search.StructuredQueryBuilder;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/14/12
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredQueryBuilderTest {
    @Test
    public void testBuilder() throws IOException {
        StructuredQueryBuilder qb = new StructuredQueryBuilder(null);
        StructuredQueryDefinition t = qb.term("leaf3");
        
        assertEquals("<query xmlns='http://marklogic.com/appservices/search'><term><text>leaf3</text></term></query>", t.serialize());
    }
}
