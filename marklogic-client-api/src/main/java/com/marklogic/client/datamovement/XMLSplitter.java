/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The XMLSplitter is used to split large XML file into separate payloads for writing to the database. For example,
 * large sets of records are often serialized as XML. In particular, relational databases can export a table as an XML
 * file. XMLSplitter could split each row in the large XML file into separate files.
 * @param <T> The type of the handle used for each split payload
 */
public class XMLSplitter<T extends XMLWriteHandle> implements Splitter<T> {

    /**
     * Construct a simple XMLSplitter which split the XML file according to element name.
     * @param nsUri The namespace URI of the element
     * @param localName the local name of the element
     * @return an XMLSplitter which splits each specified element into a separate payloads
     */
    static public XMLSplitter<StringHandle> makeSplitter(String nsUri, String localName) {

        XMLSplitter.BasicElementVisitor visitor = new XMLSplitter.BasicElementVisitor(nsUri, localName);
        return new XMLSplitter<>(visitor);
    }

    private XMLSplitter.Visitor<T> visitor;
    private long count = 0;
    private String splitFilename;

    /**
     * Construct an XMLSplitter which split the XML file according to the visitor.
     * @param visitor determines which elements to split
     */
    public XMLSplitter(XMLSplitter.Visitor<T> visitor) {
        setVisitor(visitor);
    }

    /**
     * Get the visitor used in XMLSplitter class.
     * @return the visitor used in XMLSplitter class
     */
    public XMLSplitter.Visitor<T> getVisitor() {
        return this.visitor;
    }

