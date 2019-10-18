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
 * Segment Assignment Policy for fastload
 * 
 * @author jchen
 */
public class SegmentAssignmentPolicy extends LegacyAssignmentPolicy {
    public static long rotr(BigInteger value, int shift) {
        return value.shiftRight(shift).xor(value.shiftLeft(64 - shift))
            .longValue();
    }
    
    public SegmentAssignmentPolicy() {
    }

    public SegmentAssignmentPolicy(LinkedHashSet<String> uForests) {
        super(uForests);
        policy = Kind.SEGMENT;
    }
    
    public static int getPlacementId(String uri, int size) {
        switch (size) {
        case 0:
            throw new IllegalArgumentException("getPlacementId(size = 0)");
        case 1:
            return 0;
        default:
            String nk = normalize(uri);
            BigInteger uriKey = getUriKey(nk);
            long u = uriKey.longValue();
            u ^= rotr(uriKey,2);
            u ^= rotr(uriKey,3);
            u ^= rotr(uriKey,5);
            u ^= rotr(uriKey,7);
            u ^= rotr(uriKey,11);
            u ^= rotr(uriKey,13);
            u ^= rotr(uriKey,17);
            u ^= rotr(uriKey,19);
            u ^= rotr(uriKey,23);
            u ^= rotr(uriKey,29);
            u ^= rotr(uriKey,31);
            u ^= rotr(uriKey,37);
            u ^= rotr(uriKey,41);
            u ^= rotr(uriKey,43);
            u ^= rotr(uriKey,47);
            u ^= rotr(uriKey,53);
            u ^= rotr(uriKey,59);
            u ^= rotr(uriKey,61);
            return (int) Long.remainderUnsigned(u,size);
        }
    }
    
    @Override
    public int getPlacementForestIndex(String uri) {
        return getPlacementId(uri, forests.length);
    }
}
