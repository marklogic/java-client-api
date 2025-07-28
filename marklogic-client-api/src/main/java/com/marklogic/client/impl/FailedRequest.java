/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.marklogic.client.io.Format;
import okhttp3.MediaType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.io.JSONErrorParser;

/**
 * Encapsulate data passed in an error response from a REST server instance
 *
 * This class wraps the following kind of XML structure, which is the standard
 * error payload for error conditions from the server:
 *
 * &lt;rapi:error&gt; &lt;rapi:status-code&gt;404&lt;/rapi:status-code&gt;
 * &lt;rapi:status&gt;NOT FOUND&lt;/rapi:status&gt;
 * &lt;rapi:message-code&gt;RESTAPI-NODOCUMENT&lt;/rapi:message-code&gt;
 * &lt;rapi:message&gt;RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document
 * does not exist: category: options message: Options configuration
 * &#39;photosd&#39; not found&lt;/rapi:message&gt; &lt;/rapi:error&gt;
 *
 */
public class FailedRequest {
  private static final String ERROR_NS = "http://marklogic.com/xdmp/error";

  private String messageCode;

  private String messageString;

  private int statusCode;

  private String statusString;

  private String stackTrace;

  private static class FailedRequestXMLParser implements FailedRequestParser {

    @Override
    public FailedRequest parseFailedRequest(int httpStatus, InputStream is) {
      FailedRequest failure = new FailedRequest();
      DocumentBuilderFactory factory = DocumentBuilderFactory
        .newInstance();
      factory.setNamespaceAware(true);
      factory.setValidating(false);
      DocumentBuilder builder;
      try {
        builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        String statusCode = null;
        NodeList statusCodes = doc.getElementsByTagNameNS(ERROR_NS, "status-code");
        if ( statusCodes != null && statusCodes.getLength() > 0 ) {
          statusCode = statusCodes.item(0).getTextContent();
        }
        int parsedStatusCode = Utilities.parseInt(statusCode);
        failure.setStatusCode((parsedStatusCode > 0) ? parsedStatusCode : httpStatus);
        NodeList statuses = doc.getElementsByTagNameNS(ERROR_NS, "status");
        if ( statuses != null && statuses.getLength() > 0 ) {
          failure.setStatusString( statuses.item(0).getTextContent() );
        }
        NodeList messageCodes = doc.getElementsByTagNameNS(ERROR_NS, "message-code");
        if ( messageCodes != null && messageCodes.getLength() > 0 ) {
          failure.setMessageCode( messageCodes.item(0).getTextContent() );
        }
        // the following is for eval errors
        String formatString = null;
        NodeList formatStrings = doc.getElementsByTagNameNS(ERROR_NS, "format-string");
        if ( formatStrings != null && formatStrings.getLength() > 0 ) {
          formatString = formatStrings.item(0).getTextContent();
        }
        if ( formatString != null ) {
          failure.setMessageString(formatString);
        } else {
          NodeList messageStrings = doc.getElementsByTagNameNS(ERROR_NS, "message");
          if ( messageStrings != null && messageStrings.getLength() > 0 ) {
            failure.setMessageString( messageStrings.item(0).getTextContent() );
          }
        }
      } catch (ParserConfigurationException e) {
        failure.setStatusCode(httpStatus);
        failure.setMessageString("Request failed. Unable to parse server error.");
      } catch (SAXException e) {
        failure.setStatusCode(httpStatus);
        failure.setMessageString("Request failed. Unable to parse server error details");
      } catch (IOException e) {
        failure.setStatusCode(httpStatus);
        failure.setMessageString("Request failed. Error body not received from server");
      }
      return failure;
    }

  }

  /*
   * send an InputStream to this handler in order to create an error block.
   */
  public static FailedRequest getFailedRequest(int httpStatus, String contentType, InputStream content) {
    FailedRequest failure = null;
    if (contentType != null) {
      Format format = Format.getFromMimetype(contentType);
      switch(format) {
      case XML:
        failure = xmlFailedRequest(httpStatus, content);
        break;
      case JSON:
        failure = jsonFailedRequest(httpStatus, content);
        break;
      }
    } else if (httpStatus == 404) {
      failure = new FailedRequest();
      failure.setStatusCode(httpStatus);
      failure.setMessageString("");
      failure.setStatusString("Not Found");
    }
    if (failure == null) {
      failure = new FailedRequest();
      failure.setStatusCode(httpStatus);
      failure.setMessageCode("UNKNOWN");
      failure.setMessageString("Server (not a REST instance?) did not respond with an expected REST Error message.");
      failure.setStatusString("UNKNOWN");
    }
    if (failure.getStatusCode() == 401) {
      failure.setMessageString("Unauthorized");
      failure.setStatusString("Failed Auth");
    }
    return failure;
  }

  private static FailedRequest xmlFailedRequest(int httpStatus, InputStream content) {
    return new FailedRequestXMLParser().parseFailedRequest(httpStatus, content);
  }
  private static FailedRequest jsonFailedRequest(int httpStatus, InputStream content) {
    return new JSONErrorParser().parseFailedRequest(httpStatus, content);
  }

  /**
   * No argument constructor so an external class can generate FailedRequest objects.
   */
  public FailedRequest() {
  }

  public FailedRequest(int statusCode, String messageString) {
	  this.statusCode = statusCode;
	  this.messageString = messageString;
  }

  public String getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(String stackTrace) {
    this.stackTrace = stackTrace;
  }

  public String getMessage() {
    return messageString;
  }

  public String getMessageCode() {
    return messageCode;
  }

  public String getStatus() {
    return statusString;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setMessageCode(String messageCode) {
    this.messageCode = messageCode;
  }

  public void setMessageString(String messageString) {
    this.messageString = messageString;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public void setStatusString(String statusString) {
    this.statusString = statusString;
  }
}
