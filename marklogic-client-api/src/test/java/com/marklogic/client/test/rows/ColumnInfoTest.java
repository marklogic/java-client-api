package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RawQueryDSLPlan;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RequiresML11.class)
public class ColumnInfoTest {

	private RowManager rowManager;

	@BeforeEach
	void beforeEach() {
		rowManager = Common.newClient().newRowManager();
	}

	@Test
	void allTypesWithDSLPlan() {
		String query = "op.fromView('javaClient', 'allTypes')";
		RawQueryDSLPlan plan = rowManager.newRawQueryDSLPlan(new StringHandle(query));
		verifyColumnInfo(plan);
	}

	@Test
	void allTypesWithSerializedPlan() {
		String serializedQuery = "{\n" +
			"  \"$optic\": {\n" +
			"    \"ns\": \"op\",\n" +
			"    \"fn\": \"operators\",\n" +
			"    \"args\": [\n" +
			"      {\n" +
			"        \"ns\": \"op\",\n" +
			"        \"fn\": \"from-view\",\n" +
			"        \"args\": [\n" +
			"          \"javaClient\",\n" +
			"          \"allTypes\"\n" +
			"        ]\n" +
			"      }\n" +
			"    ]\n" +
			"  }\n" +
			"}";

		RawPlanDefinition plan = rowManager.newRawPlanDefinition(new StringHandle(serializedQuery));
		verifyColumnInfo(plan);
	}

	/**
	 * The expected JSON for all the column infos is based on the results from the 20230417 build. See the internal
	 * DBQ-296 ticket for an explanation on why some of the columns have an expected type of "none".
	 *
	 * @param plan
	 */
	private void verifyColumnInfo(PlanBuilder.Plan plan) {
		StringHandle output = Common.client.newRowManager().columnInfo(plan, new StringHandle());

		assertTrue(output.getServerTimestamp() > 0, "The server timestamp should be present so that a client, such as " +
			"the Spark connector, can both get column info and identify a timestamp for future point-in-time queries.");

		String[] columnInfos = output.get().split("\n");
		assertEquals(36, columnInfos.length, "There are 35 column definitions in the allTypes TDE, and then a 36th " +
			"column info is added for the rowid column.");

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode actualColumnInfo = mapper.readTree("[" + String.join(",", columnInfos) + "]");
			JsonNode expectedColumnInfo = mapper.readTree(new ClassPathResource("allTypes-columnInfo.json").getInputStream());
			JSONAssert.assertEquals(expectedColumnInfo.toString(), actualColumnInfo.toString(), true);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
