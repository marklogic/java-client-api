/*
 * Copyright 2017-2018 MarkLogic Corporation
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

//IMPORTANT: Do not edit. This file is generated.

/**
 * An option controlling whether to calculate over all values including duplicates
 * or over distinct values for an aggregate expression in a row pipeline.
 */
public enum PlanValueOption implements PlanValueOptionSeq {
    DISTINCT, DUPLICATE;
}
