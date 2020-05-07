/*
 * Copyright (c) 2019 MarkLogic Corporation
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
package com.marklogic.client.io.marker;

/**
 * A Content Handle provides get / set access to a representation
 * of content such a byte[] array, input stream, file, reader, string,
 * and so on.
 */
public interface ContentHandle<C>
  extends AbstractReadHandle, AbstractWriteHandle {
  /**
   * Returns the content.
   * @return	the content
   */
  C get();
  /**
   * Assigns the content.
   * @param content	the content
   */
  void set(C content);
  /**
   * Returns the class of the handled content, which may be a base
   * class of the actual class of a content object.
   *
   * Note that implementations should override the default method,
   * which returns null if the handle doesn't have any content.
   *
   * @return  the class for the handled content
   */
  default Class<C> getContentClass() {
    C value = get();
    if (value != null)
      return (Class<C>) value.getClass();
    return null;
  }
}
