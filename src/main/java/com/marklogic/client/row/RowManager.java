package com.marklogic.client.row;

import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.io.marker.RowReadHandle;

public interface RowManager {
	public PlanBuilder newPlanBuilder();
// TODO: derive raw definition from plan
	public <T extends RowReadHandle> T resultDoc(Plan plan, T handle);
	public <T> T resultDocAs(Plan plan, Class<T> as);
	// TODO: row iteration as document
}
