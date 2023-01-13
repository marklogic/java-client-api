/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerConfigurationManagerTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connectRestAdmin();
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testSetGet()
    throws IOException, FailedRequestException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException
  {
    ServerConfigurationManager initialServerConfig  = Common.restAdminClient.newServerConfigManager();

    assertNull( initialServerConfig.getQueryOptionValidation());

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

    ServerConfigurationManager modifiedServerConfig = Common.restAdminClient.newServerConfigManager();
    modifiedServerConfig.setQueryValidation(modQueryValid);
    modifiedServerConfig.setQueryOptionValidation(modOptionValid);
    modifiedServerConfig.setDefaultDocumentReadTransform(modReadTrans);
    modifiedServerConfig.setDefaultDocumentReadTransformAll(modReadTransAll);
    modifiedServerConfig.setServerRequestLogging(modRequestLog);
    modifiedServerConfig.setUpdatePolicy(modVersionReq);
    modifiedServerConfig.writeConfiguration();

    Common.propertyWait();

    ServerConfigurationManager refreshedServerConfig = Common.restAdminClient.newServerConfigManager();
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

    assertEquals(            modQueryValid,   refreshQueryValid);
    assertEquals(    modOptionValid,  refreshOptionValid);
    assertEquals(     modReadTrans,    refreshReadTrans);
    assertEquals( modReadTransAll, refreshReadTransAll);
    assertEquals(      modRequestLog,   refreshRequestLog);
    assertEquals(              modVersionReq,   refreshVersionReq);

    Common.propertyWait();
  }

}
