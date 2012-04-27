package com.marklogic.client.configpojos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="suggestion-source")
public class SuggestionSource {

	@XmlAttribute
	private String ref; // TODO in check options, validity of ref
	
	@XmlElement(name="suggestion-option")
	private List<String> suggestionOptions;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="annotation", required=false)
	private List<QueryAnnotation> annotations;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="collection")
	public Collection collection;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="value")
	public Value value;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="range")
	public Range range;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="word")
	public Word word;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="element-query")
	public ElementQuery elementQuery;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="properties")
	public Properties properties;
	
	@XmlElement(namespace=Options.SEARCH_NS,name="custom")
	public Custom custom;
	
	@XmlElement(namespace=Options.SEARCH_NS,name="geo-elem")
	public GeoElement geoElem;

	@XmlElement(namespace=Options.SEARCH_NS,name="geo-attr-pair")
	public GeoAttrPair geoAttrPair;
	
	@XmlElement(namespace=Options.SEARCH_NS,name="geo-elem-pair")
	public GeoElementPair geoElemPair;
	
	@XmlElement(namespace=Options.SEARCH_NS,name = "geo-options", required = false)
    private List<String> geoOptions;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="word-lexicon")
	private WordLexicon wordLexicon;
	
	public SuggestionSource withWordLexicon() {
		this.wordLexicon = new WordLexicon().withCollation("http://marklogic.com/collation");
		return this;
	}
	
	
	public <T extends ConstraintDefinition> void setImplementation(T constraintType) {
		if (constraintType.getClass() == Collection.class) {
			collection = (Collection) constraintType;
		} else if (constraintType.getClass() == Value.class) {
			value = (Value) constraintType;
		} else if (constraintType.getClass() == Word.class) {
			word = (Word) constraintType;
		} else if (constraintType.getClass() == Range.class) {
			range = (Range) constraintType;
		} else if (constraintType.getClass() == ElementQuery.class) {
			elementQuery = (ElementQuery) constraintType;
		} else if (constraintType.getClass() == Properties.class) {
			properties = (Properties) constraintType;
		} else if (constraintType.getClass() == Custom.class) {
			custom = (Custom) constraintType;
		} else if (constraintType.getClass() == GeoElement.class) {
			geoElem = (GeoElement) constraintType;
		} else if (constraintType.getClass() == GeoAttrPair.class) {
			geoAttrPair = (GeoAttrPair) constraintType;
		} else if (constraintType.getClass() == GeoElementPair.class) {
			geoElemPair = (GeoElementPair) constraintType;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ConstraintDefinition> T getImplementation() {
		if (collection != null) {
			return (T) collection.inside(this);
		} else

		if (value != null) {
			return (T) value.inside(this);
		} else

		if (range != null) {
			return (T) range.inside(this);
		} else

		if (word != null) {
			return (T) word.inside(this);
		} else

		if (elementQuery != null) {
			return (T) elementQuery.inside(this);
		} else

		if (properties != null) {
			return (T) properties.inside(this);
		} else

		if (custom != null) {
			return (T) custom.inside(this);
		} else

		if (geoElem != null) {
			return (T) geoElem.inside(this);
		} else

		if (geoAttrPair != null) {
			return (T) geoAttrPair.inside(this);
		} else

		if (geoElemPair != null) {
			return (T) geoElemPair.inside(this);
		}
		return null;

	}


	public SuggestionSource() {
		this.suggestionOptions = new ArrayList<String>();
	}
	
	public SuggestionSource withSuggestionOption(String suggestionOption) {
		this.suggestionOptions.add(suggestionOption);
		return this;
	}


	public List<String> getSuggestionOptions() {
		return suggestionOptions;
	}
}
