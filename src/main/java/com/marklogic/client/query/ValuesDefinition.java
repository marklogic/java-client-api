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
package com.marklogic.client.query;

/**
 * A ValuesDefinition represents a values query used to retreive values from the server.
 */
public interface ValuesDefinition {
    /** The possible directions.
     *
     */
    public enum Direction {
        ASCENDING, DESCENDING;
    }

    /** The kinds of frequencies.
     *
     */
    public enum Frequency {
        FRAGMENT, ITEM;
    }

    /**
     * Returns the name of the values constraint.
     * @return The name of the values constraint.
     */
    public String getName();

    /**
     * Sets the name of the values constraint.
     * @param name The values constraint name.
     */
    public void setName(String name);

    /**
     * Returns the query definition associated with this values query.
     * @return The query definition.
     */
    public ValueQueryDefinition getQueryDefinition();

    /**
     * Set the query definition associated with this values query.
     * @param qdef The query definition.
     */
    public void setQueryDefinition(ValueQueryDefinition qdef);

    /**
     * Returns the name of the options node used for this values query.
     * @return The name of the options node.
     */
    public String getOptionsName();

    /**
     * Set the name of the options node to use for this values query.
     * @param optname The name of the options node.
     */
    public void setOptionsName(String optname);

    /**
     * Returns the name of the aggregate function applied to this query.
     * @return The name of the function.
     */
    public String[] getAggregate();

    /**
     * Sets the name of the aggregate function to be applied as part of this values query.
     * @param aggregate The name of the function.
     */
    public void setAggregate(String... aggregate);

    /**
     * Returns the aggregate path.
     * @return The path.
     */
    public String getAggregatePath();

    /**
     * Sets the aggregate path.
     * @param aggregate The aggregate path.
     */
    public void setAggregatePath(String aggregate);

    /**
     * Returns the view for this values query.
     * @return The view.
     */
    public String getView();

    /**
     * Sets the view for this values query.
     * @param view The view.
     */
    public void setView(String view);

    /**
     * Returns the direction of the results in this values query.
     * @return The direction.
     */
    public Direction getDirection();

    /**
     * Sets the direction of the results to use in this values query.
     * @param dir The direction.
     */
    public void setDirection(Direction dir);

    /**
     * Returns the frequency of the results.
     * @return The frequency.
     */
    public Frequency getFrequency();

    /**
     * Sets the frequency to be used in this values query.
     * @param freq The frequency.
     */
    public void setFrequency(Frequency freq);
}

