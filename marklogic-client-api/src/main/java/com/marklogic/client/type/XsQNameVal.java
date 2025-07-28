/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

import javax.xml.namespace.QName;

/**
 * An instance of a server qualified name value.
 */
public interface XsQNameVal extends XsAnyAtomicTypeVal, XsQNameSeqVal {
    public QName getQName();
}
