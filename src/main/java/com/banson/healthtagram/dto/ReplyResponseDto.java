package com.banson.healthtagram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyResponseDto {
    private Long replyId;
    private String reply;
    private Long heartCount;
    private String nickname;
    private LocalDateTime createdAt;
}
