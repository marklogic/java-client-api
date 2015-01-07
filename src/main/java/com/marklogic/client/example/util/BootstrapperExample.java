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
package com.marklogic.client.example.util;

import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.http.client.ClientProtocolException;

import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.example.util.Bootstrapper.ConfigServer;
import com.marklogic.client.example.util.Bootstrapper.RESTServer;

/**
 * Sample main method for a REST bootstrapper.
 *
 */
public class BootstrapperExample {
	public static void main(String[] args)
	throws ClientProtocolException, IOException, XMLStreamException, FactoryConfigurationError
	{
		new BootstrapperExample().makeSampleServer();
	}
	public void makeSampleServer()
	throws ClientProtocolException, IOException, XMLStreamException, FactoryConfigurationError
	{
		new Bootstrapper().makeServer(
				new ConfigServer(
					"localhost", 8002, "admin", "admin", Authentication.DIGEST
				),
				new RESTServer(
					"Documents", "Modules", "Default", "DocuREST", 8014
				)
			);

		System.out.println(
				"Created DocuREST server on 8014 port for Documents database"
				);
	}
}
