package com.marklogic.client.impl;

import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.MatchLocation;
import com.marklogic.client.config.search.MatchSnippet;
import com.marklogic.client.config.search.jaxb.Match;
import com.marklogic.client.config.search.jaxb.Result;
import com.marklogic.client.config.search.jaxb.Snippet;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/16/12
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class MatchDocumentSummaryImpl implements MatchDocumentSummary {
    private String uri = null;
    private int score = -1;
    private double conf = -1;
    private double fit = -1;
    private String path = null;
    private Result result = null;
    private MatchLocation[] locations = null;
    
    protected MatchDocumentSummaryImpl(String uri, int score, double confidence, double fitness, String path, Result result) {
        this.uri = uri;
        this.score = score;
        conf = confidence;
        fit = fitness;
        this.path = path;
        this.result = result;
    }
    
    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public double getConfidence() {
        return conf;
    }

    @Override
    public double getFitness() {
        return fit;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public MatchLocation[] getMatchLocations() {
        if (locations != null) {
            return locations;
        }

        List<Snippet> jaxbSnippets = result.getSnippet();
        locations = new MatchLocation[jaxbSnippets.size()];
        int idx = 0;
        for (Snippet snippet : jaxbSnippets) {
            for (Object jaxbMatch : snippet.getMatchOrAnyOrAny()) {
                if (jaxbMatch instanceof Match) {
                    Match match = (Match) jaxbMatch;
                    String path = match.getPath();
                    locations[idx] = new MatchLocationImpl(path, match);
                    idx++;
                } else {
                    throw new UnsupportedOperationException("Cannot parse customized snippets");
                }
            }
        }

        return locations;
    }
}
