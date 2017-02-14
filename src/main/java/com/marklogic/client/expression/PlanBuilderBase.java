/*
 * Copyright 2017 MarkLogic Corporation
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
package com.marklogic.client.expression;

import java.util.Map;

import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.type.AttributeNodeSeqExpr;
import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.PlanCase;
import com.marklogic.client.type.PlanFunction;
import com.marklogic.client.type.PlanGroupConcatOptionSeq;
import com.marklogic.client.type.PlanParamExpr;
import com.marklogic.client.type.PlanValueOption;
import com.marklogic.client.type.SemStoreExpr;
import com.marklogic.client.type.XmlContentNodeSeqExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsLongVal;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsQNameVal;

interface PlanBuilderBase {
    public PlanBuilder.AccessPlan fromLiterals(@SuppressWarnings("unchecked") Map<String,Object>... rows);

    public ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes, XmlContentNodeSeqExpr content);
    public PlanCase when(XsBooleanExpr condition, ItemSeqExpr value);

    public PlanFunction resolveFunction(XsQNameVal functionName, String modulePath);

    public PlanGroupConcatOptionSeq groupConcatOptions(String separator);
    public PlanGroupConcatOptionSeq groupConcatOptions(PlanValueOption option);
    // not a sequence constructor
    public PlanGroupConcatOptionSeq groupConcatOptions(String separator, PlanValueOption option);

    public interface PlanBase {
        public PlanBuilder.Plan bindParam(PlanParamExpr param, boolean literal);
        public PlanBuilder.Plan bindParam(PlanParamExpr param, byte    literal);
        public PlanBuilder.Plan bindParam(PlanParamExpr param, double  literal);
        public PlanBuilder.Plan bindParam(PlanParamExpr param, float   literal);
        public PlanBuilder.Plan bindParam(PlanParamExpr param, int     literal);
        public PlanBuilder.Plan bindParam(PlanParamExpr param, long    literal);
        public PlanBuilder.Plan bindParam(PlanParamExpr param, short   literal);
        public PlanBuilder.Plan bindParam(PlanParamExpr param, String  literal);
    }
    public interface AccessPlanBase {
    }
    public interface ExportablePlanBase {
        public <T extends JSONReadHandle> T export(T handle);
        public <T> T exportAs(Class<T> as);
    }
    public interface ModifyPlanBase {
        public PlanBuilder.ModifyPlan limit(long length);
        public PlanBuilder.ModifyPlan limit(XsLongVal length);
        public PlanBuilder.ModifyPlan limit(PlanParamExpr length);
        public PlanBuilder.ModifyPlan offset(long start);
        public PlanBuilder.ModifyPlan offset(XsLongVal start);
        public PlanBuilder.ModifyPlan offset(PlanParamExpr start);
        public PlanBuilder.ModifyPlan offsetLimit(long start, long length);
        public PlanBuilder.ModifyPlan offsetLimit(XsLongVal start, XsLongVal length);
        public PlanBuilder.ModifyPlan where(XsBooleanExpr condition);
        public PlanBuilder.ModifyPlan where(CtsQueryExpr condition);
        public PlanBuilder.ModifyPlan where(SemStoreExpr condition);
    }
    public interface PreparePlanBase {
    }
}
