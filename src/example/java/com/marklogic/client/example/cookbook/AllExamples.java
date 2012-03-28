package com.marklogic.client.example.cookbook;

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
		ClientConnect.run(         host, port, writer_user, writer_password, authType );
		DocumentWrite.run(         host, port, writer_user, writer_password, authType );
		DocumentRead.run(          host, port, writer_user, writer_password, authType );
		DocumentMetadataWrite.run( host, port, writer_user, writer_password, authType );
		DocumentMetadataRead.run(  host, port, writer_user, writer_password, authType );
		DocumentDelete.run(        host, port, writer_user, writer_password, authType );
		DocumentFormats.run(       host, port, writer_user, writer_password, authType );
		QueryOptions.run(          host, port, admin_user,  admin_password,  authType );
		StringSearch.run(          host, port, writer_user, writer_password, authType );
		KeyValueSearch.run(        host, port, writer_user, writer_password, authType );
	}

	// get the configuration for the examples
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			DocumentWrite.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
