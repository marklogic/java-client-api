/*
 * Copyright 2016-2019 MarkLogic Corporation
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.marklogic.client.type.*;

public class BaseTypeImpl {
  public static interface BaseArgImpl {
    public StringBuilder exportAst(StringBuilder strb);
  }

  public static interface ParamBinder {
    public String getParamQualifier();
    public String getParamValue();
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

  static class BaseListImpl<T extends BaseArgImpl> implements BaseArgImpl {
    protected T[] args;
    protected BaseListImpl(T[] args) {
      this.args = args;
    }
    public T[] getArgsImpl() {
      return this.args;
    }
    @Override
    public StringBuilder exportAst(StringBuilder strb) {
      return exportASTList(strb, args);
    }
    @Override
    public String toString() {
      return listToString(args);
    }
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

  static class ServerExpressionListImpl extends BaseListImpl<BaseArgImpl> implements ServerExpression {
    ServerExpressionListImpl(Object[] items) {
      this(items, false);
    }
    ServerExpressionListImpl(Object[] items, boolean flatten) {
      super(flatten ? convertSequence(items) : convertList(items));
    }
  }
  static class ServerExpressionCallImpl extends BaseCallImpl<BaseArgImpl> implements ServerExpression {
    ServerExpressionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, convertList(fnArgs));
    }
  }

  static class ItemSeqListImpl extends ServerExpressionListImpl {
    ItemSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class ItemSeqCallImpl extends ServerExpressionCallImpl {
    ItemSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ItemCallImpl extends ServerExpressionCallImpl {
    ItemCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NodeSeqListImpl extends ItemSeqListImpl {
    NodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NodeSeqCallImpl extends ServerExpressionCallImpl {
    NodeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NodeCallImpl extends ItemCallImpl {
    NodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ArrayNodeSeqListImpl extends ItemSeqListImpl {
    ArrayNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class ArrayNodeCallImpl extends ItemCallImpl {
    ArrayNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class AttributeNodeSeqListImpl extends ItemSeqListImpl {
    AttributeNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class AttributeNodeCallImpl extends ItemCallImpl {
    AttributeNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class BooleanNodeSeqListImpl extends ItemSeqListImpl {
    BooleanNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class BooleanNodeCallImpl extends ItemCallImpl {
    BooleanNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class CommentNodeSeqListImpl extends ItemSeqListImpl {
    CommentNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class CommentNodeCallImpl extends ItemCallImpl {
    CommentNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DocumentNodeSeqListImpl extends ItemSeqListImpl {
    DocumentNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class DocumentNodeCallImpl extends ItemCallImpl {
    DocumentNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ElementNodeSeqListImpl extends ItemSeqListImpl {
    ElementNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class ElementNodeCallImpl extends ItemCallImpl {
    ElementNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NullNodeSeqListImpl extends ItemSeqListImpl {
    NullNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NullNodeCallImpl extends ItemCallImpl {
    NullNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NumberNodeSeqListImpl extends ItemSeqListImpl {
    NumberNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NumberNodeCallImpl extends ItemCallImpl {
    NumberNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ObjectNodeSeqListImpl extends ItemSeqListImpl {
    ObjectNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class ObjectNodeCallImpl extends ItemCallImpl {
    ObjectNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ProcessingInstructionNodeSeqListImpl extends ItemSeqListImpl {
    ProcessingInstructionNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class ProcessingInstructionNodeCallImpl extends ItemCallImpl {
    ProcessingInstructionNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class TextNodeSeqListImpl extends ItemSeqListImpl {
    TextNodeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class TextNodeCallImpl extends ItemCallImpl {
    TextNodeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class XmlContentNodeSeqListImpl extends ItemSeqListImpl {
    XmlContentNodeSeqListImpl(Object[] items) {
      super(items);
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
      return (value == null) ? null : value.toString();
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

  static BaseArgImpl[] convertSequence(Object[] items) {
    return convertSequence(items, BaseArgImpl.class);
  }
  @SuppressWarnings("unchecked")
  static <T extends BaseArgImpl> T[] convertSequence(Object[] items, Class<T> as) {
    if (items == null) {
      return null;
    } else if (items.length == 0) {
      return (T[]) (as.isAssignableFrom(items.getClass().getComponentType()) ?
            items : Array.newInstance(as, 0));
    }

    T[] optBuf = (T[]) Array.newInstance(as, items.length);
    List<T> pessBuf = null;
    for (int i=0; i < items.length; i++) {
      Object item = items[i];

      T castItem = null;
      if (item != null) {
        if (!BaseListImpl.class.isInstance(item)) {
          if (!as.isInstance(item)) {
            throw new IllegalArgumentException("requires "+as.getName()+" argument instead of "+item.getClass().getName());
          }
          castItem = (T) item;
        } else {
          BaseArgImpl[] itemList = ((BaseListImpl) item).getArgsImpl();
          switch(itemList.length) {
            case 0:
              break;
            case 1:
              BaseArgImpl firstListItem = itemList[0];
              if (!as.isInstance(firstListItem)) {
                throw new IllegalArgumentException("requires "+as.getName()+" first list item instead of "+firstListItem.getClass().getName());
              }
              castItem = (T) firstListItem;
              break;
            default:
              if (pessBuf == null) {
                pessBuf = new ArrayList<T>(items.length + itemList.length);
              }
              for (int j=0; j < itemList.length; j++) {
                BaseArgImpl listItem = itemList[j];
                if (!as.isInstance(listItem)) {
                  throw new IllegalArgumentException("requires "+as.getName()+" list item instead of "+listItem.getClass().getName());
                }
                pessBuf.add((T) listItem);
              }
              continue;
          }
        }
      }

      if (pessBuf == null) {
        optBuf[i] = castItem;
      } else {
        pessBuf.add(castItem);
      }
    }

    return (pessBuf == null) ? optBuf : pessBuf.toArray((T[]) Array.newInstance(as, pessBuf.size()));
  }
}
