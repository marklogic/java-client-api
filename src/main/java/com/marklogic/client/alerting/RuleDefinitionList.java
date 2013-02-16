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

public class RuleDefinitionList
extends BaseHandle<InputStream,OperationNotSupported>
implements RuleListReadHandle {
	
	
	public List<RuleDefinition> rules;
	
    public Iterator<RuleDefinition> iterator(){ 
    	return rules.iterator();
    }

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