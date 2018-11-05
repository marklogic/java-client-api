package com.marklogic.client.dhs.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.dhs.JobRunner;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestJobRunner {
   @Test
   public void testRun() throws IOException {
      String csvFile = "data"+File.separator+"OrderLines.csv";

// TODO: switch to 8011
      DatabaseClient client = DatabaseClientFactory.newClient(
            "localhost", 8005, new DatabaseClientFactory.DigestAuthContext("admin", "admin")
      );

      QueryManager queryMgr = client.newQueryManager();

      JobRunner jobRunner = new JobRunner();
      String jobDirectory = jobRunner.getJobDirectory();

      try (
            InputStream csvStream = openStream(csvFile);
            InputStream jobStream = openStream(JobRunner.jobFileFor(csvFile));
      ) {
         jobRunner.run(client, csvStream, jobStream);

// TODO: assert expected count of 1960 against documents in jobDirectory
      } finally {
         DeleteQueryDefinition deleteDef = queryMgr.newDeleteDefinition();
         deleteDef.setDirectory(jobDirectory);
         queryMgr.delete(deleteDef);

         client.release();
      }

   }
   public InputStream openStream(String fileName) throws IOException {
      InputStream file = TestJobRunner.class.getClassLoader().getResourceAsStream(fileName);
      if (file == null) {
         throw new IOException("could not read file: "+fileName);
      }
      return file;
   }
}
