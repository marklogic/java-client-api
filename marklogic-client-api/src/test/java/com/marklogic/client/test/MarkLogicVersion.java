package com.marklogic.client.test;

import org.apache.commons.lang3.StringUtils;

/**
 * Parses a MarkLogic version string - i.e. from xdmp.version() - into its major and minor versions. The minor version
 * contains the patch number and starts with 100 representing minor version 1 with no patch number. This allows for
 * any combination of minor and patch number to be compared easily.
 */
public class MarkLogicVersion {

	private int major;
	private Integer minor;
	private boolean nightly;

	private final static String MAJOR_WITH_MINOR_PATTERN = "^.*-(.+)$";
	private final static String VERSION_WITH_PATCH_PATTERN = "^.*-(.+)\\..*";
	private final static String NIGHTLY_BUILD_PATTERN = "[^-]+-(\\d{4})(\\d{2})(\\d{2})";
	private final static String SEMVER_PATTERN = "^[0-9]+\\.[0-9]+\\.[0-9]+";

	public MarkLogicVersion(String version) {
		// MarkLogic 11.1.0 adheres to semantic versioning.
		if (version.matches(SEMVER_PATTERN)) {
			String[] tokens = version.split("\\.");
			this.major = Integer.parseInt(tokens[0]);
			this.minor = Integer.parseInt(tokens[1]);
		} else {
			int major = Integer.parseInt(version.replaceAll("([^.]+)\\..*", "$1"));
			if (version.matches(NIGHTLY_BUILD_PATTERN)) {
				this.nightly = true;
			} else if (version.matches(MAJOR_WITH_MINOR_PATTERN)) {
				this.minor = version.matches(VERSION_WITH_PATCH_PATTERN) ?
					parseMinorWithPatch(version) :
					Integer.parseInt(version.replaceAll(MAJOR_WITH_MINOR_PATTERN, "$1") + "00");
			}
			this.major = major;
		}
	}

	private int parseMinorWithPatch(String version) {
		final int minorNumber = Integer.parseInt(version.replaceAll(VERSION_WITH_PATCH_PATTERN, "$1"));
		final int patch = Integer.parseInt(version.replaceAll("^.*-(.+)\\.(.*)", "$2"));
		final String leftPaddedPatchNumber = patch < 10 ?
			StringUtils.leftPad(String.valueOf(patch), 2, "0") :
			String.valueOf(patch);
		return Integer.parseInt(minorNumber + leftPaddedPatchNumber);
	}

	public int getMajor() {
		return major;
	}

	public Integer getMinor() {
		return minor;
	}

	public boolean isNightly() {
		return nightly;
	}
}
