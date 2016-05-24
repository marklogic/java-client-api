/*
 * Copyright 2014-2016 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marklogic.client.functionaltest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.StringHandle;

public class ThreadClass extends BasicJavaClientREST implements Runnable {

	String msg;

	public void run() {	
		DatabaseClient client;
		try {
			client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

			TextDocumentManager docMgr = client.newTextDocumentManager();

			for(int i=1; i<=5; i++) {
				System.out.println("Writing document from: "+ msg);

				if(msg == "Thread A") {	
					// write docs
					String docId = "/multithread-content-A/filename" + i + ".txt";
					docMgr.write(docId, new StringHandle().with("This is so foo"));
				}
				else if(msg == "Thread B") {	
					// write docs
					String docId = "/multithread-content-B/filename" + i + ".txt";
					docMgr.write(docId, new StringHandle().with("This is so foo"));
				}
			}

			// release client
			client.release();
		} catch (KeyManagementException | NoSuchAlgorithmException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	ThreadClass(String mg) {
		msg=mg;
	}
}
