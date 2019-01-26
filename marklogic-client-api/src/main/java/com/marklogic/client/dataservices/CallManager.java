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
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.AbstractWriteHandle;
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
import java.util.function.Function;
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
// TODO: overload with JSONWriteHandle
    CallableEndpoint endpoint(Object serviceDeclaration, Object endpointDeclaration, String extension);

    // immutable and thus threadsafe -- can build any number of calls with new input
    interface CallableEndpoint {
        NoneCaller returningNone();
        <R> OneCaller<R> returningOne(Class<R> as);
        <R> ManyCaller<R> returningMany(Class<R> as);
    }
    interface NoneCaller extends CallBuilder<NoneCaller> {
        // can be called any number of times if the input is retryable
        void call();
    }
    interface OneCaller<R> extends CallBuilder<OneCaller<R>> {
        // can be called any number of times if the input is retryable
        R call();
    }
    interface ManyCaller<R> extends CallBuilder<ManyCaller<R>> {
        // can be called any number of times if the input is retryable
        Stream<? extends R> call();
    }

    interface CallBuilder<T extends CallBuilder> {
        T param(String name, AbstractWriteHandle... value);
        T param(String name, BigDecimal... value);
        T param(String name, Boolean... value);
        T param(String name, byte[]... value);
        T param(String name, Date... value);
        T param(String name, Document... value);
        T param(String name, Double... value);
        T param(String name, Duration... value);
        T param(String name, File... value);
        T param(String name, Float... value);
        T param(String name, InputSource... value);
        T param(String name, InputStream... value);
        T param(String name, Integer... value);
        T param(String name, JsonNode... value);
        T param(String name, JsonParser... value);
        T param(String name, LocalDate... value);
        T param(String name, LocalDateTime... value);
        T param(String name, LocalTime... value);
        T param(String name, Long... value);
        T param(String name, OffsetDateTime... value);
        T param(String name, OffsetTime... value);
        T param(String name, OutputStreamSender... value);
        T param(String name, Reader... value);
        T param(String name, Source... value);
        T param(String name, String... value);
        T param(String name, XMLEventReader... value);
        T param(String name, XMLStreamReader... value);

        // can only be specified once per call
        T session(SessionState session);

        String endpointPath();
        Boolean isSessionRequired();
        Map<String, Paramdef> getParamdefs();
        Returndef getReturndef();
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
