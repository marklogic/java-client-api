package com.marklogic.client.functionaltest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.eval.EvalResult.Type;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
/*
 * This test is intended for 
 * looping eval query for more than 100 times
 * Eval with transactions
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestEvalwithRunTimeDBnTransactions extends BasicJavaClientREST {
	private static String dbName = "TestEvalXqueryWithTransDB";
	private static String [] fNames = {"TestEvalXqueryWithTransDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 System.out.println("In setup");
		 setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
 	     createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke");
 	     createRESTUser("eval-user", "x", "test-eval","rest-admin","rest-writer","rest-reader");
//		 System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		deleteRESTUser("eval-user");
		deleteUserRole("test-eval");
	}

	@Before
	public void setUp() throws Exception {
		client = DatabaseClientFactory.newClient("localhost", restPort,"eval-user", "x", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		client.release();
	}
// loop the eval query more than 150 times and should not stuck
	@Test
	public void test1MultipleEvalQueries() throws Exception {
	
		GenericDocumentManager docMgr = client.newDocumentManager();
		File file1= null;
		FileInputStream fis=null;
		try{
		file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/Sega-4MB.jpg");
		fis = new FileInputStream(file1);
		InputStreamHandle handle1 = new InputStreamHandle(fis);
		handle1.setFormat(Format.BINARY);
	    docMgr.write("/binary4mbdoc",handle1);
	    String query = "declare variable $myInteger as xs:integer external;"
	    		+ "(fn:doc()/binary(),$myInteger,xdmp:database-name(xdmp:database()))";
	   long sizeOfBinary =docMgr.read("/binary4mbdoc",new InputStreamHandle()).getByteLength();
	    for(int i=0 ; i<=330;i++){
	    	ServerEvaluationCall evl= client.newServerEval().xquery(query);
	    	evl.addVariable("myInteger", (int)i);
	    	EvalResultIterator evr = evl.eval();
	       while(evr.hasNext()){
	    	   EvalResult er =evr.next();
				 if(er.getType().equals(Type.INTEGER)){
					 assertEquals("itration number",i,er.getNumber().intValue());
				 }else if(er.getType().equals(Type.BINARY)){
					 FileHandle readHandle1 = new FileHandle();
					 assertEquals("size of the binary ",er.get(readHandle1).get().length(),sizeOfBinary);
					 
				 }else if(er.getType().equals(Type.STRING)){
					 assertEquals("database name ","TestEvalXqueryWithTransDB",er.getString());
				 }else{
					 fail("Getting incorrect type");
				 }
				 
	       }
	    }
		}catch(Exception e){throw e;}
		finally{
		fis.close();	
		}
	}
	//issue 170 are blocking the test progress in here
	@Test
	public void test2XqueryEvalTransactions() throws Exception{
		int count=1;
		boolean tstatus =true;
		Transaction t1 = client.openTransaction();
		try{ 
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		HashMap<String,String> map= new HashMap<String,String>();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		for(int i =0;i<102;i++){
			writeset.add("/sec"+i+".xml", new DOMHandle(getDocumentContent("This is so sec"+i)));
			map.put("/sec"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so sec"+i)));
			if(count%100 == 0){
				docMgr.write(writeset,t1);
				writeset = docMgr.newWriteSet();
			}
			count++;
		}
		if(count%100 > 0){
			docMgr.write(writeset,t1);
		}
		 String query = "declare variable $myInteger as xs:integer external;"
		    		+ "(fn:count(fn:doc()))";
		 ServerEvaluationCall evl= client.newServerEval().xquery(query);
	     EvalResultIterator evr = evl.eval();
	     assertEquals("Count of documents outside of the transaction",1,evr.next().getNumber().intValue());
	     evl= client.newServerEval().xquery(query).transaction(t1);
	     evr = evl.eval();
	     assertEquals("Count of documents outside of the transaction",103,evr.next().getNumber().intValue());
		}catch(Exception e){
			System.out.println(e.getMessage());
			tstatus=true;
			throw e;
		}finally{
			if(tstatus){
				t1.rollback();
			}
		}
}
	//issue 171 are blocking the test progress in here
		@Test
		public void test3XqueryEvalTransactionsWithRunTimeDB() throws Exception{
			int count=1;
			boolean tstatus =true;
			associateRESTServerWithDB(restServerName,"Documents");
			DatabaseClient client2 =DatabaseClientFactory.newClient("localhost", restPort,dbName,"eval-user", "x", Authentication.DIGEST);
			Transaction t1 = client2.openTransaction();
			try{ 
			XMLDocumentManager docMgr = client2.newXMLDocumentManager();
			HashMap<String,String> map= new HashMap<String,String>();
			DocumentWriteSet writeset =docMgr.newWriteSet();
			for(int i =0;i<102;i++){
				writeset.add("/sec"+i+".xml", new DOMHandle(getDocumentContent("This is so sec"+i)));
				map.put("/sec"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so sec"+i)));
				if(count%100 == 0){
					docMgr.write(writeset,t1);
					writeset = docMgr.newWriteSet();
				}
				count++;
			}
			if(count%100 > 0){
				docMgr.write(writeset,t1);
			}
			 String query = "declare variable $myInteger as xs:integer external;"
			    		+ "(fn:count(fn:doc()))";
			 ServerEvaluationCall evl= client2.newServerEval().xquery(query);
		     EvalResultIterator evr = evl.eval();
		     assertEquals("Count of documents outside of the transaction",1,evr.next().getNumber().intValue());
		     evl= client2.newServerEval().xquery(query).transaction(t1);
		     evr = evl.eval();
		     assertEquals("Count of documents outside of the transaction",103,evr.next().getNumber().intValue());
			}catch(Exception e){
				System.out.println(e.getMessage());
				tstatus=true;
				throw e;
			}finally{
				if(tstatus){
					t1.rollback();
					client2.release();
				}
			}
	}
}
