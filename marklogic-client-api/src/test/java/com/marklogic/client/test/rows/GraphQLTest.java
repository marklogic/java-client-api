package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RequiresML11.class)
public class GraphQLTest extends AbstractOpticUpdateTest {

	private final static String QUERY = "query myQuery { opticUnitTest_musician {firstName lastName}}";

	private RowManager rowManager;

	@BeforeEach
	void beforeEach() {
		rowManager = Common.newClient().newRowManager();
	}

	@Test
	void jsonQuery() {
		ObjectNode query = mapper.createObjectNode().put("query", QUERY);
		JsonNode response = rowManager.graphql(new JacksonHandle(query), new JacksonHandle()).get();
		verifyResponse(response);
	}

	@Test
	void stringQuery() {
		JsonNode response = rowManager.graphql(new StringHandle("{\"query\": \"" + QUERY + "\"}"), new JacksonHandle()).get();
		verifyResponse(response);
	}

	@Test
	void getResultAsJson() {
		ObjectNode query = mapper.createObjectNode().put("query", QUERY);
		JsonNode response = rowManager.graphqlAs(new JacksonHandle(query), JsonNode.class);
		verifyResponse(response);
	}

	@Test
	void getResultAsString() throws Exception {
		ObjectNode query = mapper.createObjectNode().put("query", QUERY);
		String response = rowManager.graphqlAs(new JacksonHandle(query), String.class);
		verifyResponse(mapper.readTree(response));
	}

	@Test
	void invalidQuery() {
		ObjectNode query = mapper.createObjectNode().put("query", "this is not valid");
		JsonNode response = rowManager.graphql(new JacksonHandle(query), new JacksonHandle()).get();

		assertTrue(response.has("errors"));
		assertEquals(1, response.get("errors").size());

		String message = response.get("errors").get(0).get("message").asText();
		assertTrue(message.startsWith("GRAPHQL-PARSE: Error parsing the GraphQL request string"),
			"Unexpected error message: " + message);
	}

	private void verifyResponse(JsonNode response) {
		JsonNode data = response.get("data");
		JsonNode musicians = data.get("opticUnitTest_musician");
		assertEquals(4, musicians.size());
		musicians.forEach(musician -> {
			assertEquals(2, musician.size());
			assertTrue(musician.has("firstName"));
			assertTrue(musician.has("lastName"));
		});
	}
}
