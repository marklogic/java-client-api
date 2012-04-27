/**
 * The classes in this package compose query options for MarkLogic Server.
 * Use {@link com.marklogic.client.io.QueryOptionsManager}.newOptions() as the entry point to create
 * a {@link com.marklogic.client.io.QueryOptionsHandle} configuration object.   Once you have a complete object tree, 
 * assign a name and commit it to the database with 
 * {@link com.marklogic.client.QueryOptionsManager}.writeOptions("name", queryOptionsHandle).
 * The options name is used in conjunction with as a property of a (@link com.marklogic.client.config.QueryDefinition}.
 */
package com.marklogic.client.configpojos;