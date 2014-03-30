package com.marklogic.client.test.example.cookbook;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.example.cookbook.SearchResponseTransform;

public class SearchResponseTransformTest {
	@Test
	public void testMain()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
		boolean succeeded = false;
		try {
			SearchResponseTransform.main(new String[0]);
			succeeded = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("SearchResponseTransform example failed", succeeded);
	}
	
}
