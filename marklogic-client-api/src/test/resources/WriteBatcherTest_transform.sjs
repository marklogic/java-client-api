function transform_function(context, params, content) {
  var document = content.toObject();
  document.testProperty = params.newValue;
  return document;
};
exports.transform = transform_function;
