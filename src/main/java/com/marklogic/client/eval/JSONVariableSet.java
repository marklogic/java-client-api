/*
 * Copyright 2012-2014 MarkLogic Corporation
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
package com.marklogic.client.eval;

public interface JSONVariableSet extends VariableSet {
    public JSONVariableSet add(String name, JSONWriteHandle value);
    public JSONVariableSet addAs(String name, Object value);
    /** Convenience method since boolean is applicable to both JSON and XML
     * and therefore cannot be registered with a default handle.  */
    public JSONVariableSet add(String name, boolean value);
    /** Convenience method since Number is applicable to both JSON and XML
     * and therefore cannot be registered with a default handle.  */
    public JSONVariableSet add(String name, Number value);
    /** Convenience method since dateTime is applicable to both JSON and XML
     * and therefore cannot be registered with a default handle.  */
    public JSONVariableSet add(String name, java.util.Calendar value);
}

