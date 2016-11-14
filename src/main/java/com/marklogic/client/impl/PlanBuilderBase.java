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
import com.marklogic.client.expression.CtsExpr;
import com.marklogic.client.expression.FnExpr;
import com.marklogic.client.expression.JsonExpr;
import com.marklogic.client.expression.MapExpr;
import com.marklogic.client.expression.MathExpr;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.RdfExpr;
import com.marklogic.client.expression.SemExpr;
import com.marklogic.client.expression.SpellExpr;
import com.marklogic.client.expression.SqlExpr;
import com.marklogic.client.expression.XdmpExpr;
import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.type.ArrayNodeExpr;
import com.marklogic.client.type.AttributeNodeExpr;
import com.marklogic.client.type.AttributeNodeSeqExpr;
import com.marklogic.client.type.BooleanNodeExpr;
import com.marklogic.client.type.CommentNodeExpr;
import com.marklogic.client.type.DocumentNodeExpr;
import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.JsonContentNodeExpr;
import com.marklogic.client.type.JsonPropertyExpr;
import com.marklogic.client.type.JsonRootNodeExpr;
import com.marklogic.client.type.NullNodeExpr;
import com.marklogic.client.type.NumberNodeExpr;
import com.marklogic.client.type.ObjectNodeExpr;
import com.marklogic.client.type.PINodeExpr;
import com.marklogic.client.type.PlanAggregateOptions;
import com.marklogic.client.type.PlanExprCol;
import com.marklogic.client.type.PlanGroupConcatOptions;
import com.marklogic.client.type.PlanParam;
import com.marklogic.client.type.PlanTripleOptions;
import com.marklogic.client.type.SemIriVal;
import com.marklogic.client.type.TextNodeExpr;
import com.marklogic.client.type.XmlContentNodeExpr;
import com.marklogic.client.type.XmlRootNodeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsAnySimpleTypeSeqExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsStringExpr;

abstract class PlanBuilderBase extends PlanBuilder {
	private HandleFactoryRegistry handleRegistry;

