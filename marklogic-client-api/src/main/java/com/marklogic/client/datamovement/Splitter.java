package com.marklogic.client.datamovement;

import java.io.InputStream;
import java.util.stream.Stream;

import com.marklogic.client.io.marker.AbstractWriteHandle;

public interface Splitter<T extends AbstractWriteHandle> {
    Stream<T> split(InputStream input) throws Exception;

    long getCount();

}
