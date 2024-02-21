/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.micerlab.iot.mqtt.server.auth.service;

import cn.hutool.core.util.StrUtil;
import com.micerlab.iot.mqtt.server.common.auth.IAuthService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPrivateKey;
/**
 * 用户名和密码认证服务
 */
@Service
public class AuthService implements IAuthService {

	private RSAPrivateKey privateKey;

	@Override
	public boolean checkValid(String username, String password) {
		if (StrUtil.isBlank(username))
		{
			return false;
		}
		if (StrUtil.isBlank(password)) {
			return false;
		}
//		RSA rsa = new RSA(privateKey, null);
//		String value = rsa.encryptBcd(username, KeyType.PrivateKey);
		//TODO 密码认证
		return true;
	}

	@PostConstruct
	public void init() {
//		privateKey = IoUtil.readObj(AuthService.class.getClassLoader().getResourceAsStream("keystore/auth-private.key"));
	}

}
