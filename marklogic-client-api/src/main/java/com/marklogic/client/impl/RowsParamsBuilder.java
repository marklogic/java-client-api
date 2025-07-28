package com.marklogic.client.impl;

import com.marklogic.client.impl.PlanBuilderBaseImpl.RequestPlan;
import com.marklogic.client.row.RowManager.RowSetPart;
import com.marklogic.client.row.RowManager.RowStructure;
import com.marklogic.client.util.RequestParameters;

import java.util.Map;

/**
 * Helper for constructing request parameters for calls to /v1/rows.
 */
class RowsParamsBuilder {

    private RequestParameters params = new RequestParameters();

    public RowsParamsBuilder(RequestPlan requestPlan) {
        Map<PlanBuilderBaseImpl.PlanParamBase, BaseTypeImpl.ParamBinder> planParams = requestPlan.getParams();
        if (planParams != null) {
            for (Map.Entry<PlanBuilderBaseImpl.PlanParamBase, BaseTypeImpl.ParamBinder> entry : planParams.entrySet()) {
                BaseTypeImpl.ParamBinder binder = entry.getValue();
                StringBuilder nameBuf = new StringBuilder("bind:");
                nameBuf.append(entry.getKey().getName());
                String paramQual = binder.getParamQualifier();
                if (paramQual != null) {
                    nameBuf.append(paramQual);
                }
                params.add(nameBuf.toString(), binder.getParamValue());
            }
        }
    }

    public RowsParamsBuilder withOutput(String output) {
        if (output != null) {
            params.add("output", output);
        }
        return this;
    }

    public RowsParamsBuilder withOutput(RowStructure rowStructure) {
        if (rowStructure != null) {
            switch (rowStructure) {
                case ARRAY:
                    return withOutput("array");
                case OBJECT:
                    return withOutput("object");
            }
        }
        return this;
    }

    public RowsParamsBuilder withColumnTypes(RowSetPart columnTypes) {
        if (columnTypes != null) {
            switch (columnTypes) {
                case HEADER:
                    params.add("column-types", "header");
                    break;
                case ROWS:
                    params.add("column-types", "rows");
                    break;
            }
        }
        return this;
    }

    public RowsParamsBuilder withRowFormat(String rowFormat) {
        if (rowFormat != null) {
            params.add("row-format", rowFormat);
        }
        return this;
    }

    public RowsParamsBuilder withNodeColumns(String nodeColumns) {
        if (nodeColumns != null) {
            params.add("node-columns", nodeColumns);
        }
        return this;
    }

    public RowsParamsBuilder withOptimize(Integer optimize) {
        if (optimize != null) {
            params.add("optimize", optimize.toString());
        }
        return this;
    }

    public RowsParamsBuilder withTraceLabel(String label) {
        if (label != null) {
            params.add("tracelabel", label);
        }
        return this;
    }

	public RowsParamsBuilder withTimestamp(long serverTimestamp) {
		if (serverTimestamp > 0) {
			params.add("timestamp", serverTimestamp + "");
		}
		return this;
	}

    public RequestParameters getRequestParameters() {
        return this.params;
    }
}
