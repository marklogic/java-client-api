package com.marklogic.client.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ndw
 * Date: 5/17/12
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public final class ValuesBuilder {
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(namespace = Values.VALUES_NS, name = "values-response")

    public static final class Values  {
        public static final String VALUES_NS = "http://marklogic.com/appservices/search";

        @XmlAttribute(name = "name")
        private String name;

        @XmlAttribute(name = "type")
        private String type;

        @XmlElement(namespace = Values.VALUES_NS, name = "distinct-value")
        private ArrayList<CountedDistinctValue> distinctValues;

        public Values() {
            distinctValues = new ArrayList<CountedDistinctValue>();
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public CountedDistinctValue[] getValues() {
            return distinctValues.toArray(new CountedDistinctValue[0]);
        }
    }
}