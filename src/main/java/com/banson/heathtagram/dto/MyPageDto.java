package com.banson.heathtagram.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyPageDto {
    private String email;

    private String name;

    private String nickname;

    private String profilePicture;

    private String followingMemberId;

    private String followedMemberId;
}
