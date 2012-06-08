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

import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;

public interface ResourceExtensionsManager {
    public <T extends StructureReadHandle> T listServices(T listHandle);

    public <T extends TextReadHandle> T readServices(String resourceName, T sourceHandle);

    public void writeServices(String resourceName, TextWriteHandle sourceHandle);
    public void writeServices(String resourceName, TextWriteHandle sourceHandle, ExtensionMetadata metadata);
    public void writeServices(String resourceName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, MethodParameters... methodParams);

    public void deleteServices(String resourceName);

    public void startLogging(RequestLogger logger);
    public void stopLogging();

    public class MethodParameters extends RequestParameters {
        private MethodType method;

        public MethodParameters(MethodType method) {
        	super();
        	this.method = method;
        }

        public MethodType getMethod() {
        	return method;
        }

        @Override
		public int hashCode() {
			return getMethod().hashCode();
		}
		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (!(other instanceof MethodParameters))
				return false;

			MethodParameters otherParam = (MethodParameters) other; 
			if (!getMethod().equals(otherParam.getMethod()))
				return false;

			return super.equals(otherParam);
		}
    }
}
