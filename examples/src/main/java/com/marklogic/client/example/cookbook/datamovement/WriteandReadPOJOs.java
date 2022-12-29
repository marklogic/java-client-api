/*
 * Copyright (c) 2022 MarkLogic Corporation
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
package com.marklogic.client.example.cookbook.datamovement;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;

/**
 * WriteandReadPOJOS illustrates how to write bulk POJOs into the database
 * using WriteBatcher. It also demonstrates how to read POJOs from the
 * database using QueryBatcher with and without ExportListener
 */
public class WriteandReadPOJOs {
  private int batchSize = 10;
  private int threadCount = 5;
  private Logger logger = LoggerFactory.getLogger(WriteandReadPOJOs.class);
  // Create the Database Client and Data Movement Manager
  private DatabaseClient client =  DatabaseClientSingleton.get();
  private DataMovementManager moveMgr = client.newDataMovementManager();

  public static void main(String args[]) throws JAXBException {
    new WriteandReadPOJOs().run();
  }

  /**
   * A sample class which we will be using in order to demonstrate
   * reading and writing large number of POJOs 
   */
  @XmlRootElement
  public static class ProductDetails {
    private int id;
    private String name;
    private int distributor_id;
    public int getId() {
      return id;
    }
    public void setId(int id) {
      this.id = id;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public int getDistributor_id() {
      return distributor_id;
    }
    public void setDistributor_id(int distributor_id) {
      this.distributor_id = distributor_id;
    }
  }

  public void run() throws JAXBException {
    writeBulkPOJOS();
    readBulkPOJOS();
    readPOJOsWithExportListener();
    deleteDocuments();
    moveMgr.release();
  }

  public void writeBulkPOJOS() throws JAXBException {
    WriteBatcher batcher = moveMgr.newWriteBatcher()
      // normally a batch would be at least 100 docs at a time. We're
      // making this number small in order to demonstrate this example.
      .withBatchSize(batchSize)
      // the number of threads writing depends on how fast you have documents available
      // and how many nodes are in your MarkLogic cluster
      .withThreadCount(threadCount)
      // create an action you want to do once a batch is successfully
      // written here. It will be called for each successful batch.
      .onBatchSuccess(batch -> {
        logger.info("Batch {} wrote, {} so far", batch.getJobBatchNumber(), batch.getJobWritesSoFar());
      })
      // create an action you want to do when the batch fails. It will
      // be called for every batch that fails.
      .onBatchFailure((batch, throwable) -> throwable.printStackTrace() );
    moveMgr.startJob(batcher);

    for(int i=0;i<50;i++) {
      // create instances of the POJO class
      ProductDetails product = new ProductDetails();
      product.setName("Product"+i);
      product.setId(i);
      product.setDistributor_id(100);

      // Create metadata for the documents that needs to be ingested
      DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
      if(i<25) {
        metadataHandle.getCollections().add("products-collection1");
      } else {
        metadataHandle.getCollections().add("products-collection2");
      }

      String docId = "/dmsdkexample/"+product.getName()+".xml";
      // Write the instance of the POJO class into the database
      // along with the metadata created above
      batcher.addAs(docId, metadataHandle, product);

    }
    // any docs in an incomplete batch need to be written
    batcher.flushAndWait();
    moveMgr.stopJob(batcher);
  }

  public void readBulkPOJOS() throws JAXBException {
    // Create a query definition in order to use it with QueryBatcher
    StructuredQueryDefinition query = new StructuredQueryBuilder().collection("products-collection1");

    // Create a QueryBatcher in order to retrieve bulk POJOs 
    // from the database matching the query definition
    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(query)
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      // You can configure to do some action whenever a batch
      // of uris are retrieved and ready to process
      .onUrisReady(
        batch -> {
          // Read the URIs and display them
          XMLDocumentManager docMgr = batch.getClient().newXMLDocumentManager();
          DocumentPage documents = docMgr.read(batch.getItems());
          for ( DocumentRecord record : documents ) {
            ProductDetails product = record.getContentAs(ProductDetails.class);
            System.out.println("Product ID : "+ product.getId());
          }

        }
      )
      // Add a listener to do some action when the batch fails to retrieve
      .onQueryFailure(throwable -> throwable.printStackTrace());

    moveMgr.startJob(queryBatcher);
    // Wait till the batch completes
    queryBatcher.awaitCompletion();
    moveMgr.stopJob(queryBatcher);
  }

  public void readPOJOsWithExportListener() throws JAXBException {
    StructuredQueryDefinition query = new StructuredQueryBuilder().collection("products-collection2");

    // Create a QueryBatcher to get the documents from the database
    // and process each document retrieved using the Export Listener and
    // all documents are exported at a consistent point-in-time using
    // withConsistentSnapshot.
    QueryBatcher exportBatcher = moveMgr.newQueryBatcher(query)
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onUrisReady(
        new ExportListener()
          .withConsistentSnapshot()
          // Configure a listener to do some action for each document
          // in the batch when each batch is ready
          .onDocumentReady(
            doc -> {
              System.out.println("Product ID with Export Listener : "+ doc.getContentAs(ProductDetails.class).getId());
            }
          )
      )
      .onQueryFailure(throwable -> throwable.printStackTrace());

    moveMgr.startJob(exportBatcher);
    exportBatcher.awaitCompletion();
    moveMgr.stopJob(exportBatcher);
  }

  public void deleteDocuments() {
    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(
      new StructuredQueryBuilder().collection("products-collection1", "products-collection2")
    )
      .withBatchSize(batchSize)
      .onUrisReady(new DeleteListener()) // Sends a bulk delete for all the documents in each batch
      .withConsistentSnapshot()
      .onQueryFailure(throwable -> throwable.printStackTrace());

    moveMgr.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    moveMgr.stopJob(queryBatcher);
  }
}
