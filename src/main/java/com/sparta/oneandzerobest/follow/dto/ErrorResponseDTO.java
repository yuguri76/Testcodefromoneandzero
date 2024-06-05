package com.sparta.oneandzerobest.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ErrorResponseDTO는 오류 응답 시 반환되는 데이터를 담는 DTO입니다.
 */
@Getter
@AllArgsConstructor
public class ErrorResponseDTO {
    private String error;
}