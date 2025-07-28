/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.expression;

import com.marklogic.client.type.PlanSchemaDef;

/**
 * Defines the schema for using with the {@code validateDoc} operator;
 */
public interface SchemaDefExpr extends PlanSchemaDef {
    /**
     * Defines the mode property.
     *
     * @param mode -
     *      xmlSchema - the mode property takes a strict, lax or type value.
     *      jsonSchema or schematron - the mode property takes a full or strict value
     * @return - an instance of SchemaDefExpr
     */
    SchemaDefExpr withMode(String mode);
    /**
     * Defines the schema based on an existing document in the database.
     *
     * @param schemaUri - property takes a string with the URI of the schema
     * @return - an instance of SchemaDefExpr
     */
    SchemaDefExpr withSchemaUri(String schemaUri);
}
