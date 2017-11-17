/*
 * Copyright 2016-2017 MarkLogic Corporation
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

import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsUnsignedIntExpr;

import com.marklogic.client.type.MapMapExpr;
import com.marklogic.client.type.MapMapSeqExpr;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the map server library for a row
 * pipeline.
 */
public interface MapExpr {
    /**
  * Returns true if the key exists in the map.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/map:contains" target="mlserverdoc">map:contains</a>
  * @param map  A map.
  * @param key  A key.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr contains(MapMapExpr map, String key);
/**
  * Returns true if the key exists in the map.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/map:contains" target="mlserverdoc">map:contains</a>
  * @param map  A map.
  * @param key  A key.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr contains(MapMapExpr map, XsStringExpr key);
/**
  * Returns the number of keys used in the map.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/map:count" target="mlserverdoc">map:count</a>
  * @param map  A map.
  * @return  a XsUnsignedIntExpr expression
  */
  public XsUnsignedIntExpr count(MapMapExpr map);
/**
  * Constructs a new map with a single entry consisting of the key and value specified as arguments. This is particularly helpful when used as part of an argument to map:new().
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/map:entry" target="mlserverdoc">map:entry</a>
  * @param key  The map key.
  * @param value  The map value.
  * @return  a MapMapExpr expression
  */
  public MapMapExpr entry(XsStringExpr key, ItemSeqExpr value);
/**
  * Get a value from a map.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/map:get" target="mlserverdoc">map:get</a>
  * @param map  A map.
  * @param key  A key.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr get(MapMapExpr map, String key);
/**
  * Get a value from a map.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/map:get" target="mlserverdoc">map:get</a>
  * @param map  A map.
  * @param key  A key.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr get(MapMapExpr map, XsStringExpr key);
/**
  * Get the keys used in the map.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/map:keys" target="mlserverdoc">map:keys</a>
  * @param map  A map.
  * @return  a XsStringSeqExpr expression sequence
  */
  public XsStringSeqExpr keys(MapMapExpr map);
/**
  * Creates a map.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/map:map" target="mlserverdoc">map:map</a>
  * @return  a MapMapExpr expression
  */
  public MapMapExpr map();
/**
  * Creates a map.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/map:map" target="mlserverdoc">map:map</a>
  * @param map  A serialized map element.
  * @return  a MapMapExpr expression
  */
  public MapMapExpr map(ElementNodeExpr map);
/**
  * Constructs a sequence of MapMapExpr items.
  * @param items  the MapMapExpr items collected by the sequence
  * @return  a MapMapSeqExpr sequence
  */
  public MapMapSeqExpr mapSeq(MapMapExpr... items);

}
