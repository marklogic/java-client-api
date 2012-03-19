package com.marklogic.client;

import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/19/12
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ElementLocator extends ValueLocator {
    public QName getElement();
    public void setElement(QName qname);
    public QName getAttribute();
    public void setAttribute(QName qname);
}
