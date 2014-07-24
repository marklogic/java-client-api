package com.marklogic.javaclient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.io.DocumentMetadataHandle;

import java.net.InetAddress;

import org.junit.Test;

import static org.junit.Assert.*;

public abstract class ConnectedRESTQA {
	
	/**
	 * Use Rest call to create a database.
	 *  @param dbName
	 */
	
	public static void createDB(String dbName)	{
	try {	
		
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpPost post = new HttpPost("http://localhost:8002"+ "/manage/v2/databases?format=json");
			String JSONString = "[{\"name\":\""+ dbName + "\"}]";

       post.addHeader("Content-type", "application/json");
		    post.setEntity( new StringEntity(JSONString));
		
		
			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();

		    if (respEntity != null) {
		        // EntityUtils to get the response content
		        String content =  EntityUtils.toString(respEntity);
		        System.out.println(content);
		    }
		}catch (Exception e) {
		    // writing error to Log
		    e.printStackTrace();
		}
	}
		/*
	 * 
	 */
	public static void createForest(String fName,String dbName)	{
	try{
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(
				new AuthScope("localhost", 8002),
				new UsernamePasswordCredentials("admin", "admin"));
		HttpPost post = new HttpPost("http://localhost:8002"+ "/manage/v2/forests?format=json");
		String hName =InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
		String JSONString = 
				"{\"database\":\""+ 
				dbName + 
				"\",\"forest-name\":\""+
				fName+
				"\",\"host\":\""+hName+"\"}" 
								;
//		System.out.println(JSONString);
		post.addHeader("Content-type", "application/json");
		post.setEntity(new StringEntity(JSONString));
	
		HttpResponse response = client.execute(post);
		HttpEntity respEntity = response.getEntity();

		if (respEntity != null) {
		// EntityUtils to get the response content
		String content =  EntityUtils.toString(respEntity);
		System.out.println(content);
		}
		}catch (Exception e) {
		    // writing error to Log
		    e.printStackTrace();
		}
	}
	/*
	 * creating forests on different hosts
	 */
	public static void createForestonHost(String fName,String dbName,String hName)	{
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpPost post = new HttpPost("http://localhost:8002"+ "/manage/v2/forests?format=json");
			String JSONString = 
					"{\"database\":\""+ 
					dbName + 
					"\",\"forest-name\":\""+
					fName+
					"\",\"host\":\""+hName+"\"}" 
									;
//			System.out.println(JSONString);
			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(JSONString));
		
			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
			// EntityUtils to get the response content
			String content =  EntityUtils.toString(respEntity);
			System.out.println(content);
			}
			}catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}
		}
		
	public static void assocRESTServer(String restServerName,String dbName,int restPort)	{
		try{
			DefaultHttpClient client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpPost post = new HttpPost("http://localhost:8002"+ "/v1/rest-apis?format=json");
//			
			String JSONString = 
					"{ \"rest-api\": {\"database\":\""+ 
					dbName + 
					"\",\"name\":\""+
					restServerName +
					"\",\"port\":\""+
					restPort+
					"\"}}";
//			System.out.println(JSONString);		
			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(JSONString));
		
				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();

			    if (respEntity != null) {
			        // EntityUtils to get the response content
			        String content =  EntityUtils.toString(respEntity);
			        System.out.println(content);
			    }
			}catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}
		}
