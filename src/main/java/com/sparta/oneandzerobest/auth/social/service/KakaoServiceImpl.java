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

@Slf4j
@Service
public class KakaoServiceImpl implements KakaoService {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.token-url}")
    private String tokenUrl;

    @Value("${spring.security.oauth2.client.registration.kakao.user-info-url}")
    private String userInfoUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * getAccessToken: - 엑세스 토큰 받기
     *
     * @param code: 인증 코드
     * @return
     */
    @Override
    public String getAccessToken(String code) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 보낼 body value
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode rootNode = parseResponse(response.getBody());
            return rootNode.path("access_token").asText();
        } else {
            throw new InvalidkakaoException("엑세스 토큰 받기 실패: " + response.getBody());
        }
    }

    /**
     * Kresponse에서 엑세스 토큰 받기
     *
     * @param responseBody
     * @return
     */
    private JsonNode parseResponse(String responseBody) {
        try {
            return objectMapper.readTree(responseBody);
        } catch (Exception e) {
            throw new InvalidkakaoException("엑세스 토큰 받기 실패:");
        }
    }

    /**
     * userinfo: - 사용자 정보 가져오기
     *
     * @param accessToken
     * @return
     */
    @Override
    public String getUserInfo(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);
        } catch (NotConnectHttpClientErrorException e) {
            throw new InvalidkakaoException("엑세스 토큰 받기 실패");
        }

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            return rootNode.toString();
        } catch (IOException e) {
            throw new InvalidkakaoException("응답받기 실패");
        }
    }
}
