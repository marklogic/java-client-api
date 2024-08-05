/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
