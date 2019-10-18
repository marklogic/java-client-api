package com.marklogic.client.datamovement.impl.assignment;

import java.util.List;

public interface ForestInfo {
    long getFragmentCount();
    String getHostName();
    boolean getUpdatable();
    List<ForestHost> getReplicas();
}
