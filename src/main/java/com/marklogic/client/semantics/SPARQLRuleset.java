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
package com.marklogic.client.semantics;

public enum SPARQLRuleset {
    ALL_VALUES_FROM,
    DOMAIN,
    EQUIVALENT_CLASS,
    RANGE,
    EQUIVALENT_PROPERTY,
    FUNCTIONAL_PROPERTY,
    HAS_VALUE,
    INTERSECTION_OF,
    INVERSE_FUNCTIONAL_PROPERTY,
    INVERSE_OF,
    ON_PROPERTY,
    OWL_HORST_FULL,
    OWL_HORST,
    RDFS_FULL,
    RDFS_PLUS_FULL,
    RDFS_PLUS,
    RDFS,
    SAME_AS,
    SOME_VALUES_FROM,
    SUBCLASS_OF,
    SUBPROPERTY_OF,
    SYMMETRIC_PROPERTY,
    TRANSITIVE_PROPERTY;
    
    public static SPARQLRuleset ruleset(String name){
        // TODO: implement
        return null;
    }
}
