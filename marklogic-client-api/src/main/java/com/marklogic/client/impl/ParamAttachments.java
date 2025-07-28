package com.marklogic.client.impl;

import com.marklogic.client.io.marker.AbstractWriteHandle;

import java.util.Map;

/**
 * Captures attachments associated with a parameter name in a {@code PlanBase} along with the column that the
 * client uses to refer to attachments. The {@code attachments} map associates attachment names and attachment
 * content. It is expected that the values in the given {@code columnName} match the attachment names.
 */
class ParamAttachments {

    private PlanBuilderBaseImpl.PlanParamBase planParam;
    private String columnName;
    private Map<String, AbstractWriteHandle> attachments;

    public ParamAttachments(PlanBuilderBaseImpl.PlanParamBase planParam, String columnName, Map<String, AbstractWriteHandle> attachments) {
        this.planParam = planParam;
        this.columnName = columnName;
        this.attachments = attachments;
    }

    public PlanBuilderBaseImpl.PlanParamBase getPlanParam() {
        return planParam;
    }

    public String getColumnName() {
        return columnName;
    }

    public Map<String, AbstractWriteHandle> getAttachments() {
        return attachments;
    }
}
