// module that exports get, post, put and delete 
function getMethod(context, params) { 
    //context.outputTypes = ["application/json"];
    //return { "response": "This is a JSON document" }
    return fn.normalizeSpace(" hello  " + params["planet"]);
};

function postMethod(context, params, input) {
};

function putMethod(context, params, input) {
};

function deleteMethod(context, params) {
};

exports.GET = getMethod;
exports.POST = postMethod;
exports.PUT = putMethod;
exports.DELETE = deleteMethod;

