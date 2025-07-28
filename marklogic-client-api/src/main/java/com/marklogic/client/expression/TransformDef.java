/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.expression;

import java.util.Map;

/**
 * Defines a transform for using with the {@code transformDoc} operator; the
 * assumption is that a factory method will be used to instantiate this which
 * requires the path of the transform module.
 */
public interface TransformDef {
    /**
     * Define the kind of transform; either "mjs" (the default) or "xslt".
     *
     * @param kind
     * @return
     */
    TransformDef withKind(String kind);

    /**
     * Define a set of parameters to pass to the transform.
     *
     * @param params
     * @return
     */
    TransformDef withParams(Map<String, Object> params);

    /**
     * Convenience method for adding a single parameter to the transform definition. If a map of params has already been
     * set via {@code withParams}, then this will add to that map.
     *
     * @param name
     * @param value
     * @return
     */
    TransformDef withParam(String name, Object value);
}
