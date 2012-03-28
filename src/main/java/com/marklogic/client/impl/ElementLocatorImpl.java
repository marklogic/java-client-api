package com.marklogic.client.impl;

import com.marklogic.client.ElementLocator;

import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/19/12
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElementLocatorImpl implements ElementLocator {
    QName element = null;
    QName attribute = null;

    public ElementLocatorImpl(QName element) {
        this.element = element;
    }

    protected ElementLocatorImpl(QName element, QName attribute) {
        this.element = element;
        this.attribute = attribute;
    }

    @Override
    public QName getElement() {
        return element;
    }

    @Override
    public void setElement(QName qname) {
        element = qname;
    }

    @Override
    public QName getAttribute() {
        return attribute;
    }

    @Override
    public void setAttribute(QName qname) {
        attribute = qname;
    }
}
