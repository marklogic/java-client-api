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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ElementNodeSeqExpr;
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.NodeExpr;
import com.marklogic.client.type.NodeSeqExpr;

public class BaseTypeImpl {
	public static interface BaseArgImpl {
		public StringBuilder exportAst(StringBuilder strb);
    }

	static class BaseMapImpl implements BaseArgImpl {
		private Map<String, ?> arg;
		private Pattern quote = Pattern.compile("(\"|\\\\)");
		BaseMapImpl(Map<String, ?> arg) {
			this.arg = arg;
		}
		@Override
		public StringBuilder exportAst(StringBuilder strb) {
			strb.append("{");
			boolean isFirst = true;
			for (Map.Entry<String, ?> entry: arg.entrySet()) {
				if (isFirst) {
					isFirst = false;
				} else {
					strb.append(", ");
				}
				strb.append("\"");
				strb.append(quote.matcher(entry.getKey()).replaceAll("\\$1"));
				strb.append("\":");
				Object value = entry.getValue();
				if (value == null) {
					strb.append("null");
				} else if (value instanceof BaseArgImpl) {
					((BaseArgImpl) value).exportAst(strb);
				} else if (value instanceof Boolean) {
					strb.append(((Boolean) value).toString());
// TODO: cases unsupported in JSON as strings
				} else if (value instanceof Number) {
					strb.append(((Number) value).toString());
				} else {
					String valStr = (value instanceof String) ? (String) value : value.toString();
					strb.append("\"");
					strb.append(quote.matcher(valStr).replaceAll("\\$1"));
					strb.append("\"");
				}
			}
			strb.append("}");
			return strb;
		}
		Map<String, ?> getMap() {
			return arg;
		}
	}

	static BaseListImpl<BaseArgImpl> baseListImpl(Object[] items) {
		return new BaseListImpl<>(convertList(items));
	}
	static <T extends BaseArgImpl> BaseListImpl<T> baseListImpl(Object[] items, Class<T> as) {
		return new BaseListImpl<>(convertList(items, as));
	}
	static class BaseListImpl<T extends BaseArgImpl> implements BaseArgImpl {
		protected T[] items;
		protected BaseListImpl(T[] items) {
			this.items = items;
		}
		public T[] getItems() {
			return this.items;
		}
		@Override
		public StringBuilder exportAst(StringBuilder strb) {
			return exportASTList(strb, items);
		}
		@Override
		public String toString() {
			return listToString(items);
		}
    }

	static BaseCallImpl<BaseArgImpl> baseCallImpl(String fnPrefix, String fnName, Object[] items) {
		return new BaseCallImpl<>(fnPrefix, fnName, convertList(items));
	}
	static <T extends BaseArgImpl> BaseCallImpl<T> baseCallImpl(String fnPrefix, String fnName, Object[] items, Class<T> as) {
		return new BaseCallImpl<>(fnPrefix, fnName, convertList(items, as));
	}
	static class BaseCallImpl<T extends BaseArgImpl> extends BaseListImpl<T> {
		protected String fnPrefix = null;
		protected String fnName   = null;
		protected BaseCallImpl(String fnPrefix, String fnName, T[] fnArgs) {
			super(fnArgs);
			this.fnPrefix = fnPrefix;
			this.fnName   = fnName;
		}
		@Override
		public StringBuilder exportAst(StringBuilder strb) {
			strb.append("{\"ns\":\"").append(fnPrefix).append("\", \"fn\":\"").append(fnName).append("\", \"args\":");
			return super.exportAst(strb).append("}");
		}
		@Override
		public String toString() {
			return fnPrefix+":"+fnName+super.toString();
		}
	}

	static class BaseChainImpl<T extends BaseArgImpl> implements BaseArgImpl {
		private BaseCallImpl<T>[] chain = null;
		@SuppressWarnings("unchecked")
		protected BaseChainImpl(BaseChainImpl<T> prior, String fnPrefix, String fnName, T[] fnArgs) {
			BaseCallImpl<T> call = new BaseCallImpl<>(fnPrefix, fnName, fnArgs);
			if (prior == null) {
				chain = (BaseCallImpl<T>[]) Array.newInstance(BaseCallImpl.class, 1);
				chain[0] = call;
			} else {
				BaseCallImpl<T>[] priorChain = prior.chain;
				chain = Arrays.copyOf(priorChain, priorChain.length + 1);
				chain[priorChain.length] = call;
			}
		}
		@Override
		public StringBuilder exportAst(StringBuilder strb) {
			strb.append("{\"ns\":\"op\", \"fn\":\"operators\", \"args\":");
			return exportASTList(strb, chain).append("}");
		}
		@Override
		public String toString() {
			return Arrays.stream(chain)
			             .map(item -> item.toString())
			             .reduce((priorString,argString) -> priorString+"."+argString)
			             .get();
		}
	}

	static ItemSeqExpr items(ItemExpr... items) {
		return new ItemSeqListImpl(items);
	}
	static NodeSeqExpr nodes(NodeExpr... items) {
		return new NodeSeqListImpl(items);
	}
	static ElementNodeSeqExpr elements(ElementNodeExpr... items) {
		return new ElementNodeSeqListImpl(items);
	}

