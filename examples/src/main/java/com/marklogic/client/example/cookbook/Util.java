/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utilities to support and simplify examples.
 */
public class Util {
  /**
   * ExampleProperties represents the configuration for the examples.
   */
  static public class ExampleProperties {
    public String         host;
    public int            port = -1;
    public String         adminUser;
    public String         adminPassword;
    public String         readerUser;
    public String         readerPassword;
    public String         writerUser;
    public String         writerPassword;
    public String         jdbcUrl;
    public String         jdbcUser;
    public String         jdbcPassword;
    public ExampleProperties(Properties props) {
      super();
      host           = System.getProperty("EXAMPLE_HOST", props.getProperty("example.host"));
      System.out.println("Example program will connect to host: " + host);
      port           = Integer.parseInt(props.getProperty("example.port"));
      adminUser      = props.getProperty("example.admin_user");
      adminPassword  = System.getProperty("EXAMPLE_ADMIN_PASSWORD", props.getProperty("example.admin_password"));
      readerUser     = props.getProperty("example.reader_user");
      readerPassword = props.getProperty("example.reader_password");
      writerUser     = props.getProperty("example.writer_user");
      writerPassword = props.getProperty("example.writer_password");
      jdbcUrl = props.getProperty("example.jdbc.url");
      jdbcUser = props.getProperty("example.jdbc.user");
      jdbcPassword = props.getProperty("example.jdbc.password");
    }
  }

  public static DatabaseClient newClient(ExampleProperties props) {
	  return DatabaseClientFactory.newClient(props.host, props.port,
		  new DatabaseClientFactory.DigestAuthContext(props.writerUser, props.writerPassword)
	  );
  }

	public static DatabaseClient newAdminClient(ExampleProperties props) {
		return DatabaseClientFactory.newClient(props.host, props.port,
			new DatabaseClientFactory.DigestAuthContext(props.adminUser, props.adminPassword)
		);
	}

  /**
   * Read the configuration properties for the example from the file
   * Example.properties.
   * @return	the configuration object
   * @throws IOException if one occurs while loading the properties
   */
  public static ExampleProperties loadProperties() throws IOException {
    String propsName = "Example.properties";

    try ( InputStream propsStream = openStream(propsName) ) {
      if (propsStream == null)
        throw new IOException("Could not read properties "+propsName);

      Properties props = new Properties();
      props.load(propsStream);

      return new ExampleProperties(props);
    }
  }

  /**
   * Read a resource for an example.
   * @param fileName	the name of the resource
   * @return	an input stream for the resource
   * @throws IOException if one occurs opening the stream
   */
  public static InputStream openStream(String fileName) throws IOException {
    return Util.class.getClassLoader().getResourceAsStream(fileName);
  }
}
