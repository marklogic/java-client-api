const op = require("/MarkLogic/optic");
op.fromView("opticUnitTest", "musician")
	.generateView("qbv", "musicians");
