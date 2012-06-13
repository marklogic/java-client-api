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

import com.marklogic.client.impl.ResourceManagerImplementation;

/**
 * ResourceManager is the base class for a client interface
 * to resource services.  Resource Service extensions can be
 * installed on the server using {@link com.marklogic.client.ResourceExtensionsManager}.
 * Initialize a ResourceManager object by passing it to the
 * {@link com.marklogic.client.DatabaseClient}.init() method.
 * Internally, a ResourceManager calls the Resource Services
 * on the server using the methods of the 
 * {@link com.marklogic.client.ResourceServices} object provided
 * by the init() method of the DatabaseClient. 
 */
abstract public class ResourceManager
    extends ResourceManagerImplementation
{
	protected ResourceManager() {
		super();
	}
	public String getName() {
		ResourceServices services = getServices();
		return (services != null) ? services.getResourceName() : null;
	}
    public void startLogging(RequestLogger logger) {
		ResourceServices services = getServices();
    	if (services != null)
    		services.startLogging(logger);
    }
    public void stopLogging() {
		ResourceServices services = getServices();
    	if (services != null)
    		services.stopLogging();
    }
}
