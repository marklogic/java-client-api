/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.junit5;

import com.marklogic.client.test.Common;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Some tests can't run when using the reverse proxy server; for example, TLS/SSL messages don't yet work with our
 * reverse proxy server.
 */
public class DisabledWhenUsingReverseProxyServer implements ExecutionCondition {

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
		return Common.USE_REVERSE_PROXY_SERVER ?
			ConditionEvaluationResult.disabled("This test is disabled when the tests are run against a reverse proxy server.") :
			ConditionEvaluationResult.enabled("This test is enabled since the reverse proxy server is not being used.");
	}
}
