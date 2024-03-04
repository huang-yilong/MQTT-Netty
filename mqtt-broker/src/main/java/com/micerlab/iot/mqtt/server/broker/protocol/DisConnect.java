/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.protocol;

import com.micerlab.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.micerlab.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.micerlab.iot.mqtt.server.common.session.ISessionStoreService;
import com.micerlab.iot.mqtt.server.common.session.SessionStore;
import com.micerlab.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
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
 * DISCONNECT连接处理
 */
@Component
public class DisConnect implements Message {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisConnect.class);

    @Autowired
    private ISessionStoreService sessionStoreService;

    @Autowired
    private ISubscribeStoreService subscribeStoreService;

    @Autowired
    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    @Autowired
    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

    @Override
    public void process(Channel channel, MqttMessage msg) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        SessionStore sessionStore = sessionStoreService.get(clientId);
        if (sessionStore.isCleanSession()) {
            subscribeStoreService.removeForClient(clientId);
            dupPublishMessageStoreService.removeByClient(clientId);
            dupPubRelMessageStoreService.removeByClient(clientId);
        }
        LOGGER.debug("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
        sessionStoreService.remove(clientId);
        channel.close();
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return Arrays.asList(MqttMessageType.DISCONNECT);
    }

}
