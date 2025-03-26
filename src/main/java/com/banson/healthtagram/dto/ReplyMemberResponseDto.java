package com.banson.healthtagram.dto;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.mongodb.Reply;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReplyMemberResponseDto {
    private Reply reply;
    private String profilePicture;
    private String nickname;
    private boolean state;

}
