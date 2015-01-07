/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import com.marklogic.client.query.ValueQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;

public class ValuesDefinitionImpl implements ValuesDefinition {
    private String name = null;
    private ValueQueryDefinition qdef = null;
    private String options = null;
    private String[] aggregate = null;
    private String aggPath = null;
    private String view = null;
    private Direction direction = null;
    private Frequency frequency = null;

    public ValuesDefinitionImpl(String name, String optionsName) {
        if (name == null) {
            throw new NullPointerException("ValuesDefinition name must not be null.");
        }
        this.name = name;
        options = optionsName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("ValuesDefinition name must not be null.");
        }
        this.name = name;
    }

    @Override
    public ValueQueryDefinition getQueryDefinition() {
        return qdef;
    }

    @Override
    public void setQueryDefinition(ValueQueryDefinition qdef) {
        this.qdef = qdef;
    }

    @Override
    public String getOptionsName() {
        return options;
    }

    @Override
    public void setOptionsName(String optname) {
        options = optname;
    }

    @Override
    public String[] getAggregate() {
        return aggregate;
    }

    @Override
    public void setAggregate(String... aggregate) {
        this.aggregate = aggregate;
    }

    @Override
    public String getAggregatePath() {
        return aggPath;
    }

    @Override
    public void setAggregatePath(String aggregate) {
        aggPath = aggregate;
    }

    @Override
    public String getView() {
        return view;
    }

    @Override
    public void setView(String view) {
        this.view = view;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction dir) {
        direction = dir;
    }

    @Override
    public Frequency getFrequency() {
        return frequency;
    }

    @Override
    public void setFrequency(Frequency freq) {
        frequency = freq;
    }
}
