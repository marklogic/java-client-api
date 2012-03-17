package com.marklogic.client.config.search;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/16/12
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MatchLocation {
    public String getPath();
    public String getAllSnippetText();
    public MatchSnippet[] getSnippets();
}
