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

import java.io.File;
import java.util.Random;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;

public class ThreadWrite extends Thread{

	String msg;

	public void run()
	{	
		String filename = "flipper.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);

		File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		for(int i=1; i<=15; i++)
		{
			System.out.println("Writing document " + i + " from: " + msg);

			// write docs
			String docId = "/multithread-write/filename" + i + ".xml";
			docMgr.write(docId, new FileHandle().with(file));

			Random rand = new Random();
			int r = rand.nextInt(2000) + 1000;

			try {
				Thread.sleep(r);
			} 
			catch (InterruptedException e) { e.printStackTrace(); }
		}

		// release client
		client.release();
	}

	ThreadWrite(String mg)
	{
		msg=mg;
	}
}
