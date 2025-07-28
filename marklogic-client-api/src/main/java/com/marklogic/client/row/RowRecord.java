/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.row;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.type.PlanExprCol;
import com.marklogic.client.type.XsAnyAtomicTypeVal;

/**
 * A Row Record provides an extended, read-only map interface
 * to the column values in a row.
 */
public interface RowRecord extends Map<String, Object> {
    /**
     * Distinguishes an atomic value, an array or object container,
     * content such as a document or other node, and a null value.
     */
    enum ColumnKind {
        ATOMIC_VALUE, CONTAINER_VALUE, CONTENT, NULL;
    }

    /**
     * Identifies whether the value of a column is an atomic value,
     * a document or other content structure, or a null.
     * @param columnName	the name of the column
     * @return	the kind of value of the column in the row
     */
    ColumnKind getKind(String columnName);

    /**
     * Identifies whether the value of a column is an atomic value,
     * a document or other content structure, or a null.
     * @param col	the column identifier
     * @return	the kind of value of the column in the row
     */
    ColumnKind getKind(PlanExprCol col);

    /**
     * Identifies the server data type for a column. A column
     * may have different data types in different rows.
     * <p>
     * For atomic values, the string is the lexical form of a QName
     * with the conventional prefix for the type namespace,
     * a separating colon, and the type local name as in
     * xs:int or xs:dateTime.
     * <p>
     * For node values, the string has the node type
     * as in array, document, element, or object.
     *
     * @param columnName	the name of the column
     * @return	the server data type
     */
    String getDatatype(String columnName);

    /**
     * Identifies the server data type for a column.
     *
     * @param col	the column identifier
     * @return	the server data type
     */
    String getDatatype(PlanExprCol col);

    /**
     * Gets the value of a column with an xs:boolean schema data type
     * as a Java boolean primitive value.
     * @param columnName	the name of the column
     * @return	the boolean primitive for an xs:boolean column
     */
    boolean   getBoolean(String columnName);

    /**
     * Gets the value of a column with an xs:boolean schema data type
     * as a Java boolean primitive value.
     * @param col	the column identifier
     * @return	the boolean primitive for an xs:boolean column
     */
    boolean   getBoolean(PlanExprCol col);

    /**
     * Gets the value of a column with an xs:byte schema data type
     * as a Java byte primitive value.
     * @param columnName	the name of the column
     * @return	the byte primitive for an xs:byte column
     */
    byte      getByte(String columnName);

    /**
     * Gets the value of a column with an xs:byte schema data type
     * as a Java byte primitive value.
     * @param col	the column identifier
     * @return	the byte primitive for an xs:byte column
     */
    byte      getByte(PlanExprCol col);

    /**
     * Gets the value of a column with an xs:double schema data type
     * as a Java double primitive value.
     * @param columnName	the name of the column
     * @return	the double primitive for an xs:double column
     */
    double    getDouble(String columnName);

    /**
     * Gets the value of a column with an xs:double schema data type
     * as a Java double primitive value.
     * @param col	the column identifier
     * @return	the double primitive for an xs:double column
     */
    double    getDouble(PlanExprCol col);

    /**
     * Gets the value of a column with an xs:float schema data type
     * as a Java float primitive value.
     * @param columnName	the name of the column
     * @return	the float primitive for an xs:float column
     */
    float     getFloat(String columnName);

    /**
     * Gets the value of a column with an xs:float schema data type
     * as a Java float primitive value.
     * @param col	the column identifier
     * @return	the float primitive for an xs:float column
     */
    float     getFloat(PlanExprCol col);

    /**
     * Gets the value of a column with an xs:int schema data type
     * as a Java int primitive value.
     * @param columnName	the name of the column
     * @return	the int primitive for an xs:int column
     */
    int       getInt(String columnName);

    /**
     * Gets the value of a column with an xs:int schema data type
     * as a Java int primitive value.
     * @param col	the column identifier
     * @return	the int primitive for an xs:int column
     */
    int       getInt(PlanExprCol col);

    /**
     * Gets the value of a column with an xs:long schema data type
     * as a Java long primitive value.
     * @param columnName	the name of the column
     * @return	the long primitive for an xs:long column
     */
    long      getLong(String columnName);

    /**
     * Gets the value of a column with an xs:long schema data type
     * as a Java long primitive value.
     * @param col	the column identifier
     * @return	the long primitive for an xs:long column
     */
    long      getLong(PlanExprCol col);

