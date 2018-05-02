function ImportTest_transform_function(content, context) {
  var document = content.value.toObject();
  /* can't do this yet because of bug 37763
  var params = JSON.parse(context.transform_param);
  */
  // temporary work-around
  var params = { newValue: context.transform_param };
  document.testProperty = params.newValue;
  content.value = xdmp.unquote(xdmp.quote(document));
  return content;
};
exports.ImportTest_transform_function = ImportTest_transform_function;
