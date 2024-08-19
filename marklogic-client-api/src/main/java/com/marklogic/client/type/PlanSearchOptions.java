/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An option controlling the scoring and weighting of fromSearch()
 * for a row pipeline.
 */
public interface PlanSearchOptions {
	/**
	 * Changed in release 7.0.0 to return a float, as the server requires a float and throws an error on a double.
	 */
	XsFloatVal getQualityWeight();
    ScoreMethod getScoreMethod();
	/**
	 * @since 7.0.0; requires MarkLogic 12 or higher.
	 */
	XsDoubleVal getBm25LengthWeight();
	/**
	 * Changed in release 7.0.0 to return a float, as the server requires a float and throws an error on a double.
	 */
    PlanSearchOptions withQualityWeight(float qualityWeight);
	/**
	 * Changed in release 7.0.0 to return a float, as the server requires a float and throws an error on a double.
	 */
    PlanSearchOptions withQualityWeight(XsFloatVal qualityWeight);
    PlanSearchOptions withScoreMethod(ScoreMethod scoreMethod);
	/**
	 * @since 7.0.0; requires MarkLogic 12 or higher.
	 */
	PlanSearchOptions withBm25LengthWeight(double bm25LengthWeight);
    enum ScoreMethod {
        LOGTFIDF, LOGTF, SIMPLE, BM25;
		// zero and random aren't in the 12 EA release.
		//ZERO, RANDOM;
    }
}
