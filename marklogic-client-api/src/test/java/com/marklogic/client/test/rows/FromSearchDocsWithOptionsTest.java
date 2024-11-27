package com.marklogic.client.test.rows;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML12;
import com.marklogic.client.type.PlanSearchOptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RequiresML12.class)
class FromSearchDocsWithOptionsTest extends AbstractOpticUpdateTest {

	@Test
	void bm25() {
//		Note that this does not actually test that the scoring is correct.
//		It only tests that including the BM25 scoring option and a valid bm25LengthWeight do not cause any problems.
		rowManager.withUpdate(false);
		PlanSearchOptions options = op.searchOptions()
			.withScoreMethod(PlanSearchOptions.ScoreMethod.BM25)
			.withBm25LengthWeight(0.25);
		List<RowRecord> rows = resultRows(op.fromSearchDocs(op.cts.wordQuery("saxophone"), null, options));
		assertEquals(2, rows.size());
	}

	@Test
	void bm25ViaSearchOptions() {
		final String combinedQuery = "<search xmlns='http://marklogic.com/appservices/search'>" +
			"<options><search-option>score-bm25</search-option></options>" +
			"<qtext>saxophone</qtext></search>";

		QueryManager queryManager = Common.client.newQueryManager();
		SearchHandle results = queryManager.search(
			queryManager.newRawCombinedQueryDefinition(new StringHandle(combinedQuery).withFormat(Format.XML)),
			new SearchHandle());
		assertEquals(2, results.getTotalResults(), "Just doing a simple search to verify that score-bm25 is " +
			"recognized as a valid search option.");
	}

	@Test
	void qualityWeight() {
//		Note that this does not actually test that the scoring is correct.
//		It only tests that including a valid qualityWeight value does not cause any problems.
		rowManager.withUpdate(false);
		PlanSearchOptions options = op.searchOptions().withScoreMethod(PlanSearchOptions.ScoreMethod.LOGTFIDF).withQualityWeight(0.75F);
		List<RowRecord> rows = resultRows(op.fromSearchDocs(op.cts.wordQuery("saxophone"), null, options));
		assertEquals(2, rows.size());
	}
}
