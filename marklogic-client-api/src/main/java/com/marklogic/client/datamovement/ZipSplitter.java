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

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.Format;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
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
     * Takes a input stream of a ZIP file and convert it to a stream of DocumentWriteOperation.
     * The input stream must be a ZipInputStream, otherwise it will throw an exception.
     * The ZIP file could contain XML, JSON, TXT and BINARY files.
     * @param input is the incoming input stream.
     * @return a stream of DocumentWriteOperation
     * @throws Exception
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        if (!(input instanceof ZipInputStream)) {
            throw new IllegalArgumentException("Input should be an instance of ZipInputStream");
        }

        return splitWriteOperations((ZipInputStream) input);
    }

    /**
     * Takes a input stream and name of a ZIP file and convert it to a stream of DocumentWriteOperation.
     * The input stream must be a ZipInputStream, otherwise it will throw an exception.
     * The ZIP file could contain XML, JSON, TXT and BINARY files.
     * @param input is the incoming input stream.
     * @param inputName is the file name, including name and extension
     * @return a stream of DocumentWriteOperation
     * @throws Exception
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input, String inputName) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        if (!(input instanceof ZipInputStream)) {
            throw new IllegalArgumentException("Input should be an instance of ZipInputStream");
        }

        return splitWriteOperations((ZipInputStream) input, inputName);
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

        return splitWriteOperations(input, null);
    }

    /**
     * Takes a ZipInputStream and name of a ZIP file and convert it to a stream of DocumentWriteOperation.
     * The ZIP file could contain XML, JSON, TXT and BINARY files.
     * @param input is a ZipInputStream of a zip file
     * @param inputName is the file name, including name and extension
     * @return a stream of DocumentWriteOperation
     * @throws IOException if input cannot be split
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(ZipInputStream input, String inputName) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        DocumentWriteOperationSpliterator documentWriteOperationSpliterator =
                new DocumentWriteOperationSpliterator(this);
        documentWriteOperationSpliterator.setZipStream(input);
        documentWriteOperationSpliterator.setEntryFilter(this.entryFilter);
        documentWriteOperationSpliterator.setExtensionFormats(this.extensionFormats);

        if (getUriMaker() == null) {
            ZipSplitter.UriMakerImpl uriMaker = new ZipSplitter.UriMakerImpl();
            setUriMaker(uriMaker);
        }
        getUriMaker().setInputName(inputName);

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
        private static Pattern extensionRegex = Pattern.compile("^(.+)\\.([^.]+)$");
        private ZipInputStream zipStream;
        private Map<String,Format> extensionFormats;
        private Predicate<ZipEntry> entryFilter;
        private String[] entryNameExtension = new String[] {"", ""};

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

        String[] getEntryNameExtension() {
            return this.entryNameExtension;
        }

        void setEntryNameExtension(String name, String extension) {
            this.entryNameExtension[0] = name;
            this.entryNameExtension[1] = extension;
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
                setEntryNameExtension(matcher.group(1), matcher.group(2));
                Format format = getExtensionFormats().get(entryNameExtension[1]);

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

                splitter.count++;
                BytesHandle nextBytesHandle = readEntry(nextEntry);
                action.accept(nextBytesHandle);

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

                splitter.count++;
                BytesHandle nextBytesHandle = readEntry(nextEntry);
                String name = this.getEntryNameExtension()[0];
                String extension = this.getEntryNameExtension()[1];
                ZipSplitter.UriMakerImpl uriMaker = (ZipSplitter.UriMakerImpl) splitter.getUriMaker();
                uriMaker.setExtension(extension);
                String uri = uriMaker.makeUri(splitter.count, name, nextBytesHandle);

                DocumentWriteOperationImpl documentWriteOperation = new DocumentWriteOperationImpl(
                        DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                        uri,
                        null,
                        nextBytesHandle
                );
                action.accept(documentWriteOperation);

            } catch (IOException e) {
                throw new RuntimeException("Could not read ZipEntry", e);
            }

            return true;
        }
    }

    private UriMaker uriMaker;

    /**
     * Get the UriMaker of the splitter
     * @return the UriMaker of the splitter
     */
    public UriMaker getUriMaker() {
        return this.uriMaker;
    }

    /**
     * Set the UriMaker to the splitter
     * @param uriMaker the uriMaker to generate URI of each split file.
     */
    public void setUriMaker(UriMaker uriMaker) {
        this.uriMaker = uriMaker;
    }

    /**
     * UriMaker which generates URI for each split file
     */
    interface UriMaker extends Splitter.UriMaker {
        /**
         * Generates URI for each split
         * @param num the count of each split
         * @param entryName the name of each entry in the zip file
         * @param handle the handle which contains the content of each split
         * @return the generated URI of current split
         */
        String makeUri(int num, String entryName, BytesHandle handle);
    }

    static private class UriMakerImpl extends com.marklogic.client.datamovement.impl.UriMakerImpl<BytesHandle> implements ZipSplitter.UriMaker {

        @Override
        public String makeUri(int num, String entryName, BytesHandle handle) {
            String prefix = "";
            String uri;

            if (getInputAfter() != null && getInputAfter().length() != 0) {
                prefix += getInputAfter();
            }

            if (getInputName() != null && getInputName().length() != 0) {
                prefix += getName();
            }

            prefix += "/" + entryName;
            uri = prefix + num + "_" + UUID.randomUUID() + "." + getExtension();
            return uri;
        }
    }
}
