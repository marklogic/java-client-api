package com.marklogic.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;

import com.marklogic.client.config.search.KeyValueQueryDefinition;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.StringQueryDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.AbstractDocumentManager.Metadata;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.ElementLocator;
import com.marklogic.client.KeyLocator;
import com.marklogic.client.ValueLocator;
import com.marklogic.client.config.search.MarkLogicIOException;
import com.marklogic.client.config.search.SearchOptions;
import com.marklogic.client.io.marker.OutputStreamSender;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.Boundary;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;

public class JerseyServices implements RESTServices {
	static final private Logger logger = LoggerFactory
			.getLogger(JerseyServices.class);

	private Client client;
	private WebResource connection;

	private String QUERY_OPTIONS_BASE = "/config/search/";

	public JerseyServices() {
	}

	public void connect(String host, int port, String user, String password,
			Authentication type) {
		if (logger.isInfoEnabled())
			logger.info("Connecting to {} at {} as {}", new Object[] { host,
					port, user });

		ClientConfig config = new DefaultClientConfig();
		client = ApacheHttpClient.create(config);
		if (type == Authentication.BASIC)
			client.addFilter(new HTTPBasicAuthFilter(user, password));
		else if (type == Authentication.DIGEST)
			client.addFilter(new HTTPDigestAuthFilter(user, password));
		else
			throw new RuntimeException(
					"Internal error - unknown authentication type: "
							+ type.name());
		connection = client.resource("http://" + host + ":" + port + "/v1/");
	}

	public void release() {
		logger.info("Releasing connection");

		connection = null;
		client.destroy();
	}

