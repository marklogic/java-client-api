'use strict';
function secondsMapper(result) {
    result.seconds = new Date().getSeconds();
    return result;
}
module.exports = {
    secondsMapper: secondsMapper
};
