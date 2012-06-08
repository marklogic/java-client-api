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
