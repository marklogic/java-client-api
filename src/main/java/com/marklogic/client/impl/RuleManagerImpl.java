/*
 * Copyright 2013 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.alerting.RuleDefinition;
import com.marklogic.client.alerting.RuleDefinitionList;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.RuleReadHandle;
import com.marklogic.client.io.marker.RuleWriteHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.QueryDefinition;

public class RuleManagerImpl extends AbstractLoggingManager implements
		RuleManager {

	final static private String RULES_BASE = "/alert/rules";

	private RESTServices services;

	public RuleManagerImpl(RESTServices services) {
		this.services = services;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends RuleReadHandle> T readRule(String ruleName, T ruleHandle)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if (ruleName == null) {
			throw new IllegalArgumentException("Cannot read null rule name");
		}

		HandleImplementation ruleBase = HandleAccessor.checkHandle(ruleHandle,
				"rule");

		Format ruleFormat = ruleBase.getFormat();
		switch (ruleFormat) {
		case UNKNOWN:
			ruleFormat = Format.XML;
			break;
		case JSON:
		case XML:
			break;
		default:
			throw new UnsupportedOperationException(
					"Only JSON and XML rules are possible.");
		}

		String mimetype = ruleFormat.getDefaultMimetype();
		ruleBase.receiveContent(services.getValue(requestLogger, RULES_BASE,
				ruleName, false, mimetype, ruleBase.receiveAs()));

		return ruleHandle;
	}

	@Override
	public void writeRule(RuleDefinition ruleHandle) {
		String ruleName = ((RuleDefinition) ruleHandle).getName();
		writeRule(ruleName, ruleHandle);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void writeRule(String ruleName, RuleWriteHandle ruleHandle)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if (ruleHandle instanceof RuleDefinition) {
			String name = ((RuleDefinition) ruleHandle).getName();
			if (name != null)
				ruleName = name;
		}
		HandleImplementation ruleBase = HandleAccessor.checkHandle(ruleHandle,
				"rule");

		if (ruleBase == null)
			throw new IllegalArgumentException("Could not write null rule: "
					+ ruleName);

		Format ruleFormat = ruleBase.getFormat();
		switch (ruleFormat) {
		case UNKNOWN:
			ruleFormat = Format.XML;
			break;
		case JSON:
		case XML:
			break;
		default:
			throw new UnsupportedOperationException(
					"Only JSON and XML rules are supported.");
		}

		String mimetype = ruleFormat.getDefaultMimetype();

		services.putValue(requestLogger, RULES_BASE, ruleName, mimetype,
				ruleBase);
	}

	@Override
	public void delete(String ruleName) throws ForbiddenUserException,
			FailedRequestException {
		services.deleteValue(null, RULES_BASE, ruleName);
	}

	@Override
	public RuleDefinitionList match(QueryDefinition docQuery) {
		return match(docQuery, 1, new String[] {});
	}

	@Override
	public RuleDefinitionList match(QueryDefinition docQuery, long start,
			String[] candidateRules) {

		RuleDefinitionList ruleDefinitionList = new RuleDefinitionList();

		HandleAccessor.receiveContent(ruleDefinitionList,
				services.match(docQuery, start, candidateRules));
		;
		return ruleDefinitionList;
	}

	@Override
	public RuleDefinitionList match(String[] docIds) {
		return match(docIds, new String[] {});
	}

	@Override
	public RuleDefinitionList match(String[] docIds, String[] candidateRules) {
		RuleDefinitionList ruleDefinitionList = new RuleDefinitionList();
		HandleAccessor.receiveContent(ruleDefinitionList,
				services.match(docIds, candidateRules));
		return ruleDefinitionList;
	}

	@Override
	public RuleDefinitionList match(StructureWriteHandle document) {
		return match(document, new String[] {});
	}

	@Override
	public RuleDefinitionList match(StructureWriteHandle document,
			String[] candidateRules) {
		RuleDefinitionList ruleDefinitionList = new RuleDefinitionList();
		HandleAccessor.receiveContent(ruleDefinitionList,
				services.match(document, candidateRules));
		return ruleDefinitionList;
	}

}
