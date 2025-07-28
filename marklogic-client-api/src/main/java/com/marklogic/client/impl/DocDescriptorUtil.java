/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Helper for constructing document descriptor rows, either when using {@code fromDocDescriptors} or when using
 * {@code fromParam} and {@code bindParam} together.
 */
class DocDescriptorUtil {

    public static ArrayNode buildDocDescriptors(DocumentWriteSet writeSet) {
        return buildDocDescriptors(writeSet, null);
    }

    public static ArrayNode buildDocDescriptors(DocumentWriteSet writeSet,
                                                Map<String, AbstractWriteHandle> attachments) {
        ArrayNode docDescriptors = new ObjectMapper().createArrayNode();
        writeSet.stream().forEach(writeOp -> populateDocDescriptor(writeOp, docDescriptors.addObject(), attachments));
        return docDescriptors;
    }

    public static void populateDocDescriptor(DocumentWriteOperation writeOp, ObjectNode docDescriptor) {
        populateDocDescriptor(writeOp, docDescriptor, null);
    }

    public static void populateDocDescriptor(DocumentWriteOperation writeOp,
                                             ObjectNode docDescriptor,
                                             Map<String, AbstractWriteHandle> attachments) {
        String uri = writeOp.getUri();
        docDescriptor.put("uri", uri);

		final String temporalURI = writeOp.getTemporalDocumentURI();
        if (temporalURI != null && temporalURI.trim().length() > 0) {
            docDescriptor.put("temporalCollection", writeOp.getTemporalDocumentURI());
        }

        AbstractWriteHandle content = writeOp.getContent();
        // content can be null for when user only wants to populate metadata
        if (content != null) {
            JsonNode jsonContent = getJsonNodeFromContent(content);
            if (jsonContent != null) {
                // JSON attachments aren't yet supported by the REST endpoint, so they are always inlined, regardless
                // of whether client provided an attachments map or not
                docDescriptor.set("doc", jsonContent);
            } else if (attachments != null) {
                docDescriptor.put("doc", "attachment-" + uri);
                attachments.put("attachment-" + uri, content);
            } else {
                // As of the 5.6.0 release, an assumption is made here that if no attachments map is provided, then
                // fromDocDescriptors is being used, which only supports JSON content
                throw new IllegalArgumentException("Only JSON content can be used with fromDocDescriptors; " +
                        "non-JSON content found for document with URI: " + uri);
            }
        }

        if (writeOp.getMetadata() instanceof DocumentMetadataHandle) {
            populateMetadata((DocumentMetadataHandle) writeOp.getMetadata(), docDescriptor);
        } else if (writeOp.getMetadata() != null) {
            LoggerFactory.getLogger(DocDescriptorUtil.class).warn("Can only add metadata to a doc descriptor when " +
                    "the class is an instance of DocumentMetadataHandle; document URI: " + uri);
        }
    }

    private static void populateMetadata(DocumentMetadataHandle metadata, ObjectNode docDescriptor) {
        docDescriptor.put("quality", metadata.getQuality());

        if (!metadata.getCollections().isEmpty()) {
            ArrayNode collections = docDescriptor.putArray("collections");
            metadata.getCollections().forEach(c -> collections.add(c));
        }

        if (!metadata.getPermissions().isEmpty()) {
            ArrayNode permissions = docDescriptor.putArray("permissions");
            for (String roleName : metadata.getPermissions().keySet()) {
                for (DocumentMetadataHandle.Capability c : metadata.getPermissions().get(roleName)) {
                    permissions.addObject().put("roleName", roleName).put("capability", c.toString().toLowerCase());
                }
            }
        }

        if (!metadata.getMetadataValues().isEmpty()) {
            ObjectNode values = docDescriptor.putObject("metadata");
            for (String key : metadata.getMetadataValues().keySet()) {
                values.put(key, metadata.getMetadataValues().get(key));
            }
        }
    }

    private static JsonNode getJsonNodeFromContent(AbstractWriteHandle content) {
        if (content instanceof JacksonHandle) {
            return ((JacksonHandle) content).get();
        }
        if (content instanceof BaseHandle) {
            BaseHandle h = (BaseHandle) content;
            if (Format.JSON.equals(h.getFormat())) {
                String json = HandleAccessor.contentAsString(content);
                try {
                    return new ObjectMapper().readTree(json);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Unable to read content as JSON; cause: " + e.getMessage(), e);
                }
            }
        }
        return null;
    }
}
