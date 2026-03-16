/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

// IMPORTANT: Do not edit. This file is generated.

/**
 * An instance of a column builder returned by the columnBuilder() method
 * in a row pipeline. Used to create column definitions for op:from-docs.
 *
 * @since 8.1.0; requires MarkLogic 12.1 or higher
 */
public interface PlanColumnBuilder extends ServerExpression {

	/**
	 * Add a column definition.
	 *
	 * @param name The name of the column
	 * @return a PlanColumnBuilder object
	 */
	PlanColumnBuilder addColumn(String name);

	/**
	 * Set the XPath expression for the current column.
	 *
	 * @param path The XPath expression
	 * @return a PlanColumnBuilder object
	 */
	PlanColumnBuilder xpath(String path);

	/**
	 * Set the data type for the current column.
	 *
	 * @param type The data type (e.g., "string", "integer", "decimal", "vector", "point")
	 * @return a PlanColumnBuilder object
	 */
	PlanColumnBuilder type(String type);

	/**
	 * Specify whether the column can be null.
	 *
	 * @param nullable Whether the column can be null
	 * @return a PlanColumnBuilder object
	 */
	PlanColumnBuilder nullable(boolean nullable);

	/**
	 * Set an expression to compute the column value.
	 *
	 * @param expression The expression to compute the value
	 * @return a PlanColumnBuilder object
	 */
	PlanColumnBuilder expr(ServerExpression expression);

	/**
	 * Set a default value for the column.
	 *
	 * @param value The default value
	 * @return a PlanColumnBuilder object
	 */
	PlanColumnBuilder defaultValue(String value);

	/**
	 * Set the collation for the column.
	 *
	 * @param collation The collation URI
	 * @return a PlanColumnBuilder object
	 */
	PlanColumnBuilder collation(String collation);

	/**
	 * Set the dimension for a vector column.
	 *
	 * @param dimension The vector dimension
	 * @return a PlanColumnBuilder object
	 */
	PlanColumnBuilder dimension(int dimension);

	/**
	 * Set the coordinate system for a geospatial column.
	 *
	 * @param coordinateSystem The coordinate system (e.g., "wgs84")
	 * @return a PlanColumnBuilder object
	 */
	PlanColumnBuilder coordinateSystem(String coordinateSystem);
}
