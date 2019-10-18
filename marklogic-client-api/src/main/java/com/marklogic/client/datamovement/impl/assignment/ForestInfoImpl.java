/*
 * Copyright 2003-2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.datamovement.impl.assignment;

import java.util.ArrayList;
import java.util.List;

public class ForestInfoImpl implements ForestInfo {
    private String hostName;
    private long frangmentCount;
    private boolean updatable;
    private List<ForestHost> replicas;

    public ForestInfoImpl(String hostName, long fmCount, boolean updatable, List<ForestHost> replicas) {
        super();
        this.hostName = hostName;
        this.frangmentCount = fmCount;
        this.updatable = updatable;
        this.replicas = new ArrayList<ForestHost>();
        int start = 0;
        int len = replicas.size();
        /* find the current open forest 
         * use hostname to compare because forest is the same for shared-disk failover
         */
        for (int i = 0; i < len; i++) {
            if (hostName.equals(replicas.get(i).getHostName())) {
                start = i;
                break;
            }
        }
        /* reorder the list so that the open forest is the first in the list
         */
        for (int i = start; i < len; i++) {
            this.replicas.add(replicas.get(i));
        }
        for (int i = 0; i < start; i++) {
            this.replicas.add(replicas.get(i));
        }
    }

    @Override
    public long getFragmentCount() {
        return frangmentCount;
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public boolean getUpdatable() {
        return updatable;
    }

    @Override
    public List<ForestHost> getReplicas() {
        return replicas;
    }
}
