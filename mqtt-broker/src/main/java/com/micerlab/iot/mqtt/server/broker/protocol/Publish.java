/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import com.micerlab.iot.mqtt.server.broker.BrokerApplication;
import com.micerlab.iot.mqtt.server.broker.internal.InternalCommunication;
import com.micerlab.iot.mqtt.server.broker.internal.InternalMessage;
import com.micerlab.iot.mqtt.server.common.message.*;
import com.micerlab.iot.mqtt.server.common.session.ISessionStoreService;
import com.micerlab.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.micerlab.iot.mqtt.server.common.subscribe.SubscribeStore;
import com.micerlab.iot.mqtt.server.common.utils.JsonUtils;
import com.micerlab.iot.mqtt.server.store.bean.*;
import com.micerlab.iot.mqtt.server.store.bean.SymLink.CommandRes;
import com.micerlab.iot.mqtt.server.store.bean.SymLink.UploadData;
import com.micerlab.iot.mqtt.server.store.mapper.DeviceChainRelationMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;



import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * PUBLISH连接处理
 */
@Builder
@Slf4j
public class Publish {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publish.class);

    private ISessionStoreService sessionStoreService;

    private ISubscribeStoreService subscribeStoreService;

    private IMessageIdService messageIdService;

    private IRetainMessageStoreService retainMessageStoreService;

    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    private InternalCommunication internalCommunication;

    private KafkaTemplate<String, String> template;

    private AdminClient adminClient;

    private DeviceChainRelationMapper deviceChainRelationMapper;

    private RestTemplate restTemplate;

    //FIXME 如何确保消息发送可靠性，暂定使用kafka
    public void processPublish(Channel channel, MqttPublishMessage msg) {
        try {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            String s = new String(messageBytes);
            s = s.replace("\n", "");
            if (s.contains("nm")) {
                System.out.println(s);
                sendKafkaPublishMessage(messageBytes);
                if (msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                    this.sendPubAckMessage(channel, msg.variableHeader().packetId());
                }
                return;
            }
        } catch (Exception e) {
            log.error("发送失败");
            e.printStackTrace();
        }

//         QoS=0
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
                    .setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
                    .setDup(false).setRetain(false);
            internalCommunication.internalSend(internalMessage);
            String s = new String(messageBytes);
            s = s.replace("\n", "");
            System.out.println(s);
            if (s.contains("ioresp")) {
                CommandRes commandRes = JsonUtils.jsonToPojo(s, CommandRes.class);
                String uuid = commandRes.getIoresp().get(0).getUuid();
                try {
                    restTemplate.postForObject(BrokerApplication.callbackUrl.get(uuid), commandRes, CommandRes.class);
                } catch (Exception ignored) {

                }
            }
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
//			this.sendKafkaPublishMessage();
        }
