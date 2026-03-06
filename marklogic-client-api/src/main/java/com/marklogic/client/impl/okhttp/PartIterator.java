/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.MarkLogicIOException;
import jakarta.activation.DataHandler;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import okhttp3.Headers;
import okhttp3.MultipartReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Adapts the iterator over the OkHttp MultipartReader to conform to the Iterator that is required by
 * OkHttpEvalResultIterator. By converting each MultipartReader.Part into a jakarta.mail.BodyPart, we can reuse
 * all the existing plumbing that depends on jakarta.mail.BodyPart.
 * <p>
 * Added to resolve MLE-19222, where eval/invoke results are not being streamed but rather were all being read into
 * memory, leading to OutOfMemoryErrors.
 */
public class PartIterator implements Iterator<BodyPart> {

	private final MultipartReader reader;
	private BodyPart nextBodyPart;

	public PartIterator(MultipartReader reader) {
		this.reader = reader;
		readNextPart();
	}

	@Override
	public boolean hasNext() {
		return nextBodyPart != null;
	}

	@Override
	public BodyPart next() {
		BodyPart partToReturn = nextBodyPart;
		readNextPart();
		return partToReturn;
	}

	private void readNextPart() {
		try {
			// See http://okhttp.foofun.cn/4.x/okhttp/okhttp3/-multipart-reader/ for more info on the OkHttp
			// MultipartReader. This was actually requested many moons ago by one of the original Java Client
			// developers - https://github.com/square/okhttp/issues/3394.
			MultipartReader.Part nextPart = reader.nextPart();
			this.nextBodyPart = nextPart != null ? convertPartToBodyPart(nextPart) : null;
		} catch (Exception e) {
			throw new MarkLogicIOException(e);
		}
	}

	private static BodyPart convertPartToBodyPart(MultipartReader.Part part) throws IOException, MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();

		try {
			// Stream the part content without reading it all into memory. This is critical for large documents.
			// We DON'T close the InputStream here because the BodyPart needs to keep it open for reading later.
			InputStream inputStream = part.body().inputStream();
			bodyPart.setDataHandler(new DataHandler(new InputStreamDataSource(inputStream, part.headers().get("Content-Type"))));

			// part.headers.toMultimap() is lowercasing header names, which causes later issues.
			Headers headers = part.headers();
			for (String headerName : headers.names()) {
				for (String headerValue : headers.values(headerName)) {
					bodyPart.addHeader(headerName, headerValue);
				}
			}
			return bodyPart;
		} finally {
			// Do NOT close the part here - it needs to stay open for the InputStream to work.
			// The part will be closed when the Response is closed.
		}
	}
}
