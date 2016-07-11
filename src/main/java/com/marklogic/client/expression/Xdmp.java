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
package com.marklogic.client.expression;

// TODO: single import
import com.marklogic.client.expression.BaseType;

import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.Map;
 import com.marklogic.client.expression.BaseType;


// IMPORTANT: Do not edit. This file is generated. 
public interface Xdmp {
    public Xs.UnsignedLongExpr add64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y);
    public Xs.UnsignedLongExpr and64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y);
    public Xs.StringExpr base64Decode(String encoded);
    public Xs.StringExpr base64Decode(Xs.StringExpr encoded);
    public Xs.StringExpr base64Encode(String plaintext);
    public Xs.StringExpr base64Encode(Xs.StringExpr plaintext);
    public Xs.BooleanExpr castableAs(String namespaceUri, String localName, BaseType.ItemExpr item);
    public Xs.BooleanExpr castableAs(Xs.StringExpr namespaceUri, Xs.StringExpr localName, BaseType.ItemExpr item);
    public Xs.StringExpr crypt(String password, String salt);
    public Xs.StringExpr crypt(Xs.StringExpr password, Xs.StringExpr salt);
    public Xs.StringExpr crypt2(String password);
    public Xs.StringExpr crypt2(Xs.StringExpr password);
    public Xs.StringExpr daynameFromDate(Xs.DateExpr arg);
    public Xs.StringExpr decodeFromNCName(String name);
    public Xs.StringExpr decodeFromNCName(Xs.StringExpr name);
    public Xs.StringExpr describe(BaseType.ItemSeqExpr item);
    public Xs.StringExpr describe(BaseType.ItemSeqExpr item, Xs.UnsignedIntExpr maxSequenceLength);
    public Xs.StringExpr describe(BaseType.ItemSeqExpr item, Xs.UnsignedIntExpr maxSequenceLength, Xs.UnsignedIntExpr maxItemLength);
    public Xs.StringExpr diacriticLess(String string);
    public Xs.StringExpr diacriticLess(Xs.StringExpr string);
    public Xs.StringExpr elementContentType(BaseType.ElementExpr element);
    public Xs.StringExpr encodeForNCName(String name);
    public Xs.StringExpr encodeForNCName(Xs.StringExpr name);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue, String ordchar);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue, Xs.StringExpr ordchar);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue, Xs.StringExpr ordchar, Xs.StringExpr zeroPadding);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue, Xs.StringExpr ordchar, Xs.StringExpr zeroPadding, Xs.StringExpr groupingSeparator);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator, Xs.IntegerExpr groupingSize);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr language, Xs.StringExpr letterValue, Xs.StringExpr ordchar, Xs.StringExpr zeroPadding, Xs.StringExpr groupingSeparator, Xs.IntegerExpr groupingSize);
    public BaseType.ItemSeqExpr fromJson(BaseType.NodeExpr arg);
    public Xs.StringExpr getCurrentUser();
    public Xs.UnsignedIntExpr hash32(String string);
    public Xs.UnsignedIntExpr hash32(Xs.StringExpr string);
    public Xs.UnsignedLongExpr hash64(String string);
    public Xs.UnsignedLongExpr hash64(Xs.StringExpr string);
    public Xs.IntegerExpr hexToInteger(String hex);
    public Xs.IntegerExpr hexToInteger(Xs.StringExpr hex);
    public Xs.StringExpr hmacMd5(BaseType.ItemExpr secretkey, BaseType.ItemExpr message);
    public Xs.StringExpr hmacMd5(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, String encoding);
    public Xs.StringExpr hmacMd5(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, Xs.StringExpr encoding);
    public Xs.StringExpr hmacSha1(BaseType.ItemExpr secretkey, BaseType.ItemExpr message);
    public Xs.StringExpr hmacSha1(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, String encoding);
    public Xs.StringExpr hmacSha1(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, Xs.StringExpr encoding);
    public Xs.StringExpr hmacSha256(BaseType.ItemExpr secretkey, BaseType.ItemExpr message);
    public Xs.StringExpr hmacSha256(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, String encoding);
    public Xs.StringExpr hmacSha256(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, Xs.StringExpr encoding);
    public Xs.StringExpr hmacSha512(BaseType.ItemExpr secretkey, BaseType.ItemExpr message);
    public Xs.StringExpr hmacSha512(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, String encoding);
    public Xs.StringExpr hmacSha512(BaseType.ItemExpr secretkey, BaseType.ItemExpr message, Xs.StringExpr encoding);
    public Xs.StringExpr initcap(String string);
    public Xs.StringExpr initcap(Xs.StringExpr string);
    public Xs.StringExpr integerToHex(Xs.IntegerExpr val);
    public Xs.StringExpr integerToOctal(Xs.IntegerExpr val);
    public Xs.StringExpr keyFromQName(Xs.QNameExpr name);
    public Xs.UnsignedLongExpr lshift64(Xs.UnsignedLongExpr x, long y);
    public Xs.UnsignedLongExpr lshift64(Xs.UnsignedLongExpr x, Xs.LongExpr y);
    public Xs.StringExpr md5(BaseType.ItemExpr data);
    public Xs.StringExpr md5(BaseType.ItemExpr data, String encoding);
    public Xs.StringExpr md5(BaseType.ItemExpr data, Xs.StringExpr encoding);
    public Xs.StringExpr monthNameFromDate(Xs.DateExpr arg);
    public Xs.UnsignedLongExpr mul64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y);
    public Xs.StringSeqExpr nodeCollections(BaseType.NodeExpr node);
    public Map.MapExpr nodeMetadata(BaseType.NodeExpr arg1);
    public Xs.StringExpr nodeMetadataValue(BaseType.NodeExpr arg1, String arg2);
    public Xs.StringExpr nodeMetadataValue(BaseType.NodeExpr arg1, Xs.StringExpr arg2);
    public Xs.StringExpr nodeKind(BaseType.NodeExpr node);
    public BaseType.ItemSeqExpr nodePermissions(BaseType.NodeExpr node);
    public BaseType.ItemSeqExpr nodePermissions(BaseType.NodeExpr node, String outputKind);
    public BaseType.ItemSeqExpr nodePermissions(BaseType.NodeExpr node, Xs.StringExpr outputKind);
    public Xs.StringExpr nodeUri(BaseType.NodeExpr node);
    public Xs.UnsignedLongExpr not64(Xs.UnsignedLongExpr x);
    public Xs.IntegerExpr octalToInteger(String octal);
    public Xs.IntegerExpr octalToInteger(Xs.StringExpr octal);
    public Xs.UnsignedLongExpr or64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y);
    public Xs.DateTimeExpr parseDateTime(String picture, String value);
    public Xs.DateTimeExpr parseDateTime(Xs.StringExpr picture, Xs.StringExpr value);
    public Xs.DateTimeExpr parseDateTime(String picture, String value, String language);
    public Xs.DateTimeExpr parseDateTime(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language);
    public Xs.DateTimeExpr parseDateTime(String picture, String value, String language, String calendar);
    public Xs.DateTimeExpr parseDateTime(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language, Xs.StringExpr calendar);
    public Xs.DateTimeExpr parseDateTime(String picture, String value, String language, String calendar, String country);
    public Xs.DateTimeExpr parseDateTime(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language, Xs.StringExpr calendar, Xs.StringExpr country);
    public Xs.DateTimeExpr parseYymmdd(String picture, String value);
    public Xs.DateTimeExpr parseYymmdd(Xs.StringExpr picture, Xs.StringExpr value);
    public Xs.DateTimeExpr parseYymmdd(String picture, String value, String language);
    public Xs.DateTimeExpr parseYymmdd(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language);
    public Xs.DateTimeExpr parseYymmdd(String picture, String value, String language, String calendar);
    public Xs.DateTimeExpr parseYymmdd(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language, Xs.StringExpr calendar);
    public Xs.DateTimeExpr parseYymmdd(String picture, String value, String language, String calendar, String country);
    public Xs.DateTimeExpr parseYymmdd(Xs.StringExpr picture, Xs.StringExpr value, Xs.StringExpr language, Xs.StringExpr calendar, Xs.StringExpr country);
    public Xs.StringExpr path(BaseType.NodeExpr node);
    public Xs.StringExpr path(BaseType.NodeExpr node, boolean includeDocument);
    public Xs.StringExpr path(BaseType.NodeExpr node, Xs.BooleanExpr includeDocument);
    public Xs.IntegerExpr position(String test, String target);
    public Xs.IntegerExpr position(Xs.StringExpr test, Xs.StringExpr target);
    public Xs.IntegerExpr position(String test, String target, String collation);
    public Xs.IntegerExpr position(Xs.StringExpr test, Xs.StringExpr target, Xs.StringExpr collation);
    public Xs.QNameExpr QNameFromKey(String key);
    public Xs.QNameExpr QNameFromKey(Xs.StringExpr key);
    public Xs.IntegerExpr quarterFromDate(Xs.DateExpr arg);
    public Xs.UnsignedLongExpr random();
    public Xs.UnsignedLongExpr random(Xs.UnsignedLongExpr max);
    public Xs.AnyURIExpr resolveUri(String relative, String base);
    public Xs.AnyURIExpr resolveUri(Xs.StringExpr relative, Xs.StringExpr base);
    public Xs.UnsignedLongExpr rshift64(Xs.UnsignedLongExpr x, long y);
    public Xs.UnsignedLongExpr rshift64(Xs.UnsignedLongExpr x, Xs.LongExpr y);
    public Xs.StringExpr sha1(BaseType.ItemExpr data);
    public Xs.StringExpr sha1(BaseType.ItemExpr data, String encoding);
    public Xs.StringExpr sha1(BaseType.ItemExpr data, Xs.StringExpr encoding);
    public Xs.StringExpr sha256(BaseType.ItemExpr data);
    public Xs.StringExpr sha256(BaseType.ItemExpr data, String encoding);
    public Xs.StringExpr sha256(BaseType.ItemExpr data, Xs.StringExpr encoding);
    public Xs.StringExpr sha384(BaseType.ItemExpr data);
    public Xs.StringExpr sha384(BaseType.ItemExpr data, String encoding);
    public Xs.StringExpr sha384(BaseType.ItemExpr data, Xs.StringExpr encoding);
    public Xs.StringExpr sha512(BaseType.ItemExpr data);
    public Xs.StringExpr sha512(BaseType.ItemExpr data, String encoding);
    public Xs.StringExpr sha512(BaseType.ItemExpr data, Xs.StringExpr encoding);
    public Xs.UnsignedLongExpr step64(Xs.UnsignedLongExpr initial, Xs.UnsignedLongExpr step);
    public Xs.StringExpr strftime(String format, Xs.DateTimeExpr value);
    public Xs.StringExpr strftime(Xs.StringExpr format, Xs.DateTimeExpr value);
    public Xs.DateTimeExpr timestampToWallclock(Xs.UnsignedLongExpr timestamp);
    public BaseType.NodeExpr toJson(BaseType.ItemSeqExpr item);
    public Xs.QNameExpr type(Xs.AnyAtomicTypeExpr value);
    public Xs.StringExpr urlDecode(String encoded);
    public Xs.StringExpr urlDecode(Xs.StringExpr encoded);
    public Xs.StringExpr urlEncode(String plaintext);
    public Xs.StringExpr urlEncode(Xs.StringExpr plaintext);
    public Xs.StringExpr urlEncode(String plaintext, boolean noSpacePlus);
    public Xs.StringExpr urlEncode(Xs.StringExpr plaintext, Xs.BooleanExpr noSpacePlus);
    public Xs.UnsignedLongExpr wallclockToTimestamp(Xs.DateTimeExpr timestamp);
    public Xs.IntegerExpr weekdayFromDate(Xs.DateExpr arg);
    public Xs.IntegerExpr weekFromDate(Xs.DateExpr arg);
    public Xs.UnsignedLongExpr xor64(Xs.UnsignedLongExpr x, Xs.UnsignedLongExpr y);
    public Xs.IntegerExpr yeardayFromDate(Xs.DateExpr arg);
}
