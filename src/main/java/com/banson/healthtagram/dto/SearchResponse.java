package com.banson.healthtagram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SearchResponse {
    String nickname;
    String profilePicture;
    boolean state;
}
