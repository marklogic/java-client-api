package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class WordLexicon {


	@XmlAttribute
	private String collation;
	
	@XmlElement(name="fragment-scope")
	private FragmentScope fragmentScope;
	
	public String getCollation() {
		return collation;
	}

	public FragmentScope getFragmentScope() {
		return fragmentScope;
	}
	
	public WordLexicon withCollation(String collation) {
		this.collation = collation;
		return this;
	}
	
	public WordLexicon withFragmentScope(FragmentScope fragmentScope) {
		this.fragmentScope= fragmentScope;
		return this;
	}
}
