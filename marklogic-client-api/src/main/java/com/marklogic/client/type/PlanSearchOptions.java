/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * Options controlling the scoring, weighting, and fragment scope for {@code fromSearch()} and
 * {@code fromSearchDocs()} in a row pipeline. Use {@link #withFragment(Fragment)} to select which
 * fragment types (document, properties, locks, or any) are searched and returned.
 *
 * <p>Fragment scope support was added in release 8.2.0 and requires MarkLogic 12.1 or higher.
 * Scoring and weighting options apply to all supported MarkLogic versions.</p>
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

	/**
	 * @since 8.2.0; requires MarkLogic 12.1 or higher.
	 */
	Fragment getFragment();

	/**
	 * Specifies the type of fragment to search and return. Defaults to {@link Fragment#DOCUMENT} when no option
	 * is specified. Applies to both {@code fromSearch()} and {@code fromSearchDocs()}.
	 *
	 * @param fragment the fragment scope to select
	 * @return a new PlanSearchOptions with the fragment set
	 * @since 8.2.0; requires MarkLogic 12.1 or higher.
	 */
	PlanSearchOptions withFragment(Fragment fragment);

	/**
	 * Controls which type of fragments are searched and returned by {@code fromSearch()} and
	 * {@code fromSearchDocs()}.
	 *
	 * @since 8.2.0; requires MarkLogic 12.1 or higher.
	 */
	enum Fragment {
		DOCUMENT,
		ANY,
		PROPERTIES,
		LOCKS
	}

	enum ScoreMethod {
		LOGTFIDF,
		LOGTF,
		SIMPLE,
		BM25,

		/**
		 * @since 7.1.0; requires MarkLogic 12 EA2 or higher.
		 */
		ZERO,

		/**
		 * @since 7.1.0; requires MarkLogic 12 EA2 or higher.
		 */
		RANDOM;
	}
}
