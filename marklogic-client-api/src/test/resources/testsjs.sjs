var mem = require('/ext/memory-operations.xqy');

function transform(context, params, content) {
  var value = params.value;
  if ( value == null ) value = "UNDEFINED";
  if ( content instanceof Document && content.root instanceof Element ) {
    var transformedAttribute =
      new NodeBuilder()
        .addAttribute("my:transformed",value,"http://marklogic.com/rest-api/test/transform")
          .toNode();
    return mem.insertChild(content.root, transformedAttribute);
  } else {
    return content;
  }
};

exports.transform = transform;