	public void deleteDocument(String uri, String transactionId, Set<Metadata> categories) {
		logger.info("Deleting {} in transaction {}", uri, transactionId);

		ClientResponse response = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId, null)).delete(
				ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("delete failed " + status);
		}
	}

	// TODO:  does an Input Stream or Reader handle need to cache the response so it can close the response?

	public <T> T getDocument(String uri, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String mimetype, Class<T> as) {
		logger.info("Getting {} in transaction {}", uri, transactionId);

		WebResource.Builder resource = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId, extraParams)
				).accept(mimetype);
		if (extraParams != null && extraParams.containsKey("range"))
			resource = resource.header("range", extraParams.get("range"));
		ClientResponse response = resource.get(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.OK) {
			response.close();
			throw new RuntimeException("read failed " + status);
		}

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return entity;
	}

	public Object[] getDocument(String uri, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String[] mimetypes, Class[] as) {
		logger.info("Getting multipart for {} in transaction {}", uri, transactionId);

		if (mimetypes == null || mimetypes.length == 0)
			throw new RuntimeException("mime types not specified for read");
		if (as == null || as.length == 0)
			throw new RuntimeException("handle classes not specified for read");
		if (mimetypes.length != as.length)
			throw new RuntimeException(
					"mistmatch between mime types and handle classes for read");

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transactionId, extraParams, true);
		if (mimetypes[0].startsWith("application/")) {
			docParams.add("format",
					mimetypes[0].substring("application/".length()));
		}

		ClientResponse response = makeDocumentResource(docParams).accept(
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE))
				.get(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.OK) {
			response.close();
			throw new RuntimeException("read failed " + status);
		}

		MultiPart entity = response.getEntity(MultiPart.class);
		if (entity == null)
			return null;

		List<BodyPart> partList = entity.getBodyParts();
		if (partList == null)
			return null;

		int partCount = partList.size();
		if (partCount == 0)
			return null;
		if (partCount != as.length)
			throw new RuntimeException("read expected " + as.length
					+ " parts but got " + partCount + " parts");

		Object[] parts = new Object[partCount];
		for (int i = 0; i < partCount; i++) {
			parts[i] = partList.get(i).getEntityAs(as[i]);
		}

		response.close();

		return parts;
	}

	public Map<String, List<String>> head(String uri, String transactionId) {
		logger.info("Requesting head for {} in transaction {}", uri,
				transactionId);

		ClientResponse response = makeDocumentResource(
				makeDocumentParams(uri, null, transactionId, null)).head();
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.NOT_FOUND) {
			return null;
		}
		if (status != ClientResponse.Status.OK) {
			throw new RuntimeException("head failed "
					+ response.getClientResponseStatus());
		}
		return response.getHeaders();
	}
	public void putDocument(String uri, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String mimetype, Object value) {
		logger.info("Putting {} in transaction {}", uri, transactionId);

		ClientResponse response = makeDocumentResource(
					makeDocumentParams(uri, categories, transactionId, extraParams)
				).type(mimetype).put(ClientResponse.class,
						(value instanceof OutputStreamSender) ?
								new StreamingOutputImpl((OutputStreamSender) value) : value);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status != ClientResponse.Status.CREATED
				&& status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("write failed " + status);
		}
	}
	public void putDocument(String uri, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String[] mimetypes, Object[] values) {
		logger.info("Putting multipart for {} in transaction {}", uri, transactionId);

		if (mimetypes == null || mimetypes.length == 0)
			throw new RuntimeException("mime types not specified for write");
		if (values == null || values.length == 0)
			throw new RuntimeException("values not specified for write");
		if (mimetypes.length != values.length)
			throw new RuntimeException(
					"mistmatch between mime types and values for write");

		MultiPart multiPart = new MultiPart();
		multiPart.setMediaType(new MediaType("multipart","mixed"));
		for (int i=0; i < mimetypes.length; i++) {
			String[] typeParts = 
				mimetypes[i].contains("/") ? mimetypes[i].split("/", 2) : null;
			multiPart = multiPart.bodyPart(new BodyPart(
					(values[i] instanceof OutputStreamSender) ?
							new StreamingOutputImpl((OutputStreamSender) values[i]) : values[i],
				typeParts != null ?
					new MediaType(typeParts[0],typeParts[1]) : MediaType.WILDCARD_TYPE
					));
		}

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transactionId, extraParams, true);

		ClientResponse response = makeDocumentResource(docParams).type(
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE))
				.put(ClientResponse.class, multiPart);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status != ClientResponse.Status.CREATED
				&& status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("write failed " + status);
		}
	}

	public String openTransaction() {
        logger.info("Opening transaction");

        MultivaluedMap<String, String> transParams = new MultivaluedMapImpl();
        transParams.add("name", "java-client-"+new Random().nextLong());

        ClientResponse response = connection.path("transactions").queryParams(transParams).post(ClientResponse.class);
        ClientResponse.Status status = response.getClientResponseStatus();
        if (status != ClientResponse.Status.SEE_OTHER) {
            response.close();
            throw new RuntimeException("transaction open failed "+status);
        }

        String location = response.getHeaders().getFirst("Location");
        response.close();
        if (location == null)
            throw new RuntimeException("transaction open failed to provide location");
        if (!location.contains("/"))
            throw new RuntimeException("transaction open produced invalid location "+location);

        return location.substring(location.lastIndexOf("/") + 1);
	}
	public void commitTransaction(String transactionId) {
        logger.info("Committing transaction {}",transactionId);

        if (transactionId == null)
            throw new RuntimeException("Committing transaction without id");

        ClientResponse response = connection.path("transactions/"+transactionId).put(ClientResponse.class);
        ClientResponse.Status status = response.getClientResponseStatus();
        response.close();
        if (status != ClientResponse.Status.NO_CONTENT) {
            throw new RuntimeException("transaction commit failed "+status);
        }
	}
	public void rollbackTransaction(String transactionId) {
        logger.info("Rolling back transaction {}",transactionId);

        if (transactionId == null)
            throw new RuntimeException("Rolling back transaction without id");

        ClientResponse response = connection.path("transactions/"+transactionId).delete(ClientResponse.class);
        ClientResponse.Status status = response.getClientResponseStatus();
        response.close();
        if (status != ClientResponse.Status.NO_CONTENT) {
            throw new RuntimeException("transaction rollback failed "+status);
        }
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri, Set<Metadata> categories, String transactionId, Map<String,String> extraParams) {
		return makeDocumentParams(uri, categories, transactionId, extraParams, false);
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri,
			Set<Metadata> categories, String transactionId, Map<String, String> extraParams, boolean withContent) {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();
		if (extraParams != null && extraParams.size() > 0) {
			for (Map.Entry<String, String> entry: extraParams.entrySet()) {
				String extraKey = entry.getKey();
				if (!"range".equalsIgnoreCase(extraKey))
					docParams.putSingle(extraKey, entry.getValue());
			}
		}
		docParams.add("uri", uri);
		if (categories == null || categories.size() == 0) {
			docParams.add("category", "content");
		} else {
			if (withContent)
				docParams.add("category", "content");
			if (categories.contains(Metadata.ALL)) {
				for (String category : new String[] { "collections",
						"permissions", "properties", "quality" })
					docParams.add("category", category);
			} else {
				for (Metadata category : categories)
					docParams.add("category", category.name().toLowerCase());
			}
		}
		if (transactionId != null)
			docParams.add("txid", transactionId);
		return docParams;
	}

	private WebResource makeDocumentResource(
			MultivaluedMap<String, String> queryParams) {
		return connection.path("documents").queryParams(queryParams);
	}

    // FIXME: is this even close to reasonable?
    public <T> T search(Class<T> as, QueryDefinition queryDef, long start, String transactionId) {
        MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();
        ClientResponse response = null;

        if (start > 1) {
            docParams.add("start", ""+start);
        }
        
        if (queryDef instanceof StringQueryDefinition) {
            String text = ((StringQueryDefinition) queryDef).getCriteria();
            logger.info("Searching for {} in transaction {}", text, transactionId);

            docParams.add("q", text);
            response = connection.path("search").queryParams(docParams).get(ClientResponse.class);
        } else if (queryDef instanceof KeyValueQueryDefinition) {
            Map<ValueLocator, String> pairs = ((KeyValueQueryDefinition) queryDef);
            logger.info("Searching for keys/values in transaction {}", transactionId);

            for (ValueLocator loc : pairs.keySet()) {
                if (loc instanceof KeyLocator) {
                    docParams.add("key", ((KeyLocator) loc).getKey());
                } else {
                    ElementLocator eloc = (ElementLocator) loc;
                    docParams.add("element", eloc.getElement().toString());
                    if (eloc.getAttribute() != null) {
                        docParams.add("attribute", eloc.getAttribute().toString());
                    }
                }
                docParams.add("value", pairs.get(loc));
            }
            response = connection.path("keyvalue").queryParams(docParams).get(ClientResponse.class);
        } else {
            throw new UnsupportedOperationException("Cannot search with " + queryDef.getClass().getName());
        }
        
        ClientResponse.Status status = response.getClientResponseStatus();
        if (status != ClientResponse.Status.OK) {
            response.close();
            throw new RuntimeException("search failed "+status);
        }
        return response.getEntity(as);
    }

	// TODO rewrite to JSON, XML, or JAXB output.
	public SearchOptions get(String searchOptionsName) {
		ClientResponse clientResponse = connection.path(QUERY_OPTIONS_BASE + searchOptionsName)
				.accept("application/xml").get(ClientResponse.class);

		try {
			return new SearchOptions(clientResponse.getEntityInputStream());
		} catch (JAXBException e) {
			throw new MarkLogicIOException("Could not get options from server",
					e);

		}
	}

	public void put(String searchOptionsName, SearchOptions options) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			options.writeTo(baos);
		} catch (JAXBException e) {
			throw new MarkLogicIOException(
					"Could not build options to send to server", e);
		}
		ClientResponse clientResponse = connection.path(QUERY_OPTIONS_BASE + searchOptionsName)
				.type("application/xml")
				.put(ClientResponse.class, baos.toByteArray());

		
	}

	public void delete(String searchOptionsName) {
		ClientResponse clientResponse = connection.path(QUERY_OPTIONS_BASE + searchOptionsName)
				.accept("application/xml").delete(ClientResponse.class);

	}

	// namespaces, etc.
	public <T> T getValue(String type, String key, String mimetype, Class<T> as) {
		logger.info("Getting {}/{}", type, key);

		ClientResponse response = connection.path(type+"/"+key).accept(mimetype).get(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.OK) {
			response.close();
			if (status == ClientResponse.Status.NOT_FOUND)
				return null;
			else
				throw new RuntimeException("read failed " + status);
		}

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return entity;
	}
	public <T> T getValues(String type, String mimetype, Class<T> as) {
		logger.info("Getting {}", type);

		ClientResponse response = connection.path(type).accept(mimetype).get(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.OK) {
			response.close();
			throw new RuntimeException("read failed " + status);
		}

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return entity;
	}
	public void putValue(String type, String key, String mimetype, Object value) {
		logger.info("Putting {}/{}", type, key);

		ClientResponse response = connection.path(type+"/"+key).type(mimetype).put(
				ClientResponse.class,
				(value instanceof OutputStreamSender) ?
						new StreamingOutputImpl((OutputStreamSender) value) : value);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status != ClientResponse.Status.OK) {
			throw new RuntimeException("write failed " + status);
		}
	}
	public void deleteValue(String type, String key) {
		logger.info("Deleting {}/{}", type, key);

		ClientResponse response = connection.path(type+"/"+key).delete(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("delete failed " + status);
		}
	}
	public void deleteValues(String type) {
		logger.info("Deleting {}", type);

		ClientResponse response = connection.path(type).delete(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("delete failed " + status);
		}
	}
}
