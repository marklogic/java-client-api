/*
 * Copyright 2015-2020 MarkLogic Corporation
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
import java.util.UUID;
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
    private int count = 0;

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
     * @throws IOException
     * @throws XMLStreamException
     */
    @Override
    public Stream<T> split(InputStream input) throws IOException, XMLStreamException {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(input);
        return split(reader);
    }

    @Override
    public long getCount() {
        return count;
    }

    /**
     * Take an input of XMLStreamReader of the XML file and split it into a stream of handles to write to database.
     * @param input an XMLStreamReader of the XML file
     * @return a stream of handles to write to database
     * @throws IOException
     */
    public Stream<T> split(XMLStreamReader input) throws IOException {

        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        XMLSplitter.HandleSpliterator<T> handleSpliterator = new XMLSplitter.HandleSpliterator<>(this, input);

        return StreamSupport.stream(handleSpliterator, true);
    }

    /**
     * Take an input of XMLStreamReader of the XML file and split it into a stream of DocumentWriteOperations
     * to write to database.
     * @param input an XMLStreamReader of the XML file
     * @return a stream of DocumentWriteOperation to write to database
     */
    public Stream<DocumentWriteOperation> splitWriteOperations(XMLStreamReader input) {

        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

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
         * @param handle the handle contains target elements as content
         * @return DocumentWriteOperations to write to database
         */
        public DocumentWriteOperation makeDocumentWriteOperation(T handle) {
            if (handle == null) {
                throw new IllegalArgumentException("Handle cannot be null");
            }

            String uri = UUID.randomUUID().toString() + ".xml";

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

            if (localName == null) {
                localName = "";
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

            if (startElementReader.getNamespaceURI().equals(nsUri) && startElementReader.getLocalName().equals(localName)) {
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

                    if (event == XMLStreamReader.START_ELEMENT) {
                        StartElementReaderImpl startElementReader = new StartElementReaderImpl(xmlStreamReader);
                        NodeOperation nodeOperation = visitor.startElement(startElementReader);

                        if (nodeOperation == null) {
                            throw new IllegalStateException("No NodeOperation returned.");
                        }

                        switch (nodeOperation) {
                            case DESCEND:
                                break;

                            case PROCESS:
                                T handle = visitor.makeBufferedHandle(xmlStreamReader);
                                if (handle != null) {
                                    return handle;
                                }
                                break;

                            case SKIP:
                                int count = 0;
                                while (xmlStreamReader.hasNext()) {
                                    int next = xmlStreamReader.next();
                                    if (next == XMLStreamReader.START_ELEMENT) {
                                        count++;
                                    }

                                    if (next == XMLStreamReader.END_ELEMENT) {
                                        if (count == 0) {
                                            break;
                                        }
                                        count--;
                                    }
                                }
                                break;

                            default:
                                throw new IllegalStateException("Unknown state");
                        }
                    }else if (event == XMLStreamReader.END_ELEMENT) {
                        visitor.endElement(
                                xmlStreamReader.getNamespaceURI(),
                                xmlStreamReader.getLocalName());
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

            action.accept(handle);
            getSplitter().count++;

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

            DocumentWriteOperation documentWriteOperation = getSplitter().getVisitor().makeDocumentWriteOperation(handle);

            action.accept(documentWriteOperation);
            getSplitter().count++;

            return true;
        }
    }

}