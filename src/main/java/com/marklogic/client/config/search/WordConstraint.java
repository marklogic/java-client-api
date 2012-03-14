package com.marklogic.client.config.search;

import com.marklogic.client.config.search.jaxb.Word;

public class WordConstraint extends IndexableConstraint {

	public WordConstraint(String name, Word constraintSpec) {
		super(name);
	}

}
