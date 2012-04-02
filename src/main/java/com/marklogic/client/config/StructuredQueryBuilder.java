/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.config;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/22/12
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */

// TODO: Implement the rest of the query types

public class StructuredQueryBuilder {
    private String optionsName = null;

    public enum Ordering {
        ORDERED, UNORDERED;
    }
    
    public enum Operator {
        LT, LE, GT, GE, EQ, NE
    }
    
    public StructuredQueryBuilder(String optionsName) {
        this.optionsName = optionsName;
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
    
    public TermQuery term(String term) {
        return new TermQuery(term);
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

    /*
    public DocumentFragmentQuery documentFragment(StructuredQueryDefinition... queries) { ... }
    public PropertiesQuery properties(StructuredQueryDefinition... queries) { ... }
    public LocksQuery locks(StructuredQueryDefinition... queries) { ... }
    public ElementConstraintQuery elementConstraint(String constraintName, StructuredQueryDefinition... queries) { ... }
    public PropertiesConstraintQuery propertiesConstraint(String constraintName, StructuredQueryDefinition... queries) { ... }
    public CollectionConstraintQuery collectionConstraint(String constraintName, String uri) { ... }
    public ValueConstraintQuery valueConstraint(String constraintName, String value, double weight) { ... }
    public WordConstraintQuery wordConstraint(String constraintName, String word, double weight) { ... }
    public RangeConstraintQuery rangeConstraint(String constraintName, Operator operator, String value) { ... }
    public GeospatialConstraintQuery geospatialConstraint(String constraintName, Region... regions) { ... }
    */

    /* ************************************************************************************* */

    private abstract class AbstractStructuredQuery implements StructuredQueryDefinition {
        String uri = null;

        public String serialize() {
            if (uri == null) {
                setOptionsName(optionsName);
            }
            return "<query xmlns='http://marklogic.com/appservices/search'>" + innerSerialize() + "</query>";
        }

        public String getOptionsName() {
            return uri;
        }

        public void setOptionsName(String uri) {
            this.uri = uri;
        }

        protected abstract String innerSerialize();
    }
    
    public class AndQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition queries[] = null;

        public AndQuery(StructuredQueryDefinition... queries) {
            this.queries = queries;
        }

        public String innerSerialize() {
            String s = "<and-query>";
            for (StructuredQueryDefinition q : queries) {
                s += q.serialize();
            }
            return s + "</and-query>";
        }
    }

    public class OrQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition queries[] = null;

        public OrQuery(StructuredQueryDefinition... queries) {
            this.queries = queries;
        }

        public String innerSerialize() {
            String s = "<or-query>";
            for (StructuredQueryDefinition q : queries) {
                s += q.serialize();
            }
            return s + "</or-query>";
        }
    }

    public class NotQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition query = null;

        public NotQuery(StructuredQueryDefinition query) {
            this.query = query;
        }

        public String innerSerialize() {
            return "<not-query>" + query.serialize() + "</not-query>";
        }
    }

    public class AndNotQuery extends AbstractStructuredQuery {
        StructuredQueryDefinition positive = null;
        StructuredQueryDefinition negative = null;

        public AndNotQuery(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
            this.positive = positive;
            this.negative = negative;
        }

        public String innerSerialize() {
            return "<and-not-query>" + positive.serialize() + negative.serialize() + "</and-not-query>";
        }
    }

    public class DocumentQuery extends AbstractStructuredQuery {
        private String uri = null;

        public DocumentQuery(String uri) {
            this.uri = uri;
        }

        public String innerSerialize() {
            return "<document-query><uri>" + uri + "</uri></document-query>";
        }
    }

    public class TermQuery extends AbstractStructuredQuery {
        private String term = null;

        public TermQuery(String term) {
            this.term = term;
        }

        public String innerSerialize() {
            return "<term><text>" + term + "</text></term>";
        }
    }

    public class NearQuery extends AbstractStructuredQuery {
        private int distance = 10;
        private double weight = 1.0;
        private Ordering order = Ordering.UNORDERED;
        private StructuredQueryDefinition queries[] = null;

        public NearQuery(StructuredQueryDefinition... queries) {
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
                s += q.serialize();
            }
            s += "<ordered>" + (order == Ordering.ORDERED) + "</ordered>";
            s += "<distance>" + distance + "</distance>";
            s += "<distance-weight>" + weight + "</distance-weight>";
            return s + "</near-query>";
        }
    }

    public class CollectionQuery extends AbstractStructuredQuery {
        private String uris[] = null;

        public CollectionQuery(String... uris) {
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
}
