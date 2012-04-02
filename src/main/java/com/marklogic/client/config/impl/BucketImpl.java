package com.marklogic.client.config.impl;

import java.util.List;

import com.marklogic.client.config.Bucket;


public class BucketImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.Bucket> implements Bucket {

    public BucketImpl(com.marklogic.client.config.search.jaxb.Bucket ot) {
		jaxbObject=ot;
	}


	public String getContent() {
        return jaxbObject.getContent();
    }

    
    public void setContent(String value) {
        jaxbObject.setContent(value);
    }

    public String getName() {
        return jaxbObject.getName();
    }

    public void setName(String value) {
        jaxbObject.setName(value);
    }

    public String getGe() {
        return jaxbObject.getGe();
    }

    public void setGe(String value) {
        jaxbObject.setGe(value);
    }

    public String getLt() {
        return jaxbObject.getLt();
    }

    public void setLt(String value) {
        jaxbObject.setLt(value);
    }


	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}


}
