/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

import javax.xml.namespace.QName;

/**
 * An instance of a server qualified name value.
 */
public interface XsQNameVal extends XsAnyAtomicTypeVal, XsQNameSeqVal {
    public QName getQName();
}
