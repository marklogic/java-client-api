function transform_function(context, params, content) {
  xdmp.log(Object.prototype.toString.call(content));
  var document = content.toObject();
  document.c = params.newValue;
  return document;
};
exports.transform = transform_function;
