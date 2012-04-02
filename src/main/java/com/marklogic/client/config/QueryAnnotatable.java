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

import org.w3c.dom.Element;

/**
 * Objects implementing this interface can be annotated with metadata
 * from a client application, to provide information about particular
 * query configuration objects to the consuming application.
 */
public interface QueryAnnotatable  {

	public void addQueryAnnotation(Element annotation);
	public List<Element> getQueryAnnotations();
	
}
