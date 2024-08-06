package com.banson.heathtagram.dto;

import com.banson.heathtagram.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private String email;

    private String name;

    private String nickname;

    private String profilePicture;

    private String followingMemberId;

    private String followedMemberId;

    public static MemberDto toEntity (Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .name(member.getName())
                .followedMemberId(member.getFollowedMemberId())
                .followingMemberId(member.getFollowingMemberId())
                .profilePicture(member.getProfilePicture())
                .build();
    }
}
