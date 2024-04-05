package com.marklogic.client.impl;

import com.marklogic.client.type.PatchBuilder;
import com.marklogic.client.type.ServerExpression;
import com.marklogic.client.type.XsStringVal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is what we have to manually implement and test in order to support patching in the Optic API.
 */
class PatchBuilderImpl extends BaseTypeImpl.BaseCallImpl implements PatchBuilder, BaseTypeImpl.BaseArgImpl {

	private BaseTypeImpl.BaseArgImpl[] args;

	public PatchBuilderImpl(String contextPath) {
		super("op", "suboperators",
			new BaseTypeImpl.BaseArgImpl[]{
				new BaseTypeImpl.BaseCallImpl("op", "patchBuilder",
					new BaseTypeImpl.BaseArgImpl[]{new XsValueImpl.StringValImpl(contextPath)
					})
			});
	}

	private PatchBuilderImpl(List<BaseTypeImpl.BaseArgImpl> args) {
		super("op", "suboperators", args.toArray(new BaseTypeImpl.BaseArgImpl[]{}));
	}

	@Override
	public PatchBuilder replaceValue(String path, ServerExpression value) {
		BaseTypeImpl.BaseArgImpl newArg = new BaseTypeImpl.BaseCallImpl("op", "replaceValue",
			new BaseTypeImpl.BaseArgImpl[]{new XsValueImpl.StringValImpl(path), (BaseTypeImpl.BaseArgImpl) value});
		List<BaseTypeImpl.BaseArgImpl> newArgs = new ArrayList<>();
		newArgs.addAll(Arrays.asList(getArgsImpl()));
		newArgs.add(newArg);
		return new PatchBuilderImpl(newArgs);
	}

	@Override
	public PatchBuilder replaceValue(XsStringVal path, ServerExpression value) {
		return null;
	}

	@Override
	public PatchBuilder insertAfter(String path, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder insertAfter(XsStringVal path, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder insertBefore(String path, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder insertBefore(XsStringVal path, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder insertChild(String path, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder insertChild(XsStringVal path, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder insertNamedChild(String path, String key, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder insertNamedChild(XsStringVal path, XsStringVal key, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder remove(String path) {
		return null;
	}

	@Override
	public PatchBuilder remove(XsStringVal path) {
		return null;
	}

	@Override
	public PatchBuilder replace(String path, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder replace(XsStringVal path, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder replaceInsertChild(String parentPath, String pathToReplace) {
		return null;
	}

	@Override
	public PatchBuilder replaceInsertChild(XsStringVal parentPath, XsStringVal pathToReplace) {
		return null;
	}

	@Override
	public PatchBuilder replaceInsertChild(String parentPath, String pathToReplace, ServerExpression node) {
		return null;
	}

	@Override
	public PatchBuilder replaceInsertChild(XsStringVal parentPath, XsStringVal pathToReplace, ServerExpression node) {
		return null;
	}
}
