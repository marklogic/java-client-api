package com.marklogic.client.config.search;

/**
 * Created with IntelliJ IDEA.
 * User: ndw
 * Date: 3/31/12
 * Time: 5:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FacetResult {
    public String getName();
    public FacetValue[] getFacetValues();
}
