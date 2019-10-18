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

import java.math.BigInteger;
import java.util.LinkedHashSet;

/**
 * Bucket Assignment Policy for fastload
 */
public class BucketAssignmentPolicy extends AssignmentPolicy {
    static final int NUM_BUCKET = 1 << 14;
    private int[][] buckets;
    /**
     * forests ( including RO/DO, but retired forests are excluded)
     */
    private String[] forests;

    public BucketAssignmentPolicy(String[] forests,
        LinkedHashSet<String> uForests) {
        buckets = new int[forests.length][NUM_BUCKET];
        initBucketsTable(forests.length);
        this.forests = forests;
        this.uForests = uForests;
        policy = Kind.BUCKET;
    }

    public int[][] getBucketsTable() {
        return buckets;
    }

    private void initBucketsTable(int maxSize) {
        for (int i = 1; i < maxSize; i++) {
            // bucket to forest assignment
            int[] assignment = new int[NUM_BUCKET];
            int expectCount[] = new int[maxSize];
            int currentCount[] = new int[maxSize];

            for (int j = 0; j < NUM_BUCKET; j++)
                assignment[j] = 0;

            for (int forestCount = 2; forestCount <= maxSize; forestCount++) {

                int minAmount = NUM_BUCKET / forestCount;
                int remainAmount = NUM_BUCKET - (minAmount * forestCount);

                // assignment the number of buckets to each forest first
                for (int k = 0; k < forestCount; k++) {
                    expectCount[k] = minAmount;
                    if (remainAmount > 0) {
                        expectCount[k]++;
                        remainAmount--;
                    }
                    currentCount[k] = 0;
                }

                int newForest = forestCount - 1;
                for (int j = 0; j < NUM_BUCKET; j++) {
                    int forest = assignment[j];

                    // each forest keep the expected number of bucket
                    // and give the rest to the new forest
                    if (currentCount[forest] < expectCount[forest]) {
                        currentCount[forest]++;
                    } else {
                        assignment[j] = newForest;
                        currentCount[newForest]++;
                    }
                }
            }
            buckets[i] = assignment;
        }
    }


    /**
     * return the index to the list of updatable forests (all forest - retired -
     * RO/DO)
     * 
     * @param uri
     * @return index in the list of updatable forests
     */
    public int getPlacementForestIndex(String uri) {
        return getBucketPlacementId(uri, buckets, NUM_BUCKET, forests.length,
            uForests.size());
    }

    // return the index to the forest list (all forest - retired - RO/DO)
    private int getBucketPlacementId(String uri, int[][] buckets,
        int numBuckets, int numForests, int uForests) {
        String nk = LegacyAssignmentPolicy.normalize(uri);
        BigInteger uriKey=LegacyAssignmentPolicy.getUriKey(nk);
        long u = uriKey.longValue();

        for (int i = 14; i <= 56; i += 14) {
            u ^= uriKey.shiftRight(i).longValue();
        }

        int bucket = (int) (u&0x3fff);
        int fIdx = buckets[numForests - 1][bucket];
        boolean allUpdatble = numForests == uForests;
        if (!allUpdatble) {
            int[] partv = new int[uForests];
            int j = 0;
            for (int i = 0; i < numForests; i++) {
                if (isUpdatable(i)) {
                    partv[j++] = i;
                }
            }
            fIdx = partv[LegacyAssignmentPolicy.getPlacementId(uri, uForests)];
        }
        return fIdx;
    }

    private boolean isUpdatable(int fIdx) {
        String forestId = forests[fIdx];
        return uForests.contains(forestId);
    }
}
