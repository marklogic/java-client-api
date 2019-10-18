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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;

/**
 * Abstract class for assignment policy
 */
public abstract class AssignmentPolicy {
    protected static Logger logger = LoggerFactory.getLogger(AssignmentPolicy.class);

    public enum Kind {
        LEGACY, BUCKET, RANGE, STATISTICAL, QUERY, SEGMENT;
        public static Kind forName(String type) {
            for (Kind e : values()) {
                if (e.toString().equalsIgnoreCase(type)) {
                    return e;
                }
            }
            throw new IllegalArgumentException("No enum: " + type);
        }
    }

    protected Kind policy;
    /**
     * updatable forests
     */
    protected LinkedHashSet<String> uForests;

    public Kind getPolicyKind() {
        return policy;
    }

    public abstract int getPlacementForestIndex(String uri);

    // noops other than in StatisticalAssignmentPolicy and its derivations
    public void setBatch(int batch) {
    }
    public void start() {
    }
    public void stop() {
    }
}
