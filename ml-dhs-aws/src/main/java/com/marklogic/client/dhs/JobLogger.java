package com.marklogic.client.dhs;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface JobLogger {
	void status(ObjectNode msg);
	void error(String msg);
	void error(String msg, Throwable err);

}
