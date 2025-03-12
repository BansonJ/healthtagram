package com.banson.healthtagram.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private String nickname;
    private String profilePicture;
    @Nullable
    private List<String> filePath;
    private String followState;
}
