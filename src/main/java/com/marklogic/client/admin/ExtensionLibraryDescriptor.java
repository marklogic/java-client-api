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

import java.util.ArrayList;
import java.util.List;

/**
 * Identifies a module in the REST server's modules database.
 */
public class ExtensionLibraryDescriptor {

	public class Permission {
		public String getRoleName() {
			return roleName;
		}
		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}
		public String getCapability() {
			return capability;
		}
		public void setCapability(String capability) {
			this.capability = capability;
		}
		private String roleName;
		private String capability;	
	}
	
	private String path;
	private List<Permission> permissions;
	
	public ExtensionLibraryDescriptor() {
		this.permissions = new ArrayList<Permission>();
	}
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
	
	/**
	 * Add a permission to this module
	 * 
	 */
	public void addPermission(String roleName, String capability) {
		Permission permission = new Permission();
		permission.setRoleName(roleName);
		permission.setCapability(capability);
		this.permissions.add(permission);
	}
	
	public List<Permission> getPermissions() {
		return this.permissions;
	}
}
