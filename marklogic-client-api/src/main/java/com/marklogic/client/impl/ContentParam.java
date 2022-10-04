package com.marklogic.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines a content parameter that can be bound to a plan, optionally with a mapping of column names to
 * attachments.
 */
class ContentParam {

    private PlanBuilderBaseImpl.PlanParamBase planParam;
    private AbstractWriteHandle content;
    private Map<String, Map<String, AbstractWriteHandle>> columnAttachments;

    public ContentParam(PlanBuilderBaseImpl.PlanParamBase planParam, AbstractWriteHandle content) {
        this.planParam = planParam;
        this.content = content;
    }

    public ContentParam(PlanBuilderBaseImpl.PlanParamBase planParam, AbstractWriteHandle content, Map<String, Map<String, AbstractWriteHandle>> columnAttachments) {
        this(planParam, content);
        this.columnAttachments = columnAttachments;
    }

    public PlanBuilderBaseImpl.PlanParamBase getPlanParam() {
        return planParam;
    }

    public AbstractWriteHandle getContent() {
        return content;
    }

    public Map<String, Map<String, AbstractWriteHandle>> getColumnAttachments() {
        return columnAttachments;
    }

    /**
     * Convenience method that is less-than-ideally located here as a way of avoiding duplication between the
     * two classes that must implement {@code bindParam(String, DocumentWriteSet)} - {@code PlanBuilderSubImpl} and
     * {@code RawPlanImpl}. Those classes do not have a common parent class, and so they both depend on this method
     * to avoid duplicating the logic in both classes.
     *
     * @param param
     * @param writeSet
     * @return
     */
    public static ContentParam fromDocumentWriteSet(PlanBuilderBaseImpl.PlanParamBase param, DocumentWriteSet writeSet) {
        ArrayNode contentRows = new ObjectMapper().createArrayNode();
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();

        writeSet.stream().forEach(writeOp -> {
            String uri = writeOp.getUri();
            AbstractWriteHandle content = writeOp.getContent();

            ObjectNode row = contentRows.addObject();
            row.put("uri", uri);

            JsonNode jsonContent = getJsonNodeFromContent(content);
            if (jsonContent != null) {
                // JSON attachments aren't supported yet, so we need to inline it
                row.set("doc", jsonContent);
            } else {
                // For every other content type, make an attachment
                row.put("doc", "attachment-" + uri);
                attachments.put("attachment-" + uri, content);
            }

            if (writeOp.getMetadata() instanceof DocumentMetadataHandle) {
                DocumentMetadataHandle metadata = (DocumentMetadataHandle) writeOp.getMetadata();
                row.put("quality", metadata.getQuality());

                if (!metadata.getCollections().isEmpty()) {
                    ArrayNode collections = row.putArray("collections");
                    metadata.getCollections().forEach(c -> collections.add(c));
                }

//          if (!metadata.getPermissions().isEmpty()) {
//            ArrayNode permissions = row.putArray("permissions");
//            for (String roleName : metadata.getPermissions().keySet()) {
//              for (DocumentMetadataHandle.Capability c : metadata.getPermissions().get(roleName)) {
//                permissions.addObject().put("roleId", "7089338530631756591").put("capability", c.toString().toLowerCase());
//              }
//            }
//          }

                // Hack until the REST endpoint supports a JSON serialization of permissions
                ArrayNode permissions = row.putArray("permissions");
                permissions.addObject().put("roleId", "7089338530631756591").put("capability", "read");
                permissions.addObject().put("roleId", "7089338530631756591").put("capability", "update");

                if (!metadata.getMetadataValues().isEmpty()) {
                    ObjectNode values = row.putObject("metadata");
                    for (String key : metadata.getMetadataValues().keySet()) {
                        values.put(key, metadata.getMetadataValues().get(key));
                    }
                }
            }
        });

        Map<String, Map<String, AbstractWriteHandle>> columnAttachments = attachments.isEmpty() ? null :
                Collections.singletonMap("doc", attachments);
        JacksonHandle content = new JacksonHandle(contentRows);
        return new ContentParam(param, content, columnAttachments);
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
