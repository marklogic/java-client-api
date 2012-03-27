package com.marklogic.client.example.first;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

/**
 * ClientConnector illustrates how to connect to a database.
 */
public class ClientConnector {

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

		run(host, port, user, password, authType);
	}

	public static void run(String host, int port, String user, String password, Authentication authType)
	throws IOException {
		// connect the client
		DatabaseClient client =
			DatabaseClientFactory.connect(host, port, user, password, authType);

		System.out.println("Connected to "+host+":"+port+" as "+user);

		// release the client
		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			ClientConnector.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
