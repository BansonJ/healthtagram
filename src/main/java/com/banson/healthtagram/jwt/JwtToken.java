package com.banson.healthtagram.jwt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtToken {

    private String accessToken;
    private String refreshToken;
}
