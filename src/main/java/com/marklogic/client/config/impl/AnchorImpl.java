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

import com.marklogic.client.config.Anchor;
import com.marklogic.client.config.FunctionRef;

public class AnchorImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.Anchor> implements Anchor {

	
	public AnchorImpl(String name, FunctionRef function) {
		jaxbObject = new com.marklogic.client.config.search.jaxb.Anchor();
		jaxbObject.setApply(function.getApply());
		jaxbObject.setNs(function.getNs());
		jaxbObject.setName(name);
		jaxbObject.setAt(function.getApply());
	}

	AnchorImpl(com.marklogic.client.config.search.jaxb.Anchor anchor) {
		jaxbObject = anchor;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

	@Override
	public void setApply(String apply) {
		jaxbObject.setApply(apply);
	}

	@Override
	public void setNs(String namespace) {
		jaxbObject.setNs(namespace);
	}

	@Override
	public void setAt(String at) {
		jaxbObject.setAt(at);
	}

	@Override
	public String getApply() {
		return jaxbObject.getApply();
	}

	@Override
	public String getNs() {
		return jaxbObject.getNs();
	}

	@Override
	public String getAt() {
		return jaxbObject.getAt();
	}


}
