package com.micerlab.iot.mqtt.server.store.mapper;

import com.micerlab.iot.mqtt.server.store.bean.DeviceChainRelation;

public interface DeviceChainRelationMapper {
    int deleteByPrimaryKey(String deviceId);

    int insert(DeviceChainRelation record);

    int insertSelective(DeviceChainRelation record);

    DeviceChainRelation selectByPrimaryKey(String deviceId);

    int updateByPrimaryKeySelective(DeviceChainRelation record);

    int updateByPrimaryKey(DeviceChainRelation record);
}