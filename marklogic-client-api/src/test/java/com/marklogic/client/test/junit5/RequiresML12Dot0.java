/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.junit5;

import com.marklogic.client.test.Common;
import com.marklogic.client.test.MarkLogicVersion;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RequiresML12Dot0 implements ExecutionCondition {

	private static MarkLogicVersion markLogicVersion;

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (markLogicVersion == null) {
			markLogicVersion = Common.getMarkLogicVersion();
		}
		boolean supported =
			markLogicVersion.getMajor() == 12 && markLogicVersion.getMinor() != null && markLogicVersion.getMinor() == 0;
		return supported ?
			ConditionEvaluationResult.enabled("MarkLogic is version 12.0.x") :
			ConditionEvaluationResult.disabled("MarkLogic is not version 12.0.x");
	}
}
