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

import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.query.CtsQueryDefinition;
import com.marklogic.client.type.CtsQueryExpr;

/**
 * CtsQueryBuilder builds a query for documents in the database.
 */
public abstract class CtsQueryBuilder {

    /**
     * Builds expressions with cts server functions.
     */
    public final CtsExpr cts;

    /**
     * Builds expressions with fn server functions.
     */
    public final FnExpr fn;

    /**
     * Builds expressions with geo server functions.
     */
    public final GeoExpr geo;

    /**
     * Builds expressions with json server functions.
     */
    public final JsonExpr json;

    /**
     * Builds expressions with map server functions.
     */
    public final MapExpr map;

    /**
     * Builds expressions with math server functions.
     */
    public final MathExpr math;

    /**
     * Builds expressions with rdf server functions.
     */
    public final RdfExpr rdf;

    /**
     * Builds expressions with sem server functions.
     */
    public final SemExpr sem;

    /**
     * Builds expressions with spell server functions.
     */
    public final SpellExpr spell;

    /**
     * Builds expressions with sql server functions.
     */
    public final SqlExpr sql;

    /**
     * Builds expressions with xdmp server functions.
     */
    public final XdmpExpr xdmp;

    /**
     * Builds expressions with xs server functions.
     */
    public final XsExpr xs;

    protected CtsQueryBuilder(
            CtsExpr cts, FnExpr fn, GeoExpr geo, JsonExpr json, MapExpr map, MathExpr math, RdfExpr rdf, SemExpr sem,
            SpellExpr spell, SqlExpr sql, XdmpExpr xdmp, XsExpr xs
    ) {
        this.cts = cts;
        this.fn = fn;
        this.geo = geo;
        this.json = json;
        this.map = map;
        this.math = math;
        this.rdf = rdf;
        this.sem = sem;
        this.spell = spell;
        this.sql = sql;
        this.xdmp = xdmp;
        this.xs = xs;
    }


    /**
     * Create a CtsQueryDefinition based on a cts query
     * @param query a cts query
     * @return a CtsQueryDefinition
     */
    public abstract CtsQueryDefinition newCtsQueryDefinition(CtsQueryExpr query);

    /**
     * Create a CtsQueryDefinition based on a cts query and query options
     * @param query a cts query
     * @param queryOptions query options
     * @return a CtsQueryDefinition
     */
    public abstract CtsQueryDefinition newCtsQueryDefinition(CtsQueryExpr query, JSONWriteHandle queryOptions);

    /**
     * Export cts query into a handle in AST format
     * @param query the cts query to be exported
     * @param handle the handle to store exported query
     * @param <T> the handle type
     * @return a handle which contains exported cts query in AST format
     */
    public abstract <T extends JSONReadHandle> T export(CtsQueryExpr query, T handle);
}
