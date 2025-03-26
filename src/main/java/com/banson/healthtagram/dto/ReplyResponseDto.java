package com.banson.healthtagram.dto;

import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.entity.mongodb.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyResponseDto {
    private List<ReplyMemberResponseDto> replyMemberResponseDtoList;
    private String profilePicture;
    private String nickname;
    private Post post;
}
