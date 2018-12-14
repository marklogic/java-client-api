package com.marklogic.client.dhs.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.dhs.JobRunner;
import com.marklogic.client.dhs.test.TestJobRunner;
import com.marklogic.client.dhs.test.TestJobRunner.TestJobLogger;

import java.io.File;
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
		String objectKey = "csvTest/OrderLines.csv";

// TODO: real authentication and authorization
		AWSCredentials credentials = getCredentials();
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(regionName).build();

// TODO: take hostname for static enode - direct connect because within VPC
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8005,
				new DatabaseClientFactory.DigestAuthContext("admin", "admin"));
// TODO: configure logger for AWS CloudWatch

		S3BucketAccessor bucketAccessor = new S3BucketAccessor(s3Client, bucketName);

		try (InputStream csvStream = bucketAccessor.readObject(objectKey);
				InputStream jobStream = bucketAccessor.readObject(JobRunner.jobFileFor(objectKey));) {

			JobRunner jobRunner = new JobRunner();
			TestJobRunner testJobRunner = new TestJobRunner();
			TestJobLogger testJobLogger = testJobRunner.new TestJobLogger();
			File csvFilehandle = new File(TestJobRunner.class.getClassLoader().getResource(objectKey).toURI());
			long totalBytes = csvFilehandle.length();
			
			jobRunner.run(client, csvStream, jobStream, testJobLogger, totalBytes);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
// TODO: notification of success or failure - logging? SMS?
			client.release();
		}
	}

	private AWSCredentials getCredentials() {
		return new BasicAWSCredentials("<ACCESS_KEY>", "<SECRET_KEY>");
	}
}
