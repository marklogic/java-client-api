package com.marklogic.client.kafka;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MarkLogicSourceConfig extends AbstractConfig {

	static final String CONNECTION_HOST = "ml.connection.host";
	//static final String QUERY = "ml.query";
	static final String QUERY_COLLECTION = "ml.query.collection";
	static final String QUERY_PROCESSED_COLLECTION = "ml.query.processed-collection";
	static final String THREAD_COUNT = "ml.dmsdk.threadCount";
	static final String KAFKA_TOPIC = "topic";
	private static final Logger logger = LoggerFactory.getLogger(MarkLogicSourceConfig.class);

	public static ConfigDef CONFIG_DEF = new ConfigDef()
			.define(CONNECTION_HOST, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "MarkLogic server hostname")
			.define(THREAD_COUNT, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "DMSDK threads")
			//.define(QUERY, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "MarkLogic query")
			.define(QUERY_COLLECTION, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "MarkLogic query collection")
			.define(QUERY_PROCESSED_COLLECTION, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "MarkLogic processed collection")
			.define(KAFKA_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka topic name");

	public MarkLogicSourceConfig(Map<?, ?> originals) {
		super(CONFIG_DEF, originals, false);
	}
}