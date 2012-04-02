package com.marklogic.client.config;


/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/15/12
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SearchMetrics {
    public long getQueryResolutionTime();
    public long getFacetResolutionTime();
    public long getSnippetResolutionTime();
    public long getTotalTime();
}
