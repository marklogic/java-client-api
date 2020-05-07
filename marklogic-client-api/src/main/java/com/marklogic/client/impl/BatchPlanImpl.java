/*
 * Copyright 2020 MarkLogic Corporation
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
package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.type.ServerExpression;
import com.marklogic.client.util.RequestParameters;

public class BatchPlanImpl  {

    final public static String LOWER_BOUND = "ML_LOWER_BOUND";
    final public static String UPPER_BOUND = "ML_UPPER_BOUND";

    private String schema;
    private String view;
    private String tableID;
    private long rowCount = 0;
    private PlanBuilderImpl.PreparePlanImpl encapsulatedPlan;

    public String getSchema() {
        return this.schema;
    }
    public String getView() {
        return this.view;
    }
    public long getRowCount() {
        return this.rowCount;
    }
    public PlanBuilder.PreparePlan getEncapsulatedPlan() {
        return this.encapsulatedPlan;
    }

    public BatchPlanImpl(PlanBuilder.ModifyPlan modifyPlan, DatabaseClient client) {
        if(!(modifyPlan instanceof PlanBuilderImpl.ModifyPlanImpl))
            throw new IllegalArgumentException("Plan needs to be an instance of ModifyPlanImpl");
        PlanBuilderImpl.ModifyPlanImpl modifyPlanImpl = (PlanBuilderImpl.ModifyPlanImpl) modifyPlan;

        BaseTypeImpl.BaseCallImpl firstPlanImpl = modifyPlanImpl.getChain()[0];

        if(!(firstPlanImpl.fnName.equals("from-view")))
            throw new IllegalArgumentException("Plan must start with fromView()");

        // TODO : might need change
        schema = (firstPlanImpl.args[0] == null) ? null : firstPlanImpl.args[0].toString();
        view = firstPlanImpl.args[1].toString();
        if (view == null || view.length()==0)
            throw new IllegalArgumentException("View name cannot be empty");

        readViewInfo(client);

        BaseTypeImpl.BaseCallImpl[] arrWithView = modifyPlanImpl.getChain();

        PlanBuilder p = client.newRowManager().newPlanBuilder();

        ServerExpression tablePrefix = p.xs.string(tableID + ":");
        this.encapsulatedPlan = (PlanBuilderImpl.PreparePlanImpl)
                p.fromView(schema, view)
                 .where(p.and(
                     p.ge(p.col("rowID"), p.sql.rowID(p.fn.concat(tablePrefix, p.param(LOWER_BOUND)))),
                     p.le(p.col("rowID"), p.sql.rowID(p.fn.concat(tablePrefix, p.param(UPPER_BOUND))))
                     ))
                 .prepare(2);

        BaseTypeImpl.BaseCallImpl[] arrForEncapsulated = encapsulatedPlan.getChain();
        BaseTypeImpl.BaseCallImpl[] arrForModified = new BaseTypeImpl.BaseCallImpl[arrWithView.length + arrForEncapsulated.length - 1];
        int i=0;
        arrForModified[i] = arrWithView[i];
        for(i=1; i<(arrForEncapsulated.length-1); i++){
            arrForModified[i] = arrForEncapsulated[i];
        }
        for(int j=1; j<arrWithView.length; j++) {
            arrForModified[i] = arrWithView[j];
            i++;
        }
        arrForModified[i] = arrForEncapsulated[arrForEncapsulated.length-1];
        encapsulatedPlan.setChain(arrForModified);
    }
    private void readViewInfo(DatabaseClient client) {
        RequestParameters params = new RequestParameters();
        if (getSchema() != null) {
            params.add("schema", getSchema());
        }
        params.add("view", getView());

        JsonNode viewInfo = ((DatabaseClientImpl) client).getServices()
                .getResource(null, "internal/viewinfo", null, params, new JacksonHandle())
                .get();
        tableID = viewInfo.get("tableID").asText(null);
        rowCount = viewInfo.get("rowCount").asLong(0);
        if (tableID == null) {
            throw new IllegalArgumentException("Could not find table id for view "+getView());
        }
        if (rowCount == 0) {
            throw new IllegalArgumentException("No rows in view "+getView());
        }
   }
}