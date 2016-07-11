/*
 * Copyright 2016 MarkLogic Corporation
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

// TODO: single import
import com.marklogic.client.expression.BaseType;
import com.marklogic.client.expression.Xs;

import com.marklogic.client.expression.Xdmp;
import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.Map;
 import com.marklogic.client.expression.BaseType;
 import com.marklogic.client.impl.XsExprImpl;
 import com.marklogic.client.impl.MapExprImpl;
 import com.marklogic.client.impl.BaseTypeImpl;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class XdmpExprImpl implements Xdmp {
    private Xs xs = null;
    public XdmpExprImpl(Xs xs) {
        this.xs = xs;
    }
     @Override
        public Xs.UnsignedLongExpr add64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "add64", new Object[]{ x, y });
    }
    @Override
        public Xs.UnsignedLongExpr and64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "and64", new Object[]{ x, y });
    }
    @Override
        public Xs.StringExpr base64Decode(String encoded) {
        return base64Decode(xs.string(encoded)); 
    }
    @Override
        public Xs.StringExpr base64Decode(Xs.StringExpr encoded) {
        return new XsExprImpl.StringCallImpl("xdmp", "base64-decode", new Object[]{ encoded });
    }
    @Override
        public Xs.StringExpr base64Encode(String plaintext) {
        return base64Encode(xs.string(plaintext)); 
    }
    @Override
        public Xs.StringExpr base64Encode(Xs.StringExpr plaintext) {
        return new XsExprImpl.StringCallImpl("xdmp", "base64-encode", new Object[]{ plaintext });
    }
    @Override
        public Xs.BooleanExpr castableAs(String namespaceUri, String localName, BaseType.ItemExpr item) {
        return castableAs(xs.string(namespaceUri), xs.string(localName), item); 
    }
    @Override
        public Xs.BooleanExpr castableAs(Xs.StringExpr namespaceUri, Xs.StringExpr localName, BaseType.ItemExpr item) {
        return new XsExprImpl.BooleanCallImpl("xdmp", "castable-as", new Object[]{ namespaceUri, localName, item });
    }
    @Override
        public Xs.StringExpr crypt(String password, String salt) {
        return crypt(xs.string(password), xs.string(salt)); 
    }
    @Override
        public Xs.StringExpr crypt(Xs.StringExpr password, Xs.StringExpr salt) {
        return new XsExprImpl.StringCallImpl("xdmp", "crypt", new Object[]{ password, salt });
    }
    @Override
        public Xs.StringExpr crypt2(String password) {
        return crypt2(xs.string(password)); 
    }
    @Override
        public Xs.StringExpr crypt2(Xs.StringExpr password) {
        return new XsExprImpl.StringCallImpl("xdmp", "crypt2", new Object[]{ password });
    }
    @Override
        public Xs.StringExpr daynameFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.StringCallImpl("xdmp", "dayname-from-date", new Object[]{ arg });
    }
    @Override
        public Xs.StringExpr decodeFromNCName(String name) {
        return decodeFromNCName(xs.string(name)); 
    }
    @Override
        public Xs.StringExpr decodeFromNCName(Xs.StringExpr name) {
        return new XsExprImpl.StringCallImpl("xdmp", "decode-from-NCName", new Object[]{ name });
    }
    @Override
        public Xs.StringExpr describe(BaseType.ItemSeqExpr item) {
        return new XsExprImpl.StringCallImpl("xdmp", "describe", new Object[]{ item });
    }
    @Override
        public Xs.StringExpr describe(BaseType.ItemSeqExpr item, Xs.UnsignedIntExpr maxSequenceLength) {
        return new XsExprImpl.StringCallImpl("xdmp", "describe", new Object[]{ item, maxSequenceLength });
    }
    @Override
        public Xs.StringExpr describe(BaseType.ItemSeqExpr item, Xs.UnsignedIntExpr maxSequenceLength, Xs.UnsignedIntExpr maxItemLength) {
        return new XsExprImpl.StringCallImpl("xdmp", "describe", new Object[]{ item, maxSequenceLength, maxItemLength });
    }
    @Override
        public Xs.StringExpr diacriticLess(String string) {
        return diacriticLess(xs.string(string)); 
    }
    @Override
        public Xs.StringExpr diacriticLess(Xs.StringExpr string) {
        return new XsExprImpl.StringCallImpl("xdmp", "diacritic-less", new Object[]{ string });
    }
    @Override
        public Xs.StringExpr elementContentType(BaseType.ElementExpr element) {
        return new XsExprImpl.StringCallImpl("xdmp", "element-content-type", new Object[]{ element });
    }
    @Override
        public Xs.StringExpr encodeForNCName(String name) {
        return encodeForNCName(xs.string(name)); 
    }
    @Override
        public Xs.StringExpr encodeForNCName(Xs.StringExpr name) {
        return new XsExprImpl.StringCallImpl("xdmp", "encode-for-NCName", new Object[]{ name });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture)); 
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language)); 
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue)); 
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue, String ordchar) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue), (ordchar == null) ? null : xs.string(ordchar)); 
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue, Xs.StringExpr ordchar) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue), (ordchar == null) ? null : xs.string(ordchar), (zeroPadding == null) ? null : xs.string(zeroPadding)); 
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue, Xs.StringExpr ordchar, Xs.StringExpr zeroPadding) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar, zeroPadding });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue), (ordchar == null) ? null : xs.string(ordchar), (zeroPadding == null) ? null : xs.string(zeroPadding), (groupingSeparator == null) ? null : xs.string(groupingSeparator)); 
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue, Xs.StringExpr ordchar, Xs.StringExpr zeroPadding, Xs.StringExpr groupingSeparator) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar, zeroPadding, groupingSeparator });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator, Xs.IntegerExpr groupingSize) {
        return formatNumber(value, (picture == null) ? null : xs.string(picture), (language == null) ? null : xs.string(language), (letterValue == null) ? null : xs.string(letterValue), (ordchar == null) ? null : xs.string(ordchar), (zeroPadding == null) ? null : xs.string(zeroPadding), (groupingSeparator == null) ? null : xs.string(groupingSeparator), groupingSize); 
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue, Xs.StringExpr ordchar, Xs.StringExpr zeroPadding, Xs.StringExpr groupingSeparator, Xs.IntegerExpr groupingSize) {
        return new XsExprImpl.StringCallImpl("xdmp", "format-number", new Object[]{ value, picture, language, letterValue, ordchar, zeroPadding, groupingSeparator, groupingSize });
    }
    @Override
        public BaseType.ItemSeqExpr fromJson(BaseType.NodeExpr arg) {
        return new BaseTypeImpl.ItemSeqCallImpl("xdmp", "from-json", new Object[]{ arg });
    }
    @Override
    public Xs.StringExpr getCurrentUser() {
        return new XsExprImpl.StringCallImpl("xdmp", "get-current-user", null);
    }
    @Override
        public Xs.UnsignedIntExpr hash32(String string) {
        return hash32(xs.string(string)); 
    }
    @Override
        public Xs.UnsignedIntExpr hash32(Xs.StringExpr string) {
        return new XsExprImpl.UnsignedIntCallImpl("xdmp", "hash32", new Object[]{ string });
    }
    @Override
        public Xs.UnsignedLongExpr hash64(String string) {
        return hash64(xs.string(string)); 
    }
    @Override
        public Xs.UnsignedLongExpr hash64(Xs.StringExpr string) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "hash64", new Object[]{ string });
    }
    @Override
        public Xs.IntegerExpr hexToInteger(String hex) {
        return hexToInteger(xs.string(hex)); 
    }
    @Override
        public Xs.IntegerExpr hexToInteger(Xs.StringExpr hex) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "hex-to-integer", new Object[]{ hex });
    }
    @Override
        public Xs.StringExpr hmacMd5(BaseType.ItemExpr secretkey, BaseType.ItemExpr message) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-md5", new Object[]{ secretkey, message });
    }
    @Override
        public Xs.StringExpr hmacMd5(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, String encoding) {
        return hmacMd5(secretkey, message, xs.string(encoding)); 
    }
    @Override
        public Xs.StringExpr hmacMd5(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, Xs.StringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-md5", new Object[]{ secretkey, message, encoding });
    }
    @Override
        public Xs.StringExpr hmacSha1(BaseType.ItemExpr secretkey, BaseType.ItemExpr message) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha1", new Object[]{ secretkey, message });
    }
    @Override
        public Xs.StringExpr hmacSha1(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, String encoding) {
        return hmacSha1(secretkey, message, xs.string(encoding)); 
    }
    @Override
        public Xs.StringExpr hmacSha1(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, Xs.StringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha1", new Object[]{ secretkey, message, encoding });
    }
    @Override
        public Xs.StringExpr hmacSha256(BaseType.ItemExpr secretkey, BaseType.ItemExpr message) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha256", new Object[]{ secretkey, message });
    }
    @Override
        public Xs.StringExpr hmacSha256(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, String encoding) {
        return hmacSha256(secretkey, message, xs.string(encoding)); 
    }
    @Override
        public Xs.StringExpr hmacSha256(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, Xs.StringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha256", new Object[]{ secretkey, message, encoding });
    }
    @Override
        public Xs.StringExpr hmacSha512(BaseType.ItemExpr secretkey, BaseType.ItemExpr message) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha512", new Object[]{ secretkey, message });
    }
    @Override
        public Xs.StringExpr hmacSha512(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, String encoding) {
        return hmacSha512(secretkey, message, xs.string(encoding)); 
    }
    @Override
        public Xs.StringExpr hmacSha512(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, Xs.StringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "hmac-sha512", new Object[]{ secretkey, message, encoding });
    }
    @Override
        public Xs.StringExpr initcap(String string) {
        return initcap((string == null) ? null : xs.string(string)); 
    }
    @Override
        public Xs.StringExpr initcap(Xs.StringExpr string) {
        return new XsExprImpl.StringCallImpl("xdmp", "initcap", new Object[]{ string });
    }
    @Override
        public Xs.StringExpr integerToHex(Xs.IntegerExpr val) {
        return new XsExprImpl.StringCallImpl("xdmp", "integer-to-hex", new Object[]{ val });
    }
    @Override
        public Xs.StringExpr integerToOctal(Xs.IntegerExpr val) {
        return new XsExprImpl.StringCallImpl("xdmp", "integer-to-octal", new Object[]{ val });
    }
    @Override
        public Xs.StringExpr keyFromQName(Xs.QNameExpr name) {
        return new XsExprImpl.StringCallImpl("xdmp", "key-from-QName", new Object[]{ name });
    }
    @Override
        public Xs.UnsignedLongExpr lshift64(Xs.UnsignedLongExpr x, long y) {
        return lshift64(x, xs.longVal(y)); 
    }
    @Override
        public Xs.UnsignedLongExpr lshift64(Xs.UnsignedLongExpr x, Xs.LongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "lshift64", new Object[]{ x, y });
    }
    @Override
        public Xs.StringExpr md5(BaseType.ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "md5", new Object[]{ data });
    }
    @Override
        public Xs.StringExpr md5(BaseType.ItemExpr data, String encoding) {
        return md5(data, xs.string(encoding)); 
    }
    @Override
        public Xs.StringExpr md5(BaseType.ItemExpr data, Xs.StringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "md5", new Object[]{ data, encoding });
    }
    @Override
        public Xs.StringExpr monthNameFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.StringCallImpl("xdmp", "month-name-from-date", new Object[]{ arg });
    }
    @Override
        public Xs.UnsignedLongExpr mul64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "mul64", new Object[]{ x, y });
    }
    @Override
        public Xs.StringSeqExpr nodeCollections(BaseType.NodeExpr node) {
        return new XsExprImpl.StringSeqCallImpl("xdmp", "node-collections", new Object[]{ node });
    }
    @Override
        public Map.MapExpr nodeMetadata(BaseType.NodeExpr arg1) {
        return new MapExprImpl.MapCallImpl("xdmp", "node-metadata", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr nodeMetadataValue(BaseType.NodeExpr arg1, String arg2) {
        return nodeMetadataValue(arg1, xs.string(arg2)); 
    }
    @Override
        public Xs.StringExpr nodeMetadataValue(BaseType.NodeExpr arg1, Xs.StringExpr arg2) {
        return new XsExprImpl.StringCallImpl("xdmp", "node-metadata-value", new Object[]{ arg1, arg2 });
    }
    @Override
        public Xs.StringExpr nodeKind(BaseType.NodeExpr node) {
        return new XsExprImpl.StringCallImpl("xdmp", "node-kind", new Object[]{ node });
    }
    @Override
        public BaseType.ItemSeqExpr nodePermissions(BaseType.NodeExpr node) {
        return new BaseTypeImpl.ItemSeqCallImpl("xdmp", "node-permissions", new Object[]{ node });
    }
    @Override
        public BaseType.ItemSeqExpr nodePermissions(BaseType.NodeExpr node, String outputKind) {
        return nodePermissions(node, xs.string(outputKind)); 
    }
    @Override
        public BaseType.ItemSeqExpr nodePermissions(BaseType.NodeExpr node, Xs.StringExpr outputKind) {
        return new BaseTypeImpl.ItemSeqCallImpl("xdmp", "node-permissions", new Object[]{ node, outputKind });
    }
    @Override
        public Xs.StringExpr nodeUri(BaseType.NodeExpr node) {
        return new XsExprImpl.StringCallImpl("xdmp", "node-uri", new Object[]{ node });
    }
    @Override
        public Xs.UnsignedLongExpr not64(Xs.UnsignedLongExpr x) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "not64", new Object[]{ x });
    }
    @Override
        public Xs.IntegerExpr octalToInteger(String octal) {
        return octalToInteger(xs.string(octal)); 
    }
    @Override
        public Xs.IntegerExpr octalToInteger(Xs.StringExpr octal) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "octal-to-integer", new Object[]{ octal });
    }
    @Override
        public Xs.UnsignedLongExpr or64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "or64", new Object[]{ x, y });
    }
    @Override
        public Xs.DateTimeExpr parseDateTime(String picture, String value) {
        return parseDateTime(xs.string(picture), xs.string(value)); 
    }
    @Override
        public Xs.DateTimeExpr parseDateTime(Xs.StringExpr picture, Xs.StringExpr value) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value });
    }
    @Override
        public Xs.DateTimeExpr parseDateTime(String picture, String value, String language) {
        return parseDateTime(xs.string(picture), xs.string(value), (language == null) ? null : xs.string(language)); 
    }
    @Override
        public Xs.DateTimeExpr parseDateTime(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value, language });
    }
    @Override
        public Xs.DateTimeExpr parseDateTime(String picture, String value, String language, String calendar) {
        return parseDateTime(xs.string(picture), xs.string(value), (language == null) ? null : xs.string(language), (calendar == null) ? null : xs.string(calendar)); 
    }
    @Override
        public Xs.DateTimeExpr parseDateTime(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language, Xs.StringExpr calendar) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value, language, calendar });
    }
    @Override
        public Xs.DateTimeExpr parseDateTime(String picture, String value, String language, String calendar, String country) {
        return parseDateTime(xs.string(picture), xs.string(value), (language == null) ? null : xs.string(language), (calendar == null) ? null : xs.string(calendar), (country == null) ? null : xs.string(country)); 
    }
    @Override
        public Xs.DateTimeExpr parseDateTime(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language, Xs.StringExpr calendar, Xs.StringExpr country) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-dateTime", new Object[]{ picture, value, language, calendar, country });
    }
    @Override
        public Xs.DateTimeExpr parseYymmdd(String picture, String value) {
        return parseYymmdd(xs.string(picture), xs.string(value)); 
    }
    @Override
        public Xs.DateTimeExpr parseYymmdd(Xs.StringExpr picture, Xs.StringExpr value) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value });
    }
    @Override
        public Xs.DateTimeExpr parseYymmdd(String picture, String value, String language) {
        return parseYymmdd(xs.string(picture), xs.string(value), (language == null) ? null : xs.string(language)); 
    }
    @Override
        public Xs.DateTimeExpr parseYymmdd(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value, language });
    }
    @Override
        public Xs.DateTimeExpr parseYymmdd(String picture, String value, String language, String calendar) {
        return parseYymmdd(xs.string(picture), xs.string(value), (language == null) ? null : xs.string(language), (calendar == null) ? null : xs.string(calendar)); 
    }
    @Override
        public Xs.DateTimeExpr parseYymmdd(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language, Xs.StringExpr calendar) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value, language, calendar });
    }
    @Override
        public Xs.DateTimeExpr parseYymmdd(String picture, String value, String language, String calendar, String country) {
        return parseYymmdd(xs.string(picture), xs.string(value), (language == null) ? null : xs.string(language), (calendar == null) ? null : xs.string(calendar), (country == null) ? null : xs.string(country)); 
    }
    @Override
        public Xs.DateTimeExpr parseYymmdd(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language, Xs.StringExpr calendar, Xs.StringExpr country) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "parse-yymmdd", new Object[]{ picture, value, language, calendar, country });
    }
    @Override
        public Xs.StringExpr path(BaseType.NodeExpr node) {
        return new XsExprImpl.StringCallImpl("xdmp", "path", new Object[]{ node });
    }
    @Override
        public Xs.StringExpr path(BaseType.NodeExpr node, boolean includeDocument) {
        return path(node, xs.booleanVal(includeDocument)); 
    }
    @Override
        public Xs.StringExpr path(BaseType.NodeExpr node, Xs.BooleanExpr includeDocument) {
        return new XsExprImpl.StringCallImpl("xdmp", "path", new Object[]{ node, includeDocument });
    }
    @Override
        public Xs.IntegerExpr position(String test, String target) {
        return position((test == null) ? null : xs.string(test), (target == null) ? null : xs.string(target)); 
    }
    @Override
        public Xs.IntegerExpr position(Xs.StringExpr test, Xs.StringExpr target) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "position", new Object[]{ test, target });
    }
    @Override
        public Xs.IntegerExpr position(String test, String target, String collation) {
        return position((test == null) ? null : xs.string(test), (target == null) ? null : xs.string(target), (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public Xs.IntegerExpr position(Xs.StringExpr test, Xs.StringExpr target, Xs.StringExpr collation) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "position", new Object[]{ test, target, collation });
    }
    @Override
        public Xs.QNameExpr QNameFromKey(String key) {
        return QNameFromKey(xs.string(key)); 
    }
    @Override
        public Xs.QNameExpr QNameFromKey(Xs.StringExpr key) {
        return new XsExprImpl.QNameCallImpl("xdmp", "QName-from-key", new Object[]{ key });
    }
    @Override
        public Xs.IntegerExpr quarterFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "quarter-from-date", new Object[]{ arg });
    }
    @Override
        public Xs.UnsignedLongExpr random() {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "random", new Object[]{  });
    }
    @Override
        public Xs.UnsignedLongExpr random(Xs.UnsignedLongExpr max) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "random", new Object[]{ max });
    }
    @Override
        public Xs.AnyURIExpr resolveUri(String relative, String base) {
        return resolveUri((relative == null) ? null : xs.string(relative), xs.string(base)); 
    }
    @Override
        public Xs.AnyURIExpr resolveUri(Xs.StringExpr relative, Xs.StringExpr base) {
        return new XsExprImpl.AnyURICallImpl("xdmp", "resolve-uri", new Object[]{ relative, base });
    }
    @Override
        public Xs.UnsignedLongExpr rshift64(Xs.UnsignedLongExpr x, long y) {
        return rshift64(x, xs.longVal(y)); 
    }
    @Override
        public Xs.UnsignedLongExpr rshift64(Xs.UnsignedLongExpr x, Xs.LongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "rshift64", new Object[]{ x, y });
    }
    @Override
        public Xs.StringExpr sha1(BaseType.ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha1", new Object[]{ data });
    }
    @Override
        public Xs.StringExpr sha1(BaseType.ItemExpr data, String encoding) {
        return sha1(data, xs.string(encoding)); 
    }
    @Override
        public Xs.StringExpr sha1(BaseType.ItemExpr data, Xs.StringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha1", new Object[]{ data, encoding });
    }
    @Override
        public Xs.StringExpr sha256(BaseType.ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha256", new Object[]{ data });
    }
    @Override
        public Xs.StringExpr sha256(BaseType.ItemExpr data, String encoding) {
        return sha256(data, xs.string(encoding)); 
    }
    @Override
        public Xs.StringExpr sha256(BaseType.ItemExpr data, Xs.StringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha256", new Object[]{ data, encoding });
    }
    @Override
        public Xs.StringExpr sha384(BaseType.ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha384", new Object[]{ data });
    }
    @Override
        public Xs.StringExpr sha384(BaseType.ItemExpr data, String encoding) {
        return sha384(data, xs.string(encoding)); 
    }
    @Override
        public Xs.StringExpr sha384(BaseType.ItemExpr data, Xs.StringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha384", new Object[]{ data, encoding });
    }
    @Override
        public Xs.StringExpr sha512(BaseType.ItemExpr data) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha512", new Object[]{ data });
    }
    @Override
        public Xs.StringExpr sha512(BaseType.ItemExpr data, String encoding) {
        return sha512(data, xs.string(encoding)); 
    }
    @Override
        public Xs.StringExpr sha512(BaseType.ItemExpr data, Xs.StringExpr encoding) {
        return new XsExprImpl.StringCallImpl("xdmp", "sha512", new Object[]{ data, encoding });
    }
    @Override
        public Xs.UnsignedLongExpr step64(Xs.UnsignedLongExpr initial, Xs.UnsignedLongExpr step) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "step64", new Object[]{ initial, step });
    }
    @Override
        public Xs.StringExpr strftime(String format, Xs.DateTimeExpr value) {
        return strftime(xs.string(format), value); 
    }
    @Override
        public Xs.StringExpr strftime(Xs.StringExpr format, Xs.DateTimeExpr value) {
        return new XsExprImpl.StringCallImpl("xdmp", "strftime", new Object[]{ format, value });
    }
    @Override
        public Xs.DateTimeExpr timestampToWallclock(Xs.UnsignedLongExpr timestamp) {
        return new XsExprImpl.DateTimeCallImpl("xdmp", "timestamp-to-wallclock", new Object[]{ timestamp });
    }
    @Override
        public BaseType.NodeExpr toJson(BaseType.ItemSeqExpr item) {
        return new BaseTypeImpl.NodeCallImpl("xdmp", "to-json", new Object[]{ item });
    }
    @Override
        public Xs.QNameExpr type(Xs.AnyAtomicTypeExpr value) {
        return new XsExprImpl.QNameCallImpl("xdmp", "type", new Object[]{ value });
    }
    @Override
        public Xs.StringExpr urlDecode(String encoded) {
        return urlDecode(xs.string(encoded)); 
    }
    @Override
        public Xs.StringExpr urlDecode(Xs.StringExpr encoded) {
        return new XsExprImpl.StringCallImpl("xdmp", "url-decode", new Object[]{ encoded });
    }
    @Override
        public Xs.StringExpr urlEncode(String plaintext) {
        return urlEncode(xs.string(plaintext)); 
    }
    @Override
        public Xs.StringExpr urlEncode(Xs.StringExpr plaintext) {
        return new XsExprImpl.StringCallImpl("xdmp", "url-encode", new Object[]{ plaintext });
    }
    @Override
        public Xs.StringExpr urlEncode(String plaintext, boolean noSpacePlus) {
        return urlEncode(xs.string(plaintext), xs.booleanVal(noSpacePlus)); 
    }
    @Override
        public Xs.StringExpr urlEncode(Xs.StringExpr plaintext, Xs.BooleanExpr noSpacePlus) {
        return new XsExprImpl.StringCallImpl("xdmp", "url-encode", new Object[]{ plaintext, noSpacePlus });
    }
    @Override
        public Xs.UnsignedLongExpr wallclockToTimestamp(Xs.DateTimeExpr timestamp) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "wallclock-to-timestamp", new Object[]{ timestamp });
    }
    @Override
        public Xs.IntegerExpr weekdayFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "weekday-from-date", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr weekFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "week-from-date", new Object[]{ arg });
    }
    @Override
        public Xs.UnsignedLongExpr xor64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y) {
        return new XsExprImpl.UnsignedLongCallImpl("xdmp", "xor64", new Object[]{ x, y });
    }
    @Override
        public Xs.IntegerExpr yeardayFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("xdmp", "yearday-from-date", new Object[]{ arg });
    }
}
