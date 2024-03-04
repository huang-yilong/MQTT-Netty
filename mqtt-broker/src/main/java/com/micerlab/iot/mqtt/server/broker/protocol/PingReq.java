/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * PINGREQ连接处理，心跳
 */
@Component
public class PingReq implements Message {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingReq.class);

    @Override
    public void process(Channel channel, MqttMessage msg) {
        MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);
        LOGGER.debug("PINGREQ - clientId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get());
        channel.writeAndFlush(pingRespMessage);
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return Arrays.asList(MqttMessageType.PINGREQ);
    }

}
