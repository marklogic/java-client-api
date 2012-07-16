package com.marklogic.client.config.support;


import com.marklogic.client.config.QueryOptions.Field;
import com.marklogic.client.config.QueryOptions.JsonKey;
import com.marklogic.client.config.QueryOptions.MarkLogicQName;
import com.marklogic.client.config.QueryOptions.PathIndex;


/**
 * Classes that can have indexes attached to them are marked with this interface.
 */
public interface Indexed {

	void setField(Field field);

	void setAttribute(MarkLogicQName attribute);

	void setElement(MarkLogicQName element);

	void setJsonKey(JsonKey jsonKey);

	void setPath(PathIndex path);
}