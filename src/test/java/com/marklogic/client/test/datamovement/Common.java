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
package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

public class Common {
  final public static String USERNAME = "rest-writer";
  final public static String PASSWORD = "x";
  final public static String ADMIN_USERNAME = "rest-admin";
  final public static String ADMIN_PASSWORD = "x";
  final public static String EVAL_USERNAME = "rest-evaluator";
  final public static String EVAL_PASSWORD = "x";
  final public static String HOST     = "localhost";
  final public static int    PORT     = 8012;

  public static DatabaseClient client;
  public static DatabaseClient connect() {
    if (client != null) {
      client.release();
      client = null;
    }
    client = newClient();
    return client;
  }

  public static DatabaseClient connectAdmin() {
    client = newAdminClient();
    return client;
  }
  public static DatabaseClient connectEval() {
    client = newEvalClient();
    return client;
  }
  public static DatabaseClient newClient() {
    return DatabaseClientFactory.newClient(
        Common.HOST, Common.PORT, Common.USERNAME, Common.PASSWORD, Authentication.DIGEST
        );
  }
  public static DatabaseClient newEvalClient() {
    return DatabaseClientFactory.newClient(
        Common.HOST, Common.PORT, Common.EVAL_USERNAME, Common.EVAL_PASSWORD, Authentication.DIGEST
        );
  }
  public static DatabaseClient newAdminClient() {
    return DatabaseClientFactory.newClient(
        Common.HOST, Common.PORT, Common.ADMIN_USERNAME, Common.ADMIN_PASSWORD, Authentication.DIGEST
        );
  }
  public static void release() {
    client = null;
  }

}

