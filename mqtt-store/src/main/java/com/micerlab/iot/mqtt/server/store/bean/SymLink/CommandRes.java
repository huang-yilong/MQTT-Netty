package com.micerlab.iot.mqtt.server.store.bean.SymLink;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CommandRes {

    /**
     * ver : 65
     * ioresp : [{"uuid":"ddddd","meter":"micerlab1","tag":"11110011W3","rsn":0,"rss":"FOD操作成功!"}]
     */

    private Integer ver;
    //io响应对象
    private List<IorespBean> ioresp;

    @NoArgsConstructor
    @Data
    public static class IorespBean {
        /**
         * uuid : ddddd
         * meter : micerlab1
         * tag : 11110011W3
         * rsn : 0
         * rss : FOD操作成功!
         */

        private String uuid;
        private String meter;
        private String tag;
        private Integer rsn;
        private String rss;
    }
}
