package com.marklogic.client.dhs;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;

import java.util.Iterator;
import java.util.UUID;

public class ObjectLoader {
   private WriteBatcher batcher;
   private ObjectNode recordDef;
   private String directory;
   private DocumentMetadataHandle documentMetadata;

   public ObjectLoader(WriteBatcher batcher, ObjectNode recordDef, String directory, DocumentMetadataHandle documentMetadata) {
      this.batcher = batcher;
      this.recordDef = recordDef;
      this.directory = directory;
      this.documentMetadata = documentMetadata;
   }

   public void loadRecords(Iterator<ObjectNode> objectItr) {
      objectItr.forEachRemaining(this::loadRecord);
   }
   public void loadRecord(ObjectNode record) {
      String uri = directory+UUID.randomUUID().toString()+".json";

// TODO: wrap per-record metadata and record in DHF envelope
      JacksonHandle handle = new JacksonHandle(record);
      JSONWriteOperation operation = new JSONWriteOperation(uri, documentMetadata, handle);
      batcher.add(operation);
   }
}
