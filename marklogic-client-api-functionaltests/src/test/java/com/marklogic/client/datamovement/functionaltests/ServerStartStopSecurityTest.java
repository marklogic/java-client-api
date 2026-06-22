/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.functionaltests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerStartStopSecurityTest {

	@AfterEach
	void resetProcessStarters() {
		WBFailover.resetProcessStarterForTest();
		QBFailover.resetProcessStarterForTest();
	}

	@Test
	void wbServerStartStopRejectsSuspiciousHostnameBeforeProcessStart() {
		AtomicInteger processStartCount = new AtomicInteger(0);
		WBFailover.setProcessStarterForTest((server, commandToRun) -> {
			processStartCount.incrementAndGet();
			throw new AssertionError("Process creation should not be attempted for invalid hostname");
		});

		assertThrows(IllegalArgumentException.class,
				() -> invokeServerStartStop(new WBFailover(), "host; echo pwned", "start"));
		assertEquals(0, processStartCount.get());
	}

	@Test
	void wbServerStartStopRejectsInvalidCommandBeforeProcessStart() {
		AtomicInteger processStartCount = new AtomicInteger(0);
		WBFailover.setProcessStarterForTest((server, commandToRun) -> {
			processStartCount.incrementAndGet();
			throw new AssertionError("Process creation should not be attempted for invalid command");
		});

		assertThrows(IllegalArgumentException.class,
				() -> invokeServerStartStop(new WBFailover(), "host", "reboot"));
		assertEquals(0, processStartCount.get());
	}

	@Test
	void qbServerStartStopRejectsSuspiciousHostnameBeforeProcessStart() {
		AtomicInteger processStartCount = new AtomicInteger(0);
		QBFailover.setProcessStarterForTest((server, commandToRun) -> {
			processStartCount.incrementAndGet();
			throw new AssertionError("Process creation should not be attempted for invalid hostname");
		});

		assertThrows(IllegalArgumentException.class,
				() -> invokeServerStartStop(new QBFailover(), "host; echo pwned", "start"));
		assertEquals(0, processStartCount.get());
	}

	@Test
	void qbServerStartStopRejectsInvalidCommandBeforeProcessStart() {
		AtomicInteger processStartCount = new AtomicInteger(0);
		QBFailover.setProcessStarterForTest((server, commandToRun) -> {
			processStartCount.incrementAndGet();
			throw new AssertionError("Process creation should not be attempted for invalid command");
		});

		assertThrows(IllegalArgumentException.class,
				() -> invokeServerStartStop(new QBFailover(), "host", "reboot"));
		assertEquals(0, processStartCount.get());
	}

	private void invokeServerStartStop(Object target, String server, String command) throws Throwable {
		Method method = target.getClass().getDeclaredMethod("serverStartStop", String.class, String.class);
		method.setAccessible(true);
		try {
			method.invoke(target, server, command);
		} catch (InvocationTargetException ex) {
			throw ex.getCause();
		}
	}
}
