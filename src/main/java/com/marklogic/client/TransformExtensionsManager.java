package com.marklogic.client;

import java.util.Map;

import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

public interface TransformExtensionsManager {
	public <T extends StructureReadHandle> T listTransforms(T listHandle);

	public <T extends TextReadHandle> T readXQueryTransform(String transformName, T sourceHandle);
    public <T extends XMLReadHandle> T readXSLTransform(String transformName, T sourceHandle);

    public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle);
    public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata);
    public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String,String> paramTypes);

    public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle);
    public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata);
    public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String,String> paramTypes);

    public void deleteTransform(String transformName);

    public void startLogging(RequestLogger logger);
    public void stopLogging();
}
