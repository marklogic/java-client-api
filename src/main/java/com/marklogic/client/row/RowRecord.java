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
package com.marklogic.client.row;

import java.util.Map;

import javax.xml.namespace.QName;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.RowReadHandle;
import com.marklogic.client.type.XsAnyAtomicTypeVal;

// TODO: JavaDoc
public interface RowRecord extends RowReadHandle, Map<String, Object> {
	enum ColumnKind {
        ATOMIC_VALUE, CONTENT, URI, BNODE, NULL;
    }

	ColumnKind getKind(String columnName);

	QName getAtomicDatatype(String columnName);

	boolean   getBoolean(String columnName);
    byte      getByte(String    columnName);
    double    getDouble(String  columnName);
    float     getFloat(String   columnName);
    int       getInt(String     columnName);
    long      getLong(String    columnName);
    short     getShort(String   columnName);
    String    getString(String  columnName);

	<T extends XsAnyAtomicTypeVal> T   getValueAs(String columnName,  Class<T> as) throws Exception;
	<T extends XsAnyAtomicTypeVal> T[] getValuesAs(String columnName, Class<T> as) throws Exception;

	Format getContentFormat(String columnName);
	String getContentMimetype(String columnName);
	<T extends AbstractReadHandle> T getContent(String columnName, T contentHandle);
    <T> T getContentAs(String columnName, Class<T> as);
}
