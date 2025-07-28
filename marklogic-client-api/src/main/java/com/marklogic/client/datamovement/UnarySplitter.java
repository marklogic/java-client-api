/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;

import java.io.InputStream;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * UnarySplitter utility class makes it possible to add entire files when splitting paths, either as
 * the default splitter or for specific extensions.
 */
public class UnarySplitter implements Splitter<InputStreamHandle> {
    private UnarySplitter.UriMaker uriMaker;
    private long count = 0;

    /**
     * Get the UriMaker of the splitter
     * @return the UriMaker of the splitter
     */
    public UnarySplitter.UriMaker getUriMaker() {
        return this.uriMaker;
    }

    /**
     * Set the UriMaker to the splitter
     * @param uriMaker the uriMaker to generate URI of each split file.
     */
    public void setUriMaker(UnarySplitter.UriMaker uriMaker) {
        if (uriMaker == null) {
            throw new IllegalArgumentException("uriMaker cannot be null.");
        }
        this.uriMaker = uriMaker;
    }

    /**
     * Takes a input stream of a file and convert the entire file to a stream of InputStreamHandle
     * @param input is the incoming input stream.
     * @return a stream of InputStreamHandle
     * @throws Exception if input cannot be split
     */
    public Stream<InputStreamHandle> split(InputStream input) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        count = 0;

        InputStreamHandle handle = new InputStreamHandle(input);
        return Stream.of(handle);
    }

    /**
     * Takes a input stream of a file and convert the entire file to a stream of DocumentWriteOperation
     * @param input is the incoming input stream.
     * @return a stream of DocumentWriteOperation
     * @throws Exception if input cannot be split
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input) throws Exception {
        return splitWriteOperations(input, null);
    }

    /**
     * Takes a input stream and the name of a file, then convert the entire file to a stream of DocumentWriteOperation
     * @param input is the incoming input stream.
     * @param splitFilename is the file name, including name and extension. It is used to generate URLs for split files.
     *                  The splitFilename could either be provided here or in user-defined UriMaker.
     * @return a stream of DocumentWriteOperation
     * @throws Exception if input cannot be split
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input, String splitFilename) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        count = 0;
        InputStreamHandle handle = new InputStreamHandle(input);

        if (getUriMaker() == null) {
            UnarySplitter.UriMaker uriMaker = new UnarySplitter.UriMakerImpl();
            setUriMaker(uriMaker);
        }

        if (splitFilename != null) {
            uriMaker.setSplitFilename(splitFilename);
        }

        String uri = uriMaker.makeUri(handle);

        DocumentWriteOperationImpl documentWriteOperation = new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                uri,
                null,
                handle
        );

        this.count = getCount() + 1;
        return Stream.of(documentWriteOperation);
    }

    /**
     * Returns the number of splits. In this case, should be at most 1.
     * @return the number of splits
     */
    public long getCount() {
        return this.count;
    }

    /**
     * UriMaker which generates URI for each split file
     */
    public interface UriMaker extends Splitter.UriMaker {
        /**
         *
         * @param handle the handle which contains the content of each split. It could be utilized to make a meaningful
         *               document URI.
         * @return the generated URI of current split
         */
        String makeUri(InputStreamHandle handle);
    }

    static private class UriMakerImpl extends com.marklogic.client.datamovement.impl.UriMakerImpl<BytesHandle> implements UnarySplitter.UriMaker {

        @Override
        public String makeUri(InputStreamHandle handle) {
            StringBuilder uri = new StringBuilder();

            if (getInputAfter() != null && getInputAfter().length() != 0) {
                uri.append(getInputAfter());
            }

            if (getSplitFilename() != null && getSplitFilename().length() != 0) {
                uri.append(getName());
            }

            uri.append("_").append(UUID.randomUUID()).append(".").append(getExtension());
            return uri.toString();
        }
    }

}
