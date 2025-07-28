package com.marklogic.client.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MarkLogicVersionTest {

    private MarkLogicVersion version;

    @Test
    public void elevenNightly() {
        version = new MarkLogicVersion("11.0-20220915");
        assertEquals(11, version.getMajor());
        assertNull(version.getMinor());
        assertTrue(version.isNightly());
    }

    @Test
    public void tenNightly() {
        version = new MarkLogicVersion("10.0-20220915");
        assertEquals(10, version.getMajor());
        assertNull(version.getMinor());
        assertTrue(version.isNightly());
    }

    @Test
    public void tenMinorRelease() {
        version = new MarkLogicVersion("10.0-9.4");
        assertEquals(10, version.getMajor());
        assertEquals(904, version.getMinor().intValue());
        assertFalse(version.isNightly());
    }

    @Test
    public void minorHigherThanNine() {
        version = new MarkLogicVersion("9.0-13.1");
        assertEquals(9, version.getMajor());
        assertEquals(1301, version.getMinor().intValue());
        assertFalse(version.isNightly());
    }

    @Test
    public void noPatchNumber() {
        version = new MarkLogicVersion("10.0-1");
        assertEquals(10, version.getMajor());
        assertEquals(100, version.getMinor().intValue());
        assertFalse(version.isNightly());
    }

    @Test
    public void minorHigherThanNineWithNoPatchNumber() {
        version = new MarkLogicVersion("9.0-11");
        assertEquals(9, version.getMajor());
        assertEquals(1100, version.getMinor().intValue());
        assertFalse(version.isNightly());
    }

    @Test
    public void majorWithNoMinor() {
        version = new MarkLogicVersion("9.0");
        assertEquals(9, version.getMajor());
        assertNull(version.getMinor());
        assertFalse(version.isNightly());
    }
}
