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

import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.expression.XsValue;

import com.marklogic.client.expression.XdmpExpr;
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
 import com.marklogic.client.type.XsNumericSeqExpr;
 import com.marklogic.client.type.XsQNameExpr;
 import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsStringSeqExpr;
 import com.marklogic.client.type.XsUnsignedIntExpr;
 import com.marklogic.client.type.XsUnsignedLongExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class XdmpExprImpl implements XdmpExpr {
    private XsExprImpl xs = null;
    public XdmpExprImpl(XsExprImpl xs) {
        this.xs = xs;
    }
     @Override
        public XsUnsignedLongExpr add64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "add64", new Object[]{ x, y });
    }
    @Override
        public XsUnsignedLongExpr and64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "and64", new Object[]{ x, y });
    }
    @Override
        public XsStringExpr base64Decode(XsStringExpr encoded) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "base64-decode", new Object[]{ encoded });
    }
    @Override
        public XsStringExpr base64Encode(XsStringExpr plaintext) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "base64-encode", new Object[]{ plaintext });
    }
    @Override
        public XsBooleanExpr castableAs(XsStringExpr namespaceUri, String localName, ItemExpr item) {
        return castableAs(namespaceUri, xs.string(localName), item); 
    }
    @Override
        public XsBooleanExpr castableAs(XsStringExpr namespaceUri, XsStringExpr localName, ItemExpr item) {
        return new XsExprImpl.XsBooleanCallImpl("xdmp", "castable-as", new Object[]{ namespaceUri, localName, item });
    }
    @Override
        public XsStringExpr crypt(XsStringExpr password, String salt) {
        return crypt(password, xs.string(salt)); 
    }
    @Override
        public XsStringExpr crypt(XsStringExpr password, XsStringExpr salt) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "crypt", new Object[]{ password, salt });
    }
    @Override
        public XsStringExpr crypt2(XsStringExpr password) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "crypt2", new Object[]{ password });
    }
    @Override
        public XsStringExpr daynameFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "dayname-from-date", new Object[]{ arg });
    }
    @Override
        public XsStringExpr decodeFromNCName(XsStringExpr name) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "decode-from-NCName", new Object[]{ name });
    }
    @Override
        public XsStringExpr describe(ItemSeqExpr item) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "describe", new Object[]{ item });
    }
    @Override
        public XsStringExpr describe(ItemSeqExpr item, XsUnsignedIntExpr maxSequenceLength) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "describe", new Object[]{ item, maxSequenceLength });
    }
    @Override
        public XsStringExpr describe(ItemSeqExpr item, XsUnsignedIntExpr maxSequenceLength, XsUnsignedIntExpr maxItemLength) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "describe", new Object[]{ item, maxSequenceLength, maxItemLength });
    }
    @Override
        public XsStringExpr diacriticLess(XsStringExpr string) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "diacritic-less", new Object[]{ string });
    }
    @Override
        public XsStringExpr elementContentType(ElementNodeExpr element) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "element-content-type", new Object[]{ element });
    }
    @Override
        public XsStringExpr encodeForNCName(XsStringExpr name) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "encode-for-NCName", new Object[]{ name });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "format-number", new Object[]{ value });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture)); 
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "format-number", new Object[]{ value, picture });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language)); 
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue)); 
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue), (ordchar == null) ? null : xs.string(ordchar)); 
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue), (ordchar == null) ? null : xs.string(ordchar), (zeroPadding == null) ? null : xs.string(zeroPadding)); 
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar, zeroPadding });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue), (ordchar == null) ? null : xs.string(ordchar), (zeroPadding == null) ? null : xs.string(zeroPadding), (groupingSeparator == null) ? null : xs.string(groupingSeparator)); 
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding, XsStringExpr groupingSeparator) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar, zeroPadding, groupingSeparator });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator, XsIntegerExpr groupingSize) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue), (ordchar == null) ? null : xs.string(ordchar), (zeroPadding == null) ? null : xs.string(zeroPadding), (groupingSeparator == null) ? null : xs.string(groupingSeparator), groupingSize); 
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding, XsStringExpr groupingSeparator, XsIntegerExpr groupingSize) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar, zeroPadding, groupingSeparator, groupingSize });
    }
    @Override
        public ItemSeqExpr fromJson(NodeExpr arg) {
        return new BaseTypeImpl.ItemSeqCallImpl("xdmp", "from-json", new Object[]{ arg });
    }
    @Override
    public XsStringExpr getCurrentUser() {
        return new XsExprImpl.XsStringCallImpl("xdmp", "get-current-user", null);
    }
    @Override
        public XsUnsignedIntExpr hash32(XsStringExpr string) {
        return new XsExprImpl.XsUnsignedIntCallImpl("xdmp", "hash32", new Object[]{ string });
    }
    @Override
        public XsUnsignedLongExpr hash64(XsStringExpr string) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "hash64", new Object[]{ string });
    }
    @Override
        public XsIntegerExpr hexToInteger(XsStringExpr hex) {
        return new XsExprImpl.XsIntegerCallImpl("xdmp", "hex-to-integer", new Object[]{ hex });
    }
    @Override
        public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "hmac-md5", new Object[]{ secretkey, message });
    }
    @Override
        public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message, String encoding) {
        return hmacMd5(secretkey, message, (encoding == null) ? null : xs.string(encoding)); 
    }
    @Override
        public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "hmac-md5", new Object[]{ secretkey, message, encoding });
    }
    @Override
        public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "hmac-sha1", new Object[]{ secretkey, message });
    }
    @Override
        public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message, String encoding) {
        return hmacSha1(secretkey, message, (encoding == null) ? null : xs.string(encoding)); 
    }
    @Override
        public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "hmac-sha1", new Object[]{ secretkey, message, encoding });
    }
    @Override
        public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "hmac-sha256", new Object[]{ secretkey, message });
    }
    @Override
        public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message, String encoding) {
        return hmacSha256(secretkey, message, (encoding == null) ? null : xs.string(encoding)); 
    }
    @Override
        public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "hmac-sha256", new Object[]{ secretkey, message, encoding });
    }
    @Override
        public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "hmac-sha512", new Object[]{ secretkey, message });
    }
    @Override
        public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message, String encoding) {
        return hmacSha512(secretkey, message, (encoding == null) ? null : xs.string(encoding)); 
    }
    @Override
        public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "hmac-sha512", new Object[]{ secretkey, message, encoding });
    }
    @Override
        public XsStringExpr initcap(XsStringExpr string) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "initcap", new Object[]{ string });
    }
    @Override
        public XsStringExpr integerToHex(XsIntegerExpr val) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "integer-to-hex", new Object[]{ val });
    }
    @Override
        public XsStringExpr integerToOctal(XsIntegerExpr val) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "integer-to-octal", new Object[]{ val });
    }
    @Override
        public XsStringExpr keyFromQName(XsQNameExpr name) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "key-from-QName", new Object[]{ name });
    }
    @Override
        public XsUnsignedLongExpr lshift64(XsUnsignedLongExpr x, long y) {
        return lshift64(x, xs.longVal(y)); 
    }
    @Override
        public XsUnsignedLongExpr lshift64(XsUnsignedLongExpr x, XsLongExpr y) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "lshift64", new Object[]{ x, y });
    }
    @Override
        public XsStringExpr md5(ItemExpr data) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "md5", new Object[]{ data });
    }
    @Override
        public XsStringExpr md5(ItemExpr data, String encoding) {
        return md5(data, (encoding == null) ? null : xs.string(encoding)); 
    }
    @Override
        public XsStringExpr md5(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "md5", new Object[]{ data, encoding });
    }
    @Override
        public XsStringExpr monthNameFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "month-name-from-date", new Object[]{ arg });
    }
    @Override
        public XsUnsignedLongExpr mul64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "mul64", new Object[]{ x, y });
    }
    @Override
        public XsStringSeqExpr nodeCollections(NodeExpr node) {
        return new XsExprImpl.XsStringSeqCallImpl("xdmp", "node-collections", new Object[]{ node });
    }
    @Override
        public MapMapExpr nodeMetadata(NodeExpr arg1) {
        return new MapExprImpl.MapMapCallImpl("xdmp", "node-metadata", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr nodeMetadataValue(NodeExpr arg1, String arg2) {
        return nodeMetadataValue(arg1, xs.string(arg2)); 
    }
    @Override
        public XsStringExpr nodeMetadataValue(NodeExpr arg1, XsStringExpr arg2) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "node-metadata-value", new Object[]{ arg1, arg2 });
    }
    @Override
        public XsStringExpr nodeKind(NodeExpr node) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "node-kind", new Object[]{ node });
    }
    @Override
        public ItemSeqExpr nodePermissions(NodeExpr node) {
        return new BaseTypeImpl.ItemSeqCallImpl("xdmp", "node-permissions", new Object[]{ node });
    }
    @Override
        public ItemSeqExpr nodePermissions(NodeExpr node, String outputKind) {
        return nodePermissions(node, (outputKind == null) ? null : xs.string(outputKind)); 
    }
    @Override
        public ItemSeqExpr nodePermissions(NodeExpr node, XsStringExpr outputKind) {
        return new BaseTypeImpl.ItemSeqCallImpl("xdmp", "node-permissions", new Object[]{ node, outputKind });
    }
    @Override
        public XsStringExpr nodeUri(NodeExpr node) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "node-uri", new Object[]{ node });
    }
    @Override
        public XsUnsignedLongExpr not64(XsUnsignedLongExpr x) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "not64", new Object[]{ x });
    }
    @Override
        public XsIntegerExpr octalToInteger(XsStringExpr octal) {
        return new XsExprImpl.XsIntegerCallImpl("xdmp", "octal-to-integer", new Object[]{ octal });
    }
    @Override
        public XsUnsignedLongExpr or64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "or64", new Object[]{ x, y });
    }
    @Override
        public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value) {
        return parseDateTime(picture, xs.string(value)); 
    }
    @Override
        public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value) {
        return new XsExprImpl.XsDateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value });
    }
    @Override
        public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language) {
        return parseDateTime(picture, xs.string(value), (language == null) ? null : xs.string(language)); 
    }
    @Override
        public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language) {
        return new XsExprImpl.XsDateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value, language });
    }
    @Override
        public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language, String calendar) {
        return parseDateTime(picture, xs.string(value), (language == null) ? null : xs.string(language), (calendar == null) ? null : xs.string(calendar)); 
    }
    @Override
        public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar) {
        return new XsExprImpl.XsDateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value, language, calendar });
    }
    @Override
        public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language, String calendar, String country) {
        return parseDateTime(picture, xs.string(value), (language == null) ? null : xs.string(language), (calendar == null) ? null : xs.string(calendar), (country == null) ? null : xs.string(country)); 
    }
    @Override
        public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar, XsStringExpr country) {
        return new XsExprImpl.XsDateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value, language, calendar, country });
    }
    @Override
        public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value) {
        return parseYymmdd(picture, xs.string(value)); 
    }
    @Override
        public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value) {
        return new XsExprImpl.XsDateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value });
    }
    @Override
        public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language) {
        return parseYymmdd(picture, xs.string(value), (language == null) ? null : xs.string(language)); 
    }
    @Override
        public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language) {
        return new XsExprImpl.XsDateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value, language });
    }
    @Override
        public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language, String calendar) {
        return parseYymmdd(picture, xs.string(value), (language == null) ? null : xs.string(language), (calendar == null) ? null : xs.string(calendar)); 
    }
    @Override
        public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar) {
        return new XsExprImpl.XsDateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value, language, calendar });
    }
    @Override
        public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language, String calendar, String country) {
        return parseYymmdd(picture, xs.string(value), (language == null) ? null : xs.string(language), (calendar == null) ? null : xs.string(calendar), (country == null) ? null : xs.string(country)); 
    }
    @Override
        public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar, XsStringExpr country) {
        return new XsExprImpl.XsDateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value, language, calendar, country });
    }
    @Override
        public XsStringExpr path(NodeExpr node) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "path", new Object[]{ node });
    }
    @Override
        public XsStringExpr path(NodeExpr node, boolean includeDocument) {
        return path(node, xs.booleanVal(includeDocument)); 
    }
    @Override
        public XsStringExpr path(NodeExpr node, XsBooleanExpr includeDocument) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "path", new Object[]{ node, includeDocument });
    }
    @Override
        public XsIntegerExpr position(XsStringExpr test, String target) {
        return position(test, xs.string(target)); 
    }
    @Override
        public XsIntegerExpr position(XsStringExpr test, XsStringExpr target) {
        return new XsExprImpl.XsIntegerCallImpl("xdmp", "position", new Object[]{ test, target });
    }
    @Override
        public XsIntegerExpr position(XsStringExpr test, String target, String collation) {
        return position(test, xs.string(target), (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsIntegerExpr position(XsStringExpr test, XsStringExpr target, XsStringExpr collation) {
        return new XsExprImpl.XsIntegerCallImpl("xdmp", "position", new Object[]{ test, target, collation });
    }
    @Override
        public XsQNameExpr QNameFromKey(XsStringExpr key) {
        return new XsExprImpl.XsQNameCallImpl("xdmp", "QName-from-key", new Object[]{ key });
    }
    @Override
        public XsIntegerExpr quarterFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("xdmp", "quarter-from-date", new Object[]{ arg });
    }
    @Override
        public XsUnsignedLongExpr random() {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "random", new Object[]{  });
    }
    @Override
        public XsUnsignedLongExpr random(XsUnsignedLongExpr max) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "random", new Object[]{ max });
    }
    @Override
        public XsAnyURIExpr resolveUri(XsStringExpr relative, String base) {
        return resolveUri(relative, xs.string(base)); 
    }
    @Override
        public XsAnyURIExpr resolveUri(XsStringExpr relative, XsStringExpr base) {
        return new XsExprImpl.XsAnyURICallImpl("xdmp", "resolve-uri", new Object[]{ relative, base });
    }
    @Override
        public XsUnsignedLongExpr rshift64(XsUnsignedLongExpr x, long y) {
        return rshift64(x, xs.longVal(y)); 
    }
    @Override
        public XsUnsignedLongExpr rshift64(XsUnsignedLongExpr x, XsLongExpr y) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "rshift64", new Object[]{ x, y });
    }
    @Override
        public XsStringExpr sha1(ItemExpr data) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "sha1", new Object[]{ data });
    }
    @Override
        public XsStringExpr sha1(ItemExpr data, String encoding) {
        return sha1(data, (encoding == null) ? null : xs.string(encoding)); 
    }
    @Override
        public XsStringExpr sha1(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "sha1", new Object[]{ data, encoding });
    }
    @Override
        public XsStringExpr sha256(ItemExpr data) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "sha256", new Object[]{ data });
    }
    @Override
        public XsStringExpr sha256(ItemExpr data, String encoding) {
        return sha256(data, (encoding == null) ? null : xs.string(encoding)); 
    }
    @Override
        public XsStringExpr sha256(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "sha256", new Object[]{ data, encoding });
    }
    @Override
        public XsStringExpr sha384(ItemExpr data) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "sha384", new Object[]{ data });
    }
    @Override
        public XsStringExpr sha384(ItemExpr data, String encoding) {
        return sha384(data, (encoding == null) ? null : xs.string(encoding)); 
    }
    @Override
        public XsStringExpr sha384(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "sha384", new Object[]{ data, encoding });
    }
    @Override
        public XsStringExpr sha512(ItemExpr data) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "sha512", new Object[]{ data });
    }
    @Override
        public XsStringExpr sha512(ItemExpr data, String encoding) {
        return sha512(data, (encoding == null) ? null : xs.string(encoding)); 
    }
    @Override
        public XsStringExpr sha512(ItemExpr data, XsStringExpr encoding) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "sha512", new Object[]{ data, encoding });
    }
    @Override
        public XsUnsignedLongExpr step64(XsUnsignedLongExpr initial, XsUnsignedLongExpr step) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "step64", new Object[]{ initial, step });
    }
    @Override
        public XsStringExpr strftime(XsStringExpr format, XsDateTimeExpr value) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "strftime", new Object[]{ format, value });
    }
    @Override
        public XsDateTimeExpr timestampToWallclock(XsUnsignedLongExpr timestamp) {
        return new XsExprImpl.XsDateTimeCallImpl("xdmp", "timestamp-to-wallclock", new Object[]{ timestamp });
    }
    @Override
        public NodeExpr toJson(ItemSeqExpr item) {
        return new BaseTypeImpl.NodeCallImpl("xdmp", "to-json", new Object[]{ item });
    }
    @Override
        public XsQNameExpr type(XsAnyAtomicTypeExpr value) {
        return new XsExprImpl.XsQNameCallImpl("xdmp", "type", new Object[]{ value });
    }
    @Override
        public XsStringExpr urlDecode(XsStringExpr encoded) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "url-decode", new Object[]{ encoded });
    }
    @Override
        public XsStringExpr urlEncode(XsStringExpr plaintext) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "url-encode", new Object[]{ plaintext });
    }
    @Override
        public XsStringExpr urlEncode(XsStringExpr plaintext, boolean noSpacePlus) {
        return urlEncode(plaintext, xs.booleanVal(noSpacePlus)); 
    }
    @Override
        public XsStringExpr urlEncode(XsStringExpr plaintext, XsBooleanExpr noSpacePlus) {
        return new XsExprImpl.XsStringCallImpl("xdmp", "url-encode", new Object[]{ plaintext, noSpacePlus });
    }
    @Override
        public XsUnsignedLongExpr wallclockToTimestamp(XsDateTimeExpr timestamp) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "wallclock-to-timestamp", new Object[]{ timestamp });
    }
    @Override
        public XsIntegerExpr weekdayFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("xdmp", "weekday-from-date", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr weekFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("xdmp", "week-from-date", new Object[]{ arg });
    }
    @Override
        public XsUnsignedLongExpr xor64(XsUnsignedLongExpr x, XsUnsignedLongExpr y) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xdmp", "xor64", new Object[]{ x, y });
    }
    @Override
        public XsIntegerExpr yeardayFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("xdmp", "yearday-from-date", new Object[]{ arg });
    }
}
