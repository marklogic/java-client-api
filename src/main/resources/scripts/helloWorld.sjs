// module that exports get, post, put and delete 
function getMethod(context, params) { 
    //context.outputTypes = ["application/json"];
    //return { "response": "This is a JSON document" }
    return "hello " + params["planet"];
};

function postMethod(context, params, input) {
};

function putMethod(context, params, input) {
};

function deleteMethod(context, params) {
};

exports.get = getMethod;
exports.post = postMethod;
exports.put = putMethod;
exports.delete = deleteMethod;

