package com.micerlab.iot.mqtt.server.broker.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.List;

/**
 * @Author hyl
 * @Date 2024/3/3
 */
public interface Message {
    void process(Channel channel, MqttMessage mqttMessage);

    /**
     * 获取此协议支持的消息类型
     *
     * @return {@link MqttMessageType}
     */
    List<MqttMessageType> getMqttMessageTypes();

}
