/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement;

import java.io.InputStream;
import java.util.stream.Stream;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.marker.AbstractWriteHandle;

/**
 * Splitter splits an input stream into a Java stream of write handles. It is not thread-safe.
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
     * @param splitFilename the name and extension given to current input file. The names of split files are generated
     *                      from it.
     * @return a stream of DocumentWriteOperation objects.
     * @throws Exception if the input cannot be split
     */
    Stream<DocumentWriteOperation> splitWriteOperations(InputStream input, String splitFilename) throws Exception;

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
         * Get splitFilename of the UriMaker, which should include name and extension.
         * @return splitFilename of the UriMaker
         */
        String getSplitFilename();

        /**
         * Set splitFilename to the UriMaker
         * @param name splitFilename of UriMaker, which should include both name and extension.
         */
        void setSplitFilename(String name);
    }

    /**
     * @return the number of objects in the stream.
     */
    long getCount();

}
