package com.banson.healthtagram.dto;

import com.banson.healthtagram.entity.mongodb.Post;
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
    private List<Post> postList;
    private String followState;
    private Long follower;
    private Long following;
}
