/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.row;

import java.io.Closeable;
import java.util.stream.Stream;

/**
 * A Row Set represents a set of rows produced by a plan
 * and read from the database.
 *
 * The parameterized type will be RowRecord when reading each row as a map
 * and can be a JSON read handle when reading each row as a JSON document,
 * or an XML read handle when reading each row as a XML document.
 *
 * The parameterized type can also be a JSON or XML IO representation with
 * a registered handle such as a Jackson JsonNode or an XML DOM Document.
 *
 * @param <T>	the type for reading a row from the set
 */
public interface RowSet<T> extends Iterable<T>, Closeable {
    /**
     * Identifies the columns in the row set.
     * @return	The column names
     */
    String[] getColumnNames();

    /**
     * Identifies the data types of the columns.
     *
     * The array lists the data types only if the rows were
     * requested with a RowManager configured by calling the
     * setDatatypeStyle() method with RowSetPart.HEADER.
     *
     * Note that setting the data type style to HEADER does
     * not cast the values of the column. The actual data type
     * of the column values may be different from the header
     * data type if the column values are inconsistent.
     *
     * @return	The column data types
     */
    String[] getColumnTypes();

    /**
     * Streams each row in the set of rows.
     * @return	a stream for the set of rows read from the database
     */
    Stream<T> stream();
}
