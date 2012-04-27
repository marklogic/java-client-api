package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Models elements that locate XQuery functions with use of "ns", "apply" and "at" attributes.
 * @see com.marklogic.client.configpojos.Custom
 * @see com.marklogic.client.configpojos.Term
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XQueryFunctionLocator  {

	/**
	 * The namespace URI of an XQuery module.
	 */
	@XmlAttribute
	private String ns;
	/**
	 * Where to find the XQuery module on the filesystem.
	 */
	@XmlAttribute
	private String at;
	/**
	 * Denotes a function within the XQuery module specified by "ns" and "at"
	 */
	@XmlAttribute
	private String apply;
	public String getNs() {
		return ns;
	}
	public void setNs(String ns) {
		this.ns = ns;
	}
	public String getAt() {
		return at;
	}
	public void setAt(String at) {
		this.at = at;
	}
	public String getApply() {
		return apply;
	}
	public void setApply(String apply) {
		this.apply = apply;
	}
	
}
