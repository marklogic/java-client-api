/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
