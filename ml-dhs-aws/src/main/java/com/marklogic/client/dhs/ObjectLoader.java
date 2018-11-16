package com.marklogic.client.dhs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
   private int count;
   private JsonNode recordMetadata;

   public int getCount() {
	return count;
}

public ObjectLoader(WriteBatcher batcher, ObjectNode recordDef, String directory, DocumentMetadataHandle documentMetadata) {
      this.batcher = batcher;
      this.recordDef = recordDef;
      this.directory = directory;
      this.documentMetadata = documentMetadata;
      this.count = 0;
      this.recordMetadata = recordDef.path("metadata");
   }

   public void loadRecords(Iterator<ObjectNode> objectItr) {
      objectItr.forEachRemaining(this::loadRecord);
   }
   public void loadRecord(ObjectNode record) {
      String uri = directory+UUID.randomUUID().toString()+".json";
      count++;
      ObjectNode recordData = JsonNodeFactory.instance.objectNode();
      recordData.set("metadata", this.recordMetadata);
      recordData.set("instance", record);

// TODO: wrap per-record metadata and record in DHF envelope
      JacksonHandle handle = new JacksonHandle(recordData);
      JSONWriteOperation operation = new JSONWriteOperation(uri, documentMetadata, handle);
      batcher.add(operation);
   }
}
