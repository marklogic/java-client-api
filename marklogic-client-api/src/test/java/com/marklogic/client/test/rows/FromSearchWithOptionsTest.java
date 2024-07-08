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
class FromSearchWithOptionsTest extends AbstractOpticUpdateTest {

	@Test
	void bm25() {
//		Note that this does not actually test that the scoring is correct.
//		It only tests that including the BM25 scoring option and a valid bm25LengthWeight do not cause any problems.
		rowManager.withUpdate(false);
		PlanSearchOptions options = op.searchOptions()
			.withScoreMethod(PlanSearchOptions.ScoreMethod.BM25)
			.withBm25LengthWeight(0.25);
		List<RowRecord> rows = resultRows(
			op.fromSearch(op.cts.wordQuery("contents"), null, null, options)
				.offsetLimit(0, 5)
		);
		assertEquals(5, rows.size());
	}

	@Test
	void badBm25LengthWeight() {
		rowManager.withUpdate(false);
		PlanSearchOptions options = op.searchOptions()
			.withScoreMethod(PlanSearchOptions.ScoreMethod.BM25)
			.withBm25LengthWeight(99);
		PlanBuilder.ModifyPlan plan = op.fromSearch(op.cts.wordQuery("contents"), null, null, options)
			.offsetLimit(0, 5);
		Exception exception = assertThrows(FailedRequestException.class, () -> resultRows(plan));
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains("Server Message: XDMP-OPTION"));
		assertTrue(actualMessage.contains("Invalid option \"bm25-length-weight"));
	}

	@Test
	void zero() {
		rowManager.withUpdate(false);
		PlanSearchOptions options = op.searchOptions().withScoreMethod(PlanSearchOptions.ScoreMethod.ZERO);
		List<RowRecord> rows = resultRows(
			op.fromSearch(op.cts.wordQuery("contents"), null, null, options)
				.offsetLimit(0, 5)
		);
		assertEquals(5, rows.size());
		rows.forEach(row -> {
			assertEquals(0, row.getInt("score"), "The score for every row should be 0.");
		});
	}

	@Test
	void qualityWeight() {
//		Note that this does not actually test that the scoring is correct.
//		It only tests that including a valid qualityWeight value does not cause any problems.
		rowManager.withUpdate(false);
		PlanSearchOptions options = op.searchOptions().withScoreMethod(PlanSearchOptions.ScoreMethod.LOGTFIDF).withQualityWeight(0.75F);
		List<RowRecord> rows = resultRows(
			op.fromSearch(op.cts.wordQuery("contents"), null, null, options)
				.offsetLimit(0, 5)
		);
		assertEquals(5, rows.size());
	}
}
