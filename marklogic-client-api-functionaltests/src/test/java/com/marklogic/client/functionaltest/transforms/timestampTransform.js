/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

function timestampTransform(context, params, document) {
  var newDoc = document.toObject();
  newDoc.timestamp = fn.currentDateTime();
  newDoc.userName = xdmp.getCurrentUser();
  return newDoc;
}

module.exports = {
  transform: timestampTransform
};
