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

import com.marklogic.client.expression.XsValue;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.RowReadHandle;

// TODO: JavaDoc
public interface RowRecord extends RowReadHandle, Map<String, Object> {
	public enum ColumnKind {
        ATOMIC_VALUE, CONTENT, URI, BNODE, NULL;
    }

	public ColumnKind getKind(String columnName);

	public QName getAtomicDatatype(String columnName);

	public boolean   getBoolean(String columnName);
    public byte      getByte(String    columnName);
    public double    getDouble(String  columnName);
    public float     getFloat(String   columnName);
    public int       getInt(String     columnName);
    public long      getLong(String    columnName);
    public short     getShort(String   columnName);
    public String    getString(String  columnName);

	public <T extends XsValue.AnyAtomicTypeVal> T   getValueAs(String columnName,  Class<T> as) throws Exception;
	public <T extends XsValue.AnyAtomicTypeVal> T[] getValuesAs(String columnName, Class<T> as) throws Exception;

	public Format getContentFormat(String columnName);
	public String getContentMimetype(String columnName);
	public <T extends AbstractReadHandle> T getContent(String columnName, T contentHandle);
    public <T> T getContentAs(String columnName, Class<T> as);
}
