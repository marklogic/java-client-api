/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputSourceHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.XMLEventReaderHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

/*
TODO: rename BufferableHandleTest
operate on all bufferables
 */
public class DocumentManagerTest {
	public interface WriteSetter<H extends AbstractWriteHandle> {
		public H getHandle();
		public void setHandle(H handle);
		public void setContent(Document content) throws Exception;
	}
	public interface ReadGetter<H extends AbstractReadHandle> {
		public H getHandle();
		public void setHandle(H handle);
		public Document getContent() throws Exception;
	}

//	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
//	@AfterClass
	public static void afterClass() {
		Common.release();
	}

//	@Test
	public void testReadWrite() throws TransformerFactoryConfigurationError, Exception {
		// known document managers
		DocumentManager[] managers = {
				Common.client.newDocumentManager(),
				Common.client.newBinaryDocumentManager(),
				Common.client.newJSONDocumentManager(),
				Common.client.newTextDocumentManager(),
				Common.client.newXMLDocumentManager()
		};

		// known handles
		Class[] handleClasses = {
				BytesHandle.class, DOMHandle.class, FileHandle.class, InputSourceHandle.class,
				InputStreamHandle.class, ReaderHandle.class, SourceHandle.class,
				StringHandle.class, XMLEventReaderHandle.class, XMLStreamReaderHandle.class
		};

		HashMap<Class, Object> adapters = new HashMap<Class, Object>(handleClasses.length);
		adapters.put( BytesHandle.class,           new BytesAdapter(           new BytesHandle()           ));
		adapters.put( DOMHandle.class,             new DOMAdapter(             new DOMHandle()             ));
		adapters.put( FileHandle.class,            new FileAdapter(            new FileHandle()            ));
		adapters.put( InputSourceHandle.class,     new InputSourceAdapter(     new InputSourceHandle()     ));
		adapters.put( InputStreamHandle.class,     new InputStreamAdapter(     new InputStreamHandle()     ));
		adapters.put( ReaderHandle.class,          new ReaderAdapter(          new ReaderHandle()          ));
		adapters.put( SourceHandle.class,          new SourceAdapter(          new SourceHandle()          ));
		adapters.put( StringHandle.class,          new StringAdapter(          new StringHandle()          ));
		adapters.put( XMLEventReaderHandle.class,  new XMLEventReaderAdapter(  new XMLEventReaderHandle()  ));
		adapters.put( XMLStreamReaderHandle.class, new XMLStreamReaderAdapter( new XMLStreamReaderHandle() ));

		// key of format, value of read or write handle
		HashMap<String, HashSet<Class<AbstractReadHandle>>> readHandles =
			new HashMap<String, HashSet<Class<AbstractReadHandle>>>(managers.length);
		HashMap<String, HashSet<Class<AbstractWriteHandle>>> writeHandles = 
			new HashMap<String, HashSet<Class<AbstractWriteHandle>>>(managers.length);

		// for known handles
		for (Class handleClass: handleClasses) {
			// get the interfaces they implement (will include STRUCTURE)
			for (Class markerInterface: handleClass.getInterfaces()) {
				// skip interfaces other than markers
				if (!"com.marklogic.client.io.marker".equals(markerInterface.getPackage().getName()))
					continue;
				String markerName = markerInterface.getName();
				// add to read handle map
				if (markerName.endsWith("ReadHandle")) {
					String readFormat = markerName.replaceFirst("^com\\.marklogic\\.client\\.io\\.marker\\.(.*?)ReadHandle$", "$1").toUpperCase();
					HashSet<Class<AbstractReadHandle>> formatReaders = null;
					if (readHandles.containsKey(readFormat)) {
						formatReaders = readHandles.get(readFormat);
					} else {
						formatReaders = new HashSet<Class<AbstractReadHandle>>();
						readHandles.put(readFormat, formatReaders);
					}
					formatReaders.add(handleClass);
				// add to write handle map
				} else if (markerName.endsWith("WriteHandle")) {
					String writeFormat = markerName.replaceFirst("^com\\.marklogic\\.client\\.io\\.marker\\.(.*?)WriteHandle$", "$1").toUpperCase();
					HashSet<Class<AbstractWriteHandle>> formatWriters = null;
					if (writeHandles.containsKey(writeFormat)) {
						formatWriters = writeHandles.get(writeFormat);
					} else {
						formatWriters = new HashSet<Class<AbstractWriteHandle>>();
						writeHandles.put(writeFormat, formatWriters);
					}
					formatWriters.add(handleClass);
				} 
			}
		}

		Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = domDocument.createElement("root");
		root.setAttribute("xml:lang", "en");
		root.setAttribute("foo", "bar");
		root.appendChild(domDocument.createElement("child"));
		root.appendChild(domDocument.createTextNode("mixed"));
		domDocument.appendChild(root);

		for (DocumentManager manager: managers) {
			String format = manager.getClass().getName().replaceFirst("^com\\.marklogic\\.client\\.impl\\.(.*?)DocumentImpl$", "$1").toUpperCase();

// TODO: other formats
if (!"XML".equals(format)) continue;

			HashSet<Class<AbstractReadHandle>> formatReaders = readHandles.get(format);
			if (formatReaders == null || formatReaders.isEmpty()) {
				System.out.println("could not get readers for "+format);
				continue;
			}
			HashSet<Class<AbstractWriteHandle>> formatWriters = writeHandles.get(format);
			if (formatWriters == null || formatWriters.isEmpty()) {
				System.out.println("could not get writers for "+format);
				continue;
			}

			ArrayList<Class<AbstractReadHandle>>  readers =
				new ArrayList<Class<AbstractReadHandle>>(  formatReaders );
			ArrayList<Class<AbstractWriteHandle>> writers =
				new ArrayList<Class<AbstractWriteHandle>>( formatWriters );
			int max = Math.max(readers.size(), writers.size());
			for (int i=0; i < max; i++) {
				Class<AbstractReadHandle>  reader = readers.get((i < readers.size()) ? i : i - readers.size());
				Class<AbstractWriteHandle> writer = writers.get((i < writers.size()) ? i : i - writers.size());
				// pass comparator method and document
				testWriteRead(
						manager,
						"/test/"+format+"_"+i+".xml",
						(WriteSetter) adapters.get(writer),
						(ReadGetter)  adapters.get(reader),
						domDocument
				);
			}
		}
	}

