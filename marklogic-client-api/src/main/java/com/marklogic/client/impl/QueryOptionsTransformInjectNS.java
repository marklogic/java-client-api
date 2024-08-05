/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.XMLConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class QueryOptionsTransformInjectNS extends XMLFilterImpl {
  private static final String SEARCH_NS = "http://marklogic.com/appservices/search";
  private boolean inBinding = false;
  private Map<String, String> nsmap = new HashMap<> ();

  public QueryOptionsTransformInjectNS() {
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (SEARCH_NS.equals(uri)) {
      if ("namespace-bindings".equals(localName)) {
        inBinding = true;
        nsmap.clear();
      }

      if (inBinding && "binding".equals(localName)) {
        nsmap.put(attributes.getValue("prefix"), attributes.getValue("namespace-uri"));
      }

      if (inBinding) {
        return;
      }

      BindingAttributes attrs = new BindingAttributes(attributes);
      if ("path-index".equals(localName) || "searchable-expression".equals(localName)) {
        String xmlns = null;
        for (Map.Entry<String,String> entry : nsmap.entrySet()) {
          xmlns = "xmlns";
          if (!"".equals(entry.getKey())) {
            xmlns += ":" + entry.getKey();
          }
          attrs.addAttribute(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, xmlns, entry.getValue());
        }
      }

      super.startElement(uri, localName, qName, attrs);
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (SEARCH_NS.equals(uri) && "namespace-bindings".equals(localName)) {
      inBinding = false;
      return;
    }

    if (inBinding) {
      return;
    }

    super.endElement(uri, localName, qName);
  }

  @Override
  public void characters(char[] chars, int i, int i1) throws SAXException {
    if (inBinding) {
      return;
    }
    super.characters(chars, i, i1);
  }

  private static class BindingAttributes implements Attributes {
    private Vector<String> names = new Vector<> ();
    private Vector<String> values = new Vector<> ();
    private Vector<String> nsuris = new Vector<> ();

    @SuppressWarnings("unused")
    public BindingAttributes() {
    }

    public BindingAttributes(Attributes attributes) {
      if (attributes == null) {
        return;
      }

      for (int pos = 0; pos < attributes.getLength(); pos++) {
        names.add(attributes.getQName(pos));
        values.add(attributes.getValue(pos));
        nsuris.add(attributes.getURI(pos));
      }
    }

    public void addAttribute(String uri, String qName, String value) {
      if (getIndex(qName) < 0) {
        nsuris.add(uri);
        names.add(qName);
        values.add(value);
      }
    }

    @Override
    public int getLength() {
      return names.size();
    }

    @Override
    public String getURI(int i) {
      return nsuris.get(i);
    }

    @Override
    public String getLocalName(int i) {
      String name = names.get(i);
      if (name.contains(":")) {
        name = name.substring(name.indexOf(":"));
      }
      return name;
    }

    @Override
    public String getQName(int i) {
      return names.get(i);
    }

    @Override
    public String getType(int i) {
      return "CDATA";
    }

    @Override
    public String getValue(int i) {
      return values.get(i);
    }

    @Override
    public int getIndex(String uri, String localName) {
      for (int i = 0; i < names.size(); i++) {
        if (localName.equals(getLocalName(i)) && uri.equals(getURI(i))) {
          return i;
        }
      }

      return -1;
    }

    @Override
    public int getIndex(String qName) {
      for (int i = 0; i < names.size(); i++) {
        if (qName.equals(names.get(i))) {
          return i;
        }
      }
      return -1;
    }

    @Override
    public String getType(String uri, String localName) {
      if (getIndex(uri, localName) >= 0) {
        return "CDATA";
      } else {
        return null;
      }
    }

    @Override
    public String getType(String qName) {
      if (getIndex(qName) >= 0) {
        return "CDATA";
      } else {
        return null;
      }
    }

    @Override
    public String getValue(String uri, String localName) {
      int pos = getIndex(uri, localName);
      if (pos >= 0) {
        return getValue(pos);
      } else {
        return null;
      }
    }

    @Override
    public String getValue(String qName) {
      int pos = getIndex(qName);
      if (pos >= 0) {
        return getValue(pos);
      } else {
        return null;
      }
    }
  }
}
