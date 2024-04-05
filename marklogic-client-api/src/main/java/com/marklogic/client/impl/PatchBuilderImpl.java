package com.marklogic.client.impl;

import com.marklogic.client.type.PatchBuilder;
import com.marklogic.client.type.ServerExpression;
import com.marklogic.client.type.XsStringVal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class PatchBuilderImpl extends BaseTypeImpl.BaseCallImpl implements PatchBuilder, BaseTypeImpl.BaseArgImpl {

	PatchBuilderImpl(XsStringVal contextPath) {
		super("op", "suboperators",
			new BaseTypeImpl.BaseArgImpl[]{
				new BaseTypeImpl.BaseCallImpl("op", "patchBuilder",
					new BaseTypeImpl.BaseArgImpl[]{(BaseTypeImpl.BaseArgImpl) contextPath})
			});
	}

	PatchBuilderImpl(XsStringVal contextPath, Map<String, String> namespaces) {
		super("op", "suboperators",
			new BaseTypeImpl.BaseArgImpl[]{
				new BaseTypeImpl.BaseCallImpl("op", "patchBuilder",
					new BaseTypeImpl.BaseArgImpl[]{(BaseTypeImpl.BaseArgImpl) contextPath, new BaseTypeImpl.BaseMapImpl(namespaces)})
			});
	}

	private PatchBuilderImpl(List<BaseTypeImpl.BaseArgImpl> args) {
		super("op", "suboperators", args.toArray(new BaseTypeImpl.BaseArgImpl[]{}));
	}

	@Override
	public PatchBuilder insertAfter(String path, ServerExpression node) {
		return addArg("insertAfter", path, node);
	}

	@Override
	public PatchBuilder insertAfter(XsStringVal path, ServerExpression node) {
		return addArg("insertAfter", path, node);
	}

	@Override
	public PatchBuilder insertBefore(String path, ServerExpression node) {
		return addArg("insertBefore", path, node);
	}

	@Override
	public PatchBuilder insertBefore(XsStringVal path, ServerExpression node) {
		return addArg("insertBefore", path, node);
	}

	@Override
	public PatchBuilder insertChild(String path, ServerExpression node) {
		return addArg("insertChild", path, node);
	}

	@Override
	public PatchBuilder insertChild(XsStringVal path, ServerExpression node) {
		return addArg("insertChild", path, node);
	}

	@Override
	public PatchBuilder insertNamedChild(String path, String key, ServerExpression node) {
		return addArg("insertNamedChild", new XsValueImpl.StringValImpl(path), new XsValueImpl.StringValImpl(key), node);
	}

	@Override
	public PatchBuilder insertNamedChild(XsStringVal path, XsStringVal key, ServerExpression node) {
		return addArg("insertNamedChild", path, key, node);
	}

	@Override
	public PatchBuilder remove(String path) {
		return addArg("remove", new XsValueImpl.StringValImpl(path));
	}

	@Override
	public PatchBuilder remove(XsStringVal path) {
		return addArg("remove", path);
	}

	@Override
	public PatchBuilder replace(String path, ServerExpression node) {
		return addArg("replace", path, node);
	}

	@Override
	public PatchBuilder replace(XsStringVal path, ServerExpression node) {
		return addArg("replace", path, node);
	}

	@Override
	public PatchBuilder replaceInsertChild(String parentPath, String pathToReplace) {
		return addArg("replaceInsertChild", new XsValueImpl.StringValImpl(parentPath), new XsValueImpl.StringValImpl(pathToReplace));
	}

	@Override
	public PatchBuilder replaceInsertChild(XsStringVal parentPath, XsStringVal pathToReplace) {
		return addArg("replaceInsertChild", parentPath, pathToReplace);
	}

	@Override
	public PatchBuilder replaceInsertChild(String parentPath, String pathToReplace, ServerExpression node) {
		return addArg("replaceInsertChild", new XsValueImpl.StringValImpl(parentPath), new XsValueImpl.StringValImpl(pathToReplace), node);
	}

	@Override
	public PatchBuilder replaceInsertChild(XsStringVal parentPath, XsStringVal pathToReplace, ServerExpression node) {
		return addArg("replaceInsertChild", parentPath, pathToReplace, node);
	}

	@Override
	public PatchBuilder replaceValue(String path, ServerExpression value) {
		return addArg("replaceValue", path, value);
	}

	@Override
	public PatchBuilder replaceValue(XsStringVal path, ServerExpression value) {
		return addArg("replaceValue", path, value);
	}

	private PatchBuilder addArg(String functionName, String path, ServerExpression value) {
		return addArg(functionName, new XsValueImpl.StringValImpl(path), value);
	}

	private PatchBuilder addArg(String functionName, ServerExpression... expressions) {
		BaseTypeImpl.BaseArgImpl newArg = new BaseTypeImpl.BaseCallImpl(
			"op", functionName, makeArgs(expressions)
		);
		List<BaseTypeImpl.BaseArgImpl> newArgs = new ArrayList<>();
		newArgs.addAll(Arrays.asList(getArgsImpl()));
		newArgs.add(newArg);
		return new PatchBuilderImpl(newArgs);
	}

	private BaseTypeImpl.BaseArgImpl[] makeArgs(ServerExpression... expressions) {
		List<BaseTypeImpl.BaseArgImpl> args = new ArrayList<>();
		for (ServerExpression expression : expressions) {
			args.add((BaseTypeImpl.BaseArgImpl) expression);
		}
		return args.toArray(new BaseTypeImpl.BaseArgImpl[]{});
	}
}
