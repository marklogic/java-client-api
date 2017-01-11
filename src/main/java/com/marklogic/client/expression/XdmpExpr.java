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
package com.marklogic.client.expression;

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


// IMPORTANT: Do not edit. This file is generated. 
public interface XdmpExpr {
    public XsUnsignedLongExpr add64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
    public XsUnsignedLongExpr and64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
    public XsStringExpr base64Decode(XsStringExpr encoded);
    public XsStringExpr base64Encode(XsStringExpr plaintext);
    public XsBooleanExpr castableAs(XsStringExpr namespaceUri, String localName, ItemExpr item);
    public XsBooleanExpr castableAs(XsStringExpr namespaceUri, XsStringExpr localName, ItemExpr item);
    public XsStringExpr crypt(XsStringExpr password, String salt);
    public XsStringExpr crypt(XsStringExpr password, XsStringExpr salt);
    public XsStringExpr crypt2(XsStringExpr password);
    public XsStringExpr daynameFromDate(XsDateExpr arg);
    public XsStringExpr decodeFromNCName(XsStringExpr name);
    public XsStringExpr describe(ItemSeqExpr item);
    public XsStringExpr describe(ItemSeqExpr item, XsUnsignedIntExpr maxSequenceLength);
    public XsStringExpr describe(ItemSeqExpr item, XsUnsignedIntExpr maxSequenceLength, XsUnsignedIntExpr maxItemLength);
    public XsStringExpr diacriticLess(XsStringExpr string);
    public XsStringExpr elementContentType(ElementNodeExpr element);
    public XsStringExpr encodeForNCName(XsStringExpr name);
    public XsStringExpr formatNumber(XsNumericSeqExpr value);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding, XsStringExpr groupingSeparator);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator, XsIntegerExpr groupingSize);
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding, XsStringExpr groupingSeparator, XsIntegerExpr groupingSize);
    public ItemSeqExpr fromJson(NodeExpr arg);
    public XsStringExpr getCurrentUser();
    public XsUnsignedIntExpr hash32(XsStringExpr string);
    public XsUnsignedLongExpr hash64(XsStringExpr string);
    public XsIntegerExpr hexToInteger(XsStringExpr hex);
    public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message);
    public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message, String encoding);
    public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding);
    public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message);
    public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message, String encoding);
    public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding);
    public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message);
    public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message, String encoding);
    public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding);
    public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message);
    public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message, String encoding);
    public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding);
    public XsStringExpr initcap(XsStringExpr string);
    public XsStringExpr integerToHex(XsIntegerExpr val);
    public XsStringExpr integerToOctal(XsIntegerExpr val);
    public XsStringExpr keyFromQName(XsQNameExpr name);
    public XsUnsignedLongExpr lshift64(XsUnsignedLongExpr x, long y);
    public XsUnsignedLongExpr lshift64(XsUnsignedLongExpr x, XsLongExpr y);
    public XsStringExpr md5(ItemExpr data);
    public XsStringExpr md5(ItemExpr data, String encoding);
    public XsStringExpr md5(ItemExpr data, XsStringExpr encoding);
    public XsStringExpr monthNameFromDate(XsDateExpr arg);
    public XsUnsignedLongExpr mul64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
    public XsStringSeqExpr nodeCollections(NodeExpr node);
    public MapMapExpr nodeMetadata(NodeExpr arg1);
    public XsStringExpr nodeMetadataValue(NodeExpr arg1, String arg2);
    public XsStringExpr nodeMetadataValue(NodeExpr arg1, XsStringExpr arg2);
    public XsStringExpr nodeKind(NodeExpr node);
    public ItemSeqExpr nodePermissions(NodeExpr node);
    public ItemSeqExpr nodePermissions(NodeExpr node, String outputKind);
    public ItemSeqExpr nodePermissions(NodeExpr node, XsStringExpr outputKind);
    public XsStringExpr nodeUri(NodeExpr node);
    public XsUnsignedLongExpr not64(XsUnsignedLongExpr x);
    public XsIntegerExpr octalToInteger(XsStringExpr octal);
    public XsUnsignedLongExpr or64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value);
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value);
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language);
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language);
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language, String calendar);
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar);
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language, String calendar, String country);
    public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value);
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value);
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language);
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language);
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language, String calendar);
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar);
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language, String calendar, String country);
    public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
    public XsStringExpr path(NodeExpr node);
    public XsStringExpr path(NodeExpr node, boolean includeDocument);
    public XsStringExpr path(NodeExpr node, XsBooleanExpr includeDocument);
    public XsIntegerExpr position(XsStringExpr test, String target);
    public XsIntegerExpr position(XsStringExpr test, XsStringExpr target);
    public XsIntegerExpr position(XsStringExpr test, String target, String collation);
    public XsIntegerExpr position(XsStringExpr test, XsStringExpr target, XsStringExpr collation);
    public XsQNameExpr QNameFromKey(XsStringExpr key);
    public XsIntegerExpr quarterFromDate(XsDateExpr arg);
    public XsUnsignedLongExpr random();
    public XsUnsignedLongExpr random(XsUnsignedLongExpr max);
    public XsAnyURIExpr resolveUri(XsStringExpr relative, String base);
    public XsAnyURIExpr resolveUri(XsStringExpr relative, XsStringExpr base);
    public XsUnsignedLongExpr rshift64(XsUnsignedLongExpr x, long y);
    public XsUnsignedLongExpr rshift64(XsUnsignedLongExpr x, XsLongExpr y);
    public XsStringExpr sha1(ItemExpr data);
    public XsStringExpr sha1(ItemExpr data, String encoding);
    public XsStringExpr sha1(ItemExpr data, XsStringExpr encoding);
    public XsStringExpr sha256(ItemExpr data);
    public XsStringExpr sha256(ItemExpr data, String encoding);
    public XsStringExpr sha256(ItemExpr data, XsStringExpr encoding);
    public XsStringExpr sha384(ItemExpr data);
    public XsStringExpr sha384(ItemExpr data, String encoding);
    public XsStringExpr sha384(ItemExpr data, XsStringExpr encoding);
    public XsStringExpr sha512(ItemExpr data);
    public XsStringExpr sha512(ItemExpr data, String encoding);
    public XsStringExpr sha512(ItemExpr data, XsStringExpr encoding);
    public XsUnsignedLongExpr step64(XsUnsignedLongExpr initial, XsUnsignedLongExpr step);
    public XsStringExpr strftime(XsStringExpr format, XsDateTimeExpr value);
    public XsDateTimeExpr timestampToWallclock(XsUnsignedLongExpr timestamp);
    public NodeExpr toJson(ItemSeqExpr item);
    public XsQNameExpr type(XsAnyAtomicTypeExpr value);
    public XsStringExpr urlDecode(XsStringExpr encoded);
    public XsStringExpr urlEncode(XsStringExpr plaintext);
    public XsStringExpr urlEncode(XsStringExpr plaintext, boolean noSpacePlus);
    public XsStringExpr urlEncode(XsStringExpr plaintext, XsBooleanExpr noSpacePlus);
    public XsUnsignedLongExpr wallclockToTimestamp(XsDateTimeExpr timestamp);
    public XsIntegerExpr weekdayFromDate(XsDateExpr arg);
    public XsIntegerExpr weekFromDate(XsDateExpr arg);
    public XsUnsignedLongExpr xor64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
    public XsIntegerExpr yeardayFromDate(XsDateExpr arg);
}
