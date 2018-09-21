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
package com.marklogic.client.test.dbfunction

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.marklogic.client.DatabaseClientFactory
import com.marklogic.client.document.JSONDocumentManager
import com.marklogic.client.document.TextDocumentManager
import com.marklogic.client.io.DocumentMetadataHandle
import com.marklogic.client.io.FileHandle
import com.marklogic.client.io.StringHandle
import com.marklogic.client.tools.proxy.Generator
import java.io.File

import java.lang.IllegalStateException

val TEST_PACKAGE = "com.marklogic.client.test.dbfunction.generated"

val generator = Generator()

enum class TestVariant {
  VALUE, NULL, EMPTY
}
fun getAtomicMappingImports(): String {
  return """
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeFactory;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import javax.xml.datatype.DatatypeConfigurationException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
"""
}
fun getAtomicMappingMembers(): String {
  return """
   DatatypeFactory datatypeFactory = null;
   {
   try {
      datatypeFactory = DatatypeFactory.newInstance();
      } catch (DatatypeConfigurationException e) {
         throw new RuntimeException(e);
      }
   }

   private BigDecimal asBigDecimal(String value) {
      return DatatypeConverter.parseDecimal(value);
      }
   private Date asDate(String value) {
      return DatatypeConverter.parseTime(value).getTime();
      }
   private Duration asDuration(String value) {
      return Duration.parse(value);
      }
   private LocalDate asLocalDate(String value) {
      return DateTimeFormatter.ISO_LOCAL_DATE.parse(value, LocalDate::from);
      }
   private LocalDateTime asLocalDateTime(String value) {
      return DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value, LocalDateTime::from);
      }
   private LocalTime asLocalTime(String value) {
      return DateTimeFormatter.ISO_LOCAL_TIME.parse(value, LocalTime::from);
      }
   private OffsetDateTime asOffsetDateTime(String value) {
      return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value, OffsetDateTime::from);
      }
   private OffsetTime asOffsetTime(String value) {
      return DateTimeFormatter.ISO_OFFSET_TIME.parse(value, OffsetTime::from);
      }

   private String asString(Object value) {
      return value.toString();
   }
   private String asString(BigDecimal value) {
      return DatatypeConverter.printDecimal(value);
   }
   private String asString(Date value) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(value);
      return DatatypeConverter.printDateTime(cal);
   }
   private String asString(Duration value) {
      return value.toString();
   }
   private String asString(LocalDate value) {
      return value.format(DateTimeFormatter.ISO_LOCAL_DATE);
   }
   private String asString(LocalDateTime value) {
      return value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
   }
   private String asString(LocalTime value) {
      return value.format(DateTimeFormatter.ISO_LOCAL_TIME);
   }
   private String asString(OffsetDateTime value) {
      return value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
   }
   private String asString(OffsetTime value) {
      return value.format(DateTimeFormatter.ISO_OFFSET_TIME);
   }
"""
}
fun getAtomicMappingConstructors(): Map<String,Map<String,String>> {
  return mapOf(
      "date"              to mapOf(
          "java.time.LocalDate"      to "asLocalDate"
      ),
      "dateTime"          to mapOf(
          "java.util.Date"           to "asDate",
          "java.time.LocalDateTime"  to "asLocalDateTime",
          "java.time.OffsetDateTime" to "asOffsetDateTime"
      ),
      "dayTimeDuration"   to mapOf(
          "java.time.Duration"       to "asDuration"
      ),
      "decimal"           to mapOf(
          "java.math.BigDecimal"     to "asBigDecimal"
      ),
      "time"              to mapOf(
          "java.time.LocalTime"      to "asLocalTime",
          "java.time.OffsetTime"     to "asOffsetTime"
      )
  )
}
fun getDocumentMappingImports(): String {
  return """
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;

import java.nio.charset.Charset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.FactoryConfigurationError;

import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
"""
}
fun getDocumentMappingMembers(): String {
  return """
   private ObjectMapper mapper = new ObjectMapper();
   private TransformerFactory transformerFactory = null;
   {
      try {
         transformerFactory = TransformerFactory.newInstance();
      } catch(TransformerFactoryConfigurationError e) {
         throw new RuntimeException(e);
      }
   }
   private Transformer transformer = null;
   {
      try {
         transformer = transformerFactory.newTransformer();
      } catch(TransformerConfigurationException e) {
         throw new RuntimeException(e);
      }
   }
   private Pattern xmlPrologPattern = Pattern.compile("^\\s*<\\?xml[^>]*>\\s*");

   private Stream<byte[]> BinaryReadHandleAsBytes(Stream<BinaryReadHandle> items) {
      return items.map(this::asBytes);
   }
   private byte[] asBytes(AbstractReadHandle item) {
      if (item == null) return null;
      return ((BufferableHandle) item).toBuffer();
   }

   private Stream<String> JSONReadHandleAsString(Stream<JSONReadHandle> items) {
      return items.map(this::asString);
   }
   private Stream<String> TextReadHandleAsString(Stream<TextReadHandle> items) {
      return items.map(this::asString);
   }
   private Stream<String> XMLReadHandleAsString(Stream<XMLReadHandle> items) {
      return items.map(this::asString);
   }
   private String asString(AbstractReadHandle item) {
      if (item == null) return null;
      return item.toString();
   }

   private Stream<String> InputStreamAsString(Stream<InputStream> items) {
      return items.map(this::asString);
   }
   private String asString(InputStream item) {
      if (item == null) return null;
      try (InputStreamReader reader = new InputStreamReader(item)) {
         StringBuffer buffer = new StringBuffer();
         char[] chars = new char[1024];
         int len = 0;
         while ((len = reader.read(chars)) != -1) {
            buffer.append(chars, 0, len);
         }
         return buffer.toString();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   private Stream<String> StringAsString(Stream<String> items) {
      return items;
   }
   private String asString(String item) {
      return item;
   }

   private Stream<String> JsonParserAsString(Stream<JsonParser> items) {
      return items.map(this::asString);
   }
   private String asString(JsonParser item) {
      if (item == null) return null;
      try {
         StringWriter writer = new StringWriter();
         JsonGenerator generator = mapper.getFactory().createGenerator(writer);
         item.nextToken();
         generator.copyCurrentStructure(item);
         generator.close();
         writer.flush();
         return writer.toString();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   private Stream<String> ArrayNodeAsString(Stream<ArrayNode> items) {
      return items.map(this::asString);
   }
   private String asString(ArrayNode item) {
      if (item == null) return null;
      try {
         return mapper.writeValueAsString(item).replaceAll(",",", ");
      } catch(JsonProcessingException e) {
         throw new RuntimeException(e);
      }
   }
   private Stream<String> ObjectNodeAsString(Stream<ObjectNode> items) {
      return items.map(this::asString);
   }
   private String asString(ObjectNode item) {
      if (item == null) return null;
      try {
         return mapper.writeValueAsString(item);
      } catch(JsonProcessingException e) {
         throw new RuntimeException(e);
      }
   }
   private Stream<String> JsonNodeAsString(Stream<JsonNode> items) {
      return items.map(this::asString);
   }
   private String asString(JsonNode item) {
      if (item == null) return null;
      try {
         return mapper.writeValueAsString(item);
      } catch(JsonProcessingException e) {
         throw new RuntimeException(e);
      }
   }
   private Stream<String> DocumentAsString(Stream<Document> items) {
      return items.map(this::asString);
   }
   private String asString(Document item) {
      if (item == null) return null;
      return asString(new DOMSource(item));
   }
   private Stream<String> InputSourceAsString(Stream<InputSource> items) {
      return items.map(this::asString);
   }
   private String asString(InputSource item) {
      if (item == null) return null;
      return asString(new SAXSource(item));
   }
   private Stream<String> XMLEventReaderAsString(Stream<XMLEventReader> items) {
      return items.map(this::asString);
   }
   private String asString(XMLEventReader item) {
      if (item == null) return null;
      try {
         return asString(new StAXSource(item));
      } catch (XMLStreamException e) {
         throw new RuntimeException(e);
      }
   }
   private Stream<String> XMLStreamReaderAsString(Stream<XMLStreamReader> items) {
      return items.map(this::asString);
   }
   private String asString(XMLStreamReader item) {
      if (item == null) return null;
      return asString(new StAXSource(item));
   }
   private Stream<String> SourceAsString(Stream<Source> items) {
      return items.map(this::asString);
   }
   private String asString(Source source) {
      if (source == null) return null;
      try {
         StringWriter writer = new StringWriter();
         StreamResult result = new StreamResult(writer);
         transformer.transform(source, result);
         writer.flush();
         String out = writer.toString();
         Matcher prologMatcher = xmlPrologPattern.matcher(out);
         return prologMatcher.replaceFirst("");
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }
   }

   private byte[] binaryDocumentAsByteArray(String value) {
      return DatatypeConverter.parseBase64Binary(value);
      }
   private InputStream binaryDocumentAsInputStream(String value) {
      return new ByteArrayInputStream(binaryDocumentAsByteArray(value));
      }
   private BinaryWriteHandle binaryDocumentAsBinaryWriteHandle(String value) {
      return new InputStreamHandle(binaryDocumentAsInputStream(value)).withFormat(Format.BINARY);
      }
   private ArrayNode arrayAsArrayNode(String value) {
      try {
         return mapper.readValue(value, ArrayNode.class);
      } catch(IOException e) {
         throw new RuntimeException(e);
         }
      }
   private JsonParser arrayAsJsonParser(String value) {
      return jsonDocumentAsJsonParser(value);
      }
   private InputStream arrayAsInputStream(String value) {
      return jsonDocumentAsInputStream(value);
      }
   private JSONWriteHandle arrayAsJSONWriteHandle(String value) {
      return jsonDocumentAsJSONWriteHandle(value);
      }
   private Reader arrayAsReader(String value) {
      return jsonDocumentAsReader(value);
      }
   private String arrayAsString(String value) {
      return jsonDocumentAsString(value);
      }
   private ObjectNode objectAsObjectNode(String value) {
      try {
         return mapper.readValue(value, ObjectNode.class);
      } catch(IOException e) {
         throw new RuntimeException(e);
         }
      }
   private JsonParser objectAsJsonParser(String value) {
      return jsonDocumentAsJsonParser(value);
      }
   private InputStream objectAsInputStream(String value) {
      return jsonDocumentAsInputStream(value);
      }
   private JSONWriteHandle objectAsJSONWriteHandle(String value) {
      return jsonDocumentAsJSONWriteHandle(value);
      }
   private Reader objectAsReader(String value) {
      return jsonDocumentAsReader(value);
      }
   private String objectAsString(String value) {
      return jsonDocumentAsString(value);
      }
   private ArrayNode jsonDocumentAsArrayNode(String value) {
      try {
         return mapper.readValue(value, ArrayNode.class);
      } catch(IOException e) {
         throw new RuntimeException(e);
         }
      }
   private ObjectNode jsonDocumentAsObjectNode(String value) {
      try {
         return mapper.readValue(value, ObjectNode.class);
      } catch(IOException e) {
         throw new RuntimeException(e);
         }
      }
   private JsonNode jsonDocumentAsJsonNode(String value) {
      try {
         return mapper.readTree(value);
      } catch(IOException e) {
         throw new RuntimeException(e);
         }
      }
   private JsonParser jsonDocumentAsJsonParser(String value) {
      try {
         return mapper.getFactory().createParser(value);
      } catch(IOException e) {
         throw new RuntimeException(e);
         }
      }
   private InputStream jsonDocumentAsInputStream(String value) {
      return new ByteArrayInputStream(value.getBytes(Charset.forName("UTF-8")));
      }
   private JSONWriteHandle jsonDocumentAsJSONWriteHandle(String value) {
      return new StringHandle(value).withFormat(Format.JSON);
      }
   private Reader jsonDocumentAsReader(String value) {
      return new StringReader(value);
      }
   private String jsonDocumentAsString(String value) {
      return value;
      }
   private InputStream textDocumentAsInputStream(String value) {
      return new ByteArrayInputStream(value.getBytes(Charset.forName("UTF-8")));
      }
   private Reader textDocumentAsReader(String value) {
      return new StringReader(value);
      }
   private String textDocumentAsString(String value) {
      return value;
      }
   private TextWriteHandle textDocumentAsTextWriteHandle(String value) {
      return new StringHandle(value).withFormat(Format.TEXT);
      }
   private Document xmlDocumentAsDocument(String value) {
      try {
         return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
            xmlDocumentAsInputStream(value)
            );
      } catch(SAXException e) {
         throw new RuntimeException(e);
      } catch(IOException e) {
         throw new RuntimeException(e);
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
        }
     }
   private InputSource xmlDocumentAsInputSource(String value) {
      return new InputSource(xmlDocumentAsReader(value));
      }
   private InputStream xmlDocumentAsInputStream(String value) {
      return new ByteArrayInputStream(value.getBytes(Charset.forName("UTF-8")));
      }
   private Reader xmlDocumentAsReader(String value) {
      return new StringReader(value);
      }
   private Source xmlDocumentAsSource(String value) {
      return new StreamSource(xmlDocumentAsReader(value));
      }
   private String xmlDocumentAsString(String value) {
      return value;
      }
   private XMLEventReader xmlDocumentAsXMLEventReader(String value) {
      try {
         return XMLInputFactory.newFactory().createXMLEventReader(
            xmlDocumentAsReader(value)
            );
      } catch(XMLStreamException e) {
         throw new RuntimeException(e);
      } catch(FactoryConfigurationError e) {
         throw new RuntimeException(e);
         }
      }
   private XMLStreamReader xmlDocumentAsXMLStreamReader(String value) {
      try {
         return XMLInputFactory.newFactory().createXMLStreamReader(
            xmlDocumentAsReader(value)
            );
      } catch(XMLStreamException e) {
         throw new RuntimeException(e);
      } catch(FactoryConfigurationError e) {
         throw new RuntimeException(e);
         }
      }
   private XMLWriteHandle xmlDocumentAsXMLWriteHandle(String value) {
      return new StringHandle(value).withFormat(Format.XML);
      }
"""
}
fun getDocumentMappingConstructors(): Map<String,Map<String,String>> {
  return mapOf(
      "binaryDocument" to mapOf(
          "com.marklogic.client.io.marker.BinaryReadHandle"  to "binaryDocumentAsBinaryReadHandle",
          "com.marklogic.client.io.marker.BinaryWriteHandle" to "binaryDocumentAsBinaryWriteHandle"
          ),
      "array"          to mapOf(
          "java.io.InputStream"                              to "arrayAsInputStream",
          "java.lang.String"                                 to "arrayAsString",
          "com.fasterxml.jackson.databind.node.ArrayNode"    to "arrayAsArrayNode",
          "com.fasterxml.jackson.core.JsonParser"            to "arrayAsJsonParser",
          "com.marklogic.client.io.marker.JSONReadHandle"    to "arrayAsJSONReadHandle",
          "com.marklogic.client.io.marker.JSONWriteHandle"   to "arrayAsJSONWriteHandle"
      ),
      "object"         to mapOf(
          "java.io.InputStream"                              to "objectAsInputStream",
          "java.lang.String"                                 to "objectAsString",
          "com.fasterxml.jackson.databind.node.ObjectNode"   to "objectAsObjectNode",
          "com.fasterxml.jackson.core.JsonParser"            to "objectAsJsonParser",
          "com.marklogic.client.io.marker.JSONReadHandle"    to "objectAsJSONReadHandle",
          "com.marklogic.client.io.marker.JSONWriteHandle"   to "objectAsJSONWriteHandle"
      ),
      "jsonDocument"   to mapOf(
          "java.io.InputStream"                              to "jsonDocumentAsInputStream",
          "java.lang.String"                                 to "jsonDocumentAsString",
          "com.fasterxml.jackson.databind.JsonNode"          to "jsonDocumentAsJsonNode",
          "com.fasterxml.jackson.core.JsonParser"            to "jsonDocumentAsJsonParser",
          "com.marklogic.client.io.marker.JSONReadHandle"    to "jsonDocumentAsJSONReadHandle",
          "com.marklogic.client.io.marker.JSONWriteHandle"   to "jsonDocumentAsJSONWriteHandle"
          ),
      "textDocument"   to mapOf(
          "java.io.InputStream"                              to "textDocumentAsInputStream",
          "java.lang.String"                                 to "textDocumentAsString",
          "com.marklogic.client.io.marker.TextReadHandle"    to "textDocumentAsTextReadHandle",
          "com.marklogic.client.io.marker.TextWriteHandle"   to "textDocumentAsTextWriteHandle"
          ),
      "xmlDocument"    to mapOf(
          "org.w3c.dom.Document"                             to "xmlDocumentAsDocument",
          "org.xml.sax.InputSource"                          to "xmlDocumentAsInputSource",
          "java.io.InputStream"                              to "xmlDocumentAsInputStream",
          "javax.xml.transform.Source"                       to "xmlDocumentAsSource",
          "java.lang.String"                                 to "xmlDocumentAsString",
          "javax.xml.stream.XMLEventReader"                  to "xmlDocumentAsXMLEventReader",
          "javax.xml.stream.XMLStreamReader"                 to "xmlDocumentAsXMLStreamReader",
          "com.marklogic.client.io.marker.XMLReadHandle"     to "xmlDocumentAsXMLReadHandle",
          "com.marklogic.client.io.marker.XMLWriteHandle"    to "xmlDocumentAsXMLWriteHandle"
          )
  )
}

