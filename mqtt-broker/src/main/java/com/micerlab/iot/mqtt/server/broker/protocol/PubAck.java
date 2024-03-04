/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.protocol;

import com.micerlab.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.micerlab.iot.mqtt.server.common.message.IMessageIdService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * PUBACK连接处理
 */
@Component
public class PubAck implements Message {
    private static final Logger LOGGER = LoggerFactory.getLogger(PubAck.class);

    @Autowired
    private IMessageIdService messageIdService;

    @Autowired
    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    @Override
    public void process(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = variableHeader.messageId();
        LOGGER.debug("PUBACK - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
        dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
        messageIdService.releaseMessageId(messageId);
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return Arrays.asList(MqttMessageType.PUBACK);
    }

}
