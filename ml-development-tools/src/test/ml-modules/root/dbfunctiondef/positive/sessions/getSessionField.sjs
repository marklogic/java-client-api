'use strict';
var fieldName;
var fieldValue;

const timestamp = fn.head(xdmp.getSessionField(xs.string(fieldName)));

// console.log(Object.prototype.toString.call(fieldValue));
// console.log(Object.prototype.toString.call(timestamp));
// console.log('get '+fieldName+': '+timestamp+'='+fieldValue);

const result = (String(timestamp) == String(fieldValue));
result;
