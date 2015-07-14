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

public class SPARQLRuleset {
    public static SPARQLRuleset ALL_VALUES_FROM = new SPARQLRuleset("allValuesFrom.rules");
    public static SPARQLRuleset DOMAIN = new SPARQLRuleset("domain.rules");
    public static SPARQLRuleset EQUIVALENT_CLASS = new SPARQLRuleset("equivalentClass.rules");
    public static SPARQLRuleset RANGE = new SPARQLRuleset("range.rules");
    public static SPARQLRuleset EQUIVALENT_PROPERTY = new SPARQLRuleset("equivalentProperty.rules");
    public static SPARQLRuleset FUNCTIONAL_PROPERTY = new SPARQLRuleset("FunctionalProperty.rules");
    public static SPARQLRuleset HAS_VALUE = new SPARQLRuleset("hasValue.rules");
    public static SPARQLRuleset INTERSECTION_OF = new SPARQLRuleset("intersectionOf.rules");
    public static SPARQLRuleset INVERSE_FUNCTIONAL_PROPERTY = new SPARQLRuleset("InverseFunctionalProperty.rules");
    public static SPARQLRuleset INVERSE_OF = new SPARQLRuleset("inverseOf.rules");
    public static SPARQLRuleset ON_PROPERTY = new SPARQLRuleset("onProperty.rules");
    public static SPARQLRuleset OWL_HORST_FULL = new SPARQLRuleset("owl-horst-full.rules");
    public static SPARQLRuleset OWL_HORST = new SPARQLRuleset("owl-horst.rules");
    public static SPARQLRuleset RDFS_FULL = new SPARQLRuleset("rdfs-full.rules");
    public static SPARQLRuleset RDFS_PLUS_FULL = new SPARQLRuleset("rdfs-plus-full.rules");
    public static SPARQLRuleset RDFS_PLUS = new SPARQLRuleset("rdfs-plus.rules");
    public static SPARQLRuleset RDFS = new SPARQLRuleset("rdfs.rules");
    public static SPARQLRuleset SAME_AS = new SPARQLRuleset("sameAs.rules");
    public static SPARQLRuleset SOME_VALUES_FROM = new SPARQLRuleset("someValuesFrom.rules");
    public static SPARQLRuleset SUBCLASS_OF = new SPARQLRuleset("subClassOf.rules");
    public static SPARQLRuleset SUBPROPERTY_OF = new SPARQLRuleset("subPropertyOf.rules");
    public static SPARQLRuleset SYMMETRIC_PROPERTY = new SPARQLRuleset("SymmetricProperty.rules");
    public static SPARQLRuleset TRANSITIVE_PROPERTY = new SPARQLRuleset("TransitiveProperty.rules");
    
    private String rulesetName;
    
    public String getName() {
        return rulesetName;
    }
    
    public static SPARQLRuleset ruleset(String rulesetName) {
        return new SPARQLRuleset(rulesetName);
    }

    private SPARQLRuleset(String rulesetName){
        this.rulesetName = rulesetName;
    }
}
