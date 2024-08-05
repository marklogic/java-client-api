/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class TestPOJOwithQBEQueryDef extends AbstractFunctionalTest {

  @BeforeEach
  public void setUp() throws Exception {
    client = getDatabaseClient("rest-admin", "x", getConnType());
  }

  public Artifact getArtifact(int counter) {

    Artifact cogs = new Artifact();
    cogs.setId(counter);
    if (counter % 5 == 0) {
      cogs.setName("Cogs special");
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme special, Inc.");
        acme.setWebsite("http://www.acme special.com");
        acme.setLatitude(41.998 + counter);
        acme.setLongitude(-87.966 + counter);
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets counter Inc.");
        widgets.setWebsite("http://www.widgets counter.com");
        widgets.setLatitude(41.998 + counter);
        widgets.setLongitude(-87.966 + counter);
        cogs.setManufacturer(widgets);
      }
    } else {
      cogs.setName("Cogs " + counter);
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme " + counter + ", Inc.");
        acme.setWebsite("http://www.acme" + counter + ".com");
        acme.setLatitude(41.998 + counter);
        acme.setLongitude(-87.966 + counter);
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets " + counter + ", Inc.");
        widgets.setWebsite("http://www.widgets" + counter + ".com");
        widgets.setLatitude(41.998 + counter);
        widgets.setLongitude(-87.966 + counter);
        cogs.setManufacturer(widgets);
      }
    }
    cogs.setInventory(1000 + counter);
    return cogs;
  }

  public void validateArtifact(Artifact art)
  {
    assertNotNull( art);
    assertNotNull( art.id);
    assertTrue( art.getInventory() > 1000);
  }

  public void loadSimplePojos(PojoRepository products)
  {
    for (int i = 1; i < 111; i++) {
      if (i % 2 == 0) {
        products.write(this.getArtifact(i), "even", "numbers");
      }
      else {
        products.write(this.getArtifact(i), "odd", "numbers");
      }
    }
  }

	@Test
  public void testPOJOqbeSearchWithoutSearchHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    QueryManager queryMgr = client.newQueryManager();
    String queryAsString =
        "{\"$query\":{"
            + "\"$and\":[{\"name\":{\"$word\":\"cogs\",\"$exact\": false}}]"
            + ",\"$not\":[{\"name\":{\"$word\":\"special\",\"$exact\": false}}]"
            + "}}";

		assertThrows(ClassCastException.class, () -> {
				PojoQueryDefinition queryDef = (PojoQueryDefinition)
					queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));
			}
		);
  }

	@Test
  public void testPOJOqbeSearchWithSearchHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    QueryManager queryMgr = client.newQueryManager();
    String queryAsString =
        "{\"$query\":{"
            + "\"$and\":[{\"inventory\":{\"$gt\":1010}},{\"inventory\":{\"$le\":1110}}]"
            + ",\"$filtered\": true}}";
    System.out.println(queryAsString);
	assertThrows(ClassCastException.class, () -> {
		PojoQueryDefinition qd = (PojoQueryDefinition) queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));
	});
  }

	@Test
  public void testPOJOCombinedSearchWithJacksonHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    QueryManager queryMgr = client.newQueryManager();
    String queryAsString = "{\"search\":{\"query\":{\"and-query\":["
        + "{\"word-constraint-query\":{\"constraint-name\":\"pojo-name-field\", \"text\":\"Acme\"}},"
        + "{\"word-constraint-query\":{\"constraint-name\":\"pojo-name-field\", \"text\":\"special\"}}]},"
        + "\"options\":{\"constraint\":{\"name\":\"pojo-name-field\", \"word\":{\"json-property\":\"name\"}}}"
        + "}}";

	assertThrows(ClassCastException.class, () -> {
		PojoQueryDefinition qd = (PojoQueryDefinition) queryMgr.newRawCombinedQueryDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));
	});
  }

  // Searching for Id as Number in JSON using range query
	@Test
  public void testPOJOcombinedSearchforNumberWithStringHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    QueryManager queryMgr = client.newQueryManager();
    String queryAsString = "{\"search\":{\"query\":{"
        + "\"range-constraint-query\":{\"constraint-name\":\"id\", \"value\":[5,10,15,20,25,30]}},"
        + "\"options\":{\"return-metrics\":false, \"constraint\":{\"name\":\"id\", \"range\":{\"type\": \"xs:long\",\"json-property\":\"id\"}}}"
        + "}}";
	assertThrows(ClassCastException.class, () -> {
		PojoQueryDefinition qd = (PojoQueryDefinition) queryMgr.newRawCombinedQueryDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));
	});
  }

}
