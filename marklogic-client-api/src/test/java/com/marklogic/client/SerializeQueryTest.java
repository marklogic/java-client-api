package com.marklogic.client;

import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.rest.util.Fragment;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;

public class SerializeQueryTest {

	public static void main(String[] args) throws Exception {
		DatabaseClient client = new DatabaseClientBuilder()
									.withHost("localhost")
									.withPort(8000)
									.withDigestAuth("admin", "admin").build();

		StructuredQueryBuilder qb = client.newQueryManager().newStructuredQueryBuilder();
		StructuredQueryDefinition query = qb.and(qb.collection("hello"), qb.term("world"));

		final String SEARCH_NS = "http://marklogic.com/appservices/search";

		XMLOutputFactory factory = XMLOutputFactory.newFactory();
		// Required in order for default namespace to be applied.
		factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);

		StringWriter writer = new StringWriter();
		XMLStreamWriter serializer = factory.createXMLStreamWriter(writer);

		// Must set this in order for the serialized query to be in the correct namespace.
		serializer.setDefaultNamespace(SEARCH_NS);

		serializer.writeStartElement("someOtherNamespace", "myDocument");
		serializer.writeStartElement("someLocalElement");
		serializer.writeEndElement();

		// In order for query.serialize to work, it appears necessary to wrap the query in an element in the search
		// namespace. And because that's the default namespace on the XMLStreamWriter, it will be applied to each of
		// the child elements produced by query.serialize. 
		serializer.writeStartElement(SEARCH_NS, "theMarkLogicQuery");
		query.serialize(serializer);
		serializer.writeEndElement();

		serializer.writeEndElement();
		serializer.flush();
		serializer.close();

		String output = writer.toString();
		new Fragment(output).prettyPrint();
	}
}
