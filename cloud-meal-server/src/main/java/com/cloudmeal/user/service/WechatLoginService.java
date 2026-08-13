package com.cloudmeal.user.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.auth.security.JwtService;
import com.cloudmeal.auth.vo.LoginResponse;
import com.cloudmeal.common.exception.BusinessException;
import com.cloudmeal.payment.config.WechatProperties;
import com.cloudmeal.user.entity.User;
import com.cloudmeal.user.mapper.UserMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
public class WechatLoginService {
    private final WechatProperties properties;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final RestClient restClient = RestClient.create("https://api.weixin.qq.com");

    public WechatLoginService(WechatProperties properties, UserMapper userMapper, JwtService jwtService) {
        this.properties = properties;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }

    public LoginResponse login(String code) {
        if (!StringUtils.hasText(properties.getAppId()) || !StringUtils.hasText(properties.getAppSecret())) {
            throw new BusinessException("WECHAT_NOT_CONFIGURED", "服务端尚未配置微信小程序 AppID 和密钥");
        }
        Code2SessionResponse response = restClient.get().uri(builder -> builder
                        .path("/sns/jscode2session")
                        .queryParam("appid", properties.getAppId())
                        .queryParam("secret", properties.getAppSecret())
                        .queryParam("js_code", code)
                        .queryParam("grant_type", "authorization_code")
                        .build())
                .retrieve().body(Code2SessionResponse.class);
        if (response == null || !StringUtils.hasText(response.openid())) {
            String detail = response == null ? "微信登录服务无响应" : response.errmsg();
            throw new BusinessException("WECHAT_LOGIN_FAILED", StringUtils.hasText(detail) ? detail : "微信登录失败");
        }
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenid, response.openid()));
        if (user == null) {
            user = new User();
            user.setOpenid(response.openid());
            user.setNickname("微信用户");
            user.setStatus(1);
            try {
                userMapper.insert(user);
            } catch (DuplicateKeyException duplicateLogin) {
                user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenid, response.openid()));
            }
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw new BusinessException("USER_DISABLED", "账号已被禁用");
        }
        String token = jwtService.create(user.getId(), user.getOpenid(), "USER");
        return new LoginResponse(token, user.getId(), user.getNickname(), "USER");
    }

    private record Code2SessionResponse(
            String openid,
            @JsonProperty("session_key") String sessionKey,
            String unionid,
            Integer errcode,
            String errmsg) {
    }
}
