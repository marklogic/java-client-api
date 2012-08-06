/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.example.batch;

import java.util.Iterator;

import javax.xml.namespace.QName;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.util.RequestParameters;

/**
 * SearchCollector provides an extension for collecting a set of database
 * documents based on a search.  The query options for the search must return
 * results but need not return snippets.
 */
public class SearchCollector extends ResourceManager {
	static final public String NAME = "searchcollect";

	private long pageLength = QueryManager.DEFAULT_PAGE_LENGTH;

	public SearchCollector(DatabaseClient client) {
		super();
		client.init(NAME, this);
	}

	public long getPageLength() {
		return pageLength;
	}
	public void setPageLength(long pageLength) {
		this.pageLength = pageLength;
	}

	// Potential improvements:
	// allow collection, directory criteria
	// support facet and metadata views
	public CollectorResults collect(String criteria, long start) {
		return collect(criteria, start, null);
	}
	public CollectorResults collect(String criteria, long start, String optionsName) {
		if (criteria == null)
			throw new IllegalArgumentException("null query criteria");
		if (criteria.length() == 0)
			throw new IllegalArgumentException("empty query criteria");

		RequestParameters params = initParams(optionsName, start);
		params.add("q", criteria);

		return getResults(params);
	}
	public CollectorResults collect(String key, String value, long start) {
		return collect(key, value, start, null);
	}
	public CollectorResults collect(
		String key, String value, long start, String optionsName
	) {
		if (key == null)
			throw new IllegalArgumentException("null query key");
		if (value == null)
			throw new IllegalArgumentException("null query value ");

		RequestParameters params = initParams(optionsName, start);
		params.put("key",   key);
		params.put("value", value);

		return getResults(params);
	}
	public CollectorResults collect(QName elementName, String value, long start) {
		return collect(elementName, null, value, start, null);
	}
	public CollectorResults collect(
		QName elementName, String value, long start, String optionsName
	) {
		return collect(elementName, null, value, start, optionsName);
	}
	public CollectorResults collect(
		QName elementName, QName attributeName, String value, long start
	) {
		return collect(elementName, attributeName, value, start, null);
	}
	public CollectorResults collect(
		QName elementName, QName attributeName, String value, long start, String optionsName
	) {
		if (elementName == null)
			throw new IllegalArgumentException("null query element");
		if (value == null)
			throw new IllegalArgumentException("null query value ");

		RequestParameters params = initParams(optionsName, start);
		params.put("element", elementName.toString());
		if (attributeName != null)
			params.put("attribute", attributeName.toString());
		params.put("value",  value);

		return getResults(params);
	}
	public CollectorResults collect(StructuredQueryDefinition def, long start) {
		if (def == null)
			throw new IllegalArgumentException("null query definition");

		String criteria = def.serialize();
		if (criteria == null)
			throw new IllegalArgumentException("null query criteria");
		if (criteria.length() == 0)
			throw new IllegalArgumentException("empty query criteria");

		RequestParameters params = initParams(def.getOptionsName(), start);

		StringHandle criteriaHandle = new StringHandle(criteria);
		criteriaHandle.setFormat(Format.XML);

		ServiceResultIterator resultItr =
			getServices().post(params, criteriaHandle);
		if (resultItr == null || ! resultItr.hasNext())
			return null;

		return new CollectorResults(resultItr);
	}

	private RequestParameters initParams(String optionsName, long start) {
		RequestParameters params = new RequestParameters();
		params.add("format", "xml");
		params.add("start",  String.valueOf(start));

		if (optionsName != null && optionsName.length() > 0)
			params.add("options", optionsName);
		if (pageLength != QueryManager.DEFAULT_PAGE_LENGTH)
			params.add("pageLength", String.valueOf(pageLength));

		return params;
	}
	private CollectorResults getResults(RequestParameters params) {
		ServiceResultIterator resultItr = getServices().get(params);
		if (resultItr == null || ! resultItr.hasNext())
			return null;

		return new CollectorResults(resultItr);
	}

	static public class CollectorResults implements Iterator<ServiceResult> {
		private ServiceResultIterator resultItr;
		private ServiceResult         searchResult;

		CollectorResults(ServiceResultIterator resultItr) {
			super();
			if (resultItr != null) {
				if (resultItr.hasNext())
					searchResult = resultItr.next();
				this.resultItr = resultItr;
			}
		}

		public <R extends XMLReadHandle> R getSearchResult(R handle) {
			if (searchResult == null)
				return null;
			return searchResult.getContent(handle);
		}

		@Override
		public boolean hasNext() {
			if (resultItr == null)
				return false;

			if (!resultItr.hasNext()) {
				resultItr.close();
				resultItr    = null;
				searchResult = null;
				return false;
			}

			return true;
		}
		@Override
		public ServiceResult next() {
			if (!hasNext())
				return null;

			return resultItr.next();
		}
		public <R extends AbstractReadHandle> R next(R handle) {
			if (!hasNext())
				return null;

			return resultItr.next().getContent(handle);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("cannot remove result");
		}

		public void close() {
			if (resultItr != null) {
				resultItr.close();
				resultItr = null;
			}

			if (searchResult != null) {
				searchResult = null;
			}
		}

		@Override
		protected void finalize() throws Throwable {
			close();
			super.finalize();
		}
	}
}
