package com.marklogic.client.test.junit5;

import com.marklogic.client.test.Common;
import com.marklogic.client.test.MarkLogicVersion;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RequiresMLElevenDotOne implements ExecutionCondition {

	private static MarkLogicVersion markLogicVersion;

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (markLogicVersion == null) {
			markLogicVersion = Common.getMarkLogicVersion();
		}
		boolean supported =
			(markLogicVersion.getMajor() == 11 && markLogicVersion.getMinor() >= 1) ||
			markLogicVersion.getMajor() >= 12;
		return supported ?
			ConditionEvaluationResult.enabled("MarkLogic is version 11.1 or higher") :
			ConditionEvaluationResult.disabled("MarkLogic is version 11.0.x or lower");
	}
}
