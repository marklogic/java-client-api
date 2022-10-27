/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.type.PlanErrorDisposition;

public class ErrorDispositionDefImpl implements PlanErrorDisposition, BaseTypeImpl.BaseArgImpl {
    private String logLevel;
    private int logSize;

    public ErrorDispositionDefImpl(int logSize, String logLevel) {
        this.logLevel = logLevel;
        this.logSize = logSize;
    }

    @Override
    public StringBuilder exportAst(StringBuilder strb) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        if(logLevel != null)
            node.put("logLevel", this.logLevel);
        node.put("logSize", this.logSize);
        return strb.append(node.toString());
    }
}
