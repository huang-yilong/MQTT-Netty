package com.micerlab.iot.mqtt.server.store.bean.SymLink;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommandBO {
    /**
     * uuid : abcdefg
     * resp : 1
     * batch : [{"meter":"meter1","tag":"tag1","value":"1"},{"meter":"meter1","tag":"tag2","value":"1"}]
     */
    private String uuid;
    private Integer resp;
    private List<BatchBean> batch;
    private String returnUrl;

    @NoArgsConstructor
    @Data
    public static class BatchBean {
        /**
         * meter : meter1
         * tag : tag1
         * value : 1
         */
//        private int vt;
//        private int fmt;
//        private String bin;
        private String meter;
        private String tag;
        private String value;
    }
}
