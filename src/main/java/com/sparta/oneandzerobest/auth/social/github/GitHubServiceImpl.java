package com.sparta.oneandzerobest.auth.social.github;

import com.sparta.oneandzerobest.auth.social.service.AbstractOAuthService;
import com.sparta.oneandzerobest.exception.InvalidSocialAuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class GitHubServiceImpl extends AbstractOAuthService {

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.github.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.github.user-info-uri}")
    private String userInfoUri;

    @Override
    protected String getClientId() {
        return clientId;
    }

    @Override
    protected String getClientSecret() {
        return clientSecret;
    }

    @Override
    protected String getRedirectUri() {
        return redirectUri;
    }

    @Override
    protected String getTokenUri() {
        return tokenUri;
    }

    @Override
    protected String getUserInfoUri() {
        return userInfoUri;
    }


    // Github 엑세스 토큰 응답 파싱 - x-www-form-urlencoded 형식으로 오버라이딩
    @Override
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
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

        // GitHub의 액세스 토큰 응답을 파싱하는 부분
        try {
            String responseBody = response.getBody();
            if (responseBody != null) {
                // UriComponentsBuilder로 파싱
                Map<String, String> responseMap = UriComponentsBuilder.fromUriString("?" + responseBody).build().getQueryParams().toSingleValueMap();
                return responseMap.get("access_token");
            } else {
                throw new InvalidSocialAuthException("엑세스 토큰 파싱 실패: 응답 바디가 비어 있습니다.");
            }
        } catch (Exception e) {
            throw new InvalidSocialAuthException("엑세스 토큰 파싱 실패: " + e.getMessage());
        }
    }
}