	PlanBuilderBase(
        CtsExpr cts, FnExpr fn, JsonExpr json, MapExpr map,
        MathExpr math, RdfExpr rdf, SemExpr sem, SpellExpr spell, SqlExpr sql, XdmpExpr xdmp, XsExpr xs
    ) {
        super(cts, fn, json, map, math, rdf, sem, spell, sql, xdmp, xs);
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
	public ItemExpr caseExpr(CaseExpr... cases) {
		int lastPos = cases.length - 1;
		if (lastPos < 1) {
			throw new IllegalArgumentException("cannot specify caseExpr() without when() and elseExpr()");
		}

		BaseTypeImpl.BaseArgImpl[] whenList = new BaseTypeImpl.BaseArgImpl[lastPos];
		for (int i=0; i < lastPos; i++) {
			CaseExpr currCase = cases[i];
			if (!(currCase instanceof CaseWhenCallImpl)) {
				throw new IllegalArgumentException(
						"caseExpr() can only have when() cases and final elseExpr(): "+currCase.getClass().getName()
						);
			}
			whenList[i] = (CaseWhenCallImpl) currCase;
		}

		CaseExpr lastCase = cases[lastPos];
		if (!(lastCase instanceof CaseElseImpl)) {
			throw new IllegalArgumentException(
					"caseExpr() must have a last case of elseExpr(): "+lastCase.getClass().getName()
					);
		}
		
		return new CaseCallImpl(whenList, ((CaseElseImpl) lastCase).getArg());
	}
	@Override
    public CaseExpr when(XsBooleanExpr condition, ItemSeqExpr value) {
    	return new CaseWhenCallImpl(new Object[]{condition, value});
    }
	@Override
    public CaseExpr elseExpr(ItemSeqExpr value) {
		if (!(value instanceof BaseTypeImpl.BaseArgImpl)) {
			throw new IllegalArgumentException("invalid value for elseExpr(): "+value.getClass().getName());
		}
    	return new CaseElseImpl((BaseTypeImpl.BaseArgImpl) value);
    }

	@Override
    public ItemSeqExpr xpath(String inCol, String path) {
	       return xpath(col(inCol), xs.string(path)); 
    }
	@Override
    public ItemSeqExpr xpath(PlanExprCol inCol, XsStringExpr path) {
        return new BaseTypeImpl.ItemSeqCallImpl("op", "xpath", new Object[]{ inCol, path });
    }

	@Override
    public PlanAggregateOptions aggregateOptions(PlanValues values) {
		return new PlanAggregateOptionsImpl(makeMap(values));
    }
	@Override
    public PlanGroupConcatOptions groupConcatOptions(String separator) {
		return new PlanGroupConcatOptionsImpl(makeMap("separator", separator));
    }
	@Override
    public PlanGroupConcatOptions groupConcatOptions(PlanValues values) {
		return new PlanGroupConcatOptionsImpl(makeMap(values));
    }
	@Override
    public PlanGroupConcatOptions groupConcatOptions(String separator, PlanValues values) {
		Map<String,String> map = makeMap(values);
		map.put("separator", separator);
		return new PlanGroupConcatOptionsImpl(map);
    }

	@Override
	public PlanTripleOptions tripleOptions(PlanTriples values) {
		return new PlanTripleOptionsImpl(makeMap(values));
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
		private SemExpr sem;
		private String prefix;
		private PrefixerImpl(SemExpr sem, String prefix) {
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
    	public Map<PlanParamBase,BaseTypeImpl.ParamBinder> getParams();
    	public AbstractWriteHandle getHandle();
    }

    static abstract class PlanBase
    extends PlanChainedImpl
    implements PlanBuilder.Plan, PlanBuilder.ExportablePlan, RequestPlan, BaseTypeImpl.BaseArgImpl {
		private Map<PlanParamBase,BaseTypeImpl.ParamBinder> params = null;

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
		public Map<PlanParamBase,BaseTypeImpl.ParamBinder> getParams() {
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
	    	if (params == null) {
	    		params = new HashMap<>();
	    	}
	    	if (literal instanceof XsValueImpl.AnyAtomicTypeValImpl) {
		    	params.put((PlanParamBase) param, (XsValueImpl.AnyAtomicTypeValImpl)  literal);
	    	} else if (literal instanceof RdfValueImpl.RdfLangStringValImpl) {
		    	params.put((PlanParamBase) param, (RdfValueImpl.RdfLangStringValImpl) literal);
	    	} else if (literal instanceof SemValueImpl.SemIriValImpl) {
		    	params.put((PlanParamBase) param, (SemValueImpl.SemIriValImpl)        literal);
	    	} else {
	    		throw new IllegalArgumentException("cannot set value with unknown implementation");
	    	}
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

	static class CaseCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ItemExpr {
		CaseCallImpl(BaseTypeImpl.BaseArgImpl[] whenList, BaseTypeImpl.BaseArgImpl otherwise) {
            super("op", "case", new BaseTypeImpl.BaseArgImpl[]{
            		new BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl>(whenList), otherwise
            		});
        }
    }
	static class CaseWhenCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CaseExpr {
		CaseWhenCallImpl(Object[] args) {
            super("op", "when", BaseTypeImpl.convertList(args));
        }
    }
	static class CaseElseImpl implements CaseExpr {
		private BaseTypeImpl.BaseArgImpl arg = null;
		CaseElseImpl(BaseTypeImpl.BaseArgImpl arg) {
            this.arg = arg;
        }
		BaseTypeImpl.BaseArgImpl getArg() {
			return arg;
		}
    }

	static class PlanAggregateOptionsImpl extends BaseTypeImpl.BaseMapImpl implements PlanAggregateOptions {
		PlanAggregateOptionsImpl(Map<String,?> map) {
			super(map);
		}
	}
	static class PlanGroupConcatOptionsImpl extends BaseTypeImpl.BaseMapImpl implements PlanGroupConcatOptions {
		PlanGroupConcatOptionsImpl(Map<String,?> map) {
			super(map);
		}
	}
	static class PlanTripleOptionsImpl extends BaseTypeImpl.BaseMapImpl implements PlanTripleOptions {
		PlanTripleOptionsImpl(Map<String,?> map) {
			super(map);
		}
	}

	static Map<String,String> makeMap(PlanValues values) {
		return (values == PlanValues.DISTINCT) ? makeMap("values", "distinct") : new HashMap<String, String>();
	}
	static Map<String,String> makeMap(PlanTriples values) {
		return makeMap("dedup", (values == PlanTriples.DEDUPLICATED) ? "on" : "off");
	}
	static Map<String,String> makeMap(String key, String value) {
		Map<String, String> map = new HashMap<String, String>();
		if (key != null) {
			map.put(key, value);
		}
		return map;
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