//         QoS=1
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
                    .setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
                    .setDup(false).setRetain(false);
            internalCommunication.internalSend(internalMessage);
            String s = new String(messageBytes);
            s = s.replace("\n", "");
            if (s.contains("ioresp")) {
                CommandRes commandRes = JsonUtils.jsonToPojo(s, CommandRes.class);
                String uuid = commandRes.getIoresp().get(0).getUuid();
                try {
                    restTemplate.postForObject(BrokerApplication.callbackUrl.get(uuid), commandRes, Result.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(s);
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
            this.sendPubAckMessage(channel, msg.variableHeader().packetId());
        }
//         QoS=2
        if (msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
                    .setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
                    .setDup(false).setRetain(false);
            internalCommunication.internalSend(internalMessage);
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
            this.sendPubRecMessage(channel, msg.variableHeader().packetId());
        }
//         retain=1, 保留消息,新的订阅者建立时，发送对应topic
        if (msg.fixedHeader().isRetain()) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            if (messageBytes.length == 0) {
                retainMessageStoreService.remove(msg.variableHeader().topicName());
            } else {
                RetainMessageStore retainMessageStore = new RetainMessageStore().setTopic(msg.variableHeader().topicName()).setMqttQoS(msg.fixedHeader().qosLevel().value())
                        .setMessageBytes(messageBytes);

                retainMessageStoreService.put(msg.variableHeader().topicName(), retainMessageStore);
            }
        }
    }

    /**
     * (1)指明 partition 的情况下，直接将指明的值直接作为 partiton 值;
     * <p>
     * (2)没有指明 partition 值但有 key 的情况下，将 key 的 hash 值与 topic 的 partition 数进行取余得到 partition 值;
     * <p>
     * (3)既没有 partition 值又没有 key 值的情况下，第一次调用时随机生成一个整数(后 面每次调用在这个整数上自增)，
     * 将这个值与 topic 可用的 partition 总数取余得到 partition 值，也就是常说的 round-robin 算法。
     */
    private void sendKafkaPublishMessage(byte[] messageBytes) throws ExecutionException, InterruptedException {
        //TODO 对网关传递的时序数据进行处理，不同数据不一样
        String s = new String(messageBytes);
        Date currentTime = new Date();
        s = s.replace("\n", "");
        UploadData uploadData = JsonUtils.jsonToPojo(s, UploadData.class);
        uploadData.getImages().forEach(imagesBean -> {
            imagesBean.getTags().forEach((key, value) -> {
                BaseDataBean baseDataBean = new BaseDataBean();
                baseDataBean.setDataId(key);
                baseDataBean.setOrigin(value);
                baseDataBean.setValue(value);
                baseDataBean.setSource(1);
                baseDataBean.setRange(1);
                baseDataBean.setCycle(1);
                baseDataBean.setServiceReceiveTime(currentTime);
                String originator = baseDataBean.getDataId();
                Date time = new Date(Long.parseLong(imagesBean.getTs()));
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date time = null;
//                try {
//                    time = df.parse(imagesBean.getTs());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                baseDataBean.setDtime(time);
                MsgDTO msgDTO = new MsgDTO();
                msgDTO.setData(baseDataBean);
                msgDTO.setMetaData(new HashMap<>());
                msgDTO.setOriginator(originator);
                msgDTO.setType("test");
                //根据具体格式生成对应的Bean
//                // 暂时使用测试数据
                DeviceChainRelation deviceChainRelation = deviceChainRelationMapper.selectByPrimaryKey(originator);
                if (deviceChainRelation == null) {
                    return;
                }
                msgDTO.setRuleId(deviceChainRelation.getRuleId());
                template.send(deviceChainRelation.getTopic(), deviceChainRelation.getDeviceId(), JsonUtils.objectToJson(msgDTO));
            });
        });

//        Map<String, LinkedHashMap> data = JsonUtils.jsonToPojo(s, Map.class);
//        LinkedHashMap<String, LinkedHashMap<String, String>> dataMap = data.get("dataMap");
//
//        dataMap.forEach(new BiConsumer<String, LinkedHashMap<String, String>>() {
//            @Override
//            public void accept(String s, LinkedHashMap<String, String> data) {
//
//                String value = String.valueOf(data.get("value"));
//
//                BaseDataBean baseDataBean = new BaseDataBean();
//                baseDataBean.setDataId(data.get("dataId"));
//                baseDataBean.setOrigin(Float.parseFloat(value));
//                baseDataBean.setValue(Float.parseFloat(value));
//                baseDataBean.setSource(1);
//                baseDataBean.setRange(1);
//                baseDataBean.setCycle(1);
//                String originator = baseDataBean.getDataId();
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date time = null;
//                try {
//                    time = df.parse(data.get("time"));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//
//                baseDataBean.setDtime(time);
//                MsgDTO msgDTO = new MsgDTO();
//                msgDTO.setData(baseDataBean);
//                msgDTO.setMetaData(new HashMap<>());
//                msgDTO.setOriginator(originator);
//                msgDTO.setType("test");
//
//
////        telemetryBean.setCycle();
////            telemetryBean.setDeviceId();
//                //rule engine中进行判断
////            telemetryBean.setRange(-1);
////            telemetryBean.set
//
//                //根据具体格式生成对应的Bean
//                //TODO 暂时使用测试数据
//
//                DeviceChainRelation deviceChainRelation = deviceChainRelationMapper.selectByPrimaryKey(originator);
//                msgDTO.setRuleId(deviceChainRelation.getRuleId());
//                template.send(deviceChainRelation.getTopic(), deviceChainRelation.getDeviceId(), JsonUtils.objectToJson(msgDTO));
//            }
//        });
    }

    private void sendPublishMessage(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
        List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
        subscribeStores.forEach(subscribeStore -> {
            if (sessionStoreService.containsKey(subscribeStore.getClientId())) {
                // 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
                MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
                if (respQoS == MqttQoS.AT_MOST_ONCE) {
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, 0), Unpooled.buffer().writeBytes(messageBytes));
                    LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
                if (respQoS == MqttQoS.AT_LEAST_ONCE) {
                    int messageId = messageIdService.getNextMessageId();
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
                    LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
                    DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore().setClientId(subscribeStore.getClientId())
                            .setTopic(topic).setMqttQoS(respQoS.value()).setMessageBytes(messageBytes);
                    //重发机制,通过协议确保消息传递可靠性
                    dupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
                if (respQoS == MqttQoS.EXACTLY_ONCE) {
                    int messageId = messageIdService.getNextMessageId();
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
                    LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
                    DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore().setClientId(subscribeStore.getClientId())
                            .setTopic(topic).setMqttQoS(respQoS.value()).setMessageBytes(messageBytes);
                    dupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
            }
        });
    }

    private void sendPubAckMessage(Channel channel, int messageId) {
        MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
        channel.writeAndFlush(pubAckMessage);
    }

    private void sendPubRecMessage(Channel channel, int messageId) {
        MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
        channel.writeAndFlush(pubRecMessage);

        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("asd", "asd");
        linkedHashMap.containsKey("");
        linkedHashMap.remove("");


    }

}
