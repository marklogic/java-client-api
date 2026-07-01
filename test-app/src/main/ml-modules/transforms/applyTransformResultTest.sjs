// Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.

function transform_function(context, params, content) {
  var document = content.toObject();
  document.c = params.newValue;

  if (params.markerUri) {
    xdmp.documentInsert(params.markerUri, {
      sourceUri: context.uri,
      transformedValue: params.newValue
    });
  }

  return document;
}

exports.transform = transform_function;
