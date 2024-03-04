package com.micerlab.iot.mqtt.server.common.spi;

import java.util.List;

/**
 * @Author hyl
 * @Date 2024/3/3
 */
public interface Search {
    public List<String> searchDoc(String keyword);
}