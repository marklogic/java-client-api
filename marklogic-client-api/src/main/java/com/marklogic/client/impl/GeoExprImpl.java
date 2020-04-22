/*
 * Copyright 2016-2020 MarkLogic Corporation
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
package com.marklogic.client.impl;

import com.marklogic.client.type.CtsRegionExpr;
import com.marklogic.client.type.CtsRegionSeqExpr;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsDoubleSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.GeoExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class GeoExprImpl implements GeoExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static GeoExprImpl geo = new GeoExprImpl();

  GeoExprImpl() {
  }

    
  @Override
  public ServerExpression approxCenter(CtsRegionExpr region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for approxCenter() cannot be null");
    }
    return new CtsExprImpl.PointCallImpl("geo", "approx-center", new Object[]{ region });
  }

  
  @Override
  public ServerExpression approxCenter(CtsRegionExpr region, String options) {
    return approxCenter(region, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression approxCenter(CtsRegionExpr region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for approxCenter() cannot be null");
    }
    return new CtsExprImpl.PointCallImpl("geo", "approx-center", new Object[]{ region, options });
  }

  
  @Override
  public ServerExpression arcIntersection(ServerExpression p1, ServerExpression p2, ServerExpression q1, ServerExpression q2) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for arcIntersection() cannot be null");
    }
    if (p2 == null) {
      throw new IllegalArgumentException("p2 parameter for arcIntersection() cannot be null");
    }
    if (q1 == null) {
      throw new IllegalArgumentException("q1 parameter for arcIntersection() cannot be null");
    }
    if (q2 == null) {
      throw new IllegalArgumentException("q2 parameter for arcIntersection() cannot be null");
    }
    return new CtsExprImpl.PointCallImpl("geo", "arc-intersection", new Object[]{ p1, p2, q1, q2 });
  }

  
  @Override
  public ServerExpression arcIntersection(ServerExpression p1, ServerExpression p2, ServerExpression q1, ServerExpression q2, String options) {
    return arcIntersection(p1, p2, q1, q2, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression arcIntersection(ServerExpression p1, ServerExpression p2, ServerExpression q1, ServerExpression q2, ServerExpression options) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for arcIntersection() cannot be null");
    }
    if (p2 == null) {
      throw new IllegalArgumentException("p2 parameter for arcIntersection() cannot be null");
    }
    if (q1 == null) {
      throw new IllegalArgumentException("q1 parameter for arcIntersection() cannot be null");
    }
    if (q2 == null) {
      throw new IllegalArgumentException("q2 parameter for arcIntersection() cannot be null");
    }
    return new CtsExprImpl.PointCallImpl("geo", "arc-intersection", new Object[]{ p1, p2, q1, q2, options });
  }

  
  @Override
  public ServerExpression bearing(ServerExpression p1, ServerExpression p2) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for bearing() cannot be null");
    }
    if (p2 == null) {
      throw new IllegalArgumentException("p2 parameter for bearing() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("geo", "bearing", new Object[]{ p1, p2 });
  }

  
  @Override
  public ServerExpression bearing(ServerExpression p1, ServerExpression p2, String options) {
    return bearing(p1, p2, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression bearing(ServerExpression p1, ServerExpression p2, ServerExpression options) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for bearing() cannot be null");
    }
    if (p2 == null) {
      throw new IllegalArgumentException("p2 parameter for bearing() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("geo", "bearing", new Object[]{ p1, p2, options });
  }

  
  @Override
  public ServerExpression boundingBoxes(CtsRegionExpr region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for boundingBoxes() cannot be null");
    }
    return new CtsExprImpl.BoxSeqCallImpl("geo", "bounding-boxes", new Object[]{ region });
  }

  
  @Override
  public ServerExpression boundingBoxes(CtsRegionExpr region, String options) {
    return boundingBoxes(region, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression boundingBoxes(CtsRegionExpr region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for boundingBoxes() cannot be null");
    }
    return new CtsExprImpl.BoxSeqCallImpl("geo", "bounding-boxes", new Object[]{ region, options });
  }

  
  @Override
  public ServerExpression boxIntersects(ServerExpression box, CtsRegionSeqExpr region) {
    if (box == null) {
      throw new IllegalArgumentException("box parameter for boxIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "box-intersects", new Object[]{ box, region });
  }

  
  @Override
  public ServerExpression boxIntersects(ServerExpression box, CtsRegionSeqExpr region, String options) {
    return boxIntersects(box, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression boxIntersects(ServerExpression box, CtsRegionSeqExpr region, ServerExpression options) {
    if (box == null) {
      throw new IllegalArgumentException("box parameter for boxIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "box-intersects", new Object[]{ box, region, options });
  }

  
  @Override
  public ServerExpression circleIntersects(ServerExpression circle, CtsRegionSeqExpr region) {
    if (circle == null) {
      throw new IllegalArgumentException("circle parameter for circleIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "circle-intersects", new Object[]{ circle, region });
  }

  
  @Override
  public ServerExpression circleIntersects(ServerExpression circle, CtsRegionSeqExpr region, String options) {
    return circleIntersects(circle, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression circleIntersects(ServerExpression circle, CtsRegionSeqExpr region, ServerExpression options) {
    if (circle == null) {
      throw new IllegalArgumentException("circle parameter for circleIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "circle-intersects", new Object[]{ circle, region, options });
  }

  
  @Override
  public ServerExpression circlePolygon(ServerExpression circle, double arcTolerance) {
    return circlePolygon(circle, xs.doubleVal(arcTolerance));
  }

  
  @Override
  public ServerExpression circlePolygon(ServerExpression circle, ServerExpression arcTolerance) {
    if (circle == null) {
      throw new IllegalArgumentException("circle parameter for circlePolygon() cannot be null");
    }
    if (arcTolerance == null) {
      throw new IllegalArgumentException("arcTolerance parameter for circlePolygon() cannot be null");
    }
    return new CtsExprImpl.PolygonCallImpl("geo", "circle-polygon", new Object[]{ circle, arcTolerance });
  }

  
  @Override
  public ServerExpression circlePolygon(ServerExpression circle, double arcTolerance, String options) {
    return circlePolygon(circle, xs.doubleVal(arcTolerance), (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression circlePolygon(ServerExpression circle, ServerExpression arcTolerance, ServerExpression options) {
    if (circle == null) {
      throw new IllegalArgumentException("circle parameter for circlePolygon() cannot be null");
    }
    if (arcTolerance == null) {
      throw new IllegalArgumentException("arcTolerance parameter for circlePolygon() cannot be null");
    }
    return new CtsExprImpl.PolygonCallImpl("geo", "circle-polygon", new Object[]{ circle, arcTolerance, options });
  }

  
  @Override
  public ServerExpression countDistinctVertices(CtsRegionExpr region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for countDistinctVertices() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("geo", "count-distinct-vertices", new Object[]{ region });
  }

  
  @Override
  public ServerExpression countDistinctVertices(CtsRegionExpr region, String options) {
    return countDistinctVertices(region, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression countDistinctVertices(CtsRegionExpr region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for countDistinctVertices() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("geo", "count-distinct-vertices", new Object[]{ region, options });
  }

  
  @Override
  public ServerExpression countVertices(CtsRegionExpr region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for countVertices() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("geo", "count-vertices", new Object[]{ region });
  }

  
  @Override
  public ServerExpression destination(ServerExpression p, double bearing, double distance) {
    return destination(p, xs.doubleVal(bearing), xs.doubleVal(distance));
  }

  
  @Override
  public ServerExpression destination(ServerExpression p, ServerExpression bearing, ServerExpression distance) {
    if (p == null) {
      throw new IllegalArgumentException("p parameter for destination() cannot be null");
    }
    if (bearing == null) {
      throw new IllegalArgumentException("bearing parameter for destination() cannot be null");
    }
    if (distance == null) {
      throw new IllegalArgumentException("distance parameter for destination() cannot be null");
    }
    return new CtsExprImpl.PointCallImpl("geo", "destination", new Object[]{ p, bearing, distance });
  }

  
  @Override
  public ServerExpression destination(ServerExpression p, double bearing, double distance, String options) {
    return destination(p, xs.doubleVal(bearing), xs.doubleVal(distance), (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression destination(ServerExpression p, ServerExpression bearing, ServerExpression distance, ServerExpression options) {
    if (p == null) {
      throw new IllegalArgumentException("p parameter for destination() cannot be null");
    }
    if (bearing == null) {
      throw new IllegalArgumentException("bearing parameter for destination() cannot be null");
    }
    if (distance == null) {
      throw new IllegalArgumentException("distance parameter for destination() cannot be null");
    }
    return new CtsExprImpl.PointCallImpl("geo", "destination", new Object[]{ p, bearing, distance, options });
  }

  
  @Override
  public ServerExpression distance(ServerExpression p1, ServerExpression p2) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for distance() cannot be null");
    }
    if (p2 == null) {
      throw new IllegalArgumentException("p2 parameter for distance() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("geo", "distance", new Object[]{ p1, p2 });
  }

  
  @Override
  public ServerExpression distance(ServerExpression p1, ServerExpression p2, String options) {
    return distance(p1, p2, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression distance(ServerExpression p1, ServerExpression p2, ServerExpression options) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for distance() cannot be null");
    }
    if (p2 == null) {
      throw new IllegalArgumentException("p2 parameter for distance() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("geo", "distance", new Object[]{ p1, p2, options });
  }

  
  @Override
  public ServerExpression distanceConvert(ServerExpression distance, String unit1, String unit2) {
    return distanceConvert(distance, (unit1 == null) ? (ServerExpression) null : xs.string(unit1), (unit2 == null) ? (ServerExpression) null : xs.string(unit2));
  }

  
  @Override
  public ServerExpression distanceConvert(ServerExpression distance, ServerExpression unit1, ServerExpression unit2) {
    if (distance == null) {
      throw new IllegalArgumentException("distance parameter for distanceConvert() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("geo", "distance-convert", new Object[]{ distance, unit1, unit2 });
  }

  
  @Override
  public ServerExpression ellipsePolygon(ServerExpression center, double semiMajorAxis, double semiMinorAxis, double azimuth, double arcTolerance) {
    return ellipsePolygon(center, xs.doubleVal(semiMajorAxis), xs.doubleVal(semiMinorAxis), xs.doubleVal(azimuth), xs.doubleVal(arcTolerance));
  }

  
  @Override
  public ServerExpression ellipsePolygon(ServerExpression center, ServerExpression semiMajorAxis, ServerExpression semiMinorAxis, ServerExpression azimuth, ServerExpression arcTolerance) {
    if (semiMajorAxis == null) {
      throw new IllegalArgumentException("semiMajorAxis parameter for ellipsePolygon() cannot be null");
    }
    if (semiMinorAxis == null) {
      throw new IllegalArgumentException("semiMinorAxis parameter for ellipsePolygon() cannot be null");
    }
    if (azimuth == null) {
      throw new IllegalArgumentException("azimuth parameter for ellipsePolygon() cannot be null");
    }
    if (arcTolerance == null) {
      throw new IllegalArgumentException("arcTolerance parameter for ellipsePolygon() cannot be null");
    }
    return new CtsExprImpl.PolygonCallImpl("geo", "ellipse-polygon", new Object[]{ center, semiMajorAxis, semiMinorAxis, azimuth, arcTolerance });
  }

  
  @Override
  public ServerExpression ellipsePolygon(ServerExpression center, double semiMajorAxis, double semiMinorAxis, double azimuth, double arcTolerance, String options) {
    return ellipsePolygon(center, xs.doubleVal(semiMajorAxis), xs.doubleVal(semiMinorAxis), xs.doubleVal(azimuth), xs.doubleVal(arcTolerance), (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression ellipsePolygon(ServerExpression center, ServerExpression semiMajorAxis, ServerExpression semiMinorAxis, ServerExpression azimuth, ServerExpression arcTolerance, ServerExpression options) {
    if (semiMajorAxis == null) {
      throw new IllegalArgumentException("semiMajorAxis parameter for ellipsePolygon() cannot be null");
    }
    if (semiMinorAxis == null) {
      throw new IllegalArgumentException("semiMinorAxis parameter for ellipsePolygon() cannot be null");
    }
    if (azimuth == null) {
      throw new IllegalArgumentException("azimuth parameter for ellipsePolygon() cannot be null");
    }
    if (arcTolerance == null) {
      throw new IllegalArgumentException("arcTolerance parameter for ellipsePolygon() cannot be null");
    }
    return new CtsExprImpl.PolygonCallImpl("geo", "ellipse-polygon", new Object[]{ center, semiMajorAxis, semiMinorAxis, azimuth, arcTolerance, options });
  }

  
  @Override
  public ServerExpression geohashDecode(ServerExpression hash) {
    if (hash == null) {
      throw new IllegalArgumentException("hash parameter for geohashDecode() cannot be null");
    }
    return new CtsExprImpl.BoxCallImpl("geo", "geohash-decode", new Object[]{ hash });
  }

  
  @Override
  public ServerExpression geohashDecodePoint(String hash) {
    return geohashDecodePoint((hash == null) ? (ServerExpression) null : xs.string(hash));
  }

  
  @Override
  public ServerExpression geohashDecodePoint(ServerExpression hash) {
    if (hash == null) {
      throw new IllegalArgumentException("hash parameter for geohashDecodePoint() cannot be null");
    }
    return new CtsExprImpl.PointCallImpl("geo", "geohash-decode-point", new Object[]{ hash });
  }

  
  @Override
  public ServerExpression geohashEncode(CtsRegionExpr region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for geohashEncode() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("geo", "geohash-encode", new Object[]{ region });
  }

  
  @Override
  public ServerExpression geohashEncode(CtsRegionExpr region, long geohashPrecision) {
    return geohashEncode(region, xs.integer(geohashPrecision));
  }

  
  @Override
  public ServerExpression geohashEncode(CtsRegionExpr region, ServerExpression geohashPrecision) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for geohashEncode() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("geo", "geohash-encode", new Object[]{ region, geohashPrecision });
  }

  
  @Override
  public ServerExpression geohashEncode(CtsRegionExpr region, long geohashPrecision, String options) {
    return geohashEncode(region, xs.integer(geohashPrecision), (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression geohashEncode(CtsRegionExpr region, ServerExpression geohashPrecision, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for geohashEncode() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("geo", "geohash-encode", new Object[]{ region, geohashPrecision, options });
  }

  
  @Override
  public ServerExpression geohashNeighbors(ServerExpression hash) {
    if (hash == null) {
      throw new IllegalArgumentException("hash parameter for geohashNeighbors() cannot be null");
    }
    return new MapExprImpl.MapCallImpl("geo", "geohash-neighbors", new Object[]{ hash });
  }

  
  @Override
  public ServerExpression geohashPrecisionDimensions(ServerExpression precision) {
    if (precision == null) {
      throw new IllegalArgumentException("precision parameter for geohashPrecisionDimensions() cannot be null");
    }
    return new XsExprImpl.DoubleSeqCallImpl("geo", "geohash-precision-dimensions", new Object[]{ precision });
  }

  
  @Override
  public ServerExpression geohashSubhashes(ServerExpression hash, String which) {
    return geohashSubhashes(hash, (which == null) ? (ServerExpression) null : xs.string(which));
  }

  
  @Override
  public ServerExpression geohashSubhashes(ServerExpression hash, ServerExpression which) {
    if (hash == null) {
      throw new IllegalArgumentException("hash parameter for geohashSubhashes() cannot be null");
    }
    if (which == null) {
      throw new IllegalArgumentException("which parameter for geohashSubhashes() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("geo", "geohash-subhashes", new Object[]{ hash, which });
  }

  
  @Override
  public ServerExpression interiorPoint(CtsRegionExpr region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for interiorPoint() cannot be null");
    }
    return new CtsExprImpl.PointCallImpl("geo", "interior-point", new Object[]{ region });
  }

  
  @Override
  public ServerExpression interiorPoint(CtsRegionExpr region, String options) {
    return interiorPoint(region, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression interiorPoint(CtsRegionExpr region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for interiorPoint() cannot be null");
    }
    return new CtsExprImpl.PointCallImpl("geo", "interior-point", new Object[]{ region, options });
  }

  
  @Override
  public CtsRegionSeqExpr parseWkt(String wkt) {
    return parseWkt((wkt == null) ? (XsStringVal) null : xs.string(wkt));
  }

  
  @Override
  public CtsRegionSeqExpr parseWkt(XsStringSeqVal wkt) {
    return new CtsExprImpl.RegionSeqCallImpl("geo", "parse-wkt", new Object[]{ wkt });
  }

  
  @Override
  public CtsRegionExpr regionApproximate(CtsRegionExpr region, double threshold) {
    return regionApproximate(region, xs.doubleVal(threshold));
  }

  
  @Override
  public CtsRegionExpr regionApproximate(CtsRegionExpr region, XsDoubleVal threshold) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for regionApproximate() cannot be null");
    }
    if (threshold == null) {
      throw new IllegalArgumentException("threshold parameter for regionApproximate() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "region-approximate", new Object[]{ region, threshold });
  }

  
  @Override
  public CtsRegionExpr regionApproximate(CtsRegionExpr region, double threshold, String options) {
    return regionApproximate(region, xs.doubleVal(threshold), (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsRegionExpr regionApproximate(CtsRegionExpr region, XsDoubleVal threshold, XsStringSeqVal options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for regionApproximate() cannot be null");
    }
    if (threshold == null) {
      throw new IllegalArgumentException("threshold parameter for regionApproximate() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "region-approximate", new Object[]{ region, threshold, options });
  }

  
  @Override
  public CtsRegionExpr regionClean(CtsRegionExpr region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for regionClean() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "region-clean", new Object[]{ region });
  }

  
  @Override
  public CtsRegionExpr regionClean(CtsRegionExpr region, String options) {
    return regionClean(region, (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsRegionExpr regionClean(CtsRegionExpr region, XsStringSeqVal options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for regionClean() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "region-clean", new Object[]{ region, options });
  }

  
  @Override
  public ServerExpression regionContains(CtsRegionExpr target, CtsRegionSeqExpr region) {
    if (target == null) {
      throw new IllegalArgumentException("target parameter for regionContains() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-contains", new Object[]{ target, region });
  }

  
  @Override
  public ServerExpression regionContains(CtsRegionExpr target, CtsRegionSeqExpr region, String options) {
    return regionContains(target, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression regionContains(CtsRegionExpr target, CtsRegionSeqExpr region, ServerExpression options) {
    if (target == null) {
      throw new IllegalArgumentException("target parameter for regionContains() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-contains", new Object[]{ target, region, options });
  }

  
  @Override
  public ServerExpression regionDe9im(CtsRegionExpr region1, CtsRegionExpr region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for regionDe9im() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for regionDe9im() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("geo", "region-de9im", new Object[]{ region1, region2 });
  }

  
  @Override
  public ServerExpression regionDe9im(CtsRegionExpr region1, CtsRegionExpr region2, String options) {
    return regionDe9im(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression regionDe9im(CtsRegionExpr region1, CtsRegionExpr region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for regionDe9im() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for regionDe9im() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("geo", "region-de9im", new Object[]{ region1, region2, options });
  }

  
  @Override
  public ServerExpression regionIntersects(CtsRegionExpr target, CtsRegionSeqExpr region) {
    if (target == null) {
      throw new IllegalArgumentException("target parameter for regionIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-intersects", new Object[]{ target, region });
  }

  
  @Override
  public ServerExpression regionIntersects(CtsRegionExpr target, CtsRegionSeqExpr region, String options) {
    return regionIntersects(target, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression regionIntersects(CtsRegionExpr target, CtsRegionSeqExpr region, ServerExpression options) {
    if (target == null) {
      throw new IllegalArgumentException("target parameter for regionIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-intersects", new Object[]{ target, region, options });
  }

  
  @Override
  public ServerExpression regionRelate(CtsRegionExpr region1, String operation, CtsRegionExpr region2) {
    return regionRelate(region1, (operation == null) ? (ServerExpression) null : xs.string(operation), region2);
  }

  
  @Override
  public ServerExpression regionRelate(CtsRegionExpr region1, ServerExpression operation, CtsRegionExpr region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for regionRelate() cannot be null");
    }
    if (operation == null) {
      throw new IllegalArgumentException("operation parameter for regionRelate() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for regionRelate() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-relate", new Object[]{ region1, operation, region2 });
  }

  
  @Override
  public ServerExpression regionRelate(CtsRegionExpr region1, String operation, CtsRegionExpr region2, String options) {
    return regionRelate(region1, (operation == null) ? (ServerExpression) null : xs.string(operation), region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression regionRelate(CtsRegionExpr region1, ServerExpression operation, CtsRegionExpr region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for regionRelate() cannot be null");
    }
    if (operation == null) {
      throw new IllegalArgumentException("operation parameter for regionRelate() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for regionRelate() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-relate", new Object[]{ region1, operation, region2, options });
  }

  
  @Override
  public CtsRegionExpr removeDuplicateVertices(CtsRegionExpr region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for removeDuplicateVertices() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "remove-duplicate-vertices", new Object[]{ region });
  }

  
  @Override
  public CtsRegionExpr removeDuplicateVertices(CtsRegionExpr region, String options) {
    return removeDuplicateVertices(region, (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public CtsRegionExpr removeDuplicateVertices(CtsRegionExpr region, XsStringSeqVal options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for removeDuplicateVertices() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "remove-duplicate-vertices", new Object[]{ region, options });
  }

  
  @Override
  public ServerExpression shortestDistance(ServerExpression p1, CtsRegionSeqExpr region) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for shortestDistance() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("geo", "shortest-distance", new Object[]{ p1, region });
  }

  
  @Override
  public ServerExpression shortestDistance(ServerExpression p1, CtsRegionSeqExpr region, String options) {
    return shortestDistance(p1, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }

  
  @Override
  public ServerExpression shortestDistance(ServerExpression p1, CtsRegionSeqExpr region, ServerExpression options) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for shortestDistance() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("geo", "shortest-distance", new Object[]{ p1, region, options });
  }

  
  @Override
  public ServerExpression toWkt(CtsRegionSeqExpr wkt) {
    return new XsExprImpl.StringSeqCallImpl("geo", "to-wkt", new Object[]{ wkt });
  }

  
  @Override
  public ServerExpression validateWkt(ServerExpression wkt) {
    if (wkt == null) {
      throw new IllegalArgumentException("wkt parameter for validateWkt() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "validate-wkt", new Object[]{ wkt });
  }

  }
