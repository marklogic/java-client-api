/*
 * Copyright 2012-2015 MarkLogic Corporation
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
 * A module is stored at a path, starting with "/ext/" in the modules database.
 * This class wraps this path plus any permissions assigned to the module.
 */
public class ExtensionLibraryDescriptor {

	/**
	 * Wraps assigned permissions for an extension into a Java object.
	 * A permission has a role name and a capability ("read", "write", or "execute").
	 */
	public static class Permission {
		private String roleName;
		private String capability;	
		/**
		 * gets the role name for this permission.
		 * @return the role name
		 */
		public String getRoleName() {
			return roleName;
		}
		
		/**
		 * sets the role name for this permission
		 * @param roleName the role name
		 */
		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}
		
		/**
		 * gets the capability of this permission
		 * @return the capability, as a String
		 */
		public String getCapability() {
			return capability;
		}
		/**
		 * sets the capability for this permission
		 * @param capability
		 */
		public void setCapability(String capability) {
			this.capability = capability;
		}
	}
	
	private String path;
	private List<Permission> permissions;
	
	/**
	 * No-argument constructor.
	 */
	public ExtensionLibraryDescriptor() {
		this.permissions = new ArrayList<Permission>();
	}
	
	/**
	 * gets the path of this module/asset
	 * @return The path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path for this module descriptor.
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
	 * adds a permission to this module
	 * @param roleName the role name to which the permission applies
	 * @param capability the capability of the permission.
	 */
	public void addPermission(String roleName, String capability) {
		Permission permission = new Permission();
		permission.setRoleName(roleName);
		permission.setCapability(capability);
		this.permissions.add(permission);
	}
	
	/**
	 * gets the list of permissions assigned to this module, beyond the default permissions.
	 * @return a List of Permission objects.
	 */
	public List<Permission> getPermissions() {
		return this.permissions;
	}
}
