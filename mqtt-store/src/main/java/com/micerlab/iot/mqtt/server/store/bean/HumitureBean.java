package com.micerlab.iot.mqtt.server.store.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HumitureBean {


    /**
     * dataId : 0000000011102
     * value : 61.3
     * name : 湿度
     * time : 2021-03-11 11:14:45
     */

    private String dataId;
    private Double value;
    private String name;
    private String time;
}
