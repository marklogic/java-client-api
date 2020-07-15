/*
 * Copyright 2020 MarkLogic Corporation
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
    private ThreadLocal<Long> count = new ThreadLocal<>();

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
        count.set(0L);

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
     * @param inputName is the file name, including name and extension. It is used to generate URLs for split files.
     *                  The inputName could either be provided here or in user-defined UriMaker.
     * @return a stream of DocumentWriteOperation
     * @throws Exception if input cannot be split
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input, String inputName) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        count.set(0L);
        InputStreamHandle handle = new InputStreamHandle(input);

        if (getUriMaker() == null) {
            UnarySplitter.UriMaker uriMaker = new UnarySplitter.UriMakerImpl();
            setUriMaker(uriMaker);
        }

        if (inputName != null) {
            uriMaker.setInputName(inputName);
        }

        String uri = uriMaker.makeUri(handle);

        DocumentWriteOperationImpl documentWriteOperation = new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                uri,
                null,
                handle
        );

        this.count.set(getCount() + 1);
        return Stream.of(documentWriteOperation);
    }

    /**
     * Returns the number of splits. In this case, should be at most 1.
     * @return the number of splits
     */
    public long getCount() {
        return this.count.get();
    }

    /**
     * UriMaker which generates URI for each split file
     */
    public interface UriMaker extends Splitter.UriMaker {
        String makeUri(InputStreamHandle handle);
    }

    static private class UriMakerImpl extends com.marklogic.client.datamovement.impl.UriMakerImpl<BytesHandle> implements UnarySplitter.UriMaker {

        @Override
        public String makeUri(InputStreamHandle handle) {
            StringBuilder uri = new StringBuilder();

            if (getInputAfter() != null && getInputAfter().length() != 0) {
                uri.append(getInputAfter());
            }

            if (getInputName() != null && getInputName().length() != 0) {
                uri.append(getName());
            }

            uri.append("_").append(UUID.randomUUID()).append(".").append(getExtension());
            return uri.toString();
        }
    }

}