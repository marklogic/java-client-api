package com.marklogic.client.datamovement.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;

public class AnyForestConfiguration implements ForestConfiguration {
   private Forest[] anyForests;
   private String[] primaryHosts;

   AnyForestConfiguration(DatabaseClient client) {
      if (client == null) throw new IllegalArgumentException("client argument must not be null");

      String primaryHost = client.getHost();
      String database    = client.getDatabase();

      this.anyForests   = new Forest[]{new AnyForest(primaryHost, database,true, false)};
      this.primaryHosts = new String[]{primaryHost};
   }

   @Override
   public Forest[] listForests() {
      return anyForests;
   }
   @Override
   public String[] getPreferredHosts() {
      return primaryHosts;
   }

   static public class AnyForest extends ForestImpl {
      private AnyForest(String primaryHost, String databaseName, boolean isUpdateable, boolean isDeleteOnly) {
         super(primaryHost, primaryHost, primaryHost, primaryHost, (databaseName == null) ? "" : databaseName,
               "*ANY*", "", isUpdateable, isDeleteOnly);
      }
   }
}