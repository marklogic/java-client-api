/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
  implements Iterable<RuleDefinition>, RuleListReadHandle
{


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

  @Override
  protected Class<InputStream> receiveAs() {
    return InputStream.class;
  }

  @Override
  protected void receiveContent(InputStream content) {
    rules = new ArrayList<>();
    DOMHandle domHandle = new DOMHandle();
    HandleAccessor.receiveContent(domHandle, content);
    Document document = domHandle.get();
    NodeList ruleNodes = document.getElementsByTagNameNS(RequestConstants.RESTAPI_NS, "rule");
    int ruleNodesLength = ruleNodes.getLength();
    for (int i=0; i < ruleNodesLength; i++) {
      Element ruleElement = (Element) ruleNodes.item(i);
      RuleDefinition ruleDefinition = new RuleDefinition();
      ruleDefinition.receiveElement(ruleElement);
      rules.add(ruleDefinition);
    }
  }
}
