package com.marklogic.client.test.dataservices;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarklogicSourceConnector extends SourceConnector {
    private String filename;
    String FILE_CONFIG = "file_config";
    String TOPIC_CONFIG = "topic_config";
    private Map<String, String> config;
    private String topic;
    private static final Logger logger = LoggerFactory.getLogger(MarklogicSourceConnector.class);
    @Override
    public void start(Map<String, String> props) {
       // filename = props.get(FILE_CONFIG);
        //topic = props.get(TOPIC_CONFIG);
        config = props;
        logger.info("*********** MarklogicSourceConnector.start ************");

    }


    @Override
    public Class<? extends Task> taskClass() {

        return MarklogicSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
// number of tasks = number of forests
        // forestname passed to tasks and tasks puts it in workunit
        // map - "forestname", forest_1 etc

        logger.info("*********** MarklogicSourceConnector.taskConfigs ************");
        final List<Map<String, String>> configs = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            config.put("forestName","java-unittest-"+(i+1));
            configs.add(config);
        }
        return configs;
    }

    @Override
    public void stop() {
        logger.info("*********** MarklogicSourceConnector.stop ************");
    }

    @Override
    public ConfigDef config() {
        return MarkLogicSourceConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return "2.3.0";
    }
}
