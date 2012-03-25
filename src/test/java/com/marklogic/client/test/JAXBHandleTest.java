package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.test.util.Referred;
import com.marklogic.client.test.util.Refers;

public class JAXBHandleTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testReadWriteJAXB() throws JAXBException {
		String uri = "/test/jaxbWrite1.xml";

		HashMap<String,Integer> map = new HashMap<String,Integer>();
		map.put("alpha", 1);
		map.put("beta",  2);
		map.put("gamma", 3);
		List<String> list = new ArrayList<String>(3);
		list.add("apple");
		list.add("banana");
		list.add("cactus");
		Refers refers = new Refers();
		refers.child = new Referred();
		refers.map   = map;
		refers.list  = list;

		DocumentIdentifier docId = Common.client.newDocId(uri);

		JAXBContext context      = JAXBContext.newInstance(Refers.class);
		JAXBHandle  objectHandle = new JAXBHandle(context);

		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		docMgr.write(docId, objectHandle.with(refers));

		Refers refers2 = (Refers) docMgr.read(docId, objectHandle).get();
		assertTrue("Failed to read JAXB root", refers2 != null);
		assertEquals("JAXB document with different root", refers.name, refers2.name);
		assertTrue("Failed to read JAXB child", refers2.child != null);
		assertEquals("JAXB document with different child", refers.child.name, refers2.child.name);
		assertTrue("Failed to read JAXB map", refers2.map != null);
		assertEquals("JAXB document with different map", refers.map.size(), refers2.map.size());
		assertTrue("Failed to read JAXB list", refers2.list != null);
		assertEquals("JAXB document with different list", refers.list.size(), refers2.list.size());
	}
}
