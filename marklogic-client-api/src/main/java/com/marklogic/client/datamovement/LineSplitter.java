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
    private int count = 0;

    /**
     * Returns the document format set to splitter.
     * @return the document format set to splitter. The default is Format.JSON.
     */
    public Format getFormat() {
        return this.format;
    }

    /**
     * Used to set document format to splitter.
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
     * @throws IOException
     */
    @Override
    public Stream<StringHandle> split(InputStream input) throws IOException {
        return split(input, null);
    }

    /**
     * Takes the input stream and converts it into a stream of StringHandle. The content could be
     * line-delimited JSON file, line-delimited XML file or gzip-compressed line-delimited JSON file.
     * @param input is the incoming input stream.
     * @param charset is the encoding scheme the document uses.
     * @return a stream of StringHandle.
     * @throws IOException
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
     * @throws IOException
     */
    public Stream<StringHandle> split(Reader input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Reader cannot be null.");
        }
        return new BufferedReader(input)
                .lines()
                .map(line -> {
                    count++;
                    return new StringHandle(line).withFormat(getFormat());
                });
    }
}