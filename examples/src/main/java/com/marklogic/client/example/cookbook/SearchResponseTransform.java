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
package com.marklogic.client.example.cookbook;

import com.marklogic.client.*;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Search illustrates adding a result transformation to
 * a search.  The transformed result here is output as serialized HTML
 */
public class SearchResponseTransform {
  static final private String OPTIONS_NAME = "products";
  static final private String TRANSFORM_NAME = "search2html";

  static final private String[] filenames = {"curbappeal.xml", "flipper.xml", "justintime.xml"};

  public static void main(String[] args)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    System.out.println("example: "+SearchResponseTransform.class.getName());

    configure(props);
    installTransform(props);
    search(props);
    tearDownExample(props);
  }

  public static void installTransform(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
	  DatabaseClient client = Util.newAdminClient(props);

    // create a manager for transform extensions
    TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();

    // specify metadata about the transform extension
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Search-Response-TO-HTML XSLT Transform");
    metadata.setDescription("This plugin transforms a Search Response document to HTML");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");

    // acquire the transform source code
    try ( InputStream transStream = Util.openStream("scripts"+File.separator+TRANSFORM_NAME+".xsl") ) {
      if (transStream == null) throw new IOException("Could not read example transform");

      // create a handle on the transform source code
      InputStreamHandle handle = new InputStreamHandle();
      handle.set(transStream);

      // write the transform extension to the database
      transMgr.writeXSLTransform(TRANSFORM_NAME, handle, metadata);
    }

    System.out.println("Installed the "+TRANSFORM_NAME+" transform");

    // release the client
    client.release();
  }

  public static void configure(ExampleProperties props)
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException
  {
	  DatabaseClient client = Util.newAdminClient(props);


    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // construct the query options
    String options =
      "<search:options "+
        "xmlns:search='http://marklogic.com/appservices/search'>"+
        "<search:constraint name='industry'>"+
        "<search:value>"+
        "<search:element name='industry' ns=''/>"+
        "</search:value>"+
        "</search:constraint>"+
        "</search:options>";

    // create a handle to send the query options
    StringHandle writeHandle = new StringHandle(options);

    // write the query options to the database
    optionsMgr.writeOptions(OPTIONS_NAME, writeHandle);

    // release the client
    client.release();
  }

  public static void search(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
	  DatabaseClient client = Util.newClient(props);

    setUpExample(client);

    // create a manager for searching
    QueryManager queryMgr = client.newQueryManager();

    // create a search definition
    StringQueryDefinition querydef = queryMgr.newStringDefinition(OPTIONS_NAME);
    querydef.setCriteria("neighborhood industry:\"Real Estate\"");

    // set the response transform name
    querydef.setResponseTransform(new ServerTransform(TRANSFORM_NAME));

    // create a raw content handle for the search results
    StringHandle resultsHandle = new StringHandle();

    // run the search
    queryMgr.search(querydef, resultsHandle);

    System.out.println("Matched documents with '"+querydef.getCriteria()+"'\n");

    // all we can do is output the result.
    System.out.println(resultsHandle.get());

    // release the client
    client.release();
  }

  // set up by writing the document content and options used in the example query
  public static void setUpExample(DatabaseClient client)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    InputStreamHandle contentHandle = new InputStreamHandle();

    for (String filename: filenames) {
      try ( InputStream docStream = Util.openStream("data"+File.separator+filename) ) {
        if (docStream == null)
          throw new IOException("Could not read document example");

        contentHandle.set(docStream);

        docMgr.write("/example/"+filename, contentHandle);
      }
    }


  }

  // clean up by deleting the documents and query options used in the example query
  public static void tearDownExample(ExampleProperties props)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
	  DatabaseClient client = Util.newAdminClient(props);

    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    for (String filename: filenames) {
      docMgr.delete("/example/"+filename);
    }

    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    optionsMgr.deleteOptions(OPTIONS_NAME);

    client.release();
  }
}
