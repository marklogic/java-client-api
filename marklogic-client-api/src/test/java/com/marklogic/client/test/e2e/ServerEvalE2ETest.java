/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("E2E Test: Server Evaluation (JavaScript and XQuery)")
public class ServerEvalE2ETest extends AbstractClientTest {

	private DatabaseClient evalClient;

	@BeforeEach
	void setUp() {
		evalClient = Common.connectEval();
	}

	@Test
	@DisplayName("Server JavaScript: Basic Computation and Type Mapping")
	void testJavaScriptEvaluation() {
		// Math calculation
		int sum = evalClient.newServerEval().javascript("const a = 15; const b = 27; a + b;").eval().next().getNumber().intValue();
		assertEquals(42, sum);

		// String manipulation
		String greeting = evalClient.newServerEval().javascript("const name = 'MarkLogic'; `Hello, ${name}!`;").evalAs(String.class);
		assertEquals("Hello, MarkLogic!", greeting);

		// Boolean result
		boolean isReady = evalClient.newServerEval().javascript("10 > 5 && true;").eval().next().getBoolean();
		assertTrue(isReady);
	}

	@Test
	@DisplayName("Server JavaScript: Variable Bindings across Types")
	void testJavaScriptVariableBindings() {
		ServerEvaluationCall eval = evalClient.newServerEval();
		eval.addVariable("name", "Antigravity")
			.addVariable("iterations", 5)
			.addVariable("active", true);

		String result = eval.javascript("""
			let output = [];
			for (let i = 0; i < iterations; i++) {
				output.push(`${name}-${i}`);
			}
			JSON.stringify({result: output, active: active});
			""").evalAs(String.class);

		assertTrue(result.contains("Antigravity-0"));
		assertTrue(result.contains("Antigravity-4"));
		assertTrue(result.contains("true") && result.contains("active"));
	}

	@Test
	@DisplayName("Server XQuery: Ad-Hoc Script and Sequence Results")
	void testXQueryEvaluation() {
		ServerEvaluationCall eval = evalClient.newServerEval();
		eval.addVariable("prefix", "ITEM");

		EvalResultIterator iterator = eval.xquery("""
			declare variable $prefix external;
			for $i in (1 to 3)
			return fn:concat($prefix, "-", $i)
			""").eval();

		List<String> items = new ArrayList<>();
		while (iterator.hasNext()) {
			EvalResult res = iterator.next();
			assertEquals(EvalResult.Type.STRING, res.getType());
			items.add(res.getString());
		}

		assertEquals(List.of("ITEM-1", "ITEM-2", "ITEM-3"), items);
	}

	@Test
	@DisplayName("Server Eval: Complex JSON Return via JacksonHandle")
	void testJsonReturnFromEval() {
		ServerEvaluationCall eval = evalClient.newServerEval();
		JacksonHandle handle = new JacksonHandle();

		eval.javascript("""
			({
				service: "MarkLogic REST",
				version: 12,
				capabilities: ["search", "optic", "dmsdk", "semantics"]
			});
			""").eval(handle);

		JsonNode json = handle.get();
		assertNotNull(json);
		assertEquals("MarkLogic REST", json.get("service").asText());
		assertEquals(12, json.get("version").asInt());
		assertTrue(json.get("capabilities").isArray());
		assertEquals(4, json.get("capabilities").size());
	}

	@Test
	@DisplayName("Server Eval Error Handling: Syntax Error Returns FailedRequestException")
	void testEvalSyntaxError() {
		ServerEvaluationCall eval = evalClient.newServerEval();
		FailedRequestException ex = assertThrows(FailedRequestException.class, () ->
			eval.javascript("const unclosed = 'hello;").evalAs(String.class)
		);
		assertTrue(ex.getServerMessage().contains("Invalid or unexpected token") || ex.getServerStatusCode() == 400 || ex.getServerStatusCode() == 500);
	}
}
