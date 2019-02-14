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

package com.marklogic.client.datamovement;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.JacksonHandle;

/**
 * The JacksonCSVSplitter class uses the Jackson CSV parser without attempting to abstract it capabilities. 
 * The application can override defaults by configuring the Jackson ObjectReader and CsvSchema including parsing TSV
 */
public class JacksonCSVSplitter implements Splitter<JacksonHandle> {
    private CsvSchema csvSchema = null;
    private CsvMapper csvMapper;
    private long count = 0;
    private ArrayNode headers = null;
    
    public CsvMapper getCsvMapper() {
        return csvMapper;
    }

    public JacksonCSVSplitter withCsvSchema(CsvSchema schema) {
        this.csvSchema = schema;
        return this;
    } 
    
    public JacksonCSVSplitter withCsvMapper(CsvMapper mapper) {
        this.csvMapper = mapper;
        return this;
    }
    
    public CsvSchema getCsvSchema() {
        return csvSchema;
    }
    
    private CsvMapper configureCsvMapper() {
        if(csvMapper == null) {
        csvMapper = new CsvMapper()
                .configure(CsvParser.Feature.ALLOW_TRAILING_COMMA, true)
                .configure(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS, false)
                .configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, false)
                .configure(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS, false)
                .configure(CsvParser.Feature.SKIP_EMPTY_LINES, true)
                .configure(CsvParser.Feature.TRIM_SPACES, true)
                .configure(CsvParser.Feature.WRAP_AS_ARRAY, false)
                .configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, true);
        }
        return csvMapper;
    }

    @Override
    public Stream<JacksonHandle> split(InputStream input) throws Exception { 

        if(input == null) {
            throw new IllegalArgumentException("InputSteam cannot be null.");
        }
        PeekingIterator<JsonNode> peekingIterator = configureSplitObj(configureObjReader().readValues(input));
        
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(peekingIterator, Spliterator.ORDERED), false)
                .map(this::wrapJacksonHandle);
    }
    
    public Stream<JacksonHandle> split(Reader input) throws Exception  { 

        if(input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        PeekingIterator<JsonNode> peekingIterator = configureSplitObj(configureObjReader().readValues(input));
        setHeaders(peekingIterator.getFirst());
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(peekingIterator, Spliterator.ORDERED), false)
                .map(this::wrapJacksonHandle);
    }

    @Override
    public long getCount() { 
        return this.count;
    }
    public ArrayNode getHeaders() {
        return this.headers;
    }
    
    private void incrementCount() {
        this.count++;
    }
    
    private ObjectReader configureObjReader() {
        this.count=0;
        CsvSchema firstLineSchema = getCsvSchema()!=null? getCsvSchema():CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = getCsvMapper()!=null ? getCsvMapper() : configureCsvMapper();
        ObjectReader objectReader = csvMapper.readerFor(JsonNode.class);
        
        return objectReader.with(firstLineSchema);
    }
    
    private JacksonHandle wrapJacksonHandle(JsonNode content) {
        incrementCount();
        return new JacksonHandle(content);
    }
    
    private void setHeaders(JsonNode headerNode) {
        Iterator<String> headerValue = headerNode.fieldNames();
        this.headers = new ObjectMapper().createArrayNode();
        while (headerValue.hasNext()) {
            headers.add(headerValue.next());
        }
    }
    
    private PeekingIterator<JsonNode> configureSplitObj(Iterator<JsonNode> nodeItr){
        if (nodeItr == null || !nodeItr.hasNext()) {
            throw new MarkLogicIOException("No header found.");
        }
        PeekingIterator<JsonNode> peekingIterator = new PeekingIterator<JsonNode>(nodeItr);
        
        Iterator<String> headerValue = peekingIterator.getFirst().fieldNames();
        this.headers = new ObjectMapper().createArrayNode();
        while (headerValue.hasNext()) {
            headers.add(headerValue.next());
        }
        
        return peekingIterator;
    }
}
