/*
 * Copyright 2015 MarkLogic Corporation
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
 * A ContentHandleFactory creates instances of a ContentHandle that provides
 * an adapter for an IO representation.
 * @see ContentHandle
 */
public interface ContentHandleFactory {
	/**
	 * Returns classes that the handle always supports;
	 * @return	the classes if any
	 */
	public Class<?>[] getHandledClasses();
	/**
	 * Returns true if the factory can create a handle for instances
	 * of the IO class.
	 * @param type	the class of the IO representation
	 * @return	whether the factory can create a handle
	 */
	public boolean isHandled(Class<?> type);
	/**
	 * Instantiates a handle for an IO class recognized by the factory.
	 * @param type	the class of the IO representation
	 * @return	the handle or null if the class is unrecognized
	 */
	public <C> ContentHandle<C> newHandle(Class<C> type);
}
