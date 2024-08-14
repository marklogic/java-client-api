/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The JSONSplitter is used to split large JSON file into separate payloads for writing to the database. The JSON file
 * is typically an array containing an object for each record. JSONSplitter could split each targeted object or array
 * into separate files.
 * @param <T> The type of the handle used for each split payload
 */
public class JSONSplitter<T extends JSONWriteHandle> implements Splitter<T> {

    /**
     * Construct a simple JSONSplitter which split objects or arrays under an array.
     * @return a JSONSplitter class which splits each object or array into a separate payload
     */
    static public JSONSplitter<StringHandle> makeArraySplitter() {

        JSONSplitter.ArrayVisitor arrayVisitor = new JSONSplitter.ArrayVisitor();
        return new JSONSplitter<>(arrayVisitor);
    }

    private JSONSplitter.Visitor<T> visitor;
    private long count = 0;
    private String splitFilename;

    /**
     * Construct a JSONSplitter which splits the JSON file according to the visitor.
     * @param visitor describes how to spit the file
     */
    public JSONSplitter(JSONSplitter.Visitor<T> visitor) {
        setVisitor(visitor);
    }

    /**
     * Get the visitor used in JSONSplitter class.
     * @return the visitor used in JSONSplitter class
     */
    public JSONSplitter.Visitor<T> getVisitor() {
        return this.visitor;
    }

