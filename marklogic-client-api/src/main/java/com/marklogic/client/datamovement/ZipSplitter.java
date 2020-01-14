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

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.Format;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The ZipSplitter class is used to split compressed files.
 * The zip file could include XML, JSON, TXT or BINARY files.
 */
public class ZipSplitter implements Splitter<BytesHandle> {
    private Map<String, Format> extensionFormats;
    private Predicate<ZipEntry> entryFilter;
    private Function<String, String> uriTransformer;
    private int count = 0;

    /**
     * Returns the extensionFormats set to splitter. The extensionFormat is pre-defined in splitter.
     * It includes "json", "txt" and "xml" extensions. If the file has no extension, it is treated as binary file.
     * You can also add mappings from other extensions in the zipfile to one of the four MarkLogic formats.
     * @return a map of extensionFormats
     */
    public Map<String, Format> getExtensionFormats() {
        return this.extensionFormats;
    }

    /**
     * Returns the entryFilter set to splitter.
     * @return the entryFilter set to splitter
     */
    public Predicate<ZipEntry> getEntryFilter() {
        return this.entryFilter;
    }

    /**
     * Used to set entryFilter to splitter. The entryFilter is a lambda function, which can be used to inspect
     * the zip entry and return false for any document in the zipfile that should be ignored.
     * @param entryFilter the filter that applied to each zipEntry
     */
    public void setEntryFilter(Predicate<ZipEntry> entryFilter) {
        this.entryFilter = entryFilter;
    }

    /**
     * Returns the uriTransformer set to splitter.
     * @return the uriTransformer set to splitter
     */
    public Function<String, String> getUriTransformer() {
        return this.uriTransformer;
    }

    /**
     * Used to set uriTransformer to splitter. The uriTransformer is a lambda function, which can be used to
     * transform the name of the document in the zipfile into the document URI for the database.
     * @param uriTransformer the uriTransformer which applied on each document URI
     */
    public void setUriTransformer(Function<String, String> uriTransformer) {
        this.uriTransformer = uriTransformer;
    }

    /**
     * Create a new ZIP splitter.
     */
    public ZipSplitter() {
        extensionFormats = new HashMap<>();
        extensionFormats.put("", Format.UNKNOWN);
        extensionFormats.put("json", Format.JSON);
        extensionFormats.put("txt", Format.TEXT);
        extensionFormats.put("xml", Format.XML);
    }

    @Override
    public long getCount() {
        return this.count;
    }

    /**
     * Takes a input stream of a ZIP file and convert it to a stream of BytesHandle.
     * The input stream must be a ZipInputStream, otherwise it will throw an exception.
     * The ZIP file could contain XML, JSON, TXT and BINARY files.
     * @param input is the incoming input stream
     * @return a stream of BytesHandle
     * @throws IOException if input cannot be split
     */
    @Override
    public Stream<BytesHandle> split(InputStream input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        if (!(input instanceof ZipInputStream)) {
            throw new IllegalArgumentException("Input should be an instance of ZipInputStream");
        }

        return split((ZipInputStream) input);
    }

    /**
     * Takes a ZipInputStream of a ZIP file and convert it to a stream of BytesHandle.
     * The ZIP file could contain XML, JSON, TXT and BINARY files.
     * @param input is a ZipInputStream of a zip file
     * @return a stream of BytesHandle
     * @throws IOException if input cannot be split
     */
    public Stream<BytesHandle> split(ZipInputStream input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        BytesHandleSpliterator bytesHandleSpliterator = new BytesHandleSpliterator(this);
        bytesHandleSpliterator.setZipStream(input);
        bytesHandleSpliterator.setEntryFilter(this.entryFilter);
        bytesHandleSpliterator.setExtensionFormats(this.extensionFormats);

        return StreamSupport.stream(bytesHandleSpliterator, true);
    }

