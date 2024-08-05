/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
     * @param capability the capability for this permission
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
    this.permissions = new ArrayList<>();
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
