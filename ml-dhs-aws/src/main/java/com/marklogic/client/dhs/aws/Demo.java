package com.marklogic.client.dhs.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;

import java.io.IOException;
import java.io.InputStream;

public class Demo {
   public static void main(String... args) throws IOException {
      new Demo().loadJSONFromCSVOnS3();
   }

// TODO: receive notification of input (bucket + object), output (hostname), credentials, and metadata
   private void loadJSONFromCSVOnS3() throws IOException {
      String regionName = "us-west-1";
      String bucketName = "ehennum-west-test";
      String objectKey  = "csvTest/OrderLines.csv";

// TODO: real authentication and authorization
      AWSCredentials credentials = new BasicAWSCredentials(
            "<ACCESS_KEY>",
            "<SECRET_KEY>"
      );
      AmazonS3 s3Client = AmazonS3ClientBuilder
         .standard()
         .withCredentials(new AWSStaticCredentialsProvider(credentials))
         .withRegion(regionName)
         .build();


// TODO: take hostname for Curation load balancer, set ConnectionType.GATEWAY
      DatabaseClient client = DatabaseClientFactory.newClient(
            "localhost", 8005, new DatabaseClientFactory.DigestAuthContext("admin", "admin")
      );
// TODO: configure logger for AWS

      S3BucketAccessor bucketAccessor = new S3BucketAccessor(s3Client, bucketName);

      try (
            InputStream csvStream = bucketAccessor.readObject(objectKey);
            InputStream jobStream = bucketAccessor.readObject(JobRunner.jobFileFor(objectKey));
            ) {

         JobRunner jobRunner = new JobRunner();
         jobRunner.run(client, csvStream, jobStream);
      } finally {
// TODO: notification of success or failure - logging? SMS?
         client.release();
      }
   }
}
