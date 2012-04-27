package com.marklogic.client.configpojos;


public enum TermApply {

	ALL_RESULTS, NO_RESULTS;

	public String toXmlString() {
		return this.toString().toLowerCase().replace("_", "-");
	}

	public static TermApply fromXmlString(String xmlString) {
		return TermApply.valueOf(xmlString.toUpperCase().replace("-", "_"));
	}

}
