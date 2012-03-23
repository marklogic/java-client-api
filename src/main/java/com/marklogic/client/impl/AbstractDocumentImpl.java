package com.marklogic.client.impl;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.AbstractDocumentManager;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.Format;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

abstract class AbstractDocumentImpl<R extends AbstractReadHandle, W extends AbstractWriteHandle>
	implements AbstractDocumentManager<R, W>
{
	static final private Logger logger = LoggerFactory.getLogger(AbstractDocumentImpl.class);

	final private Set<Metadata> processedMetadata;

	private RESTServices       services;
	private Format             contentFormat;
	private String             readTransformName;
    private Map<String,String> readTransformParams;
	private String             writeTransformName;
    private Map<String,String> writeTransformParams;
    private String             forestName;
	private MetadataUpdate     metadataUpdatePolicy;
	private boolean            versionMatched = false;
	private RequestLogger      requestLogger;

	AbstractDocumentImpl(RESTServices services, Format contentFormat) {
		this.services = services;
		this.contentFormat = contentFormat;
	}

	RESTServices getServices() {
		return services;
	}
	void setServices(RESTServices services) {
		this.services = services;
	}

    public Format getContentFormat() {
    	return contentFormat;
    }

    // select categories of metadata to read, write, or reset
	{
		HashSet<Metadata> metadata = new HashSet<Metadata>();
		metadata.add(Metadata.ALL);
		processedMetadata = metadata;
	}
    public Set<Metadata> getMetadataCategories() {
    	return processedMetadata;
    }
    public void setMetadataCategories(Set<Metadata> categories) {
		processedMetadata.clear();
		processedMetadata.addAll(categories);
    }
    public void setMetadataCategories(Metadata... categories) {
   		processedMetadata.clear();
    	for (Metadata category: categories)
    		processedMetadata.add(category);
    }

	public boolean exists(DocumentIdentifier docId) {
		return exists(docId, null);
    }
	public boolean exists(DocumentIdentifier docId, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Checking existence of {}",uri);

		Map<String,List<String>> headers = services.head(uri, (transaction == null) ? null : transaction.getTransactionId());
		if (headers == null)
			return false;

		List<String> values = null;
		if (headers.containsKey("Content-Type")) {
			values = headers.get("Content-Type");
			if (values != null) {
				String type = values.get(0);
				docId.setMimetype(
						type.contains(";") ? type.substring(0, type.indexOf(";")) : type
						);
			}
		}
		if (headers.containsKey("Content-Length")) {
			values = headers.get("Content-Length");
			if (values != null) {
				docId.setByteLength(
						Integer.valueOf(values.get(0))
						);
			}
		}

		return true;
	}

	public <T extends R> T read(DocumentIdentifier docId, T contentHandle) {
		return read(docId, null, contentHandle, null);
	}
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle) {
		return read(docId, metadataHandle, contentHandle, null);
	}
	public <T extends R> T read(DocumentIdentifier docId, T contentHandle, Transaction transaction) {
		return read(docId, null, contentHandle, transaction);
	}
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction) {
		return read(docId, metadataHandle, contentHandle, transaction, getReadParams());
	}
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction, Map<String,String> extraParams) {
		String uri = docId.getUri();
		logger.info("Reading metadata and content for {}",uri);

		String metadataMimetype = null;
		Set<Metadata> metadata = null;
		if (metadataHandle != null) {
			Format metadataFormat = metadataHandle.getFormat();
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				metadataHandle.setFormat(Format.XML);
				metadataFormat = Format.XML;
			}

			metadataMimetype = metadataFormat.getDefaultMimetype();

			metadata = processedMetadata;
		}

		String contentMimetype = null;
		if (contentHandle != null) {
			contentMimetype = docId.getMimetype();
			if (contentFormat != null && contentFormat != Format.UNKNOWN) {
				contentHandle.setFormat(contentFormat);
				if (contentMimetype == null)
					contentMimetype = contentFormat.getDefaultMimetype();
			}
		}

		if (metadataHandle != null && contentHandle != null) {
			Object[] values = services.getDocument(
					uri, 
					(transaction == null) ? null : transaction.getTransactionId(),
					metadata,
					extraParams,
					new String[]{metadataMimetype, contentMimetype},
					new Class[]{metadataHandle.receiveAs(), contentHandle.receiveAs()}
					);
			metadataHandle.receiveContent(values[0]);
			contentHandle.receiveContent(values[1]);
		} else if (metadataHandle != null) {
			metadataHandle.receiveContent(
					services.getDocument(
							uri,
							(transaction == null) ? null : transaction.getTransactionId(),
							metadata,
							extraParams,
							metadataMimetype,
							metadataHandle.receiveAs()
							)
					);
		} else if (contentHandle != null) {
			contentHandle.receiveContent(
				services.getDocument(
						uri,
						(transaction == null) ? null : transaction.getTransactionId(),
						null,
						extraParams,
						contentMimetype,
						contentHandle.receiveAs()
						)
				);
		}

		// TODO: after response, reset metadata and set flag

		return contentHandle;
	}

	public void write(DocumentIdentifier docId, W contentHandle) {
		write(docId, null, contentHandle, null);
	}
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadata, W contentHandle) {
		write(docId, metadata, contentHandle, null);
	}
	public void write(DocumentIdentifier docId, W contentHandle, Transaction transaction) {
		write(docId, null, contentHandle, transaction);
	}
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction) {
		write(docId, metadataHandle, contentHandle, transaction, getWriteParams());
	}
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction, Map<String,String> extraParams) {
		String uri = docId.getUri();
		logger.info("Writing content for {}",uri);

		String metadataMimetype = null;
		Set<Metadata> metadata = null;
		if (metadataHandle != null) {
			Format metadataFormat = metadataHandle.getFormat();
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				metadataHandle.setFormat(Format.XML);
				metadataFormat = Format.XML;
			}

			metadataMimetype = metadataFormat.getDefaultMimetype();

			metadata = processedMetadata;
		}

		String contentMimetype = null;
		if (contentHandle != null) {
			contentMimetype = docId.getMimetype();
			if (contentFormat != null && contentFormat != Format.UNKNOWN) {
				contentHandle.setFormat(contentFormat);
				if (contentMimetype == null)
					contentMimetype = contentFormat.getDefaultMimetype();
			}
		}

		if (metadataHandle != null && contentHandle != null) {
			services.putDocument(
					uri,
					(transaction == null) ? null : transaction.getTransactionId(),
					metadata,
					extraParams,
					new String[]{metadataMimetype, contentMimetype},
					new Object[] {metadataHandle.sendContent(), contentHandle.sendContent()}
					);
		} else if (metadataHandle != null) {
			services.putDocument(
					uri,
					(transaction == null) ? null : transaction.getTransactionId(),
					metadata,
					extraParams,
					metadataMimetype,
					metadataHandle.sendContent()
					);
		} else if (contentHandle != null) {
			services.putDocument(
					uri,
					(transaction == null) ? null : transaction.getTransactionId(),
					null,
					extraParams,
					contentMimetype,
					contentHandle.sendContent()
					);
		}
	}

	public void delete(DocumentIdentifier docId) {
		delete(docId, null);
	}
	public void delete(DocumentIdentifier docId, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Deleting {}",uri);

		services.deleteDocument(uri, (transaction == null) ? null : transaction.getTransactionId(), null);
	}

    public <T extends DocumentMetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle) {
		return readMetadata(docId, metadataHandle, null);
    }
    public <T extends DocumentMetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle, Transaction transaction) {
		read(docId, metadataHandle, null, transaction);

		return metadataHandle;
    }

    public void writeMetadata(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle) {
		writeMetadata(docId, metadataHandle, null);
    }
    public void writeMetadata(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle, Transaction transaction) {
		write(docId, metadataHandle, null, transaction);
    }

    public void writeDefaultMetadata(DocumentIdentifier docId) {
		writeDefaultMetadata(docId, null);
    }
    public void writeDefaultMetadata(DocumentIdentifier docId, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Resetting metadata for {}",uri);

		services.deleteDocument(uri, (transaction == null) ? null : transaction.getTransactionId(), processedMetadata);
    }

    public String getReadTransformName() {
    	return readTransformName;
    }
    public void setReadTransformName(String name) {
    	this.readTransformName = name;
    }

    public Map<String,String> getReadTransformParameters() {
    	return readTransformParams;
    }
    public void setReadTransformParameters(Map<String,String> parameters) {
    	this.readTransformParams = parameters;
    }
 
    public String getWriteTransformName() {
    	return writeTransformName;
    }
    public void setWriteTransformName(String name) {
    	this.writeTransformName = name;
    }

    public Map<String,String> getWriteTransformParameters() {
    	return writeTransformParams;
    }
    public void setWriteTransformParameters(Map<String,String> parameters) {
    	this.writeTransformParams = parameters;
    }
 
    public String getForestName() {
    	return forestName;
    }
    public void setForestName(String forestName) {
    	this.forestName = forestName;
    }

    public MetadataUpdate getMetadataUpdatePolicy() {
    	return metadataUpdatePolicy;
    }
    public void SetMetadataUpdatePolicy(MetadataUpdate policy) {
    	metadataUpdatePolicy = policy;
    }

	public boolean isVersionMatched() {
		return versionMatched;
	}
	public void setVersionMatched(boolean match) {
		versionMatched = match;
	}

	// hooks for extension
	protected Map<String,String> getReadParams() {
		return null;
	}
	protected Map<String,String> getWriteParams() {
		return null;
	}

	public void startLogging(RequestLogger logger) {
		requestLogger = logger;
	}
	public void stopLogging() {
		if (requestLogger == null) return;

		PrintStream out = requestLogger.getPrintStream();
		if (out != null) out.flush();

		requestLogger = null;
	}
	protected boolean isLoggerEnabled() {
		if (requestLogger != null)
			return requestLogger.isEnabled();

		return false;
	}
	protected PrintStream getLogger() {
		if (requestLogger == null)
			return null;

		return requestLogger.getPrintStream();
	}
}