    /**
     * Gets the value of a column with an xs:short schema data type
     * as a Java short primitive value.
     * @param columnName	the name of the column
     * @return	the short primitive for an xs:short column
     */
    short     getShort(String columnName);

    /**
     * Gets the value of a column with an xs:short schema data type
     * as a Java short primitive value.
     * @param col	the column identifier
     * @return	the short primitive for an xs:short column
     */
    short     getShort(PlanExprCol col);

    /**
     * Gets the value of a column with an xs:string schema data type
     * as a Java String literal value.
     * @param columnName	the name of the column
     * @return	the String literal for an xs:string column
     */
    String    getString(String columnName);

    /**
     * Gets the value of a column with an xs:string schema data type
     * as a Java String literal value.
     * @param col	the column identifier
     * @return	the String literal for an xs:string column
     */
    String    getString(PlanExprCol col);

    /**
     * Gets the value of a column with an atomic schema data type.
     * <p>
     * The value class must have the same schema data type
     * as the value of the column in the row.
     *
     * @param columnName	the name of the column
     * @param as	the value class for the schema data type of the column
     * @param <T> the type of the object for the value
     * @return	an object of the class with the value of the data type
     */
    <T extends XsAnyAtomicTypeVal> T getValueAs(String columnName,  Class<T> as);

    /**
     * Gets the value of a column with an atomic schema data type.
     *
     * @param col	the column identifier
     * @param as	the value class for the schema data type of the column
     * @param <T> the type of the object for the value
     * @return	an object of the class with the value of the data type
     */
    <T extends XsAnyAtomicTypeVal> T getValueAs(PlanExprCol col,  Class<T> as);

    JsonNode getContainer(String columnName);
    JsonNode getContainer(PlanExprCol col);
    <T extends JSONReadHandle> T getContainer(String columnName, T containerHandle);
    <T extends JSONReadHandle> T getContainer(PlanExprCol col, T containerHandle);
    <T> T getContainerAs(String columnName, Class<T> as);
    <T> T getContainerAs(PlanExprCol col, Class<T> as);

    /**
     * Identifies the format where a column has a document or
     * other content node value in the row instead of an atomic value.
     * @param columnName	the name of the column
     * @return	the format of the column content
     */
    Format getContentFormat(String columnName);

    /**
     * Identifies the format where a column has a document or
     * other content node value in the row instead of an atomic value.
     * @param col	the column identifier
     * @return	the format of the column content
     */
    Format getContentFormat(PlanExprCol col);

    /**
     * Identifies the format where a column has a document or
     * other content node value in the row instead of an atomic value.
     * @param columnName	the name of the column
     * @return	the mimetype of the column content
     */
    String getContentMimetype(String columnName);

    /**
     * Identifies the format where a column has a document or
     * other content node value in the row instead of an atomic value.
     * @param col	the column identifier
     * @return	the mimetype of the column content
     */
    String getContentMimetype(PlanExprCol col);

    /**
     * Gets the content of a column with a document or other
     * content node value.
     * <p>
     * The handle must support IO for content with the format
     * and mimetype. For instance, an XML DOM handle cannot be
     * used to read a JSON document.
     *
     * @param columnName	the name of the column
     * @param contentHandle	a handle for reading the content of the column value
     * @param <T> the type of the handle for reading the content
     * @return	the content handle populated with the content of the column value
     */
    <T extends AbstractReadHandle> T getContent(String columnName, T contentHandle);

    /**
     * Gets the content of a column with a document or other
     * content node value.
     *
     * @param col	the column identifier
     * @param contentHandle	a handle for reading the content of the column value
     * @param <T> the type of the handle for reading the content
     * @return	the content handle populated with the content of the column value
     */
    <T extends AbstractReadHandle> T getContent(PlanExprCol col, T contentHandle);

    /**
     * Gets the content of a column with a document or other
     * content node value.
     * <p>
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     * <p>
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     *
     * @param columnName	the name of the column
     * @param as	the IO class for reading the content of the column value
     * @param <T> the type of the IO object for the content
     * @return	an object of the IO class with the content of the column value
     */
    <T> T getContentAs(String columnName, Class<T> as);

    /**
     * Gets the content of a column with a document or other
     * content node value.
     *
     * @param col	the column identifier
     * @param as	the IO class for reading the content of the column value
     * @param <T> the type of the IO object for the content
     * @return	an object of the IO class with the content of the column value
     */
    <T> T getContentAs(PlanExprCol col, Class<T> as);
}