/*
 * Creating RESTServer With default content and module database
 */
	public static void createRESTServerWithDB(String restServerName,int restPort)	{
		try{
			DefaultHttpClient client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpPost post = new HttpPost("http://localhost:8002"+ "/v1/rest-apis?format=json");
//			
			String JSONString = 
					"{ \"rest-api\": {\"name\":\""+
					restServerName +
					"\",\"port\":\""+
					restPort+
					"\"}}";
			//System.out.println(JSONString);		
			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(JSONString));
		
				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();

			    if (respEntity != null) {
			        // EntityUtils to get the response content
			        String content =  EntityUtils.toString(respEntity);
			        System.out.println(content);
			    }
			}catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}
		}

	/*
	 * This function creates database,forests and REST server independently and attaches the database to the rest server
	 * 
	 */
	public static void setupJavaRESTServer(String dbName, String fName, String restServerName, int restPort)throws Exception{
		 
			createDB(dbName); 
			createForest(fName,dbName); 
			Thread.sleep(1500); 
	        assocRESTServer(restServerName, dbName,restPort);
	        createRESTUser("rest-admin","x","rest-admin");
	        createRESTUser("rest-writer","x","rest-writer");
	        createRESTUser("rest-reader","x","rest-reader");
	}
	/*
	 * This function creates a REST server with default content DB, Module DB
	 */

	 public static void createRESTUser(String usrName, String pass, String... roleNames ){
	    	try{
				DefaultHttpClient client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(
						new AuthScope("localhost", 8002),
						new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://localhost:8002"+ "/manage/v2/users/"+usrName);
			HttpResponse resp = client.execute(getrequest);
			 
			if( resp.getStatusLine().getStatusCode() == 200)
             {
				 System.out.println("User already exist");
             }
			 else {
				 System.out.println("User dont exist");
				 client = new DefaultHttpClient();
					client.getCredentialsProvider().setCredentials(
							new AuthScope("localhost", 8002),
							new UsernamePasswordCredentials("admin", "admin"));
						
		 	ObjectMapper mapper = new ObjectMapper();
			ObjectNode mainNode = mapper.createObjectNode();
//			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode childArray = mapper.createArrayNode();
			mainNode.put("name",usrName);
			mainNode.put("description", "user discription");
			mainNode.put("password", pass);
			for(String rolename: roleNames)
			childArray.add(rolename);
			mainNode.withArray("role").addAll(childArray);
			//System.out.println(type + mainNode.path("range-element-indexes").path("range-element-index").toString());
				System.out.println(mainNode.toString());
				HttpPost post = new HttpPost("http://localhost:8002"+ "/manage/v2/users?format=json");
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(mainNode.toString()));
			
				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();
               if( response.getStatusLine().getStatusCode() == 400)
               {
             	  System.out.println("User already exist");
               }
               else if (respEntity != null) {
				// EntityUtils to get the response content
				String content =  EntityUtils.toString(respEntity);
				System.out.println(content);
				}
               else {System.out.print("No Proper Response");}
			 }
				}catch (Exception e) {
				    // writing error to Log
				    e.printStackTrace();
				}
	    }
	 
	 /*
	  *  "permission": [
    {
      "role-name": "dls-user",
      "capability": "read"
    }
	  */

	 public static ObjectNode getPermissionNode(String roleName, DocumentMetadataHandle.Capability... cap){
		 ObjectMapper mapper= new ObjectMapper();
		 ObjectNode mNode = mapper.createObjectNode();
		 ArrayNode aNode = mapper.createArrayNode();
		 
		 for(DocumentMetadataHandle.Capability c : cap){
			 ObjectNode roleNode =mapper.createObjectNode();
			 roleNode.put("role-name",roleName);
			 roleNode.put("capability", c.toString().toLowerCase());
		 	 aNode.add(roleNode);
		 }
		 mNode.withArray("permission").addAll(aNode);
		 return mNode;
	 }
	 
	 /*
	  * "collection":
[
"dadfasd",
"adfadsfads"
]
	  */
	 public static ObjectNode getCollectionNode(String... collections){
		 ObjectMapper mapper= new ObjectMapper();
		 ObjectNode mNode = mapper.createObjectNode();
		 ArrayNode aNode = mapper.createArrayNode();
		 
		 for(String c : collections){
			 aNode.add(c);
		 }
		 mNode.withArray("collection").addAll(aNode);
		 return mNode;
	 }
	 
	 public static void createRESTUserWithPermissions(String usrName, String pass,ObjectNode perm,ObjectNode colections, String... roleNames ){
	    	try{
				DefaultHttpClient client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(
						new AuthScope("localhost", 8002),
						new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://localhost:8002"+ "/manage/v2/users/"+usrName);
			HttpResponse resp = client.execute(getrequest);
			 
			if( resp.getStatusLine().getStatusCode() == 200)
          {
				 System.out.println("User already exist");
          }
			 else {
				 System.out.println("User dont exist");
				 client = new DefaultHttpClient();
					client.getCredentialsProvider().setCredentials(
							new AuthScope("localhost", 8002),
							new UsernamePasswordCredentials("admin", "admin"));
						
		 	ObjectMapper mapper = new ObjectMapper();
			ObjectNode mainNode = mapper.createObjectNode();
//			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode childArray = mapper.createArrayNode();
			mainNode.put("name",usrName);
			mainNode.put("description", "user discription");
			mainNode.put("password", pass);
			for(String rolename: roleNames)
			childArray.add(rolename);
			mainNode.withArray("role").addAll(childArray);
			mainNode.setAll(perm);
			mainNode.setAll(colections);
			//System.out.println(type + mainNode.path("range-element-indexes").path("range-element-index").toString());
				System.out.println(mainNode.toString());
				HttpPost post = new HttpPost("http://localhost:8002"+ "/manage/v2/users?format=json");
				post.addHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(mainNode.toString()));
			
				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();
            if( response.getStatusLine().getStatusCode() == 400)
            {
          	  System.out.println("Bad User creation request");
            }
            else if (respEntity != null) {
				// EntityUtils to get the response content
				String content =  EntityUtils.toString(respEntity);
				System.out.println(content);
				}
            else {System.out.print("No Proper Response");}
			 }
				}catch (Exception e) {
				    // writing error to Log
				    e.printStackTrace();
				}
	    }

    public static void deleteRESTUser(String usrName){
	try{
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(
				new AuthScope("localhost", 8002),
				new UsernamePasswordCredentials("admin", "admin"));
		
		HttpDelete delete = new HttpDelete("http://localhost:8002/manage/v2/users/"+usrName);
		
			HttpResponse response = client.execute(delete);
		if(response.getStatusLine().getStatusCode()== 202){
						Thread.sleep(3500);
		}
		}catch (Exception e) {
		    // writing error to Log
		    e.printStackTrace();
		}
	
}
	 
	 public static void setupJavaRESTServerWithDB( String restServerName, int restPort)throws Exception{		 
        createRESTServerWithDB(restServerName, restPort);
        createRESTUser("rest-admin","x","rest-admin");
        createRESTUser("rest-writer","x","rest-writer");
        createRESTUser("rest-reader","x","rest-reader"); 
}
/*
 * This function deletes the REST appserver along with attached content database and module database
 */
	
	
	public static void deleteRESTServerWithDB(String restServerName)	{
		try{
			DefaultHttpClient client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete("http://localhost:8002/v1/rest-apis/"+restServerName+"?include=content&include=modules");
			
				HttpResponse response = client.execute(delete);
			if(response.getStatusLine().getStatusCode()== 202){
							Thread.sleep(3500);
			}
			}catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}
		}
	/*
	 * 
	 */
	public static void deleteRESTServer(String restServerName)	{
		try{
			DefaultHttpClient client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete("http://localhost:8002/v1/rest-apis/"+restServerName+"&include=modules");
			HttpResponse response = client.execute(delete);
			
			if(response.getStatusLine().getStatusCode()== 202){
				Thread.sleep(3500);
				waitForServerRestart();
			}
			else System.out.println("Server response "+response.getStatusLine().getStatusCode());
			}catch (Exception e) {
			    // writing error to Log
				System.out.println("Inside Deleting Rest server is throwing an error");
			    e.printStackTrace();
			}
		}
	public static void detachForest(String dbName, String fName){
		try{
			DefaultHttpClient client = new DefaultHttpClient();

			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpPost post = new HttpPost("http://localhost:8002"+ "/manage/v2/forests/"+fName);
//			
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("state", "detach"));
			urlParameters.add(new BasicNameValuePair("database", dbName));
			
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
						
				HttpResponse response = client.execute(post);
				HttpEntity respEntity = response.getEntity();

			    if (respEntity != null) {
			        // EntityUtils to get the response content
			        String content =  EntityUtils.toString(respEntity);
			        System.out.println(content);
			    }
			}catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}	
	}
	/*
	 * Deleting a forest is a HTTP Delete request
	 * 
	 */
	public static void deleteForest(String fName){
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete("http://localhost:8002/manage/v2/forests/"+fName+"?level=full");
			client.execute(delete);
			
			}catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}
	}
