/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.pojo;

import com.marklogic.client.query.QueryDefinition;

/**
 * A marker interface identifying QueryDefinition types compatible with
 * {@link PojoRepository#search(PojoQueryDefinition, long) PojoRepository.search}
 * @see PojoRepository#search(PojoQueryDefinition, long)
 * @see PojoRepository#search(PojoQueryDefinition, long, Transaction)
 * @see PojoRepository#search(PojoQueryDefinition, long, SearchReadHandle)
 * @see PojoRepository#search(PojoQueryDefinition, long, SearchReadHandle, Transaction)
 */
public interface PojoQueryDefinition extends QueryDefinition {}
