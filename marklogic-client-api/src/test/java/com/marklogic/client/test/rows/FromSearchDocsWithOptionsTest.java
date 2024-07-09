package com.marklogic.client.test.rows;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.junit5.RequiresML12;
import com.marklogic.client.type.PlanSearchOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
		List<RowRecord> rows = resultRows(
			op.fromSearchDocs(op.cts.wordQuery("contents"), null, options)
				.offsetLimit(0, 5)
		);
		assertEquals(5, rows.size());
	}

	@Test
	void qualityWeight() {
//		Note that this does not actually test that the scoring is correct.
//		It only tests that including a valid qualityWeight value does not cause any problems.
		rowManager.withUpdate(false);
		PlanSearchOptions options = op.searchOptions().withScoreMethod(PlanSearchOptions.ScoreMethod.LOGTFIDF).withQualityWeight(0.75F);
		List<RowRecord> rows = resultRows(
			op.fromSearchDocs(op.cts.wordQuery("contents"), null, options)
				.offsetLimit(0, 5)
		);
		assertEquals(5, rows.size());
	}
}
