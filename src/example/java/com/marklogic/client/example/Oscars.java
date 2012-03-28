package com.marklogic.client.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.MatchLocation;
import com.marklogic.client.config.search.MatchSnippet;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.config.search.ValueConstraint;
import com.marklogic.client.config.search.impl.ValueConstraintImpl;
import com.marklogic.client.example.cookbook.DocumentWrite;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;

public class Oscars {
	static final private String OPTIONS_NAME = "oscars";

	public static void main(String[] args) throws IOException, ParserConfigurationException {
		Properties props = loadProperties();

		// connection parameters
		String         host     = props.getProperty("example.host");
		int            port     = Integer.parseInt(props.getProperty("oscars.port"));
		String         user     = "admin";
		String         password = "admin";
		Authentication authType = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		Oscars oscars = new Oscars();
		oscars.configure( host, port, user, password, authType );
		oscars.search(    host, port, user, password, authType );
	}

	public void configure(String host, int port, String user, String password, Authentication authType)
	throws IOException, ParserConfigurationException {
		// connect the client
		DatabaseClient client =
			DatabaseClientFactory.connect(host, port, user, password, authType);

		// create a manager for writing query options
		QueryOptionsManager optionsMgr = client.newQueryOptionsManager();

		// create a handle with query options
		QueryOptionsHandle writeHandle = new QueryOptionsHandle();

		ValueConstraint constraint = null;

		// add a constraint for the year index
		constraint = new ValueConstraintImpl("year");
		constraint.addElementAttributeIndex(
				new QName("http://marklogic.com/wikipedia","oscar"),
				new QName("year")
				);
		writeHandle.add(constraint);

		// add a constraint for the award index
		constraint = new ValueConstraintImpl("award");
		constraint.addElementAttributeIndex(
				new QName("http://marklogic.com/wikipedia","oscar"),
				new QName("award")
				);
		writeHandle.add(constraint);

		// add a constraint for the winner index
		constraint = new ValueConstraintImpl("winner");
		constraint.addElementAttributeIndex(
				new QName("http://marklogic.com/wikipedia","oscar"),
				new QName("winner")
				);
		writeHandle.add(constraint);

		writeHandle.setReturnResults(false);
		writeHandle.setReturnFacets(false);

		// write the query options to the database
		optionsMgr.writeOptions(OPTIONS_NAME, writeHandle);

// System.out.println(optionsMgr.readOptions(OPTIONS_NAME, new StringHandle()).get());

		// release the client
		client.release();
	}

	public void search(String host, int port, String user, String password, Authentication authType)
	throws IOException {
		// connect the client
		DatabaseClient client =
			DatabaseClientFactory.connect(host, port, user, password, authType);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		StringQueryDefinition querydef = queryMgr.newStringDefinition(OPTIONS_NAME);
		querydef.setCriteria("Olivier AND Elsinore");

		// create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// run the search
		queryMgr.search(querydef, resultsHandle);

		// create a manager for the matched XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create a handle for the content of matched XML documents
		DOMHandle docHandle = new DOMHandle();

		// iterate over the matched documents
		System.out.println("Matched "+resultsHandle.getTotalResults()+" documents:\n");
		for (MatchDocumentSummary docSummary: resultsHandle.getMatchResults()) {
			// read a matched document into the handle
			docMgr.read(docSummary, docHandle);

			System.out.print(docSummary.getUri()+": ");

			// get the content of the matched document
			Document document = docHandle.get();

			Element root = document.getDocumentElement();
			NamedNodeMap attributes = root.getAttributes();
			for (int i=0; i < attributes.getLength(); i++) {
				Attr attribute = (Attr) attributes.item(i);

				String attName = attribute.getName();
				if (attName.startsWith("xmlns:"))
					continue;

				if (i > 0)
					System.out.print(",");

				System.out.print(" "+attName+"="+attribute.getNodeValue());
			}

			System.out.println();
		}

		// release the client
		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			DocumentWrite.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}
}

/* TODO: delete

DOMHandle writeHandle = new DOMHandle();

Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
writeHandle.set(document);

String searchNs = "http://marklogic.com/appservices/search";
String wikiNs   = "http://marklogic.com/wikipedia";

Element root = document.createElementNS(searchNs, "options");
document.appendChild(root);

Element parent = document.createElementNS(searchNs, "constraint");
parent.setAttribute("name", "award");
root.appendChild(parent);

Element child = document.createElementNS(searchNs, "value");
parent.appendChild(child);
parent = child;

child = document.createElementNS(searchNs, "element");
child.setAttribute("ns",    wikiNs);
child.setAttribute("name", "oscar");
parent.appendChild(child);

child = document.createElementNS(searchNs, "attribute");
child.setAttribute("ns",    XMLConstants.NULL_NS_URI);
child.setAttribute("name", "award");
parent.appendChild(child);

parent = document.createElementNS(searchNs, "constraint");
parent.setAttribute("name", "year");
root.appendChild(parent);

child = document.createElementNS(searchNs, "value");
parent.appendChild(child);
parent = child;

child = document.createElementNS(searchNs, "element");
child.setAttribute("ns",    wikiNs);
child.setAttribute("name", "oscar");
parent.appendChild(child);

child = document.createElementNS(searchNs, "attribute");
child.setAttribute("ns",    XMLConstants.NULL_NS_URI);
child.setAttribute("name", "year");
parent.appendChild(child);

parent = document.createElementNS(searchNs, "constraint");
parent.setAttribute("name", "winner");
root.appendChild(parent);

child = document.createElementNS(searchNs, "value");
parent.appendChild(child);
parent = child;

child = document.createElementNS(searchNs, "element");
child.setAttribute("ns",    wikiNs);
child.setAttribute("name", "oscar");
parent.appendChild(child);

child = document.createElementNS(searchNs, "attribute");
child.setAttribute("ns",    XMLConstants.NULL_NS_URI);
child.setAttribute("name", "winner");
parent.appendChild(child);

parent = document.createElementNS(searchNs, "return-results");
parent.setTextContent("true");
root.appendChild(parent);

parent = document.createElementNS(searchNs, "return-facets");
parent.setTextContent("false");
root.appendChild(parent);
*/
