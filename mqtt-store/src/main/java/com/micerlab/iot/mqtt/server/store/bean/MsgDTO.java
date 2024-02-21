package com.micerlab.iot.mqtt.server.store.bean;

import lombok.Data;

import java.util.Map;

@Data
public class MsgDTO {
    private String ruleId;
    private String type;
    private String originator;
    private Map<String, String> metaData;
    private Object data;
}
