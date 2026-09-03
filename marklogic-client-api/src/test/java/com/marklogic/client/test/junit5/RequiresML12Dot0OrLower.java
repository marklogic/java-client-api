/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.junit5;

import com.marklogic.client.test.Common;
import com.marklogic.client.test.MarkLogicVersion;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RequiresML12Dot0OrLower implements ExecutionCondition {

	private static MarkLogicVersion markLogicVersion;

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (markLogicVersion == null) {
			markLogicVersion = Common.getMarkLogicVersion();
		}
		boolean isML12Dot0OrLower = markLogicVersion.getMajor() < 12 ||
			(markLogicVersion.getMajor() == 12 && (markLogicVersion.getMinor() == null || markLogicVersion.getMinor() == 0));
		return isML12Dot0OrLower ?
			ConditionEvaluationResult.enabled("MarkLogic is version 12.0 or lower") :
			ConditionEvaluationResult.disabled("MarkLogic is version 12.1 or higher");
	}
}
