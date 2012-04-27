package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Models a constraint on collection URI.
 *
 */
@XmlRootElement(name="collection")
public class Collection extends FacetableConstraintDefinition<Collection> {

	/**
	 * This value is removed from collection URIs when creating facet labels.
	 */
	@XmlAttribute
	private String prefix;

	/**
	 * Set the collection prefix, returning the modified Collection object.
	 * @param prefix The prefix to be removed from collection URIs in generating facet labels.
	 * @return The modified Collection object.
	 */
	public Collection withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	
	
	/**
	 * 
	 * @return The prefix to be removed from collection URIs in generating facet labels.
	 */
	
	public String getPrefix() {
		return prefix;
	}
	

}
