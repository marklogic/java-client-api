package com.marklogic.client.impl;

import com.marklogic.client.KeyLocator;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/19/12
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class KeyLocatorImpl implements KeyLocator {
    String key = null;
    
    protected KeyLocatorImpl(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }
}
