package com.marklogic.client.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.marklogic.client.expression.Cts;
import com.marklogic.client.expression.Fn;
import com.marklogic.client.expression.Json;
import com.marklogic.client.expression.Math;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.Rdf;
import com.marklogic.client.expression.Sem;
import com.marklogic.client.expression.Sql;
import com.marklogic.client.expression.Xdmp;
import com.marklogic.client.expression.Xs;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.marker.JSONReadHandle;

abstract class PlanBuilderBase extends PlanBuilder {
	PlanBuilderBase(
        Cts cts, Fn fn, Json json, com.marklogic.client.expression.Map map,
        Math math, Rdf rdf, Sem sem, Sql sql, Xdmp xdmp, Xs xs
    ) {
        super(cts, fn, json, map, math, rdf, sem, sql, xdmp, xs);
    }

	@Override
    public PlanParam param(String name) {
    	return param(name);
    }

	static BaseTypeImpl.Literal literal(Object value) {
		return new BaseTypeImpl.Literal(value);
	}

    static class PlanParamBase implements PlanParam {
		String name = null;
		PlanParamBase(String name) {
			if (name == null) {
				throw new IllegalArgumentException("cannot define parameter with null name");
			}
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}

    static abstract class PlanBase
    extends PlanChainedImpl
    implements PlanBuilder.Plan, PlanBuilder.ExportablePlan, BaseTypeImpl.BaseArgImpl {
		Map<PlanParamBase,String> params = null;
		PlanBase(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
			super(prior, fnPrefix, fnName, fnArgs);
		}

		@Override
		public <T extends JSONReadHandle> T export(T handle) {
			if (!(handle instanceof BaseHandle)) {
				throw new IllegalArgumentException("cannot export with handle that doesn't extend base");
			}
			String planAst = getAst();
// TODO: move to utility?
			BaseHandle baseHandle = (BaseHandle) handle;
			Class as = baseHandle.receiveAs();
			if (InputStream.class.isAssignableFrom(as)) {
				baseHandle.receiveContent(new ByteArrayInputStream(planAst.getBytes()));
			} else if (Reader.class.isAssignableFrom(as)) {
				baseHandle.receiveContent(new StringReader(planAst));
			} else if (byte[].class.isAssignableFrom(as)) {
				baseHandle.receiveContent(planAst.getBytes());
			} else if (String.class.isAssignableFrom(as)) {
				baseHandle.receiveContent(planAst);
			} else {
				throw new IllegalArgumentException("cannot export with handle that doesn't accept content as byte[], input stream, reader, or string");
			}
			return handle;
		}
		@Override
	    public <T> T exportAs(Class<T> as) {
// TODO: look up in registry
	    	return null;
	    }

		public String getAst() {
	    	StringBuilder strb = new StringBuilder();
	    	strb.append("{$optic:");
	    	return exportAst(strb).append("}").toString();
	    }

	    Map<PlanParamBase,String> getParams() {
	    	if (params == null) {
	    		params = new HashMap<PlanParamBase,String>();
	    	}
	    	return params;
	    }

	    @Override
	    public Plan bindParam(PlanParam param, String literal) {
	    	if (!(param instanceof PlanParamBase)) {
	    		throw new IllegalArgumentException("cannot set parameter that doesn't extend base");
	    	}
	    	getParams().put((PlanParamBase) param, literal);
// TODO: return clone with param for immutability
	    	return this;
	    }
// TODO: implementation method for constructing request parameters
	}

    static class PlanBaseImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> {
        PlanBaseImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class PlanListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> {
        PlanListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class PlanChainedImpl extends BaseTypeImpl.BaseChainImpl<BaseTypeImpl.BaseArgImpl> {
        PlanChainedImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
}
