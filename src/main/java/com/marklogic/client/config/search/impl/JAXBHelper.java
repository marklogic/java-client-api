package com.marklogic.client.config.search.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.marklogic.client.config.search.FunctionRef;

public class JAXBHelper {


	public static JAXBElement<String> wrapString(QName elementName, String str) {

		JAXBElement<String> elem = new JAXBElement<String>(elementName,
				String.class, str);
		return elem;
	}

	public static JAXBElement<String> wrapString(String elementName, String str) {

		JAXBElement<String> elem = new JAXBElement<String>(JAXBHelper.newQNameFor(elementName),
				String.class, str);
		return elem;
	}

	public static JAXBElement<FunctionRef> wrapFunction(QName elementName,
			FunctionRef function) {
		JAXBElement<FunctionRef> elem = new JAXBElement<FunctionRef>(
				elementName, FunctionRef.class, FunctionRefImpl.class, function);
		return elem;
	}

	public static QName newQNameFor(String localName) {
		return new QName("http://marklogic.com/appservices/search", localName);
	}

}
