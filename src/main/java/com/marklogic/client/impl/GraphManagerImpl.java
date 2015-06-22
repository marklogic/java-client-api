/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.QuadsWriteHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.io.marker.TriplesWriteHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.TriplesParsingHandle;
import com.marklogic.client.semantics.TriplesSerializingHandle;

public class GraphManagerImpl<R extends TriplesReadHandle, W extends TriplesWriteHandle>
    implements GraphManager
{

	@Override
	public TriplesSerializingHandle newTriplesSerializingHandle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TriplesParsingHandle newTriplesParsingHandle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator listGraphUris() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TriplesReadHandle read(String uri, TriplesReadHandle handle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TriplesReadHandle read(String uri, TriplesReadHandle handle,
			Transaction transaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TriplesReadHandle readAs(String uri, Class clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TriplesReadHandle readAs(String uri, Class clazz,
			Transaction transaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphPermissions getPermissions(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphPermissions getPermissions(String uri, Transaction transaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePermissions(String uri, GraphPermissions permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePermissions(String uri, GraphPermissions permissions,
			Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writePermissions(String uri, GraphPermissions permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writePermissions(String uri, GraphPermissions permissions,
			Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mergePermissions(String uri, GraphPermissions permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mergePermissions(String uri, GraphPermissions permissions,
			Transaction transcation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void merge(String uri, TriplesWriteHandle handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void merge(String uri, TriplesWriteHandle handle,
			Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void merge(String uri, TriplesWriteHandle handle,
			GraphPermissions permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void merge(String uri, TriplesWriteHandle handle,
			GraphPermissions permissions, Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mergeAs(String uri, Object graphData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mergeAs(String uri, Object graphData, Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mergeAs(String uri, Object graphData,
			GraphPermissions permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mergeAs(String uri, Object graphData,
			GraphPermissions permissions, Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String uri, TriplesWriteHandle handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String uri, TriplesWriteHandle handle,
			Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String uri, TriplesWriteHandle handle,
			GraphPermissions permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String uri, TriplesWriteHandle handle,
			GraphPermissions permissions, Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeAs(String uri, Object graphData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeAs(String uri, Object graphData, Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeAs(String uri, Object graphData,
			GraphPermissions permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeAs(String uri, Object graphData,
			GraphPermissions permissions, Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String uri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String uri, Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TriplesReadHandle things(String[] iris, TriplesReadHandle handle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object thingsAs(String[] iris, Class clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mergeGraphs(QuadsWriteHandle handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mergeGraphsAs(Object quadsData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replaceGraphs(QuadsWriteHandle handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replaceGraphsAs(Object quadsData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteGraphs() {
		// TODO Auto-generated method stub
		
	}


}


