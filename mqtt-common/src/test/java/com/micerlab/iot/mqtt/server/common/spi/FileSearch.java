package com.micerlab.iot.mqtt.server.common.spi;

import java.util.List;

/**
 * @Author hyl
 * @Date 2024/3/3
 */
public class FileSearch implements Search{
    @Override
    public List<String> searchDoc(String keyword) {
        System.out.println("文件搜索 "+keyword);
        return null;
    }
}
