/*
 * Copyright (c) 2019 MarkLogic Corporation
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

public abstract class CtsQueryBuilder {

    public final CtsExpr cts;
    public final FnExpr fn;
    public final GeoExpr geo;
    public final JsonExpr json;
    public final MapExpr map;
    public final MathExpr math;
    public final RdfExpr rdf;
    public final SemExpr sem;
    public final SpellExpr spell;
    public final SqlExpr sql;
    public final XdmpExpr xdmp;
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


    public abstract CtsQueryDefinition newCtsQueryDefinition(CtsQueryExpr query);

    public abstract CtsQueryDefinition newCtsQueryDefinition(CtsQueryExpr query, JSONWriteHandle queryOptions);

    public abstract <T extends JSONReadHandle> T export(CtsQueryExpr query, T handle);
}
