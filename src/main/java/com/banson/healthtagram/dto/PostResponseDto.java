package com.banson.healthtagram.dto;

import com.banson.healthtagram.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private String content;
    private List<String> filePath;
    private List<String> tagList;
    private String nickname;
    private Long heartCount;
    private LocalDateTime createdAt;
    private boolean likeState;
    private Long postId;
}
