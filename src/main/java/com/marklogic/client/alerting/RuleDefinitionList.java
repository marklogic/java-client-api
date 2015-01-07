/*
 * Copyright 2013-2015 MarkLogic Corporation
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
package com.marklogic.client.alerting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.marklogic.client.RequestConstants;
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.RuleListReadHandle;

/**
 * Models a list of RuleDefinitions.
 * Returned by a call to RuleManager.match
 *
 */
public class RuleDefinitionList
extends BaseHandle<InputStream,OperationNotSupported>
implements Iterable<RuleDefinition>, RuleListReadHandle {
	
	
	private List<RuleDefinition> rules;
	
	/**
	 * returns an iterator over a set of rules.
	 * @return an iterator over [matched] rules.
	 */
	@Override
    public Iterator<RuleDefinition> iterator(){ 
    	return rules.iterator();
    }

	/**
	 * gets the number of rules in the list.
	 * @return the number of rules.
	 */
	public int size() {
		return rules.size();
	}

	protected Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	
	protected void receiveContent(InputStream content) {
		rules = new ArrayList<RuleDefinition>();
		DOMHandle domHandle = new DOMHandle();
		HandleAccessor.receiveContent(domHandle, content);
		Document document = domHandle.get();
		NodeList ruleNodes = document.getElementsByTagNameNS(RequestConstants.RESTAPI_NS, "rule");
		for (int i=0; i < ruleNodes.getLength(); i++) {
			Element ruleElement = (Element) ruleNodes.item(i);
			RuleDefinition ruleDefinition = new RuleDefinition();
			ruleDefinition.receiveElement(ruleElement);
			rules.add(ruleDefinition);
		}
	}
}
