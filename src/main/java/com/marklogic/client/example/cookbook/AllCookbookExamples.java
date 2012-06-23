/*
 * Copyright 2012 MarkLogic Corporation
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
import java.io.InputStream;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.marklogic.client.DatabaseClientFactory.Authentication;

/**
 * AllExamples executes all examples.  Please set up a REST server and configure Example.properties
 * before running any example.
 */
public class AllCookbookExamples {
	public static void main(String[] args) throws IOException, JAXBException {
		Properties props = loadProperties();

		// connection parameters for writer and admin users
		String         host            = props.getProperty("example.host");
		int            port            = Integer.parseInt(props.getProperty("example.port"));
		String         writer_user     = props.getProperty("example.writer_user");
		String         writer_password = props.getProperty("example.writer_password");
		String         admin_user      = props.getProperty("example.admin_user");
		String         admin_password  = props.getProperty("example.admin_password");
		Authentication authType        = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		// execute the examples
		ClientCreator.run(         host, port, writer_user, writer_password, authType );
		DocumentWrite.run(         host, port, writer_user, writer_password, authType );
		DocumentRead.run(          host, port, writer_user, writer_password, authType );
		DocumentMetadataWrite.run( host, port, writer_user, writer_password, authType );
		DocumentMetadataRead.run(  host, port, writer_user, writer_password, authType );
		DocumentDelete.run(        host, port, writer_user, writer_password, authType );
		DocumentFormats.run(       host, port, writer_user, writer_password, authType );
		DocumentOutputStream.run(  host, port, writer_user, writer_password, authType );
		JAXBDocument.run(          host, port, writer_user, writer_password, authType );
		KeyValueSearch.run(        host, port, writer_user, writer_password, authType );
		QueryOptions.run(          host, port, admin_user,  admin_password,  authType );
 		StringSearch.run(
				host, port, admin_user, admin_password, writer_user, writer_password, authType);
 		StructuredSearch.run(
				host, port, admin_user, admin_password, writer_user, writer_password, authType);
		MultiStatementTransaction.run(
				host, port, writer_user, writer_password, authType);
 		DocumentReadTransform.run(
				host, port, admin_user, admin_password, writer_user, writer_password, authType);
 		DocumentWriteTransform.run(
				host, port, admin_user, admin_password, writer_user, writer_password, authType);
 		OptimisticLocking.run(
				host, port, admin_user, admin_password, writer_user, writer_password, authType);
 		ResourceExtension.run(
				host, port, admin_user, admin_password, writer_user, writer_password, authType);
		// SSLClientCreator is not included in this list because it requires a change
		//     to the REST server that invalidates all of the other examples.  See
		//     the comments in SSLClientCreator.
	}

	// get the configuration for the examples
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			AllCookbookExamples.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
