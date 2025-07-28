package com.marklogic.client.example.cookbook.datamovement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.function.Function;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.expression.PlanBuilder.AccessPlan;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.row.RowManager;

/**
 * This is the same use case as BulkExportOpticResults but it is more advanced
 * than that. In BulkExportOpticResults, we just print the output rows. Here, we
 * use custom created listener - OpticExportToWriterListener to write the rows
 * to a Writer output stream. Here, we write it into a text file.
 * 
 * JoinWithOptic illustrates how to use DMSDK with Optic API. We use DMSDK to
 * query and retrieve a set of document URIs and per batch of URIs, we use TDE
 * to create views and use Optic API to perform joins.<br>
 * <br>
 * SCENARIO: We have a requirement to do a recall and retrieve the customer
 * names and phone numbers who have placed an order for a particular product.
 * For this requirement, we have to join both customer information and order
 * information to get both phone number and order information. Since we could
 * have a huge number of orders in a production environment, we use DMSDK to get
 * the orders having that particular product and process it batch by batch to
 * join with customer information in a multi-threaded asynchronous manner
 * instead of doing a single join over the large dataset. For this example, we
 * use the same process but on a smaller dataset.<br>
 * We have a set of documents which have information about the customers namely
 * customerid, name, phone number, state etc. We also have a set of documents
 * which have information about the orders placed by those customers namely
 * orderid, customerid of the customer who placed the order, products ordered
 * and the date on which the order was placed.<br>
 *
 */
public class BulkExportOpticResultsToWriter {
  private DatabaseClient client = DatabaseClientSingleton.get();
  private DataMovementManager moveMgr = client.newDataMovementManager();
  private int batchSize = 3;
  private int threadCount = 3;
  private String schemaDB = "Schemas";
  private String outputFileName = "opticExampleOutput";
  private String customerTemplateFile = "/opticExample/customer.tdex";
  private String orderTemplateFile = "/opticExample/order.tdex";
  private DatabaseClient schemaDBclient = DatabaseClientSingleton.getAdmin(schemaDB);

  public static void main(String args[]) throws IOException {
    new BulkExportOpticResultsToWriter().run();
  }

  private void run() throws IOException {
    int productID = 15;
    setup();
    exportWithOptic(productID);
    tearDown();
  }

