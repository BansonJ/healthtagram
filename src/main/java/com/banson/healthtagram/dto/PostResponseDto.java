package com.banson.healthtagram.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class PostResponseDto {
    private String content;
    private List<String> filePath;
    private List<String> tagList;
    private String nickname;
    private Long heartCount;
    private LocalDateTime createdAt;
    private boolean likeState;
}
