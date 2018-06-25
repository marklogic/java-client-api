'use strict';
var fieldName;

const timestamp = xdmp.wallclockToTimestamp(fn.currentDateTime());
const result    = fn.head(xdmp.setSessionField(xs.string(fieldName), timestamp));

// console.log('set '+fieldName+': '+fn.head(xdmp.getSessionField(xs.string(fieldName))));

result;
