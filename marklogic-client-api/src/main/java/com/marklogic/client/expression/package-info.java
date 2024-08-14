/**
 * The package provides classes for building Optic plan pipelines and expressions
 * for execution on the REST server.
 * Use {@link com.marklogic.client.row.RowManager} to create
 * the {@link com.marklogic.client.expression.PlanBuilder} object.
 *
 * <p>The PlanBuilder can define expressions for execution on the server
 * by composing calls to server functions.  In Java, the parameters
 * and return values of the server expression functions are typed
 * as {@link com.marklogic.client.type.ServerExpression}.  On the
 * server, expression have server types that are determined dynamically.
 * The server coerces the actual type of the return value from a call
 * to a server expression function to the expected type of a parameter
 * for a call to a server expression function.
 * To see the relationships between the server data types, see
 * the <a href="{@docRoot}/doc-files/types/ml-server-type-tree.html">Server Expression Type Hierarchy</a>.
 * </p>
 */
/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.expression;
