const op = require("/MarkLogic/optic");

op.fromView("opticUnitTest", "musician")

	// Remove this select to see the error occur due to two "rowid" columns.
	.select(["firstName", "lastName"])

	.joinInner(
		op.fromView("opticUnitTest", "musician2"),
		op.on(op.col("firstName"), op.col("firstName2"))
	)
	.generateView("qbv", "musicians2")


// ML 11
// SQL-AMBCOLUMN: result = plan.generateView(plandef, schemaName, viewName); -- Ambiguous column reference: found opticUnitTest.musician.rowid and opticUnitTest.musician2.rowid

// ML 12 - 12.1.20251217
// SQL-AMBCOLUMN: result = plan.generateView(plandef, schemaName, viewName); -- Ambiguous column reference: found opticUnitTest.musician.rowid and opticUnitTest.musician2.rowid
