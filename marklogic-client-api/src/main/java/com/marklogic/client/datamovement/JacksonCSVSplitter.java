package com.marklogic.client.datamovement;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.marklogic.client.io.JacksonHandle;

/**
 * The JacksonCSVSplitter class uses the Jackson CSV parser without attempting to abstract it capabilities. 
 * The application can override defaults by configuring the Jackson ObjectReader and CsvSchema including parsing TSV
 */
public class JacksonCSVSplitter implements Splitter<JacksonHandle> {
    private CsvSchema csvSchema = null;
    private CsvMapper csvMapper;
    private ObjectReader objectParser;
    private long count;
    
    public JacksonCSVSplitter() {
        setCsvMapper();
        setCount(0);
    }
    
    public JacksonCSVSplitter(CsvSchema newSchema) {
        setCsvSchema(newSchema);
        setCsvMapper();
        setCount(0);
    }
    
    private void setCsvMapper() {
        csvMapper = new CsvMapper()
                .configure(CsvParser.Feature.ALLOW_TRAILING_COMMA, true)
                .configure(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS, false)
                .configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, false)
                .configure(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS, false)
                .configure(CsvParser.Feature.SKIP_EMPTY_LINES, true)
                .configure(CsvParser.Feature.TRIM_SPACES, true)
                .configure(CsvParser.Feature.WRAP_AS_ARRAY, false);
    }
    // overriding the default Jackson configuration
    public void setObjectReader(ObjectReader parser) { 
        this.objectParser = parser;
    } 
    public ObjectReader getObjectReader() {
        return objectParser;
    }
    public void setCsvSchema(CsvSchema schema) {
        this.csvSchema = schema;
    } 
    public CsvSchema getCsvSchema() {
        return csvSchema;
    }

    @Override
    public Stream<JacksonHandle> split(InputStream input) throws Exception { 

        Iterator<JsonNode> nodeItr = configureObjReader().readValues(input);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(nodeItr, Spliterator.ORDERED), false)
                .map(JacksonHandle::new);
    }
    public Stream<JacksonHandle> split(Reader input) throws Exception { 

        Iterator<JsonNode> nodeItr = configureObjReader().readValues(input);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(nodeItr, Spliterator.ORDERED), false)
                .map(JacksonHandle::new);
    }

    @Override
    public long getCount() { 
        return this.count;
    }
    public void incrementCount() {
        this.count++;
    }
    private void setCount(long countValue) {
        this.count = countValue;
    }
    
    private ObjectReader configureObjReader() {
        CsvSchema firstLineSchema = getCsvSchema()!=null? getCsvSchema():CsvSchema.emptySchema().withHeader();
        ObjectReader objectReader = getObjectReader()!=null? getObjectReader():csvMapper.readerFor(JsonNode.class);
        
        return objectReader.with(firstLineSchema);
    }
}
