/*
 * Copyright 2012-2013 MarkLogic Corporation
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
package com.marklogic.client.query;

// TODO: Implement the rest of the query types

import com.marklogic.client.impl.AbstractQueryDefinition;

public class StructuredQueryBuilder {
    private String builderOptionsURI = null;

    public enum Ordering {
        ORDERED, UNORDERED;
    }
    
	public enum Operator {
        LT, LE, GT, GE, EQ, NE
    }
    
    public StructuredQueryBuilder(String optionsName) {
        builderOptionsURI = optionsName;
    }

    public OrQuery or(StructuredQueryDefinition... queries) {
        return new OrQuery(queries);
    }

    public NotQuery not(StructuredQueryDefinition query) {
        return new NotQuery(query);
    }

    public AndQuery and(StructuredQueryDefinition... queries) {
        return new AndQuery(queries);
    }

    public AndNotQuery andNot(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
        return new AndNotQuery(positive, negative);
    }

    public DocumentQuery document(String uri) {
        return new DocumentQuery(uri);
    }
    
    public TermQuery term(String... terms) {
        return new TermQuery(0.0, terms);
    }

    public TermQuery term(double weight, String... terms) {
        return new TermQuery(weight, terms);
    }

    public NearQuery near(StructuredQueryDefinition... queries) {
        return new NearQuery(queries);
    }

    public NearQuery near(int distance, double weight, Ordering order, StructuredQueryDefinition... queries) {
        return new NearQuery(distance, weight, order, queries);
    }

    public CollectionQuery collection(String... uris) {
        return new CollectionQuery(uris);
    }

    public DirectoryQuery directory(boolean isInfinite, String... uris) {
        return new DirectoryQuery(isInfinite, uris);
    }

    public DocumentFragmentQuery documentFragment(StructuredQueryDefinition query) {
        return new DocumentFragmentQuery(query);
    }

    public PropertiesQuery properties(StructuredQueryDefinition query) {
        return new PropertiesQuery(query);
    }

    public LocksQuery locks(StructuredQueryDefinition query) {
        return new LocksQuery(query);
    }

    public ElementConstraintQuery elementConstraint(String constraintName, StructuredQueryDefinition query) {
        return new ElementConstraintQuery(constraintName, query);
    }

    public PropertiesConstraintQuery propertiesConstraint(String constraintName, StructuredQueryDefinition query) {
        return new PropertiesConstraintQuery(constraintName, query);
    }

    public CollectionConstraintQuery collectionConstraint(String constraintName, String... uris) {
        return new CollectionConstraintQuery(constraintName, uris);
    }

    public ValueConstraintQuery valueConstraint(String constraintName, String... values) {
        return new ValueConstraintQuery(constraintName, values);
    }

    public ValueConstraintQuery valueConstraint(String constraintName, double weight, String... values) {
        return new ValueConstraintQuery(constraintName, weight, values);
    }

    public WordConstraintQuery wordConstraint(String constraintName, String... words) {
        return new WordConstraintQuery(constraintName, words);
    }

    public WordConstraintQuery wordConstraint(String constraintName, double weight, String... words) {
        return new WordConstraintQuery(constraintName, weight, words);
    }

    public RangeConstraintQuery rangeConstraint(String constraintName, Operator operator, String... values) {
        return new RangeConstraintQuery(constraintName, operator, values);
    }

    public GeospatialConstraintQuery geospatialConstraint(String constraintName, Region... regions) {
        return new GeospatialConstraintQuery(constraintName, regions);
    }

    public Point point(double latitude, double longitude) {
        return new Point(latitude, longitude);
    }

    public Circle circle(double latitude, double longitude, double radius) {
        return new Circle(latitude, longitude, radius);
    }

    public Circle circle(Point center, double radius) {
        return new Circle(center.getLatitude(), center.getLongitude(), radius);
    }

    public Box box(double south, double west, double north, double east) {
        return new Box(south, west, north, east);
    }

    public Polygon polygon(Point... points) {
        return new Polygon(points);
    }

    public CustomConstraintQuery customConstraint(String constraintName, String... text) {
        return new CustomConstraintQuery(constraintName, text);
    }

    /* ************************************************************************************* */

    private abstract class AbstractStructuredQuery extends AbstractQueryDefinition implements StructuredQueryDefinition {
        public AbstractStructuredQuery() {
            optionsUri = builderOptionsURI;
        }

        public String serialize() {
            if (optionsUri != null) {
                setOptionsName(optionsUri);
            }
            return "<query xmlns='http://marklogic.com/appservices/search'>" + innerSerialize() + "</query>";
        }

        public String getOptionsName() {
            return optionsUri;
        }

        public void setOptionsName(String uri) {
            optionsUri = uri;
        }

        protected abstract String innerSerialize();
    }
    
    public class AndQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition queries[] = null;

        public AndQuery(StructuredQueryDefinition... queries) {
            super();
            this.queries = queries;
        }

        public String innerSerialize() {
            String s = "<and-query>";
            for (StructuredQueryDefinition q : queries) {
                s += ((AbstractStructuredQuery) q).innerSerialize();
            }
            return s + "</and-query>";
        }
    }

    public class OrQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition queries[] = null;

        public OrQuery(StructuredQueryDefinition... queries) {
            super();
            this.queries = queries;
        }

        public String innerSerialize() {
            String s = "<or-query>";
            for (StructuredQueryDefinition q : queries) {
                s += ((AbstractStructuredQuery) q).innerSerialize();
            }
            return s + "</or-query>";
        }
    }

    public class NotQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition query = null;

        public NotQuery(StructuredQueryDefinition query) {
            super();
            this.query = query;
        }

        public String innerSerialize() {
            return "<not-query>" + ((AbstractStructuredQuery) query).innerSerialize() + "</not-query>";
        }
    }

    public class AndNotQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition positive = null;
        StructuredQueryDefinition negative = null;

        public AndNotQuery(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
            super();
            this.positive = positive;
            this.negative = negative;
        }

        public String innerSerialize() {
            return "<and-not-query><positive-query>"
                    + ((AbstractStructuredQuery) positive).innerSerialize()
                    + "</positive-query><negative-query>"
                    + ((AbstractStructuredQuery) negative).innerSerialize()
                    + "</negative-query></and-not-query>";
        }
    }

    public class DocumentQuery extends AbstractStructuredQuery {
        private String uri = null;

        public DocumentQuery(String uri) {
            super();
            this.uri = uri;
        }

        public String innerSerialize() {
            return "<document-query><uri>" + uri + "</uri></document-query>";
        }
    }

    public class TermQuery extends AbstractStructuredQuery {
        private String[] terms = null;
        private double weight = 0.0;

        public TermQuery(double weight, String... terms) {
            super();
            this.weight = weight;
            this.terms = terms;
        }

        public String innerSerialize() {
            String s = "<term-query>";
            for (String text : terms) {
                s += "<text>" + text + "</text>";
            }
            if (weight != 0.0) {
                s += "<weight>" + weight + "</weight>";
            }
            return s + "</term-query>";
        }
    }

    public class NearQuery extends AbstractStructuredQuery {
        private int distance = -1;
        private double weight = -1.0;
        private Ordering order = null;
        private StructuredQueryDefinition queries[] = null;

        public NearQuery(StructuredQueryDefinition... queries) {
            super();
            this.queries = queries;
        }

        public NearQuery(int distance, double weight, Ordering order, StructuredQueryDefinition... queries) {
            this.distance = distance;
            this.weight = weight;
            this.order = order;
            this.queries = queries;
        }

        public String innerSerialize() {
            String s = "<near-query>";
            for (StructuredQueryDefinition q : queries) {
                s += ((AbstractStructuredQuery) q).innerSerialize();
            }
            if (order != null) {
                s += "<ordered>" + (order == Ordering.ORDERED) + "</ordered>";
            }
            if (distance >= 0) {
                s += "<distance>" + distance + "</distance>";
            }
            if (weight >= 0.0) {
                s += "<distance-weight>" + weight + "</distance-weight>";
            }
            return s + "</near-query>";
        }
    }

    public class CollectionQuery extends AbstractStructuredQuery {
        private String uris[] = null;

        public CollectionQuery(String... uris) {
            super();
            this.uris = uris;
        }

        public String innerSerialize() {
            String s = "<collection-query>";
            if (uris != null) {
                for (String uri : uris) {
                    s += "<uri>" + uri + "</uri>";
                }
            }
            return s + "</collection-query>";
        }
    }

    public class DirectoryQuery extends AbstractStructuredQuery {
        private String uris[] = null;
        private boolean infinite = false;

        public DirectoryQuery(boolean isInfinite, String... uris) {
            super();
            infinite = isInfinite;
            this.uris = uris;
        }

        public String innerSerialize() {
            String s = "<directory-query>";
            if (uris != null) {
                for (String uri : uris) {
                    s += "<uri>" + uri + "</uri>";
                }
            }
            s += "<infinite>" + infinite + "</infinite>";
            return s + "</directory-query>";
        }
    }

    public class DocumentFragmentQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition query = null;

        public DocumentFragmentQuery(StructuredQueryDefinition query) {
            super();
            this.query = query;
        }

        public String innerSerialize() {
            return "<document-fragment-query>"
                    + ((AbstractStructuredQuery) query).innerSerialize()
                    + "</document-fragment-query>";
        }
    }

    public class PropertiesQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition query = null;

        public PropertiesQuery(StructuredQueryDefinition query) {
            super();
            this.query = query;
        }

        public String innerSerialize() {
            return "<properties-query>"
                    + ((AbstractStructuredQuery) query).innerSerialize()
                    + "</properties-query>";
        }
    }

    public class LocksQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition query = null;

        public LocksQuery(StructuredQueryDefinition query) {
            super();
            this.query = query;
        }

        public String innerSerialize() {
            return "<locks-query>"
                    + ((AbstractStructuredQuery) query).innerSerialize()
                    + "</locks-query>";
        }
    }

    public class ElementConstraintQuery extends AbstractStructuredQuery {
        String name = null;
        StructuredQueryDefinition query = null;

        public ElementConstraintQuery(String constraintName, StructuredQueryDefinition query) {
            super();
            name = constraintName;
            this.query = query;
        }

        public String innerSerialize() {
            return "<element-constraint-query>"
                    + "<constraint-name>" + name + "</constraint-name>"
                    + ((AbstractStructuredQuery) query).innerSerialize()
                    + "</element-constraint-query>";
        }
    }

    public class PropertiesConstraintQuery extends AbstractStructuredQuery {
        String name = null;
        StructuredQueryDefinition query = null;

        public PropertiesConstraintQuery(String constraintName, StructuredQueryDefinition query) {
            super();
            name = constraintName;
            this.query = query;
        }

        public String innerSerialize() {
            return "<properties-constraint-query>"
                    + "<constraint-name>" + name + "</constraint-name>"
                    + ((AbstractStructuredQuery) query).innerSerialize()
                    + "</properties-constraint-query>";
        }
    }

    public class CollectionConstraintQuery extends AbstractStructuredQuery {
        String name = null;
        String[] uris = null;

        public CollectionConstraintQuery(String constraintName, String... uris) {
            super();
            name = constraintName;
            this.uris = uris;
        }

        public String innerSerialize() {
            String s = "<collection-constraint-query>"
                        + "<constraint-name>" + name + "</constraint-name>";

            for (String uri : uris) {
                s += "<uri>" + uri + "</uri>";
            }

            return s +"</collection-constraint-query>";
        }
    }

    public class ValueConstraintQuery extends AbstractStructuredQuery {
        String name = null;
        String[] values = null;
        double weight = -1.0;

        public ValueConstraintQuery(String constraintName, String... values) {
            super();
            name = constraintName;
            this.values = values;
        }

        public ValueConstraintQuery(String constraintName, double weight, String... values) {
            name = constraintName;
            this.values = values;
            this.weight = weight;
        }

        public String innerSerialize() {
            String s = "<value-constraint-query>"
                    + "<constraint-name>" + name + "</constraint-name>";

            for (String value : values) {
                s += "<text>" + value + "</text>";
            }

            if (weight >= 0.0) {
                s += "<weight>" + weight + "</weight>";
            }

            return s +"</value-constraint-query>";
        }
    }

    public class WordConstraintQuery extends AbstractStructuredQuery {
        String name = null;
        String[] words = null;
        double weight = -1.0;

        public WordConstraintQuery(String constraintName, String... words) {
            super();
            name = constraintName;
            this.words = words;
        }

        public WordConstraintQuery(String constraintName, double weight, String... words) {
            name = constraintName;
            this.words = words;
            this.weight = weight;
        }

        public String innerSerialize() {
            String s = "<word-constraint-query>"
                    + "<constraint-name>" + name + "</constraint-name>";

            for (String word : words) {
                s += "<text>" + word + "</text>";
            }

            if (weight >= 0.0) {
                s += "<weight>" + weight + "</weight>";
            }

            return s +"</word-constraint-query>";
        }
    }

    public class RangeConstraintQuery extends AbstractStructuredQuery {
        String name = null;
        String[] values = null;
        Operator operator = null;

        public RangeConstraintQuery(String constraintName, Operator operator, String... values) {
            super();
            name = constraintName;
            this.values = values;
            this.operator = operator;
        }

        public String innerSerialize() {
            String s = "<range-constraint-query>"
                    + "<constraint-name>" + name + "</constraint-name>";

            for (String value : values) {
                s += "<value>" + value + "</value>";
            }

            s += "<range-operator>" + operator + "</range-operator>";

            return s +"</range-constraint-query>";
        }
    }

    public class GeospatialConstraintQuery extends AbstractStructuredQuery {
        String name = null;
        Region[] regions = null;

        public GeospatialConstraintQuery(String constraintName, Region... regions) {
            super();
            name = constraintName;
            this.regions = regions;
        }

        public String innerSerialize() {
            String s = "<geospatial-constraint-query>"
                    + "<constraint-name>" + name + "</constraint-name>";

            for (Region region : regions) {
                s += region.serialize();
            }

            return s +"</geospatial-constraint-query>";
        }
    }

    public class CustomConstraintQuery extends AbstractStructuredQuery {
        private String terms[] = null;
        private String name = null;

        public CustomConstraintQuery(String constraintName, String... terms) {
            super();
            name = constraintName;
            this.terms = terms;
        }

        public String innerSerialize() {
            String s = "<custom-constraint-query>"
                    + "<constraint-name>" + name + "</constraint-name>";
            for (String term : terms) {
                s += "<text>" + term + "</text>";
            }
            return s + "</custom-constraint-query>";
        }
    }

    /* ************************************************************************************* */

    public abstract class Region {
        public abstract String serialize();
    }

    public class Point extends Region {
        private double lat = 0.0;
        private double lng = 0.0;

        public Point(double latitude, double longitude) {
            lat = latitude;
            lng = longitude;
        }

        public double getLatitude() {
            return lat;
        }

        public double getLongitude() {
            return lng;
        }

        public String serialize() {
            return "<point><latitude>" + lat + "</latitude><longitude>" + lng + "</longitude></point>";
        }
    }

    public class Circle extends Region {
        private Point center = null;
        private double radius = 0.0;

        public Circle(double latitude, double longitude, double radius) {
            center = new Point(latitude, longitude);
            this.radius = radius;
        }

        public String serialize() {
            return "<circle><radius>" + radius + "</radius>" + center.serialize() + "</circle>";
        }
    }

    public class Box extends Region {
        private double south, west, north, east;

        public Box(double south, double west, double north, double east) {
            this.south = south;
            this.west = west;
            this.north = north;
            this.east = east;
        }

        public String serialize() {
            String s = "<box>";
            s += "<south>" + south + "</south>";
            s += "<west>" + west + "</west>";
            s += "<north>" + north + "</north>";
            s += "<east>" + east + "</east>";
            return s + "</box>";
        }
    }

    public class Polygon extends Region {
        private Point[] points;

        public Polygon(Point... points) {
            this.points = points;
        }

        public String serialize() {
            String s = "<polygon>";
            for (Point point : points) {
                s += point.serialize();
            }
            return s + "</polygon>";
        }
    }
}
