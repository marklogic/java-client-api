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

    private final static String VERSION_WITH_PATCH_PATTERN = "^.*-(.+)\\..*";

    public MarkLogicVersion(String version) {
        int major = Integer.parseInt(version.replaceAll("([^.]+)\\..*", "$1"));
        final String nightlyPattern = "[^-]+-(\\d{4})(\\d{2})(\\d{2})";
        final String majorWithMinorPattern = "^.*-(.+)$";
        if (version.matches(nightlyPattern)) {
            this.nightly = true;
        } else if (version.matches(majorWithMinorPattern)) {
            this.minor = version.matches(VERSION_WITH_PATCH_PATTERN) ?
                    parseMinorWithPatch(version) :
                    Integer.parseInt(version.replaceAll(majorWithMinorPattern, "$1") + "00");
        }
        this.major = major;
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
