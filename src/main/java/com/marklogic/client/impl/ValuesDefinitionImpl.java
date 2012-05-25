package com.marklogic.client.impl;

import com.marklogic.client.config.QueryDefinition;
import com.marklogic.client.config.ValuesDefinition;

/**
 * Created with IntelliJ IDEA.
 * User: ndw
 * Date: 5/22/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValuesDefinitionImpl implements ValuesDefinition {
    private String name = null;
    private QueryDefinition qdef = null;
    private String options = null;
    private String aggregate = null;
    private String aggPath = null;
    private String view = null;
    private Direction direction = null;
    private Frequency frequency = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public ValuesDefinitionImpl(String optionsName) {
        options = optionsName;
    }

    @Override
    public QueryDefinition getQueryDefinition() {
        return qdef;
    }

    @Override
    public void setQueryDefinition(QueryDefinition qdef) {
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
    public String getAggregate() {
        return aggregate;
    }

    @Override
    public void setAggregate(String aggregate) {
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
