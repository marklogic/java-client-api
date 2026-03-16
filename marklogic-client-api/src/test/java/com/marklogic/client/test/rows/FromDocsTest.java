/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.rows;

import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML12;
import com.marklogic.client.type.PlanColumnBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the new fromDocs ModifyPlan method that dynamically maps semi-structured 
 * data (JSON/XML) into rows and columns without deploying a TDE template.
 */
@ExtendWith(RequiresML12.class)
public class FromDocsTest extends AbstractClientTest {

	protected RowManager rowManager;
	protected PlanBuilder op;

	@BeforeEach
	public void setup() {
		Common.client = Common.newClientBuilder().withUsername("rest-reader").build();
		rowManager = Common.client.newRowManager();
		op = rowManager.newPlanBuilder();
	}

	@Test
	public void fromDocsBasic() {
		PlanColumnBuilder columnSpec = op.columnBuilder()
			.addColumn("lastName").xpath("./lastName").type("string")
			.addColumn("firstName").xpath("./firstName").type("string")
			.addColumn("dob").xpath("./dob").type("string")
			.addColumn("instrument").xpath("./instrument").type("string");

		PlanBuilder.AccessPlan plan = op.fromDocs(
			op.cts.wordQuery("Coltrane"),
			"/musician",
			columnSpec,
			"MusicianView"
		);

		List<RowRecord> rows = resultRows(plan);
		assertEquals(1, rows.size());

		RowRecord row = rows.get(0);
		assertEquals("Coltrane", row.getString("MusicianView.lastName"));
		assertEquals("John", row.getString("MusicianView.firstName"));
		assertEquals("1926-09-23", row.getString("MusicianView.dob"));
		assertEquals("saxophone", row.getString("MusicianView.instrument"));
	}

	@Test
	public void fromDocsWithDefault() {
		PlanColumnBuilder columnSpec = op.columnBuilder()
			.addColumn("lastName").xpath("./lastName").type("string")
				.collation("http://marklogic.com/collation/")
			.addColumn("firstName").xpath("./firstName").type("string")
				.collation("http://marklogic.com/collation/")
			.addColumn("birthDate").xpath("./birthDate").type("string")
				.defaultValue("Unknown")
			.addColumn("instrument").xpath("./instrument").type("string")
			.addColumn("genre").xpath("./genre").type("string")
				.defaultValue("Jazz");

		PlanBuilder.AccessPlan plan = op.fromDocs(
			op.cts.wordQuery("Coltrane"),
			"/musician",
			columnSpec,
			"MusicianView"
		);

		List<RowRecord> rows = resultRows(plan);
		assertEquals(1, rows.size());

		RowRecord row = rows.get(0);
		assertEquals("Coltrane", row.getString("MusicianView.lastName"));
		assertEquals("John", row.getString("MusicianView.firstName"));
		assertEquals("Unknown", row.getString("MusicianView.birthDate"));
		assertEquals("saxophone", row.getString("MusicianView.instrument"));
		assertEquals("Jazz", row.getString("MusicianView.genre"));
	}

	@Test
	public void fromDocsWithContextAndXpath() {
		PlanColumnBuilder columnSpec = op.columnBuilder()
			.addColumn("name").xpath("./name").type("string")
			.addColumn("quantity").xpath("./quantity").type("integer")
			.addColumn("price").xpath("./price").type("decimal")
			.addColumn("totalCost")
				.nullable(true)
				.expr(
					op.multiply(
						op.xs.decimal(op.xpath(op.context(), op.xs.string("./price"))),
						op.xs.integer(op.xpath(op.context(), op.xs.string("./quantity")))
					)
				)
				.type("decimal");

		PlanBuilder.ModifyPlan plan = op.fromDocs(
			op.cts.wordQuery("Widget"),
			"/product",
			columnSpec,
			"ProductView"
		).orderBy(op.viewCol("ProductView", "name"));

		List<RowRecord> rows = resultRows(plan);
		assertEquals(2, rows.size());

		RowRecord firstRow = rows.get(0);
		assertEquals("Widget Alpha", firstRow.getString("ProductView.name"));
		assertEquals(25.50, firstRow.getDouble("ProductView.price"), 0.01);
		assertEquals(100, firstRow.getInt("ProductView.quantity"));
		assertEquals(2550, firstRow.getDouble("ProductView.totalCost"), 0.01);

		RowRecord secondRow = rows.get(1);
		assertEquals("Widget Beta", secondRow.getString("ProductView.name"));
		assertEquals(42.99, secondRow.getDouble("ProductView.price"), 0.01);
		assertEquals(250, secondRow.getInt("ProductView.quantity"));
		assertEquals(10747.5, secondRow.getDouble("ProductView.totalCost"), 0.01);
	}

