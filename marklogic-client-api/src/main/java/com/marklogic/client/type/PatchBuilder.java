package com.marklogic.client.type;

/**
 * Manually constructed by first adding the methods to PlanBuilder in optic-defs.json (which is the wrong place) and
 * then moving them to this manually created interface, and then removing the methods from optic-defs.json.
 */
public interface PatchBuilder {

	/**
	 * Insert a new node after another node.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param node The path to insert the node after.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder insertAfter(String path, ServerExpression node);

	/**
	 * Insert a new node after another node.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param node The path to insert the node after.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder insertAfter(XsStringVal path, ServerExpression node);

	/**
	 * Insert a new node before another node.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param node The path to insert the node before.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder insertBefore(String path, ServerExpression node);

	/**
	 * Insert a new node before another node.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param node The path to insert the node before.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder insertBefore(XsStringVal path, ServerExpression node);

	/**
	 * Insert a node as child.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param node The path to insert the child.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder insertChild(String path, ServerExpression node);

	/**
	 * Insert a node as child.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param node The path to insert the child.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder insertChild(XsStringVal path, ServerExpression node);

	/**
	 * This method is specific for JSON and inserts a key/value pair to an object.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param key  The path which returns an JSON Object.
	 * @param node The key to insert.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder insertNamedChild(String path, String key, ServerExpression node);

	/**
	 * This method is specific for JSON and inserts a key/value pair to an object.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param key  The path which returns an JSON Object.
	 * @param node The key to insert.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder insertNamedChild(XsStringVal path, XsStringVal key, ServerExpression node);

	/**
	 * This method deletes a document from the database. If the document does not exist, this method does not throw an error. Delete a node.
	 *
	 * @param path If this column is not specified then it assumes a column 'uri' is present. This can be a string of the uri column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. This can also be a map object specify the uri column name.
	 * @return a PatchBuilder object
	 */
	PatchBuilder remove(String path);

	/**
	 * This method deletes a document from the database. If the document does not exist, this method does not throw an error. Delete a node.
	 *
	 * @param path If this column is not specified then it assumes a column 'uri' is present. This can be a string of the uri column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. This can also be a map object specify the uri column name.
	 * @return a PatchBuilder object
	 */
	PatchBuilder remove(XsStringVal path);

	/**
	 * Replace a node with another node.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param node The path to replace.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder replace(String path, ServerExpression node);

	/**
	 * Replace a node with another node.
	 *
	 * @param path The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param node The path to replace.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder replace(XsStringVal path, ServerExpression node);

	/**
	 * Replace a child if it exist, or insert if it does not exist.
	 *
	 * @param parentPath    The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param pathToReplace The parent path to insert/replace.
	 * @return a PatchBuilder object
	 */
	PatchBuilder replaceInsertChild(String parentPath, String pathToReplace);

	/**
	 * Replace a child if it exist, or insert if it does not exist.
	 *
	 * @param parentPath    The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param pathToReplace The parent path to insert/replace.
	 * @return a PatchBuilder object
	 */
	PatchBuilder replaceInsertChild(XsStringVal parentPath, XsStringVal pathToReplace);

	/**
	 * Replace a child if it exist, or insert if it does not exist.
	 *
	 * @param parentPath    The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param pathToReplace The parent path to insert/replace.
	 * @param node          The path to insert/replace which is relative to parent-path.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder replaceInsertChild(String parentPath, String pathToReplace, ServerExpression node);

	/**
	 * Replace a child if it exist, or insert if it does not exist.
	 *
	 * @param parentPath    The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param pathToReplace The parent path to insert/replace.
	 * @param node          The path to insert/replace which is relative to parent-path.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder replaceInsertChild(XsStringVal parentPath, XsStringVal pathToReplace, ServerExpression node);

	/**
	 * Replace the value of a path with a new value.
	 *
	 * @param path  The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param value The path to replace the value.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder replaceValue(String path, ServerExpression value);

	/**
	 * Replace the value of a path with a new value.
	 *
	 * @param path  The Patch Builder Plan. You can either use the XQuery =&gt; chaining operator or specify the variable that captures the return value from the previous operation.
	 * @param value The path to replace the value.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
	 * @return a PatchBuilder object
	 */
	PatchBuilder replaceValue(XsStringVal path, ServerExpression value);

}
