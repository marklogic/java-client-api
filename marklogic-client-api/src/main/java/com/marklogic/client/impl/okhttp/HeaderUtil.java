/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.document.ContentDescriptor;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.impl.HandleImplementation;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.io.Format;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.ContentDisposition;
import jakarta.mail.internet.ParseException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static com.marklogic.client.impl.RESTServices.HEADER_CONTENT_DISPOSITION;
import static com.marklogic.client.impl.RESTServices.HEADER_CONTENT_LENGTH;
import static com.marklogic.client.impl.RESTServices.HEADER_CONTENT_TYPE;
import static com.marklogic.client.impl.RESTServices.HEADER_ETAG;
import static com.marklogic.client.impl.RESTServices.HEADER_ML_EFFECTIVE_TIMESTAMP;
import static com.marklogic.client.impl.RESTServices.HEADER_VND_MARKLOGIC_DOCUMENT_FORMAT;

/**
 * Contains convenience methods for working with HTTP headers in the context of OkHttp and MarkLogic.
 * This code was moved here from OkHttpServices without any modification during the move.
 *
 * @since 8.2.0
 */
public class HeaderUtil {
	static final private Logger logger = LoggerFactory.getLogger(HeaderUtil.class);
  static final private Pattern versionPattern = Pattern.compile("^(.+;)*\\s*versionId=([0-9]+).*$");

	public static void updateDescriptor(ContentDescriptor desc, Headers headers) {
		if (desc == null || headers == null) return;

		updateFormat(desc, headers);
		updateMimetype(desc, headers);
		updateLength(desc, headers);
		updateServerTimestamp(desc, headers);
	}

	@SuppressWarnings("rawtypes")
	public static void copyDescriptor(DocumentDescriptor desc, HandleImplementation handleBase) {
		if (handleBase == null) return;

		if (desc.getFormat() != null) handleBase.setFormat(desc.getFormat());
		if (desc.getMimetype() != null) handleBase.setMimetype(desc.getMimetype());
		handleBase.setByteLength(desc.getByteLength());
	}

	public static void updateFormat(ContentDescriptor descriptor, Headers headers) {
		updateFormat(descriptor, getHeaderFormat(headers));
	}

	public static void updateFormat(ContentDescriptor descriptor, Format format) {
		if (format != null) {
			descriptor.setFormat(format);
		}
	}

	public static Format getHeaderFormat(Headers headers) {
		String format = headers.get(HEADER_VND_MARKLOGIC_DOCUMENT_FORMAT);
		if (format != null && format.length() > 0) {
			return Format.valueOf(format.toUpperCase());
		}
		String contentType = headers.get(HEADER_CONTENT_TYPE);
		if (contentType != null && contentType.length() > 0) {
			return Format.getFromMimetype(contentType);
		}
		return null;
	}

	public static Format getHeaderFormat(BodyPart part) {
		String contentDisposition = getHeader(part, HEADER_CONTENT_DISPOSITION);
		String formatRegex = ".* format=(text|binary|xml|json).*";
		String format = getHeader(part, HEADER_VND_MARKLOGIC_DOCUMENT_FORMAT);
		String contentType = getHeader(part, HEADER_CONTENT_TYPE);
		if (format != null && format.length() > 0) {
			return Format.valueOf(format.toUpperCase());
		} else if (contentDisposition != null && contentDisposition.matches(formatRegex)) {
			format = contentDisposition.replaceFirst("^.*" + formatRegex + ".*$", "$1");
			return Format.valueOf(format.toUpperCase());
		} else if (contentType != null && contentType.length() > 0) {
			return Format.getFromMimetype(contentType);
		}
		return null;
	}

	// Bulk multi-document reads usually carry the version as a "versionId" param on Content-Disposition.
	public static long getHeaderVersion(BodyPart part) {
		String contentDisposition = getHeader(part, HEADER_CONTENT_DISPOSITION);
		if (contentDisposition != null) {
      Matcher matcher = versionPattern.matcher(contentDisposition);
      if (matcher.matches()) {
        String version = matcher.replaceFirst("$2");
        return Utilities.parseLong(version, DocumentDescriptor.UNKNOWN_VERSION);
      }
		}
		return extractVersion(getHeader(part, HEADER_ETAG));
	}

	public static void updateMimetype(ContentDescriptor descriptor, Headers headers) {
		updateMimetype(descriptor, getHeaderMimetype(headers.get(HEADER_CONTENT_TYPE)));
	}

