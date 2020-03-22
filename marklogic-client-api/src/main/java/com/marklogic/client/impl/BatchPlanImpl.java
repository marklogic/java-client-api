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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.expression.PlanBuilder;

public class BatchPlanImpl  {

    public static String LOWER_BOUND = "ML_LOWER_BOUND";
    public static String UPPER_BOUND = "ML_UPPER_BOUND";
    private String schema;
    private String view;
    private PlanBuilder.PreparePlan encapsulatedPlan;

    public String getSchema() {
        return this.schema;
    }
    public String getView() {return this.view;}
    public PlanBuilder.PreparePlan getEncapsulatedPlan() {
        return this.encapsulatedPlan;
    }

    public BatchPlanImpl(PlanBuilder.ModifyPlan modifyPlan, DatabaseClient client) {

        if(!(modifyPlan instanceof PlanBuilderImpl.ModifyPlanImpl))
            throw new IllegalArgumentException("Plan needs to be an instance of ModifyPlanImpl");
        PlanBuilderImpl.ModifyPlanImpl modifyPlanImpl = (PlanBuilderImpl.ModifyPlanImpl) modifyPlan;

        BaseTypeImpl.BaseCallImpl firstPlanImpl = modifyPlanImpl.getChain()[0];

        if(!(firstPlanImpl.fnName.equals("from-view")))
            throw new IllegalArgumentException("The first operation needs to be FROM-VIEW");
        StringBuilder str = new StringBuilder();

        // TODO : might need change
        view = firstPlanImpl.args[0].toString();
        schema = firstPlanImpl.args[1].toString();

        if(getSchema() == null || getSchema().length()==0)
            throw new IllegalArgumentException("Schema cannot be empty");

        BaseTypeImpl.BaseCallImpl[] arrWithView = modifyPlanImpl.getChain();

        PlanBuilder p = client.newRowManager().newPlanBuilder();

        PlanBuilder.AccessPlan viewPlan = (PlanBuilder.AccessPlan) firstPlanImpl;

        this.encapsulatedPlan = viewPlan
                .where(p.and(p.ge(p.col("rowID"), p.param(LOWER_BOUND)),
                        p.le(p.col("rowID"), p.param(UPPER_BOUND))))
                .prepare(2);

        BaseTypeImpl.BaseCallImpl[] arrForEncapsulated = ((PlanBuilderImpl.PreparePlanImpl)encapsulatedPlan).getChain();
        BaseTypeImpl.BaseCallImpl[] arrForModified = new BaseTypeImpl.BaseCallImpl[arrWithView.length + arrForEncapsulated.length - 1];
        int i=0;
        for(; i<arrForEncapsulated.length-1 ; i++){
            arrForModified[i] = arrForEncapsulated[i];
        }
        for(int j=1; j<arrWithView.length; j++) {
            arrForModified[i] = arrWithView[j];
            i++;
        }
        arrForModified[i] = arrForEncapsulated[arrForEncapsulated.length-1];
        ((PlanBuilderImpl.PreparePlanImpl) encapsulatedPlan).setChain(arrForModified);
    }
}