/*
 * Deleting Database
 * 
 */
	public static void deleteDB(String dbName){
	try{
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpDelete delete = new HttpDelete("http://localhost:8002/manage/v2/databases/"+dbName);
			client.execute(delete);
			
			}catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}
	}
	
	public static void clearDB(int port){
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", port),
					new UsernamePasswordCredentials("admin", "admin"));
			String uri = "http://localhost:"+port+"/v1/search/";
			HttpDelete delete = new HttpDelete(uri);
			client.execute(delete);
			
			}catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}
	}
	public static void waitForServerRestart()
	{
		try{
		int count = 0;
		while(count <20){
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(
				new AuthScope("localhost", 8001),
				new UsernamePasswordCredentials("admin", "admin"));
		
			count++;
			try{
		HttpGet getrequest = new HttpGet("http://localhost:8001/admin/v1/timestamp");
		HttpResponse response = client.execute(getrequest);
		if(response.getStatusLine().getStatusCode() == 503){Thread.sleep(4000);}
		else if(response.getStatusLine().getStatusCode() == 200){
				break;
		}
		else {
			System.out.println("Waiting for response from server, Trial :"+response.getStatusLine().getStatusCode()+count);
			Thread.sleep(2000);
		}
			}catch(Exception e){Thread.sleep(2000);}
		}
	}catch(Exception e){
		System.out.println("Inside wait for server restart is throwing an error");
	                e.printStackTrace();
	        }
	}
	/*
	 * This function deletes rest server first and deletes forests and databases in separate calls
	 */
	public static void tearDownJavaRESTServer(String dbName, String [] fNames, String restServerName) throws Exception{
			
		try{
			deleteRESTServer(restServerName); 
		}catch(Exception e){
			System.out.println("From Deleting Rest server called funnction is throwing an error");
			e.printStackTrace(); 
		}
		waitForServerRestart(); 
		try{
			for(int i = 0; i < fNames.length; i++){
				detachForest(dbName, fNames[i]); 
			}
		}catch(Exception e){
	                e.printStackTrace();
	        }

		try{
			for(int i = 0; i < fNames.length; i++){
				deleteForest(fNames[i]); 
			}
		}catch(Exception e){
	                e.printStackTrace();
	        }
		
			deleteDB(dbName); 
	    }
	
	/*
	 * This function deletes rest server along with default forest and database 
	 */
	public static void tearDownJavaRESTServerWithDB(String restServerName) throws Exception{
			
		try{
			deleteRESTServerWithDB(restServerName); 
		}catch(Exception e){
			e.printStackTrace(); 
		}
		Thread.sleep(6000); 
		 
	    }
	
	/*
	 * 
	 * setting up AppServices configurations 
	 * setting up database properties whose value is string
	 */
	public static void setDatabaseProperties(String dbName,String prop,String propValue ) throws IOException{
		InputStream jsonstream=null;
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://localhost:8002"+ "/manage/v2/databases/"+dbName+"/properties?format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream =response1.getEntity().getContent();
			JsonNode jnode= new ObjectMapper().readTree(jsonstream);
            if(!jnode.isNull()){       	
            	((ObjectNode)jnode).put(prop, propValue);
//            System.out.println(jnode.toString()+"\n"+ response1.getStatusLine().getStatusCode());
            HttpPut put = new HttpPut("http://localhost:8002"+ "/manage/v2/databases/"+dbName+"/properties?format=json");
    		put.addHeader("Content-type", "application/json");
    		put.setEntity(new StringEntity(jnode.toString()));
    	
    		HttpResponse response2 = client.execute(put);
    		HttpEntity respEntity = response2.getEntity();
    		if(respEntity != null){
    			String content =  EntityUtils.toString(respEntity);
    			System.out.println(content);
    		}
    		}
            else{
            	System.out.println("REST call for database properties returned NULL ");
            }
		}catch (Exception e) {
		    // writing error to Log
		    e.printStackTrace();
		}
		finally{
			if(jsonstream == null){}
			else{
				jsonstream.close();
			}
			}
		}
			
	public static void setDatabaseProperties(String dbName,String prop,boolean propValue ) throws IOException{
		InputStream jsonstream=null;
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://localhost:8002"+ "/manage/v2/databases/"+dbName+"/properties?format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream =response1.getEntity().getContent();
			JsonNode jnode= new ObjectMapper().readTree(jsonstream);
            if(!jnode.isNull()){       	
         	((ObjectNode)jnode).put(prop, propValue)   ;
//            System.out.println(jnode.toString()+"\n"+ response1.getStatusLine().getStatusCode());
            HttpPut put = new HttpPut("http://localhost:8002"+ "/manage/v2/databases/"+dbName+"/properties?format=json");
    		put.addHeader("Content-type", "application/json");
    		put.setEntity(new StringEntity(jnode.toString()));
    	
    		HttpResponse response2 = client.execute(put);
    		HttpEntity respEntity = response2.getEntity();
    		if(respEntity != null){
    			String content =  EntityUtils.toString(respEntity);
    			System.out.println(content);
    		}
    		}
            else{
            	System.out.println("REST call for database properties returned NULL ");
            }
		}catch (Exception e) {
		    // writing error to Log
		    e.printStackTrace();
		}
		finally{
			if(jsonstream == null){}
			else{
				jsonstream.close();
			}
			}
		}
	
	/*
	 * This Method takes the root property name and object node under it 
	 * if root propname exist and equals to null then it just add the object node under root property name else if it has an existing sub property name then it adds 
	 * elements to that array 
	 */
	public static void setDatabaseProperties(String dbName,String propName, ObjectNode objNode ) throws IOException{
		InputStream jsonstream=null;
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://localhost:8002"+ "/manage/v2/databases/"+dbName+"/properties?format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream =response1.getEntity().getContent();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode= mapper.readTree(jsonstream);
            if(!jnode.isNull()){
            	
            	if(!jnode.has(propName)){
            		((ObjectNode)jnode).putArray(propName).addAll(objNode.withArray(propName));
//            		 System.out.println("when Node is null"+propName + objNode.toString());
            	}
            	else{
            		if(!jnode.path(propName).isArray()){
            			System.out.println("property is not array");
            			((ObjectNode)jnode).putAll(objNode);
            			}
            		else{
            			JsonNode member = jnode.withArray(propName);
            			if(objNode.path(propName).isArray()){
            			((ArrayNode)member).addAll(objNode.withArray(propName));
   //            			System.out.println("when Node is not null"+ propName + objNode.withArray(propName).toString());
            			}
            		}
            	}
            
            HttpPut put = new HttpPut("http://localhost:8002"+ "/manage/v2/databases/"+dbName+"/properties?format=json");
    		put.addHeader("Content-type", "application/json");
    		put.setEntity(new StringEntity(jnode.toString()));
    	
    		HttpResponse response2 = client.execute(put);
    		HttpEntity respEntity = response2.getEntity();
    		if(respEntity != null){
    			String content =  EntityUtils.toString(respEntity);
    			System.out.println(content);
    		}
    		}
            else{
            	System.out.println("REST call for database properties returned NULL \n"+jnode.toString()+"\n"+ response1.getStatusLine().getStatusCode());
            }
		}catch (Exception e) {
		    // writing error to Log
		    e.printStackTrace();
		}
		finally{
			if(jsonstream == null){}
			else{
				jsonstream.close();
			}
			}
		}
	
	public static void enableCollectionLexicon(String dbName) throws Exception{
		setDatabaseProperties(dbName,"collection-lexicon",true );
	}
	/*
	 * "word-lexicons":  [
      "http:\/\/marklogic.com\/collation\/"
    ]
  }
	 */
	public static void enableWordLexicon(String dbName) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		childArray.add("http://marklogic.com/collation/");
		childNode.putArray("word-lexicon").addAll(childArray);
		setDatabaseProperties(dbName,"word-lexicons",childNode);
		
	}
	public static void enableTrailingWildcardSearches(String dbName) throws Exception{
		setDatabaseProperties(dbName,"trailing-wildcard-searches",true );
	}
	
	public static void setMaintainLastModified(String dbName,boolean opt) throws Exception{
		setDatabaseProperties(dbName,"maintain-last-modified",opt);
	}
	/*
	 * This function constructs a range element index with default collation,range-value-positions and invalid values
	 * 
	 */
	public static void addRangeElementIndex(String dbName,  String type, String namespace, String localname) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mainNode = mapper.createObjectNode();
	//	ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put( "scalar-type", type);
		childNodeObject.put( "namespace-uri", namespace);
		childNodeObject.put( "localname", localname);
		childNodeObject.put( "collation", "");
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", "reject");
		childArray.add(childNodeObject);		
		mainNode.putArray("range-element-index").addAll(childArray);
	//	mainNode.put("range-element-indexes", childNode);
