package com.marklogic.client.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created with IntelliJ IDEA.
 * User: ndw
 * Date: 5/23/12
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class CountedDistinctValue {
    @XmlAttribute(name = "frequency")
    long frequency;

    @XmlValue
    String value;

    public long getCount() {
        return frequency;
    }

    public <T> T get(Class<T> as) {
        return DistinctValue.getValue(value, as);
    }
}