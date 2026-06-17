/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.marklogic.client.type.*;

public class BaseTypeImpl {
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

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

    public BaseCallImpl<T>[] getChain() {
      return chain;
    }

    void setChain(BaseCallImpl<T>[] chain) {
      this.chain = chain;
    }

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
      super(fnPrefix, fnName, convertList(validateNoOpticParamInCtsCall(fnPrefix, fnName, fnArgs)));
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
  static class DocumentNodeSeqCallImpl extends ItemCallImpl {
    DocumentNodeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
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

  static private Object[] validateNoOpticParamInCtsCall(String fnPrefix, String fnName, Object[] fnArgs) {
    if (!"cts".equals(fnPrefix) || fnArgs == null) {
      return fnArgs;
    }
    if (containsPlanParam(fnArgs)) {
      throw new IllegalArgumentException(
        "Cannot pass op:param() to cts:" + fnName + "(). Use cts:param() for cts namespace expressions."
      );
    }
    return fnArgs;
  }

  static private boolean containsPlanParam(Object value) {
    if (value == null) {
      return false;
    }
    if (value instanceof PlanParamExpr) {
      return true;
    }
    if (value instanceof Object[]) {
      for (Object item : (Object[]) value) {
        if (containsPlanParam(item)) {
          return true;
        }
      }
      return false;
    }
    if (value instanceof BaseListImpl) {
      return containsPlanParam(((BaseListImpl<?>) value).getArgsImpl());
    }
    if (value instanceof BaseMapImpl) {
      return containsPlanParam(((BaseMapImpl) value).getMap().values().toArray());
    }
    if (value instanceof java.util.Map<?, ?>) {
      java.util.Map<?, ?> mapValue = (java.util.Map<?, ?>) value;
      return containsPlanParam(mapValue.keySet().toArray()) || containsPlanParam(mapValue.values().toArray());
    }
    return false;
  }

  static String getCtsParamName(CtsParamExpr param) {
    if (param == null) {
      throw new IllegalArgumentException("param for cts query binding cannot be null");
    }
    if (!(param instanceof BaseCallImpl)) {
      throw new IllegalArgumentException("param for cts query binding must be of type CtsParamExpr");
    }

    BaseCallImpl<?> paramCall = (BaseCallImpl<?>) param;
    if (!"cts".equals(paramCall.fnPrefix) || !"param".equals(paramCall.fnName)) {
      throw new IllegalArgumentException("param for cts query binding must be of type CtsParamExpr");
    }

    BaseArgImpl[] args = paramCall.getArgsImpl();
    if (args == null || args.length != 1 || !(args[0] instanceof XsStringVal)) {
      throw new IllegalArgumentException("CtsParamExpr must have exactly one XsStringVal argument");
    }

    String paramName = ((XsStringVal) args[0]).getString();
    if (paramName == null) {
      throw new IllegalArgumentException("CtsParamExpr name cannot be null");
    }

    return paramName;
  }

  static String bindCtsQueryParamsInAst(String planAst, Map<String, CtsQueryExpr> queryBindings) {
    if (planAst == null || queryBindings == null || queryBindings.isEmpty()) {
      return planAst;
    }

    try {
      JsonNode root = JSON_MAPPER.readTree(planAst);

      Map<String, JsonNode> bindingNodes = new HashMap<>();
      for (Map.Entry<String, CtsQueryExpr> binding : queryBindings.entrySet()) {
        String bindingName = binding.getKey();
        if (bindingName == null) {
          continue;
        }
        StringBuilder queryAst = new StringBuilder();
        ((BaseArgImpl) binding.getValue()).exportAst(queryAst);
        JsonNode queryNode = JSON_MAPPER.readTree(queryAst.toString());
        bindingNodes.put(bindingName, queryNode);
      }

      JsonNode replacedRoot = replaceCtsParamNodes(root, bindingNodes);
      return JSON_MAPPER.writeValueAsString(replacedRoot);
    } catch (JsonProcessingException ex) {
      throw new IllegalArgumentException("Unable to bind cts:param() query placeholder", ex);
    }
  }

  private static JsonNode replaceCtsParamNodes(JsonNode node, Map<String, JsonNode> bindingNodes) {
    if (node == null) {
      return null;
    }

    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;

      if (isQueryParamNode(objectNode)) {
        String paramName = getCtsParamNameFromAst(objectNode);
        JsonNode replacement = bindingNodes.get(paramName);
        if (replacement != null) {
          return replacement.deepCopy();
        }
      }

      objectNode.fieldNames().forEachRemaining(fieldName -> {
        JsonNode child = objectNode.get(fieldName);
        JsonNode replacedChild = replaceCtsParamNodes(child, bindingNodes);
        if (replacedChild != child) {
          objectNode.set(fieldName, replacedChild);
        }
      });
      return objectNode;
    }

    if (node.isArray()) {
      ArrayNode arrayNode = (ArrayNode) node;
      for (int i = 0; i < arrayNode.size(); i++) {
        JsonNode child = arrayNode.get(i);
        JsonNode replacedChild = replaceCtsParamNodes(child, bindingNodes);
        if (replacedChild != child) {
          arrayNode.set(i, replacedChild);
        }
      }
      return arrayNode;
    }

    return node;
  }

  /**
   * Returns true for both {@code cts:param} and {@code op:param} AST nodes, both of which
   * represent named query placeholders that can be bound to a {@code CtsQueryExpr} via
   * {@code bindParam}.
   */
  private static boolean isQueryParamNode(ObjectNode node) {
    String fn = node.path("fn").asText();
    if (!"param".equals(fn)) {
      return false;
    }
    String ns = node.path("ns").asText();
    return "cts".equals(ns) || "op".equals(ns);
  }

  private static String getCtsParamNameFromAst(ObjectNode node) {
    JsonNode nameNode = node.path("args").path(0).path("args").path(0);
    return nameNode.isTextual() ? nameNode.asText() : null;
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
			  // Prior to 7.1.0, this threw an exception, as it was requiring every item to be an instance of the given
			  // class. This meant that a primitive value could never be passed. But that forces the server to support
			  // both a primitive value and a "wrapped" value (e.g. with ns=xs, fn=float, args=value) for every
			  // argument. This instead assumes that it can just write the item as-is and the server will accept it.
			  return (BaseArgImpl) serializedPlanBuilder -> serializedPlanBuilder.append(item);
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
