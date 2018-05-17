package com.today.eventbus.rest.support;

import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Desc: ParserUtil
 *
 * @author hz.lei
 * @date 2018年05月17日 下午12:08
 */
public class ParserUtil {
    private static Logger logger = LoggerFactory.getLogger(ParserUtil.class);

    private static RestConsumerConfig consumerConfig = parserXmlData();

    public static RestConsumerConfig getConsumerConfig() {
        return consumerConfig;
    }

    public static Map<String, RestConsumerEndpoint> getConsumersMap() {
        Map consumersMap = new HashMap(16);
        consumerConfig.getRestConsumerEndpoints().forEach(consumer -> consumersMap.put(consumer.getService(), consumer));
        return consumersMap;
    }


    private static RestConsumerConfig parserXmlData() {
        Persister persister = new Persister();
        RestConsumerConfig config = null;
        File file;
        FileInputStream inputStream = null;
        try {
            //==images==//
            inputStream = new FileInputStream("conf/rest-consumer.xml");
            config = persister.read(RestConsumerConfig.class, inputStream);
        } catch (FileNotFoundException e) {
            logger.warn("read file system NotFound [conf/rest-consumer.xml],found conf file [rest-consumer.xml] on classpath");
            try {
                //==develop==//
                file = ResourceUtils.getFile("classpath:rest-consumer.xml");
                config = persister.read(RestConsumerConfig.class, file);
            } catch (FileNotFoundException e1) {
                throw new RuntimeException("rest-consumer.xml in classpath and conf/ NotFound, please Settings");
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        Assert.notNull(config, "Endpoint must be set");

        transferEl(config);

        return config;
    }

    private static void transferEl(RestConsumerConfig config) {
        config.getRestConsumerEndpoints().forEach(endpoint -> {
            String kafkaHostKey = endpoint.getKafkaHost();

            String kafkaHost = get(kafkaHostKey, null);
            logger.info("transfer env key, endpoint id: {}, kafkaHost: {}", endpoint.getId(), kafkaHost);

            if (kafkaHost != null) {
                endpoint.setKafkaHost(kafkaHost);
            } else {
                logger.error("kafka msgAgent endpoint id [" + endpoint.getId() + "] need env [" + kafkaHostKey + "] but NotFound");
                throw new NullPointerException("kafka msgAgent endpoint id [" + endpoint.getId() + "] need env [" + kafkaHostKey + "] but NotFound");
            }
        });


    }

    private static String get(String key, String defaultValue) {
        String envValue = System.getenv(key.replaceAll("\\.", "_"));

        if (envValue == null)
            return System.getProperty(key, defaultValue);

        return envValue;
    }


}
