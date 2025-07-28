/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.extensions;

import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.impl.ResourceManagerImplementation;

/**
 * ResourceManager is the base class for a client interface
 * to resource services.  Resource Service extensions can be
 * installed on the server using {@link com.marklogic.client.admin.ResourceExtensionsManager}.
 * Initialize a ResourceManager object by passing it to the
 * {@link com.marklogic.client.DatabaseClient}.init() method.
 *
 * <p>To expose the services provided by a resource service extension to
 * applications, implement a subclass of ResourceManager. In your subclass, use
 * the methods of a {@link ResourceServices} object to call the Resource Services
 * on the server.</p>
 *
 * <p>Obtain a {@link ResourceServices} object by calling the protected
 * <code>getServices</code> method of the ResourceManager. This method
 * has the following signature:</p>
 *
 * <p><code>{@link ResourceServices} getServices()</code></p>
 */
abstract public class ResourceManager
  extends ResourceManagerImplementation
{
  protected ResourceManager() {
    super();
  }
  /**
   * Returns the name of the resource.
   * @return	the name of the resource
   */
  public String getName() {
    ResourceServices services = getServices();
    return (services != null) ? services.getResourceName() : null;
  }

  /**
   * Starts debugging client requests. You can suspend and resume debugging output
   * using the methods of the logger.
   *
   * @param logger	the logger that receives debugging output
   */
  public void startLogging(RequestLogger logger) {
    ResourceServices services = getServices();
    if (services != null)
      services.startLogging(logger);
  }
  /**
   *  Stops debugging client requests.
   */
  public void stopLogging() {
    ResourceServices services = getServices();
    if (services != null)
      services.stopLogging();
  }
}
