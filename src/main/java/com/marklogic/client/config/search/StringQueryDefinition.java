package com.marklogic.client.config.search;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/14/12
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StringQueryDefinition extends QueryDefinition {
    public String getCriteria();
    public void setCriteria(String criteria);
}
