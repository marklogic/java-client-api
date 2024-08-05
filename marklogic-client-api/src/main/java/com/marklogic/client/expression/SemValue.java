/*
 * Copyright (c) 2022 MarkLogic Corporation
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
package com.marklogic.client.expression;

import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.SemIriSeqVal;
import com.marklogic.client.type.SemIriVal;
import com.marklogic.client.type.SemStoreExpr;
import com.marklogic.client.type.SemStoreSeqExpr;
import com.marklogic.client.type.XsStringSeqVal;

/**
 * SemValue takes Java values and constructs atomic values and
 * sequences of atomic values with a semantic data type
 *
 * The typed values can then be passed to expression functions
 * for execution on the server.
 */
public interface SemValue {
    /**
     * Takes a semantic iri as a string and constructs a sem:iri value
     * @param stringIri	the uri as a string
     * @return	a value with an sem:iri data type
     */
    public SemIriVal     iri(String stringIri);
    /**
     * Takes any number of iris as a string and constructs an sem:iri sequence
     * @param stringIris	the iris as strings
     * @return	a value sequence with an sem:iri data type
     */
    public SemIriSeqVal iriSeq(String... stringIris);
    /**
     * Takes any number of iris as semtyped :iri values and constructs an sem:iri sequence
     * @param iris	the iris as strings
     * @return	a value sequence with an sem:iri data type
     */
    public SemIriSeqVal iriSeq(SemIriVal... iris);

    /**
     * Specifies a store for accessing triples, modifying the triple retrieval based on the options.
     * @param options	the specification for tuning the retrieval of triples
     * @return	the specification of the store with the triples
     */
    public SemStoreExpr store(String... options);
    /**
     * Specifies a store for accessing triples, modifying the triple retrieval based on the options
     * with a restriction to the triples contained in documents matching the query.
     * @param options	the specification for tuning the retrieval of triples
     * @param query	the CtsQuery matching the documents that contain the triples
     * @return	the specification of the store with the triples
     */
    public SemStoreExpr store(XsStringSeqVal options, CtsQueryExpr query);
    /**
     * Specifies a store with additional triples inferred based on rules.
     * @param locations	specifies the rulesets for inferring additional triples
     * @return	the specification of the store with the rulesets for inferring triples
     */
    public SemStoreExpr rulesetStore(String... locations);
    /**
     * Specifies a store with additional triples inferred based on rules and
     * a base store providing triples as input for the rules.
     * @param locations	specifies the rulesets for inferring additional triples
     * @param stores	specifies the stores providing triples as input for inferrence
     * @return	the specification of the store with the rulesets for inferring triples
     */
    public SemStoreExpr rulesetStore(XsStringSeqVal locations, SemStoreExpr... stores);
    /**
     * Specifies a store with additional triples inferred based on rules and
     * a base store providing triples as input for the rules.
     * @param locations	specifies the rulesets for inferring additional triples
     * @param stores	specifies the stores providing triples as input for inferrence
     * @param options	the specification for tuning the inference of new triples
     * @return	the specification of the store with the rulesets for inferring triples
     */
    public SemStoreExpr rulesetStore(XsStringSeqVal locations, SemStoreSeqExpr stores, String... options);

    /**
     * Takes any number of triple store specifications and constructs a store sequence
     * @param stores	the triple store specifications
     * @return	a sequence of triple store specifications
     */
    public SemStoreSeqExpr stores(SemStoreExpr... stores);
}