	@Test
	public void fromDocsWithGeospatialQuery() {
		PlanColumnBuilder columnSpec = op.columnBuilder()
			.addColumn("city").xpath("./city").type("string")
			.addColumn("location-wgs84").xpath("./latLong").type("point")
				.coordinateSystem("wgs84")
			.addColumn("description").xpath("./description").type("string");

		// find cities within 650 miles of Portland, OR (45.52, -122.68)
		// use existing geospatial element index on latLong property, which is defined as a point with wgs84 coordinate system
		PlanBuilder.ModifyPlan plan = op.fromDocs(
			op.cts.collectionQuery("/optic/locations"),
			"/location",
			columnSpec,
			"LocationView"
		).where(
			op.cts.jsonPropertyGeospatialQuery(
				"latLong",
				op.cts.circle(650, op.cts.point(45.52, -122.68)),
				"coordinate-system=wgs84"
			)
		);

		List<RowRecord> rows = resultRows(plan);

		assertEquals(3, rows.size());

		Set<String> cities = rows.stream()
			.map(row -> row.getString("LocationView.city"))
			.collect(Collectors.toSet());

		assertTrue(cities.contains("Portland"));
		assertTrue(cities.contains("Seattle"));
		assertTrue(cities.contains("San Francisco"));
		assertFalse(cities.contains("New York"));
	}

	@Test
	public void fromDocsWithVectorAndDimension() {
		PlanColumnBuilder columnSpec = op.columnBuilder()
			.addColumn("name").xpath("./name").type("string")
			.addColumn("summary").xpath("./summary").type("string")
			.addColumn("embedding").xpath("vec:vector(./embedding)")
				.type("vector")
				.dimension(3)
			.addColumn("cosineDistance")
				.nullable(true)
				.expr(
					op.vec.cosineDistance(
						op.vec.vector(op.xs.doubleSeq(1, 2, 3)),
						op.vec.vector(op.xpath(op.context(), op.xs.string("./embedding")))
					)
				)
				.type("double")
			.addColumn("cosine")
				.nullable(true)
				.expr(
					op.vec.cosine(
						op.vec.vector(op.xs.doubleSeq(1, 2, 3)),
						op.vec.vector(op.xpath(op.context(), op.xs.string("./embedding")))
					)
				)
				.type("double")
			.addColumn("euclideanDistance")
				.nullable(true)
				.expr(
					op.math.trunc(
						op.vec.euclideanDistance(
							op.vec.vector(op.xs.doubleSeq(1, 2, 3)),
							op.vec.vector(op.xpath(op.context(), op.xs.string("./embedding")))
						),
						4
					)
				)
				.type("double");

		PlanBuilder.ModifyPlan plan = op.fromDocs(
			op.cts.wordQuery("*"),
			"/person",
			columnSpec,
			"PersonView"
		).where(
			op.lt(
				op.vec.cosineDistance(
					op.vec.vector(op.xs.doubleSeq(1, 2, 3)),
					op.viewCol("PersonView", "embedding")
				),
				op.xs.doubleVal(0.1)
			)
		).orderBy(op.viewCol("PersonView", "euclideanDistance"));

		List<RowRecord> rows = resultRows(plan);
		assertEquals(1, rows.size());

		// Alice should return.
		assertEquals(0.3741, rows.get(0).getDouble("PersonView.euclideanDistance"), 0.0001);
		assertEquals(1, rows.get(0).getDouble("PersonView.cosine"), 0.0001);
		assertEquals(0, rows.get(0).getDouble("PersonView.cosineDistance"), 0.0001);

		List<String> names = rows.stream()
			.map(row -> row.getString("PersonView.name"))
			.toList();

		assertTrue(names.contains("Alice"));
		assertFalse(names.contains("Bob"));
	}

	/**
	 * Convenience method for executing a plan and getting the rows back as a list.
	 */
	protected final List<RowRecord> resultRows(Plan plan) {
		return rowManager.resultRows(plan).stream().toList();
	}
}
