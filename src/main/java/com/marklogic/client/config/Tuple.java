package com.marklogic.client.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ndw
 * Date: 5/24/12
 * Time: 9:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class Tuple {
    @XmlAttribute(name = "frequency")
    private long frequency;

    @XmlElement(namespace = TuplesBuilder.Tuples.TUPLES_NS, name = "distinct-value")
    private List<TypedDistinctValue> distinctValues;

    public Tuple() {
        distinctValues = new ArrayList<TypedDistinctValue>();
    }

    public long getCount() {
        return frequency;
    }

    public TypedDistinctValue[] getValues() {
        return distinctValues.toArray(new TypedDistinctValue[0]);
    }
}
