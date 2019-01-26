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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class NodeConverter {
   static private ObjectMapper           mapper;
   static private DocumentBuilderFactory documentBuilderFactory;
   static private XMLInputFactory        xmlInputFactory;

   private NodeConverter() {
      super();
   }

   static private ObjectMapper getMapper() {
      // okay if one thread overwrites another during lazy initialization
      if (mapper == null) {
         mapper = new ObjectMapper();
      }
      return mapper;
   }
   static private DocumentBuilderFactory getDocumentBuilderFactory() {
      // okay if one thread overwrites another during lazy initialization
      if (documentBuilderFactory == null) {
         documentBuilderFactory = DocumentBuilderFactory.newInstance();
      }
      return documentBuilderFactory;
   }
   static private XMLInputFactory getXMLInputFactory() {
      // okay if one thread overwrites another during lazy initialization
      if (xmlInputFactory == null) {
         xmlInputFactory = XMLInputFactory.newFactory();
      }
      return xmlInputFactory;
   }

   static public BinaryWriteHandle BinaryWriter(BinaryWriteHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.BINARY);
   }
   static public Stream<BinaryWriteHandle> BinaryWriter(Stream<? extends BinaryWriteHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::BinaryWriter);
   }
   static public BinaryReadHandle BinaryReader(BinaryReadHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.BINARY);
   }
   static public Stream<BinaryReadHandle> BinaryReader(Stream<? extends BinaryReadHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::BinaryReader);
   }
   static public JSONWriteHandle JSONWriter(JSONWriteHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.JSON);
   }
   static public Stream<JSONWriteHandle> JSONWriter(Stream<? extends JSONWriteHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::JSONWriter);
   }
   static public JSONReadHandle JSONReader(JSONReadHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.JSON);
   }
   static public Stream<JSONReadHandle> JSONReader(Stream<? extends JSONReadHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::JSONReader);
   }
   static public TextWriteHandle TextWriter(TextWriteHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.TEXT);
   }
   static public Stream<TextWriteHandle> TextWriter(Stream<? extends TextWriteHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::TextWriter);
   }
   static public TextReadHandle TextReader(TextReadHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.TEXT);
   }
   static public Stream<TextReadHandle> TextReader(Stream<? extends TextReadHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::TextReader);
   }
   static public XMLWriteHandle XMLWriter(XMLWriteHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.XML);
   }
   static public Stream<XMLWriteHandle> XMLWriter(Stream<? extends XMLWriteHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::XMLWriter);
   }
   static public XMLReadHandle XMLReader(XMLReadHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.XML);
   }
   static public Stream<XMLReadHandle> XMLReader(Stream<? extends XMLReadHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::XMLReader);
   }
   static public <T> T withFormat(T handle, Format format) {
      if (handle != null) {
         if (!(handle instanceof BaseHandle)) {
            throw new IllegalArgumentException(
                    "cannot set format on handle of "+handle.getClass().getName()
            );
         }
         if (format != null) {
            ((BaseHandle) handle).setFormat(format);
         }
      }
      return handle;
   }
   static public <T extends AbstractWriteHandle> Stream<T> streamWithFormat(Stream<T> handles, Format format) {
      if (handles == null || format == null) {
         return handles;
      }
      final Formatter<T> formatter = new Formatter(format);
      return handles.map(formatter);
   }
   static private class Formatter<T extends AbstractWriteHandle> implements Function<T,T> {
      private Format format;
      Formatter(Format format) {
         this.format = format;
      }
      @Override
      public T apply(T handle) {
         return NodeConverter.withFormat(handle, format);
      }
   }

   static public AbstractWriteHandle ObjectToHandle(Object value, Format format) {
      if (value == null) {
        return null;
      } else if (value instanceof byte[]) {
        return BytesObjectToHandle(value, format);
      } else if (value instanceof File) {
        return FileObjectToHandle(value, format);
      } else if (value instanceof InputStream) {
        return InputStreamObjectToHandle(value, format);
      } else if (value instanceof Reader) {
        return ReaderObjectToHandle(value, format);
      } else if (value instanceof String) {
        return StringObjectToHandle(value, format);
      }
      throw new IllegalArgumentException(
          "unsupported value argument "+value.getClass().getCanonicalName()+
          ", expected byte[], File, InputStream, Reader, or String"
      );
   }
   static private BytesHandle BytesObjectToHandle(Object value, Format format) {
     return BytesToHandle((byte[]) value).withFormat(format);
   }
   static private FileHandle FileObjectToHandle(Object value, Format format) {
     return FileToHandle((File) value).withFormat(format);
   }
   static private InputStreamHandle InputStreamObjectToHandle(Object value, Format format) {
     return InputStreamToHandle((InputStream) value).withFormat(format);
   }
   static private ReaderHandle ReaderObjectToHandle(Object value, Format format) {
     return ReaderToHandle((Reader) value).withFormat(format);
   }
   static private StringHandle StringObjectToHandle(Object value, Format format) {
     return StringToHandle((String) value).withFormat(format);
   }

   static public Stream<? extends AbstractWriteHandle> ObjectToHandle(Stream<?> values, Format format) {
     if (values == null) {
       return null;
     }
     final ObjectHandler handler = new ObjectHandler(format);
     return values.map(handler::toHandle);
   }
   static private class ObjectHandler {
     private Format format;
     private BiFunction<Object, Format, ? extends AbstractWriteHandle> toHandler = null;
     private Class<?> itemType = null;
     ObjectHandler(Format format) {
       this.format = format;
     }
     AbstractWriteHandle toHandle(Object value) {
       if (value == null) {
         return null;
       } else if (toHandler == null || itemType == null) {
         init(value);
       } else if (!itemType.isInstance(value)) {
         throw new IllegalArgumentException(
             "inconsistent stream - expected instance of "+itemType.getSimpleName()+
             " but received "+value.getClass().getCanonicalName()
         );
       }
       return toHandler.apply(value, format);
     }
     private synchronized void init(Object value) {
       if (value instanceof byte[]) {
         toHandler = NodeConverter::BytesObjectToHandle;
         itemType  = byte[].class;
       } else if (value instanceof File) {
         toHandler = NodeConverter::FileObjectToHandle;
         itemType  = File.class;
       } else if (value instanceof InputStream) {
         toHandler = NodeConverter::InputStreamObjectToHandle;
         itemType  = InputStream.class;
       } else if (value instanceof Reader) {
         toHandler = NodeConverter::ReaderObjectToHandle;
         itemType  = Reader.class;
       } else if (value instanceof String) {
         toHandler = NodeConverter::StringObjectToHandle;
         itemType  = String.class;
       } else {
         throw new IllegalArgumentException(
            "unsupported stream item argument "+value.getClass().getCanonicalName()+
            ", expected byte[], File, InputStream, Reader, or String"
         );
       }
     }
   }

   static public BytesHandle BytesToHandle(byte[] value) {
      return (value == null) ? null : new BytesHandle(value);
   }
   static public Stream<BytesHandle> BytesToHandle(Stream<? extends byte[]> values) {
      return (values == null) ? null : values.map(NodeConverter::BytesToHandle);
   }
   static public DOMHandle DocumentToHandle(Document value) {
      return (value == null) ? null : new DOMHandle(value);
   }
   static public Stream<DOMHandle> DocumentToHandle(Stream<? extends Document> values) {
      return (values == null) ? null : values.map(NodeConverter::DocumentToHandle);
   }
   static public FileHandle FileToHandle(File value) {
      return (value == null) ? null : new FileHandle(value);
   }
   static public Stream<FileHandle> FileToHandle(Stream<? extends File> values) {
      return (values == null) ? null : values.map(NodeConverter::FileToHandle);
   }
   static public InputStreamHandle InputStreamToHandle(InputStream value) {
      return (value == null) ? null : new InputStreamHandle(value);
   }
   static public Stream<InputStreamHandle> InputStreamToHandle(Stream<? extends InputStream> values) {
      return (values == null) ? null : values.map(NodeConverter::InputStreamToHandle);
   }
   static public InputSourceHandle InputSourceToHandle(InputSource value) {
      return (value == null) ? null : new InputSourceHandle(value);
   }
   static public Stream<InputSourceHandle> InputSourceToHandle(Stream<? extends InputSource> values) {
      return (values == null) ? null : values.map(NodeConverter::InputSourceToHandle);
   }
   static public JacksonHandle JsonNodeToHandle(JsonNode value) {
      return (value == null) ? null : new JacksonHandle(value);
   }
   static public Stream<JacksonHandle> JsonNodeToHandle(Stream<? extends JsonNode> values) {
      return (values == null) ? null : values.map(NodeConverter::JsonNodeToHandle);
   }
   static public JacksonParserHandle JsonParserToHandle(JsonParser value) {
      if (value == null) {
         return null;
      }
      JacksonParserHandle handle = new JacksonParserHandle();
      handle.set(value);
      return handle;
   }
   static public Stream<JacksonParserHandle> JsonParserToHandle(Stream<? extends JsonParser> values) {
      return (values == null) ? null : values.map(NodeConverter::JsonParserToHandle);
   }
   static public ArrayNode ReaderToArrayNode(Reader value) {
      try {
         return (value == null) ? null : getMapper().readValue(value, ArrayNode.class);
      } catch(IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<ArrayNode> ReaderToArrayNode(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToArrayNode);
   }
   static public JsonNode ReaderToJsonNode(Reader value) {
      try {
         return (value == null) ? null : getMapper().readTree(value);
      } catch(IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<JsonNode> ReaderToJsonNode(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToJsonNode);
   }
   static public ObjectNode ReaderToObjectNode(Reader value) {
      try {
         return (value == null) ? null : getMapper().readValue(value, ObjectNode.class);
      } catch(IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<ObjectNode> ReaderToObjectNode(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToObjectNode);
   }
   static public JsonParser ReaderToJsonParser(Reader value) {
      try {
         return (value == null) ? null : getMapper().getFactory().createParser(value);
      } catch(IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<JsonParser> ReaderToJsonParser(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToJsonParser);
   }

   static public Document InputStreamToDocument(InputStream inputStream) {
      try {
         return (inputStream == null) ? null : getDocumentBuilderFactory().newDocumentBuilder().parse(inputStream);
      } catch(SAXException e) {
         throw new RuntimeException(e);
      } catch(IOException e) {
         throw new RuntimeException(e);
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<Document> InputStreamToDocument(Stream<? extends InputStream> values) {
      return (values == null) ? null : values.map(NodeConverter::InputStreamToDocument);
   }
   static public InputSource ReaderToInputSource(Reader reader) {
      return (reader == null) ? null : new InputSource(reader);
   }
   static public Stream<InputSource> ReaderToInputSource(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToInputSource);
   }
   static public Source ReaderToSource(Reader reader) {
      return (reader == null) ? null : new StreamSource(reader);
   }
   static public Stream<Source> ReaderToSource(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToSource);
   }
   static public XMLEventReader ReaderToXMLEventReader(Reader reader) {
      try {
         return (reader == null) ? null : getXMLInputFactory().createXMLEventReader(reader);
      } catch(XMLStreamException e) {
         throw new RuntimeException(e);
      } catch(FactoryConfigurationError e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<XMLEventReader> ReaderToXMLEventReader(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToXMLEventReader);
   }
   static public XMLStreamReader ReaderToXMLStreamReader(Reader reader) {
      try {
         return (reader == null) ? null : getXMLInputFactory().createXMLStreamReader(reader);
      } catch(XMLStreamException e) {
         throw new RuntimeException(e);
      } catch(FactoryConfigurationError e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<XMLStreamReader> ReaderToXMLStreamReader(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToXMLStreamReader);
   }

   static public OutputStreamHandle OutputStreamSenderToHandle(OutputStreamSender value) {
      return (value == null) ? null : new OutputStreamHandle(value);
   }
   static public Stream<OutputStreamHandle> OutputStreamSenderToHandle(Stream<? extends OutputStreamSender> values) {
      return (values == null) ? null : values.map(NodeConverter::OutputStreamSenderToHandle);
   }
   static public ReaderHandle ReaderToHandle(Reader value) {
      return (value == null) ? null : new ReaderHandle(value);
   }
   static public Stream<ReaderHandle> ReaderToHandle(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToHandle);
   }
   static public StringHandle StringToHandle(String value) {
      return (value == null) ? null : new StringHandle(value);
   }
   static public Stream<StringHandle> StringToHandle(Stream<? extends String> values) {
      return (values == null) ? null : values.map(NodeConverter::StringToHandle);
   }
   static public SourceHandle SourceToHandle(Source value) {
      return (value == null) ? null : new SourceHandle(value);
   }
   static public Stream<SourceHandle> SourceToHandle(Stream<? extends Source> values) {
      return (values == null) ? null : values.map(NodeConverter::SourceToHandle);
   }
   static public XMLEventReaderHandle XMLEventReaderToHandle(XMLEventReader value) {
      return (value == null) ? null : new XMLEventReaderHandle(value);
   }
   static public Stream<XMLEventReaderHandle> XMLEventReaderToHandle(Stream<? extends XMLEventReader> values) {
      return (values == null) ? null : values.map(NodeConverter::XMLEventReaderToHandle);
   }
   static public XMLStreamReaderHandle XMLStreamReaderToHandle(XMLStreamReader value) {
      return (value == null) ? null : new XMLStreamReaderHandle(value);
   }
   static public Stream<XMLStreamReaderHandle> XMLStreamReaderToHandle(Stream<? extends XMLStreamReader> values) {
      return (values == null) ? null : values.map(NodeConverter::XMLStreamReaderToHandle);
   }

   static public byte[] InputStreamToBytes(InputStream inputStream) {
      try {
         if (inputStream == null) {
            return null;
         }

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         byte[] buf = new byte[8192];
         int byteCount = -1;
         while ((byteCount=inputStream.read(buf)) != -1) {
            out.write(buf, 0, byteCount);
         }
         return out.toByteArray();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Reader InputStreamToReader(InputStream inputStream) {
      try {
         if (inputStream == null) {
            return null;
         }

         return new InputStreamReader(inputStream, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }
   }
   static public String InputStreamToString(InputStream inputStream) {
      return ReaderToString(InputStreamToReader(inputStream));
   }
   static public String ReaderToString(Reader reader) {
      try {
         if (reader == null) {
            return null;
         }

         StringBuilder bldr = new StringBuilder();
         char[] buf = new char[8192];
         int charCount = -1;
         while ((charCount=reader.read(buf)) != -1) {
            bldr.append(buf, 0, charCount);
         }
         return bldr.toString();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   static public JsonNode ObjectToJsonNode(Object jsonTree) {
      try {
         if (jsonTree == null || jsonTree instanceof JsonNode) {
            return (JsonNode) jsonTree;
         }

         ObjectMapper mapper = new ObjectMapper();
         if (jsonTree instanceof byte[]) {
            return mapper.readTree((byte[]) jsonTree);
         } else if (jsonTree instanceof File) {
            return mapper.readTree((File) jsonTree);
         } else if (jsonTree instanceof JsonParser) {
            return mapper.readTree((JsonParser) jsonTree);
         } else if (jsonTree instanceof InputStream) {
            return mapper.readTree((InputStream) jsonTree);
         } else if (jsonTree instanceof Reader) {
            return mapper.readTree((Reader) jsonTree);
         } else if (jsonTree instanceof String) {
            return mapper.readTree((String) jsonTree);
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      throw new IllegalArgumentException(
              "Could not convert JSON of class: "+jsonTree.getClass().getCanonicalName()+"\n"+jsonTree.toString()
      );
   }
}
