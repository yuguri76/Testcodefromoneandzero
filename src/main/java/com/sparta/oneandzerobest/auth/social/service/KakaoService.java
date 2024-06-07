package com.sparta.oneandzerobest.auth.social.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.oneandzerobest.exception.InvalidkakaoException;
import com.sparta.oneandzerobest.exception.NotConnectHttpClientErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


public interface KakaoService {
    String getAccessToken(String code);

    String getUserInfo(String accessToken);
}

