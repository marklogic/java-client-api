package com.marklogic.client.impl;

import com.marklogic.client.config.QueryDefinition;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: ndw
 * Date: 5/24/12
 * Time: 7:09 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractQueryDefinition implements QueryDefinition {
    protected String optionsUri = null;
    protected HashSet<String> collections = new HashSet<String>();
    protected String directory = null;

    @Override
    public String getOptionsName() {
        return optionsUri;
    }

    @Override
    public void setOptionsName(String uri) {
        // FIXME: check for null?
        optionsUri = uri;
    }


    @Override
    public String[] getCollections() {
        return collections.toArray(new String[0]);
    }

    @Override
    public void setCollections(String... collections) {
        this.collections.clear();

        for (String collection : collections) {
            this.collections.add(collection);
        }
    }

    @Override
    public String getDirectory() {
        return directory;
    }

    @Override
    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
