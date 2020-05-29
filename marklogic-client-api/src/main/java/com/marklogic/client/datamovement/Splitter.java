/*
 * Copyright (c) 2019 MarkLogic Corporation
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
import java.util.stream.Stream;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.marker.AbstractWriteHandle;

/**
 * Splitter splits an input stream into a Java stream of write handles.
 */
public interface Splitter<T extends AbstractWriteHandle> {
    /**
     * Converts the incoming input stream to a stream of AbstractWriteHandle objects.
     * @param input is the incoming input stream.
     * @return a stream of AbstractWriteHandle objects.
     * @throws Exception if the input cannot be split
     */
    Stream<T> split(InputStream input) throws Exception;

    /**
     * Converts the incoming input stream to a stream of DocumentWriteOperation objects.
     * @param input is the incoming input stream.
     * @return a stream of DocumentWriteOperation objects.
     * @throws Exception if the input cannot be split
     */
    Stream<DocumentWriteOperation> splitWriteOperations(InputStream input) throws Exception;

    /**
     * Converts the incoming input stream to a stream of DocumentWriteOperation objects.
     * @param input is the incoming input stream.
     * @param inputName
     * @return a stream of DocumentWriteOperation objects.
     * @throws Exception if the input cannot be split
     */
    Stream<DocumentWriteOperation> splitWriteOperations(InputStream input, String inputName) throws Exception;

    /**
     * UriMaker generates URI for each split file.
     */
    interface UriMaker {
        /**
         * Get inputAfter of the UriMaker, which could be base directory
         * @return inputAfter of the UriMaker
         */
        String getInputAfter();

        /**
         * Set inputAfter of the UriMaker, which could be base URI
         * @param base inputAfter of the UriMaker
         */
        void setInputAfter(String base);

        /**
         * Get inputName of the UriMaker, which should include name and extension.
         * @return inputName of the UriMaker
         */
        String getInputName();

        /**
         * Set inputName to the UriMaker
         * @param name inputName of UriMaker, which should include both name and extension.
         */
        void setInputName(String name);
    }

    /**
     * @return the number of objects in the stream.
     */
    long getCount();

}
