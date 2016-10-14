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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.expression.Cts;
import com.marklogic.client.expression.Fn;
import com.marklogic.client.expression.Json;
import com.marklogic.client.expression.Math;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.Rdf;
import com.marklogic.client.expression.Sem;
import com.marklogic.client.expression.Spell;
import com.marklogic.client.expression.Sql;
import com.marklogic.client.expression.Xdmp;
import com.marklogic.client.expression.Xs;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.type.PlanParam;
import com.marklogic.client.type.SemIriVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;

abstract class PlanBuilderBase extends PlanBuilder {
	private HandleFactoryRegistry handleRegistry;

	PlanBuilderBase(
        Cts cts, Fn fn, Json json,
        Math math, Rdf rdf, Sem sem, Spell spell, Sql sql, Xdmp xdmp, Xs xs
    ) {
        super(cts, fn, json, math, rdf, sem, spell, sql, xdmp, xs);
    }

	HandleFactoryRegistry getHandleRegistry() {
		return handleRegistry;
	}
	void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
		this.handleRegistry = handleRegistry;
	}

	@Override
	public Prefixer prefixer(String base) {
		if (base == null || base.length() == 0) {
			throw new IllegalArgumentException("cannot create prefixer with empty string");
		}

		String lastChar = base.substring(base.length() - 1);
		String prefix = (lastChar.equals("/") || lastChar.equals("#") || lastChar.equals("?")) ?
				base : base + "/";

		return new PrefixerImpl(sem, prefix);
	}

	static public class PrefixerImpl implements Prefixer {
		private Sem    sem;
		private String prefix;
		private PrefixerImpl(Sem sem, String prefix) {
			this.prefix = prefix;
			this.sem    = sem;
		}

		public SemIriVal iri(String suffix) {
			if (suffix == null || suffix.length() == 0) {
				throw new IllegalArgumentException("cannot create SemIriVal with empty string");
			}

			String firstChar = suffix.substring(0, 1);
			if (firstChar.equals("/") || firstChar.equals("#") || firstChar.equals("?")) {
				if (suffix.length() == 1) {
					throw new IllegalArgumentException("cannot create SemIriVal from: "+suffix);
				}
				suffix = suffix.substring(1);
			}

			return sem.iri(prefix+suffix);
		}
	}

	@Override
    public PlanParam param(String name) {
    	return new PlanParamBase(name);
    }

	static BaseTypeImpl.Literal literal(Object value) {
		return new BaseTypeImpl.Literal(value);
	}

    static class PlanParamBase extends BaseTypeImpl.BaseCallImpl<XsValueImpl.StringValImpl> implements PlanParam {
		String name = null;
		PlanParamBase(String name) {
			super("op", "param", new XsValueImpl.StringValImpl[]{new XsValueImpl.StringValImpl(name)});
			if (name == null) {
				throw new IllegalArgumentException("cannot define parameter with null name");
			}
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}

    static interface RequestPlan {
    	public Map<PlanParamBase,XsValueImpl.AnyAtomicTypeValImpl> getParams();
    	public AbstractWriteHandle getHandle();
    }

    static abstract class PlanBase
    extends PlanChainedImpl
    implements PlanBuilder.Plan, PlanBuilder.ExportablePlan, RequestPlan, BaseTypeImpl.BaseArgImpl {
		private Map<PlanParamBase,XsValueImpl.AnyAtomicTypeValImpl> params = null;

		PlanBase(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
			super(prior, fnPrefix, fnName, fnArgs);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends JSONReadHandle> T export(T handle) {
			if (!(handle instanceof BaseHandle)) {
				throw new IllegalArgumentException("cannot export with handle that doesn't extend base");
			}
			String planAst = getAst();
// TODO: move to a method of BaseHandle?
			@SuppressWarnings("rawtypes")
			BaseHandle baseHandle = (BaseHandle) handle;
			@SuppressWarnings("rawtypes")
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
			if (as == null) {
				throw new IllegalArgumentException("Must specify a class to export content with a registered handle");
			}

			ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
			if (handle == null) {
				throw new IllegalArgumentException("No handle registered for class: "+as.getName());
			}
			if (!(handle instanceof JSONReadHandle)) {
				throw new IllegalArgumentException("Cannot parse JSON with handle registered for class: "+as.getName());
			}

			export((JSONReadHandle) handle);

			return handle.get();
	    }

		String getAst() {
	    	StringBuilder strb = new StringBuilder();
	    	strb.append("{\"$optic\":");
	    	return exportAst(strb).append("}").toString();
	    }

		@Override
		public Map<PlanParamBase,XsValueImpl.AnyAtomicTypeValImpl> getParams() {
	    	return params;
	    }

		@Override
		public AbstractWriteHandle getHandle() {
// TODO: maybe serialize plan to JSON using JSON writer?
			return new StringHandle(getAst()).withFormat(Format.JSON);
		}

	    @Override
	    public Plan bindParam(PlanParam param, boolean literal) {
	    	return bindParam(param, new XsValueImpl.BooleanValImpl(literal));
	    }
	    @Override
	    public Plan bindParam(PlanParam param, byte literal) {
	    	return bindParam(param, new XsValueImpl.ByteValImpl(literal));
	    }
	    @Override
	    public Plan bindParam(PlanParam param, double literal) {
	    	return bindParam(param, new XsValueImpl.DoubleValImpl(literal));
	    }
	    @Override
	    public Plan bindParam(PlanParam param, float literal) {
	    	return bindParam(param, new XsValueImpl.FloatValImpl(literal));
	    }
	    @Override
	    public Plan bindParam(PlanParam param, int literal) {
	    	return bindParam(param, new XsValueImpl.IntValImpl(literal));
	    }
	    @Override
	    public Plan bindParam(PlanParam param, long literal) {
	    	return bindParam(param, new XsValueImpl.LongValImpl(literal));
	    }
	    @Override
	    public Plan bindParam(PlanParam param, short literal) {
	    	return bindParam(param, new XsValueImpl.ShortValImpl(literal));
	    }
	    @Override
	    public Plan bindParam(PlanParam param, String literal) {
	    	return bindParam(param, new XsValueImpl.StringValImpl(literal));
	    }
	    @Override
	    public Plan bindParam(PlanParam param, XsAnyAtomicTypeVal literal) {
	    	if (!(param instanceof PlanParamBase)) {
	    		throw new IllegalArgumentException("cannot set parameter that doesn't extend base");
	    	}
	    	if (!(literal instanceof XsValueImpl.AnyAtomicTypeValImpl)) {
	    		throw new IllegalArgumentException("cannot set value with unknown implementation");
	    	}
	    	if (params == null) {
	    		params = new HashMap<>();
	    	}
	    	params.put((PlanParamBase) param, (XsValueImpl.AnyAtomicTypeValImpl)literal);
// TODO: return clone with param for immutability
	    	return this;
	    }
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
		private HandleFactoryRegistry handleRegistry;

        PlanChainedImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            if (prior != null) {
            	setHandleRegistry(prior.getHandleRegistry());
            }
        }

        HandleFactoryRegistry getHandleRegistry() {
			return handleRegistry;
		}
		void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
			this.handleRegistry = handleRegistry;
		}
    }
}
