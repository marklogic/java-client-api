package com.marklogic.client.impl;

import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.SearchMetrics;
import com.marklogic.client.config.search.SearchResults;
import com.marklogic.client.config.search.jaxb.Metrics;
import com.marklogic.client.config.search.jaxb.Response;
import com.marklogic.client.config.search.jaxb.Result;
import com.marklogic.client.config.search.jaxb.Snippet;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/14/12
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultsImpl implements SearchResults {
    QueryDefinition criteria = null;
    Response jaxbResponse = null;
    SearchMetrics metrics = null;
    MatchDocumentSummary[] summary = null;

    protected SearchResultsImpl(QueryDefinition criteria, Response response) {
        this.criteria = criteria;
        jaxbResponse = response;
    }
    
    @Override
    public QueryDefinition getQueryCriteria() {
        return criteria;
    }

    @Override
    public int getTotalResults() {
        return -1;
    }

    @Override
    public SearchMetrics getMetrics() {
        if (metrics != null) {
            return metrics;
        }

        Date now = new Date();
        Metrics jaxbMetrics = jaxbResponse.getMetrics();
        long qrTime = jaxbMetrics.getQueryResolutionTime() == null ? -1 : jaxbMetrics.getQueryResolutionTime().getTimeInMillis(now);
        long frTime = jaxbMetrics.getFacetResolutionTime() == null ? -1 : jaxbMetrics.getFacetResolutionTime().getTimeInMillis(now);
        long srTime = jaxbMetrics.getSnippetResolutionTime() == null ? - 1: jaxbMetrics.getSnippetResolutionTime().getTimeInMillis(now);
        long totalTime = jaxbMetrics.getTotalTime() == null ? -1 : jaxbMetrics.getTotalTime().getTimeInMillis(now);
        metrics = new SearchMetricsImpl(qrTime, frTime, srTime, totalTime);
        return metrics;
    }

    @Override
    public MatchDocumentSummary[] getMatchResults() {
        if (summary != null) {
            return summary;
        }

        List<Result> results = jaxbResponse.getResult();
        summary = new MatchDocumentSummary[results.size()];
        int idx = 0;
        for (Result result : results) {
            String uri = result.getUri();
            int score = result.getScore().intValue();
            double conf = result.getConfidence();
            double fit = result.getFitness();
            String path = result.getPath();
            summary[idx] = new MatchDocumentSummaryImpl(uri, score, conf, fit, path, result);
            idx++;
        }

        return summary;
    }
}
