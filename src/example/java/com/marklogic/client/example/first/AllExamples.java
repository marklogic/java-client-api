package com.marklogic.client.example.first;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.marklogic.client.DatabaseClientFactory.Authentication;

/**
 * AllExamples executes all examples.  Please set up a REST server and configure Example.properties
 * before running any example.
 */
public class AllExamples {
	public static void main(String[] args) throws IOException {
		Properties props = loadProperties();

		// connection parameters
		String         host     = props.getProperty("example.host");
		int            port     = Integer.parseInt(props.getProperty("example.port"));
		String         user     = props.getProperty("example.writer_user");
		String         password = props.getProperty("example.writer_password");
		Authentication authType = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		// execute the examples
		ClientConnector.run(host, port, user, password, authType);
		DocumentWriter.run(host, port, user, password, authType);
		DocumentReader.run(host, port, user, password, authType);
		DocumentMetadataWriter.run(host, port, user, password, authType);
		DocumentMetadataReader.run(host, port, user, password, authType);
		DocumentDeleter.run(host, port, user, password, authType);
		DocumentFormats.run(host, port, user, password, authType);
	}

	// get the configuration for the examples
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			DocumentWriter.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
