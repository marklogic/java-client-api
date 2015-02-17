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

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

/**
 * QueryOptions illustrates writing, reading, and deleting query options.
 */
public class QueryOptions {
	public static void main(String[] args)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		System.out.println("example: "+QueryOptions.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);

		// use either shortcut or strong typed IO
		runShortcut(client);
		runStrongTyped(client);

		// release the client
		client.release();
	}

	public static void runShortcut(DatabaseClient client)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		String optionsName = "products";

		// create a manager for writing, reading, and deleting query options
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// construct the query options
        String options = new StringBuilder()
        	.append("<search:options ")
        	.append(    	"xmlns:search='http://marklogic.com/appservices/search'>")
            .append(    "<search:constraint name='industry'>")
            .append(    	"<search:value>")
            .append(    		"<search:element name='industry' ns=''/>")
            .append(    	"</search:value>")
            .append(    "</search:constraint>")
            .append(   "</search:options>")
            .toString();

		// write the query options to the database
		optionsMgr.writeOptionsAs(optionsName, Format.XML, options);

		// read the query options from the database
		String readOptions = optionsMgr.readOptionsAs(optionsName, Format.XML, String.class);

		// delete the query options
		optionsMgr.deleteOptions(optionsName);

		System.out.println("(Shortcut) Wrote, read, and deleted '"+optionsName+
				"' query options:\n"+readOptions);
	}

	public static void runStrongTyped(DatabaseClient client)
	throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		String optionsName = "products";

		// create a manager for writing, reading, and deleting query options
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// construct the query options
        String options = new StringBuilder()
        	.append("<search:options ")
        	.append(    	"xmlns:search='http://marklogic.com/appservices/search'>")
            .append(    "<search:constraint name='industry'>")
            .append(    	"<search:value>")
            .append(    		"<search:element name='industry' ns=''/>")
            .append(    	"</search:value>")
            .append(    "</search:constraint>")
            .append(   "</search:options>")
            .toString();

        // create a handle to send the query options
		StringHandle writeHandle = new StringHandle(options);

		// write the query options to the database
		optionsMgr.writeOptions(optionsName, writeHandle);

		// create a handle to receive the query options
		StringHandle readHandle = new StringHandle();

		// read the query options from the database
		optionsMgr.readOptions(optionsName, readHandle);

		// access the query options
		String readOptions = readHandle.get();

		// delete the query options
		optionsMgr.deleteOptions(optionsName);

		System.out.println("(Strong Typed) Wrote, read, and deleted '"+optionsName+
				"' query options:\n"+readOptions);
	}
}