    /**
     * Takes a ZipInputStream of a ZIP file and convert it to a stream of DocumentWriteOperation.
     * The ZIP file could contain XML, JSON, TXT and BINARY files.
     * @param input is a ZipInputStream of a zip file
     * @return a stream of DocumentWriteOperation
     * @throws IOException if input cannot be split
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(ZipInputStream input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        DocumentWriteOperationSpliterator documentWriteOperationSpliterator =
                new DocumentWriteOperationSpliterator(this);
        documentWriteOperationSpliterator.setZipStream(input);
        documentWriteOperationSpliterator.setEntryFilter(this.entryFilter);
        documentWriteOperationSpliterator.setExtensionFormats(this.extensionFormats);

        return StreamSupport.stream(documentWriteOperationSpliterator, true);
    }

    private static class FormatEntry {
        private ZipEntry zipEntry;
        private Format   format;

        private ZipEntry getZipEntry() {
            return this.zipEntry;
        }

        private void setZipEntry(ZipEntry zipEntry) {
            if (zipEntry == null) {
                throw new IllegalArgumentException("ZipEntry cannot be null");
            }

            this.zipEntry = zipEntry;
        }

        private Format getFormat() {
            return this.format;
        }

        private void setFormat(Format format) {
            if (format == null) {
                throw new IllegalArgumentException("Format cannot be null");
            }

            this.format = format;
        }
    }

    private static abstract class ZipEntrySpliterator<T> extends Spliterators.AbstractSpliterator<T> {
        private static Pattern extensionRegex = Pattern.compile("\\.([^.]+)$");
        private ZipInputStream zipStream;
        private Map<String,Format> extensionFormats;
        private Predicate<ZipEntry> entryFilter;

        ZipEntrySpliterator(long est, int additionalCharacteristics) {
            super(est, additionalCharacteristics);
        }

        ZipInputStream getZipStream() {
            return this.zipStream;
        }

        void setZipStream(ZipInputStream zipStream) {
            if (zipStream == null) {
                throw new IllegalArgumentException("ZipStream cannot be null");
            }

            this.zipStream = zipStream;
        }

        Map<String, Format> getExtensionFormats() {
            return this.extensionFormats;
        }

        void setExtensionFormats(Map<String, Format> extensionFormats) {
            if (extensionFormats == null) {
                throw new IllegalArgumentException("ExtensionFormats cannot be null");
            }
            this.extensionFormats = extensionFormats;
        }

        Predicate<ZipEntry> getEntryFilter() {
            return this.entryFilter;
        }

        void setEntryFilter(Predicate<ZipEntry> entryFilter) {
            this.entryFilter = entryFilter;
        }

        protected FormatEntry getNextEntry() throws IOException {
            ZipEntry candidateEntry;

            while ((candidateEntry = getZipStream().getNextEntry()) != null) {
                if (getEntryFilter() != null && !getEntryFilter().test(candidateEntry)) {
                    continue;
                }

                String name = candidateEntry.getName();
                Matcher matcher = extensionRegex.matcher(name);
                matcher.find();
                String extension = matcher.group(1);
                Format format = getExtensionFormats().get(extension);

                if (format == null) {
                    format = getExtensionFormats().get("");
                }

                if (format == null || format == Format.UNKNOWN) {
                    continue;
                }

                FormatEntry newEntry = new FormatEntry();
                newEntry.setFormat(format);
                newEntry.setZipEntry(candidateEntry);

                return newEntry;
            }

            return null;
        }

        protected BytesHandle readEntry(FormatEntry entry) throws IOException {
            byte[] content = new byte[(int) entry.getZipEntry().getSize()];
            getZipStream().read(content, 0, content.length);
            return new BytesHandle(content).withFormat(entry.getFormat());
        }
    }

    private static class BytesHandleSpliterator extends ZipEntrySpliterator<BytesHandle> {

        private ZipSplitter splitter;

        protected BytesHandleSpliterator(ZipSplitter splitter) {
            super(Long.MAX_VALUE, Spliterator.NONNULL + Spliterator.IMMUTABLE);
            this.splitter = splitter;
        }

        @Override
        public boolean tryAdvance(Consumer<? super BytesHandle> action) {
            try {
                FormatEntry nextEntry = getNextEntry();
                if (nextEntry == null) {
                    return false;
                }

                BytesHandle nextBytesHandle = readEntry(nextEntry);
                action.accept(nextBytesHandle);
                splitter.count++;

            } catch (IOException e) {
                throw new RuntimeException("Could not read ZipEntry", e);
            }

            return true;
        }
    }

    private static class DocumentWriteOperationSpliterator extends ZipEntrySpliterator<DocumentWriteOperation> {

        private ZipSplitter splitter;

        protected DocumentWriteOperationSpliterator(ZipSplitter splitter) {
            super(Long.MAX_VALUE, Spliterator.NONNULL + Spliterator.IMMUTABLE);
            this.splitter = splitter;
        }

        @Override
        public boolean tryAdvance(Consumer<? super DocumentWriteOperation> action) {
            try {
                FormatEntry nextEntry = getNextEntry();

                if (nextEntry == null) {
                    return false;
                }

                BytesHandle nextBytesHandle = readEntry(nextEntry);
                String name = nextEntry.getZipEntry().getName();

                String uri = name;
                if (splitter.uriTransformer != null) {
                    uri = splitter.uriTransformer.apply(name);
                }

                DocumentWriteOperationImpl documentWriteOperation = new DocumentWriteOperationImpl(
                        DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                        uri,
                        null,
                        nextBytesHandle
                );
                action.accept(documentWriteOperation);
                splitter.count++;

            } catch (IOException e) {
                throw new RuntimeException("Could not read ZipEntry", e);
            }

            return true;
        }
    }
}
