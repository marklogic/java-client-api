package com.marklogic.client.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

public class BasicXMLSerializer {
	private static final byte LEFT_BRACKET  = '<';
	private static final byte RIGHT_BRACKET = '>';
	private static final byte FORWARD_SLASH = '/';
	private static final byte SPACE         = ' ';
	private static final byte EQUALS        = '=';
	private static final byte COLON         = ':';
	private static final byte QUOTE	        = '"';

	private static final byte[] XML_PROLOG    = "<?xml version=\"1.0\" encoding=\"utf-8\"?>".getBytes();
	private static final byte[] XMLNS_PREFIX  = "xmlns".getBytes();
	private static final byte[] NEWLINE       = "\n".getBytes();

	private static final Pattern QUOTE_PATTERN        = Pattern.compile("\"");
	private static final Pattern AMPERSAND_PATTERN    = Pattern.compile("&");
	private static final Pattern LESS_THAN_PATTERN    = Pattern.compile("<");
	private static final Pattern GREATER_THAN_PATTERN = Pattern.compile(">");

	private int containerDepth = 0;
	private int containedDepth = 0;
	private int indentStep = 3;

	public BasicXMLSerializer() {
	}

	public int getIndentStep() {
		return indentStep;
	}
	public void setIndentStep(int indentStep) {
		this.indentStep = indentStep;
	}

	public void writeXMLProlog(OutputStream out) throws IOException {
		out.write(XML_PROLOG);
		out.write(NEWLINE);
	}
	public void writeOpen(OutputStream out, String prefix, String localPart) throws IOException {
		writeOpenStart(out, prefix, localPart);
		writeOpenEnd(out);
	}
	public void writeOpen(OutputStream out, String qname) throws IOException {
		writeOpenStart(out, qname, false);
		writeOpenEnd(out);
	}
	public void writeContainerOpen(OutputStream out, String prefix, String localPart) throws IOException {
		writeContainerOpenStart(out, prefix, localPart);
		writeOpenEnd(out);
	}
	public void writeContainerOpen(OutputStream out, String qname) throws IOException {
		writeOpenStart(out, qname, true);
		writeOpenEnd(out);
	}
	public void writeSingleton(OutputStream out, String prefix, String localPart) throws IOException {
		writeOpenStart(out, prefix, localPart);
		writeSingletonClose(out);
	}
	public void writeSingleton(OutputStream out, String qname) throws IOException {
		writeOpenStart(out, qname, false);
		writeSingletonClose(out);
	}
	public void writeContainerSingleton(OutputStream out, String prefix, String localPart) throws IOException {
		writeContainerOpenStart(out, prefix, localPart);
		writeSingletonClose(out);
	}
	public void writeContainerSingleton(OutputStream out, String qname) throws IOException {
		writeOpenStart(out, qname, true);
		writeSingletonClose(out);
	}
	public void writeOpenStart(OutputStream out, String prefix, String localPart) throws IOException {
		writeOpenStart(out, makeQName(prefix, localPart), false);
	}
	public void writeOpenStart(OutputStream out, String qname) throws IOException {
		writeOpenStart(out, qname, false);
	}
	public void writeContainerOpenStart(OutputStream out, String prefix, String localPart) throws IOException {
		writeOpenStart(out, makeQName(prefix, localPart), true);
	}
	public void writeContainerOpenStart(OutputStream out, String qname) throws IOException {
		writeOpenStart(out, qname, true);
	}
	private void writeOpenStart(OutputStream out, String qname, boolean isContainer) throws IOException {
		if (containedDepth == 0) {
			indent(out);
			containerDepth++;
		}
		if (!isContainer || containedDepth > 0) {
			containedDepth++;
		}
		out.write(LEFT_BRACKET);
		out.write(qname.getBytes());
	}
	public void writeNamespace(OutputStream out, String uri) throws IOException {
		writeNamespace(out, null, uri);
	}
	public void writeNamespace(OutputStream out, String prefix, String uri) throws IOException {
		out.write(SPACE);
		out.write(XMLNS_PREFIX);
		if (prefix != null) {
			out.write(COLON);
			out.write(prefix.getBytes());
		}
		out.write(EQUALS);
		out.write(QUOTE);
		out.write(uri.getBytes());
		out.write(QUOTE);
	}
	public void writeAttribute(OutputStream out, String name, String value) throws IOException {
		out.write(SPACE);
		out.write(name.getBytes());
		out.write(EQUALS);
		out.write(QUOTE);
		if (value != null)
			out.write(value.getBytes());
		out.write(QUOTE);
	}
	public void writeEscapedAttribute(OutputStream out, String name, String value) throws IOException {
		writeAttribute(out, name, escapeAttributeValue(value));
	}
	public void writeSingletonClose(OutputStream out) throws IOException {
		out.write(FORWARD_SLASH);
		out.write(RIGHT_BRACKET);
		if (containedDepth > 0)
			containedDepth--;
		if (containedDepth == 0) {
			out.write(NEWLINE);
			containerDepth--;
		}
	}
	public void writeOpenEnd(OutputStream out) throws IOException {
		out.write(RIGHT_BRACKET);
		if (containedDepth == 0)
			out.write(NEWLINE);
	}
	public void writeText(OutputStream out, String text) throws IOException {
		if (text != null)
			out.write(text.getBytes());
	}
	public void writeEscapedText(OutputStream out, String text) throws IOException {
		writeText(out, escapeText(text));
	}
	public void writeClose(OutputStream out, String prefix, String localPart) throws IOException {
		writeClose(out, makeQName(prefix, localPart));
	}
	public void writeClose(OutputStream out, String qname) throws IOException {
		boolean wasContained = containedDepth > 0; 
		if (wasContained)
			containedDepth--;
		if (containedDepth == 0) {
			containerDepth--;
			if (!wasContained)
				indent(out);
		}
		out.write(LEFT_BRACKET);
		out.write(FORWARD_SLASH);
		out.write(qname.getBytes());
		out.write(RIGHT_BRACKET);
		if (containedDepth == 0) {
			out.write(NEWLINE);
		}
	}

	private String escapeAttributeValue(String text) {
		text = escapeText(text);
		if (text == null)
			return null;
		
		if (text.contains("\"")) {
			text = QUOTE_PATTERN.matcher(text).replaceAll("&quot;");
		}

		return text;
	}
	private String escapeText(String text) {
		if (text == null || text.length() < 1)
			return null;

		// must precede other escapes
		if (text.contains("&")) {
			text = AMPERSAND_PATTERN.matcher(text).replaceAll("&amp;");
		}
		if (text.contains("<")) {
			text= LESS_THAN_PATTERN.matcher(text).replaceAll("&lt;");
		}
		if (text.contains(">")) {
			text = GREATER_THAN_PATTERN.matcher(text).replaceAll("&gt;");
		}

		return text;
	}
	private void indent(OutputStream out) throws IOException {
		int indentMax = containerDepth * indentStep;
		for (int i=0; i < indentMax; i++)
			out.write(SPACE);
	}
	private String makeQName(String prefix, String localPart) throws IOException {
		return (prefix != null) ? prefix+":"+localPart : localPart;
	}

}
