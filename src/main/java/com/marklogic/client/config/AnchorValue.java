package com.marklogic.client.config;

public enum AnchorValue {
	
	NOW, START_OF_DAY, START_OF_MONTH, START_OF_YEAR;
	public String toXmlString() {
		return this.toString().toLowerCase().replace("_", "-");
	}
	public static AnchorValue fromXmlString(String xmlString) {
		return AnchorValue.valueOf(xmlString.toUpperCase().replace("-", "_"));
	}		
				
	
	
}