//		System.out.println(type + mainNode.path("range-element-indexes").path("range-element-index").toString());
		setDatabaseProperties(dbName,"range-element-index",mainNode);
		
	}
	

	/*
	 * This is a overloaded function constructs a range element index with default range-value-positions and invalid values
	 * 
	 */
	public static void addRangeElementIndex(String dbName,  String type, String namespace, String localname, String collation) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mainNode = mapper.createObjectNode();
	//	ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put( "scalar-type", type);
		childNodeObject.put( "namespace-uri", namespace);
		childNodeObject.put( "localname", localname);
		childNodeObject.put( "collation", collation);
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", "reject");
		childArray.add(childNodeObject);
		mainNode.putArray("range-element-index").addAll(childArray);
		
//		System.out.println(type + mainNode.path("range-element-indexes").path("range-element-index").toString());
		setDatabaseProperties(dbName,"range-element-index",mainNode);
		
	}

	/*
	 * "scalar-type": "int",
        "collation": "",
        "parent-namespace-uri": "",
        "parent-localname": "test",
        "namespace-uri": "",
        "localname": "testAttr",
        "range-value-positions": false,
        "invalid-values": "reject"
	 */
	
	public static void addRangeElementAttributeIndex(String dbName, String type, String parentnamespace, String parentlocalname, String namespace, String localname, String collation) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
	//	ObjectNode mainNode = mapper.createObjectNode();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put( "scalar-type", type);
		childNodeObject.put( "collation", collation);
		childNodeObject.put( "parent-namespace-uri", parentnamespace);
		childNodeObject.put( "parent-localname", parentlocalname);
		childNodeObject.put( "namespace-uri", namespace);
		childNodeObject.put( "localname", localname);
		
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", "reject");
		childArray.add(childNodeObject);
		childNode.putArray("range-element-attribute-index").addAll(childArray);

	//	mainNode.put("range-element-attribute-indexes", childNode);
