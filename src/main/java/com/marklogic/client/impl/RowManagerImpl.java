/*
 * Copyright 2016 MarkLogic Corporation
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
package com.marklogic.client.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.Transaction;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.expression.Xs;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.RowReadHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.util.RequestParameters;

public class RowManagerImpl
    extends AbstractLoggingManager
    implements RowManager
{
    private RESTServices services;
	private HandleFactoryRegistry handleRegistry;

	public RowManagerImpl(RESTServices services) {
		super();
		this.services = services;
	}

	HandleFactoryRegistry getHandleRegistry() {
		return handleRegistry;
	}
	void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
		this.handleRegistry = handleRegistry;
	}

	@Override
	public PlanBuilder newPlanBuilder() {
		Xs xs = new XsExprImpl();
		return new PlanBuilderImpl(
				new CtsExprImpl(xs), new FnExprImpl(xs), new JsonExprImpl(xs), new MapExprImpl(xs),
				new MathExprImpl(xs), new RdfExprImpl(xs), new SemExprImpl(xs), new SqlExprImpl(xs),
				new XdmpExprImpl(xs), xs
				);
	}

	@Override
	public <T> T resultDocAs(Plan plan, Class<T> as) {
		return resultDocAs(plan, as, null);
	}
	@Override
	public <T> T resultDocAs(Plan plan, Class<T> as, Transaction transaction) {
		if (as == null) {
			throw new IllegalArgumentException("Must specify a class for content with a registered handle");
		}

		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!(handle instanceof RowReadHandle)) {
			if (handle == null) {
		    	throw new IllegalArgumentException("Class \"" + as.getName() + "\" has no registerd handle");
			} else {
		    	throw new IllegalArgumentException("Class \"" + as.getName() + "\" uses handle " +
						handle.getClass().getName() + " which is not a RowReadHandle");
			}
	    }

	    if (resultDoc(plan, (RowReadHandle) handle, transaction) == null) {
	    	return null;
	    }

		return handle.get();
	}
	@Override
	public <T extends RowReadHandle> T resultDoc(Plan plan, T resultsHandle) {
		return resultDoc(plan, resultsHandle, null);
	}
	@Override
	public <T extends RowReadHandle> T resultDoc(Plan plan, T resultsHandle, Transaction transaction) {
		if (!(plan instanceof PlanBuilderBase.PlanBase)) {
			if (plan == null) {
				throw new IllegalArgumentException("Must specify a plan to produce the row result document");
			} else {
				throw new IllegalArgumentException("Cannot retrieve row result document with invalid plan having class "+plan.getClass().getName());
			}
		}
		if (resultsHandle == null) {
			throw new IllegalArgumentException("Must specify a handle to read the row result document");
		}

		PlanBuilderBase.PlanBase exportablePlan = (PlanBuilderBase.PlanBase) plan;

		// TODO: maybe serialize plan to JSON using JSON writer?
		String ast = exportablePlan.getAst();
		AbstractWriteHandle astHandle = new StringHandle(ast);

// TODO: parameter bindings
		RequestParameters params = new RequestParameters();

		return services.postResource(requestLogger, "rows", transaction, params, astHandle, resultsHandle);
	}

	@Override
	public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle) {
		return resultRows(plan, rowHandle, null);
	}
	@Override
	public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rawHandle, Transaction transaction) {
		if (!(plan instanceof PlanBuilderBase.PlanBase)) {
			if (plan == null) {
				throw new IllegalArgumentException("Must specify a plan to iterate the row results");
			} else {
				throw new IllegalArgumentException("Cannot iterate rows with invalid plan having class "+plan.getClass().getName());
			}
		}
		if (rawHandle == null) {
			throw new IllegalArgumentException("Must specify a handle to iterate over the rows");
		} else if (!(rawHandle instanceof BaseHandle)) {
			throw new IllegalArgumentException("Cannot iterate rows with invalid handle having class "+rawHandle.getClass().getName());
		}

		BaseHandle<?,?> rowHandle = (BaseHandle<?,?>) rawHandle;

		Format handleFormat = rowHandle.getFormat();
		String rowFormat    = "sparql-json";
		switch (handleFormat) {
		case JSON:
		case UNKNOWN:
			break;
		case XML:
			rowFormat = "sparql-xml";
			break;
		default:
			throw new IllegalArgumentException("Must use JSON or XML format to iterate rows instead of "+handleFormat.name());
		}

		PlanBuilderBase.PlanBase exportablePlan = (PlanBuilderBase.PlanBase) plan;

		// TODO: maybe serialize plan to JSON using JSON writer?
		String ast = exportablePlan.getAst();
		AbstractWriteHandle astHandle = new StringHandle(ast);

		// TODO: parameter bindings
		RequestParameters params = new RequestParameters();
		params.add("row-format", rowFormat);

// QUESTION: outputMimetypes a noop?
		ServiceResultIterator iter = 
				services.postIteratedResource(requestLogger, "rows", transaction, params, astHandle);

// TODO: distinguish request for homogeneous JSON or XML rows from map that can process CIDs
// TODO: RowRecord should wrap ServiceResult
		return new RowSetImpl<T>(rawHandle, iter);
	}
	static class RowSetImpl<T extends RowReadHandle> implements RowSet<T>, Iterator<T> {
		private T                     rowHandle = null;
		private ServiceResultIterator results   = null;
		private ServiceResult         nextRow   = null;

		RowSetImpl(T rowHandle, ServiceResultIterator results) {
			this.rowHandle = rowHandle;
			this.results   = results;
			if (results.hasNext()) {
				nextRow = results.next();
			}
		}

		@Override
		public String[] getColumnNames() {
			// TODO: get from SPARQL header
			return null;
		}

		@Override
		public Iterator<T> iterator() {
			return this;
		}
		@Override
		public Stream<T> stream() {
			return StreamSupport.stream(this.spliterator(), false);
		}

		@Override
		public boolean hasNext() {
			if (results == null) {
				return false;
			}
			return results.hasNext();
		}
		@Override
		public T next() {
			ServiceResult currentRow = nextRow;
			if (currentRow == null) {
				throw new NoSuchElementException("no next row");
			}

// QUESTION: threading guarantees - multiple handles? precedent?
			T currentHandle = rowHandle;
			if (results.hasNext()) {
				nextRow = results.next();
			} else {
				closeImpl();
			}

			return currentRow.getContent(currentHandle);
		}

		@Override
		public void close() {
			closeImpl();
		}
		@Override
		protected void finalize() throws Throwable {
			closeImpl();
			super.finalize();
		}
		private void closeImpl() {
			if (results != null) {
				results.close();
				results   = null;
				nextRow   = null;
				rowHandle = null;
			}
		}
	}
}
