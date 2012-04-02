/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
