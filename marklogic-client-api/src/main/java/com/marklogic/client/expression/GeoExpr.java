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

import com.marklogic.client.type.CtsBoxExpr;
import com.marklogic.client.type.CtsBoxSeqExpr;
import com.marklogic.client.type.CtsCircleExpr;
import com.marklogic.client.type.CtsPointExpr;
import com.marklogic.client.type.CtsPointSeqExpr;
import com.marklogic.client.type.CtsPolygonExpr;
import com.marklogic.client.type.CtsRegionExpr;
import com.marklogic.client.type.CtsRegionSeqExpr;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsDoubleSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.ServerExpression;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the geo server library for a row
 * pipeline.
 */
public interface GeoExpr {
    /**
  * Return a point approximating the center of the given region. For a point, this is the point itself. For a circle, it is the center point. For a box, it is the point whose latitude is half-way between the northern and southern limits and whose longitude is half-way between the western and eastern limits. For polygons, complex polygons, and linestrings, an approximate centroid is returned. This approximation is rough, and useful for quick comparisons.
  *
  * <a name="ml-server-type-approx-center"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:approx-center" target="mlserverdoc">geo:approx-center</a> server function.
  * @param region  A geospatial region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression approxCenter(ServerExpression region);
/**
  * Return a point approximating the center of the given region. For a point, this is the point itself. For a circle, it is the center point. For a box, it is the point whose latitude is half-way between the northern and southern limits and whose longitude is half-way between the western and eastern limits. For polygons, complex polygons, and linestrings, an approximate centroid is returned. This approximation is rough, and useful for quick comparisons.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:approx-center" target="mlserverdoc">geo:approx-center</a> server function.
  * @param region  A geospatial region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options. The default is ().  Options include:  "box-percent=n" An integer between 0 and 100 (default is 100) that indicates what percentage of a polygon's bounding box slivers should be used in constructing the approximate centroid. Lower numbers use fewer slivers, giving faster but less accurate results; larger numbers use more slivers, giving slower but more accurate results.  "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" The precision use for this operation, including the interpretation of input values. Allowed values: float, double. Default: The precision of the governing coordinate system.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression approxCenter(ServerExpression region, String options);
/**
  * Return a point approximating the center of the given region. For a point, this is the point itself. For a circle, it is the center point. For a box, it is the point whose latitude is half-way between the northern and southern limits and whose longitude is half-way between the western and eastern limits. For polygons, complex polygons, and linestrings, an approximate centroid is returned. This approximation is rough, and useful for quick comparisons.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:approx-center" target="mlserverdoc">geo:approx-center</a> server function.
  * @param region  A geospatial region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options. The default is ().  Options include:  "box-percent=n" An integer between 0 and 100 (default is 100) that indicates what percentage of a polygon's bounding box slivers should be used in constructing the approximate centroid. Lower numbers use fewer slivers, giving faster but less accurate results; larger numbers use more slivers, giving slower but more accurate results.  "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" The precision use for this operation, including the interpretation of input values. Allowed values: float, double. Default: The precision of the governing coordinate system.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression approxCenter(ServerExpression region, ServerExpression options);
/**
  * Returns the point at the intersection of two arcs. If the arcs do not intersect, or lie on the same great circle, or if either arc covers more than 180 degrees, an error is raised.
  *
  * <a name="ml-server-type-arc-intersection"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:arc-intersection" target="mlserverdoc">geo:arc-intersection</a> server function.
  * @param p1  The starting point of the first arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param p2  The ending point of the first arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param q1  The starting point of the second arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param q2  The ending point of the second arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression arcIntersection(ServerExpression p1, ServerExpression p2, ServerExpression q1, ServerExpression q2);
/**
  * Returns the point at the intersection of two arcs. If the arcs do not intersect, or lie on the same great circle, or if either arc covers more than 180 degrees, an error is raised.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:arc-intersection" target="mlserverdoc">geo:arc-intersection</a> server function.
  * @param p1  The starting point of the first arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param p2  The ending point of the first arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param q1  The starting point of the second arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param q2  The ending point of the second arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance and the radii of circles in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression arcIntersection(ServerExpression p1, ServerExpression p2, ServerExpression q1, ServerExpression q2, String options);
/**
  * Returns the point at the intersection of two arcs. If the arcs do not intersect, or lie on the same great circle, or if either arc covers more than 180 degrees, an error is raised.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:arc-intersection" target="mlserverdoc">geo:arc-intersection</a> server function.
  * @param p1  The starting point of the first arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param p2  The ending point of the first arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param q1  The starting point of the second arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param q2  The ending point of the second arc.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance and the radii of circles in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression arcIntersection(ServerExpression p1, ServerExpression p2, ServerExpression q1, ServerExpression q2, ServerExpression options);
/**
  * Returns the true bearing in radians of the path from the first point to the second. An error is raised if the two points are the same.
  *
  * <a name="ml-server-type-bearing"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:bearing" target="mlserverdoc">geo:bearing</a> server function.
  * @param p1  The first point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param p2  The second point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression bearing(ServerExpression p1, ServerExpression p2);
/**
  * Returns the true bearing in radians of the path from the first point to the second. An error is raised if the two points are the same.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:bearing" target="mlserverdoc">geo:bearing</a> server function.
  * @param p1  The first point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param p2  The second point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double.  "units=value" Unit of measure of the tolerance value. Valid values are miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression bearing(ServerExpression p1, ServerExpression p2, String options);
/**
  * Returns the true bearing in radians of the path from the first point to the second. An error is raised if the two points are the same.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:bearing" target="mlserverdoc">geo:bearing</a> server function.
  * @param p1  The first point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param p2  The second point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double.  "units=value" Unit of measure of the tolerance value. Valid values are miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression bearing(ServerExpression p1, ServerExpression p2, ServerExpression options);
/**
  * Returns a sequence of boxes that bound the given region.
  *
  * <a name="ml-server-type-bounding-boxes"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:bounding-boxes" target="mlserverdoc">geo:bounding-boxes</a> server function.
  * @param region  A geographic region (box, circle, polygon, or point).  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_box.html">cts:box</a> server data type
  */
  public ServerExpression boundingBoxes(ServerExpression region);
/**
  * Returns a sequence of boxes that bound the given region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:bounding-boxes" target="mlserverdoc">geo:bounding-boxes</a> server function.
  * @param region  A geographic region (box, circle, polygon, or point).  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include: Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "box-percent=n" An integer between 0 and 100 (default is 100) that indicates what percentage of a polygon's bounding box slivers should be returned. Lower numbers give fewer, less accurate boxes; larger numbers give more, more accurate boxes. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. The bounding boxes will be padded to cover any points within tolerance of the region. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. The default value is 0 (no padding). "boundaries-included" Points on boxes', circles', and polygons' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on boxes', circles', and polygons' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_box.html">cts:box</a> server data type
  */
  public ServerExpression boundingBoxes(ServerExpression region, String options);
/**
  * Returns a sequence of boxes that bound the given region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:bounding-boxes" target="mlserverdoc">geo:bounding-boxes</a> server function.
  * @param region  A geographic region (box, circle, polygon, or point).  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include: Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "box-percent=n" An integer between 0 and 100 (default is 100) that indicates what percentage of a polygon's bounding box slivers should be returned. Lower numbers give fewer, less accurate boxes; larger numbers give more, more accurate boxes. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. The bounding boxes will be padded to cover any points within tolerance of the region. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. The default value is 0 (no padding). "boundaries-included" Points on boxes', circles', and polygons' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on boxes', circles', and polygons' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_box.html">cts:box</a> server data type
  */
  public ServerExpression boundingBoxes(ServerExpression region, ServerExpression options);
/**
  * Returns true if the box intersects with a region.
  *
  * <a name="ml-server-type-box-intersects"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:box-intersects" target="mlserverdoc">geo:box-intersects</a> server function.
  * @param box  A geographic box.  (of <a href="{@docRoot}/doc-files/types/cts_box.html">cts:box</a>)
  * @param region  One or more geographic regions (boxes, circles, polygons, or points). Where multiple regions are specified, return true if any region intersects the box.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression boxIntersects(ServerExpression box, ServerExpression region);
/**
  * Returns true if the box intersects with a region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:box-intersects" target="mlserverdoc">geo:box-intersects</a> server function.
  * @param box  A geographic box.  (of <a href="{@docRoot}/doc-files/types/cts_box.html">cts:box</a>)
  * @param region  One or more geographic regions (boxes, circles, polygons, or points). Where multiple regions are specified, return true if any region intersects the box.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include: Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. "boundaries-included" Points on boxes', circles', and polygons' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on boxes', circles', and polygons' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression boxIntersects(ServerExpression box, ServerExpression region, String options);
/**
  * Returns true if the box intersects with a region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:box-intersects" target="mlserverdoc">geo:box-intersects</a> server function.
  * @param box  A geographic box.  (of <a href="{@docRoot}/doc-files/types/cts_box.html">cts:box</a>)
  * @param region  One or more geographic regions (boxes, circles, polygons, or points). Where multiple regions are specified, return true if any region intersects the box.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include: Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. "boundaries-included" Points on boxes', circles', and polygons' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on boxes', circles', and polygons' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression boxIntersects(ServerExpression box, ServerExpression region, ServerExpression options);
/**
  * Returns true if the circle intersects with a region.
  *
  * <a name="ml-server-type-circle-intersects"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:circle-intersects" target="mlserverdoc">geo:circle-intersects</a> server function.
  * @param circle  A geographic circle.  (of <a href="{@docRoot}/doc-files/types/cts_circle.html">cts:circle</a>)
  * @param region  One or more geographic regions (boxes, circles, polygons, or points). Where multiple regions are specified, return true if any region intersects the target circle.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression circleIntersects(ServerExpression circle, ServerExpression region);
/**
  * Returns true if the circle intersects with a region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:circle-intersects" target="mlserverdoc">geo:circle-intersects</a> server function.
  * @param circle  A geographic circle.  (of <a href="{@docRoot}/doc-files/types/cts_circle.html">cts:circle</a>)
  * @param region  One or more geographic regions (boxes, circles, polygons, or points). Where multiple regions are specified, return true if any region intersects the target circle.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. "boundaries-included" Points on boxes', circles', and polygons' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on boxes', circles', and polygons' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression circleIntersects(ServerExpression circle, ServerExpression region, String options);
/**
  * Returns true if the circle intersects with a region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:circle-intersects" target="mlserverdoc">geo:circle-intersects</a> server function.
  * @param circle  A geographic circle.  (of <a href="{@docRoot}/doc-files/types/cts_circle.html">cts:circle</a>)
  * @param region  One or more geographic regions (boxes, circles, polygons, or points). Where multiple regions are specified, return true if any region intersects the target circle.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. "boundaries-included" Points on boxes', circles', and polygons' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on boxes', circles', and polygons' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression circleIntersects(ServerExpression circle, ServerExpression region, ServerExpression options);
/**
  * Construct a polygon approximating a circle.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:circle-polygon" target="mlserverdoc">geo:circle-polygon</a> server function.
  * @param circle  A cts circle that defines the circle to be approximated.  (of <a href="{@docRoot}/doc-files/types/cts_circle.html">cts:circle</a>)
  * @param arcTolerance  How far the approximation can be from the actual circle, specified in the same units as the units option. Arc-tolerance should be greater than the value of the tolerance option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_polygon.html">cts:polygon</a> server data type
  */
  public ServerExpression circlePolygon(ServerExpression circle, double arcTolerance);
/**
  * Construct a polygon approximating a circle.
  *
  * <a name="ml-server-type-circle-polygon"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:circle-polygon" target="mlserverdoc">geo:circle-polygon</a> server function.
  * @param circle  A cts circle that defines the circle to be approximated.  (of <a href="{@docRoot}/doc-files/types/cts_circle.html">cts:circle</a>)
  * @param arcTolerance  How far the approximation can be from the actual circle, specified in the same units as the units option. Arc-tolerance should be greater than the value of the tolerance option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_polygon.html">cts:polygon</a> server data type
  */
  public ServerExpression circlePolygon(ServerExpression circle, ServerExpression arcTolerance);
/**
  * Construct a polygon approximating a circle.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:circle-polygon" target="mlserverdoc">geo:circle-polygon</a> server function.
  * @param circle  A cts circle that defines the circle to be approximated.  (of <a href="{@docRoot}/doc-files/types/cts_circle.html">cts:circle</a>)
  * @param arcTolerance  How far the approximation can be from the actual circle, specified in the same units as the units option. Arc-tolerance should be greater than the value of the tolerance option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param options  Options with which you can customize this operation. The following options are available:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. Tolerance should be smaller than the value of the arc-tolerance parameter.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_polygon.html">cts:polygon</a> server data type
  */
  public ServerExpression circlePolygon(ServerExpression circle, double arcTolerance, String options);
/**
  * Construct a polygon approximating a circle.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:circle-polygon" target="mlserverdoc">geo:circle-polygon</a> server function.
  * @param circle  A cts circle that defines the circle to be approximated.  (of <a href="{@docRoot}/doc-files/types/cts_circle.html">cts:circle</a>)
  * @param arcTolerance  How far the approximation can be from the actual circle, specified in the same units as the units option. Arc-tolerance should be greater than the value of the tolerance option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param options  Options with which you can customize this operation. The following options are available:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. Tolerance should be smaller than the value of the arc-tolerance parameter.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_polygon.html">cts:polygon</a> server data type
  */
  public ServerExpression circlePolygon(ServerExpression circle, ServerExpression arcTolerance, ServerExpression options);
/**
  * Compares geospatial regions to see if they fulfill the 'contains' DE-9IM relation.
  *
  * <a name="ml-server-type-contains"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:contains" target="mlserverdoc">geo:contains</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of contains.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of contains.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression contains(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'contains' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:contains" target="mlserverdoc">geo:contains</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of contains.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of contains.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression contains(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'contains' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:contains" target="mlserverdoc">geo:contains</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of contains.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of contains.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression contains(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Return a count of the distinct number of vertices in a region, taking tolerance into account.
  *
  * <a name="ml-server-type-count-distinct-vertices"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:count-distinct-vertices" target="mlserverdoc">geo:count-distinct-vertices</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression countDistinctVertices(ServerExpression region);
/**
  * Return a count of the distinct number of vertices in a region, taking tolerance into account.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:count-distinct-vertices" target="mlserverdoc">geo:count-distinct-vertices</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression countDistinctVertices(ServerExpression region, String options);
/**
  * Return a count of the distinct number of vertices in a region, taking tolerance into account.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:count-distinct-vertices" target="mlserverdoc">geo:count-distinct-vertices</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression countDistinctVertices(ServerExpression region, ServerExpression options);
/**
  * This function returns a count of the number of vertices in a region.
  *
  * <a name="ml-server-type-count-vertices"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:count-vertices" target="mlserverdoc">geo:count-vertices</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression countVertices(ServerExpression region);
/**
  * Compares geospatial regions to see if they fulfill the 'covered by' DE-9IM relation.
  *
  * <a name="ml-server-type-covered-by"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:covered-by" target="mlserverdoc">geo:covered-by</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of covered-by.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of covered-by.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression coveredBy(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'covered by' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:covered-by" target="mlserverdoc">geo:covered-by</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of covered-by.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of covered-by.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression coveredBy(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'covered by' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:covered-by" target="mlserverdoc">geo:covered-by</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of covered-by.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of covered-by.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression coveredBy(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Compares geospatial regions to see if they fulfill the 'covers' DE-9IM relation.
  *
  * <a name="ml-server-type-covers"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:covers" target="mlserverdoc">geo:covers</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of covers.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of covers.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression covers(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'covers' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:covers" target="mlserverdoc">geo:covers</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of covers.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of covers.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression covers(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'covers' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:covers" target="mlserverdoc">geo:covers</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of covers.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of covers.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression covers(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Compares geospatial regions to see if they fulfill the 'crosses' DE-9IM relation.
  *
  * <a name="ml-server-type-crosses"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:crosses" target="mlserverdoc">geo:crosses</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of crosses.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of crosses.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression crosses(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'crosses' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:crosses" target="mlserverdoc">geo:crosses</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of crosses.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of crosses.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression crosses(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'crosses' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:crosses" target="mlserverdoc">geo:crosses</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of crosses.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of crosses.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression crosses(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Returns the point at the given distance (in units) along the given bearing (in radians) from the starting point.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:destination" target="mlserverdoc">geo:destination</a> server function.
  * @param p  The starting point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param bearing  The bearing, in radians.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param distance  The distance, in units. See the units option, below.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression destination(ServerExpression p, double bearing, double distance);
/**
  * Returns the point at the given distance (in units) along the given bearing (in radians) from the starting point.
  *
  * <a name="ml-server-type-destination"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:destination" target="mlserverdoc">geo:destination</a> server function.
  * @param p  The starting point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param bearing  The bearing, in radians.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param distance  The distance, in units. See the units option, below.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression destination(ServerExpression p, ServerExpression bearing, ServerExpression distance);
/**
  * Returns the point at the given distance (in units) along the given bearing (in radians) from the starting point.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:destination" target="mlserverdoc">geo:destination</a> server function.
  * @param p  The starting point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param bearing  The bearing, in radians.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param distance  The distance, in units. See the units option, below.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance and the radii of circles in the specified units. Allowed values: miles (default), km, feet, meters.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression destination(ServerExpression p, double bearing, double distance, String options);
/**
  * Returns the point at the given distance (in units) along the given bearing (in radians) from the starting point.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:destination" target="mlserverdoc">geo:destination</a> server function.
  * @param p  The starting point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param bearing  The bearing, in radians.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param distance  The distance, in units. See the units option, below.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance and the radii of circles in the specified units. Allowed values: miles (default), km, feet, meters.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression destination(ServerExpression p, ServerExpression bearing, ServerExpression distance, ServerExpression options);
/**
  * Compares geospatial regions to see if they fulfill the 'disjoint' DE-9IM relation.
  *
  * <a name="ml-server-type-disjoint"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:disjoint" target="mlserverdoc">geo:disjoint</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of disjoint.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of disjoint.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression disjoint(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'disjoint' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:disjoint" target="mlserverdoc">geo:disjoint</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of disjoint.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of disjoint.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression disjoint(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'disjoint' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:disjoint" target="mlserverdoc">geo:disjoint</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of disjoint.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of disjoint.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression disjoint(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Returns the distance (in units) between two points.
  *
  * <a name="ml-server-type-distance"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:distance" target="mlserverdoc">geo:distance</a> server function.
  * @param p1  The first point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param p2  The second point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression distance(ServerExpression p1, ServerExpression p2);
/**
  * Returns the distance (in units) between two points.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:distance" target="mlserverdoc">geo:distance</a> server function.
  * @param p1  The first point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param p2  The second point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance and the radii of circles in the specified units. Allowed values: miles (default), km, feet, meters.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression distance(ServerExpression p1, ServerExpression p2, String options);
/**
  * Returns the distance (in units) between two points.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:distance" target="mlserverdoc">geo:distance</a> server function.
  * @param p1  The first point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param p2  The second point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance and the radii of circles in the specified units. Allowed values: miles (default), km, feet, meters.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression distance(ServerExpression p1, ServerExpression p2, ServerExpression options);
/**
  * This function converts a distance from one unit of measure to another. The supported units are "miles", "feet", "km", and "meters". This is a proper superset of the units supported as options to various geospatial functions ("miles","km").
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:distance-convert" target="mlserverdoc">geo:distance-convert</a> server function.
  * @param distance  The distance.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param unit1  The unit of the input distance parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param unit2  The unit to which the distance should be converted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression distanceConvert(ServerExpression distance, String unit1, String unit2);
/**
  * This function converts a distance from one unit of measure to another. The supported units are "miles", "feet", "km", and "meters". This is a proper superset of the units supported as options to various geospatial functions ("miles","km").
  *
  * <a name="ml-server-type-distance-convert"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:distance-convert" target="mlserverdoc">geo:distance-convert</a> server function.
  * @param distance  The distance.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param unit1  The unit of the input distance parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param unit2  The unit to which the distance should be converted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression distanceConvert(ServerExpression distance, ServerExpression unit1, ServerExpression unit2);
/**
  * Construct a polygon approximating an ellipse.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:ellipse-polygon" target="mlserverdoc">geo:ellipse-polygon</a> server function.
  * @param center  Center of the ellipse.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param semiMajorAxis  The semi major axis of the ellipse. The units are governed by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param semiMinorAxis  The semi minor axis of the ellipse. The units are governed by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param azimuth  The azimuth.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param arcTolerance  How far the approximation can be from the actual ellipse, specified in the same units as the units option. Arc-tolerance should be greater than the value of the tolerance option, which defaults to 0.05km (0.3106856 miles).  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_polygon.html">cts:polygon</a> server data type
  */
  public ServerExpression ellipsePolygon(ServerExpression center, double semiMajorAxis, double semiMinorAxis, double azimuth, double arcTolerance);
/**
  * Construct a polygon approximating an ellipse.
  *
  * <a name="ml-server-type-ellipse-polygon"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:ellipse-polygon" target="mlserverdoc">geo:ellipse-polygon</a> server function.
  * @param center  Center of the ellipse.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param semiMajorAxis  The semi major axis of the ellipse. The units are governed by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param semiMinorAxis  The semi minor axis of the ellipse. The units are governed by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param azimuth  The azimuth.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param arcTolerance  How far the approximation can be from the actual ellipse, specified in the same units as the units option. Arc-tolerance should be greater than the value of the tolerance option, which defaults to 0.05km (0.3106856 miles).  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_polygon.html">cts:polygon</a> server data type
  */
  public ServerExpression ellipsePolygon(ServerExpression center, ServerExpression semiMajorAxis, ServerExpression semiMinorAxis, ServerExpression azimuth, ServerExpression arcTolerance);
/**
  * Construct a polygon approximating an ellipse.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:ellipse-polygon" target="mlserverdoc">geo:ellipse-polygon</a> server function.
  * @param center  Center of the ellipse.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param semiMajorAxis  The semi major axis of the ellipse. The units are governed by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param semiMinorAxis  The semi minor axis of the ellipse. The units are governed by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param azimuth  The azimuth.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param arcTolerance  How far the approximation can be from the actual ellipse, specified in the same units as the units option. Arc-tolerance should be greater than the value of the tolerance option, which defaults to 0.05km (0.3106856 miles).  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param options  Options with which to configure the behavior. Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, the axes of the ellipse, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_polygon.html">cts:polygon</a> server data type
  */
  public ServerExpression ellipsePolygon(ServerExpression center, double semiMajorAxis, double semiMinorAxis, double azimuth, double arcTolerance, String options);
/**
  * Construct a polygon approximating an ellipse.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:ellipse-polygon" target="mlserverdoc">geo:ellipse-polygon</a> server function.
  * @param center  Center of the ellipse.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param semiMajorAxis  The semi major axis of the ellipse. The units are governed by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param semiMinorAxis  The semi minor axis of the ellipse. The units are governed by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param azimuth  The azimuth.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param arcTolerance  How far the approximation can be from the actual ellipse, specified in the same units as the units option. Arc-tolerance should be greater than the value of the tolerance option, which defaults to 0.05km (0.3106856 miles).  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param options  Options with which to configure the behavior. Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, the axes of the ellipse, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_polygon.html">cts:polygon</a> server data type
  */
  public ServerExpression ellipsePolygon(ServerExpression center, ServerExpression semiMajorAxis, ServerExpression semiMinorAxis, ServerExpression azimuth, ServerExpression arcTolerance, ServerExpression options);
/**
  * Compares geospatial regions to see if they fulfill the 'equals' DE-9IM relation.
  *
  * <a name="ml-server-type-equals"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:equals" target="mlserverdoc">geo:equals</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of equals.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of equals.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression equals(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'equals' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:equals" target="mlserverdoc">geo:equals</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of equals.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of equals.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression equals(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'equals' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:equals" target="mlserverdoc">geo:equals</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of equals.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of equals.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression equals(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Given a geohash string, return the bounding box for that hash.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-decode" target="mlserverdoc">geo:geohash-decode</a> server function.
  * @param hash  The geohash value, as produced by geo:geohash-encode.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_box.html">cts:box</a> server data type
  */
  public ServerExpression geohashDecode(String hash);
/**
  * Given a geohash string, return the bounding box for that hash.
  *
  * <a name="ml-server-type-geohash-decode"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-decode" target="mlserverdoc">geo:geohash-decode</a> server function.
  * @param hash  The geohash value, as produced by geo:geohash-encode.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_box.html">cts:box</a> server data type
  */
  public ServerExpression geohashDecode(ServerExpression hash);
/**
  * Given a geohash string, return the point for that hash.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-decode-point" target="mlserverdoc">geo:geohash-decode-point</a> server function.
  * @param hash  The geohash string, as produced from the function geo:geohash-encode.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression geohashDecodePoint(String hash);
/**
  * Given a geohash string, return the point for that hash.
  *
  * <a name="ml-server-type-geohash-decode-point"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-decode-point" target="mlserverdoc">geo:geohash-decode-point</a> server function.
  * @param hash  The geohash string, as produced from the function geo:geohash-encode.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression geohashDecodePoint(ServerExpression hash);
/**
  * Compute a set of covering geohashes for the given region, to the given level of precision.
  *
  * <a name="ml-server-type-geohash-encode"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-encode" target="mlserverdoc">geo:geohash-encode</a> server function.
  * @param region  The region to encode.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression geohashEncode(ServerExpression region);
/**
  * Compute a set of covering geohashes for the given region, to the given level of precision.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-encode" target="mlserverdoc">geo:geohash-encode</a> server function.
  * @param region  The region to encode.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param geohashPrecision  The desired precision (length of the geohash). The precision should be between 1 and 12. If the precision is less than 1, or unspecified, the default geohash-precision of 6 is used. A geohash-precision greater than 12 is treated as the same as 12. In the worst case (at the equator) a precision of 12 gives resolution of less than a centimeter.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression geohashEncode(ServerExpression region, long geohashPrecision);
/**
  * Compute a set of covering geohashes for the given region, to the given level of precision.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-encode" target="mlserverdoc">geo:geohash-encode</a> server function.
  * @param region  The region to encode.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param geohashPrecision  The desired precision (length of the geohash). The precision should be between 1 and 12. If the precision is less than 1, or unspecified, the default geohash-precision of 6 is used. A geohash-precision greater than 12 is treated as the same as 12. In the worst case (at the equator) a precision of 12 gives resolution of less than a centimeter.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression geohashEncode(ServerExpression region, ServerExpression geohashPrecision);
/**
  * Compute a set of covering geohashes for the given region, to the given level of precision.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-encode" target="mlserverdoc">geo:geohash-encode</a> server function.
  * @param region  The region to encode.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param geohashPrecision  The desired precision (length of the geohash). The precision should be between 1 and 12. If the precision is less than 1, or unspecified, the default geohash-precision of 6 is used. A geohash-precision greater than 12 is treated as the same as 12. In the worst case (at the equator) a precision of 12 gives resolution of less than a centimeter.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision.  Geohashing is not allowed for non-geodetic coordinate systems. Attempting to use this function with the raw or raw/double coordinate system will result in an XDMP-GEOHASH-COORD error.  "precision=string" Use the coordinate system at the given precision. Allowed values: float (default) and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. geohashes=value Specify which geohashes to return. Allowed values:  all Return a complete set of covering hashes for the region (boundary + interior). This is the default behavior. boundary Return only geohashes that intersect with the boundary of the region. interior Return only geohashes completely contained in the interior of the region. exterior Return all geohashes disjoint from the region. That is, all geohashes completely contained in the exterior of the region.     (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression geohashEncode(ServerExpression region, long geohashPrecision, String options);
/**
  * Compute a set of covering geohashes for the given region, to the given level of precision.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-encode" target="mlserverdoc">geo:geohash-encode</a> server function.
  * @param region  The region to encode.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param geohashPrecision  The desired precision (length of the geohash). The precision should be between 1 and 12. If the precision is less than 1, or unspecified, the default geohash-precision of 6 is used. A geohash-precision greater than 12 is treated as the same as 12. In the worst case (at the equator) a precision of 12 gives resolution of less than a centimeter.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision.  Geohashing is not allowed for non-geodetic coordinate systems. Attempting to use this function with the raw or raw/double coordinate system will result in an XDMP-GEOHASH-COORD error.  "precision=string" Use the coordinate system at the given precision. Allowed values: float (default) and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. geohashes=value Specify which geohashes to return. Allowed values:  all Return a complete set of covering hashes for the region (boundary + interior). This is the default behavior. boundary Return only geohashes that intersect with the boundary of the region. interior Return only geohashes completely contained in the interior of the region. exterior Return all geohashes disjoint from the region. That is, all geohashes completely contained in the exterior of the region.     (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression geohashEncode(ServerExpression region, ServerExpression geohashPrecision, ServerExpression options);
/**
  * Given a geohash string, return hashes for the neighbors. The result is a map with the keys "N", "NE", "E", "SE", "S", "SW", "W", "NW" for the neighbors in those directions.
  *
  * <a name="ml-server-type-geohash-neighbors"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-neighbors" target="mlserverdoc">geo:geohash-neighbors</a> server function.
  * @param hash  The geohash string, as produced by geo:geohash-encode.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a> server data type
  */
  public ServerExpression geohashNeighbors(ServerExpression hash);
/**
  * Given a geohash string, return the height and width for the given precision. The result is a pair of double: the height (latitude span) followed by the width (longitude span).
  *
  * <a name="ml-server-type-geohash-precision-dimensions"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-precision-dimensions" target="mlserverdoc">geo:geohash-precision-dimensions</a> server function.
  * @param precision  The precision. This should be a number between 0 and 12, as with geo:geohash-encode.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression geohashPrecisionDimensions(ServerExpression precision);
/**
  * Given a geohash string, return the 32 subhashes.
  *
  * <a name="ml-server-type-geohash-subhashes"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-subhashes" target="mlserverdoc">geo:geohash-subhashes</a> server function.
  * @param hash  The geohash string, as produced from the function geo:geohash-encode.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression geohashSubhashes(ServerExpression hash);
/**
  * Given a geohash string, return the 32 subhashes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-subhashes" target="mlserverdoc">geo:geohash-subhashes</a> server function.
  * @param hash  The geohash string, as produced from the function geo:geohash-encode.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param which  Which subhashes to return, one of "S","W","N","E","SW","SE","NW","NE" or "ALL". The default is "ALL".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression geohashSubhashes(ServerExpression hash, String which);
/**
  * Given a geohash string, return the 32 subhashes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:geohash-subhashes" target="mlserverdoc">geo:geohash-subhashes</a> server function.
  * @param hash  The geohash string, as produced from the function geo:geohash-encode.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param which  Which subhashes to return, one of "S","W","N","E","SW","SE","NW","NE" or "ALL". The default is "ALL".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression geohashSubhashes(ServerExpression hash, ServerExpression which);
/**
  * This function returns a point that is guaranteed to be inside the bounds of the given region. For a given region and set of options, the point returned should be stable from one call to the next.
  *
  * <a name="ml-server-type-interior-point"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:interior-point" target="mlserverdoc">geo:interior-point</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression interiorPoint(ServerExpression region);
/**
  * This function returns a point that is guaranteed to be inside the bounds of the given region. For a given region and set of options, the point returned should be stable from one call to the next.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:interior-point" target="mlserverdoc">geo:interior-point</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression interiorPoint(ServerExpression region, String options);
/**
  * This function returns a point that is guaranteed to be inside the bounds of the given region. For a given region and set of options, the point returned should be stable from one call to the next.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:interior-point" target="mlserverdoc">geo:interior-point</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a> server data type
  */
  public ServerExpression interiorPoint(ServerExpression region, ServerExpression options);
/**
  * Compares geospatial regions to see if they fulfill the 'intersects' DE-9IM relation.
  *
  * <a name="ml-server-type-intersects"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:intersects" target="mlserverdoc">geo:intersects</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of intersects.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of intersects.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression intersects(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'intersects' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:intersects" target="mlserverdoc">geo:intersects</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of intersects.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of intersects.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression intersects(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'intersects' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:intersects" target="mlserverdoc">geo:intersects</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of intersects.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of intersects.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression intersects(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Compares geospatial regions to see if they fulfill the 'overlaps' DE-9IM relation.
  *
  * <a name="ml-server-type-overlaps"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:overlaps" target="mlserverdoc">geo:overlaps</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of overlaps.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of overlaps.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression overlaps(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'overlaps' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:overlaps" target="mlserverdoc">geo:overlaps</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of overlaps.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of overlaps.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression overlaps(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'overlaps' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:overlaps" target="mlserverdoc">geo:overlaps</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of overlaps.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of overlaps.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression overlaps(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Returns a sequence of geospatial regions parsed from Well-Known Text format.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:parse-wkt" target="mlserverdoc">geo:parse-wkt</a> server function.
  * @param wkt  A sequence of strings in Well-Known Text format.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionSeqExpr parseWkt(String wkt);
/**
  * Returns a sequence of geospatial regions parsed from Well-Known Text format.
  *
  * <a name="ml-server-type-parse-wkt"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:parse-wkt" target="mlserverdoc">geo:parse-wkt</a> server function.
  * @param wkt  A sequence of strings in Well-Known Text format.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionSeqExpr parseWkt(ServerExpression wkt);
/**
  * This function returns a simplified approximation of the region, using the Douglas-Peucker algorithm.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-approximate" target="mlserverdoc">geo:region-approximate</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param threshold  How close the approximation should be, in the units specified by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr regionApproximate(ServerExpression region, double threshold);
/**
  * This function returns a simplified approximation of the region, using the Douglas-Peucker algorithm.
  *
  * <a name="ml-server-type-region-approximate"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-approximate" target="mlserverdoc">geo:region-approximate</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param threshold  How close the approximation should be, in the units specified by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr regionApproximate(ServerExpression region, ServerExpression threshold);
/**
  * This function returns a simplified approximation of the region, using the Douglas-Peucker algorithm.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-approximate" target="mlserverdoc">geo:region-approximate</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param threshold  How close the approximation should be, in the units specified by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param options  Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. Tolerance must be smaller than the value of the threshold parameter.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr regionApproximate(ServerExpression region, double threshold, String options);
/**
  * This function returns a simplified approximation of the region, using the Douglas-Peucker algorithm.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-approximate" target="mlserverdoc">geo:region-approximate</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param threshold  How close the approximation should be, in the units specified by the units option.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param options  Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. Tolerance must be smaller than the value of the threshold parameter.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr regionApproximate(ServerExpression region, ServerExpression threshold, ServerExpression options);
/**
  * This function fixes various problems with the region or raises an error if it is not repairable. The only relevant fix for MarkLogic is to remove duplicate adjacent vertices in polygons (including inner and outer polygons of complex polygons). The only relevant options are options controlling the coordinate system and the tolerance option.
  *
  * <a name="ml-server-type-region-clean"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-clean" target="mlserverdoc">geo:region-clean</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr regionClean(ServerExpression region);
/**
  * This function fixes various problems with the region or raises an error if it is not repairable. The only relevant fix for MarkLogic is to remove duplicate adjacent vertices in polygons (including inner and outer polygons of complex polygons). The only relevant options are options controlling the coordinate system and the tolerance option.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-clean" target="mlserverdoc">geo:region-clean</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  The tolerance, units, coordinate system. Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr regionClean(ServerExpression region, String options);
/**
  * This function fixes various problems with the region or raises an error if it is not repairable. The only relevant fix for MarkLogic is to remove duplicate adjacent vertices in polygons (including inner and outer polygons of complex polygons). The only relevant options are options controlling the coordinate system and the tolerance option.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-clean" target="mlserverdoc">geo:region-clean</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  The tolerance, units, coordinate system. Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr regionClean(ServerExpression region, ServerExpression options);
/**
  * Returns true if one region contains the other region.
  *
  * <a name="ml-server-type-region-contains"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-contains" target="mlserverdoc">geo:region-contains</a> server function.
  * @param target  A geographic region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region  One or more geographic regions (boxes, circles, polygons, or points). Where multiple regions are specified, return true if target contains any of the regions.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionContains(ServerExpression target, ServerExpression region);
/**
  * Returns true if one region contains the other region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-contains" target="mlserverdoc">geo:region-contains</a> server function.
  * @param target  A geographic region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region  One or more geographic regions (boxes, circles, polygons, or points). Where multiple regions are specified, return true if target contains any of the regions.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include: Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. "boundaries-included" Points on boxes', circles', and regions' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on boxes', circles', and regions' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionContains(ServerExpression target, ServerExpression region, String options);
/**
  * Returns true if one region contains the other region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-contains" target="mlserverdoc">geo:region-contains</a> server function.
  * @param target  A geographic region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region  One or more geographic regions (boxes, circles, polygons, or points). Where multiple regions are specified, return true if target contains any of the regions.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include: Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. "boundaries-included" Points on boxes', circles', and regions' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on boxes', circles', and regions' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionContains(ServerExpression target, ServerExpression region, ServerExpression options);
/**
  * Calculates the Dimensionally Extended nine-Intersection Matrix (DE-9IM) of two geospatial regions.
  *
  * <a name="ml-server-type-region-de9im"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-de9im" target="mlserverdoc">geo:region-de9im</a> server function.
  * @param region1  The first geospatial region to compare.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression regionDe9im(ServerExpression region1, ServerExpression region2);
/**
  * Calculates the Dimensionally Extended nine-Intersection Matrix (DE-9IM) of two geospatial regions.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-de9im" target="mlserverdoc">geo:region-de9im</a> server function.
  * @param region1  The first geospatial region to compare.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression regionDe9im(ServerExpression region1, ServerExpression region2, String options);
/**
  * Calculates the Dimensionally Extended nine-Intersection Matrix (DE-9IM) of two geospatial regions.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-de9im" target="mlserverdoc">geo:region-de9im</a> server function.
  * @param region1  The first geospatial region to compare.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression regionDe9im(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Returns true if the target region intersects with a region.
  *
  * <a name="ml-server-type-region-intersects"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-intersects" target="mlserverdoc">geo:region-intersects</a> server function.
  * @param target  A geographic region (box, circle, polygon, or point).  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region  One or more geographic regions. Where multiple regions are specified, return true if any region intersects the target region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionIntersects(ServerExpression target, ServerExpression region);
/**
  * Returns true if the target region intersects with a region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-intersects" target="mlserverdoc">geo:region-intersects</a> server function.
  * @param target  A geographic region (box, circle, polygon, or point).  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region  One or more geographic regions. Where multiple regions are specified, return true if any region intersects the target region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include: Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. "boundaries-included" Points on regions' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on regions' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionIntersects(ServerExpression target, ServerExpression region, String options);
/**
  * Returns true if the target region intersects with a region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-intersects" target="mlserverdoc">geo:region-intersects</a> server function.
  * @param target  A geographic region (box, circle, polygon, or point).  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region  One or more geographic regions. Where multiple regions are specified, return true if any region intersects the target region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include: Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option. "boundaries-included" Points on regions' boundaries are counted as matching. This is the default. "boundaries-excluded" Points on regions' boundaries are not counted as matching. "boundaries-latitude-excluded" Points on boxes' latitude boundaries are not counted as matching. "boundaries-longitude-excluded" Points on boxes' longitude boundaries are not counted as matching. "boundaries-south-excluded" Points on the boxes' southern boundaries are not counted as matching. "boundaries-west-excluded" Points on the boxes' western boundaries are not counted as matching. "boundaries-north-excluded" Points on the boxes' northern boundaries are not counted as matching. "boundaries-east-excluded" Points on the boxes' eastern boundaries are not counted as matching. "boundaries-circle-excluded" Points on circles' boundary are not counted as matching. "boundaries-endpoints-excluded" Points on linestrings' boundary (the endpoints) are not counted as matching.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionIntersects(ServerExpression target, ServerExpression region, ServerExpression options);
/**
  * Compares geospatial regions based on a specified relationship. For example, determine if two regions overlap.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-relate" target="mlserverdoc">geo:region-relate</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of $operation.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param operation  The operation to apply between the region specified in the $region-1 and $region-2 parameters. Allowed values: contains, covered-by, covers, crosses, disjoint, equals, intersects, overlaps, touches, within. See the Usage Notes for details.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of $operation.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionRelate(ServerExpression region1, String operation, ServerExpression region2);
/**
  * Compares geospatial regions based on a specified relationship. For example, determine if two regions overlap.
  *
  * <a name="ml-server-type-region-relate"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-relate" target="mlserverdoc">geo:region-relate</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of $operation.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param operation  The operation to apply between the region specified in the $region-1 and $region-2 parameters. Allowed values: contains, covered-by, covers, crosses, disjoint, equals, intersects, overlaps, touches, within. See the Usage Notes for details.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of $operation.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionRelate(ServerExpression region1, ServerExpression operation, ServerExpression region2);
/**
  * Compares geospatial regions based on a specified relationship. For example, determine if two regions overlap.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-relate" target="mlserverdoc">geo:region-relate</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of $operation.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param operation  The operation to apply between the region specified in the $region-1 and $region-2 parameters. Allowed values: contains, covered-by, covers, crosses, disjoint, equals, intersects, overlaps, touches, within. See the Usage Notes for details.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of $operation.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionRelate(ServerExpression region1, String operation, ServerExpression region2, String options);
/**
  * Compares geospatial regions based on a specified relationship. For example, determine if two regions overlap.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:region-relate" target="mlserverdoc">geo:region-relate</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of $operation.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param operation  The operation to apply between the region specified in the $region-1 and $region-2 parameters. Allowed values: contains, covered-by, covers, crosses, disjoint, equals, intersects, overlaps, touches, within. See the Usage Notes for details.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of $operation.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression regionRelate(ServerExpression region1, ServerExpression operation, ServerExpression region2, ServerExpression options);
/**
  * Remove duplicate (adjacent) vertices.
  *
  * <a name="ml-server-type-remove-duplicate-vertices"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:remove-duplicate-vertices" target="mlserverdoc">geo:remove-duplicate-vertices</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr removeDuplicateVertices(ServerExpression region);
/**
  * Remove duplicate (adjacent) vertices.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:remove-duplicate-vertices" target="mlserverdoc">geo:remove-duplicate-vertices</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  The tolerance, units, coordinate system. Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr removeDuplicateVertices(ServerExpression region, String options);
/**
  * Remove duplicate (adjacent) vertices.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:remove-duplicate-vertices" target="mlserverdoc">geo:remove-duplicate-vertices</a> server function.
  * @param region  A cts region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  The tolerance, units, coordinate system. Options include:   "coordinate-system=value" Use the given coordinate system. Valid values are wgs84, wgs84/double, etrs89, etrs89/double, raw and raw/double. Defaults to the governing coordinating system. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. Defaults to the precision of the governing coordinate system. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a> server data type
  */
  public CtsRegionExpr removeDuplicateVertices(ServerExpression region, ServerExpression options);
/**
  * Returns the great circle distance (in units) between a point and a region. The region is defined by a cts:region.
  *
  * <a name="ml-server-type-shortest-distance"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:shortest-distance" target="mlserverdoc">geo:shortest-distance</a> server function.
  * @param p1  The first point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param region  A region such as a circle, box, polygon, linestring, or complex-polygon. For compatibility with previous versions, a sequence of points is interpreted as a sequence of arcs (defined pairwise) and the distance returned is the shortest distance to one of those points. If the first parameter is a point within the region specified in this parameter, then this function returns 0. If the point specified in the first parameter in not in the region specified in this parameter, then this function returns the shortest distance to the boundary of the region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression shortestDistance(ServerExpression p1, ServerExpression region);
/**
  * Returns the great circle distance (in units) between a point and a region. The region is defined by a cts:region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:shortest-distance" target="mlserverdoc">geo:shortest-distance</a> server function.
  * @param p1  The first point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param region  A region such as a circle, box, polygon, linestring, or complex-polygon. For compatibility with previous versions, a sequence of points is interpreted as a sequence of arcs (defined pairwise) and the distance returned is the shortest distance to one of those points. If the first parameter is a point within the region specified in this parameter, then this function returns 0. If the point specified in the first parameter in not in the region specified in this parameter, then this function returns the shortest distance to the boundary of the region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "units=value" Measure distance and the radii of circles in the specified units. Allowed values: miles (default), km, feet, meters. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression shortestDistance(ServerExpression p1, ServerExpression region, String options);
/**
  * Returns the great circle distance (in units) between a point and a region. The region is defined by a cts:region.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:shortest-distance" target="mlserverdoc">geo:shortest-distance</a> server function.
  * @param p1  The first point.  (of <a href="{@docRoot}/doc-files/types/cts_point.html">cts:point</a>)
  * @param region  A region such as a circle, box, polygon, linestring, or complex-polygon. For compatibility with previous versions, a sequence of points is interpreted as a sequence of arcs (defined pairwise) and the distance returned is the shortest distance to one of those points. If the first parameter is a point within the region specified in this parameter, then this function returns 0. If the point specified in the first parameter in not in the region specified in this parameter, then this function returns the shortest distance to the boundary of the region.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options for the operation. The default is (). Options include:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "units=value" Measure distance and the radii of circles in the specified units. Allowed values: miles (default), km, feet, meters. "precision=value" Use the coordinate system at the given precision. Allowed values: float and double.    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression shortestDistance(ServerExpression p1, ServerExpression region, ServerExpression options);
/**
  * Returns a sequence of strings in Well-Known Text format.
  *
  * <a name="ml-server-type-to-wkt"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:to-wkt" target="mlserverdoc">geo:to-wkt</a> server function.
  * @param wkt  A sequence of geospatial regions.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression toWkt(ServerExpression wkt);
/**
  * Compares geospatial regions to see if they fulfill the 'touches' DE-9IM relation.
  *
  * <a name="ml-server-type-touches"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:touches" target="mlserverdoc">geo:touches</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of touches.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of touches.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression touches(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'touches' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:touches" target="mlserverdoc">geo:touches</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of touches.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of touches.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression touches(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'touches' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:touches" target="mlserverdoc">geo:touches</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of touches.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of touches.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression touches(ServerExpression region1, ServerExpression region2, ServerExpression options);
/**
  * Returns true if the string is valid Well-Known Text for a supported region type.
  *
  * <a name="ml-server-type-validate-wkt"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:validate-wkt" target="mlserverdoc">geo:validate-wkt</a> server function.
  * @param wkt  A string to validate.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression validateWkt(ServerExpression wkt);
/**
  * Compares geospatial regions to see if they fulfill the 'within' DE-9IM relation.
  *
  * <a name="ml-server-type-within"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:within" target="mlserverdoc">geo:within</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of within.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of within.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression within(ServerExpression region1, ServerExpression region2);
/**
  * Compares geospatial regions to see if they fulfill the 'within' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:within" target="mlserverdoc">geo:within</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of within.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of within.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression within(ServerExpression region1, ServerExpression region2, String options);
/**
  * Compares geospatial regions to see if they fulfill the 'within' DE-9IM relation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/geo:within" target="mlserverdoc">geo:within</a> server function.
  * @param region1  The first geospatial region to compare. This region is the left operand of within.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param region2  The second geospatial region to compare. This region is the right operand of within.  (of <a href="{@docRoot}/doc-files/types/cts_region.html">cts:region</a>)
  * @param options  Options to this operation. The default is (). Available options:   "coordinate-system=string" Use the given coordinate system. Valid values are:  wgs84The WGS84 coordinate system with degrees as the angular unit. wgs84/radiansThe WGS84 coordinate system with radians as the angular unit. wgs84/doubleThe WGS84 coordinate system at double precision with degrees as the angular unit. wgs84/radians/doubleThe WGS84 coordinate system at double precision with radians as the angular unit. etrs89The ETRS89 coordinate system. etrs89/doubleThe ETRS89 coordinate system at double precision. rawThe raw (unmapped) coordinate system. raw/doubleThe raw coordinate system at double precision.   "precision=value" Use the coordinate system at the given precision. Allowed values: float and double. "units=value" Measure distance, radii of circles, and tolerance in the specified units. Allowed values: miles (default), km, feet, meters. "tolerance=distance" Tolerance is the largest allowable variation in geometry calculations. If the distance between two points is less than tolerance, then the two points are considered equal. For the raw coordinate system, use the units of the coordinates. For geographic coordinate systems, use the units specified by the units option.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression within(ServerExpression region1, ServerExpression region2, ServerExpression options);
}
