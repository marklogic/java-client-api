/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.SuggestDefinition;

/**
 * Suggest illustrates getting suggestions for words to find in an element.
 * 
 * NOTE:  To get suggestions, you must configure the database with an element
 * word lexicon on the description element before running this example.  You
 * can configure an element word lexicon using the Admin UI on port 8000.
 */
public class Suggest {
	static final private String OPTIONS_NAME = "description";

	static final private String[] filenames = {"curbappeal.xml", "flipper.xml", "justintime.xml"};

	public static void main(String[] args)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		System.out.println("example: "+Suggest.class.getName());

		configure(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);

		suggest(props.host, props.port,
				props.writerUser, props.writerPassword, props.authType);

		tearDownExample(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);
	}

	public static void configure(String host, int port, String user, String password, Authentication authType)
	throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// create a manager for writing query options
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// construct the query options
        String options =
        	"<search:options "+
                	"xmlns:search='http://marklogic.com/appservices/search'>"+
                "<search:default-suggestion-source>"+
    				"<search:word>"+
    					"<search:element ns='' name='description'/>"+
    				"</search:word>"+
    			"</search:default-suggestion-source>"+
            "</search:options>";

        // create a handle to send the query options
		StringHandle writeHandle = new StringHandle(options);
		
		// write the query options to the database
		optionsMgr.writeOptions(OPTIONS_NAME, writeHandle);

		// release the client
		client.release();
	}

	public static void suggest(String host, int port, String user, String password, Authentication authType)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		setUpExample(client);

		// create a manager for getting suggestions
		QueryManager queryMgr = client.newQueryManager();

		// specify the partial criteria for the suggestions and the options
		//     that define the source of the suggestion words
		String partialCriteria = "tr";
		SuggestDefinition def = queryMgr.newSuggestDefinition(
				partialCriteria, OPTIONS_NAME);

		// get the suggestions
		String[] suggestions = queryMgr.suggest(def);

		System.out.println("'"+partialCriteria+"' criteria matched "+
				suggestions.length+" suggestions:");

		// iterate over the suggestions
		for (String suggestion: suggestions) {
			System.out.println("    "+suggestion);
		}

		// release the client
		client.release();
	}

	// set up by writing the document content and options used in the example query
	public static void setUpExample(DatabaseClient client)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		InputStreamHandle contentHandle = new InputStreamHandle();

		for (String filename: filenames) {
			InputStream docStream = Util.openStream("data"+File.separator+filename);
			if (docStream == null)
				throw new IOException("Could not read document example");

			contentHandle.set(docStream);

			docMgr.write("/example/"+filename, contentHandle);
		}
	}

	// clean up by deleting the documents and query options used in the example query
	public static void tearDownExample(
			String host, int port, String user, String password, Authentication authType)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		for (String filename: filenames) {
			docMgr.delete("/example/"+filename);
		}

		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		optionsMgr.deleteOptions(OPTIONS_NAME);

		client.release();
	}
}
