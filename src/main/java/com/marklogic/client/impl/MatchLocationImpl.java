package com.marklogic.client.impl;

import com.marklogic.client.config.search.MatchLocation;
import com.marklogic.client.config.search.MatchSnippet;
import com.marklogic.client.config.search.jaxb.Match;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/16/12
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatchLocationImpl implements MatchLocation {
    private String path = null;
    private MatchSnippet[] snippets = null;
    private Match jaxbMatch = null;

    protected MatchLocationImpl(String path, Match match) {
        this.path = path;
        jaxbMatch = match;
    }
    
    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getAllSnippetText() {
        getSnippets();
        String text = "";
        for (MatchSnippet snippet : snippets) {
            text += snippet.getText();
        }
        return text;
    }

    @Override
    public MatchSnippet[] getSnippets() {
        List<Serializable> jaxbContent = jaxbMatch.getContent();
        snippets = new MatchSnippet[jaxbContent.size()];
        int idx = 0;
        for (Object content : jaxbContent) {
            if (content instanceof String) {
                snippets[idx] = new MatchSnippetImpl(false, (String) content);
            } else {
                snippets[idx] = new MatchSnippetImpl(true, (String) ((JAXBElement) content).getValue());
            }
            idx++;
        }

        return snippets;
    }
}