  private void setup() {
    /*
     * Populate the schema database with template documents (Template Driven
     * Extraction) which are needed to project SQL tables from the documents
     * present in the database. We have two template documents - one for
     * customers and the other for orders.
     * 
     * From customers, we project name, id and phone number From orders, we
     * project orderid, customerid and orderdate
     * 
     */
    TextDocumentManager schemaDocMgr = schemaDBclient.newTextDocumentManager();
    StringHandle orderHandle = new StringHandle();
    orderHandle.set("<template xmlns=\"http://marklogic.com/xdmp/tde\"> " + 
      "<context>/orders</context>" +  
      "<rows>" +
      "<row>" +
        "<schema-name>opticExample</schema-name>" +
        "<view-name>orders</view-name>" +
        "<columns>" +
          "<column><name>id</name><scalar-type>int</scalar-type><val>id</val></column>" +
          "<column><name>customerid</name><scalar-type>int</scalar-type><val>customerid</val></column>" +
          "<column><name>orderdate</name><scalar-type>dateTime</scalar-type><val>orderdate</val></column>" +
        "</columns>" +
      "</row>" +
      "</rows>" +
      "</template>");
    StringHandle customerHandle = new StringHandle();
    customerHandle.set("<template xmlns=\"http://marklogic.com/xdmp/tde\">" +
      "<context>/customers</context>" +
      "<rows>" + 
      "<row>" + 
        "<schema-name>opticExample</schema-name>" + 
        "<view-name>customers</view-name>" + 
        "<columns>" + 
          "<column><name>name</name><scalar-type>string</scalar-type><val>name</val></column>" + 
          "<column><name>id</name><scalar-type>int</scalar-type><val>id</val></column>" + 
          "<column><name>phone</name><scalar-type>string</scalar-type><val>phone</val></column>" + 
        "</columns>" + 
      "</row>" + 
      "</rows>" + 
      "</template>");
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    metadataHandle.getPermissions().add("rest-writer", Capability.UPDATE, Capability.READ);
    metadataHandle.getPermissions().add("rest-reader", Capability.READ);
    metadataHandle.getCollections().add("http://marklogic.com/xdmp/tde");
    schemaDocMgr.write(orderTemplateFile, metadataHandle, orderHandle);
    schemaDocMgr.write(customerTemplateFile, metadataHandle, customerHandle);

    /*
     * Create customer and order documents to demonstrate the example.
     * 
     * Sample customer document:
     * <customers> 
     *   <name>Alice</name> 
     *   <id>1</id> 
     *   <phone>8793993333</phone> 
     *   <state>CA</state> 
     * </customers>
     * 
     * Sample order document:
     * <orders>
     *   <id>16</id>
     *   <customerid>3</customerid>
     *   <products>
     *     <product>37</product>
     *     <product>45</product>
     *   </products>
     *   <orderdate>2017-07-10T03:25:45Z</orderdate>
     * </orders>
     * 
     */

    // Populate Customers data set
    String[] customers = {
        "<customers> <name>Alice</name> <id>1</id> <phone>8793993333</phone> <state>CA</state> </customers>",
        "<customers> <name>Bob</name> <id>2</id> <phone>8793993334</phone> <state>CA</state> </customers>",
        "<customers> <name>Carl</name> <id>3</id> <phone>8793993335</phone> <state>CA</state> </customers>",
        "<customers> <name>Dennis</name> <id>4</id> <phone>8793993336</phone> <state>CA</state> </customers>",
        "<customers> <name>Evelyn</name> <id>5</id> <phone>8793993337</phone> <state>CA</state> </customers>" };
    DocumentManager docMgr = client.newDocumentManager();
    docMgr.write(
        docMgr.newWriteSet().add("/opticExample/customer1.xml", new StringHandle(customers[0]).withFormat(Format.XML))
            .add("/opticExample/customer2.xml", new StringHandle(customers[1]).withFormat(Format.XML))
            .add("/opticExample/customer3.xml", new StringHandle(customers[2]).withFormat(Format.XML))
            .add("/opticExample/customer4.xml", new StringHandle(customers[3]).withFormat(Format.XML))
            .add("/opticExample/customer5.xml", new StringHandle(customers[4]).withFormat(Format.XML)));

    // Populate Order Data set
    int numOfProductsPerOrder = 10;
    DocumentWriteSet orderWriteSet = docMgr.newWriteSet();
    DocumentMetadataHandle orderMetadataHandle = new DocumentMetadataHandle();
    orderMetadataHandle.getCollections().add("orders");
    Random customerRandom = new Random();
    Random productRandom = new Random();
    Random productIDRandom = new Random();
    long diff = (long) 30 * 24 * 60 * 60 * 1000;
    long endTime = new Date().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    for (int i = 0; i < 30; i++) {
      StringBuilder products = new StringBuilder("<products>");
      int numProducts = productRandom.nextInt(numOfProductsPerOrder) + 1;
      String orderDate = "<orderdate>" + dateFormat.format(endTime - (long) (diff * Math.random())) + "</orderdate>";
      for (int j = 0; j < numProducts; j++) {
        products.append("<product>" + (productIDRandom.nextInt(15) + 1) + "</product>");
      }
      products.append("</products>");
      String orderDocument = "<orders> <id>" + (i + 1) + "</id> <customerid>" + (customerRandom.nextInt(5) + 1)
          + "</customerid>" + products.toString() + orderDate + "</orders>";
      orderWriteSet.add("/opticExample/order" + (i + 1) + ".xml", orderMetadataHandle,
          new StringHandle(orderDocument).withFormat(Format.XML));
    }
    docMgr.write(orderWriteSet);
  }

