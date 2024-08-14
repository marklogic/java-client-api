/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.bitemporal;

import com.marklogic.client.document.DocumentDescriptor;
import jakarta.xml.bind.DatatypeConverter;

public interface TemporalDescriptor extends DocumentDescriptor {
  /**
   * Returns the URI identifier for the database document.
   * @return	the document URI
   */
  @Override
  String getUri();

  /**
   * Returns the temporal system time when the document was written or deleted.
   * The time is returned in ISO 8601 format like all MarkLogic timestamps.  It
   * can be parsed by
   * {@link DatatypeConverter#parseDateTime DatatypeConverter.parseDateTime}
   * but will lose precision since java.util.Calendar only supports millisecond
   * precision.
   * @return  the temporal system time
   */
  String getTemporalSystemTime();
}
