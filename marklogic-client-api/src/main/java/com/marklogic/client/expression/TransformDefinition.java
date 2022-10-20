package com.marklogic.client.expression;

import java.util.Map;

/**
 * Defines a transform for using with the {@code transformDoc} operator; the
 * assumption is that a factory method will be used to instantiate this which
 * requires the path of the transform module.
 */
public interface TransformDefinition {
    /**
     * Define the kind of transform; either "mjs" (the default) or "xslt".
     * 
     * @param kind
     * @return
     */
    TransformDefinition withKind(String kind);

    /**
     * Define a set of parameters to pass to the transform.
     * 
     * @param params
     * @return
     */
    TransformDefinition withParams(Map<String, Object> params);

    /**
     * Convenience method for adding a single parameter to the transform definition. If a map of params has already been
     * set via {@code withParams}, then this will add to that map.
     * 
     * @param name
     * @param value
     * @return
     */
    TransformDefinition withParam(String name, Object value);
}
