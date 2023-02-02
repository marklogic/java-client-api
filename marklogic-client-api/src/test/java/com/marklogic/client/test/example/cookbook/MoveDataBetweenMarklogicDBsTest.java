/*
 * Copyright (c) 2023 MarkLogic Corporation
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
package com.marklogic.client.test.example.cookbook;

import com.marklogic.client.example.cookbook.datamovement.MoveDataBetweenMarklogicDBs;
import org.junit.jupiter.api.Test;

public class MoveDataBetweenMarklogicDBsTest {
  @Test
  public void testMain() throws Exception {
    MoveDataBetweenMarklogicDBs.main(new String[0]);
  }
}
