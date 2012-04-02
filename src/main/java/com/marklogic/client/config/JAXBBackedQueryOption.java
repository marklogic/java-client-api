package com.marklogic.client.config;

import java.util.List;



/**
 * This interface binds the hierarchy of Config Options classes 
 * to an implementation backed by a JAXB facade.
 * TODO -- replace with a DOM-based implementation.
 * @author cgreer
 *
 */
public interface JAXBBackedQueryOption {

	/**
	 * 
	 * @return the JAXB Java object that backs the QueryOptions component.
	 */
	public Object asJAXB();

	/**
	 * 
	 * @return the list of child objects associated with this JAXBBackedQueryOption componenet.
	 */
	public List<Object> getJAXBChildren();
	
}
