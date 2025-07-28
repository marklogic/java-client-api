/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.pojo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Use this annotation to specify that a Geospatial Path Index is needed
 * for this pojo property. The value should follow the rules
 * for any Geospatial Path Index in MarkLogic Server with coordinate
 * system "wgs84" and point format "point". Specifically, the value
 * should be in "{latititude} {longitude}" point format where {latitude}
 * and {longitude} are numeric geospatial wgs84 position values.
 *
 * This annotation can be associated with a public field:
 * <pre>    import com.marklogic.client.pojo.annotation.GeospatialPathIndexProperty;
 *    public class MyClass {
 *      <b>{@literal @}GeospatialPathIndexProperty</b>
 *      public String myGeoProperty;
 *    }</pre>
 *
 * or with a public getter method:
 * <pre>
 *    public class MyClass {
 *      private String myGeoProperty;
 *      <b>{@literal @}GeospatialPathIndexProperty</b>
 *      public String getMyGeoProperty() {
 *        return myGeoProperty;
 *      }
 *      // ... setter methods ...
 *    }</pre>
 *
 * or with a public setter method:
 * <pre>
 *    public class MyClass {
 *      private String myGeoProperty;
 *      // ... getter methods ...
 *      <b>{@literal @}GeospatialPathIndexProperty</b>
 *      public void setMyGeoProperty(String myGeoProperty) {
 *        this.myGeoProperty = myGeoProperty;
 *      }
 *    }</pre>
 * Run
 * {@link com.marklogic.client.pojo.util.GenerateIndexConfig} to generate
 * a package that can be used by administrators to create the indexes in
 * MarkLogic Server.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GeospatialPathIndexProperty {
}
