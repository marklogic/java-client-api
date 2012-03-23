package com.marklogic.client.config.search;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/22/12
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */

// TODO: Implement the rest of the query types

public class StructuredQueryBuilder {
    String optionsName = null;

    public StructuredQueryBuilder(String optionsName) {
        this.optionsName = optionsName;
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
    
    private abstract class AbstractStructuredQuery implements StructuredQueryDefinition {
        String uri = null;

        public String serialize() {
            if (uri == null) {
                setOptionsUri(optionsName);
            }
            return "<query xmlns='http://marklogic.com/appservices/search'>" + innerSerialize() + "</query>";
        }

        public String getOptionsUri() {
            return uri;
        }

        public void setOptionsUri(String uri) {
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
}
