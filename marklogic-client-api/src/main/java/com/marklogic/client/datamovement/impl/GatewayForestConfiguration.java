package com.marklogic.client.datamovement.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;

import java.util.stream.Stream;

public class GatewayForestConfiguration extends ForestConfigurationImpl {
   private String[] preferredHosts;

   GatewayForestConfiguration(String primaryHost, Forest[] forests) {
      super(forests);
      if (primaryHost == null) throw new IllegalArgumentException("primaryHost argument must not be null");

      this.preferredHosts = new String[]{primaryHost};
   }

   @Override
   public String[] getPreferredHosts() {
      return preferredHosts;
   }

   static public class GatewayForest extends ForestImpl {
      public GatewayForest(String primaryHost, String databaseName, String forestName, String forestId, boolean isUpdateable, boolean isDeleteOnly) {
         super(primaryHost, primaryHost, primaryHost, primaryHost, databaseName, forestName, forestId, isUpdateable, isDeleteOnly);
      }
   }
}