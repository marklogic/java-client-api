/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.xpath.XPathExpressionException;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;

/**
 * AllCookbookExamples executes all of the recipe examples in the cookbook.
 * Please set up a REST server and configure Example.properties
 * before running any example.
 */
public class AllCookbookExamples {
  public static void main(String[] args)
    throws IOException, JAXBException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException, XPathExpressionException {
    ExampleProperties props = Util.loadProperties();

    // execute the examples
    ClientCreator.run(             props );
    DocumentWrite.run(             props );
    DocumentWriteServerURI.run(    props );
    DocumentRead.run(              props );
    DocumentMetadataWrite.run(     props );
    DocumentMetadataRead.run(      props );
    DocumentDelete.run(            props );
    DocumentFormats.run(           props );
    DocumentOutputStream.run(      props );
    JAXBDocument.run(              props );
    QueryOptions.run(              props );
    StringSearch.run(              props );
    StructuredSearch.run(          props );
    RawCombinedSearch.run(         props );
    SearchResponseTransform.run(   props );
    MultiStatementTransaction.run( props );
    DocumentReadTransform.run(     props );
    DocumentWriteTransform.run(    props );
    OptimisticLocking.run(         props );
    RawClientAlert.run(            props );
    ResourceExtension.run(         props );
    // SSLClientCreator is not included in this list because it requires a change
    //     to the REST server that invalidates all of the other examples.  See
    //     the comments in SSLClientCreator.
  }
}
