function TestE2EItemPrice(inputIntegertVaue) {
var retDoubleValue;

if (inputIntegertVaue == null) {
// Return a valid datatype back to client so that we know null input parameter works
retDoubleValue = 55555.00;
return encodeURI(retDoubleValue);
}

console.debug("Inside CLIENT API inputIntegertVaue %s", inputIntegertVaue);
var inputInt = parseInt(inputIntegertVaue, 10);


if (inputInt == 10) {
// Return invalid datatype when input is 10
retDoubleValue = "String10";
}
else if (inputInt == 1000) {
// Return null when input is 1000
retDoubleValue = null;
}
else {
retDoubleValue = inputInt + 1.00;
}
console.debug("Return from CLIENT API new double value is : %s", retDoubleValue);
return encodeURI(retDoubleValue);
}

var t = TestE2EItemPrice(xdmp.getRequestField("itemId"));
t;
