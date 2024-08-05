/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.io.OutputStreamSender;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

class StreamingOutputImpl extends RequestBody {
  private OutputStreamSender handle;
  private RequestLogger      logger;
  private MediaType          contentType;

  StreamingOutputImpl(OutputStreamSender handle, RequestLogger logger, MediaType contentType) {
    super();
    this.handle = handle;
    this.logger = logger;
    this.contentType = contentType;
  }

  @Override
  public MediaType contentType() {
    return contentType;
  }

  @Override
  public void writeTo(BufferedSink sink) throws IOException {
    OutputStream out = sink.outputStream();

    if (logger != null) {
      OutputStream tee = logger.getPrintStream();
      long         max = logger.getContentMax();
      if (tee != null && max > 0) {
        handle.write(new OutputStreamTee(out, tee, max));

        return;
      }
    }

    handle.write(out);
  }
}
