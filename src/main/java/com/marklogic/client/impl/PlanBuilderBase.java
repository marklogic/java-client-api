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
import java.util.Arrays;
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
import com.marklogic.client.type.XsAnySimpleTypeSeqExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsStringExpr;

import com.marklogic.client.type.ArrayNodeExpr;
import com.marklogic.client.type.AttributeNodeExpr;
import com.marklogic.client.type.AttributeNodeSeqExpr;
import com.marklogic.client.type.BooleanNodeExpr;
import com.marklogic.client.type.CommentNodeExpr;
import com.marklogic.client.type.DocumentNodeExpr;
import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.NullNodeExpr;
import com.marklogic.client.type.NumberNodeExpr;
import com.marklogic.client.type.ObjectNodeExpr;
import com.marklogic.client.type.PINodeExpr;
import com.marklogic.client.type.TextNodeExpr;

import com.marklogic.client.type.JsonPropertyExpr;

import com.marklogic.client.type.JsonRootNodeExpr;
import com.marklogic.client.type.JsonContentNodeExpr;
import com.marklogic.client.type.XmlRootNodeExpr;
import com.marklogic.client.type.XmlContentNodeExpr;

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

	@Override
    public DocumentNodeExpr jsonDocument(JsonRootNodeExpr root) {
		return new JsonDocumentCallImpl(new Object[]{ root });
    }
	@Override
    public ObjectNodeExpr jsonObject(JsonPropertyExpr... properties) {
		return new JsonObjectCallImpl(new Object[]{ new JsonPropertySeqListImpl(properties) });
    }
	@Override
    public ArrayNodeExpr jsonArray(JsonContentNodeExpr... items) {
		return new JsonArrayCallImpl(new Object[]{ new JsonContentSeqListImpl(items) });
    }
	@Override
    public JsonPropertyExpr prop(String key, JsonContentNodeExpr value) {
		return prop(new XsValueImpl.StringValImpl(key), value);
    }
	@Override
    public JsonPropertyExpr prop(XsStringExpr key, JsonContentNodeExpr value) {
		return new JsonPropertyCallImpl(new Object[]{ key, value });
    }
	@Override
    public TextNodeExpr jsonString(String value) {
		return jsonString(new XsValueImpl.StringValImpl(value));
	}
	@Override
    public TextNodeExpr jsonString(XsAnySimpleTypeSeqExpr value) {
		return new JsonStringCallImpl(new Object[]{ value });
    }
	@Override
    public NumberNodeExpr jsonNumber(double value) {
		return jsonNumber(new XsValueImpl.DoubleValImpl(value));
	}
	@Override
    public NumberNodeExpr jsonNumber(long value) {
		return jsonNumber(new XsValueImpl.LongValImpl(value));
	}
	@Override
    public NumberNodeExpr jsonNumber(XsNumericExpr value) {
		return new JsonNumberCallImpl(new Object[]{ value });
    }
	@Override
    public BooleanNodeExpr jsonBoolean(boolean value) {
		return jsonBoolean(new XsValueImpl.BooleanValImpl(value));
	}
	@Override
    public BooleanNodeExpr jsonBoolean(XsBooleanExpr value) {
		return new JsonBooleanCallImpl(new Object[]{ value });
    }
	@Override
    public NullNodeExpr jsonNull() {
		return new JsonNullCallImpl();
    }

	@Override
    public DocumentNodeExpr xmlDocument(XmlRootNodeExpr root) {
		return new XmlDocumentCallImpl(new Object[]{ root });
    }
	@Override
    public ElementNodeExpr xmlElement(String name, AttributeNodeExpr... attributes) {
		return xmlElement(new XsValueImpl.QNameValImpl(name), attributes);
    }
	@Override
    public ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeExpr... attributes) {
		return new XmlElementCallImpl(new Object[]{ name, new XmlAttributeSeqListImpl(attributes) });
    }
	@Override
    public ElementNodeExpr xmlElement(String name, XmlContentNodeExpr... content) {
		return xmlElement(new XsValueImpl.QNameValImpl(name), content);
    }
	@Override
    public ElementNodeExpr xmlElement(XsQNameExpr name, XmlContentNodeExpr... content) {
		return new XmlElementCallImpl(new Object[]{ name, new XmlContentSeqListImpl(content) });
    }
	@Override
    public ElementNodeExpr xmlElement(String name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content) {
		return xmlElement(new XsValueImpl.QNameValImpl(name), attributes, content);
    }
	@Override
    public ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content) {
		return new XmlElementCallImpl(new Object[]{ name, attributes, new XmlContentSeqListImpl(content) });
    }
	@Override
    public AttributeNodeExpr xmlAttribute(String name, XsAnySimpleTypeSeqExpr value) {
		return xmlAttribute(new XsValueImpl.QNameValImpl(name), value);
    }
	@Override
    public AttributeNodeExpr xmlAttribute(XsQNameExpr name, XsAnySimpleTypeSeqExpr value) {
		return new XmlAttributeCallImpl(new Object[]{ name, value });
    }
	@Override
    public TextNodeExpr xmlText(String value) {
		return xmlText(new XsValueImpl.StringValImpl(value));
	}
	@Override
    public TextNodeExpr xmlText(XsAnySimpleTypeSeqExpr value) {
		return new XmlTextCallImpl(new Object[]{ value });
    }
	@Override
    public CommentNodeExpr xmlComment(String content) {
		return xmlComment(new XsValueImpl.StringValImpl(content));
	}
	@Override
    public CommentNodeExpr xmlComment(XsAnySimpleTypeSeqExpr content) {
		return new XmlCommentCallImpl(new Object[]{ content });
    }
	@Override
    public PINodeExpr xmlPI(String name, String value) {
		return xmlPI(new XsValueImpl.StringValImpl(name), new XsValueImpl.StringValImpl(value));
    }
	@Override
    public PINodeExpr xmlPI(XsStringExpr name, XsAnySimpleTypeSeqExpr value) {
		return new XmlPICallImpl(new Object[]{ name, value });
    }

	@Override
	public AttributeNodeSeqExpr xmlAttributes(AttributeNodeExpr... attributes) {
        return new XmlAttributeSeqListImpl(attributes);
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

    static interface JsonContentCallImpl extends JsonContentNodeExpr, BaseTypeImpl.BaseArgImpl {}
    static class JsonContentSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> {
    	JsonContentSeqListImpl(JsonContentNodeExpr[] items) {
            super(Arrays.copyOf(items, items.length, JsonContentCallImpl[].class));
        }
    }

    static class JsonPropertySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> {
    	JsonPropertySeqListImpl(JsonPropertyExpr[] items) {
            super(Arrays.copyOf(items, items.length, JsonPropertyCallImpl[].class));
        }
    }

    static class JsonDocumentCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DocumentNodeExpr {
    	JsonDocumentCallImpl(Object[] args) {
            super("op", "json-document", BaseTypeImpl.convertList(args));
        }
    }
    static class JsonObjectCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ObjectNodeExpr, JsonContentCallImpl {
    	JsonObjectCallImpl(Object[] args) {
            super("op", "json-object", BaseTypeImpl.convertList(args));
        }
    }
    static class JsonPropertyCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements JsonPropertyExpr {
    	JsonPropertyCallImpl(Object[] args) {
            super("op", "prop", BaseTypeImpl.convertList(args));
        }
    }
    static class JsonArrayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ArrayNodeExpr, JsonContentCallImpl {
    	JsonArrayCallImpl(Object[] args) {
            super("op", "json-array", BaseTypeImpl.convertList(args));
        }
    }
    static class JsonStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements TextNodeExpr, JsonContentCallImpl {
    	JsonStringCallImpl(Object[] args) {
            super("op", "json-string", BaseTypeImpl.convertList(args));
        }
    }
    static class JsonNumberCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NumberNodeExpr, JsonContentCallImpl {
    	JsonNumberCallImpl(Object[] args) {
            super("op", "json-number", BaseTypeImpl.convertList(args));
        }
    }
    static class JsonBooleanCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements BooleanNodeExpr, JsonContentCallImpl {
    	JsonBooleanCallImpl(Object[] args) {
            super("op", "json-boolean", BaseTypeImpl.convertList(args));
        }
    }
    static class JsonNullCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NullNodeExpr, JsonContentCallImpl {
    	JsonNullCallImpl() {
            super("op", "json-number", new BaseTypeImpl.BaseArgImpl[]{});
        }
    }

    static class XmlAttributeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements AttributeNodeSeqExpr {
    	XmlAttributeSeqListImpl(AttributeNodeExpr[] items) {
            super(Arrays.copyOf(items, items.length, XmlAttributeCallImpl[].class));
        }
    }

    static interface XmlContentCallImpl  extends XmlContentNodeExpr,  BaseTypeImpl.BaseArgImpl {}
    static class XmlContentSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> {
    	XmlContentSeqListImpl(XmlContentNodeExpr[] items) {
            super(Arrays.copyOf(items, items.length, XmlContentCallImpl[].class));
        }
    }

    static class XmlDocumentCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DocumentNodeExpr {
    	XmlDocumentCallImpl(Object[] args) {
            super("op", "xml-document", BaseTypeImpl.convertList(args));
        }
    }
    static class XmlElementCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ElementNodeExpr, XmlContentCallImpl {
    	XmlElementCallImpl(Object[] args) {
            super("op", "xml-element", BaseTypeImpl.convertList(args));
        }
    }
    static class XmlAttributeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements AttributeNodeExpr {
    	XmlAttributeCallImpl(Object[] args) {
            super("op", "xml-attribute", BaseTypeImpl.convertList(args));
        }
    }
    static class XmlTextCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements TextNodeExpr, XmlContentCallImpl {
    	XmlTextCallImpl(Object[] args) {
            super("op", "xml-text", BaseTypeImpl.convertList(args));
        }
    }
    static class XmlCommentCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CommentNodeExpr, XmlContentCallImpl {
    	XmlCommentCallImpl(Object[] args) {
            super("op", "xml-comment", BaseTypeImpl.convertList(args));
        }
    }
    static class XmlPICallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PINodeExpr, XmlContentCallImpl {
    	XmlPICallImpl(Object[] args) {
            super("op", "xml-pi", BaseTypeImpl.convertList(args));
        }
    }
}
