package com.micerlab.iot.mqtt.server.broker.kafka;


import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaInitialConfiguration {


    @Autowired
    KafkaAdmin kafkaAdmin;

    @Bean  //kafka客户端，在spring中创建这个bean之后可以注入并且创建topic,用于集群环境，创建对个副本
    public AdminClient adminClient() {
        return AdminClient.create(kafkaAdmin.getConfig());
    }


}
