package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.document.DocumentWriteSet;
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
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        ArrayNode contentRows = DocDescriptorUtil.buildDocDescriptors(writeSet, attachments);
        Map<String, Map<String, AbstractWriteHandle>> columnAttachments =
                attachments.isEmpty() ? null : Collections.singletonMap("doc", attachments);
        JacksonHandle content = new JacksonHandle(contentRows);
        return new ContentParam(param, content, columnAttachments);
    }
}
