package com.marklogic.client.test.dataservices;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Date;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.dataservices.CallBatcher.CallArgsGenerator;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.dataservices.CallManager.CallArgs;
import com.marklogic.client.dataservices.impl.CallBatcherImpl;
import com.marklogic.client.dataservices.impl.CallBatcherImpl.BuilderImpl;
import com.marklogic.client.dataservices.impl.CallManagerImpl.CallerImpl;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.test.Common;

public class CallbackGeneratorTest implements CallArgsGenerator<CallManager.CallEvent>{

    private static DatabaseClient db = Common.connect();
    private static CallerImpl callerImpl;
    private static CallbackGeneratorTest callbackGeneratorTest;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() {
        callerImpl = new CallerImpl<CallManager.CallEvent>(null) {

            @Override
            public CallManager.CallEvent callForEvent(DatabaseClient client, CallArgs args) throws Exception {
                return null;
            }
        };
        callbackGeneratorTest = new CallbackGeneratorTest();
    }

    @Test(expected = Test.None.class)
    public void forArgsGeneratorTest() {
        BuilderImpl builderImpl = new BuilderImpl(db,callerImpl);
        assertNotNull(builderImpl.forArgsGenerator(callbackGeneratorTest));
    }
    
    @Test
    public void voidInputTypeTest() {
        
        CallBatcherImpl batcherImpl = new CallBatcherImpl(db, callerImpl, Void.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Cannot call add() when supplying arguments with generator."));
        batcherImpl.add(null);
    }
    
    @Test
    public void callbatcherInitializeTest() {
        CallBatcherImpl batcherImpl = new CallBatcherImpl(db, callerImpl, Void.class, callbackGeneratorTest);
        expectedException.expect(MarkLogicInternalException.class);
        expectedException.expectMessage(startsWith("Unsupported implementation of call arguments."));
        batcherImpl.start(null);
    }

    @Override
    public CallArgs apply(CallManager.CallEvent t) {
        return new CallArgs() {
            
            @Override
            public CallArgs param(String name, XMLStreamReader[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, XMLStreamReader value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, XMLEventReader[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, XMLEventReader value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, String[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, String value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Source[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Source value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Reader[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Reader value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, OffsetTime[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, OffsetTime value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, OffsetDateTime[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, OffsetDateTime value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Long[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Long value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, LocalTime[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, LocalTime value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, LocalDateTime[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, LocalDateTime value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, LocalDate[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, LocalDate value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, JsonParser[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, JsonParser value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, JsonNode[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, JsonNode value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Integer[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Integer value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, InputStream[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, InputStream value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, InputSource[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, InputSource value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Float[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Float value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, File[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, File value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Duration[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Duration value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Double[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Double value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Document[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Document value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Date[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Date value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, byte[][] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, byte[] value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Boolean[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, Boolean value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, BigDecimal[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, BigDecimal value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, AbstractWriteHandle[] values) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public CallArgs param(String name, AbstractWriteHandle value) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String[] getAssignedParamNames() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
    
    @AfterClass
    public void release() {
        db.release();
    }
}
