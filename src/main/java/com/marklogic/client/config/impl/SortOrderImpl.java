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

import org.w3c.dom.Element;

import com.marklogic.client.config.SortOrder;
import com.marklogic.client.config.search.jaxb.Score;

public class SortOrderImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.SortOrder> implements SortOrder {

	
	SortOrderImpl(com.marklogic.client.config.search.jaxb.SortOrder ot) {
		jaxbObject = ot;
		indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	public SortOrderImpl() {
		jaxbObject = new com.marklogic.client.config.search.jaxb.SortOrder();
		indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getElementOrAttributeOrField();
	}


	@Override
	public String getType() {
		return jaxbObject.getType();
	}

	@Override
	public void setType(String type) {
		jaxbObject.setType(type);
	}

	@Override
	public Direction getDirection() {
		return Direction.valueOf(jaxbObject.getDirection().toUpperCase());
	}

	@Override
	public void setDirection(Direction direction) {
		jaxbObject.setDirection(direction.toString().toLowerCase());
	}

	@Override
	public void setCollation(String collation) {
		jaxbObject.setCollation(collation);
		
	}

	@Override
	public String getCollation() {
		return jaxbObject.getCollation();
	}

	@Override
	public void setScore() {
		getJAXBChildren().add(new Score());
	}

	@Override
	public boolean getScore() {
		List<Object> l = getJAXBChildren();
		for (int i=0;i<l.size();i++) {
			Object o = l.get(i);
			if (o instanceof Score) {
				return true;
			}
		}
		return false;
	}

}
