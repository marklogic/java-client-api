/*
 * Copyright 2012 MarkLogic Corporation
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

import com.marklogic.client.config.StringQueryDefinition;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/14/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringQueryDefinitionImpl implements StringQueryDefinition {
    private String criteria = null;
    private String optionsUri = null;
    
    public StringQueryDefinitionImpl(String uri) {
        optionsUri = uri;
    }
    
    @Override
    public String getCriteria() {
        return criteria;
    }

    @Override
    public void setCriteria(String criteria) {
        // FIXME: check for null?
        this.criteria = criteria;
    }

    @Override
    public String getOptionsName() {
        return optionsUri;
    }

    @Override
    public void setOptionsName(String uri) {
        // FIXME: check for null?
        optionsUri = uri;
    }
}
