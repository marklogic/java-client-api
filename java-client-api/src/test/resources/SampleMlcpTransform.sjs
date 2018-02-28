var mem = require('/ext/memory-operations.xqy');

function transform(content, context) {
  var params = { value: "UNDEFINED" };
  if ( context.transform_param != null && context.transform_param.length > 0 ) {
    params = fn.head(xdmp.fromJsonString(context.transform_param));
  }
  var value = params.value;
  if ( content.value instanceof Document && content.value.root instanceof Element ) {
    var transformedAttribute =
      new NodeBuilder()
        .addAttribute("my:transformed",value,"http://marklogic.com/rest-api/test/transform")
          .toNode();
    content.value = mem.insertChild(content.value.root, transformedAttribute);
  }
  return content;
};

exports.transform = transform;
