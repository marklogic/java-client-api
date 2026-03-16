/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.type.PlanColumnBuilder;
import com.marklogic.client.type.ServerExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
class ColumnBuilderImpl extends BaseTypeImpl.BaseCallImpl implements PlanColumnBuilder, BaseTypeImpl.BaseArgImpl {

	ColumnBuilderImpl() {
		super("op", "suboperators",
			new BaseTypeImpl.BaseArgImpl[]{
				new BaseTypeImpl.BaseCallImpl("op", "column-builder", new BaseTypeImpl.BaseArgImpl[]{})
			});
	}

	private ColumnBuilderImpl(List<BaseTypeImpl.BaseArgImpl> args) {
		super("op", "suboperators", args.toArray(new BaseTypeImpl.BaseArgImpl[]{}));
	}

	public PlanColumnBuilder addColumn(String name) {
		return addArg("add-column", name);
	}

	public PlanColumnBuilder xpath(String path) {
		return addArg("xpath", path);
	}

	public PlanColumnBuilder type(String type) {
		return addArg("type", type);
	}

	public PlanColumnBuilder nullable(boolean nullable) {
		return addArg("nullable", new XsValueImpl.BooleanValImpl(nullable));
	}

	public PlanColumnBuilder expr(ServerExpression expression) {
		return addArg("expr", expression);
	}

	public PlanColumnBuilder defaultValue(String value) {
		return addArg("default", value);
	}

	public PlanColumnBuilder collation(String collation) {
		return addArg("collation", collation);
	}

	public PlanColumnBuilder dimension(int dimension) {
		return addArg("dimension", dimension);
	}

	public PlanColumnBuilder coordinateSystem(String coordinateSystem) {
		return addArg("coordinate-system", coordinateSystem);
	}

	private PlanColumnBuilder addArg(String functionName, Object... args) {
		BaseTypeImpl.BaseArgImpl newArg = new BaseTypeImpl.BaseCallImpl(
			"op", functionName, makeArgs(args)
		);
		List<BaseTypeImpl.BaseArgImpl> newArgs = new ArrayList<>();
		newArgs.addAll(Arrays.asList(getArgsImpl()));
		newArgs.add(newArg);
		return new ColumnBuilderImpl(newArgs);
	}

	private BaseTypeImpl.BaseArgImpl[] makeArgs(Object... args) {
		List<BaseTypeImpl.BaseArgImpl> argList = new ArrayList<>();
		for (Object arg : args) {
			if (arg instanceof BaseTypeImpl.BaseArgImpl) {
				argList.add((BaseTypeImpl.BaseArgImpl) arg);
			} else {
				// Use Literal for plain values (strings, numbers, etc.)
				argList.add(new BaseTypeImpl.Literal(arg));
			}
		}
		return argList.toArray(new BaseTypeImpl.BaseArgImpl[]{});
	}
}
