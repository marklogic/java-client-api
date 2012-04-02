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

import com.marklogic.client.config.AnchorValue;
import com.marklogic.client.config.ComputedBucket;

public class ComputedBucketImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.ComputedBucket> implements ComputedBucket {


	ComputedBucketImpl(
			com.marklogic.client.config.search.jaxb.ComputedBucket ot) {
		jaxbObject = ot;
	}

	public ComputedBucketImpl() {
		jaxbObject = new com.marklogic.client.config.search.jaxb.ComputedBucket();
	}

	@Override
	public AnchorValue getAnchor() {
		return AnchorValue.fromXmlString(jaxbObject.getAnchor());
	}

	@Override
	public void setAnchor(AnchorValue anchor) {
		jaxbObject.setAnchor(anchor.toXmlString());
	}
	

	@Override
	public AnchorValue getLtAnchor() {
		return AnchorValue.fromXmlString(jaxbObject.getAnchor());
	}

	@Override
	public void setLtAnchor(AnchorValue anchor) {
		jaxbObject.setAnchor(anchor.toXmlString());
	}
	@Override
	public AnchorValue getGeAnchor() {
		return AnchorValue.fromXmlString(jaxbObject.getAnchor());
	}

	@Override
	public void setGeAnchor(AnchorValue anchor) {
		jaxbObject.setAnchor(anchor.toXmlString());
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

	@Override
	public String getName() {
		return jaxbObject.getName();
	}

	@Override
	public void setName(String name) {
		jaxbObject.setName(name);
	}

	@Override
	public String getGe() {
		return jaxbObject.getGe();
	}

	@Override
	public void setGe(String ge) {
		jaxbObject.setGe(ge);
	}

	@Override
	public String getLt() {
		return jaxbObject.getLt();
	}

	@Override
	public void setLt(String lt) {
		jaxbObject.setLt(lt);
	}

	@Override
	public String getContent() {
		return jaxbObject.getContent();
	}

	@Override
	public void setContent(String content) {
		jaxbObject.setContent(content);
	}

}