//		System.out.println(type + mainNode.path("range-element-attribute-indexes").path("range-element-attribute-index").toString());
		setDatabaseProperties(dbName,"range-element-attribute-index",childNode);
		
	}
	/*
	 * Overloaded function with default collation
	 */
	public static void addRangeElementAttributeIndex(String dbName, String type, String parentnamespace, String parentlocalname, String namespace, String localname) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		//ObjectNode mainNode = mapper.createObjectNode();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put( "scalar-type", type);
		childNodeObject.put( "collation", "");
		childNodeObject.put( "parent-namespace-uri", parentnamespace);
		childNodeObject.put( "parent-localname", parentlocalname);
		childNodeObject.put( "namespace-uri", namespace);
		childNodeObject.put( "localname", localname);
		
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", "reject");
		childArray.add(childNodeObject);
		childNode.putArray("range-element-attribute-index").addAll(childArray);
	//	mainNode.put("range-element-attribute-indexes", childNode);
//		System.out.println(type + mainNode.path("range-element-attribute-indexes").path("range-element-attribute-index").toString());
		setDatabaseProperties(dbName,"range-element-attribute-index",childNode);
		
	}
	/*
	 *  "range-path-indexes": {
    "range-path-index": [
      {
        "scalar-type": "string",
        "collation": "http:\/\/marklogic.com\/collation\/",
        "path-expression": "\/Employee\/fn",
        "range-value-positions": false,
        "invalid-values": "reject"
      }
    ]
  }
	 */
	public static void addRangePathIndex(String dbName, String type, String pathexpr, String collation, String invalidValues) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
//		ObjectNode mainNode = mapper.createObjectNode();
		ObjectNode childNode = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		childNodeObject.put( "scalar-type", type);
		childNodeObject.put( "collation", collation);
		childNodeObject.put( "path-expression", pathexpr);
		childNodeObject.put("range-value-positions", false);
		childNodeObject.put("invalid-values", invalidValues);
		childArray.add(childNodeObject);
		childNode.putArray("range-path-index").addAll(childArray);
