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
	// If price is 1000, it's expected that the TestE2ENumberOfConcurrentUsers test is being run, in which case we need
	// to sleep for a little to ensure that multiple requests from the same user do not succeed. This unfortunately
	// is not 100% reliable and the associated test may intermittently fail.
	xdmp.sleep(2000);
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
