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
package com.marklogic.client.config;

import java.util.List;



/**
 * This interface binds the hierarchy of Config OptionsFromJAXB classes 
 * to an implementation backed by a JAXB facade.
 * TODO -- replace with a DOM-based implementation.
 * @author cgreer
 *
 */
public interface BoundQueryOption {

	/**
	 * 
	 * @return the JAXB Java object that backs the QueryOptions component.
	 */
	public Object asJAXB();

	/**
	 * 
	 * @return the list of child objects associated with this BoundQueryOption componenet.
	 */
	public List<Object> getJAXBChildren();
	
}