fun main(args: Array<String>) {
  if (args.size == 1) {
    dbfTestGenerate(args[0])
  } else {
    System.err.println("usage: fntestgen testDir")
    System.exit(-1)
  }
}

fun dbfTestGenerate(testDir: String) {
  val javaBaseDir  = testDir+"java/"
  val testPath     = javaBaseDir+TEST_PACKAGE.replace(".", "/")+"/"
  val jsonPath     = testDir+"ml-modules/root/dbfunctiondef/generated/"
  val endpointBase = "/dbf/test/"

  File(testPath).mkdirs()
  File(jsonPath).mkdirs()

  val mapper     = jacksonObjectMapper()
  val serializer = mapper.writerWithDefaultPrettyPrinter()

  val host = System.getenv("TEST_HOST") ?: "localhost"

  val db = DatabaseClientFactory.newClient(
      host,
      8000,
      "DBFUnitTest",
      DatabaseClientFactory.DigestAuthContext("admin", "admin")
  )
  val modDb = DatabaseClientFactory.newClient(
      host,
      8000,
      "DBFUnitTestModules",
      DatabaseClientFactory.DigestAuthContext("admin", "admin")
  )

  val docMgr = db.newJSONDocumentManager()
  val modMgr = modDb.newTextDocumentManager()

  val docMeta = DocumentMetadataHandle()
  val docPerm = docMeta.permissions
  docPerm.add("rest-reader", DocumentMetadataHandle.Capability.READ)
  docPerm.add("rest-writer", DocumentMetadataHandle.Capability.UPDATE)

  val testdefFile = File(testDir+"resources/testdef.json")
  val testdefs    = mapper.readValue<ObjectNode>(testdefFile)
  docMgr.write("/dbf/test.json", docMeta, FileHandle(testdefFile))

  val mappedTestdefFile = File(testDir+"resources/mappedTestdef.json")
  val mappedTestdefs    = mapper.readValue<ObjectNode>(mappedTestdefFile)
  docMgr.write("/dbf/mappedTest.json", docMeta, FileHandle(mappedTestdefFile))

  val atomicMap   = generator.getAtomicDataTypes()
  val atomicNames = atomicMap.keys.toTypedArray()
  val atomicMax   = atomicNames.size - 1

  val documentMap   = generator.getDocumentDataTypes()
  val documentNames = documentMap.keys.toTypedArray()
  val documentMax   = documentNames.size - 1

  val multipleTestTypes    = listOf(
      mapOf("nullable" to false, "multiple" to true),
      mapOf("nullable" to true,  "multiple" to true)
    )
  val nonMultipleTestTypes = listOf(
      mapOf("nullable" to false, "multiple" to false),
      mapOf("nullable" to true,  "multiple" to false)
    )
  val allTestTypes         = listOf(
      mapOf("nullable" to false, "multiple" to false),
      mapOf("nullable" to true,  "multiple" to false),
      mapOf("nullable" to false, "multiple" to true),
      mapOf("nullable" to true,  "multiple" to true)
    )

  val allAtomicParams = atomicNames.indices.map{i ->
    val atomicCurr = atomicNames[i]
    val testMultiple = (i % 3) == 0
    val testNullable = (i % 2) == 0
    mapOf(
        "name"     to "param"+(i + 1), "datatype" to atomicCurr,
        "multiple" to testMultiple,    "nullable" to testNullable
      )
    }
  val allDocumentParams = documentNames.indices.map{i ->
    val documentCurr = documentNames[i]
    val testMultiple = (i % 3) == 0
    val testNullable = (i % 2) == 0
    mapOf(
        "name"     to "param"+(i + 1), "datatype" to documentCurr,
        "multiple" to testMultiple,    "nullable" to testNullable
      )
    }

if (true) {
  val responseBodyTypes = listOf("none", "text", "document", "multipart")
  for (responseBodyNum in responseBodyTypes.indices) {
    val responseBodyType = responseBodyTypes[responseBodyNum]
    val responseBody     = responseBodyType.capitalize()

    val requestBodyTypes = listOf("none", "urlencoded", "multipart")
    for (requestBodyNum in requestBodyTypes.indices) {
      val requestBodyType = requestBodyTypes[requestBodyNum]
      val requestBody     = requestBodyType.capitalize()
      val isSJS           = ((responseBodyNum + requestBodyNum) % 2) == 0

      val requestParams =
        if (responseBodyType === "none")
          null
        else
// TODO: mix of multiple and nullable based on modulo
          when (requestBodyType) {
          "none"       -> null
          "urlencoded" -> listOf(mapOf(
            "name"     to "param1", "datatype" to atomicNames[responseBodyNum],
            "multiple" to true,     "nullable" to false
            ))
          "multipart"  -> listOf(mapOf(
            "name"     to "param1", "datatype" to documentNames[responseBodyNum % documentMax],
            "multiple" to true,     "nullable" to false
            ))
          else -> throw IllegalStateException("""unknown request body type ${requestBodyType}""")
          }
      val responseReturnValue =
        if (requestBodyType === "none")
          null
        else
          when (responseBodyType) {
          "none"      -> null
          "text"      -> mapOf(
            "datatype" to atomicNames[requestBodyNum],
            "multiple" to true, "nullable" to false
            )
          "document"  -> mapOf(
            "datatype" to documentNames[requestBodyNum % documentMax],
            "multiple" to false, "nullable" to false
            )
          "multipart" -> mapOf(
            "datatype" to documentNames[requestBodyNum % documentMax],
            "multiple" to true, "nullable" to false
            )
          else -> throw IllegalStateException("""unknown response body type ${responseBodyType}""")
          }

      for (endpointMethod in listOf("post")) {
        val testBaseStart = endpointMethod+"Of"+requestBody
        val testBaseEnd = "For"+responseBody

        val testBundle       = testBaseStart+testBaseEnd
        val bundleTested     = testBundle.capitalize()+"Bundle"
        val bundleTester     = bundleTested+"Test"
        val bundleJSONPath   = jsonPath+testBundle+"/"
        val bundleFilename   = bundleJSONPath+"service.json"
        val bundleEndpoint   = endpointBase+testBundle+"/"
        val bundleJSONString = serializer.writeValueAsString(mapOf(
            "endpointDirectory" to bundleEndpoint,
            "\$javaClass"       to TEST_PACKAGE+"."+bundleTested
        ))
        File(bundleJSONPath).mkdirs()
        File(bundleFilename).writeText(bundleJSONString, Charsets.UTF_8)

        val testingFuncs = mutableListOf<String>()

        when(responseBodyType) {
          "none" -> {
            when(requestBodyType) {
              "none" -> {
                val testName = testBaseStart+testBaseEnd+"0"
                val noneJSONString = serializer.writeValueAsString(
                    if (responseReturnValue === null) mapOf("functionName" to testName)
                    else mapOf("functionName" to testName, "return" to responseReturnValue)
                )

                persistServerdef(
                    docMgr, modMgr, bundleEndpoint, testName, docMeta, noneJSONString, null, isSJS
                )
                generateClientdef(
                    bundleJSONPath, bundleEndpoint, testName, noneJSONString, null, isSJS
                )
                testingFuncs.add(
                    generateJUnitCallTest(testName, null, responseReturnValue, testdefs)
                )
// TODO: negative tests

// TODO: response tests
              }
              "urlencoded" -> {
                // test of all atomic parameters
                val allParamFuncName   = testBaseStart+"All"+testBaseEnd+"0"
                val allParamJSONString = serializer.writeValueAsString(
                  if (responseReturnValue === null) mapOf(
                    "functionName" to allParamFuncName, "params" to allAtomicParams
                    )
                  else mapOf(
                    "functionName" to allParamFuncName, "params" to allAtomicParams, "return" to responseReturnValue
                    )
                  )
                persistServerdef(
                  docMgr, modMgr, bundleEndpoint, allParamFuncName, docMeta, allParamJSONString, allAtomicParams, isSJS
                  )
                generateClientdef(
                  bundleJSONPath, bundleEndpoint, allParamFuncName, allParamJSONString, allAtomicParams, isSJS
                  )
                testingFuncs.add(
                  generateJUnitCallTest(allParamFuncName, allAtomicParams, responseReturnValue, testdefs)
                  )
                testingFuncs.add(
                  generateJUnitCallTest(
                    allParamFuncName, allAtomicParams, responseReturnValue, testdefs, TestVariant.NULL
                    )
                  )
                testingFuncs.add(
                  generateJUnitCallTest(
                    allParamFuncName, allAtomicParams, responseReturnValue, testdefs, TestVariant.EMPTY
                    )
                  )

                for (i in atomicNames.indices) {
                  val atomicCurr = atomicNames[i]
                  for (testNum in allTestTypes.indices) {
                    val testType     = allTestTypes[testNum]
                    val testMultiple = testType.get("multiple") as Boolean
                    val testNullable = testType.get("nullable") as Boolean
                    val funcParams   = listOf(mapOf(
                      "name"     to "param1",     "datatype" to atomicCurr,
                      "multiple" to testMultiple, "nullable" to testNullable
                      ))
                    val testName     = testBaseStart+atomicCurr.capitalize()+testBaseEnd+testNum
                    val testdef      =
                      if (responseReturnValue === null) mapOf(
                        "functionName"   to testName, "params" to funcParams
                        )
                      else mapOf(
                        "functionName" to testName, "params" to funcParams, "return" to responseReturnValue
                        )

                    val testJSONString = serializer.writeValueAsString(testdef)

                    persistServerdef(
                      docMgr, modMgr, bundleEndpoint, testName, docMeta, testJSONString, funcParams, isSJS
                      )
                    generateClientdef(
                      bundleJSONPath, bundleEndpoint, testName, testJSONString, funcParams, isSJS
                      )
                    testingFuncs.add(
                      generateJUnitCallTest(testName, funcParams, responseReturnValue, testdefs)
                      )

                    if (testNullable) {
                      testingFuncs.add(
                        generateJUnitCallTest(testName, funcParams, responseReturnValue, testdefs, TestVariant.NULL)
                        )
                      if (testMultiple) {
                        testingFuncs.add(
                          generateJUnitCallTest(testName, funcParams, responseReturnValue, testdefs, TestVariant.EMPTY)
                          )
                      }
                    } else {
                      // negative test of actual null value for expected non-nullable value
                      val nullErrName      = testName+"NullErr"
                      val nullErrServerdef = replaceFuncName(testdef, nullErrName)
                      persistServerdef(
                          docMgr, modMgr, bundleEndpoint, nullErrName, docMeta,
                          serializer.writeValueAsString(nullErrServerdef), funcParams, isSJS
                        )
                      val nullErrClientParams = replaceParamValue(funcParams, "nullable", true)
                      val nullErrClientdef    = replaceFuncParams(nullErrServerdef, nullErrClientParams)
                      generateClientdef(
                          bundleJSONPath, bundleEndpoint, nullErrName,
                          serializer.writeValueAsString(nullErrClientdef), nullErrClientParams, isSJS
                        )
                      testingFuncs.add(
                        generateJUnitCallTest(
                          nullErrName, nullErrClientParams, responseReturnValue, testdefs, TestVariant.NULL, true
                          )
                        )

                      if (!testMultiple) {
                        // negative test of actual multiple values for expected single value
                        val multiErrName = testName+"MultiErr"
                        val multiErrServerdef = replaceFuncName(testdef, multiErrName)
                        persistServerdef(
                            docMgr, modMgr, bundleEndpoint, multiErrName, docMeta,
                            serializer.writeValueAsString(multiErrServerdef), funcParams, isSJS
                          )
                        val multiErrClientParams = replaceParamValue(funcParams, "multiple", true)
                        val multiErrClientdef    = replaceFuncParams(multiErrServerdef, multiErrClientParams)
                        generateClientdef(
                            bundleJSONPath, bundleEndpoint, multiErrName,
                            serializer.writeValueAsString(multiErrClientdef), multiErrClientParams, isSJS
                          )
                        testingFuncs.add(
                          generateJUnitCallTest(
                            multiErrName, multiErrClientParams, responseReturnValue, testdefs, TestVariant.VALUE, true
                            )
                          )
                      }

                      if (atomicCurr != "string") {
                        // negative test of actual uncastable data type for expected current data type
                        val typeErrName      = testName+"TypeErr"
                        val typeErrServerdef = replaceFuncName(testdef, typeErrName)
                        persistServerdef(
                            docMgr, modMgr, bundleEndpoint, typeErrName, docMeta,
                            serializer.writeValueAsString(typeErrServerdef), funcParams, isSJS
                          )
                        val typeErrClientParams = replaceParamValue(
                            funcParams, "datatype", uncastableAtomicType(testdefs, atomicCurr)
                          )
                        val typeErrClientdef    = replaceFuncParams(typeErrServerdef, typeErrClientParams)
                        generateClientdef(
                            bundleJSONPath, bundleEndpoint, typeErrName,
                            serializer.writeValueAsString(typeErrClientdef), typeErrClientParams, isSJS
                          )
                        testingFuncs.add(
                          generateJUnitCallTest(
                            typeErrName, typeErrClientParams, responseReturnValue, testdefs, TestVariant.VALUE, true
                            )
                          )
                      }

                      // negative test of actual multiple arity for expected single arity
                      val atomicOther       = atomicNames[if (i == atomicMax) 0 else i + 1]
                      val arityErrName      = testName+"ArityErr"
                      val arityErrServerdef = replaceFuncName(testdef, arityErrName)
                      persistServerdef(
                          docMgr, modMgr, bundleEndpoint, arityErrName, docMeta,
                          serializer.writeValueAsString(arityErrServerdef), funcParams, isSJS
                       )
                      val arityErrClientParams = funcParams.plus(mapOf("name" to "param2", "datatype" to atomicOther))
                      val arityErrClientdef    = replaceFuncParams(arityErrServerdef, arityErrClientParams)
                      generateClientdef(
                          bundleJSONPath, bundleEndpoint, arityErrName,
                          serializer.writeValueAsString(arityErrClientdef), arityErrClientParams, isSJS
                        )
                      testingFuncs.add(
                        generateJUnitCallTest(
                          arityErrName, arityErrClientParams, responseReturnValue, testdefs, TestVariant.VALUE, true
                          )
                        )
                    }
                  }
                }
              }
              "multipart" -> {
                // test of all document parameters
                val allParamFuncName   = testBaseStart+"All"+testBaseEnd+"0"
                val allParamJSONString = serializer.writeValueAsString(
                  if (responseReturnValue === null) mapOf(
                    "functionName" to allParamFuncName, "params" to allDocumentParams
                    )
                  else mapOf(
                    "functionName" to allParamFuncName, "params" to allDocumentParams, "return" to responseReturnValue
                    )
                  )
                persistServerdef(
                  docMgr, modMgr, bundleEndpoint, allParamFuncName, docMeta, allParamJSONString, allDocumentParams, isSJS
                  )
                generateClientdef(
                  bundleJSONPath, bundleEndpoint, allParamFuncName, allParamJSONString, allDocumentParams, isSJS
                  )
                testingFuncs.add(
                  generateJUnitCallTest(allParamFuncName, allDocumentParams, responseReturnValue, testdefs)
                  )
                testingFuncs.add(
                  generateJUnitCallTest(
                    allParamFuncName, allDocumentParams, responseReturnValue, testdefs, TestVariant.NULL
                    )
                  )
                testingFuncs.add(
                  generateJUnitCallTest(
                    allParamFuncName, allDocumentParams, responseReturnValue, testdefs, TestVariant.EMPTY
                    )
                  )

                for (i in documentNames.indices) {
                  val docCurr = documentNames[i]
                  val atomic1 = atomicNames[i]
                  val atomic2 = atomicNames[if (i == atomicMax) 0 else i + 1]
                  val docTestTypes = allTestTypes
                  for (testNum in docTestTypes.indices) {
                    val testType      = docTestTypes[testNum]
                    val testMultiple  = testType.get("multiple") as Boolean
                    val testNullable  = testType.get("nullable") as Boolean
                    val docFuncParams = listOf(mapOf(
                        "name"     to "param1",     "datatype" to docCurr,
                        "multiple" to testMultiple, "nullable" to testNullable
                    ))
                    val testMax     = 2
                    val docTestName = testBaseStart+docCurr.capitalize()+testBaseEnd+(testNum * testMax)
                    val docTestdef  =
                      if (responseReturnValue === null) mapOf(
                        "functionName" to docTestName, "params" to docFuncParams
                        )
                      else mapOf(
                        "functionName" to docTestName, "params" to docFuncParams, "return" to responseReturnValue
                        )
                    for (j in 1 .. testMax) {
                      val funcParams =
                          if (j < testMax) docFuncParams
                          else docFuncParams + listOf(
                              mapOf("name" to "param2", "datatype" to atomic1),
                              mapOf("name" to "param3", "datatype" to atomic2)
                          )

                      val testName = testBaseStart+docCurr.capitalize()+testBaseEnd+((testNum * testMax) + j - 1)
                      val testdef1 = replaceFuncName(docTestdef, testName)
                      val testdef2 =
                          if (j < testMax) testdef1
                          else replaceFuncParams(testdef1, funcParams)

                      val testJSONString = serializer.writeValueAsString(testdef2)

                      persistServerdef(
                        docMgr, modMgr, bundleEndpoint, testName, docMeta, testJSONString, funcParams, isSJS
                        )
                      generateClientdef(
                        bundleJSONPath, bundleEndpoint, testName, testJSONString, funcParams, isSJS
                        )
                      testingFuncs.add(
                        generateJUnitCallTest(testName, funcParams, responseReturnValue, testdefs)
                        )
                    }

                    if (testNullable) {
                      testingFuncs.add(
                        generateJUnitCallTest(
                          docTestName, docFuncParams, responseReturnValue, testdefs, TestVariant.NULL
                          )
                        )
                      if (testMultiple) {
                        testingFuncs.add(
                          generateJUnitCallTest(
                            docTestName, docFuncParams, responseReturnValue, testdefs, TestVariant.EMPTY
                            )
                          )
                      }
                    } else {
                      // negative test of actual null value for expected non-nullable value
                      val nullErrName      = docTestName+"NullErr"
                      val nullErrServerdef = replaceFuncName(docTestdef, nullErrName)
                      persistServerdef(
                          docMgr, modMgr, bundleEndpoint, nullErrName, docMeta,
                          serializer.writeValueAsString(nullErrServerdef), docFuncParams, isSJS
                        )
                      val nullErrClientParams = replaceParamValue(docFuncParams, "nullable", true)
                      val nullErrClientdef    = replaceFuncParams(nullErrServerdef, nullErrClientParams)
                      generateClientdef(
                          bundleJSONPath, bundleEndpoint, nullErrName,
                          serializer.writeValueAsString(nullErrClientdef), nullErrClientParams, isSJS
                        )
                      testingFuncs.add(
                        generateJUnitCallTest(
                          nullErrName, nullErrClientParams, responseReturnValue, testdefs, TestVariant.NULL, true
                          )
                        )

                      if (!testMultiple) {
                        // negative test of actual multiple values for expected single value
                        val multiErrName = docTestName+"MultiErr"
                        val multiErrServerdef = replaceFuncName(docTestdef, multiErrName)
                        persistServerdef(
                            docMgr, modMgr, bundleEndpoint, multiErrName, docMeta,
                            serializer.writeValueAsString(multiErrServerdef), docFuncParams, isSJS
                          )
                        val multiErrClientParams = replaceParamValue(docFuncParams, "multiple", true)
                        val multiErrClientdef    = replaceFuncParams(multiErrServerdef, multiErrClientParams)
                        generateClientdef(
                            bundleJSONPath, bundleEndpoint, multiErrName,
                            serializer.writeValueAsString(multiErrClientdef), multiErrClientParams, isSJS
                          )
                        testingFuncs.add(
                          generateJUnitCallTest(
                            multiErrName, multiErrClientParams, responseReturnValue, testdefs, TestVariant.VALUE, true
                            )
                          )
                      }

                      if (docCurr === "jsonDocument" || docCurr === "xmlDocument") {
                        // negative test of actual different data type for expected current data type
                        val docOther         =
                            when (docCurr) {
                              "array", "object", "jsonDocument" -> "xmlDocument"
                              "xmlDocument" -> "jsonDocument"
                              else          -> throw IllegalArgumentException("No type error test for "+docCurr)
                            }
                        val typeErrName      = docTestName+"TypeErr"
                        val typeErrServerdef = replaceFuncName(docTestdef, typeErrName)
                        persistServerdef(
                            docMgr, modMgr, bundleEndpoint, typeErrName, docMeta,
                            serializer.writeValueAsString(typeErrServerdef), docFuncParams, isSJS
                        )
                        val typeErrClientParams = replaceParamValue(docFuncParams, "datatype", docOther)
                        val typeErrClientdef    = replaceFuncParams(typeErrServerdef, typeErrClientParams)
                        generateClientdef(
                            bundleJSONPath, bundleEndpoint, typeErrName,
                            serializer.writeValueAsString(typeErrClientdef), typeErrClientParams, isSJS
                        )
                        testingFuncs.add(
                            generateJUnitCallTest(
                                typeErrName, typeErrClientParams, responseReturnValue, testdefs, TestVariant.VALUE, true
                            )
                        )
                      }

                      // negative test of actual multiple arity for expected single arity
                      val atomicOther       = atomicNames[if (i == atomicMax) 0 else i + 1]
                      val arityErrName      = docTestName+"ArityErr"
                      val arityErrServerdef = replaceFuncName(docTestdef, arityErrName)
                      persistServerdef(
                          docMgr, modMgr, bundleEndpoint, arityErrName, docMeta,
                          serializer.writeValueAsString(arityErrServerdef), docFuncParams, isSJS
                        )
                      val arityErrClientParams = docFuncParams.plus(mapOf("name" to "param2", "datatype" to atomicOther))
                      val arityErrClientdef    = replaceFuncParams(arityErrServerdef, arityErrClientParams)
                      generateClientdef(
                          bundleJSONPath, bundleEndpoint, arityErrName,
                          serializer.writeValueAsString(arityErrClientdef), arityErrClientParams, isSJS
                        )
                      testingFuncs.add(
                        generateJUnitCallTest(
                          arityErrName, arityErrClientParams, responseReturnValue, testdefs, TestVariant.VALUE, true
                          )
                        )
                    }
                  }
                }
              }
            else -> throw IllegalStateException("""unknown request body type ${requestBodyType}""")
            }} // end of branching on request body type
          "text" -> {
            for (i in atomicNames.indices) {
              val atomicCurr = atomicNames[i]
              for (testNum in nonMultipleTestTypes.indices) {
                val testType     = nonMultipleTestTypes[testNum]
                val testNullable = testType.get("nullable") as Boolean
                val funcReturn   = mapOf(
                  "datatype" to atomicCurr, "nullable" to testNullable
                  )
                val testName     = testBaseStart+testBaseEnd+atomicCurr.capitalize()+testNum
                val testdef      =
                  if (requestParams === null) mapOf(
                    "functionName" to testName, "return" to funcReturn
                    )
                  else mapOf(
                    "functionName" to testName, "params" to requestParams, "return" to funcReturn
                    )

                val testJSONString = serializer.writeValueAsString(testdef)

                persistServerdef(
                  docMgr, modMgr, bundleEndpoint, testName, docMeta, testJSONString, requestParams, isSJS
                  )
                generateClientdef(
                  bundleJSONPath, bundleEndpoint, testName, testJSONString, requestParams, isSJS
                  )
                testingFuncs.add(
                  generateJUnitCallTest(testName, requestParams, funcReturn, testdefs)
                  )

                if (testNullable) {
                  // positive test of actual null value for expected nullable value
                  val nulledTestName       = testName+"ReturnNull"
                  val nulledTestdef        = replaceFuncName(testdef, nulledTestName)
                  val nulledTestJSONString = serializer.writeValueAsString(nulledTestdef)
                  persistServerdef(
                    docMgr, modMgr, bundleEndpoint, nulledTestName, docMeta, nulledTestJSONString, requestParams, isSJS
                    )
                  generateClientdef(
                    bundleJSONPath, bundleEndpoint, nulledTestName, nulledTestJSONString, requestParams, isSJS
                    )
                  testingFuncs.add(
                    generateJUnitCallTest(
                      nulledTestName, requestParams, funcReturn, testdefs, TestVariant.NULL
                      )
                    )
                } else {

/* TODO: enable the tests appropriate for non-multiple single value response

// TODO: client-side errors when null returned for any non-nullable
                  if (!testMultiple) {
                    val mappedType = getJavaDataType(atomicCurr, null, "atomic", testNullable, testMultiple)
                    if (mappedType !== "String") {
                      // negative test of actual null value for expected non-nullable value
                      val nulledTestName       = testName+"ErrReturnNull"
                      val nulledTestdef        = replaceFuncName(testdef, nulledTestName)
                      val nulledTestJSONString = serializer.writeValueAsString(nulledTestdef)
                      generateClientdef(bundleJSONPath, bundleEndpoint, nulledTestName, nulledTestJSONString, isSJS)
                      testingFuncs.add(
                        generateJUnitCallTest(
                          nulledTestName, requestParams, funcReturn, testdefs, expectError=true
                          )
                        )
                      persistServerdef(docMgr, modMgr, bundleEndpoint, nulledTestName, docMeta, nulledTestJSONString, isSJS)

// TODO: return one for testMultiple of true

                      // any non-true string coerces to false
                      if (atomicCurr != "boolean") {
                        // negative test of actual uncastable data type for expected current data type
                        val typeErrName       = testName+"TypeErr"
                        val typeErrClientdef  = replaceFuncName(testdef, typeErrName)
                        generateClientdef(
                          bundleJSONPath, bundleEndpoint, typeErrName,
                          serializer.writeValueAsString(typeErrClientdef), isSJS
                          )
                        testingFuncs.add(
                          generateJUnitCallTest(
                            typeErrName, requestParams, funcReturn, testdefs, expectError=true
                            )
                          )
                        val typeErrFuncReturn = replaceDataType(funcReturn, uncastableAtomicType(testdefs, atomicCurr))
                        val typeErrServerdef  = replaceFuncReturn(typeErrClientdef, typeErrFuncReturn)
                        persistServerdef(
                          docMgr, modMgr, bundleEndpoint, typeErrName, docMeta,
                          serializer.writeValueAsString(typeErrServerdef), isSJS
                          )
                      }
                    }

                    // negative test of actual multiple values for expected single value
                    val multiErrName = testName+"MultiErr"
                    val multiErrClientdef = replaceFuncName(testdef, multiErrName)
                    generateClientdef(
                      bundleJSONPath, bundleEndpoint, multiErrName,
                      serializer.writeValueAsString(multiErrClientdef), isSJS
                      )
                    testingFuncs.add(
                      generateJUnitCallTest(
                        multiErrName, requestParams, funcReturn, testdefs, expectError=true
                        )
                      )
                    val multiErrServerReturn = replaceMultiple(funcReturn, true)
                    val multiErrServerdef    = replaceFuncReturn(multiErrClientdef, multiErrServerReturn)
                    persistServerdef(
                      docMgr, modMgr, bundleEndpoint, multiErrName, docMeta,
                      serializer.writeValueAsString(multiErrServerdef), isSJS
                    )
                  } */
                }
              }
            }}
          "document" -> {
            for (i in documentNames.indices) {
              val docCurr = documentNames[i]
// TODO: atomics as well as documents
//            val atomic1 = atomicNames[i]
//            val atomic2 = atomicNames[if (i == atomicMax) 0 else i + 1]

              val docTestTypes = nonMultipleTestTypes
              for (testNum in docTestTypes.indices) {
                val testType = docTestTypes[testNum]
                val testMultiple = testType.get("multiple") as Boolean
                val testNullable = testType.get("nullable") as Boolean
                val funcReturn   = mapOf(
                  "datatype" to docCurr,
                  "multiple" to testMultiple, "nullable" to testNullable
                  )
                val testName     = testBaseStart+testBaseEnd+docCurr.capitalize()+testNum
                val testdef      =
                  if (requestParams === null) mapOf(
                    "functionName" to testName, "return" to funcReturn
                    )
                  else mapOf(
                    "functionName" to testName, "params" to requestParams, "return" to funcReturn
                    )

                val testJSONString = serializer.writeValueAsString(testdef)

                persistServerdef(docMgr, modMgr, bundleEndpoint, testName, docMeta, testJSONString, requestParams, isSJS)
                generateClientdef(bundleJSONPath, bundleEndpoint, testName, testJSONString, requestParams, isSJS)

                testingFuncs.add(
                  generateJUnitCallTest(testName, requestParams, funcReturn, testdefs)
                  )

                if (testNullable) {
                  // positive test of actual null value for expected nullable value
                  val nulledTestName       = testName+"ReturnNull"
                  val nulledTestdef        = replaceFuncName(testdef, nulledTestName)
                  val nulledTestJSONString = serializer.writeValueAsString(nulledTestdef)
                  persistServerdef(
                    docMgr, modMgr, bundleEndpoint, nulledTestName, docMeta, nulledTestJSONString, requestParams, isSJS
                    )
                  generateClientdef(
                    bundleJSONPath, bundleEndpoint, nulledTestName, nulledTestJSONString, requestParams, isSJS
                    )
                  testingFuncs.add(
                    generateJUnitCallTest(
                      nulledTestName, requestParams, funcReturn, testdefs, TestVariant.NULL
                      )
                    )
                } else {
// TODO: client-side errors when null returned for any non-nullable
                  // negative test of actual null value for expected non-nullable value
                  val nulledTestName       = testName+"ErrReturnNull"
                  val nulledTestdef        = replaceFuncName(testdef, nulledTestName)
                  val nulledTestJSONString = serializer.writeValueAsString(nulledTestdef)
                  generateClientdef(
                    bundleJSONPath, bundleEndpoint, nulledTestName, nulledTestJSONString, requestParams, isSJS
                    )
                  testingFuncs.add(
                    generateJUnitCallTest(
                      nulledTestName, requestParams, funcReturn, testdefs, expectError=true
                      )
                    )
                  persistServerdef(
                    docMgr, modMgr, bundleEndpoint, nulledTestName, docMeta, nulledTestJSONString, requestParams, isSJS
                    )

// TODO: return one for testMultiple of true

                  // negative test of actual uncastable data type for expected current data type
                  val docOther         = pickDocOther(documentNames, documentMax, docCurr, i)
                  val typeErrName      = testName+"TypeErr"
                  val typeErrClientdef = replaceFuncName(testdef, typeErrName)
                  generateClientdef(
                    bundleJSONPath, bundleEndpoint, typeErrName,
                    serializer.writeValueAsString(typeErrClientdef), requestParams, isSJS
                    )
                  testingFuncs.add(
                    generateJUnitCallTest(
                      typeErrName, requestParams, funcReturn, testdefs, expectError=true
                      )
                    )
                  val typeErrFuncReturn = replaceDataType(funcReturn, docOther)
                  val typeErrServerdef  = replaceFuncReturn(typeErrClientdef, typeErrFuncReturn)
                  persistServerdef(
                      docMgr, modMgr, bundleEndpoint, typeErrName, docMeta,
                      serializer.writeValueAsString(typeErrServerdef), requestParams, isSJS
                    )
                }
              }
            }}
          "multipart" -> {
            for (i in documentNames.indices) {
              val docCurr = documentNames[i]
// TODO: atomics as well as documents
//            val atomic1 = atomicNames[i]
//            val atomic2 = atomicNames[if (i == atomicMax) 0 else i + 1]

              val docTestTypes = multipleTestTypes
              for (testNum in docTestTypes.indices) {
                val testType = docTestTypes[testNum]
                val testMultiple = testType.get("multiple") as Boolean
                val testNullable = testType.get("nullable") as Boolean
                val funcReturn   = mapOf(
                    "datatype" to docCurr,
                    "multiple" to testMultiple, "nullable" to testNullable
                )
                val testName     = testBaseStart + testBaseEnd + docCurr.capitalize() + testNum
                val testdef      =
                  if (requestParams === null) mapOf(
                    "functionName" to testName, "return" to funcReturn
                    )
                  else mapOf(
                    "functionName" to testName, "params" to requestParams, "return" to funcReturn
                    )

                val testJSONString = serializer.writeValueAsString(testdef)

                persistServerdef(
                  docMgr, modMgr, bundleEndpoint, testName, docMeta, testJSONString, requestParams, isSJS
                  )
                generateClientdef(
                  bundleJSONPath, bundleEndpoint, testName, testJSONString, requestParams, isSJS
                  )

                testingFuncs.add(
                  generateJUnitCallTest(testName, requestParams, funcReturn, testdefs)
                  )

                if (testNullable) {
                  // positive test of actual null value for expected nullable value
                  val nulledTestName       = testName+"ReturnNull"
                  val nulledTestdef        = replaceFuncName(testdef, nulledTestName)
                  val nulledTestJSONString = serializer.writeValueAsString(nulledTestdef)
                  persistServerdef(
                    docMgr, modMgr, bundleEndpoint, nulledTestName, docMeta, nulledTestJSONString, requestParams, isSJS
                    )
                  generateClientdef(
                    bundleJSONPath, bundleEndpoint, nulledTestName, nulledTestJSONString, requestParams, isSJS
                    )
                  testingFuncs.add(
                    generateJUnitCallTest(
                      nulledTestName, requestParams, funcReturn, testdefs, TestVariant.NULL
                      )
                    )
                } else {
// TODO: client-side errors when null returned for any non-nullable
                  if (!testMultiple) {
                    // negative test of actual null value for expected non-nullable value
                    val nulledTestName       = testName+"ErrReturnNull"
                    val nulledTestdef        = replaceFuncName(testdef, nulledTestName)
                    val nulledTestJSONString = serializer.writeValueAsString(nulledTestdef)
                    generateClientdef(
                      bundleJSONPath, bundleEndpoint, nulledTestName, nulledTestJSONString, requestParams, isSJS
                      )
                    testingFuncs.add(
                      generateJUnitCallTest(
                        nulledTestName, requestParams, funcReturn, testdefs, expectError=true
                        )
                      )
                    persistServerdef(
                      docMgr, modMgr, bundleEndpoint, nulledTestName, docMeta, nulledTestJSONString, requestParams, isSJS
                      )

                    // negative test of actual uncastable data type for expected current data type
                    val docOther          = pickDocOther(documentNames, documentMax, docCurr, i)
                    val typeErrName       = testName+"TypeErr"
                    val typeErrClientdef  = replaceFuncName(testdef, typeErrName)
                    generateClientdef(
                      bundleJSONPath, bundleEndpoint, typeErrName,
                      serializer.writeValueAsString(typeErrClientdef), requestParams, isSJS
                      )
                    testingFuncs.add(
                      generateJUnitCallTest(
                        typeErrName, requestParams, funcReturn, testdefs, expectError=true
                        )
                      )
                    val typeErrFuncReturn = replaceDataType(funcReturn, docOther)
                    val typeErrServerdef  = replaceFuncReturn(typeErrClientdef, typeErrFuncReturn)
                    persistServerdef(
                      docMgr, modMgr, bundleEndpoint, typeErrName, docMeta,
                      serializer.writeValueAsString(typeErrServerdef), requestParams, isSJS
                      )

                    // negative test of actual multiple values for expected single value
                    val multiErrName = testName+"MultiErr"
                    val multiErrClientdef = replaceFuncName(testdef, multiErrName)
                    generateClientdef(
                      bundleJSONPath, bundleEndpoint, multiErrName,
                      serializer.writeValueAsString(multiErrClientdef), requestParams, isSJS
                      )
                    testingFuncs.add(
                      generateJUnitCallTest(
                        multiErrName, requestParams, funcReturn, testdefs, expectError=true
                        )
                      )
                    val multiErrServerReturn = replaceMultiple(funcReturn, true)
                    val multiErrServerdef    = replaceFuncReturn(multiErrClientdef, multiErrServerReturn)
                    persistServerdef(
                      docMgr, modMgr, bundleEndpoint, multiErrName, docMeta,
                      serializer.writeValueAsString(multiErrServerdef), requestParams, isSJS
                      )
                  }
                }
              }
            }}
          else -> throw IllegalStateException("""unknown response body type ${responseBodyType}""")
        } // end of branching on response body type

        generator.serviceBundleToJava(bundleFilename, javaBaseDir)
        writeJUnitRequestTest(
            testPath+bundleTester+".java",
            generateJUnitTest(bundleTested, bundleTester, testingFuncs)
          )
      } // end of iteration on endpoint methods
    } // end of iteration on request body types
  } // end of iteration on response body types
}

// TODO: function parameterized for atomics or documents
if (true) {
  val atomicMappingConstructors     = getAtomicMappingConstructors()
  val atomicMappingBundle           = "mapAtomics"
  val atomicMappingBundleTested     = atomicMappingBundle.capitalize()+"Bundle"
  val atomicMappingBundleTester     = atomicMappingBundleTested+"Test"
  val atomicMappingBundleJSONPath   = jsonPath+atomicMappingBundle+"/"
  val atomicMappingBundleFilename   = atomicMappingBundleJSONPath+"service.json"
  val atomicMappingBundleEndpoint   = endpointBase+atomicMappingBundle+"/"
  val atomicMappingBundleJSONString = serializer.writeValueAsString(mapOf(
      "endpointDirectory" to atomicMappingBundleEndpoint,
      "\$javaClass"       to TEST_PACKAGE+"."+atomicMappingBundleTested
  ))
  File(atomicMappingBundleJSONPath).mkdirs()
  File(atomicMappingBundleFilename).writeText(atomicMappingBundleJSONString, Charsets.UTF_8)

  val atomicMappingTestingFuncs     = mutableListOf<String>()

  val atomicMappingDatatypes = atomicMappingConstructors.keys.toTypedArray()
  for (datatypeNum in atomicMappingDatatypes.indices) {
    val datatype             = atomicMappingDatatypes[datatypeNum]
    val testBaseStart        = atomicMappingBundle+datatype.capitalize()
    val datatypeConstructors = atomicMappingConstructors[datatype] as Map<String,String>
    val isSJS                = (datatypeNum % 2) == 0
    for (mappedType in datatypeConstructors.keys) {
      // mappedType.capitalize().replace('.', '_')
      val testMapped        = mappedType.split('.').map{word -> word.capitalize()}.joinToString("")
      val mappedConstructor = datatypeConstructors[mappedType] as String
      val typeConstructors  = mapOf(datatype to mappedConstructor)
      for (testNum in allTestTypes.indices) {
        val testType       = allTestTypes[testNum]
        val testMultiple   = testType.get("multiple") as Boolean
        val testNullable   = testType.get("nullable") as Boolean
        val funcParams     = listOf(mapOf(
            "name"        to "param1",     "datatype" to datatype,
            "multiple"    to testMultiple, "nullable" to testNullable,
            "\$javaClass" to mappedType
        ))
        var testName       = testBaseStart+"Of"+testMapped+"ForNone"+testNum
        var testdef        = mapOf("functionName" to testName, "params" to funcParams)

        var testJSONString = serializer.writeValueAsString(testdef)

        persistServerdef(
          docMgr, modMgr, atomicMappingBundleEndpoint, testName, docMeta, testJSONString, funcParams, isSJS
          )
        generateClientdef(
          atomicMappingBundleJSONPath, atomicMappingBundleEndpoint, testName, testJSONString, funcParams, isSJS
          )
        atomicMappingTestingFuncs.add(
            generateJUnitCallTest(
                testName, funcParams, null, testdefs,
                typeConstructors = typeConstructors, mappedTestdefs = mappedTestdefs
            )
        )

        val funcReturn = mapOf(
            "datatype" to datatype, "multiple" to testMultiple, "nullable" to testNullable,
            "\$javaClass" to mappedType
        )
        testName       = testBaseStart+"OfNoneForText"+testMapped+testNum
        testdef        = mapOf("functionName" to testName, "return" to funcReturn)

        testJSONString = serializer.writeValueAsString(testdef)

        persistServerdef(
          docMgr, modMgr, atomicMappingBundleEndpoint, testName, docMeta, testJSONString, null, isSJS
          )
        generateClientdef(
          atomicMappingBundleJSONPath, atomicMappingBundleEndpoint, testName, testJSONString, null, isSJS
          )
        atomicMappingTestingFuncs.add(
            generateJUnitCallTest(
                testName, null, funcReturn, testdefs,
                typeConstructors = typeConstructors, mappedTestdefs = mappedTestdefs
            )
        )
      }
    }
  }

  generator.serviceBundleToJava(atomicMappingBundleFilename, javaBaseDir)
  writeJUnitRequestTest(
      testPath+atomicMappingBundleTester+".java",
      generateJUnitTest(atomicMappingBundleTested, atomicMappingBundleTester, atomicMappingTestingFuncs,
          extraImports = getAtomicMappingImports(), extraMembers = getAtomicMappingMembers())
  )
}

if (true) {
  val documentMappingConstructors     = getDocumentMappingConstructors()
  val documentMappingBundle           = "mapDocuments"
  val documentMappingBundleTested     = documentMappingBundle.capitalize()+"Bundle"
  val documentMappingBundleTester     = documentMappingBundleTested+"Test"
  val documentMappingBundleJSONPath   = jsonPath+documentMappingBundle+"/"
  val documentMappingBundleFilename   = documentMappingBundleJSONPath+"service.json"
  val documentMappingBundleEndpoint   = endpointBase+documentMappingBundle+"/"
  val documentMappingBundleJSONString = serializer.writeValueAsString(mapOf(
      "endpointDirectory" to documentMappingBundleEndpoint,
      "\$javaClass"       to TEST_PACKAGE+"."+documentMappingBundleTested
  ))
  File(documentMappingBundleJSONPath).mkdirs()
  File(documentMappingBundleFilename).writeText(documentMappingBundleJSONString, Charsets.UTF_8)

  val documentMappingTestingFuncs     = mutableListOf<String>()

  val documentMappedDatatypes = documentMappingConstructors.keys.toTypedArray()
  for (datatypeNum in documentMappedDatatypes.indices) {
    val datatype             = documentMappedDatatypes[datatypeNum]
    val testBaseStart        = documentMappingBundle+datatype.capitalize()
    val datatypeConstructors = documentMappingConstructors[datatype] as Map<String,String>
    val isSJS                = (datatypeNum % 2) == 0
    for (mappedType in datatypeConstructors.keys) {
      val testMapped        = mappedType.split('.').map{word -> word.capitalize()}.joinToString("")
      val mappedConstructor = datatypeConstructors[mappedType] as String
      val typeConstructors  = mapOf(datatype to mappedConstructor)
      for (testNum in allTestTypes.indices) {
        val testType = allTestTypes[testNum]
        val testMultiple = testType.get("multiple") as Boolean
        val testNullable = testType.get("nullable") as Boolean

        if (!mappedType.endsWith("ReadHandle")) {
          val funcParams = listOf(mapOf(
              "name" to "param1", "datatype" to datatype,
              "multiple" to testMultiple, "nullable" to testNullable,
              "\$javaClass" to mappedType
          ))
          val testName = testBaseStart + "Of" + testMapped + "ForNone" + testNum
          val testdef = mapOf("functionName" to testName, "params" to funcParams)

          val testJSONString = serializer.writeValueAsString(testdef)

          persistServerdef(
              docMgr, modMgr, documentMappingBundleEndpoint, testName, docMeta, testJSONString, funcParams, isSJS
          )
          generateClientdef(
              documentMappingBundleJSONPath, documentMappingBundleEndpoint, testName, testJSONString, funcParams, isSJS
          )
          documentMappingTestingFuncs.add(
              generateJUnitCallTest(
                  testName, funcParams, null, testdefs,
                  typeConstructors = typeConstructors, mappedTestdefs = mappedTestdefs
              )
          )
        }

        if (!mappedType.endsWith("WriteHandle")) {
          val funcReturn = mapOf(
              "datatype" to datatype, "multiple" to testMultiple, "nullable" to testNullable,
              "\$javaClass" to mappedType
          )
          val testName       = testBaseStart+"OfNoneForText"+testMapped+testNum
          val testdef        = mapOf("functionName" to testName, "return" to funcReturn)

          val testJSONString = serializer.writeValueAsString(testdef)

          persistServerdef(
              docMgr, modMgr, documentMappingBundleEndpoint, testName, docMeta, testJSONString, null, isSJS
          )
          generateClientdef(
              documentMappingBundleJSONPath, documentMappingBundleEndpoint, testName, testJSONString, null, isSJS
          )
          documentMappingTestingFuncs.add(
              generateJUnitCallTest(
                  testName, null, funcReturn, testdefs,
                  typeConstructors = typeConstructors, mappedTestdefs = mappedTestdefs
              )
          )
        }
      }
    }
  }

// System.out.println(documentMappingTestingFuncs.joinToString("\n"))
  generator.serviceBundleToJava(documentMappingBundleFilename, javaBaseDir)
  writeJUnitRequestTest(
      testPath+documentMappingBundleTester+".java",
      generateJUnitTest(documentMappingBundleTested, documentMappingBundleTester, documentMappingTestingFuncs,
          extraImports = getDocumentMappingImports(), extraMembers = getDocumentMappingMembers())
  )
}

if (true) {
  for (testName in listOf("sessions", "described")) {
    val manualBundleJSONPath = "${testDir}ml-modules/root/dbfunctiondef/positive/${testName}/"
    val manualBundleEndpoint = endpointBase+testName+"/"
    val manualBundleFilename = manualBundleJSONPath+"service.json"

    generator.serviceBundleToJava(manualBundleFilename, javaBaseDir)
    File(manualBundleJSONPath)
        .listFiles()
        .filter{file -> (file.extension == "api")}
        .forEach{apiFile ->
          val baseName = apiFile.nameWithoutExtension
          val apiName  = baseName +".api"
          val modName  = baseName +".sjs"
          modMgr.write(
              modMgr.newWriteSet()
                  .add(manualBundleEndpoint+apiName, docMeta, FileHandle(apiFile))
                  .add(manualBundleEndpoint+modName, docMeta, FileHandle(File(manualBundleJSONPath+modName)))
          )
        }
  }
}

if (true) {
  val moduleInitName       = "initializer"
  val moduleInitParams     = allTestTypes.mapIndexed{i, testdef -> mapOf(
      "name"     to "param" + (i + 1),
      "datatype" to atomicNames[i],
      "multiple" to testdef["multiple"],
      "nullable" to testdef["nullable"]
  )}
  val moduleInitReturn     = mapOf("datatype" to "boolean")
  val moduleInitTestdef    = mapOf(
      "functionName" to moduleInitName,
      "params"       to moduleInitParams,
      "return"       to moduleInitReturn
      )
  val moduleInitTestString = serializer.writeValueAsString(moduleInitTestdef)
  for (modExtension in listOf("sjs", "xqy")) {
    val moduleInitBundle           = "moduleInit"+modExtension.capitalize()
    val moduleInitBundleTested     = moduleInitBundle.capitalize()+"Bundle"
    val moduleInitBundleTester     = moduleInitBundleTested+"Test"
    val moduleInitBundleJSONPath   = jsonPath+moduleInitBundle+"/"
    val moduleInitBundleFilename   = moduleInitBundleJSONPath+"service.json"
    val moduleInitBundleEndpoint   = endpointBase+moduleInitBundle+"/"
    val moduleInitBundleJSONString = serializer.writeValueAsString(mapOf(
        "endpointDirectory" to moduleInitBundleEndpoint,
        "\$javaClass"       to TEST_PACKAGE+"."+moduleInitBundleTested
    ))
    File(moduleInitBundleJSONPath).mkdirs()
    File(moduleInitBundleFilename).writeText(moduleInitBundleJSONString, Charsets.UTF_8)

    val moduleInitAPIName     = moduleInitName+".api"
    val moduleInitAPIFilename = moduleInitBundleJSONPath+moduleInitAPIName
    val moduleInitAPIFile     = File(moduleInitAPIFilename)

    val moduleInitModName     = moduleInitName+"."+modExtension
    val moduleInitModFilename = moduleInitBundleJSONPath+moduleInitModName
    val moduleInitModFile     = File(moduleInitModFilename)

    moduleInitAPIFile.writeText(moduleInitTestString, Charsets.UTF_8)

    if (moduleInitModFile.exists()) {
      moduleInitModFile.delete()
    }
    generator.endpointDeclToModStubImpl(moduleInitAPIFilename, modExtension)
    moduleInitModFile.appendText("""
${
    when (modExtension) {
      "sjs" -> "true;"
      "xqy" -> "fn:true()"
      else  -> IllegalArgumentException("unknown module extension: "+modExtension)
    }}
""", Charsets.UTF_8)

    generator.serviceBundleToJava(moduleInitBundleFilename, javaBaseDir)

    modMgr.write(
        modMgr.newWriteSet()
            .add(moduleInitBundleEndpoint+moduleInitAPIName, docMeta, FileHandle(moduleInitAPIFile))
            .add(moduleInitBundleEndpoint+moduleInitModName, docMeta, FileHandle(moduleInitModFile))
        )

    val moduleInitTestingFunctions = listOf(
        generateJUnitCallTest(moduleInitName, moduleInitParams, moduleInitReturn, testdefs)
        )
    writeJUnitRequestTest(
        testPath+moduleInitBundleTester+".java",
        generateJUnitTest(moduleInitBundleTested, moduleInitBundleTester, moduleInitTestingFunctions)
        )
  }
}

  db.release()
}
fun pickDocOther(documentNames: Array<String>, documentMax: Int, docCurr: String, i: Int
) : String {
  val docOther =
      when (docCurr) {
        "array", "object", "jsonDocument" -> "xmlDocument"
        "xmlDocument"                     -> "jsonDocument"
        else                              -> documentNames[if (i == documentMax) 0 else i + 1]
      }
  return docOther
}
fun replaceFuncName(funcdef: Map<String,*>, funcName: String) : Map<String,*> {
  return replaceKeyValue(funcdef, "functionName", funcName)
}
fun replaceFuncParams(funcdef: Map<String,*>, funcParams: List<Map<String,*>>) : Map<String,*> {
  return replaceKeyValue(funcdef, "params", funcParams)
}
fun replaceParamValue(funcParams: List<Map<String,*>>, key: String, value: Any) : List<Map<String,*>> {
  return funcParams.map{funcParam -> replaceKeyValue(funcParam, key, value)}
}
fun replaceFuncReturn(funcdef: Map<String,*>, funcReturn: Map<String,*>) : Map<String,*> {
  return replaceKeyValue(funcdef, "return", funcReturn)
}
fun replaceDataType(typed: Map<String,*>, dataType: String) : Map<String,*> {
  return replaceKeyValue(typed, "datatype", dataType)
}
fun replaceMultiple(typed: Map<String,*>, isMultiple: Boolean) : Map<String,*> {
  return replaceKeyValue(typed, "multiple", isMultiple)
}
fun replaceKeyValue(originalMap: Map<String,*>, key: String, value: Any) : Map<String,*> {
  return originalMap.mapValues{entry ->
    if (entry.key === key) value else entry.value
  }
}
fun uncastableAtomicType(testdefs: ObjectNode, dataType: String) : String {
  return if (testdefs.withArray(dataType)[0].isBoolean) "double" else "boolean"
}
fun persistServerdef(docMgr: JSONDocumentManager, modMgr: TextDocumentManager, endpointBase: String,
      funcName: String, docMeta: DocumentMetadataHandle, funcdef: String, funcParams: List<Map<String,*>>?,
      isSJS: Boolean) {
  val docIdBase = endpointBase+funcName
  val apiId     = docIdBase+".api"
  val moduleId  = makeModulePath(docIdBase, isSJS)
  val apiHandle = StringHandle(funcdef)
  val moduleDoc = makeModuleDoc(docIdBase, funcParams, isSJS)
  docMgr.write(apiId, docMeta, apiHandle)
  modMgr.write(
    modMgr.newWriteSet().add(apiId, docMeta, apiHandle).add(moduleId, docMeta, StringHandle(moduleDoc))
    )
}
fun generateClientdef(jsonPath: String, endpointBase: String, funcName: String,
                      funcdef: String, funcParams: List<Map<String,*>>?, isSJS: Boolean) {
  val funcPathBase     = jsonPath+funcName
  val funcClientJSON   = funcPathBase+".api"
  val moduleClientPath = makeModulePath(funcPathBase, isSJS)
  val moduleClientDoc  = makeModuleDoc(endpointBase+funcName, funcParams, isSJS)
  File(funcClientJSON).writeText(funcdef, Charsets.UTF_8)
  File(moduleClientPath).writeText(moduleClientDoc, Charsets.UTF_8)
}
fun makeModulePath(basePath: String, isSJS: Boolean) : String {
  val modulePath =
      if (isSJS) basePath+".sjs"
      else       basePath+".xqy"
  return modulePath;
}
fun makeModuleDoc(docIdBase: String, funcParams: List<Map<String,*>>?, isSJS: Boolean) : String {
  val paramNames = funcParams?.map{param -> param["name"] as String} ?: emptyList<String>()
  val moduleDoc  =
      if (isSJS) """'use strict';
${paramNames.map{paramName -> """var ${paramName};
"""}.joinToString("")}
const inspector = require('/dbf/test/testInspector.sjs');
const errorList = [];
const funcdef   = inspector.getFunctiondef('${docIdBase}', errorList);
let fields = {};
${paramNames.map{paramName -> """fields = inspector.addField(
  '${docIdBase}', fields, '${paramName}', ${paramName}, xdmp.getRequestField('${paramName}', null)
  );
"""}.joinToString("")}
fields = inspector.getFields(funcdef, fields, errorList);
inspector.makeResult('${docIdBase}', funcdef, fields, errorList);
"""
      else       """xquery version "1.0-ml";
declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";
${paramNames.map{paramName -> """declare variable ${'$'}${paramName} external := ();
"""}.joinToString("")}
let ${'$'}errorList := json:array()
let ${'$'}funcdef   := xdmp:apply(xdmp:function(xs:QName("getFunctiondef"), "/dbf/test/testInspector.sjs"),
    "${docIdBase}", ${'$'}errorList
    )
let ${'$'}fields   := map:map()
${paramNames.map{paramName -> """let ${'$'}fields   := xdmp:apply(xdmp:function(xs:QName("addField"), "/dbf/test/testInspector.sjs"),
    "${docIdBase}", ${'$'}fields, "${paramName}", ${'$'}${paramName}, xdmp:get-request-field("${paramName}")
    )
"""}.joinToString("")}
let ${'$'}fields   := xdmp:apply(xdmp:function(xs:QName("getFields"), "/dbf/test/testInspector.sjs"),
    ${'$'}funcdef, ${'$'}fields, ${'$'}errorList
    )
return xdmp:apply(xdmp:function(xs:QName("makeResult"), "/dbf/test/testInspector.sjs"),
    "${docIdBase}", ${'$'}funcdef, ${'$'}fields, ${'$'}errorList
    )
"""
  return moduleDoc;
}
fun serializeArg(paramTest: JsonNode, mappedType: String, testConstructor: String?) : String {
  val text   = paramTest.asText()
  val argRaw =
      when(mappedType) {
      "String"      -> "\"" + text + "\""
      "Float"       -> text+"F"
      "float"       -> text+"F"
      "Long"        -> text+"L"
      "long"        -> text+"L"
      "InputStream" -> """new ByteArrayInputStream(DatatypeConverter.parseBase64Binary("${text}"))"""
      "Reader"      -> """new StringReader("${text}")"""
      else          ->
// TODO: binary document mapping
        if (mappedType.contains('.')) "\"" + text + "\""
        else text
      }
  val arg    =
      if (testConstructor === null) argRaw
      else """${testConstructor}(${argRaw})"""
  return arg
}
fun writeJUnitRequestTest(testingFilename: String, testingSrc: String) {
  val testingFile     = File(testingFilename)
  testingFile.writeText(testingSrc)
}
fun generateJUnitTest(
    testedClass: String, testingClass: String, funcTests: List<String>,
    extraImports: String = "", extraMembers: String = ""
): String {
  val testingSrc = """package ${TEST_PACKAGE};

// IMPORTANT: Do not edit. This file is generated.

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Reader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.Array;

${extraImports}

import org.junit.Test;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.marklogic.client.test.dbfunction.DBFunctionTestUtil;

public class ${testingClass} {
   ${testedClass} testObj = ${testedClass}.on(DBFunctionTestUtil.db);

   void assertBytesStreamEqual(String testName, String[] expectedVals, Stream<byte[]> actual) throws IOException {
      byte[][] actualVals = actual.toArray(size -> new byte[size][]);
      assertEquals(testName, expectedVals.length, actualVals.length);
      for (int i=0; i < expectedVals.length; i++) {
         assertBytesEqual(testName, expectedVals[i], actualVals[i]);
      }
   }
   void assertBytesEqual(String testName, String[] expectedVals, Stream<InputStream> actual) throws IOException {
      InputStream[] actualVals = actual.toArray(size -> new InputStream[size]);
      assertEquals(testName, expectedVals.length, actualVals.length);
      for (int i=0; i < expectedVals.length; i++) {
         assertBytesEqual(testName, expectedVals[i], actualVals[i]);
      }
   }
   void assertBytesEqual(String testName, String expectedVal, InputStream actualVal) throws IOException {
      assertTrue(testName, (actualVal != null));
      byte[] expected = DatatypeConverter.parseBase64Binary(expectedVal);
      byte[] actual   = new byte[expected.length];
      int actualLen = actualVal.read(actual);
      boolean noExtraLen = (actualVal.read() == -1);
      actualVal.close();
      assertTrue(testName, noExtraLen);
      assertBytesEqual(testName, expected, actual);
   }
   void assertBytesEqual(String testName, String expectedVal, byte[] actual) {
      assertBytesEqual(testName, DatatypeConverter.parseBase64Binary(expectedVal), actual);
   }
   void assertBytesEqual(String testName, byte[] expected, byte[] actual) {
      assertTrue(testName, (actual != null && actual.length != 0));
      assertEquals(testName, expected.length, actual.length);
      assertArrayEquals(testName, expected, actual);
   }

   void assertEqual(String testName, String[] expectedVals, Stream<?> actual) {
      assertStringEqual(testName, expectedVals, actual.map(e -> e.toString()));
   }
   void assertStringEqual(String testName, String[] expectedVals, Stream<String> actual) {
      String[] actualVals = actual.toArray(size -> new String[size]);
      assertEquals(testName, expectedVals.length, actualVals.length);
      for (int i=0; i < expectedVals.length; i++) {
         assertStringEqual(testName, expectedVals[i], actualVals[i]);
      }
   }
   void assertStringEqual(String testName, String expected, String actual) {
      assertCharsEqual(testName,
         expected.replaceAll(", ",",").toCharArray(),
         actual.trim().replaceAll(", ",",").toCharArray()
         );
   }

   void assertCharsEqual(String testName, String[] expectedVals, Stream<Reader> actual) throws IOException {
      Reader[] actualVals = actual.toArray(size -> new Reader[size]);
      assertEquals(testName, expectedVals.length, actualVals.length);
      for (int i=0; i < expectedVals.length; i++) {
         assertCharsEqual(testName, expectedVals[i], actualVals[i]);
      }
   }
   void assertCharsEqual(String testName, String expectedVal, Reader actualVal) throws IOException {
      assertTrue(testName, (actualVal != null));
      char[] expected = expectedVal.toCharArray();
      char[] actual   = new char[expected.length];
      actualVal.read(actual);
      actualVal.close();
      assertCharsEqual(testName, expected, actual);
   }
   void assertCharsEqual(String testName, String expected, char[] actual) {
      assertCharsEqual(testName, expected.toCharArray(), actual);
   }
   void assertCharsEqual(String testName, char[] expected, char[] actual) {
      assertTrue(testName, (actual != null && actual.length != 0));
      assertEquals(testName, expected.length, actual.length);
      assertArrayEquals(testName, expected, actual);
   }

   private <T> void assertStreamEquals(String msg, Stream<T> expected, Stream<T> actual, Class<T> as) {
      assertArrayEquals(msg,
         expected.toArray(size -> (T[]) Array.newInstance(as, size)),
         actual.toArray(size   -> (T[]) Array.newInstance(as, size))
         );
   }

${extraMembers}

${funcTests.joinToString("\n")}
}
"""
  return testingSrc
}
fun generateJUnitCallTest(
    funcName: String, funcParams: List<Map<String,*>>?, funcReturn: Map<String,*>?,
    typeTests: ObjectNode, testVariant: TestVariant = TestVariant.VALUE,
    expectError: Boolean = false, typeConstructors: Map<String,String>? = null,
    mappedTestdefs: ObjectNode? = null
): String {
// TODO: treat NULL and EMPTY differently
  val testType         =
      when(testVariant) {
      TestVariant.NULL  -> "NulledTest"
      TestVariant.EMPTY -> "EmptyTest"
      else              -> "Test"
      }
  val testName         = funcName+testType

  val testingArgs      = funcParams?.map{ funcParam ->
    testVal(typeTests, funcParam, testVariant, typeConstructors = typeConstructors, mappedTestdefs = mappedTestdefs)
    }?.joinToString(", ") ?: ""

  val returnType        = funcReturn?.get("datatype")    as String?
  val returnMapping     = funcReturn?.get("\$javaClass") as String?
  val returnKind        = getDataKind(returnType, funcReturn)
  val returnMultiple    = funcReturn?.get("multiple") == true
  val returnNullable    = funcReturn?.get("nullable") == true
  val returnMappedType  =
      if (returnType === null || returnKind === null) null
      else generator.getJavaDataType(returnType, returnMapping, returnKind, returnMultiple)
  val returnCustom      = returnMapping !== null && returnMappedType?.contains('.') == true
  val returnConstructor =
      if (typeConstructors === null || !returnCustom) null
      else typeConstructors[returnType]?.substringAfter(returnType+"As")
  val returnAssign      =
      if (returnMappedType === null) ""
      else if (!returnMultiple)      returnMappedType+" return1 = "
      else                           "Stream<"+returnMappedType+"> return1 = "
  val asserter         =
      if (expectError || testVariant != TestVariant.VALUE || returnMappedType === null || returnKind === "document")
        null
      else
        "assertEquals"
  val assertExpected   =
      if (testVariant != TestVariant.VALUE || returnType === null || returnMappedType === null || returnKind === null)
        null
      else if (returnKind === "atomic")
        testVal(
          typeTests, returnType, returnNullable, returnMultiple, returnMappedType, testVariant,
            typeConstructors = typeConstructors, mappedTestdefs = mappedTestdefs
          )
      else if (returnMultiple)
        typeTests.withArray(returnType).map{testVal -> "\""+testVal.asText()+"\""}.joinToString(",")
      else
        "\""+typeTests.withArray(returnType)[0].asText()+"\""
  val assertActual     =
      if (asserter === null)
        ""
      else if (returnType == "double")
        "return1, 0.1);"
      else if (returnType == "float")
        "return1, 0.1F);"
      else if (returnKind === "atomic" && returnMultiple && returnConstructor != null)
        """return1, ${returnConstructor.substringAfter("as")}.class);"""
      else
        "return1);"
  val assertion        =
      if (expectError)
        """fail("no error for negative test");"""
      else if (returnMappedType === null)
        ""
      else if (testVariant != TestVariant.VALUE)
        if (!returnMultiple)              """assertNull("${testName}", return1);"""
        else if (returnKind === "atomic") """assertEquals("${testName}", 0, return1.length);"""
        else                              """assertEquals("${testName}", 0, return1.count());"""
// TODO: factor out similar to parameters
      else if (returnKind === "document")
        if (returnType === "binaryDocument")
          if (returnMultiple)
            if (returnConstructor !== null)
              """assertBytesStreamEqual("${testName}", new String[]{${assertExpected}}, ${returnConstructor}AsBytes(return1));"""
            else
              """assertBytesEqual("${testName}", new String[]{${assertExpected}}, return1);"""
          else
            if (returnCustom)
              """assertBytesEqual("${testName}", ${assertExpected}, asBytes(return1));"""
            else
              """assertBytesEqual("${testName}", ${assertExpected}, return1);"""
        else
          if (returnMultiple)
            if (returnConstructor !== null)
              """assertStringEqual("${testName}", new String[]{${assertExpected}}, ${returnConstructor}AsString(return1));"""
            else
              """assertCharsEqual("${testName}", new String[]{${assertExpected}}, return1);"""
          else
            if (returnCustom)
              """assertStringEqual("${testName}", ${assertExpected}, asString(return1));"""
            else
              """assertCharsEqual("${testName}", ${assertExpected}, return1);"""
      else if (returnMultiple)
        if (returnConstructor !== null)
          """assertStreamEquals("${testName}", ${assertExpected}, ${assertActual}"""
        else
          """assertStringEqual("${testName}", new String[]{${assertExpected}}, ${assertActual}"""
      else if (returnNullable && generator.getPrimitiveDataTypes().containsKey(returnType))
        """${asserter}("${testName}", ${returnMappedType}.valueOf(${assertExpected}), ${assertActual}"""
      else if (returnType == "double" || returnType == "float")
        """${asserter}("${testName}", ${assertExpected}, ${assertActual}"""
      else
        """${asserter}("${testName}", (Object) ${assertExpected}, ${assertActual}"""

// TODO: assert read() = -1 and close()

  val funcTest         = """
   @Test
   public void ${testName}() {
      try {
         ${returnAssign}testObj.${funcName}(
             ${testingArgs}
             );
         ${assertion}
      } catch(Exception e) {
         ${
           if (expectError) ""
           else """fail(e.getClass().getSimpleName()+": "+e.getMessage());"""
           }
      }
   }
"""
  return funcTest
}
fun testVal(
    typeTests: ObjectNode?, typedef: Map<String,*>, testVariant: TestVariant,
    typeConstructors: Map<String,String>? = null, mappedTestdefs: ObjectNode? = null
) : String {
  val dataType   = typedef.get("datatype") as String
  val mapping    = typedef.get("\$javaClass") as String?
  val dataKind   = getDataKind(dataType, typedef) as String
  val isMultiple = typedef.get("multiple") == true
  val isNullable = typedef.get("nullable") == true
  val mappedType = generator.getJavaDataType(dataType, mapping, dataKind, isMultiple)
  return testVal(
    typeTests, dataType, isNullable, isMultiple, mappedType, testVariant, typeConstructors, mappedTestdefs
    )
}
fun testVal(
    typeTests: ObjectNode?, dataType: String, isNullable: Boolean, isMultiple: Boolean,
    mappedType: String, testVariant: TestVariant,
    typeConstructors: Map<String,String>? = null, mappedTestdefs: ObjectNode? = null
) : String {
  val mappedTestVals  = mappedTestdefs?.withArray(mappedType)
  val testValues      =
      if (mappedTestVals !== null && mappedTestVals.size() > 0) mappedTestVals
      else                                                      typeTests?.withArray(dataType)
  val testConstructor =
      if (typeConstructors === null) null
      else typeConstructors[dataType]
  val testValue       =
      if (testValues === null || testValues.size() == 0 || (isNullable && testVariant === TestVariant.NULL)) {
        "null"
      } else if (isNullable && isMultiple && testVariant === TestVariant.EMPTY) {
        """Stream.empty()"""
      } else if (isMultiple) {
        """Stream.of(${testValues.map{ testValue ->
          serializeArg(testValue, mappedType, testConstructor)
        }.joinToString(", ")})"""
      } else {
        serializeArg(testValues[0], mappedType, testConstructor)
      }
  return testValue
}
fun getDataKind(dataType: String?, typedef: Map<String,*>?) : String? {
  val dataKind = typedef?.get("dataKind")
  if (dataKind !== null)
    return dataKind as String
  else if (dataType === null)
    return null
  else if (dataType.endsWith("Document") || dataType == "array" || dataType == "object")
    return "document"
  else
    return "atomic"
}
