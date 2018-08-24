/*
 * Copyright 2012-2018 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;

public class ServerConfigurationManagerTest {
  @BeforeClass
  public static void beforeClass() {
    Common.connectAdmin();
  }
  @AfterClass
  public static void afterClass() {
  }

  @Test
  public void testSetGet()
    throws IOException, FailedRequestException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException
  {
    ServerConfigurationManager initialServerConfig  = Common.adminClient.newServerConfigManager();

    assertNull("Initial query option validation not null", initialServerConfig.getQueryOptionValidation());

    initialServerConfig.readConfiguration();

    Boolean initialQueryValid      = initialServerConfig.getQueryValidation();
    Boolean initialOptionValid     = initialServerConfig.getQueryOptionValidation();
    String  initialReadTrans       = initialServerConfig.getDefaultDocumentReadTransform();
    Boolean initialReadTransAll    = initialServerConfig.getDefaultDocumentReadTransformAll();
    Boolean initialRequestLog      = initialServerConfig.getServerRequestLogging();
    UpdatePolicy initialVersionReq = initialServerConfig.getUpdatePolicy();

    Boolean      modQueryValid   = !(initialQueryValid == true);
    Boolean      modOptionValid  = !(initialOptionValid == true);
    String       modReadTrans    = "modifiedReadTransform";
    Boolean      modReadTransAll = !(initialReadTransAll == true);
    Boolean      modRequestLog   = !(initialOptionValid == true);
    UpdatePolicy modVersionReq   = (initialVersionReq == UpdatePolicy.VERSION_OPTIONAL) ?
                                   UpdatePolicy.VERSION_REQUIRED : UpdatePolicy.VERSION_OPTIONAL;

    ServerConfigurationManager modifiedServerConfig = Common.adminClient.newServerConfigManager();
    modifiedServerConfig.setQueryValidation(modQueryValid);
    modifiedServerConfig.setQueryOptionValidation(modOptionValid);
    modifiedServerConfig.setDefaultDocumentReadTransform(modReadTrans);
    modifiedServerConfig.setDefaultDocumentReadTransformAll(modReadTransAll);
    modifiedServerConfig.setServerRequestLogging(modRequestLog);
    modifiedServerConfig.setUpdatePolicy(modVersionReq);
    modifiedServerConfig.writeConfiguration();

    Common.propertyWait();

    ServerConfigurationManager refreshedServerConfig = Common.adminClient.newServerConfigManager();
    refreshedServerConfig.readConfiguration();

    Boolean refreshQueryValid      = refreshedServerConfig.getQueryValidation();
    Boolean refreshOptionValid     = refreshedServerConfig.getQueryOptionValidation();
    String  refreshReadTrans       = refreshedServerConfig.getDefaultDocumentReadTransform();
    Boolean refreshReadTransAll    = refreshedServerConfig.getDefaultDocumentReadTransformAll();
    Boolean refreshRequestLog      = refreshedServerConfig.getServerRequestLogging();
    UpdatePolicy refreshVersionReq = refreshedServerConfig.getUpdatePolicy();

    // restore the initial settings for the sake of other tests
    initialServerConfig.setQueryValidation(initialQueryValid);
    initialServerConfig.setQueryOptionValidation(initialOptionValid);
    initialServerConfig.setDefaultDocumentReadTransform(initialReadTrans);
    initialServerConfig.setDefaultDocumentReadTransformAll(initialReadTransAll);
    initialServerConfig.setServerRequestLogging(initialRequestLog);
    initialServerConfig.setUpdatePolicy(initialVersionReq);
    initialServerConfig.writeConfiguration();

    assertEquals("Failed to change query validation",            modQueryValid,   refreshQueryValid);
    assertEquals("Failed to change query options validation",    modOptionValid,  refreshOptionValid);
    assertEquals("Failed to change document read transform",     modReadTrans,    refreshReadTrans);
    assertEquals("Failed to change document read transform all", modReadTransAll, refreshReadTransAll);
    assertEquals("Failed to change server request logging",      modRequestLog,   refreshRequestLog);
    assertEquals("Failed to change update policy ",              modVersionReq,   refreshVersionReq);

    Common.propertyWait();
  }

}
