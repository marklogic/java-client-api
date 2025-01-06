/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.IoUtil;
import jakarta.activation.DataHandler;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;
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
			try (InputStream inputStream = part.body().inputStream()) {
				byte[] bytes = IoUtil.streamToBytes(inputStream);
				bodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(bytes, part.headers().get("Content-Type"))));
			}

			// part.headers.toMultimap() is lowercasing header names, which causes later issues.
			Headers headers = part.headers();
			for (String headerName : headers.names()) {
				for (String headerValue : headers.values(headerName)) {
					bodyPart.addHeader(headerName, headerValue);
				}
			}
			return bodyPart;
		} finally {
			// Looking at the OkHttp source code, this does not appear necessary, as closing the InputStream above should
			// achieve the same effect. But there is no downside to doing this, as it may be required by a future version
			// of OkHttp.
			part.close();
		}
	}
}
