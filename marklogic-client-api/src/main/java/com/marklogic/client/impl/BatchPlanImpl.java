package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.util.RequestParameters;

public class BatchPlanImpl  {


    private String schema;
    // new variable for view;

    public String getSchema() {
        return schema;
    }

    public static PlanBuilder.PreparePlan modifyChain(PlanBuilder.ModifyPlan modifyPlan, DatabaseClient client, Long lowerBound, int batchSize) {

        if(!(modifyPlan instanceof PlanBuilderImpl.ModifyPlanImpl))
            throw new IllegalArgumentException("Plan needs to be an instance of ModifyPlanImpl");

        PlanBuilderImpl.ModifyPlanImpl modifyPlanImpl = (PlanBuilderImpl.ModifyPlanImpl) modifyPlan;

        BaseTypeImpl.BaseCallImpl firstPlanImpl = modifyPlanImpl.getChain()[0];

        if(!(firstPlanImpl.fnName.equals("from-view")))
            throw new IllegalArgumentException("The first operation needs to be FROM-VIEW");

        BaseTypeImpl.BaseCallImpl[] arrWithView = modifyPlanImpl.getChain();
        String upperBound = String.valueOf(Long.valueOf(lowerBound)+batchSize - 1);

        PlanBuilder p = client.newRowManager().newPlanBuilder();

        PlanBuilder.AccessPlan viewPlan = (PlanBuilder.AccessPlan) firstPlanImpl;
        PlanBuilder.PreparePlan encapsulatedPlan = viewPlan
                .where(p.and(p.ge(p.col("rowID"), p.param(String.valueOf(lowerBound))),
                        p.le(p.col("rowID"), p.param(upperBound))))
                .prepare(2);

        BaseTypeImpl.BaseCallImpl[] arrForEncapsulated = ((PlanBuilderImpl.ModifyPlanImpl)encapsulatedPlan).getChain();
        BaseTypeImpl.BaseCallImpl[] arrForModified = new BaseTypeImpl.BaseCallImpl[arrWithView.length + arrForEncapsulated.length - 1];
        for(int i=0; i<arrForEncapsulated.length-1 ; i++){
            arrForModified[i] = arrForEncapsulated[i];
        }
        int i = arrForEncapsulated.length-1;
        for(int j=1; j<arrWithView.length; j++) {
            arrForModified[i] = arrWithView[j];
            i++;
        }
        arrForModified[i] = arrForEncapsulated[arrForEncapsulated.length-1];
        ((PlanBuilderImpl.ModifyPlanImpl) encapsulatedPlan).setChain(arrForModified);
        return encapsulatedPlan;
    }
}