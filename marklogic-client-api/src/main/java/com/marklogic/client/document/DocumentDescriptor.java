/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.io.Format;

/**
 * A Document Descriptor describes a database document. If content versioning is enabled on the app server used
 * to retrieve a document via an instance of this class, note that you may receive a null return value if the
 * corresponding document has not been modified. 
 */
public interface DocumentDescriptor extends ContentDescriptor {
  /**
   * Indicates that the version of the database document is not known.
   */
  long UNKNOWN_VERSION = -1;

  /**
   * Returns the URI identifier for the database document.
   * @return	the document URI
   */
  String getUri();
  /**
   * Specifies the URI identifier for a database document.
   * @param uri	the document URI
   */
  void setUri(String uri);

  /**
   * Specifies the format for a database document and
   * returns the descriptor object
   * @param format	the document format
   * @return	the descriptor object
   */
  DocumentDescriptor withFormat(Format format);

  /**
   * Returns the version for the database document.  Each update
   * creates a new version of a document.  Version numbering can be
   * used to refresh a client document cache or for optimistic locking.
   * Use {@link com.marklogic.client.admin.ServerConfigurationManager}
   * to enable versioning on content.
   * @return	the document version number
   */
  long getVersion();
  /**
   * Specifies the document version.  Checking the existence
   * of a document or reading a document specifies the document version
   * if you have enabled versioning on content.
   * @param version	the document version number
   */
  void setVersion(long version);
}
