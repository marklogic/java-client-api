/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;

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

import com.marklogic.client.expression.GeoExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class GeoExprImpl implements GeoExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static GeoExprImpl geo = new GeoExprImpl();

  GeoExprImpl() {
  }


  @Override
  public ServerExpression approxCenter(ServerExpression region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for approxCenter() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "approx-center", new Object[]{ region });
  }


  @Override
  public ServerExpression approxCenter(ServerExpression region, String options) {
    return approxCenter(region, (options == null) ? (XsStringVal) null : xs.string(options));
  }


  @Override
  public ServerExpression approxCenter(ServerExpression region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for approxCenter() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "approx-center", new Object[]{ region, options });
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
    return new CtsExprImpl.RegionCallImpl("geo", "arc-intersection", new Object[]{ p1, p2, q1, q2 });
  }


  @Override
  public ServerExpression arcIntersection(ServerExpression p1, ServerExpression p2, ServerExpression q1, ServerExpression q2, String options) {
    return arcIntersection(p1, p2, q1, q2, (options == null) ? (XsStringVal) null : xs.string(options));
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
    return new CtsExprImpl.RegionCallImpl("geo", "arc-intersection", new Object[]{ p1, p2, q1, q2, options });
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
  public ServerExpression boundingBoxes(ServerExpression region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for boundingBoxes() cannot be null");
    }
    return new CtsExprImpl.RegionSeqCallImpl("geo", "bounding-boxes", new Object[]{ region });
  }


  @Override
  public ServerExpression boundingBoxes(ServerExpression region, String options) {
    return boundingBoxes(region, (options == null) ? (XsStringVal) null : xs.string(options));
  }


  @Override
  public ServerExpression boundingBoxes(ServerExpression region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for boundingBoxes() cannot be null");
    }
    return new CtsExprImpl.RegionSeqCallImpl("geo", "bounding-boxes", new Object[]{ region, options });
  }


  @Override
  public ServerExpression boxIntersects(ServerExpression box, ServerExpression region) {
    if (box == null) {
      throw new IllegalArgumentException("box parameter for boxIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "box-intersects", new Object[]{ box, region });
  }


  @Override
  public ServerExpression boxIntersects(ServerExpression box, ServerExpression region, String options) {
    return boxIntersects(box, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression boxIntersects(ServerExpression box, ServerExpression region, ServerExpression options) {
    if (box == null) {
      throw new IllegalArgumentException("box parameter for boxIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "box-intersects", new Object[]{ box, region, options });
  }


  @Override
  public ServerExpression circleIntersects(ServerExpression circle, ServerExpression region) {
    if (circle == null) {
      throw new IllegalArgumentException("circle parameter for circleIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "circle-intersects", new Object[]{ circle, region });
  }


  @Override
  public ServerExpression circleIntersects(ServerExpression circle, ServerExpression region, String options) {
    return circleIntersects(circle, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression circleIntersects(ServerExpression circle, ServerExpression region, ServerExpression options) {
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
    return new CtsExprImpl.RegionCallImpl("geo", "circle-polygon", new Object[]{ circle, arcTolerance });
  }


  @Override
  public ServerExpression circlePolygon(ServerExpression circle, double arcTolerance, String options) {
    return circlePolygon(circle, xs.doubleVal(arcTolerance), (options == null) ? (XsStringVal) null : xs.string(options));
  }


  @Override
  public ServerExpression circlePolygon(ServerExpression circle, ServerExpression arcTolerance, ServerExpression options) {
    if (circle == null) {
      throw new IllegalArgumentException("circle parameter for circlePolygon() cannot be null");
    }
    if (arcTolerance == null) {
      throw new IllegalArgumentException("arcTolerance parameter for circlePolygon() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "circle-polygon", new Object[]{ circle, arcTolerance, options });
  }


  @Override
  public ServerExpression contains(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for contains() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for contains() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "contains", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression contains(ServerExpression region1, ServerExpression region2, String options) {
    return contains(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression contains(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for contains() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for contains() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "contains", new Object[]{ region1, region2, options });
  }


  @Override
  public ServerExpression countDistinctVertices(ServerExpression region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for countDistinctVertices() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("geo", "count-distinct-vertices", new Object[]{ region });
  }


  @Override
  public ServerExpression countDistinctVertices(ServerExpression region, String options) {
    return countDistinctVertices(region, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression countDistinctVertices(ServerExpression region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for countDistinctVertices() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("geo", "count-distinct-vertices", new Object[]{ region, options });
  }


  @Override
  public ServerExpression countVertices(ServerExpression region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for countVertices() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("geo", "count-vertices", new Object[]{ region });
  }


  @Override
  public ServerExpression coveredBy(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for coveredBy() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for coveredBy() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "covered-by", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression coveredBy(ServerExpression region1, ServerExpression region2, String options) {
    return coveredBy(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression coveredBy(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for coveredBy() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for coveredBy() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "covered-by", new Object[]{ region1, region2, options });
  }


  @Override
  public ServerExpression covers(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for covers() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for covers() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "covers", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression covers(ServerExpression region1, ServerExpression region2, String options) {
    return covers(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression covers(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for covers() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for covers() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "covers", new Object[]{ region1, region2, options });
  }


  @Override
  public ServerExpression crosses(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for crosses() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for crosses() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "crosses", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression crosses(ServerExpression region1, ServerExpression region2, String options) {
    return crosses(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression crosses(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for crosses() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for crosses() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "crosses", new Object[]{ region1, region2, options });
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
    return new CtsExprImpl.RegionCallImpl("geo", "destination", new Object[]{ p, bearing, distance });
  }


  @Override
  public ServerExpression destination(ServerExpression p, double bearing, double distance, String options) {
    return destination(p, xs.doubleVal(bearing), xs.doubleVal(distance), (options == null) ? (XsStringVal) null : xs.string(options));
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
    return new CtsExprImpl.RegionCallImpl("geo", "destination", new Object[]{ p, bearing, distance, options });
  }


  @Override
  public ServerExpression disjoint(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for disjoint() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for disjoint() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "disjoint", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression disjoint(ServerExpression region1, ServerExpression region2, String options) {
    return disjoint(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression disjoint(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for disjoint() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for disjoint() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "disjoint", new Object[]{ region1, region2, options });
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
    return new CtsExprImpl.RegionCallImpl("geo", "ellipse-polygon", new Object[]{ center, semiMajorAxis, semiMinorAxis, azimuth, arcTolerance });
  }


  @Override
  public ServerExpression ellipsePolygon(ServerExpression center, double semiMajorAxis, double semiMinorAxis, double azimuth, double arcTolerance, String options) {
    return ellipsePolygon(center, xs.doubleVal(semiMajorAxis), xs.doubleVal(semiMinorAxis), xs.doubleVal(azimuth), xs.doubleVal(arcTolerance), (options == null) ? (XsStringVal) null : xs.string(options));
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
    return new CtsExprImpl.RegionCallImpl("geo", "ellipse-polygon", new Object[]{ center, semiMajorAxis, semiMinorAxis, azimuth, arcTolerance, options });
  }


  @Override
  public ServerExpression equals(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for equals() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for equals() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "equals", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression equals(ServerExpression region1, ServerExpression region2, String options) {
    return equals(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression equals(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for equals() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for equals() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "equals", new Object[]{ region1, region2, options });
  }


  @Override
  public ServerExpression geohashDecode(String hash) {
    return geohashDecode((hash == null) ? (XsStringVal) null : xs.string(hash));
  }


  @Override
  public ServerExpression geohashDecode(ServerExpression hash) {
    if (hash == null) {
      throw new IllegalArgumentException("hash parameter for geohashDecode() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "geohash-decode", new Object[]{ hash });
  }


  @Override
  public ServerExpression geohashDecodePoint(String hash) {
    return geohashDecodePoint((hash == null) ? (XsStringVal) null : xs.string(hash));
  }


  @Override
  public ServerExpression geohashDecodePoint(ServerExpression hash) {
    if (hash == null) {
      throw new IllegalArgumentException("hash parameter for geohashDecodePoint() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "geohash-decode-point", new Object[]{ hash });
  }


  @Override
  public ServerExpression geohashEncode(ServerExpression region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for geohashEncode() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("geo", "geohash-encode", new Object[]{ region });
  }


  @Override
  public ServerExpression geohashEncode(ServerExpression region, long geohashPrecision) {
    return geohashEncode(region, xs.integer(geohashPrecision));
  }


  @Override
  public ServerExpression geohashEncode(ServerExpression region, ServerExpression geohashPrecision) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for geohashEncode() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("geo", "geohash-encode", new Object[]{ region, geohashPrecision });
  }


  @Override
  public ServerExpression geohashEncode(ServerExpression region, long geohashPrecision, String options) {
    return geohashEncode(region, xs.integer(geohashPrecision), (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression geohashEncode(ServerExpression region, ServerExpression geohashPrecision, ServerExpression options) {
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
  public ServerExpression geohashSubhashes(ServerExpression hash) {
    if (hash == null) {
      throw new IllegalArgumentException("hash parameter for geohashSubhashes() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("geo", "geohash-subhashes", new Object[]{ hash });
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
  public ServerExpression interiorPoint(ServerExpression region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for interiorPoint() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "interior-point", new Object[]{ region });
  }


  @Override
  public ServerExpression interiorPoint(ServerExpression region, String options) {
    return interiorPoint(region, (options == null) ? (XsStringVal) null : xs.string(options));
  }


  @Override
  public ServerExpression interiorPoint(ServerExpression region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for interiorPoint() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "interior-point", new Object[]{ region, options });
  }


  @Override
  public ServerExpression intersects(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for intersects() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for intersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "intersects", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression intersects(ServerExpression region1, ServerExpression region2, String options) {
    return intersects(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression intersects(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for intersects() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for intersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "intersects", new Object[]{ region1, region2, options });
  }


  @Override
  public ServerExpression overlaps(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for overlaps() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for overlaps() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "overlaps", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression overlaps(ServerExpression region1, ServerExpression region2, String options) {
    return overlaps(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression overlaps(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for overlaps() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for overlaps() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "overlaps", new Object[]{ region1, region2, options });
  }


  @Override
  public CtsRegionSeqExpr parseWkt(String wkt) {
    return parseWkt((wkt == null) ? (XsStringVal) null : xs.string(wkt));
  }


  @Override
  public CtsRegionSeqExpr parseWkt(ServerExpression wkt) {
    return new CtsExprImpl.RegionSeqCallImpl("geo", "parse-wkt", new Object[]{ wkt });
  }


  @Override
  public CtsRegionExpr regionApproximate(ServerExpression region, double threshold) {
    return regionApproximate(region, xs.doubleVal(threshold));
  }


  @Override
  public CtsRegionExpr regionApproximate(ServerExpression region, ServerExpression threshold) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for regionApproximate() cannot be null");
    }
    if (threshold == null) {
      throw new IllegalArgumentException("threshold parameter for regionApproximate() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "region-approximate", new Object[]{ region, threshold });
  }


  @Override
  public CtsRegionExpr regionApproximate(ServerExpression region, double threshold, String options) {
    return regionApproximate(region, xs.doubleVal(threshold), (options == null) ? (XsStringVal) null : xs.string(options));
  }


  @Override
  public CtsRegionExpr regionApproximate(ServerExpression region, ServerExpression threshold, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for regionApproximate() cannot be null");
    }
    if (threshold == null) {
      throw new IllegalArgumentException("threshold parameter for regionApproximate() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "region-approximate", new Object[]{ region, threshold, options });
  }


  @Override
  public CtsRegionExpr regionClean(ServerExpression region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for regionClean() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "region-clean", new Object[]{ region });
  }


  @Override
  public CtsRegionExpr regionClean(ServerExpression region, String options) {
    return regionClean(region, (options == null) ? (XsStringVal) null : xs.string(options));
  }


  @Override
  public CtsRegionExpr regionClean(ServerExpression region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for regionClean() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "region-clean", new Object[]{ region, options });
  }


  @Override
  public ServerExpression regionContains(ServerExpression target, ServerExpression region) {
    if (target == null) {
      throw new IllegalArgumentException("target parameter for regionContains() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-contains", new Object[]{ target, region });
  }


  @Override
  public ServerExpression regionContains(ServerExpression target, ServerExpression region, String options) {
    return regionContains(target, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression regionContains(ServerExpression target, ServerExpression region, ServerExpression options) {
    if (target == null) {
      throw new IllegalArgumentException("target parameter for regionContains() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-contains", new Object[]{ target, region, options });
  }


  @Override
  public ServerExpression regionDe9im(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for regionDe9im() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for regionDe9im() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("geo", "region-de9im", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression regionDe9im(ServerExpression region1, ServerExpression region2, String options) {
    return regionDe9im(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression regionDe9im(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for regionDe9im() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for regionDe9im() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("geo", "region-de9im", new Object[]{ region1, region2, options });
  }


  @Override
  public ServerExpression regionIntersects(ServerExpression target, ServerExpression region) {
    if (target == null) {
      throw new IllegalArgumentException("target parameter for regionIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-intersects", new Object[]{ target, region });
  }


  @Override
  public ServerExpression regionIntersects(ServerExpression target, ServerExpression region, String options) {
    return regionIntersects(target, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression regionIntersects(ServerExpression target, ServerExpression region, ServerExpression options) {
    if (target == null) {
      throw new IllegalArgumentException("target parameter for regionIntersects() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "region-intersects", new Object[]{ target, region, options });
  }


  @Override
  public ServerExpression regionRelate(ServerExpression region1, String operation, ServerExpression region2) {
    return regionRelate(region1, (operation == null) ? (ServerExpression) null : xs.string(operation), region2);
  }


  @Override
  public ServerExpression regionRelate(ServerExpression region1, ServerExpression operation, ServerExpression region2) {
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
  public ServerExpression regionRelate(ServerExpression region1, String operation, ServerExpression region2, String options) {
    return regionRelate(region1, (operation == null) ? (ServerExpression) null : xs.string(operation), region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression regionRelate(ServerExpression region1, ServerExpression operation, ServerExpression region2, ServerExpression options) {
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
  public CtsRegionExpr removeDuplicateVertices(ServerExpression region) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for removeDuplicateVertices() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "remove-duplicate-vertices", new Object[]{ region });
  }


  @Override
  public CtsRegionExpr removeDuplicateVertices(ServerExpression region, String options) {
    return removeDuplicateVertices(region, (options == null) ? (XsStringVal) null : xs.string(options));
  }


  @Override
  public CtsRegionExpr removeDuplicateVertices(ServerExpression region, ServerExpression options) {
    if (region == null) {
      throw new IllegalArgumentException("region parameter for removeDuplicateVertices() cannot be null");
    }
    return new CtsExprImpl.RegionCallImpl("geo", "remove-duplicate-vertices", new Object[]{ region, options });
  }


  @Override
  public ServerExpression shortestDistance(ServerExpression p1, ServerExpression region) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for shortestDistance() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("geo", "shortest-distance", new Object[]{ p1, region });
  }


  @Override
  public ServerExpression shortestDistance(ServerExpression p1, ServerExpression region, String options) {
    return shortestDistance(p1, region, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression shortestDistance(ServerExpression p1, ServerExpression region, ServerExpression options) {
    if (p1 == null) {
      throw new IllegalArgumentException("p1 parameter for shortestDistance() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("geo", "shortest-distance", new Object[]{ p1, region, options });
  }


  @Override
  public ServerExpression toWkt(ServerExpression wkt) {
    return new XsExprImpl.StringSeqCallImpl("geo", "to-wkt", new Object[]{ wkt });
  }


  @Override
  public ServerExpression touches(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for touches() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for touches() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "touches", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression touches(ServerExpression region1, ServerExpression region2, String options) {
    return touches(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression touches(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for touches() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for touches() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "touches", new Object[]{ region1, region2, options });
  }


  @Override
  public ServerExpression validateWkt(ServerExpression wkt) {
    if (wkt == null) {
      throw new IllegalArgumentException("wkt parameter for validateWkt() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "validate-wkt", new Object[]{ wkt });
  }


  @Override
  public ServerExpression within(ServerExpression region1, ServerExpression region2) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for within() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for within() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "within", new Object[]{ region1, region2 });
  }


  @Override
  public ServerExpression within(ServerExpression region1, ServerExpression region2, String options) {
    return within(region1, region2, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression within(ServerExpression region1, ServerExpression region2, ServerExpression options) {
    if (region1 == null) {
      throw new IllegalArgumentException("region1 parameter for within() cannot be null");
    }
    if (region2 == null) {
      throw new IllegalArgumentException("region2 parameter for within() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("geo", "within", new Object[]{ region1, region2, options });
  }

  }
