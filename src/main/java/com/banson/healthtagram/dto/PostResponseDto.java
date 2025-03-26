package com.banson.healthtagram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private String content;
    private List<String> filePath;
    private String tagList;
    private String nickname;
    private Long heartCount;
    private LocalDateTime createdAt;
    private boolean likeState;
    private Long postId;
}
