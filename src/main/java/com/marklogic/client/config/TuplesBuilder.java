package com.marklogic.client.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ndw
 * Date: 5/17/12
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public final class TuplesBuilder {
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(namespace = Tuples.TUPLES_NS, name = "values-response")

    public static final class Tuples {
        public static final String TUPLES_NS = "http://marklogic.com/appservices/search";

        @XmlAttribute(name = "name")
        private String name;

        @XmlElement(namespace = Tuples.TUPLES_NS, name = "tuple")
        private List<Tuple> tuples;

        public Tuples() {
            tuples = new ArrayList<Tuple>();
        }

        public Tuple[] getTuples() {
            return tuples.toArray(new Tuple[0]);
        }
    }
}