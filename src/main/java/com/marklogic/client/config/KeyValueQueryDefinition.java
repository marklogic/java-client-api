package com.marklogic.client.config;

import com.marklogic.client.ValueLocator;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/19/12
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface KeyValueQueryDefinition extends QueryDefinition, Map<ValueLocator,String> {
}
