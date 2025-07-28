/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.row;

import com.marklogic.client.expression.PlanBuilder;

/**
 * An abstraction for a serialization of a plan
 * such as an AST in JSON format, a Query DSL in JavaScript syntax,
 * or a SQL or SPARQL SELECT query.
 */
public interface RawPlan extends PlanBuilder.Plan {
}
