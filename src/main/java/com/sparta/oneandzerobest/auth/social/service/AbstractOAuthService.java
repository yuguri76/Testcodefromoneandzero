package com.sparta.oneandzerobest.auth.social.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.oneandzerobest.exception.InvalidSocialAuthException;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * OAuthService 구체화
 * - Google, Kakao 인증 공통 로직
 */
public abstract class AbstractOAuthService implements OAuthService {

    // Json 파싱
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 하위클레스에서 제공자별 설정 값
    protected abstract String getClientId();

    protected abstract String getClientSecret();

    protected abstract String getRedirectUri();

    protected abstract String getUserInfoUri();

    protected abstract String getTokenUri();

    /**
     * 인증 코드로 액세스 토큰 받기
     * @param code 인증 코드
     * @return 액세스 토큰
     */
    @Override
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", getClientId());
        params.add("redirect_uri", getRedirectUri());
        params.add("code", code);
        params.add("client_secret", getClientSecret());

        // HTTP 요청 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response;

        // 액세스 토큰 요청 및 응답 처리
        try {
            response = restTemplate.exchange(getTokenUri(), HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            throw new InvalidSocialAuthException("엑세스 토큰 받기 실패: " + e.getMessage());
        }

        // JSON 응답 파싱 및 액세스 토큰 추출
        try {
            JsonNode rootNode = parseResponse(response.getBody());
            return rootNode.path("access_token").asText();
        } catch (Exception e) {
            throw new InvalidSocialAuthException("응답 파싱 실패: " + e.getMessage());
        }
    }

    /**
     * 주어진 액세스 토큰으로 사용자 정보를 가져오기
     * @param accessToken 액세스 토큰
     * @return 사용자 정보
     */
    @Override
    public String getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // HTTP 요청 생성
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response;

        // 사용자 정보 요청 및 응답 처리
        try {
            response = restTemplate.exchange(getUserInfoUri(), HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new InvalidSocialAuthException("사용자 정보 가져오기 실패: " + e.getMessage());
        }

        // JSON 응답 파싱 및 사용자 정보 추출
        try {
            JsonNode rootNode = parseResponse(response.getBody());
            return rootNode.toString();
        } catch (Exception e) {
            throw new InvalidSocialAuthException("응답 파싱 실패: " + e.getMessage());
        }
    }

    /**
     * JSON 응답을 파싱하여 JsonNode로 변환
     * @param responseBody 응답 본문
     * @return JsonNode 객체
     */
    private JsonNode parseResponse(String responseBody) {
        try {
            return objectMapper.readTree(responseBody);
        } catch (Exception e) {
            throw new InvalidSocialAuthException("응답 파싱 실패: " + e.getMessage());
        }
    }
}
