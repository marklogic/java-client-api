/*
 * Copyright (c) 2022 MarkLogic Corporation
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
