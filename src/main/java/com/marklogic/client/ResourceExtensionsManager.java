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
