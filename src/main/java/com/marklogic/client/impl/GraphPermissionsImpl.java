/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphPermissions;

public class GraphPermissionsImpl extends HashMap<String, Set<Capability>> implements GraphPermissions {
    @Override
    public GraphPermissions permission(String role, Capability... capabilities) {
        if ( capabilities == null ) throw new IllegalArgumentException("capabilities cannot be null");
        if ( this.get(role) == null ) {
            this.put(role, new HashSet<Capability>(Arrays.asList(capabilities)) );
        } else {
            this.get(role).addAll(Arrays.asList(capabilities));
        }
        return this;
    }
}
