package com.sparta.oneandzerobest.newsfeed.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsfeedRequestDto {

    @NotBlank(message = "내용이 비어있습니다.")
    private String content;
}