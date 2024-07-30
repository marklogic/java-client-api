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
package com.marklogic.client.type;

/**
 * An option controlling the scoring and weighting of fromSearch()
 * for a row pipeline.
 */
public interface PlanSearchOptions {
	/**
	 * Changed in release 6.7.0 to return a float, as the server requires a float and throws an error on a double.
	 */
	XsFloatVal getQualityWeight();
    ScoreMethod getScoreMethod();
	/**
	 * @since 6.7.0
	 */
	XsDoubleVal getBm25LengthWeight();
	/**
	 * Changed in release 6.7.0 to return a float, as the server requires a float and throws an error on a double.
	 */
    PlanSearchOptions withQualityWeight(float qualityWeight);
	/**
	 * Changed in release 6.7.0 to return a float, as the server requires a float and throws an error on a double.
	 */
    PlanSearchOptions withQualityWeight(XsFloatVal qualityWeight);
    PlanSearchOptions withScoreMethod(ScoreMethod scoreMethod);
	/**
	 * @since 6.7.0
	 */
	PlanSearchOptions withBm25LengthWeight(double bm25LengthWeight);
    enum ScoreMethod {
        LOGTFIDF, LOGTF, SIMPLE, BM25;
		// zero and random aren't in the 12 EA release.
		//ZERO, RANDOM;
    }
}
