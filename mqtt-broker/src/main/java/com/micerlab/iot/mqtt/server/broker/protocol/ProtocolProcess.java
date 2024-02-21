/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.protocol;

import com.micerlab.iot.mqtt.server.broker.internal.InternalCommunication;
import com.micerlab.iot.mqtt.server.common.auth.IAuthService;
import com.micerlab.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.micerlab.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.micerlab.iot.mqtt.server.common.message.IMessageIdService;
import com.micerlab.iot.mqtt.server.common.message.IRetainMessageStoreService;
import com.micerlab.iot.mqtt.server.common.session.ISessionStoreService;
import com.micerlab.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.micerlab.iot.mqtt.server.store.mapper.DeviceChainRelationMapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 协议处理
 */
@Component
public class ProtocolProcess {

    //会话缓存
    @Autowired
    private ISessionStoreService sessionStoreService;

    //订阅相关信息缓存
    @Autowired
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

    private Connect connect;

    private Subscribe subscribe;

    private UnSubscribe unSubscribe;

    private Publish publish;

    private DisConnect disConnect;

    private PingReq pingReq;

    private PubRel pubRel;

    private PubAck pubAck;

    private PubRec pubRec;

    private PubComp pubComp;

    public Connect connect() {
        if (connect == null) {
            connect = new Connect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authService);
        }
        return connect;
    }

    public Subscribe subscribe() {
        if (subscribe == null) {
            subscribe = new Subscribe(subscribeStoreService, messageIdService, messageStoreService);
        }
        return subscribe;
    }

    public UnSubscribe unSubscribe() {
        if (unSubscribe == null) {
            unSubscribe = new UnSubscribe(subscribeStoreService);
        }
        return unSubscribe;
    }

    public Publish publish() {
        if (publish == null) {
//			publish = new Publish(sessionStoreService, subscribeStoreService, messageIdService, messageStoreService, dupPublishMessageStoreService, internalCommunication);
            publish = Publish.builder().dupPublishMessageStoreService(dupPublishMessageStoreService)
                    .internalCommunication(internalCommunication)
                    .messageIdService(messageIdService)
                    .retainMessageStoreService(messageStoreService)
                    .sessionStoreService(sessionStoreService)
                    .subscribeStoreService(subscribeStoreService)
                    .template(kafkaTemplate).adminClient(kafkaAdminClient)
                    .deviceChainRelationMapper(deviceChainRelationMapper).restTemplate(restTemplate).build();
        }
        return publish;
    }


    public DisConnect disConnect() {
        if (disConnect == null) {
            disConnect = new DisConnect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService);
        }
        return disConnect;
    }

    public PingReq pingReq() {
        if (pingReq == null) {
            pingReq = new PingReq();
        }
        return pingReq;
    }

    public PubRel pubRel() {
        if (pubRel == null) {
            pubRel = new PubRel();
        }
        return pubRel;
    }

    public PubAck pubAck() {
        if (pubAck == null) {
            pubAck = new PubAck(messageIdService, dupPublishMessageStoreService);
        }
        return pubAck;
    }

    public PubRec pubRec() {
        if (pubRec == null) {
            pubRec = new PubRec(dupPublishMessageStoreService, dupPubRelMessageStoreService);
        }
        return pubRec;
    }

    public PubComp pubComp() {
        if (pubComp == null) {
            pubComp = new PubComp(messageIdService, dupPubRelMessageStoreService);
        }
        return pubComp;
    }

    public ISessionStoreService getSessionStoreService() {
        return sessionStoreService;
    }

}