	public void testWriteRead(
			DocumentManager docMgr,
			String                  docId,
			WriteSetter             writeSetter,
			ReadGetter              readGetter,
			Document                writeContent
			) throws Exception
	{
System.out.println(
docMgr.getClass().getName()+
" writing "+writeSetter.getClass().getName()+
" reading "+readGetter.getClass().getName());
		writeSetter.setContent(writeContent);
		AbstractWriteHandle writeHandle = writeSetter.getHandle();
		docMgr.write(docId, writeHandle);

		AbstractReadHandle readHandle = readGetter.getHandle();
		docMgr.read(docId, readHandle);
		Document readContent = readGetter.getContent();

		assertXMLEqual(
				String.format("Failed with %s manager, %s writer, and %s reader",
						docMgr.getClass().getName(),
						writeHandle.getClass().getName(),
						readHandle.getClass().getName()),
				writeContent,
				readContent
				);
	}

	// TODO: move into package?
	static public class XMLUtil {
		static public File documentToFile(Document document) throws FileNotFoundException, ParserConfigurationException, IOException {
			File file = File.createTempFile("tmp", null);
			documentToOutputStream(document, new FileOutputStream(file));
			return file;
		}
		static public Document fileToDocument(File file) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
			return inputStreamToDocument(new FileInputStream(file));
		}
		static public Reader documentToReader(Document document) throws ParserConfigurationException {
			return new StringReader(documentToString(document));
		}
		static public Document readerToDocument(Reader reader) throws ParserConfigurationException, SAXException, IOException {
			StringBuilder builder = new StringBuilder();
			char[] cbuf = new char[1024];
			int length = 0;
			while ((length = reader.read(cbuf)) != -1) {
				builder.append(cbuf, 0, length);
			}
			return stringToDocument(builder.toString());
		}
		static public String documentToString(Document document) throws ParserConfigurationException {
			return new String(documentToBytes(document));
		}
		static public Document stringToDocument(String string) throws ParserConfigurationException, SAXException, IOException {
			return bytesToDocument(string.getBytes());
		}
		static public byte[] documentToBytes(Document document) throws ParserConfigurationException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			documentToOutputStream(document, baos);
			return baos.toByteArray();
		}
		static public Document bytesToDocument(byte[] bytes) throws ParserConfigurationException, SAXException, IOException {
			return inputStreamToDocument(new ByteArrayInputStream(bytes));
		}
		static public void documentToOutputStream(Document document, OutputStream out) throws ParserConfigurationException {
			DOMImplementationLS domImpl = (DOMImplementationLS) makeDocumentBuilder().getDOMImplementation();
			LSOutput domOutput = domImpl.createLSOutput();
			domOutput.setByteStream(out);
			domImpl.createLSSerializer().write(document, domOutput);
		}
		static public InputStream documentToInputStream(Document document) throws ParserConfigurationException {
			return new ByteArrayInputStream(documentToBytes(document));
		}
		static private DocumentBuilder makeDocumentBuilder() throws ParserConfigurationException {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);

			return factory.newDocumentBuilder();
		}
		static public Document inputStreamToDocument(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
			return makeDocumentBuilder().parse(stream);
		}
		static public Source documentToSource (Document document) throws ParserConfigurationException {
			return new StreamSource(documentToInputStream(document));
		}
		static public Document sourceToDocument(Source source) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
			DOMResult result = new DOMResult();
			TransformerFactory.newInstance().newTransformer().transform(source, result);
			return (Document) result.getNode();
		}
		static public Document inputSourceToDocument(InputSource source) throws SAXException, IOException, ParserConfigurationException {
			return makeDocumentBuilder().parse(source);
		}
		static public Document xmlEventReaderToDocument(XMLEventReader event) throws XMLStreamException, FactoryConfigurationError, ParserConfigurationException, SAXException, IOException {
			StringWriter stringWriter = new StringWriter();
			XMLEventWriter eventWriter = XMLOutputFactory.newFactory().createXMLEventWriter(stringWriter);
			eventWriter.add(event);
			eventWriter.flush();
			eventWriter.close();
			return stringToDocument(stringWriter.toString());
		}
		static public Document xmlStreamReaderToDocument(XMLStreamReader stream) throws XMLStreamException, FactoryConfigurationError, ParserConfigurationException, SAXException, IOException {
			return xmlEventReaderToDocument(XMLInputFactory.newFactory().createXMLEventReader(stream));
		}
	}
	public class BytesAdapter
	implements WriteSetter<BytesHandle>, ReadGetter<BytesHandle> {
		private BytesHandle handle;
		public BytesAdapter(BytesHandle handle) {
			super();
			setHandle(handle);
		}
		@Override
		public BytesHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(BytesHandle handle) {
			this.handle = handle;
		}
		@Override
		public void setContent(Document content) throws ParserConfigurationException {
			handle.set(XMLUtil.documentToBytes(content));
		}
		@Override
		public Document getContent() throws ParserConfigurationException, SAXException, IOException {
			return XMLUtil.bytesToDocument(handle.get());
		}
	}
	public class DOMAdapter
	implements WriteSetter<DOMHandle>, ReadGetter<DOMHandle> {
		private DOMHandle handle;
		public DOMAdapter(DOMHandle handle) {
			super();
			setHandle(handle);
		}
		@Override
		public DOMHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(DOMHandle handle) {
			this.handle = handle;
		}
		@Override
		public Document getContent() throws Exception {
			return handle.get();
		}
		@Override
		public void setContent(Document content) throws Exception {
			handle.set(content);
		}
	}
	public class FileAdapter
	implements WriteSetter<FileHandle>, ReadGetter<FileHandle> {
		private FileHandle handle;
		public FileAdapter(FileHandle handle) {
			super();
			setHandle(handle);
		}
		@Override
		public FileHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(FileHandle handle) {
			this.handle = handle;
		}
		@Override
		public Document getContent() throws Exception {
			return XMLUtil.fileToDocument(handle.get());
		}
		@Override
		public void setContent(Document content) throws Exception {
			handle.set(XMLUtil.documentToFile(content));
		}
	}
	public class InputSourceAdapter
	implements ReadGetter<InputSourceHandle> {
		private InputSourceHandle handle;
		public InputSourceAdapter(InputSourceHandle handle) {
			super();
			setHandle(handle);
		}
		@Override
		public InputSourceHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(InputSourceHandle handle) {
			this.handle = handle;
		}

		@Override
		public Document getContent() throws Exception {
			return XMLUtil.inputSourceToDocument(handle.get());
		}
	}
	public class InputStreamAdapter
	implements WriteSetter<InputStreamHandle>, ReadGetter<InputStreamHandle> {
		private InputStreamHandle handle;
		public InputStreamAdapter(InputStreamHandle handle) {
			super();
			setHandle(handle);
		}
		@Override
		public InputStreamHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(InputStreamHandle handle) {
			this.handle = handle;
		}

		@Override
		public Document getContent() throws Exception {
			return XMLUtil.inputStreamToDocument(handle.get());
		}
		@Override
		public void setContent(Document content) throws Exception {
			handle.set(XMLUtil.documentToInputStream(content));
		}
	}
	public class ReaderAdapter
	implements WriteSetter<ReaderHandle>, ReadGetter<ReaderHandle> {
		private ReaderHandle handle;
		public ReaderAdapter(ReaderHandle handle) {
			super();
			setHandle(handle);
		}

		@Override
		public ReaderHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(ReaderHandle handle) {
			this.handle = handle;
		}

		@Override
		public Document getContent() throws Exception {
			return XMLUtil.readerToDocument(handle.get());
		}
		@Override
		public void setContent(Document content) throws Exception {
			handle.set(XMLUtil.documentToReader(content));
		}
	}
	public class SourceAdapter
	implements WriteSetter<SourceHandle>, ReadGetter<SourceHandle> {
		private SourceHandle handle;
		public SourceAdapter(SourceHandle handle) {
			super();
			setHandle(handle);
		}

		@Override
		public SourceHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(SourceHandle handle) {
			this.handle = handle;
		}

		@Override
		public Document getContent() throws Exception {
			return XMLUtil.sourceToDocument(handle.get());
		}
		@Override
		public void setContent(Document content) throws Exception {
			handle.set(XMLUtil.documentToSource(content));
		}
	}
	public class StringAdapter
	implements WriteSetter<StringHandle>, ReadGetter<StringHandle> {
		private StringHandle handle;
		public StringAdapter(StringHandle handle) {
			super();
			setHandle(handle);
		}

		@Override
		public StringHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(StringHandle handle) {
			this.handle = handle;
		}

		@Override
		public Document getContent() throws Exception {
			return XMLUtil.stringToDocument(handle.get());
		}
		@Override
		public void setContent(Document content) throws Exception {
			handle.set(XMLUtil.documentToString(content));
		}
	}
	public class XMLEventReaderAdapter
	implements ReadGetter<XMLEventReaderHandle> {
		private XMLEventReaderHandle handle;
		public XMLEventReaderAdapter(XMLEventReaderHandle handle) {
			super();
			setHandle(handle);
		}
		@Override
		public XMLEventReaderHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(XMLEventReaderHandle handle) {
			this.handle = handle;
		}

		@Override
		public Document getContent() throws Exception {
			return XMLUtil.xmlEventReaderToDocument(handle.get());
		}
	}
	public class XMLStreamReaderAdapter
	implements ReadGetter<XMLStreamReaderHandle> {
		private XMLStreamReaderHandle handle;
		public XMLStreamReaderAdapter(XMLStreamReaderHandle handle) {
			super();
			setHandle(handle);
		}
		@Override
		public XMLStreamReaderHandle getHandle() {
			return handle;
		}
		@Override
		public void setHandle(XMLStreamReaderHandle handle) {
			this.handle = handle;
		}

		@Override
		public Document getContent() throws Exception {
			return XMLUtil.xmlStreamReaderToDocument(handle.get());
		}
	}
}
