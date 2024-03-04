/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.protocol;

import com.micerlab.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * UNSUBSCRIBE连接处理
 */
@Component
public class UnSubscribe implements Message {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnSubscribe.class);

    private ISubscribeStoreService subscribeStoreService;

    @Override
    public void process(Channel channel, MqttMessage mqttMessage) {
        MqttUnsubscribeMessage msg = (MqttUnsubscribeMessage) mqttMessage;
        List<String> topicFilters = msg.payload().topics();
        String clinetId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        topicFilters.forEach(topicFilter -> {
            subscribeStoreService.remove(topicFilter, clinetId);
            LOGGER.debug("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clinetId, topicFilter);
        });
        MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
        channel.writeAndFlush(unsubAckMessage);
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return Arrays.asList(MqttMessageType.UNSUBSCRIBE);
    }

}
