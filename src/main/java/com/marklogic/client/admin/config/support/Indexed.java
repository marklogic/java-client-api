/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.admin.config.support;


import com.marklogic.client.admin.config.QueryOptions.Field;
import com.marklogic.client.admin.config.QueryOptions.JsonKey;
import com.marklogic.client.admin.config.QueryOptions.MarkLogicQName;
import com.marklogic.client.admin.config.QueryOptions.PathIndex;


/**
 * Marks classes that are backed by MarkLogic indexes.
 * Used to support {@link com.marklogic.client.admin.config.QueryOptionsBuilder} expressions.
 */
@SuppressWarnings("deprecation")
public interface Indexed {

	void setField(Field field);

	void setAttribute(MarkLogicQName attribute);

	void setElement(MarkLogicQName element);

	void setJsonKey(JsonKey jsonKey);

	void setPath(PathIndex path);
}
