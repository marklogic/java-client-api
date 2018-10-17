function TestE2EItemPriceErrorCond(itemCnt, price) {
var retFloatPriceValue;

if (itemCnt == null && price != null) {
// Return a valid datatype back to client so that we know null input parameter works
retFloatPriceValue = 10000.00;
return encodeURI(retFloatPriceValue);
}
else if (itemCnt != null && price == null ) {
// Return a valid datatype back to client so that we know null input parameter works
retFloatPriceValue = 20000.00;
return encodeURI(retFloatPriceValue);
}
else if (itemCnt == null && price == null ) {
// Return a valid datatype back to client so that we know null input parameter works
retFloatPriceValue = 35000.00;
return encodeURI(retFloatPriceValue);
}

else if (itemCnt == 1000 && price == 1000 ) {
// Simulate a long running module so that multiple users test gets a Forbidden user exception. Return sleep period back to caller.
var i;
for (i = 0; i < 10000000; i++) {
}
retFloatPriceValue = 10000.00;
return encodeURI(retFloatPriceValue);
}
else {

var itemNo = parseInt(itemCnt, 10);
var price = parseFloat(price, 10);

retFloatPriceValue = itemNo * price * 100.50;
}

return encodeURI(retFloatPriceValue);
}

var t = TestE2EItemPriceErrorCond(xdmp.getRequestField("items"), xdmp.getRequestField("price"));
t;
