/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.protocol;

import com.micerlab.iot.mqtt.server.broker.internal.InternalCommunication;
import com.micerlab.iot.mqtt.server.broker.server.BrokerServer;
import com.micerlab.iot.mqtt.server.common.auth.IAuthService;
import com.micerlab.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.micerlab.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.micerlab.iot.mqtt.server.common.message.IMessageIdService;
import com.micerlab.iot.mqtt.server.common.message.IRetainMessageStoreService;
import com.micerlab.iot.mqtt.server.common.session.ISessionStoreService;
import com.micerlab.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.micerlab.iot.mqtt.server.store.mapper.DeviceChainRelationMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * 协议处理
 */
@Component
public class ProtocolProcess {

    //会话缓存
    @Autowired
    private ISessionStoreService sessionStoreService;

    @Autowired
    //订阅相关信息缓存
    private ISubscribeStoreService subscribeStoreService;

    @Autowired
    private IAuthService authService;

    //报文标识符
    @Autowired
    private IMessageIdService messageIdService;

    //retain消息
    @Autowired
    private IRetainMessageStoreService messageStoreService;

    @Autowired
    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    @Autowired
    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private AdminClient kafkaAdminClient;

    @Autowired
    private DeviceChainRelationMapper deviceChainRelationMapper;


    //集群内部通信相关，不一定需要这种方式
    @Autowired
    private InternalCommunication internalCommunication;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServer.class);

    private Map<MqttMessageType, Message> types;

    public Map<MqttMessageType, Message> getTypes() {
        if (types == null) {
            types = new HashMap<>();
            ServiceLoader<Message> load = ServiceLoader.load(Message.class);
            StreamSupport.stream(load.spliterator(), false).forEach(protocol ->
                    protocol.getMqttMessageTypes().forEach(type -> {
                        MqttMessageType t = (MqttMessageType) type;
                        types.put(t, protocol);
                    }));
        }
        return types;
    }

    public ISessionStoreService getSessionStoreService() {
        return sessionStoreService;
    }

    public void processMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        Optional<Message> message = Optional.ofNullable(getTypes().get(mqttMessage.fixedHeader().messageType()));
        if (message.isPresent()) {
            message.get().process(ctx.channel(), mqttMessage);
        } else {
            //不知道是啥直接关闭
            ctx.channel().close();
        }
    }
}