	public static void updateMimetype(ContentDescriptor descriptor, String mimetype) {
		if (mimetype != null) {
			descriptor.setMimetype(mimetype);
		}
	}

	public static String getHeader(Map<String, List<String>> headers, String name) {
		List<String> values = headers.get(name);
		if (values != null && values.size() > 0) {
			return values.get(0);
		}
		return null;
	}

	public static String getHeader(BodyPart part, String name) {
		if (part == null) throw new MarkLogicInternalException("part must not be null");
		try {
			String[] values = part.getHeader(name);
			if (values != null && values.length > 0) {
				return values[0];
			}
			return null;
		} catch (MessagingException e) {
			throw new MarkLogicIOException(e);
		}
	}

	public static String getHeaderMimetype(String contentType) {
		if (contentType != null) {
			int offset = contentType.indexOf(";");
			String mimetype = (offset == -1) ? contentType : contentType.substring(0, offset);
			// TODO: if "; charset=foo" set character set
			if (mimetype != null && mimetype.length() > 0) {
				return mimetype;
			}
		}
		return null;
	}

	public static void updateLength(ContentDescriptor descriptor, Headers headers) {
		updateLength(descriptor, getHeaderLength(headers.get(HEADER_CONTENT_LENGTH)));
	}

	public static void updateLength(ContentDescriptor descriptor, long length) {
		descriptor.setByteLength(length);
	}

	public static void updateServerTimestamp(ContentDescriptor descriptor, Headers headers) {
		updateServerTimestamp(descriptor, getHeaderServerTimestamp(headers));
	}

	private static long getHeaderServerTimestamp(Headers headers) {
		return Utilities.parseLong(headers.get(HEADER_ML_EFFECTIVE_TIMESTAMP));
	}

	@SuppressWarnings("rawtypes")
	public static void updateServerTimestamp(ContentDescriptor descriptor, long timestamp) {
		if (descriptor instanceof HandleImplementation) {
			if (descriptor != null && timestamp != -1) {
				((HandleImplementation) descriptor).setResponseServerTimestamp(timestamp);
			}
		}
	}

	public static long getHeaderLength(String length) {
		return Utilities.parseLong(length, ContentDescriptor.UNKNOWN_LENGTH);
	}

	public static String getHeaderUri(BodyPart part) {
		try {
			if (part == null) {
				return null;
			}

			try {
				String filename = part.getFileName();
				if (filename != null) {
					return filename;
				}
			} catch (ParseException e) {
				// Jakarta Mail's parser failed due to malformed Content-Disposition header.
				// Check if MarkLogic sent a malformed "format=" parameter at the end, which violates RFC 2183.
				String contentDisposition = getHeader(part, "Content-Disposition");
				if (contentDisposition != null && contentDisposition.matches(".*;\\s*format\\s*=\\s*$")) {
					// Remove the trailing "; format=" to fix the malformed header
					String cleaned = contentDisposition.replaceFirst(";\\s*format\\s*=\\s*$", "").trim();
					logger.debug("Removed trailing 'format=' from malformed Content-Disposition header: {} -> {}", contentDisposition, cleaned);
					return extractFilenameFromContentDisposition(cleaned);
				}
				throw e;
			}

			return null;
		} catch (MessagingException e) {
			throw new MarkLogicIOException(e);
		}
	}

	private static String extractFilenameFromContentDisposition(String contentDisposition) {
		if (contentDisposition == null) {
			return null;
		}
		try {
			// Use Jakarta Mail's ContentDisposition parser to extract the filename parameter. This is the class
			// that throws an error when "format=" exists in the value, but that has been removed already.
			ContentDisposition cd = new ContentDisposition(contentDisposition);
			return cd.getParameter("filename");
		} catch (ParseException e) {
			logger.warn("Failed to parse cleaned Content-Disposition header: {}; cause: {}",
				contentDisposition, e.getMessage());
			return null;
		}
	}

	public static void updateVersion(DocumentDescriptor descriptor, Headers headers) {
		updateVersion(descriptor, extractVersion(headers.get(HEADER_ETAG)));
	}

	public static void updateVersion(DocumentDescriptor descriptor, long version) {
		descriptor.setVersion(version);
	}

	private static long extractVersion(String header) {
		if (header != null && header.length() > 0) {
			// trim the double quotes
			return Long.parseLong(header.substring(1, header.length() - 1));
		}
		return DocumentDescriptor.UNKNOWN_VERSION;
	}  
}
