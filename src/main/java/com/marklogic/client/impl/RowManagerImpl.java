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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.Transaction;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.expression.PlanBuilder.PlanParam;
import com.marklogic.client.expression.Xs;
import com.marklogic.client.expression.XsValue;
import com.marklogic.client.expression.XsValue.AnyAtomicTypeVal;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.impl.RESTServices.RESTServiceResult;
import com.marklogic.client.impl.RESTServices.RESTServiceResultIterator;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.RowReadHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
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
	public RawPlanDefinition newRawPlanDefinition(JSONWriteHandle handle) {
		return new RawPlanDefinitionImpl(handle);
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
		AbstractWriteHandle astHandle = getPlanHandle(plan);

		if (resultsHandle == null) {
			throw new IllegalArgumentException("Must specify a handle to read the row result document");
		}

// TODO: parameter bindings
		RequestParameters params = new RequestParameters();

		return services.postResource(requestLogger, "rows", transaction, params, astHandle, resultsHandle);
	}

	@Override
	public RowSet<RowRecord> resultRows(Plan plan) {
		return resultRows(plan, new RowRecordImpl(), null);
	}
	@Override
	public RowSet<RowRecord> resultRows(Plan plan, Transaction transaction) {
		return resultRows(plan, new RowRecordImpl(), transaction);
	}
	@Override
	public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle) {
		return resultRows(plan, rowHandle, null);
	}
	@Override
	public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle, Transaction transaction) {
		AbstractWriteHandle astHandle = getPlanHandle(plan);

		if (rowHandle == null) {
			throw new IllegalArgumentException("Must specify a handle to iterate over the rows");
		}

		String rowFormat = "sparql-json";
		String docCols   = "inline";
		if (rowHandle instanceof RowRecordImpl) {
			docCols = "reference";
		} else if (rowHandle instanceof BaseHandle) {
			BaseHandle<?,?> baseHandle = (BaseHandle<?,?>) rowHandle;

			Format handleFormat = baseHandle.getFormat();
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
		} else {
			throw new IllegalArgumentException("Cannot iterate rows with invalid handle having class "+rowHandle.getClass().getName());
		}


// TODO: parameter bindings
		RequestParameters params = new RequestParameters();
		params.add("row-format",       rowFormat);
		params.add("document-columns", docCols);

// QUESTION: outputMimetypes a noop?
		RESTServiceResultIterator iter = 
				services.postIteratedResource(requestLogger, "rows", transaction, params, astHandle);

		return new RowSetImpl<T>(rowHandle, iter);
	}
	private AbstractWriteHandle getPlanHandle(Plan plan) {
		if (plan == null) {
			throw new IllegalArgumentException("Must specify a plan to produce row results");
		} else if (plan instanceof PlanBuilderBase.PlanBase) {
			PlanBuilderBase.PlanBase exportablePlan = (PlanBuilderBase.PlanBase) plan;

			// TODO: maybe serialize plan to JSON using JSON writer?
			String ast = exportablePlan.getAst();
			return new StringHandle(ast);
		} else if (plan instanceof RawPlanDefinitionImpl) {
			RawPlanDefinitionImpl rawPlan = (RawPlanDefinitionImpl) plan;
			return rawPlan.getHandle();
		}
		throw new IllegalArgumentException("Cannot produce rows with invalid plan having class "+plan.getClass().getName());
	}

	static class RowSetImpl<T extends RowReadHandle> implements RowSet<T>, Iterator<T> {
		private T                         rowHandle   = null;
		private boolean                   isRowRecord = false;
		private RESTServiceResultIterator results     = null;
		private RESTServiceResult         nextRow     = null;

		RowSetImpl(T rowHandle, RESTServiceResultIterator results) {
			this.rowHandle = rowHandle;
			this.results   = results;
			if (rowHandle instanceof RowRecord) {
			    isRowRecord = true;
			}
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
			return nextRow != null;
		}
		@Override
		public T next() {
			RESTServiceResult currentRow = nextRow;
			if (currentRow == null) {
				throw new NoSuchElementException("no next row");
			}

			T currentHandle = rowHandle;

			boolean hasMoreRows = results.hasNext();

// QUESTION: threading guarantees - multiple handles? precedent?
			if (!isRowRecord) {
				if (hasMoreRows) {
					nextRow = results.next();
				} else {
					closeImpl();
				}
				return currentRow.getContent(currentHandle);
			}

			try {
				RowRecordImpl rowRecord = (RowRecordImpl) rowHandle;

				@SuppressWarnings("unchecked")
				Map<String, Object> row = new ObjectMapper().readValue(currentRow.getContent(new InputStreamHandle()).get(), Map.class);
				row.replaceAll((key, rawBinding) -> {
					@SuppressWarnings("unchecked")
					Map<String,Object> binding = (Map<String,Object>) rawBinding;
// TODO: capture SPARQL type and datatype for casting on request
// TODO: special processing for cid?
					String type     = (String) binding.get("type");
					String datatype = (String) binding.get("datatype");
					Object value    = binding.get("value");
					return value;
				});

				while (hasMoreRows) {
					currentRow = results.next();
					Map<String,List<String>> headers = currentRow.getHeaders();

					List<String> headerList = headers.get("Content-Disposition");
					if (headerList == null || headerList.isEmpty()) {
						break;
					}
					String headerValue = headerList.get(0);
					if (!headerValue.startsWith("attachment;")) {
						break;
					}

					headerList = headers.get("Content-ID");
					if (headerList == null || headerList.isEmpty()) {
						break;
					}
					headerValue = headerList.get(0);
					if (!headerValue.startsWith("<")) {
						break;
					}
					int pos = headerValue.indexOf(">",1);
					if (pos == -1) {
						break;
					}
					String colName = headerValue.substring(1, pos);

					row.put(colName, currentRow);

					hasMoreRows = results.hasNext();
				}

				rowRecord.init(row);

				if (hasMoreRows) {
					nextRow = currentRow;
				} else {
					closeImpl();
				}

				return currentHandle;
			} catch (JsonParseException e) {
				throw new MarkLogicIOException("could not part row record", e);
			} catch (JsonMappingException e) {
				throw new MarkLogicIOException("could not map row record", e);
			} catch (IOException e) {
				throw new MarkLogicIOException("could not read row record", e);
			}
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
	static class RowRecordImpl implements RowRecord {
		private static final Map<Class<?>,Constructor<?>> constructors = new HashMap<Class<?>,Constructor<?>>();

		private Map<String, Object> row = null;

// QUESTION:  threading guarantees - multiple handles? precedent?
		void init(Map<String, Object> row) {
			this.row = row;
		}

		// supported operations for unmodifiable map
		@Override
		public boolean containsKey(Object key) {
			return row.containsKey(key);
		}
		@Override
		public boolean containsValue(Object value) {
			return row.containsValue(value);
		}
		@Override
		public Set<Entry<String, Object>> entrySet() {
			return row.entrySet();
		}
		@Override
		public Object get(Object key) {
			return row.get(key);
		}
		@Override
		public boolean isEmpty() {
			return row.isEmpty();
		}
		@Override
		public Set<String> keySet() {
			return row.keySet();
		}
		@Override
		public Collection<Object> values() {
			return row.values();
		}
		@Override
		public int size() {
			return row.size();
		}

		// unsupported operations for unmodifiable map
		@Override
		public Object put(String key, Object value) {
			throw new UnsupportedOperationException("cannot modify row record");
		}
		@Override
		public Object remove(Object key) {
			throw new UnsupportedOperationException("cannot modify row record");
		}
		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
			throw new UnsupportedOperationException("cannot modify row record");
		}
		@Override
		public void clear() {
			throw new UnsupportedOperationException("cannot modify row record");
		}

// TODO: metadata accessors

		// literal casting convenience getters
		@Override
		public boolean getBoolean(String columnName) {
			return (Boolean) get(columnName);
		}
		@Override
		public byte getByte(String columnName) {
			return (Byte) get(columnName);
		}
		@Override
		public double getDouble(String columnName) {
			return (Double) get(columnName);
		}
		@Override
		public float getFloat(String columnName) {
			return (Float) get(columnName);
		}
		@Override
		public int getInt(String columnName) {
			return (Integer) get(columnName);
		}
		@Override
		public long getLong(String columnName) {
			return (Long) get(columnName);
		}
		@Override
		public short getShort(String columnName) {
			return (Short) get(columnName);
		}
		@Override
		public String getString(String columnName) {
			return get(columnName).toString();
		}
		
		private RESTServiceResult getServiceResult(String columnName) {
			return (RESTServiceResult) get(columnName);
		}

		@Override
		public <T extends AnyAtomicTypeVal> T getValueAs(String columnName, Class<T> as) throws Exception {
			@SuppressWarnings("unchecked")
			Constructor<T> constructor = (Constructor<T>) constructors.get(as);
			if (constructor == null) {
				constructor = as.getConstructor(String.class);
				constructors.put(as, constructor);
			}
			return constructor.newInstance(getString(columnName));
		}
		@Override
		public <T extends AnyAtomicTypeVal> T[] getValuesAs(String columnName, Class<T> as) throws Exception {
// TODO: constructor array for sequence
			return null;
		}

// TODO: ColumnDocument interface inherited by RESTServiceResult?

		@Override
		public Format getContentFormat(String columnName) {
			RESTServiceResult docResult = getServiceResult(columnName);
			if (docResult == null) {
				return null;
			}
			return docResult.getFormat();
		}
		@Override
		public String getContentMimetype(String columnName) {
			RESTServiceResult docResult = getServiceResult(columnName);
			if (docResult == null) {
				return null;
			}
			return docResult.getMimetype();
		}
		@Override
		public <T extends AbstractReadHandle> T getContent(String columnName, T contentHandle) {
			RESTServiceResult docResult = getServiceResult(columnName);
			if (docResult == null) {
				return null;
			}
			return docResult.getContent(contentHandle);
		}
		@Override
		public <T> T getContentAs(String columnName, Class<T> as) {
// TODO
			return null;
		}
	}
	static class RawPlanDefinitionImpl implements RawPlanDefinition {
		private JSONWriteHandle handle = null;
		RawPlanDefinitionImpl(JSONWriteHandle handle) {
			setHandle(handle);
		}

		@Override
		public Plan bindParam(PlanParam param, String literal) {
// TODO 
			return null;
		}

		@Override
		public JSONWriteHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(JSONWriteHandle handle) {
			if (handle == null) {
				throw new IllegalArgumentException("Must specify handle for reading raw plan");
			}
			this.handle = handle;
		}
		@Override
		public RawPlanDefinition withHandle(JSONWriteHandle handle) {
			setHandle(handle);
			return this;
		}
	}
}
