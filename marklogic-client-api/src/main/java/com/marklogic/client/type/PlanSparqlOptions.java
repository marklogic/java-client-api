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
package com.marklogic.client.type;

// IMPORTANT: Do not edit. This file is generated.

/**
 * An option controlling whether to check for duplicate triples (which is more expensive) or
 * to supply a base IRI for triples IRIs.
 */
public interface PlanSparqlOptions {
    XsStringVal getBase();
    PlanSparqlOptions withBase(String base);
    PlanSparqlOptions withBase(XsStringVal base);
    XsBooleanVal getDeduplicated();
    PlanSparqlOptions withDeduplicated(boolean deduplicate);
    PlanSparqlOptions withDeduplicated(XsBooleanVal deduplicate);
}
