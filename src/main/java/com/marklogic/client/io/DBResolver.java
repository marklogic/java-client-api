package com.marklogic.client.io;

import javax.xml.transform.URIResolver;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import javax.xml.stream.XMLResolver;

public interface DBResolver extends EntityResolver, LSResourceResolver, URIResolver, XMLResolver {
    public String getBaseURI();
    public void setBaseUri(String uri);
}
