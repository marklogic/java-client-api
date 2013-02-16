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

public class RuleManagerImpl extends AbstractLoggingManager implements RuleManager {


	final static private String RULES_BASE = "/alert/rules";
	
	private RESTServices services;

	public RuleManagerImpl(RESTServices services) {
		this.services = services;
	}

	@Override
	public boolean exists(String ruleName) {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends RuleReadHandle> T readRule(String ruleName, T ruleHandle)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if (ruleName == null) {
			throw new IllegalArgumentException(
					"Cannot read null rule name");
		}

		HandleImplementation ruleBase = HandleAccessor.checkHandle(
				ruleHandle, "rule");

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
		ruleBase.receiveContent(services.getValue(
				requestLogger, RULES_BASE, ruleName, false, mimetype,
				ruleBase.receiveAs()));

		return ruleHandle;
		}

	@SuppressWarnings("rawtypes")
	@Override
	public void writeRule(RuleWriteHandle ruleHandle)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		String name=null;
		if (ruleHandle instanceof RuleDefinition) {
			name = ((RuleDefinition) ruleHandle).getName();
		}
		HandleImplementation ruleBase = HandleAccessor.checkHandle(
				ruleHandle, "rule");
		
		if (ruleBase == null)
			throw new IllegalArgumentException("Could not write null rule: "
					+ name);

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

		services.putValue(requestLogger, RULES_BASE, name, mimetype, ruleBase);
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
	public RuleDefinitionList match(QueryDefinition docQuery,
			long start, String[] candidateRules) {
		
		RuleDefinitionList ruleDefinitionList = new RuleDefinitionList();
		
		HandleAccessor.receiveContent(ruleDefinitionList, services.match(docQuery, start, candidateRules));
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
		HandleAccessor.receiveContent(ruleDefinitionList,services.match(docIds, candidateRules));
		return ruleDefinitionList;
	}

	@Override
	public RuleDefinitionList match(
			StructureWriteHandle document) {
		return match(document, new String[] {});
	}

	@Override
	public RuleDefinitionList match(
			StructureWriteHandle document, String[] candidateRules) {
		RuleDefinitionList ruleDefinitionList = new RuleDefinitionList();
		HandleAccessor.receiveContent(ruleDefinitionList,services.match(document, candidateRules));
		return ruleDefinitionList;
	}

}
