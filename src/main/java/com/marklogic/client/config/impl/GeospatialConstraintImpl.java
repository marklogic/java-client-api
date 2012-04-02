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

import com.marklogic.client.config.Heatmap;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Element;
import com.marklogic.client.config.search.jaxb.Lat;
import com.marklogic.client.config.search.jaxb.Lon;
import com.marklogic.client.config.search.jaxb.Parent;

public abstract class GeospatialConstraintImpl<T> extends ConstraintImpl<T> {

	protected Heatmap heatmap;

	public GeospatialConstraintImpl(String name) {
		super(name);
	}

	GeospatialConstraintImpl(Constraint constraint) {
		super(constraint);
	}

	private String getElementAndAttribute(String elementName, String attrKey) {
		for (Object o : getJAXBChildren()) {
			if (o instanceof Parent && elementName.equals("parent")) {
				Parent p = (Parent) o;
				if (attrKey.equals("ns"))
					return p.getNs();
				else if (attrKey.equals("name")) {
					return p.getName();
				}
			} else if (o instanceof Lat && elementName.equals("lat")) {
				Lat p = (Lat) o;
				if (attrKey.equals("ns"))
					return p.getNs();
				else if (attrKey.equals("name")) {
					return p.getName();
				}
			} else if (o instanceof Lon && elementName.equals("lon")) {
				Lon p = (Lon) o;
				if (attrKey.equals("ns"))
					return p.getNs();
				else if (attrKey.equals("name")) {
					return p.getName();
				}
			}
		}
		return null;
	}

	private String setElementAndAttribute(String elementName, String attrKey,
			String value) {
		for (Object o : getJAXBChildren()) {
			if (o instanceof Parent && elementName.equals("parent")) {
				Parent p = (Parent) o;
				if (attrKey.equals("ns"))
					p.setName(value);
				else if (attrKey.equals("name")) {
					p.setName(value);
				}
			} else if (o instanceof Lat && elementName.equals("lat")) {
				Lat p = (Lat) o;
				if (attrKey.equals("ns"))
					p.setNs(value);
				else if (attrKey.equals("name")) {
					p.setName(value);
				}
			} else if (o instanceof Lon && elementName.equals("lon")) {
				Lon p = (Lon) o;
				if (attrKey.equals("ns"))
					p.setNs(value);
				else if (attrKey.equals("name")) {
					p.setName(value);
				}
			} else if (o instanceof Element && elementName.equals("element")) {
				Lon p = (Lon) o;
				if (attrKey.equals("ns"))
					p.setNs(value);
				else if (attrKey.equals("name")) {
					p.setName(value);
				}
			}
		}
		return null;
	}

	public String getParentNs() {
		return getElementAndAttribute("parent", "ns");
	}

	public void setParentNs(String parentNs) {
		setElementAndAttribute("parent", "ns", parentNs);
	}

	public String getParentName() {
		return getElementAndAttribute("parent", "name");
	}

	public void setParentName(String parentNs) {
		setElementAndAttribute("parent", "ns", parentNs);
	}

	public String getLatNs() {
		return getElementAndAttribute("lat", "ns");
	}

	public void setLatNs(String latNs) {
		setElementAndAttribute("lat", "ns", latNs);

	}

	public String getLatName() {
		return getElementAndAttribute("lat", "name");

	}

	public void setLatName(String latName) {
		setElementAndAttribute("lat", "name", latName);

	}

	public String getLonNs() {
		return getElementAndAttribute("lon", "ns");

	}

	public void setLonNs(String lonNs) {
		setElementAndAttribute("lon", "ns", lonNs);

	}

	public String getLonName() {
		return getElementAndAttribute("lon", "name");

	}

	public void setLonName(String lonName) {
		setElementAndAttribute("lon", "name", lonName);
	}

	public String getElementNs() {
		return getElementAndAttribute("element", "ns");

	}

	public void setElementNs(String elementNs) {
		setElementAndAttribute("element", "ns", elementNs);

	}

	public String getElementName() {
		return getElementAndAttribute("element", "name");

	}

	public void setElementName(String elementName) {
		setElementAndAttribute("element", "name", elementName);
	}

	public Heatmap getHeatmap() {
		return heatmap;
	}

	public void setHeatmap(Heatmap heatmap) {
		this.heatmap = heatmap;
	}

}