    static class ItemSeqListImpl extends BaseListImpl<BaseArgImpl> implements ItemSeqExpr {
    	ItemSeqListImpl(Object[] items) {
            super(convertList(items));
        }
    }
    static class ItemSeqCallImpl extends BaseCallImpl<BaseArgImpl> implements ItemSeqExpr {
    	ItemSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, convertList(fnArgs));
        }
    }
    static class ItemCallImpl extends BaseCallImpl<BaseArgImpl> implements ItemExpr {
    	ItemCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, convertList(fnArgs));
        }
    }
    static class NodeSeqListImpl extends BaseListImpl<BaseArgImpl> implements NodeSeqExpr {
    	NodeSeqListImpl(Object[] items) {
            super(convertList(items));
        }
    }
    static class NodeCallImpl extends BaseCallImpl<BaseArgImpl> implements NodeExpr {
    	NodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, convertList(fnArgs));
        }
    }
    static class ElementNodeSeqListImpl extends BaseListImpl<BaseArgImpl> implements ElementNodeSeqExpr {
    	ElementNodeSeqListImpl(Object[] items) {
            super(convertList(items));
        }
    }
    static class ElementNodeCallImpl extends BaseCallImpl<BaseArgImpl> implements ElementNodeExpr {
    	ElementNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, convertList(fnArgs));
        }
    }

    static class Literal implements BaseTypeImpl.BaseArgImpl {
    	private Object value = null;
    	Literal(Object value) {
    		this.value = value;
    	}
		@Override
		public StringBuilder exportAst(StringBuilder strb) {
			astifyObject(strb, value);
			return strb;
		}
		@Override
		public String toString() {
			if (value == null) {
				return null;
			}
			return value.toString();
		}
    }

    static private String listToString(BaseArgImpl[] items) {
    	if (items == null) {
    		return "()";
    	}
		return "("+stringifyList(items)+")";
    }
    static private StringBuilder exportASTList(StringBuilder strb, BaseArgImpl[] items) {
    	astifyArray(strb, items);
    	return strb;
    }
    static private String stringifyList(BaseArgImpl[] items) {
    	if (items == null) {
    		return null;
    	}
		return Arrays.stream(items)
		             .map(item -> (item == null) ? "null" : item.toString())
		             .reduce((priorString,argString) -> priorString+", "+argString)
		             .get();
    }
    static private void astifyArray(StringBuilder strb, Object[] items) {
	   	strb.append("[");
    	if (items != null && items.length > 0) {
    		boolean isFirst = true;
    	    for (Object item: items) {
    			if (isFirst) {
    				isFirst = false;
    			} else {
    				strb.append(", ");
    			}
    			astifyObject(strb, item);
    		}
    	}
		strb.append("]");
    }
    // TODO: collection for set, list
    static private void astifyMap(StringBuilder strb, java.util.Map<?,?> map) {
		strb.append("{");
    	if (map != null && map.size() > 0) {
    		boolean isFirst = true;
        	for (java.util.Map.Entry<?, ?> entry: map.entrySet()) {
    			if (isFirst) {
    				isFirst = false;
    			} else {
    				strb.append(", ");
    			}
    			strb.append("\"");
    			strb.append(entry.getKey().toString());
    			strb.append("\"");
    			strb.append(":");
    			astifyObject(strb, entry.getValue());
        	}
    	}
		strb.append("}");
    }
    static private void astifyObject(StringBuilder strb, Object value) {
		if (value == null) {
			strb.append("null");
		} else if (value instanceof BaseArgImpl) {
			((BaseArgImpl) value).exportAst(strb); 
		} else if (value instanceof Number || value instanceof Boolean) {
			strb.append(value.toString());
		} else if (value instanceof Object[]) {
			astifyArray(strb, (Object[]) value);
		} else if (value instanceof java.util.Map<?,?>) {
			astifyMap(strb, (java.util.Map<?,?>) value);
		} else {
			strb.append("\"");
			strb.append(value.toString());
			strb.append("\"");
		}
    }

	static BaseArgImpl[] convertList(Object[] items) {
		return convertList(items, BaseArgImpl.class);
	}
    @SuppressWarnings("unchecked")
	static <T extends BaseArgImpl> T[] convertList(Object[] items, Class<T> as) {
    	if (items == null) {
    		return null;
    	}
    	if (as.isAssignableFrom(items.getClass().getComponentType())) {
    		return (T[]) items;
    	}
	    return (items == null || items.length == 0) ? null :
	    	Arrays.stream(items)
	    	      .map(item -> {
	    	          if (item != null && !as.isInstance(item)) {
	    	            throw new IllegalArgumentException("expected "+as.getName()+" argument instead of "+item.getClass().getName());
	    	          }
	    	          return (T) item;
	    	          })
	    	      .toArray(size -> (T[]) Array.newInstance(as, size));
	}
}
