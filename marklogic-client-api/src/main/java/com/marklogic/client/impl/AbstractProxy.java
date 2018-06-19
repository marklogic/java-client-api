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
package com.marklogic.client.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import okhttp3.*;

import okio.BufferedSink;
import okio.Okio;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.internet.MimeMultipart;

// TODO: better name?
public class AbstractProxy {
   private final static String UTF8_ID = StandardCharsets.UTF_8.toString();

   private final static MediaType URLENCODED_MIME_TYPE =
         MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");

   private static ObjectMapper mapper = null;

   private DatabaseClient db;
   private String         endpointDir;

   private OkHttpServicesPlus services;

   static protected ObjectMapper getMapper() {
      // okay if one thread overwrites another during lazy initialization
      if (mapper == null) {
         mapper = new ObjectMapper();
      }
      return mapper;
   }

   protected AbstractProxy(DatabaseClient db, String endpointDir) {
      if (db == null) {
         throw new IllegalStateException("Cannot connect with null database client");
      }
      if (endpointDir == null || endpointDir.length() == 0) {
         throw new IllegalStateException("Cannot make requests with null or empty endpoint directory");
      }

      this.db          = db;
      this.endpointDir = endpointDir;

      OkHttpClient client = (OkHttpClient) db.getClientImplementation();

      DatabaseClientImpl dbImpl = (DatabaseClientImpl) db;

// TODO:  baseUrl for mocking
      HttpUrl baseUrl = new HttpUrl.Builder()
// TODO:  scheme from security context
//            .scheme(dbImpl.getSecurityContext().getSSLContext() == null ? "http" : "https")
            .scheme("http")
            .host(dbImpl.getHost())
            .port(dbImpl.getPort())
            .encodedPath("/")
            .build();

      services = new OkHttpServicesPlus(client, baseUrl);
// TODO: rely on ping from base Java to establish first connection for streaming inputs
      try {
         Response response = client.newCall(new Request.Builder().url(baseUrl).head().build()).execute();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   static protected interface ServerDataType {
   }
   static protected interface AtomicDataType extends ServerDataType {
   }
   static protected interface NodeDataType extends ServerDataType {
// TODO: format for passing to asHandle()???
   }
   static final protected class BooleanType implements AtomicDataType {
      static final public String         fromBoolean(Boolean value)                    { return ValueConverter.BooleanToString(value);  }
      static final public Stream<String> fromBoolean(Stream<? extends Boolean> values) { return ValueConverter.BooleanToString(values); }
      static final public String         fromString(String value)                      { return value;                                  }
      static final public Stream<String> fromString(Stream<String> values)             { return values;                                 }
      static final public Boolean         toBoolean(SingleResponse response)   { return ValueConverter.StringToBoolean(response.asString());         }
      static final public Stream<Boolean> toBoolean(MultipleResponse response) { return ValueConverter.StringToBoolean(response.asStreamOfString()); }
      static final public String          toString(SingleResponse response)    { return response.asString();                                         }
      static final public Stream<String>  toString(MultipleResponse response)  { return response.asStreamOfString();                                 }
   }
   static final protected class DateType implements AtomicDataType {
      static final public String         fromLocalDate(LocalDate value)                    { return ValueConverter.LocalDateToString(value);  }
      static final public Stream<String> fromLocalDate(Stream<? extends LocalDate> values) { return ValueConverter.LocalDateToString(values); }
      static final public String         fromString(String value)                          { return value;                                    }
      static final public Stream<String> fromString(Stream<String> values)                 { return values;                                   }
      static final public LocalDate         toLocalDate(SingleResponse response)   { return ValueConverter.StringToLocalDate(response.asString());         }
      static final public Stream<LocalDate> toLocalDate(MultipleResponse response) { return ValueConverter.StringToLocalDate(response.asStreamOfString()); }
      static final public String            toString(SingleResponse response)      { return response.asString();                                           }
      static final public Stream<String>    toString(MultipleResponse response)    { return response.asStreamOfString();                                   }
   }
   static final protected class DateTimeType implements AtomicDataType {
      static final public String         fromDate(Date value)                                         { return ValueConverter.DateToString(value);            }
      static final public Stream<String> fromDate(Stream<? extends Date> values)                      { return ValueConverter.DateToString(values);           }
      static final public String         fromLocalDateTime(LocalDateTime value)                       { return ValueConverter.LocalDateTimeToString(value);   }
      static final public Stream<String> fromLocalDateTime(Stream<? extends LocalDateTime> values)    { return ValueConverter.LocalDateTimeToString(values);  }
      static final public String         fromOffsetDateTime(OffsetDateTime value)                     { return ValueConverter.OffsetDateTimeToString(value);  }
      static final public Stream<String> fromOffsetDateTime(Stream<? extends OffsetDateTime> values)  { return ValueConverter.OffsetDateTimeToString(values); }
      static final public String         fromString(String value)                                     { return value;                                         }
      static final public Stream<String> fromString(Stream<String> values)                            { return values;                                        }
      static final public Date                   toDate(SingleResponse response)             { return ValueConverter.StringToDate(response.asString());                   }
      static final public Stream<Date>           toDate(MultipleResponse response)           { return ValueConverter.StringToDate(response.asStreamOfString());           }
      static final public LocalDateTime          toLocalDateTime(SingleResponse response)    { return ValueConverter.StringToLocalDateTime(response.asString());          }
      static final public Stream<LocalDateTime>  toLocalDateTime(MultipleResponse response)  { return ValueConverter.StringToLocalDateTime(response.asStreamOfString());  }
      static final public OffsetDateTime         toOffsetDateTime(SingleResponse response)   { return ValueConverter.StringToOffsetDateTime(response.asString());         }
      static final public Stream<OffsetDateTime> toOffsetDateTime(MultipleResponse response) { return ValueConverter.StringToOffsetDateTime(response.asStreamOfString()); }
      static final public String                 toString(SingleResponse response)           { return response.asString();                                                }
      static final public Stream<String>         toString(MultipleResponse response)         { return response.asStreamOfString();                                        }
   }
   static final protected class DayTimeDurationType implements AtomicDataType {
      static final public String         fromDuration(Duration value)                    { return ValueConverter.DurationToString(value);  }
      static final public Stream<String> fromDuration(Stream<? extends Duration> values) { return ValueConverter.DurationToString(values); }
      static final public String         fromString(String value)                        { return value;                                   }
      static final public Stream<String> fromString(Stream<String> values)               { return values;                                  }
      static final public Duration         toDuration(SingleResponse response)   { return ValueConverter.StringToDuration(response.asString());         }
      static final public Stream<Duration> toDuration(MultipleResponse response) { return ValueConverter.StringToDuration(response.asStreamOfString()); }
      static final public String           toString(SingleResponse response)     { return response.asString();                                          }
      static final public Stream<String>   toString(MultipleResponse response)   { return response.asStreamOfString();                                  }
   }
   static final protected class DecimalType implements AtomicDataType {
      static final public String         fromBigDecimal(BigDecimal value)                    { return ValueConverter.BigDecimalToString(value);  }
      static final public Stream<String> fromBigDecimal(Stream<? extends BigDecimal> values) { return ValueConverter.BigDecimalToString(values); }
      static final public String         fromString(String value)                            { return value;                                     }
      static final public Stream<String> fromString(Stream<String> values)                   { return values;                                    }
      static final public BigDecimal         toBigDecimal(SingleResponse response)   { return ValueConverter.StringToBigDecimal(response.asString());         }
      static final public Stream<BigDecimal> toBigDecimal(MultipleResponse response) { return ValueConverter.StringToBigDecimal(response.asStreamOfString()); }
      static final public String             toString(SingleResponse response)       { return response.asString();                                            }
      static final public Stream<String>     toString(MultipleResponse response)     { return response.asStreamOfString();                                    }
   }
   static final protected class DoubleType implements AtomicDataType {
      static final public String         fromDouble(Double value)                    { return ValueConverter.DoubleToString(value);  }
      static final public Stream<String> fromDouble(Stream<? extends Double> values) { return ValueConverter.DoubleToString(values); }
      static final public String         fromString(String value)                    { return value;                                 }
      static final public Stream<String> fromString(Stream<String> values)           { return values;                                }
      static final public Double         toDouble(SingleResponse response)   { return ValueConverter.StringToDouble(response.asString());         }
      static final public Stream<Double> toDouble(MultipleResponse response) { return ValueConverter.StringToDouble(response.asStreamOfString()); }
      static final public String         toString(SingleResponse response)   { return response.asString();                                        }
      static final public Stream<String> toString(MultipleResponse response) { return response.asStreamOfString();                                }
   }
   static final protected class FloatType implements AtomicDataType {
      static final public String         fromFloat(Float value)                    { return ValueConverter.FloatToString(value);  }
      static final public Stream<String> fromFloat(Stream<? extends Float> values) { return ValueConverter.FloatToString(values); }
      static final public String         fromString(String value)                  { return value;                                }
      static final public Stream<String> fromString(Stream<String> values)         { return values;                               }
      static final public Float          toFloat(SingleResponse response)    { return ValueConverter.StringToFloat(response.asString());         }
      static final public Stream<Float>  toFloat(MultipleResponse response)  { return ValueConverter.StringToFloat(response.asStreamOfString()); }
      static final public String         toString(SingleResponse response)   { return response.asString();                                       }
      static final public Stream<String> toString(MultipleResponse response) { return response.asStreamOfString();                               }
   }
   static final protected class IntegerType implements AtomicDataType {
      static final public String         fromInteger(Integer value)                    { return ValueConverter.IntegerToString(value);  }
      static final public Stream<String> fromInteger(Stream<? extends Integer> values) { return ValueConverter.IntegerToString(values); }
      static final public String         fromString(String value)                      { return value;                                  }
      static final public Stream<String> fromString(Stream<String> values)             { return values;                                 }
      static final public Integer         toInteger(SingleResponse response)   { return ValueConverter.StringToInteger(response.asString());         }
      static final public Stream<Integer> toInteger(MultipleResponse response) { return ValueConverter.StringToInteger(response.asStreamOfString()); }
      static final public String          toString(SingleResponse response)    { return response.asString();                                         }
      static final public Stream<String>  toString(MultipleResponse response)  { return response.asStreamOfString();                                 }
   }
   static final protected class LongType implements AtomicDataType {
      static final public String         fromLong(Long value)                    { return ValueConverter.LongToString(value);  }
      static final public Stream<String> fromLong(Stream<? extends Long> values) { return ValueConverter.LongToString(values); }
      static final public String         fromString(String value)                { return value;                               }
      static final public Stream<String> fromString(Stream<String> values)       { return values;                              }
      static final public Long           toLong(SingleResponse response)     { return ValueConverter.StringToLong(response.asString());         }
      static final public Stream<Long>   toLong(MultipleResponse response)   { return ValueConverter.StringToLong(response.asStreamOfString()); }
      static final public String         toString(SingleResponse response)   { return response.asString();                                      }
      static final public Stream<String> toString(MultipleResponse response) { return response.asStreamOfString();                              }
   }
   static final protected class StringType implements AtomicDataType {
      static final public String         fromString(String value)          { return value;  }
      static final public Stream<String> fromString(Stream<String> values) { return values; }
      static final public String         toString(SingleResponse response)   { return response.asString();         }
      static final public Stream<String> toString(MultipleResponse response) { return response.asStreamOfString(); }
   }
   static final protected class TimeType implements AtomicDataType {
      static final public String         fromLocalTime(LocalTime value)                      { return ValueConverter.LocalTimeToString(value);   }
      static final public Stream<String> fromLocalTime(Stream<? extends LocalTime> values)   { return ValueConverter.LocalTimeToString(values);  }
      static final public String         fromOffsetTime(OffsetTime value)                    { return ValueConverter.OffsetTimeToString(value);  }
      static final public Stream<String> fromOffsetTime(Stream<? extends OffsetTime> values) { return ValueConverter.OffsetTimeToString(values); }
      static final public String         fromString(String value)                            { return value;                                     }
      static final public Stream<String> fromString(Stream<String> values)                   { return values;                                    }
      static final public LocalTime          toLocalTime(SingleResponse response)    { return ValueConverter.StringToLocalTime(response.asString());          }
      static final public Stream<LocalTime>  toLocalTime(MultipleResponse response)  { return ValueConverter.StringToLocalTime(response.asStreamOfString());  }
      static final public OffsetTime         toOffsetTime(SingleResponse response)   { return ValueConverter.StringToOffsetTime(response.asString());         }
      static final public Stream<OffsetTime> toOffsetTime(MultipleResponse response) { return ValueConverter.StringToOffsetTime(response.asStreamOfString()); }
      static final public String             toString(SingleResponse response)       { return response.asString();                                            }
      static final public Stream<String>     toString(MultipleResponse response)     { return response.asStreamOfString();                                    }
   }
   static final protected class UnsignedIntegerType implements AtomicDataType {
      static final public String         fromInteger(Integer value)                    { return ValueConverter.UnsignedIntegerToString(value);  }
      static final public Stream<String> fromInteger(Stream<? extends Integer> values) { return ValueConverter.UnsignedIntegerToString(values); }
      static final public String         fromString(String value)                      { return value;                                          }
      static final public Stream<String> fromString(Stream<String> values)             { return values;                                         }
      static final public Integer         toInteger(SingleResponse response)   { return ValueConverter.StringToUnsignedInteger(response.asString());         }
      static final public Stream<Integer> toInteger(MultipleResponse response) { return ValueConverter.StringToUnsignedInteger(response.asStreamOfString()); }
      static final public String          toString(SingleResponse response)    { return response.asString();                                                 }
      static final public Stream<String>  toString(MultipleResponse response)  { return response.asStreamOfString();                                         }
   }
   static final protected class UnsignedLongType implements AtomicDataType {
      static final public String         fromLong(Long value)                    { return ValueConverter.UnsignedLongToString(value);  }
      static final public Stream<String> fromLong(Stream<? extends Long> values) { return ValueConverter.UnsignedLongToString(values); }
      static final public String         fromString(String value)                { return value;                                       }
      static final public Stream<String> fromString(Stream<String> values)       { return values;                                      }
      static final public Long           toLong(SingleResponse response)     { return ValueConverter.StringToUnsignedLong(response.asString());         }
      static final public Stream<Long>   toLong(MultipleResponse response)   { return ValueConverter.StringToUnsignedLong(response.asStreamOfString()); }
      static final public String         toString(SingleResponse response)   { return response.asString();                                              }
      static final public Stream<String> toString(MultipleResponse response) { return response.asStreamOfString();                                      }
   }
   static final protected class BinaryDocumentType implements NodeDataType {
      static final public BinaryWriteHandle fromBinaryWriteHandle(BinaryWriteHandle value) {
         return NodeConverter.BinaryWriter(value);
      }
      static final public Stream<BinaryWriteHandle> fromBinaryWriteHandle(Stream<? extends BinaryWriteHandle> values) {
         return NodeConverter.BinaryWriter(values);
      }
      static final public BinaryWriteHandle fromBytes(byte[] value) {
         return NodeConverter.BinaryWriter(NodeConverter.BytesToHandle(value));
      }
      static final public Stream<BinaryWriteHandle> fromBytes(Stream<? extends byte[]> values) {
         return NodeConverter.BinaryWriter(NodeConverter.BytesToHandle(values));
      }
      static final public BinaryWriteHandle fromFile(File value) {
         return NodeConverter.BinaryWriter(NodeConverter.FileToHandle(value));
      }
      static final public Stream<BinaryWriteHandle> fromFile(Stream<? extends File> values) {
         return NodeConverter.BinaryWriter(NodeConverter.FileToHandle(values));
      }
      static final public BinaryWriteHandle fromInputStream(InputStream value) {
         return NodeConverter.BinaryWriter(NodeConverter.InputStreamToHandle(value));
      }
      static final public Stream<BinaryWriteHandle> fromInputStream(Stream<? extends InputStream> values) {
         return NodeConverter.BinaryWriter(NodeConverter.InputStreamToHandle(values));
      }
      static final public BinaryWriteHandle fromOutputStreamSender(OutputStreamSender value) {
         return NodeConverter.BinaryWriter(NodeConverter.OutputStreamSenderToHandle(value));
      }
      static final public Stream<BinaryWriteHandle> fromOutputStreamSender(Stream<? extends OutputStreamSender> values) {
         return NodeConverter.BinaryWriter(NodeConverter.OutputStreamSenderToHandle(values));
      }

      static final public byte[]                   toBytes(SingleResponse response)              { return response.asBytes();               }
      static final public Stream<byte[]>           toBytes(MultipleResponse response)            { return response.asStreamOfBytes();       }
      static final public BinaryReadHandle         toBinaryReadHandle(SingleResponse response)   {
         return NodeConverter.BinaryReader(NodeConverter.InputStreamToHandle(response.asInputStream()));
      }
      static final public Stream<BinaryReadHandle> toBinaryReadHandle(MultipleResponse response) {
         return NodeConverter.BinaryReader(NodeConverter.InputStreamToHandle(response.asStreamOfInputStream()));
      }
      static final public InputStream              toInputStream(SingleResponse response)        { return response.asInputStream();         }
      static final public Stream<InputStream>      toInputStream(MultipleResponse response)      { return response.asStreamOfInputStream(); }
   }
   static protected class JsonDocumentType implements NodeDataType {
      static final public JSONWriteHandle fromJSONWriteHandle(JSONWriteHandle value) {
         return NodeConverter.JSONWriter(value);
      }
      static final public Stream<JSONWriteHandle> fromJSONWriteHandle(Stream<? extends JSONWriteHandle> values) {
         return NodeConverter.JSONWriter(values);
      }
      static final public JSONWriteHandle fromJsonNode(JsonNode value) {
         return NodeConverter.JSONWriter(NodeConverter.JsonNodeToHandle(value));
      }
      static final public Stream<JSONWriteHandle> fromJsonNode(Stream<? extends JsonNode> values) {
         return NodeConverter.JSONWriter(NodeConverter.JsonNodeToHandle(values));
      }
      static final public JSONWriteHandle fromArrayNode(ArrayNode value) {
         return NodeConverter.JSONWriter(NodeConverter.JsonNodeToHandle(value));
      }
      static final public Stream<JSONWriteHandle> fromArrayNode(Stream<? extends ArrayNode> values) {
         return NodeConverter.JSONWriter(NodeConverter.JsonNodeToHandle(values));
      }
      static final public JSONWriteHandle fromObjectNode(ObjectNode value) {
         return NodeConverter.JSONWriter(NodeConverter.JsonNodeToHandle(value));
      }
      static final public Stream<JSONWriteHandle> fromObjectNode(Stream<? extends ObjectNode> values) {
         return NodeConverter.JSONWriter(NodeConverter.JsonNodeToHandle(values));
      }
      static final public JSONWriteHandle fromJsonParser(JsonParser value) {
         return NodeConverter.JSONWriter(NodeConverter.JsonParserToHandle(value));
      }
      static final public Stream<JSONWriteHandle> fromJsonParser(Stream<? extends JsonParser> values) {
         return NodeConverter.JSONWriter(NodeConverter.JsonParserToHandle(values));
      }
      static final public JSONWriteHandle fromFile(File value) {
         return NodeConverter.JSONWriter(NodeConverter.FileToHandle(value));
      }
      static final public Stream<JSONWriteHandle> fromFile(Stream<? extends File> values) {
         return NodeConverter.JSONWriter(NodeConverter.FileToHandle(values));
      }
      static final public JSONWriteHandle fromInputStream(InputStream value) {
         return NodeConverter.JSONWriter(NodeConverter.InputStreamToHandle(value));
      }
      static final public Stream<JSONWriteHandle> fromInputStream(Stream<? extends InputStream> values) {
         return NodeConverter.JSONWriter(NodeConverter.InputStreamToHandle(values));
      }
      static final public JSONWriteHandle fromOutputStreamSender(OutputStreamSender value) {
         return NodeConverter.JSONWriter(NodeConverter.OutputStreamSenderToHandle(value));
      }
      static final public Stream<JSONWriteHandle> fromOutputStreamSender(Stream<? extends OutputStreamSender> values) {
         return NodeConverter.JSONWriter(NodeConverter.OutputStreamSenderToHandle(values));
      }
      static final public JSONWriteHandle fromReader(Reader value)                            {
         return NodeConverter.JSONWriter(NodeConverter.ReaderToHandle(value));
      }
      static final public Stream<JSONWriteHandle> fromReader(Stream<? extends Reader> values) {
         return NodeConverter.JSONWriter(NodeConverter.ReaderToHandle(values));
      }
      static final public JSONWriteHandle fromString(String value)                            {
         return NodeConverter.JSONWriter(NodeConverter.StringToHandle(value));
      }
      static final public Stream<JSONWriteHandle> fromString(Stream<? extends String> values) {
         return NodeConverter.JSONWriter(NodeConverter.StringToHandle(values));
      }

      static final public ArrayNode              toArrayNode(SingleResponse response)        {
         return NodeConverter.ReaderToArrayNode(response.asReader());
      }
      static final public Stream<ArrayNode>      toArrayNode(MultipleResponse response)      {
         return NodeConverter.ReaderToArrayNode(response.asStreamOfReader());
      }
      static final public JsonNode               toJsonNode(SingleResponse response)         {
         return NodeConverter.ReaderToJsonNode(response.asReader());
      }
      static final public Stream<JsonNode>       toJsonNode(MultipleResponse response)       {
         return NodeConverter.ReaderToJsonNode(response.asStreamOfReader());
      }
      static final public ObjectNode             toObjectNode(SingleResponse response)       {
         return NodeConverter.ReaderToObjectNode(response.asReader());
      }
      static final public Stream<ObjectNode>     toObjectNode(MultipleResponse response)     {
         return NodeConverter.ReaderToObjectNode(response.asStreamOfReader());
      }
      static final public JsonParser             toJsonParser(SingleResponse response)       {
         return NodeConverter.ReaderToJsonParser(response.asReader());
      }
      static final public Stream<JsonParser>     toJsonParser(MultipleResponse response)     {
         return NodeConverter.ReaderToJsonParser(response.asStreamOfReader());
      }
      static final public InputStream            toInputStream(SingleResponse response)      { return response.asInputStream();         }
      static final public Stream<InputStream>    toInputStream(MultipleResponse response)    { return response.asStreamOfInputStream(); }
      static final public Reader                 toReader(SingleResponse response)           { return response.asReader();              }
      static final public Stream<Reader>         toReader(MultipleResponse response)         { return response.asStreamOfReader();      }
      static final public JSONReadHandle         toJSONReadHandle(SingleResponse response)   {
         return NodeConverter.JSONReader(NodeConverter.ReaderToHandle(response.asReader()));
      }
      static final public Stream<JSONReadHandle> toJSONReadHandle(MultipleResponse response) {
         return NodeConverter.JSONReader(NodeConverter.ReaderToHandle(response.asStreamOfReader()));
      }
      static final public String                 toString(SingleResponse response)           { return response.asString();              }
      static final public Stream<String>         toString(MultipleResponse response)         { return response.asStreamOfString();      }
   }
   static final protected class ArrayType extends JsonDocumentType {
   }
   static final protected class ObjectType extends JsonDocumentType {
   }
   static final protected class TextDocumentType implements NodeDataType {
      static final public TextWriteHandle fromTextWriteHandle(TextWriteHandle value) {
         return NodeConverter.TextWriter(value);
      }
      static final public Stream<TextWriteHandle> fromTextWriteHandle(Stream<? extends TextWriteHandle> values) {
         return NodeConverter.TextWriter(values);
      }
      static final public TextWriteHandle fromFile(File value) {
         return NodeConverter.TextWriter(NodeConverter.FileToHandle(value));
      }
      static final public Stream<TextWriteHandle> fromFile(Stream<? extends File> values) {
         return NodeConverter.TextWriter(NodeConverter.FileToHandle(values));
      }
      static final public TextWriteHandle fromInputStream(InputStream value) {
         return NodeConverter.TextWriter(NodeConverter.InputStreamToHandle(value));
      }
      static final public Stream<TextWriteHandle> fromInputStream(Stream<? extends InputStream> values) {
         return NodeConverter.TextWriter(NodeConverter.InputStreamToHandle(values));
      }
      static final public TextWriteHandle fromOutputStreamSender(OutputStreamSender value) {
         return NodeConverter.TextWriter(NodeConverter.OutputStreamSenderToHandle(value));
      }
      static final public Stream<TextWriteHandle> fromOutputStreamSender(Stream<? extends OutputStreamSender> values) {
         return NodeConverter.TextWriter(NodeConverter.OutputStreamSenderToHandle(values));
      }
      static final public TextWriteHandle fromReader(Reader value) {
         return NodeConverter.TextWriter(NodeConverter.ReaderToHandle(value));
      }
      static final public Stream<TextWriteHandle> fromReader(Stream<? extends Reader> values) {
         return NodeConverter.TextWriter(NodeConverter.ReaderToHandle(values));
      }
      static final public TextWriteHandle fromString(String value) {
         return NodeConverter.TextWriter(NodeConverter.StringToHandle(value));
      }
      static final public Stream<TextWriteHandle> fromString(Stream<String> values) {
         return NodeConverter.TextWriter(NodeConverter.StringToHandle(values));
      }

      static final public InputStream         toInputStream(SingleResponse response)         { return response.asInputStream();         }
      static final public Stream<InputStream> toInputStream(MultipleResponse response)       { return response.asStreamOfInputStream(); }
      static final public Reader              toReader(SingleResponse response)              { return response.asReader();              }
      static final public Stream<Reader>      toReader(MultipleResponse response)            { return response.asStreamOfReader();      }
      static final public TextReadHandle      toTextReadHandle(SingleResponse response)      {
         return NodeConverter.TextReader(NodeConverter.ReaderToHandle(response.asReader()));
      }
      static final public Stream<TextReadHandle> toTextReadHandle(MultipleResponse response) {
         return NodeConverter.TextReader(NodeConverter.ReaderToHandle(response.asStreamOfReader()));
      }
      static final public String              toString(SingleResponse response)              { return response.asString();              }
      static final public Stream<String>      toString(MultipleResponse response)            { return response.asStreamOfString();      }
   }
   static final protected class XmlDocumentType implements NodeDataType {
      static final public XMLWriteHandle fromXMLWriteHandle(XMLWriteHandle value) {
         return NodeConverter.XMLWriter(value);
      }
      static final public Stream<XMLWriteHandle> fromXMLWriteHandle(Stream<? extends XMLWriteHandle> values) {
         return NodeConverter.XMLWriter(values);
      }
      static final public XMLWriteHandle fromDocument(Document value) {
         return NodeConverter.XMLWriter(NodeConverter.DocumentToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromDocument(Stream<? extends Document> values) {
         return NodeConverter.XMLWriter(NodeConverter.DocumentToHandle(values));
      }
      static final public XMLWriteHandle fromInputSource(InputSource value) {
         return NodeConverter.XMLWriter(NodeConverter.InputSourceToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromInputSource(Stream<? extends InputSource> values) {
         return NodeConverter.XMLWriter(NodeConverter.InputSourceToHandle(values));
      }
      static final public XMLWriteHandle fromSource(Source value) {
         return NodeConverter.XMLWriter(NodeConverter.SourceToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromSource(Stream<? extends Source> values) {
         return NodeConverter.XMLWriter(NodeConverter.SourceToHandle(values));
      }
      static final public XMLWriteHandle fromXMLEventReader(XMLEventReader value) {
         return NodeConverter.XMLWriter(NodeConverter.XMLEventReaderToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromXMLEventReader(Stream<? extends XMLEventReader> values) {
         return NodeConverter.XMLWriter(NodeConverter.XMLEventReaderToHandle(values));
      }
      static final public XMLWriteHandle fromXMLStreamReader(XMLStreamReader value) {
         return NodeConverter.XMLWriter(NodeConverter.XMLStreamReaderToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromXMLStreamReader(Stream<? extends XMLStreamReader> values) {
         return NodeConverter.XMLWriter(NodeConverter.XMLStreamReaderToHandle(values));
      }
      static final public XMLWriteHandle fromFile(File value) {
         return NodeConverter.XMLWriter(NodeConverter.FileToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromFile(Stream<? extends File> values) {
         return NodeConverter.XMLWriter(NodeConverter.FileToHandle(values));
      }
      static final public XMLWriteHandle fromInputStream(InputStream value) {
         return NodeConverter.XMLWriter(NodeConverter.InputStreamToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromInputStream(Stream<? extends InputStream> values)  {
         return NodeConverter.XMLWriter(NodeConverter.InputStreamToHandle(values));
      }
      static final public XMLWriteHandle fromOutputStreamSender(OutputStreamSender value) {
         return NodeConverter.XMLWriter(NodeConverter.OutputStreamSenderToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromOutputStreamSender(Stream<? extends OutputStreamSender> values) {
         return NodeConverter.XMLWriter(NodeConverter.OutputStreamSenderToHandle(values));
      }
      static final public XMLWriteHandle fromReader(Reader value) {
         return NodeConverter.XMLWriter(NodeConverter.ReaderToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromReader(Stream<? extends Reader> values) {
         return NodeConverter.XMLWriter(NodeConverter.ReaderToHandle(values));
      }
      static final public XMLWriteHandle fromString(String value) {
         return NodeConverter.XMLWriter(NodeConverter.StringToHandle(value));
      }
      static final public Stream<XMLWriteHandle> fromString(Stream<? extends String> values) {
         return NodeConverter.XMLWriter(NodeConverter.StringToHandle(values));
      }

      static final public Document              toDocument(SingleResponse response)           {
         return NodeConverter.InputStreamToDocument(response.asInputStream());
      }
      static final public Stream<Document>      toDocument(MultipleResponse response)         {
         return NodeConverter.InputStreamToDocument(response.asStreamOfInputStream());
      }
      static final public InputSource           toInputSource(SingleResponse response)        {
         return NodeConverter.ReaderToInputSource(response.asReader());
      }
      static final public Stream<InputSource>   toInputSource(MultipleResponse response)      {
         return NodeConverter.ReaderToInputSource(response.asStreamOfReader());
      }
      static final public Source                toSource(SingleResponse response)             {
         return NodeConverter.ReaderToSource(response.asReader());
      }
      static final public Stream<Source>        toSource(MultipleResponse response)           {
         return NodeConverter.ReaderToSource(response.asStreamOfReader());
      }
      static final public XMLEventReader        toXMLEventReader(SingleResponse response)     {
         return NodeConverter.ReaderToXMLEventReader(response.asReader());
      }
      static final public Stream<XMLEventReader>toXMLEventReader(MultipleResponse response)   {
         return NodeConverter.ReaderToXMLEventReader(response.asStreamOfReader());
      }
      static final public XMLStreamReader       toXMLStreamReader(SingleResponse response)    {
         return NodeConverter.ReaderToXMLStreamReader(response.asReader());
      }
      static final public Stream<XMLStreamReader>toXMLStreamReader(MultipleResponse response) {
         return NodeConverter.ReaderToXMLStreamReader(response.asStreamOfReader());
      }

      static final public InputStream           toInputStream(SingleResponse response)     { return response.asInputStream();         }
      static final public Stream<InputStream>   toInputStream(MultipleResponse response)   { return response.asStreamOfInputStream(); }
      static final public Reader                toReader(SingleResponse response)          { return response.asReader();              }
      static final public Stream<Reader>        toReader(MultipleResponse response)        { return response.asStreamOfReader();      }
      static final public XMLReadHandle         toXMLReadHandle(SingleResponse response)   {
         return NodeConverter.XMLReader(NodeConverter.ReaderToHandle(response.asReader()));
      }
      static final public Stream<XMLReadHandle> toXMLReadHandle(MultipleResponse response) {
         return NodeConverter.XMLReader(NodeConverter.ReaderToHandle(response.asStreamOfReader()));
      }
      static final public String                toString(SingleResponse response)          { return response.asString();              }
      static final public Stream<String>        toString(MultipleResponse response)        { return response.asStreamOfString();      }
   }
   static protected enum ParameterValuesKind {
      NONE, SINGLE_ATOMIC, SINGLE_NODE, MULTIPLE_ATOMICS, MULTIPLE_NODES, MULTIPLE_MIXED;
   }

   protected DBFunctionRequest request(String module, ParameterValuesKind paramsKind) {
      if (module == null) {
         throw new IllegalArgumentException("null module");
      }
      return new DBFunctionRequest(this.services, this.endpointDir, module, paramsKind);
   }

   /* TODO: DELETE
   static protected DBMultipleAtomicParam atomicParam(String paramName, boolean isNullable, String[] values) {
            return isParamEmpty(paramName, isNullable, (values == null) ? 0 : values.length) ? null :
                  new DBMultipleAtomicParam(paramName, Stream.of(values));
         }
    */
   static protected DBSingleAtomicParam atomicParam(String paramName, boolean isNullable, String value) {
      return isParamNull(paramName, isNullable, value)  ? null : new DBSingleAtomicParam(paramName, value);
   }
   static protected DBMultipleAtomicParam atomicParam(String paramName, boolean isNullable, Stream<String> values) {
      return isParamNull(paramName, isNullable, values) ? null : new DBMultipleAtomicParam(paramName, values);
   }
   static protected DBSingleNodeParam documentParam(String paramName, boolean isNullable, AbstractWriteHandle value) {
      return isParamNull(paramName, isNullable, value)  ? null : new DBSingleNodeParam(paramName, value);
   }
   static protected DBMultipleNodeParam documentParam(String paramName, boolean isNullable, Stream<? extends AbstractWriteHandle> values) {
      return isParamNull(paramName, isNullable, values) ? null : new DBMultipleNodeParam(paramName, values);
   }
   static protected boolean isParamNull(String paramName, boolean isNullable, Object value) {
      if (value != null) {
         return false;
      } else if (!isNullable) {
         throw new RequiredParamException("null value for required parameter: " + paramName);
      }
      return true;
   }

   static protected class DBFunctionRequest {
      private OkHttpServicesPlus  services;
      private String              endpoint;
      private ParameterValuesKind paramsKind;
      private DBFunctionParam[]   params;
      private SessionState        session;
      // TODO: use
      private String              method;
      private DBFunctionRequest(OkHttpServicesPlus services, String endpointDir, String module, ParameterValuesKind paramsKind) {
         this.services   = services;
         endpoint        = endpointDir + module;
         this.paramsKind = paramsKind;
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
      public DBFunctionRequest withParams(DBFunctionParam... params) {
         this.params = params;
         return this;
      }
      public DBFunctionRequest withMethod(String method) {
         this.method = method;
         return this;
      }
      private RequestDefinition makeRequest() {
         switch(paramsKind) {
            case NONE:
               return services.makePostRequest(endpoint, session);
            case SINGLE_ATOMIC:
               if (params == null || params.length == 0) {
                  return services.makePostRequest(endpoint, session);
               } else if (params.length > 1) {
                  throw new InternalError("multiple parameters instead of single atomic");
               } else if (params[0] == null) {
                  return services.makePostRequest(endpoint, session);
               } else if (params.length > 1 || !(params[0] instanceof DBSingleAtomicParam)) {
                  throw new InternalError("invalid parameter type instead of single atomic: "+params[0].getClass().getName());
               }
               DBSingleAtomicParam singleAtomic = (DBSingleAtomicParam) params[0];
               return services.makePostRequest(endpoint, session, encodeParamValue(singleAtomic));
            case MULTIPLE_ATOMICS:
               String encodedParams = (String) Stream.of(params)
                     .map(param -> encodeParamValue(param))
                     .filter(param -> param != null)
                     .collect(Collectors.joining("&"));
               return services.makePostRequest(endpoint, session, encodedParams);
            case SINGLE_NODE:
               if (params == null || params.length == 0) {
                  return services.makePostRequest(endpoint, session);
               } else if (params.length > 1) {
                  throw new InternalError("multiple parameters instead of single node");
               } else if (params[0] == null) {
                  return services.makePostRequest(endpoint, session);
               } else if (params.length > 1 || !(params[0] instanceof DBSingleNodeParam)) {
                  throw new InternalError("invalid parameter type instead of single node: "+params[0].getClass().getName());
               }
/* TODO: potential future
            DBSingleNodeParam singleNode = (DBSingleNodeParam) params[0];
            return services.makePostRequest(endpoint, session, singleNode.getParamValue());
 */
               return services.makePostRequest(endpoint, session, params);
            case MULTIPLE_NODES:
            case MULTIPLE_MIXED:
               return services.makePostRequest(endpoint, session, params);
            default:
               throw new InternalError("unknown parameters kind: "+paramsKind.name());
         }
      }
      private String encodeParamValue(String paramName, String value) {
         if (value == null) {
            return null;
         }
         try {
            return paramName+"="+URLEncoder.encode(value, UTF8_ID);
         } catch(UnsupportedEncodingException e) {
// TODO: library error
            throw new RuntimeException(e);
         }
      }
      private String encodeParamValue(DBSingleAtomicParam param) {
         if (param == null) {
            return null;
         }
         return encodeParamValue(param.getParamName(), param.getParamValue());
      }
      private String encodeParamValue(DBMultipleAtomicParam param) {
         if (param == null) {
            return null;
         }
         String         paramName   = param.getParamName();
         Stream<String> paramValues = param.getParamValues();
         if (paramValues == null) {
            return null;
         }
         String encodedParamValues = paramValues
               .map(paramValue -> encodeParamValue(paramName, paramValue))
               .filter(paramValue -> (paramValue != null))
               .collect(Collectors.joining("&"));
         if (encodedParamValues == null || encodedParamValues.length() == 0) {
            return null;
         }
         return encodedParamValues;
      }
      private String encodeParamValue(DBFunctionParam param) {
         if (param == null) {
            return null;
         } else if (param instanceof DBSingleAtomicParam) {
            return encodeParamValue((DBSingleAtomicParam) param);
         } else if (param instanceof DBMultipleAtomicParam) {
            return encodeParamValue((DBMultipleAtomicParam) param);
         }
         throw new IllegalStateException(
               "could not encode parameter "+param.getParamName()+" of type: "+param.getClass().getName()
         );
      }

      public void responseNone() {
         RequestDefinition requestdef = makeRequest();
         BasicResponse responsedef = requestdef.withEmptyResponse();
         checkResponse(responsedef);
      }
      public SingleResponse responseSingle(boolean isNullable, Format returnFormat) {
         RequestDefinition requestdef = makeRequest();
         SingleResponse responsedef = requestdef.withDocumentResponse((returnFormat == null) ? Format.TEXT : returnFormat);
         checkResponse(responsedef);
         if (responsedef.isNull() && !isNullable) {
            throw new RequiredReturnException("null for required single return value");
         }
         return responsedef;
      }
      public MultipleResponse responseMultiple(boolean isNullable, Format returnFormat) {
         RequestDefinition requestdef  = makeRequest();
         MultipleResponse  responsedef = requestdef.withMultipartMixedResponse((returnFormat == null) ? Format.TEXT : returnFormat);
         checkResponse(responsedef);
         if (responsedef.isNull() && !isNullable) {
            throw new RequiredReturnException("null for required multiple return value");
         }
         return responsedef;
      }

      void checkResponse(BasicResponse responseImpl) {
         int statusCode = responseImpl.getStatusCode();
         System.out.println("code: "+statusCode);
         if (statusCode >= 300) {
            try {
               ObjectMapper mapper = getMapper();

               String errorBody    = responseImpl.getErrorBody();
               if (errorBody != null && errorBody.length() > 0) {
                  ObjectNode errorObj = mapper.readValue(errorBody, ObjectNode.class);
                  JsonNode   errResponse = errorObj.get("errorResponse");
                  JsonNode   errMsgProp  = (errResponse != null) ? errResponse.get("message") : null;
                  String     errMsgText  = (errMsgProp  != null) ? errMsgProp.asText()        : null;
// TODO: stack trace
                  if (errMsgText != null && errMsgText.length() > 0) {
                     System.out.println("error: "+errMsgText);
                     throw new IllegalArgumentException(errMsgText);
                  }
               }
            } catch (JsonParseException e) {
               throw new RuntimeException(e);
            } catch (JsonMappingException e) {
               throw new RuntimeException(e);
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
            throw new RuntimeException("request failed");
         }
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

   // START: candidates for RestServices
// TODO: maybe rename form field instead of function param; form-data representation vs higher level?
   static public abstract class DBFunctionParam {
      private String paramName;
      DBFunctionParam(String paramName) {
         this.paramName = paramName;
      }
      public String getParamName() {
         return paramName;
      }
   }
   static public class DBSingleAtomicParam extends DBFunctionParam {
      private String paramValue;
      DBSingleAtomicParam(String paramName, String paramValue) {
         super(paramName);
         this.paramValue = paramValue;
      }
      public String getParamValue() {
         return paramValue;
      }
   }
   static public class DBMultipleAtomicParam extends DBFunctionParam {
      private Stream<String> paramValues;
      DBMultipleAtomicParam(String paramName, Stream<String> paramValues) {
         super(paramName);
         this.paramValues = paramValues;
      }
      public Stream<String> getParamValues() {
         return paramValues;
      }
   }
   static public class DBSingleNodeParam extends DBFunctionParam {
      private AbstractWriteHandle paramValue;
      DBSingleNodeParam(String paramName, AbstractWriteHandle paramValue) {
         super(paramName);
         this.paramValue = paramValue;
      }
      public AbstractWriteHandle getParamValue() {
         return paramValue;
      }
   }
   static public class DBMultipleNodeParam extends DBFunctionParam {
      private Stream<? extends AbstractWriteHandle> paramValues;
      DBMultipleNodeParam(String paramName, Stream<? extends AbstractWriteHandle> paramValues) {
         super(paramName);
         this.paramValues = paramValues;
      }
      public Stream<? extends AbstractWriteHandle> getParamValues() {
         return paramValues;
      }
   }

   static public interface RequestDefinition {
      public BasicResponse    withEmptyResponse();
      public SingleResponse   withDocumentResponse(Format format);
      public MultipleResponse withMultipartMixedResponse(Format format);
   }
   static public interface BasicResponse {
      public boolean isNull();
      public int     getStatusCode();
      public String  getStatusMsg();
      public String  getErrorBody();
   }
   static public interface SingleResponse extends BasicResponse {
      public byte[]            asBytes();
      public InputStream       asInputStream();
      public InputStreamHandle asInputStreamHandle();
      public Reader            asReader();
      public ReaderHandle      asReaderHandle();
      public String            asString();
   }
   static public interface MultipleResponse extends BasicResponse {
      public Stream<byte[]>            asStreamOfBytes();
      public Stream<InputStream>       asStreamOfInputStream();
      public Stream<InputStreamHandle> asStreamOfInputStreamHandle();
      public Stream<Reader>            asStreamOfReader();
      public Stream<ReaderHandle>      asStreamOfReaderHandle();
      public Stream<String>            asStreamOfString();
   }
// END: candidates for RestServices

// START: candidates for or reuse from OkHttpServices

   static protected class OkHttpServicesPlus {
      private OkHttpClient client;
      private HttpUrl      baseUrl;

      protected OkHttpServicesPlus(OkHttpClient client, HttpUrl baseUrl) {
         this.client  = client;
         this.baseUrl = baseUrl;
      }

      public RequestDefinition makeRequestDefinition(Request.Builder builder) {
         return new RequestDefinitionImpl(this, builder);
      }
      public RequestDefinition makePostRequest(Request.Builder builder) {
         return makeRequestDefinition(builder.post(new EmptyRequestBody()));
      }
      public RequestDefinition makePostRequest(String endpoint, SessionState session) {
         Request.Builder builder = toEndpoint(endpoint, session);
         return makePostRequest(builder);
      }
      public RequestDefinition makePostRequest(String endpoint, SessionState session, String atomics) {
         Request.Builder builder = toEndpoint(endpoint, session);
         if (atomics == null || atomics.length() == 0) {
            return makePostRequest(builder);
         }
         return makeRequestDefinition(builder.post(
               RequestBody.create(URLENCODED_MIME_TYPE, (atomics == null) ? "" : atomics)
         ));
      }
/* TODO: potential future
      public RequestDefinition makePostRequest(String endpoint, SessionState session, AbstractWriteHandle document) {
         Request.Builder builder = toEndpoint(endpoint, session);
         return makeRequestDefinition(builder.post(makeRequestBody(document)));
      }
      */
      public RequestDefinition makePostRequest(String endpoint, SessionState session, DBFunctionParam[] params) {
         Request.Builder builder = toEndpoint(endpoint, session);
         if (params == null || params.length == 0) {
            return makePostRequest(builder);
         }
         return makeRequestDefinition(builder.post(makeRequestBody(params)));
      }

      HttpUrl makeURL(String endpointPath) {
         return baseUrl.resolve(endpointPath);
      }

      Request.Builder makeRequest(String endpointPath) {
         if (endpointPath == null) {
            throw new IllegalArgumentException("No endpoint specified");
         }
         HttpUrl url = makeURL(endpointPath);
         return new Request.Builder().url(url);
      }
      RequestBody makeRequestBody(String value) {
         if (value == null) {
            return new EmptyRequestBody();
         }
         return new AtomicRequestBody(value, MediaType.parse("text/plain"));
      }
      RequestBody makeRequestBody(AbstractWriteHandle document) {
         if (document == null) {
            return new EmptyRequestBody();
         }
         HandleImplementation handleBase = HandleAccessor.as(document);
         Format format = handleBase.getFormat();
         String mimetype = (format == Format.BINARY) ?
               "application/x-unknown-content-type" : handleBase.getMimetype();
         MediaType mediaType = MediaType.parse(mimetype);
         return (document instanceof OutputStreamSender) ?
            new StreamingOutputImpl((OutputStreamSender) document,      mediaType) :
            new ObjectRequestBody(HandleAccessor.sendContent(document), mediaType);
      }
      RequestBody makeRequestBody(DBFunctionParam[] params) {
         if (params == null || params.length == 0) {
            return new EmptyRequestBody();
         }

         MultipartBody.Builder multiBldr = new MultipartBody.Builder();
         multiBldr.setType(MultipartBody.FORM);

         Condition hasValue = new Condition();
         for (DBFunctionParam param: params) {
            if (param == null) {
               continue;
            }

            final String paramName = param.getParamName();
            if (param instanceof DBSingleAtomicParam) {
               String paramValue = ((DBSingleAtomicParam) param).getParamValue();
               if (paramValue != null) {
                  hasValue.set();
                  multiBldr.addFormDataPart(paramName, null, makeRequestBody(paramValue));
               }
            } else if (param instanceof DBMultipleAtomicParam) {
               Stream<String> paramValues = ((DBMultipleAtomicParam) param).getParamValues();
               if (paramValues != null) {
                   paramValues
                       .filter(paramValue -> paramValue != null)
                       .forEachOrdered(paramValue ->
                             multiBldr.addFormDataPart(paramName, null, makeRequestBody(paramValue))
                       );
               }
            } else if (param instanceof DBSingleNodeParam) {
               AbstractWriteHandle paramValue = ((DBSingleNodeParam) param).getParamValue();
               if (paramValue != null) {
                  hasValue.set();
                  multiBldr.addFormDataPart(paramName, null, makeRequestBody(paramValue));
               }
            } else if (param instanceof DBMultipleNodeParam) {
               Stream<? extends AbstractWriteHandle> paramValues = ((DBMultipleNodeParam) param).getParamValues();
               if (paramValues != null) {
                  paramValues
                        .filter(paramValue -> paramValue != null)
                        .forEachOrdered(paramValue -> {
                           hasValue.set();
                           multiBldr.addFormDataPart(paramName, null, makeRequestBody(paramValue));
                        });
               }
            } else {
               throw new IllegalStateException(
                     "unknown multipart "+paramName+" param of: "+param.getClass().getName()
               );
            }
         }

         if (!hasValue.get()) {
            return new EmptyRequestBody();
         }

         return multiBldr.build();
      }

      Request.Builder forDocumentResponse(Request.Builder requestBldr, Format format) {
         return requestBldr.addHeader("Accept", (format == Format.BINARY) ?
               "application/x-unknown-content-type" : format.getDefaultMimetype());
      }
      Request.Builder forMultipartMixedResponse(Request.Builder requestBldr) {
         return requestBldr.addHeader(
               "Accept",
               "multipart/mixed; boundary=\""+UUID.randomUUID().toString()+"\""
            );
      }

      Request.Builder toEndpoint(String endpointPath, SessionState session) {
         Request.Builder builder = makeRequest(endpointPath);
         if (session == null) {
            return builder;
         }
         return builder.addHeader("Cookie", "SessionID="+session.getSessionId());
      }

      void receiveImpl(Request.Builder requestBldr, BasicResponseImpl responseImpl) {
         try {
            Request request = requestBldr.build();
System.out.println("calling "+request.url().toString());
/*
System.out.println(request.body().contentType().toString());
for (String headerName: request.headers().names()) {
   System.out.print(headerName+":");
   for (String headerValue: request.headers(headerName)) {
      System.out.print(" "+headerValue);
   }
   System.out.println();
}
BufferedSink sink = Okio.buffer(Okio.sink(System.out));
request.body().writeTo(sink);
sink.flush();
 */
            Response response = client.newCall(request).execute();

            responseImpl.setResponse(response);
/*
System.out.println();
for (String headerName: response.headers().names()) {
   System.out.print(headerName+":");
   for (String headerValue: response.headers(headerName)) {
      System.out.print(" "+headerValue);
   }
   System.out.println();
}
 */

         } catch (IOException e) {
// TODO: library error
            throw new RuntimeException(e);
         }
      }

   }

   static public class RequestDefinitionImpl implements RequestDefinition {
      private OkHttpServicesPlus services;
      private Request.Builder requestBldr;
      RequestDefinitionImpl(OkHttpServicesPlus services, Request.Builder requestBldr) {
         this.services    = services;
         this.requestBldr = requestBldr;
      }

      @Override
      public BasicResponse withEmptyResponse() {
         BasicResponseImpl responseImpl = new BasicResponseImpl();
         services.receiveImpl(requestBldr, responseImpl);
         return responseImpl;
      }
      @Override
      public SingleResponse withDocumentResponse(Format format) {
         SingleResponseImpl responseImpl = new SingleResponseImpl(format);
         services.receiveImpl(services.forDocumentResponse(requestBldr, format), responseImpl);
         return responseImpl;
      }
      @Override
      public MultipleResponse withMultipartMixedResponse(Format format) {
         MultipleResponseImpl responseImpl = new MultipleResponseImpl(format);
         services.receiveImpl(services.forMultipartMixedResponse(requestBldr), responseImpl);
         return responseImpl;
      }
   }

// TODO:  move exception throwing into higher-level response body processing?
   static protected boolean checkNull(ResponseBody body, Format format) {
      if (body != null) {
         if (body.contentLength() == 0) {
            body.close();
         } else {
            MediaType actualType  = body.contentType();
            String    defaultType = (format == Format.BINARY) ?
                  "application/x-unknown-content-type" : format.getDefaultMimetype();
            if (actualType == null) {
               body.close();
               throw new RuntimeException(
                     "Returned document with unknown mime type instead of "+defaultType
               );
            } else if (!actualType.toString().startsWith(defaultType)) {
               body.close();
               throw new RuntimeException(
                     "Returned document as "+actualType.toString()+" instead of "+defaultType
               );
            }
            return false;
         }
      }
      return true;
   }
// TODO:  move exception throwing into higher-level response body processing?
   static protected boolean checkNull(MimeMultipart multipart, Format format) {
      if (multipart != null) {
         try {
            if (multipart.getCount() != 0) {
               BodyPart firstPart   = multipart.getBodyPart(0);
               String   actualType  = (firstPart == null) ? null : firstPart.getContentType();
               String   defaultType = (format == Format.BINARY) ?
                     "application/x-unknown-content-type" : format.getDefaultMimetype();
               if (actualType == null || !actualType.startsWith(defaultType)) {
                  throw new RuntimeException(
                     "Returned document as "+actualType+" instead of "+defaultType
                  );
               }
               return false;
            }
         } catch (MessagingException e) {
            new RuntimeException(e);
         }
      }
      return true;
   }

   static protected class Condition {
      private boolean is = false;
      protected boolean get() {
         return is;
      }
      protected void set() {
         if (!is)
            is = true;
      }
   }

   static private class EmptyRequestBody extends RequestBody {
      @Override
      public MediaType contentType() {
         return null;
      }
      @Override
      public void writeTo(BufferedSink sink) {
      }
   }

   static class AtomicRequestBody extends RequestBody {
      private MediaType  contentType;
      private String     value;
      AtomicRequestBody(String value, MediaType contentType) {
         super();
         this.value = value;
         this.contentType = contentType;
      }
      @Override
      public MediaType contentType() {
         return contentType;
      }
      @Override
      public void writeTo(BufferedSink sink) throws IOException {
         sink.writeUtf8(value);
      }
   }

   static class StreamingOutputImpl extends RequestBody {
      private OutputStreamSender handle;
      private MediaType          contentType;

      StreamingOutputImpl(OutputStreamSender handle, MediaType contentType) {
         super();
         this.handle = handle;
         this.contentType = contentType;
      }

      @Override
      public MediaType contentType() {
         return contentType;
      }

      @Override
      public void writeTo(BufferedSink sink) throws IOException {
         OutputStream out = sink.outputStream();

         handle.write(out);
         out.flush();
      }
   }
   static private class ObjectRequestBody extends RequestBody {
      private Object obj;
      private MediaType contentType;

      ObjectRequestBody(Object obj, MediaType contentType) {
         super();
         this.obj = obj;
         this.contentType = contentType;
      }

      @Override
      public MediaType contentType() {
         return contentType;
      }

      @Override
      public void writeTo(BufferedSink sink) throws IOException {
         if ( obj instanceof InputStream ) {
            sink.writeAll(Okio.source((InputStream) obj));
         } else if ( obj instanceof File ) {
            try ( okio.Source source = Okio.source((File) obj) ) {
               sink.writeAll(source);
            }
         } else if ( obj instanceof byte[] ) {
            // TODO: supported?
            sink.write((byte[]) obj);
         } else if ( obj instanceof String) {
            sink.writeUtf8((String) obj);
         } else if ( obj == null ) {
         } else {
            throw new IllegalStateException("Cannot write object of type: " + obj.getClass());
         }
// TODO: needed?
//       sink.flush();
      }
   }

   static class BasicResponseImpl implements BasicResponse {
      private boolean  isNull = true;
      private Response response;
      void setResponse(Response response) {
         this.response = response;
      }
      @Override
      public boolean isNull() {
         return isNull;
      }
      void setNull(boolean isNull) {
         this.isNull = isNull;
      }
      @Override
      public int getStatusCode() {
         return response.code();
      }
      @Override
      public String getStatusMsg() {
         return response.message();
      }
      @Override
      public String getErrorBody() {
         try (ResponseBody errorBody = response.body()) {
            if (errorBody.contentLength() > 0) {
               MediaType errorType = errorBody.contentType();
               if (errorType != null) {
                  String errorContentType = errorType.toString();
                  if (errorContentType != null && errorContentType.startsWith("application/") && errorContentType.contains("json")) {
                     return errorBody.string();
                  }
               }
            }
         } catch(IOException e) {
            throw new RuntimeException(e);
         }
         return null;
      }
   }
   static class SingleResponseImpl extends BasicResponseImpl implements SingleResponse, AutoCloseable {
      private Format format;
      private ResponseBody responseBody;
      SingleResponseImpl(Format format) {
         this.format = format;
      }
      void setResponse(Response response) {
         super.setResponse(response);
         setResponseBody(response.body());
      }
      void setResponseBody(ResponseBody responseBody) {
         if (!checkNull(responseBody, format)) {
            this.responseBody = responseBody;
            setNull(false);
         }
      }

      @Override
      public byte[] asBytes() {
         try {
            if (responseBody == null) {
               return null;
            }
            byte[] value = responseBody.bytes();
            closeImpl();
            return value;
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
      @Override
      public InputStream asInputStream() {
         return (responseBody == null) ? null : responseBody.byteStream();
      }
      @Override
      public InputStreamHandle asInputStreamHandle() {
         return (responseBody == null) ? null : new InputStreamHandle(asInputStream());
      }
      @Override
      public Reader asReader() {
         return (responseBody == null) ? null : responseBody.charStream();
      }
      @Override
      public ReaderHandle asReaderHandle() {
         return (responseBody == null) ? null : new ReaderHandle(asReader());
      }
      @Override
      public String asString() {
         try {
            if (responseBody == null) {
               return null;
            }
            String value = responseBody.string();
            closeImpl();
            return value;
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public void close() throws Exception {
         if (responseBody != null) {
            closeImpl();
         }
      }
      private void closeImpl() {
         responseBody.close();
         responseBody = null;
      }
   }
   static class MultipleResponseImpl extends BasicResponseImpl implements MultipleResponse {
      private Format format;
      private MimeMultipart multipart;
      MultipleResponseImpl(Format format){
         this.format = format;
      }
      void setResponse(Response response) {
         try {
            super.setResponse(response);
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
               setNull(true);
               return;
            }
            MediaType contentType = responseBody.contentType();
            if (contentType == null) {
               setNull(true);
               return;
            }
            ByteArrayDataSource dataSource = new ByteArrayDataSource(
                  responseBody.byteStream(), contentType.toString()
            );
            setMultipart(new MimeMultipart(dataSource));
         } catch (IOException e) {
            throw new RuntimeException(e);
         } catch (MessagingException e) {
            throw new RuntimeException(e);
         }
      }
      void setMultipart(MimeMultipart multipart) {
         if (!checkNull(multipart, format)) {
            this.multipart = multipart;
            setNull(false);
         }
      }

      @Override
      public Stream<byte[]> asStreamOfBytes() {
         try {
            if (multipart == null) {
               return Stream.empty();
            }
            int partCount = multipart.getCount();

            Stream.Builder<byte[]> builder = Stream.builder();
            for (int i=0; i < partCount; i++) {
               BodyPart bodyPart = multipart.getBodyPart(i);
               builder.accept(NodeConverter.InputStreamToBytes(bodyPart.getInputStream()));
            }
            return builder.build();
         } catch (MessagingException e) {
            throw new RuntimeException(e);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
      @Override
      public Stream<InputStreamHandle> asStreamOfInputStreamHandle() {
         try {
            if (multipart == null) {
               return Stream.empty();
            }
            int partCount = multipart.getCount();

            Stream.Builder<InputStreamHandle> builder = Stream.builder();
            for (int i=0; i < partCount; i++) {
               BodyPart bodyPart = multipart.getBodyPart(i);
               builder.accept(new InputStreamHandle(bodyPart.getInputStream()));
            }
            return builder.build();
         } catch (MessagingException e) {
            throw new RuntimeException(e);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
      @Override
      public Stream<InputStream> asStreamOfInputStream() {
         try {
            if (multipart == null) {
               return Stream.empty();
            }
            int partCount = multipart.getCount();

            Stream.Builder<InputStream> builder = Stream.builder();
            for (int i=0; i < partCount; i++) {
               BodyPart bodyPart = multipart.getBodyPart(i);
               builder.accept(bodyPart.getInputStream());
            }
            return builder.build();
         } catch (MessagingException e) {
            throw new RuntimeException(e);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
      @Override
      public Stream<Reader> asStreamOfReader() {
         try {
            if (multipart == null) {
               return Stream.empty();
            }
            int partCount = multipart.getCount();

            Stream.Builder<Reader> builder = Stream.builder();
            for (int i=0; i < partCount; i++) {
               BodyPart bodyPart = multipart.getBodyPart(i);
               builder.accept(NodeConverter.InputStreamToReader(bodyPart.getInputStream()));
            }
            return builder.build();
         } catch (MessagingException e) {
            throw new RuntimeException(e);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
      @Override
      public Stream<ReaderHandle> asStreamOfReaderHandle() {
         try {
            if (multipart == null) {
               return Stream.empty();
            }
            int partCount = multipart.getCount();

            Stream.Builder<ReaderHandle> builder = Stream.builder();
            for (int i=0; i < partCount; i++) {
               BodyPart bodyPart = multipart.getBodyPart(i);
               builder.accept(new ReaderHandle(NodeConverter.InputStreamToReader(bodyPart.getInputStream())));
            }
            return builder.build();
         } catch (MessagingException e) {
            throw new RuntimeException(e);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
      @Override
      public Stream<String> asStreamOfString() {
         try {
            if (multipart == null) {
               return Stream.empty();
            }
            int partCount = multipart.getCount();

            Stream.Builder<String> builder = Stream.builder();
            for (int i=0; i < partCount; i++) {
               BodyPart bodyPart = multipart.getBodyPart(i);
               builder.accept(NodeConverter.InputStreamToString(bodyPart.getInputStream()));
            }
            return builder.build();
         } catch (MessagingException e) {
            throw new RuntimeException(e);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
   }

   protected SessionState newSessionStateImpl() {
      return new SessionStateImpl();
   }
// TODO: use okhttp3.CookieJar for cookies? spin off into standalone impl class?
   static private class SessionStateImpl implements SessionState {
      private String sessionId;
      protected SessionStateImpl() {
         sessionId = Long.toUnsignedString(ThreadLocalRandom.current().nextLong(), 16);
      }

      @Override
      public String getSessionId() {
         return sessionId;
      }
   }
// END: candidates for or reuse from OkHttpServices
}