    /**
     * Set the visitor to select elements to split in XMLSplitter.
     * @param visitor the visitor used in XMLSplitter class
     */
    public void setVisitor(XMLSplitter.Visitor<T> visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor cannot be null");
        }
        this.visitor = visitor;
    }

    /**
     * Takes an input stream of an XML file and split it into a steam of handles.
     * @param input is the incoming input stream of an XML file
     * @return a stream of handles to write to database
     * @throws IOException if the input cannot be split
     * @throws XMLStreamException if there is an error processing the underlying XML source
     */
    @Override
    public Stream<T> split(InputStream input) throws IOException, XMLStreamException {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(input);
        return split(reader);
    }

    /**
     * Takes an input stream of an XML file and split it into a steam of DocumentWriteOperation.
     * @param input is the incoming input stream.
     * @return a stream of DocumentWriteOperation to write to database
     * @throws Exception if the input cannot be split
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input) throws Exception {
        return splitWriteOperations(input, null);
    }

    /**
     * Takes an input stream of an XML file and input file name, split it into a steam of DocumentWriteOperation.
     * @param input is the incoming input stream.
     * @param splitFilename is the name of input file, including name and extension. It is used to generate URLs for split
     *                  files.The splitFilename could either be provided here or in user-defined UriMaker.
     * @return a stream of DocumentWriteOperation to write to database
     * @throws Exception if the input cannot be split
     */
    @Override
    public Stream<DocumentWriteOperation> splitWriteOperations(InputStream input, String splitFilename) throws Exception {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(input);
        return splitWriteOperations(reader, splitFilename);
    }

    /**
     * Returns the number of splits.
     * @return the number of splits
     */
    @Override
    public long getCount() {
        return count;
    }

    /**
     * Take an input of XMLStreamReader of the XML file and split it into a stream of handles to write to database.
     * @param input an XMLStreamReader of the XML file
     * @return a stream of handles to write to database
     * @throws IOException if the input cannot be split
     */
    public Stream<T> split(XMLStreamReader input) throws IOException {

        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        count = 0;

        XMLSplitter.HandleSpliterator<T> handleSpliterator = new XMLSplitter.HandleSpliterator<>(this, input);

        return StreamSupport.stream(handleSpliterator, true);
    }

    /**
     * Take an input of XMLStreamReader of the XML file and split it into a stream of DocumentWriteOperations
     * to write to database.
     * @param input an XMLStreamReader of the XML file
     * @param splitFilename is the name of the input file, including name and extension. It is used to generate URLs for
     *                  split files.The splitFilename could either be provided here or in user-defined UriMaker.
     * @return a stream of DocumentWriteOperation to write to database
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(XMLStreamReader input, String splitFilename) {

        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        count = 0;

        this.splitFilename = splitFilename;
        XMLSplitter.DocumentWriteOperationSpliterator<T> documentWriteOperationSpliterator =
                new XMLSplitter.DocumentWriteOperationSpliterator<>(this, input);

        return StreamSupport.stream(documentWriteOperationSpliterator, true);
    }

    /**
     * The StartElementReader is used in visitor to check if the current element is the one to split. It supports some
     * of the XMLStreamReader methods that inspect the start element state without changing the stream state.
     */
    public interface StartElementReader {
        int getAttributeCount();
        String getAttributeLocalName(int index);
        QName getAttributeName(int index);
        String getAttributeNamespace(int index);
        String getAttributePrefix(int index);
        String getAttributeType(int index);
        String getAttributeValue(int index);
        String getAttributeValue(String namespaceURI, String localName);
        String getLocalName();
        QName getName();
        NamespaceContext getNamespaceContext();
        int getNamespaceCount();
        String getNamespacePrefix(int index);
        String getNamespaceURI();
        String getNamespaceURI(int index);
        String getNamespaceURI(String prefix);
        String getPrefix();
        boolean isAttributeSpecified(int index);
    }

    /**
     * The Visitor class is used to check if the current element is the target to split. If it is, convert the target
     * elements into buffered handles or DocumentWriteOperations.
     * @param <T> The type of the handle used for each split
     */
    static public abstract class Visitor<T extends XMLWriteHandle>   {
        private TransformerFactory transformerFactory = TransformerFactory.newInstance();
        private Transformer transformer;

        /**
         * Use the methods in StartElementReader to check if the current element is the one to split.
         * @param startElementReader inspects the start element state
         * @return different operations to either process current element, go down the XML tree or skip current element
         */
        public abstract NodeOperation startElement(StartElementReader startElementReader);

        /**
         * Receives a notification when hitting end element.
         * @param nsUri the namespace URI of the target element
         * @param localName the local name of the target element
         */
        public void endElement(String nsUri, String localName) {
        }

        /**
         * Construct buffered content handles with proper types from XMLStreamReader.
         * @param xmlStreamReader the XMLStreamReader with target element as start element in the current state
         * @return the handle with target elements as content
         */
        public abstract T makeBufferedHandle(XMLStreamReader xmlStreamReader);

        /**
         * Construct buffered DocumentWriteOperations from XMLStreamReader
         * @param uriMaker the UriMake to construct the URI for each document
         * @param count the count of each split
         * @param handle the handle contains target elements as content
         * @return DocumentWriteOperations to write to database
         */
        public DocumentWriteOperation makeDocumentWriteOperation(XMLSplitter.UriMaker uriMaker, long count, T handle) {
            if (handle == null) {
                throw new IllegalArgumentException("Handle cannot be null");
            }

            String uri = uriMaker.makeUri(count, handle);

            return new DocumentWriteOperationImpl(
                    DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                    uri,
                    null,
                    handle
            );
        }

        /**
         * Serialize the target elements in XMLStreamReader to Strings.
         * @param reader the XMLStreamReader with target element as start element in the current state
         * @return String contains target elements
         */
        public String serialize(XMLStreamReader reader)  {
            if (reader == null) {
                throw new IllegalArgumentException("XMLStreamReader cannot be null");
            }

            try {
                if (transformer == null) {
                    transformer = transformerFactory.newTransformer();
                }

                StAXSource stAXSrouce = new StAXSource(reader);
                StringWriter stringWriter = new StringWriter();
                StreamResult streamResult = new StreamResult(stringWriter);
                transformer.transform(stAXSrouce, streamResult);
                stringWriter.flush();

                transformer.reset();
                return stringWriter.toString();
            } catch (TransformerException e) {
                throw new RuntimeException("Could not serialize the document", e);
            }
        }
    }

    /**
     * The basic visitor only splits elements with matching element namespace URI and local name.
     */
    static public class BasicElementVisitor extends Visitor<StringHandle>   {
        private String nsUri, localName;

        /**
         * Construct the BasicElementVisitor with target namespace URI and local name.
         * @param nsUri Namespace URI
         * @param localName Element local name
         */
        public BasicElementVisitor(String nsUri, String localName) {
            if (nsUri == null) {
                nsUri = "";
            }

            if (localName == null || localName.equals("")) {
                throw new IllegalArgumentException("LocalName cannot be null");
            }

            this.nsUri = nsUri;
            this.localName = localName;
        }

        /**
         * Checks if the current element matches the namespace URI and local name. If it matches, split this element.
         * @param startElementReader inspects the start element state
         * @return different operations to either split current element, go down the XML tree or skip the branch
         */
        public NodeOperation startElement(StartElementReader startElementReader) {
            if (startElementReader == null) {
                throw new IllegalArgumentException("StartElementReader cannot be null");
            }

            final String startNsUri = startElementReader.getNamespaceURI();
            final String startLocalName = startElementReader.getLocalName();
            if (((startNsUri != null && startNsUri.equals(nsUri)) ||
                    (startNsUri == null && (nsUri == null || nsUri.length() == 0))) &&
                (startLocalName != null && startLocalName.equals(localName) ||
                    (startLocalName == null && (localName == null || localName.length() == 0)))) {
                return NodeOperation.PROCESS;
            }

            return  NodeOperation.DESCEND;
        }

        /**
         * Construct StringHandles from XMLStreamReader with target element.
         * @param xmlStreamReader the XMLStreamReader with target element as start element in the current state
         * @return Buffered StringHandles with target element.
         */
        public StringHandle makeBufferedHandle(XMLStreamReader xmlStreamReader) {
            if (xmlStreamReader == null) {
                throw new IllegalArgumentException("XMLStreamReader cannot be null");
            }
            String content = serialize(xmlStreamReader);
            return new StringHandle(content).withFormat(Format.XML);
        }
    }

    private static class StartElementReaderImpl
            extends StreamReaderDelegate
            implements StartElementReader {
        private StartElementReaderImpl(XMLStreamReader reader) {
            super(reader);
        }
    }

    private static abstract class XMLSpliterator<U, T extends XMLWriteHandle> extends Spliterators.AbstractSpliterator<U> {

        private XMLSplitter<T> splitter;
        private XMLStreamReader xmlStreamReader;
        private Visitor<T> visitor;

        private XMLStreamReader getXmlStreamReader() {
            return this.xmlStreamReader;
        }

        private void setXmlStreamReader(XMLStreamReader xmlStreamReader) {
            if (xmlStreamReader == null) {
                throw new IllegalArgumentException("XMLStreamReader cannot be null");
            }
            this.xmlStreamReader = xmlStreamReader;
        }

        private void setSplitter(XMLSplitter<T> xmlSplitter) {
            if (xmlSplitter == null) {
                throw new IllegalArgumentException("XMLSplitter cannot be null");
            }
            this.splitter = xmlSplitter;
        }

        XMLSplitter<T> getSplitter() {
            return this.splitter;
        }

        XMLSpliterator(XMLSplitter<T> xmlSplitter, XMLStreamReader input) {
            super(Long.MAX_VALUE, Spliterator.NONNULL + Spliterator.IMMUTABLE);
            setSplitter(xmlSplitter);
            setXmlStreamReader(input);
            visitor = splitter.getVisitor();
        }

        T getNextHandle() {
            try {
                while (xmlStreamReader.hasNext()) {
                    int event = xmlStreamReader.next();

                    checkForHandle:
                    switch (event) {
                        case XMLStreamReader.START_ELEMENT:
                            StartElementReaderImpl startElementReader = new StartElementReaderImpl(xmlStreamReader);
                            NodeOperation nodeOperation = visitor.startElement(startElementReader);

                            if (nodeOperation == null) {
                                throw new IllegalStateException("No NodeOperation returned.");
                            }

                            switch (nodeOperation) {
                                case DESCEND:
                                    break checkForHandle;

                                case PROCESS:
                                    T handle = visitor.makeBufferedHandle(new XMLBranchStreamReader(xmlStreamReader));
                                    if (handle != null) {
                                        return handle;
                                    }
                                    break checkForHandle;

                                case SKIP:
                                    int depth = 0;
                                    while (xmlStreamReader.hasNext()) {
                                        int next = xmlStreamReader.next();
                                        skipCheck:
                                        switch (next) {
                                            case XMLStreamReader.START_ELEMENT:
                                                depth++;
                                                break skipCheck;

                                            case XMLStreamReader.END_ELEMENT:
                                                if (depth == 0) {
                                                    break checkForHandle;
                                                }
                                                depth--;
                                                break skipCheck;

                                            default:
                                                break skipCheck;
                                        }
                                    }
                                    break checkForHandle;

                                default:
                                    throw new IllegalStateException("Unknown state");
                            }

                        case XMLStreamReader.END_ELEMENT:
                            visitor.endElement(
                                    xmlStreamReader.getNamespaceURI(),
                                    xmlStreamReader.getLocalName());
                            break checkForHandle;

                        default:
                            break checkForHandle;
                    }
                }

                return null;
            } catch (XMLStreamException e) {
                throw new RuntimeException("Failed to traverse document", e);
            }
        }
    }

    private static class HandleSpliterator<T extends XMLWriteHandle> extends XMLSpliterator<T, T> {

        HandleSpliterator(XMLSplitter<T> xmlSplitter, XMLStreamReader input) {
            super(xmlSplitter, input);
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            T handle = (T) getNextHandle();
            if (handle == null) {
                return false;
            }

            getSplitter().count = getSplitter().getCount() + 1;
            action.accept(handle);

            return true;
        }
    }

    private static class DocumentWriteOperationSpliterator<T extends XMLWriteHandle> extends XMLSpliterator<DocumentWriteOperation, T> {

        DocumentWriteOperationSpliterator(XMLSplitter<T> xmlSplitter, XMLStreamReader input) {
            super(xmlSplitter, input);
        }
        @Override
        public boolean tryAdvance(Consumer<? super DocumentWriteOperation> action) {

            T handle = (T) getNextHandle();
            if (handle == null) {
                return false;
            }

            XMLSplitter splitter = getSplitter();
            if (splitter.getUriMaker() == null) {
                XMLSplitter.UriMakerImpl uriMaker = new XMLSplitter.UriMakerImpl();
                uriMaker.setSplitFilename(splitter.splitFilename);
                uriMaker.setExtension("xml");
                splitter.setUriMaker(uriMaker);
            } else {
                if (splitter.splitFilename != null) {
                    splitter.getUriMaker().setSplitFilename(splitter.splitFilename);
                }
            }

            splitter.count = splitter.getCount() + 1;
            DocumentWriteOperation documentWriteOperation = splitter.getVisitor().makeDocumentWriteOperation(
                    splitter.getUriMaker(),
                    splitter.getCount(),
                    handle);

            action.accept(documentWriteOperation);

            return true;
        }
    }

    private static class XMLBranchStreamReader extends StreamReaderDelegate {
        private int depth = 1;

        XMLBranchStreamReader(XMLStreamReader reader) {
            super(reader);
        }

        public boolean hasNext() {
            return (depth > 0);
        }

        public int next() throws XMLStreamException {
            if (depth < 1) {
                return XMLStreamReader.END_DOCUMENT;
            }

            int next = super.next();
            if (depth >= 1) {
                if (next == XMLStreamReader.START_ELEMENT) {
                    depth++;
                }
                if (next == XMLStreamReader.END_ELEMENT) {
                    depth--;
                }
            }

            return next;
        }

        public int nextTag() throws XMLStreamException {
            if (depth < 1) {
                return XMLStreamReader.END_DOCUMENT;
            }
            int next = super.nextTag();
            if (next == XMLStreamReader.START_ELEMENT) {
                depth++;
            }
            if (next == XMLStreamReader.END_ELEMENT) {
                depth--;
            }

            return next;
        }

        public void close() {
            throw new UnsupportedOperationException("Current XML branch cannot be closed.");
        }
    }

    private XMLSplitter.UriMaker uriMaker;

    /**
     * Get the UriMaker of the splitter
     * @return the UriMaker of the splitter
     */
    public XMLSplitter.UriMaker getUriMaker() {
        return this.uriMaker;
    }

    /**
     * Set the UriMaker to the splitter
     * @param uriMaker the uriMaker to generate URI of each split file.
     */
    public void setUriMaker(XMLSplitter.UriMaker uriMaker) {
        this.uriMaker = uriMaker;
    }

    /**
     * UriMaker which generates URI for each split file
     */
    public interface UriMaker extends Splitter.UriMaker {
        /**
         * Generates URI for each split
         * @param num the count of each split
         * @param handle the handle which contains the content of each split. It could be utilized to make a meaningful
         *               document URI.
         * @return the generated URI of current split
         */
        String makeUri(long num, XMLWriteHandle handle);
    }

    private static class UriMakerImpl extends com.marklogic.client.datamovement.impl.UriMakerImpl<XMLWriteHandle>
            implements XMLSplitter.UriMaker {

    }



}
