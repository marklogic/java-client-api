package com.marklogic.client.example.cookbook.datamovement;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;

/**
 * This example demonstrates how to move data from one Marklogic Database to
 * another Marklogic Database. In this example, we can provide in a Cts query
 * thereby reducing the scope of documents to be transferred from one database
 * to another. We used an empty and query and retrieve the entire document set
 * from the source database. Also, this example shows how to export the
 * documents with transforms. We filter the order documents when doing the
 * export (filter in JVM) and for each customer document, we append all the
 * orders made by that customer with the help of a transform (in database
 * transform).
 *
 */
public class MoveDataBetweenMarklogicDBs {
  private static DatabaseClient sourceClient;
  private static DataMovementManager sourceMoveMgr;
  private static DataMovementManager destMoveMgr;
  private int batchSize = 3;
  private int threadCount  = 3;
  private String transformName = "moveDataBetweenMLDBs";
  private String ctsQuery = "<cts:and-query/>";
  
  public void run() {
    setup();
    loadDataIntoSourceDB();
    moveDataBetweenMarklogicDBs();
  }

  /*
   * Main function which exports the data from one database to another.
   */
  public void moveDataBetweenMarklogicDBs() {
    // Empty and query to retrieve the entire set of documents. This can be
    // replaced with any cts query narrowing the scope of documents to be
    // exported.
    String rawSearch = new StringBuilder()
        .append("<search:search ")
        .append("xmlns:search='http://marklogic.com/appservices/search' xmlns:cts='http://marklogic.com/cts'>")
        .append(ctsQuery)
        .append("</search:search>")
        .toString();
    ServerTransform transform = null;
    if ( !transformName.equals("") ) {
      transform = new ServerTransform(transformName);
    }
    QueryManager queryMgr = sourceClient.newQueryManager();
    RawCombinedQueryDefinition queryDef = queryMgr.newRawCombinedQueryDefinitionAs(Format.XML, rawSearch);

    // WriteBatcher for the destination DB to write the documents
    WriteBatcher destWriteBatcher = destMoveMgr.newWriteBatcher()
        .withBatchSize(batchSize)
        .withThreadCount(threadCount)
        .onBatchSuccess(
            batch -> System.out.println("Written " + batch.getJobWritesSoFar() + " documents into the target database"))
        .onBatchFailure((batch, throwable) -> throwable.printStackTrace());
    destMoveMgr.startJob(destWriteBatcher);

    ExportListener exportListener = new ExportListener()
        .withConsistentSnapshot()
        .onDocumentReady(record -> {
          /*
           * This is where you do a client side map (write one or more documents
           * per document read) or reduce (write one document per many documents
           * read) or filter (write certain documents which match a criteria)
           * Here we do a filter to export only customer documents.
           */
          if ( record.getUri().contains("customer") ) {
            destWriteBatcher.add("/exported" + record.getUri(), record.getMetadata(new StringHandle()),
                record.getContent(new StringHandle()).withFormat(record.getFormat()));
          }
        })
        .withMetadataCategory(Metadata.COLLECTIONS);

    // Read the documents with transform if a transform is specified - (in
    // database transform)
    if ( transform != null ) {
      exportListener.withTransform(transform);
    }

    // QueryBatcher for the source DB to query the documents to be exported.
    QueryBatcher sourceQueryBatcher = sourceMoveMgr.newQueryBatcher(queryDef)
        .withBatchSize(batchSize)
        .withThreadCount(threadCount)
        .onUrisReady(exportListener)
        .onQueryFailure(exception -> exception.printStackTrace());
    sourceMoveMgr.startJob(sourceQueryBatcher);
    // Wait till the query batcher completes.
    sourceQueryBatcher.awaitCompletion();
    sourceMoveMgr.stopJob(sourceQueryBatcher);

    // Wait till the write batcher completes
    destWriteBatcher.flushAndWait();
    destMoveMgr.stopJob(destWriteBatcher);
  }

