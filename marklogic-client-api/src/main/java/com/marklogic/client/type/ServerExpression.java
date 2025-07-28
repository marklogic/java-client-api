/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * One or more server expressions.
 * To pass a sequence of server expressions, see {@link com.marklogic.client.expression.PlanBuilder#seq(ServerExpression...)}.
 *
 * <p>When building server expressions in Java, the parameters and return values
 * of the server expression functions are typed as ServerExpression objects.
 * On the server, expression have server types that are determined dynamically.
 * The server coerces the actual type of the return value from a call
 * to a server expression function to the expected type of a parameter
 * for a call to a server expression function.
 * To see the relationships between the server data types, see
 * the <a href="{@docRoot}/doc-files/types/ml-server-type-tree.html">Server Expression Type Hierarchy</a>.
 * </p>
 */
public interface ServerExpression {
}
