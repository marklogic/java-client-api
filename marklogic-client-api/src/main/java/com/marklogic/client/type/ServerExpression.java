/*
 * Copyright 2018-2019 MarkLogic Corporation
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
package com.marklogic.client.type;

/**
 * One or more server expressions.
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
