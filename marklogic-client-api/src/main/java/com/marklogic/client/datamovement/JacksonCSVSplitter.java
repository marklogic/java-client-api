/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
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
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
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

    /**
     * The CsvMapper configured for the current instance.
     * @return the CsvMapper for the current instance.
     */
    public CsvMapper getCsvMapper() {
        return csvMapper;
    }

    /**
     * Used to set the CsvSchema for the current instance.
     * @param schema is the CsvSchema passed in.
     * @return an instance of JacksonCSVSplitter with CsvSchema set to the parameter.
     */
    public JacksonCSVSplitter withCsvSchema(CsvSchema schema) {
        this.csvSchema = schema;
        return this;
    }

    /**
     * Used to set the CsvMapper for the current instance.
     * @param mapper is the CsvMapper passed in.
     * @return an instance of JacksonCSVSplitter with CsvMapper set to the parameter.
     */
    public JacksonCSVSplitter withCsvMapper(CsvMapper mapper) {
        this.csvMapper = mapper;
        return this;
    }

    /**
     * The CsvSchema configured for the current instance.
     * @return the CsvSchema for the current instance.
     */
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

    /**
     * Takes the input stream and converts it into a stream of JacksonHandle by setting the schema
     *  and wrapping the JsonNode into JacksonHandle.
     * @param input the input stream passed in.
     * @return a stream of JacksonHandle.
     * @throws IOException if the input cannot be split
     */
    @Override
    public Stream<JacksonHandle> split(InputStream input) throws IOException {
        if(input == null) {
            throw new IllegalArgumentException("InputSteam cannot be null.");
        }
        return configureInput(configureObjReader().readValues(input));
    }

    /**
     * Takes the input stream and converts it into a stream of JacksonHandle by setting the schema
     *  and wrapping the JsonNode into JacksonHandle.
     * @param input the Reader stream passed in.
     * @return a stream of JacksonHandle.
     * @throws IOException if the input cannot be split
     */
    public Stream<JacksonHandle> split(Reader input) throws IOException {
        if(input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        Iterator<JsonNode> nodeItr = configureObjReader().readValues(input);
        return configureInput(nodeItr);
    }


    /**
     * Takes the input stream and converts it into a stream of DocumentWriteOperation by setting the schema
     * and wrapping the JsonNode into DocumentWriteOperation.
     * @param input is the incoming input stream.
     * @return a stream of DocumentWriteOperation.
     * @throws Exception if the input cannot be split
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input) throws Exception {
        return splitWriteOperations(input, null);
    }

    /**
     * Takes the input stream and the input name, then converts the input into a stream of DocumentWriteOperation
     * by setting the schema and wrapping the JsonNode into DocumentWriteOperation.
     * @param input is the incoming input stream.
     * @param splitFilename the name of the input stream, including name and extension. It is used to generate URLs for
     *                  split files.The splitFilename could either be provided here or in user-defined UriMaker.
     * @return a stream of DocumentWriteOperation.
     * @throws Exception if the input cannot be split
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input, String splitFilename) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        if (getUriMaker() == null) {
            JacksonCSVSplitter.UriMakerImpl uriMaker = new UriMakerImpl();
            setUriMaker(uriMaker);
        }

        if (splitFilename != null) {
            getUriMaker().setSplitFilename(splitFilename);
        }

        Iterator<JsonNode> nodeItr = configureObjReader().readValues(input);
        return configureInputDocumentWriteOperation(nodeItr);
    }

    /**
     * Takes the input Reader and converts it into a stream of DocumentWriteOperation by setting the schema
     * and wrapping the JsonNode into DocumentWriteOperation.
     * @param input is the incoming input Reader.
     * @return a stream of DocumentWriteOperation.
     * @throws Exception if the input cannot be split
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(Reader input) throws Exception {
        return splitWriteOperations(input, null);
    }

    /**
     * Takes the input Reader and the input name, then converts the input Reader into a stream of DocumentWriteOperation
     * by setting the schema and wrapping the JsonNode into DocumentWriteOperation.
     * @param input is the incoming input Reader.
     * @param splitFilename the name of the input Reader, including name and extension. It is used to generate URLs for
     *                  split files.The splitFilename could either be provided here or in user-defined UriMaker.
     * @return a stream of DocumentWriteOperation.
     * @throws Exception if the input cannot be split
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(Reader input, String splitFilename) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        if (getUriMaker() == null) {
            JacksonCSVSplitter.UriMakerImpl uriMaker = new UriMakerImpl();
            setUriMaker(uriMaker);
        }

        if (splitFilename != null) {
            getUriMaker().setSplitFilename(splitFilename);
        }

        //for case file.csv, to generate uris with extension "json"
        //for default UriMaker only, not custom UriMaker
        if (getUriMaker() instanceof JacksonCSVSplitter.UriMakerImpl) {
            ((UriMakerImpl) getUriMaker()).setExtension("json");
        }

        Iterator<JsonNode> nodeItr = configureObjReader().readValues(input);
        return configureInputDocumentWriteOperation(nodeItr);
    }

    /**
     * The number of JsonNodes found so far.
     * @return the number of JsonNodes found in the input stream.
     */
    @Override
    public long getCount() {
        return this.count;
    }

    /**
     * The headers of the csv file.
     * @return the headers found in the csv file.
     */
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

    private DocumentWriteOperation wrapDocumentWriteOperation(JsonNode content) {
        JacksonHandle handle = wrapJacksonHandle(content);
        String uri = uriMaker.makeUri(count, handle);

        return new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                uri,
                null,
                handle
        );
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

    private Stream<JacksonHandle> configureInput(Iterator<JsonNode> nodeItr) {

        if(getCsvSchema() == null) {
            PeekingIterator<JsonNode> peekingIterator = configureSplitObj(nodeItr);
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(peekingIterator, Spliterator.ORDERED), false).map(this::wrapJacksonHandle);
        }
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(nodeItr, Spliterator.ORDERED), false).map(this::wrapJacksonHandle);
    }

    private Stream<DocumentWriteOperation> configureInputDocumentWriteOperation(Iterator<JsonNode> nodeItr) {
        if(getCsvSchema() == null) {
            PeekingIterator<JsonNode> peekingIterator = configureSplitObj(nodeItr);
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(peekingIterator, Spliterator.ORDERED), false).map(this::wrapDocumentWriteOperation);
        }
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(nodeItr, Spliterator.ORDERED), false).map(this::wrapDocumentWriteOperation);
    }

    private JacksonCSVSplitter.UriMaker uriMaker;

    /**
     * Get the UriMaker of the splitter
     * @return the UriMaker of the splitter
     */
    public JacksonCSVSplitter.UriMaker getUriMaker() {
        return this.uriMaker;
    }

    /**
     * Set the UriMaker to the splitter
     * @param uriMaker the uriMaker to generate URI of each split file.
     */
    public void setUriMaker(JacksonCSVSplitter.UriMaker uriMaker) {
        this.uriMaker = uriMaker;
    }

    /**
     * UriMaker which generates URI for each split file
     */
    public interface UriMaker extends Splitter.UriMaker {
        /**
         * Generates URI for each split
         * @param num the count of each split
         * @param handle the handle which contains the content of each split. It could be utilized to make a meaningful
         *               document URI.
         * @return the generated URI of current split
         */
        String makeUri(long num, JacksonHandle handle);
    }

    private static class UriMakerImpl extends com.marklogic.client.datamovement.impl.UriMakerImpl<JacksonHandle> implements UriMaker {
        @Override
        public String makeUri(long num, JacksonHandle handle) {
            StringBuilder uri = new StringBuilder();

            if (getInputAfter() != null && getInputAfter().length() != 0) {
                uri.append(getInputAfter());
            }

            if (getSplitFilename() != null && getSplitFilename().length() != 0) {
                uri.append(getName());
            }

            if (uri.length() == 0) {
                uri.append("/");
            }

            uri.append(num).append("_").append(UUID.randomUUID()).append(".json");
            return uri.toString();
        }
    }
}
