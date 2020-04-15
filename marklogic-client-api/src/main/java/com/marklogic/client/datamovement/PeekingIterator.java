/*
 * Copyright (c) 2019 MarkLogic Corporation
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
package com.marklogic.client.datamovement;

import java.util.Iterator;

import com.marklogic.client.MarkLogicInternalException;

public class PeekingIterator<T> implements Iterator<T>  {
    
    private Iterator<T> wrappedItr;
    private T first;
    private boolean isFirst = true;
    public PeekingIterator(Iterator<T> itr) {
        if(itr == null)
            throw new MarkLogicInternalException("Iterator cannot be null");
        this.wrappedItr = itr;
        if (wrappedItr.hasNext()) {
            first = wrappedItr.next();
        } else {
            isFirst = false;
        }
        
    }
    @Override
    public T next() {
        if (isFirst) {
            isFirst = false;
            return first;
        }
        return wrappedItr.next();
    }

    @Override
    public boolean hasNext() {
        return wrappedItr.hasNext();
    }
    
    public T getFirst() {
        
        return first;
    }

}
