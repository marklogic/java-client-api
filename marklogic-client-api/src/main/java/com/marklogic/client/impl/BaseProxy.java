/*
 * Copyright 2018-2019 MarkLogic Corporation
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
package com.marklogic.client.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.*;
import com.marklogic.client.impl.RESTServices.*;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import java.io.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class BaseProxy {
   final static private Pattern EXTENSION_PATTERN = Pattern.compile("\\.\\w+$");
   static private ObjectMapper mapper = null;
   private String         endpointDir;
   private String         endpointExtension;
   private DatabaseClient db;

   static protected ObjectMapper getMapper() {
      // okay if one thread overwrites another during lazy initialization
      if (mapper == null) {
         mapper = new ObjectMapper();
      }
      return mapper;
   }

   public BaseProxy() {
   }
   public BaseProxy(String endpointDir, JSONWriteHandle serviceDeclaration) {
      this();
      if (serviceDeclaration == null) {
         init(endpointDir);
      } else {
         JsonNode serviceDecl = NodeConverter.handleToJsonNode(serviceDeclaration);

         JsonNode endpointDirProp = serviceDecl.get("endpointDirectory");
         if (endpointDirProp == null) {
            throw new IllegalArgumentException("Service declaration without endpointDirectory property");
         }

         JsonNode endpointExtsnProp = serviceDecl.get("endpointExtension");
         if (endpointExtsnProp != null) {
            endpointExtension = endpointExtsnProp.asText();
            if (endpointExtension == null) {
            } else if (endpointExtension.length() == 0) {
               endpointExtension = null;
            } else {
               endpointExtension = endpointExtension.toLowerCase();
               if (!endpointExtension.startsWith(".")) {
                  endpointExtension = "."+endpointExtension;
               }
               boolean isValid = false;
               for (String extension: new String[]{".mjs", ".sjs", ".xqy"}) {
                  if (!extension.equals(endpointExtension)) continue;
                  isValid = true;
                  break;
      }
               if (!isValid)
                  throw new IllegalArgumentException(
                          "endpoint extension must be mjs, sjs, or xqy and not: "+endpointExtension
                  );
            }
   }

         init(endpointDirProp.asText());
   }
   }
   // backward-compatible constructor for 5.0.x legacy
   public BaseProxy(DatabaseClient db, String endpointDir, JSONWriteHandle serviceDeclaration) {
      this(endpointDir, serviceDeclaration);

      if (db == null) {
         throw new IllegalArgumentException("Cannot connect with null database client");
      } else if (db.getDatabase() != null) {
         throw new IllegalArgumentException("Client cannot specify a database - specified: "+db.getDatabase());
      }

      this.db = db;
   }
   // backward-compatible constructor for 4.x legacy
   public BaseProxy(DatabaseClient db, String endpointDir) {
      this(db, endpointDir, null);
   }
   private void init(String endpointDir) {
      if (endpointDir == null || endpointDir.length() == 0) {
         throw new IllegalArgumentException("Cannot make requests with null or empty endpoint directory");
      }

      this.endpointDir = endpointDir;
   }

   public interface ServerDataType {
   }
   public interface AtomicDataType extends ServerDataType {
   }
   public interface NodeDataType extends ServerDataType {
   }
   public interface CharacterNodeDataType extends NodeDataType {
   }
   static final public class BooleanType implements AtomicDataType {
      final public static String NAME = "boolean";
      static final public String         fromBoolean(Boolean value)                    { return ValueConverter.BooleanToString(value);  }
      static final public Stream<String> fromBoolean(Stream<? extends Boolean> values) { return ValueConverter.BooleanToString(values); }
      static final public String[] fromBoolean(Boolean[] values) { 
          return ValueConverter.convert(values, ValueConverter::BooleanToString); 
      }
      static final public String         fromString(String value)                      { return value;                                  }
      static final public Stream<String> fromString(Stream<String> values)             { return values;                                 }
      static final public Boolean        toBoolean(SingleCallResponse response)   { return ValueConverter.StringToBoolean(response.asString());         }
      static final public Stream<Boolean> toBoolean(MultipleCallResponse response) { return ValueConverter.StringToBoolean(response.asStreamOfString()); }
      static final public String          toString(SingleCallResponse response)    { return response.asString();                                         }
      static final public Stream<String>  toString(MultipleCallResponse response)  { return response.asStreamOfString();                                 }
   }
   static final public class DateType implements AtomicDataType {
      final public static String NAME = "date";
      static final public String         fromLocalDate(LocalDate value)                    { return ValueConverter.LocalDateToString(value);  }
      static final public Stream<String> fromLocalDate(Stream<? extends LocalDate> values) { return ValueConverter.LocalDateToString(values); }
      static final public String[] fromLocalDate(LocalDate[] values) { 
          return ValueConverter.convert(values, ValueConverter::LocalDateToString);
      }
      static final public String         fromString(String value)                          { return value;                                    }
      static final public Stream<String> fromString(Stream<String> values)                 { return values;                                   }
      static final public LocalDate         toLocalDate(SingleCallResponse response)   { return ValueConverter.StringToLocalDate(response.asString());         }
      static final public Stream<LocalDate> toLocalDate(MultipleCallResponse response) { return ValueConverter.StringToLocalDate(response.asStreamOfString()); }
      static final public String            toString(SingleCallResponse response)      { return response.asString();                                           }
      static final public Stream<String>    toString(MultipleCallResponse response)    { return response.asStreamOfString();                                   }
   }
   static final public class DateTimeType implements AtomicDataType {
      final public static String NAME = "dateTime";
      static final public String         fromDate(Date value)                                         { return ValueConverter.DateToString(value);            }
      static final public Stream<String> fromDate(Stream<? extends Date> values)                      { return ValueConverter.DateToString(values);           }
      static final public String[] fromDate(Date[] values) { 
          return ValueConverter.convert(values, ValueConverter::DateToString);
      }
      static final public String         fromLocalDateTime(LocalDateTime value)                       { return ValueConverter.LocalDateTimeToString(value);   }
      static final public Stream<String> fromLocalDateTime(Stream<? extends LocalDateTime> values)    { return ValueConverter.LocalDateTimeToString(values);  }
      static final public String[] fromLocalDateTime(LocalDateTime[] values) { 
          return ValueConverter.convert(values, ValueConverter::LocalDateTimeToString);
      }
      static final public String         fromOffsetDateTime(OffsetDateTime value)                     { return ValueConverter.OffsetDateTimeToString(value);  }
      static final public Stream<String> fromOffsetDateTime(Stream<? extends OffsetDateTime> values)  { return ValueConverter.OffsetDateTimeToString(values); }
      static final public String[] fromOffsetDateTime(OffsetDateTime[] values) { 
          return ValueConverter.convert(values, ValueConverter::OffsetDateTimeToString);
      }
      static final public String         fromString(String value)                                     { return value;                                         }
      static final public Stream<String> fromString(Stream<String> values)                            { return values;                                        }
      static final public Date                   toDate(SingleCallResponse response)             { return ValueConverter.StringToDate(response.asString());                   }
      static final public Stream<Date>           toDate(MultipleCallResponse response)           { return ValueConverter.StringToDate(response.asStreamOfString());           }
      static final public LocalDateTime          toLocalDateTime(SingleCallResponse response)    { return ValueConverter.StringToLocalDateTime(response.asString());          }
      static final public Stream<LocalDateTime>  toLocalDateTime(MultipleCallResponse response)  { return ValueConverter.StringToLocalDateTime(response.asStreamOfString());  }
      static final public OffsetDateTime         toOffsetDateTime(SingleCallResponse response)   { return ValueConverter.StringToOffsetDateTime(response.asString());         }
      static final public Stream<OffsetDateTime> toOffsetDateTime(MultipleCallResponse response) { return ValueConverter.StringToOffsetDateTime(response.asStreamOfString()); }
      static final public String                 toString(SingleCallResponse response)           { return response.asString();                                                }
      static final public Stream<String>         toString(MultipleCallResponse response)         { return response.asStreamOfString();                                        }
   }
   static final public class DayTimeDurationType implements AtomicDataType {
      final public static String NAME = "dayTimeDuration";
      static final public String         fromDuration(Duration value)                    { return ValueConverter.DurationToString(value);  }
      static final public Stream<String> fromDuration(Stream<? extends Duration> values) { return ValueConverter.DurationToString(values); }
      static final public String[] fromDuration(Duration[] values) { 
          return ValueConverter.convert(values, ValueConverter::DurationToString);
      }
      static final public String         fromString(String value)                        { return value;                                   }
      static final public Stream<String> fromString(Stream<String> values)               { return values;                                  }
      static final public Duration         toDuration(SingleCallResponse response)   { return ValueConverter.StringToDuration(response.asString());         }
      static final public Stream<Duration> toDuration(MultipleCallResponse response) { return ValueConverter.StringToDuration(response.asStreamOfString()); }
      static final public String           toString(SingleCallResponse response)     { return response.asString();                                          }
      static final public Stream<String>   toString(MultipleCallResponse response)   { return response.asStreamOfString();                                  }
   }
   static final public class DecimalType implements AtomicDataType {
      final public static String NAME = "decimal";
      static final public String         fromBigDecimal(BigDecimal value)                    { return ValueConverter.BigDecimalToString(value);  }
      static final public Stream<String> fromBigDecimal(Stream<? extends BigDecimal> values) { return ValueConverter.BigDecimalToString(values); }
      static final public String[] fromBigDecimal(BigDecimal[] values) { 
          return ValueConverter.convert(values, ValueConverter::BigDecimalToString);
      }
      static final public String         fromString(String value)                            { return value;                                     }
      static final public Stream<String> fromString(Stream<String> values)                   { return values;                                    }
      static final public BigDecimal         toBigDecimal(SingleCallResponse response)   { return ValueConverter.StringToBigDecimal(response.asString());         }
      static final public Stream<BigDecimal> toBigDecimal(MultipleCallResponse response) { return ValueConverter.StringToBigDecimal(response.asStreamOfString()); }
      static final public String             toString(SingleCallResponse response)       { return response.asString();                                            }
      static final public Stream<String>     toString(MultipleCallResponse response)     { return response.asStreamOfString();                                    }
   }
   static final public class DoubleType implements AtomicDataType {
      final public static String NAME = "double";
      static final public String         fromDouble(Double value)                    { return ValueConverter.DoubleToString(value);  }
      static final public Stream<String> fromDouble(Stream<? extends Double> values) { return ValueConverter.DoubleToString(values); }
      static final public String[] fromDouble(Double[] values) { 
          return ValueConverter.convert(values, ValueConverter::DoubleToString);
      }
      static final public String         fromString(String value)                    { return value;                                 }
      static final public Stream<String> fromString(Stream<String> values)           { return values;                                }
      static final public Double         toDouble(SingleCallResponse response)   { return ValueConverter.StringToDouble(response.asString());         }
      static final public Stream<Double> toDouble(MultipleCallResponse response) { return ValueConverter.StringToDouble(response.asStreamOfString()); }
      static final public String         toString(SingleCallResponse response)   { return response.asString();                                        }
      static final public Stream<String> toString(MultipleCallResponse response) { return response.asStreamOfString();                                }
   }
   static final public class FloatType implements AtomicDataType {
      final public static String NAME = "float";
      static final public String         fromFloat(Float value)                    { return ValueConverter.FloatToString(value);  }
      static final public Stream<String> fromFloat(Stream<? extends Float> values) { return ValueConverter.FloatToString(values); }
      static final public String[] fromFloat(Float[] values) { 
          return ValueConverter.convert(values, ValueConverter::FloatToString);
      }
      static final public String         fromString(String value)                  { return value;                                }
      static final public Stream<String> fromString(Stream<String> values)         { return values;                               }
      static final public Float          toFloat(SingleCallResponse response)    { return ValueConverter.StringToFloat(response.asString());         }
      static final public Stream<Float>  toFloat(MultipleCallResponse response)  { return ValueConverter.StringToFloat(response.asStreamOfString()); }
      static final public String         toString(SingleCallResponse response)   { return response.asString();                                       }
      static final public Stream<String> toString(MultipleCallResponse response) { return response.asStreamOfString();                               }
   }
   static final public class IntegerType implements AtomicDataType {
      final public static String NAME = "int";
      static final public String         fromInteger(Integer value)                    { return ValueConverter.IntegerToString(value);  }
      static final public Stream<String> fromInteger(Stream<? extends Integer> values) { return ValueConverter.IntegerToString(values); }
      static final public String[] fromInteger(Integer[] values) { 
          return ValueConverter.convert(values, ValueConverter::IntegerToString);
      }
      static final public String         fromString(String value)                      { return value;                                  }
      static final public Stream<String> fromString(Stream<String> values)             { return values;                                 }
      static final public Integer         toInteger(SingleCallResponse response)   { return ValueConverter.StringToInteger(response.asString());         }
      static final public Stream<Integer> toInteger(MultipleCallResponse response) { return ValueConverter.StringToInteger(response.asStreamOfString()); }
      static final public String          toString(SingleCallResponse response)    { return response.asString();                                         }
      static final public Stream<String>  toString(MultipleCallResponse response)  { return response.asStreamOfString();                                 }
   }
   static final public class LongType implements AtomicDataType {
      final public static String NAME = "long";
      static final public String         fromLong(Long value)                    { return ValueConverter.LongToString(value);  }
      static final public Stream<String> fromLong(Stream<? extends Long> values) { return ValueConverter.LongToString(values); }
      static final public String[] fromLong(Long[] values) { 
          return ValueConverter.convert(values, ValueConverter::LongToString);
      }
      static final public String         fromString(String value)                { return value;                               }
      static final public Stream<String> fromString(Stream<String> values)       { return values;                              }
      static final public Long           toLong(SingleCallResponse response)     { return ValueConverter.StringToLong(response.asString());         }
      static final public Stream<Long>   toLong(MultipleCallResponse response)   { return ValueConverter.StringToLong(response.asStreamOfString()); }
      static final public String         toString(SingleCallResponse response)   { return response.asString();                                      }
      static final public Stream<String> toString(MultipleCallResponse response) { return response.asStreamOfString();                              }
   }
   static final public class StringType implements AtomicDataType {
      final public static String NAME = "string";
      static final public String         fromString(String value)                { return value;                       }
      static final public Stream<String> fromString(Stream<String> values)       { return values;                      }
      static final public String         toString(SingleCallResponse response)   { return response.asString();         }
      static final public Stream<String> toString(MultipleCallResponse response) { return response.asStreamOfString(); }
   }
   static final public class TimeType implements AtomicDataType {
      final public static String NAME = "time";
      static final public String         fromLocalTime(LocalTime value)                      { return ValueConverter.LocalTimeToString(value);   }
      static final public Stream<String> fromLocalTime(Stream<? extends LocalTime> values)   { return ValueConverter.LocalTimeToString(values);  }
      static final public String[] fromLocalTime(LocalTime[] values) { 
          return ValueConverter.convert(values, ValueConverter::LocalTimeToString);
      }
      static final public String         fromOffsetTime(OffsetTime value)                    { return ValueConverter.OffsetTimeToString(value);  }
      static final public Stream<String> fromOffsetTime(Stream<? extends OffsetTime> values) { return ValueConverter.OffsetTimeToString(values); }
      static final public String[] fromOffsetTime(OffsetTime[] values) { 
          return ValueConverter.convert(values, ValueConverter::OffsetTimeToString);
      }
      static final public String         fromString(String value)                            { return value;                                     }
      static final public Stream<String> fromString(Stream<String> values)                   { return values;                                    }
      static final public LocalTime          toLocalTime(SingleCallResponse response)    { return ValueConverter.StringToLocalTime(response.asString());          }
      static final public Stream<LocalTime>  toLocalTime(MultipleCallResponse response)  { return ValueConverter.StringToLocalTime(response.asStreamOfString());  }
      static final public OffsetTime         toOffsetTime(SingleCallResponse response)   { return ValueConverter.StringToOffsetTime(response.asString());         }
      static final public Stream<OffsetTime> toOffsetTime(MultipleCallResponse response) { return ValueConverter.StringToOffsetTime(response.asStreamOfString()); }
      static final public String             toString(SingleCallResponse response)       { return response.asString();                                            }
      static final public Stream<String>     toString(MultipleCallResponse response)     { return response.asStreamOfString();                                    }
   }
   static final public class UnsignedIntegerType implements AtomicDataType {
      final public static String NAME = "unsignedInt";
      static final public String         fromInteger(Integer value)                    { return ValueConverter.UnsignedIntegerToString(value);  }
      static final public Stream<String> fromInteger(Stream<? extends Integer> values) { return ValueConverter.UnsignedIntegerToString(values); }
      static final public String[] fromInteger(Integer[] values) { 
          return ValueConverter.convert(values, ValueConverter::UnsignedIntegerToString);
      }
      static final public String         fromString(String value)                      { return value;                                          }
      static final public Stream<String> fromString(Stream<String> values)             { return values;                                         }
      static final public Integer         toInteger(SingleCallResponse response)   { return ValueConverter.StringToUnsignedInteger(response.asString());         }
      static final public Stream<Integer> toInteger(MultipleCallResponse response) { return ValueConverter.StringToUnsignedInteger(response.asStreamOfString()); }
      static final public String          toString(SingleCallResponse response)    { return response.asString();                                                 }
      static final public Stream<String>  toString(MultipleCallResponse response)  { return response.asStreamOfString();                                         }
   }
   static final public class UnsignedLongType implements AtomicDataType {
      final public static String NAME = "unsignedLong";
      static final public String         fromLong(Long value)                    { return ValueConverter.UnsignedLongToString(value);  }
      static final public Stream<String> fromLong(Stream<? extends Long> values) { return ValueConverter.UnsignedLongToString(values); }
      static final public String[] fromLong(Long[] values) { 
          return ValueConverter.convert(values, ValueConverter::UnsignedLongToString);
      }
      static final public String         fromString(String value)                { return value;                                       }
      static final public Stream<String> fromString(Stream<String> values)       { return values;                                      }
      static final public Long           toLong(SingleCallResponse response)     { return ValueConverter.StringToUnsignedLong(response.asString());         }
      static final public Stream<Long>   toLong(MultipleCallResponse response)   { return ValueConverter.StringToUnsignedLong(response.asStreamOfString()); }
      static final public String         toString(SingleCallResponse response)   { return response.asString();                                              }
      static final public Stream<String> toString(MultipleCallResponse response) { return response.asStreamOfString();                                      }
   }
   static final public class BinaryDocumentType implements NodeDataType {
      final public static String NAME   = "binaryDocument";
      final public static Format FORMAT = Format.BINARY;
      static final public BinaryWriteHandle fromBinaryWriteHandle(BinaryWriteHandle value) {
         return NodeConverter.BinaryWriter(value);
      }
      static final public Stream<BinaryWriteHandle> fromBinaryWriteHandle(Stream<? extends BinaryWriteHandle> values) {
         return NodeConverter.BinaryWriter(values);
      }
      static final public BinaryWriteHandle[] fromBinaryWriteHandle(BinaryWriteHandle[] values) {
          BinaryWriteHandle[] binaryWriteHandleValues = Stream.of(values).map(value->NodeConverter.BinaryWriter(value)).toArray(BinaryWriteHandle[]::new);
          return NodeConverter.arrayWithFormat(binaryWriteHandleValues, FORMAT);
      }
      static final public BytesHandle fromBytes(byte[] value) {
         return NodeConverter.withFormat(NodeConverter.BytesToHandle(value), FORMAT);
      }
      static final public Stream<BytesHandle> fromBytes(Stream<? extends byte[]> values) {
         return NodeConverter.streamWithFormat(NodeConverter.BytesToHandle(values), FORMAT);
      }
      static final public BytesHandle[] fromBytes(byte[][] values) {
         return NodeConverter.arrayWithFormat(NodeConverter.BytesToHandle(values), FORMAT);
      }
      static final public FileHandle fromFile(File value) {
         return NodeConverter.withFormat(NodeConverter.FileToHandle(value), FORMAT);
      }
      static final public Stream<FileHandle> fromFile(Stream<? extends File> values) {
         return NodeConverter.streamWithFormat(NodeConverter.FileToHandle(values), FORMAT);
      }
      static final public FileHandle[] fromFile(File[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.FileToHandle(values), FORMAT);
      }
      static final public InputStreamHandle fromInputStream(InputStream value) {
         return NodeConverter.withFormat(NodeConverter.InputStreamToHandle(value), FORMAT);
      }
      static final public Stream<InputStreamHandle> fromInputStream(Stream<? extends InputStream> values) {
         return NodeConverter.streamWithFormat(NodeConverter.InputStreamToHandle(values), FORMAT);
      }
      static final public InputStreamHandle[] fromInputStream(InputStream[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.InputStreamToHandle(values), FORMAT);
      }
      static final public OutputStreamHandle fromOutputStreamSender(OutputStreamSender value) {
         return NodeConverter.withFormat(NodeConverter.OutputStreamSenderToHandle(value), FORMAT);
      }
      static final public Stream<OutputStreamHandle> fromOutputStreamSender(Stream<? extends OutputStreamSender> values) {
         return NodeConverter.streamWithFormat(NodeConverter.OutputStreamSenderToHandle(values), FORMAT);
      }
      static final public OutputStreamHandle[] fromOutputStreamSender(OutputStreamSender[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.OutputStreamSenderToHandle(values), FORMAT);
      }

      static final public byte[]                   toBytes(SingleCallResponse response)              { return response.asBytes();               }
      static final public Stream<byte[]>           toBytes(MultipleCallResponse response)            { return response.asStreamOfBytes();       }
      static final public BinaryReadHandle         toBinaryReadHandle(SingleCallResponse response)   {
         return NodeConverter.BinaryReader(NodeConverter.InputStreamToHandle(response.asInputStream()));
      }
      static final public Stream<BinaryReadHandle> toBinaryReadHandle(MultipleCallResponse response) {
         return NodeConverter.BinaryReader(NodeConverter.InputStreamToHandle(response.asStreamOfInputStream()));
      }
      static final public InputStream              toInputStream(SingleCallResponse response)        { return response.asInputStream();         }
      static final public Stream<InputStream>      toInputStream(MultipleCallResponse response)      { return response.asStreamOfInputStream(); }
   }
   static public class JsonDocumentType implements CharacterNodeDataType {
      final public static String NAME   = "jsonDocument";
      final public static Format FORMAT = Format.JSON;
      static final public JSONWriteHandle fromJSONWriteHandle(JSONWriteHandle value) {
         return NodeConverter.JSONWriter(value);
      }
      static final public Stream<JSONWriteHandle> fromJSONWriteHandle(Stream<? extends JSONWriteHandle> values) {
         return NodeConverter.JSONWriter(values);
      }
      static final public JSONWriteHandle[] fromJSONWriteHandle(JSONWriteHandle[] values) {
          JSONWriteHandle[] handleValues = Stream.of(values).map(value->NodeConverter.JSONWriter(value)).toArray(JSONWriteHandle[]::new);
          return NodeConverter.arrayWithFormat(handleValues, FORMAT);
      }
      static final public JacksonHandle fromJsonNode(JsonNode value) {
         return NodeConverter.withFormat(NodeConverter.JsonNodeToHandle(value), FORMAT);
      }
      static final public Stream<JacksonHandle> fromJsonNode(Stream<? extends JsonNode> values) {
         return NodeConverter.streamWithFormat(NodeConverter.JsonNodeToHandle(values), FORMAT);
      }
      static final public JacksonHandle[] fromJsonNode(JsonNode[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.JsonNodeToHandle(values), FORMAT);
      }
      static final public JacksonHandle fromArrayNode(ArrayNode value) {
         return NodeConverter.withFormat(NodeConverter.JsonNodeToHandle(value), FORMAT);
      }
      static final public Stream<JacksonHandle> fromArrayNode(Stream<? extends ArrayNode> values) {
         return NodeConverter.streamWithFormat(NodeConverter.JsonNodeToHandle(values), FORMAT);
      }
      static final public JacksonHandle[] fromArrayNode(ArrayNode[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.JsonNodeToHandle(values), FORMAT);
      }
      static final public JacksonHandle fromObjectNode(ObjectNode value) {
         return NodeConverter.withFormat(NodeConverter.JsonNodeToHandle(value), FORMAT);
      }
      static final public Stream<JacksonHandle> fromObjectNode(Stream<? extends ObjectNode> values) {
         return NodeConverter.streamWithFormat(NodeConverter.JsonNodeToHandle(values), FORMAT);
      }
      static final public JacksonHandle[] fromObjectNode(ObjectNode[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.JsonNodeToHandle(values), FORMAT);
      }
      static final public JacksonParserHandle fromJsonParser(JsonParser value) {
         return NodeConverter.withFormat(NodeConverter.JsonParserToHandle(value), FORMAT);
      }
      static final public Stream<JacksonParserHandle> fromJsonParser(Stream<? extends JsonParser> values) {
         return NodeConverter.streamWithFormat(NodeConverter.JsonParserToHandle(values), FORMAT);
      }
      static final public JacksonParserHandle[] fromJsonParser(JsonParser[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.JsonParserToHandle(values), FORMAT);
      }
      static final public FileHandle fromFile(File value) {
         return NodeConverter.withFormat(NodeConverter.FileToHandle(value), FORMAT);
      }
      static final public Stream<FileHandle> fromFile(Stream<? extends File> values) {
         return NodeConverter.streamWithFormat(NodeConverter.FileToHandle(values), FORMAT);
      }
      static final public FileHandle[] fromFile(File[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.FileToHandle(values), FORMAT);
      }
      static final public InputStreamHandle fromInputStream(InputStream value) {
         return NodeConverter.withFormat(NodeConverter.InputStreamToHandle(value), FORMAT);
      }
      static final public Stream<InputStreamHandle> fromInputStream(Stream<? extends InputStream> values) {
         return NodeConverter.streamWithFormat(NodeConverter.InputStreamToHandle(values), FORMAT);
      }
      static final public InputStreamHandle[] fromInputStream(InputStream[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.InputStreamToHandle(values), FORMAT);
      }
      static final public OutputStreamHandle fromOutputStreamSender(OutputStreamSender value) {
         return NodeConverter.withFormat(NodeConverter.OutputStreamSenderToHandle(value), FORMAT);
      }
      static final public Stream<OutputStreamHandle> fromOutputStreamSender(Stream<? extends OutputStreamSender> values) {
         return NodeConverter.streamWithFormat(NodeConverter.OutputStreamSenderToHandle(values), FORMAT);
      }
      static final public OutputStreamHandle[] fromOutputStreamSender(OutputStreamSender[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.OutputStreamSenderToHandle(values), FORMAT);
      }
      static final public ReaderHandle fromReader(Reader value)                            {
         return NodeConverter.withFormat(NodeConverter.ReaderToHandle(value), FORMAT);
      }
      static final public Stream<ReaderHandle> fromReader(Stream<? extends Reader> values) {
         return NodeConverter.streamWithFormat(NodeConverter.ReaderToHandle(values), FORMAT);
      }
      static final public ReaderHandle[] fromReader(Reader[] values) {
         return NodeConverter.arrayWithFormat (NodeConverter.ReaderToHandle(values) , FORMAT);
      }
      static final public StringHandle fromString(String value)                            {
         return NodeConverter.withFormat(NodeConverter.StringToHandle(value), FORMAT);
      }
      static final public Stream<StringHandle> fromString(Stream<? extends String> values) {
         return NodeConverter.streamWithFormat(NodeConverter.StringToHandle(values), FORMAT);
      }
      static final public StringHandle[] fromString(String[] values) {
          return NodeConverter.arrayWithFormat (NodeConverter.StringToHandle(values) , FORMAT);
      }

      static final public ArrayNode              toArrayNode(SingleCallResponse response)        {
         return NodeConverter.ReaderToArrayNode(response.asReader());
      }
      static final public Stream<ArrayNode>      toArrayNode(MultipleCallResponse response)      {
         return NodeConverter.ReaderToArrayNode(response.asStreamOfReader());
      }
      static final public JsonNode               toJsonNode(SingleCallResponse response)         {
         return NodeConverter.ReaderToJsonNode(response.asReader());
      }
      static final public Stream<JsonNode>       toJsonNode(MultipleCallResponse response)       {
         return NodeConverter.ReaderToJsonNode(response.asStreamOfReader());
      }
      static final public ObjectNode             toObjectNode(SingleCallResponse response)       {
         return NodeConverter.ReaderToObjectNode(response.asReader());
      }
      static final public Stream<ObjectNode>     toObjectNode(MultipleCallResponse response)     {
         return NodeConverter.ReaderToObjectNode(response.asStreamOfReader());
      }
      static final public JsonParser             toJsonParser(SingleCallResponse response)       {
         return NodeConverter.ReaderToJsonParser(response.asReader());
      }
      static final public Stream<JsonParser>     toJsonParser(MultipleCallResponse response)     {
         return NodeConverter.ReaderToJsonParser(response.asStreamOfReader());
      }
      static final public byte[]                 toBytes(SingleCallResponse response)            { return response.asBytes();               }
      static final public Stream<byte[]>         toBytes(MultipleCallResponse response)          { return response.asStreamOfBytes();       }
      static final public InputStream            toInputStream(SingleCallResponse response)      { return response.asInputStream();         }
      static final public Stream<InputStream>    toInputStream(MultipleCallResponse response)    { return response.asStreamOfInputStream(); }
      static final public Reader                 toReader(SingleCallResponse response)           { return response.asReader();              }
      static final public Stream<Reader>         toReader(MultipleCallResponse response)         { return response.asStreamOfReader();      }
      static final public JSONReadHandle         toJSONReadHandle(SingleCallResponse response)   {
         return NodeConverter.JSONReader(NodeConverter.ReaderToHandle(response.asReader()));
      }
      static final public Stream<JSONReadHandle> toJSONReadHandle(MultipleCallResponse response) {
         return NodeConverter.JSONReader(NodeConverter.ReaderToHandle(response.asStreamOfReader()));
      }
      static final public String                 toString(SingleCallResponse response)           { return response.asString();              }
      static final public Stream<String>         toString(MultipleCallResponse response)         { return response.asStreamOfString();      }
   }
   static final public class ArrayType extends JsonDocumentType {
      final public static String NAME   = "array";
      final public static Format FORMAT = Format.JSON;
   }
   static final public class ObjectType extends JsonDocumentType {
      final public static String NAME   = "object";
      final public static Format FORMAT = Format.JSON;
   }
   static final public class TextDocumentType implements CharacterNodeDataType {
      final public static String NAME   = "textDocument";
      final public static Format FORMAT = Format.TEXT;
      static final public TextWriteHandle fromTextWriteHandle(TextWriteHandle value) {
         return NodeConverter.TextWriter(value);
      }
      static final public Stream<TextWriteHandle> fromTextWriteHandle(Stream<? extends TextWriteHandle> values) {
         return NodeConverter.TextWriter(values);
      }
      static final public TextWriteHandle[] fromTextWriteHandle(TextWriteHandle[] values) {
          TextWriteHandle[] handleValues = Stream.of(values).map(value->NodeConverter.TextWriter(value)).toArray(TextWriteHandle[]::new);
          return NodeConverter.arrayWithFormat(handleValues, FORMAT);
      }
      static final public FileHandle fromFile(File value) {
         return NodeConverter.withFormat(NodeConverter.FileToHandle(value), FORMAT);
      }
      static final public Stream<FileHandle> fromFile(Stream<? extends File> values) {
         return NodeConverter.streamWithFormat(NodeConverter.FileToHandle(values), FORMAT);
      }
      static final public FileHandle[] fromFile(File[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.FileToHandle(values), FORMAT);
      }
      static final public InputStreamHandle fromInputStream(InputStream value) {
         return NodeConverter.withFormat(NodeConverter.InputStreamToHandle(value), FORMAT);
      }
      static final public Stream<InputStreamHandle> fromInputStream(Stream<? extends InputStream> values) {
         return NodeConverter.streamWithFormat(NodeConverter.InputStreamToHandle(values), FORMAT);
      }
      static final public InputStreamHandle[] fromInputStream(InputStream[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.InputStreamToHandle(values), FORMAT);
      }
      static final public OutputStreamHandle fromOutputStreamSender(OutputStreamSender value) {
         return NodeConverter.withFormat(NodeConverter.OutputStreamSenderToHandle(value), FORMAT);
      }
      static final public Stream<OutputStreamHandle> fromOutputStreamSender(Stream<? extends OutputStreamSender> values) {
         return NodeConverter.streamWithFormat(NodeConverter.OutputStreamSenderToHandle(values), FORMAT);
      }
      static final public OutputStreamHandle[] fromOutputStreamSender(OutputStreamSender[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.OutputStreamSenderToHandle(values), FORMAT);
      }
      static final public ReaderHandle fromReader(Reader value) {
         return NodeConverter.withFormat(NodeConverter.ReaderToHandle(value), FORMAT);
      }
      static final public Stream<ReaderHandle> fromReader(Stream<? extends Reader> values) {
         return NodeConverter.streamWithFormat(NodeConverter.ReaderToHandle(values), FORMAT);
      }
      static final public ReaderHandle[] fromReader(Reader[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.ReaderToHandle(values), FORMAT);
      }
      static final public StringHandle fromString(String value) {
         return NodeConverter.withFormat(NodeConverter.StringToHandle(value), FORMAT);
      }
      static final public Stream<StringHandle> fromString(Stream<String> values) {
         return NodeConverter.streamWithFormat(NodeConverter.StringToHandle(values), FORMAT);
      }
      static final public StringHandle[] fromString(String[] values) {
         return NodeConverter.arrayWithFormat(NodeConverter.StringToHandle(values), FORMAT);
      }

      static final public byte[]              toBytes(SingleCallResponse response)               { return response.asBytes();               }
      static final public Stream<byte[]>      toBytes(MultipleCallResponse response)             { return response.asStreamOfBytes();       }
      static final public InputStream         toInputStream(SingleCallResponse response)         { return response.asInputStream();         }
      static final public Stream<InputStream> toInputStream(MultipleCallResponse response)       { return response.asStreamOfInputStream(); }
      static final public Reader              toReader(SingleCallResponse response)              { return response.asReader();              }
      static final public Stream<Reader>      toReader(MultipleCallResponse response)            { return response.asStreamOfReader();      }
      static final public TextReadHandle      toTextReadHandle(SingleCallResponse response)      {
         return NodeConverter.TextReader(NodeConverter.ReaderToHandle(response.asReader()));
      }
      static final public Stream<TextReadHandle> toTextReadHandle(MultipleCallResponse response) {
         return NodeConverter.TextReader(NodeConverter.ReaderToHandle(response.asStreamOfReader()));
      }
      static final public String              toString(SingleCallResponse response)              { return response.asString();              }
      static final public Stream<String>      toString(MultipleCallResponse response)            { return response.asStreamOfString();      }
   }
   static final public class XmlDocumentType implements CharacterNodeDataType {
      final public static String NAME   = "xmlDocument";
      final public static Format FORMAT = Format.XML;
      static final public XMLWriteHandle fromXMLWriteHandle(XMLWriteHandle value) {
         return NodeConverter.XMLWriter(value);
      }
      static final public Stream<XMLWriteHandle> fromXMLWriteHandle(Stream<? extends XMLWriteHandle> values) {
         return NodeConverter.XMLWriter(values);
      }
      static final public XMLWriteHandle[] fromXMLWriteHandle(XMLWriteHandle[] values) {
          XMLWriteHandle[] handleValues = Stream.of(values).map(value-> NodeConverter.XMLWriter(value)).toArray(XMLWriteHandle[]::new);
          return NodeConverter.arrayWithFormat(handleValues, FORMAT);
      }
      static final public DOMHandle fromDocument(Document value) {
         return NodeConverter.withFormat(NodeConverter.DocumentToHandle(value), FORMAT);
      }
      static final public Stream<DOMHandle> fromDocument(Stream<? extends Document> values) {
         return NodeConverter.streamWithFormat(NodeConverter.DocumentToHandle(values), FORMAT);
      }
      static final public DOMHandle[] fromDocument(Document[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.DocumentToHandle(values), FORMAT);
      }
      static final public InputSourceHandle fromInputSource(InputSource value) {
         return NodeConverter.withFormat(NodeConverter.InputSourceToHandle(value), FORMAT);
      }
      static final public Stream<InputSourceHandle> fromInputSource(Stream<? extends InputSource> values) {
         return NodeConverter.streamWithFormat(NodeConverter.InputSourceToHandle(values), FORMAT);
      }
      static final public InputSourceHandle[] fromInputSource(InputSource[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.InputSourceToHandle(values), FORMAT);
      }
      static final public SourceHandle fromSource(Source value) {
         return NodeConverter.withFormat(NodeConverter.SourceToHandle(value), FORMAT);
      }
      static final public Stream<SourceHandle> fromSource(Stream<? extends Source> values) {
         return NodeConverter.streamWithFormat(NodeConverter.SourceToHandle(values), FORMAT);
      }
      static final public SourceHandle[] fromSource(Source[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.SourceToHandle(values), FORMAT);
      }
      static final public XMLEventReaderHandle fromXMLEventReader(XMLEventReader value) {
         return NodeConverter.withFormat(NodeConverter.XMLEventReaderToHandle(value), FORMAT);
      }
      static final public Stream<XMLEventReaderHandle> fromXMLEventReader(Stream<? extends XMLEventReader> values) {
         return NodeConverter.streamWithFormat(NodeConverter.XMLEventReaderToHandle(values), FORMAT);
      }
      static final public XMLEventReaderHandle[] fromXMLEventReader(XMLEventReader[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.XMLEventReaderToHandle(values), FORMAT);
      }
      static final public XMLStreamReaderHandle fromXMLStreamReader(XMLStreamReader value) {
         return NodeConverter.withFormat(NodeConverter.XMLStreamReaderToHandle(value), FORMAT);
      }
      static final public Stream<XMLStreamReaderHandle> fromXMLStreamReader(Stream<? extends XMLStreamReader> values) {
         return NodeConverter.streamWithFormat(NodeConverter.XMLStreamReaderToHandle(values), FORMAT);
      }
      static final public XMLStreamReaderHandle[] fromXMLStreamReader(XMLStreamReader[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.XMLStreamReaderToHandle(values), FORMAT);
      }
      static final public FileHandle fromFile(File value) {
         return NodeConverter.withFormat(NodeConverter.FileToHandle(value), FORMAT);
      }
      static final public Stream<FileHandle> fromFile(Stream<? extends File> values) {
         return NodeConverter.streamWithFormat(NodeConverter.FileToHandle(values), FORMAT);
      }
      static final public FileHandle[] fromFile(File[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.FileToHandle(values), FORMAT);
      }
      static final public InputStreamHandle fromInputStream(InputStream value) {
         return NodeConverter.withFormat(NodeConverter.InputStreamToHandle(value), FORMAT);
      }
      static final public Stream<InputStreamHandle> fromInputStream(Stream<? extends InputStream> values)  {
         return NodeConverter.streamWithFormat(NodeConverter.InputStreamToHandle(values), FORMAT);
      }
      static final public InputStreamHandle[] fromInputStream(InputStream[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.InputStreamToHandle(values), FORMAT);
      }
      static final public OutputStreamHandle fromOutputStreamSender(OutputStreamSender value) {
         return NodeConverter.withFormat(NodeConverter.OutputStreamSenderToHandle(value), FORMAT);
      }
      static final public Stream<OutputStreamHandle> fromOutputStreamSender(Stream<? extends OutputStreamSender> values) {
         return NodeConverter.streamWithFormat(NodeConverter.OutputStreamSenderToHandle(values), FORMAT);
      }
      static final public OutputStreamHandle[] fromOutputStreamSender(OutputStreamSender[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.OutputStreamSenderToHandle(values), FORMAT);
      }
      static final public ReaderHandle fromReader(Reader value) {
         return NodeConverter.withFormat(NodeConverter.ReaderToHandle(value), FORMAT);
      }
      static final public Stream<ReaderHandle> fromReader(Stream<? extends Reader> values) {
         return NodeConverter.streamWithFormat(NodeConverter.ReaderToHandle(values), FORMAT);
      }
      static final public ReaderHandle[] fromReader(Reader[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.ReaderToHandle(values), FORMAT);
      }
      static final public StringHandle fromString(String value) {
         return NodeConverter.withFormat(NodeConverter.StringToHandle(value), FORMAT);
      }
      static final public Stream<StringHandle> fromString(Stream<? extends String> values) {
         return NodeConverter.streamWithFormat(NodeConverter.StringToHandle(values), FORMAT);
      }
      static final public StringHandle[] fromString(String[] values) {
          return NodeConverter.arrayWithFormat(NodeConverter.StringToHandle(values), FORMAT);
      }

      static final public Document              toDocument(SingleCallResponse response)           {
         return NodeConverter.InputStreamToDocument(response.asInputStream());
      }
      static final public Stream<Document>      toDocument(MultipleCallResponse response)         {
         return NodeConverter.InputStreamToDocument(response.asStreamOfInputStream());
      }
      static final public InputSource           toInputSource(SingleCallResponse response)        {
         return NodeConverter.ReaderToInputSource(response.asReader());
      }
      static final public Stream<InputSource>   toInputSource(MultipleCallResponse response)      {
         return NodeConverter.ReaderToInputSource(response.asStreamOfReader());
      }
      static final public Source                toSource(SingleCallResponse response)             {
         return NodeConverter.ReaderToSource(response.asReader());
      }
      static final public Stream<Source>        toSource(MultipleCallResponse response)           {
         return NodeConverter.ReaderToSource(response.asStreamOfReader());
      }
      static final public XMLEventReader        toXMLEventReader(SingleCallResponse response)     {
         return NodeConverter.ReaderToXMLEventReader(response.asReader());
      }
      static final public Stream<XMLEventReader>toXMLEventReader(MultipleCallResponse response)   {
         return NodeConverter.ReaderToXMLEventReader(response.asStreamOfReader());
      }
      static final public XMLStreamReader       toXMLStreamReader(SingleCallResponse response)    {
         return NodeConverter.ReaderToXMLStreamReader(response.asReader());
      }
      static final public Stream<XMLStreamReader>toXMLStreamReader(MultipleCallResponse response) {
         return NodeConverter.ReaderToXMLStreamReader(response.asStreamOfReader());
      }
      static final public byte[]                toBytes(SingleCallResponse response)           { return response.asBytes();               }
      static final public Stream<byte[]>        toBytes(MultipleCallResponse response)         { return response.asStreamOfBytes();       }
      static final public InputStream           toInputStream(SingleCallResponse response)     { return response.asInputStream();         }
      static final public Stream<InputStream>   toInputStream(MultipleCallResponse response)   { return response.asStreamOfInputStream(); }
      static final public Reader                toReader(SingleCallResponse response)          { return response.asReader();              }
      static final public Stream<Reader>        toReader(MultipleCallResponse response)        { return response.asStreamOfReader();      }
      static final public XMLReadHandle         toXMLReadHandle(SingleCallResponse response)   {
         return NodeConverter.XMLReader(NodeConverter.ReaderToHandle(response.asReader()));
      }
      static final public Stream<XMLReadHandle> toXMLReadHandle(MultipleCallResponse response) {
         return NodeConverter.XMLReader(NodeConverter.ReaderToHandle(response.asStreamOfReader()));
      }
      static final public String                toString(SingleCallResponse response)          { return response.asString();              }
      static final public Stream<String>        toString(MultipleCallResponse response)        { return response.asStreamOfString();      }
   }
   public enum ParameterValuesKind {
      NONE, SINGLE_ATOMIC, SINGLE_NODE, MULTIPLE_ATOMICS, MULTIPLE_NODES, MULTIPLE_MIXED;

      static public ParameterValuesKind forNodeCount(int nodeCount) {
         switch (nodeCount) {
            case 0:  return NONE;
            case 1:  return SINGLE_NODE;
            default: return MULTIPLE_NODES;
         }
      }
   }

   public DBFunctionRequest request(String defaultModule, ParameterValuesKind paramsKind) {
      final String module = (endpointExtension == null) ? defaultModule :
              EXTENSION_PATTERN.matcher(defaultModule).replaceFirst(endpointExtension);
      DBFunctionRequest request = request(endpointDir, module, paramsKind);
      // backward-compatible initialization
      return (db != null) ? request.on(db) : request;
   }
   static public DBFunctionRequest request(String endpointDir, String module, ParameterValuesKind paramsKind) {
      if (endpointDir == null || endpointDir.length() == 0) {
         throw new IllegalArgumentException("Cannot make requests with null or empty endpoint directory");
      }
      if (module == null) {
         throw new IllegalArgumentException("null module");
      }

      return new DBFunctionRequest(endpointDir, module, paramsKind);
   }
   static public DBFunctionRequest moduleRequest(String endpoint, ParameterValuesKind paramsKind) {
      return new DBFunctionRequest(endpoint, paramsKind);
   }

   static public SingleAtomicCallField atomicParam(String paramName, boolean isNullable, String value) {
      return isParamNull(paramName, isNullable, value)  ? null : new SingleAtomicCallField(paramName, value);
   }
   static public MultipleAtomicCallField atomicParam(String paramName, boolean isNullable, Stream<String> values) {
      return isParamNull(paramName, isNullable, values) ? null : new UnbufferedMultipleAtomicCallField(paramName, values);
   }
   static public SingleNodeCallField documentParam(String paramName, boolean isNullable, BufferableHandle value) {
      return isParamNull(paramName, isNullable, value)  ? null : new SingleNodeCallField(paramName, value);
   }
   static public MultipleNodeCallField documentParam(String paramName, boolean isNullable, Stream<? extends BufferableHandle> values) {
      return isParamNull(paramName, isNullable, values) ? null : new UnbufferedMultipleNodeCallField(paramName, values);
   }
   static public MultipleNodeCallField documentParam(String paramName, boolean isNullable, BufferableHandle[] values) {
      return isParamNull(paramName, isNullable, values) ? null : new BufferedMultipleNodeCallField(paramName, values);
   }
   static protected boolean isParamNull(String paramName, boolean isNullable, Object value) {
      if (value != null) {
         return false;
      } else if (!isNullable) {
         throw new RequiredParamException("null value for required parameter: " + paramName);
      }
      return true;
   }

   static public class DBFunctionRequest {
      private RESTServices        services;
      private String              endpoint;
      private ParameterValuesKind paramsKind;
      private CallField[]         params;
      private SessionState        session;
      private HttpMethod          method = HttpMethod.POST;
      private DBFunctionRequest(RESTServices services, String endpointDir, String module, ParameterValuesKind paramsKind) {
         this(services, endpointDir + module, paramsKind);
      }
      private DBFunctionRequest(RESTServices services, String endpoint, ParameterValuesKind paramsKind, HttpMethod method) {
         this(services, endpoint, paramsKind);
         this.method = method;
      }
      private DBFunctionRequest(RESTServices services, String endpoint, ParameterValuesKind paramsKind) {
         this(endpoint, paramsKind);
         this.services   = services;
      }
      private DBFunctionRequest(String endpointDir, String module, ParameterValuesKind paramsKind) {
         this(endpointDir + module, paramsKind);
      }
      private DBFunctionRequest(String endpoint, ParameterValuesKind paramsKind) {
         this.endpoint   = endpoint;
         this.paramsKind = paramsKind;
      }

      public DBFunctionRequest withMethod(String method) {
         this.method = HttpMethod.valueOf(method);
         return this;
      }

// TODO: two different classes instead of cloning DBFunctionRequest; distinguish in signatures
      public DBFunctionRequest on(DatabaseClient db) {
         if (db == null) {
            throw new IllegalArgumentException("Cannot connect with null database client");
         } else if (!(db instanceof DatabaseClientImpl)) {
            throw new IllegalArgumentException("Cannot connect with non-standard implementation of database client");
         } else if (db.getDatabase() != null) {
            throw new IllegalArgumentException("Client cannot specify a database - specified: "+db.getDatabase());
         }
         return new DBFunctionRequest(
             ((DatabaseClientImpl) db).getServices(), this.endpoint, this.paramsKind, this.method
         );
      }

      public DBFunctionRequest withSession() {
         return this;
      }
      public DBFunctionRequest withSession(String paramName, SessionState session, boolean isNullable) {
         if (session != null) {
            this.session = session;
         } else if (!isNullable) {
            throw new RequiredParamException("null value for required session parameter: " + paramName);
         }
         return this;
      }
      public DBFunctionRequest withParams(CallField... params) {
         this.params = params;
         return this;
      }

      private CallRequest makeRequest() {
         switch(paramsKind) {
            case NONE:
               return services.makeEmptyRequest(endpoint, method, session);
            case SINGLE_ATOMIC:
            case MULTIPLE_ATOMICS:
               if (params == null || params.length == 0 || (params.length == 1 && params[0] == null)) {
                  return services.makeEmptyRequest(endpoint, method, session);
               }
               return services.makeAtomicBodyRequest(endpoint, method, session, params);
            case SINGLE_NODE:
               if (params == null || params.length == 0) {
                  return services.makeEmptyRequest(endpoint, method, session);
               } else if (params.length > 1) {
                  throw new InternalError("multiple parameters instead of single node");
               } else if (params[0] == null) {
                  return services.makeEmptyRequest(endpoint, method, session);
               } else if (params.length > 1 || !(params[0] instanceof SingleNodeCallField)) {
                  throw new InternalError("invalid parameter type instead of single node: "+params[0].getClass().getName());
               }
               return services.makeNodeBodyRequest(endpoint, method, session, params);
            case MULTIPLE_NODES:
            case MULTIPLE_MIXED:
               return services.makeNodeBodyRequest(endpoint, method, session, params);
            default:
               throw new InternalError("unknown parameters kind: "+paramsKind.name());
         }
      }

      public void responseNone() {
         makeRequest().withEmptyResponse();
      }
      public SingleCallResponse responseSingle(boolean isNullable, Format returnFormat) {
         SingleCallResponse responsedef = makeRequest().withDocumentResponse(
                 (returnFormat == null) ? Format.TEXT : returnFormat
         );
         if (responsedef.isNull() && !isNullable) {
            responsedef.close();
            throw new RequiredReturnException("null for required single return value");
         }
         return responsedef;
      }
      public MultipleCallResponse responseMultiple(boolean isNullable, Format returnFormat) {
         MultipleCallResponse responsedef = makeRequest().withMultipartMixedResponse(
                 (returnFormat == null) ? Format.TEXT : returnFormat
         );
         if (responsedef.isNull() && !isNullable) {
            throw new RequiredReturnException("null for required multiple return value");
         }
         return responsedef;
      }
   }

   static public class RequiredParamException extends IllegalArgumentException {
      public RequiredParamException(String s) {
         super(s);
      }
   }
   static public class RequiredReturnException extends IllegalArgumentException {
      public RequiredReturnException(String s) {
         super(s);
      }
   }

   public SessionState newSessionState() {
      return new SessionStateImpl();
   }
}
