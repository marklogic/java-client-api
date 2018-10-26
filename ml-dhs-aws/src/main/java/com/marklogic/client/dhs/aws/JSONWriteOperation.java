package com.marklogic.client.dhs.aws;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

public class JSONWriteOperation implements DocumentWriteOperation {
   private String uri;
   private DocumentMetadataWriteHandle documentMetadata;
   private AbstractWriteHandle handle;

   JSONWriteOperation(String uri, DocumentMetadataHandle documentMetadata, JSONWriteHandle record) {
      this.uri = uri;
      this.documentMetadata = documentMetadata;
      this.handle = record;
   }

   @Override
   public OperationType getOperationType() {
      return OperationType.DOCUMENT_WRITE;
   }
   @Override
   public String getUri() {
      return uri;
   }
   @Override
   public DocumentMetadataWriteHandle getMetadata() {
      return documentMetadata;
   }
   @Override
   public AbstractWriteHandle getContent() {
      return handle;
   }
   @Override
   public String getTemporalDocumentURI() {
      return null;
   }
}
