/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.functionaltest.GeoCompany;
import com.marklogic.client.functionaltest.GeoSpecialArtifact;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPOJOQueryBuilderGeoQueries extends AbstractFunctionalTest {

  @BeforeEach
  public void setUp() throws Exception {
    client = getDatabaseClient("rest-admin", "x", getConnType());
  }

  public GeoSpecialArtifact getGeoArtifact(int counter) {

    GeoSpecialArtifact cogs = new GeoSpecialArtifact();
    cogs.setId(counter);
    if (counter % 5 == 0) {
      cogs.setName("Cogs special");
      if (counter % 2 == 0) {
        GeoCompany acme = new GeoCompany();
        acme.setName("Acme special, Inc.");
        acme.setState("Reno");
        acme.setLatitude(39.5272);
        acme.setLongitude(119.8219);
        acme.setLatLongPoint("39.5272 119.8219");
        cogs.setManufacturer(acme);

      } else {
        GeoCompany widgets = new GeoCompany();
        widgets.setName("Widgets counter Inc.");
        widgets.setState("Las Vegas");
        widgets.setLatitude(36.1215);
        widgets.setLongitude(115.1739);
        widgets.setLatLongPoint("36.1215 115.1739");
        cogs.setManufacturer(widgets);
      }
    } else {
      cogs.setName("Cogs " + counter);
      if (counter % 2 == 0) {
        GeoCompany acme = new GeoCompany();
        acme.setName("Acme " + counter + ", Inc.");
        acme.setState("Los Angles");
        acme.setLatitude(34.0500);
        acme.setLongitude(118.2500);
        acme.setLatLongPoint("34.0500 118.2500");
        cogs.setManufacturer(acme);

      } else {
        GeoCompany widgets = new GeoCompany();
        widgets.setName("Widgets " + counter + ", Inc.");
        widgets.setState("San Fransisco");
        widgets.setLatitude(37.7833);
        widgets.setLongitude(122.4167);
        widgets.setLatLongPoint("37.7833 122.4167");
        cogs.setManufacturer(widgets);
      }
    }
    cogs.setInventory(1000 + counter);
    return cogs;
  }

  public void validateArtifact(GeoSpecialArtifact art)
  {
    assertNotNull( art);
    assertNotNull( art.id);
    assertTrue( art.getInventory() > 1000);
  }

  public void loadSimplePojos(PojoRepository products)
  {
    for (int i = 1; i < 111; i++) {
      if (i % 2 == 0) {
        products.write(this.getGeoArtifact(i), "even", "numbers");
      }
      else {
        products.write(this.getGeoArtifact(i), "odd", "numbers");
      }
    }
  }

  // Below scenario is to test the geoPair -130

  // This test is to verify GeoPair query works fine,
  // searching for lattitud and longitude of Reno
  @Test
  public void testPOJOGeoQuerySearchWithGeoPair() {
    PojoRepository<GeoSpecialArtifact, Long> products = client.newPojoRepository(GeoSpecialArtifact.class, Long.class);
    PojoPage<GeoSpecialArtifact> p;
    this.loadSimplePojos(products);

    PojoQueryBuilder qb = products.getQueryBuilder();
    PojoQueryBuilder containerQb = qb.containerQueryBuilder("manufacturer", GeoCompany.class);
    PojoQueryDefinition qd = containerQb.geospatial(containerQb.geoPair("latitude", "longitude"), containerQb.circle(39.5272, 119.8219, 1));

    JacksonHandle jh = new JacksonHandle();
    products.setPageLength(5);
    p = products.search(qd, 1, jh);
    System.out.println(jh.get().toString());
    assertEquals( 3, p.getTotalPages());
    System.out.println(jh.get().toString());

    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo);
      while (p.hasNext()) {
        GeoSpecialArtifact a = p.next();
        validateArtifact(a);
        assertTrue( a.getId() % 5 == 0);
        assertEquals( "Reno", a.getManufacturer().getState());
        count++;
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    assertEquals( 3, p.getPageNumber());
    assertEquals( 3, p.getTotalPages());
    assertEquals( 5, jh.get().path("page-length").asInt());
    assertEquals( 11, jh.get().path("total").asInt());

  }

  // This test is to verify GeoProperty query works fine
  @Test
  public void testPOJOGeoQuerySearchWithGeoProperty() {
    PojoRepository<GeoSpecialArtifact, Long> products = client.newPojoRepository(GeoSpecialArtifact.class, Long.class);
    PojoPage<GeoSpecialArtifact> p;
    this.loadSimplePojos(products);

    PojoQueryBuilder qb = products.getQueryBuilder();
    PojoQueryBuilder containerQb = qb.containerQueryBuilder("manufacturer", GeoCompany.class);
    PojoQueryDefinition qd = containerQb.filteredQuery(containerQb.geospatial(containerQb.geoPath("latlongPoint"), containerQb.circle(36.1215, 115.1739, 1)));

    JacksonHandle jh = new JacksonHandle();
    products.setPageLength(5);
    p = products.search(qd, 1, jh);
    System.out.println(jh.get().toString());

    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo);
      while (p.hasNext()) {
        GeoSpecialArtifact a = p.next();
        validateArtifact(a);
        assertTrue( a.getId() % 5 == 0);
        assertEquals( "Las Vegas", a.getManufacturer().getState());
        count++;
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    assertEquals( 5, jh.get().path("results").size());

  }

  // This test is to verify GeoPath query works fine,
  // searching for lattitud and longitude of Reno
  @Test
  public void testPOJOGeoQuerySearchWithGeoPath() {
    PojoRepository<GeoSpecialArtifact, Long> products = client.newPojoRepository(GeoSpecialArtifact.class, Long.class);
    PojoPage<GeoSpecialArtifact> p;
    this.loadSimplePojos(products);

    PojoQueryBuilder qb = products.getQueryBuilder();
    PojoQueryBuilder containerQb = qb.containerQueryBuilder("manufacturer", GeoCompany.class);
    PojoQueryDefinition qd = containerQb.geospatial(containerQb.geoPath("latlongPoint"), containerQb.circle(34.0500, 118.2500, 1));

    JacksonHandle jh = new JacksonHandle();
    products.setPageLength(15);
    p = products.search(qd, 1, jh);
    System.out.println(jh.get().toString());
    assertEquals( 3, p.getTotalPages());
    System.out.println(jh.get().toString());

    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo);
      while (p.hasNext()) {
        GeoSpecialArtifact a = p.next();
        validateArtifact(a);
        assertTrue( a.getId() % 2 == 0);
        assertEquals( "Los Angles", a.getManufacturer().getState());
        count++;
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    assertEquals( 3, p.getPageNumber());
    assertEquals( 3, p.getTotalPages());
    assertEquals( 15, jh.get().path("page-length").asInt());
    assertEquals( 44, jh.get().path("total").asInt());

  }
}
