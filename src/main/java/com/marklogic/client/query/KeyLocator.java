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
package com.marklogic.client.query;

/**
 * @deprecated Use {@link RawQueryByExampleDefinition Query By Example} instead for easy-to-write and much more full-featured key/value search.
 * <br><br>
 *
 * A Key Locator specifies a JSON key containing a value as part
 * of a KeyValueQueryDefinition.
 */
@Deprecated
public interface KeyLocator extends ValueLocator {
    /**
     * Returns a JSON key.
     * @return	the JSON key
     */
	public String getKey();
	/**
	 * Specifies the JSON key.
	 * @param key	the JSON key
	 */
    public void setKey(String key);
}
