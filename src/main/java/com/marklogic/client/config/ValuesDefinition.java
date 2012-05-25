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
package com.marklogic.client.config;

public interface ValuesDefinition {
    public enum Direction {
        ASCENDING, DESCENDING;
    }

    public enum Frequency {
        FRAGMENT, ITEM;
    }

    public String getName();
    public void setName(String name);

    public QueryDefinition getQueryDefinition();
    public void setQueryDefinition(QueryDefinition qdef);

    public String getOptionsName();
    public void setOptionsName(String optname);

    public String getAggregate();
    public void setAggregate(String aggregate);

    public String getAggregatePath();
    public void setAggregatePath(String aggregate);

    public String getView();
    public void setView(String view);

    public Direction getDirection();
    public void setDirection(Direction dir);

    public Frequency getFrequency();
    public void setFrequency(Frequency freq);
}

