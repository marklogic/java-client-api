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

/**
 * Identifies a module in the REST server's modules database.
 */
public class ExtensionLibraryDescriptor {

	private String path;
	
	/**
	 * Gets the path of this module/asset
	 * 
	 * @return The path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the patch for this module descriptor
	 * 
	 * @param path
	 *            The path. Must begin with "/ext/"
	 */
	public void setPath(String path) {
		if (!path.startsWith("/ext/")) {
			throw new IllegalArgumentException("Module paths must begin with '/ext/'");
		}
		else {
			this.path = path;
		}
	}
}
