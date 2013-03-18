/*
 * Copyright 2012-2013 MarkLogic Corporation
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
package com.marklogic.client.admin;

import com.marklogic.client.io.Format;

/**
 * The ServerConfigurationManager reads and writes the configurable properties
 * of the server.
 */
public interface ServerConfigurationManager {
	/**
	 * Specifies the policy for use of a capability. 
	 */
	public enum Policy {
		/**
		 * Specifies that the capability must be used.
		 */
		REQUIRED,
		/**
		 * Specifies that the capability may be used.
		 */
		OPTIONAL,
		/**
		 * Specifies that the capability may not be used.
		 */
		NONE;
	}

	/**
	 * Reads the values of the properties from the server into this object.
	 */
	public void readConfiguration();
	/**
	 * Writes the values of the properties of this object to the server.
	 */
	public void writeConfiguration();

	/**
	 * Returns whether the server validates query options before storing them.
	 * @return	true if query options are validated
	 */
	public Boolean getQueryOptionValidation();
	/**
	 * Specifies whether the server validates query options before storing them.
	 * @param on true to validate the query options
	 */
	public void setQueryOptionValidation(Boolean on);

	/**
	 * Returns the name of the default transform for reading documents.
	 * @return	the default transform name
	 */
	public String getDefaultDocumentReadTransform();
	/**
	 * Specifies the name of the default transform for reading documents.
	 * The default transform is applied before any transform specified
	 * on the read request.
	 * @param name	the default transform name
	 */
	public void   setDefaultDocumentReadTransform(String name);

	/**
	 * Returns whether the server logs requests to the error log on the server.
	 * @return	true if the server logs requests
	 */
	public Boolean getServerRequestLogging();
	/**
	 * Specifies whether the server logs requests to the error log on the server.
	 * @param on	true to log requests on the server
	 */
	public void setServerRequestLogging(Boolean on);

	/**
	 * Returns whether the server requires, allows, or ignores document versionss
	 * on document read, write, and delete requests.
	 * @return	the policy as required, optional, or none
	 */
	public Policy getContentVersionRequests();
	/**
	 * Specifies whether the server requires, allows, or ignores document versionss
	 * on document read, write, and delete requests.
	 * @param policy	required, optional, or none for document versions
	 */
	public void setContentVersionRequests(Policy policy);

	/**
	 * Creates a manager for listing, reading, writing, and deleting query options.
	 * @return	a new manager for query options
	 */
	public QueryOptionsManager        newQueryOptionsManager();
	/**
	 * Creates a manager for listing, reading, writing, and deleting
	 * namespace bindings.
	 * @return	a new manager for namespace bindings
	 */
    public NamespacesManager newNamespacesManager();
	/**
	 * Creates a manager for listing, reading, writing, and deleting
	 * resource service extensions.
	 * @return	a new manager for resource service extensions
	 */
	public ResourceExtensionsManager  newResourceExtensionsManager();
	/**
	 * Creates a manager for listing, reading, writing, and deleting
	 * transform extensions.
	 * @return	a new manager for transform extensions
	 */
	public TransformExtensionsManager newTransformExtensionsManager();
	/**
	 * Sets the serialization format of MarkLogic errors. 
	 * The Java API only parses errors reported in XML.  JavaScript clients generally use JSON.
	 * While one REST server cannot support both kinds of clients, 
	 * two REST servers can share one database.
	 * @param errorFormat either Format.JSON or Format.XML
	 */
	public void setErrorFormat(Format errorFormat);
	/**
	 * Returns the configured error format for the REST server instance.  Transparent to the Java API itself.
	 * @return either Format.JSON or Format.XML
	 */
	public Format getErrorFormat();
	
	/**
	 * Creates a manager for writing and reading assets in the REST
	 * server's modules database.
	 * @return The extensions manager.
	 */
	public ExtensionLibrariesManager newExtensionLibrariesManager();
}
