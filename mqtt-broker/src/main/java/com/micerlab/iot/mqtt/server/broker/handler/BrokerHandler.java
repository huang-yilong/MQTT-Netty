/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.handler;

import com.micerlab.iot.mqtt.server.broker.protocol.ProtocolProcess;
import com.micerlab.iot.mqtt.server.common.session.SessionStore;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.io.IOException;

/**
 * MQTT消息处理
 */
public class BrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private ProtocolProcess protocolProcess;

    public BrokerHandler(ProtocolProcess protocolProcess) {
        this.protocolProcess = protocolProcess;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        protocolProcess.processMessage(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            // 远程主机强迫关闭了一个现有的连接的异常
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * @param ctx
     * @param evt
     * @throws Exception 检测通道的空闲状态事件，如果通道长时间没有数据交互，它将执行一些操作，包括发送遗嘱消息并关闭通道
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {  //空闲状态事件对象
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.ALL_IDLE) {  //所有操作都空闲，说明长时间没有数据交互
                Channel channel = ctx.channel();
                String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
                // 发送遗嘱消息
                if (this.protocolProcess.getSessionStoreService().containsKey(clientId)) {
                    SessionStore sessionStore = this.protocolProcess.getSessionStoreService().get(clientId);
                    if (sessionStore.getWillMessage() != null) {
                        this.protocolProcess.getTypes().get(MqttMessageType.PUBLISH).process(ctx.channel(), sessionStore.getWillMessage());
                    }
                }
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
