package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.expression.Xs;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.RowReadHandle;
import com.marklogic.client.row.RowManager;
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

	    if (resultDoc(plan, (RowReadHandle) handle) == null) {
	    	return null;
	    }

		return handle.get();
	}
	@Override
	public <T extends RowReadHandle> T resultDoc(Plan plan, T resultsHandle) {
		if (!(plan instanceof PlanBuilderBase.PlanBase)) {
			if (plan == null) {
				throw new IllegalArgumentException("Must specify a plan to produce the row results");
			} else {
				throw new IllegalArgumentException("Invalid plan with class "+plan.getClass().getName());
			}
		}
		if (resultsHandle == null) {
			throw new IllegalArgumentException("Must specify a handle to read the row result document");
		}

		PlanBuilderBase.PlanBase exportablePlan = (PlanBuilderBase.PlanBase) plan;

		String ast = exportablePlan.getAst();

		// TODO: BINDINGS
		RequestParameters params = new RequestParameters();

		AbstractWriteHandle astHandle = new StringHandle(ast);

		// TODO: maybe serialize plan to JSON using JSON writer? or at least StringBuilder
		return services.postResource(requestLogger, "rows", null, params, astHandle, resultsHandle);
	}
}
