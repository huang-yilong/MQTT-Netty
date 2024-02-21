package com.micerlab.iot.mqtt.server.common.utils;

public class CodeUtils {

    public static String getProjectId(String code) {
        return code.substring(1,4);
    }


}
