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
