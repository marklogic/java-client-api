package com.marklogic.client.spark;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SparkConnector {

    public static void main(String[] args) throws IOException, InterruptedException {

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("BulkIOSparkConnector").setMaster("local");

        SparkSession spark = SparkSession.builder().appName("BulkIOSparkConnector").config(sparkConf).getOrCreate();

        //ExecutorService automatically provides a pool of threads and API for assigning tasks to it.
        ExecutorService executor = Executors.newFixedThreadPool(5);
        System.out.println("Available " + Runtime.getRuntime().availableProcessors() + " threads...");
        executor.execute(new SparkTask("java-unittest-1"));
        executor.execute(new SparkTask("java-unittest-2"));
        executor.execute(new SparkTask("java-unittest-3"));

        executor.awaitTermination(30, TimeUnit.SECONDS);
        executor.shutdown();
        spark.stop();
    }
}