//		mainNode.put("range-path-indexes", childNode);
//		System.out.println(type + mainNode.path("range-path-indexes").path("range-path-index").toString());
		setDatabaseProperties(dbName,"range-path-index",childNode);
		
	}
	 public static void addGeospatialElementIndexes(String dbName,String localname,String namespace,String coordinateSystem,String pointFormat,boolean rangeValuePositions,String invalidValues) throws Exception{
		 ObjectMapper mapper = new ObjectMapper();
	//		ObjectNode mainNode = mapper.createObjectNode();
			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode childArray = mapper.createArrayNode();
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put( "namespace-uri", namespace);
			childNodeObject.put( "localname", localname);
			childNodeObject.put( "coordinate-system", coordinateSystem);
			childNodeObject.put("range-value-positions", false);
			childNodeObject.put("invalid-values", invalidValues);
			childNodeObject.put("point-format",pointFormat);
			childArray.add(childNodeObject);
			childNode.putArray("geospatial-element-index").addAll(childArray);
//			mainNode.put("geospatial-element-indexes", childNode);
//			System.out.println(type + mainNode.path("range-path-indexes").path("range-path-index").toString());
			setDatabaseProperties(dbName,"geospatial-element-index",childNode);
	 }
	 public static void addGeoSpatialElementChildIndexes(String dbName,String parentNamespaceUri,String parentLocalName,String namespace,String localname,String coordinateSystem,String pointFormat,boolean rangeValuePositions,String invalidValues) throws Exception{
		 ObjectMapper mapper = new ObjectMapper();
	//		ObjectNode mainNode = mapper.createObjectNode();
			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode childArray = mapper.createArrayNode();
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put( "parent-namespace-uri", parentNamespaceUri);
			childNodeObject.put( "parent-localname", parentLocalName);
			childNodeObject.put( "namespace-uri", namespace);
			childNodeObject.put( "localname", localname);
			childNodeObject.put( "coordinate-system", coordinateSystem);
			childNodeObject.put("range-value-positions", false);
			childNodeObject.put("invalid-values", invalidValues);
			childNodeObject.put("point-format",pointFormat);
			childArray.add(childNodeObject);
			childNode.putArray("geospatial-element-child-index").addAll(childArray);
//			mainNode.put("geospatial-element-child-indexes", childNode);
//			System.out.println(type + mainNode.path("range-path-indexes").path("range-path-index").toString());
			setDatabaseProperties(dbName,"geospatial-element-child-index",childNode);
	 }
	 public static void addGeospatialElementPairIndexes(String dbName,String parentNamespaceUri,String parentLocalName,String latNamespace,String latLocalname,String longNamespace,String longLocalname,String coordinateSystem,boolean rangeValuePositions,String invalidValues) throws Exception{
		 ObjectMapper mapper = new ObjectMapper();
	//		ObjectNode mainNode = mapper.createObjectNode();
			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode childArray = mapper.createArrayNode();
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put( "parent-namespace-uri", parentNamespaceUri);
			childNodeObject.put( "parent-localname", parentLocalName);
			childNodeObject.put( "latitude-namespace-uri", latNamespace);
			childNodeObject.put( "latitude-localname", latLocalname);
			childNodeObject.put( "longitude-namespace-uri", latNamespace);
			childNodeObject.put( "longitude-localname", longLocalname);
			childNodeObject.put( "coordinate-system", coordinateSystem);
			childNodeObject.put("range-value-positions", false);
			childNodeObject.put("invalid-values", invalidValues);
			childArray.add(childNodeObject);
			childNode.putArray("geospatial-element-pair-index").addAll(childArray);
//			mainNode.put("geospatial-element-pair-indexes", childNode);
//			System.out.println(type + mainNode.path("range-path-indexes").path("range-path-index").toString());
			setDatabaseProperties(dbName,"geospatial-element-pair-index",childNode);
	 }
	 public static void addGeospatialElementAttributePairIndexes(String dbName,String parentNamespaceUri,String parentLocalName,String latNamespace,String latLocalname,String longNamespace,String longLocalname,String coordinateSystem,boolean rangeValuePositions,String invalidValues) throws Exception{
		 ObjectMapper mapper = new ObjectMapper();
//			ObjectNode mainNode = mapper.createObjectNode();
			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode childArray = mapper.createArrayNode();
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put( "parent-namespace-uri", parentNamespaceUri);
			childNodeObject.put( "parent-localname", parentLocalName);
			childNodeObject.put( "latitude-namespace-uri", latNamespace);
			childNodeObject.put( "latitude-localname", latLocalname);
			childNodeObject.put( "longitude-namespace-uri", latNamespace);
			childNodeObject.put( "longitude-localname", longLocalname);
			childNodeObject.put( "coordinate-system", coordinateSystem);
			childNodeObject.put("range-value-positions", false);
			childNodeObject.put("invalid-values", invalidValues);
			childArray.add(childNodeObject);
			childNode.putArray("geospatial-element-attribute-pair-index").addAll(childArray);
//			mainNode.put("geospatial-element-attribute-pair-indexes", childNode);
//			System.out.println(type + mainNode.path("range-path-indexes").path("range-path-index").toString());
			setDatabaseProperties(dbName,"geospatial-element-attribute-pair-index",childNode);
	 }
	 public static void addGeospatialPathIndexes(String dbName,String pathExpression,String coordinateSystem,String pointFormat,boolean rangeValuePositions,String invalidValues) throws Exception{
		 ObjectMapper mapper = new ObjectMapper();
		//	ObjectNode mainNode = mapper.createObjectNode();
			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode childArray = mapper.createArrayNode();
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put( "path-expression", pathExpression);
			childNodeObject.put( "coordinate-system", coordinateSystem);
			childNodeObject.put("range-value-positions", false);
			childNodeObject.put("invalid-values", invalidValues);
			childNodeObject.put("point-format",pointFormat);
			childArray.add(childNodeObject);
			childNode.putArray("geospatial-path-index").addAll(childArray);
	//		mainNode.put("geospatial-path-indexes", childNode);
//			System.out.println(type + mainNode.path("range-path-indexes").path("range-path-index").toString());
			setDatabaseProperties(dbName,"geospatial-path-index",childNode);
	 }
	/*
	 * Add field will include root and it appends field to an existing fields
	 * "fields":{
		"field":[
					{
					"field-name": "",
					"include-root": true,
					"included-elements": null,
					"excluded-elements": null
					}
					,
					{
					"field-name": "para",
					"include-root": false,
					"included-elements": null,
					"excluded-elements": null,
					"tokenizer-overrides": null
					}
				]
			}
	 */
	 public static void addField(String dbName, String fieldName) throws Exception{
		 	ObjectMapper mapper = new ObjectMapper();
		//	ObjectNode mainNode = mapper.createObjectNode();
			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode arrNode = mapper.createArrayNode();
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put( "field-name", fieldName);
			childNodeObject.put("field-type", "root");
			childNodeObject.put( "include-root", true);
			childNodeObject.putNull( "included-elements");
			childNodeObject.putNull( "excluded-elements");
			childNodeObject.putNull( "tokenizer-overrides");
			arrNode.add(childNodeObject);
			childNode.putArray("field").addAll(arrNode);
	//		mainNode.put("fields", childNode);
// 		   System.out.println("Entered field to make it true");
			setDatabaseProperties(dbName,"field",childNode);
		 
	 }
	 public static void addFieldExcludeRoot(String dbName, String fieldName) throws Exception{
		 	ObjectMapper mapper = new ObjectMapper();
//			ObjectNode mainNode = mapper.createObjectNode();
			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode arrNode = mapper.createArrayNode();
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put( "field-name", fieldName);
			childNodeObject.put( "include-root", false);
			childNodeObject.putNull( "included-elements");
			childNodeObject.putNull( "excluded-elements");
			childNodeObject.putNull( "tokenizer-overrides");
			arrNode.add(childNodeObject);
			childNode.putArray("field").addAll(arrNode);
//			mainNode.put("fields", childNode);
//			System.out.println( childNode.toString());
			setDatabaseProperties(dbName,"field",childNode);
		 
	 }
	 public static void   addBuiltInGeoIndex (String dbName)throws Exception {
	 addGeospatialElementIndexes(dbName,"g-elem-point","","wgs84","point",false,"reject");
	 addGeoSpatialElementChildIndexes(dbName,"","g-elem-child-parent","","g-elem-child-point","wgs84","point",false,"reject");
	 addGeospatialElementPairIndexes(dbName,"","g-elem-pair","","lat","","long","wgs84",false,"reject");
	 addGeospatialElementAttributePairIndexes(dbName,"","g-attr-pair","","lat","","long","wgs84",false,"reject");
	 addGeospatialPathIndexes(dbName,"/doc/g-elem-point","wgs84","point",false,"ignore");
	 }
	 
