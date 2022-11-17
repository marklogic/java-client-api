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
'use strict';
// declareUpdate(); // Note: uncomment if changing the database state
var errCode; // string
var errMsg;  // string?

if (errCode == void 0) {
  errCode = 418;
  errMsg = 'Status Message For 418';
}

fn.error(null, errCode, errMsg);

const returnValue = "Error should be thrown before execution reaches this line";
returnValue;
