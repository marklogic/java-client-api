/*
 * Copyright 2016-2017 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl;

import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.MapMapExpr;
import com.marklogic.client.type.NodeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsAnyURIExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsDateExpr;
import com.marklogic.client.type.XsDateTimeExpr;
import com.marklogic.client.type.XsIntegerExpr;
import com.marklogic.client.type.XsLongExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsNumericSeqExpr;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsUnsignedIntExpr;
import com.marklogic.client.type.XsUnsignedLongExpr;



import com.marklogic.client.expression.XdmpExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class XdmpExprImpl implements XdmpExpr {

    final static XsExprImpl xs = XsExprImpl.xs;

    final static XdmpExprImpl xdmp = new XdmpExprImpl();

    XdmpExprImpl() {
    }

    
    @Override
    public XsUnsignedLongExpr add64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "add64", new Object[]{ x, y });
    }

    
    @Override
    public XsUnsignedLongExpr and64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "and64", new Object[]{ x, y });
    }

    
    @Override
    public XsStringExpr base64Decode(XsStringExpr encoded) {
        return new XsExprImpl.StringCallImpl("xdmp", "base64-decode", new Object[]{ encoded });
    }

    
    @Override
    public XsStringExpr base64Encode(XsStringExpr plaintext) {
        return new XsExprImpl.StringCallImpl("xdmp", "base64-encode", new Object[]{ plaintext });
    }

    
    @Override
    public XsBooleanExpr castableAs(XsStringExpr namespaceUri, String localName, ItemExpr item) {
        return castableAs(namespaceUri, (localName == null) ? (XsStringExpr) null : xs.string(localName), item);
    }

    
    @Override
    public XsBooleanExpr castableAs(XsStringExpr namespaceUri, XsStringExpr localName, ItemExpr item) {
        return new XsExprImpl.BooleanCallImpl("xdmp", "castable-as", new Object[]{ namespaceUri, localName, item });
    }

    
    @Override
    public XsStringExpr crypt(XsStringExpr password, String salt) {
        return crypt(password, (salt == null) ? (XsStringExpr) null : xs.string(salt));
    }

    
    @Override
    public XsStringExpr crypt(XsStringExpr password, XsStringExpr salt) {
        return new XsExprImpl.StringCallImpl("xdmp", "crypt", new Object[]{ password, salt });
    }

    
    @Override
    public XsStringExpr crypt2(XsStringExpr password) {
        return new XsExprImpl.StringCallImpl("xdmp", "crypt2", new Object[]{ password });
    }

    
    @Override
    public XsStringExpr daynameFromDate(XsDateExpr arg) {
        return new XsExprImpl.StringCallImpl("xdmp", "dayname-from-date", new Object[]{ arg });
    }

    
    @Override
    public XsStringExpr decodeFromNCName(XsStringExpr name) {
        return new XsExprImpl.StringCallImpl("xdmp", "decode-from-NCName", new Object[]{ name });
    }

    
    @Override
    public XsStringExpr describe(ItemSeqExpr item) {
        return new XsExprImpl.StringCallImpl("xdmp", "describe", new Object[]{ item });
    }

    
    @Override
    public XsStringExpr describe(ItemSeqExpr item, XsUnsignedIntExpr maxSequenceLength) {
        return new XsExprImpl.StringCallImpl("xdmp", "describe", new Object[]{ item, maxSequenceLength });
    }

    
    @Override
    public XsStringExpr describe(ItemSeqExpr item, XsUnsignedIntExpr maxSequenceLength, XsUnsignedIntExpr maxItemLength) {
        return new XsExprImpl.StringCallImpl("xdmp", "describe", new Object[]{ item, maxSequenceLength, maxItemLength });
    }

    
    @Override
    public XsStringExpr diacriticLess(XsStringExpr string) {
        return new XsExprImpl.StringCallImpl("xdmp", "diacritic-less", new Object[]{ string });
    }

    
    @Override
    public XsStringExpr elementContentType(ElementNodeExpr element) {
        return new XsExprImpl.StringCallImpl("xdmp", "element-content-type", new Object[]{ element });
    }

    
    @Override
    public XsStringExpr encodeForNCName(XsStringExpr name) {
        return new XsExprImpl.StringCallImpl("xdmp", "encode-for-NCName", new Object[]{ name });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericExpr... value) {
        return formatNumber(new XsExprImpl.NumericSeqListImpl(value));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture) {
        return formatNumber(value, (picture == null) ? (XsStringExpr) null : xs.string(picture));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language) {
        return formatNumber(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue) {
        return formatNumber(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (letterValue == null) ? (XsStringExpr) null : xs.string(letterValue));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar) {
        return formatNumber(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (letterValue == null) ? (XsStringExpr) null : xs.string(letterValue), (ordchar == null) ? (XsStringExpr) null : xs.string(ordchar));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding) {
        return formatNumber(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (letterValue == null) ? (XsStringExpr) null : xs.string(letterValue), (ordchar == null) ? (XsStringExpr) null : xs.string(ordchar), (zeroPadding == null) ? (XsStringExpr) null : xs.string(zeroPadding));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar, zeroPadding });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator) {
        return formatNumber(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (letterValue == null) ? (XsStringExpr) null : xs.string(letterValue), (ordchar == null) ? (XsStringExpr) null : xs.string(ordchar), (zeroPadding == null) ? (XsStringExpr) null : xs.string(zeroPadding), (groupingSeparator == null) ? (XsStringExpr) null : xs.string(groupingSeparator));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding, XsStringExpr groupingSeparator) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar, zeroPadding, groupingSeparator });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator, long groupingSize) {
        return formatNumber(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (letterValue == null) ? (XsStringExpr) null : xs.string(letterValue), (ordchar == null) ? (XsStringExpr) null : xs.string(ordchar), (zeroPadding == null) ? (XsStringExpr) null : xs.string(zeroPadding), (groupingSeparator == null) ? (XsStringExpr) null : xs.string(groupingSeparator), xs.integer(groupingSize));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding, XsStringExpr groupingSeparator, XsIntegerExpr groupingSize) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar, zeroPadding, groupingSeparator, groupingSize });
    }

    
    @Override
    public ItemSeqExpr fromJson(NodeExpr arg) {
        return new BaseTypeImpl.ItemSeqCallImpl("xdmp", "from-json", new Object[]{ arg });
    }

    
    @Override
    public XsStringExpr getCurrentUser() {
        return new XsExprImpl.StringCallImpl("xdmp", "get-current-user", new Object[]{  });
    }

    
    @Override
    public XsUnsignedIntExpr hash32(XsStringExpr string) {
        return new XsExprImpl.UnsignedIntCallImpl("xdmp", "hash32", new Object[]{ string });
    }

    
    @Override
    public XsUnsignedLongExpr hash64(XsStringExpr string) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "hash64", new Object[]{ string });
    }

    
    @Override
    public XsIntegerExpr hexToInteger(XsStringExpr hex) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "hex-to-integer", new Object[]{ hex });
    }

    
    @Override
    public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-md5", new Object[]{ secretkey, message });
    }

    
    @Override
    public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message, String encoding) {
        return hmacMd5(secretkey, message, (encoding == null) ? (XsStringExpr) null : xs.string(encoding));
    }

    
    @Override
    public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-md5", new Object[]{ secretkey, message, encoding });
    }

    
    @Override
    public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha1", new Object[]{ secretkey, message });
    }

    
    @Override
    public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message, String encoding) {
        return hmacSha1(secretkey, message, (encoding == null) ? (XsStringExpr) null : xs.string(encoding));
    }

    
    @Override
    public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha1", new Object[]{ secretkey, message, encoding });
    }

    
    @Override
    public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha256", new Object[]{ secretkey, message });
    }

    
    @Override
    public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message, String encoding) {
        return hmacSha256(secretkey, message, (encoding == null) ? (XsStringExpr) null : xs.string(encoding));
    }

    
    @Override
    public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha256", new Object[]{ secretkey, message, encoding });
    }

    
    @Override
    public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha512", new Object[]{ secretkey, message });
    }

    
    @Override
    public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message, String encoding) {
        return hmacSha512(secretkey, message, (encoding == null) ? (XsStringExpr) null : xs.string(encoding));
    }

    
    @Override
    public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha512", new Object[]{ secretkey, message, encoding });
    }

    
    @Override
    public XsStringExpr initcap(XsStringExpr string) {
        return new XsExprImpl.StringCallImpl("xdmp", "initcap", new Object[]{ string });
    }

    
    @Override
    public XsStringExpr integerToHex(XsIntegerExpr val) {
        return new XsExprImpl.StringCallImpl("xdmp", "integer-to-hex", new Object[]{ val });
    }

    
    @Override
    public XsStringExpr integerToOctal(XsIntegerExpr val) {
        return new XsExprImpl.StringCallImpl("xdmp", "integer-to-octal", new Object[]{ val });
    }

    
    @Override
    public XsStringExpr keyFromQName(XsQNameExpr name) {
        return new XsExprImpl.StringCallImpl("xdmp", "key-from-QName", new Object[]{ name });
    }

    
    @Override
    public XsUnsignedLongExpr lshift64(XsUnsignedLongExpr x, long y) {
        return lshift64(x, xs.longVal(y));
    }

    
    @Override
    public XsUnsignedLongExpr lshift64(XsUnsignedLongExpr x, XsLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "lshift64", new Object[]{ x, y });
    }

    
    @Override
    public XsStringExpr md5(ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "md5", new Object[]{ data });
    }

    
    @Override
    public XsStringExpr md5(ItemExpr data, String encoding) {
        return md5(data, (encoding == null) ? (XsStringExpr) null : xs.string(encoding));
    }

    
    @Override
    public XsStringExpr md5(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "md5", new Object[]{ data, encoding });
    }

    
    @Override
    public XsStringExpr monthNameFromDate(XsDateExpr arg) {
        return new XsExprImpl.StringCallImpl("xdmp", "month-name-from-date", new Object[]{ arg });
    }

    
    @Override
    public XsUnsignedLongExpr mul64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "mul64", new Object[]{ x, y });
    }

    
    @Override
    public XsStringSeqExpr nodeCollections(NodeExpr node) {
        return new XsExprImpl.StringSeqCallImpl("xdmp", "node-collections", new Object[]{ node });
    }

    
    @Override
    public XsStringExpr nodeKind(NodeExpr node) {
        return new XsExprImpl.StringCallImpl("xdmp", "node-kind", new Object[]{ node });
    }

    
    @Override
    public MapMapExpr nodeMetadata(NodeExpr node) {
        return new MapExprImpl.MapCallImpl("xdmp", "node-metadata", new Object[]{ node });
    }

    
    @Override
    public XsStringExpr nodeMetadataValue(NodeExpr uri, String keyName) {
        return nodeMetadataValue(uri, (keyName == null) ? (XsStringExpr) null : xs.string(keyName));
    }

    
    @Override
    public XsStringExpr nodeMetadataValue(NodeExpr uri, XsStringExpr keyName) {
        return new XsExprImpl.StringCallImpl("xdmp", "node-metadata-value", new Object[]{ uri, keyName });
    }

    
    @Override
    public ItemSeqExpr nodePermissions(NodeExpr node) {
        return new BaseTypeImpl.ItemSeqCallImpl("xdmp", "node-permissions", new Object[]{ node });
    }

    
    @Override
    public ItemSeqExpr nodePermissions(NodeExpr node, String outputKind) {
        return nodePermissions(node, (outputKind == null) ? (XsStringExpr) null : xs.string(outputKind));
    }

    
    @Override
    public ItemSeqExpr nodePermissions(NodeExpr node, XsStringExpr outputKind) {
        return new BaseTypeImpl.ItemSeqCallImpl("xdmp", "node-permissions", new Object[]{ node, outputKind });
    }

    
    @Override
    public XsStringExpr nodeUri(NodeExpr node) {
        return new XsExprImpl.StringCallImpl("xdmp", "node-uri", new Object[]{ node });
    }

    
    @Override
    public XsUnsignedLongExpr not64(XsUnsignedLongExpr x) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "not64", new Object[]{ x });
    }

    
    @Override
    public XsIntegerExpr octalToInteger(XsStringExpr octal) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "octal-to-integer", new Object[]{ octal });
    }

    
    @Override
    public XsUnsignedLongExpr or64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "or64", new Object[]{ x, y });
    }

    
    @Override
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value) {
        return parseDateTime(picture, (value == null) ? (XsStringExpr) null : xs.string(value));
    }

    
    @Override
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value });
    }

    
    @Override
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language) {
        return parseDateTime(picture, (value == null) ? (XsStringExpr) null : xs.string(value), (language == null) ? (XsStringExpr) null : xs.string(language));
    }

    
    @Override
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value, language });
    }

    
    @Override
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language, String calendar) {
        return parseDateTime(picture, (value == null) ? (XsStringExpr) null : xs.string(value), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar));
    }

    
    @Override
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value, language, calendar });
    }

    
    @Override
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language, String calendar, String country) {
        return parseDateTime(picture, (value == null) ? (XsStringExpr) null : xs.string(value), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar), (country == null) ? (XsStringExpr) null : xs.string(country));
    }

    
    @Override
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar, XsStringExpr country) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value, language, calendar, country });
    }

    
    @Override
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value) {
        return parseYymmdd(picture, (value == null) ? (XsStringExpr) null : xs.string(value));
    }

    
    @Override
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value });
    }

    
    @Override
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language) {
        return parseYymmdd(picture, (value == null) ? (XsStringExpr) null : xs.string(value), (language == null) ? (XsStringExpr) null : xs.string(language));
    }

    
    @Override
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value, language });
    }

    
    @Override
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language, String calendar) {
        return parseYymmdd(picture, (value == null) ? (XsStringExpr) null : xs.string(value), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar));
    }

    
    @Override
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value, language, calendar });
    }

    
    @Override
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language, String calendar, String country) {
        return parseYymmdd(picture, (value == null) ? (XsStringExpr) null : xs.string(value), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar), (country == null) ? (XsStringExpr) null : xs.string(country));
    }

    
    @Override
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar, XsStringExpr country) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value, language, calendar, country });
    }

    
    @Override
    public XsStringExpr path(NodeExpr node) {
        return new XsExprImpl.StringCallImpl("xdmp", "path", new Object[]{ node });
    }

    
    @Override
    public XsStringExpr path(NodeExpr node, boolean includeDocument) {
        return path(node, xs.booleanVal(includeDocument));
    }

    
    @Override
    public XsStringExpr path(NodeExpr node, XsBooleanExpr includeDocument) {
        return new XsExprImpl.StringCallImpl("xdmp", "path", new Object[]{ node, includeDocument });
    }

    
    @Override
    public XsIntegerExpr position(XsStringExpr test, String target) {
        return position(test, (target == null) ? (XsStringExpr) null : xs.string(target));
    }

    
    @Override
    public XsIntegerExpr position(XsStringExpr test, XsStringExpr target) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "position", new Object[]{ test, target });
    }

    
    @Override
    public XsIntegerExpr position(XsStringExpr test, String target, String collation) {
        return position(test, (target == null) ? (XsStringExpr) null : xs.string(target), (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsIntegerExpr position(XsStringExpr test, XsStringExpr target, XsStringExpr collation) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "position", new Object[]{ test, target, collation });
    }

    
    @Override
    public XsQNameExpr QNameFromKey(XsStringExpr key) {
        return new XsExprImpl.QNameCallImpl("xdmp", "QName-from-key", new Object[]{ key });
    }

    
    @Override
    public XsIntegerExpr quarterFromDate(XsDateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "quarter-from-date", new Object[]{ arg });
    }

    
    @Override
    public XsUnsignedLongExpr random() {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "random", new Object[]{  });
    }

    
    @Override
    public XsUnsignedLongExpr random(XsUnsignedLongExpr max) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "random", new Object[]{ max });
    }

    
    @Override
    public XsAnyURIExpr resolveUri(XsStringExpr relative, String base) {
        return resolveUri(relative, (base == null) ? (XsStringExpr) null : xs.string(base));
    }

    
    @Override
    public XsAnyURIExpr resolveUri(XsStringExpr relative, XsStringExpr base) {
        return new XsExprImpl.AnyURICallImpl("xdmp", "resolve-uri", new Object[]{ relative, base });
    }

    
    @Override
    public XsUnsignedLongExpr rshift64(XsUnsignedLongExpr x, long y) {
        return rshift64(x, xs.longVal(y));
    }

    
    @Override
    public XsUnsignedLongExpr rshift64(XsUnsignedLongExpr x, XsLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "rshift64", new Object[]{ x, y });
    }

    
    @Override
    public XsStringExpr sha1(ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha1", new Object[]{ data });
    }

    
    @Override
    public XsStringExpr sha1(ItemExpr data, String encoding) {
        return sha1(data, (encoding == null) ? (XsStringExpr) null : xs.string(encoding));
    }

    
    @Override
    public XsStringExpr sha1(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha1", new Object[]{ data, encoding });
    }

    
    @Override
    public XsStringExpr sha256(ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha256", new Object[]{ data });
    }

    
    @Override
    public XsStringExpr sha256(ItemExpr data, String encoding) {
        return sha256(data, (encoding == null) ? (XsStringExpr) null : xs.string(encoding));
    }

    
    @Override
    public XsStringExpr sha256(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha256", new Object[]{ data, encoding });
    }

    
    @Override
    public XsStringExpr sha384(ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha384", new Object[]{ data });
    }

    
    @Override
    public XsStringExpr sha384(ItemExpr data, String encoding) {
        return sha384(data, (encoding == null) ? (XsStringExpr) null : xs.string(encoding));
    }

    
    @Override
    public XsStringExpr sha384(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha384", new Object[]{ data, encoding });
    }

    
    @Override
    public XsStringExpr sha512(ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha512", new Object[]{ data });
    }

    
    @Override
    public XsStringExpr sha512(ItemExpr data, String encoding) {
        return sha512(data, (encoding == null) ? (XsStringExpr) null : xs.string(encoding));
    }

    
    @Override
    public XsStringExpr sha512(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha512", new Object[]{ data, encoding });
    }

    
    @Override
    public XsUnsignedLongExpr step64(XsUnsignedLongExpr initial, XsUnsignedLongExpr step) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "step64", new Object[]{ initial, step });
    }

    
    @Override
    public XsStringExpr strftime(XsStringExpr format, String value) {
        return strftime(format, (value == null) ? (XsDateTimeExpr) null : xs.dateTime(value));
    }

    
    @Override
    public XsStringExpr strftime(XsStringExpr format, XsDateTimeExpr value) {
        return new XsExprImpl.StringCallImpl("xdmp", "strftime", new Object[]{ format, value });
    }

    
    @Override
    public XsDateTimeExpr timestampToWallclock(XsUnsignedLongExpr timestamp) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "timestamp-to-wallclock", new Object[]{ timestamp });
    }

    
    @Override
    public NodeExpr toJson(ItemSeqExpr item) {
        return new BaseTypeImpl.NodeCallImpl("xdmp", "to-json", new Object[]{ item });
    }

    
    @Override
    public XsQNameExpr type(XsAnyAtomicTypeExpr value) {
        return new XsExprImpl.QNameCallImpl("xdmp", "type", new Object[]{ value });
    }

    
    @Override
    public XsStringExpr urlDecode(XsStringExpr encoded) {
        return new XsExprImpl.StringCallImpl("xdmp", "url-decode", new Object[]{ encoded });
    }

    
    @Override
    public XsStringExpr urlEncode(XsStringExpr plaintext) {
        return new XsExprImpl.StringCallImpl("xdmp", "url-encode", new Object[]{ plaintext });
    }

    
    @Override
    public XsStringExpr urlEncode(XsStringExpr plaintext, boolean noSpacePlus) {
        return urlEncode(plaintext, xs.booleanVal(noSpacePlus));
    }

    
    @Override
    public XsStringExpr urlEncode(XsStringExpr plaintext, XsBooleanExpr noSpacePlus) {
        return new XsExprImpl.StringCallImpl("xdmp", "url-encode", new Object[]{ plaintext, noSpacePlus });
    }

    
    @Override
    public XsUnsignedLongExpr wallclockToTimestamp(XsDateTimeExpr timestamp) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "wallclock-to-timestamp", new Object[]{ timestamp });
    }

    
    @Override
    public XsIntegerExpr weekFromDate(XsDateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "week-from-date", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr weekdayFromDate(XsDateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "weekday-from-date", new Object[]{ arg });
    }

    
    @Override
    public XsUnsignedLongExpr xor64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "xor64", new Object[]{ x, y });
    }

    
    @Override
    public XsIntegerExpr yeardayFromDate(XsDateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "yearday-from-date", new Object[]{ arg });
    }

    }
