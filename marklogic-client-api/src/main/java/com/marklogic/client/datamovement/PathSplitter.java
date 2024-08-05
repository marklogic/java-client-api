/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.marker.AbstractWriteHandle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * The PathSplitter utility class splits the Stream of paths into a Stream of AbstractWriteHandles or
 * DocumentWriteOperations suitable for writing in batches.
 */
public class PathSplitter {
    /**
     * The default splitter key in splitterMap
     */
    public final static String DEFAULT_SPLITTER_KEY = "default";

    private Map<String, Splitter<? extends AbstractWriteHandle>> splitterMap;
    private Path documentUriAfter;
    private final Pattern extensionRegex = Pattern.compile("\\.([^.]+)$");

    /**
     * Create a new PathSplitter with set splitterMap. By default,
     * File with extension "csv" will be applied with JacksonCSVSplitter
     * File with extension "jsonl" will be applied with LineSplitter
     * File with extension "zip" will be applied with ZipSplitter
     * File with other extension will be applied with UnarySplitter.
     * You could change the default behavior for all other extensions.
     * For example, splitting large XML file with XMLSplitter or splitting large JSON file with JSONSplitter.
     */
    public PathSplitter() {
        splitterMap = new HashMap<>();
        splitterMap.put("csv", new JacksonCSVSplitter());
        splitterMap.put("jsonl", new LineSplitter());
        splitterMap.put("zip", new ZipSplitter());
        splitterMap.put("default", new UnarySplitter());
    }

    /**
     * Get the splitterMap of the PathSplitter
     * @return the splitterMap of extensions and splitters
     */
    public Map<String, Splitter<? extends AbstractWriteHandle>> getSplitters() {
        return this.splitterMap;
    }

    /**
     * Get documentUriAfter, which is the path of the directory to process
     * @return documentUriAfter of the PathSplitter
     */
    public Path getDocumentUriAfter() {
        return this.documentUriAfter;
    }

    /**
     * set documentUriAfter to the PathSplitter
     * @param path the path of the directory which contains documents
     * @return  the created PathSplitter with documentUriAfter set
     * @throws IOException if the path is not accessible
     */
    public PathSplitter withDocumentUriAfter(Path path) throws IOException {
        documentUriAfter = path;
        return this;
    }

    /**
     * Take a stream of Paths and convert the content into a stream of AbstractWriteHandle
     * @param paths a stream of Paths of target files
     * @return a stream of AbstractWriteHandle
     * @throws Exception if the path is not accessible
     */
    public Stream<? extends AbstractWriteHandle> splitHandles(Stream<Path> paths) throws Exception{
        if (paths == null) {
            throw new IllegalArgumentException("Stream<Path> cannot be null.");
        }
        return paths.flatMap(this::flatMapHandles);
    }

    /**
     * Take a stream of Paths and convert the content into a stream of DocumentWriteOperation
     * @param paths a stream of Paths of target files
     * @return a stream of DocumentWriteOperation
     * @throws Exception if the path is not accessible
     */
    public Stream<DocumentWriteOperation> splitDocumentWriteOperations(Stream<Path> paths) throws Exception{
        if (paths == null) {
            throw new IllegalArgumentException("Stream<Path> cannot be null.");
        }
        return paths.flatMap(this::flatMapDocumentWriteOperations);
    }

    private Stream<? extends AbstractWriteHandle> flatMapHandles(Path path) {
        String extension = getExtension(path);
        Splitter splitter = lookupSplitter(extension);
        if (splitter == null) {
            return Stream.empty();
        }
        try {
            InputStream inputStream = openInputStream(path, extension);
            return splitter.split(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    private Stream<DocumentWriteOperation> flatMapDocumentWriteOperations(Path path)  {
        String extension = getExtension(path);
        Splitter splitter = lookupSplitter(extension);
        if (splitter == null) {
            return Stream.empty();
        }

        try {
            InputStream inputStream = openInputStream(path, extension);
            String filename = getFileName(path).toString();
            return splitter.splitWriteOperations(inputStream, getFileName(path).toString());
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }

    }

    private Path getFileName(Path path) {
        if (this.documentUriAfter == null) {
            return path;
        }

        Path relative = documentUriAfter.relativize(path);
        return path.toAbsolutePath().getRoot().resolve(relative);
    }

    private String getExtension(Path path) {
        Path fileName = getFileName(path);
        Matcher matcher = extensionRegex.matcher(fileName.toString());
        matcher.find();
        return matcher.group(1);
    }

    private Splitter<? extends AbstractWriteHandle> lookupSplitter(String extension) {
        Splitter splitter = splitterMap.get(extension);
        if (splitter == null && splitterMap.get(DEFAULT_SPLITTER_KEY) != null) {
            return splitterMap.get(DEFAULT_SPLITTER_KEY);
        }
        return splitter;
    }

    private InputStream openInputStream(Path path, String extension) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }

        InputStream inputStream = new BufferedInputStream(Files.newInputStream(path));
        if ("zip".equals(extension)) {
            return new ZipInputStream(inputStream);
        } else if ("gz".equals(extension)) {
            //TODO: extension for UnarySplitter case, eg line-delimited.jsonl_23efa244-ba04-4318-a43d-276290b8b63e.gz
            return new GZIPInputStream(inputStream);
        }

        return inputStream;
    }
}
