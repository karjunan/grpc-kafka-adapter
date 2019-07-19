package com.grpc.server.util;

import com.salesforce.kafka.test.KafkaBroker;
import com.salesforce.kafka.test.KafkaBrokers;
import com.salesforce.kafka.test.KafkaTestServer;
import com.salesforce.kafka.test.ZookeeperTestServer;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.apache.curator.test.InstanceSpec;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Collections;
import java.util.Properties;

public class KafkaGrpcTestServer extends KafkaTestServer {
    private static final String DEFAULT_HOSTNAME = "127.0.0.1";
    private KafkaServerStartable broker;
    private int brokerPort;
    private KafkaConfig brokerConfig;
    private ZookeeperTestServer zookeeperTestServer;
    private boolean isManagingZookeeper;
    private final Properties overrideBrokerProperties;

    public KafkaGrpcTestServer() {
        this(new Properties());
    }

    /** @deprecated */
    @Deprecated
    public KafkaGrpcTestServer(String localHostname) {
        this(new Properties());
        this.overrideBrokerProperties.put("host.name", localHostname);
    }

    public KafkaGrpcTestServer(Properties overrideBrokerProperties) throws IllegalArgumentException {
        this.isManagingZookeeper = true;
        this.overrideBrokerProperties = new Properties();
        if (overrideBrokerProperties == null) {
            throw new IllegalArgumentException("Cannot pass null overrideBrokerProperties argument.");
        } else {
            this.overrideBrokerProperties.putAll(overrideBrokerProperties);
        }
    }

    public KafkaGrpcTestServer(Properties overrideBrokerProperties, ZookeeperTestServer zookeeperTestServer) {
        this(overrideBrokerProperties);
        if (zookeeperTestServer != null) {
            this.isManagingZookeeper = false;
        }

        this.zookeeperTestServer = zookeeperTestServer;
    }

    public String getKafkaConnectString() {
        this.validateState(true, "Cannot get connect string prior to service being started.");
        return this.getConfiguredHostname() + ":" + this.brokerPort;
    }

    public KafkaBrokers getKafkaBrokers() {
        this.validateState(true, "Cannot get brokers before service has been started.");
        return new KafkaBrokers(Collections.singletonList(new KafkaBroker(this)));
    }

    public int getBrokerId() {
        this.validateState(true, "Cannot get brokerId prior to service being started.");
        return this.brokerConfig.brokerId();
    }

    public String getZookeeperConnectString() {
        this.validateState(true, "Cannot get connect string prior to service being started.");
        return this.zookeeperTestServer.getConnectString();
    }

    public void start() throws Exception {
        if (this.zookeeperTestServer == null) {
            this.zookeeperTestServer = new ZookeeperTestServer();
        }

        if (this.isManagingZookeeper) {
            this.zookeeperTestServer.restart();
        } else {
            this.zookeeperTestServer.start();
        }

        if (this.broker == null) {
            Properties brokerProperties = new Properties();
            brokerProperties.putAll(this.overrideBrokerProperties);
            this.setPropertyIfNotSet(brokerProperties, "zookeeper.connect", this.zookeeperTestServer.getConnectString());
//            this.setPropertyIfNotSet(brokerProperties, "zookeeper.connect", "localhost:2181");
            this.brokerPort = Integer.parseInt((String)this.setPropertyIfNotSet(brokerProperties, "port", String.valueOf(InstanceSpec.getRandomPort())));
            if (brokerProperties.getProperty("log.dir") == null) {
                brokerProperties.setProperty("log.dir", "\\tmp");
            }

            this.setPropertyIfNotSet(brokerProperties, "host.name", this.getConfiguredHostname());
            this.setPropertyIfNotSet(brokerProperties, "advertised.host.name", this.getConfiguredHostname());
            this.setPropertyIfNotSet(brokerProperties, "advertised.port", String.valueOf(this.brokerPort));
            this.setPropertyIfNotSet(brokerProperties, "advertised.listeners", "PLAINTEXT://" + this.getConfiguredHostname() + ":" + this.brokerPort);
            this.setPropertyIfNotSet(brokerProperties, "listeners", "PLAINTEXT://" + this.getConfiguredHostname() + ":" + this.brokerPort);
            this.setPropertyIfNotSet(brokerProperties, "auto.create.topics.enable", "true");
            this.setPropertyIfNotSet(brokerProperties, "zookeeper.session.timeout.ms", "30000");
            this.setPropertyIfNotSet(brokerProperties, "broker.id", "1");
            this.setPropertyIfNotSet(brokerProperties, "auto.offset.reset", "latest");
            this.setPropertyIfNotSet(brokerProperties, "num.io.threads", "2");
            this.setPropertyIfNotSet(brokerProperties, "num.network.threads", "2");
            this.setPropertyIfNotSet(brokerProperties, "log.flush.interval.messages", "1");
            this.setPropertyIfNotSet(brokerProperties, "offsets.topic.replication.factor", "1");
            this.setPropertyIfNotSet(brokerProperties, "offset.storage.replication.factor", "1");
            this.setPropertyIfNotSet(brokerProperties, "transaction.state.log.replication.factor", "1");
            this.setPropertyIfNotSet(brokerProperties, "transaction.state.log.min.isr", "1");
            this.setPropertyIfNotSet(brokerProperties, "transaction.state.log.num.partitions", "4");
            this.setPropertyIfNotSet(brokerProperties, "config.storage.replication.factor", "1");
            this.setPropertyIfNotSet(brokerProperties, "status.storage.replication.factor", "1");
            this.setPropertyIfNotSet(brokerProperties, "default.replication.factor", "1");
            this.brokerConfig = new KafkaConfig(brokerProperties);
            this.broker = new KafkaServerStartable(this.brokerConfig);
        }

        this.broker.startup();
    }

    public void stop() throws Exception {
        this.close();
    }

    public void close() throws Exception {
        if (this.broker != null) {
            this.broker.shutdown();
        }

        if (this.zookeeperTestServer != null && this.isManagingZookeeper) {
            this.zookeeperTestServer.stop();
        }

    }

    private Object setPropertyIfNotSet(Properties properties, String key, String defaultValue) {
        if (properties == null) {
            throw new NullPointerException("properties argument cannot be null.");
        } else if (key == null) {
            throw new NullPointerException("key argument cannot be null.");
        } else {
            properties.setProperty(key, properties.getProperty(key, defaultValue));
            return properties.get(key);
        }
    }

    private String getConfiguredHostname() {
        return this.overrideBrokerProperties.getProperty("host.name", "127.0.0.1");
    }

    private void validateState(boolean requireServiceStarted, String errorMessage) throws IllegalStateException {
        if (requireServiceStarted && this.broker == null) {
            throw new IllegalStateException(errorMessage);
        } else if (!requireServiceStarted && this.broker != null) {
            throw new IllegalStateException(errorMessage);
        }
    }


}
