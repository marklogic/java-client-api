package com.marklogic.client.config.search;

import com.marklogic.client.DocumentIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/16/12
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MatchDocumentSummary {
    public String getUri();
    public int    getScore();
    public double getConfidence();
    public double getFitness();
    public String getPath();
    public DocumentIdentifier docId();
    public MatchLocation[] getMatchLocations();
}
