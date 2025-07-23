/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test;

/**
 * This is cloned from the marklogic-junit5 library to avoid a dependency on that library. That library brings in
 * Spring 5.x dependencies that then make Black Duck unhappy because it cannot seem to understand our Gradle config
 * for excluding all dependencies of marklogic-junit5.
 * <p>
 * This is only needed for FixMjsModulesForMarkLogic12. Eventually, we can probably only run that on MarkLogic
 * 12 or higher and then remove the need for this class and for FixMjsModulesForMarkLogic12.
 */
public class ClonedMarkLogicVersion {

	// For semver releases, which started with MarkLogic 11.
	private final static String SEMVER_PATTERN = "^[0-9]+\\.[0-9]+\\.[0-9]+";
	private final static String NIGHTLY_SEMVER_PATTERN = "^[0-9]+\\.[0-9]+\\.[0-9]{8}";

	// For non-semver releases, such as MarkLogic 10 and earlier..
	private final static String MAJOR_WITH_MINOR_PATTERN = "^.*-(.+)$";
	private final static String VERSION_WITH_PATCH_PATTERN = "^.*-(.+)\\..*";
	private final static String NIGHTLY_BUILD_PATTERN = "[^-]+-(\\d{4})(\\d{2})(\\d{2})";

	private final int major;
	private final Integer minor;
	private final Integer patch;
	private final boolean nightly;

	public ClonedMarkLogicVersion(String version) {
		if (version.matches(NIGHTLY_SEMVER_PATTERN)) {
			String[] tokens = version.split("\\.");
			this.major = Integer.parseInt(tokens[0]);
			this.minor = Integer.parseInt(tokens[1]);
			this.patch = null;
			this.nightly = true;
		} else if (version.matches(SEMVER_PATTERN)) {
			String[] tokens = version.split("\\.");
			this.major = Integer.parseInt(tokens[0]);
			this.minor = Integer.parseInt(tokens[1]);
			this.patch = Integer.parseInt(tokens[2]);
			this.nightly = false;
		} else {
			this.major = Integer.parseInt(version.replaceAll("([^.]+)\\..*", "$1"));
			if (version.matches(NIGHTLY_BUILD_PATTERN)) {
				this.minor = null;
				this.patch = null;
				this.nightly = true;
			} else {
				this.nightly = false;
				if (version.matches(MAJOR_WITH_MINOR_PATTERN)) {
					if (version.matches(VERSION_WITH_PATCH_PATTERN)) {
						this.minor = Integer.parseInt(version.replaceAll(VERSION_WITH_PATCH_PATTERN, "$1"));
						this.patch = Integer.parseInt(version.replaceAll("^.*-(.+)\\.(.*)", "$2"));
					} else {
						this.minor = Integer.parseInt(version.replaceAll(MAJOR_WITH_MINOR_PATTERN, "$1"));
						this.patch = null;
					}
				} else {
					this.minor = null;
					this.patch = null;
				}
			}
		}
	}

	public int getMajor() {
		return major;
	}

	public Integer getMinor() {
		return minor;
	}

	public Integer getPatch() {
		return patch;
	}

	public boolean isNightly() {
		return nightly;
	}
}
