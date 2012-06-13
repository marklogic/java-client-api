/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client;

/**
 * The ServerConfigurationManager reads and writes the configurable properties
 * of the server.
 */
public interface ServerConfigurationManager {
	public enum Policy {
		REQUIRED, OPTIONAL, NONE;
	}

	/**
	 * Reads the values of the properties from the server into this object.
	 */
	public void readConfiguration();
	/**
	 * Writes the values of the properties of this object to the server.
	 */
	public void writeConfiguration();

	public Boolean getQueryOptionValidation();
	/**
	 * Specifies whether the server validates query options before storing them.
	 * @param on true to validate the query options
	 */
	public void setQueryOptionValidation(Boolean on);

	public String getDefaultDocumentReadTransform();
	public void   setDefaultDocumentReadTransform(String name);

	// for debug logging on the server side
	public Boolean getServerRequestLogging();
	public void setServerRequestLogging(Boolean on);

	// conditional requests based on the version
	public Policy getContentVersionRequests();
	public void setContentVersionRequests(Policy policy);

	public QueryOptionsManager        newQueryOptionsManager();
    public NamespacesManager          newNamespacesManager();
	public ResourceExtensionsManager  newResourceExtensionsManager();
	public TransformExtensionsManager newTransformExtensionsManager();
}
