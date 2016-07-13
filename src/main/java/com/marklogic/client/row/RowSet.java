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

import java.io.Closeable;
import java.util.stream.Stream;

import com.marklogic.client.io.marker.RowReadHandle;

// TODO: JavaDoc
public interface RowSet<T extends RowReadHandle> extends Iterable<T>, Closeable {
    public String[] getColumnNames();

    public Stream<T> stream();
}
