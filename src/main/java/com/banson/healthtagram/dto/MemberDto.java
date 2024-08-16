package com.banson.healthtagram.dto;

import com.banson.healthtagram.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private String email;

    private String name;

    private String nickname;

    private String profilePicture;

    public static MemberDto toEntity (Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .name(member.getName())
                .profilePicture(member.getProfilePicture())
                .build();
    }
}
