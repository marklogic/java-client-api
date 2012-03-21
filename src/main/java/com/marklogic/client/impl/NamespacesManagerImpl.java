package com.marklogic.client.impl;

import java.io.InputStream;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.EditableNamespaceContext;
import com.marklogic.client.NamespacesManager;

class NamespacesManagerImpl implements NamespacesManager {
	static final private Logger logger = LoggerFactory.getLogger(NamespacesManagerImpl.class);

	private RESTServices services;

	NamespacesManagerImpl(RESTServices services) {
		super();
		this.services = services;
	}

	private final static Pattern NAMESPACE_PATTERN = Pattern.compile(
			"<([^: >]+:)?namespace(>| [^>]+>)([^<>]+)</([^: >]+:)?namespace>");

	@Override
	public String readPrefix(String prefix) {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot read namespace for null prefix");

		String def = services.getValue("namespaces", prefix, "application/xml", String.class);
		if (def == null)
			return null;

//		def.replaceFirst(regex, replacement)

		// TODO: extract namespaceUri from structure
		return null;
	}
	@Override
	public void writePrefix(String prefix, String namespaceUri) {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot write binding for null prefix");
		if (namespaceUri == null)
			throw new IllegalArgumentException("Cannot write binding for null namespaceUri");

		String structure =
			"<?xml version=\"1.0\" encoding=\"utf-8\">\n"+
			"<namespace xmlns=\"http://marklogic.com/xdmp/group\">\n"+
			"    <prefix>"+prefix+"</prefix>\n"+
			"    <namespace-uri>"+namespaceUri+"</namespace-uri>\n"+
			"</namespace>\n";

		services.putValue("namespaces", prefix, "application/xml", structure);
	}
	@Override
	public void deletePrefix(String prefix) {
		if (prefix == null)
			throw new IllegalArgumentException("Cannot delete binding for null prefix");

		services.deleteValue("namespaces", prefix);
	}

	@Override
	public EditableNamespaceContext readAll() {
		EditableNamespaceContext context = new EditableNamespaceContext();

		InputStream stream = services.getValues("namespaces", "application/xml", InputStream.class);

		// TODO extract and assign to context

		return context;
	}
	@Override
	public void deleteAll() {
		services.deleteValues("namespaces");
	}
}
