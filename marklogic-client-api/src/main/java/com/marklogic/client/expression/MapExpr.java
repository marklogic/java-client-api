/*
 * Copyright (c) 2024 MarkLogic Corporation
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

package com.marklogic.client.expression;

import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsUnsignedIntVal;

import com.marklogic.client.type.ServerExpression;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the map server library for a row
 * pipeline.
 */
public interface MapExpr {
    /**
  * Returns true if the key exists in the map.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:contains" target="mlserverdoc">map:contains</a> server function.
  * @param map  A map.  (of <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a>)
  * @param key  A key.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression contains(ServerExpression map, String key);
/**
  * Returns true if the key exists in the map.
  *
  * <a name="ml-server-type-contains"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:contains" target="mlserverdoc">map:contains</a> server function.
  * @param map  A map.  (of <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a>)
  * @param key  A key.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression contains(ServerExpression map, ServerExpression key);
/**
  * Returns the number of keys used in the map.
  *
  * <a name="ml-server-type-count"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:count" target="mlserverdoc">map:count</a> server function.
  * @param map  A map.  (of <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a> server data type
  */
  public ServerExpression count(ServerExpression map);
/**
  * Constructs a new map with a single entry consisting of the key and value specified as arguments. This is particularly helpful when used as part of an argument to map:new().
  *
  * <a name="ml-server-type-entry"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:entry" target="mlserverdoc">map:entry</a> server function.
  * @param key  The map key.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The map value.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a> server data type
  */
  public ServerExpression entry(ServerExpression key, ServerExpression value);
/**
  * Get a value from a map.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:get" target="mlserverdoc">map:get</a> server function.
  * @param map  A map.  (of <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a>)
  * @param key  A key.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression get(ServerExpression map, String key);
/**
  * Get a value from a map.
  *
  * <a name="ml-server-type-get"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:get" target="mlserverdoc">map:get</a> server function.
  * @param map  A map.  (of <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a>)
  * @param key  A key.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression get(ServerExpression map, ServerExpression key);
/**
  * Get the keys used in the map.
  *
  * <a name="ml-server-type-keys"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:keys" target="mlserverdoc">map:keys</a> server function.
  * @param map  A map.  (of <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression keys(ServerExpression map);
/**
  * Creates a map.
  *
  * <a name="ml-server-type-map"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:map" target="mlserverdoc">map:map</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a> server data type
  */
  public ServerExpression map();
/**
  * Creates a map.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:map" target="mlserverdoc">map:map</a> server function.
  * @param map  A serialized map element.  (of <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a> server data type
  */
  public ServerExpression map(ServerExpression map);
/**
  * Constructs a new map by combining the keys from the maps given as an argument. If a given key exists in more than one argument map, the value from the last such map is used.
  *
  * <a name="ml-server-type-new"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:new" target="mlserverdoc">map:new</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a> server data type
  */
  public ServerExpression newExpr();
/**
  * Constructs a new map by combining the keys from the maps given as an argument. If a given key exists in more than one argument map, the value from the last such map is used.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/map:new" target="mlserverdoc">map:new</a> server function.
  * @param maps  The argument maps.  (of <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a> server data type
  */
  public ServerExpression newExpr(ServerExpression maps);
}
