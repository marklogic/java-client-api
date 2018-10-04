/*
 * Copyright 2012-2018 MarkLogic Corporation
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
package com.marklogic.client.impl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.marklogic.client.MarkLogicInternalException;

public class DOMWriter {
  private XMLStreamWriter serializer;

  public DOMWriter() {
    super();
  }
  public DOMWriter(XMLStreamWriter serializer) {
    this();
    this.serializer = serializer;
  }

  public void serializeNodeList(NodeList list) throws XMLStreamException {
    for (int i=0; i < list.getLength(); i++) {
      serializeNode(list.item(i));
    }
  }
  public void serializeNode(Node node) throws XMLStreamException {
    switch (node.getNodeType()) {
      case Node.DOCUMENT_NODE:
        serializeDocument((Document) node);
        break;
      case Node.ELEMENT_NODE:
        serializeElement((Element) node);
        break;
      case Node.CDATA_SECTION_NODE:
        serializeCDATASection((CDATASection) node);
        break;
      case Node.TEXT_NODE:
        serializeText((Text) node);
        break;
      case Node.PROCESSING_INSTRUCTION_NODE:
        serializeProcessingInstruction((ProcessingInstruction) node);
        break;
      case Node.COMMENT_NODE:
        serializeComment((Comment) node);
        break;
      default:
        throw new MarkLogicInternalException(
          "Cannot process node type of: "+node.getClass().getName()
        );
    }
  }
  public void serializeDocument(Document document) throws XMLStreamException {
	  
    String encoding = document.getInputEncoding();
    String version  = document.getXmlVersion();
    if (encoding != null) {
      serializer.writeStartDocument(encoding, version);
    } else {
      serializer.writeStartDocument(version);
    }
    if (document.hasChildNodes()) {
      serializeNodeList(document.getChildNodes());
    }
    serializer.writeEndDocument();
  }
  public void serializeElement(Element element) throws XMLStreamException {
    String namespaceURI = element.getNamespaceURI();
    String prefix       = (namespaceURI != null) ? element.getPrefix() : null;
    String localName    = (namespaceURI != null) ? element.getLocalName() : element.getTagName();
    if (element.hasChildNodes()) {
      if (prefix != null) {
        serializer.writeStartElement(prefix, localName, namespaceURI);
      } else if (namespaceURI != null) {
        serializer.writeStartElement("", localName, namespaceURI);
      } else {
        serializer.writeStartElement(localName);
      }
      if (element.hasAttributes()) {
        serializeAttributes(element.getAttributes());
      }
      serializeNodeList(element.getChildNodes());
      serializer.writeEndElement();
    } else {
      if (prefix != null) {
        serializer.writeEmptyElement(prefix, localName, namespaceURI);
      } else if (namespaceURI != null) {
        serializer.writeEmptyElement("", localName, namespaceURI);
      } else {
        serializer.writeEmptyElement(localName);
      }
      if (element.hasAttributes()) {
        serializeAttributes(element.getAttributes());
      }
    }
  }
  public void serializeAttributes(NamedNodeMap attributes) throws XMLStreamException {
    for (int i=0; i < attributes.getLength(); i++) {
      Attr attribute = (Attr) attributes.item(i);
      String namespaceURI = attribute.getNamespaceURI();
      String prefix       = (namespaceURI != null) ? attribute.getPrefix() : null;
      String localName    = (namespaceURI != null) ? attribute.getLocalName() : attribute.getName();
      String value        = attribute.getValue();
      if ("http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
        //TODO: Seems there should be a better way to prevent redundant namespaces.
      } else if (prefix != null) {
        serializer.writeAttribute(prefix, namespaceURI, localName, value);
      } else if (namespaceURI != null) {
        serializer.writeAttribute(namespaceURI, localName, value);
      } else {
        serializer.writeAttribute(localName, value);
      }
    }
  }
  public void serializeText(Text text) throws XMLStreamException {
    serializer.writeCharacters(text.getData());
  }
  public void serializeCDATASection(CDATASection cdata) throws XMLStreamException {
    serializer.writeCData(cdata.getData());
  }
  public void serializeComment(Comment comment) throws XMLStreamException {
    serializer.writeComment(comment.getData());
  }
  public void serializeProcessingInstruction(ProcessingInstruction pi) throws XMLStreamException {
    String target = pi.getTarget();
    String data   = pi.getData();
    if (data != null)
      serializer.writeProcessingInstruction(target, data);
    else
      serializer.writeProcessingInstruction(target);
  }
}
