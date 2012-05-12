package com.marklogic.client.io;

import com.marklogic.client.config.QueryOptionsBuilder.QueryOptions;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;


public class SearchPrefixMapper  extends NamespacePrefixMapper {
	
	public String getPreferredPrefix(String namespaceUri, String suggestion,
			boolean requirePrefix) {
		if (namespaceUri.equals(QueryOptions.SEARCH_NS)) {
			return "search";
		}
		return suggestion;
	}
}