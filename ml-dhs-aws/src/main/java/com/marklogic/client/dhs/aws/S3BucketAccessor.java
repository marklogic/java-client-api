package com.marklogic.client.dhs.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;

public class S3BucketAccessor {
   private AmazonS3 s3Client;
   private String bucketName;

   public S3BucketAccessor(AmazonS3 s3Client, String bucketName) {
      this.s3Client   = s3Client;
      this.bucketName = bucketName;
   }

   public InputStream readObject(String objectKey) {
      S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, objectKey));
      InputStream objectStream = s3Object.getObjectContent();
      return objectStream;
   }
}