/*
 This method is trying to add include element or exclude elements to the existing fields
 *
 */
	 public static void setDatabaseFieldProperties(String dbName,String field_name, String propName, ObjectNode objNode ) throws IOException{
			InputStream jsonstream=null;
			try{
				DefaultHttpClient client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(
						new AuthScope("localhost", 8002),
						new UsernamePasswordCredentials("admin", "admin"));
				HttpGet getrequest = new HttpGet("http://localhost:8002"+ "/manage/v2/databases/"+dbName+"/properties?format=json");
				HttpResponse response1 = client.execute(getrequest);
				jsonstream =response1.getEntity().getContent();
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jnode= mapper.readTree(jsonstream);
	            if(!jnode.isNull()&& jnode.has("field")){
	            	JsonNode  fieldNode = jnode.withArray("field");
	            	Iterator<JsonNode> fnode = fieldNode.elements();
	            	while(fnode.hasNext()) {
	            		JsonNode fnchild =fnode.next();
	            		if((fnchild.path("field-name").asText()).equals(field_name)){
//            			System.out.println("Hurray" +fnchild.has(propName));
	            		if(!fnchild.has(propName)){
	            			((ObjectNode)fnchild).putArray(propName).addAll(objNode.withArray(propName));
	            			System.out.println("Adding child array include node" + jnode.toString());
	            		    }
	            		    else{
	            		    	JsonNode member = fnchild.withArray(propName);
	            		    	((ArrayNode)member).addAll(objNode.withArray(propName));
	            		    		}
	            		    	
	            		}
	            	}
	           	            
	            HttpPut put = new HttpPut("http://localhost:8002"+ "/manage/v2/databases/"+dbName+"/properties?format=json");
	    		put.addHeader("Content-type", "application/json");
	    		put.setEntity(new StringEntity(jnode.toString()));
	    	
	    		HttpResponse response2 = client.execute(put);
	    		HttpEntity respEntity = response2.getEntity();
	    		if(respEntity != null){
	    			String content =  EntityUtils.toString(respEntity);
	    			System.out.println(content);
	    		}
	    		}
	            else{
	            	System.out.println("REST call for database properties returned NULL \n"+jnode.toString()+"\n"+ response1.getStatusLine().getStatusCode());
	            }
			}catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}
			finally{
				if(jsonstream == null){}
				else{
					jsonstream.close();
				}
				}
			}

	 public static void includeElementField(String dbName, String field_name, String namespace, String elementName) throws Exception{
		 ObjectMapper mapper = new ObjectMapper();
		//	ObjectNode mainNode = mapper.createObjectNode();
			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode arrNode = mapper.createArrayNode();
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put( "namespace-uri", namespace);
			childNodeObject.put( "localname", elementName);
			childNodeObject.put("weight", 1.0);
			arrNode.add(childNodeObject);
			childNode.putArray("included-element").addAll(arrNode);
		//	mainNode.put("included-elements", childNode);
			System.out.println( childNode.toString());
			setDatabaseFieldProperties(dbName,field_name,"included-element",childNode);
		  
	 }
	 public static void includeElementFieldWithWeight(String dbName, String field_name, String namespace, String elementName, double weight) throws Exception{
		 
		 	ObjectMapper mapper = new ObjectMapper();
//			ObjectNode mainNode = mapper.createObjectNode();
			ObjectNode childNode = mapper.createObjectNode();
			ArrayNode arrNode = mapper.createArrayNode();
			ObjectNode childNodeObject = mapper.createObjectNode();
			childNodeObject.put( "namespace-uri", namespace);
			childNodeObject.put( "localname", elementName);
			childNodeObject.put("weight", weight);
			arrNode.add(childNodeObject);
			childNode.putArray("included-element").addAll(arrNode);
//			mainNode.put("included-elements", childNode);
//			System.out.println( childNode.toString());
			setDatabaseFieldProperties(dbName,field_name,"included-element",childNode);
		  
	 }
	 public static void setupAppServicesConstraint(String dbName) throws Exception {
		enableCollectionLexicon(dbName);
        enableWordLexicon(dbName);
        addRangeElementIndex(dbName, "date", "http://purl.org/dc/elements/1.1/", "date");
        addRangeElementIndex(dbName, "int", "", "popularity");
        addRangeElementIndex(dbName, "int", "http://test.tups.com", "rate");
        addRangeElementIndex(dbName, "decimal", "http://test.aggr.com", "score");
        addRangeElementIndex(dbName, "string", "", "title", "http://marklogic.com/collation/");
        addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
        enableTrailingWildcardSearches(dbName);
        addFieldExcludeRoot(dbName, "para");
        includeElementFieldWithWeight(dbName, "para", "", "p", 5);
        addRangePathIndex(dbName, "string", "/Employee/fn", "http://marklogic.com/collation/", "ignore");
        addRangePathIndex(dbName, "int", "/root/popularity", "", "ignore");
        addRangePathIndex(dbName, "decimal", "//@amt", "", "ignore");
	    }
	 public static void setupAppServicesGeoConstraint(String dbName) throws Exception {
		 	enableCollectionLexicon(dbName);
	        addRangeElementIndex(dbName, "dateTime", "", "bday", "http://marklogic.com/collation/");
	        addRangeElementIndex(dbName, "int", "", "height1", "http://marklogic.com/collation/");
	        addRangeElementIndex(dbName, "int", "", "height2", "http://marklogic.com/collation/");
	        addRangePathIndex(dbName, "string", "/doc/name", "http://marklogic.com/collation/", "ignore");
	        addField(dbName, "description");
	        includeElementField(dbName, "description", "", "description");
	        addBuiltInGeoIndex(dbName);
	        
	    }
	 public static void loadBug18993(){
		 try{
				DefaultHttpClient client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(
						new AuthScope("localhost", 8011),
						new UsernamePasswordCredentials("admin", "admin"));
				String document ="<foo>a space b</foo>";
				String  perm = "perm:rest-writer=read&perm:rest-writer=insert&perm:rest-writer=update&perm:rest-writer=execute";
	            HttpPut put = new HttpPut("http://localhost:8011/v1/documents?uri=/a%20b&"+perm);
	    		put.addHeader("Content-type", "application/xml");
	    		put.setEntity(new StringEntity(document)); 	
	    		HttpResponse response = client.execute(put);
	    		HttpEntity respEntity = response.getEntity();
	    		if(respEntity != null){
	    			String content =  EntityUtils.toString(respEntity);
	    			System.out.println(content);
	    		}
	    	 }catch (Exception e) {
			    // writing error to Log
			    e.printStackTrace();
			}
			
	 }

	 public static void setAuthentication(String level){
		 
	 }
	 public static void setDefaultUser(String usr){
		 
	 }
}