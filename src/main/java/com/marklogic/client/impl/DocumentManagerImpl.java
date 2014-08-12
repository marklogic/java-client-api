/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.impl.DocumentMetadataPatchBuilderImpl.DocumentPatchHandleImpl;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.util.RequestParameters;

abstract class DocumentManagerImpl<R extends AbstractReadHandle, W extends AbstractWriteHandle>
    extends AbstractLoggingManager
    implements DocumentManager<R, W>
{
    static final private long DEFAULT_PAGE_LENGTH = 50;

	static final private Logger logger = LoggerFactory.getLogger(DocumentManagerImpl.class);

    private boolean isProcessedMetadataModified = false;
	final private Set<Metadata> processedMetadata = new HashSet<Metadata>() {
        public boolean add(Metadata e) {
            isProcessedMetadataModified = true;
            return super.add(e);
        }
        public boolean addAll(Collection<? extends Metadata> c) {
            isProcessedMetadataModified = true;
            return super.addAll(c);
        }
    };
    {
        processedMetadata.add(Metadata.ALL);
        // we need to know if the user modifies after us
        isProcessedMetadataModified = false;
    }
        

	private RESTServices          services;
	private Format                contentFormat;
	private HandleFactoryRegistry handleRegistry;
	private ServerTransform       readTransform;
	private ServerTransform       writeTransform;
    private String                forestName;
    private long                  pageLength = DEFAULT_PAGE_LENGTH;
    private QueryView searchView = QueryView.RESULTS;
    private Format responseFormat = Format.XML;

	DocumentManagerImpl(RESTServices services, Format contentFormat) {
		super();
		this.services       = services;
		this.contentFormat  = contentFormat;
	}

	RESTServices getServices() {
		return services;
	}
	void setServices(RESTServices services) {
		this.services = services;
	}

	HandleFactoryRegistry getHandleRegistry() {
		return handleRegistry;
	}
	void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
		this.handleRegistry = handleRegistry;
	}

	@Override
    public Format getContentFormat() {
    	return contentFormat;
    }

    // select categories of metadata to read, write, or reset
	@Override
    public void setMetadataCategories(Set<Metadata> categories) {
		clearMetadataCategories();
		processedMetadata.addAll(categories);
    }
	@Override
    public void setMetadataCategories(Metadata... categories) {
		clearMetadataCategories();
    	for (Metadata category: categories)
    		processedMetadata.add(category);
    }
	@Override
    public Set<Metadata> getMetadataCategories() {
    	return processedMetadata;
    }
	@Override
    public void clearMetadataCategories() {
   		processedMetadata.clear();
    }


	@Override
	public DocumentDescriptor exists(String uri) throws ForbiddenUserException, FailedRequestException {
		return exists(uri, null);
    }
	@Override
	public DocumentDescriptor exists(String uri, Transaction transaction) throws ForbiddenUserException, FailedRequestException {
		return services.head(requestLogger, uri, (transaction == null) ? null : transaction.getTransactionId());
	}

	// shortcut readers
	@Override
    public <T> T readAs(String uri, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readAs(uri, null, as, null);
	}
	@Override
	public <T> T readAs(String uri, Class<T> as, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readAs(uri, null, as, transform);
	}
	@Override
	public <T> T readAs(String uri, DocumentMetadataReadHandle metadataHandle, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readAs(uri, metadataHandle, as, null);
	}
	@Override
    public <T> T readAs(String uri, DocumentMetadataReadHandle metadataHandle, Class<T> as, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
			ContentHandle<T> handle = getHandleRegistry().makeHandle(as);

			if (null == read(uri, metadataHandle, castAbstractReadHandle(as, handle), transform)) {
				return null;
			}

			return handle.get();
	}
	R castAbstractReadHandle(Class<?> as, AbstractReadHandle handle) {
		try {
			@SuppressWarnings("unchecked")
			R readHandle = (R) handle;
			return readHandle;
		} catch(ClassCastException e) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used in the context to read "+as.getName()
					);
		}
	}

	// strongly typed readers
	@Override
	public <T extends R> T read(String uri, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, null, contentHandle, null, null, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, null, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, metadataHandle, contentHandle, null, null, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, metadataHandle, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, null, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, null, contentHandle, transform, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, metadataHandle, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, metadataHandle, contentHandle, transform, transaction, getReadParams());
	}

    public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(new DocumentDescriptorImpl(uri, true), metadataHandle, contentHandle, transform, transaction, getReadParams());
	}

	@Override
    public <T extends R> T read(DocumentDescriptor desc, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, null, contentHandle, null, null, getReadParams());
	}

	@Override
    public <T extends R> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, null, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, metadataHandle, contentHandle, null, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, metadataHandle, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, null, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, null, contentHandle, transform, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, metadataHandle, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, metadataHandle, contentHandle, transform, transaction, getReadParams());
	}

    @SuppressWarnings("rawtypes")
	public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		if (desc == null)
			throw new IllegalArgumentException("Attempt to call read with null DocumentDescriptor");

		if (logger.isInfoEnabled())
			logger.info("Reading metadata and content for {}", desc.getUri());

		if (metadataHandle != null) {
			HandleImplementation metadataBase = HandleAccessor.checkHandle(metadataHandle, "metadata");
			Format metadataFormat = metadataBase.getFormat();
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				if (logger.isWarnEnabled())
					logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				metadataBase.setFormat(Format.XML);
			}
		}

		checkContentFormat(contentHandle);

		boolean wasModified = services.getDocument(
				requestLogger,
				desc, 
				(transaction != null) ? transaction.getTransactionId() : null,
				(metadataHandle != null) ? processedMetadata : null,
				mergeTransformParameters(
						(transform != null) ? transform : getReadTransform(),
						extraParams
						),
				metadataHandle,
				contentHandle
				);

		// TODO: after response, reset metadata and set flag

		return wasModified ? contentHandle : null;
	}

	public DocumentPage read(String... uris) {
		return read(null, null, uris);
	}

	public DocumentPage read(Transaction transaction, String... uris) {
		return read(null, transaction, uris);
	}

	public DocumentPage read(ServerTransform transform, String... uris) {
		return read(transform, null, uris);
	}

	public DocumentPage read(ServerTransform transform, Transaction transaction, String... uris) {
		boolean withContent = true;
		return read(transform, transaction, withContent, uris);
	}

	public DocumentPage read(ServerTransform transform, Transaction transaction,
		boolean withContent, String... uris)
	{
		if (uris == null || uris.length == 0)
			throw new IllegalArgumentException("Attempt to call read with no uris");

		if (logger.isInfoEnabled())
			logger.info("Reading metadata and content for multiple uris beginning with {}", uris[0]);

        return services.getBulkDocuments(
            requestLogger,
            (transaction == null) ? null : transaction.getTransactionId(),
            // the default for bulk is no metadata, which differs from the normal default of ALL
            isProcessedMetadataModified ? processedMetadata : null,
            responseFormat,
            mergeTransformParameters(
                    (transform != null) ? transform : getReadTransform(),
                    null
            ),
            withContent,
            uris);
   	}

	public DocumentPage readMetadata(String... uris) {
		boolean withContent = false;
		return read(null, null, withContent, uris);
	}

	public DocumentPage readMetadata(Transaction transaction, String... uris) {
		boolean withContent = false;
		return read(null, transaction, withContent, uris);
	}

	public DocumentPage search(QueryDefinition querydef, long start) {
		return search(querydef, start, null, null);
	}

	public DocumentPage search(QueryDefinition querydef, long start, SearchReadHandle searchHandle) {
		return search(querydef, start, searchHandle, null);
	}

	public DocumentPage search(QueryDefinition querydef, long start, Transaction transaction) {
		return search(querydef, start, null, transaction);
	}

	public DocumentPage search(QueryDefinition querydef, long start, SearchReadHandle searchHandle, Transaction transaction) {

        if ( searchHandle != null ) {
            HandleImplementation searchBase = HandleAccessor.checkHandle(searchHandle, "search");
            if (searchHandle instanceof SearchHandle) {
                SearchHandle responseHandle = (SearchHandle) searchHandle;
                responseHandle.setHandleRegistry(getHandleRegistry());
                responseHandle.setQueryCriteria(querydef);
            }
            if ( responseFormat != searchBase.getFormat() ) {
                throw new UnsupportedOperationException("The format supported by your handle:[" + 
                    searchBase.getFormat() + "] does not match your setResponseFormat:[" + responseFormat + "]");
            }
        }

        String tid = transaction == null ? null : transaction.getTransactionId();
        // the default for bulk is no metadata, which differs from the normal default of ALL
        Set<Metadata> metadata = isProcessedMetadataModified ? processedMetadata : null;
        return services.getBulkDocuments( requestLogger, querydef, start, getPageLength(), 
            tid, searchHandle, searchView, metadata, responseFormat, null);
	}

    public long getPageLength() {
        return pageLength;
    }

    public void setPageLength(long length) {
        this.pageLength = length;
    }

    public QueryView getSearchView() {
        return searchView;
    }

    public void setSearchView(QueryView view) {
        this.searchView = view;
    }

    public Format getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(Format responseFormat) {
        if ( responseFormat != Format.XML && responseFormat != Format.JSON ) {
            throw new UnsupportedOperationException("Only XML and JSON are valid response formats.  You specified:[" + 
                responseFormat + "]");
        }
        this.responseFormat = responseFormat;
    }

	public DocumentWriteSet newWriteSet() {
		return new DocumentWriteSetImpl();
	}

	public void write(DocumentWriteSet writeSet) {
        write(writeSet, null, null);
	}

	public void write(DocumentWriteSet writeSet, ServerTransform transform) {
        write(writeSet, transform, null);
	}

	public void write(DocumentWriteSet writeSet, Transaction transaction) {
        write(writeSet, null, transaction);
	}

	public void write(DocumentWriteSet writeSet, ServerTransform transform, Transaction transaction) {
		Format defaultFormat = contentFormat;
		services.postBulkDocuments(
            requestLogger,
            writeSet,
            (transform != null) ? transform : getWriteTransform(),
            (transaction == null) ? null : transaction.getTransactionId(),
			defaultFormat,
			null);
	}

	// shortcut writers
	@Override
    public void writeAs(String uri, Object content)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		writeAs(uri, null, content, null);
    }
	@Override
    public void writeAs(String uri, Object content, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		writeAs(uri, null, content, transform);
    }
	@Override
    public void writeAs(String uri, DocumentMetadataWriteHandle metadataHandle, Object content)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		writeAs(uri, metadataHandle, content, null);
    }
	@Override
    public void writeAs(String uri, DocumentMetadataWriteHandle metadataHandle, Object content, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (content == null) {
			throw new IllegalArgumentException("no content to write");
		}

		Class<?> as = content.getClass();

		W writeHandle = null;
		if (AbstractWriteHandle.class.isAssignableFrom(as)) {
			AbstractWriteHandle handle = (AbstractWriteHandle) content;
			writeHandle = castAbstractWriteHandle(null, handle);			
		} else {
			ContentHandle<?> handle = getHandleRegistry().makeHandle(as);
			Utilities.setHandleContent(handle, content);
			writeHandle = castAbstractWriteHandle(as, handle);			
		}

		write(uri, metadataHandle, writeHandle, transform);			
	}
	W castAbstractWriteHandle(Class<?> as, AbstractWriteHandle handle) {
		try {
			@SuppressWarnings("unchecked")
			W writeHandle = (W) handle;
			return writeHandle;
		} catch(ClassCastException e) {
			if (as == null) {
				throw new IllegalArgumentException(
						"Handle "+handle.getClass().getName()+
						" cannot be used in the context for writing"
						);
			}
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used in the context to write "+as.getName()
					);
		}
	}

	// strongly typed writers
	@Override
	public void write(String uri, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, null, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(String uri, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, null, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(String uri, DocumentMetadataWriteHandle metadata, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, metadata, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(String uri, DocumentMetadataWriteHandle metadata, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, metadata, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(String uri, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, null, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(String uri, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, null, contentHandle, transform, transaction, getWriteParams());
	}
	@Override
	public void write(String uri, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, metadataHandle, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(String uri, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, metadataHandle, contentHandle, transform, transaction, getWriteParams());
	}

	public void write(String uri, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(new DocumentDescriptorImpl(uri, true), metadataHandle, contentHandle, transform, transaction, getWriteParams());
	}

	@Override
	public void write(DocumentDescriptor desc, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, null, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, null, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadata, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, metadata, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadata, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, metadata, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, null, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, null, contentHandle, transform, transaction, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, metadataHandle, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, metadataHandle, contentHandle, transform, transaction, getWriteParams());
	}

	@SuppressWarnings("rawtypes")
	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		if (desc == null)
			throw new IllegalArgumentException("Writing document with null identifier");

		if (logger.isInfoEnabled())
			logger.info("Writing content for {}",desc.getUri());

		if (metadataHandle != null) {
			HandleImplementation metadataBase = HandleAccessor.checkHandle(metadataHandle, "metadata");
			Format metadataFormat = metadataBase.getFormat();
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				if (logger.isWarnEnabled())
					logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				metadataBase.setFormat(Format.XML);
			}
		}

		checkContentFormat(contentHandle);

		services.putDocument(
				requestLogger,
				desc,
				(transaction == null) ? null : transaction.getTransactionId(),
				(metadataHandle != null) ? processedMetadata : null,
				mergeTransformParameters(
						(transform != null) ? transform : getWriteTransform(),
						extraParams
						),
				metadataHandle,
				contentHandle
				);
	}

	@Override
	public void delete(String uri) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		delete(uri, null);
	}
	@Override
	public void delete(String uri, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		delete(new DocumentDescriptorImpl(uri, true), transaction);
	}
	@Override
    public void delete(DocumentDescriptor desc) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		delete(desc, null);
    }
	@Override
    public void delete(DocumentDescriptor desc, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (desc == null)
			throw new IllegalArgumentException("Deleting document with null identifier");

		if (logger.isInfoEnabled())
			logger.info("Deleting {}",desc.getUri());

		services.deleteDocument(requestLogger, desc, (transaction == null) ? null : transaction.getTransactionId(), null);
    }

	// shortcut creators
	@Override
    public DocumentDescriptor createAs(DocumentUriTemplate template, Object content)
	throws ForbiddenUserException, FailedRequestException {
		return createAs(template, null, content, null);
    }
	@Override
    public DocumentDescriptor createAs(DocumentUriTemplate template, Object content, ServerTransform transform)
	throws ForbiddenUserException, FailedRequestException {
		return createAs(template, null, content, transform);
    }
	@Override
    public DocumentDescriptor createAs(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle, Object content)
	throws ForbiddenUserException, FailedRequestException {
		return createAs(template, metadataHandle, content, null);
    }
	@Override
    public DocumentDescriptor createAs(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle, Object content, ServerTransform transform)
	throws ForbiddenUserException, FailedRequestException {
		Class<?> as = content.getClass();
		W writeHandle = null;
		if (AbstractWriteHandle.class.isAssignableFrom(as)) {
			AbstractWriteHandle handle = (AbstractWriteHandle) content;
			writeHandle = castAbstractWriteHandle(null, handle);			
		} else {
			ContentHandle<?> handle = getHandleRegistry().makeHandle(as);
			Utilities.setHandleContent(handle, content);
			writeHandle = castAbstractWriteHandle(as, handle);			
		}
		return create(template, metadataHandle, writeHandle, transform);
	}

	// strongly typed creators
	@Override
	public DocumentDescriptor create(DocumentUriTemplate template, W contentHandle)
	throws ForbiddenUserException, FailedRequestException {
		return create(template, null, contentHandle, null, null, getWriteParams());
	}
	@Override
	public DocumentDescriptor create(DocumentUriTemplate template, W contentHandle, 
			ServerTransform transform)
	throws ForbiddenUserException, FailedRequestException {
		return create(template, null, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public DocumentDescriptor create(DocumentUriTemplate template, W contentHandle,
			Transaction transaction)
	throws ForbiddenUserException, FailedRequestException {
		return create(template, null, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public DocumentDescriptor create(DocumentUriTemplate template, W contentHandle,
			ServerTransform transform, Transaction transaction)
	throws ForbiddenUserException, FailedRequestException {
		return create(template, null, contentHandle, transform, transaction, getWriteParams());
	}
	@Override
	public DocumentDescriptor create(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle,
			W contentHandle)
	throws ForbiddenUserException, FailedRequestException {
		return create(template, metadataHandle, contentHandle, null, null, getWriteParams());
	}
	@Override
	public DocumentDescriptor create(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle,
			W contentHandle, ServerTransform transform)
	throws ForbiddenUserException, FailedRequestException {
		return create(template, metadataHandle, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public DocumentDescriptor create(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle,
			W contentHandle, Transaction transaction)
	throws ForbiddenUserException, FailedRequestException {
		return create(template, metadataHandle, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public DocumentDescriptor create(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle,
			W contentHandle, ServerTransform transform, Transaction transaction)
	throws ForbiddenUserException, FailedRequestException {
		return create(template, metadataHandle, contentHandle, transform, transaction, getWriteParams());
	}
	@SuppressWarnings("rawtypes")
	public DocumentDescriptor create(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle,
			W contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ForbiddenUserException, FailedRequestException {
		if (logger.isInfoEnabled())
			logger.info("Creating content");

		if (metadataHandle != null) {
			HandleImplementation metadataBase = HandleAccessor.checkHandle(metadataHandle, "metadata");
			Format metadataFormat = metadataBase.getFormat();
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				if (logger.isWarnEnabled())
					logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				metadataBase.setFormat(Format.XML);
			}
		}

		checkContentFormat(contentHandle);

		return services.postDocument(
				requestLogger,
				template,
				(transaction == null) ? null : transaction.getTransactionId(),
				(metadataHandle != null) ? processedMetadata : null,
				mergeTransformParameters(
						(transform != null) ? transform : getWriteTransform(),
						extraParams
						),
				metadataHandle,
				contentHandle
				);
	}

	@Override
	public void patchAs(String uri, Object patch)
	throws ForbiddenUserException, FailedRequestException {
		if (patch == null) {
			throw new IllegalArgumentException("no patch to apply");
		}

		Class<?> as = patch.getClass();

		DocumentPatchHandle patchHandle = null;
		if (DocumentPatchHandle.class.isAssignableFrom(as)) {
			patchHandle = (DocumentPatchHandle) patch;
		} else {
			ContentHandle<?> handle = getHandleRegistry().makeHandle(as);
			if (!DocumentPatchHandle.class.isAssignableFrom(handle.getClass())) {
				throw new IllegalArgumentException(
						"Handle "+handle.getClass().getName()+
						" cannot be used to apply patch as "+as.getName()
						);
			}
			Utilities.setHandleContent(handle, patch);
			patchHandle = (DocumentPatchHandle) handle;
		}

		patch(uri, patchHandle);
	}

	@Override
	public void patch(String uri, DocumentPatchHandle patch)
	throws ForbiddenUserException, FailedRequestException {
		patch(uri, patch, null);
	}
	@Override
	public void patch(String uri, DocumentPatchHandle patch, Transaction transaction)
	throws ForbiddenUserException, FailedRequestException {
		patch(new DocumentDescriptorImpl(uri, true), patch, transaction);
	}
	@Override
	public void patch(DocumentDescriptor desc, DocumentPatchHandle patch)
	throws ForbiddenUserException, FailedRequestException {
		patch(desc, patch, null);
	}
	@Override
	public void patch(DocumentDescriptor desc, DocumentPatchHandle patch, Transaction transaction)
	throws ForbiddenUserException, FailedRequestException {
		if (logger.isInfoEnabled())
			logger.info("Patching document");

		DocumentPatchHandleImpl builtPatch = 
			(patch instanceof DocumentPatchHandleImpl) ?
			(DocumentPatchHandleImpl) patch : null;
		services.patchDocument(
				requestLogger,
				desc,
				(transaction == null) ? null : transaction.getTransactionId(),
				(builtPatch != null) ?
						builtPatch.getMetadata() : processedMetadata,
				(builtPatch != null) ?
						builtPatch.isOnContent() : true,
				patch
				);
	}

	@Override
    public <T extends DocumentMetadataReadHandle> T readMetadata(String uri, T metadataHandle) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readMetadata(uri, metadataHandle, null);
    }
	@Override
    public <T extends DocumentMetadataReadHandle> T readMetadata(String uri, T metadataHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		read(uri, metadataHandle, null, transaction);

		return metadataHandle;
    }

	@Override
    public void writeMetadata(String uri, DocumentMetadataWriteHandle metadataHandle) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		writeMetadata(uri, metadataHandle, null);
    }
	@Override
    public void writeMetadata(String uri, DocumentMetadataWriteHandle metadataHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		write(uri, metadataHandle, (W) null, transaction);
    }

	@Override
    public void writeDefaultMetadata(String uri) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		writeDefaultMetadata(uri, null);
    }
	@Override
    public void writeDefaultMetadata(String uri, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (uri == null)
			throw new IllegalArgumentException("Resetting document metadata with null identifier");

		if (logger.isInfoEnabled())
			logger.info("Resetting metadata for {}",uri);

		services.deleteDocument(requestLogger, new DocumentDescriptorImpl(uri, true), (transaction == null) ? null : transaction.getTransactionId(), processedMetadata);
    }

	@Override
    public ServerTransform getReadTransform() {
    	return readTransform;
    }
	@Override
    public void setReadTransform(ServerTransform transform) {
    	this.readTransform = transform;
    }

	@Override
    public ServerTransform getWriteTransform() {
    	return writeTransform;
    }
	@Override
    public void setWriteTransform(ServerTransform transform) {
    	this.writeTransform = transform;
    }

	@Override
    public String getForestName() {
    	return forestName;
    }
	@Override
    public void setForestName(String forestName) {
    	this.forestName = forestName;
    }

	@Override
	public DocumentDescriptor newDescriptor(String uri) {
		return new DocumentDescriptorImpl(uri, false);
	}

	@Override
    public DocumentUriTemplate newDocumentUriTemplate(String extension) {
		return new DocumentUriTemplateImpl(extension);
	}

	@Override
    public DocumentMetadataPatchBuilder newPatchBuilder(Format pathFormat) {
    	return new DocumentMetadataPatchBuilderImpl(pathFormat);
    }

	private void checkContentFormat(Object contentHandle) {
		checkContentFormat(HandleAccessor.checkHandle(contentHandle, "content"));
	}
	@SuppressWarnings("rawtypes")
	private void checkContentFormat(HandleImplementation contentBase) {
		if (contentBase == null)
			return;

		if (contentFormat != null && contentFormat != Format.UNKNOWN) {
			Format currFormat = contentBase.getFormat();
			if (currFormat != contentFormat) {
				contentBase.setFormat(contentFormat);
				if (currFormat != Format.UNKNOWN)
					contentBase.setMimetype(contentFormat.getDefaultMimetype());
			}
		}
	}
	protected RequestParameters mergeTransformParameters(ServerTransform transform, RequestParameters extraParams) {
		if (transform == null)
			return extraParams;

		if (extraParams == null)
			extraParams = new RequestParameters();

		transform.merge(extraParams);

		return extraParams;
	}

	// hooks for extension
	protected RequestParameters getReadParams() {
		return null;
	}
	protected RequestParameters getWriteParams() {
		return null;
	}
}
