/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.broker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务配置
 */
@Component
@ConfigurationProperties(prefix = "spring.mqtt.broker")
public class BrokerProperties {

	/**
	 * Broker唯一标识
	 */
	private String id;

	/**
	 * SSL端口号, 默认8883端口
	 */
	private int sslPort = 8885;

	/**
	 * WebSocket SSL端口号, 默认9993端口
	 */
	private int websocketSslPort = 9995;

	/**
	 * WebSocket Path值, 默认值 /mqtt
	 */
	private String websocketPath = "/mqtt";

	/**
	 * SSL密钥文件密码
	 */
	private String sslPassword;

	/**
	 * 心跳时间(秒), 默认60秒, 该值可被客户端连接时相应配置覆盖
	 */
	private int keepAlive = 60;

	/**
	 * 是否开启Epoll模式, 默认关闭
	 */
	private boolean useEpoll = false;

	/**
	 * Sokcet参数, 存放已完成三次握手请求的队列最大长度, 默认511长度
	 */
	private int soBacklog = 511;

	/**
	 * Socket参数, 是否开启心跳保活机制, 默认开启
	 */
	private boolean soKeepAlive = true;

	/**
	 * 集群配置, 是否基于组播发现, 默认开启
	 */
	private boolean enableMulticastGroup = true;

	/**
	 * 集群配置, 基于组播发现
	 */
	private String multicastGroup;

	/**
	 * 集群配置, 当组播模式禁用时, 使用静态IP开启配置集群
	 */
	private String staticIpAddresses;

	public String getId() {
		return id;
	}

	public BrokerProperties setId(String id) {
		this.id = id;
		return this;
	}

	public int getSslPort() {
		return sslPort;
	}

	public BrokerProperties setSslPort(int sslPort) {
		this.sslPort = sslPort;
		return this;
	}

	public int getWebsocketSslPort() {
		return websocketSslPort;
	}

	public BrokerProperties setWebsocketSslPort(int websocketSslPort) {
		this.websocketSslPort = websocketSslPort;
		return this;
	}

	public String getWebsocketPath() {
		return websocketPath;
	}

	public BrokerProperties setWebsocketPath(String websocketPath) {
		this.websocketPath = websocketPath;
		return this;
	}

	public String getSslPassword() {
		return sslPassword;
	}

	public BrokerProperties setSslPassword(String sslPassword) {
		this.sslPassword = sslPassword;
		return this;
	}

	public int getKeepAlive() {
		return keepAlive;
	}

	public BrokerProperties setKeepAlive(int keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}

	public boolean isUseEpoll() {
		return useEpoll;
	}

	public BrokerProperties setUseEpoll(boolean useEpoll) {
		this.useEpoll = useEpoll;
		return this;
	}

	public int getSoBacklog() {
		return soBacklog;
	}

	public BrokerProperties setSoBacklog(int soBacklog) {
		this.soBacklog = soBacklog;
		return this;
	}

	public boolean isSoKeepAlive() {
		return soKeepAlive;
	}

	public BrokerProperties setSoKeepAlive(boolean soKeepAlive) {
		this.soKeepAlive = soKeepAlive;
		return this;
	}

	public boolean isEnableMulticastGroup() {
		return enableMulticastGroup;
	}

	public BrokerProperties setEnableMulticastGroup(boolean enableMulticastGroup) {
		this.enableMulticastGroup = enableMulticastGroup;
		return this;
	}

	public String getMulticastGroup() {
		return multicastGroup;
	}

	public BrokerProperties setMulticastGroup(String multicastGroup) {
		this.multicastGroup = multicastGroup;
		return this;
	}

	public String getStaticIpAddresses() {
		return staticIpAddresses;
	}

	public BrokerProperties setStaticIpAddresses(String staticIpAddresses) {
		this.staticIpAddresses = staticIpAddresses;
		return this;
	}
}
