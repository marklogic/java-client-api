/*
 * Copyright 2018 MarkLogic Corporation
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
package com.marklogic.client.moveToCommunity;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.type.*;
import com.tableausoftware.TableauException;
import com.tableausoftware.common.Collation;
import com.tableausoftware.extract.Extract;
import com.tableausoftware.extract.Row;
import com.tableausoftware.extract.TableDefinition;
import com.tableausoftware.extract.Table;
import com.tableausoftware.common.Type;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

// writes raw data out to a file of format Tableau Data Extract
public class TableauDataExtractWriter
  implements Consumer<LinkedHashMap<String,XsAnyAtomicTypeVal>>, AutoCloseable
{
  private boolean initialized = false;
  private TableDefinition tableDef;
  private LinkedHashMap<String,Column> columns = new LinkedHashMap<>();
  private Extract extract;
  private Table table;

  // the tableauTdeFileName specifies the tableau data extract file we'll write to
  public TableauDataExtractWriter(String tableauExtractFileName) {
    if ( new File(tableauExtractFileName).exists() ) {
      throw new IllegalStateException("Filename \"" + tableauExtractFileName + "\" already exists");
    }
    try {
      extract = new Extract(tableauExtractFileName);
      tableDef = new TableDefinition();
    } catch (TableauException e) {
      throw new MarkLogicIOException(e);
    }
  }

  // the apply function is compatible with ExtractViaTemplateListener
  public void accept(LinkedHashMap<String,XsAnyAtomicTypeVal> vals) {
    try {
      Row row = new Row(getTableDef(vals));
      try {
        Iterator<Column> values = columns.values().iterator();
        for ( int index=0; values.hasNext(); index++ ) {
          Column column = values.next();
          XsAnyAtomicTypeVal value = vals.get(column.columnName);
          if ( value != null ) {
            if ( column.type == Type.BOOLEAN ) {
              if ( value instanceof XsBooleanVal ) {
                row.setBoolean(index, ((XsBooleanVal)value).getBoolean());
                continue;
              }
            } else if ( column.type == Type.UNICODE_STRING ||
                        column.type == Type.CHAR_STRING )
            {
              if ( value instanceof XsStringVal ) {
                row.setString(index, ((XsStringVal)value).getString());
                continue;
              }
            } else if ( column.type == Type.DOUBLE ) {
              if ( value instanceof XsFloatVal) {
                row.setDouble(index, ((XsFloatVal)value).getFloat());
                continue;
              } else if ( value instanceof XsDoubleVal) {
                row.setDouble(index, ((XsDoubleVal) value).getDouble());
                continue;
              }
            } else if ( column.type == Type.INTEGER ) {
              if ( value instanceof XsShortVal ) {
                row.setInteger(index, ((XsShortVal)value).getBigInteger().shortValueExact());
                continue;
              } else if ( value instanceof XsIntVal ) {
                row.setInteger(index, ((XsIntegerVal)value).getBigInteger().intValueExact());
                continue;
              } else if ( value instanceof XsIntegerVal) {
                row.setInteger(index, ((XsIntegerVal)value).getBigInteger().intValueExact());
                continue;
              } else if ( value instanceof XsLongVal) {
                row.setLongInteger(index, ((XsIntegerVal)value).getBigInteger().longValueExact());
                continue;
              }
            }
            // we already validated the column types when we added them, so the value
            // types must not be matching
            throw new IllegalStateException("Column \"" + column.columnName + "\" type " +
              column.type + " is incompatible with data type \"" + value.getClass().getName() + "\"");
          }
        }
        table.insert(row);
      } finally {
        row.close();
      }
    } catch (TableauException e) {
      throw new MarkLogicIOException(e);
    }
  }

  private TableDefinition getTableDef(LinkedHashMap<String,XsAnyAtomicTypeVal> vals) {
    initialize(vals);
    return tableDef;
  }

  private void initialize(LinkedHashMap<String,XsAnyAtomicTypeVal> vals) {
    if ( initialized == false ) {
      synchronized(this) {
        if ( initialized == false ) {
          try {
            if ( columns.size() == 0 ) {
              for (String columnName : vals.keySet()) {
                XsAnyAtomicTypeVal value = vals.get(columnName);
                if (value instanceof XsBooleanVal) {
                  withColumn(columnName, Type.BOOLEAN);
                } else if (value instanceof XsStringVal) {
                  withColumn(columnName, Type.UNICODE_STRING);
                } else if (value instanceof XsIntegerVal ||
                           value instanceof XsIntVal ||
                           value instanceof XsShortVal ||
                           value instanceof XsLongVal ) {
                  withColumn(columnName, Type.INTEGER);
                } else if (value instanceof XsFloatVal ||
                           value instanceof XsDoubleVal ) {
                  withColumn(columnName, Type.DOUBLE);
                } else {
                  throw new IllegalStateException("Unsupported type: " + value.getClass().getName());
                }
              }
            }
            for ( Column column : columns.values() ) {
              if ( column.collation != null ) {
                tableDef.addColumnWithCollation(column.columnName,
                  column.type, column.collation);
              } else {
                tableDef.addColumn(column.columnName, column.type);
              }
            }
            // as of version 10.3.7 Tableau Data Extact SDK only supports
            // one table named "Extract"
            table = extract.addTable("Extract", tableDef);
            initialized = true;
          } catch (TableauException e) {
            throw new MarkLogicIOException(e);
          }
        }
      }
    }
  }

  public TableauDataExtractWriter withColumn(String columnName, Type type) {
    checkSupportedTypes(type);
    columns.put(columnName, new Column(columnName, type));
    return this;
  }

  public TableauDataExtractWriter withColumn(String columnName, Type type, Collation collation) {
    checkSupportedTypes(type);
    columns.put(columnName, new Column(columnName, type, collation));
    return this;
  }

  // closes underlying TableDefinition and Extract
  public void close() {
    extract.close();
  }

  private void checkSupportedTypes(Type type) {
      if ( type != Type.BOOLEAN &&
           type != Type.CHAR_STRING &&
           type != Type.DOUBLE &&
           type != Type.INTEGER &&
           type != Type.UNICODE_STRING )
      {
        throw new IllegalStateException("Type " + type + " is not yet supported");
      }
  }

  private class Column {
    String columnName;
    Type type;
    Collation collation;

    Column(String columnName, Type type) {
      this.columnName = columnName;
      this.type = type;
    }

    Column(String columnName, Type type, Collation collation) {
      this(columnName, type);
      this.collation = collation;
    }
  }
}
