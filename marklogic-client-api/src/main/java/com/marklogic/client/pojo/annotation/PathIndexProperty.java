/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.pojo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Use this annotation to specify that a Path Index (required for range
 * queries) is needed for this pojo property.  A path index is
 * used rather than an element range index because we can restrict matches
 * to only properties that are direct children of this class type.  We
 * do this by including the class name in the path specification on the
 * path index.
 *
 * This annotation can be associated with a public field:
 * <pre>    import com.marklogic.client.pojo.annotation.PathIndexProperty;
 *    public class MyClass {
 *      <b>{@literal @}PathIndexProperty</b>
 *      public Long myLongValue;
 *    }</pre>
 *
 * or with a public getter method:
 * <pre>
 *    public class MyClass {
 *      private Long myLongValue;
 *      <b>{@literal @}PathIndexProperty</b>
 *      public Long getMyLongValue() {
 *        return myLongValue;
 *      }
 *      // ... setter methods ...
 *    }</pre>
 *
 * or with a public setter method:
 * <pre>
 *    public class MyClass {
 *      private Long myLongValue;
 *
 *      // ... getter methods ...
 *
 *      <b>{@literal @}PathIndexProperty</b>
 *      public void setMyLongValue(Long myLongValue) {
 *        this.myLongValue = myLongValue;
 *      }
 *    }</pre>
 * Run
 * {@link com.marklogic.client.pojo.util.GenerateIndexConfig} to generate
 * a package that can be used by administrators to create the indexes in
 * MarkLogic Server.
 * @see com.marklogic.client.pojo.PojoQueryBuilder#range
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PathIndexProperty {
  ScalarType scalarType();

  public enum ScalarType {
    STRING            ("string"),
    INT               ("int"),
    UNSIGNED_INT      ("unsignedInt"),
    LONG              ("long"),
    UNSIGNED_LONG     ("unsignedLong"),
    FLOAT             ("float"),
    DOUBLE            ("double"),
    DECIMAL           ("decimal"),
    DATETIME          ("dateTime"),
    TIME              ("time"),
    DATE              ("date"),
    GYEARMONTH        ("gYearMonth"),
    GYEAR             ("gYear"),
    GMONTH            ("gMonth"),
    GDAY              ("gDay"),
    YEARMONTH_DURATION("yearMonthDuration"),
    DAYTIME_DURATION  ("dayTimeDuration"),
    ANYURI            ("anyURI");

    private String string;
    private ScalarType(String string) {
      this.string = string;
    };

    @Override
    public String toString() { return string; };
  };
}
