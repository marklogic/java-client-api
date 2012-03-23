package com.marklogic.client.io;

import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.MatchLocation;
import com.marklogic.client.config.search.MatchSnippet;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.SearchMetrics;
import com.marklogic.client.config.search.SearchResults;
import com.marklogic.client.config.search.jaxb.Match;
import com.marklogic.client.config.search.jaxb.Metrics;
import com.marklogic.client.config.search.jaxb.Response;
import com.marklogic.client.config.search.jaxb.Result;
import com.marklogic.client.config.search.jaxb.Snippet;
import com.marklogic.client.io.marker.SearchReadHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/20/12
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class SearchHandle implements SearchReadHandle<InputStream>, SearchResults {
    static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);
    protected JAXBContext jc = null;
    protected Unmarshaller unmarshaller = null;
    protected Marshaller m = null;
    private Response jaxbResponse = null;
    private QueryDefinition querydef = null;
    SearchMetrics metrics = null;
    MatchDocumentSummary[] summary = null;

    @Override
    public Format getFormat() {
        return Format.XML;
    }

    @Override
    public void setFormat(Format format) {
        if (format != Format.XML)
            new IllegalArgumentException("SearchHandle supports the XML format only");
    }

	public SearchHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	@Override
    public Class<InputStream> receiveAs() {
        return InputStream.class;
    }

    @Override
    public void receiveContent(InputStream content) {
        try {
            jc = JAXBContext.newInstance("com.marklogic.client.config.search.jaxb");
            unmarshaller = jc.createUnmarshaller();
            m = jc.createMarshaller();
            jaxbResponse = (Response) unmarshaller.unmarshal(content);
        } catch (JAXBException e) {
            throw new MarkLogicIOException(
                    "Could not construct search results because of thrown JAXB Exception",
                    e);
        }
    }

    /** Sets the query definition used in the search.
     *
     * Calling this method always deletes any cached search results.
     *
     * @param querydef The new QueryDefinition
     */
    public void setQueryCriteria(QueryDefinition querydef) {
        this.querydef = querydef;
        jaxbResponse = null;
        metrics = null;
        summary = null;
    }
    
    @Override
    public QueryDefinition getQueryCriteria() {
        return querydef;
    }

    @Override
    public long getTotalResults() {
        if (jaxbResponse == null) {
            return -1;
        } else {
            return jaxbResponse.getTotal();
        }
    }

    @Override
    public SearchMetrics getMetrics() {
        if (jaxbResponse == null || metrics != null) {
            return metrics;
        }

        Date now = new Date();
        Metrics jaxbMetrics = jaxbResponse.getMetrics();
        long qrTime = jaxbMetrics.getQueryResolutionTime() == null ? -1 : jaxbMetrics.getQueryResolutionTime().getTimeInMillis(now);
        long frTime = jaxbMetrics.getFacetResolutionTime() == null ? -1 : jaxbMetrics.getFacetResolutionTime().getTimeInMillis(now);
        long srTime = jaxbMetrics.getSnippetResolutionTime() == null ? - 1: jaxbMetrics.getSnippetResolutionTime().getTimeInMillis(now);
        long totalTime = jaxbMetrics.getTotalTime() == null ? -1 : jaxbMetrics.getTotalTime().getTimeInMillis(now);
        metrics = new SearchMetricsImpl(qrTime, frTime, srTime, totalTime);
        return metrics;
    }

    @Override
    public MatchDocumentSummary[] getMatchResults() {
        if (jaxbResponse == null || summary != null) {
            return summary;
        }

        List<Result> results = jaxbResponse.getResult();
        summary = new MatchDocumentSummary[results.size()];
        int idx = 0;
        for (Result result : results) {
            String uri = result.getUri();
            int score = result.getScore().intValue();
            double conf = result.getConfidence();
            double fit = result.getFitness();
            String path = result.getPath();
            summary[idx] = new MatchDocumentSummaryImpl(uri, score, conf, fit, path, result);
            idx++;
        }

        return summary;
    }

    private class SearchMetricsImpl implements SearchMetrics {
        long qrTime = -1;
        long frTime = -1;
        long srTime = -1;
        long totalTime = -1;

        public SearchMetricsImpl(long qrTime, long frTime, long srTime, long totalTime) {
            this.qrTime = qrTime;
            this.frTime = frTime;
            this.srTime = srTime;
            this.totalTime = totalTime;
        }

        @Override
        public long getQueryResolutionTime() {
            return qrTime;
        }

        @Override
        public long getFacetResolutionTime() {
            return frTime;
        }

        @Override
        public long getSnippetResolutionTime() {
            return srTime;
        }

        @Override
        public long getTotalTime() {
            return totalTime;
        }
    }

    private class MatchDocumentSummaryImpl implements MatchDocumentSummary {
        private String uri = null;
        private int score = -1;
        private double conf = -1;
        private double fit = -1;
        private String path = null;
        private Result result = null;
        private MatchLocation[] locations = null;
        private String mimetype = null;
        private int byteLength = 0;

        public MatchDocumentSummaryImpl(String uri, int score, double confidence, double fitness, String path, Result result) {
            this.uri = uri;
            this.score = score;
            conf = confidence;
            fit = fitness;
            this.path = path;
            this.result = result;
        }

        @Override
        public String getUri() {
            return uri;
        }

        @Override
        public int getScore() {
            return score;
        }

        @Override
        public double getConfidence() {
            return conf;
        }

        @Override
        public double getFitness() {
            return fit;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public MatchLocation[] getMatchLocations() {
            if (locations != null) {
                return locations;
            }

            List<Snippet> jaxbSnippets = result.getSnippet();
            locations = new MatchLocation[jaxbSnippets.size()];
            int idx = 0;
            for (Snippet snippet : jaxbSnippets) {
                for (Object jaxbMatch : snippet.getMatchOrAnyOrAny()) {
                    if (jaxbMatch instanceof Match) {
                        Match match = (Match) jaxbMatch;
                        String path = match.getPath();
                        locations[idx] = new MatchLocationImpl(path, match);
                        idx++;
                    } else {
                        throw new UnsupportedOperationException("Cannot parse customized snippets");
                    }
                }
            }

            return locations;
        }

        @Override
        public void setUri(String uri) {
            throw new UnsupportedOperationException("Cannot set URI on MatchDocumentSummar");
        }

        @Override
        public DocumentIdentifier withUri(String uri) {
            if (uri != null && uri.equals(this.uri)) {
                return this;
            } else {
                throw new UnsupportedOperationException("Cannot set URI on MatchDocumentSummar");
            }
        }

        @Override
        public String getMimetype() {
            return mimetype;
        }

        @Override
        public void setMimetype(String mimetype) {
            this.mimetype = mimetype;
        }

        @Override
        public DocumentIdentifier withMimetype(String mimetype) {
            setMimetype(mimetype);
            return this;
        }

        @Override
        public int getByteLength() {
            return byteLength;
        }

        @Override
        public void setByteLength(int length) {
            byteLength = length;
        }
    }

    public class MatchLocationImpl implements MatchLocation {
        private String path = null;
        private MatchSnippet[] snippets = null;
        private Match jaxbMatch = null;

        public MatchLocationImpl(String path, Match match) {
            this.path = path;
            jaxbMatch = match;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public String getAllSnippetText() {
            getSnippets();
            String text = "";
            for (MatchSnippet snippet : snippets) {
                text += snippet.getText();
            }
            return text;
        }

        @Override
        public MatchSnippet[] getSnippets() {
            List<Serializable> jaxbContent = jaxbMatch.getContent();
            snippets = new MatchSnippet[jaxbContent.size()];
            int idx = 0;
            for (Object content : jaxbContent) {
                if (content instanceof String) {
                    snippets[idx] = new MatchSnippetImpl(false, (String) content);
                } else {
                    snippets[idx] = new MatchSnippetImpl(true, (String) ((JAXBElement) content).getValue());
                }
                idx++;
            }

            return snippets;
        }
    }

    private class MatchSnippetImpl implements MatchSnippet {
        private boolean high = false;
        private String text = null;

        public MatchSnippetImpl(boolean high, String text) {
            this.high = high;
            this.text = text;
        }

        @Override
        public boolean isHighlighted() {
            return high;
        }

        @Override
        public String getText() {
            return text;
        }
    }
}
