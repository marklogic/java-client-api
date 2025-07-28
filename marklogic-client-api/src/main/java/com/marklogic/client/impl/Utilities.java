/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class Utilities {
  private static DatatypeFactory datatypeFactory;
  private static final int BUFFER_SIZE = 8192;

  public static List<XMLEvent> importFromHandle(AbstractWriteHandle writeHandle) {
    if (writeHandle == null) {
      return null;
    }

    @SuppressWarnings("rawtypes")
    HandleImplementation baseHandle = HandleAccessor.checkHandle(writeHandle,
      "import");

    return objectToEvents(baseHandle.sendContent());
  }
  static public List<XMLEvent> objectToEvents(Object content) {
    if (content == null) {
      return null;
    } else if (content instanceof byte[]) {
      return bytesToEvents((byte[]) content);
    } else if (content instanceof File) {
      return fileToEvents((File) content);
    } else if (content instanceof InputStream) {
      return inputStreamToEvents((InputStream) content);
    } else if (content instanceof Reader) {
      return readerToEvents((Reader) content);
    } else if (content instanceof String) {
      return stringToEvents((String) content);
    } else if (content instanceof OutputStreamSender) {
      return outputSenderToEvents((OutputStreamSender) content);
    } else {
      throw new IllegalArgumentException(
        "Unrecognized class for import: "+content.getClass().getName()
      );
    }
  }
  static public List<XMLEvent> bytesToEvents(byte[] bytes) {
    return readerToEvents(readBytes(bytes));
  }
  static public List<XMLEvent> fileToEvents(File file) {
    return readerToEvents(readFile(file));
  }
  static public List<XMLEvent> inputStreamToEvents(InputStream stream) {
    return readerToEvents(readInputStream(stream));
  }
  static public List<XMLEvent> outputSenderToEvents(OutputStreamSender sender) {
    return readerToEvents(readOutputSender(sender));
  }
  static public List<XMLEvent> readerToEvents(Reader reader) {
    return readerToEvents(readReader(reader));
  }
  static public List<XMLEvent> stringToEvents(String string) {
    return readerToEvents(readString(string));
  }
  static XMLEventReader readBytes(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return null;
    }
    return readInputStream(new ByteArrayInputStream(bytes));
  }
  static XMLEventReader readFile(File file) {
    try {
      if (file == null) {
        return null;
      }
      return readInputStream(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      throw new MarkLogicIOException(e);
    }
  }
  static XMLEventReader readInputStream(InputStream stream) {
    try {
      if (stream == null) {
        return null;
      }
      return makeInputFactory().createXMLEventReader(stream);
    } catch (XMLStreamException e) {
      throw new MarkLogicIOException(e);
    }
  }
  static XMLEventReader readOutputSender(OutputStreamSender sender) {
    try {
      if (sender == null) {
        return null;
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      sender.write(baos);
      return readBytes(baos.toByteArray());
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }
  static XMLEventReader readReader(Reader reader) {
    try {
      if (reader == null) {
        return null;
      }
      return makeInputFactory().createXMLEventReader(reader);
    } catch (XMLStreamException e) {
      throw new MarkLogicIOException(e);
    }
  }
  static XMLEventReader readString(String string) {
    if (string == null) {
      return null;
    }
    return readReader(new StringReader(string));
  }
  static XMLInputFactory makeInputFactory() {
    XMLInputFactory factory = XmlFactories.makeNewInputFactory();
    factory.setProperty("javax.xml.stream.isNamespaceAware", true);
    factory.setProperty("javax.xml.stream.isValidating",     false);

    return factory;
  }
  static public List<XMLEvent> readerToEvents(XMLEventReader reader) {
    try {
      if (reader == null) {
        return null;
      }

      List<XMLEvent> events = new ArrayList<>();
      while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();
        switch (event.getEventType()) {
          case XMLEvent.START_DOCUMENT:
          case XMLEvent.END_DOCUMENT:
            // skip prolog and end
            break;
          default:
            events.add(event);
            break;
        }
      }

      if (events.isEmpty()) {
        return null;
      }
      return events;
    } catch (XMLStreamException e) {
      throw new MarkLogicIOException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends AbstractReadHandle> T exportToHandle(
    List<XMLEvent> events, T handle
  ) {
    if (handle == null) {
      return null;
    }

    @SuppressWarnings("rawtypes")
    HandleImplementation baseHandle = HandleAccessor.checkHandle(handle,
      "export");

    baseHandle.receiveContent(
      eventsToObject(events, baseHandle.receiveAs())
    );

    return handle;
  }
  public static Object eventsToObject(List<XMLEvent> events, Class<?> as) {
    if (events == null || events.isEmpty()) {
      return null;
    }

    if (byte[].class.isAssignableFrom(as)) {
      return eventsToBytes(events);
    } else if (File.class.isAssignableFrom(as)) {
      return eventsToFile(events, ".xml");
    } else if (InputStream.class.isAssignableFrom(as)) {
      return eventsToInputStream(events);
    } else if (Reader.class.isAssignableFrom(as)) {
      return eventsToReader(events);
    } else if (String.class.isAssignableFrom(as)) {
      return eventsToString(events);
    } else {
      throw new IllegalArgumentException(
        "Unrecognized class for export: "+as.getName()
      );
    }
  }
  public static byte[] eventsToBytes(List<XMLEvent> events) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    if (!writeEvents(events, baos)) {
      return null;
    }

    return baos.toByteArray();
  }
  public static File eventsToFile(List<XMLEvent> events, String extension) {
    try {
      File tempFile = File.createTempFile("tmp", extension);
      if (!writeEvents(events, new FileOutputStream(tempFile))) {
        if (tempFile.exists()) {
          tempFile.delete();
        }

        return null;
      }

      return tempFile;
    } catch (IOException e) {
      throw new MarkLogicIOException(e);
    }
  }
  public static InputStream eventsToInputStream(List<XMLEvent> events) {
    byte[] bytes = eventsToBytes(events);
    if (bytes == null || bytes.length == 0) {
      return null;
    }

    return new ByteArrayInputStream(bytes);
  }
  public static Reader eventsToReader(List<XMLEvent> events) {
    String string = eventsToString(events);
    if (string == null) {
      return null;
    }

    return new StringReader(string);
  }
  public static String eventsToString(List<XMLEvent> events) {
    byte[] bytes = eventsToBytes(events);
    if (bytes == null || bytes.length == 0) {
      return null;
    }

    return new String(bytes, StandardCharsets.UTF_8);
  }
  public static boolean writeEvents(List<XMLEvent> events, OutputStream out) {
    if (events == null || events.isEmpty()) {
      return false;
    }

    try {
      XMLOutputFactory factory = XmlFactories.getOutputFactory();

      XMLEventWriter eventWriter = factory.createXMLEventWriter(out, "UTF-8");

      for (XMLEvent event: events) {
        eventWriter.add(event);
      }

      eventWriter.flush();
      eventWriter.close();

      return true;
    } catch (XMLStreamException e) {
      throw new MarkLogicIOException(e);
    }
  }

  public static String eventTextToString(List<XMLEvent> events) {
    if (events == null || events.isEmpty()) {
      return null;
    }

    StringBuilder buf = new StringBuilder();
    for (XMLEvent event: events) {
      if (event.isCharacters()) {
        buf.append(event.asCharacters().getData());
      }
    }

    return buf.toString();
  }

	@SuppressWarnings("unchecked")
  static public void setHandleContent(ContentHandle handle, Object content) {
    if (handle == null) {
      return;
    }

    handle.set(content);
  }
  @SuppressWarnings("rawtypes")
  static public void setHandleStructuredFormat(ContentHandle handle, Format format) {
    if (handle == null || format == null) {
      return;
    }

    if (BaseHandle.class.isAssignableFrom(handle.getClass())) {
      setHandleStructuredFormat((BaseHandle) handle, format);
    }
  }
  static private void setHandleStructuredFormat(BaseHandle handle, Format format) {
    if (format != Format.JSON && format != Format.XML) {
      throw new IllegalArgumentException("Received "+format.name()+" format instead of JSON or XML");
    }

    handle.setFormat(format);
  }

  static public DatatypeFactory getDatatypeFactory() {
    if (datatypeFactory == null) {
      try {
        datatypeFactory = DatatypeFactory.newInstance();
      } catch (DatatypeConfigurationException e) {
        throw new MarkLogicInternalException(e);
      }
    }
    return datatypeFactory;
  }

  /**
   * Writes bytes from the input stream to the output stream.
   * @param in - the input stream passed in.
   * @param outStream - output stream where the bytes are written.
   */
  static public void write(InputStream in, OutputStream outStream) throws IOException {
      if(in == null || outStream == null)
          return;
      try {
          byte[] byteArray = new byte[BUFFER_SIZE * 2];

          int byteCount;
          while ((byteCount = in.read(byteArray)) != -1) {
              outStream.write(byteArray, 0, byteCount);
          }
          outStream.flush();
      } finally {
          in.close();
      }
  }

  /**
   * Writes bytes from the input Reader to the Writer stream.
   * @param in - the Reader passed in.
   * @param out - Writer stream where the bytes are written.
   */
  static public void write(Reader in, Writer out) throws IOException {
      if(in == null || out == null)
          return;
      try {
          char[] charArray = new char[BUFFER_SIZE * 2];
          int charCount;
          while ((charCount = in.read(charArray)) != -1) {
              out.write(charArray, 0, charCount);
          }
          out.flush();
      } finally {
          in.close();
      }
  }

  /**
   * Writes bytes from the input Reader to the output stream.
   * @param in - the Reader passed in.
   * @param out - OutputStream where the bytes are written.
   */
  static public void write(Reader in, OutputStream out) throws IOException {
      write(in, new OutputStreamWriter(out));
  }

  static String escapeMultipartParamAssignment(CharsetEncoder asciiEncoder, String value) {
    if (value == null) return null;
    String assignment;
    if (asciiEncoder.canEncode(value)) {
      // escape any quotes or back-slashes
      assignment = "=\"" + value.replace("\"", "\\\"").replace("\\", "\\\\") + "\"";
    } else {
      try {
        assignment = "*=UTF-8''" + URLEncoder.encode(value, "UTF-8");
      } catch (Throwable ex) {
        throw new IllegalArgumentException("Uri cannot be encoded as UFT-8: "+value, ex);
      }
    }
    asciiEncoder.reset();
    return assignment;
  }

  public static double parseDouble(String value) {
    return parseDouble(value, -1);
  }
  public static double parseDouble(String value, double defaultValue) {
    return (value == null || value.isEmpty()) ? defaultValue : Double.parseDouble(value);
  }
  public static int parseInt(String value) {
    return parseInt(value, -1);
  }
  public static int parseInt(String value, int defaultValue) {
    return (value == null || value.isEmpty()) ? defaultValue : Integer.parseInt(value);
  }
  public static long parseLong(String value) {
    return parseLong(value, -1L);
  }
  public static long parseLong(String value, long defaultValue) {
    return (value == null || value.isEmpty()) ? defaultValue : Long.parseLong(value);
  }

	@SuppressWarnings("unchecked")
  public static void setHandleToString(AbstractReadHandle handle, String content) {
    if (!(handle instanceof BaseHandle)) {
      throw new IllegalArgumentException("cannot export with handle that doesn't extend base");
    }
    @SuppressWarnings("rawtypes")
    BaseHandle baseHandle = (BaseHandle) handle;
    @SuppressWarnings("rawtypes")
    Class as = baseHandle.receiveAs();
    if (InputStream.class.isAssignableFrom(as)) {
      baseHandle.receiveContent(new ByteArrayInputStream(content.getBytes()));
    } else if (Reader.class.isAssignableFrom(as)) {
      baseHandle.receiveContent(new StringReader(content));
    } else if (byte[].class.isAssignableFrom(as)) {
      baseHandle.receiveContent(content.getBytes());
    } else if (String.class.isAssignableFrom(as)) {
      baseHandle.receiveContent(content);
    } else {
      throw new IllegalArgumentException("cannot export with handle that doesn't accept content as byte[], input stream, reader, or string");
    }
  }
}
