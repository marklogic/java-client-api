/*
 * Copyright 2019 MarkLogic Corporation
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
package com.marklogic.client.dataservices;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import java.io.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

// TODO: JavaDoc

// immutable and thus threadsafe -- can build calls for any number of endpoints
public interface CallManager {
    // manager factory
    public static CallManager on(DatabaseClient client) {
        return new CallManagerImpl(client);
    }

    // optional but available for maintaining state across calls
    SessionState newSessionState();

    // service declaration must be readable multiple times to be used for multiple endpoints
    CallableEndpoint endpoint(JSONWriteHandle serviceDeclaration, JSONWriteHandle endpointDeclaration, String extension);

    // immutable and thus threadsafe -- can build any number of calls with new input
    interface CallableEndpoint {
        NoneCaller returningNone();
        <R> OneCaller<R> returningOne(Class<R> as);
        <R> ManyCaller<R> returningMany(Class<R> as);
    }
    interface CallBuilder {
        <T extends CallBuilder> T param(String name, AbstractWriteHandle... value);
        <T extends CallBuilder> T param(String name, BigDecimal... value);
        <T extends CallBuilder> T param(String name, Boolean... value);
        <T extends CallBuilder> T param(String name, byte[]... value);
        <T extends CallBuilder> T param(String name, Date... value);
        <T extends CallBuilder> T param(String name, Document... value);
        <T extends CallBuilder> T param(String name, Double... value);
        <T extends CallBuilder> T param(String name, Duration... value);
        <T extends CallBuilder> T param(String name, File... value);
        <T extends CallBuilder> T param(String name, Float... value);
        <T extends CallBuilder> T param(String name, InputSource... value);
        <T extends CallBuilder> T param(String name, InputStream... value);
        <T extends CallBuilder> T param(String name, Integer... value);
        <T extends CallBuilder> T param(String name, JsonNode... value);
        <T extends CallBuilder> T param(String name, JsonParser... value);
        <T extends CallBuilder> T param(String name, LocalDate... value);
        <T extends CallBuilder> T param(String name, LocalDateTime... value);
        <T extends CallBuilder> T param(String name, LocalTime... value);
        <T extends CallBuilder> T param(String name, Long... value);
        <T extends CallBuilder> T param(String name, OffsetDateTime... value);
        <T extends CallBuilder> T param(String name, OffsetTime... value);
        <T extends CallBuilder> T param(String name, Reader... value);
        <T extends CallBuilder> T param(String name, Source... value);
        <T extends CallBuilder> T param(String name, String... value);
        <T extends CallBuilder> T param(String name, XMLEventReader... value);
        <T extends CallBuilder> T param(String name, XMLStreamReader... value);

        // can only be specified once per call
        <T extends CallBuilder> T session(SessionState session);

        String endpointPath();
        Boolean isSessionRequired();
        Map<String, Paramdef> getParamdefs();
        Returndef getReturndef();
    }
    interface NoneCaller extends CallBuilder {
        NoneCaller param(String name, AbstractWriteHandle... value);
        NoneCaller param(String name, BigDecimal... value);
        NoneCaller param(String name, Boolean... value);
        NoneCaller param(String name, byte[]... value);
        NoneCaller param(String name, Date... value);
        NoneCaller param(String name, Document... value);
        NoneCaller param(String name, Double... value);
        NoneCaller param(String name, Duration... value);
        NoneCaller param(String name, File... value);
        NoneCaller param(String name, Float... value);
        NoneCaller param(String name, InputSource... value);
        NoneCaller param(String name, InputStream... value);
        NoneCaller param(String name, Integer... value);
        NoneCaller param(String name, JsonNode... value);
        NoneCaller param(String name, JsonParser... value);
        NoneCaller param(String name, LocalDate... value);
        NoneCaller param(String name, LocalDateTime... value);
        NoneCaller param(String name, LocalTime... value);
        NoneCaller param(String name, Long... value);
        NoneCaller param(String name, OffsetDateTime... value);
        NoneCaller param(String name, OffsetTime... value);
        NoneCaller param(String name, Reader... value);
        NoneCaller param(String name, Source... value);
        NoneCaller param(String name, String... value);
        NoneCaller param(String name, XMLEventReader... value);
        NoneCaller param(String name, XMLStreamReader... value);

        // can only be specified once per call
        NoneCaller session(SessionState session);

        // can be called any number of times if the input is retryable
        void call();
    }
    interface OneCaller<R> extends CallBuilder {
        OneCaller<R> param(String name, AbstractWriteHandle... value);
        OneCaller<R> param(String name, BigDecimal... value);
        OneCaller<R> param(String name, Boolean... value);
        OneCaller<R> param(String name, byte[]... value);
        OneCaller<R> param(String name, Date... value);
        OneCaller<R> param(String name, Document... value);
        OneCaller<R> param(String name, Double... value);
        OneCaller<R> param(String name, Duration... value);
        OneCaller<R> param(String name, File... value);
        OneCaller<R> param(String name, Float... value);
        OneCaller<R> param(String name, InputSource... value);
        OneCaller<R> param(String name, InputStream... value);
        OneCaller<R> param(String name, Integer... value);
        OneCaller<R> param(String name, JsonNode... value);
        OneCaller<R> param(String name, JsonParser... value);
        OneCaller<R> param(String name, LocalDate... value);
        OneCaller<R> param(String name, LocalDateTime... value);
        OneCaller<R> param(String name, LocalTime... value);
        OneCaller<R> param(String name, Long... value);
        OneCaller<R> param(String name, OffsetDateTime... value);
        OneCaller<R> param(String name, OffsetTime... value);
        OneCaller<R> param(String name, Reader... value);
        OneCaller<R> param(String name, Source... value);
        OneCaller<R> param(String name, String... value);
        OneCaller<R> param(String name, XMLEventReader... value);
        OneCaller<R> param(String name, XMLStreamReader... value);

        // can only be specified once per call
        OneCaller<R> session(SessionState session);

        // can be called any number of times if the input is retryable
        R call();
    }
    interface ManyCaller<R> extends CallBuilder {
        ManyCaller<R> param(String name, AbstractWriteHandle... value);
        ManyCaller<R> param(String name, BigDecimal... value);
        ManyCaller<R> param(String name, Boolean... value);
        ManyCaller<R> param(String name, byte[]... value);
        ManyCaller<R> param(String name, Date... value);
        ManyCaller<R> param(String name, Document... value);
        ManyCaller<R> param(String name, Double... value);
        ManyCaller<R> param(String name, Duration... value);
        ManyCaller<R> param(String name, File... value);
        ManyCaller<R> param(String name, Float... value);
        ManyCaller<R> param(String name, InputSource... value);
        ManyCaller<R> param(String name, InputStream... value);
        ManyCaller<R> param(String name, Integer... value);
        ManyCaller<R> param(String name, JsonNode... value);
        ManyCaller<R> param(String name, JsonParser... value);
        ManyCaller<R> param(String name, LocalDate... value);
        ManyCaller<R> param(String name, LocalDateTime... value);
        ManyCaller<R> param(String name, LocalTime... value);
        ManyCaller<R> param(String name, Long... value);
        ManyCaller<R> param(String name, OffsetDateTime... value);
        ManyCaller<R> param(String name, OffsetTime... value);
        ManyCaller<R> param(String name, Reader... value);
        ManyCaller<R> param(String name, Source... value);
        ManyCaller<R> param(String name, String... value);
        ManyCaller<R> param(String name, XMLEventReader... value);
        ManyCaller<R> param(String name, XMLStreamReader... value);

        // can only be specified once per call
        ManyCaller<R> session(SessionState session);

        // can be called any number of times if the input is retryable
        Stream<R> call();
    }

    interface Paramdef {
        String  getParamName();
        String  getDataType();
        boolean isNullable();
        boolean isMultiple();
    }
    interface Returndef {
        String  getDataType();
        boolean isNullable();
        boolean isMultiple();
    }
}
