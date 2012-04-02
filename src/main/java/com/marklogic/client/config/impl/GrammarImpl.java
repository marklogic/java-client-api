package com.marklogic.client.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.Grammar;
import com.marklogic.client.config.JAXBBackedQueryOption;
import com.marklogic.client.config.Joiner;
import com.marklogic.client.config.Starter;

public class GrammarImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.Grammar> implements JAXBBackedQueryOption, Grammar {


	//TODO construct from scratch.
	
	GrammarImpl(com.marklogic.client.config.search.jaxb.Grammar ot) {
		jaxbObject = ot;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getQuotationOrImplicitOrJoiner();
	}

	@Override
	public String getQuotation() {
		List<String> l = getElementList(this, String.class);
		if (l.size() == 0) {
			return null;
		}
		else {
			return l.get(0);
		}
	}


	@Override
	public Element getImplicit() {
		List<com.marklogic.client.config.search.jaxb.Implicit> l = getElementList(this, com.marklogic.client.config.search.jaxb.Implicit.class);
		if (l.size() == 0) {
			return null;
		}
		else {
			return l.get(0).getAny();
		}
	}

	@Override
	public List<Joiner> getJoiners() {
		List<com.marklogic.client.config.search.jaxb.Joiner> l = getElementList(this, com.marklogic.client.config.search.jaxb.Joiner.class);
		List<Joiner> joiners = new ArrayList<Joiner>();
		for (com.marklogic.client.config.search.jaxb.Joiner j : l) {
			Joiner newJoiner = new JoinerImpl(j);
			joiners.add(newJoiner);
		}
		return joiners;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getElementList(JAXBBackedQueryOption option,
			Class<T> clazz) {
		List<T> l = new ArrayList<T>();
		for (Object o : option.getJAXBChildren() ) {
			if (o.getClass() == clazz) {
				l.add((T) o);
			}
		}
		return l;
	}

	@Override
	public List<Starter> getStarters() {
		List<com.marklogic.client.config.search.jaxb.Starter> s = getElementList(this, com.marklogic.client.config.search.jaxb.Starter.class);
		List<Starter> starters = new ArrayList<Starter>();
		for (com.marklogic.client.config.search.jaxb.Starter starter : s) {
			Starter newStarter = new StarterImpl(starter);
			starters.add(newStarter);
		}
		return starters;
	}

	
}
