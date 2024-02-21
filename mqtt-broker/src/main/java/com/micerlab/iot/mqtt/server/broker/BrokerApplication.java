/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker;

import com.micerlab.iot.mqtt.server.broker.config.BrokerProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过SpringBoot启动服务
 */
@SpringBootApplication(scanBasePackages = {"com.micerlab.iot.mqtt.server"})
@MapperScan("com.micerlab.iot.mqtt.server.store.mapper")
public class BrokerApplication {

    public static Map<String,String> callbackUrl=new ConcurrentHashMap<>();


    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BrokerApplication.class);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }

    @Bean
    public BrokerProperties brokerProperties() {
        return new BrokerProperties();
    }


}
