package com.marklogic.client.impl;

import com.marklogic.client.config.search.MatchSnippet;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/16/12
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatchSnippetImpl implements MatchSnippet {
    private boolean high = false;
    private String text = null;
    
    protected MatchSnippetImpl(boolean high, String text) {
        this.high = high;
        this.text = text;
    }
    
    @Override
    public boolean isHighlighted() {
        return high;
    }

    @Override
    public String getText() {
        return text;
    }
}