    /**
     * Set the visitor to select objects or arrays to split in JSONSplitter.
     * @param visitor the visitor describes the rule to split the JSON file
     */
    public void setVisitor(JSONSplitter.Visitor<T> visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor cannot be null");
        }
        this.visitor = visitor;
    }

    /**
     * Returns the number of splits.
     * @return the number of splits
     */
    @Override
    public long getCount() {
        return count;
    }

    /**
     * Takes an InputStream of a JSON file and split it into a steam of handles.
     * @param input is the incoming InputStream of a JSON file.
     * @return a stream of handles to write to database
     * @throws IOException if the input cannot be split
     */
    @Override
    public Stream<T> split(InputStream input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        JsonParser jsonParser = new JsonFactory().createParser(input);
        return split(jsonParser);
    }

    /**
     * Takes an InputStream of a JSON file and split it into a steam of DocumentWriteOperation to write to database.
     * @param input is the incoming input stream of a JSON file
     * @return a stream of DocumentWriteOperation to write to database
     * @throws Exception if the input cannot be split
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input) throws Exception {
        return splitWriteOperations(input, null);
    }

    /**
     * Takes an InputStream of a JSON file and file name and split it into a steam of DocumentWriteOperation
     * to write to database.
     * @param input is the incoming input stream of a JSON file
     * @param splitFilename is the name of input file, including name and extension. It is used to generate URLs for split
     *                  files.The splitFilename could either be provided here or in user-defined UriMaker.
     * @return a stream of DocumentWriteOperation to write to database
     * @throws Exception if the input cannot be split
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input, String splitFilename) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        JsonParser jsonParser = new JsonFactory().createParser(input);
        return splitWriteOperations(jsonParser, splitFilename);
    }

    /**
     * Take an input of JsonParser created from the JSON file and split it into a stream of handles to write to database.
     * @param input JsonParser created from the JSON file
     * @return a stream of handles to write to database
     * @throws IOException if the input cannot be split
     */
    public Stream<T> split(JsonParser input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        count = 0;

        JSONSplitter.HandleSpliterator<T> handleSpliterator = new JSONSplitter.HandleSpliterator<>(this, input);
        return StreamSupport.stream(handleSpliterator, true);
    }

    /**
     * Take an input of JsonParser created from the JSON file and split it into a stream of DocumentWriteOperations
     * to write to database.
     * @param input JsonParser created from the JSON file
     * @param splitFilename is the name of input file, including name and extension. It is used to generate URLs for split
     *                  files.The splitFilename could either be provided here or in user-defined UriMaker.
     * @return a stream of DocumentWriteOperation to write to database
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(JsonParser input, String splitFilename) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        count = 0;

        this.splitFilename = splitFilename;
        JSONSplitter.DocumentWriteOperationSpliterator spliterator =
                new JSONSplitter.DocumentWriteOperationSpliterator<>(this, input);
        return StreamSupport.stream(spliterator, true);
    }

    /**
     * The Visitor class is used to accumulate and inspect state during the depth-first traversal of the JSON tree
     * and make the decision of how to split the JSON file.
     * It checks if the current object or array is the target to split.
     * If it is, convert the target object or array into buffered handles or DocumentWriteOperations.
     * @param <T> The type of the handle used for each split
     */
    static public abstract class Visitor<T extends AbstractWriteHandle>   {

        /**
         * This method inspects the state of the current object and decides whether to split it or not. This method
         * can be overridden to accumulate and inspect state during the depth-first traversal of the JSON tree and make
         * the decision of whether to split.
         * @param containerKey The key of the object which the value contains current object
         * @return different operations to either process current object, go down the JSON tree or skip current object
         */
        public NodeOperation startObject(String containerKey) {
            return NodeOperation.DESCEND;
        }

        /**
         * Receives a notification when hitting the end of current object.
         * @param containerKey The key of the object which the value contains current object
         */
        public void endObject(String containerKey) {
        }

        /**
         * This method inspects the state of the current array and decides whether to split it or not. This method
         * can be overridden to accumulate and inspect state during the depth-first traversal of the JSON tree and make
         * the decision of whether to split.
         * @param containerKey The key of the object which the value contains current array
         * @return different operations to either process current array, go down the JSON tree or skip current array
         */
        public NodeOperation startArray(String containerKey) {
            return NodeOperation.DESCEND;
        }

        /**
         * Receives a notification when hitting end of array.
         * @param containerKey The key of the object which the value contains current array
         */
        public void endArray(String containerKey) {
        }

        /**
         * Construct buffered content handles with proper types from JsonParser.
         * @param containerParser the JsonParser with target object or array
         * @return the handle with target object or array as content
         */
        public abstract T makeBufferedHandle(JsonParser containerParser);

        /**
         * Construct buffered DocumentWriteOperations from the handle which contains target content
         * @param uriMaker the UriMake to construct the URI for each document
         * @param count the count of each split
         * @param handle the handle contains target object or array
         * @return DocumentWriteOperations to write to database
         */
        public DocumentWriteOperation makeDocumentWriteOperation(UriMaker uriMaker, long count, T handle) {
            if (handle == null) {
                throw new IllegalArgumentException("Handle cannot be null");
            }

            String uri = uriMaker.makeUri(count, (JSONWriteHandle) handle);

            return new DocumentWriteOperationImpl(
                    DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                    uri,
                    null,
                    handle
            );
        }

        /**
         * Serialize the target object or array in JsonParser to Strings.
         * @param containerParser the JsonParser with target object or array
         * @return Serialized string containing target object or array
         */
        public String serialize(JsonParser containerParser) {
            if (containerParser == null) {
                throw new IllegalArgumentException("JsonParser cannot be null");
            }

            try {
                StringWriter stringWriter = new StringWriter();
                JsonGenerator jsonGenerator = new JsonFactory().createGenerator(stringWriter);
                jsonGenerator.copyCurrentStructure(containerParser);
                jsonGenerator.close();
                return stringWriter.toString();
            } catch (IOException e) {
                throw new RuntimeException("Could not serialize the document", e);
            }
        }
    }

    /**
     * The basic visitor only splits objects or arrays under top array.
     */
    static public class ArrayVisitor extends Visitor<StringHandle>   {

        private int arrayDepth = 0;

        /**
         * Use the arrayDepth and containerKey to check if the current object is the target to split.
         * @param containerKey The key of the object which the value contains current object
         * @return different operations to either process current object or go down the JSON tree
         */
        public NodeOperation startObject(String containerKey) {
            if (arrayDepth > 0) {
                return NodeOperation.PROCESS;
            }

            return NodeOperation.DESCEND;
        }

        /**
         * Use the arrayDepth and containerKey to check if the current array is the target to split.
         * Also increase arrayDepth.
         * @param containerKey The key of the object which the value contains current array
         * @return different operations to either process current array or go down the JSON tree
         */
        public NodeOperation startArray(String containerKey) {
            incrementArrayDepth();

            if (arrayDepth > 1) {
                decrementArrayDepth();;
                return NodeOperation.PROCESS;
            }

            return NodeOperation.DESCEND;
        }

        /**
         * Receives a notification when hitting end of array, and decreases arrayDepth.
         * @param containerKey The key of the object which the value contains current array
         */
        public void endArray(String containerKey) {
            decrementArrayDepth();
        }

        /**
         * Get the current array depth in the JSON tree.
         * @return current array depth
         */
        public int getArrayDepth() {
            return arrayDepth;
        }

        /**
         * Increment array depth by 1 while traversing the JSON tree.
         */
        public void incrementArrayDepth() {
            arrayDepth++;
        }

        /**
         * Decrement array depth by 1 while traversing the JSON tree.
         */
        public void decrementArrayDepth() {
            arrayDepth--;
        }

        /**
         * Construct buffered StringHandles from JsonParser.
         * @param containerParser the JsonParser with target object or array
         * @return the StringHandle with target object or array
         */
        public StringHandle makeBufferedHandle(JsonParser containerParser) {
            if (containerParser == null) {
                throw new IllegalArgumentException("JsonParser cannot be null");
            }
            String content = serialize(containerParser);
            return new StringHandle(content).withFormat(Format.JSON);
        }
    }

    private static abstract class JSONSpliterator<U, T extends JSONWriteHandle> extends Spliterators.AbstractSpliterator<U> {

        private JsonParser jsonParser;

        private JsonParser getJsonParser() {
            return this.jsonParser;
        }

        private void setJsonParser(JsonParser jsonParser) {
            this.jsonParser = jsonParser;
        }

        private ArrayDeque<String> key = new ArrayDeque<>();
        private Visitor<T> visitor;
        private JSONSplitter<T> splitter;

        private void setSplitter(JSONSplitter<T> splitter) {
            if (splitter == null) {
                throw new IllegalArgumentException("JSONSplitter cannot be null");
            }
            this.splitter = splitter;
        }

        JSONSplitter<T> getSplitter() {
            return this.splitter;
        }

        JSONSpliterator(JSONSplitter<T> splitter, JsonParser jsonParser) {
            super(Long.MAX_VALUE, Spliterator.NONNULL + Spliterator.IMMUTABLE);
            setSplitter(splitter);
            setJsonParser(jsonParser);
            this.visitor = splitter.getVisitor();
        }

        T getNextHandle() {
            try {
                while (jsonParser.nextToken() != null) {
                    JsonToken currentToken = jsonParser.currentToken();

                    switch (currentToken) {
                        case FIELD_NAME:
                            key.pop();
                            key.push(jsonParser.getCurrentName());
                            break;

                        case START_OBJECT:
                            NodeOperation operation = visitor.startObject(key.peek());
                            switch (operation) {
                                case DESCEND:
                                    //For maintenance: We push key in DESCEND case only as the parser won't hit
                                    //END_OBJECT in case PROCESS and case SKIP.
                                    key.push(currentToken.asString());
                                    break;

                                case PROCESS:
                                    T handle = visitor.makeBufferedHandle(new JSONSplitter.JsonContainerParser(jsonParser));
                                    if (handle != null) {
                                        return handle;
                                    }
                                    break;

                                case SKIP:
                                    jsonParser.skipChildren();
                                    break;

                                default:
                                    throw new IllegalStateException("Unknown state");
                            }

                            break;

                        case END_OBJECT:
                            visitor.endObject(key.peek());
                            key.pop();
                            break;

                        case START_ARRAY:
                            NodeOperation operation_array = visitor.startArray(key.peek());
                            //For maintenance: this is similar switch statement as in case START_OBJECT.
                            switch (operation_array) {
                                case DESCEND:
                                    break;

                                case PROCESS:
                                    T handle = visitor.makeBufferedHandle(new JSONSplitter.JsonContainerParser(jsonParser));
                                    if (handle != null) {
                                        return handle;
                                    }
                                    break;

                                case SKIP:
                                    jsonParser.skipChildren();
                                    break;

                                default:
                                    throw new IllegalStateException("Unknown state");
                            }

                            break;

                        case END_ARRAY:
                            visitor.endArray(key.peek());
                            break;
                    }
                }

                return null;

            } catch (IOException e) {
                throw new RuntimeException("Failed to traverse document", e);
            }
        }

    }

    private static class HandleSpliterator<T extends JSONWriteHandle> extends JSONSpliterator<T, T> {

        HandleSpliterator(JSONSplitter<T> splitter, JsonParser jsonParser) {
            super(splitter, jsonParser);
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            T handle = (T) getNextHandle();
            if (handle == null) {
                return false;
            }

            getSplitter().count = getSplitter().getCount() + 1;
            action.accept(handle);

            return true;
        }
    }

    private static class DocumentWriteOperationSpliterator<T extends JSONWriteHandle> extends JSONSpliterator<DocumentWriteOperation, T> {

        DocumentWriteOperationSpliterator(JSONSplitter<T> splitter, JsonParser jsonParser) {
            super(splitter, jsonParser);
        }

        @Override
        public boolean tryAdvance(Consumer<? super DocumentWriteOperation> action) {
            T handle = (T) getNextHandle();
            if (handle == null) {
                return false;
            }

            JSONSplitter splitter = getSplitter();
            if (splitter.getUriMaker() == null) {
                JSONSplitter.UriMakerImpl uriMaker = new JSONSplitter.UriMakerImpl();
                uriMaker.setSplitFilename(splitter.splitFilename);
                uriMaker.setExtension("json");
                splitter.setUriMaker(uriMaker);
            } else {
                if (splitter.splitFilename != null) {
                    splitter.getUriMaker().setSplitFilename(splitter.splitFilename);
                }
            }

            getSplitter().count = getSplitter().getCount() + 1;
            DocumentWriteOperation documentWriteOperation = getSplitter().getVisitor().makeDocumentWriteOperation(
                    getSplitter().getUriMaker(),
                    getSplitter().getCount(),
                    handle);

            action.accept(documentWriteOperation);

            return true;
        }
    }

    private static class JsonContainerParser extends JsonParserDelegate {

        JsonContainerParser(JsonParser jsonParser) {
            super(jsonParser);
        }

        private int depth = 1;

        @Override
        public boolean isClosed() {
            return (depth > 0);
        }

        private void maintainDepth(JsonToken currentToken) {
            if (currentToken == null) return;

            switch(currentToken) {
                case START_OBJECT:
                case START_ARRAY:
                    depth++;
                    break;

                case END_OBJECT:
                case END_ARRAY:
                    depth--;
                    break;
            }
        }

        private void maintainDepth() throws IOException {
            JsonToken next = super.nextToken();
            maintainDepth(next);
        }

        @Override
        public JsonToken nextToken() throws IOException {
            if (depth == 0) {
                throw new IllegalStateException("The JSON branch is closed");
            }

            maintainDepth();
            return super.getCurrentToken();
        }

        @Override
        public Boolean nextBooleanValue() throws IOException {
            if (depth == 0) {
                return null;
            }

            maintainDepth();
            return super.getBooleanValue();
        }

        @Override
        public String nextFieldName() throws IOException {
            if (depth == 0) {
                return null;
            }

            maintainDepth();
            return super.getText();
        }

        @Override
        public int nextIntValue(int defaultValue) throws IOException {
            if (depth == 0) {
                return -1;
            }

            maintainDepth();
            return super.getIntValue();
        }

        @Override
        public long nextLongValue(long defaultValue) throws IOException {
            if (depth == 0) {
                return -1;
            }

            maintainDepth();
            return super.getLongValue();
        }

        @Override
        public String nextTextValue() throws IOException {
            if (depth == 0) {
                return null;
            }

            maintainDepth();
            return super.getText();
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException("Current JSON branch cannot be closed.");
        }
    }

    private JSONSplitter.UriMaker uriMaker;

    /**
     * Get the UriMaker of the splitter
     * @return the UriMaker of the splitter
     */
    public JSONSplitter.UriMaker getUriMaker() {
        return this.uriMaker;
    }

    /**
     * Set the UriMaker to the splitter
     * @param uriMaker the uriMaker to generate URI of each split file.
     */
    public void setUriMaker(JSONSplitter.UriMaker uriMaker) {
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
        String makeUri(long num, JSONWriteHandle handle);
    }

    private static class UriMakerImpl extends com.marklogic.client.datamovement.impl.UriMakerImpl<JSONWriteHandle>
            implements JSONSplitter.UriMaker {

    }
}
