package com.sparta.oneandzerobest.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {

    @NotNull(message = "뉴스피드 ID는 필수입니다.")
    private Long newsfeedId;

    @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
    @Size(max = 255, message = "댓글은 최대 255자까지 입력 가능합니다.")
    private String content;

    @Builder
    public CommentRequestDto(Long newsfeedId, String content) {
        this.newsfeedId = newsfeedId;
        this.content = content;
    }
}