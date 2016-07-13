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
package com.marklogic.client.row;

import com.marklogic.client.Transaction;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.io.marker.RowReadHandle;

// TODO: JavaDoc
public interface RowManager {
	public PlanBuilder newPlanBuilder();

	// TODO: derive raw definition from plan

	public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle);
    public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle, Transaction transaction);

/* TODO: design
    public <T> RowSet<T> resultRows(Plan plan, Class<T> as);
    public <T> RowSet<T> resultRows(Plan plan, Class<T> as, Transaction transaction);
 */

	public <T extends RowReadHandle> T resultDoc(Plan plan, T handle);
	public <T extends RowReadHandle> T resultDoc(Plan plan, T handle, Transaction transaction);

	public <T> T resultDocAs(Plan plan, Class<T> as);
	public <T> T resultDocAs(Plan plan, Class<T> as, Transaction transaction);
}
