package com.micerlab.iot.mqtt.server.common.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @Author hyl
 * @Date 2024/3/3
 */
public class TestCase {
    public static void main(String[] args) {
        ServiceLoader<Search> s = ServiceLoader.load(Search.class);
        Iterator<Search> iterator = s.iterator();
        while (iterator.hasNext()) {
            Search search =  iterator.next();
            search.searchDoc("hello world");
        }
    }
}
