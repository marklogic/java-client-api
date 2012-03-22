package com.marklogic.client.config.search.impl;

import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.IndexReference;
import com.marklogic.client.config.search.Indexable;
import com.marklogic.client.config.search.SuggestionSource;
import com.marklogic.client.config.search.jaxb.Annotation;
import com.marklogic.client.config.search.jaxb.WordLexicon;

public class SuggestionSourceImpl extends AbstractSearchOption implements Indexable,SuggestionSource {


	private com.marklogic.client.config.search.jaxb.SuggestionSource jaxbObject;
	protected IndexReference indexReferenceImpl;
	
	public SuggestionSourceImpl(
			com.marklogic.client.config.search.jaxb.SuggestionSource ot) {		
		this.jaxbObject = ot;
		indexReferenceImpl = new IndexReferenceImpl(this.jaxbObject.getConstraintTypeOrWordLexiconOrSuggestionOption());
	}

	public SuggestionSourceImpl() {
       this(new com.marklogic.client.config.search.jaxb.SuggestionSource());
	}

	@Override
	public Object asJaxbObject() {
		return this.jaxbObject;
	}
	

	@Override
	public void addElementAttributeIndex(QName elementQName, QName attributeQName) {
		indexReferenceImpl.addElementAttributeIndex(elementQName, attributeQName);
	}


	@Override
	public void addElementIndex(QName elementQName) {
		indexReferenceImpl.addElementIndex(elementQName);
	}
	
	@Override
	public void addFieldIndex(String fieldName) {
		indexReferenceImpl.addFieldIndex(fieldName);
	}
	
	@Override
	public IndexReference getIndex() {
		return indexReferenceImpl;
	}

	@Override
	public void addAnnotation(Element annotation) {
		Annotation a = new com.marklogic.client.config.search.jaxb.Annotation();  //TODO provide for existing annotation.
		a.getContent().add(annotation);
		jaxbObject.getConstraintTypeOrWordLexiconOrSuggestionOption().add(annotation);
	}

	@Override
	public void addSuggestionOption(String option) {
		jaxbObject.getConstraintTypeOrWordLexiconOrSuggestionOption().add(JAXBHelper.wrapString("suggestion-option", option));
	}
	

	@Override
	public void useWordLexicon(String collation) {
		useWordLexicon(collation, "Document");
	}


	@Override
	public void useWordLexicon(String collation, String fragmentScope) {
		WordLexicon lexicon = new WordLexicon();
		lexicon.setFragmentScope(fragmentScope);
		lexicon.setCollation(collation);
		jaxbObject.getConstraintTypeOrWordLexiconOrSuggestionOption().add(new WordLexicon());
		
	}

	@Override
	public void useWordLexicon() {
		useWordLexicon("http://marklogic.com/collation/", "Documents");
	}

	@Override
	public List<Element> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getConstraintTypeOrWordLexiconOrSuggestionOption();
	}

	@Override
	public List<String> getSuggestionOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
