package com.micerlab.iot.mqtt.server.broker.server;

import com.micerlab.iot.mqtt.server.broker.BrokerApplication;
import com.micerlab.iot.mqtt.server.broker.internal.InternalCommunication;
import com.micerlab.iot.mqtt.server.common.session.ISessionStoreService;
import com.micerlab.iot.mqtt.server.common.utils.JsonUtils;
import com.micerlab.iot.mqtt.server.store.bean.Result;
import com.micerlab.iot.mqtt.server.store.bean.SymLink.Command;
import com.micerlab.iot.mqtt.server.store.bean.SymLink.CommandBO;
import com.micerlab.iot.mqtt.server.store.bean.SymLink.CommandRes;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/device")
public class HttpServer {

    @Autowired
    private InternalCommunication communication;

    @PostMapping("/modify/{topic}")
    public Result commandRes(@PathVariable String topic, @RequestBody CommandBO command) {
        Command requestCommon=new Command();
        BeanUtils.copyProperties(command,requestCommon);
        String commandString = JsonUtils.objectToJson(requestCommon);
        System.out.println(topic+":        "+requestCommon);
        communication.sendPublishMessage("/"+topic+"/iocmds", MqttQoS.AT_MOST_ONCE, commandString.getBytes(), false, false);
        BrokerApplication.callbackUrl.put(command.getUuid(), command.getReturnUrl());
        return Result.OK().build();
    }
}
//{"uuid":"210418142663013629952","resp":1,"batch":[{"meter":"11110011W5","tag":"开始小时2","value":"9.0"}]}