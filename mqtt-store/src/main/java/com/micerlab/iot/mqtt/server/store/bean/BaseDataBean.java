package com.micerlab.iot.mqtt.server.store.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class BaseDataBean {
    private String dataId;
    private float value;
    private int source;
    private int range;
    private int cycle;
    private float origin;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date dtime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date serviceReceiveTime;

}
