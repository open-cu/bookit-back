package com.opencu.bookit.adapter.out.security.spring.service;

import com.opencu.bookit.adapter.out.security.spring.payload.response.TinkoffUserInfoResponse;
import org.springframework.stereotype.Service;

@Service
public class TinkoffIdService {

    public String getAccessToken(String code) {
        // TODO: Реализовать обмен code на access_token через Tinkoff OAuth2 API
        return null;
    }

    public TinkoffUserInfoResponse getUserInfo(String accessToken) {
        // TODO: Реализовать запрос userinfo через Tinkoff API
        return null;
    }
}