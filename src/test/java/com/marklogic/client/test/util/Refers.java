package com.marklogic.client.test.util;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Refers {
    public String              name  = "refers";
    public Referred            child = null;
    public Map<String,Integer> map   = null;
    public List<String>        list  = null;
}
