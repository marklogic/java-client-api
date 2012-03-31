package com.marklogic.client.config.search;

/**
 * Created with IntelliJ IDEA.
 * User: ndw
 * Date: 3/31/12
 * Time: 5:55 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FacetHeatmapValue extends FacetValue {
    public double[] getBox();
}
