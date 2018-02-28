function transform_function(context, params, content) {
  var uri = context.uri;
  var document = content.toObject();
  document.testProperty = params.newValue;
  xdmp.documentInsert(uri, document, [xdmp.permission("rest-reader","read"), xdmp.permission("rest-writer","update")]);
  return uri;
};
exports.transform = transform_function;
