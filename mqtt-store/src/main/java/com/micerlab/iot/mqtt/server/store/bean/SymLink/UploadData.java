package com.micerlab.iot.mqtt.server.store.bean.SymLink;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class UploadData {


    /**
     * mid : SL10000012030100000air
     * nm : z
     * images : [{"flg":1,"tags":{"11110011WF":0,"1111001121":0,"11110011W6":0,"11110011VB":0,"11110011V9":0,"11110011WD":0,"11110011W4":0,"1111001126":0,"11110011VG":0,"11110011V7":0,"11110011WB":0,"1111001112":0,"1111001124":0,"11110011W9":0,"11110011VE":0,"11110011V5":0,"201r":0,"11110011WG":0,"1111001122":0,"11110011W7":0,"11110011VC":0,"11110011V3":0,"11110011X2":0,"11110011WE":0,"11110011W5":0,"11110011VA":0,"11110011V1":0,"1111001127":0,"11110011VH":80,"11110011V8":0,"11110011WC":0,"11110011W3":0,"1111001125":0,"11110011VF":0,"11110011V6":0,"11110011WA":0,"11110011W1":0,"11110011WH":80,"1111001123":0,"11110011W8":0,"11110011VD":0,"11110011V4":0},"ts":0}]
     * ver : 38
     */
    //网关标识
    private String mid;
    //网关名称
    private String nm;
    private List<ImagesBean> images;
    //版本号
    private Integer ver;

    @Data
    @NoArgsConstructor
    public static class ImagesBean {
        private int flg;
        Map<String, Float> tags;
        private String ts;
    }

}
