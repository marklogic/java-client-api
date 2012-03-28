package com.marklogic.client.config.search.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.SortOrder;
import com.marklogic.client.config.search.jaxb.Score;

public class SortOrderImpl extends AbstractQueryOption implements SortOrder {

	private com.marklogic.client.config.search.jaxb.SortOrder jaxbObject;
	
	public SortOrderImpl(com.marklogic.client.config.search.jaxb.SortOrder ot) {
		jaxbObject = ot;
		indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	public SortOrderImpl() {
		jaxbObject = new com.marklogic.client.config.search.jaxb.SortOrder();
		indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	@Override
	public Object asJaxbObject() {
		return jaxbObject;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getElementOrAttributeOrField();
	}

	@Override
	public void addAnnotation(Element annotation) {
		addAnnotation(this, annotation);
	}

	@Override
	public List<Element> getAnnotations() {
		return getAnnotations(this);
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
