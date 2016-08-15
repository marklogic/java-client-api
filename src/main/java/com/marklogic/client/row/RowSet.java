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

/**
 * A Row Set represents a set of rows produced by a plan
 * and read from the database.
 * 
 * The handle will be RowRecord when reading each row as a map,
 * a JSON read handle when reading each row as a JSON document,
 * or an XML read handle when reading each row as a XML document.
 * @param <T>	the type of the handle for reading the set of rows
 */
public interface RowSet<T> extends Iterable<T>, Closeable {
    String[] getColumnNames();

    /**
     * Streams each row in the set of rows.
     * @return	a stream for the set of rows read from the database
     */
    Stream<T> stream();
}
