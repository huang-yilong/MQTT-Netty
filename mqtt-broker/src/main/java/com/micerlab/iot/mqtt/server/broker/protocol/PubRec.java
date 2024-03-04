/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.protocol;

import com.micerlab.iot.mqtt.server.common.message.DupPubRelMessageStore;
import com.micerlab.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.micerlab.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * PUBREC连接处理
 */
@Component
public class PubRec implements Message {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubRel.class);

    @Autowired
    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    @Autowired
    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

    @Override
    public void process(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
        LOGGER.debug("PUBREC - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
        dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
        DupPubRelMessageStore dupPubRelMessageStore = new DupPubRelMessageStore().setClientId((String) channel.attr(AttributeKey.valueOf("clientId")).get())
                .setMessageId(variableHeader.messageId());
        dupPubRelMessageStoreService.put((String) channel.attr(AttributeKey.valueOf("clientId")).get(), dupPubRelMessageStore);
        channel.writeAndFlush(pubRelMessage);
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return Arrays.asList(MqttMessageType.PUBREC);
    }

}
