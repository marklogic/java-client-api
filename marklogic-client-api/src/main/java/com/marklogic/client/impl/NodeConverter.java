/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.MarkLogicIOException;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;
import java.util.stream.Stream;

public class NodeConverter {
   static private ObjectMapper           mapper;
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

   static private XMLInputFactory getXMLInputFactory() {
      // okay if one thread overwrites another during lazy initialization
      if (xmlInputFactory == null) {
         xmlInputFactory = XmlFactories.makeNewInputFactory();
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
	@SuppressWarnings("unchecked")
	static public <T extends AbstractWriteHandle> Stream<T> streamWithFormat(Stream<T> handles, Format format) {
      if (handles == null || format == null) {
         return handles;
      }
      final Formatter<T> formatter = new Formatter(format);
      return handles.map(formatter);
   }
   static public <T> T[] arrayWithFormat(T[] handles, Format format) {
       if (handles == null) return handles;
       for (T handle: handles) {
          withFormat(handle, format);
       }
       return handles;
    }
   static private class Formatter<T extends AbstractWriteHandle> implements Function<T,T> {
      private final Format format;
      Formatter(Format format) {
         this.format = format;
      }
      @Override
      public T apply(T handle) {
         return NodeConverter.withFormat(handle, format);
      }
   }

   static public BytesHandle BytesToHandle(byte[] value) {
      return (value == null) ? null : new BytesHandle(value);
   }
   static public Stream<BytesHandle> BytesToHandle(Stream<? extends byte[]> values) {
      return (values == null) ? null : values.map(NodeConverter::BytesToHandle);
   }
   static public BytesHandle[] BytesToHandle(byte[][] values) {
      return (values == null) ? null : convert(values, new BytesHandle[values.length], NodeConverter::BytesToHandle);
   }
   static public DOMHandle DocumentToHandle(Document value) {
      return (value == null) ? null : new DOMHandle(value);
   }
   static public Stream<DOMHandle> DocumentToHandle(Stream<? extends Document> values) {
      return (values == null) ? null : values.map(NodeConverter::DocumentToHandle);
   }
   static public DOMHandle[] DocumentToHandle(Document[] values) {
      return (values == null) ? null : convert(values, new DOMHandle[values.length], NodeConverter::DocumentToHandle);
   }
   static public FileHandle FileToHandle(File value) {
      return (value == null) ? null : new FileHandle(value);
   }
   static public Stream<FileHandle> FileToHandle(Stream<? extends File> values) {
      return (values == null) ? null : values.map(NodeConverter::FileToHandle);
   }
   static public FileHandle[] FileToHandle(File[] values) {
      return (values == null) ? null : convert(values, new FileHandle[values.length], NodeConverter::FileToHandle);
   }
   static public InputStreamHandle InputStreamToHandle(InputStream value) {
      return (value == null) ? null : new InputStreamHandle(value);
   }
   static public Stream<InputStreamHandle> InputStreamToHandle(Stream<? extends InputStream> values) {
      return (values == null) ? null : values.map(NodeConverter::InputStreamToHandle);
   }
   static public InputStreamHandle[] InputStreamToHandle(InputStream[] values) {
      return (values == null) ? null : convert(values, new InputStreamHandle[values.length], NodeConverter::InputStreamToHandle);
   }
   static public InputSourceHandle InputSourceToHandle(InputSource value) {
      return (value == null) ? null : new InputSourceHandle(value);
   }
   static public Stream<InputSourceHandle> InputSourceToHandle(Stream<? extends InputSource> values) {
      return (values == null) ? null : values.map(NodeConverter::InputSourceToHandle);
   }
   static public InputSourceHandle[] InputSourceToHandle(InputSource[] values) {
      return (values == null) ? null : convert(values, new InputSourceHandle[values.length], NodeConverter::InputSourceToHandle);
   }
   static public JacksonHandle JsonNodeToHandle(JsonNode value) {
      return (value == null) ? null : new JacksonHandle(value);
   }
   static public Stream<JacksonHandle> JsonNodeToHandle(Stream<? extends JsonNode> values) {
      return (values == null) ? null : values.map(NodeConverter::JsonNodeToHandle);
   }
   static public JacksonHandle[] JsonNodeToHandle(JsonNode[] values) {
      return (values == null) ? null : convert(values, new JacksonHandle[values.length], NodeConverter::JsonNodeToHandle);
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
   static public JacksonParserHandle[] JsonParserToHandle(JsonParser[] values) {
      return (values == null) ? null : convert(values, new JacksonParserHandle[values.length], NodeConverter::JsonParserToHandle);
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
         return (inputStream == null) ? null : XmlFactories.getDocumentBuilderFactory().newDocumentBuilder().parse(inputStream);
      } catch(SAXException | IOException | ParserConfigurationException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<Document> InputStreamToDocument(Stream<? extends InputStream> values) {
      return (values == null) ? null : values.map(NodeConverter::InputStreamToDocument);
   }
   static public File InputStreamToFile(InputStream inputStream) {
      if (inputStream == null) {
         return null;
      }
      try {
         Path tempFile = Files.createTempFile("tmp", null);
         Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
         return tempFile.toFile();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
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
      } catch(XMLStreamException | FactoryConfigurationError e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<XMLEventReader> ReaderToXMLEventReader(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToXMLEventReader);
   }
   static public XMLStreamReader ReaderToXMLStreamReader(Reader reader) {
      try {
         return (reader == null) ? null : getXMLInputFactory().createXMLStreamReader(reader);
      } catch(XMLStreamException | FactoryConfigurationError e) {
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
   static public OutputStreamHandle[] OutputStreamSenderToHandle(OutputStreamSender[] values) {
       return (values == null) ? null : convert(values, new OutputStreamHandle[values.length], NodeConverter::OutputStreamSenderToHandle);
   }
   static public ReaderHandle ReaderToHandle(Reader value) {
      return (value == null) ? null : new ReaderHandle(value);
   }
   static public Stream<ReaderHandle> ReaderToHandle(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToHandle);
   }
   static public ReaderHandle[] ReaderToHandle(Reader[] values) {
      return (values == null) ? null : convert(values, new ReaderHandle[values.length], NodeConverter::ReaderToHandle);
   }
   static public StringHandle StringToHandle(String value) {
      return (value == null) ? null : new StringHandle(value);
   }
   static public Stream<StringHandle> StringToHandle(Stream<? extends String> values) {
      return (values == null) ? null : values.map(NodeConverter::StringToHandle);
   }
   static public StringHandle[] StringToHandle(String[] values) {
      return (values == null) ? null : convert(values, new StringHandle[values.length], NodeConverter::StringToHandle);
   }
   static public SourceHandle SourceToHandle(Source value) {
      return (value == null) ? null : new SourceHandle(value);
   }
   static public Stream<SourceHandle> SourceToHandle(Stream<? extends Source> values) {
      return (values == null) ? null : values.map(NodeConverter::SourceToHandle);
   }
   static public SourceHandle[] SourceToHandle(Source[] values) {
      return (values == null) ? null : convert(values, new SourceHandle[values.length], NodeConverter::SourceToHandle);
   }
   static public XMLEventReaderHandle XMLEventReaderToHandle(XMLEventReader value) {
      return (value == null) ? null : new XMLEventReaderHandle(value);
   }
   static public Stream<XMLEventReaderHandle> XMLEventReaderToHandle(Stream<? extends XMLEventReader> values) {
      return (values == null) ? null : values.map(NodeConverter::XMLEventReaderToHandle);
   }
   static public XMLEventReaderHandle[] XMLEventReaderToHandle(XMLEventReader[] values) {
      return (values == null) ? null : convert(values, new XMLEventReaderHandle[values.length], NodeConverter::XMLEventReaderToHandle);
   }
   static public XMLStreamReaderHandle XMLStreamReaderToHandle(XMLStreamReader value) {
      return (value == null) ? null : new XMLStreamReaderHandle(value);
   }
   static public Stream<XMLStreamReaderHandle> XMLStreamReaderToHandle(Stream<? extends XMLStreamReader> values) {
      return (values == null) ? null : values.map(NodeConverter::XMLStreamReaderToHandle);
   }
   static public XMLStreamReaderHandle[] XMLStreamReaderToHandle(XMLStreamReader[] values) {
      return (values == null) ? null : convert(values, new XMLStreamReaderHandle[values.length], NodeConverter::XMLStreamReaderToHandle);
   }

   static public byte[] InputStreamToBytes(InputStream inputStream) {
      try {
         if (inputStream == null) {
            return null;
         }

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         byte[] buf = new byte[8192];
         int byteCount;
         while ((byteCount=inputStream.read(buf)) != -1) {
            out.write(buf, 0, byteCount);
         }
         return out.toByteArray();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Reader InputStreamToReader(InputStream inputStream) {
	 if (inputStream == null) {
		return null;
	 }

	 return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
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
         int charCount;
         while ((charCount=reader.read(buf)) != -1) {
            bldr.append(buf, 0, charCount);
         }
         return bldr.toString();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

	@SuppressWarnings("unchecked")
	static public <T extends JSONReadHandle> T jsonNodeToHandle(JsonNode node, T handle) {
      if (node == null) {
         return null;
      } else if (handle == null) {
         throw new IllegalArgumentException("cannot convert JSON node to null handle");
      }

      try {
         HandleImplementation handleImpl = HandleAccessor.checkHandle(handle, "json");

         Class<?> as = handleImpl.receiveAs();
         if (as == null) {
            throw new IllegalArgumentException("handle does not specify class to receive content");
         } else if (byte[].class.isAssignableFrom(as)) {
            handleImpl.receiveContent(getMapper().writeValueAsBytes(node));
         } else if (File.class.isAssignableFrom(as)) {
            Path tempFile = Files.createTempFile("tmp", ".json");
            Files.copy(new ByteArrayInputStream(getMapper().writeValueAsBytes(node)), tempFile, StandardCopyOption.REPLACE_EXISTING);
            handleImpl.receiveContent(tempFile.toFile());
// TODO: JsonParser.class.isAssignableFrom(as)
         } else if (InputStream.class.isAssignableFrom(as)) {
            handleImpl.receiveContent(new ByteArrayInputStream(getMapper().writeValueAsBytes(node)));
         } else if (Reader.class.isAssignableFrom(as)) {
            handleImpl.receiveContent(new StringReader(getMapper().writeValueAsString(node)));
         } else if (String.class.isAssignableFrom(as)) {
            handleImpl.receiveContent(getMapper().writeValueAsString(node));
         } else {
            throw new IllegalArgumentException("handle receives content with unsupported class: "+as.getSimpleName());
         }

         return handle;
      } catch (JsonProcessingException e) {
         throw new MarkLogicIOException("could not set handle to JsonNode", e);
      } catch (IOException e) {
         throw new MarkLogicIOException("could not create file for JsonNode", e);
      }
   }
   static public JsonNode handleToJsonNode(JSONWriteHandle jsonHandle) {
      if (jsonHandle == null) {
         return null;
      } else if (!(jsonHandle instanceof ContentHandle)) {
         throw new IllegalArgumentException(
             "JSONWriteHandle must implement ContentHandle: "+jsonHandle.getClass().getCanonicalName()
         );
      }
      Object jsonTree = ((ContentHandle<?>) jsonHandle).get();

      if (jsonTree == null || jsonTree instanceof JsonNode) {
         return (JsonNode) jsonTree;
      }

      try {
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
              "Could not convert JSON of class: "+jsonTree.getClass().getCanonicalName()+"\n"+jsonTree
      );
   }

   public static BufferableHandle bufferAsBytes(BufferableHandle handle) {
       return handle == null ? null: new BytesHandle(handle);
   }

   public static BufferableHandle[] bufferAsBytes(BufferableHandle[] handles) {
      if (handles == null || handles.length == 0)
         return null;
      BufferableHandle[] bufferableBytesHandles = new BufferableHandle[handles.length];
      for(int i=0; i < handles.length; i++) {
         bufferableBytesHandles[i] = bufferAsBytes(handles[i]);
      }
      return bufferableBytesHandles;
   }

   static public <I, O> O[] convert(I[] in, O[] out, Function<I, O> converter) {
      for (int i=0; i < in.length; i++) {
         out[i] = converter.apply(in[i]);
      }
      return out;
   }
   static public <I> BufferableHandle[] convert(I[] in, Function<I, BufferableHandle> converter) {
     if (in == null) return null;
     BufferableHandle[] out = new BufferableHandle[in.length];
     for (int i=0; i < in.length; i++) {
       out[i] = converter.apply(in[i]);
     }
     return out;
   }
}
