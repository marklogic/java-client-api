package com.marklogic.client.test.junit5;

import com.marklogic.client.test.Common;
import com.marklogic.client.test.MarkLogicVersion;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RequiresML11OrLower implements ExecutionCondition {

	private static MarkLogicVersion markLogicVersion;

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (markLogicVersion == null) {
			markLogicVersion = Common.getMarkLogicVersion();
		}
		return markLogicVersion.getMajor() <= 11 ?
			ConditionEvaluationResult.enabled("MarkLogic major version is 11 or lower") :
			ConditionEvaluationResult.disabled("MarkLogic major version is 12 or higher");
	}
}
