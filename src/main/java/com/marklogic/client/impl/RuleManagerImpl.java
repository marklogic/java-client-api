/*
 * Copyright 2013-2015 MarkLogic Corporation
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
import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.alerting.RuleDefinition;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.RuleListReadHandle;
import com.marklogic.client.io.marker.RuleReadHandle;
import com.marklogic.client.io.marker.RuleWriteHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;

public class RuleManagerImpl
	extends AbstractLoggingManager
	implements RuleManager
{

	final static private String RULES_BASE = "/alert/rules";

	private RESTServices          services;
	private HandleFactoryRegistry handleRegistry;

	public RuleManagerImpl(RESTServices services) {
		this.services = services;
	}

	public HandleFactoryRegistry getHandleRegistry() {
		return handleRegistry;
	}
	public void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
		this.handleRegistry = handleRegistry;
	}

	@Override
	public boolean exists(String ruleName) {
		return services.exists(RULES_BASE + "/" + ruleName);
	}

	@Override
	public <T> T readRuleAs(String ruleName, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!RuleReadHandle.class.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used to read rule as "+as.getName()
					);
		}

		readRule(ruleName, (RuleReadHandle) handle);

		return handle.get();
	}
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends RuleReadHandle> T readRule(String ruleName, T ruleHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
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
	public void writeRuleAs(String ruleName, Object ruleSource)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (ruleSource == null) {
			throw new IllegalArgumentException("no source to write");
		}

		Class<?> as = ruleSource.getClass();

		RuleWriteHandle sourceHandle = null;
		if (RuleWriteHandle.class.isAssignableFrom(as)) {
			sourceHandle = (RuleWriteHandle) ruleSource;
		} else {
			ContentHandle<?> handle = getHandleRegistry().makeHandle(as);
			if (!RuleWriteHandle.class.isAssignableFrom(handle.getClass())) {
				throw new IllegalArgumentException(
						"Handle "+handle.getClass().getName()+
						" cannot be used to write rule source as "+as.getName()
						);
			}
			Utilities.setHandleContent(handle, ruleSource);
			sourceHandle = (RuleWriteHandle) handle;
		}

		writeRuleAs(ruleName, sourceHandle);
	}
	@Override
	public void writeRule(RuleDefinition ruleHandle) {
		String ruleName = ruleHandle.getName();
		writeRule(ruleName, ruleHandle);
	}
	@SuppressWarnings("rawtypes")
	@Override
	public void writeRule(String ruleName, RuleWriteHandle writeHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (writeHandle instanceof RuleDefinition) {
			String name = ((RuleDefinition) writeHandle).getName();
			if (name != null)
				ruleName = name;
		}
		HandleImplementation ruleBase = HandleAccessor.checkHandle(writeHandle,
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

		services.putValue(requestLogger, RULES_BASE, ruleName, mimetype, ruleBase);
	}

	@Override
	public void delete(String ruleName) throws ForbiddenUserException,
			FailedRequestException {
		services.deleteValue(null, RULES_BASE, ruleName);
	}

	@Override
	public <T extends RuleListReadHandle> T match(QueryDefinition docQuery, T ruleListHandle) {
		return match(docQuery, 1, QueryManager.DEFAULT_PAGE_LENGTH, new String[] {}, ruleListHandle);
	}
	@Override
	public <T extends RuleListReadHandle> T match(QueryDefinition docQuery, long start,
			long pageLength, String[] candidateRules, T ruleListHandle) {
		return match(docQuery, start, pageLength, candidateRules, ruleListHandle, null);
	}
	@Override
	public <T extends RuleListReadHandle> T match(QueryDefinition docQuery, long start,
			long pageLength, String[] candidateRules, T ruleListHandle, ServerTransform transform) {

		HandleAccessor.receiveContent(ruleListHandle,
				services.match(docQuery, start, pageLength, candidateRules, transform));
		;
		return ruleListHandle;
	}

	@Override
	public <T extends RuleListReadHandle> T match(String[] docIds, T ruleListHandle) {
		return match(docIds, new String[] {}, ruleListHandle);
	}
	@Override
	public <T extends RuleListReadHandle> T match(String[] docIds, String[] candidateRules, T ruleListHandle) {
		return match(docIds, candidateRules, ruleListHandle, null);
	}
	@Override
	public <T extends RuleListReadHandle> T match(String[] docIds, String[] candidateRules, T ruleListHandle, ServerTransform transform) {
		HandleAccessor.receiveContent(ruleListHandle,
				services.match(docIds, candidateRules, transform));
		return ruleListHandle;
	}

	@Override
	public <T extends RuleListReadHandle> T matchAs(Object content, T ruleListHandle) {
		return matchAs(content, null, ruleListHandle, null);
	}
	@Override
	public <T extends RuleListReadHandle> T matchAs(Object content, String[] candidateRules, T ruleListHandle) {
		return matchAs(content, candidateRules, ruleListHandle, null);
	}
	@Override
	public <T extends RuleListReadHandle> T matchAs(Object content,
			String[] candidateRules, T ruleListHandle, ServerTransform transform) {
		if (content == null) {
			throw new IllegalArgumentException("no content for matching rules");
		}

		Class<?> as = content.getClass();

		StructureWriteHandle matchHandle = null;
		if (StructureWriteHandle.class.isAssignableFrom(as)) {
			matchHandle = (StructureWriteHandle) content;
		} else {
			ContentHandle<?> handle = getHandleRegistry().makeHandle(as);
			if (!StructureWriteHandle.class.isAssignableFrom(handle.getClass())) {
				throw new IllegalArgumentException(
						"Handle "+handle.getClass().getName()+
						" cannot be used to match rules as "+as.getName()
						);
			}
			Utilities.setHandleContent(handle, content);
			matchHandle = (StructureWriteHandle) handle;
		}

		return match(matchHandle, candidateRules, ruleListHandle, transform);
	}
	@Override
	public <T extends RuleListReadHandle> T match(StructureWriteHandle document, T ruleListHandle) {
		return match(document, new String[] {}, ruleListHandle);
	}
	@Override
	public <T extends RuleListReadHandle> T match(StructureWriteHandle document,
			String[] candidateRules, T ruleListHandle) {
		return match(document, candidateRules, ruleListHandle, null);
	}
	@SuppressWarnings("rawtypes")
	@Override
	public <T extends RuleListReadHandle> T match(StructureWriteHandle document,
			String[] candidateRules, T ruleListHandle, ServerTransform transform) {
		
		HandleImplementation searchBase = HandleAccessor.checkHandle(document, "match");
		
		Format searchFormat = searchBase.getFormat();
        switch(searchFormat) {
        case UNKNOWN:
        	searchFormat = Format.XML;
        	break;
        case JSON:
        case XML:
        	break;
        default:
            throw new UnsupportedOperationException("Only XML and JSON queries can filter for rule matches.");
        }

        String mimeType = searchFormat.getDefaultMimetype();

		
		HandleAccessor.receiveContent(ruleListHandle,
				services.match(document, candidateRules, mimeType, transform));
		return ruleListHandle;
	}

}
