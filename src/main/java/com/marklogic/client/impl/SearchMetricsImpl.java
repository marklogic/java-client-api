package com.marklogic.client.impl;

import com.marklogic.client.config.search.SearchMetrics;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * long: 3/15/12
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchMetricsImpl implements SearchMetrics {
    long qrTime = -1;
    long frTime = -1;
    long srTime = -1;
    long totalTime = -1;
    
    protected SearchMetricsImpl(long qrTime, long frTime, long srTime, long totalTime) {
        this.qrTime = qrTime;
        this.frTime = frTime;
        this.srTime = srTime;
        this.totalTime = totalTime;
    }
    
    @Override
    public long getQueryResolutionTime() {
        return qrTime;
    }

    @Override
    public long getFacetResolutionTime() {
        return frTime;
    }

    @Override
    public long getSnippetResolutionTime() {
        return srTime;
    }

    @Override
    public long getTotalTime() {
        return totalTime;
    }
}
