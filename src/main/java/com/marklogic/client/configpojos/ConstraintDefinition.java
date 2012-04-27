package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;


/**
 * Each constraint in the MarkLogic Search API is of a certain type.  This class is the root of the class hierarchy of Range, Word, Value, etc.
 * Note: It contains convenience methods for helping with index definitions, which are not applicable to all constraint types.
 *
 */
public abstract class  ConstraintDefinition<T extends ConstraintDefinition<T>> {

	private Constraint parent;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="element")
	private QNamePOJO elementReference;

	@XmlElement(namespace=Options.SEARCH_NS, name="attribute")
	private QNamePOJO attributeReference;

	@XmlElement(namespace=Options.SEARCH_NS, name="field")
	private Field fieldReference;
	
	private SuggestionSource suggestionSource;

	public ConstraintDefinition() {
	}
	
	/**
	 * Create a new Constraint definition and nest it inside of a constraint, returning the child object.
	 * @param constraint constraint in which to embed this definition.
	 * @return this object, for further fluent setters.
	 */
	public T inside(Constraint constraint) {
		this.parent = constraint;
		this.parent.setImplementation(this);
		return (T) this;
	}
	

	public T inside(SuggestionSource suggestionSource) {
		this.suggestionSource = suggestionSource;
		this.suggestionSource.setImplementation(this);
		return (T) this;
	}
	
	public String getConstraintName() {
		return parent.getName();
	}

	/**
	 * @return the constraint enclosing this ConstraintDefinition
	 */
	public Constraint getConstraint() {
		return parent;
	}


	/**
	 * Add a reference to an element to this ConstraintDefinition
	 * @param ns Namespace URI of the element's QName;
	 * @param name Local name of the element.
	 * @return this
	 */
	public T withElement(String ns, String name) {
		this.elementReference = new QNamePOJO(ns, name);
		return (T) this;
	}
	public T withElement(String name) {
		return this.withElement("", name);
	}
	


	public T withAttribute(String ns, String name) {
		this.attributeReference = new QNamePOJO(ns, name);
		return (T) this;
	}


	public T withAttribute(String name) {
		return this.withAttribute("", name);
	}
	
	public QName getAttribute() {
		return attributeReference.asQName();
	}



	public QName getElement() {
		return elementReference.asQName();
	}

	public T withField(String name) {
		this.fieldReference = new Field(name);
		return (T) this;

	}

	public String getField() {
		return this.fieldReference.getName();
	}

	public SuggestionSource getSuggestionSource() {
		return suggestionSource;
	}
	
}