  private void exportWithOptic(int productID) throws IOException {
    // Build a query to retrieve the order documents that contain a particular
    // product with a given product ID and pass it to the QueryBatcher.
    StructuredQueryBuilder sb = new StructuredQueryBuilder();
    StructuredQueryDefinition query = sb.and(sb.collection("orders"), 
        sb.value(sb.element("product"), productID));
    // Create a Row manager to construct plans and query on
    // rows projected from the documents
    RowManager rowMgr = client.newRowManager();
    File outputFile = File.createTempFile(outputFileName, ".txt");
    System.out.println("Writing the results to " + outputFile.getAbsolutePath());
    try (FileWriter writer = new FileWriter(outputFile)) {
      // Create a Function to pass to the OpticExportToWriterListener which
      // would take each batch and do the necessary optic operations and return
      // the Plan that will retrieve the rows
      Function<QueryBatch, PlanBuilder.Plan> function = batch -> {
        // Create a PlanBuilder to build different plans to be executed on the
        // server
        PlanBuilder planBuilder = rowMgr.newPlanBuilder();
        // Create a plan from the orders view. We pass in the
        // URIs from the batch in the where clause thereby limiting the plan
        // to contain rows matched by the above query definition. Hence this
        // plan would ultimately retrieve one batch of order information of the
        // orders which contains a specific product.
        ModifyPlan orderPlan = planBuilder.fromView("opticExample", "orders")
            .where(planBuilder.cts.documentQuery(planBuilder.xs.stringSeq(batch.getItems())));
        // Create a plan from the customers view. This plan would
        // retrieve the list of all the customers present in the database
        AccessPlan customerPlan = planBuilder.fromView("opticExample", "customers");
        // Create a new plan from the existing plans by doing an inner join on
        // the above two plans on customer id which would get us the name and
        // phone number information of the customers who have ordered a specific
        // product.
        ModifyPlan joinPlan = customerPlan
            .joinInner(orderPlan,
                planBuilder.on(planBuilder.schemaCol("opticExample", "orders", "customerid"),
                    customerPlan.col("id")))
            .select(planBuilder.col("name"), planBuilder.col("phone"), planBuilder.col("orderdate"),
                planBuilder.as("orderid", planBuilder.schemaCol("opticExample", "orders", "id")));
        return joinPlan;
      };
      // Create a QueryBatcher to get the uris from the database which match the
      // query definition and pass it to the OpticExportToWriterListener for
      // creating the Optic API plans and writing the results of the plan to the
      // Writer
      QueryBatcher queryBatcher = moveMgr.newQueryBatcher(query)
          .withBatchSize(batchSize)
          .withThreadCount(threadCount)
          .onUrisReady(
              // Pass in the Function created above for transforming the batch
              // into Optic Plan, RowManager to iterate through the matching
              // records and Writer to which the records need to be written to
              // OpticExportToWriterListener.
              new OpticExportToWriterListener(function, rowMgr, writer).withRecordSuffix("\n")
              // Pass in a listener which would act on each RowRecord
              // generated from the Plan object and give an output string
              // for the Writer to write
              .onGenerateOutput(record -> {
                return "Order id : " + record.getString("orderid") 
                    + "\nCustomer name : " + record.getString("name")
                    + "\nPhone number : " + record.getString("phone") 
                    + "\nOrder date : " + record.getString("orderdate") + " \n\n";
              }))
          // Add a listener to do some action when the batch fails to retrieve
          .onQueryFailure(throwable -> throwable.printStackTrace());

      moveMgr.startJob(queryBatcher);
      // Wait till the batch completes
      queryBatcher.awaitCompletion();
      moveMgr.stopJob(queryBatcher);
    }
  }

  private void tearDown() {
    // Delete the inserted template documents from the Schema Database
    TextDocumentManager schemaDocMgr = schemaDBclient.newTextDocumentManager();
    schemaDocMgr.delete(customerTemplateFile);
    schemaDocMgr.delete(orderTemplateFile);

    // Delete the dataset - order documents and customer documents
    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(new StructuredQueryBuilder()
        .directory(true, "/opticExample/"))
        .withBatchSize(batchSize)
        .onUrisReady(new DeleteListener())
        .withConsistentSnapshot()
        .onQueryFailure(throwable -> throwable.printStackTrace());

    moveMgr.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    moveMgr.stopJob(queryBatcher);
    moveMgr.release();
  }
}