  private void setup() {
    sourceClient = DatabaseClientSingleton.getAdmin("Documents");
    sourceMoveMgr = sourceClient.newDataMovementManager();
    try {
      Util.ExampleProperties props = Util.loadProperties();
      destMoveMgr = DatabaseClientFactory.newClient(props.host, props.port, new DigestAuthContext(props.adminUser, props.adminPassword))
              .newDataMovementManager();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /*
   * Load the documents into source DB for running this example 
   */
  private void loadDataIntoSourceDB() {
    String[] customers = { "{ \"name\" : \"Alice\", \"id\": 1, \"phone\" : 8793993333, \"state\":\"CA\"}",
        "{ \"name\" : \"Bob\", \"id\": 2, \"phone\" : 8793993334, \"state\":\"WA\"}",
        "{ \"name\" : \"Carl\", \"id\": 3, \"phone\" : 8793993335, \"state\":\"CA\"}",
        "{ \"name\" : \"Dennis\", \"id\": 4, \"phone\" : 8793993336, \"state\":\"OR\"}",
        "{ \"name\" : \"Evelyn\", \"id\": 5, \"phone\" : 8793993337, \"state\":\"CA\"}",
        "{ \"name\" : \"Falcon\", \"id\": 6, \"phone\" : 8793993338, \"state\":\"AZ\"}",
        "{ \"name\" : \"Gerald\", \"id\": 7, \"phone\" : 8793993339, \"state\":\"CA\"}",
        "{ \"name\" : \"Howard\", \"id\": 8, \"phone\" : 8793993340, \"state\":\"PA\"}",
        "{ \"name\" : \"Irwin\", \"id\": 9, \"phone\" : 8793993341, \"state\":\"CA\"}",
        "{ \"name\" : \"Jack\", \"id\": 10, \"phone\" : 8793993342, \"state\":\"OR\"}" };
    DocumentManager docMgr = sourceClient.newDocumentManager();
    DocumentMetadataHandle customerMetadataHandle = new DocumentMetadataHandle();
    customerMetadataHandle.getCollections().add("customers");
    docMgr.write(
        docMgr.newWriteSet().add("/moveData/customer1.json", customerMetadataHandle, new StringHandle(customers[0]).withFormat(Format.JSON))
            .add("/moveData/customer2.json", customerMetadataHandle, new StringHandle(customers[1]).withFormat(Format.JSON))
            .add("/moveData/customer3.json", customerMetadataHandle, new StringHandle(customers[2]).withFormat(Format.JSON))
            .add("/moveData/customer4.json", customerMetadataHandle, new StringHandle(customers[3]).withFormat(Format.JSON))
            .add("/moveData/customer5.json", customerMetadataHandle, new StringHandle(customers[4]).withFormat(Format.JSON))
            .add("/moveData/customer6.json", customerMetadataHandle, new StringHandle(customers[5]).withFormat(Format.JSON))
            .add("/moveData/customer7.json", customerMetadataHandle, new StringHandle(customers[6]).withFormat(Format.JSON))
            .add("/moveData/customer8.json", customerMetadataHandle, new StringHandle(customers[7]).withFormat(Format.JSON))
            .add("/moveData/customer9.json", customerMetadataHandle, new StringHandle(customers[8]).withFormat(Format.JSON))
            .add("/moveData/customer10.json", customerMetadataHandle, new StringHandle(customers[9]).withFormat(Format.JSON)));
    Random customerRandom = new Random();
    DocumentMetadataHandle orderMetadataHandle = new DocumentMetadataHandle();
    orderMetadataHandle.getCollections().add("orders");
    DocumentWriteSet orderWriteSet = docMgr.newWriteSet();
    long diff = (long) 30 * 24 * 60 * 60 * 1000;
    long endTime = new Date().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    for (int i = 0; i < 50; i++) {
      String orderDate = "\"orderdate\" : \"" + dateFormat.format(endTime - (long) (diff * Math.random())) + "\"";
      String orderDocument = "{ \"id\" : " + (i + 1) + ", \"customerid\" : " + (customerRandom.nextInt(10) + 1) + ", "
          + orderDate + "}";
      orderWriteSet.add("/moveData/order" + (i + 1) + ".json", orderMetadataHandle,
          new StringHandle(orderDocument).withFormat(Format.JSON));
    }
    docMgr.write(orderWriteSet);
    installTransform(sourceClient);
  }

  /*
   * Install the transform on the server which would find all the orders made by
   * the customer and append all the orders information to the corresponding
   * customer document.
   */
  private void installTransform(DatabaseClient client) {
    // this transform seeks orders associated with a single customer
    // record and injects the orders into the customer record
    String transform =
      "function transform_function(context, params, content) { " +
      "  var uri = context.uri; " +
      "  var customer = content.toObject(); " +
      "  var orders = cts.search(cts.andQuery([" +
      "    cts.collectionQuery('orders'), " +
      "    cts.jsonPropertyValueQuery('customerid', customer.id)" +
      "  ])); " +
      "  if ( fn.count(orders) > 0 ) { " +
      "    customer.orders = new Array(); " +
      "    for (let order of orders) { " +
      "      var customerOrder = order.toObject(); " +
      "      delete customerOrder.customerid; " +
      "      customer.orders.push(customerOrder); " +
      "    } " +
      "  } " +
      "  return customer; " +
      "}; " +
      "exports.transform = transform_function";
    ServerConfigurationManager confMgr = client.newServerConfigManager();
    TransformExtensionsManager transformMgr = confMgr.newTransformExtensionsManager();
    transformMgr.writeJavascriptTransformAs(transformName, transform);
  }

  public static void main(String args[]) {
    new MoveDataBetweenMarklogicDBs().run();
  }
}
