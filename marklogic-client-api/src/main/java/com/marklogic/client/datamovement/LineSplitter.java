/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

import java.io.*;
import java.nio.charset.Charset;
import java.util.stream.Stream;

/**
 * The LineSplitter class is used to separate lines in line-delimited JSON, XML or TEXT files.
 * It should also work with gzip-compressed line-delimited files.
 */
public class LineSplitter implements Splitter<StringHandle> {
    private Format format = Format.JSON;
    private long count = 0;

    /**
     * Returns the document format set to splitter.
     * @return the document format set to splitter. The default is Format.JSON.
     */
    public Format getFormat() {
        return this.format;
    }

    /**
     * Used to set document format to splitter. The extension of URLs generated for splits is determined by this format.
     * The format should be set before splitting. If not set, the default is JSON.
     * @param format the document content format.
     */
    public void setFormat(Format format) {
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null.");
        }

        this.format = format;
    }

    /**
     * Used to return the number of objects in the stream.
     * @return the number of objects in the stream.
     */
    @Override
    public long getCount() {
        return count;
    }

    /**
     * Takes the input stream and converts it into a stream of StringHandle. The content could be
     * line-delimited JSON, XML or TEXT file. It could also be gzip-compressed line-delimited files.
     * Provide GZIPInputStream to the splitter when splitting gzip files.
     * @param input is the incoming input stream.
     * @return a stream of StringHandle.
     * @throws IOException if the input cannot be split
     */
    @Override
    public Stream<StringHandle> split(InputStream input) throws IOException {
        return split(input, null);
    }

    /**
     * Takes the input stream and converts it into a stream of DocumentWriteOperation. The content could be
     * line-delimited JSON, XML or TEXT file. It could also be gzip-compressed line-delimited files.
     * Provide GZIPInputStream to the splitter when splitting gzip files.
     * @param input is the incoming input stream.
     * @return a stream of DocumentWriteOperation.
     * @throws Exception if the input cannot be split
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input) throws Exception {
        return splitWriteOperations(input, null);
    }

    /**
     * Takes the input stream and input file name and converts it into a stream of DocumentWriteOperation. The content
     * could be line-delimited JSON, XML or TEXT file. It could also be gzip-compressed line-delimited files.
     * Provide GZIPInputStream to the splitter when splitting gzip files.
     * @param input is the incoming input stream.
     * @param splitFilename is the name of the input file, including name and extension. It is used to generate URLs for
     *                  split files. The splitFilename could either be provided here or in user-defined UriMaker.
     * @return a stream of DocumentWriteOperation.
     * @throws Exception if the input cannot be split
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input, String splitFilename) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("InputStream cannot be null.");
        }

        count = 0;
        String extension = getFormat().getDefaultExtension();
        if (getUriMaker() == null) {
            LineSplitter.UriMakerImpl uriMaker = new LineSplitter.UriMakerImpl();
            uriMaker.setSplitFilename(splitFilename);
            uriMaker.setExtension(extension);
            setUriMaker(uriMaker);
        } else {
            if (splitFilename != null) {
                getUriMaker().setSplitFilename(splitFilename);
            }
            if (getUriMaker() instanceof LineSplitter.UriMakerImpl) {
                ((LineSplitter.UriMakerImpl)getUriMaker()).setExtension(extension);
            }
        }

        return new BufferedReader(new InputStreamReader(input))
                .lines()
                .filter(line -> line.length() != 0)
                .map(line -> {
                    count = count + 1;
                    StringHandle handle = new StringHandle(line);
                    String uri = getUriMaker().makeUri(count, handle);
                    return new DocumentWriteOperationImpl(
                            DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                            uri,
                            null,
                            handle
                    );
                });
    }

    /**
     * Takes the input stream and converts it into a stream of StringHandle. The content could be
     * line-delimited JSON file, line-delimited XML file or gzip-compressed line-delimited JSON file.
     * @param input is the incoming input stream.
     * @param charset is the encoding scheme the document uses.
     * @return a stream of StringHandle.
     * @throws IOException if the input cannot be split
     */
    public Stream<StringHandle> split(InputStream input, Charset charset) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("InputStream cannot be null.");
        }

        return split( (charset == null) ?
                new InputStreamReader(input) :
                new InputStreamReader(input, charset));
    }

    /**
     *Takes the Reader input and converts it into a stream of StringHandle. The content could be
     * line-delimited JSON file, line-delimited XML file or gzip-compressed line-delimited JSON file.
     * @param input is the incoming Reader.
     * @return a stream of StringHandle.
     * @throws IOException if the input cannot be split
     */
    public Stream<StringHandle> split(Reader input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Reader cannot be null.");
        }

        count = 0;
        return new BufferedReader(input)
                .lines()
                .filter(line -> line.length() != 0)
                .map(line -> {
                    count = count + 1;
                    return new StringHandle(line).withFormat(getFormat());
                });
    }

    private LineSplitter.UriMaker uriMaker;

    /**
     * Get the UriMaker of the splitter
     * @return the UriMaker of the splitter
     */
    public LineSplitter.UriMaker getUriMaker() {
        return this.uriMaker;
    }

    /**
     * Set the UriMaker to the splitter
     * @param uriMaker the uriMaker to generate URI of each split file.
     */
    public void setUriMaker(LineSplitter.UriMaker uriMaker) {
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
        String makeUri(long num, StringHandle handle);
    }

    private static class UriMakerImpl extends com.marklogic.client.datamovement.impl.UriMakerImpl<StringHandle> implements LineSplitter.UriMaker {
    }
}
